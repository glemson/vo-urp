package org.ivoa.metamodel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Enumeration complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="Enumeration">  &lt;complexContent>
 *     &lt;extension base="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}ValueType">      &lt;sequence>
 *         &lt;element name="literal" maxOccurs="unbounded">          &lt;complexType>            &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">                &lt;sequence>
 *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>              &lt;/restriction>            &lt;/complexContent>
 *           &lt;/complexType>        &lt;/element>      &lt;/sequence>    &lt;/extension>  &lt;/complexContent>
 * &lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Enumeration", propOrder =  {
  "literal"})
public class Enumeration extends ValueType {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlElement(required = true)
  protected List<Enumeration.Literal> literal;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Gets the value of the literal property.<p>This accessor method returns a reference to the live list, not a
   * snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This
   * is why there is not a <CODE>set</CODE> method for the literal property.</p>
   *  <p>For example, to add a new item, do as follows:<pre>   getLiteral().add(newItem);</pre></p>
   *  <p>Objects of the following type(s) are allowed in the list {@link Enumeration.Literal }</p>
   *
   * @return value TODO : Value Description
   */
  public List<Enumeration.Literal> getLiteral() {
    if (literal == null) {
      literal = new ArrayList<Enumeration.Literal>();
    }

    return this.literal;
  }

  //~ Inner Classes ----------------------------------------------------------------------------------------------------

  /**
   * <p>Java class for anonymous complex type.</p>
   *  <p>The following schema fragment specifies the expected content contained within this class.<pre>&lt;complexType>
   *   &lt;complexContent>    &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">      &lt;sequence>
   *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
   *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
   *       &lt;/sequence>    &lt;/restriction>  &lt;/complexContent>&lt;/complexType></pre></p>
   */
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder =  {
    "value", "description"}
  )
  public static class Literal {
    //~ Members --------------------------------------------------------------------------------------------------------

    @XmlElement(required = true)
    protected String                             value;
    protected String                             description;

    //~ Methods --------------------------------------------------------------------------------------------------------

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
     * Gets the value of the description property.
     *
     * @return possible object is {@link String }
     */
    public String getDescription() {
      return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is {@link String }
     */
    public void setDescription(final String value) {
      this.description = value;
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
