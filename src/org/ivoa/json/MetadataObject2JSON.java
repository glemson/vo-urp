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


public class MetadataObject2JSON {
    public static final String nl = "\n";
    public static final String colon = " : ";
    public static final String comma = ",";
    public static final String baseindent = "\t";
    public static final String lb = "{";
    public static final String rb = "}";
    public static final String type = "type";
    public static final String attributes = "attributes";
    public static final String collections = "collections";
    public static final String references = "references";

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }

    public String toJSONString(MetadataObject obj) {
        String s = null;

        try {
            JSONObject json = toJSON(obj);

            if (json != null) {
                s = json.toString(2);
            } else {
                s = "Error serialising metadata object to JSON.";
            }
        } catch (JSONException e) {
            s = "Error serialising JSONObject.";
        }

        return s;
    }

    /**
     * Write JSON string using org.json.JSONObject etc.<br/>
     * @param obj MetadataObject
     * @return JSONObject or null
     */
    public JSONObject toJSON(MetadataObject obj) {
    	if(obj == null) {
    		return null;
        }
        JSONObject json = new JSONObject();
        String s = null;

        try {
            typeMetadata2JSON(json, obj);
            attributes2JSON(json, obj);
            collections2JSON(json, obj);

            //		references2JSON(json, obj);
        } catch (JSONException e) {
            return null;
        }

        return json;
    }

    private void typeMetadata2JSON(JSONObject json, MetadataObject obj)
        throws JSONException {
        ObjectClassType md = obj.getClassMetaData();
        json.put("type", md.getType().getName());
    }

    private void attributes2JSON(JSONObject json, MetadataObject obj)
        throws JSONException {
        ObjectClassType md = obj.getClassMetaData();
        Map<String, Object> map = new HashMap<String, Object>();

        for (Attribute a : md.getAttributeList()) {
            map.put(a.getName(), obj.getProperty(a.getName()));
        }

        json.put(attributes, map);
    }

    private void collections2JSON(JSONObject json, MetadataObject obj)
        throws JSONException {
        ObjectClassType md = obj.getClassMetaData();
        Map<String, Object> map = new HashMap<String, Object>();
        json.put(collections, map);

        for (org.ivoa.metamodel.Collection c : md.getCollectionList()) {
            JSONArray a = new JSONArray();
            map.put(c.getName(), a);

            List<MetadataObject> col = (List<MetadataObject>) obj.getProperty(c.getName());

            for (MetadataObject child : col) {
                a.put(toJSON(child));
            }
        }
    }

    public MetadataObject toMetadataObject(String jsonString) 
         throws JSONException, InstantiationException, IllegalAccessException
    {
    	JSONObject json = new JSONObject(jsonString);
    	String t = json.getString(type);
    	Class cl = MetaModelFactory.getInstance().getClass(t);
    	MetadataObject o = (MetadataObject)cl.newInstance();
    	return o;
    }
}
