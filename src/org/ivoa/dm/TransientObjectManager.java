package org.ivoa.dm;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.MetadataRootEntities;
import org.ivoa.dm.model.MetadataRootEntityObject;
import org.ivoa.dm.model.reference.ReferenceResolver;


public class TransientObjectManager extends ArrayList<MetadataRootEntityObject> implements IMetadataObjectContainer{

	/**
	 * 
	 */
  private static final long serialVersionUID = 3500784209700310075L;

  private DataModelManager dmm;

	public TransientObjectManager(String pu){
		this.dmm = new DataModelManager(pu);
	}

	public void addEntity(MetadataRootEntityObject entity){
		this.add(entity);
	}

	public void persist(String userName){
		dmm.persist(this,userName);
	}
	public void writeXML(String targetXML)
	{
		MetadataRootEntities ent = new MetadataRootEntities();
		List<MetadataRootEntityObject> ents = ent.getEntity();
		for (MetadataRootEntityObject eo : this)
			ents.add(eo);

		ModelFactory mf = ModelFactory.getInstance();
		mf.marshallObject(targetXML, ent);
	}
	
	public List<MetadataObject>  queryJPA(String query) {
		return dmm.queryJPA(query);
	}
	@Override
	public IMetadataObjectContainer getContainer() {
		return null;
	}

}
