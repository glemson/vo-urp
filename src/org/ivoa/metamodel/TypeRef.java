package org.ivoa.metamodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TypeRef complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="TypeRef">  &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="xmiidref" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="relation" type="{http://www.w3.org/2001/XMLSchema}string" />    &lt;/restriction>
 *   &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TypeRef")
public class TypeRef {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlAttribute
  protected String                                                     name;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  @XmlIDREF
  @XmlSchemaType(name = "IDREF")
  protected Object xmiidref;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  protected String relation;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Gets the value of the name property.
   *
   * @return possible object is {@link String }
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of the name property.
   *
   * @param value allowed object is {@link String }
   */
  public void setName(final String value) {
    this.name = value;
  }

  /**
   * Gets the value of the xmiidref property.
   *
   * @return possible object is {@link Object }
   */
  public Object getXmiidref() {
    return xmiidref;
  }

  /**
   * Sets the value of the xmiidref property.
   *
   * @param value allowed object is {@link Object }
   */
  public void setXmiidref(final Object value) {
    this.xmiidref = value;
  }

  /**
   * Gets the value of the relation property.
   *
   * @return possible object is {@link String }
   */
  public String getRelation() {
    return relation;
  }

  /**
   * Sets the value of the relation property.
   *
   * @param value allowed object is {@link String }
   */
  public void setRelation(final String value) {
    this.relation = value;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
