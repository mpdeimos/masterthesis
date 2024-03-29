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

import org.conqat.engine.commons.pattern.PatternList;

import com.mpdeimos.ct_tests.vcs.Commit;

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
public class SuspectionOracle
{
	public static boolean isSuspicious(PatternList pl,	Commit commit) {
		return isSuspicious(pl, commit, -1, -1);
	}
	public static boolean isSuspicious(PatternList pl, Commit commit, int minChangesetSize, int maxChangesetSize
			) {
		if (commit.isCompound())
			return false;
		String message = commit.getMessage();
		message = message.substring(0,Math.min(message.length(), 1000));
		return (maxChangesetSize < 0 || commit.getModified().size() <= maxChangesetSize) && (minChangesetSize < 0 || commit.getModified().size() >= minChangesetSize) && pl.matchesAny(message);
	}
}
