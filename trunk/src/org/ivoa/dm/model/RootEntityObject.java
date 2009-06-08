package org.ivoa.dm.model;

import java.sql.Timestamp;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;

public abstract class RootEntityObject extends MetadataObject {

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
    
    
    public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	  /**
	   * TODO : Method Description
	   *
	   * @param sb 
	   * @param isDeep 
	   * @param ids 
	   *
	   * @return value TODO : Value Description
	   */
	  protected StringBuilder deepToString(final StringBuilder sb, final boolean isDeep,
	                                       final Map<MetadataElement, Object> ids) {
	    return null;
	  }

    public Timestamp getCreateTimestamp() {
      return createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
      this.createTimestamp = createTimestamp;
    }

    public Timestamp getUpdateTimestamp() {
      return updateTimestamp;
    }

    public void setUpdateTimestamp(Timestamp updateTimestamp) {
      this.updateTimestamp = updateTimestamp;
    }
}
