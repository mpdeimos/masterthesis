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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.map.HashedMap;
import org.conqat.lib.commons.collections.PairList;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.filter.commit.CommitDiffFilter;
import org.gitective.core.filter.commit.CommitFilter;

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
public class GitCommitListFilter extends CommitDiffFilter {
	List<Commit> commits = new ArrayList<Commit>();
	
	/** {@inheritDoc} */
	@Override
	public boolean include(RevCommit commit, Collection<DiffEntry> diffs) {
		commits.add(new Commit(new GitChange(commit, diffs)));
		return true;
	}
	
	/** {@inheritDoc} */
	@Override
	public CommitFilter reset() {
		commits = new ArrayList<Commit>();
		return super.reset();
	}
	
	/** {@inheritDoc} */
	@Override
	public RevFilter clone() {
		return new GitCommitListFilter();
	}
	
	public List<Commit> getCommits()
	{
		return commits;
	}
	
}