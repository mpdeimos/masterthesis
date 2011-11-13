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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Ignore;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.filesystem.DirectoryOnlyFilter;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.TestletBase;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.report.CloneReportReader;
import org.conqat.engine.code_clones.core.utils.CloneUtils;
import org.conqat.engine.code_clones.core.utils.ECloneClassChange;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.logging.testutils.TestDriver;

/**
 * Runs a clone trace scenario computation.
 * 
 * @author juergens
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating YELLOW Hash: 5A06F69FA09B77F24373892A41F5A480
 */
// Ignore tells JUnit runner not to execute testlet
@Ignore
public class TracingScenarioTestlet extends TestletBase {

	/** Name of the file that describes the expected results */
	public static String EXPECTED_RESULT_FILENAME = "expectedresult.txt";

	/** Folder that contains code for different system versions */
	private final File traceScenarioFolder;

	/** Constructor */
	public TracingScenarioTestlet(File traceScenarioFolder) {
		this.traceScenarioFolder = traceScenarioFolder;
	}

	/** Return name of trace scenario folder as test name */
	@Override
	public String getName() {
		return traceScenarioFolder.getName();
	}

	/** Use smoke test logging configuration */
	@Override
	protected void setUp() throws IOException {
		if (targetDirectory().exists()) {
			FileSystemUtils.deleteRecursively(targetDirectory());
		}
	}

	/** Run ConQAT on the configuration. */
	@Override
	public void test() throws IOException, ConQATException, DriverException {

		File systemVersionFolder = new File(traceScenarioFolder, "git");
		if (!systemVersionFolder.isDirectory())
			fail(systemVersionFolder.getAbsolutePath() + " needs to be a directory");

		TestDriver driver = prepareDriver();
		driver.addCommandLineProperty("dir.src="
				+ systemVersionFolder.getAbsolutePath());
		driver.addCommandLineProperty("dir.out="
				+ targetDirectory());
		driver.addBundleCollection("../conqat-root/engine");
		driver.drive(getConfigurationName());

		File expectedResult = new File(traceScenarioFolder,
				EXPECTED_RESULT_FILENAME);

		assertExpectedResult(expectedResult, targetDirectory());
	}

	/** Assert that clone tracing produced the expected result */
	private void assertExpectedResult(File expectedResult, File reportDir)
			throws ConQATException {
		if (!expectedResult.canRead()) {
			fail("Cannot read expected results file: " + expectedResult);
		}

		String[] lines;
		try {
			lines = StringUtils.splitLines(FileSystemUtils
					.readFile(expectedResult));
		} catch (IOException e) {
			throw new ConQATException(e);
		}
		for (String line : lines) {
			if (isComment(line)) {
				continue;
			}

			assertReport(line, reportDir);
		}
	}

	/**
	 * Assert that the report contains the specified clones
	 */
	private void assertReport(String line, File reportDir)
			throws ConQATException {
		assertTrue("No ':' found in line: " + line, line.contains(":"));

		String[] parts = line.split(":");
		assertTrue(
				"Expecting lines of the form <reportname>:<cloneclasses>, but found: "
						+ line, parts.length <= 2);
		String reportName = parts[0];
		File report = new File(reportDir, reportName);
		assertTrue("Report not found: " + report, report.exists());

		CloneReportReader reader = new CloneReportReader(report);
		List<CloneClass> cloneClasses = reader.getCloneClasses();

		if (parts.length == 2) {
			String expectedCloneClassesDesc = parts[1];
			assertCloneClasses(expectedCloneClassesDesc, cloneClasses,
					reportName);
		} else {
			assertTrue("Expected empty report " + reportName + ", but found "
					+ cloneClasses.size() + " clone classes.", cloneClasses
					.isEmpty());
		}
	}

	/** Asserts that the clone classes in a list are as expected */
	private void assertCloneClasses(String expectedCloneClassesDesc,
			List<CloneClass> cloneClasses, String reportName) {

		String[] parts = expectedCloneClassesDesc.split("\\s*,\\s*");
		assertEquals("Unexpected number of clone classes found in report "
				+ reportName + ": " + expectedCloneClassesDesc, parts.length,
				cloneClasses.size());

		for (String part : parts) {
			CCSMAssert.isTrue(Pattern.matches("CC\\(\\d+(-[A-Z]+)?\\)", part),
					"Expected format CC(123-change) but found: " + part);
			String numberString;
			ECloneClassChange expectedChange = null;
			if (part.contains("-")) {
				numberString = part.substring(3, part.indexOf("-"));
				String changeString = part.substring(part.indexOf("-") + 1,
						part.indexOf(")"));
				expectedChange = ECloneClassChange.valueOf(changeString);
			} else {
				numberString = part.substring(3, part.indexOf(")"));
			}
			int expectedCardinality = Integer.valueOf(numberString);

			CloneClass cloneClass = getCloneClass(expectedCardinality,
					cloneClasses, expectedChange);
			assertNotNull("Did not find clone class of cardinality "
					+ expectedCardinality
					+ " (or not enough of them) with change type "
					+ expectedChange + " in report " + reportName, cloneClass);
			cloneClasses.remove(cloneClass);
		}

		assertTrue("Unexpected clone class found", cloneClasses.isEmpty());
	}

	/**
	 * Returns the first clone class of expected cardinality from a list, or
	 * null, if none is found
	 */
	private CloneClass getCloneClass(int expectedCardinality,
			List<CloneClass> cloneClasses, ECloneClassChange expectedChange) {
		for (CloneClass cloneClass : cloneClasses) {
			ECloneClassChange actualChange = CloneUtils
					.getChangeType(cloneClass);
			if (cloneClass.size() == expectedCardinality
					&& (expectedChange == null || expectedChange == actualChange)) {
				return cloneClass;
			}
		}
		return null;
	}

	/** Determine whether a line is a comment */
	private boolean isComment(String line) {
		return line.startsWith("#") || line.startsWith("//");
	}

	/** Assert that folder is named according to a system date */
	private String systemDateString(File systemVersionFolder) {
		String name = systemVersionFolder.getName();
		CCSMAssert.isTrue(name.length() == 8,
				"Expecting folder names of style yyyyMMdd");
		CCSMAssert.isTrue(Pattern.matches("\\d+", name),
				"Expecting folder names of style yyyyMMDd");
		return name;
	}

	/** Prepare driver and set properties common to all runs */
	private TestDriver prepareDriver() throws IOException {
		TestDriver driver = new TestDriver();
		driver.readPropertyFile(getPropertiesFilename());
		driver.addCommandLineProperty("clone.minlength=5");
		return driver;
	}

	/** Return target folder */
	private File targetDirectory() throws IOException {
		File targetDirectory = new File(getTmpDirectory(), traceScenarioFolder
				.getName());
		FileSystemUtils.ensureDirectoryExists(targetDirectory);
		return targetDirectory;
	}

	/** Return named subfolder of target directory */
	private File targetDirectory(String name) throws IOException {
		File targetDirectory = new File(targetDirectory(), name);
		FileSystemUtils.ensureDirectoryExists(targetDirectory);
		return targetDirectory;
	}

	/** Return name of properties file */
	private String getPropertiesFilename() {
		return getConfigurationName() + ".cqr";
	}

	/** Return name of config file */
	private String getConfigurationName() {
		return useTestFile("CDLoopTest.cqb").getAbsolutePath();
	}

}