package org.ivoa.dm.model.visitor;

import org.ivoa.dm.ObjectClassType;
import org.ivoa.dm.model.MetaDataObjectVisitor;
import org.ivoa.dm.model.MetadataObject;
import org.ivoa.metamodel.Collection;
import org.ivoa.metamodel.Reference;

/**
 * MetadataObject TreeVisitor implementation :<br/>
 * Unmarshall processor to resolve references & collection specific attributes (container / rank)
 *
 * TODO : Use an IdentityMap to avoid cyclic loop :
 *     final Map<MetadataElement, Object> ids = new IdentityHashMap<MetadataElement, Object>(DEFAULT_IDENTITY_CAPACITY);

 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class UnmarshallObjectProcessor extends MetaDataObjectVisitor {

  /** singleton instance (java 5 memory model) */
  private static UnmarshallObjectProcessor instance = null;

  /**
   * Return the UnmarshallObjectProcessor singleton instance
   *
   * @return UnmarshallObjectProcessor singleton instance
   *
   * @throws IllegalStateException if a problem occured
   */
  public static final UnmarshallObjectProcessor getInstance() {
    if (instance == null) {
      instance = prepareInstance(new UnmarshallObjectProcessor());
    }
    return instance;
  }

  /**
   * Concrete implementations of the SingletonSupport's clearStaticReferences() method :<br/>
   * Callback to clean up the possible static references used by this SingletonSupport instance
   * iso clear static references
   *
   * @see org.ivoa.bean.SingletonSupport#clearStaticReferences()
   */
  @Override
  protected void clearStaticReferences() {
    if (instance != null) {
      instance = null;
    }
  }

  /**
   * Protected constructor to avoid to create instance except for singletons (stateless classes)
   */
  protected UnmarshallObjectProcessor() {
    super(true);
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Process the specified object
   *
   * @param object MetadataObject instance
   * @param argument optional argument
   */
  @Override
  @SuppressWarnings("unchecked")
  public void process(final MetadataObject object, final Object argument) {
    final ObjectClassType ct = object.getClassMetaData();
    if (log.isInfoEnabled()) {
      log.info("UnmarshallObjectProcessor.process : enter : " + object + " with type definition : " +
              ct.getObjectType().getName());
    }

    String propertyName;
    Object value;

    // navigate through references :
    // implies that lazy references will be resolved by ReferenceResolver :
    for (final Reference r : ct.getReferences().values()) {
      propertyName = r.getName();
      // this getProperty implies the lazy reference to be resolved now :
      value = object.getProperty(propertyName);
    }

    java.util.Collection<? extends MetadataObject> col;
    // navigate through collections :
    for (final Collection c : ct.getCollections().values()) {
      propertyName = c.getName();
      value = object.getProperty(propertyName);

      if (value != null) {
        col = (java.util.Collection<? extends MetadataObject>) value;

        if (col.size() > 0) {
          int i = 1;
          for (final MetadataObject item : col) {
            if (item != null) {
              checkContainer(item, i, object);
              i++;
            }
          }
        }
      }
    }

    if (log.isInfoEnabled()) {
      log.info("UnmarshallObjectProcessor.process : exit : " + object);
    }
  }

  /**
   * Sets containement references (parent with collection items). <br>
   * Does support collection Ordering base on a rank smallint field
   *
   * @param childItem MetadataObject instance
   * @param position index in the parent collection
   * @param parent parent MetadataObject instance
   */
  private void checkContainer(final MetadataObject childItem, final int position, final MetadataObject parent) {
    if (log.isInfoEnabled()) {
      log.info("UnmarshallObjectProcessor.checkContainer : enter : " + childItem);
    }

    // check getContainer() value :
    final MetadataObject container = (MetadataObject) childItem.getProperty(MetadataObject.PROPERTY_CONTAINER);

    if (container == null) {
      if (log.isInfoEnabled()) {
        log.info("UnmarshallObjectProcessor.checkContainer : setContainer to : " + parent);
      }

      childItem.setProperty(MetadataObject.PROPERTY_CONTAINER, parent);
    }

    // check getRank() value :
    final Integer rank = (Integer) childItem.getProperty(MetadataObject.PROPERTY_RANK);

    if (rank.intValue() <= 0) {
      if (log.isInfoEnabled()) {
        log.info("UnmarshallObjectProcessor.checkContainer : setRank to : " + position);
      }

      childItem.setProperty(MetadataObject.PROPERTY_RANK, Integer.valueOf(position));
    }

    if (log.isInfoEnabled()) {
      log.info("UnmarshallObjectProcessor.checkContainer : exit : " + childItem);
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
