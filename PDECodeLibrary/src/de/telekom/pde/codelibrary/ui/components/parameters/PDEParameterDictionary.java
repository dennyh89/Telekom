/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.parameters;


import de.telekom.pde.codelibrary.ui.color.PDEColor;

//----------------------------------------------------------------------------------------------------------------------
// PDEParameterDictionary
//----------------------------------------------------------------------------------------------------------------------


public class PDEParameterDictionary {

    private PDEDictionary mParameters;


    public PDEParameterDictionary() {
        mParameters = new PDEDictionary();
    }


    /**
     * @brief Create a copy.
     */
    public PDEParameterDictionary copy()
    {
        PDEParameterDictionary newDictionary = new PDEParameterDictionary();

        // copy by setting
        newDictionary.setParameterDictionary(this);

        // return
        return newDictionary;
    }


    public PDEDictionary getParameters(){
        return mParameters;
    }


//----- multiple parameter operations ----------------------------------------------------------------------------------


    /**
     * @brief Set the given parameter dictionary - completely overwrite the existing one.
     *
     * Make a true copy (copying the dictionary would only copy the keys, but keep references on
     * the values)
     */
    public void setParameterDictionary(PDEParameterDictionary parameterDict)
    {
        // clear old
        mParameters.clear();

        // add the new dictionary
        addParameterDictionary(parameterDict);
    }

    /**
     * @brief Add all parameters, replace existing parameters completely.
     *
     * Make copies of the parameters - we don't want to modify the parameters of another dict.
     */
    public void addParameterDictionary(PDEParameterDictionary parameterDict)
    {
        // go through all of them
        for (String key: parameterDict.getParameters().keySet()) {
            // merge them (make sure the right setParameter is called (otherwise we end up with a parameter in a parameter...)
            setParameter(key, (PDEParameter)parameterDict.getParameters().get(key));
        }
    }


    /**
     * @brief Merge in all parameters.
     */
    public void mergeParameterDictionary(PDEParameterDictionary parameterDict)
    {
        // go through all of them
        for (String key: parameterDict.getParameters().keySet()) {
            // merge them
            mergeParameter(key,(PDEParameter)parameterDict.getParameters().get(key));
        }
    }


//----- parameter setting ----------------------------------------------------------------------------------------------


    /**
     * @brief Set a single basic parameter (replace existing one).
     */
    public void setParameter(String name,String value)
    {
        PDEParameter param;

        // create the parameter
        param= PDEParameter.withString(value);

        // and set it
        mParameters.put(name,param);
    }


    /**
     * @brief Set a single basic parameter (replace existing one).
     */
    public void setParameter(String name, Object object)
    {
        PDEParameter param;

        // create the parameter
        param= PDEParameter.withObject(object);

        // and set it
        mParameters.put(name,param);
    }


    /**
     * @brief Set a single parameter (replace existing one).
     */
    public void setParameter(String name,PDEParameter parameter)
    {
        PDEParameter param;

        // create a copy of the parameter
        param= PDEParameter.withParameter(parameter).copy();

        // and set it
        mParameters.put(name,param);
    }


    /**
     * @brief Set a single parameter from a dictionary.
     */
    public void setParameter(String name,PDEDictionary dictionary)
    {
        PDEParameter param;

        // create the parameter
        param= PDEParameter.withDictionary(dictionary);

        // and set it
        mParameters.put(name,param);
    }


//----- parameter merging ----------------------------------------------------------------------------------------------


    /**
     * @brief Merge in the given subparameter.
     *
     * If no parameter is given, create a new one.
     */
    public void mergeParameter(String name,String value, String subKey)
    {
        PDEParameter param;

        // find or create the parameter
        param=parameterForName(name);
        if (param==null) {
            param = new PDEParameter();
            mParameters.put(name,param);
        }

        // and merge the value
        param.addValue(value,subKey);
    }


    /**
     * @brief Merge in the given subparameter.
     *
     * If no parameter is given, create a new one.
     */
    public void mergeParameter(String name,Object object,String subKey)
    {
        PDEParameter param;

        // find or create the parameter
        param=parameterForName(name);
        if (param==null) {
            param = new PDEParameter();
            mParameters.put(name,param);
        }

        // and merge the value
        param.addObject(object,subKey);
    }


