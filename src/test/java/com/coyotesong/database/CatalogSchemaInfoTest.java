package com.coyotesong.database;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test
 */
public class CatalogSchemaInfoTest {

    @Test
    public void testEmpty() {
        final CatalogSchemaSupport support = new CatalogSchemaSupport();
        for (CatalogSchemaSupport.Operation operation : CatalogSchemaSupport.Operation.values()) {
            assertThat(support.getSupport(operation), equalTo(CatalogSchemaSupport.Support.NONE));
        }
    }

    @Test
    public void testCatalogsOnly() {
        final CatalogSchemaSupport support = new CatalogSchemaSupport();
        for (CatalogSchemaSupport.Operation operation : CatalogSchemaSupport.Operation.values()) {
            support.setValue(operation, operation.getPattern().formatted("Catalogs"), true);
        }
        for (CatalogSchemaSupport.Operation operation : CatalogSchemaSupport.Operation.values()) {
            assertThat(support.getSupport(operation), equalTo(CatalogSchemaSupport.Support.CATALOGS_ONLY));
        }
    }

    @Test
    public void testSchemasOnly() {
        final CatalogSchemaSupport support = new CatalogSchemaSupport();
        for (CatalogSchemaSupport.Operation operation : CatalogSchemaSupport.Operation.values()) {
            support.setValue(operation, operation.getPattern().formatted("Schemas"), true);
        }
        for (CatalogSchemaSupport.Operation operation : CatalogSchemaSupport.Operation.values()) {
            assertThat(support.getSupport(operation), equalTo(CatalogSchemaSupport.Support.SCHEMAS_ONLY));
        }
    }

    @Test
    public void testBoth() {
        final CatalogSchemaSupport support = new CatalogSchemaSupport();
        for (CatalogSchemaSupport.Operation operation : CatalogSchemaSupport.Operation.values()) {
            support.setValue(operation, operation.getPattern().formatted("Catalogs"), true);
            support.setValue(operation, operation.getPattern().formatted("Schemas"), true);
        }
        for (CatalogSchemaSupport.Operation operation : CatalogSchemaSupport.Operation.values()) {
            assertThat(support.getSupport(operation), equalTo(CatalogSchemaSupport.Support.BOTH));
        }
    }
}
