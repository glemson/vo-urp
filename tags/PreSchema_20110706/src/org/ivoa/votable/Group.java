package org.ivoa.votable;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for Group complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="Group">  &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">      &lt;sequence>
 *         &lt;element name="DESCRIPTION" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}anyTEXT" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="FIELDref" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}FieldRef"/>
 *           &lt;element name="PARAMref" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}ParamRef"/>
 *           &lt;element name="PARAM" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Param"/>
 *           &lt;element name="GROUP" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Group"/>        &lt;/choice>
 *       &lt;/sequence>      &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="ucd" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}ucdType" />
 *       &lt;attribute name="utype" type="{http://www.w3.org/2001/XMLSchema}string" />    &lt;/restriction>
 *   &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Group", propOrder =  {
  "description", "fielDrevesAndPARAMrevesAndPARAMS"})
public class Group {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlElement(name = "DESCRIPTION")
  protected AnyTEXT                                                                                                                                                                                                                                              description;
  /**
   * TODO : Field Description
   */
  @XmlElements({@XmlElement(name = "GROUP", type = Group.class)
    , @XmlElement(name = "FIELDref", type = FieldRef.class)
    , @XmlElement(name = "PARAMref", type = ParamRef.class)
    , @XmlElement(name = "PARAM", type = Param.class)
  })
  protected List<Object> fielDrevesAndPARAMrevesAndPARAMS;
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
  protected String name;
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
   * Gets the value of the fielDrevesAndPARAMrevesAndPARAMS property.<p>This accessor method returns a reference
   * to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside
   * the JAXB object. This is why there is not a <CODE>set</CODE> method for the fielDrevesAndPARAMrevesAndPARAMS
   * property.</p>
   *  <p>For example, to add a new item, do as follows:<pre>   getFIELDrevesAndPARAMrevesAndPARAMS().add(newItem);
   * </pre></p>
   *  <p>Objects of the following type(s) are allowed in the list {@link Group }{@link FieldRef }{@link ParamRef
   * }{@link Param }</p>
   *
   * @return value TODO : Value Description
   */
  public List<Object> getFIELDrevesAndPARAMrevesAndPARAMS() {
    if (fielDrevesAndPARAMrevesAndPARAMS == null) {
      fielDrevesAndPARAMrevesAndPARAMS = new ArrayList<Object>();
    }

    return this.fielDrevesAndPARAMrevesAndPARAMS;
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
