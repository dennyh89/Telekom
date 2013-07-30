/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */


//
// DT commonly used helper functions for parameters. Really specific, but often used, so located here.
//

package de.telekom.pde.codelibrary.ui.components.helpers;


import android.content.Context;
import android.graphics.PointF;
import android.text.TextUtils;
import android.util.Log;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEDictionary;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

//----------------------------------------------------------------------------------------------------------------------
//  DTParameterHelpers
//----------------------------------------------------------------------------------------------------------------------

public class PDEComponentHelpers {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEComponentHelpers.class.getName();

    // debug configurations
    //
    private final static boolean DEBUG = false;
    private final static boolean DEBUGPRESETS = DEBUG;
    private final static boolean PERFORMANCECHECK = DEBUG;


    /**
     * @brief Calculate state base values.
     *
     * After this function, every state must have a base value assigned, if nothing is defined, we take the main value.
     * or we take over the main value. Only existing states are calculated, with the exception of the default state,
     * which is always created.
     *
     * If no main values or default values can be found, the property values are unchanged. No default state is added.
     */
    public static void fillStateBaseValues(PDEParameter parameter, Object externalDefault)
    {
        Object defaultObject;

        // get the main value
        defaultObject = parameter.getBaseObject();

        // if there is no main object, try to use the default state object
        if (defaultObject==null) {
            defaultObject = parameter.getObjectForKey(PDEButton.PDEButtonStateDefault);
        }

        // if there is still no main object, try if we have a default object for the idle agent state
        if (defaultObject==null) {
            defaultObject = parameter.getObjectForKey(PDEButton.PDEButtonStateDefaultIdle);
        }

        // if we can't find one, use the supplied default
        if (defaultObject==null) {
            defaultObject = externalDefault;
        }

        // if nothing is supplied, we stop here
        if (defaultObject==null) return;

        // The "default" state is always included in the list, define it if it's not already defined
        if ( parameter.getObjectForKey(PDEButton.PDEButtonStateDefault)==null) {
            // create default state with default object
            parameter.addObject(defaultObject, PDEButton.PDEButtonStateDefault);
        }

        // now set the default object to all known states
        for (String state: parameter.states()) {
            // if no default object exists, set it
            if (parameter.getObjectForKey(state)==null) {
                // create default state key with base key
                parameter.addObject(defaultObject,state);
            }
        }
    }


    /**
     * @brief Calculate missing agent state colors for all given states.
     */
    public static void fillAgentStates(PDEParameter parameter,int animation)
    {
        // go trough all states of the parameter
        for (String state: parameter.states()) {
            // calculate for a single state
            fillAgentStates(parameter,animation,state);
        }
    }


    /**
     * @brief Agent state fillup. Only propagates values.
     */
    public static void fillAgentStates(PDEParameter parameter,int animation,String state)
    {
        Object object,next;
        String key;

        // don't propagate at all if we have a state only animation
        if (animation == PDEAgentHelper.PDEAgentHelperAnimationStateOnly) return;

        // get startout object -> prioritized order where to take the starting object from. If we can't find
        // a starting object assume nothing until we find one.
        object = parameter.getObjectForKey(state);
        if (object==null) object = parameter.getObjectForKey(PDEButton.PDEButtonStateDefault);
        if (object==null) object = parameter.getBaseObject();
        if (object==null) object = parameter.getObjectForKey(PDEButton.PDEButtonStateDefaultIdle);

        // check our state's idle color, set to previous color
        key = state + "." + PDEButton.PDEButtonAgentStateIdle;
        next = parameter.getObjectForKey(key);
        if (next==null && object!=null) {
            // not defined and can be defined? -> set use the current color
            next = object;
            parameter.addObject(next,key);
        }
        object = next;

        // further propagation depends on type of animation
        if (animation == PDEAgentHelper.PDEAgentHelperAnimationInteractive) {

            // check our state's focus value
            key = state+ "." + PDEButton.PDEButtonAgentStateFocus;
            next = parameter.getObjectForKey(key);
            if (next==null && object!=null) {
                // not defined and can be defined? -> propagate value
                next = object;
                parameter.addObject(next,key);
            }
            object = next;

            // check our state's taking input value
            key = state + "." + PDEButton.PDEButtonAgentStateTakingInput;
            next = parameter.getObjectForKey(key);
            if (next==null && object!=null) {
                // not defined and can be defined? -> propagate value
                next = object;
                parameter.addObject(next,key);
            }

        } else if (animation == PDEAgentHelper.PDEAgentHelperAnimationDown) {

            // check our state's down value
            key = state + "." + PDEButton.PDEButtonAgentStateDown;
            next = parameter.getObjectForKey(key);
            if (next==null && object!=null) {
                // not defined and can be defined? -> propagate value
                next = object;
                parameter.addObject(next,key);
            }

        }
    }


//----- basic color modification functions -----------------------------------------------------------------------------


