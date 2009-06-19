package org.ivoa.conf;

/**
 * This interface defines the Configuration class to use in this project and its file name
 * 
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public interface Configurable {

  /** Configuration class name */
  public static final String CONFIGURATION_CLASS = "org.ivoa.conf.RuntimeConfiguration";

  /** file name for property file */
  public static final String CONFIGURATION_FILE = "runtime.properties";

}
