package org.ivoa.votable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Max complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="Max">  &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="inclusive" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}yesno" default="yes" />
 *     &lt;/restriction>  &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Max")
public class Max {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlAttribute(required = true)
  protected String                               value;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  protected Yesno inclusive;

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
   * Gets the value of the inclusive property.
   *
   * @return possible object is {@link Yesno }
   */
  public Yesno getInclusive() {
    if (inclusive == null) {
      return Yesno.YES;
    } else {
      return inclusive;
    }
  }

  /**
   * Sets the value of the inclusive property.
   *
   * @param value allowed object is {@link Yesno }
   */
  public void setInclusive(final Yesno value) {
    this.inclusive = value;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
