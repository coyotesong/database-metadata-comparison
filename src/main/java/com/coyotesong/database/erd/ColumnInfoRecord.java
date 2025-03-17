package com.coyotesong.database.erd;

public record ColumnInfoRecord(String columnName, String typeName, Boolean isNullable, Integer pkeySeq) {
    public String getColumnName() {
        return columnName;
    }

    public String getTypeName() {
        return typeName;
    }
}
