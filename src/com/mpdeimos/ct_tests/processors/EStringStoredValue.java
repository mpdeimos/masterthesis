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
package com.mpdeimos.ct_tests.processors;

import org.conqat.engine.code_clones.core.KeyValueStoreBase;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.commons.node.IConQATNode;

/**
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating RED Hash:
 */
public enum EStringStoredValue {
	
	BUGSUSPECTION("bugsuspection"),
	BUGSUSPECTION_REV("bugsuspection_rev"),
	BUGSUSPECTION_N("bugsuspection%d"),
	BUGSUSPECTION_N_REV("bugsuspection%d_rev"),
	;
	
	private String key;
	
	private EStringStoredValue(String key)
	{
		this.key = key;
	}
	
	public void set(KeyValueStoreBase kv, String value, Object... args)
	{
		kv.setValue(getKey(args), value);
	}
	
	public String get(KeyValueStoreBase kv, Object... args)
	{
		return kv.getString(getKey(args));
	}

	public void mark(IConQATNode node, Object... args) {
		node.setValue(getKey(args), true);
	}
	public boolean isMarked(IConQATNode node, Object... args) {
		Object o = node.getValue(getKey(args));
		return (o != null);
	}
	
	public String getKey(Object... args)
	{
		String k = key;
		if (args != null && args.length > 0)
			k = String.format(k, args);
		
		return k;
	}
}