    /**
     * @brief Calculate missing agent state colors for all given states.
     */
    public static void fillAgentStateColors(PDEParameter parameter,int animation)
    {
        // go trough all states of the parameter
        for (String state: parameter.states()) {
            // calculate for a single state
            fillAgentStateColors(parameter,animation,state);
        }
    }


    /**
     * @brief Agent state calculation for single state.
     */
    public static void fillAgentStateColors(PDEParameter parameter,int animation, String state)
    {
        PDEColor color;
        PDEColor next;
        String key;

        // don't propagate at all if we have a state only animation
        if (animation == PDEAgentHelper.PDEAgentHelperAnimationStateOnly) return;

        // get startout color -> prioritized order where to take the starting color from. If we can't find
        // a starting color assume nothing until we find one.
        color = (PDEColor)parameter.getObjectForKey(state);
        if (color==null) color = (PDEColor)parameter.getObjectForKey(PDEButton.PDEButtonStateDefault);
        if (color==null) color = (PDEColor)parameter.getBaseObject();
        if (color==null) color = (PDEColor)parameter.getObjectForKey(PDEButton.PDEButtonStateDefaultIdle);

        // check our state's idle color, set to previous color
        key = state + "." + PDEButton.PDEButtonAgentStateIdle;
        next = (PDEColor)parameter.getObjectForKey(key);
        if (next==null && color!=null) {
            // not defined and can be defined? -> set use the current color
            next = color;
            parameter.addObject(next,key);
        }
        color = next;

        // further logic depends on the mode we're using
        if (animation == PDEAgentHelper.PDEAgentHelperAnimationInteractive) {

            // check our state's focus color (make it darker if not defined)
            key = state + "." + PDEButton.PDEButtonAgentStateFocus;
            next = (PDEColor)parameter.getObjectForKey(key);
            if (next==null && color!=null) {
                // not defined and can be defined? -> set use the current color, make darker
                next = color.styleguideAgentStateColor(1.0f);
                parameter.addObject(next,key);
            }
            color = next;

            // check our state's taking input color (make it darker if not defined)
            key = state + "." + PDEButton.PDEButtonAgentStateTakingInput;
            next = (PDEColor)parameter.getObjectForKey(key);
            if (next==null && color!=null) {
                // not defined and can be defined? -> set use the current color, make darker
                next = color.styleguideAgentStateColor(1.0f);
                parameter.addObject(next,key);
            }

        } else if (animation == PDEAgentHelper.PDEAgentHelperAnimationDown) {

            // check our state's down color (make it darker if not defined, two steps)
            key = state + "." + PDEButton.PDEButtonAgentStateDown;
            next = (PDEColor)parameter.getObjectForKey(key);
            if (next==null && color!=null) {
                // not defined and can be defined? -> set use the current color, make darker
                next = color.styleguideAgentStateColor(2.0f);
                parameter.addObject(next,key);
            }

        }
    }


    /**
     * @brief Gradient (darker/lighter) calculation for all missing colors that are not already gradients.
     *
     * Used in buttons; for colors associated with agent states. A non-gradient color is detected by it having exactly
     * one dot; all others are either direct state or main colors (which are ignored) or already gradients.
     */
    public static void fillGradientColors(PDEParameter parameter)
    {
        int pos,searchStartPos;
        String indexOfStr = ".";


        // go trough all states (we're modifying the array, so we iterate over a copy if the keys)
        // in android we need to make a explicit copy!!! (kd)
        Set<String> keys = new HashSet<String>(parameter.getParameters().keySet());

        for (String key: keys) {
        //for (Iterator<String> iterator = keys.iterator(); iterator.hasNext(); ) {
            //String key = iterator.next();
            // check key -> first dot must be present
            pos = key.indexOf(indexOfStr);
            if (pos == -1) continue;
            // check key -> second dot must not be present
            searchStartPos = pos+indexOfStr.length();
            pos = key.indexOf(indexOfStr,searchStartPos);
            if (pos!=-1) continue;
            // now calculate the gradient colors
            fillGradientColors(parameter,key);
        }
    }


    /**
     * @brief Gradient colors ("<key>.lighter","<key>.darker") calculation for a given key.
     */
    public static void fillGradientColors(PDEParameter parameter,String key)
    {
        String key2;
        PDEColor color;
        PDEColor color2 = null;

        // get the original color (check that it's a color, we now have additional helper values of other types)
        try {
            color = (PDEColor)parameter.getObjectForKey(key);
        } catch (ClassCastException e) {
            return;
        }

        if (color == null) return;

        // do we already have the lighter color?
        key2 = key + PDEButton.PDEButtonColorSuffixLighter;
        try {
            color2 = (PDEColor)parameter.getObjectForKey(key2);
        } catch (ClassCastException e) {
            //nothing done here
        }
        if (color2 == null) {
            // if not add it
            parameter.addObject(color.styleguideGradientLighterColor(),key2);
        }

        // do we already have the darker color?
        key2 = key + PDEButton.PDEButtonColorSuffixDarker;
        try {
            color2 = (PDEColor)parameter.getObjectForKey(key2);
        } catch (ClassCastException e) {
            // nothing done here
        }
        if (color2 == null) {
            // if not add it
            parameter.addObject(color.styleguideGradientDarkerColor(),key2);
        }
    }


