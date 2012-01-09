package org.ivoa.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ivoa.dm.MetaModelFactory;
import org.ivoa.dm.ObjectClassType;
import org.ivoa.dm.model.MetadataObject;
import org.ivoa.metamodel.Attribute;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class contains utility methods to convert metadataObjects to JSON structures
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class MetadataObject2JSON {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** TODO : Field Description */
  public static final String nl = "\n";
  /** TODO : Field Description */
  public static final String colon = " : ";
  /** TODO : Field Description */
  public static final String comma = ",";
  /** TODO : Field Description */
  public static final String baseindent = "\t";
  /** TODO : Field Description */
  public static final String lb = "{";
  /** TODO : Field Description */
  public static final String rb = "}";
  /** TODO : Field Description */
  public static final String type = "type";
  /** TODO : Field Description */
  public static final String attributes = "attributes";
  /** TODO : Field Description */
  public static final String collections = "collections";
  /** TODO : Field Description */
  public static final String references = "references";

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Forbidden constructor
   */
  private MetadataObject2JSON() {
    /* no-op */
  }

  /**
   * TODO : Method Description
   *
   * @param obj
   *
   * @return value TODO : Value Description
   */
  public static final String toJSONString(final MetadataObject obj) {
    String s = null;

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
  public static final JSONObject toJSON(final MetadataObject obj) {
    if (obj == null) {
      return null;
    }

    JSONObject json = new JSONObject();

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
  private static final void typeMetadata2JSON(final JSONObject json, final MetadataObject obj)
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
  private static final void attributes2JSON(final JSONObject json, final MetadataObject obj)
          throws JSONException {
    ObjectClassType md = obj.getClassMetaData();
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
  @SuppressWarnings("unchecked")
  private static final void collections2JSON(final JSONObject json, final MetadataObject obj)
          throws JSONException {
    final Map<String, Object> map = new HashMap<String, Object>();

    json.put(collections, map);

    final ObjectClassType md = obj.getClassMetaData();
    
    for (final org.ivoa.metamodel.Collection c : md.getCollectionList()) {
      JSONArray a = new JSONArray();

      map.put(c.getName(), a);

      java.util.Collection<?> col = (java.util.Collection<?>) obj.getProperty(c.getName());

      for (final Object val : col) {
        if (val != null) {
          if (val instanceof MetadataObject) {
            final MetadataObject child = (MetadataObject) val;
            a.put(toJSON(child));
          } else {
            // primitive object:
            a.put(val.toString());
          }
        }
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
  public static final MetadataObject toMetadataObject(final String jsonString)
          throws JSONException, InstantiationException, IllegalAccessException {
    JSONObject json = new JSONObject(jsonString);
    String t = json.getString(type);
    Class<?> cl = MetaModelFactory.getInstance().getClass(t);
    MetadataObject o = (MetadataObject) cl.newInstance();

    return o;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
