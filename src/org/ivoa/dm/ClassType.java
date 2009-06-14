package org.ivoa.dm;


import org.ivoa.metamodel.Attribute;
import org.ivoa.metamodel.DataType;
import org.ivoa.metamodel.Type;
import org.ivoa.metamodel.TypeRef;


import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.ivoa.bean.LogSupport;


/**
 * ClassType represents a java class (in memory) corresponding to the metamodel for an UML DataType. This class is
 * used to find directly all elements inside an inheritance hierarchy
 *
 * @author laurent bourges (voparis)
 */
public class ClassType extends LogSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** default toString buffer size */
  public static final int STRING_BUFFER_CAPACITY = 2048;

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** wrapped type */
  protected final Type type;
  /** all DataType attributes ordered by the class hierarchy */
  private Map<String, Attribute> attributes = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor for a given type
   *
   * @param type to wrap
   */
  public ClassType(final Type type) {
    this.type = type;
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   */
  public void init() {
    if (log.isInfoEnabled()) {
      log.info("ClassType.init : enter : " + type.getName());
    }

    this.process(getDataType());

    if (log.isInfoEnabled()) {
      log.info("ClassType.init : exit : " + type.getName() + " :\n" + toString());
    }
  }

  /**
   * TODO : Method Description
   *
   * @param t
   */
  private void process(final DataType t) {
    if (log.isInfoEnabled()) {
      log.info("ClassType.process : enter : " + t.getName());
    }

    // parent identityType definition :
    final TypeRef parentTypeRef = t.getExtends();

    if (parentTypeRef != null) {
      if (log.isInfoEnabled()) {
        log.info("ClassType.process : find definition for : " + parentTypeRef.getName());
      }

      final DataType parentType = MetaModelFactory.getInstance().getDataType(parentTypeRef.getName());

      if (parentType != null) {
        // go up in inheritance hierarchy and later down :
        this.process(parentType);
      }
    }

    String name;

    // check collection to prepare local collection :
    if (t.getAttribute().size() > 0) {
      lazyAttributes();

      // navigate through attributes :
      for (final Attribute a : t.getAttribute()) {
        name = a.getName();

        // attribute can be overriden for a given name :
        getAttributes().put(name, a);
      }
    }

    if (log.isInfoEnabled()) {
      log.info("ClassType.process : exit : " + t.getName());
    }
  }

  /**
   * Returns a string representation : creates a temporary StringBuilder(STRING_BUFFER_CAPACITY) and calls
   * #toString(java.lang.StringBuilder) method
   *
   * @return string representation
   *
   * @see #toString(java.lang.StringBuilder) method
   */
  @Override
  public final String toString() {
    // always gives an initial size to buffer : 
    return toString(new StringBuilder(STRING_BUFFER_CAPACITY)).toString();
  }

  /**
   * Puts the string representation in the given string buffer : NO DEEP toString(java.lang.StringBuilder,
   * boolean) recursion
   *
   * @param sb given string buffer to fill
   *
   * @return the given string buffer filled with the string representation
   */
  public StringBuilder toString(final StringBuilder sb) {
    sb.append("ClassType[");
    sb.append(getDataType().getName());
    sb.append("]={");

    if (isHasAttributes()) {
      sb.append("attributes={");

      for (final String name : getAttributes().keySet()) {
        sb.append(name).append(" ");
      }

      sb.append("}");
    }

    return sb;
  }

  // --- getters -----
  /**
   * Returns the wrapped UML type
   *
   * @return wrapped UML type
   */
  public final Type getType() {
    return type;
  }

  /**
   * Returns the wrapped UML datatype
   *
   * @return wrapped UML datatype
   */
  public final DataType getDataType() {
    return (DataType) type;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public final boolean isHasAttributes() {
    return attributes != null;
  }

  /**
   * TODO : Method Description
   */
  protected void lazyAttributes() {
    if (getAttributes() == null) {
      this.attributes = new LinkedHashMap<String, Attribute>();
    }
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public final Map<String, Attribute> getAttributes() {
    return attributes;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public final Collection<Attribute> getAttributeList() {
    return attributes.values();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
