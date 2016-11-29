/**
 * Temporal domain objects are those that are only used while executing SQL statements.
 * Most typically, the information within a SQL statement String is parsed 
 * into a temporal domain object which is then integrated into the persistent
 * data structures. Having done so, temporal domain objects will be removed
 * by the Garbage Collector of the JVM.
 */
package com.iksgmbh.sql.pojomemodb.dataobjects.temporal;
