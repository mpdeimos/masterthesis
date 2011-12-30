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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections.iterators.ArrayListIterator;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.string.StringUtils;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.gitective.core.service.CommitFinder;

import com.mpdeimos.ct_tests.vcs.Commit;
import com.mpdeimos.ct_tests.vcs.GitCommitListFilter;

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
@AConQATProcessor(description = "TODO")
public class GitRevisionLooper extends RevisionLooperMethodBase {
	
	private File root;
	private List<Commit> commits;
	private FileRepository repository;
	private String endRef = "HEAD";
	private String startRef = null;
	
	@AConQATParameter(name = "directory", minOccurrences = 1, maxOccurrences = 1, description = "working directory")
	public void setRoot(
			@AConQATAttribute(name = "path", description = "git checkout path") String filename
			) throws ConQATException {
		this.root = new File(filename);
		if (!this.root.isDirectory())
			throw new ConQATException(filename + " needs to be a directory.");
		
		
		try {
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			repository = builder.setWorkTree(this.root).build();
		} catch (IOException e) {
			throw new ConQATException(filename + "/.git needs to be a directory.");
		}
	}
	
	@AConQATParameter(name = "limit", maxOccurrences = 1, description = "working directory")
	public void setLimits(
			@AConQATAttribute(name = "start", description = "start ref (default null)", defaultValue="null") String start,
			@AConQATAttribute(name = "end", description = "end ref (default HEAD)", defaultValue="HEAD") String end
			) throws ConQATException {
		
		if (!StringUtils.isEmpty(start) && !start.equalsIgnoreCase("null"))
			this.startRef = start;
		
		if (!StringUtils.isEmpty(end) && !end.equalsIgnoreCase("null"))
			this.endRef = end;
	}

	/** {@inheritDoc} */
	@Override
	protected void onProcess() throws ConQATException {
		try
		{
			ObjectId end = repository.resolve(this.endRef);
			repository.resolve(this.endRef);
			
			CommitFinder finder = new CommitFinder(repository);
			GitCommitListFilter filter = new GitCommitListFilter();
			finder.setFilter(filter);
			if (this.startRef != null)
			{
				ObjectId start = repository.resolve(this.endRef);
				finder.findBetween(end, start); // reversed!
			}
			else
			{
				finder.findFrom(end);
			}
			commits = filter.getCommits();
		}
		catch (Exception e)
		{
			throw new ConQATException(e);
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public Iterator<RevisionInfo> iterator() {
		
		return new Iterator<RevisionInfo>() {
			int index = commits.size();
			
			@Override
			public void remove() {
				throw new IllegalStateException();
			}
			
			@Override
			public RevisionInfo next() {
				Commit commit = commits.get(--index);
				
				CheckoutCommand checkout = new CheckoutCommand(repository) {/**/};
				checkout.setName(commit.getId());
				try {
					checkout.call();
				} catch (Exception e) {
					getLogger().error(e);
					return null;
				}
				
				return new RevisionInfo(commits.size() - index - 1, commit.getId(), commit, root);
			}
			
			@Override
			public boolean hasNext() {
				return index > 0;
			}
		};
	}


}
