/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
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
package com.mpdeimos.ct_tests.tracking.scenario;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.conqat.lib.commons.filesystem.DirectoryOnlyFilter;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * The tracing scenario test suite inspects this package's test data directory.
 * Each folder it finds in there is taken to be a clone tracing scenario. Each
 * subfolder is expected to contain source code in a single system version.
 * Clone tracing is performed across system versions (i.e. subfolders).
 * 
 * TODO (LH) Some of the tests fail on Linux
 * 
 * @author juergens
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating YELLOW Hash: 1749A627F2BE377CB3D40DEEE24C8565
 */
public class TracingScenarioTest extends CCSMTestCaseBase {

	/** Create a smoke test suite. */
	public static Test suite() {
		// find files
		File[] inputFolders = findEvolutionTestInputFolders();

		// create suite
		TestSuite suite = new TestSuite("EvolutionTest");
		suite.setName("Evolution Test [" + inputFolders.length
				+ " test folders]");
		for (File inputFolder : inputFolders) {
			if (!ignored(inputFolder)) {
				suite.addTest(new TracingScenarioTestlet(inputFolder));
			}
		}

		// return suite
		return suite;
	}

	/** Determines whether folder is ignored */
	private static boolean ignored(File inputFolder) {
		String folderName = inputFolder.getName();
		boolean ignored = folderName.contains(".svn")
				|| folderName.equals("unchanged");
		// Use this line to selectively include certain tests during debugging
		// boolean focused = folderName.contains("split");
		boolean focused = true;
		return ignored || !focused;
	}

	/** Determines the folders that serve as input for the evolution tests */
	private static File[] findEvolutionTestInputFolders() {
		// we have to create an instance of SmokeTest here, since useTestFile is
		// an instance method. This method must be static, since JUnit expects
		// suite methods to be static.
		File directory = new TracingScenarioTest().useTestFile("");
		return directory.listFiles(new DirectoryOnlyFilter());
	}

}