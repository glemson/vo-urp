package org.ivoa.votable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for Link complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="Link">  &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" />      &lt;attribute name="content-role">
 *         &lt;simpleType>          &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="query"/>            &lt;enumeration value="hints"/>
 *             &lt;enumeration value="doc"/>            &lt;enumeration value="location"/>          &lt;/restriction>
 *         &lt;/simpleType>      &lt;/attribute>
 *       &lt;attribute name="content-type" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="href" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="gref" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="action" type="{http://www.w3.org/2001/XMLSchema}anyURI" />    &lt;/restriction>
 *   &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Link", propOrder =  {
  "content"})
public class Link {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlValue
  protected String                                                                                                                    content;
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
  @XmlAttribute(name = "content-role")
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  protected String contentRole;
  /**
   * TODO : Field Description
   */
  @XmlAttribute(name = "content-type")
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "token")
  protected String contentType;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  protected String title;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  protected String value;
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
  @XmlSchemaType(name = "token")
  protected String gref;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  @XmlSchemaType(name = "anyURI")
  protected String action;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Gets the value of the content property.
   *
   * @return possible object is {@link String }
   */
  public String getContent() {
    return content;
  }

  /**
   * Sets the value of the content property.
   *
   * @param value allowed object is {@link String }
   */
  public void setContent(final String value) {
    this.content = value;
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
   * Gets the value of the contentRole property.
   *
   * @return possible object is {@link String }
   */
  public String getContentRole() {
    return contentRole;
  }

  /**
   * Sets the value of the contentRole property.
   *
   * @param value allowed object is {@link String }
   */
  public void setContentRole(final String value) {
    this.contentRole = value;
  }

  /**
   * Gets the value of the contentType property.
   *
   * @return possible object is {@link String }
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Sets the value of the contentType property.
   *
   * @param value allowed object is {@link String }
   */
  public void setContentType(final String value) {
    this.contentType = value;
  }

  /**
   * Gets the value of the title property.
   *
   * @return possible object is {@link String }
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the value of the title property.
   *
   * @param value allowed object is {@link String }
   */
  public void setTitle(final String value) {
    this.title = value;
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
   * Gets the value of the gref property.
   *
   * @return possible object is {@link String }
   */
  public String getGref() {
    return gref;
  }

  /**
   * Sets the value of the gref property.
   *
   * @param value allowed object is {@link String }
   */
  public void setGref(final String value) {
    this.gref = value;
  }

  /**
   * Gets the value of the action property.
   *
   * @return possible object is {@link String }
   */
  public String getAction() {
    return action;
  }

  /**
   * Sets the value of the action property.
   *
   * @param value allowed object is {@link String }
   */
  public void setAction(final String value) {
    this.action = value;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
