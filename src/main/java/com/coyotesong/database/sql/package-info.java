/**
 * <p>
 * This pockage contains an enhanced DatabaseMetaData class that
 * replaces many boolean or numeric values with corresponding enum
 * classes.
 * </p>
 * <p>
 * There are two reasons for this:
 * <ul>
 *     <li>Conciseness - multiple boolean values can be combined to a single value</li>
 *     <li>I18N - the enum labels could be easily internationalized</li>
 * </ul>
 * </p>
 */
package com.coyotesong.database.sql;
