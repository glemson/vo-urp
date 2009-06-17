/*
 * Visitor.java
 *
 * Author Gerard Lemson
 * Created on 29 Jun 2008
 */
package org.ivoa.dm.model;



/**
 * Represents a visitor in the visitor pattern.<br>
 * Can traverse a MetadataObjbect containment tree.
 *
 * By default traverses only collections.
 * Has a pre- and a postprocess method.
 * The former is called before the contained collections are traversed,
 * the latter afterwards.
 *
 * TODO decide whether we want to traverse a complete graph, in which case
 * we need to keep track of whether objects have already been visited, as
 * objects may now be reached in multiple ways.
 *
 * @author Gerard Lemson
 * @since 29 Jun 200829 Jun 2008
 */
public abstract class Visitor {
  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Process the specified object before its collections are being processed.</br>
   * @param object
   */
  public abstract void preProcess(final MetadataObject object);

  /**
   * Process the specified object after its collections have been processed.</br>
   * @param object
   */
  public abstract void postProcess(final MetadataObject object);


  // ~ utility methods for accessing protected methods of MetadataObject

  protected void setIvoID(MetadataObject object, String ivoId)
  {
	    object.getIdentity().setIvoId(ivoId);
  }
  protected void setXmlId(MetadataObject object, String xmlId)
  {
	    object.getIdentity().setIvoId(xmlId);
  }
  protected void resetReferencesAfterMarshalling(MetadataObject object)
  {
	  object.resetReferencesAfterMarshalling();
  }
  protected void prepareReferencesForMarshalling(MetadataObject object)
  {
	  object.prepareReferencesForMarshalling();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------





