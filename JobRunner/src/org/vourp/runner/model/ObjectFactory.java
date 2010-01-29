
package org.vourp.runner.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.vourp.runner.model package. 
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

    private final static QName _Model_QNAME = new QName("http://gavo.org/JobRunner/LegacyApps/v0.1", "model");
    private final static QName _LegacyApp_QNAME = new QName("http://gavo.org/JobRunner/LegacyApps/v0.1", "legacyApp");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.vourp.runner.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ParameterSetting }
     * 
     */
    public ParameterSetting createParameterSetting() {
        return new ParameterSetting();
    }

    /**
     * Create an instance of {@link Model }
     * 
     */
    public Model createModel() {
        return new Model();
    }

    /**
     * Create an instance of {@link LegacyApp }
     * 
     */
    public LegacyApp createLegacyApp() {
        return new LegacyApp();
    }

    /**
     * Create an instance of {@link ParameterDeclaration }
     * 
     */
    public ParameterDeclaration createParameterDeclaration() {
        return new ParameterDeclaration();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Model }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://gavo.org/JobRunner/LegacyApps/v0.1", name = "model")
    public JAXBElement<Model> createModel(Model value) {
        return new JAXBElement<Model>(_Model_QNAME, Model.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LegacyApp }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://gavo.org/JobRunner/LegacyApps/v0.1", name = "legacyApp")
    public JAXBElement<LegacyApp> createLegacyApp(LegacyApp value) {
        return new JAXBElement<LegacyApp>(_LegacyApp_QNAME, LegacyApp.class, null, value);
    }

}
