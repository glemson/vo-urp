
package org.ivoa.metamodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SKOSConcept complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SKOSConcept">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="vocabularyURI" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SKOSConcept", propOrder = {
    "vocabularyURI"
})
public class SKOSConcept {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String vocabularyURI;

    /**
     * Gets the value of the vocabularyURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVocabularyURI() {
        return vocabularyURI;
    }

    /**
     * Sets the value of the vocabularyURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVocabularyURI(String value) {
        this.vocabularyURI = value;
    }

}
