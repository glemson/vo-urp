package org.ivoa.votable;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>&lt;complexType>
 *   &lt;complexContent>    &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">      &lt;sequence>
 *         &lt;element name="DESCRIPTION" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}anyTEXT" minOccurs="0"/>
 *         &lt;element name="DEFINITIONS" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Definitions" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="COOSYS" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}CoordinateSystem"/>
 *           &lt;element name="GROUP" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Group"/>
 *           &lt;element name="PARAM" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Param"/>
 *           &lt;element name="INFO" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Info" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element name="RESOURCE" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Resource" maxOccurs="unbounded"/>
 *         &lt;element name="INFO" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Info" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>      &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="version">        &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">            &lt;enumeration value="1.2"/>
 *           &lt;/restriction>        &lt;/simpleType>      &lt;/attribute>    &lt;/restriction>  &lt;/complexContent>
 * &lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =  {
  "description", "definitions", "coosysAndGROUPSAndPARAMS"})
@XmlRootElement(name = "VOTABLE")
public class VOTABLE {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlElement(name = "DESCRIPTION")
  protected AnyTEXT                                                                                                                                                                                                                                                                                                                                                                                          description;
  /**
   * TODO : Field Description
   */
  @XmlElement(name = "DEFINITIONS")
  protected Definitions definitions;
  /**
   * TODO : Field Description
   */
  @XmlElements({@XmlElement(name = "INFO", required = true, type = Info.class)
    , @XmlElement(name = "COOSYS", required = true, type = CoordinateSystem.class)
    , @XmlElement(name = "GROUP", required = true, type = Group.class)
    , @XmlElement(name = "RESOURCE", required = true, type = Resource.class)
    , @XmlElement(name = "PARAM", required = true, type = Param.class)
  })
  protected List<Object> coosysAndGROUPSAndPARAMS;
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
  protected String version;

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
   * Gets the value of the definitions property.
   *
   * @return possible object is {@link Definitions }
   */
  public Definitions getDEFINITIONS() {
    return definitions;
  }

  /**
   * Sets the value of the definitions property.
   *
   * @param value allowed object is {@link Definitions }
   */
  public void setDEFINITIONS(final Definitions value) {
    this.definitions = value;
  }

  /**
   * Gets the value of the coosysAndGROUPSAndPARAMS property.<p>This accessor method returns a reference to the
   * live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the
   * JAXB object. This is why there is not a <CODE>set</CODE> method for the coosysAndGROUPSAndPARAMS property.</p>
   *  <p>For example, to add a new item, do as follows:<pre>   getCOOSYSAndGROUPSAndPARAMS().add(newItem);</pre></p>
   *  <p>Objects of the following type(s) are allowed in the list {@link Info }{@link CoordinateSystem }{@link
   * Group }{@link Resource }{@link Param }</p>
   *
   * @return value TODO : Value Description
   */
  public List<Object> getCOOSYSAndGROUPSAndPARAMS() {
    if (coosysAndGROUPSAndPARAMS == null) {
      coosysAndGROUPSAndPARAMS = new ArrayList<Object>();
    }

    return this.coosysAndGROUPSAndPARAMS;
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
   * Gets the value of the version property.
   *
   * @return possible object is {@link String }
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the value of the version property.
   *
   * @param value allowed object is {@link String }
   */
  public void setVersion(final String value) {
    this.version = value;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
