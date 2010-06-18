
package org.vourp.runner.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for EnumeratedParameter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EnumeratedParameter">
 *   &lt;complexContent>
 *     &lt;extension base="{http://gavo.org/JobRunner/LegacyApps/v0.1}ParameterDeclaration">
 *       &lt;sequence>
 *         &lt;element name="validvalues" type="{http://gavo.org/JobRunner/LegacyApps/v0.1}Validvalues" maxOccurs="unbounded"/>
 *         &lt;element name="dependency" type="{http://gavo.org/JobRunner/LegacyApps/v0.1}Dependency" minOccurs="0"/>
 *         &lt;element name="numSlaves" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnumeratedParameter", propOrder = {
    "validvalues",
    "dependency",
    "numSlaves"
})
public class EnumeratedParameter
    extends ParameterDeclaration
{

    @XmlElement(required = true)
    protected List<Validvalues> validvalues;
    protected Dependency dependency;
    @XmlElement(defaultValue = "0")
    protected Integer numSlaves;
    @XmlAttribute(name = "ID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the validvalues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the validvalues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValidvalues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Validvalues }
     * 
     * 
     */
    public List<Validvalues> getValidvalues() {
        if (validvalues == null) {
            validvalues = new ArrayList<Validvalues>();
        }
        return this.validvalues;
    }

    /**
     * Gets the value of the dependency property.
     * 
     * @return
     *     possible object is
     *     {@link Dependency }
     *     
     */
    public Dependency getDependency() {
        return dependency;
    }

    /**
     * Sets the value of the dependency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Dependency }
     *     
     */
    public void setDependency(Dependency value) {
        this.dependency = value;
    }

    /**
     * Gets the value of the numSlaves property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumSlaves() {
        return numSlaves;
    }

    /**
     * Sets the value of the numSlaves property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumSlaves(Integer value) {
        this.numSlaves = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setID(String value) {
        this.id = value;
    }

}
