package org.ivoa.votable;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;
/**
 * <p>Java class for encodingType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="encodingType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="gzip"/>
 *     &lt;enumeration value="base64"/>
 *     &lt;enumeration value="dynamic"/>
 *     &lt;enumeration value="none"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "encodingType")
@XmlEnum
public enum EncodingType {GZIP("gzip"), BASE_64("base64"), DYNAMIC("dynamic"), NONE("none");
  /**
   * TODO : Field Description
   */
  private final String value;

  /**
   * Creates a new EncodingType object
   *
   * @param v 
   */
  EncodingType(final String v) {
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
  public static EncodingType fromValue(final String v) {
    for (final EncodingType c : EncodingType.values()) {
      if (c.value.equals(v)) {
        return c;
      }
    }

    throw new IllegalArgumentException(v);
  }
}//~ End of file --------------------------------------------------------------------------------------------------------
