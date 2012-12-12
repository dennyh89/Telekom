/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;




//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerOverlayRadio
//----------------------------------------------------------------------------------------------------------------------


import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonLayoutHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEDictionary;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShape;
import de.telekom.pde.codelibrary.ui.elements.common.PDELayerSunken;
import de.telekom.pde.codelibrary.ui.elements.common.PDEViewBackground;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

import android.content.Context;
import android.graphics.RectF;


/**
 * @brief Background for an Checkbox button.
 */

public class PDEButtonLayerOverlayRadio extends Object implements PDEButtonLayerInterface {


     /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEButtonLayerOverlayRadio.class.getName();
    // debug messages switch
    private final static boolean DEBUGPARAMS = false;


    // size modes
    enum PDEButtonLayerOverlayRadioSizeMode {
        PDEButtonLayerOverlayRadioSizeModeUndefined,
        PDEButtonLayerOverlayRadioSizeModeAutomatic,
        PDEButtonLayerOverlayRadioSizeModeStyleguide,
        PDEButtonLayerOverlayRadioSizeModeFixed
    } ;


    // parameters needed
    private PDEParameterDictionary mParameters;
    private PDEParameter mParamColor;
    private PDEParameter mParamBorderColor;
    private PDEParameter mParamState;

    // content layers
    private PDELayerSunken mSunkenLayer;
    private PDEDrawableShape mMarkerLayer;
    private PDEViewBackground mMarkerView;

    // configuration
    private float mRadioSize;
    private PDEButtonLayerOverlayRadioSizeMode mRadioSizeMode;
    private PDEConstants.PDEAlignment mRadioAlignment;

    // configuration
    private float mRadioUsedSize;
    private float mMarkerUsedSize;

    // layout info
    private PDEButtonLayoutHelper mLayout;

    // agent helpers
    private PDEAgentHelper mAgentHelper;

    // the layer
    protected PDEAbsoluteLayout mLayer = null;

    // global variables
    //
    static PDEDictionary PDEButtonLayerOverlayRadioGlobalColorDefault = null;
    static PDEDictionary PDEButtonLayerOverlayRadioGlobalBorderDefault = null;



    /**
     * @brief Class initialization.
     */
    PDEButtonLayerOverlayRadio(Context context) {

        // read default dictionaries
        PDEButtonLayerOverlayRadioGlobalColorDefault = PDEComponentHelpers.readDefaultColorDictionary("dt_button_flat_color_defaults");
        PDEButtonLayerOverlayRadioGlobalBorderDefault = PDEComponentHelpers.readDefaultColorDictionary("dt_button_border_color_defaults");

        // init
        mParameters = null;
        mRadioUsedSize = 0.0f;
        mMarkerUsedSize = 0.0f;

        mLayout = new PDEButtonLayoutHelper();

        // default configuration
        mRadioAlignment = PDEConstants.PDEAlignment.PDEAlignmentCenter;
        mRadioSizeMode = PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeStyleguide;
        mRadioSize = 0.0f;

        // create the layer
        mLayer = new PDEAbsoluteLayout(context);
//        mLayer.masksToBounds = YES;

        // agent helper
        mAgentHelper = new PDEAgentHelper();

        // create sunken layer
        mSunkenLayer = new PDELayerSunken(PDECodeLibrary.getInstance().getApplicationContext());
        mLayer.addView(mSunkenLayer);



        // create marker circle layer
        mMarkerLayer = new PDEDrawableShape();
        mMarkerLayer.setBackgroundColor(PDEColor.valueOf("DTMagenta").getIntegerColor());
        mMarkerView = new PDEViewBackground(PDECodeLibrary.getInstance().getApplicationContext(),mMarkerLayer);
        mLayer.addView(mMarkerView);


        // set empty complex parameters
        mParamColor = new PDEParameter();
        mParamBorderColor = new PDEParameter();
        mParamState = new PDEParameter();

        // forced set of parameter -> this sets defaults
        setParameters( new PDEParameterDictionary(),true);
    }


    //----- basic functions ------------------------------------------------------------------------------------------------



