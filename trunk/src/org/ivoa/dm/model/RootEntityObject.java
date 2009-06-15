package org.ivoa.dm.model;

import java.sql.Timestamp;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;

/**
 * Root Entity Object is a base type corresponding to a full XML document
 * 
 * @author laurent bourges / gerard lemson
 */
public abstract class RootEntityObject extends MetadataObject {
  // ~ Constants
  // --------------------------------------------------------------------------------------------------------

  /**
   * serial UID for Serializable interface : every concrete class must have its value corresponding
   * to last modification date of the UML model
   */
  private static final long serialVersionUID = 1L;

  // ~ Members
  // ----------------------------------------------------------------------------------------------------------

  /**
   * Username of owner of this root entity class.<br/>
   */
  @Basic(optional = true)
  @Column(name = "ownerUser", nullable = true)
  private String owner;

  /**
   * Username of owner of this root entity class.<br/>
   */
  /**
     * 
     */
  @Basic(optional = true)
  @Column(name = "updateUser", nullable = true)
  private String updateUser;

  /**
   * The timestamp when this record was added to the database.
   */
  @Basic(optional = true)
  @Column(name = "createTimestamp", nullable = true)
  private Timestamp createTimestamp;

  /**
   * The timestamp when this record was last updated in the database.
   */
  @Basic(optional = true)
  @Column(name = "updateTimestamp", nullable = true)
  private Timestamp updateTimestamp;

  // ~ Constructors
  // -----------------------------------------------------------------------------------------------------

  /**
   * Public constructor
   */
  public RootEntityObject() {
    super();
  }

  // ~ Methods
  // ----------------------------------------------------------------------------------------------------------

  /**
   * @return owner
   */
  public String getOwner() {
    return owner;
  }

  /**
   * @param pOwner owner
   */
  public void setOwner(final String pOwner) {
    this.owner = pOwner;
  }

  /**
   * @return updateUser
   */
  public String getUpdateUser() {
    return updateUser;
  }

  /**
   * @param pUpdateUser updateUser
   */
  public void setUpdateUser(final String pUpdateUser) {
    this.updateUser = pUpdateUser;
  }

  /**
   * TODO : Method Description
   * 
   * @param sb
   * @param isDeep
   * @param ids
   * @return value TODO : Value Description
   */
  @Override
  protected StringBuilder deepToString(final StringBuilder sb, final boolean isDeep,
      final Map<MetadataElement, Object> ids) {
    return null;
  }

  /**
   * @return createTimestamp
   */
  public Timestamp getCreateTimestamp() {
    return createTimestamp;
  }

  /**
   * @param pCreateTimestamp createTimestamp
   */
  public void setCreateTimestamp(final Timestamp pCreateTimestamp) {
    this.createTimestamp = pCreateTimestamp;
  }

  /**
   * @return updateTimestamp
   */
  public Timestamp getUpdateTimestamp() {
    return updateTimestamp;
  }

  /**
   * @param pUpdateTimestamp updateTimestamp
   */
  public void setUpdateTimestamp(final Timestamp pUpdateTimestamp) {
    this.updateTimestamp = pUpdateTimestamp;
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------
