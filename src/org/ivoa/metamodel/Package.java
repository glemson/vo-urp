package org.ivoa.metamodel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Package complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="Package">  &lt;complexContent>
 *     &lt;extension base="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}Element">      &lt;sequence>
 *         &lt;element name="depends" type="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}PackageReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="objectType" type="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}ObjectType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="dataType" type="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}DataType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="enumeration" type="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}Enumeration" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="primitiveType" type="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}PrimitiveType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="package" type="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}Package" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>    &lt;/extension>  &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Package", propOrder =  {
  "depends", "objectType", "dataType", "enumeration", "primitiveType", "_package"})
public class Package extends Element {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  protected List<PackageReference>                     depends;
  /**
   * TODO : Field Description
   */
  protected List<ObjectType> objectType;
  /**
   * TODO : Field Description
   */
  protected List<DataType> dataType;
  /**
   * TODO : Field Description
   */
  protected List<Enumeration> enumeration;
  /**
   * TODO : Field Description
   */
  protected List<PrimitiveType> primitiveType;
  /**
   * TODO : Field Description
   */
  @XmlElement(name = "package")
  protected List<Package> _package;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Gets the value of the depends property.<p>This accessor method returns a reference to the live list, not a
   * snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This
   * is why there is not a <CODE>set</CODE> method for the depends property.</p>
   *  <p>For example, to add a new item, do as follows:<pre>   getDepends().add(newItem);</pre></p>
   *  <p>Objects of the following type(s) are allowed in the list {@link PackageReference }</p>
   *
   * @return value TODO : Value Description
   */
  public List<PackageReference> getDepends() {
    if (depends == null) {
      depends = new ArrayList<PackageReference>();
    }

    return this.depends;
  }

  /**
   * Gets the value of the objectType property.<p>This accessor method returns a reference to the live list, not
   * a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This
   * is why there is not a <CODE>set</CODE> method for the objectType property.</p>
   *  <p>For example, to add a new item, do as follows:<pre>   getObjectType().add(newItem);</pre></p>
   *  <p>Objects of the following type(s) are allowed in the list {@link ObjectType }</p>
   *
   * @return value TODO : Value Description
   */
  public List<ObjectType> getObjectType() {
    if (objectType == null) {
      objectType = new ArrayList<ObjectType>();
    }

    return this.objectType;
  }

  /**
   * Gets the value of the dataType property.<p>This accessor method returns a reference to the live list, not a
   * snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This
   * is why there is not a <CODE>set</CODE> method for the dataType property.</p>
   *  <p>For example, to add a new item, do as follows:<pre>   getDataType().add(newItem);</pre></p>
   *  <p>Objects of the following type(s) are allowed in the list {@link DataType }</p>
   *
   * @return value TODO : Value Description
   */
  public List<DataType> getDataType() {
    if (dataType == null) {
      dataType = new ArrayList<DataType>();
    }

    return this.dataType;
  }

  /**
   * Gets the value of the enumeration property.<p>This accessor method returns a reference to the live list, not
   * a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This
   * is why there is not a <CODE>set</CODE> method for the enumeration property.</p>
   *  <p>For example, to add a new item, do as follows:<pre>   getEnumeration().add(newItem);</pre></p>
   *  <p>Objects of the following type(s) are allowed in the list {@link Enumeration }</p>
   *
   * @return value TODO : Value Description
   */
  public List<Enumeration> getEnumeration() {
    if (enumeration == null) {
      enumeration = new ArrayList<Enumeration>();
    }

    return this.enumeration;
  }

  /**
   * Gets the value of the primitiveType property.<p>This accessor method returns a reference to the live list,
   * not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
   * This is why there is not a <CODE>set</CODE> method for the primitiveType property.</p>
   *  <p>For example, to add a new item, do as follows:<pre>   getPrimitiveType().add(newItem);</pre></p>
   *  <p>Objects of the following type(s) are allowed in the list {@link PrimitiveType }</p>
   *
   * @return value TODO : Value Description
   */
  public List<PrimitiveType> getPrimitiveType() {
    if (primitiveType == null) {
      primitiveType = new ArrayList<PrimitiveType>();
    }

    return this.primitiveType;
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
