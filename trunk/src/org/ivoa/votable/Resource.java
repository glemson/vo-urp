package org.ivoa.votable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;


/**
 * Added in Version 1.2: INFO for diagnostics in several places<p>Java class for Resource complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="Resource">  &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">      &lt;sequence>
 *         &lt;element name="DESCRIPTION" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}anyTEXT" minOccurs="0"/>
 *         &lt;element name="INFO" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Info" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="COOSYS" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}CoordinateSystem"/>
 *           &lt;element name="GROUP" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Group"/>
 *           &lt;element name="PARAM" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Param"/>        &lt;/choice>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="LINK" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Link" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;choice>            &lt;element name="TABLE" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Table"/>
 *             &lt;element name="RESOURCE" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Resource"/>
 *           &lt;/choice>
 *           &lt;element name="INFO" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Info" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>        &lt;any/>      &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="utype" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" default="results">        &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="results"/>            &lt;enumeration value="meta"/>          &lt;/restriction>
 *         &lt;/simpleType>      &lt;/attribute>    &lt;/restriction>  &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Resource", propOrder =  {
  "description", "infosAndCOOSYSAndGROUPS", "anies"})
public class Resource {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlElement(name = "DESCRIPTION")
  protected AnyTEXT                                                                                                                                                                                                                                                                                                                                                                                                         description;
  /**
   * TODO : Field Description
   */
  @XmlElements({@XmlElement(name = "RESOURCE", type = Resource.class)
    , @XmlElement(name = "LINK", type = Link.class)
    , @XmlElement(name = "COOSYS", type = CoordinateSystem.class)
    , @XmlElement(name = "GROUP", type = Group.class)
    , @XmlElement(name = "TABLE", type = Table.class)
    , @XmlElement(name = "PARAM", type = Param.class)
    , @XmlElement(name = "INFO", type = Info.class)
  })
  protected List<Object> infosAndCOOSYSAndGROUPS;
  /**
   * TODO : Field Description
   */
  @XmlAnyElement
  protected List<Element> anies;
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
  @XmlAttribute(name = "ID")
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlID
  @XmlSchemaType(name = "ID")
  protected String id;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  protected String utype;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  protected String type;
  /**
   * TODO : Field Description
   */
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap<QName, String>();

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
   * Gets the value of the infosAndCOOSYSAndGROUPS property.<p>This accessor method returns a reference to the
   * live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the
   * JAXB object. This is why there is not a <CODE>set</CODE> method for the infosAndCOOSYSAndGROUPS property.</p>
   *  <p>For example, to add a new item, do as follows:<pre>   getINFOSAndCOOSYSAndGROUPS().add(newItem);</pre></p>
   *  <p>Objects of the following type(s) are allowed in the list {@link Resource }{@link Link }{@link
   * CoordinateSystem }{@link Group }{@link Table }{@link Param }{@link Info }</p>
   *
   * @return value TODO : Value Description
   */
  public List<Object> getINFOSAndCOOSYSAndGROUPS() {
    if (infosAndCOOSYSAndGROUPS == null) {
      infosAndCOOSYSAndGROUPS = new ArrayList<Object>();
    }

    return this.infosAndCOOSYSAndGROUPS;
  }

  /**
   * Gets the value of the anies property.<p>This accessor method returns a reference to the live list, not a
   * snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This
   * is why there is not a <CODE>set</CODE> method for the anies property.</p>
   *  <p>For example, to add a new item, do as follows:<pre>   getAnies().add(newItem);</pre></p>
   *  <p>Objects of the following type(s) are allowed in the list {@link Element }</p>
   *
   * @return value TODO : Value Description
   */
  public List<Element> getAnies() {
    if (anies == null) {
      anies = new ArrayList<Element>();
    }

    return this.anies;
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

  /**
   * Gets the value of the type property.
   *
   * @return possible object is {@link String }
   */
  public String getType() {
    if (type == null) {
      return "results";
    }
    return type;
  }

  /**
   * Sets the value of the type property.
   *
   * @param value allowed object is {@link String }
   */
  public void setType(final String value) {
    this.type = value;
  }

  /**
   * Gets a map that contains attributes that aren't bound to any typed property on this class.<p>the map is
   * keyed by the name of the attribute and  the value is the string value of the attribute.  the map returned by this
   * method is live, and you can add new attribute by updating the map directly. Because of this design, there's no
   * setter.</p>
   *
   * @return always non-null
   */
  public Map<QName, String> getOtherAttributes() {
    return otherAttributes;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
