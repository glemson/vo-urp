package org.ivoa.metamodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ValueType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;ValueType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}Type&quot;&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValueType")
@XmlSeeAlso( { DataType.class, PrimitiveType.class, Enumeration.class })
public abstract class ValueType extends Type {
  /* no-op */

}
