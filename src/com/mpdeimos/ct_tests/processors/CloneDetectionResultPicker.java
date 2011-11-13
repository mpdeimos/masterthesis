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
import java.util.List;

import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
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
public class CloneDetectionResultPicker extends ListPicker<CloneDetectionResultElement> {
 // nothing
}
