package org.ivoa.json;

import org.ivoa.dm.MetaModelFactory;
import org.ivoa.dm.ModelFactory;
import org.ivoa.dm.ObjectClassType;

import org.ivoa.dm.model.MetadataObject;

import org.ivoa.metamodel.Attribute;
import org.ivoa.metamodel.Reference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * TODO : Class Description
 *
 * @author laurent bourges (voparis) / Gerard Lemson (mpe)
  */
public class MetadataObject2JSON {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  public static final String nl          = "\n";
  /**
   * TODO : Field Description
   */
  public static final String colon = " : ";
  /**
   * TODO : Field Description
   */
  public static final String comma = ",";
  /**
   * TODO : Field Description
   */
  public static final String baseindent = "\t";
  /**
   * TODO : Field Description
   */
  public static final String lb = "{";
  /**
   * TODO : Field Description
   */
  public static final String rb = "}";
  /**
   * TODO : Field Description
   */
  public static final String type = "type";
  /**
   * TODO : Field Description
   */
  public static final String attributes = "attributes";
  /**
   * TODO : Field Description
   */
  public static final String collections = "collections";
  /**
   * TODO : Field Description
   */
  public static final String references = "references";

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * 
  DOCUMENT ME!
   *
   * @param args
   */
  public static void main(final String[] args) {
    // TODO Auto-generated method stub
  }

  /**
   * TODO : Method Description
   *
   * @param obj 
   *
   * @return value TODO : Value Description
   */
  public String toJSONString(final MetadataObject obj) {
    String s                             = null;

    try {
      JSONObject json = toJSON(obj);

      if (json != null) {
        s = json.toString(2);
      } else {
        s = "Error serialising metadata object to JSON.";
      }
    } catch (final JSONException e) {
      s = "Error serialising JSONObject.";
    }

    return s;
  }

  /**
   * Write JSON string using org.json.JSONObject etc.<br>
   *
   * @param obj MetadataObject
   *
   * @return JSONObject or null
   */
  public JSONObject toJSON(final MetadataObject obj) {
    if (obj == null) {
      return null;
    }

    JSONObject json = new JSONObject();
    String     s    = null;

    try {
      typeMetadata2JSON(json, obj);
      attributes2JSON(json, obj);
      collections2JSON(json, obj);

      //		references2JSON(json, obj);
    } catch (final JSONException e) {
      return null;
    }

    return json;
  }

  /**
   * TODO : Method Description
   *
   * @param json 
   * @param obj 
   *
   * @throws JSONException 
   */
  private void typeMetadata2JSON(final JSONObject json, final MetadataObject obj)
                          throws JSONException {
    ObjectClassType md = obj.getClassMetaData();

    json.put("type", md.getType().getName());
  }

  /**
   * TODO : Method Description
   *
   * @param json 
   * @param obj 
   *
   * @throws JSONException 
   */
  private void attributes2JSON(final JSONObject json, final MetadataObject obj)
                        throws JSONException {
    ObjectClassType     md  = obj.getClassMetaData();
    Map<String, Object> map = new HashMap<String, Object>();

    for (final Attribute a : md.getAttributeList()) {
      map.put(a.getName(), obj.getProperty(a.getName()));
    }

    json.put(attributes, map);
  }

  /**
   * TODO : Method Description
   *
   * @param json 
   * @param obj 
   *
   * @throws JSONException 
   */
  private void collections2JSON(final JSONObject json, final MetadataObject obj)
                         throws JSONException {
    ObjectClassType     md  = obj.getClassMetaData();
    Map<String, Object> map = new HashMap<String, Object>();

    json.put(collections, map);

    for (final org.ivoa.metamodel.Collection c : md.getCollectionList()) {
      JSONArray a = new JSONArray();

      map.put(c.getName(), a);

      List<MetadataObject> col = (List<MetadataObject>) obj.getProperty(c.getName());

      for (final MetadataObject child : col) {
        a.put(toJSON(child));
      }
    }
  }

  /**
   * TODO : Method Description
   *
   * @param jsonString 
   *
   * @return value TODO : Value Description
   *
   * @throws JSONException 
   * @throws InstantiationException 
   * @throws IllegalAccessException 
   */
  public MetadataObject toMetadataObject(final String jsonString)
                                  throws JSONException, InstantiationException, IllegalAccessException {
    JSONObject     json = new JSONObject(jsonString);
    String         t    = json.getString(type);
    Class          cl   = MetaModelFactory.getInstance().getClass(t);
    MetadataObject o    = (MetadataObject) cl.newInstance();

    return o;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
