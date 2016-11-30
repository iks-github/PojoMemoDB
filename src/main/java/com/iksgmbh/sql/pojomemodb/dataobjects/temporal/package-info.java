/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Temporal domain objects are those that are only used while executing SQL statements.
 * Most typically, the information within a SQL statement String is parsed 
 * into a temporal domain object which is then integrated into the persistent
 * data structures. Having done so, temporal domain objects will be removed
 * by the Garbage Collector of the JVM.
 */
package com.iksgmbh.sql.pojomemodb.dataobjects.temporal;