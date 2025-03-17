/*
 * Copyright (c) 2024 Bear Giles <bgiles@coyotesong.com>.
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.coyotesong.database.erd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Create an ER Diagram using graphviz.
 *
 * Defaults:
 * - H2: 'UNAMED' catalog, 'INFORMATION_SCHEMA' and 'PUBLIC' schemas
 * - PostgreSQL: no catalog, 'information_schema", 'pg_catalog', and 'public' schemas
 */
public class EntityRelationDiagramUtil {
    private static final Logger LOG = LoggerFactory.getLogger(EntityRelationDiagramUtil.class);

    public EntityRelationDiagramUtil() {

    }

    String showColumns(ResultSetMetaData metaData) throws SQLException {
        List<String> columnNames = new ArrayList<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        return " -------------------- [ " + String.join(", ", columnNames) + " ]";
    }

    public SchemaInfoRecord scan(DatabaseMetaData dbmd) throws SQLException {
        try (ResultSet rs = dbmd.getCatalogs()) {
            LOG.info("catalogs...");
            // LOG.info(showColumns(rs.getMetaData()));
            while (rs.next()) {
                LOG.info(rs.getString("TABLE_CAT"));
            }
        }

        try (ResultSet rs = dbmd.getSchemas()) {
            LOG.info("schemas...");
            // LOG.info(showColumns(rs.getMetaData()));
            while (rs.next()) {
                LOG.info(rs.getString("TABLE_SCHEM"), rs.getString("TABLE_CATALOG"));
            }
        }

        // FIXME: scan catalogs and schemas found above
        return scanSchema(dbmd, "public");
    }

    public SchemaInfoRecord scanSchema(DatabaseMetaData dbmd, String schema) throws SQLException {
        final List<TableInfoRecord> tables = new ArrayList<>();

        try (ResultSet rs = dbmd.getTableTypes()) {
            while (rs.next()) {
                String tableType = rs.getString("TABLE_TYPE").toUpperCase();
                if (tableType.startsWith("SYSTEM")) {
                    continue;
                } else if (tableType.equals("SEQUENCE")) {
                    continue;
                } else if (tableType.endsWith("INDEX")) {
                    continue;
                } else {
                    tables.addAll(scanSchemaAndTableType(dbmd, schema, tableType));
                }
            }
        }

        return new SchemaInfoRecord(schema, tables);
    }

    public List<TableInfoRecord> scanSchemaAndTableType(DatabaseMetaData dbmd, String schema, String tableType) throws SQLException {
        final List<TableInfoRecord> tables = new ArrayList<>();
        try (ResultSet rs = dbmd.getTables(null, schema, "%", new String[]{ tableType })) {
            // LOG.info(showColumns(rs.getMetaData()));
            while (rs.next()) {
                final String tableName = rs.getString("TABLE_NAME");
                if ("flyway_schema_history".equals(tableName)) {
                    // explicitly skip this table
                } else {
                    // TABLE_CAT
                    tables.add(scanTable(dbmd, rs.getString("TABLE_SCHEM"), tableName));
                }
            }
        }

        return tables;
    }

    TableInfoRecord scanTable(DatabaseMetaData dbmd, String schemaName, String tableName) throws SQLException {
        final List<ColumnInfoRecord> columns = new ArrayList<>();
        final List<String> pkeys = new ArrayList<>();
        final List<ForeignKeyRecord> exportedKeys = new ArrayList<>();
        final List<ForeignKeyRecord> importedKeys = new ArrayList<>();


        // get list of primary keys
        try (ResultSet rs = dbmd.getPrimaryKeys(null, schemaName, tableName)) {
            // LOG.info(showColumns(rs.getMetaData()));
            while (rs.next()) {
                pkeys.add(rs.getString("COLUMN_NAME"));
            }
        }

        try (ResultSet rs = dbmd.getExportedKeys( null, schemaName, tableName)) {
            // LOG.info(showColumns(rs.getMetaData()));
            while (rs.next()) {
                // PKTABLE_CAT, UPDATE_RULE, DELETE_RULE, DEFERRABILITY
                exportedKeys.add(new ForeignKeyRecord(rs.getString("PKTABLE_SCHEM"), rs.getString("PKTABLE_NAME"),
                        rs.getString("PKCOLUMN_NAME"), rs.getString("FKTABLE_SCHEM"), rs.getString("FKTABLE_NAME"),
                        rs.getString("FKCOLUMN_NAME"), rs.getInt("KEY_SEQ"), rs.getString("PK_NAME"),
                        rs.getString("FK_NAME")));
            }
        }

        try (ResultSet rs = dbmd.getImportedKeys( null, schemaName, tableName)) {
            // LOG.info(showColumns(rs.getMetaData()));
            while (rs.next()) {
                // PKTABLE_CAT, UPDATE_RULE, DELETE_RULE, DEFERRABILITY
                importedKeys.add(new ForeignKeyRecord(rs.getString("PKTABLE_SCHEM"), rs.getString("PKTABLE_NAME"),
                        rs.getString("PKCOLUMN_NAME"), rs.getString("FKTABLE_SCHEM"), rs.getString("FKTABLE_NAME"),
                        rs.getString("FKCOLUMN_NAME"), rs.getInt("KEY_SEQ"), rs.getString("PK_NAME"),
                        rs.getString("FK_NAME")));
            }
        }

        try (ResultSet rs = dbmd.getColumns(null, schemaName, tableName, "%")) {
            // LOG.info(showColumns(rs.getMetaData()));
            while (rs.next()) {
                // column_size, buffer_length, digital_digits, nullable
                final String columnName = rs.getString("COLUMN_NAME");
                final String typeName = rs.getString("TYPE_NAME");
                Boolean isNullable = rs.getBoolean("IS_NULLABLE");
                if (rs.wasNull()) {
                    isNullable = null;
                }
                columns.add(new ColumnInfoRecord(columnName, typeName, isNullable, null));
            }
        }

        return new TableInfoRecord(schemaName, tableName, columns, pkeys, exportedKeys, importedKeys);
    }
}
