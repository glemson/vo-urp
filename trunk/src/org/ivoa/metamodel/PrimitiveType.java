package org.ivoa.metamodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for PrimitiveType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;PrimitiveType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}ValueType&quot;&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrimitiveType")
public class PrimitiveType extends ValueType {
  /* no-op */

}
