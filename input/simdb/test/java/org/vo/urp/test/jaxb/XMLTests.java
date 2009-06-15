package org.vo.urp.test.jaxb;

import org.ivoa.bean.LogSupport;
import org.ivoa.dm.ModelFactory;
import org.ivoa.dm.model.MetadataObject;

import org.ivoa.env.ApplicationMain;

import org.ivoa.simdb.DataType;
import org.ivoa.simdb.protocol.InputParameter;
import org.ivoa.simdb.protocol.Simulator;


/**
 * TODO : Class Description
 *
 * @author laurent bourges (voparis) / Gerard Lemson (mpe)
 */
public final class XMLTests extends LogSupport implements ApplicationMain {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * local /test/ folder
   */
  public static final String TEST_PATH = "../test/";

  /**
   * xml file extension
   */
  public static final String XML_EXT = ".xml";

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Constructor
   */
  public XMLTests() {
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param args
   */
  public void run(final String[] args) {
    log.info("XMLTests.run : enter");

    testMarshall();

    log.info("XMLTests.run : exit");
  }

  /**
   * Marshalls a simple Simulation to XML
   */
  public void testMarshall() {
    // Always declare variables as final to avoid instable code :
    final Simulator s = new Simulator();

    s.setName("Gadget");
    s.setDescription("Volker's code");

    final InputParameter p1 = new InputParameter(s);

    p1.setName("H0");
    p1.setDatatype(DataType.DOUBLE);

    final InputParameter p2 = new InputParameter(s);

    p2.setName("Lambda_0");
    p2.setDatatype(DataType.DOUBLE);

    testMarshall(s);
  }

  /**
   * TODO : Method Description
   *
   * @param e
   */
  public void testMarshall(final MetadataObject e) {
    // create an Unmarshaller
//    ModelFactory.getInstance().getJaxbFactory().dump(e, 4096);
    System.out.print(ModelFactory.getInstance().marshallObject(e, 4096));
  }
  
  /**
   * TODO : Method Description
   *
   * @param o object to save
   * @param filePath file to produce
   */
  public void saveMarshall(final MetadataObject o, final String filePath) {
    // create an Unmarshaller
    ModelFactory.getInstance().marshallObject(filePath, o);
  }

  /**
   * Loads an XML document to test unmarshalling
   *
   * @param filePath document to load
   * @return metadataObject complete java model
   */
  public MetadataObject testUnMashall(final String filePath) {
    return ModelFactory.getInstance().unmarshallToObject(filePath);
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------