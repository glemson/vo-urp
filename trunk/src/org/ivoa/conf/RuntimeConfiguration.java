package org.ivoa.conf;


/**
 * Runtime Configuration for the application. Reads a file that is generated by the pipeline. 
 * It includes model specific properties.
 *
 * @author gerard lemson (GAVO) / laurent bourges (voparis)
 */
public final class RuntimeConfiguration extends Configuration {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;

  //~ Methods ----------------------------------------------------------------------------------------------------------


  /**
   * Get RuntimeConfiguration Instance (singleton)
   *
   * @return RuntimeConfiguration
   *
   * @throws IllegalStateException if init() failed
   */
  public static final RuntimeConfiguration get() {
    return (RuntimeConfiguration) getInstance();
  }
  
  
  /**
   * Gives the property value for the key [intermediate.model.file]
   *
   * @return property value for the key [intermediate.model.file] or null if not found or contains only white spaces
   */
  public String getIntermediateModelFile() {
    return getProperty("intermediate.model.file");
  }

  /**
   * Gives the property value for the key [project.name]
   *
   * @return property value for the key [project.name] or null if not found or contains only white spaces
   */
  public String getProjectName() {
    return getProperty("project.name");
  }

  /**
   * Gives the property value for the key [project.contact]
   *
   * @return property value for the key [project.contact] or null if not found or contains only white spaces
   */
  public String getProjectContact() {
    return getProperty("project.contact");
  }

  /**
   * Gives the property value for the key [project.version]
   *
   * @return property value for the key [project.version] or null if not found or contains only white spaces
   */
  public String getProjectVersion() {
    return getProperty("project.version");
  }

  /**
   * Gives the property value for the key [project.title]
   *
   * @return property value for the key [project.title] or null if not found or contains only white spaces
   */
  public String getProjectTitle() {
    return getProperty("project.title");
  }

  /**
   * Gives the property value for the key [base.package]
   *
   * @return property value for the key [base.package] or null if not found or contains only white spaces
   */
  public String getBasePackage() {
    return getProperty("base.package");
  }

  /**
   * Gives the property value for the key [jaxb.package]
   *
   * @return property value for the key [jaxb.package] or null if not found or contains only white spaces
   */
  public String getJAXBPackage() {
    return getProperty("jaxb.package");
  }

  /**
   * Gives the property value for the key [intermediate.model.xmlns]
   *
   * @return property value for the key [intermediate.model.xmlns] or null if not found or contains only white spaces
   */
  public String getIntermediateModelXmlns() {
    return getProperty("intermediate.model.xmlns");
  }

  /**
   * Gives the property value for the key [jpa.persistence.unit]
   *
   * @return property value for the key [jpa.persistence.unit] or null if not found or contains only white spaces
   */
  public String getJPAPU() {
    return getProperty("jpa.persistence.unit");
  }

  /**
   * Gives the property value for the key [root.schema.url]
   *
   * @return property value for the key [root.schema.url] or null if not found or contains only white spaces
   */
  public String getRootSchemaURL() {
    return getProperty("root.schema.url");
  }

  /**
   * Gives the property value for the key [jaxb.context.classpath]
   *
   * @return property value for the key [jaxb.context.classpath] or null if not found or contains only white spaces
   */
  public String getJAXBContextClasspath() {
    return getProperty("jaxb.context.classpath");
  }

  /**
   * Gives the property value for the key [tap.metadata.xml.file]
   *
   * @return property value for the key [tap.metadata.xml.file] or null if not found or contains only white spaces
   */
  public String getTAPMetadataXMLFile() {
    return getProperty("tap.metadata.xml.file");
  }

  /**
   * Gives the property value for the key [tap.metadata.votable.file]
   *
   * @return property value for the key [tap.metadata.votable.file] or null if not found or contains only white spaces
   */
  public String getTAPMetadataVOTableFile() {
    return getProperty("tap.metadata.votable.file");
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
