package org.ivoa.dm;

import java.util.Vector;

import org.ivoa.dm.model.MetadataObject;

public class TransientObjectManager extends Vector<MetadataObject> implements IMetadataObjectContainer{

	private DataModelManager dmm;
	public TransientObjectManager(DataModelManager dmm){
		this.dmm = dmm;
	}
	@Override
	public IMetadataObjectContainer getContainer() {
		return null;
	}

}
