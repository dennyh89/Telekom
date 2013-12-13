/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.helpers.parameters;

//----------------------------------------------------------------------------------------------------------------------
//  PDEParameter private helper classes
//----------------------------------------------------------------------------------------------------------------------

import android.graphics.PointF;
import android.text.TextUtils;
import android.util.Log;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.helpers.PDEDictionary;

import java.util.*;

public class PDEParameter {

    private class PDEParameterMetaInformation {
        private boolean mBaseValue;
        private int mNumValues;


        private PDEParameterMetaInformation() {
            // init
            mBaseValue = false;
            mNumValues = 0;
        }

        public void setBaseValue(boolean baseValue){
            mBaseValue = baseValue;
        }


        public boolean hasBaseValue(){
            return mBaseValue;
        }

        public void setNumValues(int numValues){
            mNumValues = numValues;
        }

        public int getNumValues() {
            return mNumValues;
        }

        public PDEParameterMetaInformation copy(){
            PDEParameterMetaInformation copy = new PDEParameterMetaInformation();

            copy.setBaseValue(this.mBaseValue);
            copy.setNumValues(this.mNumValues);

            return copy;
        }
    }

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEParameter.class.getName();

    private PDEDictionary mParameters;
    private PDEDictionary mMetaInformation;
    private boolean mBase;

    public PDEParameter() {
        mParameters = new PDEDictionary();
        mMetaInformation = new PDEDictionary();
        mBase = false;
    }


    /**
     * @brief Create a copy.
     */
    public PDEParameter copy()
    {
        PDEParameter newParameter = new PDEParameter();

        // copy by setting
        newParameter.setWithParameter(this);

        // return
        return newParameter;
    }


//----- class functions ------------------------------------------------------------------------------------------------


    /**
     * @brief Create a parameter with given main value.
     */
    public static PDEParameter withString(String value)
    {
        PDEParameter newParameter = new PDEParameter();

        // set data
        newParameter.setBaseValue(value);

        // done
        return newParameter;
    }


    /**
     * @brief Create a parameter with given main object.
     */
    public static PDEParameter withObject(Object object)
    {
        PDEParameter newParameter = new PDEParameter();

        // set data
        newParameter.setBaseObject(object);

        // done
        return newParameter;
    }


    /**
     * @brief Create a parameter with given main value.
     */
    public static PDEParameter withParameter(PDEParameter parameter)
    {
        PDEParameter newParameter = new PDEParameter();

        // set data
        newParameter.setWithParameter(parameter);

        // done
        return newParameter;
    }


    /**
     * @brief Create a parameter with given main value.
     */
    public static PDEParameter withDictionary(PDEDictionary dictionary)
    {
        PDEParameter newParameter = new PDEParameter();

        // set data
        newParameter.setWithDictionary(dictionary);

        // done
        return newParameter;
    }


//----- parameter setting -----------------------------------------------------------------------------------------


    public PDEDictionary getParameters() {
        return mParameters;
    }

    public PDEDictionary getMetaInformation() {
        return mMetaInformation;
    }

    /**
     * @brief Set the main value (the main value has an empty key).
     *
     * All other values are deleted.
     */
    public void setBaseValue(String value)
    {
        // clear dictionary
        removeAllObjects();

        // merge in the value for an empty key
        addValue(value,"");
    }


    /**
     * @brief Set the main object (the main object has an empty key).
     *
     * All other values are deleted.
     */
    public void setBaseObject(Object object)
    {
        // clear dictionary
        removeAllObjects();

        // add the value as the empty key.
        addObject(object,"");
    }


    /**
     * @brief Set the given parameter. Overwrites all existing parameters.
     */
    public void setWithParameter(PDEParameter parameter)
    {
        // clear everything
        removeAllObjects();

        // safety (helps when setting nonexistent parameters)
        if (parameter==null) return;

        // create mutable copy of parameters (the objects stored are unchanged)
        mParameters = parameter.getParameters().copy();

        // create new metadata by copying each object (the metadata itself needs to stay mutable)
        for ( String key: parameter.getMetaInformation().keySet() ) {
            //copy the object and add to dictionary
            mMetaInformation.put(key, ((PDEParameterMetaInformation) parameter.getMetaInformation().get(key)).copy());
        }

        // and copy the rest of the plain meta information
        mBase = parameter.mBase;
    }