    /**
     * @brief Border colors from existing main colors.
     *
     * Used in buttons; for colors associated with agent states. Calculates the border colors
     * from given main colors.
     */
    public static void fillBorderColors(PDEParameter borderToFill, PDEParameter mainColor)
    {
        int pos,searchStartPos;
        String indexOfStr = ".";
        Object c;

        // go trough keys in color
        for (String key: mainColor.getParameters().keySet()) {
            // check key -> first dot must be present
            pos = key.indexOf(indexOfStr);
            if (pos == -1) continue;
            // check key -> second dot must not be present
            searchStartPos = pos+indexOfStr.length();
            pos = key.indexOf(indexOfStr, searchStartPos);
            if (pos != -1) continue;
            // check if key alrady exists in border
            if (borderToFill.getObjectForKey(key) != null) continue;
            // build darker color and set it
            c = mainColor.getObjectForKey(key);
            if (c == null || !(c instanceof PDEColor)) continue;
            c = ((PDEColor)c).styleguideBorderColor();
            borderToFill.addObject(c,key);
        }
    }


    /**
     * @brief Brighten all colors (for use in text field).
     *
     * Usually done before calculating agent state colors. There's no default table at the moment.
     */
    public static void brightenColors(PDEParameter color)
    {
        Object c;

        // go through keys in color ###2do we're changing the keys, so basic enumeration does not work, now using a copy in allKeys => find a better way for this!
        for (String key: color.getParameters().keySet()) {
            // build lighter color and set it
            c = color.getObjectForKey(key);
            if (c==null || !(c instanceof PDEColor)) continue;
            c = ((PDEColor)c).styleguideBrightColor();
            color.addObject(c,key);
        }
    }


    /**
     * @brief Text colors from existing main colors.
     *
     * Used in buttons; for main colors only. No state colors are necessary. If the reference color is transparent,
     * the hinted color is used.
     */
    public static void fillTitleColors(PDEParameter titleColors, PDEParameter fromMainColors, PDEColor transparentColor)
    {
        int pos;
        PDEColor c,tc;

        // go trough keys in color
        for (String key: fromMainColors.getParameters().keySet()) {
            // check key -> empty default key is not processed
            if (key.length() == 0) continue;
            // check key -> no dot must be present (using base values only)
            pos = key.indexOf(".");
            if (pos != -1) continue;
            // check if key alrady exists in text color
            if (titleColors.getObjectForKey(key)!=null) continue;
            // get color
            c = (PDEColor)fromMainColors.getObjectForKey(key);
            // what to use
            if (c.isTransparentColor()) {
                // it's transparent, use hinted transparent color
                tc = transparentColor;
            } else {
                // calculate luminance
                if (c.isDarkColor()) {
                    // dark main color -> white text
                    tc = PDEColor.valueOf("DTDarkUIText");
                } else {
                    // light main color -> black text
                    tc = PDEColor.valueOf("DTLightUIText");
                }
            }
            // and set it
            titleColors.addObject(tc,key);
        }
    }


    /**
     * @brief Text shadow colors from existing main colors.
     *
     * Used in gradient style buttons; for main colors only. Shadow colors only depend on the reference color.
     */
    public static void fillTitleShadowColors(PDEParameter shadowColors ,PDEParameter fromMainColors, PDEColor transparentBackground)
    {
        int pos;
        PDEColor c,sc;

        // go trough keys in color
        for (String key: fromMainColors.getParameters().keySet()) {
            // check key -> empty default key is not processed
            if (key.length() == 0) continue;
            // check key -> no dot must be present (using base values only)
            pos = key.indexOf(".");
            if (pos != -1) continue;
            // check if key alrady exists in text color
            if (shadowColors.getObjectForKey(key)!=null) continue;
            // get color
            c = (PDEColor)fromMainColors.getObjectForKey(key);
            // check for transparency
            if (c.isTransparentColor()) {
                // it's transparent, replace with given transparent background
                c = transparentBackground;
            }
            // shadow to use from luminance only
            if (c.isDarkColor()) {
                // dark main color -> light text, dark shadow
                sc = PDEColor.valueOf("DTBlack");
            } else {
                // light main color -> dark text, light shadow
                sc = PDEColor.valueOf("DTWhite");
            }
            // and set it
            shadowColors.addObject(sc,key);
        }
    }

