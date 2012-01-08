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
package com.mpdeimos.ct_tests.processors;

import java.io.File;
import java.lang.ref.SoftReference;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.conqat.engine.clone_tracking.editpropagation.CloneEditPropagator3;
import org.conqat.engine.clone_tracking.gateways.KeyValueGateway;
import org.conqat.engine.clone_tracking.matching.CloneMatcher3;
import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.core.IDatabaseSpace;
import org.conqat.engine.code_clones.core.KeyValueStoreBase;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.core.utils.CloneUtils;
import org.conqat.engine.code_clones.core.utils.EBooleanStoredValue;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.code_clones.index.CloneIndex;
import org.conqat.engine.code_clones.index.CloneIndexBuilder;
import org.conqat.engine.code_clones.index.CloneIndexCloneDetector;
import org.conqat.engine.code_clones.index.store.ICloneIndexStore;
import org.conqat.engine.code_clones.normalization.provider.IUnitProvider;
import org.conqat.engine.code_clones.result.CloneReportWriterProcessor;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.driver.instance.ConQATStringPool;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.engine.persistence.DatabaseUtils;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.build.ResourceBuilder;
import org.conqat.engine.resource.scope.filesystem.FileSystemScope;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenResourceSelector;
import org.conqat.lib.commons.algo.Diff.Delta;
import org.conqat.lib.commons.collections.CollectionUtils;

import com.mpdeimos.ct_tests.configuration.CDConfiguration;
import com.mpdeimos.ct_tests.looper.RevisionInfo;
import com.mpdeimos.ct_tests.looper.RevisionLooperMethodBase;
import com.mpdeimos.ct_tests.looper.SuspectionOracle;
import com.mpdeimos.ct_tests.vcs.Commit;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 139C7200BB445B7BEBD03FE30193C923
 */
@AConQATProcessor(description = "TODO")
public class CloneTrackingLoop extends ConQATProcessorBase  {

	private File baseDir;
	private File outDir;
	private ICloneIndexStore indexStore;

	private ITokenResource tokenResource;
	private String projectName;
	private RevisionLooperMethodBase revisionLooper;
	private IDatabaseSpace dbSpace;
	private PatternList regionIgnorePattern;
	private CDConfiguration config;
	private List<CloneDetectionResultElement> cdResultList;
	private boolean writeInterimResults;
	
	
	@AConQATFieldParameter(parameter="message-analyzer", attribute="pattern", description="")
	public PatternList messageAnanlyzerPattern;
	
	@AConQATParameter(name = "project", minOccurrences = 1, maxOccurrences = 1, description = "working directory")
	public void setFilename(
			@AConQATAttribute(name = "name", description = "project name", defaultValue="") String projectName,
			@AConQATAttribute(name = "includePattern", description = "IGNORED PARAM (TODO REMOVE)") String includePattern // TODO remove
			) throws ConQATException {
		this.projectName = projectName;
	}
	
	@AConQATParameter(name = "looper", minOccurrences = 1, maxOccurrences= -1, description = "working directory")
	public void setFilename(
			@AConQATAttribute(name = "ref", description = "looper implementation") RevisionLooperMethodBase revisionLooper
			) {
		this.revisionLooper = revisionLooper;
	}
	
	
	@AConQATParameter(name = "result", minOccurrences = 0, description = "detection result")
	public void setFilename(
			@AConQATAttribute(name = "list", description = "detection result list") SoftReference<List<CloneDetectionResultElement>> cdResultList
			) {
		this.cdResultList = cdResultList.get();
	}
	
