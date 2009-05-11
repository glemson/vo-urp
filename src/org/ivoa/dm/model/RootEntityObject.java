package org.ivoa.dm.model;

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
}
