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

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35532 $
 * @ConQAT.Rating YELLOW Hash: FD23B6FC74A53ECF8C80824F88E2D3ED
 */
@AConQATProcessor(description = "Returns always true.")
public class AlwaysTrueCondition extends ConQATProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "input", minOccurrences = 1, maxOccurrences = 1, description = "Some parameters.")
	public void addValue(
			@SuppressWarnings("unused") @AConQATAttribute(name = "param", description = "Parameter to ignore") Object param) {
		// nothing to do
	}

	/** {@inheritDoc} */
	@Override
	public Boolean process() {
		return true;
	}
}