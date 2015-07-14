
package org.ivoa.dm.model.gen;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * 
 *       Represent the root of a containment hierarchy. 
 *       
 * 
 * <p>Java class for MetadataRootEntityObject complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MetadataRootEntityObject">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.ivoa.net/xml/dm/base/v0.1}MetadataObject">
 *       &lt;sequence>
 *         &lt;element name="creationUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modificationUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="creationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="modificationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataRootEntityObject", propOrder = {
    "creationUser",
    "modificationUser",
    "creationDate",
    "modificationDate"
})
public class MetadataRootEntityObject
    extends MetadataObject
{

    protected String creationUser;
    protected String modificationUser;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar modificationDate;

    /**
     * Gets the value of the creationUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreationUser() {
        return creationUser;
    }

    /**
     * Sets the value of the creationUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreationUser(String value) {
        this.creationUser = value;
    }

    /**
     * Gets the value of the modificationUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModificationUser() {
        return modificationUser;
    }

    /**
     * Sets the value of the modificationUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModificationUser(String value) {
        this.modificationUser = value;
    }

    /**
     * Gets the value of the creationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreationDate(XMLGregorianCalendar value) {
        this.creationDate = value;
    }

    /**
     * Gets the value of the modificationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getModificationDate() {
        return modificationDate;
    }

    /**
     * Sets the value of the modificationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setModificationDate(XMLGregorianCalendar value) {
        this.modificationDate = value;
    }

}
