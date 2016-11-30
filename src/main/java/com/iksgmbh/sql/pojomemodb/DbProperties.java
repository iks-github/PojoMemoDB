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
package com.iksgmbh.sql.pojomemodb;

/**
 * Allows to configure some behaviour of the memory database.
 * 
 * TODO these settings are not yet tested for false values !!!
 * 
 * @author Reik Oberrath
 */
public class DbProperties {

	public static final boolean REPLACE_EMPTY_STRING_BY_NULL = true;
	public static final boolean USE_ORACLE_DUAL_TABLE = true;

}