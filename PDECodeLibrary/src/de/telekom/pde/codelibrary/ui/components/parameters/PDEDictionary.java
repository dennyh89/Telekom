/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.parameters;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

//Just a helper class
// for example needed for simple instanceof checks....
public class PDEDictionary extends HashMap<String,Object> {

    public PDEDictionary()
    {
        super();
    }

    public PDEDictionary(Object... params)
    {
        super();
        // We always need pairs of Key and Value, so the number of params has to be even
        if (params.length%2 != 0 ){
            Log.e("PDEDictionary", "Number of parameters isn't even. We need pairs of keys and values here");
            return;
        }
        for(int i=0; i<params.length;i+=2){
            if(!(params[i] instanceof String)){
                Log.e("PDEDictionary", "Keys always have to be of type String!");
                return;
            }
            if(params[i+1]==null){
                Log.e("PDEDictionary", "Value mustn't be null!");
                return;
            }
            put(params[i].toString(),params[i+1]);
        }
    }

	//TODO check this complete!!!
    // copy's the hashmap-dictionary but keeps references to the containend objects -> should do the same like on ios!
    public PDEDictionary(Map<? extends String, ? extends Object> map)
    {
        super(map);
    }


    //TODO check this complete!!!
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
        for (PDEDictionary.Entry<String,Object> entry: dictionary.entrySet()) {
           this.put(entry.getKey(),entry.getValue());
        }
    }
}
