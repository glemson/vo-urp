package org.ivoa.votable;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
/**
 * <p>Java class for dataType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="dataType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="boolean"/>
 *     &lt;enumeration value="bit"/>
 *     &lt;enumeration value="unsignedByte"/>
 *     &lt;enumeration value="short"/>
 *     &lt;enumeration value="int"/>
 *     &lt;enumeration value="long"/>
 *     &lt;enumeration value="char"/>
 *     &lt;enumeration value="unicodeChar"/>
 *     &lt;enumeration value="float"/>
 *     &lt;enumeration value="double"/>
 *     &lt;enumeration value="floatComplex"/>
 *     &lt;enumeration value="doubleComplex"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "dataType")
@XmlEnum
public enum DataType {BOOLEAN("boolean"), BIT("bit"), UNSIGNED_BYTE("unsignedByte"), SHORT("short"), INT("int"), 
  LONG("long"), CHAR("char"), UNICODE_CHAR("unicodeChar"), FLOAT("float"), DOUBLE("double"), 
  FLOAT_COMPLEX("floatComplex"), DOUBLE_COMPLEX("doubleComplex");
  /**
   * TODO : Field Description
   */
  private final String value;

  /**
   * Creates a new DataType object
   *
   * @param v 
   */
  DataType(final String v) {
    value = v;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String value() {
    return value;
  }

  /**
   * TODO : Method Description
   *
   * @param v 
   *
   * @return value TODO : Value Description
   */
  public static DataType fromValue(final String v) {
    for (final DataType c : DataType.values()) {
      if (c.value.equals(v)) {
        return c;
      }
    }

    throw new IllegalArgumentException(v);
  }
}//~ End of file --------------------------------------------------------------------------------------------------------
