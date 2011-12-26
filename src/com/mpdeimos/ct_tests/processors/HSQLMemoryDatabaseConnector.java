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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.persistence.DatabaseConnectorBase;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.hsqldb.jdbcDriver;

/**
 * {@ConQAT.Doc}
 * 
 * @author Florian Deissenboeck
 * @author $Author: juergens $
 * @version $Rev: 35197 $
 * @ConQAT.Rating GREEN Hash: E03561C1429B5321C83F364517253320
 */
@AConQATProcessor(description = "This processor creates a connection "
		+ "to a file-based HSQLDB database.")
public class HSQLMemoryDatabaseConnector extends DatabaseConnectorBase {

	/** Database file. */
	private String dbName;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "db", minOccurrences = 1, maxOccurrences = 1, description = "Database.")
	public void setFile(
			@AConQATAttribute(name = "name", description = "The name of the database.") String dbName) {
		this.dbName = dbName;
	}

	/** {@inheritDoc} */
	@Override
	protected Connection setupConnection() throws SQLException, ConQATException {

		try {
			Class.forName(jdbcDriver.class.getName());
		} catch (ClassNotFoundException e) {
			throw new ConQATException("Can't load driver for HSQLDB.");
		}

		Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:"
				 + dbName + ";shutdown=true", "SA", "");

		return connection;
	}

}