    /**
     * @brief Text shadow offsets from existing main colors.
     *
     * Used in gradient style buttons; for main colors only. Shadow colors only depend on the reference color.
     * Shadows for dark fonts are below, shadows for light fonts are above
     *
     * !!!!!!!!!!!!!ON IOS A NSValue (with CGPointMake) is stored in shadowOffset PDEParameter, here we save a PointF direct!!!!!!!!!!
     */
    public static void fillTitleShadowOffsets(PDEParameter shadowOffsets,PDEParameter fromMainColors, PDEColor transparentBackground)
    {
        int pos;
        PDEColor c;
        PointF point;

        // go trough keys in color
        for (String key: fromMainColors.getParameters().keySet()) {
            // check key -> empty default key is not processed
            if (key.length() == 0) continue;
            // check key -> no dot must be present (using base values only)
            pos = key.indexOf(".");
            if (pos !=-1) continue;
            // check if key alrady exists in text color
            if (shadowOffsets.getObjectForKey(key)!=null) continue;
            // get color
            c = (PDEColor)fromMainColors.getObjectForKey(key);
            // check for transparency
            if (c.isTransparentColor()) {
                // it's transparent, replace with given transparent background
                c = transparentBackground;
            }
            // shadow to use from luminance only
            if (c.isDarkColor()) {
                // dark main color -> light text, dark shadow, above
                point = new PointF(0.0f,-1.0f);
            } else {
                // light main color -> dark text, light shadow, below
                point = new PointF(0.0f,1.0f);
            }
            // and set it
            shadowOffsets.addObject(point,key);
        }
    }


//-- complex value and color building ----------------------------------------------------------------------------------


    /**
     * @brief Complex value calculation.
     *
     * Can use dictionaries for defaults.
     */
    public static void buildValues(PDEParameter parameter, PDEDictionary reference, Object defaultObject, int animation)
    {
        Object defaultSet;
        String key;

        // build up state base values and mark which states are simple and which are actually used
        fillStateBaseValues(parameter,defaultObject);

        // do lookups if we have a reference
        if (reference!=null) {
            // walk all known states
            for (String state: parameter.states()) {
                // we are allowed to lookup if we are a simple state (as determined earlier)
                if (parameter.isStateSimple(state)) {
                    // get the string to lookup from ourself (if we have any - non-strings will not be lookuped)
                    key = parameter.getValueForKey(state);
                    if (key!=null) {
                        // do we have it?
                        defaultSet = reference.get(key);
                        if (defaultSet!=null) {
                            // debug
                            if (DEBUGPRESETS){
                                Log.d(LOG_TAG,"Found preset for " + key);
                            }

                            // take over result (if it's a dictionary)
                            if ( defaultSet instanceof PDEDictionary) {
                                parameter.addObjectsWithDictionaryForState((PDEDictionary)defaultSet,state);
                            }
                            // debug
                            if (DEBUGPRESETS){
                                parameter.debugOut("Parameter after setting preset");
                            }
                        }
                    }
                }
            }
        }

        // calculate missing agent states (only value propagation)
        fillAgentStates(parameter,animation);
    }


    /**
     * @brief Complex color calculation.
     *
     * Calculate missing colors for agent state logic by applying styleguide logic, and checking if there is a
     * reference set.
     *
     * Used on main color. Calling the more generic function which is able to build derived colors up to a certain
     * step.
     */
    public static void buildColors(PDEParameter parameter,PDEDictionary reference,Object defaultObject,int animation)
    {
        // call complex version
        buildColors(parameter,reference,null,defaultObject,animation);
    }


    /**
     * @brief Complex color calculation.
     *
     * Flexible version, can use reference on itself, or on empty parameters using a base parameter and
     * the base's states.
     */
    public static void buildColors(PDEParameter parameter,PDEDictionary reference,PDEParameter baseParameter,Object defaultObject,int animation)
    {
        Object defaultSet;
        boolean changed;
        PDEColor color;
        String colorString;

        // init
        changed = false;

        // build up state base values and mark which states are simple and which are actually used
        fillStateBaseValues(parameter,defaultObject);

        // convert all to UIColor (default object might not have been a color, so do it later)
        parameter.convertToColor();

        // do lookups if we have a reference
        if (reference!=null) {
            // which mode? if we have a reference parameter and reference states, use it
            if (baseParameter!=null) {
                // walk all base states
                for (String state: baseParameter.states()) {
                    // do we not have this state at all?
                    if (!parameter.hasState(state)) {
                        // get the color to lookup from the base
                        color = (PDEColor)baseParameter.getObjectForKey(state);
                        if (color!=null) {
                            // convert to hex string for searching
                            colorString = color.getHexColorString();
                            // do we have it?
                            defaultSet = reference.get(colorString);
                            if (defaultSet!=null) {
                                // debug
                                if (DEBUGPRESETS){
                                    Log.d(LOG_TAG, "Found preset for " + colorString);
                                }
                                // take over result (if it's a dictionary)
                                if ( defaultSet instanceof PDEDictionary) {
                                    parameter.addObjectsWithDictionaryForState((PDEDictionary) defaultSet, state);
                                }
                                // debug
                                if (DEBUGPRESETS){
                                    parameter.debugOut("Parameter after setting preset");
                                }
                                // remember as changed for later.
                                changed = true;
                            }
                        }
                    }
                }
            } else {
                // walk all known states
                for (String state :parameter.states()) {
                    // we are allowed to lookup if we are a simple state (as determined earlier)
                    if (parameter.isStateSimple(state)) {
                        // get the color to lookup from ourself
                        color = (PDEColor)parameter.getObjectForKey(state);
                        if (color!=null) {
                            // convert to hex string for searching
                            colorString = color.getHexColorString();
                            // do we have it?
                            defaultSet = reference.get(colorString);
                            if (defaultSet!=null) {
                                // debug
                                if (DEBUGPRESETS){
                                    Log.d(LOG_TAG,"Found preset for " + colorString);
                                }
                                // take over result (if it's a dictionary)
                                if ( defaultSet instanceof PDEDictionary) {
                                    parameter.addObjectsWithDictionaryForState((PDEDictionary)defaultSet,state);
                                }
                                // debug
                                if (DEBUGPRESETS){
                                    parameter.debugOut("Parameter after setting preset");
                                }
                                // remember as changed for later.
                                changed = true;
                            }
                        }
                    }
                }
            }
        }

        // convert again if it was changed by adding defaults
        if (changed) {
            parameter.convertToColor();
        }

        // calculate missing agent state values for color and border
        fillAgentStateColors(parameter,animation);
    }


//-- interpolation -----------------------------------------------------------------------------------------------------