	@AConQATParameter(name = "output", minOccurrences = 1, maxOccurrences = 1, description = "TODO")
	public void setOutput(
			@AConQATAttribute(name = "path", description = "TODO") String filename,
			@AConQATAttribute(name = "write-interim-results", description = "TODO") boolean writeInterimResults) throws ConQATException {
		this.writeInterimResults = writeInterimResults;
		this.outDir = new File(filename);
		if (!outDir.exists())
			outDir.mkdir();
		if (!outDir.isDirectory())
			throw new ConQATException(filename + " needs to be a directory.");
	}
	
	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "database", description = "Database space this processor works on", minOccurrences = 1, maxOccurrences = 1)
	public void setDb(
			@AConQATAttribute(name = "space", description = ConQATParamDoc.INPUT_REF_DESC) IDatabaseSpace dbSpace,
			@AConQATAttribute(name = "index-store", description = "TODO")
			ICloneIndexStore indexStore) throws ConQATException {
		this.indexStore = indexStore;
		this.dbSpace = dbSpace;
	}
	
	@AConQATParameter(name = "configuration", minOccurrences = 1, description = "TODO")
	public void setConfiguration(
			@AConQATAttribute(name = "ref", description = "TODO") CDConfiguration config)
	{
		this.config = config;
	}
	
	private ProcessorInfoMock infoMock = new ProcessorInfoMock();

	private Map<String, Unit[]> units = new HashMap<String, Unit[]>();
	private boolean disablePersist = false;
	
	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "dummy", minOccurrences = 0, maxOccurrences = -1, description = ""
			+ "TODO")
	public void addConstraint(
			@AConQATAttribute(name = "ref", description = "TODO") Object ref) {
		// nothing
	}
	
	@AConQATParameter(name = "persist", description = "", minOccurrences = 0, maxOccurrences = 1)
	public void setPersistDisabled(
			@AConQATAttribute(name = "disable", description = "") boolean disable) {
		this.disablePersist  = disable;
	}
	
	

	/** {@inheritDoc} */
	@Override
	public CloneDetectionResultElement process() throws ConQATException {
		try
		{
			CloneIndex index = new CloneIndex(indexStore, getLogger());
			CloneDetectionResultElement detectionResult = null;
			CloneDetectionResultElement lastResult = null;
	
			boolean initPhase = true;
			
			for (RevisionInfo revision : revisionLooper)
			{
				lastResult = detectionResult;
				
				Map<String, Unit[]> hMap = new HashMap<String, Unit[]>();
				hMap.putAll(units);
				units = hMap;
				
				getLogger().debug("### _______________________________ ###");
				getLogger().info("### Processing Revision " + revision.getIndex() + " ### " + revision.getID() + " ### ");
				infoMock.getLogger().info("### Processing Revision " + revision.getIndex() + " ### " + revision.getID() + " ### ");
				getLogger().debug("###");
				
				Commit vcsData = revision.getCommit();
				
				getLogger().debug("Commit id: " + vcsData.getId());
				getLogger().debug("Commit message: " + vcsData.getMessage());
				getLogger().debug("Commit files: " + vcsData.getAdded().size());
				for(String file : vcsData.getAdded())
				{
					getLogger().debug("    " + file);
				}
				
				ITokenResource resource = loop_buildTokenScope(revision.getPath());
				
				loop_updateCloneIndex(index, initPhase, vcsData, resource);
				initPhase = false;
				
				getLogger().info("... index updated");
				
				detectionResult = loop_runCloneDetection(revision, resource);
				if (writeInterimResults)
				{
					loop_writeResults(revision, detectionResult, "all-bt");
				}
				getLogger().info("... clones detected");
				
				CloneDetectionResultElement updatedDetectionResult = loop_cloneTracking(lastResult, detectionResult);
				getLogger().info("... clones tracked");
				
				KeyValueGateway keyValueGateway = null;
				if (!disablePersist)
					keyValueGateway = new KeyValueGateway(dbSpace, getLogger()); // TODO member?
				boolean newBugs = loop_markReportAndClones(detectionResult, revision, keyValueGateway);
				
				getLogger().info("... clones marked");
				
				if (cdResultList != null)
					cdResultList.add(detectionResult);
				if (writeInterimResults)
				{
					loop_writeResults(revision, detectionResult, "all");
					loop_writeResults(revision, updatedDetectionResult, "propagated");
				}
				
				if (newBugs)
				{
					loop_writeResults(revision, detectionResult, "suspect");
				}
				
				getLogger().info("... done");
			}
			
			if (detectionResult != null)
					loop_writeResults(null, detectionResult, "final");
			
				return detectionResult;
		}
		catch (ConQATException e)
		{
			getLogger().error("FAIL", e);
			throw e;
		}
	}

	private CloneDetectionResultElement loop_cloneTracking(CloneDetectionResultElement oldDetectionResult,
			CloneDetectionResultElement newDetectionResult) throws ConQATException {
		CloneEditPropagator3 cloneEditPropagator = new CloneEditPropagator3();
		cloneEditPropagator.init(infoMock);
		cloneEditPropagator.setDbSpace(this.dbSpace);
		cloneEditPropagator.setDetectionResult(oldDetectionResult, newDetectionResult);
		cloneEditPropagator.setPersistDisabled(disablePersist);
		CloneDetectionResultElement updatedDetectionResult = cloneEditPropagator.process();
		
		CloneMatcher3 cloneMatcher = new CloneMatcher3();
		cloneMatcher.init(infoMock);
		cloneMatcher.setDbSpace(this.dbSpace);
		cloneMatcher.setMinLength(config.getCloneMinLength());
		cloneMatcher.setNewDetectionResult(newDetectionResult);
		cloneMatcher.setUpdatedClones(updatedDetectionResult);
		cloneMatcher.setPersistDisabled(disablePersist);
		cloneMatcher.process(); // pipelined to detectionResult
		
		// TODO depending on how we decide on non-changing fingerprints we need to shift this into the clone matching algorithm
		for (CloneClass cloneClass : newDetectionResult.getList())
		{
			for (Clone clone : cloneClass.getClones())
			{
				Delta<Unit> delta = CloneUtils.getEditOperations(clone);
				// FIXME hack
				if (changesetSize(delta) > 0)
				{
					clone.setFingerprintLastModified(newDetectionResult.getSystemDate());
					EBooleanStoredValue.FINGERPRINT_CHANGED.setValue(clone, true);
				}
				// TODO persist to DB?
				// for now it doesn't make any difference since we're always comparing w/ latest run.
			}
		}
		return updatedDetectionResult;
	}

	/**
	 * @param delta
	 * @return
	 */
	private int changesetSize(Delta<Unit> delta) {
		if (delta == null)
			return 0;
		
		return delta.getSize();
	}

	private HashMap<Long, Clone> lastPotentialBugs = new HashMap<Long, Clone>();

	
	/**
	 * @param detectionResult
	 * @param revision
	 * @param keyValueGateway 
	 */
	private boolean loop_markReportAndClones(CloneDetectionResultElement detectionResult,
			RevisionInfo revision, KeyValueGateway keyValueGateway) throws ConQATException {
		try
		{
			HashMap<Long, Clone> newPotentialBugs = new HashMap<Long, Clone>();
			String message = revision.getCommit().getMessage();
			Statement stmt = null;
			if (!disablePersist)
				stmt = dbSpace.getDbConnection().createStatement();
			boolean potentialBug = SuspectionOracle.isSuspicious(messageAnanlyzerPattern, message);
			boolean newBugs = false;
			for (CloneClass cloneClass : detectionResult.getList())
			{
				if (EBooleanStoredValue.GHOST.falseForAllClones(cloneClass)) // no propagation needed
					continue;
				
				String bugMessage = null;
//					if (ECloneClassChange.INCONSISTENT == CloneUtils.getChangeType(cloneClass))
//					if (!EBooleanStoredValue.GHOST.falseForAllClones(cloneClass)) // inconsistent
		
				// if something has changed update w/ new suspection message /// XXX what to do w/ old message?
				if (potentialBug && EBooleanStoredValue.FINGERPRINT_CHANGED.trueForAtLeastOneClone(cloneClass)) // inconsistent
				{
					bugMessage = message;
					for (Clone clone : cloneClass.getClones())
					{
						addSuspection(keyValueGateway, message, stmt, clone);
						newPotentialBugs.put(clone.getId(), clone);
					}
					newBugs = true;
				}
				else
				{
					for (Clone clone : cloneClass.getClones())
					{
						if (EBooleanStoredValue.HAS_ANCESTOR.getValue(clone))
						{
							long ancestorId = CloneUtils.getAncestorId(clone);
							Clone ancestor = lastPotentialBugs.get(ancestorId);
							if (ancestor != null)
							{
								bugMessage = EStringStoredValue.BUGSUSPECTION.get(ancestor);
								addSuspection(keyValueGateway, bugMessage, stmt, clone);
								newPotentialBugs.put(clone.getId(), clone);
							}
						}
					}
				}
				
				if (bugMessage != null)
				{
					EStringStoredValue.BUGSUSPECTION.mark(detectionResult);
					addSuspection(keyValueGateway, bugMessage, stmt, cloneClass);
				}
			}
			
			if (!disablePersist)
				DatabaseUtils.executeAndClose(stmt);
			this.lastPotentialBugs = newPotentialBugs;
			
			return newBugs;
		}
		catch (SQLException e)
		{
			throw new ConQATException(e);
		}
	}

	private void addSuspection(KeyValueGateway keyValueGateway, String message,
			Statement stmt, KeyValueStoreBase kv) throws SQLException {
		EStringStoredValue.BUGSUSPECTION.set(kv, message);
		if (!disablePersist)
			stmt.addBatch(keyValueGateway.createInsertValueSql(kv.getId(), EStringStoredValue.BUGSUSPECTION.getKey(), message));
	}

	private CloneDetectionResultElement loop_runCloneDetection(RevisionInfo revision, ITokenResource resource)
			throws ConQATException {
		/// the clone detection
		CloneIndexCloneDetector detector = new CloneIndexCloneDetector();
		detector.init(infoMock);
		
		detector.setInput(resource);
		detector.setStoreFactory(indexStore);
		detector.setMinLength(config.getCloneMinLength()); // TODO make configurable
		detector.setIdProvider(dbSpace.getIdProvider());
		config.attachConstraints(detector);
		CloneDetectionResultElement result = detector.process();
		result = reattachUnits(result);
		
		return result;
	}

	/**
	 * @param result
	 * 
	 * TODO should be handled from within CloneIndexDetector
	 */
	private CloneDetectionResultElement reattachUnits(CloneDetectionResultElement result) {
		
		// attaches units to clone reports
		for (CloneClass cloneClass : result.getList())
		{
			for (Clone clone : cloneClass.getClones())
			{
				Unit[] fileUnits = units.get(clone.getUniformPath());
				
				// todo create something like an array backed ptr list w/ dynamic offsets
				List<Unit> cloneUnits = new ArrayList<Unit>(clone.getLengthInUnits());
				for (int i = 0; i < clone.getLengthInUnits(); i++)
				{
					cloneUnits.add(fileUnits[clone.getStartUnitIndexInElement() + i]);
				}
				
				CloneUtils.setUnits(clone, cloneUnits);
			}
		}
		
		// attach units globally
		return new CloneDetectionResultElement(result.getSystemDate(), result.getRoot(), result.getList(), units);
	}

	private void loop_writeResults(RevisionInfo revision,
			CloneDetectionResultElement result, String postfix) {
		try
		{
		File outFile = new File(outDir.getPath(), ( revision== null?"":revision.getIndex()) + "clones-" + postfix + ".xml");
//			CloneListReportWriterProcessor.writeReport(result.getList(), vcsData.getDate(), outFile, getLogger());
		CloneReportWriterProcessor.writeReport(result.getList(), result.getRoot(), result.getSystemDate(), outFile, getLogger());
		}
		catch (Throwable e)
		{
			getLogger().error(e.getMessage(), e);
		}
		
//		
//		* ERROR : Processor 'com.mpdeimos.ct_tests.CDLoop.revision-looper' failed badly: Inconsistent clone data: origin Project/src/Extensions/Banshee.Moblin/Banshee.Moblin/SearchEntry.cs unknown.
//		java.lang.AssertionError: Inconsistent clone data: origin Project/src/Extensions/Banshee.Moblin/Banshee.Moblin/SearchEntry.cs unknown.
	}

	private void loop_updateCloneIndex(CloneIndex index, boolean initPhase,
			Commit vcsData, ITokenResource resource) throws ConQATException,
			CloneDetectionException, StorageException {
		
		Map<String, ITokenElement> resourceMap = ResourceTraversalUtils.createUniformPathToElementMap(resource, ITokenElement.class);
		
		/// init of the index builder
		if (initPhase)
		{
			// TODO this could also be nuked, but there is some option stuff that is set via the index builder...
			// ...after some re-thinking: we may not always start with rev-1 in a repository, so keep for now
			CloneIndexBuilder cloneIndexBuilder = new CloneIndexBuilder();
			cloneIndexBuilder.init(infoMock);
			config.attachNormalization(cloneIndexBuilder);
			cloneIndexBuilder.setStore(indexStore);
			cloneIndexBuilder.setIgnoreKey("ignore");
			cloneIndexBuilder.setRoot(resource);
			cloneIndexBuilder.setChunkLength(config.getCloneMinLength());
			cloneIndexBuilder.process();
			
			for (String file : resourceMap.keySet())
			{
				extractUnits(resourceMap.get(file));
			}
		}
		else
		{
			for (String file : vcsData.getAdded())
			{
				file = projectName + "/" + file;
				
				ITokenElement iTokenElement = resourceMap.get(file);
				if (iTokenElement == null)
					continue;
				
				getLogger().info(" ++ " + file);
				
				index.insertFile(iTokenElement);
				extractUnits(iTokenElement);
			}
			for (String file : vcsData.getModified())
			{
				file = projectName + "/" + file;
				
				ITokenElement iTokenElement = resourceMap.get(file);
				if (iTokenElement == null)
					continue;
				
				getLogger().info(" +- " + file);
				
				if (indexStore.getChunksByOrigin(file) != null)
					indexStore.removeChunks(file);
				index.insertFile(iTokenElement);
				extractUnits(iTokenElement);
			}
			for (String file : vcsData.getDeleted())
			{
				file = projectName + "/" + file;
				
				getLogger().info(" -- " + file);
				
				units.remove(file);
				
				// we need to operate directly on the index store
				// other idea would be caching the elements from last run...
				// nevertheless we need to check if the files existed before otherwise we'll raise a NPE (-> bug?) XXX
				if (indexStore.getChunksByOrigin(file) != null)
					indexStore.removeChunks(file);
			}
		}
	}

	/**
	 * @param file
	 * @param iTokenElement
	 * @param units
	 * @throws CloneDetectionException 
	 */
	private void extractUnits(ITokenElement element) throws CloneDetectionException {
		ConQATStringPool.clear();
		IUnitProvider<ITokenResource, Unit> normalizer = config.getNormalization(element.getLanguage());
		
		normalizer.init(element, getLogger());
		ArrayList<Unit> fileUnits = new ArrayList<Unit>();
		Unit unit = null;
		while ((unit = normalizer.getNext()) != null) {
			if (!unit.isSynthetic()) {
				fileUnits.add(unit);
			}
		}
		units.put(element.getUniformPath(), CollectionUtils.toArray(fileUnits, Unit.class));
		ConQATStringPool.clear();
//		if (unitCount == 0) {
//			return 0;
//		}
	}

	/**
	 * @param dir
	 * @return
	 */
	private ITokenResource loop_buildTokenScope(File dir) throws ConQATException {
		FileSystemScope scopeBuilder = new FileSystemScope();
		scopeBuilder.init(infoMock);
		scopeBuilder.projectName = this.projectName;
		scopeBuilder.rootDirectoryName = dir.getPath();
		scopeBuilder.addIncludePattern(this.config.getIncludePattern());
		IContentAccessor[] scope = scopeBuilder.process();
		
		ResourceBuilder resourceBuilder = new ResourceBuilder();
		resourceBuilder.init(infoMock);
		config.attachFactory(resourceBuilder);
		resourceBuilder.addContentAccessors(scope);
		IResource root = resourceBuilder.process();
		
		TokenResourceSelector tokenResourceSelector = new TokenResourceSelector();
		tokenResourceSelector.init(infoMock);
		tokenResourceSelector.addRoot(root);
		
		ITokenResource tokens = tokenResourceSelector.process();
		
		return config.preprocess(tokens);
	}
}