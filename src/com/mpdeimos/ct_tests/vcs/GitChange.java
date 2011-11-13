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
package com.mpdeimos.ct_tests.vcs;

import java.util.Collection;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
public class GitChange {

	private final RevCommit commit;
	private final Collection<DiffEntry> diffs;

	/**
	 * @param commit
	 * @param diffs
	 */
	public GitChange(RevCommit commit, Collection<DiffEntry> diffs) {
		this.commit = commit;
		this.diffs = diffs;
	}

	/** Returns commit. */
	public RevCommit getCommit() {
		return commit;
	}

	/** Returns diffs. */
	public Collection<DiffEntry> getDiffs() {
		return diffs;
	}
	
}
