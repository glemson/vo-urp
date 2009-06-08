package org.ivoa.xml;



import java.io.FileInputStream;


import java.util.HashMap;



/**
 * XSLT transformer with Saxon 9 API (xslt 2 engine)
 *
 * @author laurent bourges (voparis) / Gerard Lemson (mpe)
 */
public final class XSLTTransformerTest {
  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Main test
   *
   * @param args
   */
  public static void main(final String[] args) {
    /*
       mimic:
           <xslt2 in="C:/workspaces/eclipse3.2.2/SimDB/xslt/test/views/normalised_experiment.xml"
                   out="C:/workspaces/eclipse3.2.2/SimDB/xslt/test/views/denormalised_experiment.xml"
                 style="C:/workspaces/eclipse3.2.2/SimDB/xslt/transform_simdb.xsl">
               <param name="resolverURL" expression="${xslt.document.resolverURL}" />
               <param name="debug_protocol" expression="C:/workspaces/eclipse3.2.2/SimDB/xslt/test/views/theProtocol.xml" />
               <param name="mode" expression="forward"/>
           </xslt2>

     */
    try {
      final String infile    = "C:/workspaces/eclipse3.2.2/SimDB/xslt/test/views/normalised_experiment.xml";
      String sheetFile = "C:/workspaces/eclipse3.2.2/SimDB/xslt/transform_experiment.xsl";

      sheetFile = "C:/workspaces/eclipse_vo-urp/vo-urp.googlecode.com/input/simdb/views/dn/ExpToFromExp_dn.xslt";

      final HashMap<String, String> p = new HashMap<String, String>();

      p.put("mode", "forward");
      p.put("debug_protocol", "C:/workspaces/eclipse3.2.2/SimDB/xslt/test/views/theProtocol.xml");

      XSLTTransformer.transform(sheetFile, p, new FileInputStream(infile), System.out);
    } catch (final Throwable t) {
      t.printStackTrace();
    }
  }

}
//~ End of file --------------------------------------------------------------------------------------------------------
