package org.ivoa.votable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Stream complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="Stream">  &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="type" default="locator">        &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="locator"/>            &lt;enumeration value="other"/>          &lt;/restriction>
 *         &lt;/simpleType>      &lt;/attribute>
 *       &lt;attribute name="href" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="actuate" default="onRequest">        &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="onLoad"/>            &lt;enumeration value="onRequest"/>
 *             &lt;enumeration value="other"/>            &lt;enumeration value="none"/>          &lt;/restriction>
 *         &lt;/simpleType>      &lt;/attribute>
 *       &lt;attribute name="encoding" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}encodingType" default="none" />
 *       &lt;attribute name="expires" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="rights" type="{http://www.w3.org/2001/XMLSchema}token" />    &lt;/extension>
 *   &lt;/simpleContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Stream", propOrder =  {
  "value"})
public class Stream {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlValue
  protected String                                                                                             value;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  protected String type;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  @XmlSchemaType(name = "anyURI")
  protected String href;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  protected String actuate;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  protected EncodingType encoding;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar expires;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "token")
  protected String rights;

  //~ Methods ----------------------------------------------------------------------------------------------------------

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
   * Gets the value of the type property.
   *
   * @return possible object is {@link String }
   */
  public String getType() {
    if (type == null) {
      return "locator";
    } else {
      return type;
    }
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
   * Gets the value of the href property.
   *
   * @return possible object is {@link String }
   */
  public String getHref() {
    return href;
  }

  /**
   * Sets the value of the href property.
   *
   * @param value allowed object is {@link String }
   */
  public void setHref(final String value) {
    this.href = value;
  }

  /**
   * Gets the value of the actuate property.
   *
   * @return possible object is {@link String }
   */
  public String getActuate() {
    if (actuate == null) {
      return "onRequest";
    } else {
      return actuate;
    }
  }

  /**
   * Sets the value of the actuate property.
   *
   * @param value allowed object is {@link String }
   */
  public void setActuate(final String value) {
    this.actuate = value;
  }

  /**
   * Gets the value of the encoding property.
   *
   * @return possible object is {@link EncodingType }
   */
  public EncodingType getEncoding() {
    if (encoding == null) {
      return EncodingType.NONE;
    } else {
      return encoding;
    }
  }

  /**
   * Sets the value of the encoding property.
   *
   * @param value allowed object is {@link EncodingType }
   */
  public void setEncoding(final EncodingType value) {
    this.encoding = value;
  }

  /**
   * Gets the value of the expires property.
   *
   * @return possible object is {@link XMLGregorianCalendar }
   */
  public XMLGregorianCalendar getExpires() {
    return expires;
  }

  /**
   * Sets the value of the expires property.
   *
   * @param value allowed object is {@link XMLGregorianCalendar }
   */
  public void setExpires(final XMLGregorianCalendar value) {
    this.expires = value;
  }

  /**
   * Gets the value of the rights property.
   *
   * @return possible object is {@link String }
   */
  public String getRights() {
    return rights;
  }

  /**
   * Sets the value of the rights property.
   *
   * @param value allowed object is {@link String }
   */
  public void setRights(final String value) {
    this.rights = value;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