    /**
     * @brief Sets the given dictionary. Overwrites all existing parameters.
     */
    public void setWithDictionary(PDEDictionary dictionary)
    {
        // clear everything
        removeAllObjects();

        // add the dictionary (we need to adjust the metainformation, we cannot simply copied)
        addObjectsWithDictionary(dictionary);
    }


//----- paremeter adding -----------------------------------------------------------------------------------------------


    /**
     * @brief Add the value. If it already exists, overwrite it.
     */
    public void addValue(String value,String key)
    {
        // simply add the string as object
        addObject(value,key);
    }


    /**
     * @brief Add the object. If the object already exists, overwrite it.
     *
     * If nil is passed for the object, the key is removed.
     */
    public void addObject(Object object,String key)
    {
        String state;
        PDEParameterMetaInformation meta;

        // safety (adds when adding nonexistent parameters)
        if (key==null) return;

        // delete
        if (object==null) {
            removeObjectForKey(key);
            //TODO Thomas is the return here right (otherwise we an exception will be thrown in setObject:nil ... later on)
            return;
        }

        // check if it's a new key. If so, we need to do meta information housekeeping
        if ( mParameters.get(key)==null ) {
            // empty key?
            if (key.length() == 0) {
                // base object gets simplified treatment.
                mBase = true;
            } else {
                // determine the state
                state = extractStateFromKey(key);
                // find it in metadata list
                meta = (PDEParameterMetaInformation)mMetaInformation.get(state);
                // create if not already present
                if (meta==null) {
                    meta =  new PDEParameterMetaInformation();
                    mMetaInformation.put(state,meta);
                }
                // count the new key
                meta.setNumValues(meta.getNumValues() + 1);
                // if it's the state's base value, remember this
                if (state.length() == key.length()) {
                    meta.setBaseValue(true);
                }
            }
        }

        // now set it in the dictionary
        mParameters.put(key,object);
    }


    /**
     * @brief All all values. Existing values get overwritten.
     */
    public void addObjectsWithParameter(PDEParameter parameter)
    {
        // safety (adds when adding nonexistent parameters)
        if (parameter==null) return;

        // add values individually; we need to do metadata housekeeping.
        for (String key: parameter.getParameters().keySet()) {
            addObject(parameter.getParameters().get(key),key);
        }
    }


    /**
     * @brief Merge in all values. Existing values get overwritten.
     */
    public void addObjectsWithDictionary(PDEDictionary dictionary)
    {
        // safety (adds when adding nonexistent parameters)
        if (dictionary==null) return;

        // add values individually; we need to do metadata housekeeping.
        for (String key: dictionary.keySet()) {
             addObject(dictionary.get(key),key);
        }
    }


//----- paremeter removal ----------------------------------------------------------------------------------------------


    /**
     * @brief Completely clear everything
     */
    public void removeAllObjects()
    {
        // clear parameters
        mParameters.clear();

        // clear metadata
        mMetaInformation.clear();
        mBase = false;
    }


    /**
     * @brief Remove the key.
     */
    public void removeObjectForKey(String key)
    {
        String state;
        PDEParameterMetaInformation meta;

        // safety
        if (key==null) return;

        // do nothing if we don't know the key
        if ( mParameters.get(key)==null ){
            return;
        }

        // now remove it from meta-information
        if (key.length() == 0) {
            // base object gets special treatment
            mBase = false;
        } else {
            // extract the state
            state = extractStateFromKey(key);
            // find metadata (should be present, but do a safety check anyway]
            meta = (PDEParameterMetaInformation)mMetaInformation.get(state);
            if (meta!=null) {
                // count down
                meta.setNumValues(meta.getNumValues() - 1);
                // set state base status
                if (state.length() == key.length()) {
                    meta.setBaseValue(false);
                }
                // forget this state if we no longer have any of it's keys.
                if (meta.getNumValues() == 0) {
                    mMetaInformation.remove(state);
                }
            }
        }

        // remove from dictionary
        mParameters.remove(key);
    }


//----- special operations ---------------------------------------------------------------------------------------------


