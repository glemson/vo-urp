package org.ivoa.util.runner;

import java.io.Serializable;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.vourp.runner.model.Choice;
import org.vourp.runner.model.Datatype;
import org.vourp.runner.model.EnumeratedParameter;
import org.vourp.runner.model.Literal;
import org.vourp.runner.model.ParameterDeclaration;
import org.vourp.runner.model.Validvalues;


/**
 * Generic parameter setting: name and value. Context must be provided by the
 * container context.
 * 
 * @author Gerard Lemson
 */
@Entity
@Table(name = "parameter_setting")
@NamedQueries( { @NamedQuery(name = "ParameterSetting.findById", query = "SELECT o FROM ParameterSetting o WHERE o.id = :id") })
public class ParameterSetting implements Serializable, Cloneable {
	/**
	 * serial UID for Serializable interface
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Job identifier
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false, precision = 18, scale = 0)
	private Long id;

	/**
	 * container gives the parent entity which owns a collection containing
	 * instances of this class
	 */
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH })
	@JoinColumn(name = "containerId", referencedColumnName = "id", nullable = false)
	protected RunContext container;

	@Basic(optional = false)
	@Column(name = "name", nullable = false)
	private String name;

	@Basic(optional = false)
	@Column(name = "value", nullable = false)
	private String value;
	
	public ParameterSetting(){}
	public ParameterSetting(RunContext run, String name, String value)
	{
		run.addParameter(this);
		this.container = run;
		this.name = name;
		this.value = value;
	}
	
	
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

	public RunContext getContainer() {
		return container;
	}

	public void setContainer(RunContext container) {
		this.container = container;
	}

}
