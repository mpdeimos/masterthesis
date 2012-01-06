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

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.build.ResourceBuilder;
import org.conqat.engine.resource.scope.filesystem.FileSystemScope;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenResourceSelector;

import com.mpdeimos.ct_tests.vcs.Commit;

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
public class RevisionInfo {
	
	private final int index;
	private final String id;
	private final Commit commit;
	private final File path;
	
	RevisionInfo(int index, String id, Commit commit, File path)
	{
		this.index = index;
		this.id = id;
		this.commit = commit;
		this.path = path;
		
	}
	
	/** Returns index. */
	public int getIndex() {
		return index;
	}
	/** Returns id. */
	public String getID() {
		return id;
	}
	/** Returns commit. */
	public Commit getCommit() {
		return commit;
	}
	/** Returns path. 
	 * @throws ConQATException */
	public File getPath() throws ConQATException {
		return path;
	}

}
