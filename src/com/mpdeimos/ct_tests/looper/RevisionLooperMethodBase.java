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
package com.mpdeimos.ct_tests.looper;

import java.util.ListIterator;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.ConQATException;

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
public abstract class RevisionLooperMethodBase extends ConQATProcessorBase implements Iterable<RevisionInfo> {
	public final RevisionLooperMethodBase process() throws ConQATException
	{
		onProcess();
		return this;
	}

	abstract protected void onProcess() throws ConQATException;
	public abstract RevIterator iterator();
	
}
