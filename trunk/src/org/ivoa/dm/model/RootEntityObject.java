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
  @Column(name = "ownerUser", nullable = true)
  private String owner;

  /**
   * Username of owner of this root entity class.<br/>
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

    //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Public constructor
   */
  public RootEntityObject() {
    super();
  }

    //~ Methods ----------------------------------------------------------------------------------------------------------

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