    /**
     * @brief Interpolation helper function, extracting the necessary parameters from a PDEAgentHelper.
     */
    public static float interpolateFloat(PDEParameter parameter,PDEAgentHelper agentHelper,int animation,String suffix)
    {
        float value;

        PDEAgentHelper.InterpolationStateHelper interpolationHelper = agentHelper.getInterpolationInformationForAnimation(animation);

        // interpolate colors by calling complex logic color interpolation helper
        value = interpolateFloat(parameter,interpolationHelper.mState1,interpolationHelper.mState2,interpolationHelper.mStateBlend,interpolationHelper.mSubState1,interpolationHelper.mSubState2,interpolationHelper.mSubStateBlend,suffix);

        // done
        return value;
    }


    /**
     * @brief Complex interpolation (between states and substates) for floats.
     */
    public static float interpolateFloat(PDEParameter parameter,String state1,String state2,float stateblend,String substate1,String substate2,float substateblend,String suffix)
    {
        float value,value2,value3;

        // safety: do nothing if state1 is not defined
        if (state1==null) return 0.0f;

        // additional fallback: if a state is requested, but is not defined at all, we use the default state
        if (!parameter.hasState(state1)) {
            state1 = PDEButton.PDEButtonStateDefault;
        }
        if (state2!=null && !parameter.hasState(state2)) {
            state2 = PDEButton.PDEButtonStateDefault;
        }

        // limit blend factors
        if (stateblend < 0.0f) stateblend = 0.0f;
        if (stateblend > 1.0f) stateblend = 1.0f;
        if (substateblend < 0.0f) substateblend = 0.0f;
        if (substateblend > 1.0f) substateblend = 1.0f;

        // start with first state and substate
        value = getFloat(parameter,state1,substate1,suffix);

        // add in second substate if required
        if (substate2!=null) {
            value2 = getFloat(parameter,state1,substate2,suffix);
            value = (value*(1.0f-substateblend)) + (value2*substateblend);
        }

        // if we have a second state, also calculate this
        if (state2!=null) {
            // second state first substate
            value2 = getFloat(parameter,state2,substate1,suffix);
            // add in second substate if required
            if (substate2!=null) {
                value3 = getFloat(parameter,state2,substate2,suffix);
                value2 = (value2*(1.0f-substateblend)) + (value3*substateblend);
            }
            // now mix states
            value = (value*(1.0f-stateblend)) + (value2*stateblend);
        }

        // done
        return value;
    }


    /**
     * @brief Helper function to retrieve a coded float.
     */
    public static float getFloat(PDEParameter parameter,String state,String substate,String suffix)
    {
        String str;
        float value;

        // build string
        str=state;
        if (!TextUtils.isEmpty(substate)) {
            str = str + "." + substate;
        }
        if (!TextUtils.isEmpty(suffix)) {
            str = str + suffix;
        }

        // performance warning
        if( PERFORMANCECHECK ){
            Object object;
            object = parameter.getObjectForKey(str);
            if( !(object instanceof Number) ){
                Log.w(LOG_TAG,"Interpolation number lookup: class mismatch");
            }
        }

        // get number; convert or use 0.0f as default
        value = parameter.getFloatForKey(str);

        // done
        return value;
    }


    /**
     * @brief Interpolation helper function, extracting the necessary parameters from a PDEAgentHelper.
     */
    public static PDEColor interpolateColor(PDEParameter parameter,PDEAgentHelper agentHelper,int animation,String suffix)
    {
        PDEColor color;
        PDEAgentHelper.InterpolationStateHelper interpolationHelper;

        // get states from agent helper
        interpolationHelper = agentHelper.getInterpolationInformationForAnimation(animation);

        // interpolate colors by calling complex logic color interpolation helper
        color = interpolateColor(parameter,interpolationHelper.mState1,interpolationHelper.mState2,interpolationHelper.mStateBlend,interpolationHelper.mSubState1,interpolationHelper.mSubState2,interpolationHelper.mSubStateBlend,suffix);

        // done
        return color;
    }


