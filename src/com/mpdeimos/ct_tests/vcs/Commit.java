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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections.ListUtils;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.ArrayUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.eclipse.jgit.diff.DiffEntry;

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
public class Commit {
	
	private static final String MESSAGE = "message";
	private static final String REVISION = "revision";
	private static final String DATE = "date";
	private static final String ADDED = "added";
	private static final String DELETED = "deleted";
	private static final String MODIFIED = "modified";

	private String revision;
	
	private String message;
	
	private Date date;

	private List<String> added;

	private List<String> modified;

	private List<String> deleted;
	private boolean compound = false;
	
	/** Constructor. */
	public Commit(String id, Date date, String message, String[] added, String[] deleted, String[] modified)
	{
		this.revision = id;
		this.date = date;
		this.message = message;
		this.added = Arrays.asList(added);
		this.modified = Arrays.asList(modified);
		this.deleted = Arrays.asList(deleted);
	}
	/** Constructor. */
	public Commit(String id, Date date, String message, List<String> added, List<String> deleted, List<String> modified)
	{
		this.revision = id;
		this.date = date;
		this.message = message;
		this.added = added;
		this.modified = modified;
		this.deleted = deleted;
	}
	
	/** Constructor. */
	public Commit(GitChange change)
	{
		this.revision = change.getCommit().getName();
		this.message = change.getCommit().getFullMessage();
		this.date = change.getCommit().getAuthorIdent().getWhen();
		
		this.added = new ArrayList<String>();
		this.deleted = new ArrayList<String>();
		this.modified = new ArrayList<String>();
		
		for (DiffEntry diff : change.getDiffs())
		{
			switch (diff.getChangeType())
			{
			case ADD:
			case COPY:
				added.add(diff.getNewPath());
				break;
				
			case DELETE:
				deleted.add(diff.getOldPath());
				break;
				
			case MODIFY:
				modified.add(diff.getNewPath());
				break;
				
			case RENAME:
				deleted.add(diff.getOldPath());
				added.add(diff.getNewPath());
				break;
			}
		}
	}
	
	/** Constructor. */
	public Commit(String vcsDataFile)
	{
		Properties properties = new Properties();

		try {
			InputStream inputStream = new FileInputStream(vcsDataFile);
			properties.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			throw new IllegalArgumentException("Can't read file: " + vcsDataFile + "!", e);
		}

		this.message = properties.getProperty(MESSAGE);
		if (message == null) {
			throw new IllegalArgumentException("Key '" + MESSAGE + "' not found.");
		}
		this.revision = properties.getProperty(REVISION);
		if (revision == null) {
			throw new IllegalArgumentException("Key '" + REVISION + "' not found.");
		}
		String dateString = properties.getProperty(DATE);
		if (dateString == null) {
			throw new IllegalArgumentException("Key '" + DATE + "' not found.");
		}
		Date date;
		try {
			this.date = CommonUtils.parseDate(dateString, "yyyy-MM-dd hh:mm:ss");
		} catch (ConQATException e) {
			throw new IllegalArgumentException("Date '" + DATE + "' malformed.");
		}
		
		String _added = properties.getProperty(ADDED);
		this.added = StringUtils.splitLinesAsList(_added);
		
		String _deleted = properties.getProperty(DELETED);
		this.deleted = StringUtils.splitLinesAsList(_deleted);
		
		String _modified = properties.getProperty(MODIFIED);
		this.modified = StringUtils.splitLinesAsList(_modified);
	}
	
	/** Returns id. */
	public String getId() {
		return revision;
	}
	
	/** Returns date. */
	public Date getDate() {
		return date;
	}
	
	/** Returns message. */
	public String getMessage() {
		return message;
	}

	public List<String> getAdded() {
		return this.added;
	}
	
	public List<String> getDeleted() {
		return this.deleted;
	}
	
	public List<String> getModified() {
		return this.modified;
	}
	/**
	 * @param b
	 */
	public void setCompund(boolean b) {
		compound = true;
	}
	
	/** Returns compound. */
	public boolean isCompound() {
		return compound;
	}
}
