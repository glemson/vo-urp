package org.ivoa.votable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Param complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="Param">  &lt;complexContent>
 *     &lt;extension base="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Field">
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>  &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Param")
public class Param extends Field {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlAttribute(required = true)
  protected String value;

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
}
//~ End of file --------------------------------------------------------------------------------------------------------
