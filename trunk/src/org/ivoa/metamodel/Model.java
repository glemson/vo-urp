package org.ivoa.metamodel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>&lt;complexType>
 *   &lt;complexContent>    &lt;extension base="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}Element">
 *       &lt;sequence>        &lt;element name="lastModifiedDate" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="author" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="package" type="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}Package" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>    &lt;/extension>  &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =  {
  "lastModifiedDate", "author", "title", "_package"})
@XmlRootElement(name = "model")
public class Model extends Element {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  protected long                                       lastModifiedDate;
  /**
   * TODO : Field Description
   */
  protected List<String> author;
  /**
   * TODO : Field Description
   */
  protected String title;
  /**
   * TODO : Field Description
   */
  @XmlElement(name = "package")
  protected List<Package> _package;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Gets the value of the lastModifiedDate property.
   *
   * @return value TODO : Value Description
   */
  public long getLastModifiedDate() {
    return lastModifiedDate;
  }

  /**
   * Sets the value of the lastModifiedDate property.
   *
   * @param value
   */
  public void setLastModifiedDate(final long value) {
    this.lastModifiedDate = value;
  }

  /**
   * Gets the value of the author property.<p>This accessor method returns a reference to the live list, not a
   * snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This
   * is why there is not a <CODE>set</CODE> method for the author property.</p>
   *  <p>For example, to add a new item, do as follows:<pre>   getAuthor().add(newItem);</pre></p>
   *  <p>Objects of the following type(s) are allowed in the list {@link String }</p>
   *
   * @return value TODO : Value Description
   */
  public List<String> getAuthor() {
    if (author == null) {
      author = new ArrayList<String>();
    }

    return this.author;
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
   * Gets the value of the package property.<p>This accessor method returns a reference to the live list, not a
   * snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This
   * is why there is not a <CODE>set</CODE> method for the package property.</p>
   *  <p>For example, to add a new item, do as follows:<pre>   getPackage().add(newItem);</pre></p>
   *  <p>Objects of the following type(s) are allowed in the list {@link Package }</p>
   *
   * @return value TODO : Value Description
   */
  public List<Package> getPackage() {
    if (_package == null) {
      _package = new ArrayList<Package>();
    }

    return this._package;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
