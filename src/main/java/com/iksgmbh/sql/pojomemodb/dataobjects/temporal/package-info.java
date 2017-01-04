/**
 * Temporal domain objects are those that are only used while executing a SQL statement and accessing a ResultSet.
 * All information within a SQL statement String is parsed into a temporal domain object.
 * Either this information contains data to be stored (those are then integrated into the persistent data structures)
 * or this information is used to read data from the database (those are accessible from a ResultSet object).
 * Having executed a SQL-Statement and accessed a ResultSet, all temporal domain objects will be removed
 * by the Garbage Collector of the JVM.
 */
package com.iksgmbh.sql.pojomemodb.dataobjects.temporal;