package com.coyotesong.database.erd;

import java.util.List;

/**
 * FIXME: add crossReference...
 * @param schemaName
 * @param tables
 */
public record SchemaInfoRecord(String schemaName, List<TableInfoRecord> tables) {
    public String getSchemaName() {
        return schemaName;
    }

    public List<TableInfoRecord> getTables() {
        return tables;
    }
}
