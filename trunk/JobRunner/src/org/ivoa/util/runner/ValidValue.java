package org.ivoa.util.runner;

import java.io.Serializable;

public class ValidValue implements Serializable, Cloneable {

	/** the actual value represented by this valid value */
	private String value;
	/** the human readable name of this valid value */ 
	private String name;
	/** the description of this valid value */
	private String description;
}
