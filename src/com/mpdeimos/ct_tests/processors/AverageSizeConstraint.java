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

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.constraint.ConstraintBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 29D379074144EC7A74B33ED4F3516287
 */
@AConQATProcessor(description = ""
		+ "Constraint that is satisfied if the ratio of gaps to units in the clone class are below a set threshold")
public class AverageSizeConstraint extends ConstraintBase {

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cc) {
		int mean = 0;
		for (Clone c : cc.getClones())
		{
			mean += c.getLengthInUnits();
		}
		mean /= cc.getClones().size();
		
		for (Clone c : cc.getClones())
		{
			if (c.getLengthInUnits() > mean*4/3 || c.getLengthInUnits() < mean*2/3)
			{
				return false;
			}
		}
		
		return true;
	}
}