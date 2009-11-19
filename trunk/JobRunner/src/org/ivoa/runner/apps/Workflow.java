
package org.ivoa.runner.apps;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *       A WebApp wraps a pipeline of legacy apps, which are tasks.
 *       They all run in a common working directory in sequential order.
 *     
 * 
 * <p>Java class for Workflow complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Workflow">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="legacyApp" type="{http://vo-urp.googlecode.com/LegacyApps}LegacyApp" maxOccurs="unbounded"/>
 *         &lt;element name="task" type="{http://vo-urp.googlecode.com/LegacyApps}Task" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Workflow", propOrder = {
    "legacyApp",
    "task"
})
public class Workflow {

    @XmlElement(required = true)
    protected List<LegacyApp> legacyApp;
    @XmlElement(required = true)
    protected List<Task> task;

    /**
     * Gets the value of the legacyApp property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the legacyApp property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLegacyApp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LegacyApp }
     * 
     * 
     */
    public List<LegacyApp> getLegacyApp() {
        if (legacyApp == null) {
            legacyApp = new ArrayList<LegacyApp>();
        }
        return this.legacyApp;
    }

    /**
     * Gets the value of the task property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the task property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTask().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Task }
     * 
     * 
     */
    public List<Task> getTask() {
        if (task == null) {
            task = new ArrayList<Task>();
        }
        return this.task;
    }

}
