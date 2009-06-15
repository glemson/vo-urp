package org.ivoa.votable;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anyTEXT complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="anyTEXT">  &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">      &lt;sequence>        &lt;any/>
 *       &lt;/sequence>    &lt;/restriction>  &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "anyTEXT", propOrder =  {
  "content"})
public class AnyTEXT {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlMixed
  @XmlAnyElement
  protected List<Object> content;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Gets the value of the content property.<p>This accessor method returns a reference to the live list, not a
   * snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This
   * is why there is not a <CODE>set</CODE> method for the content property.</p>
   *  <p>For example, to add a new item, do as follows:<pre>   getContent().add(newItem);</pre></p>
   *  <p>Objects of the following type(s) are allowed in the list {@link Element }{@link String }</p>
   *
   * @return value TODO : Value Description
   */
  public List<Object> getContent() {
    if (content == null) {
      content = new ArrayList<Object>();
    }

    return this.content;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
