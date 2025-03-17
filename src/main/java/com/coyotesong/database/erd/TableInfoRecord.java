package com.coyotesong.database.erd;

import java.util.List;

public record TableInfoRecord(String schemaName, String tableName, List<ColumnInfoRecord> columns, List<String> pkeys, List<ForeignKeyRecord> exportedKeys, List<ForeignKeyRecord> importedKeys) {
    public String getSchemaName() {
        return schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public List<ColumnInfoRecord> getColumns() {
        return columns;
    }

    public List<ForeignKeyRecord> getExportedKeys() {
        return exportedKeys;
    }

    public List<ForeignKeyRecord> getImportedKeys() {
        return importedKeys;
    }
}

