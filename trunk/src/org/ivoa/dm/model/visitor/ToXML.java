/*
 * ToXML.java
 * 
 * Author Gerard Lemson
 * Created on 29 Jun 2008
 */
package org.ivoa.dm.model.visitor;

import org.ivoa.dm.ObjectClassType;
import org.ivoa.dm.model.MetadataObject;
import org.ivoa.metamodel.Reference;
import org.ivoa.metamodel.Attribute;

/**
 * This test Visitor calculates a simple XML representation of a starting object.<br/>
 * 
 * @author Gerard Lemson
 * @since 29 Jun 200829 Jun 2008
 */
public class ToXML implements Visitor {

  // StringBuffer that is used to build up the XML representation
  private StringBuffer sb;
  public ToXML()
  {
    sb = new StringBuffer();
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
  }
  public void preProcess(MetadataObject object) {
    sb.append("<").append(object.getClassName()).append(">\n");
    ObjectClassType metadataObject = object.getClassMetaData();
    java.util.Collection<Attribute> attributes = metadataObject.getAttributeList();
    for(Attribute attribute: attributes)
    {
      sb.append("<").append(attribute.getName()).append(">");
      sb.append(object.getProperty(attribute.getName()));
      sb.append("</").append(attribute.getName()).append(">\n");
    }
    java.util.Collection<Reference> references = metadataObject.getReferenceList();
    for(Reference reference: references)
    {
      sb.append("<").append(reference.getName()).append(">");
      MetadataObject referencedObject = (MetadataObject)object.getProperty(reference.getName());
      if(referencedObject != null)
        sb.append(referencedObject.getIvoId());
      sb.append("</").append(reference.getName()).append(">\n");
    }
  }

  public void postProcess(MetadataObject object) {
    sb.append("</").append(object.getClassName()).append(">\n");
  }

  public String getXML()
  {
    return sb.toString();
  }
}