    /**
     * @brief Add the dictionary to the parameters; the dictionary contains no state information.
     *
     * The given state is prefixed before all dictionary entries before adding them.
     */
    public void addObjectsWithDictionaryForState(PDEDictionary dictionary, String state)
    {
        String prefix;
        String stateKey;

        // safety (adds when adding nonexistent parameters)
        if (dictionary==null) return;

        // build the prefix (state + dot)
        prefix = state + ".";

        // add values individually; we need to do metadata housekeeping.
        for (String key: dictionary.keySet()){
            // build new key
            if (TextUtils.isEmpty(key)) {
                // state default: use the state as key
                stateKey = state;
            } else {
                // prefix
                stateKey = prefix + key;
            }
            // and add it
            addObject(dictionary.get(key),stateKey);
        }
    }


//----- meta information retrieval -------------------------------------------------------------------------------------


    /**
     * @brief Check if we have any values at all.
     */
    public boolean isEmpty()
    {
        return mParameters.isEmpty();
    }


    /**
     * @brief A parameter is simple if it contains exactly one value, and this value is for the empty key.
     */
    public boolean isSimple()
    {
        // must have at most one object
        if ( mParameters.size() != 1) return false;

        // and have the base object
        if (!hasBase()) return false;

        // it's simple
        return true;
    }


    /**
     * @brief Simple check if the base object exists.
     */
    public boolean hasBase()
    {
        // access meta information directly
        return mBase;
    }


    /**
     * @brief Return a list of states used by our keys.
     */
    public Set<String> states()
    {
        // the states are listed as keys in the meta information storage
        return mMetaInformation.keySet();
    }


    /**
     * @brief Check if the requested state exists.
     */
    public boolean hasState(String state)
    {
        // check the keys of the meta information storage
        return (mMetaInformation.get(state)!=null );
    }


    /**
     * @brief Check if a state is simple: It has exactly one value, and this is the base value.
     */
    public boolean isStateSimple(String state)
    {
        PDEParameterMetaInformation meta;

        // get meta information
        meta = (PDEParameterMetaInformation)mMetaInformation.get(state);

        // no meta? -> cannot be simple
        if (meta==null) return false;

        // check count and base value
        if (meta.getNumValues() != 1) return false;

        if (!meta.hasBaseValue()) return false;

        // it's simple
        return true;
    }


    /**
     * @brief Check if a state has a base value.
     */
    @SuppressWarnings("unused")
    public boolean hasStateBase(String state)
    {
        PDEParameterMetaInformation meta;

        // get meta information
        meta = (PDEParameterMetaInformation)mMetaInformation.get(state);

        // no meta? -> no base value
        if (meta==null) return false;

        // return state of base value
        return meta.hasBaseValue();
    }


    /**
     * @brief Check if a state has a base value.
     */
    @SuppressWarnings("unused")
    public int countStateKeys(String state)
    {
        PDEParameterMetaInformation meta;

        // get meta information
        meta = (PDEParameterMetaInformation)mMetaInformation.get(state);

        // no meta? -> keys
        if (meta==null) return 0;

        // return state of base value
        return meta.getNumValues();
    }


//----- value access ---------------------------------------------------------------------------------------------------

    /**
     * @brief Retrieve the main value (empty string for key).
     *
     * @return The parameter value for the empty key. Or nil if there's no empty key.
     */
    public String getBaseValue()
    {
        Object stringObj;

        stringObj = mParameters.get("");
        if (stringObj instanceof String){
            return (String)stringObj;
        } else {
            return null;
        }
    }


    /**
     * @brief Retrieve the specified value string.
     *
     * @param key The key for the value to be retrieved.
     * @return The parameter value string for the given key. Or nil if there's no empty key set.
     */
    public String getValueForKey(String key)
    {
        Object stringObj;

        stringObj = mParameters.get(key);
        if (stringObj instanceof String){
            return (String)stringObj;
        } else {
            return null;
        }
    }


    /**
     * @brief Get the value string for the given key or the default if not present.
     */
    public String getValueForKey(String key, String defaultValue)
    {
        String value;

        // get it
        value = getValueForKey(key);

        // return default if not available
        if (value == null) return defaultValue;

        // done
        return value;
    }


    /**
     * @brief Retrieve the main object (empty string for key).
     *
     * @return  The parameter object for the empty key. Or nil if there's no empty key.
     */
    public Object getBaseObject()
    {
        return mParameters.get("");
    }


    /**
     * @brief Retrieve the specified object.
     *
     * @param key The key for the value to be retrieved.
     * @return The parameter object for the given subkey. Or nil if there's no empty key set.
     */
    public Object getObjectForKey(String key)
    {
        return mParameters.get(key);
    }


