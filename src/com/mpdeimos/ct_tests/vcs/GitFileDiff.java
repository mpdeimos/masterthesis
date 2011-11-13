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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;

/**
 * @deprecated
 * A class with information about the changes to a file introduced in a
 * commit.
 * 
 * Adopted from eGit Project -- original source:
 * http://egit.eclipse.org/w/?p=egit.git;a=blob_plain;f=org.eclipse.egit.ui/src/org/eclipse/egit/ui/internal/history/FileDiff.java;hb=HEAD
 */
public class GitFileDiff {

	private final RevCommit commit;

	private DiffEntry diffEntry;

	private static ObjectId[] trees(final RevCommit commit) {
		final ObjectId[] r = new ObjectId[commit.getParentCount() + 1];
		for (int i = 0; i < r.length - 1; i++)
			r[i] = commit.getParent(i).getTree().getId();
		r[r.length - 1] = commit.getTree().getId();
		return r;
	}

	/**
	 * Computer file diffs for specified tree walk and commit
	 *
	 * @param walk
	 * @param commit
	 * @return non-null but possibly empty array of file diffs
	 * @throws MissingObjectException
	 * @throws IncorrectObjectTypeException
	 * @throws CorruptObjectException
	 * @throws IOException
	 */
	public static GitFileDiff[] compute(final TreeWalk walk, final RevCommit commit)
			throws MissingObjectException, IncorrectObjectTypeException,
			CorruptObjectException, IOException {
		final ArrayList<GitFileDiff> r = new ArrayList<GitFileDiff>();

		if (commit.getParentCount() > 0)
			walk.reset(trees(commit));
		else {
			walk.reset();
			walk.addTree(new EmptyTreeIterator());
			walk.addTree(commit.getTree());
		}

		if (walk.getTreeCount() <= 2) {
			List<DiffEntry> entries = DiffEntry.scan(walk);
			for (DiffEntry entry : entries) {
				final GitFileDiff d = new GitFileDiff(commit, entry);
				r.add(d);
			}
		}
		else { // DiffEntry does not support walks with more than two trees
			final int nTree = walk.getTreeCount();
			final int myTree = nTree - 1;
			while (walk.next()) {
				if (matchAnyParent(walk, myTree))
					continue;

				final GitFileDiffForMerges d = new GitFileDiffForMerges(commit);
				d.path = walk.getPathString();
				int m0 = 0;
				for (int i = 0; i < myTree; i++)
					m0 |= walk.getRawMode(i);
				final int m1 = walk.getRawMode(myTree);
				d.change = ChangeType.MODIFY;
				if (m0 == 0 && m1 != 0)
					d.change = ChangeType.ADD;
				else if (m0 != 0 && m1 == 0)
					d.change = ChangeType.DELETE;
				else if (m0 != m1 && walk.idEqual(0, myTree))
					d.change = ChangeType.MODIFY; // there is no ChangeType.TypeChanged
				d.blobs = new ObjectId[nTree];
				d.modes = new FileMode[nTree];
				for (int i = 0; i < nTree; i++) {
					d.blobs[i] = walk.getObjectId(i);
					d.modes[i] = walk.getFileMode(i);
				}
				r.add(d);
			}

		}

		final GitFileDiff[] tmp = new GitFileDiff[r.size()];
		r.toArray(tmp);
		return tmp;
	}

	private static boolean matchAnyParent(final TreeWalk walk, final int myTree) {
		final int m = walk.getRawMode(myTree);
		for (int i = 0; i < myTree; i++)
			if (walk.getRawMode(i) == m && walk.idEqual(i, myTree))
				return true;
		return false;
	}

	/**
	 * Creates a textual diff together with meta information.
	 * TODO So far this works only in case of one parent commit.
	 *
	 * @param d
	 *            the StringBuilder where the textual diff is added to
	 * @param db
	 *            the Repo
	 * @param diffFmt
	 *            the DiffFormatter used to create the textual diff

	 * @throws IOException
	 */
	public void outputDiff(final StringBuilder d, final Repository db,
		final DiffFormatter diffFmt) throws IOException {
		diffFmt.setRepository(db);
		diffFmt.format(diffEntry);
		return;
	}

	/**
	 * Get commit
	 *
	 * @return commit
	 */
	public RevCommit getCommit() {
		return commit;
	}

	/**
	 * Get path
	 *
	 * @return path
	 */
	public String getPath() {
		if (ChangeType.DELETE.equals(diffEntry.getChangeType()))
			return diffEntry.getOldPath();
		return diffEntry.getNewPath();
	}

	/**
	 * Get change type
	 *
	 * @return type
	 */
	public ChangeType getChange() {
		return diffEntry.getChangeType();
	}

	/**
	 * Get blob object ids
	 *
	 * @return non-null but possibly empty array of object ids
	 */
	public ObjectId[] getBlobs() {
		List<ObjectId> objectIds = new ArrayList<ObjectId>();
		if (diffEntry.getOldId() != null)
			objectIds.add(diffEntry.getOldId().toObjectId());
		if (diffEntry.getNewId() != null)
			objectIds.add(diffEntry.getNewId().toObjectId());
		return objectIds.toArray(new ObjectId[]{});
	}

	/**
	 * Get file modes
	 *
	 * @return non-null but possibly empty array of file modes
	 */
	public FileMode[] getModes() {
		List<FileMode> modes = new ArrayList<FileMode>();
		if (diffEntry.getOldMode() != null)
			modes.add(diffEntry.getOldMode());
		if (diffEntry.getOldMode() != null)
			modes.add(diffEntry.getOldMode());
		return modes.toArray(new FileMode[]{});
	}

	/**
	 * Create a file diff for a specified {@link RevCommit} and
	 * {@link DiffEntry}
	 *
	 * @param c
	 * @param entry
	 */
	public GitFileDiff(final RevCommit c, final DiffEntry entry) {
		diffEntry = entry;
		commit = c;
	}

	private static class GitFileDiffForMerges extends GitFileDiff {
		private String path;

		private ChangeType change;

		private ObjectId[] blobs;

		private FileMode[] modes;

		private GitFileDiffForMerges(final RevCommit c) {
			super (c, null);
		}

		@Override
		public String getPath() {
			return path;
		}

		@Override
		public ChangeType getChange() {
			return change;
		}

		@Override
		public ObjectId[] getBlobs() {
			return blobs;
		}

		@Override
		public FileMode[] getModes() {
			return modes;
		}
	}
}
