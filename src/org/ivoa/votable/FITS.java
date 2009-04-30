package org.ivoa.votable;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FITS complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="FITS">  &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">      &lt;sequence>
 *         &lt;element name="STREAM" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Stream"/>      &lt;/sequence>
 *       &lt;attribute name="extnum" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />    &lt;/restriction>
 *   &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FITS", propOrder =  {
  "stream"})
public class FITS {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlElement(name = "STREAM", required = true)
  protected Stream                                                          stream;
  /**
   * TODO : Field Description
   */
  @XmlAttribute
  @XmlSchemaType(name = "positiveInteger")
  protected BigInteger extnum;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Gets the value of the stream property.
   *
   * @return possible object is {@link Stream }
   */
  public Stream getSTREAM() {
    return stream;
  }

  /**
   * Sets the value of the stream property.
   *
   * @param value allowed object is {@link Stream }
   */
  public void setSTREAM(final Stream value) {
    this.stream = value;
  }

  /**
   * Gets the value of the extnum property.
   *
   * @return possible object is {@link BigInteger }
   */
  public BigInteger getExtnum() {
    return extnum;
  }

  /**
   * Sets the value of the extnum property.
   *
   * @param value allowed object is {@link BigInteger }
   */
  public void setExtnum(final BigInteger value) {
    this.extnum = value;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
