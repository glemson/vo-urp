package org.ivoa.votable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for Td complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="Td">  &lt;simpleContent>    &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="encoding" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}encodingType" />
 *     &lt;/extension>  &lt;/simpleContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Td", propOrder =  {
  "value"})
public class Td {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlValue
  protected String                    value;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  protected EncodingType encoding;

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
   * Gets the value of the encoding property.
   *
   * @return possible object is {@link EncodingType }
   */
  public EncodingType getEncoding() {
    return encoding;
  }

  /**
   * Sets the value of the encoding property.
   *
   * @param value allowed object is {@link EncodingType }
   */
  public void setEncoding(final EncodingType value) {
    this.encoding = value;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
