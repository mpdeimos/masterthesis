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
package com.mpdeimos.ct_tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.conqat.lib.commons.string.StringUtils;

import junit.framework.Assert;


/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
public class ReplaceTest {
	final static int loops = 10000;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		testOwnNormalization();
//		benchmark();
		StringUtil_encodeAsHexBenchmark();
	}
	/**
	 * 
	 */
	private static void StringUtil_encodeAsHexBenchmark() {
		
		byte[] bytes = new byte[Byte.MAX_VALUE];
		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] = (byte)i;
		}
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < loops; i++)
		{
			StringUtils.encodeAsHex(bytes);
		}
		System.out.println("string util took " + (System.currentTimeMillis() - start) + " ms");
		start = System.currentTimeMillis();
		
		start = System.currentTimeMillis();
		for (int i = 0; i < loops; i++)
		{
			encodeAsHex(bytes);
		}
		System.out.println("string util took " + (System.currentTimeMillis() - start) + " ms");
		start = System.currentTimeMillis();
		
	}
	private static String encodeAsHex(byte[] data) {
		String fmt = "%02X";
		StringBuilder sb = new StringBuilder();
		Object[] cp = new Object[data.length];
		for (int i = 0; i < data.length; i++) {
			sb.append(fmt);
			cp[i] = (byte)(data[i] & 0xff);
		}
		return String.format(sb.toString(), cp);
	}
	/**
	 * 
	 */
	private static void testOwnNormalization() {
		Assert.assertEquals("a", normalizeNewlines("a"));
		Assert.assertEquals("abc", normalizeNewlines("abc"));
		Assert.assertEquals("abc\n", normalizeNewlines("abc\n"));
		Assert.assertEquals("\n", normalizeNewlines("\n"));
		Assert.assertEquals("\n", normalizeNewlines("\n\r"));
		Assert.assertEquals("\n", normalizeNewlines("\r\n"));
		Assert.assertEquals("\n", normalizeNewlines("\r"));
		Assert.assertEquals("\n\n", normalizeNewlines("\n\n"));
		Assert.assertEquals("\n\n", normalizeNewlines("\n\r\n"));
		Assert.assertEquals("\n\n", normalizeNewlines("\r\n\r"));
		Assert.assertEquals("\n\n", normalizeNewlines("\r\r"));
		Assert.assertEquals("", normalizeNewlines(""));
		Assert.assertEquals("a\n\nb\n", normalizeNewlines("a\r\rb\r"));
		Assert.assertEquals("a\n\nb\n", normalizeNewlines("a\n\r\n\rb\n\r"));
		Assert.assertEquals("a\n\nb\n", normalizeNewlines("a\n\r\nb\r"));
		Assert.assertEquals("a\n\nbaa\naa\na", normalizeNewlines("a\n\r\n\rbaa\raa\n\ra"));
	}
	
	private static void benchmark() {
		try {
			File f = new File("/media/truecrypt3/ExposureSetDataAccessor.cs");
			String contents = new Scanner( f ).useDelimiter("\\A").next();
			
			long start = System.currentTimeMillis();
			for (int i = 0; i < loops; i++)
			{
				contents.replaceAll("\\r\\n|\\n\\r|\\r|\\n", "\n");
			}
			System.out.println("replaceAll rn, nr, r, n took " + (System.currentTimeMillis() - start) + " ms");
			start = System.currentTimeMillis();
			
			for (int i = 0; i < loops; i++)
			{
				contents.replaceAll("\\r\\n|\\n\\r|\\r", "\n");
			}
			System.out.println("replaceAll rn, nr, r took " + (System.currentTimeMillis() - start) + " ms");
			start = System.currentTimeMillis();
			
			Pattern p = Pattern.compile("\\r\\n|\\n\\r|\\r");
			for (int i = 0; i < loops; i++)
			{
				p.matcher(contents).replaceAll("\n");
			}
			System.out.println("pattern rn, nr, r took " + (System.currentTimeMillis() - start) + " ms");
			start = System.currentTimeMillis();
			
			for (int i = 0; i < loops; i++)
			{
				String r = contents.replace("\n\r", "\n");
				r = r.replace("\r\n", "\n");
				r = r.replace("\r", "\n");
			}
			System.out.println("simple replace rn, nr, r took " + (System.currentTimeMillis() - start) + " ms");
			start = System.currentTimeMillis();
			
			for (int i = 0; i < loops; i++)
			{
				normalizeNewlines(contents);
			}
			System.out.println("own method took " + (System.currentTimeMillis() - start) + " ms");
			start = System.currentTimeMillis();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	private static String normalizeNewlines(String contents) {
		StringBuilder r = new StringBuilder();
		int begin = 0;
		int length = contents.length();
		boolean lastWasCR = false;
		boolean lastWasNL = false;
		char c;
		for(int j = 0; j < length; j++)
		{
			c = contents.charAt(j);
			if (lastWasCR)
			{
				r.append(contents.substring(begin, j-1));
				begin = j;
				if (c != '\n')
				{
					r.append("\n");
				}
				lastWasCR = c == '\r';
				lastWasNL = false;
				continue;
			}
			if (lastWasNL && c == '\r')
			{
				r.append(contents.substring(begin, j));
				begin = j+1;
				lastWasCR = false;
				lastWasNL = false;
				continue;
			}
			lastWasCR = c == '\r';
			lastWasNL = c == '\n';
		}
		if (begin < length)
		{
			if (lastWasCR)
			{
				r.append(contents.substring(begin, length-1));
				r.append("\n");
			}
			else
			{
				r.append(contents.substring(begin, length));
			}
		}
		return r.toString();
	}

}
