package org.ivoa.dm.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Root Entity Object is a base type corresponding to a full XML document
 * 
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "MetadataRootEntityObject", namespace = "http://www.ivoa.net/xml/dm/base/v0.1")
public abstract class MetadataRootEntityObject extends MetadataObject {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /**
     * serial UID for Serializable interface
     */
    private static final long serialVersionUID = 1L;

    //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * Username of owner of this root entity class.<br/>
   */
  @Basic(optional = true)
  @Column(name = "creationUser", nullable = true)
  @XmlElement(name = "creationUser", required = false, type = String.class)
  private String creationUser;

  /**
   * Username of owner of this root entity class.<br/>
   */
  @Basic(optional = true)
  @Column(name = "modificationUser", nullable = true)
  @XmlElement(name = "modificationUser", required = false, type = String.class)
  private String modificationUser;

  /**
   * The timestamp when this record was added to the database.
   */
  @Basic(optional = true)
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creationDate", nullable = true)
  @XmlElement(name = "creationDate", required = false, type = Date.class)
  private Date creationDate;

  /**
   * The timestamp when this record was last updated in the database.
   */
  @Basic(optional = true)
    @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "modificationDate", nullable = true)
  @XmlElement(name = "modificationDate", required = true, type = Date.class)
  private Date modificationDate;

    //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Public constructor
   */
  public MetadataRootEntityObject() {
    super();
  }

    //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * @return owner
   */
  public String getCreationUser() {
    return creationUser;
  }

  /**
   * @param pOwner owner
   */
  public void setCreationUser(final String pCreationUser) {
    this.creationUser = pCreationUser;
  }

  /**
   * @return updateUser
   */
  public String getModificationUser() {
    return modificationUser;
  }

  /**
   * @param pUpdateUser updateUser
   */
  public void setModificationUser(final String pModificationUser) {
    this.modificationUser = pModificationUser;
  }

  /**
   * @return  timestamp at insert time
   */
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * @param pInsertTimestamp timestamp at insert time
   */
  public void setCreationDate(final Date pCreationDate) {
    this.creationDate = pCreationDate;
  }

  /**
   * @return timestamp at update time
   */
  public Date getModificationDate() {
    return modificationDate;
  }

  /**
   * @param pDBUpdateTimestamp timestamp at update time
   */
  public void setModificationDate(final Date pModificationDate) {
    this.modificationDate = pModificationDate;
  }

  /**
   * Puts the string representation in the given string buffer : &lt;br&gt; "Type =[class name @ hashcode] : {
   * field name = field value , ...}". If isDeep is true, it uses an IdentityHashMap to avoid duplicate toString() in
   * the recursion
   *
   * @param sb given string buffer to fill
   * @param isDeep true means to call toString(sb, true) recursively for all attributes / references / collections
   *        which are MetadataElement implementations
   *
   * @return the given string buffer filled with the string representation
   */
  @Override
  protected StringBuilder deepToString(final StringBuilder sb, final boolean isDeep,
      final Map<MetadataElement, Object> ids) {
      // TODO : implement
    return null;
  }

}
//~ End of file ------------------------------------------------------------------------------------------------------
