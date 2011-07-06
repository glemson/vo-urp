
package org.vourp.runner.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *     ...
 *     
 * 
 * <p>Java class for Choice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Choice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="if" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="choose" type="{http://www.w3.org/2001/XMLSchema}IDREF"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Choice", propOrder = {
    "_if",
    "choose"
})
public class Choice {

    @XmlElement(name = "if", required = true)
    protected String _if;
    @XmlElement(required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object choose;

    /**
     * Gets the value of the if property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIf() {
        return _if;
    }

    /**
     * Sets the value of the if property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIf(String value) {
        this._if = value;
    }

    /**
     * Gets the value of the choose property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getChoose() {
        return choose;
    }

    /**
     * Sets the value of the choose property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setChoose(Object value) {
        this.choose = value;
    }

}
