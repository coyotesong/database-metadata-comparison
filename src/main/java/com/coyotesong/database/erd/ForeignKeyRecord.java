package com.coyotesong.database.erd;

public record ForeignKeyRecord(String pk_schema, String pk_table, String pk_column, String fk_schema, String fk_table, String fk_column, int keySeq, String pkName, String fkName) {
    public String getPkTable() {
        return pk_table;
    }

    public String getFkTable() {
        return fk_table;
    }
}
