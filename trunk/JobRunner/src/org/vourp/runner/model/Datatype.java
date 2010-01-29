
package org.vourp.runner.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Datatype.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Datatype">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="short"/>
 *     &lt;enumeration value="integer"/>
 *     &lt;enumeration value="float"/>
 *     &lt;enumeration value="double"/>
 *     &lt;enumeration value="boolean"/>
 *     &lt;enumeration value="datetime"/>
 *     &lt;enumeration value="string"/>
 *     &lt;enumeration value="anyURI"/>
 *     &lt;enumeration value="file"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Datatype")
@XmlEnum
public enum Datatype {

    @XmlEnumValue("short")
    SHORT("short"),
    @XmlEnumValue("integer")
    INTEGER("integer"),
    @XmlEnumValue("float")
    FLOAT("float"),
    @XmlEnumValue("double")
    DOUBLE("double"),
    @XmlEnumValue("boolean")
    BOOLEAN("boolean"),
    @XmlEnumValue("datetime")
    DATETIME("datetime"),
    @XmlEnumValue("string")
    STRING("string"),
    @XmlEnumValue("anyURI")
    ANY_URI("anyURI"),

    /**
     * 
     *       Represents a file. This can be a local file, or a file that must be uploaded in case of a web application.
     *       
     * 
     */
    @XmlEnumValue("file")
    FILE("file");
    private final String value;

    Datatype(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Datatype fromValue(String v) {
        for (Datatype c: Datatype.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
