
package org.ivoa.runner.apps;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.ivoa.runner.apps package. 
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

    private final static QName _Workflow_QNAME = new QName("http://vo-urp.googlecode.com/LegacyApps", "workflow");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.ivoa.runner.apps
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ParameterFile }
     * 
     */
    public ParameterFile createParameterFile() {
        return new ParameterFile();
    }

    /**
     * Create an instance of {@link Workflow }
     * 
     */
    public Workflow createWorkflow() {
        return new Workflow();
    }

    /**
     * Create an instance of {@link Task }
     * 
     */
    public Task createTask() {
        return new Task();
    }

    /**
     * Create an instance of {@link Parameter }
     * 
     */
    public Parameter createParameter() {
        return new Parameter();
    }

    /**
     * Create an instance of {@link LegacyApp }
     * 
     */
    public LegacyApp createLegacyApp() {
        return new LegacyApp();
    }

    /**
     * Create an instance of {@link ValidValue }
     * 
     */
    public ValidValue createValidValue() {
        return new ValidValue();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Workflow }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://vo-urp.googlecode.com/LegacyApps", name = "workflow")
    public JAXBElement<Workflow> createWorkflow(Workflow value) {
        return new JAXBElement<Workflow>(_Workflow_QNAME, Workflow.class, null, value);
    }

}
