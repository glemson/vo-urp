package org.ivoa.dm.model.visitor;

import org.ivoa.dm.model.visitor.*;
import org.ivoa.dm.ObjectClassType;

import org.ivoa.dm.model.MetadataObject;

import org.ivoa.metamodel.Attribute;
import org.ivoa.metamodel.Reference;


/**
 * This test Visitor calculates a simple XML representation of a starting object.<br>
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class ToXML implements Visitor {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /** TODO : Field Description */
  private StringBuffer sb;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Creates a new ToXML object
   */
  public ToXML() {
    sb = new StringBuffer();
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param object
   */
  public void preProcess(final MetadataObject object) {
    sb.append("<").append(object.getClassName()).append(">\n");

    ObjectClassType                 metadataObject = object.getClassMetaData();
    java.util.Collection<Attribute> attributes     = metadataObject.getAttributeList();

    for (final Attribute attribute : attributes) {
      sb.append("<").append(attribute.getName()).append(">");
      sb.append(object.getProperty(attribute.getName()));
      sb.append("</").append(attribute.getName()).append(">\n");
    }

    java.util.Collection<Reference> references = metadataObject.getReferenceList();

    for (final Reference reference : references) {
      sb.append("<").append(reference.getName()).append(">");

      MetadataObject referencedObject = (MetadataObject) object.getProperty(reference.getName());

      if (referencedObject != null) {
        sb.append(referencedObject.getIvoId());
      }

      sb.append("</").append(reference.getName()).append(">\n");
    }
  }

  /**
   * TODO : Method Description
   *
   * @param object
   */
  public void postProcess(final MetadataObject object) {
    sb.append("</").append(object.getClassName()).append(">\n");
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getXML() {
    return sb.toString();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
