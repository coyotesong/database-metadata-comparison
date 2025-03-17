/**
 * Support for Entity-Relationship Diagrams
 *
 * Since this project already has all of the required elements this is a
 * quick exploration of creating Entity-Relationship Diagrams (ERD) using
 * the JDBC metadata, jinja, and graphviz (external application).
 *
 * It starts by extending the existing docker containers to include support
 * for 'flyway'. This was already on my wish list since it allows meaningful
 * performance comparisons between databases.
 *
 * (Sidenote - I thought the flyway loaders only loaded files with a
 * matching database name but that appears to be incorrect.)
 *
 * Once this is done we can create an object reflecting the database's DDL.
 * A better alternative may be reading the 'information_schema' but not all
 * databases support that (yet). This information is so basic we can use
 * Records instead of full Classes.
 *
 * It's then a simple matter of walking the captured DDL and using
 * a jinja template to create a corresponding '.dot' file. The dirty
 * details are in the attributes of the nodes and edges - we want them
 * to properly reflect whether a connection is one-to-one, one-to-many,
 * or many-to-many. This detail could be extracted via a bit of logic...
 * or configuration by convention. (E.g., is the column 'id' or 'table_id'?)
 *
 * It's currently at the 'proof of concept' level and will probably be
 * spun out into a separate project later. However it is a nice complement
 * to using docker + flyway - it lets you immediately create a ERD so
 * you can verify your test database is what you expected.
 */
package com.coyotesong.database.erd;
