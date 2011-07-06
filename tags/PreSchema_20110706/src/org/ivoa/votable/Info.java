package org.ivoa.votable;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for Info complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="Info">  &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">      &lt;sequence>
 *         &lt;element name="DESCRIPTION" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}anyTEXT" minOccurs="0"/>
 *         &lt;element name="VALUES" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Values" minOccurs="0"/>
 *         &lt;element name="LINK" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Link" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>      &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="unit" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="ucd" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}ucdType" />
 *       &lt;attribute name="utype" type="{http://www.w3.org/2001/XMLSchema}string" />    &lt;/restriction>
 *   &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Info", propOrder =  {
  "description", "values", "links"})
public class Info {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlElement(name = "DESCRIPTION")
  protected AnyTEXT                                                                                                             description;
  /**
   * TODO : Field Description
   */
  @XmlElement(name = "VALUES")
  protected Values values;
  /**
   * TODO : Field Description
   */
  @XmlElement(name = "LINK")
  protected List<Link> links;
  /**
   * TODO : Field Description
   */
  @XmlAttribute(required = true)
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "token")
  protected String name;
  /**
   * TODO : Field Description
   */
  @XmlAttribute(required = true)
  protected String value;
  /**
   * TODO : Field Description
   */
  @XmlAttribute(name = "ID")
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlID
  @XmlSchemaType(name = "ID")
  protected String id;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "token")
  protected String unit;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  @XmlIDREF
  @XmlSchemaType(name = "IDREF")
  protected Object ref;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  protected String ucd;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  protected String utype;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Gets the value of the description property.
   *
   * @return possible object is {@link AnyTEXT }
   */
  public AnyTEXT getDESCRIPTION() {
    return description;
  }

  /**
   * Sets the value of the description property.
   *
   * @param value allowed object is {@link AnyTEXT }
   */
  public void setDESCRIPTION(final AnyTEXT value) {
    this.description = value;
  }

  /**
   * Gets the value of the values property.
   *
   * @return possible object is {@link Values }
   */
  public Values getVALUES() {
    return values;
  }

  /**
   * Sets the value of the values property.
   *
   * @param value allowed object is {@link Values }
   */
  public void setVALUES(final Values value) {
    this.values = value;
  }

  /**
   * Gets the value of the links property.<p>This accessor method returns a reference to the live list, not a
   * snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This
   * is why there is not a <CODE>set</CODE> method for the links property.</p>
   *  <p>For example, to add a new item, do as follows:<pre>   getLINKS().add(newItem);</pre></p>
   *  <p>Objects of the following type(s) are allowed in the list {@link Link }</p>
   *
   * @return value TODO : Value Description
   */
  public List<Link> getLINKS() {
    if (links == null) {
      links = new ArrayList<Link>();
    }

    return this.links;
  }

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
   * Gets the value of the value property.
   *
   * @return possible object is {@link String }
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value of the value property.
   *
   * @param value allowed object is {@link String }
   */
  public void setValue(final String value) {
    this.value = value;
  }

  /**
   * Gets the value of the id property.
   *
   * @return possible object is {@link String }
   */
  public String getID() {
    return id;
  }

  /**
   * Sets the value of the id property.
   *
   * @param value allowed object is {@link String }
   */
  public void setID(final String value) {
    this.id = value;
  }

  /**
   * Gets the value of the unit property.
   *
   * @return possible object is {@link String }
   */
  public String getUnit() {
    return unit;
  }

  /**
   * Sets the value of the unit property.
   *
   * @param value allowed object is {@link String }
   */
  public void setUnit(final String value) {
    this.unit = value;
  }

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

  /**
   * Gets the value of the ucd property.
   *
   * @return possible object is {@link String }
   */
  public String getUcd() {
    return ucd;
  }

  /**
   * Sets the value of the ucd property.
   *
   * @param value allowed object is {@link String }
   */
  public void setUcd(final String value) {
    this.ucd = value;
  }

  /**
   * Gets the value of the utype property.
   *
   * @return possible object is {@link String }
   */
  public String getUtype() {
    return utype;
  }

  /**
   * Sets the value of the utype property.
   *
   * @param value allowed object is {@link String }
   */
  public void setUtype(final String value) {
    this.utype = value;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
