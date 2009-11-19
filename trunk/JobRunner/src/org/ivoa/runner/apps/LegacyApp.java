
package org.ivoa.runner.apps;

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
 * 
 *    Represents an existing (i.e. legacy) application. 
 *    Consists of an executable that accepts the name of, or assumes the existence of a named parameter file.
 *     
 * 
 * <p>Java class for LegacyApp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LegacyApp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="documentatyionURL" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="executable" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parameterFile" type="{http://vo-urp.googlecode.com/LegacyApps}ParameterFile" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="commandLineParameter" type="{http://vo-urp.googlecode.com/LegacyApps}Parameter" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ID" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LegacyApp", propOrder = {
    "name",
    "description",
    "documentatyionURL",
    "executable",
    "parameterFile",
    "commandLineParameter"
})
public class LegacyApp {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String description;
    @XmlSchemaType(name = "anyURI")
    protected String documentatyionURL;
    @XmlElement(required = true)
    protected String executable;
    protected List<ParameterFile> parameterFile;
    protected List<Parameter> commandLineParameter;
    @XmlAttribute(name = "ID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the documentatyionURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentatyionURL() {
        return documentatyionURL;
    }

    /**
     * Sets the value of the documentatyionURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentatyionURL(String value) {
        this.documentatyionURL = value;
    }

    /**
     * Gets the value of the executable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExecutable() {
        return executable;
    }

    /**
     * Sets the value of the executable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExecutable(String value) {
        this.executable = value;
    }

    /**
     * Gets the value of the parameterFile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameterFile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameterFile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParameterFile }
     * 
     * 
     */
    public List<ParameterFile> getParameterFile() {
        if (parameterFile == null) {
            parameterFile = new ArrayList<ParameterFile>();
        }
        return this.parameterFile;
    }

    /**
     * Gets the value of the commandLineParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the commandLineParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCommandLineParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Parameter }
     * 
     * 
     */
    public List<Parameter> getCommandLineParameter() {
        if (commandLineParameter == null) {
            commandLineParameter = new ArrayList<Parameter>();
        }
        return this.commandLineParameter;
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
