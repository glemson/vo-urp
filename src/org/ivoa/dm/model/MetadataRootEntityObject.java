package org.ivoa.dm.model;

import java.sql.Timestamp;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;

/**
 * Root Entity Object is a base type corresponding to a full XML document
 * 
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
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
  @Column(name = "dbInsertTimestamp", nullable = true)
  private Timestamp dbInsertTimestamp;

  /**
   * The timestamp when this record was last updated in the database.
   */
  @Basic(optional = true)
  @Column(name = "dbUpdateTimestamp", nullable = true)
  private Timestamp dbUpdateTimestamp;

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
   * @return  timestamp at insert time
   */
  public Timestamp getDBInsertTimestamp() {
    return dbInsertTimestamp;
  }

  /**
   * @param pInsertTimestamp timestamp at insert time
   */
  public void setDBInsertTimestamp(final Timestamp pInsertTimestamp) {
    this.dbInsertTimestamp = pInsertTimestamp;
  }

  /**
   * @return timestamp at update time
   */
  public Timestamp getDBUpdateTimestamp() {
    return dbUpdateTimestamp;
  }

  /**
   * @param pDBUpdateTimestamp timestamp at update time
   */
  public void setDbUpdateTimestamp(final Timestamp pDBUpdateTimestamp) {
    this.dbUpdateTimestamp = pDBUpdateTimestamp;
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
