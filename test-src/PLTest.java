import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.ConQATException;
import org.junit.Test;

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

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
public class PLTest {

	@Test
	public void testMatch() {
//		System.out.println(("abc".substring(0,Math.min(3, 100))));
//		File f = new File(this.getClass().getResource("test").toURI()); //(.|\\s)*
		assertTrue(Pattern.compile("(?is).*(this code was generated by a tool).*").matcher("foo\n//     This code was generated by a tool.\nfoo\n").matches());
		assertTrue(Pattern.compile("(?is).*(fix|bug|defe(c|k)t|fehler|behoben|co(r|rr)ect|ko(r|rr)igier).*").matcher("Rename Fix Dev folder into DEMCore\r\n\r\ngit-tfs-id").matches());
//		assertTrue(Pattern.compile("(?i)(.|\n|\r)*(fix|bug|defe(c|k)t|fehler|behoben|co(r|rr)ect|ko(r|rr)igier)(.|\n|\r)*").matcher("Rename Dev folder into DEMCore\r\n\r\ngit-tfs-id: [http://tfs.dev.munich.munichre.com:8080/tfs/DefaultCollection]$/UWPF_DetExp_v1/DEMCore/Main/Source;C209128\r\n").matches());
//		assertTrue(Pattern.compile("(?i)(.|\n)*(fix|bug|defe(c|k)t|fehler|behoben)(.|\n)*").matcher(f.).matches());
	}
	
	@Test
	public void testFileIgnore() throws ConQATException
	{
		PatternList pl = new PatternList();
		pl.add(CommonUtils.compilePattern("(?m)^(.*)$"));
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("/media/truecrypt2/DEMDetHaz/Common/CommonData/DataSets/Cam_CatAnalysisProjectDS.Designer.cs")));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while(line != null)
			{
				sb.append(line + "\n");
				line = br.readLine();
			}
			System.out.println(sb.toString());
			Assert.assertTrue(pl.matchesAny(sb.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
