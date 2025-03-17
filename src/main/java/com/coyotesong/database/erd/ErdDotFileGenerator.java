package com.coyotesong.database.erd;

import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.loader.ClasspathResourceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ErdDotFileGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(ErdDotFileGenerator.class);

    private static final ClasspathResourceLocator LOCATOR = new ClasspathResourceLocator();
    private static final JinjavaConfig CONFIG = new JinjavaConfig();

    public void writeERD(JdbcDatabaseContainer<?> db, String filename) {
        try (Connection conn = db.createConnection("")) {
            SchemaInfoRecord schemaInfoRecord = new EntityRelationDiagramUtil().scan(conn.getMetaData());
            try (Writer w = new FileWriter(filename)) {
                w.write(generate(schemaInfoRecord));
            } catch (IOException e) {
                LOG.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
            }
        } catch (SQLException e) {
            LOG.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    public String generate(SchemaInfoRecord schemaInfoRecord) throws IOException {
        final Jinjava jinjava = new Jinjava(CONFIG);
        jinjava.setResourceLocator(LOCATOR);
        JinjavaInterpreter interpreter = JinjavaInterpreter.getCurrent();

        final Map<String, Object> context = new HashMap<>();
        context.put("schema", schemaInfoRecord);

        final String template = LOCATOR.getString(
                "templates/dot/erd.dot.j2", StandardCharsets.UTF_8, interpreter);

        try (StringWriter w = new StringWriter()) {
            w.write(jinjava.render(template, context));

            return w.toString();
        }
    }
}
