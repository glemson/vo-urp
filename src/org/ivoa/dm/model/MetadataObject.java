package org.ivoa.dm.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.ivoa.conf.Configuration;
import org.ivoa.dm.ObjectClassType;
import org.ivoa.metamodel.Collection;
import org.ivoa.util.JavaUtils;

/**
 * MetadataObject : Super class for all UML ObjectType only.
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "MetadataObject", namespace = "http://www.ivoa.net/xml/dm/base/v0.1")
public abstract class MetadataObject extends MetadataElement {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /** serial UID for Serializable interface */
    private static final long serialVersionUID = 1L;
    /** id field name */
    public static final String PROPERTY_ID = "id";
    /** current field name */
    public static final String PROPERTY_IDENTITY = "identity";
    /** container field name */
    public static final String PROPERTY_CONTAINER = "container";
    /** rank field name */
    public static final String PROPERTY_RANK = "rank";

    //~ Members ----------------------------------------------------------------------------------------------------------
    /** Variable storing the lifecycle status of this object */
    @Transient
    @XmlTransient
    private final State _state = new State();
    /**
     * Primary key = ID (current value generated by the database at insert statement execution). This field is not
     * mapped with JAXB annotations : this PK does not belong to XML schemas
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, precision = 18, scale = 0)
    protected Long id = null;
    /** Identity associated class : all flavor for identifiers */
    @Embedded
    @XmlElement(name = "identity", required = false, type = Identity.class)
    protected Identity identity = null;
    /** A reference to this object, lazily instantiated */
    @Transient
    @XmlTransient
    protected Reference reference = null;

    //~ Constructors -----------------------------------------------------------------------------------------------------
    /**
     * Public No-arg Constructor for JAXB / JPA Compliance
     */
    public MetadataObject() {
        super();
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * Returns ObjectClassType metadata via MetaModelFactory singleton
     *
     * @return ObjectClassType metadata
     */
    public final ObjectClassType getClassMetaData() {
        return getMetaModelFactory().getObjectClassType(getClassName());
    }

    /**
     * Updates the primary key of the current instance  <br>
     * Must be public (eclipselink bug if protected whereas JPA spec says it possible)
     */
    @PostLoad
    @PostPersist
    public void updateIdentity() {
        final Identity old = getIdentityField();

        if (old != null) {
            old.setId(this.getId());
        }
    }

    /**
     * Returns the primary key (auto generated value by database)
     *
     * @return the primary key or null if undefined (before persist operation)
     */
    public Long getId() {
        return this.id;
    }
    /**
     * Return true if this object does not have a representation in the database yet, false otherwise.<br/>
     * 
     * TODO currently a little bit a shaky implementation. We may want to implement this using a proper status object. 
     * Or by using special values for the id.
     * 
     * @return true if this object does not have a representation in the database yet, false otherwise
     */
    public boolean isPurelyTransient() {
      return getId() == null;
    }

    /**
     * Returns the current field but creates a new Identity if needed. <br>
     * This method is dedicated to : <br>
     * - JPA client code via Identity methods : setXmlId() or setIvoId() - JAXB integration for JAXB unmarshalling of
     * the xmlId attribute while importing a XML document
     *
     * @return the current for this instance
     *
     * @see #lazyIdentity()
     */
    public Identity getIdentity() {
        return lazyIdentity();
    }

    /**
     * Returns computed hashcode from v attribute in this implementation : NO DEEP hashCode(boolean) recursion.
     * Child classes can override this method to force use isDeep = true
     *
     * @return v.hashCode() or 0 if v is null
     */
    @Override
    public int hashCode() {
        return this.hashCode(false);
    }

    /**
     * Returns computed hashcode from v attribute in this implementation. Child classes can override this method to
     * allow deep computation with attributes / references / collections
     *
     * @param isDeep true means to call hashCode(sb, true) recursively for all attributes / references / collections
     *        which are MetadataElement implementations
     *
     * @return (v.hashCode() or 0 if v is null) in this default implementation
     */
    protected int hashCode(final boolean isDeep) {
        final Object v = getId();

        return (v != null) ? v.hashCode() : 0;
    }

    /**
     * Returns true if the given object has the same type and the same v in this implementation. Child classes can
     * override this method to provide different behaviours like deep equals with attributes / references / collections
     *
     * @param object the reference object with which to compare
     * @param isDeep true means to call equals(Object, true) recursively for all attributes / references / collections
     *        which are MetadataElement implementations
     *
     * @return <code>true</code> if this object is the same as the object argument; <code>false</code> otherwise
     *
     * @see #equals(java.lang.Object) method
     */
    @Override
    public boolean equals(final Object object, final boolean isDeep) {
        if (!super.equals(object, isDeep)) {
            return false;
        }

        final MetadataObject other = (MetadataObject) object;

        return areEquals(this.getId(), other.getId());
    }

    /**
     * Puts the base string representation in the given buffer : "[$id : $xmlId : $ivoId] $class_name$ @
     * $hashcode$" pattern
     *
     * @param sb given string buffer to fill
     *
     * @return the given string buffer filled with the string representation
     */
    @Override
    protected final StringBuilder baseToString(final StringBuilder sb) {
        if (getIdentityField() != null) {
            getIdentityField().toString(sb);
        } else {
            sb.append("[").append((getId() != null) ? getId() : "N/A").append("] ");
        }

        return super.baseToString(sb);
    }

    /**
     * Returns the current field but creates a new Identity if needed.
     *
     * @return the current for this instance
     */
    protected final Identity lazyIdentity() {
        final Identity old = getIdentityField();

        if (old == null) {
            if (log.isDebugEnabled()) {
                log.debug("creates an Identity instance for : " + this.toString());
            }

            setIdentityField(new Identity(getId()));

            return getIdentityField();
        }

        return old;
    }

    /**
     * Returns the current field (this.current)
     *
     * @return the current field (this.current)
     */
    protected Identity getIdentityField() {
        return this.identity;
    }

    /**
     * Sets the current field (this.current)
     *
     * @param pIdentity pIdentity instance
     */
    private void setIdentityField(final Identity pIdentity) {
        this.identity = pIdentity;
    }

    /**
     * Return the publisherDID
     *
     * @return publisherDID
     */
    public String getPublisherDID() {
        return getIdentity().getPublisherDID();
    }

    /**
     * Sets the publisherDID field<br>
     *
     * @param publisherDID publisherDID value
     */
    public void setPublisherDID(final String publisherDID) {
        getIdentity().setPublisherDID(publisherDID);
    }

    /**
     * Sets the XmlID field on the identity<br>
     */
    public void setXmlId() {
        getIdentity().setXmlId(getXmlId());
    }

    /**
     * return the UTYPE of this object
     *
     * @return UTYPE of this object
     */
    public String getUtype() {
        return getClassMetaData().getObjectType().getUtype();
    }

    /**
     * Calculate the IvoID of this object
     *
     * @return IvoID of this object
     */
    public String getIvoId() {
        if (id == null) {
            return null;
        }
        return Configuration.getInstance().getIVOIdPrefix() + getUtype() + "/" + id;
    }

    /**
     * Calculate the XmlId of this object
     *
     * @return XmlId of this object
     */
    public String getXmlId() {
        return this.getClass().getSimpleName() + "_" + id;
    }

    /**
     * Tries to resolve the supplied reference to the corresponding MetadataObject of the specified type. <br>
     * Tests whether the object retrieved has the correct type, throws an ...Exception if not.
     *
     * @param pReference to resolve
     * @param type
     *
     * @return resolved metadata object or null if not found
     */
    protected final MetadataObject resolve(final Reference pReference, final Class<? extends MetadataObject> type) {
        return ReferenceResolver.resolve(pReference, type);
    }

    /**
     * Creates a new Reference for the given target
     *
     * @param target
     *
     * @return Reference to that target object
     */
    protected final Reference newReference(final MetadataObject target) {
        return target.asReference();
    }

    /**
     * Returns the property value given the property name. <br>
     * Can be any property (internal, attribute, reference, collection) and all type must be supported (dataType,
     * objectType, enumeration)
     *
     * @param propertyName name of the property (like in UML model)
     *
     * @return property value or null if unknown or not defined
     */
    @Override
    public Object getProperty(final String propertyName) {
        if (PROPERTY_ID.equals(propertyName)) {
            return getId();
        }

        if (PROPERTY_IDENTITY.equals(propertyName)) {
            return getIdentity();
        }

        return null;
    }

    /**
     * Sets the property value to the given property name. <br>
     * Can be any property (internal, attribute, reference, collection) and all type must be supported (dataType,
     * objectType, enumeration)
     *
     * @param propertyName name of the property (like in UML model)
     * @param value to be set
     *
     * @return true if property has been set
     */
    @Override
    public boolean setProperty(final String propertyName, final Object value) {
        /*
         * TODO : support for identity field (later)
         */

        /*
        if (PROPERTY_IDENTITY.equals(propertyName)) {
        setIdentity(xxx);
        }
         */
        return false;
    }

    /**
     * Traversal method in the visitor pattern.<br>
     * First the preProcess method of the specified visitor is called with this in the argument. Than it is sent on to
     * the contained collections. At the end the postProcess method is called with thihs.
     *
     * @param visitor
     */
    @SuppressWarnings("unchecked")
    public final void traverse(final Visitor visitor) {
        visitor.preProcess(this);

        final ObjectClassType metadataObject = getClassMetaData();
        final java.util.Collection<Collection> collections = metadataObject.getCollectionList();

        List<? extends MetadataObject> objects;
        for (Collection collection : collections) {
            objects = (List<? extends MetadataObject>) getProperty(collection.getName());

            if (!JavaUtils.isEmpty(objects)) {
                for (MetadataObject object : objects) {
                    object.traverse(visitor);
                }
            }
        }

        visitor.postProcess(this);
    }

    /**
     * Lazy getter for a Reference object that references this metadata object.<br>
     * Only to be used hen marshalling.
     *
     * @return Reference object that references this metadata object
     */
    public final Reference asReference() {
        synchronized (this) {
            if (reference == null && id != null) {
                reference = new Reference();
            }

            if (getInternalState().isToBeMarshalled()) {
                // only internal (XML ID/IDREF) references required

                reference.setIvoId(null);
                reference.setXmlId(getXmlId());
            } else {
                // reference to object NOT in the XMl document. Should add possible publisherDID.

                reference.setIvoId(getIvoId());
                reference.setXmlId(null);
            }

            // In all cases add the publisherDID if it exists :
            if (JavaUtils.isSet(getPublisherDID())) {
                reference.setPublisherDID(getPublisherDID());
            }
        }

        return reference;
    }

    /**
     * Set all Reference objects to their appropriate value.<br>
     */
    protected void prepareReferencesForMarshalling() {
        /* no-op */
    }

    /**
     * Set all Reference objects to null.
     */
    protected void resetReferencesAfterMarshalling() {
        /* no-op */
    }

    /**
     * return the status object of this object
     *
     * @return status object of this object
     */
    protected final State getInternalState() {
        return _state;
    }

    /**
     * return the status object of the source MetadataObject instance
     *
     * @param source MetadataObject instance
     * @return status object of the source object
     */
    protected final State getStateFor(final MetadataObject source) {
        return source.getInternalState();
    }

}
//~ End of file --------------------------------------------------------------------------------------------------------
