package com.coyotesong.database.formatters;

import com.coyotesong.database.Pivots;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.interpret.RenderResult;
import com.hubspot.jinjava.interpret.TemplateError;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Implementation of OutputFormatter using Jinja2
 * <p>
 * Generating the output using a template engine instead of manually coded
 * content has several benefits. First it allows a more natural approach,
 * second it allows us to quickly implement new formats with no change to
 * the existing code.
 * </p>
 * <p>
 * You can avoid static methods via Guice injection. Something similar for
 * spring? {@see https://github.com/HubSpot/jinjava/blob/master/src/main/java/com/hubspot/jinjava/lib/fn/InjectedContextFunctionProxy.java}
 * </p>
 * <p>
 * {@see https://product.hubspot.com/blog/jinjava-a-jinja-for-your-java}
 * </p>
 */
public class Jinja2Formatter {
    private static final Logger LOG = LoggerFactory.getLogger(Jinja2Formatter.class);

    // private static final String HASHMARK = Character.toString(0x10102); // hash (X) mark
    // private static final String CHECKMARK = Character.toString(0x1F5F8);
    // private static final String BALLOT_BOX_WITH_BOLD_CHECKMARK = Character.toString(0x1F5F9);
    private static final String BALLOT_BOX_WITH_CHECKMARK = Character.toString(0x2611);

    private final Pivots pivots;

    public Jinja2Formatter(Pivots pivots) {
        this.pivots = pivots;
    }

    /**
     * Read contents from file in classpath
     * <p>
     * Jinjava can also load a file from the classpath but it also requires the 'interpreter'
     * and does not provide the line separation we want for our error messages.
     * </p>
     * @param classpath classpath of text file
     * @return list of strings containing contents of file
     * @throws IOException
     */
    public List<String> readFromClasspath(String classpath) throws IOException {
        // read from classpath
        // we capture individual lines for better error reports
        final List<String> lines = new ArrayList<>();
        final URL url = Thread.currentThread().getContextClassLoader().getResource(classpath);
        try (InputStream is = url.openStream();
             BufferedInputStream bis = new BufferedInputStream(is);
             Reader r = new InputStreamReader(bis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(r)) {

            br.lines().forEach(lines::add);
        }
        return lines;
    }

    public String format(String templateName) throws IOException {
        final Map<String, Object> context = new HashMap<>();

        final String filename = "templates/" + templateName;
        final List<String> lines = readFromClasspath(filename);
        context.put("source", lines);

        try (StringWriter sw = new StringWriter()) {
            // note: won't work with multicharacter line separators. (Windows?)
            final String template = Strings.join(lines, System.lineSeparator().charAt(0));

            final Jinjava jinjava = JinjavaFactory.newInstance(pivots, CharacterEscape.MARKDOWN);
            final RenderResult result = jinjava.renderForResult(template, context);

            for (TemplateError error : result.getErrors()) {
                // could also use 'error.getSourceTemplate().get()' and then call 'split'
                LOG.warn("{}: line {}: {}: {}", error.getSeverity(), error.getLineno(), error.getMessage(), lines.get(error.getLineno()));
            }

            sw.write(result.getOutput());
            return sw.toString();
        }
    }
}
