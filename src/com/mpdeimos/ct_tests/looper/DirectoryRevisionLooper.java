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
import java.util.Arrays;
import java.util.Iterator;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.filesystem.DirectoryOnlyFilter;
import org.conqat.lib.commons.filesystem.FilenameComparator;

import com.mpdeimos.ct_tests.vcs.Commit;

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
@AConQATProcessor(description = "TODO")
public class DirectoryRevisionLooper extends RevisionLooperMethodBase {
	
	private File[] subDirs;
	private File root;
	
	@AConQATParameter(name = "directory", minOccurrences = 1, maxOccurrences = 1, description = "working directory")
	public void setRoot(
			@AConQATAttribute(name = "path", description = "revision root path") String filename
			) throws ConQATException {
		this.root = new File(filename);
		if (!this.root.isDirectory())
			throw new ConQATException(filename + " needs to be a directory.");
	}

	/** {@inheritDoc} */
	@Override
	protected void onProcess() {
		subDirs = root.listFiles(new DirectoryOnlyFilter() {
			@Override
			public boolean accept(File file) {
				if (ignoreDirectory(file))
					return false;
				
				return super.accept(file);
			}
		});
		Arrays.sort(subDirs, new FilenameComparator());
	}

	/** {@inheritDoc} */
	@Override
	public RevIterator iterator() {
		
		return new RevIterator() {
			int index = -1;

			@Override
			public boolean hasNext() {
				return index < subDirs.length-1;
			}

			@Override
			public RevisionInfo next() {
				File path = subDirs[++index];
				Commit commit = new Commit(path.getPath()+"/vcs.data");
				
				return new RevisionInfo(index, Integer.toString(index), commit, path);
			}

			@Override
			public void remove() {
				throw new IllegalStateException();
			}

			@Override
			public String peekMessage() {
				if (!hasNext())
					throw new IllegalStateException();
				
				File path = subDirs[index+1];
				Commit commit = new Commit(path.getPath()+"/vcs.data");
				return commit.getMessage();
			}
		};
	}
	
	/**
	 * @param dir
	 * @return
	 */
	private boolean ignoreDirectory(File dir) {
		return dir.getName().startsWith(".");
	}


}
