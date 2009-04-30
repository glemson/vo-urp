package org.ivoa.metamodel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataType complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="DataType">  &lt;complexContent>
 *     &lt;extension base="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}ValueType">      &lt;sequence>
 *         &lt;element name="attribute" type="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}Attribute" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>    &lt;/extension>  &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataType", propOrder =  {
  "attribute"})
public class DataType extends ValueType {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  protected List<Attribute> attribute;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Gets the value of the attribute property.<p>This accessor method returns a reference to the live list, not a
   * snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This
   * is why there is not a <CODE>set</CODE> method for the attribute property.</p>
   *  <p>For example, to add a new item, do as follows:<pre>   getAttribute().add(newItem);</pre></p>
   *  <p>Objects of the following type(s) are allowed in the list {@link Attribute }</p>
   *
   * @return value TODO : Value Description
   */
  public List<Attribute> getAttribute() {
    if (attribute == null) {
      attribute = new ArrayList<Attribute>();
    }

    return this.attribute;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
