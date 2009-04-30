package org.ivoa.metamodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for Element complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="Element">  &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">      &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>      &lt;attribute name="xmiid" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>  &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Element", propOrder =  {
  "name", "description"})
@XmlSeeAlso({Package.class, Attribute.class, Relation.class, Type.class, Model.class})
public abstract class Element {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  protected String                                                                                                name;
  /**
   * TODO : Field Description
   */
  protected String description;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlID
  @XmlSchemaType(name = "ID")
  protected String xmiid;

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
   * Gets the value of the description property.
   *
   * @return possible object is {@link String }
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of the description property.
   *
   * @param value allowed object is {@link String }
   */
  public void setDescription(final String value) {
    this.description = value;
  }

  /**
   * Gets the value of the xmiid property.
   *
   * @return possible object is {@link String }
   */
  public String getXmiid() {
    return xmiid;
  }

  /**
   * Sets the value of the xmiid property.
   *
   * @param value allowed object is {@link String }
   */
  public void setXmiid(final String value) {
    this.xmiid = value;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
