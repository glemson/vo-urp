
package org.ivoa.metamodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValueType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}Type">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValueType")
@XmlSeeAlso({
    DataType.class,
    Enumeration.class,
    PrimitiveType.class
})
public abstract class ValueType
    extends Type
{


}
