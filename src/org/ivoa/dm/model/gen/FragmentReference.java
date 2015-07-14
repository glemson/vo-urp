
package org.ivoa.dm.model.gen;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *         Indicates an indirect reference to an object.
 *         Uses less direct means than the ivoId or publisherDID, which are unique, to identify a referenced object.
 *         Example, can use a reference to a container, an indication of the colleciton on the container
 *         plus a restriction on a property of the contained object which is constrained to be uniqueInCollection.
 *         Note, the reference to the container could be built up the same way.
 *         NB This concept has similarities to the UFI concept [TBD add ref to that].
 *         TBD "Is" a FragmentReference "a" Reference? I.e. should FragmentReference be an xsd:extension Reference?
 *           PRO: Removes one level in hierarchy, as a reference need not have a nested <fragment>
 *           PRO: Conceptually, a fragment reference indedd is-a" reference.
 *           CON: This concept is introduce to make a user's life easier, forcing them to use xmi:type reduces that usability.
 *         TBD: Consider a string version of this nesting, iso this element hierarchy.
 *       
 * 
 * <p>Java class for FragmentReference complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FragmentReference">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="container" type="{http://www.ivoa.net/xml/dm/base/v0.1}Reference" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="collection" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="property" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FragmentReference", propOrder = {
    "container"
})
public class FragmentReference {

    protected Reference container;
    @XmlAttribute(name = "collection")
    protected String collection;
    @XmlAttribute(name = "property", required = true)
    protected String property;
    @XmlAttribute(name = "value", required = true)
    protected String value;

    /**
     * Gets the value of the container property.
     * 
     * @return
     *     possible object is
     *     {@link Reference }
     *     
     */
    public Reference getContainer() {
        return container;
    }

    /**
     * Sets the value of the container property.
     * 
     * @param value
     *     allowed object is
     *     {@link Reference }
     *     
     */
    public void setContainer(Reference value) {
        this.container = value;
    }

    /**
     * Gets the value of the collection property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCollection() {
        return collection;
    }

    /**
     * Sets the value of the collection property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCollection(String value) {
        this.collection = value;
    }

    /**
     * Gets the value of the property property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProperty() {
        return property;
    }

    /**
     * Sets the value of the property property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProperty(String value) {
        this.property = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

}
