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

import static org.conqat.engine.commons.CommonUtils.DEFAULT_DATE_FORMAT_PATTERN;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

import com.mpdeimos.ct_tests.vcs.Commit;

/**
 * {@ConQAT.doc}
 * 
 * @author poehlman
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 */
@AConQATProcessor(description = "Processor for extracting the last commit date from a list of commits.")
public class VCSLatestDateExtractor extends ConQATProcessorBase {

	/** The date string. */
	private List<Commit> commits = null;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "commits", description = "TODO", minOccurrences = 1, maxOccurrences = 1)
	public void setPackageName(
			@AConQATAttribute(name = "ref", description = "TODO") List<Commit> commits) {
		this.commits = commits;
	}

	/** Concatenate parts @throws ConQATException */
	@Override
	public Date process() {
		if (commits.size() == 0)
			return new Date();
		
		return commits.get(commits.size()-1).getDate();
	}
}