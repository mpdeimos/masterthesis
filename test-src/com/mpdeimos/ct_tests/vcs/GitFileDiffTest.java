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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.gitective.core.TreeUtils;
import org.gitective.core.filter.commit.CommitDiffFilter;
import org.gitective.core.filter.commit.CommitListFilter;
import org.gitective.core.service.CommitFinder;
import org.gitective.core.stat.FileHistogramFilter;
import org.junit.Test;

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
public class GitFileDiffTest extends CCSMTestCaseBase {

	
	
	private List<DiffEntry> scan;

	@Test
	public void testAddedFiles() {
		
		try
		{
//			ArrayList<RevCommit> commits = new ArrayList<RevCommit>();
//			
//			Repository repository = new FileRepository(gitDir);
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			Repository repository = builder.setWorkTree(useTestFile("diff1")).build();
			ObjectId head = repository.resolve("new");
//			TreeWalk treeWalk = TreeUtils.diffWithParents(repository, head);
//			scan = DiffEntry.scan(treeWalk);
//			treeWalk.release();
			
			
			
			CommitFinder finder = new CommitFinder(repository);
			GitCommitListFilter filter = new GitCommitListFilter();
			finder.setFilter(filter);
			finder.findFrom(head);
			for (Commit commit : filter.getCommits())
			{
//				ResetCommand reset = new Git(repository).reset();
//				reset.setMode(ResetType.HARD);
//				reset.setRef(commit.getId());
				CheckoutCommand checkout = new Git(repository).checkout();
				checkout.setName(commit.getId());
//				checkout.setStartPoint(commit.getId());
//				checkout.addPath(".");
				try {
//					Ref call = reset.call();
					checkout.call();
					System.out.println(checkout.getResult().getStatus() + " checked out " + commit.getMessage() );
				} catch (Exception e) {
					fail(e.getMessage());
				}
				
//				System.out.println(change.getCommit().getFullMessage());
				
				
			}
			
			
//			head = repository.resolve("HEAD~1");
//			RevWalk revWalk = new RevWalk(repository);
//			RevCommit tip = revWalk.parseCommit(head);
//			revWalk.markStart(tip);
//			revWalk.
//			TreeWalk treeWalk = new TreeWalk(repository);
//			treeWalk.setRecursive(true);
//			treeWalk.addTree(tip.getTree());
//			
			
//			revWalk.markStart(tip);
			
//			GitFileDiff[] compute = GitFileDiff.compute(treeWalk, tip);
			
//			System.out.println(compute);
			
			
			
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

}