    /**
     * @brief Layer access.
     */
    @Override
    public PDEAbsoluteLayout getLayer() {
        return mLayer;
    }



    /**
     * @brief Process agent events.
     */
    @Override
    public void agentEvent(PDEEvent event)
    {
        boolean needsUpdate;

        // pass on agent events to agent helper
        needsUpdate = mAgentHelper.processAgentEvent(event);

        // update if necessary
        if (needsUpdate) {
            // update animateable parameters on change
            updateColors_priv();
            updateState_priv();
        }
    }


//----- property handling ----------------------------------------------------------------------------------------------


    /**
     * @brief Set button basic data.
     *
     * Should only be called from main button class, it handles the state, and also does change management.
     */
    @Override
    public void setParameters(PDEParameterDictionary parameters, boolean force)
    {
        PDEParameterDictionary oldParams;

        // for change management, keep around the old params
        oldParams = mParameters;

        // completely copy the new ones for further change management
        mParameters = parameters.copy();

        // color is a fixed parameter right now
        if (force) {
            prepareColor_priv();
        }

        // state is a fixed parameter right now
        if (force) {
            prepareState_priv();
        }

        // non-animatable properties do their change management internally
        prepareAlignment_priv();
        prepareSize_priv();
    }


//----- animated parameters: preparation -------------------------------------------------------------------------------


    /**
     * @brief Set the (animatable) title color.
     *
     * If no title color is given, the title color is calculated from a) the main color, and b) the hints (indicative
     * buttons have a special color), and c) the system preset (dark or light system).
     *
     * The shadow color is also calculated; the shadow color depends solely on the main color (and does only change
     * inbetween states)
     */
    private void prepareColor_priv()
    {
        // create fixed defaults (old way for old compilers, dictionary literal would be better readable)
        mParamColor.removeAllObjects();
        mParamColor.setBaseValue("DTWhite");
        mParamBorderColor.removeAllObjects();
        mParamBorderColor.setBaseValue("DTGrey237_Idle_Border");

        // debug output
        if( DEBUGPARAMS ){
            mParamColor.debugOut("Color before building");
            mParamBorderColor.debugOut("Border before building");
        }

        // basic propagation of colors (no agent states required), convert to colors
        PDEComponentHelpers.fillStateBaseValues(mParamColor, null);
        mParamColor.convertToColor();


        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamColor, PDEButtonLayerOverlayRadioGlobalColorDefault, null, PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamBorderColor, PDEButtonLayerOverlayRadioGlobalBorderDefault, mParamColor, null, PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // if border color for a state is still not defined, calculate it from main agent colors (use gradient darker step)
        PDEComponentHelpers.fillBorderColors(mParamBorderColor, mParamColor);

        // debug output
        if( DEBUGPARAMS ){
            mParamColor.debugOut("Color after building");
            mParamBorderColor.debugOut("Border after building");
        }

        // and apply once
        updateColors_priv();
    }


    /**
     * @brief Prepare shadow strength parameter set.
     *
     * Shadow strengs animates the shadow as a whole. The range is 0.0..1.0.
     */
    private void prepareState_priv()
    {
        // set the new values
        mParamState.removeAllObjects();

        // create fixed defaults (old way for old compilers, dictionary literal would be better readable)
        mParamState.setWithDictionary(new PDEDictionary("default","0.0","selected","1.0"));

        // debug output
        if( DEBUGPARAMS ){
            mParamState.debugOut("State before building");
        }

        // calculate agent states
        PDEComponentHelpers.buildValues(mParamState, null, null, PDEAgentHelper.PDEAgentHelperAnimationStateOnly);

        // and convert to number
        mParamState.convertToNumber();

        // debug output
        if( DEBUGPARAMS ){
            mParamState.debugOut("State after building");
        }

        // apply once
        updateState_priv();
    }


//----- non-animated parameters: preparation and application -----------------------------------------------------------