    /**
     * @brief Complex color interpolation (between states and substates)
     */
    public static PDEColor interpolateColor(PDEParameter parameter,String state1,String state2,float stateblend,String substate1,String substate2,float substateblend,String suffix)
    {
        PDEColor color,color2,color3;

        // safety: do nothing if state1 is not defined
        if (state1==null) return PDEColor.valueOf("DTBlack");

        // additional fallback: if a state is requested, but is not defined at all, we use the default state
        if (!parameter.hasState(state1)) {
            state1 = PDEButton.PDEButtonStateDefault;
        }
        if (state2!=null && !parameter.hasState(state2)) {
            state2 = PDEButton.PDEButtonStateDefault;
        }

        // limit blend factors
        if (stateblend < 0.0f) stateblend = 0.0f;
        if (stateblend > 1.0f) stateblend = 1.0f;
        if (substateblend < 0.0f) substateblend = 0.0f;
        if (substateblend > 1.0f) substateblend = 1.0f;

        // start with first state and substate
        color = getColor(parameter,state1,substate1,suffix);

        // add in second substate if required
        if (substate2!=null) {
            color2 = getColor(parameter,state1,substate2,suffix);
            color = color.mixColors(color2,substateblend);
        }

        // if we have a second state, also calculate this
        if (state2!=null) {
            // second state first substate
            color2 = getColor(parameter,state2,substate1,suffix);
            // add in second substate if required
            if (substate2!=null) {
                color3 = getColor(parameter,state2,substate2,suffix);
                color2 = color2.mixColors(color3,substateblend);
            }
            // now mix states
            color = color.mixColors(color2,stateblend);
        }

        // done
        return color;
    }


    /**
     * @brief Helper function to retrieve a coded color.
     */
    public static PDEColor getColor(PDEParameter parameter,String state,String substate,String suffix)
    {
        String str;
        PDEColor color;

        // build string
        str=state;
        if (substate!=null) {
            str = str + "." + substate;
        }
        if (suffix!=null) {
            str = str + suffix;
        }

        if( PERFORMANCECHECK ){
            Object object;
            object = parameter.getObjectForKey(str);
            if( !(object instanceof PDEColor) ){
                Log.w(LOG_TAG,"Interpolation color lookup: class mismatch");
            }
        }

        // get color (if not defined, use black)
        color = parameter.getColorForKey(str);
        if (color == null) return PDEColor.valueOf("DTBlack");

        // done
        return color;
    }


    /**
     * @brief Interpolation helper function, extracting the necessary parameters from a PDEAgentHelper.
     */
    public static PointF interpolatePosition(PDEParameter parameter,PDEAgentHelper agentHelper,int animation, String suffix)
    {
        PDEAgentHelper.InterpolationStateHelper isHelper;
        PointF value;

        // get states from agent helper
        isHelper = agentHelper.getInterpolationInformationForAnimation(animation);

        // interpolate colors by calling complex logic color interpolation helper
        value = interpolatePosition(parameter,isHelper.mState1,isHelper.mState2,isHelper.mStateBlend,isHelper.mSubState1,isHelper.mSubState2,isHelper.mSubStateBlend,suffix);

        // done
        return value;
    }


    /**
     * @brief Complex interpolation (between states and substates) for floats.
     */
    public static PointF interpolatePosition(PDEParameter parameter,String state1,String state2,float stateblend,String substate1,String substate2,float substateblend,String suffix)
    {
        PointF value,value2,value3;

        // safety: do nothing if state1 is not defined
        if (state1==null) return new PointF(0.0f,0.0f);

        // additional fallback: if a state is requested, but is not defined at all, we use the default state
        if (!parameter.hasState(state1)) {
            state1 = PDEButton.PDEButtonStateDefault;
        }
        if (state2!=null && !parameter.hasState(state2)) {
            state2 = PDEButton.PDEButtonStateDefault;
        }

        // limit blend factors
        if (stateblend < 0.0f) stateblend = 0.0f;
        if (stateblend > 1.0f) stateblend = 1.0f;
        if (substateblend < 0.0f) substateblend = 0.0f;
        if (substateblend > 1.0f) substateblend = 1.0f;

        // start with first state and substate
        value = getPosition(parameter,state1,substate1,suffix);

        // add in second substate if required
        if (substate2!=null) {
            value2 = getPosition(parameter,state1,substate2,suffix);
            value.x = (value.x*(1.0f-substateblend)) + (value2.x*substateblend);
            value.y = (value.y*(1.0f-substateblend)) + (value2.y*substateblend);
        }

        // if we have a second state, also calculate this
        if (state2!=null) {
            // second state first substate
            value2 = getPosition(parameter,state2,substate1,suffix);
            // add in second substate if required
            if (substate2!=null) {
                value3 = getPosition(parameter,state2,substate2,suffix);
                value2.x = (value2.x*(1.0f-substateblend)) + (value3.x*substateblend);
                value2.y = (value2.y*(1.0f-substateblend)) + (value3.y*substateblend);
            }
            // now mix states
            value.x = (value.x*(1.0f-stateblend)) + (value2.x*stateblend);
            value.y = (value.y*(1.0f-stateblend)) + (value2.y*stateblend);
        }

        // done
        return value;
    }


