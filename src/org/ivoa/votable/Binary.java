package org.ivoa.votable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Binary complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="Binary">  &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">      &lt;sequence>
 *         &lt;element name="STREAM" type="{http://www.ivoa.net/xml/VOTable/v1.2/beta}Stream"/>      &lt;/sequence>
 *     &lt;/restriction>  &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Binary", propOrder =  {
  "stream"})
public class Binary {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlElement(name = "STREAM", required = true)
  protected Stream stream;

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
}
//~ End of file --------------------------------------------------------------------------------------------------------
