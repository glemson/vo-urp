package org.ivoa.votable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ParamRef complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="ParamRef">  &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />    &lt;/restriction>
 *   &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParamRef")
public class ParamRef {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlAttribute(required = true)
  @XmlIDREF
  @XmlSchemaType(name = "IDREF")
  protected Object ref;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Gets the value of the ref property.
   *
   * @return possible object is {@link Object }
   */
  public Object getRef() {
    return ref;
  }

  /**
   * Sets the value of the ref property.
   *
   * @param value allowed object is {@link Object }
   */
  public void setRef(final Object value) {
    this.ref = value;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
