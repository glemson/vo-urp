package org.ivoa.web.model;

import java.util.HashMap;
import java.util.Map;
import org.ivoa.dm.ObjectClassType;
import org.ivoa.dm.MetaModelFactory;


/**
 *
 * @author laurent
 */
public final class EntityConfigFactory {
  
  private static EntityConfigFactory instance = new EntityConfigFactory();
  
  public final static EntityConfigFactory getInstance() {
    return instance;
  }
  
  public static void clean() {
    instance = null;
  }

  // members :
  
  /** EntityConfig Classes mapped by alias */
  private Map<String, EntityConfig> configs = new HashMap<String, EntityConfig>();

  
  private EntityConfigFactory() {
    init();
  }

  public void init() {
    
    // process all ClassTypes :
    final MetaModelFactory mf = MetaModelFactory.getInstance();
    
    String name;
    EntityConfig ec = null;
    for (ObjectClassType ct : mf.getObjectClassTypes().values()) {
      name = ct.getType().getName();
      
      ec = new EntityConfig(mf.getClass(name), true, true);
      ec.addPage("xml", "DM2XML");
      ec.addPage("json", "DM2JSON");
      ec.addPage("edit", "EditMetadataObject");
      addConfig(ec);
    }
  }
  
  public void addConfig(final EntityConfig e) {
    // calls init (compute) :
    e.init();
    
    configs.put(e.getName(), e);
  }

  public Map getConfigs() {
    return configs;
  }
  
  public EntityConfig getConfig(final String alias) {
    return configs.get(alias);
  }

}