    /**
     * @brief Private function - prepare internal layout.
     *
     * IconOnRightSide and IconTextAlignment are non-animated and uses the base parameter
     */
    private void prepareAlignment_priv()
    {
        String alignmentString;
        PDEConstants.PDEAlignment alignment;

        // get the title from the parameters
        alignmentString = mParameters.parameterValueForNameWithDefault(PDEButton.PDEButtonParameterRadioAlignment, PDEConstants.PDEAlignmentStringLeft);

        // parse value
        if ( alignmentString.equals(PDEConstants.PDEAlignmentStringLeft) ) alignment= PDEConstants.PDEAlignment.PDEAlignmentLeft;
        else if ( alignmentString.equals(PDEConstants.PDEAlignmentStringCenter) ) alignment= PDEConstants.PDEAlignment.PDEAlignmentCenter;
        else if ( alignmentString.equals(PDEConstants.PDEAlignmentStringRight) ) alignment= PDEConstants.PDEAlignment.PDEAlignmentRight;
        else alignment= PDEConstants.PDEAlignment.PDEAlignmentLeft;

        // any change?
        if (alignment == mRadioAlignment) return;

        // remember
        mRadioAlignment = alignment;

        // relayout
        relayout_priv();
    }


    /**
     * @brief: Private function - prepare the font.
     *
     * Preparation function for the font which evaluates the parameters. Note that we need to
     */
    private void prepareSize_priv()
    {
        Object sizeObject;
        PDEButtonLayerOverlayRadioSizeMode radioMode;
        float radioSize;
        String str;

        // get font parameters
        sizeObject = mParameters.parameterObjectForName(PDEButton.PDEButtonParameterFontSize);

        // parse font size
        if ( sizeObject instanceof Number ) {
            // use given size directly
            radioMode = PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeFixed;
            radioSize = ((Number)sizeObject).floatValue();
        } else if ( sizeObject instanceof String ) {
            // extract string
            str = (String)sizeObject;
            // button specific mode / fixed size?
            if ( str.equals(PDEButton.PDEButtonParameterValueSizeAuto) || str.equals(PDEButton.PDEButtonParameterValueSizeAutomatic) ) {
                // automatic sizing
                radioMode = PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeAutomatic;
                radioSize = 0.0f;
            } else if ( str.equals(PDEButton.PDEButtonParameterValueSizeStyleguide)) {
                // styleguide defined sizes sizing
                radioMode = PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeStyleguide;
                radioSize = 0.0f;
            } else {
                // not a button specific value -> use float value (2do: BU parsing)
                radioMode = PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeFixed;
                radioSize = PDEBuildingUnits.parseSize(str);
            }
        } else {
            // nothing set -> use default
            radioMode = PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeStyleguide;
            radioSize = 0.0f;
        }

        // changed?
        if (radioMode==mRadioSizeMode && radioSize==mRadioSize) return;

        // remember
        mRadioSizeMode = radioMode;
        mRadioSize = radioSize;

        // update size, and relayout
        updateSize_priv();
        relayout_priv();
    }


//----- fixed parameters -----------------------------------------------------------------------------------------------


//----- animated parameter updates -------------------------------------------------------------------------------------


    /**
     * @brief Private function - update all colors.
     *
     * Title colors, shadow colors. All colors are precalculated and only animated.
     */
    private void updateColors_priv()
    {
        PDEColor color,border;

        // interpolate colors by calling complex logic color interpolation helper
        color = PDEComponentHelpers.interpolateColor(mParamColor, mAgentHelper, PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);
        border = PDEComponentHelpers.interpolateColor(mParamBorderColor, mAgentHelper, PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);

        // set color
        mSunkenLayer.setSunkenBackgroundColor(color);
        mSunkenLayer.setSunkenBorderColor(border);
    }


    /**
     * @brief Private function - update state, show marker if required.
     */
    private void updateState_priv()
    {
        float alpha;

        // interpolate colors by calling complex logic color interpolation helper
        alpha = PDEComponentHelpers.interpolateFloat(mParamState, mAgentHelper, PDEAgentHelper.PDEAgentHelperAnimationStateOnly, null);

        // show/hide marker
        mMarkerLayer.setShapeOpacity(alpha);
    }


