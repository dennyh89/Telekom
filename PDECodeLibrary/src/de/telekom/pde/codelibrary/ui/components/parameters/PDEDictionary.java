/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.parameters;

import android.util.Log;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//----------------------------------------------------------------------------------------------------------------------
// PDEDictionary
//----------------------------------------------------------------------------------------------------------------------


//Just a helper class
// for example needed for simple instanceof checks....
public class PDEDictionary implements Serializable {

    /**
	 * @brief serial number for serialization 
	 */
	private static final long serialVersionUID = 4494615765124062014L;
	
	private HashMap<String,Object> mMap;

    public PDEDictionary()
    {
        super();
        mMap = new HashMap<String,Object>();
    }

    public PDEDictionary(Object... params)
    {
        super();

        mMap = new HashMap<String,Object>();

        // We always need pairs of Key and Value, so the number of params has to be even
        if (params.length%2 != 0 ){
            Log.e("PDEDictionary", "Number of parameters isn't even. We need pairs of keys and values here");
            return;
        }
        for(int i=0; i<params.length; i+=2){
            if(!(params[i] instanceof String)){
                Log.e("PDEDictionary", "Keys always have to be of type String!");
                return;
            }
            if(params[i+1]==null){
                Log.e("PDEDictionary", "Value mustn't be null!");
                return;
            }
            mMap.put(params[i].toString(), params[i + 1]);
        }
    }

    // copy's the hashmap-dictionary but keeps references to the containend objects -> should do the same like on ios!
    public PDEDictionary(Map<? extends String, ?> map)
    {
        super();

        mMap = new HashMap<String,Object>(map);

    }

    // copy's the hashmap-dictionary but keeps references to the containend objects -> should do the same like on ios!
    public PDEDictionary(PDEDictionary dict)
    {
        super();

        mMap = new HashMap<String,Object>(dict.getHashMap());

    }


    // copy's the hashmap-dictionary but keeps references to the containend objects -> should do the same like on ios!
    public PDEDictionary copy()
    {
        return new PDEDictionary(this);
    }

    /**
     * @brief add all entries of one PDEDictionary to the current one.
     * @param dictionary with the entries you want to add
     */
    public void addEntriesFromDictionary(PDEDictionary dictionary)
    {
        for (Map.Entry<String,Object> entry: dictionary.entrySet()) {
           this.put(entry.getKey(),entry.getValue());
        }
    }


    public Set<Map.Entry<String,Object>> entrySet() {
        return mMap.entrySet();
    }

    public Object put(String key, Object value) {
        return mMap.put(key, value);
    }

    public Object get(String key) {
        return mMap.get(key);
    }

    public void clear() {
        mMap.clear();
    }

    public Set<String> keySet() {
        return mMap.keySet();
    }

    public Object remove(String key) {
        return mMap.remove(key);
    }

    public boolean isEmpty() {
        return mMap.isEmpty();
    }

    public int size() {
        return mMap.size();
    }

    public boolean containsKey(String key) {
        return mMap.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return mMap.containsValue(value);
    }

    public Collection<Object> values() {
        return mMap.values();
    }

    public HashMap<String,Object> getHashMap () {
        return mMap;
    }
}
