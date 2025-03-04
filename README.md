# Database Metadata Comparison

**This is prelimary work - it's only public for discussions during my job search **

This repo contains a simple app that launches a large number of databases (via TestContainers),
performs database metadata queries, then produces tables containing the results.

The next big step is a comparison of the standard types. I know from prior work that that will be... interesting.

Here's the current output (see [TestContainerDatabaseMetadataComparison](https://github.com/coyotesong/database-metadata-comparison/blob/main/src/test/java/com/coyotesong/database/TestContainerDatabaseMetadataComparison.java))

(updated 2025-03-03 - this will be split soon...)

## Product Summary
| Name | Version | SQL Grammar | Isolation | Holdability | RowID Lifetime | SQL State Type |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| DB2/LINUXX8664 | 12 | EXTENDED | READ_COMMITTED | HOLDS | UNSUPPORTED | SQL99 |
| Microsoft SQL Server | 16 | CORE | READ_COMMITTED | HOLDS | UNSUPPORTED | SQL99 |
| Oracle | 21 | EXTENDED | READ_COMMITTED | HOLDS | FOREVER | UNKNOWN |
| PostgreSQL | 17 | MINIMUM | READ_COMMITTED | HOLDS | UNSUPPORTED | SQL99 |
| H2 | 2 | CORE | READ_COMMITTED | CLOSES | UNSUPPORTED | SQL99 |
| SQLite | 3 | CORE | SERIALIZABLE | CLOSES | UNSUPPORTED | SQL99 |



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
| DB2/LINUXX8664 | 12 | com.ibm.db2.jcc.DB2Driver | [com.ibm.db2:com.ibm.db2:4.32.28](https://central.sonatype.com/artifact/com.ibm.db2/com.ibm.db2/4.32.28) |
| Microsoft SQL Server | 16 | com.microsoft.sqlserver.jdbc.SQLServerDriver | [com.microsoft.sqlserver:com.microsoft.sqlserver:12.4.0.0](https://central.sonatype.com/artifact/com.microsoft.sqlserver/com.microsoft.sqlserver/12.4.0.0) |
| Oracle | 21 | oracle.jdbc.driver.OracleDriver | [com.oracle.database.jdbc:com.oracle.database.jdbc:23.2.0.0.0](https://central.sonatype.com/artifact/com.oracle.database.jdbc/com.oracle.database.jdbc/23.2.0.0.0) |
| PostgreSQL | 17 | org.postgresql.Driver | [org.postgresql:org.postgresql:42.5.4](https://central.sonatype.com/artifact/org.postgresql/org.postgresql/42.5.4) |
| H2 | 2 | org.h2.Driver | [com.h2database:com.h2database:2.2.220](https://central.sonatype.com/artifact/com.h2database/com.h2database/2.2.220) |
| SQLite | 3 | org.sqlite.JDBC | [org.xerial:org.xerial:3.42.0.0](https://central.sonatype.com/artifact/org.xerial/org.xerial/3.42.0.0) |



## ClientInfo Properties
| Name | ClientInfo Properties |
|---|---|
| DB2/LINUXX8664 | ApplicationName, ClientAccountingInformation, ClientHostname, ClientUser |
| Microsoft SQL Server |  |
| Oracle |  |
| PostgreSQL | ApplicationName |
| H2 | numServers |
| SQLite |  |



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
|---|:---:|:--------------------:|:---:|:---:|:---:|:---: |
| max binary literal length | 4 kiB - 96 |                      | 1 kiB - 24 |  |  |  |
| max catalog name length | 8 |         128          |  | 63 |  |  |
| max char literal length | 32 kiB - 96 |                      | 2 kiB - 48 |  |  |  |
| max column name length | 128 |         128          | 128 | 63 |  |  |
| max columns in group by | 1 kiB - 12 |                      |  |  |  |  |
| max columns in index | 16 |          16          | 32 | 32 |  |  |
| max columns in order by | 1 kiB - 12 |                      |  |  |  |  |
| max columns in select | 1 kiB - 12 |        4 kiB         |  |  |  |  |
| max columns in table | 1 kiB - 12 |        1 kiB         | 1 kiB - 24 | 1600 |  |  |
| max connections |  |      32 kiB - 1      |  | 8 kiB |  |  |
| max cursor name length | 128 |                      |  | 63 |  |  |
| max index length | 1 kiB |         900          |  |  |  |  |
| max logical lob size | 2 GiB - 1 |      2 GiB - 1       | [no access] |  |  |  |
| max procedure name length | 128 |         128          | 128 | 63 |  |  |
| max row size | 32 kiB - 91 |    8 kiB - 132    |  | 1 GiB |  |  |
| max schema name length | 128 |         128          | 128 | 63 |  |  |
| max statement length | 2 MiB |       500 MiB        | 64 kiB - 1 |  |  |  |
| max statements |  |                      |  |  |  |  |
| max table name length | 128 |         128          | 128 | 63 |  |  |
| max tables in select |  |         256          |  |  |  |  |
| max user name length | 30 |         128          | 128 | 63 |  |  |


## Table Types
|  | DB2/LINUXX8664 | Microsoft SQL Server | Oracle | PostgreSQL | H2 | SQLite |
|---|:---:|:---:|:---:|:---:|:---:|:---: |
| ALIAS |  | ☑ |  |  |  |  |  |
| BASE TABLE |  |  |  |  |  | ☑ |  |
| FOREIGN TABLE |  |  |  |  | ☑ |  |  |
| GLOBAL TEMPORARY |  |  |  |  |  | ☑ | ☑ |
| HIERARCHY TABLE |  | ☑ |  |  |  |  |  |
| INDEX |  |  |  |  | ☑ |  |  |
| INOPERATIVE VIEW |  | ☑ |  |  |  |  |  |
| LOCAL TEMPORARY |  |  |  |  |  | ☑ |  |
| MATERIALIZED QUERY TABLE |  | ☑ |  |  |  |  |  |
| MATERIALIZED VIEW |  |  |  |  | ☑ |  |  |
| NICKNAME |  | ☑ |  |  |  |  |  |
| PARTITIONED INDEX |  |  |  |  | ☑ |  |  |
| PARTITIONED TABLE |  |  |  |  | ☑ |  |  |
| SEQUENCE |  |  |  |  | ☑ |  |  |
| SYNONYM |  | ☑ |  | ☑ |  | ☑ |  |
| SYSTEM INDEX |  |  |  |  | ☑ |  |  |
| SYSTEM TABLE |  | ☑ | ☑ |  | ☑ |  | ☑ |
| SYSTEM TOAST INDEX |  |  |  |  | ☑ |  |  |
| SYSTEM TOAST TABLE |  |  |  |  | ☑ |  |  |
| SYSTEM VIEW |  |  |  |  | ☑ |  |  |
| TABLE |  | ☑ | ☑ | ☑ | ☑ |  | ☑ |
| TEMPORARY INDEX |  |  |  |  | ☑ |  |  |
| TEMPORARY SEQUENCE |  |  |  |  | ☑ |  |  |
| TEMPORARY TABLE |  |  |  |  | ☑ |  |  |
| TEMPORARY VIEW |  |  |  |  | ☑ |  |  |
| TYPE |  |  |  |  | ☑ |  |  |
| TYPED TABLE |  | ☑ |  |  |  |  |  |
| TYPED VIEW |  | ☑ |  |  |  |  |  |
| VIEW |  | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |


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
| generated key always returned | ☑ | ☑ |  | ☑ | ☑ | [not supported] |
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
| auto commit failure closes all result sets |  |  |  |  |  | [not supported] |
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
| ref cursors | ☑ |  | [no access] | ☑ |  |  |
| stored functions using call syntax |  | ☑ | ☑ | ☑ | ☑ | [not supported] |
| stored procedures | ☑ | ☑ | ☑ | ☑ |  |  |


## Other Boolean Properties
|  | DB2/LINUXX8664 | Microsoft SQL Server | Oracle | PostgreSQL | H2 | SQLite |
|---|:---:|:---:|:---:|:---:|:---:|:---: |
| does max row size include blobs |  |  | ☑ |  |  |  |
| is catalog at start | ☑ | ☑ |  | ☑ | ☑ | ☑ |
| is compatible122 or greater |  |  | [no access] |  |  |  |
| is IDS database ansi compliant | [not supported] |  |  |  |  |  |
| is IDS database logging | [not supported] |  |  |  |  |  |
| is reset required for DB2eWWLM | ☑ |  |  |  |  |  |
| is SQLrowset cursor support enabled | ☑ |  |  |  |  |  |
| is server big SCN |  |  | [no access] |  |  |  |
| locators update copy | ☑ | ☑ | ☑ | ☑ |  |  |
| binary xml format | ☑ |  |  |  |  |  |
| column aliasing | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| convert |  | ☑ |  |  | ☑ |  |
| DB2 progressive streaming | ☑ |  |  |  |  |  |
| different table correlation names |  |  | ☑ |  |  |  |
| integrity enhancement facility | ☑ |  | ☑ | ☑ | ☑ |  |
| RDB implicit commit |  |  |  |  |  |  |
| SQLrowset cursors |  |  |  |  |  |  |
| statement pooling |  |  | ☑ |  |  |  |
| table correlation names | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| use fixed length clob substr statement |  |  |  |  |  |  |
| uses local file per table |  |  |  |  |  |  |
| uses local files |  |  |  |  | ☑ | ☑ |


## Other Properties
|  | DB2/LINUXX8664 | Microsoft SQL Server | Oracle | PostgreSQL | H2 | SQLite |
|---|:---:|:---:|:---:|:---:|:---:|:---: |
| access banner |  |  | [no access] |  |  |  |
| audit banner |  |  | [no access] |  |  |  |
| database compatibility level |  | 160 |  |  |  |  |
| database functional level | s241216103 |  |  |  |  |  |
| jcc driver build number | 28 |  |  |  |  |  |
| jcc driver build number | 28 |  |  |  |  |  |
| lob max length |  |  | 8 EiB - 1 |  |  |  |
| lob precision |  |  | -1 |  |  |  |
| trace id |  |  |  |  |  |  |
| trace object name |  |  |  |  | dbMeta0 |  |


## SQL Keywords
|  | DB2/LINUXX8664 | Microsoft SQL Server | Oracle | PostgreSQL | H2 | SQLite |
|---|:---:|:---:|:---:|:---:|:---:|:---: |
| ABORT |  |  |  |  | ☑ |  | ☑ |
| ABSENT |  |  |  |  | ☑ |  |  |
| ACCESS |  |  |  | ☑ | ☑ |  |  |
| ACTION |  |  |  |  |  |  | ☑ |
| ADD |  |  | ☑ | ☑ |  |  |  |
| AFTER |  | ☑ |  |  |  |  | ☑ |
| AGGREGATE |  |  |  |  | ☑ |  |  |
| ALIAS |  | ☑ |  |  |  |  |  |
| ALL |  |  | ☑ |  |  |  |  |
| ALLOW |  | ☑ |  |  |  |  |  |
| ALL_PL_SQL_RESERVED_ WORDS |  |  |  | ☑ |  |  |  |
| ALSO |  |  |  |  | ☑ |  |  |
| ALTER |  |  | ☑ | ☑ |  |  |  |
| ANALYSE |  |  |  |  | ☑ |  |  |
| ANALYZE |  |  |  |  | ☑ |  | ☑ |
| AND |  |  | ☑ |  |  |  |  |
| ANY |  |  | ☑ |  |  |  |  |
| APPLICATION |  | ☑ |  |  |  |  |  |
| AS |  |  | ☑ |  |  |  |  |
| ASC |  |  | ☑ |  |  |  |  |
| ASSOCIATE |  | ☑ |  |  |  |  |  |
| ASUTIME |  | ☑ |  |  |  |  |  |
| ATTACH |  |  |  |  | ☑ |  | ☑ |
| AUDIT |  | ☑ |  | ☑ |  |  |  |
| AUTHORIZATION |  |  | ☑ |  |  |  |  |
| AUTOINCREMENT |  |  |  |  |  |  | ☑ |
| AUX |  | ☑ |  |  |  |  |  |
| AUXILIARY |  | ☑ |  |  |  |  |  |
| BACKUP |  |  | ☑ |  |  |  |  |
| BACKWARD |  |  |  |  | ☑ |  |  |
| BEFORE |  | ☑ |  |  |  |  | ☑ |
| BEGIN |  |  | ☑ |  |  |  |  |
| BETWEEN |  |  | ☑ |  |  |  |  |
| BINARY |  | ☑ |  |  |  |  |  |
| BIT |  |  |  |  | ☑ |  |  |
| BREAK |  |  | ☑ |  |  |  |  |
| BROWSE |  |  | ☑ |  |  |  |  |
| BUFFERPOOL |  | ☑ |  |  |  |  |  |
| BULK |  |  | ☑ |  |  |  |  |
| BY |  |  | ☑ |  |  |  |  |
| CACHE |  | ☑ |  |  | ☑ |  |  |
| CALL |  | ☑ |  |  |  |  |  |
| CALLED |  | ☑ |  |  |  |  |  |
| CAPTURE |  | ☑ |  |  |  |  |  |
| CARDINALITY |  | ☑ |  |  |  |  |  |
| CASCADE |  |  | ☑ |  |  |  | ☑ |
| CASE |  |  | ☑ |  |  |  |  |
| CCSID |  | ☑ |  |  |  |  |  |
| CHECK |  |  | ☑ |  |  |  |  |
| CHECKPOINT |  |  | ☑ |  | ☑ |  |  |
| CLASS |  |  |  |  | ☑ |  |  |
| CLOSE |  |  | ☑ |  |  |  |  |
| CLUSTER |  | ☑ |  | ☑ | ☑ |  |  |
| CLUSTERED |  |  | ☑ |  |  |  |  |
| COALESCE |  |  | ☑ |  |  |  |  |
| COLLATE |  |  | ☑ |  |  |  |  |
| COLLECTION |  | ☑ |  |  |  |  |  |
| COLLID |  | ☑ |  |  |  |  |  |
| COLUMN |  |  | ☑ | ☑ |  |  |  |
| COLUMNS |  |  |  |  | ☑ |  |  |
| COMMENT |  | ☑ |  | ☑ | ☑ |  |  |
| COMMENTS |  |  |  |  | ☑ |  |  |
| COMMIT |  |  | ☑ |  |  |  |  |
| COMPRESS |  |  |  | ☑ |  |  |  |
| COMPRESSION |  |  |  |  | ☑ |  |  |
| COMPUTE |  |  | ☑ |  |  |  |  |
| CONCAT |  | ☑ |  |  |  |  |  |
| CONCURRENTLY |  |  |  |  | ☑ |  |  |
| CONDITION |  | ☑ |  |  |  |  |  |
| CONDITIONAL |  |  |  |  | ☑ |  |  |
| CONFIGURATION |  |  |  |  | ☑ |  |  |
| CONFLICT |  |  |  |  | ☑ |  | ☑ |
| CONNECT |  |  |  | ☑ |  |  |  |
| CONNECTION |  |  |  |  | ☑ |  |  |
| CONSTRAINT |  |  | ☑ |  |  |  |  |
| CONTAINS |  | ☑ | ☑ |  |  |  |  |
| CONTAINSTABLE |  |  | ☑ |  |  |  |  |
| CONTENT |  |  |  |  | ☑ |  |  |
| CONTINUE |  |  | ☑ |  |  |  |  |
| CONVERSION |  |  |  |  | ☑ |  |  |
| CONVERT |  |  | ☑ |  |  |  |  |
| COPY |  |  |  |  | ☑ |  |  |
| COST |  |  |  |  | ☑ |  |  |
| COUNT_BIG |  | ☑ |  |  |  |  |  |
| CREATE |  |  | ☑ |  |  |  |  |
| CROSS |  |  | ☑ |  |  |  |  |
| CSV |  |  |  |  | ☑ |  |  |
| CURRENT |  |  | ☑ |  |  |  |  |
| CURRENT_CATALOG |  |  |  |  | ☑ | ☑ |  |
| CURRENT_DATE |  |  | ☑ |  |  |  |  |
| CURRENT_LC_CTYPE |  | ☑ |  |  |  |  |  |
| CURRENT_PATH |  | ☑ |  |  |  |  |  |
| CURRENT_SCHEMA |  |  |  |  | ☑ | ☑ |  |
| CURRENT_SERVER |  | ☑ |  |  |  |  |  |
| CURRENT_TIME |  |  | ☑ |  |  |  |  |
| CURRENT_TIMESTAMP |  |  | ☑ |  |  |  |  |
| CURRENT_TIMEZONE |  | ☑ |  |  |  |  |  |
| CURRENT_USER |  |  | ☑ |  |  |  |  |
| CURSOR |  |  | ☑ |  |  |  |  |
| CYCLE |  | ☑ |  |  |  |  |  |
| DATA |  | ☑ |  |  |  |  |  |
| DATABASE |  | ☑ | ☑ |  | ☑ |  | ☑ |
| DATE |  |  |  | ☑ |  |  |  |
| DAYS |  | ☑ |  |  |  |  |  |
| DB2GENERAL |  | ☑ |  |  |  |  |  |
| DB2GENRL |  | ☑ |  |  |  |  |  |
| DB2SQL |  | ☑ |  |  |  |  |  |
| DBCC |  |  | ☑ |  |  |  |  |
| DBINFO |  | ☑ |  |  |  |  |  |
| DEALLOCATE |  |  | ☑ |  |  |  |  |
| DECLARE |  |  | ☑ |  |  |  |  |
| DEFAULT |  |  | ☑ |  |  |  |  |
| DEFAULTS |  | ☑ |  |  |  |  |  |
| DEFERRABLE |  |  |  |  |  |  | ☑ |
| DEFERRED |  |  |  |  |  |  | ☑ |
| DEFINITION |  | ☑ |  |  |  |  |  |
| DELETE |  |  | ☑ |  |  |  |  |
| DELIMITER |  |  |  |  | ☑ |  |  |
| DELIMITERS |  |  |  |  | ☑ |  |  |
| DENY |  |  | ☑ |  |  |  |  |
| DEPENDS |  |  |  |  | ☑ |  |  |
| DESC |  |  | ☑ |  |  |  | ☑ |
| DETACH |  |  |  |  | ☑ |  | ☑ |
| DETERMINISTIC |  | ☑ |  |  |  |  |  |
| DICTIONARY |  |  |  |  | ☑ |  |  |
| DISABLE |  |  |  |  | ☑ |  |  |
| DISALLOW |  | ☑ |  |  |  |  |  |
| DISCARD |  |  |  |  | ☑ |  |  |
| DISK |  |  | ☑ |  |  |  |  |
| DISTINCT |  |  | ☑ |  |  |  |  |
| DISTRIBUTED |  |  | ☑ |  |  |  |  |
| DO |  | ☑ |  |  | ☑ |  |  |
| DOCUMENT |  |  |  |  | ☑ |  |  |
| DOUBLE |  |  | ☑ |  |  |  |  |
| DROP |  |  | ☑ | ☑ |  |  |  |
| DSNHATTR |  | ☑ |  |  |  |  |  |
| DSSIZE |  | ☑ |  |  |  |  |  |
| DUMP |  |  | ☑ |  |  |  |  |
| DYNAMIC |  | ☑ |  |  |  |  |  |
| EACH |  | ☑ |  |  |  |  |  |
| EDITPROC |  | ☑ |  |  |  |  |  |
| ELSE |  |  | ☑ |  |  |  |  |
| ELSEIF |  | ☑ |  |  |  |  |  |
| EMPTY |  |  |  |  | ☑ |  |  |
| ENABLE |  |  |  |  | ☑ |  |  |
| ENCODING |  | ☑ |  |  | ☑ |  |  |
| ENCRYPTED |  |  |  |  | ☑ |  |  |
| END |  |  | ☑ |  |  |  |  |
| END-EXEC1 |  | ☑ |  |  |  |  |  |
| ENUM |  |  |  |  | ☑ |  |  |
| ERASE |  | ☑ |  |  |  |  |  |
| ERRLVL |  |  | ☑ |  |  |  |  |
| ERROR |  |  |  |  | ☑ |  |  |
| ESCAPE |  |  | ☑ |  |  |  |  |
| EVENT |  |  |  |  | ☑ |  |  |
| EXCEPT |  |  | ☑ |  |  |  |  |
| EXCLUDING |  | ☑ |  |  |  |  |  |
| EXCLUSIVE |  |  |  | ☑ | ☑ |  | ☑ |
| EXEC |  |  | ☑ |  |  |  |  |
| EXECUTE |  |  | ☑ |  |  |  |  |
| EXISTS |  |  | ☑ |  |  |  |  |
| EXIT |  | ☑ | ☑ |  |  |  |  |
| EXPLAIN |  |  |  |  | ☑ |  | ☑ |
| EXPRESSION |  |  |  |  | ☑ |  |  |
| EXTENSION |  |  |  |  | ☑ |  |  |
| EXTERNAL |  |  | ☑ |  |  |  |  |
| FAIL |  |  |  |  |  |  | ☑ |
| FAMILY |  |  |  |  | ☑ |  |  |
| FENCED |  | ☑ |  |  |  |  |  |
| FETCH |  |  | ☑ |  |  |  |  |
| FIELDPROC |  | ☑ |  |  |  |  |  |
| FILE |  | ☑ | ☑ | ☑ |  |  |  |
| FILLFACTOR |  |  | ☑ |  |  |  |  |
| FINAL |  | ☑ |  |  |  |  |  |
| FINALIZE |  |  |  |  | ☑ |  |  |
| FOR |  |  | ☑ |  |  |  |  |
| FORCE |  |  |  |  | ☑ |  |  |
| FOREIGN |  |  | ☑ |  |  |  |  |
| FORMAT |  |  |  |  | ☑ |  |  |
| FORWARD |  |  |  |  | ☑ |  |  |
| FREE |  | ☑ |  |  |  |  |  |
| FREETEXT |  |  | ☑ |  |  |  |  |
| FREETEXTTABLE |  |  | ☑ |  |  |  |  |
| FREEZE |  |  |  |  | ☑ |  |  |
| FROM |  |  | ☑ |  |  |  |  |
| FULL |  |  | ☑ |  |  |  |  |
| FUNCTION |  | ☑ | ☑ |  |  |  |  |
| FUNCTIONS |  |  |  |  | ☑ |  |  |
| GENERAL |  | ☑ |  |  |  |  |  |
| GENERATED |  | ☑ |  |  | ☑ |  |  |
| GLOB |  |  |  |  |  |  | ☑ |
| GOTO |  |  | ☑ |  |  |  |  |
| GRANT |  |  | ☑ |  |  |  |  |
| GRAPHIC |  | ☑ |  |  |  |  |  |
| GREATEST |  |  |  |  | ☑ |  |  |
| GROUP |  |  | ☑ |  |  |  |  |
| GROUPS |  |  |  |  | ☑ | ☑ |  |
| HANDLER |  | ☑ |  |  | ☑ |  |  |
| HAVING |  |  | ☑ |  |  |  |  |
| HEADER |  |  |  |  | ☑ |  |  |
| HOLD |  | ☑ |  |  |  |  |  |
| HOLDLOCK |  |  | ☑ |  |  |  |  |
| HOURS |  | ☑ |  |  |  |  |  |
| IDENTIFIED |  |  |  | ☑ |  |  |  |
| IDENTITY |  |  | ☑ |  |  |  |  |
| IDENTITYCOL |  |  | ☑ |  |  |  |  |
| IDENTITY_INSERT |  |  | ☑ |  |  |  |  |
| IF |  | ☑ | ☑ |  | ☑ | ☑ |  |
| IGNORE |  |  |  |  |  |  | ☑ |
| ILIKE |  |  |  |  | ☑ | ☑ |  |
| IMMEDIATE |  |  |  | ☑ |  |  |  |
| IMMUTABLE |  |  |  |  | ☑ |  |  |
| IMPLICIT |  |  |  |  | ☑ |  |  |
| IMPORT |  |  |  |  | ☑ |  |  |
| IN |  |  | ☑ |  |  |  |  |
| INCLUDE |  |  |  |  | ☑ |  |  |
| INCLUDING |  | ☑ |  |  |  |  |  |
| INCREMENT |  | ☑ |  | ☑ |  |  |  |
| INDENT |  |  |  |  | ☑ |  |  |
| INDEX |  | ☑ | ☑ | ☑ | ☑ |  | ☑ |
| INDEXED |  |  |  |  |  |  | ☑ |
| INDEXES |  |  |  |  | ☑ |  |  |
| INHERIT |  | ☑ |  |  | ☑ |  |  |
| INHERITS |  |  |  |  | ☑ |  |  |
| INITIAL |  |  |  | ☑ |  |  |  |
| INITIALLY |  |  |  |  |  |  | ☑ |
| INLINE |  |  |  |  | ☑ |  |  |
| INNER |  |  | ☑ |  |  |  |  |
| INOUT |  | ☑ |  |  |  |  |  |
| INSERT |  |  | ☑ |  |  |  |  |
| INSTEAD |  |  |  |  | ☑ |  | ☑ |
| INTEGRITY |  | ☑ |  |  |  |  |  |
| INTERSECT |  |  | ☑ | ☑ |  |  |  |
| INTO |  |  | ☑ |  |  |  |  |
| IS |  |  | ☑ |  |  |  |  |
| ISNULL |  |  |  |  | ☑ |  | ☑ |
| ISOBID |  | ☑ |  |  |  |  |  |
| ITERATE |  | ☑ |  |  |  |  |  |
| JAR |  | ☑ |  |  |  |  |  |
| JAVA |  | ☑ |  |  |  |  |  |
| JOIN |  |  | ☑ |  |  |  |  |
| JSON |  |  |  |  | ☑ |  |  |
| JSON_ARRAY |  |  |  |  | ☑ |  |  |
| JSON_ARRAYAGG |  |  |  |  | ☑ |  |  |
| JSON_EXISTS |  |  |  |  | ☑ |  |  |
| JSON_OBJECT |  |  |  |  | ☑ |  |  |
| JSON_OBJECTAGG |  |  |  |  | ☑ |  |  |
| JSON_QUERY |  |  |  |  | ☑ |  |  |
| JSON_SCALAR |  |  |  |  | ☑ |  |  |
| JSON_SERIALIZE |  |  |  |  | ☑ |  |  |
| JSON_TABLE |  |  |  |  | ☑ |  |  |
| JSON_VALUE |  |  |  |  | ☑ |  |  |
| KEEP |  |  |  |  | ☑ |  |  |
| KEY |  |  | ☑ |  |  | ☑ | ☑ |
| KEYS |  |  |  |  | ☑ |  |  |
| KILL |  |  | ☑ |  |  |  |  |
| LABEL |  | ☑ |  |  | ☑ |  |  |
| LC_CTYPE |  | ☑ |  |  |  |  |  |
| LEAKPROOF |  |  |  |  | ☑ |  |  |
| LEAST |  |  |  |  | ☑ |  |  |
| LEAVE |  | ☑ |  |  |  |  |  |
| LEFT |  |  | ☑ |  |  |  |  |
| LEVEL |  |  |  | ☑ |  |  |  |
| LIKE |  |  | ☑ |  |  |  |  |
| LIMIT |  |  |  |  | ☑ | ☑ | ☑ |
| LINENO |  |  | ☑ |  |  |  |  |
| LINKTYPE |  | ☑ |  |  |  |  |  |
| LISTEN |  |  |  |  | ☑ |  |  |
| LOAD |  |  | ☑ |  | ☑ |  |  |
| LOCALE |  | ☑ |  |  |  |  |  |
| LOCATION |  |  |  |  | ☑ |  |  |
| LOCATOR |  | ☑ |  |  |  |  |  |
| LOCATORS |  | ☑ |  |  |  |  |  |
| LOCK |  | ☑ |  | ☑ | ☑ |  |  |
| LOCKED |  |  |  |  | ☑ |  |  |
| LOCKMAX |  | ☑ |  |  |  |  |  |
| LOCKSIZE |  | ☑ |  |  |  |  |  |
| LOGGED |  |  |  |  | ☑ |  |  |
| LONG |  | ☑ |  | ☑ |  |  |  |
| LOOP |  | ☑ |  |  |  |  |  |
| MAPPING |  |  |  |  | ☑ |  |  |
| MATERIALIZED |  |  |  |  | ☑ |  |  |
| MAXEXTENTS |  |  |  | ☑ |  |  |  |
| MAXVALUE |  | ☑ |  |  |  |  |  |
| MERGE |  |  | ☑ |  |  |  |  |
| MERGE_ACTION |  |  |  |  | ☑ |  |  |
| MICROSECOND |  | ☑ |  |  |  |  |  |
| MICROSECONDS |  | ☑ |  |  |  |  |  |
| MINUS |  |  |  | ☑ |  | ☑ |  |
| MINUTES |  | ☑ |  |  |  |  |  |
| MINVALUE |  | ☑ |  |  |  |  |  |
| MODE |  | ☑ |  | ☑ | ☑ |  |  |
| MODIFIES |  | ☑ |  |  |  |  |  |
| MONTHS |  | ☑ |  |  |  |  |  |
| MOVE |  |  |  |  | ☑ |  |  |
| NATIONAL |  |  | ☑ |  |  |  |  |
| NESTED |  |  |  |  | ☑ |  |  |
| NEW |  | ☑ |  |  |  |  |  |
| NEW_TABLE |  | ☑ |  |  |  |  |  |
| NFC |  |  |  |  | ☑ |  |  |
| NFD |  |  |  |  | ☑ |  |  |
| NFKC |  |  |  |  | ☑ |  |  |
| NFKD |  |  |  |  | ☑ |  |  |
| NOAUDIT |  |  |  | ☑ |  |  |  |
| NOCACHE |  | ☑ |  |  |  |  |  |
| NOCHECK |  |  | ☑ |  |  |  |  |
| NOCOMPRESS |  |  |  | ☑ |  |  |  |
| NOCYCLE |  | ☑ |  |  |  |  |  |
| NODENAME |  | ☑ |  |  |  |  |  |
| NODENUMBER |  | ☑ |  |  |  |  |  |
| NOMAXVALUE |  | ☑ |  |  |  |  |  |
| NOMINVALUE |  | ☑ |  |  |  |  |  |
| NONCLUSTERED |  |  | ☑ |  |  |  |  |
| NOORDER |  | ☑ |  |  |  |  |  |
| NOT |  |  | ☑ |  |  |  |  |
| NOTHING |  |  |  |  | ☑ |  |  |
| NOTIFY |  |  |  |  | ☑ |  |  |
| NOTNULL |  |  |  |  | ☑ |  | ☑ |
| NOWAIT |  |  |  | ☑ | ☑ |  |  |
| NULL |  |  | ☑ |  |  |  |  |
| NULLIF |  |  | ☑ |  |  |  |  |
| NULLS |  | ☑ |  |  |  |  |  |
| NUMBER |  |  |  | ☑ |  |  |  |
| NUMPARTS |  | ☑ |  |  |  |  |  |
| OBID |  | ☑ |  |  |  |  |  |
| OF |  |  | ☑ |  |  |  |  |
| OFF |  |  | ☑ |  | ☑ |  |  |
| OFFLINE |  |  |  | ☑ |  |  |  |
| OFFSET |  |  |  |  | ☑ | ☑ | ☑ |
| OFFSETS |  |  | ☑ |  |  |  |  |
| OIDS |  |  |  |  | ☑ |  |  |
| OLD |  | ☑ |  |  |  |  |  |
| OLD_TABLE |  | ☑ |  |  |  |  |  |
| OMIT |  |  |  |  | ☑ |  |  |
| ON |  |  | ☑ |  |  |  |  |
| ONLINE |  |  |  | ☑ |  |  |  |
| OPEN |  |  | ☑ |  |  |  |  |
| OPENDATASOURCE |  |  | ☑ |  |  |  |  |
| OPENQUERY |  |  | ☑ |  |  |  |  |
| OPENROWSET |  |  | ☑ |  |  |  |  |
| OPENXML |  |  | ☑ |  |  |  |  |
| OPERATOR |  |  |  |  | ☑ |  |  |
| OPTIMIZATION |  | ☑ |  |  |  |  |  |
| OPTIMIZE |  | ☑ |  |  |  |  |  |
| OPTION |  |  | ☑ |  |  |  |  |
| OR |  |  | ☑ |  |  |  |  |
| ORDER |  |  | ☑ |  |  |  |  |
| OUT |  | ☑ |  |  |  |  |  |
| OUTER |  |  | ☑ |  |  |  |  |
| OVER |  |  | ☑ |  |  |  |  |
| OVERRIDING |  | ☑ |  |  |  |  |  |
| OWNED |  |  |  |  | ☑ |  |  |
| OWNER |  |  |  |  | ☑ |  |  |
| PACKAGE |  | ☑ |  |  |  |  |  |
| PARALLEL |  |  |  |  | ☑ |  |  |
| PARAMETER |  | ☑ |  |  |  |  |  |
| PARSER |  |  |  |  | ☑ |  |  |
| PART |  | ☑ |  |  |  |  |  |
| PARTITION |  | ☑ |  |  |  |  |  |
| PASSING |  |  |  |  | ☑ |  |  |
| PASSWORD |  |  |  |  | ☑ |  |  |
| PATH |  | ☑ |  |  |  |  |  |
| PCTFREE |  |  |  | ☑ |  |  |  |
| PERCENT |  |  | ☑ |  |  |  |  |
| PIECESIZE |  | ☑ |  |  |  |  |  |
| PIVOT |  |  | ☑ |  |  |  |  |
| PLAN |  | ☑ | ☑ |  | ☑ |  | ☑ |
| PLANS |  |  |  |  | ☑ |  |  |
| POLICY |  |  |  |  | ☑ |  |  |
| PRAGMA |  |  |  |  |  |  | ☑ |
| PRECISION |  |  | ☑ |  |  |  |  |
| PREPARED |  |  |  |  | ☑ |  |  |
| PRIMARY |  |  | ☑ |  |  |  |  |
| PRINT |  |  | ☑ |  |  |  |  |
| PRIOR |  |  |  | ☑ |  |  |  |
| PRIQTY |  | ☑ |  |  |  |  |  |
| PROC |  |  | ☑ |  |  |  |  |
| PROCEDURAL |  |  |  |  | ☑ |  |  |
| PROCEDURE |  |  | ☑ |  |  |  |  |
| PROCEDURES |  |  |  |  | ☑ |  |  |
| PROGRAM |  | ☑ |  |  | ☑ |  |  |
| PSID |  | ☑ |  |  |  |  |  |
| PUBLIC |  |  | ☑ |  |  |  |  |
| PUBLICATION |  |  |  |  | ☑ |  |  |
| QUALIFY |  |  |  |  |  | ☑ |  |
| QUERY |  |  |  |  |  |  | ☑ |
| QUERYNO |  | ☑ |  |  |  |  |  |
| QUOTE |  |  |  |  | ☑ |  |  |
| QUOTES |  |  |  |  | ☑ |  |  |
| RAISE |  |  |  |  |  |  | ☑ |
| RAISERROR |  |  | ☑ |  |  |  |  |
| READ |  |  | ☑ |  |  |  |  |
| READS |  | ☑ |  |  |  |  |  |
| READTEXT |  |  | ☑ |  |  |  |  |
| REASSIGN |  |  |  |  | ☑ |  |  |
| RECHECK |  |  |  |  | ☑ |  |  |
| RECONFIGURE |  |  | ☑ |  |  |  |  |
| RECOVERY |  | ☑ |  |  |  |  |  |
| REFERENCES |  |  | ☑ |  |  |  |  |
| REFERENCING |  | ☑ |  |  |  |  |  |
| REFRESH |  |  |  |  | ☑ |  |  |
| REGEXP |  |  |  |  |  | ☑ | ☑ |
| REINDEX |  |  |  |  | ☑ |  | ☑ |
| RELEASE |  | ☑ |  |  |  |  |  |
| RENAME |  | ☑ |  |  | ☑ |  | ☑ |
| REPEAT |  | ☑ |  |  |  |  |  |
| REPLACE |  |  |  |  | ☑ |  | ☑ |
| REPLICA |  |  |  |  | ☑ |  |  |
| REPLICATION |  |  | ☑ |  |  |  |  |
| RESET |  | ☑ |  |  | ☑ |  |  |
| RESIGNAL |  | ☑ |  |  |  |  |  |
| RESTART |  | ☑ |  |  |  |  |  |
| RESTORE |  |  | ☑ |  |  |  |  |
| RESTRICT |  |  | ☑ |  | ☑ |  | ☑ |
| RESULT |  | ☑ |  |  |  |  |  |
| RESULT_SET_LOCATOR |  | ☑ |  |  |  |  |  |
| RETURN |  | ☑ | ☑ |  |  |  |  |
| RETURNING |  |  |  |  | ☑ |  |  |
| RETURNS |  | ☑ |  |  |  |  |  |
| REVERT |  |  | ☑ |  |  |  |  |
| REVOKE |  |  | ☑ |  |  |  |  |
| RIGHT |  |  | ☑ |  |  |  |  |
| ROLLBACK |  |  | ☑ |  |  |  |  |
| ROUTINE |  | ☑ |  |  |  |  |  |
| ROUTINES |  |  |  |  | ☑ |  |  |
| ROW |  | ☑ |  |  |  |  |  |
| ROWCOUNT |  |  | ☑ |  |  |  |  |
| ROWGUIDCOL |  |  | ☑ |  |  |  |  |
| ROWNUM |  |  |  |  |  | ☑ |  |
| RRN |  | ☑ |  |  |  |  |  |
| RULE |  |  | ☑ |  | ☑ |  |  |
| RUN |  | ☑ |  |  |  |  |  |
| SAVE |  |  | ☑ |  |  |  |  |
| SAVEPOINT |  | ☑ |  |  |  |  |  |
| SCALAR |  |  |  |  | ☑ |  |  |
| SCHEMA |  |  | ☑ |  |  |  |  |
| SCHEMAS |  |  |  |  | ☑ |  |  |
| SCRATCHPAD |  | ☑ |  |  |  |  |  |
| SECONDS |  | ☑ |  |  |  |  |  |
| SECQTY |  | ☑ |  |  |  |  |  |
| SECURITY |  | ☑ |  |  |  |  |  |
| SECURITYAUDIT |  |  | ☑ |  |  |  |  |
| SELECT |  |  | ☑ |  |  |  |  |
| SEMANTICKEYPHRASETABLE |  |  | ☑ |  |  |  |  |
| SEMANTICSIMILARITYDETAILSTABLE |  |  | ☑ |  |  |  |  |
| SEMANTICSIMILARITYTABLE |  |  | ☑ |  |  |  |  |
| SENSITIVE |  | ☑ |  |  |  |  |  |
| SEQUENCES |  |  |  |  | ☑ |  |  |
| SERVER |  |  |  |  | ☑ |  |  |
| SESSION_USER |  |  | ☑ |  |  |  |  |
| SET |  |  | ☑ |  |  |  |  |
| SETOF |  |  |  |  | ☑ |  |  |
| SETUSER |  |  | ☑ |  |  |  |  |
| SHARE |  |  |  |  | ☑ |  |  |
| SHOW |  |  |  |  | ☑ |  |  |
| SHUTDOWN |  |  | ☑ |  |  |  |  |
| SIGNAL |  | ☑ |  |  |  |  |  |
| SIMPLE |  | ☑ |  |  |  |  |  |
| SKIP |  |  |  |  | ☑ |  |  |
| SNAPSHOT |  |  |  |  | ☑ |  |  |
| SOME |  |  | ☑ |  |  |  |  |
| SOURCE |  | ☑ |  |  |  |  |  |
| SPECIFIC |  | ☑ |  |  |  |  |  |
| SQLID |  | ☑ |  |  |  |  |  |
| STABLE |  |  |  |  | ☑ |  |  |
| STANDALONE |  |  |  |  | ☑ |  |  |
| STANDARD |  | ☑ |  |  |  |  |  |
| START |  | ☑ |  |  |  |  |  |
| STATIC |  | ☑ |  |  |  |  |  |
| STATISTICS |  |  | ☑ |  | ☑ |  |  |
| STAY |  | ☑ |  |  |  |  |  |
| STDIN |  |  |  |  | ☑ |  |  |
| STDOUT |  |  |  |  | ☑ |  |  |
| STOGROUP |  | ☑ |  |  |  |  |  |
| STORAGE |  |  |  |  | ☑ |  |  |
| STORED |  |  |  |  | ☑ |  |  |
| STORES |  | ☑ |  |  |  |  |  |
| STRICT |  |  |  |  | ☑ |  |  |
| STRING |  |  |  |  | ☑ |  |  |
| STRIP |  |  |  |  | ☑ |  |  |
| STYLE |  | ☑ |  |  |  |  |  |
| SUBPAGES |  | ☑ |  |  |  |  |  |
| SUBSCRIPTION |  |  |  |  | ☑ |  |  |
| SUPPORT |  |  |  |  | ☑ |  |  |
| SYNONYM |  | ☑ |  |  |  |  |  |
| SYSFUN |  | ☑ |  |  |  |  |  |
| SYSIBM |  | ☑ |  |  |  |  |  |
| SYSID |  |  |  |  | ☑ |  |  |
| SYSPROC |  | ☑ |  |  |  |  |  |
| SYSTEM |  | ☑ |  |  |  |  |  |
| SYSTEM_USER |  |  | ☑ |  |  |  |  |
| TABLE |  |  | ☑ |  |  |  |  |
| TABLES |  |  |  |  | ☑ |  |  |
| TABLESAMPLE |  |  | ☑ |  |  |  |  |
| TABLESPACE |  | ☑ |  |  | ☑ |  |  |
| TARGET |  |  |  |  | ☑ |  |  |
| TEMP |  |  |  |  | ☑ |  | ☑ |
| TEMPLATE |  |  |  |  | ☑ |  |  |
| TEMPORARY |  |  |  |  |  |  | ☑ |
| TEXT |  |  |  |  | ☑ |  |  |
| TEXTSIZE |  |  | ☑ |  |  |  |  |
| THEN |  |  | ☑ |  |  |  |  |
| TO |  |  | ☑ |  |  |  |  |
| TOP |  |  | ☑ |  |  | ☑ |  |
| TRAN |  |  | ☑ |  |  |  |  |
| TRANSACTION |  |  | ☑ |  |  |  | ☑ |
| TRIGGER |  | ☑ | ☑ |  |  |  |  |
| TRUNCATE |  |  | ☑ |  | ☑ |  |  |
| TRUSTED |  |  |  |  | ☑ |  |  |
| TRY_CONVERT |  |  | ☑ |  |  |  |  |
| TSEQUAL |  |  | ☑ |  |  |  |  |
| TYPE |  | ☑ |  |  |  |  |  |
| TYPES |  |  |  |  | ☑ |  |  |
| UNCONDITIONAL |  |  |  |  | ☑ |  |  |
| UNDO |  | ☑ |  |  |  |  |  |
| UNENCRYPTED |  |  |  |  | ☑ |  |  |
| UNION |  |  | ☑ |  |  |  |  |
| UNIQUE |  |  | ☑ |  |  |  |  |
| UNLISTEN |  |  |  |  | ☑ |  |  |
| UNLOGGED |  |  |  |  | ☑ |  |  |
| UNPIVOT |  |  | ☑ |  |  |  |  |
| UNTIL |  | ☑ |  |  | ☑ |  |  |
| UPDATE |  |  | ☑ |  |  |  |  |
| UPDATETEXT |  |  | ☑ |  |  |  |  |
| USE |  |  | ☑ |  |  |  |  |
| USER |  |  | ☑ |  |  |  |  |
| VACUUM |  |  |  |  | ☑ |  | ☑ |
| VALID |  |  |  |  | ☑ |  |  |
| VALIDATE |  |  |  |  | ☑ |  |  |
| VALIDATOR |  |  |  |  | ☑ |  |  |
| VALIDPROC |  | ☑ |  |  |  |  |  |
| VALUES |  |  | ☑ |  |  |  |  |
| VARIABLE |  | ☑ |  |  |  |  |  |
| VARIADIC |  |  |  |  | ☑ |  |  |
| VARIANT |  | ☑ |  |  |  |  |  |
| VARYING |  |  | ☑ |  |  |  |  |
| VCAT |  | ☑ |  |  |  |  |  |
| VERBOSE |  |  |  |  | ☑ |  |  |
| VERSION |  |  |  |  | ☑ |  |  |
| VIEW |  |  | ☑ |  |  |  | ☑ |
| VIEWS |  |  |  |  | ☑ |  |  |
| VIRTUAL |  |  |  |  |  |  | ☑ |
| VOLATILE |  |  |  |  | ☑ |  |  |
| VOLUMES |  | ☑ |  |  |  |  |  |
| WAITFOR |  |  | ☑ |  |  |  |  |
| WHEN |  |  | ☑ |  |  |  |  |
| WHERE |  |  | ☑ |  |  |  |  |
| WHILE |  | ☑ | ☑ |  |  |  |  |
| WHITESPACE |  |  |  |  | ☑ |  |  |
| WITH |  |  | ☑ |  |  |  |  |
| WITHIN GROUP |  |  | ☑ |  |  |  |  |
| WLM |  | ☑ |  |  |  |  |  |
| WRAPPER |  |  |  |  | ☑ |  |  |
| WRITETEXT |  |  | ☑ |  |  |  |  |
| XML |  |  |  |  | ☑ |  |  |
| XMLATTRIBUTES |  |  |  |  | ☑ |  |  |
| XMLCONCAT |  |  |  |  | ☑ |  |  |
| XMLELEMENT |  |  |  |  | ☑ |  |  |
| XMLEXISTS |  |  |  |  | ☑ |  |  |
| XMLFOREST |  |  |  |  | ☑ |  |  |
| XMLNAMESPACES |  |  |  |  | ☑ |  |  |
| XMLPARSE |  |  |  |  | ☑ |  |  |
| XMLPI |  |  |  |  | ☑ |  |  |
| XMLROOT |  |  |  |  | ☑ |  |  |
| XMLSERIALIZE |  |  |  |  | ☑ |  |  |
| XMLTABLE |  |  |  |  | ☑ |  |  |
| YEARS |  | ☑ |  |  |  |  |  |
| YES |  |  |  |  | ☑ |  |  |
| _ROWID_ |  |  |  |  |  | ☑ |  |