    /**
     * @brief Private function - calculate the new size and apply it.
     *
     * The layouting positions the graphical element; the sizing of red dot and sunken layer is done here.
     */
    private void updateSize_priv()
    {
        float height,radioSize,markerSize;

        // commonly used data
        height = mLayout.mLayoutRect.height();

        // which mode?
        if (mRadioSizeMode == PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeAutomatic) {
            // marker height is 1 third of button height, rounded; outline height is double this size
            markerSize = PDEBuildingUnits.roundToScreenCoordinates(height/3.0f);
            radioSize = markerSize * 2.0f;
        } else if (mRadioSizeMode == PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeStyleguide) {
            // standardized heights: buttons defined are 4BU, 3BU and 2.5BU; use the nearest one to calculate
            if (height > PDEBuildingUnits.exactPixelFromBU(3.5f)) {
                // display height > 3,5*BU -> use 4 BU as reference size
                markerSize = PDEBuildingUnits.roundToScreenCoordinates(PDEBuildingUnits.pixelFromBU(4.0f)/3.0f);
                radioSize = markerSize * 2.0f;
            } else if (height > PDEBuildingUnits.exactPixelFromBU(2.75f)) {
                // 2,75*BU < display height <= 3,5*BU -> use 3 BU as reference size
                markerSize = PDEBuildingUnits.roundToScreenCoordinates(PDEBuildingUnits.pixelFromBU(3.0f)/3.0f);
                radioSize = markerSize * 2.0f;
            } else {
                // display height < 2,75*BU -> use 2.5 BU as reference size
                markerSize = PDEBuildingUnits.roundToScreenCoordinates(PDEBuildingUnits.pixelFromBU(2.5f)/3.0f);
                radioSize = markerSize * 2.0f;
            }
        } else if (mRadioSizeMode == PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeFixed) {
            // fixed height, marker is half the height
            radioSize = mRadioSize;
            markerSize = radioSize / 2.0f;
        } else {
            // unknown mode -> do nothing
            return;
        }

        // compare with last value, stop if not changed
        if (markerSize==mMarkerUsedSize && radioSize==mRadioUsedSize) return;

        // remember
        mRadioUsedSize = radioSize;
        mMarkerUsedSize = markerSize;

        // directly apply to the layers
        mSunkenLayer.setShapeOval(new RectF(0,0,mRadioUsedSize,mRadioUsedSize));

        // create new shape
        mMarkerLayer.setShapeOval(new RectF(0,0,mMarkerUsedSize,mMarkerUsedSize));
    }


//----- layout ---------------------------------------------------------------------------------------------------------



    /**
     * @brief Apply new layout when set from the outside.
     */
    @Override
    public void setLayout(PDEButtonLayoutHelper layout)
    {
        // any change?
        // make a own equals method in the PDEButtonLayoutHelper class if it exists after adjustments!!!!!!
        if (mLayout.equals(layout)) {
            return;
        }


        // remember
        mLayout = layout;

        // update the size
        updateSize_priv();

// ToDo find out what to do with cliprect stuff
        // set bounds and offset of our layer; we're clipping
//        self.layer.anchorPoint = CGPointMake(0.0f,0.0f);
//        self.layer.frame = layout->mClipRect;

        // and perform a new layout (-> this might adjust further layout rects)
        performLayout_priv(layout);
    }


    /**
     * @brief Relayout.
     *
     * Use the stored layout. This is not correct since it does not update dependent layers. When we have change
     * management we must trigger a complete button layout here.
     */
    private void relayout_priv()
    {
        PDEButtonLayoutHelper layout;

        // create a copy (the layout is changed during layouting)
        layout = new PDEButtonLayoutHelper(mLayout);

        // perform layout with stored layout data
        performLayout_priv(layout);
    }


