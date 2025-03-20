package com.coyotesong.database.formatters;

import com.coyotesong.database.MetadataMethods;
import com.coyotesong.database.Pivots;
import com.google.common.base.Strings;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.interpret.Context;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.interpret.JinjavaInterpreterFactory;
import com.hubspot.jinjava.lib.filter.Filter;
import com.hubspot.jinjava.lib.fn.ELFunctionDefinition;
import com.hubspot.jinjava.loader.ClasspathResourceLocator;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Encapsulate implementation details via custom functions, filters, etc.
 * <p>
 * The 'format' method is not jinjava-specific.
 * </p>
 * <p>
 * Implementation note: normally we would hqndle destination-specific formatting
 * here but that's a bit complicated since jinjava requires static functions
 * (or injection...). We could try a thread-local static value but I felt it
 * would be clenaer to push that formatting into the Pivots class itself for
 * now.
 * </p>
 */
public class JinjavaFactory {
    private static final Logger LOG = LoggerFactory.getLogger(JinjavaFactory.class);

    // private static final String HASHMARK = Character.toString(0x10102); // hash (𐄂) mark
    // private static final String CHECKMARK = Character.toString(0x1F5F8); // check (🗸) mark
    // private static final String BALLOT_BOX = Character.toString(0x2610); // '☐'
    // private static final String BALLOT_BOX_WITH_CHECKMARK = Character.toString(0x2611); // '☑'
    private static final String BALLOT_BOX_WITH_BOLD_CHECKMARK = Character.toString(0x1F5F9); // '🗹';

    // these are workarounds to the jinjava requirement for static functions.
    private static final ThreadLocal<Pivots> PIVOTS = new ThreadLocal<>();
    private static final ThreadLocal<CharacterEscape> ESCAPE = new ThreadLocal<>();

    public static Jinjava newInstance(Pivots pivots, CharacterEscape escape) throws IOException {
        PIVOTS.set(pivots);
        ESCAPE.set(escape);

        final ClasspathResourceLocator locator = new ClasspathResourceLocator();
        final JinjavaConfig config = new JinjavaConfig();
        final Jinjava jinjava = new Jinjava(config);

        registerExtensions(jinjava.getGlobalContext());

        // JinjavaInterpreter interpreter = jinjava.newInterpreter();

        registerIsSupportedFunction(jinjava, "isSqlKeywordSupported");
        registerIsSupportedFunction(jinjava, "isTableTypeSupported");

        return jinjava;
    }

    static void registerExtensions(Context context) {
        registerGetters(context, JinjavaFactory.class);
        registerFilters(context, MetadataMethods.class);

        context.registerFunction(
                new ELFunctionDefinition("fn", "functionSupported", JinjavaFactory.class, "isFunctionSupported", String.class, String.class));
        context.registerFunction(
                new ELFunctionDefinition("fn", "pad", JinjavaFactory.class, "pad", String.class, String.class));
    }

