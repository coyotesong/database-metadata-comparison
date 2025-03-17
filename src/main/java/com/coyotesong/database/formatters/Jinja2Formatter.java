package com.coyotesong.database.formatters;

import com.coyotesong.database.DatabaseComparisons;
import com.coyotesong.database.config.ExternalRepositories;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.interpret.RenderResult;
import com.hubspot.jinjava.interpret.TemplateError;
import com.hubspot.jinjava.loader.ClasspathResourceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of OutputFormatter using Jinja2
 * <p>
 * Generating the output using a template engine instead of manually coded
 * content has several benefits. First it allows a more natural approach,
 * second it allows us to quickly implement new formats with no change to
 * the existing code.
 * </p>
 * <p>
 * {@see https://product.hubspot.com/blog/jinjava-a-jinja-for-your-java}
 * </p>
 */
public class Jinja2Formatter extends AbstractOutputFormatter {
    private static final Logger LOG = LoggerFactory.getLogger(Jinja2Formatter.class);

    private final ExternalRepositories repos;

    public Jinja2Formatter(ExternalRepositories repos, DatabaseComparisons databases) {
        super(databases);
        this.repos = repos;
    }

    // for testing
    protected Jinja2Formatter() {
        super(new DatabaseComparisons());
        this.repos = new ExternalRepositories();
    }

    public void formatGeneral(PrintWriter pw) throws IOException {
        ClasspathResourceLocator locator = new ClasspathResourceLocator();
        JinjavaConfig config = new JinjavaConfig();

        Jinjava jinjava = new Jinjava(config);
        jinjava.setResourceLocator(locator);
        JinjavaInterpreter interpreter = JinjavaInterpreter.getCurrent();

        Map<String, Object> context = new HashMap<>();
        context.put("databases", databases);

        // FIXME - found other documentation saying this isn't necessary.
        String template = locator.getString(
                "templates/markdown/general.md.j2", StandardCharsets.UTF_8, interpreter);

        RenderResult result = jinjava.renderForResult(template, context);

        for (TemplateError error : result.getErrors()) {
            LOG.warn("{}: line {}: {}", error.getSeverity(), error.getLineno(), error.getMessage());
        }

        String renderedTemplate = result.getOutput();
        pw.print(renderedTemplate);
    }

    //
    // The jinja engine can use user-provided static functions. The example given in
    // the documentation is the java static method
    //
    //    public List[] Lists.newArrayList(Object... content);
    //
    // is registered as
    //
    //   jinjava.getGlobalContext().register(
    //       new ELFunctionDefinition("fn", "list", Lists.class, "newArrayList", Object[].class));
    //
    // (where the 'Object[].class' is actually vargargs)
    //
    // and executed as
    //
    //    {% set mylist = fn:list() %}
    //
    // This means that we'll need to add static mmethods to 'DatabaseComparisons' and all that entails
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
    // a 'filter' is saomething like '| toLower'
    //  it implements com.hubspot.jinjava.lib.Filter
    //
    //     jinjava.getGlobalContext().registerFilter(new MyAwesomeFilter());
    //
    //  // define any number of classes which extend Importable
    //  jinjava.getGlobalContext().registerClasses(Class<? extends Importable>... classes);
}