    /**
     * @brief Get the object for the given key or the default if not present.
     */
    public Object getObjectForKey(String key, Object defaultObject)
    {
        Object object;

        // get it
        object = getObjectForKey(key);

        // return default if not available
        if (object==null) return defaultObject;

        // done
        return object;
    }


//----- typed access and type conversions ------------------------------------------------------------------------------

    /**
     * @brief Get the base value as NSNumber.
     */
    public Number getBaseNumber()
    {
        return getNumberForKey("");
    }


    /**
     * @brief Get the value as NSNumber for the given key.
     *
     * @return The NSNumber or nil if not found or not convertible.
     */
    public Number getNumberForKey(String key)
    {
        Object object;

        // get it
        object = getObjectForKey(key);

        // try to convert
        if ( object instanceof  Number ) {
            // already a number
            return (Number)object;
        } else if ( object instanceof  String ) {
            return Float.valueOf((String)object);
        } else {
            // unknown type
            return null;
        }
    }


    /**
     * @brief Get the value as NSNumber for the given key with fallback if not present.
     *
     * @return The NSNumber or the default if not found or not convertible.
     */
    public Number getNumberForKey(String key, Number defaultNumber)
    {
        Number number;

        // get it
        number = getNumberForKey(key);

        // return default if not available
        if (number==null) return defaultNumber;

        // done
        return number;
    }


    /**
     * @brief Get the base value as UIColor.
     */
    public PDEColor getBaseColor()
    {
        return getColorForKey("");
    }


/**
 * @brief Get the value as UIColor for the given key.
 *
 * @return The UIColor or nil if not found or not convertible.
 */
    public PDEColor getColorForKey(String key)
    {
        Object object;

        // get it
        object = getObjectForKey(key);

        // try to convert
        if ( object instanceof PDEColor) {
            // already a color
            return (PDEColor)object;
        } else if ( object instanceof String ) {
            // convert to color
            return PDEColor.valueOf((String) object);
        } else {
            // unsupported type
            return null;
        }
    }


    /**
     * @brief Get the value as UIColor for the given key with fallback if not present.
     *
     * @return The UIColor or the default if not found or not convertible.
     */
    public PDEColor getColorForKey(String key, PDEColor defaultColor)
    {
        PDEColor color;

        // get it
        color = getColorForKey(key);

        // return default if not available
        if (color==null) return defaultColor;

        // done
        return color;
    }


    /**
     * @brief Get the base value as float.
     */
    public boolean getBaseBool()
    {
        // get via extended function
        return getBoolForKey("", false);
    }


    /**
     * @brief Get the value as float for the given key.
     *
     * @return The float or 0.0f if not found or not convertible.
     */
    public boolean getBoolForKey(String key)
    {
        // get via extended function
        return getBoolForKey(key, false);
    }


    /**
     * @brief Get the value as float for the given key with fallback if not present.
     *
     * @return The float or the default if not found or not convertible.
     */
    public boolean getBoolForKey(String key, boolean defaultValue)
    {
        Object object;

        // get it
        object = getObjectForKey(key);

        // try to convert
        if ( object instanceof Boolean ) {
            // already a value, retrieve BOOL
            return (Boolean)object;
        } else if ( object instanceof String ) {
            // convert string
            if ( object.equals("0") ) return false;
            else if ( ((String) object).equalsIgnoreCase("false") ) return false;
            else if ( ((String) object).equalsIgnoreCase("NO") ) return false;
            else if ( ((String) object).equalsIgnoreCase("1") ) return true;
            else if ( ((String) object).equalsIgnoreCase("true") ) return true;
            else if ( ((String) object).equalsIgnoreCase("YES") ) return true;
            else return defaultValue;
        } else {
            // unknown type
            return defaultValue;
        }
    }

    /**
     * @brief Get the base value as float.
     */
    public float getBaseFloat()
    {
        return getFloatForKey("");
    }


    /**
     * @brief Get the value as float for the given key.
     *
     * @return The float or 0.0f if not found or not convertible.
     */
    public float getFloatForKey(String key)
    {
        Number number;

        // get it
        number = getNumberForKey(key);

        // valid?
        if (number==null) return 0.0f;

        // retrieve the value
        return number.floatValue();
    }


    /**
     * @brief Get the value as float for the given key with fallback if not present.
     *
     * @return The float or the default if not found or not convertible.
     */
    public float getFloatForKey(String key, float defaultFloat)
    {
        Number number;

        // get it
        number = getNumberForKey(key);

        // valid?
        if (number==null) return defaultFloat;

        // retrieve the value
        return number.floatValue();
    }


