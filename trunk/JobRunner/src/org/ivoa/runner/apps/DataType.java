
package org.ivoa.runner.apps;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DataType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="boolean"/>
 *     &lt;enumeration value="short"/>
 *     &lt;enumeration value="integer"/>
 *     &lt;enumeration value="long"/>
 *     &lt;enumeration value="float"/>
 *     &lt;enumeration value="double"/>
 *     &lt;enumeration value="datetime"/>
 *     &lt;enumeration value="string"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DataType")
@XmlEnum
public enum DataType {

    @XmlEnumValue("boolean")
    BOOLEAN("boolean"),
    @XmlEnumValue("short")
    SHORT("short"),
    @XmlEnumValue("integer")
    INTEGER("integer"),
    @XmlEnumValue("long")
    LONG("long"),
    @XmlEnumValue("float")
    FLOAT("float"),
    @XmlEnumValue("double")
    DOUBLE("double"),
    @XmlEnumValue("datetime")
    DATETIME("datetime"),
    @XmlEnumValue("string")
    STRING("string");
    private final String value;

    DataType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataType fromValue(String v) {
        for (DataType c: DataType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