    /**
     * @brief Merge in the parameter
     *
     * If no parameter is given, create a new one.
     */
    public void mergeParameter(String name,PDEParameter parameter)
    {
        PDEParameter param;

        // find or create the parameter
        param=parameterForName(name);
        if (param==null) {
            param = new PDEParameter();
            mParameters.put(name,param);
        }

        // and merge the value
        param.addObjectsWithParameter(parameter);
    }


    /**
     * @brief Merge in the dictionary for the parameter.
     *
     * If the parameter does not exist, create a new one.
     */
    public void mergeParameter(String name,PDEDictionary dictionary)
    {
        PDEParameter param;

        // find or create the parameter
        param=parameterForName(name);
        if (param==null) {
            param = new PDEParameter();
            mParameters.put(name,param);
        }

        // and merge the value
        param.addObjectsWithDictionary(dictionary);
    }


//----- parameter access -----------------------------------------------------------------------------------------------


    /**
     * @brief Retrieve the complete parameter data.
     *
     * @result The parameter or nil if it does not exist.
     */
    public PDEParameter parameterForName(String name)
    {
        // find the parameter
        return (PDEParameter)mParameters.get(name);
    }


//----- direct value access --------------------------------------------------------------------------------------------


    /**
     * @brief Retrieve the basic parameter value.
     */
    public String parameterValueForName(String name)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return null;

        // retrieve the basic value
        return param.getBaseValue();
    }


    /**
     * @brief Retrieve the basic parameter value.
     */
    public String parameterValueForNameWithDefault(String name,String defaultValue)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return defaultValue;

        // retrieve the basic value
        return param.getValueForKey("", defaultValue);
    }


    /**
     * @brief Retrieve the parameter value for given name and key.
     */
    public String parameterValueForNameAndKey(String name,String key)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return null;

        // retrieve the value
        return param.getValueForKey(key);
    }


    /**
     * @brief Retrieve the parameter value for given name and key.
     */
    public String parameterValueForNameAndyKeyWithDefault(String name,String key,String defaultValue)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return defaultValue;

        // retrieve the value
        return param.getValueForKey(key, defaultValue);
    }


    /**
     * @brief Retrieve the basic parameter value.
     */
    public Object parameterObjectForName(String name)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return null;

        // retrieve the basic value
        return param.getBaseObject();
    }


    /**
     * @brief Retrieve the basic parameter value.
     */
    public Object parameterObjectForName(String name,Object defaultValue)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return defaultValue;

        // retrieve the basic value
        return param.getObjectForKey("", defaultValue);
    }


    /**
     * @brief Retrieve the parameter value for given name and key.
     */
    public Object parameterObjectForName(String name,String key)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return null;

        // retrieve the value
        return param.getObjectForKey(key);
    }


    /**
     * @brief Retrieve the parameter value for given name and key.
     */
    public Object parameterObjectForName(String name,String key,Object defaultValue)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return defaultValue;

        // retrieve the value
        return param.getObjectForKey(key, defaultValue);
    }


    /**
     * @brief Retrieve the basic parameter value.
     */
    public Number parameterNumberForName(String name)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return null;

        // retrieve the basic value
        return param.getBaseNumber();
    }


    /**
     * @brief Retrieve the basic parameter value.
     */
    public Number parameterNumberForName(String name,Number defaultValue)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return defaultValue;

        // retrieve the basic value
        return param.getNumberForKey("", defaultValue);
    }


    /**
     * @brief Retrieve the parameter value for given name and key.
     */
    public Number parameterNumberForName(String name,String key)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return null;

        // retrieve the value
        return param.getNumberForKey(key);
    }


    /**
     * @brief Retrieve the parameter value for given name and key.
     */
    public Number parameterNumberForName(String name,String key,Number defaultValue)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return defaultValue;

        // retrieve the value
        return param.getNumberForKey(key, defaultValue);
    }


    /**
     * @brief Retrieve the basic parameter value.
     */
    public PDEColor parameterColorForName(String name)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return null;

        // retrieve the basic value
        return param.getBaseColor();
    }


    /**
     * @brief Retrieve the basic parameter value.
     */
    public PDEColor parameterColorForName(String name, PDEColor defaultValue)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return defaultValue;

        // retrieve the basic value
        return param.getColorForKey("", defaultValue);
    }


    /**
     * @brief Retrieve the parameter value for given name and key.
     */
    public PDEColor parameterColorForName(String name,String key)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return null;

        // retrieve the value
        return param.getColorForKey(key);
    }


    /**
     * @brief Retrieve the parameter value for given name and key.
     */
    public PDEColor parameterColorForName(String name,String key, PDEColor defaultValue)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return defaultValue;

        // retrieve the value
        return param.getColorForKey(key, defaultValue);
    }