    /**
     * @brief Helper function to retrieve a coded float.
     */
    public static PointF getPosition(PDEParameter parameter,String state,String substate,String suffix)
    {
        String str;
        PointF position;

        // build string
        str=state;
        if (substate!=null) {
            str += ".";
            str += substate;
        }
        if (suffix!=null) {
            str += suffix;
        }

        // performance warning
        if( PERFORMANCECHECK ){
            Object object;
            object = parameter.getObjectForKey(str);
            if (!(object instanceof PointF)) {
                Log.w(LOG_TAG,"Interpolation number lookup: class mismatch");
            }
        }

        // get number; automatic default for non-convertibles is 0.0
        position = parameter.getPositionForKey(str);

        // done
        return position;
    }

//-- default dictionary handling ---------------------------------------------------------------------------------------

    /**
     * @brief Read a default dictionary, convert all main keys to hexcolors (we're looking for those as reference).
     */
    public static PDEDictionary readDefaultColorDictionary(String dictionaryName)
    {
        PDEDictionary source;
        PDEDictionary dest;
        PDEColor color;
        Object object;

        // try to find the dictionary
        source = readDictionaryXml(dictionaryName);

        // error while reading?
        if (source==null) return null;

        // create new dictionary
        dest = new PDEDictionary();

        // copy all keys; convert color values
        for (String key: source.keySet()) {
            // get color from key, and object
            color = PDEColor.valueOf(key);
            object = source.get(key);
            // valid?
            if (color!=null) {
                // store key in new dictionary
                dest.put(color.getHexColorString(),object);
            }
        }

        // done
        return dest;
    }


    /**
     * @brief  MUST HAVE THE SAME STRUCT LIKE IOS PLIST FILE !!!!!!!!!!!!!!!!!
     * <key></key>
     * <value></value>
     * <key></key>
     * <value></value>
     * ........
     */
    public static PDEDictionary readDictionaryXml(String dictionaryName){
        PDEDictionary destDict = new PDEDictionary();
        int id=0;

        // try to find the dictionary
        Context context = null;

        context = PDECodeLibrary.getInstance().getApplicationContext();

        //DOM parser only works with xml files in the raw resource, but there are some other problems....
        id = getDictionaryNameIdentifier(dictionaryName);
        if( id == 0 ) return null;

        try {
            XmlPullParser parser=context.getResources().getXml(id);
            parseXML(parser, destDict, null);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }

        // done
        return destDict;
    }


