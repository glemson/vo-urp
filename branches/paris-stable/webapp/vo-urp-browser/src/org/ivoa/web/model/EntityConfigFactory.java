package org.ivoa.web.model;

import org.ivoa.dm.MetaModelFactory;
import org.ivoa.dm.ObjectClassType;

import java.util.HashMap;
import java.util.Map;
import org.ivoa.bean.SingletonSupport;

/**
 * EntityConfig factory to hold references to EntityConfig
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class EntityConfigFactory extends SingletonSupport {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /** singleton instance */
    private static volatile EntityConfigFactory instance = null;

    //~ Members ----------------------------------------------------------------------------------------------------------

    // members :
    /** EntityConfig Classes mapped by alias */
    private final Map<String, EntityConfig> configs = new HashMap<String, EntityConfig>();

    //~ Constructors -----------------------------------------------------------------------------------------------------
    /**
     * Creates a new EntityConfigFactory object
     */
    protected EntityConfigFactory() {
        super();
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * Return the EntityConfigFactory singleton instance
     *
     * @return EntityConfigFactory singleton instance
     *
     * @throws IllegalStateException if a problem occured
     */
    public static final EntityConfigFactory getInstance() {
        if (instance == null) {
            instance = prepareInstance(new EntityConfigFactory());
        }
        return instance;
    }

    /**
     * Concrete implementations of the SingletonSupport's initialize() method :<br/>
     * Callback to initialize this SingletonSupport instance
     *
     * @see SingletonSupport#initialize()
     *
     * @throws IllegalStateException if a problem occured
     */
    @Override
    protected void initialize() throws IllegalStateException {
        // process all ClassTypes :
        final MetaModelFactory mf = MetaModelFactory.getInstance();

        String name;
        EntityConfig ec = null;

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
     * Concrete implementations of the SingletonSupport's clear() method :<br/>
     * Callback to clean up this SingletonSupport instance iso clear instance fields
     *
     * @see SingletonSupport#clear()
     */
    @Override
    protected void clear() {
        // force GC :
        getConfigs().clear();

    }

    /**
     * Concrete implementations of the SingletonSupport's clearStaticReferences() method :<br/>
     * Callback to clean up the possible static references used by this SingletonSupport instance
     * iso clear static references
     *
     * @see SingletonSupport#clearStaticReferences()
     */
    @Override
    protected void clearStaticReferences() {
        if (instance != null) {
            instance = null;
        }
    }

    /**
     * Add the given entityConfig instance to the entityConfig map
     *
     * @param ec entityConfig instance to add
     */
    public void addConfig(final EntityConfig ec) {
        // calls init (compute) :
        ec.init();

        configs.put(ec.getName(), ec);
    }

    /**
     * Return the entityConfig map
     *
     * @return entityConfig map
     */
    protected Map<String, EntityConfig> getConfigs() {
        return configs;
    }

    /**
     * Return the entityConfig instance for the given alias
     *
     * @param alias class type name
     *
     * @return entityConfig instance or null if not found
     */
    public EntityConfig getConfig(final String alias) {
        return configs.get(alias);
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