    static void registerGetters(Context context, Class<?> clzz) {
        for (Method method : clzz.getMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && method.getName().startsWith("get")) {
                final String functionName = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
                context.registerFunction(new ELFunctionDefinition("fn", functionName, method));
            }
        }
    }

    static void registerFilters(Context context, Class<?> clzz) {
        for (Method method : clzz.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers()) && method.getName().startsWith("is") && method.getParameterCount() == 1 && method.getParameterTypes()[0] == String.class) {
                context.registerFilter(new PropertyFilter(method));
            }
        }
    }

    static void registerIsSupportedFunction(Jinjava jinjava, String functionName) {
        try {
            final Method m = JinjavaFactory.class.getMethod(functionName, String.class, String.class);
            jinjava.getGlobalContext().registerFunction(
                    new ELFunctionDefinition("fn", functionName, m));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("unable to find '" + functionName + "'", e);
        }
    }

    public static String pad(String database, String pad) {
        if (StringUtils.isNotEmpty(pad)) {
            return Strings.padStart("", database.length(), pad.charAt(0));
        }
        return "";
    }

    public static List<String> getDatabaseLabels() {
        return PIVOTS.get().getDatabaseLabels().stream().map(ESCAPE.get()).toList();
    }

    public static List<String> getNumericFunctions() {
        return PIVOTS.get().getNumericFunctions().stream().map(ESCAPE.get()).toList();
    }

    public static List<String> getPropertyNames() {
        return PIVOTS.get().getPropertyNames().stream().map(ESCAPE.get()).toList();
    }

    public static List<String> getStringFunctions() {
        return PIVOTS.get().getStringFunctions().stream().map(ESCAPE.get()).toList();
    }

    public static List<String> getSqlKeywords() {
        return PIVOTS.get().getSqlKeywords().stream().map(ESCAPE.get()).toList();
    }

    public static List<String> getSqlProperties() {
        return PIVOTS.get().getSqlProperties().stream().map(ESCAPE.get()).toList();
    }

    public static List<String> getSystemFunctions() {
        return PIVOTS.get().getSystemFunctions().stream().map(ESCAPE.get()).toList();
    }

    public static List<String> getTableTypes() {
        return PIVOTS.get().getTableTypes().stream().map(ESCAPE.get()).toList();
    }

    public static List<String> getTemporalFunctions() {
        return PIVOTS.get().getTemporalFunctions().stream().map(ESCAPE.get()).toList();
    }

    public static String isSqlKeywordSupported(String sqlKeyword, String database) {
        return format(PIVOTS.get().isSqlKeywordSupported(sqlKeyword, database));
    }

    public static String isTableTypeSupported(String sqlKeyword, String database) {
        return format(PIVOTS.get().isTableTypeSupported(sqlKeyword, database));
    }

    public static String getProperty(String propertyKey, String database) {
        return format(PIVOTS.get().getProperty(propertyKey, database));
    }

    public static String isFunctionSupported(String functionName, String database) {
        return format(PIVOTS.get().isFunctionSupported(functionName, database));
    }

    public static Collection<String> getClientInfoProperties(String database) {
        return PIVOTS.get().getClientInfoProperties(database);
    }

    public static String  getDatabaseProductName(String database) {
        return PIVOTS.get().getDatabaseProductName(database);
    }

    public static String  getDatabaseProductVersion(String database) {
        return PIVOTS.get().getDatabaseProductVersion(database);
    }

    public static String  getDriverClassname(String database) {
        return PIVOTS.get().getDriverClassname(database);
    }

    public static String getMavenCoordinates(String database) {
        return PIVOTS.get().getMavenCoordinates(database);
    }

    public static String getDockerRepo(String database) {
        return PIVOTS.get().getDockerRepo(database);
    }

    public static class PropertyFilter implements Filter, Predicate<String> {
        private static final Logger LOG = LoggerFactory.getLogger(PropertyFilter.class);
        private final String name;
        private Method m;

        public PropertyFilter(Method m) {
            this.name = m.getName();
            this.m = m;
        }

        public PropertyFilter(String name) {
            this.name = name;
            try {
                this.m = MetadataMethods.class.getMethod(name, String.class);
            } catch (NoSuchMethodException e) {
                LOG.warn("No such method found: {}", name);
            }
        }

        @Override
        public boolean test(String value) {
            if (m == null) {
                return false;
            }

            // this hides the exception so we can use streaming later.
            try {
                return Boolean.TRUE.equals(m.invoke(MetadataMethods.INSTANCE, value));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Object filter(Object var, JinjavaInterpreter interpreter, String... args) {
            if (m == null) {
                return null;
            }

            @SuppressWarnings("unchecked") final Collection<String> values = (Collection<String>) var;
            return values.stream().filter(this).toList();
        }
    }

    //
    // The jinja engine can use user-provided static functions. The example given in
    // the documentation is the java static method
    //
    // ------------------------------------------------------------
    //
    // Add'l information from http://www.javadoc.io/doc/com.hubspot.jinjava/jinjava
    //
    //  a 'tag' is something like '{% timestamp %}'
    //  it implements com.hubspot.jinjava.lib.Tag
    //
    //     jinjava.getGlobalContext().registerTag(new MyCustomTag());
    //
    //  // define any number of classes which extend Importable
    //  jinjava.getGlobalContext().registerClasses(Class<? extends Importable>... classes);

    /**
     * Perform uniform formatting of arbitrary objects.
     *
     * @param value unformatted object
     * @return formatted string
     */
    @NotNull
    protected static String format(Object value) {
        if (value == null) {
            return "[null]";
        } else if (value instanceof String) {
            return ESCAPE.get().apply((String) value);
        } else if (value instanceof Boolean) {
            if ((Boolean) value) {
                return BALLOT_BOX_WITH_BOLD_CHECKMARK;
            } else {
                return "";
            }
        } else if (value instanceof Number) {
            // should be a bit more clever here...
            long v = ((Number) value).longValue();
            long v1KiB = 1024L;
            long v1MiB = 1024L * v1KiB;
            long v1GiB = 1024L * v1MiB;
            long v1TiB = 1024L * v1GiB;
            long v1PiB = 1024L * v1TiB;
            long v1EiB = 1024L * v1PiB; // for Oracle LOB

            if (v == 0) {
                return "";
            } else if (v % v1EiB == 0) {
                return Long.toString(v / v1EiB) + " EiB";
            } else if ((v > v1EiB - 1024) && (v % v1EiB > (v1EiB - 1024L))) {
                long vv = 1L + v / v1EiB;
                return Long.toString(vv) + " EiB - " + (vv * v1EiB - v);

            } else if (v % v1PiB == 0) {
                return Long.toString(v / v1PiB) + " PiB";
            } else if ((v > v1PiB - 1024) && (v % v1PiB > (v1PiB - 1024L))) {
                long vv = 1L + v / v1PiB;
                return Long.toString(vv) + " PiB - " + (vv * v1PiB - v);

            } else if (v % v1TiB == 0) {
                return Long.toString(v / v1TiB) + " TiB";
            } else if ((v > v1TiB - 1024) && (v % v1TiB > (v1TiB - 1024L))) {
                long vv = 1L + v / v1TiB;
                return Long.toString(vv) + " TiB - " + (vv * v1TiB - v);

            } else if (v % v1GiB == 0) {
                return Long.toString(v / v1GiB) + " GiB";
            } else if ((v > v1GiB - 1024) && (v % v1GiB > (v1GiB - 1024L))) {
                long vv = 1L + v / v1GiB;
                return Long.toString(vv) + " GiB - " + (vv * v1GiB - v);

            } else if ((v % v1MiB) == 0L) {
                return Long.toString(v / v1MiB) + " MiB";
            } else if ((v > v1MiB - 1024) && (v % v1MiB) > (v1MiB - 1024L)) {
                long vv = 1L + (v / v1MiB);
                return Long.toString(vv) + " MiB - " + (vv * v1MiB - v);

            } else if ((v % v1KiB) == 0L) {
                return Long.toString(v / v1KiB) + " kiB";
            } else if ((v > v1KiB - 256) && (v % v1KiB) > (v1KiB - 256L)) {
                long vv = 1L + (v / v1KiB);
                return Long.toString(vv) + " kiB - " + (vv * v1KiB - v);

            } else {
                return String.valueOf(v);
            }
        } else if (value instanceof SQLFeatureNotSupportedException) {
            // return "[Method not yet implemented]";
            return "_n/a_";
        } else if (value instanceof Exception) {
            return "[Ex]";
        } else {
            return "[" + value.getClass().getSimpleName() + "]";
        }
    }

    // keep this?
    String simplify(String propertyName) {
        // save some effort...
        if ("isResetRequiredForDB2eWLM".equals(propertyName)) {
            return "is reset required for DB2eWWLM";
        }

        int idx = 0;
        if (propertyName.startsWith("supports")) {
            idx = 8;
        } else if (propertyName.startsWith("get")) {
            idx = 3;
        }

        StringBuilder sb = new StringBuilder();
        for (char c : propertyName.substring(idx).toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append(" ");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }

        String s = sb.toString()
                .replace("s q l ", "SQL")
                .replace("i d s", "IDS")
                .replace("r d b", "RDB")
                .replace("s c n", "SCN")
                .replace("d b2", "DB2")
                .replace("j c c", "jcc")
                .replace("u r l", "URL");

        return s.trim();
    }
}
