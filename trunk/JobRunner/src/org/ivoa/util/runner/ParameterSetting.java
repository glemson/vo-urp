package org.ivoa.util.runner;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Generic parameter setting: name and value. Context must be provided by the container context.
 *
 * @author Gerard Lemson
 */
@Entity
@Table(name = "ParameterSetting")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.STRING, length = 32)
@DiscriminatorValue("ParameterSetting")
@NamedQueries({@NamedQuery(name = "ParameterSetting.findById", query = "SELECT o FROM ParameterSetting o WHERE o.id = :id")
})
public class ParameterSetting implements Serializable, Cloneable{
	  /**
	   * serial UID for Serializable interface
	   */
	  private static final long serialVersionUID = 1L;
	  
	private RunContext container;
	
	@Basic(optional = false)
    @Column(name = "name", nullable = false)
	private String name;
	
	@Basic(optional = false)
    @Column(name = "value", nullable = false)
	private String value;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