    /**
     * @brief Perform the actual layouting tasks.
     *
     * Called when anything has changed. Performs the new layout for all elements and sets it
     * accordingly. The member variables are already set correctly outside.
     */
    private void performLayout_priv(PDEButtonLayoutHelper layout)
    {
        float xoffset,yoffset,width,height,radioX,radioY,markerX,markerY,dist,oldvalue;

        // commonly used data (adjusted for our special clipping logic)
        xoffset = layout.mLayoutRect.left - layout.mClipRect.left;
        yoffset = layout.mLayoutRect.top - layout.mClipRect.top;
        width = layout.mLayoutRect.width();
        height = layout.mLayoutRect.height();

        // center radio in Y, round to screen
        radioY = yoffset + PDEBuildingUnits.roundToScreenCoordinates((height-mRadioUsedSize)/2.0f);



        // check alignment, adjusts layout for further children
        if (mRadioAlignment == PDEConstants.PDEAlignment.PDEAlignmentLeft) {
            // left, one BU from border
            radioX = xoffset + PDEBuildingUnits.BU();
            // adjusting distance
            dist = PDEBuildingUnits.BU() + mRadioUsedSize;
            // adjust layout rect
            layout.mLayoutRect.left += dist;
            layout.mLayoutRect.right -= dist; // ToDo: check if this is correct; original code was: width -=dist;
            // and adjust clip rect
            oldvalue = layout.mClipRect.left;
            //on ios [PDEBuildingUnits nativePixel] -> 1.0f on android
            layout.mClipRect.left = layout.mLayoutRect.left + 1;
            layout.mClipRect.right -= layout.mClipRect.left - oldvalue;  // ToDo: check if this is correct;original code was: width -= ..;
        } else if (mRadioAlignment == PDEConstants.PDEAlignment.PDEAlignmentCenter) {
            // centered, aligned to screen pixels
            radioX = xoffset + PDEBuildingUnits.roundToScreenCoordinates((width-mRadioUsedSize)/2.0f);
        } else if (mRadioAlignment == PDEConstants.PDEAlignment.PDEAlignmentRight) {
            // right, one BU from border
            radioX = xoffset + PDEBuildingUnits.roundToScreenCoordinates((width-mRadioUsedSize)/2.0f);
            // adjusting distance
            dist = PDEBuildingUnits.BU() + mRadioUsedSize;
            // adjust layout rect
            layout.mLayoutRect.right -= dist; // ToDo check if correct; replaced width by right
            // and adjust clip rect
            oldvalue = layout.mLayoutRect.left + layout.mLayoutRect.width();
            layout.mClipRect.right = (int) oldvalue - 1 - layout.mClipRect.left;  // ToDo check if correct; replaced width by right
        } else {
            // unknown, default
            radioX = 0.0f;
        }

        // set positions
        PDEAbsoluteLayout.LayoutParams sunkenLayerParams = (PDEAbsoluteLayout.LayoutParams) mSunkenLayer.getLayoutParams();
        sunkenLayerParams.x = PDEBuildingUnits.roundToScreenCoordinates(radioX);
        sunkenLayerParams.y = PDEBuildingUnits.roundToScreenCoordinates(radioY);
        // also width & height???
        mSunkenLayer.setLayoutParams(sunkenLayerParams);

        PDEAbsoluteLayout.LayoutParams iconLayerParams = (PDEAbsoluteLayout.LayoutParams) mMarkerView.getLayoutParams();
        // center marker Y inside radio, round to screen
        markerY = radioY + PDEBuildingUnits.roundToScreenCoordinates((mRadioUsedSize-mMarkerUsedSize)/2.0f);
        // marker X centered inside radio, round to screen
        markerX = radioX + PDEBuildingUnits.roundToScreenCoordinates((mRadioUsedSize-mMarkerUsedSize)/2.0f);

        iconLayerParams.x = PDEBuildingUnits.roundToScreenCoordinates(markerX);
        iconLayerParams.y = PDEBuildingUnits.roundToScreenCoordinates(markerY);
        // also width & height???
        mMarkerView.setLayoutParams(iconLayerParams);
    }


    @Override
    public void collectHints(PDEDictionary hints) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setHints(PDEDictionary hints) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void collectButtonPaddingRequest(PDEButtonPadding padding) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
