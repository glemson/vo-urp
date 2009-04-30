package org.ivoa.votable;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
/**
 * <p>Java class for yesno.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="yesno">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="yes"/>
 *     &lt;enumeration value="no"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "yesno")
@XmlEnum
public enum Yesno {YES("yes"), NO("no");
  /**
   * TODO : Field Description
   */
  private final String value;

  /**
   * Creates a new Yesno object
   *
   * @param v 
   */
  Yesno(final String v) {
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
  public static Yesno fromValue(final String v) {
    for (final Yesno c : Yesno.values()) {
      if (c.value.equals(v)) {
        return c;
      }
    }

    throw new IllegalArgumentException(v);
  }
}//~ End of file --------------------------------------------------------------------------------------------------------