    /**
     * @brief Get the base value as UIColor.
     */
    @SuppressWarnings("unused")
    public PointF getBasePosition()
    {
        return getPositionForKey("");
    }


    /**
     * @brief Get the value as CGPoint for the given key.
     *
     * @return The CGPoint 0.0f/0.0f if not found or not convertible.
     */
    public PointF getPositionForKey(String key)
    {
        // use function with default
        return getPositionForKey(key, new PointF(0.0f, 0.0f));
    }


    /**
     * @brief Get the value as float for the given key with fallback if not present.
     *
     * @return The NSNumber or the default if not found or not convertible.
     */
    public PointF getPositionForKey(String key, PointF defaultValue)
    {
        Object object;

        // get it
        object = getObjectForKey(key);

        // try to convert
        if ( object instanceof PointF ) {
            // already a value, retrieve CGPoint
            return (PointF)object;
        } else if ( object instanceof String) {
            // convert to position
            Log.e(LOG_TAG, "IMPLEMENT FUNCTION TO PARSE STRING TO POINTF!!!!!!!!!!!!");
            //todo implement funciton to parse string to pointF
            //return CGPointFromString ([NSString stringWithFormat:@"{%@}",object]);
            return new PointF(0.0f,0.0f);
        } else {
            // unknown type
            return defaultValue;
        }
    }


//----- type conversions -----------------------------------------------------------------------------------------------


    /**
     * @brief Convert all string entries of the parameter to NSNumber (if they are not already so).
     *
     * Default behaviour is to remove all non convertible entries.
     */
    public void convertToNumber()
    {
        convertToNumber(true);
    }


    /**
     * @brief Convert all string entries of the parameter to NSNumber (if they are not already so).
     *
     * Non convertibles are removed if requested
     */
    public void convertToNumber(boolean removeNonConvertibles)
    {
        Number number;
        Object object;

        // walk all subkeys (note we enumerate a copy of the keys, to be able to modify the dictionary)
        for ( String key: mParameters.keySet() ) {
            // get associated object
            object = getObjectForKey(key);
            // action depending on type
            if ( object instanceof  String ) {
                // convert to number
                number = Float.valueOf((String)object);
                // replace (if the number is nil, it gets removed)
                if (number!=null) {
                    addObject(number,key);
                } else {
                    removeObjectForKey(key);
                }
            } else if ( object instanceof Number ) {
                // already correct
                continue;
            } else if (removeNonConvertibles) {
                // remove nonfitting if requested
                removeObjectForKey(key);
            }
        }
    }


    /**
     * @brief Convert all string entries of the parameter to NSNumber/BOOL (if they are not already so).
     *
     * Default behaviour is to remove all non convertible entries.
     */
    @SuppressWarnings("unused")
    public void convertToBool()
    {
        convertToBool(true);
    }


    /**
     * @brief Convert all string entries of the parameter to NSNumber/BOOL (if they are not already so).
     *
     * Non convertibles are removed if requested. Strings recognized are 0,1,YES,NO,TRUE,FALSE; regardless of case.
     */
    public void convertToBool(boolean removeNonConvertibles)
    {
        boolean value=false;
        boolean valid=false;
        Object object;

        // walk all sub keys (note we enumerate a copy of the keys, to be able to modify the dictionary)
        for (String key: mParameters.keySet() ) {
            // get associated object
            object = getObjectForKey(key);
            // action depending on type
            if ( object instanceof String) {
                // check well known strings
                if ( ((String) object).equalsIgnoreCase("0")) {value=false; valid=true;}
                else if ( ((String) object).equalsIgnoreCase("false")) {value=false; valid=true;}
                else if ( ((String) object).equalsIgnoreCase("1")) {value=true; valid=true;}
                else if ( ((String) object).equalsIgnoreCase("true")) {value=true; valid=true;}
                else {valid=false;}
                // replace (if the number is nil, it gets removed)
                if (valid) {
                    addObject(value,key);
                } else {
                    removeObjectForKey(key);
                }
            } else if ( object instanceof Boolean ) {
                // already correct
                continue;
            } else if (removeNonConvertibles) {
                // remove non fitting if requested
                removeObjectForKey(key);
            }
        }
    }


    /**
     * @brief Convert all string entries of the parameter to UIColor (if they are not already so).
     *
     * Default behaviour is to remove all non convertible entries.
     */
    public void convertToColor()
    {
        convertToColor(true);
    }


