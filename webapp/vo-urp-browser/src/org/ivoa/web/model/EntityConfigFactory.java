package org.ivoa.web.model;

import org.ivoa.dm.MetaModelFactory;
import org.ivoa.dm.ObjectClassType;

import java.util.HashMap;
import java.util.Map;


/**
 * 
DOCUMENT ME!
 *
 * @author laurent
 */
public final class EntityConfigFactory {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  private static EntityConfigFactory instance = new EntityConfigFactory();

  //~ Members ----------------------------------------------------------------------------------------------------------

  // members :
  /** EntityConfig Classes mapped by alias */
  private Map<String, EntityConfig> configs = new HashMap<String, EntityConfig>();

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Creates a new EntityConfigFactory object
   */
  private EntityConfigFactory() {
    init();
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public static final EntityConfigFactory getInstance() {
    return instance;
  }

  /**
   * TODO : Method Description
   */
  public static void clean() {
    instance = null;
  }

  /**
   * TODO : Method Description
   */
  public void init() {
    // process all ClassTypes :
    final MetaModelFactory mf   = MetaModelFactory.getInstance();

    String                 name;
    EntityConfig           ec   = null;

    for (final ObjectClassType ct : mf.getObjectClassTypes().values()) {
      name = ct.getType().getName();

      ec = new EntityConfig(mf.getClass(name), true, true);
      ec.addPage("xml", "DM2XML");
      ec.addPage("json", "DM2JSON");
      ec.addPage("edit", "EditMetadataObject");
      addConfig(ec);
    }
  }

  /**
   * TODO : Method Description
   *
   * @param e 
   */
  public void addConfig(final EntityConfig e) {
    // calls init (compute) :
    e.init();

    configs.put(e.getName(), e);
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public Map getConfigs() {
    return configs;
  }

  /**
   * TODO : Method Description
   *
   * @param alias 
   *
   * @return value TODO : Value Description
   */
  public EntityConfig getConfig(final String alias) {
    return configs.get(alias);
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
