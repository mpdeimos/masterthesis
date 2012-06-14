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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections.iterators.ArrayListIterator;
import org.conqat.engine.commons.pattern.PatternList;
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
public class RevisionCompressor extends RevisionLooperMethodBase {
	
	private RevisionLooperMethodBase looper;
	private PatternList pl;
	private boolean enable;
	private int maxChangesetSize = -1;
	private int minChangesetSize = -1;
	
	@AConQATParameter(name = "revision-looper", minOccurrences = 1, description = "")
	public void setLooper(
			@AConQATAttribute(name = "ref", description = "") RevisionLooperMethodBase looper
			) throws ConQATException {
				this.looper = looper;
	}
	
	@AConQATParameter(name = "include", maxOccurrences = 1, description = "")
	public void setPL(
			@AConQATAttribute(name = "pattern", description = "") PatternList pl
			) throws ConQATException {
				this.pl = pl;
		
	}
	
	@AConQATParameter(name = "compress", maxOccurrences = 1, description = "")
	public void setCompress(
			@AConQATAttribute(name = "enable", description = "") boolean enable
			) throws ConQATException {
		this.enable = enable;
		
	}
	@AConQATParameter(name = "changesetSize", maxOccurrences = 1, description = "")
	public void setCompress(
			@AConQATAttribute(name = "max-changeset-size", description = "default -1") int maxChangesetSize,
			@AConQATAttribute(name = "min-changeset-size", description = "default -1") int minChangesetSize
			) throws ConQATException {
		this.maxChangesetSize = maxChangesetSize;
		this.minChangesetSize = minChangesetSize;
		
	}
	
	/** {@inheritDoc} */
	@Override
	protected void onProcess() throws ConQATException {
	}
	
	/** {@inheritDoc} */
	@Override
	public RevIterator iterator() {
		if (!enable)
			return looper.iterator();
		
		return new RevIterator() {
			
			RevIterator iterator = looper.iterator();
			boolean nextOfInterest = false;
			
			@Override
			public void remove() {
				throw new IllegalStateException();
			}
			
			@Override
			public RevisionInfo next() {
				if (SuspectionOracle.isSuspicious(pl, iterator.peekCommit(), minChangesetSize, maxChangesetSize))
				{
					RevisionInfo c = iterator.next();
					return c;
				}
				
				HashMap<String, EChangeType> map = new HashMap<String, EChangeType>();
				Commit c = null;
				RevisionInfo next = null;
				while (iterator.hasNext())
				{
					next = iterator.next();
					
					c = next.getCommit();
					
					for (String s : c.getAdded())
						map.put(s, EChangeType.ADDED);
					for (String s : c.getDeleted())
						map.put(s, EChangeType.DELETED);
					for (String s : c.getModified())
						map.put(s, EChangeType.MODIFIED);
							
					// interesting commit
					if (iterator.hasNext() && SuspectionOracle.isSuspicious(pl, iterator.peekCommit(), minChangesetSize, maxChangesetSize))
						break;
				}
				
				final RevisionInfo last = next;
				
				List<String> added = new ArrayList<String>();
				List<String> deleted = new ArrayList<String>();
				List<String> modified = new ArrayList<String>();
				for (String s : map.keySet())
				{
					switch(map.get(s))
					{
					case ADDED:
						added.add(s);
						break;
					case DELETED:
						deleted.add(s);
						break;
					case MODIFIED:
						modified.add(s);
						break;
					}
				}
				
				// ignore NPEs
				Commit compound = new Commit(c.getId(), c.getDate(), c.getMessage(), added, deleted, modified);
				compound.setCompund(true);
				return new RevisionInfo(next.getIndex(), compound.getId(), compound, null)
				{
					/** {@inheritDoc} 
					 * @throws ConQATException */
					@Override
					public File getPath() throws ConQATException {
						return last.getPath();
					}
				};
				
			}
			
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public Commit peekCommit() {
				return iterator.peekCommit();
			}
		};
	}

	private enum EChangeType
	{
		ADDED, DELETED, MODIFIED;
	}

}