    /**
     * @brief Convert all string entries of the parameter to UIColor (if they are not already so).
     *
     * Non-Strings values are kept as they are. Non-convertible colors are removed.
     */
    public void convertToColor(boolean removeNonConvertibles)
    {
        PDEColor color;
        Object object;

        // walk all subkeys (note we enumerate a copy of the keys, to be able to modify the dictionary)
        for ( String key: mParameters.keySet() ) {
            // get associated object
            object = getObjectForKey(key);
            // can we convert it?
            if ( object instanceof String ) {
                // convert to color
                color = PDEColor.valueOf((String) object);
                // replace (the returned color is always valid)
                addObject(color,key);
            } else if ( object instanceof PDEColor) {
                // already correct
                continue;
            } else if (removeNonConvertibles) {
                // remove nonfitting if requested
                removeObjectForKey(key);
            }
        }
    }


    /**
     * @brief Convert all string entries of the parameter to NSValue/CGPoint (if they are not already so).
     *
     * Default behaviour is to remove all non convertible entries.
     */
    @SuppressWarnings("unused")
    public void convertToPosition()
    {
        convertToPosition(true);
    }


    /**
     * @brief Convert all string entries of the parameter to NSValue/CGPoint (if they are not already so).
     *
     * Non-Strings values are kept as they are. Non-convertible colors are removed.
     */
    public void convertToPosition(boolean removeNonConvertibles)
    {
        PointF position;
        Object object;

        // walk all subkeys (note we enumerate a copy of the keys, to be able to modify the dictionary)
        for (String key: mParameters.keySet()) {
            // get associated object
            object = getObjectForKey(key);
            // can we convert it?
            if ( object instanceof String ) {
                // convert to position
                Log.e(LOG_TAG, "IMPLEMENT FUNCTION TO PARSE STRING TO POINTF!!!!!!!!!!!!");
                //todo implement funciton to parse string to pointF
                // convert to point (currently using an iOS class, use our own parser once it's ready)
                //position = CGPointFromString ([NSString stringWithFormat:@"{%@}",object]);
                position = new PointF(0.0f,0.0f);
                // replace (the returned color is always valid)
                addObject(position,key);
            } else if (object instanceof PointF) {
                // already correct
                continue;
            } else if (removeNonConvertibles) {
                // remove nonfitting if requested
                removeObjectForKey(key);
            }
        }
    }



//----- comparison -----------------------------------------------------------------------------------------------------


    /**
     * @brief Compare by directly comparing the dictionaries.
     *
     * Meta information is built while building the dictionaries, so if they are equal, metainformation is also equal.
     * This function relies on Objective Cs dictionary comparison routines.
     */
    public boolean isEqual(PDEParameter parameter)
    {
        return mParameters.equals(parameter.getParameters());
    }


//----- helpers & debugging --------------------------------------------------------------------------------------------


    /**
     * @brief Extract the state from a key.
     *
     * Key definition is "<state>.<rest>" (the rest can be further subdevided, but that's not of interest at this level).
     */
    private String extractStateFromKey(String key)
    {
        int pos;

        if( TextUtils.isEmpty(key)) return key;

        // find the dot
        pos = key.indexOf(".");

        // found?
        if (pos==-1) {
            // no dot -> return the whole string (it's a state base)
            return key;
        }

        // split off state
        return key.substring(0,pos);
    }


    /**
     * @brief Debug output.
     */
    public void debugOut(String title)
    {
        List<String> keys = new ArrayList<String>();
        Object object;

        // header
        Log.d(LOG_TAG, "Parameter '" + title +"' (" + mParameters.size() + " entries):" );

        // get keys and sort them for output
        //TODO check if this really copys the Dictionary key set, objects are untouched so this should work
        keys.addAll( new HashSet<String>(mParameters.keySet()));


        Collections.sort(keys);

        // content (sorted)
        for (String key : keys) {
            // get objects
            object = getObjectForKey(key);
            // debugout depending on type
            if ( object instanceof PDEColor) {
                // color specific output
                Log.d(LOG_TAG ,"    '" + key + "%' := "+((PDEColor) object).debug());
            } else {
                // default output
                Log.d(LOG_TAG, "    '"+key+"' := '" + object + "'");
            }
        }
    }

    @SuppressWarnings("unused")
    public static
    <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }

}
