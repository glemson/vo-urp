/*
 * ReferenceResolver.java
 * 
 * Author Gerard Lemson
 * Created on 29 Jun 2008
 */
package org.ivoa.dm.model.visitor;

import java.util.IdentityHashMap;
import java.util.Map;

import org.ivoa.dm.ObjectClassType;
import org.ivoa.dm.model.MetadataElement;
import org.ivoa.dm.model.MetadataObject;
import org.ivoa.metamodel.Collection;
import org.ivoa.metamodel.Reference;
/**
 * This class resolves lazy references.<br/>
 * For the object to be handled it checks whether any references have been set
 * only "lazily", i.e. using a Reference object
 * 
 * @author Gerard Lemson
 * @since 30 Jun 200830 Jun 2008
 */
public class ReferenceResolver implements Visitor {
  /** TODO : Field Description */
  public static final int DEFAULT_IDENTITY_CAPACITY = 128;

  /** Holds on to IDs of objects already resolved/loaded */
  private final Map<MetadataElement, Object> ids;

  public ReferenceResolver()
  {
    ids = new IdentityHashMap<MetadataElement, Object>(DEFAULT_IDENTITY_CAPACITY);
  }
  public void postProcess(MetadataObject object) {
    // TODO Auto-generated method stub

  }

  public void preProcess(MetadataObject object) {
    String                                        propertyName;
    Object                                        value;
    MetadataObject                                child;
    java.util.Collection<?extends MetadataObject> col;
    ObjectClassType                               ct = object.getClassMetaData();
    // navigate through references :
    // implies that lazy references will be resolved by ReferenceResolver :
    for (final Reference r : ct.getReferences().values()) {
      propertyName = r.getName();
      value = object.getProperty(propertyName);

// TODO if we want to process the referenced objects, this should be handled by the traversal        
    }



  }

}
