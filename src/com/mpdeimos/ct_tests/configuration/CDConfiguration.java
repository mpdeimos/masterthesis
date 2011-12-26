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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.core.constraint.ICloneClassConstraint;
import org.conqat.engine.code_clones.index.CloneIndexBuilder;
import org.conqat.engine.code_clones.index.CloneIndexCloneDetector;
import org.conqat.engine.code_clones.normalization.provider.IUnitProvider;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.build.IElementFactory;
import org.conqat.engine.resource.build.ResourceBuilder;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.scanner.ELanguage;

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
@AConQATProcessor(description = "TODO")
public class CDConfiguration extends ConfigurationProcessor<CDConfiguration> {
	private static final int CLONE_MINLENGTH = 7;
	
	@AConQATFieldParameter(attribute = "value", parameter = "case-sensitive", description = "", optional=true)
	public boolean caseSensitive = true;
	
	@AConQATFieldParameter(attribute = "clone", parameter = "min-length", description = "", optional=true)
	public int cloneMinLength = CLONE_MINLENGTH;
	
	private ArrayList<Preprocessor> preprocessors = new ArrayList<Preprocessor>();
	@AConQATParameter(name = "preprocessor", description = "TODO")
	public void addPreprocessor(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) Preprocessor preprocessor
			) throws ConQATException {
		this.preprocessors.add(preprocessor);
	}
	private ArrayList<IElementFactory> factories = new ArrayList<IElementFactory>();
	@AConQATParameter(name = "scope-factories", description = "TODO")
	public void addPreprocessor(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IElementFactory f
			) throws ConQATException {
		this.factories.add(f);
	}
	
	/** Normalizations used. */
	private final Map<ELanguage, IUnitProvider<ITokenResource, Unit>> normalizations = new EnumMap<ELanguage, IUnitProvider<ITokenResource, Unit>>(
			ELanguage.class);
	@AConQATParameter(name = "include", description = "Sets the normalization used for a given language.", minOccurrences = 1, maxOccurrences = 1)
	public void setIncludePattern(
	@AConQATAttribute(name = "pattern", description = "The include pattern.") String pattern)
	{
		includePattern = pattern;
	}
	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "normalization", description = "Sets the normalization used for a given language.", minOccurrences = 1)
	public void setNormalization(
			@AConQATAttribute(name = "language", description = "The language for which the normalization applies.") ELanguage language,
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IUnitProvider<ITokenResource, Unit> normalization)
			throws ConQATException {
		

		if (normalizations.put(language, normalization) != null) {
			throw new ConQATException(
					"Duplicate normalization applied for language " + language);
		}
	}
	
	private List<ICloneClassConstraint> constraints = new ArrayList<ICloneClassConstraint>();

	private String includePattern;
	
	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "constraint", minOccurrences = 0, maxOccurrences = -1, description = ""
			+ "Adds a constraint that each detected clone class must satisfy")
	public void addConstraint(
			@AConQATAttribute(name = "type", description = "Clone classes that do not match the constraint are filtered") ICloneClassConstraint constraint) {
		constraints.add(constraint);
	}

	public ITokenResource preprocess(ITokenResource tokens) throws ConQATException
	{
		for (Preprocessor p : preprocessors)
		{
			tokens = p.preprocess(tokens);
		}
		return tokens;
	}

	/**
	 * @param resourceBuilder
	 * @throws ConQATException 
	 */
	public void attachFactory(ResourceBuilder resourceBuilder) throws ConQATException {
		for (IElementFactory factory : this.factories)
		{
			resourceBuilder.addFactory("**", factory, this.caseSensitive);
		}
	}
	
	/**
	 * @param cloneIndexBuilder
	 * @throws ConQATException 
	 */
	public void attachNormalization(CloneIndexBuilder cloneIndexBuilder) throws ConQATException {
		for (ELanguage lang : this.normalizations.keySet())
		{
			cloneIndexBuilder.setNormalization(lang, this.normalizations.get(lang));
		}
	}

	/**
	 * @param language
	 * @return
	 */
	public IUnitProvider<ITokenResource, Unit> getNormalization(
			ELanguage language) {
		return this.normalizations.get(language);
	}
	
	
	public static interface Preprocessor
	{
		public ITokenResource preprocess(ITokenResource tokens) throws ConQATException;
	}
	
	public int getCloneMinLength()
	{
		return this.cloneMinLength;
	}

	/**
	 * @param detector
	 */
	public void attachConstraints(CloneIndexCloneDetector detector) {
		for (ICloneClassConstraint constraint: this.constraints)
		{
			detector.addConstraint(constraint);
		}
	}
	
	public String getIncludePattern()
	{
		return this.includePattern;
	}
}
