/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package com.mpdeimos.ct_tests.processors;

import java.sql.Connection;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * Merges multiple database connections and returns the last assigned one.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35197 $
 * @ConQAT.Rating GREEN Hash: 0CFDD2DF8A9DDC973AFE934BE0AA122D
 */
@AConQATProcessor(description = "Selects the last assigned db connection.")
public class DatabaseConnectionSelector extends ConQATProcessorBase {

	private Connection connection;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "db", minOccurrences = 1, description = "Database.")
	public void setFile(
			@AConQATAttribute(name = "connection", description = "The database connection.") Connection connection) {
		this.connection = connection;
	}

	/** {@inheritDoc} */
	@Override
	public Connection process() throws ConQATException {
		return this.connection;
	}

}