
package org.ivoa.dm.model.gen;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.ivoa.dm.model.gen package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Root_QNAME = new QName("http://www.ivoa.net/xml/dm/base/v0.1", "root");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.ivoa.dm.model.gen
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MetadataRootEntities }
     * 
     */
    public MetadataRootEntities createMetadataRootEntities() {
        return new MetadataRootEntities();
    }

    /**
     * Create an instance of {@link Reference }
     * 
     */
    public Reference createReference() {
        return new Reference();
    }

    /**
     * Create an instance of {@link MetadataRootEntityObject }
     * 
     */
    public MetadataRootEntityObject createMetadataRootEntityObject() {
        return new MetadataRootEntityObject();
    }

    /**
     * Create an instance of {@link FragmentReference }
     * 
     */
    public FragmentReference createFragmentReference() {
        return new FragmentReference();
    }

    /**
     * Create an instance of {@link Identity }
     * 
     */
    public Identity createIdentity() {
        return new Identity();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MetadataRootEntities }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ivoa.net/xml/dm/base/v0.1", name = "root")
    public JAXBElement<MetadataRootEntities> createRoot(MetadataRootEntities value) {
        return new JAXBElement<MetadataRootEntities>(_Root_QNAME, MetadataRootEntities.class, null, value);
    }

}
