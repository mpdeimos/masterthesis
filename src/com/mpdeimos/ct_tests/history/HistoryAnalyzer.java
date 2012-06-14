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
package com.mpdeimos.ct_tests.history;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.commons.pattern.PatternListDef;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.PairList;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.gitective.core.service.CommitFinder;

import com.mpdeimos.ct_tests.looper.SuspectionOracle;
import com.mpdeimos.ct_tests.vcs.Commit;
import com.mpdeimos.ct_tests.vcs.GitCommitListFilter;

/**
 * Aggregates Git-based VCS Information
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
public class HistoryAnalyzer {
	
	public static String path = "/media/truecrypt2/monodevelop";
	public static String ref = "before-rename";
	
	public static int countFixes = 0;
	public static int countCommits = 0;
	public static PairList<Date, Double> fixesHistogram = new PairList<Date, Double>();

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ConQATException 
	 */
	public static void main(String[] args) throws IOException, ConQATException {
		
		PatternList pl = new PatternList();
		pl.add(CommonUtils.compilePattern("(?is).*(fix|bug|defe(c|k)t|fehler|behoben|co(r|rr)ect|ko(r|rr)igier).*"));
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		File workTree = new File(path);
		FileRepository repository = builder.setWorkTree(workTree).build();
		
		ObjectId end = repository.resolve(ref);
		
		CommitFinder finder = new CommitFinder(repository);
		GitCommitListFilter filter = new GitCommitListFilter();
		finder.setFilter(filter);
		finder.findFrom(end);
		List<Commit> commits = filter.getCommits();
		Collections.reverse(commits);
		for (Commit commit : commits)
		{
			countCommits++;
			if (SuspectionOracle.isSuspicious(pl, commit))
			{
				countFixes++;
			}
			fixesHistogram.add(commit.getDate(),countFixes/(double)countCommits);
		}
		
		System.out.println(countFixes/(double)countCommits);
		System.out.println(countFixes);
		System.out.println(countCommits);

		File output = new File(workTree, "historyAnalyze.csv");
		output.createNewFile();
		
		FileWriter fw = new FileWriter(output);
		fw.append("Commits;Fixes\n");
		fw.append(countCommits+";"+countFixes+"\n\n");
		
		fw.append("Date;\"Relative Fixes\"\n");
		
		for (int i = 0; i < fixesHistogram.size(); i++)
		{
			Date d = fixesHistogram.getFirst(i);
			double r = fixesHistogram.getSecond(i);
			fw.append(String.format(Locale.US, "%d-%d-%d;%f\n", d.getYear()+1900, d.getMonth()+1, d.getDay()+1, r));
		}
		fw.close();
	}

}
