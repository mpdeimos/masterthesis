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
package com.mpdeimos.ct_tests.configuration;

import org.conqat.engine.code_clones.normalization.repetition.RepetitiveStatementsRegionMarker;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.engine.resource.mark.ContentMarker;
import org.conqat.engine.resource.regions.RegexRegionMarker;
import org.conqat.engine.sourcecode.analysis.BlockMarker;
import org.conqat.engine.sourcecode.resource.ITokenResource;

import com.mpdeimos.ct_tests.configuration.CDConfiguration.Preprocessor;

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
@AConQATProcessor(description="TODO")
public class InputClassificationPreprocessor extends ConfigurationProcessor<InputClassificationPreprocessor> implements Preprocessor {

	private PatternList regionIgnorePattern;
	private int minLength;
	private PatternList fileIgnorePattern;
	private PatternList blockIgnorePattern;

	@AConQATParameter(name = "ignore-file", minOccurrences = 0, maxOccurrences = 1, description = "TODO")
	public void setIgnoreFile(
			@AConQATAttribute(name = "pattern", description = "TODO") PatternList fp)
	{
		this.fileIgnorePattern = fp;
	}
	
	@AConQATParameter(name = "ignore-region", minOccurrences = 0, maxOccurrences = 1, description = "TODO")
	public void setIgnoreRegion(
			@AConQATAttribute(name = "pattern", description = "TODO") PatternList rp)
	{
		this.regionIgnorePattern = rp;
	}
	
	@AConQATParameter(name = "ignore-blocks", minOccurrences = 0, maxOccurrences = 1, description = "TODO")
	public void setIgnoreBlocks(
			@AConQATAttribute(name = "pattern", description = "TODO") PatternList rp)
	{
		this.blockIgnorePattern = rp;
	}
	
	@AConQATParameter(name = "repetition", minOccurrences = 1, maxOccurrences = 1, description = "TODO")
	public void setClones(
			@AConQATAttribute(name = "min-length", description = "TODO") int minLength)
	{
				this.minLength = minLength;
	}
	
	/** {@inheritDoc} 
	 * @throws ConQATException */
	@Override
	public ITokenResource preprocess(ITokenResource tokens) throws ConQATException {
		// Input Classification
		// TODO other restrictions from java clone tracking?
		
		ProcessorInfoMock processorInfoMock = new ProcessorInfoMock();
		
		if (this.blockIgnorePattern != null)
		{
			BlockMarker bm = new BlockMarker();
			bm.init(processorInfoMock);
			bm.setRegionSetName("ignore");
			bm.setRoot(tokens);
			bm.setPatterns(this.blockIgnorePattern);
			bm.process();
		}
		
		if (regionIgnorePattern != null)
		{
			RegexRegionMarker rrm = new RegexRegionMarker();
			rrm.init(processorInfoMock);
			rrm.setRoot(tokens);
			rrm.setRegionParameters(this.regionIgnorePattern, "package statements", false);
			rrm.setRegionSetName("ignore");
			rrm.process();
		}
		
		if (fileIgnorePattern != null) {
			ContentMarker cm = new ContentMarker();
			cm.init(processorInfoMock);
			cm.setRoot(tokens);
			cm.setPatternList(this.fileIgnorePattern);
			cm.setMarkValue("ignore", "true", "boolean");
			cm.process();
		}
//		RepetitiveStatementsRegionMarker rsrm = new RepetitiveStatementsRegionMarker();
//		rsrm.init(processorInfoMock);
//		rsrm.setRoot(tokens);
//		rsrm.setMinLength(this.minLength, 2, 1, 10);
//		rsrm.process(); // pipelined
		
		return tokens;
	}

}
