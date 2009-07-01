package org.ivoa.dm.model.visitor;

import org.ivoa.dm.ObjectClassType;

import org.ivoa.dm.model.MetadataElement;
import org.ivoa.dm.model.MetadataObject;

import org.ivoa.metamodel.Reference;

import java.util.IdentityHashMap;
import java.util.Map;


/**
 * This class resolves lazy references.<br>
 * For the object to be handled it checks whether any references have been set only "lazily", i.e. using a Reference
 * object
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class ReferenceResolver implements Visitor<MetadataObject> {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** TODO : Field Description */
  public static final int DEFAULT_IDENTITY_CAPACITY = 128;

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** Holds on to IDs of objects already resolved/loaded */
  private final Map<MetadataElement, Object> ids;

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Creates a new ReferenceResolver object
   */
  public ReferenceResolver() {
    ids = new IdentityHashMap<MetadataElement, Object>(DEFAULT_IDENTITY_CAPACITY);
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param object 
   */
  public void postProcess(final MetadataObject object) {
    // TODO Auto-generated method stub
  }

  /**
   * TODO : Method Description
   *
   * @param object 
   */
  public void preProcess(final MetadataObject object) {
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
//~ End of file --------------------------------------------------------------------------------------------------------
