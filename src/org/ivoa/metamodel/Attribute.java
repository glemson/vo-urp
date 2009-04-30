package org.ivoa.metamodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Attribute complex type.</p>
 *  <p>The following schema fragment specifies the expected content contained within this class.<pre>
 * &lt;complexType name="Attribute">  &lt;complexContent>
 *     &lt;extension base="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}Element">      &lt;sequence>
 *         &lt;element name="datatype" type="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}TypeRef"/>
 *         &lt;element name="multiplicity" type="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}Multiplicity"/>
 *         &lt;element name="constraints" type="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}Constraints" minOccurs="0"/>
 *         &lt;element name="ontologyterm" type="{http://ivoa.org/theory/datamodel/generationmetadata/v0.1}OntologyTerm" minOccurs="0"/>
 *       &lt;/sequence>    &lt;/extension>  &lt;/complexContent>&lt;/complexType></pre></p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Attribute", propOrder =  {
  "datatype", "multiplicity", "constraints", "ontologyterm"})
public class Attribute extends Element {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @XmlElement(required = true)
  protected TypeRef                             datatype;
  /**
   * TODO : Field Description
   */
  @XmlElement(required = true)
  protected String multiplicity;
  /**
   * TODO : Field Description
   */
  protected Constraints constraints;
  /**
   * TODO : Field Description
   */
  protected OntologyTerm ontologyterm;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Gets the value of the datatype property.
   *
   * @return possible object is {@link TypeRef }
   */
  public TypeRef getDatatype() {
    return datatype;
  }

  /**
   * Sets the value of the datatype property.
   *
   * @param value allowed object is {@link TypeRef }
   */
  public void setDatatype(final TypeRef value) {
    this.datatype = value;
  }

  /**
   * Gets the value of the multiplicity property.
   *
   * @return possible object is {@link String }
   */
  public String getMultiplicity() {
    return multiplicity;
  }

  /**
   * Sets the value of the multiplicity property.
   *
   * @param value allowed object is {@link String }
   */
  public void setMultiplicity(final String value) {
    this.multiplicity = value;
  }

  /**
   * Gets the value of the constraints property.
   *
   * @return possible object is {@link Constraints }
   */
  public Constraints getConstraints() {
    return constraints;
  }

  /**
   * Sets the value of the constraints property.
   *
   * @param value allowed object is {@link Constraints }
   */
  public void setConstraints(final Constraints value) {
    this.constraints = value;
  }

  /**
   * Gets the value of the ontologyterm property.
   *
   * @return possible object is {@link OntologyTerm }
   */
  public OntologyTerm getOntologyterm() {
    return ontologyterm;
  }

  /**
   * Sets the value of the ontologyterm property.
   *
   * @param value allowed object is {@link OntologyTerm }
   */
  public void setOntologyterm(final OntologyTerm value) {
    this.ontologyterm = value;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