/**
 * @brief Retrieve the basic parameter value.
 */
    public boolean parameterBoolForName(String name)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param == null) return false;

        // retrieve the basic value
        return param.getBaseBool();
    }


/**
 * @brief Retrieve the basic parameter value.
 */
    public boolean parameterBoolForName(String name, boolean defaultValue)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param == null) return defaultValue;

        // retrieve the basic value
        return param.getBoolForKey("", defaultValue);
    }


/**
 * @brief Retrieve the parameter value for given name and key.
 */
    public boolean parameterBoolForName(String name, String key)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param == null) return false;

        // retrieve the value
        return param.getBoolForKey(key);
    }


/**
 * @brief Retrieve the parameter value for given name and key.
 */
    public boolean parameterBoolForName(String name, String key, boolean defaultValue)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param == null) return defaultValue;

        // retrieve the value
        return param.getBoolForKey(key, defaultValue);
    }



    /**
     * @brief Retrieve the basic parameter value.
     */
    public float parameterFloatForName(String name)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return 0.0f;

        // retrieve the basic value
        return param.getBaseFloat();
    }


    /**
     * @brief Retrieve the basic parameter value.
     */
    public float parameterFloatForName(String name,float defaultValue)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return defaultValue;

        // retrieve the basic value
        return param.getFloatForKey("", defaultValue);
    }


    /**
     * @brief Retrieve the parameter value for given name and key.
     */
    public float parameterFloatForName(String name,String key)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return 0.0f;

        // retrieve the value
        return param.getFloatForKey(key);
    }


    /**
     * @brief Retrieve the parameter value for given name and key.
     */
    public float parameterFloatForName(String name,String key,float defaultValue)
    {
        PDEParameter param;

        // seek the param
        param = parameterForName(name);
        if (param==null) return defaultValue;

        // retrieve the value
        return param.getFloatForKey(key, defaultValue);
    }


//----- comparisions ---------------------------------------------------------------------------------------------------


    /**
     * @brief Compare the two parameters.
     *
     * They are not equal if one exists, and the other doesn't.
     */
    public boolean isEqual(PDEParameterDictionary parameters, String parameterName)
    {
        PDEParameter param1;
        PDEParameter param2;

        // get both parameters
        param1 = parameterForName(parameterName);
        param2 = parameters.parameterForName(parameterName);

        // if both don't exist, they count as equal
        if (param1==null && param2==null) return true;

        // if only one doesn't exist, they are not equal
        if (param1==null || param2==null) return false;

        // let them check for themself
        return param1.isEqual(param2);
    }


//----- class helpers --------------------------------------------------------------------------------------------------


    /**
     * @brief Comparison helper function, also works if one of the dictionaries is not defined.
     */
    public static boolean areParametersEqual(PDEParameterDictionary dictionary1,PDEParameterDictionary dictionary2,String parameterName)
    {
        PDEParameter param;

        // if both don't exist, they count as equal
        if (dictionary1==null && dictionary2==null) return true;

        // if only one doesn't exist, check if the parameter exists in the other dictionary
        if (dictionary1==null) {
            param = dictionary2.parameterForName(parameterName);
            if (param!=null)
                return false;
            else return true;
        }
        if (dictionary2==null) {
            param = dictionary1.parameterForName(parameterName);
            if (param!=null)
                return false;
            else return true;
        }

        // let them check for themself
        return dictionary1.isEqual(dictionary2,parameterName);
    }
}
