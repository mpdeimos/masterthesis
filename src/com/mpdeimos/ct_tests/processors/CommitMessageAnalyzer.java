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

import java.util.Collections;
import java.util.List;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.utils.CloneUtils;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.code_clones.result.CloneClassNode;
import org.conqat.engine.code_clones.result.DetectionResultRootNode;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.node.StringSetNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.string.StringUtils;

import com.mpdeimos.ct_tests.vcs.Commit;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 139C7200BB445B7BEBD03FE30193C923
 */
@AConQATProcessor(description = "TODO")
public class CommitMessageAnalyzer extends ConQATProcessorBase  {

	/** Clone detection result from which clone classes are taken */
	private CloneDetectionResultElement result;
	private List<Commit> commits;

	/** ConQAT Parameter */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, description = ConQATParamDoc.INPUT_DESC, minOccurrences = 1, maxOccurrences = 1)
	public void setResult(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) CloneDetectionResultElement result,
			@AConQATAttribute(name = "commits", description = "TODO") List<Commit> commits) {
		this.result = result;
		this.commits = commits;
	}

	/** {@inheritDoc} */
	@Override
	public ListNode process() {
		ListNode root = new ListNode("x", "y");
		root.setValue(NodeConstants.HIDE_ROOT, true);
		NodeUtils.addToDisplayList(root, "Fingerprint");
		NodeUtils.addToDisplayList(root, "Critical");
		
		boolean fix = false;
		for (Commit commit : commits)
		{
			if (StringUtils.containsOneOf(commit.getMessage().toLowerCase(), "fix"))
			{
				fix = true;
				break;
			}
		}
		
		for (CloneClass cloneClass : result.getList()) {
			// TODO DIFF, FILE NAME COMPARE, ...
			ListNode ln = new ListNode();
			ln.setValue("Fingerprint", cloneClass.getFingerprint());
			ln.setValue("Critical", fix ? "yes" : "no");
			root.addChild(ln);
		}

		return root;
	}
}