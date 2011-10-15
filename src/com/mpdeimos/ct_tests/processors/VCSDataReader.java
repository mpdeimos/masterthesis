/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.date.DateUtils;

import com.mpdeimos.ct_tests.vcs.Commit;

/**
 * {@ConQAT.Doc}
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
@AConQATProcessor(description = "This process or reads version control system data from a properties file")
public class VCSDataReader extends ConQATProcessorBase {

	private static final String MESSAGE = "message";
	private static final String REVISION = "revision";
	private static final String DATE = "date";
	/** name of the properties file */
	private String filename;

	/** Set name of the properties file. */
	@AConQATParameter(name = "file", minOccurrences = 1, maxOccurrences = 1, description = "Properties file to read data from")
	public void setFilename(
			@AConQATAttribute(name = "name", description = "Filename")
			String filename) {
		this.filename = filename;
	}

	/**
	 * Reads properties file and extracts value.
	 * 
	 * @throws ConQATException
	 *             if the file isn't found or the key isn't present
	 */
	@Override
	public List<Commit> process() throws ConQATException {
		Properties properties = new Properties();

		try {
			InputStream inputStream = new FileInputStream(filename);
			properties.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			throw new ConQATException("Can't read file: " + filename + "!", e);
		}

		String message = properties.getProperty(MESSAGE);
		if (message == null) {
			throw new ConQATException("Key '" + MESSAGE + "' not found.");
		}
		String revision = properties.getProperty(REVISION);
		if (revision == null) {
			throw new ConQATException("Key '" + REVISION + "' not found.");
		}
		String dateString = properties.getProperty(DATE);
		if (dateString == null) {
			throw new ConQATException("Key '" + DATE + "' not found.");
		}
		
		Date date = CommonUtils.parseDate(dateString, "yyyy-MM-dd hh:mm:ss");
		
		List<Commit> commits = new ArrayList<Commit>(1);
		commits.add(new Commit(revision, date, message));
		
		return commits;
	}
}
