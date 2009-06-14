package org.ivoa.dm.model;



import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.ivoa.bean.LogSupport;


/**
 * This class defines a reference (close to Identity class) as it contains both xmlId and ivoId identifiers : <br>
 * - string xmlId : string for XML IDREF (xsd:idref type) for xml instances <br>
 * - ivoId : URI format for external references
 *
 * @author Gerard Lemson, Laurent Bourges
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Reference", namespace = "http://www.ivoa.net/xml/dm/base/v0.1")
public class Reference extends LogSupport implements Serializable {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * serial UID for Serializable interface : every concrete class must have its value corresponding to last
   * modification date of the UML model
   */
  private static final long serialVersionUID = 1L;

  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * An Identity value representing a local xsd:ID for this object, can be used when registering a DM/Resource
   * that has internal references.
   */
  @XmlIDREF
  @XmlSchemaType(name = "IDREF")
  @XmlAttribute(name = "xmlId", required = false)
  private Identity xmlIdentity;
  /** The URI uniquely specifying the object as it is published in a DM service */
  @XmlSchemaType(name = "anyURI")
  @XmlAttribute(name = "ivoId", required = false)
  private String ivoId;
  /**
   * The identifier assigned to the referenced object by the publisher.  Must also be a URI (see SSA andother
   * similar usages) and unique.
   */
  @XmlSchemaType(name = "anyURI")
  @XmlAttribute(name = "publisherDID", required = false)
  private String publisherDID;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * The identifier assigned to the referenced object by the publisher.
   * Must also be a URI (see SSA andother similar usages) and unique.
   */

  //  @XmlElement(name = "fragment", required = false, type=FragmentReference.class)
  //  private FragmentReference fragment;
/**
   * Public No-arg Constructor for JAXB Compliance
   */
  public Reference() {
    // nothing to do
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Returns the external identifier (URI)
   *
   * @return external identifier (URI)
   */
  public String getIvoId() {
    return ivoId;
  }

  /**
   * Sets the external identifier (URI)
   *
   * @param ivoId external identifier (URI)
   */
  protected void setIvoId(final String ivoId) {
    this.ivoId = ivoId;
  }

  /**
   * Returns an Identity value representing a local xsd:ID for this object
   *
   * @return Identity value representing a local xsd:ID for this object
   */

  /*
     public Identity getXmlIdentity() {
       return xmlIdentity;
     }
   */
  /**
   * Sets an Identity value representing a local xsd:ID for this object
   *
   * @return value TODO : Value Description
   */

  /*
     protected void setXmlIdentity(final Identity xmlIdentity) {
       this.xmlIdentity = xmlIdentity;
     }
   */
  /**
   * Returns a string representation : creates a temporary StringBuilder(STRING_BUFFER_CAPACITY) and calls
   * #toString(java.lang.StringBuilder) method
   *
   * @return string representation
   *
   * @see #toString(java.lang.StringBuilder) method
   */
  @Override
  public final String toString() {
    // always gives an initial size to buffer : 
    return toString(new StringBuilder(128)).toString();
  }

  /**
   * Puts the string representation in the given buffer : "[$id : $xmlId : $ivoId] " pattern
   *
   * @param sb given string buffer to fill
   *
   * @return the given string buffer filled with the string representation
   */
  protected final StringBuilder toString(final StringBuilder sb) {
    sb.append("[");

    if (getXmlId() != null) {
      sb.append(" - xmlId: ").append(getXmlId());
    }

    if (getIvoId() != null) {
      sb.append(" - ivoId: ").append(getIvoId());
    }

    return sb.append("] ");
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getXmlId() {
    return ((xmlIdentity == null) ? null : xmlIdentity.getXmlId());
  }

  /**
   * TODO : Method Description
   *
   * @param xmlId
   */
  protected void setXmlId(final String xmlId) {
    if (xmlId != null) {
      // if xmlIdentity != null then xmlIdentity.xmlId must be not null (used by XMLIDREF) :
      if (xmlIdentity == null) {
        xmlIdentity = new Identity();
      }

      xmlIdentity.setXmlId(xmlId);
    }
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getPublisherDID() {
    return publisherDID;
  }

  /**
   * TODO : Method Description
   *
   * @param publisherDID
   */
  public void setPublisherDID(final String publisherDID) {
    this.publisherDID = publisherDID;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