    /**
     * @brief  MUST HAVE THE SAME STRUCT LIKE IOS PLIST FILE !!!!!!!!!!!!!!!!!
     *
     */
    private static void parseXML(XmlPullParser parser, PDEDictionary dictToFill, PDEDictionary parent)  throws XmlPullParserException, IOException{
        String keyString = "";

        int eventType = parser.getEventType();

        // process tag while not reaching the end of document
        while(eventType != XmlPullParser.END_DOCUMENT) {
            // get tag name
            String tagName = parser.getName();
            switch(eventType) {
                case XmlPullParser.START_TAG:
                    //check the start of the plist (root)
                    if( tagName.equalsIgnoreCase("plist")){
                        //the next element must be the root dict
                        eventType = parser.next();
                        parser.require(XmlPullParser.START_TAG, null, "dict");
                        parser.next();
                        //set new dict
                        parseXML(parser,dictToFill,parent);
                    }
                    else if( tagName.equalsIgnoreCase("key")){
                        keyString = parser.nextText();
                    }
                    else if(tagName.equalsIgnoreCase("dict")){
                        parser.next();
                        PDEDictionary newDict = new PDEDictionary();
                        parseXML(parser,newDict,parent);
                        dictToFill.put(keyString,newDict);
                    }
                    else if( tagName.equalsIgnoreCase("string")){
                        dictToFill.put(keyString,parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    //check if dictionary is finished -> go back
                    if( tagName.equalsIgnoreCase("dict")){
                        return;
                    }
            }
            // jump to next event
            eventType = parser.next();
        }
    }


    /**
     * @brief just little helper for readDictionaryXml function
     */
    private static int getDictionaryNameIdentifier(String dictionaryName)
    {
        Context context = PDECodeLibrary.getInstance().getApplicationContext();
        return context.getResources().getIdentifier(dictionaryName, "xml", context.getPackageName() );
    }

//-- hint handling -----------------------------------------------------------------------------------------------------


    /**
     * @brief Extract hint for dark style.
     *
     * If no hints (dark style, background color) are set use system default.
     */
    public static boolean extractDarkStyleHint(PDEDictionary hints)
    {
        Object object;

        // cascading lookups: explicitly set overall background color
        object = hints.get(PDEButton.PDEButtonHintBackgroundColor);
        if (object!=null) {
            // something is set; might be a string or a color
            if ( object instanceof PDEColor) {
                return ((PDEColor)object).isDarkColor();
            } else if ( object instanceof String) {
                return PDEColor.valueOf((String) object).isDarkColor();
            }
        }

        // explicitly set dark style flag
        object = hints.get(PDEButton.PDEButtonHintDarkStyle);
        if (object!=null && (object instanceof  Boolean) ) {
            // use the flag
            return (Boolean)object;
        }

        // if nothing is set use system default
        return PDECodeLibrary.getInstance().isDarkStyle();
    }


    /**
     * @brief Extract hint for background color.
     *
     * If no hints (dark style, background color) are set use system default.
     */
    public static PDEColor extractBackgroundColorHint(PDEDictionary hints)
    {
        Object object;
        boolean darkStyle;

        // cascading lookups: explicitly set overall background color
        object = hints.get(PDEButton.PDEButtonHintBackgroundColor);
        if (object!=null) {
            // something is set; might be a string or a color
            if ( object instanceof PDEColor) {
                return (PDEColor)object;
            } else if ( object instanceof String ) {
                return PDEColor.valueOf((String) object);
            }
        }

        // explicitly set dark style flag
        object = hints.get(PDEButton.PDEButtonHintDarkStyle);
        if (object!=null && (object instanceof Boolean)) {
            // get the flag
            darkStyle = (Boolean)object;
            // use system default for this style
            if (darkStyle){
                return PDEColor.valueOf("DTDarkUIBackground");
            }
            else {
                return PDEColor.valueOf("DTLightUIBackground");
            }
        }

        // if nothing is set use system default
        if ( PDECodeLibrary.getInstance().isDarkStyle()){
            return PDEColor.valueOf("DTDarkUIBackground");
        }
        else{
            return PDEColor.valueOf("DTLightUIBackground");
        }
    }


    /**
     * @brief Extract hint for 3D style.
     *
     * If hint is not set, 3D style is turned off.
     */
    public static boolean extract3DStyleHint(PDEDictionary hints)
    {
        Object object;

        // check for hint
        object = hints.get(PDEButton.PDEButtonHint3DStyle);
        if (object!=null && (object instanceof Boolean) )  {
            // use the flag
            return (Boolean)object;
        }

        // nothing set, use default
        return false;
    }


    /**
     * @brief Extract hint for default UI color (interactive color).
     */
    public static PDEColor extractDefaultColorHint(PDEDictionary hints)
    {
        boolean darkStyle;
        Object object;

        // cascaded: first check for explicit hint
        object = hints.get(PDEButton.PDEButtonHintDefaultColor);
        if (object != null) {
            // something is set; might be a string or a color
            if ( object instanceof PDEColor) {
                return (PDEColor)object;
            } else if ( object instanceof String ) {
                return PDEColor.valueOf((String) object);
            }
        }

        // if we're here, we have nothing set -> get dark style
        darkStyle = extractDarkStyleHint(hints);

        // color dependent on dark style
        if (darkStyle){
            return PDEColor.valueOf("DTDarkUIInteractive");
        }
        else{
            return PDEColor.valueOf("DTLightUIInteractive");
        }
    }


    /**
     * @brief Extract hint for default text color on transparent background
     */
    public static PDEColor extractTextOnTransparentColorHint(PDEDictionary hints)
    {
        Object object;
        boolean darkStyle;

        // cascaded: first check if we have something explicitly set
        object=hints.get(PDEButton.PDEButtonHintTextOnTransparentColor);
        if (object!=null) {
            // something is set; might be a string or a color
            if (object instanceof PDEColor) {
                return (PDEColor)object;
            } else if ( object instanceof String) {
                return PDEColor.valueOf((String) object);
            }
        }

        // if we're here, we have nothing set -> get dark style
        darkStyle = extractDarkStyleHint(hints);

        // now the color depends on the dark style: white on dark, black on light
        if (darkStyle){
            return PDEColor.valueOf("DTDarkUIText");
        }
        else {
            return PDEColor.valueOf("DTLightUIText");
        }
    }


    //-- hint handling -----------------------------------------------------------------------------------------------------
//
//
//    /**
//     * @brief Extract hint for dark style.
//     *
//     * If no hints (dark style, background color) are set use system default.
//     */
//    public static boolean extractDarkStyleHint(PDEDictionary hints)
//    {
//        Object object;
//
//        // cascading lookups: explicitly set overall background color
//        object = hints.get(PDEButton.PDEButtonHintBackgroundColor);
//        if (object != null) {
//            // something is set; might be a string or a color
//            if (object instanceof PDEColor) {
//                return ((PDEColor) object).isDarkColor();
//            } else if (object instanceof String) {
//                return PDEColor.valueOf(((String)object)).isDarkColor();
//            }
//        }
//
//        // explicitly set dark style flag
//        object = hints.get(PDEButton.PDEButtonHintDarkStyle);
//        if (object != null && object instanceof Boolean) {
//            // use the flag
//            return ((Boolean)object).booleanValue();
//        }
//
//        // if nothing is set use system default
//        return PDECodeLibrary.getInstance().isDarkStyle();
//    }



}