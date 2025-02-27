# Database Metadata Comparison

**This is prelimary work - it's only public for discussions during my job search **

This repo contains a simple app that launches a large number of databases (via TestContainers),
performs database metadata queries, then produces tables containing the results.

The next big step is a comparison of the standard types. I know from prior work that that will be... interesting.

Here's the current output (see [TestContainerDatabaseMetadataComparison](https://github.com/coyotesong/database-metadata-comparison/blob/main/src/test/java/com/coyotesong/database/TestContainerDatabaseMetadataComparison.java))

(updated 2025-02-26)


## Product Summary
| Name | Version | SQL Grammar | Isolation | Holdability | RowID Lifetime | SQL State Type |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| DB2/LINUXX8664 | 12 | EXTENDED | null | null | ROWID_UNSUPPORTED | null |
| Microsoft SQL Server | 16 | CORE | null | null | ROWID_UNSUPPORTED | null |
| Oracle | 21 | EXTENDED | null | null | ROWID_VALID_FOREVER | null |
| PostgreSQL | 17 | MINIMUM | null | null | [null] | null |
| H2 | 2 | CORE | null | null | ROWID_UNSUPPORTED | null |
| SQLite | 3 | CORE | null | null | [null] | null |



## Docker Images
| Name | Version | Docker Image Name |
|---|:---:|:---|
| DB2/LINUXX8664 | 12 | [icr.io/db2_community/db2](https://www.ibm.com/docs/en/db2/11.5?topic=deployments-db2-community-edition-docker):latest |
| Microsoft SQL Server | 16 | [mcr.microsoft.com/mssql/server](https://mcr.microsoft.com/en-us/product/mssql/server):latest |
| Oracle | 21 | [gvenzl/oracle-xe](https://hub.docker.com/r/gvenzl/oracle-xe):21-slim |
| PostgreSQL | 17 | [postgres](https://hub.docker.com/_/postgres):latest |
| H2 | 2 | n/a |
| SQLite | 3 | n/a |



## Drivers
| Name | Version | Driver Classname | Maven Coordinates |
|---|:---:|:---|:---|
| DB2/LINUXX8664 | 12 | com.ibm.db2.jcc.DB2Driver | [com.ibm.db2:jcc:4.32.28](https://central.sonatype.com/artifact/com.ibm.db2/jcc/4.32.28) |
| Microsoft SQL Server | 16 | com.microsoft.sqlserver.jdbc.SQLServerDriver | [com.microsoft.sqlserver:mssql-jdbc:12.4.0.0](https://central.sonatype.com/artifact/com.microsoft.sqlserver/mssql-jdbc/12.4.0.0) |
| Oracle | 21 | oracle.jdbc.driver.OracleDriver | [com.oracle.database.jdbc:ojdbc11:23.2.0.0.0](https://central.sonatype.com/artifact/com.oracle.database.jdbc/ojdbc11/23.2.0.0.0) |
| PostgreSQL | 17 | org.postgresql.Driver | [org.postgresql:postgresql:42.5.4](https://central.sonatype.com/artifact/org.postgresql/postgresql/42.5.4) |
| H2 | 2 | org.h2.Driver | [com.h2database:h2:2.2.220](https://central.sonatype.com/artifact/com.h2database/h2/2.2.220) |
| SQLite | 3 | org.sqlite.JDBC | [org.xerial:sqlite-jdbc:3.42.0.0](https://central.sonatype.com/artifact/org.xerial/sqlite-jdbc/3.42.0.0) |



## Catalog and Schema Support
|  | DB2/LINUXX8664 | Microsoft SQL Server | Oracle | PostgreSQL | H2 | SQLite |
|---|:---:|:---:|:---:|:---:|:---:|:---: |
| Data Manipulation | Schemas | Both | Schemas | Schemas | Both | None |
| Index Definitions | Schemas | Both | Schemas | Schemas | Both | None |
| Privilege Definitions | Schemas | Both | Schemas | Schemas | Both | None |
| Procedure Calls | Schemas | Both | Schemas | Schemas | Schemas | None |
| Table Definitions | Schemas | Both | Schemas | Schemas | Both | None |


## SQL Properties
| Name | Version | Full Tablename | Procedure Term | Quote | Escape | Extra | Nulls Sort | Identifier | Quoted Identifier |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| DB2/LINUXX8664 | 12 | schema.table | stored procedure | " | \ | '#', '@' | as HIGH | UPPER_CASE | UNKNOWN |
| Microsoft SQL Server | 16 | database.schema.table | stored procedure | " | \ | '#', '$', '@' | as LOW | MIXED_CASE | MIXED_CASE |
| Oracle | 21 | schematable | procedure | " | / | '#', '$' | as HIGH | UPPER_CASE | MIXED_CASE |
| PostgreSQL | 17 | schema.table | function | " | \ |  | as HIGH | LOWER_CASE | UNKNOWN |
| H2 | 2 | catalog.schema.table | procedure | " | \ |  | as LOW | UPPER_CASE | UNKNOWN |
| SQLite | 3 |  | not_implemented | " | \ |  | to START | MIXED_CASE | UNKNOWN |



## Limits
|  | DB2/LINUXX8664 | Microsoft SQL Server | Oracle | PostgreSQL | H2 | SQLite |
|---|:---:|:---:|:---:|:---:|:---:|:---: |
| max binary literal length | 4 kiB - 96 |  | 1 kiB - 24 |  |  |  |
| max catalog name length | 8 | 128 |  | 63 |  |  |
| max char literal length | 32 kiB - 96 |  | 2 kiB - 48 |  |  |  |
| max column name length | 128 | 128 | 128 | 63 |  |  |
| max columns in group by | 1 kiB - 12 |  |  |  |  |  |
| max columns in index | 16 | 16 | 32 | 32 |  |  |
| max columns in order by | 1 kiB - 12 |  |  |  |  |  |
| max columns in select | 1 kiB - 12 | 4 kiB |  |  |  |  |
| max columns in table | 1 kiB - 12 | 1 kiB | 1 kiB - 24 | 1600 |  |  |
| max connections |  | 32 kiB - 1 |  | 8 kiB |  |  |
| max cursor name length | 128 |  |  | 63 |  |  |
| max index length | 1 kiB | 900 |  |  |  |  |
| max logical lob size | 2 GiB - 1 | 2 GiB - 1 | [null] |  |  |  |
| max procedure name length | 128 | 128 | 128 | 63 |  |  |
| max row size | 32 kiB - 91 | 8060 |  | 1 GiB |  |  |
| max schema name length | 128 | 128 | 128 | 63 |  |  |
| max statement length | 2 MiB | 500 MiB | 64 kiB - 1 |  |  |  |
| max statements |  |  |  |  |  |  |
| max table name length | 128 | 128 | 128 | 63 |  |  |
| max tables in select |  | 256 |  |  |  |  |
| max user name length | 30 | 128 | 128 | 63 |  |  |


## DDL Statements
|  | DB2/LINUXX8664 | Microsoft SQL Server | Oracle | PostgreSQL | H2 | SQLite |
|---|:---:|:---:|:---:|:---:|:---:|:---: |
| data definition causes transaction commit |  |  | ☑ |  | ☑ |  |
| data definition ignored in transactions |  |  |  |  |  |  |
| alter table with add column | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| alter table with drop column |  | ☑ |  | ☑ | ☑ |  |
| data definition and data manipulation transactions | ☑ | ☑ | ☑ | ☑ |  | ☑ |
| data manipulation transactions only |  |  | ☑ |  | ☑ |  |
| get generated keys | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| mixed case identifiers |  | ☑ |  |  |  | ☑ |
| mixed case quoted identifiers | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| non nullable columns | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| sharding |  |  | ☑ |  |  |  |


## DML Statements
|  | DB2/LINUXX8664 | Microsoft SQL Server | Oracle | PostgreSQL | H2 | SQLite |
|---|:---:|:---:|:---:|:---:|:---:|:---: |
| all procedures are callable |  | ☑ |  | ☑ | ☑ |  |
| all tables are selectable |  | ☑ |  | ☑ | ☑ | ☑ |
| generated key always returned | ☑ | ☑ |  | ☑ | ☑ | [null] |
| null plus non null is null | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| batch updates | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| correlated subqueries | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| expressions in order by | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| full outer joins | ☑ | ☑ | ☑ | ☑ |  | ☑ |
| group by | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| group by beyond select | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| group by unrelated | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| like escape clause | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| limited outer joins | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| multiple open results | ☑ |  |  |  |  |  |
| multiple result sets | ☑ | ☑ |  | ☑ |  |  |
| order by unrelated | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| outer joins | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| select for update | ☑ |  | ☑ | ☑ | ☑ |  |
| subqueries in comparisons | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| subqueries in exists | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| subqueries in ins | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| subqueries in quantifieds | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| union | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| union all | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |


## Transactions
|  | DB2/LINUXX8664 | Microsoft SQL Server | Oracle | PostgreSQL | H2 | SQLite |
|---|:---:|:---:|:---:|:---:|:---:|:---: |
| auto commit failure closes all result sets |  |  |  |  |  | [null] |
| multiple transactions | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| open cursors across commit | ☑ |  |  |  |  |  |
| open cursors across rollback |  |  |  |  |  |  |
| open statements across commit | ☑ | ☑ |  | ☑ | ☑ |  |
| open statements across rollback | ☑ | ☑ |  | ☑ | ☑ |  |
| savepoints | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| transactions | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |


## Stored Procedures
|  | DB2/LINUXX8664 | Microsoft SQL Server | Oracle | PostgreSQL | H2 | SQLite |
|---|:---:|:---:|:---:|:---:|:---:|:---: |
| named parameters | ☑ | ☑ | ☑ |  |  | ☑ |
| positioned delete | ☑ | ☑ |  |  |  |  |
| positioned update | ☑ | ☑ |  |  |  |  |
| ref cursors | ☑ |  | [null] | ☑ |  |  |
| stored functions using call syntax |  | ☑ | ☑ | ☑ | ☑ | [null] |
| stored procedures | ☑ | ☑ | ☑ | ☑ |  |  |


## Other Boolean Properties
|                                        | DB2/LINUXX8664 | Microsoft SQL Server | Oracle | PostgreSQL | H2 | SQLite |
|----------------------------------------|:---:|:---:|:---:|:---:|:---:|:---: |
| does max row size include blobs        |  |  | ☑ |  |  |  |
| is catalog at start                    | ☑ | ☑ |  | ☑ | ☑ | ☑ |
| is compatible122 or greater            |  |  | [null] |  |  |  |
| is IDS database ansi compliant         | [null] |  |  |  |  |  |
| is IDS database logging                | [null] |  |  |  |  |  |
| is reset required fo RDB2eWLM          | ☑ |  |  |  |  |  |
| is SQLrowset cursor support enabled    | ☑ |  |  |  |  |  |
| is server big SCN                      |  |  | [null] |  |  |  |
| locators update copy                   | ☑ | ☑ | ☑ | ☑ |  |  |
| binary xml format                      | ☑ |  |  |  |  |  |
| column aliasing                        | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| convert                                |  | ☑ |  |  | ☑ |  |
| DB2 progressive streaming              | ☑ |  |  |  |  |  |
| different table correlation names      |  |  | ☑ |  |  |  |
| integrity enhancement facility         | ☑ |  | ☑ | ☑ | ☑ |  |
| RDB implicit commit                    |  |  |  |  |  |  |
| SQLrowset cursors                      |  |  |  |  |  |  |
| statement pooling                      |  |  | ☑ |  |  |  |
| table correlation names                | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| use fixed length clob substr statement |  |  |  |  |  |  |
| uses local file per table              |  |  |  |  |  |  |
| uses local files                       |  |  |  |  | ☑ | ☑ |


## Other Properties
|  | DB2/LINUXX8664 | Microsoft SQL Server | Oracle | PostgreSQL | H2 | SQLite |
|---|:---:|:---:|:---:|:---:|:---:|:---: |
| access banner |  |  | [null] |  |  |  |
| audit banner |  |  | [null] |  |  |  |
| database compatibility level |  | 160 |  |  |  |  |
| database functional level | s241216103 |  |  |  |  |  |
| jcc driver build number | 28 |  |  |  |  |  |
| jcc driver build number | 28 |  |  |  |  |  |
| lob max length |  |  | 8589934592 GiB - 1 |  |  |  |
| lob precision |  |  | -1 |  |  |  |
| trace id |  |  |  |  |  |  |
| trace object name |  |  |  |  | dbMeta0 |  |

