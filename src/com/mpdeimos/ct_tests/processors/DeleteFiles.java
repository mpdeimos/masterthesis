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

import java.io.File;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
@AConQATProcessor(description = "TODO")
public class DeleteFiles extends ConQATProcessorBase {



	private String file;
	private File path;


	@AConQATParameter(name = "delete", minOccurrences = 1, maxOccurrences = 1, description = "TODO")
	public void setOutput(
			@AConQATAttribute(name = "path", description = "TODO") String path,
			@AConQATAttribute(name = "file", description = "TODO") String file
			) throws ConQATException {
		this.file = file;
		this.path = new File(path);
		if (!this.path.exists() || !this.path.isDirectory())
			throw new ConQATException(path + " needs to be a directory.");
	}
	
	
	/** {@inheritDoc} 
	 * @return */
	@Override
	public Object process()  {
		for (File f : path.listFiles())
		{
			if (f.getName().startsWith(file))
				f.delete();
		}
		
		return null;
	}

}
