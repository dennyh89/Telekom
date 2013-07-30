/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEDictionary;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableBorderLine;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShape;
import de.telekom.pde.codelibrary.ui.elements.wrapper.PDEViewWrapper;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerBackgroundIndicative
//----------------------------------------------------------------------------------------------------------------------



/**
 * @brief Background for an indicative button.
 *
 * Color gradient, frame, inner shadow on pressed.
 */
class PDEButtonLayerBackgroundIndicative extends PDEAbsoluteLayout implements PDEButtonLayerInterface {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEButtonLayerBackgroundIndicative.class.getName();
    // debug messages switch
    private final static boolean DEBUGPARAMS = false;
    private final static boolean SHOW_DEBUG_LOGS = false;

    // local parameters needed
    PDEParameterDictionary mParameters;
    PDEParameter mParamColor;
    PDEParameter mParamBorderColor;

    // Drawables
    PDEDrawableShape mMainDrawable;
    PDEDrawableBorderLine mBorderLineDrawable;

    // additional helper variables
    boolean mDarkStyle;
    float mCornerRadius;
    float mOutlineWidth;

    // agent helpers
    PDEAgentHelper mAgentHelper;

    // global variables
    //
    public static PDEDictionary PDEButtonLayerBackgroundIndicativeGlobalColorDefault = null;
    public static PDEDictionary PDEButtonLayerBackgroundIndicativeGlobalBorderLightDefault = null;
    public static PDEDictionary PDEButtonLayerBackgroundIndicativeGlobalBorderDarkDefault = null;


    public PDEButtonLayerBackgroundIndicative(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PDEButtonLayerBackgroundIndicative(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * @brief Class initialization.
     */
    public PDEButtonLayerBackgroundIndicative(Context context) {
        super(context);
        init(context);

    }

    private void init(Context context) {
        // read default dictionaries
        PDEButtonLayerBackgroundIndicativeGlobalColorDefault = PDEComponentHelpers.readDefaultColorDictionary(
                "dt_button_flat_color_defaults");
        PDEButtonLayerBackgroundIndicativeGlobalBorderLightDefault = PDEComponentHelpers.readDefaultColorDictionary(
                "dt_button_indicative_border_color_light_defaults");
        PDEButtonLayerBackgroundIndicativeGlobalBorderDarkDefault = PDEComponentHelpers.readDefaultColorDictionary(
                "dt_button_indicative_border_color_dark_defaults");

        // init
        mParameters = null;
        //mDisplayRect = new Rect(0, 0, 0, 0);
        mDarkStyle = PDECodeLibrary.getInstance().isDarkStyle();

        // set empty complex parameters
        mParamColor = new PDEParameter();
        mParamBorderColor = new PDEParameter();

        // agent helper
        mAgentHelper = new PDEAgentHelper();

        setClipChildren(false);
        mMainDrawable = new PDEDrawableShape();
        addView(mMainDrawable.getWrapperView());
        mBorderLineDrawable = new PDEDrawableBorderLine();
        addView(mBorderLineDrawable.getWrapperView());

        // further rasterization... -> not now, do more tests and only turn it on if the button is stable / not animated
        //mCollectionLayer.rasterizationScale = [PDEBuildingUnits scale];
        //mCollectionLayer.shouldRasterize = YES;

        // take over the default parameters from layer (iOS defaults of CALayer)
        mCornerRadius = 0.0f;
        mOutlineWidth = 0.0f;

        // apply currently nonparametrized default configuration
        setOutlineWidth(1.0f);

        // forced set of parameter -> this sets defaults
        setParameters(new PDEParameterDictionary(), true);
    }



    /**
     * @brief Layer access.
     */
    @Override
    public PDEAbsoluteLayout getLayer() {
        return this;
    }

    /**
     * @brief Process agent events.
     */
    @Override
    public void agentEvent(PDEEvent event) {
        boolean needsUpdate;

        // pass on agent events to agent helper
        needsUpdate = mAgentHelper.processAgentEvent(event);

        // update if necessary
        if (needsUpdate) {
            // update animatable parameters on change
            updateColors();
        }
    }


    /**
     * @brief Set button parameters.
     *
     * Check for changes; if changed, determine missing parameters and fill them with defaults, then apply.
     */
    @Override
    public void setParameters(PDEParameterDictionary parameters, boolean force) {
        PDEParameterDictionary oldParams;

        // for local change management keep the old params for a while
        oldParams = mParameters;

        // completely copy the new ones to have a reference for further change management
        mParameters = parameters.copy();

        // check for color or border changes (all in one go)
        if (force || !PDEParameterDictionary.areParametersEqual(mParameters, oldParams, PDEButton.PDEButtonParameterColor)
            ||
            !PDEParameterDictionary.areParametersEqual(mParameters, oldParams, PDEButton.PDEButtonParameterBorderColor)) {
            prepareColors();
        }

        // non-animated parameters are simpler to handle: change management is in internal functions.
        prepareCornerRadius();
    }


//----- animated parameters: preparation -------------------------------------------------------------------------------


    /**
     * @brief Prepare color parameter set. This prepares main and border color.
     *
     * Note that main color is quite sophisticated (automatic color generation). If border color is not
     * specified, a default color set is searched. If there is no default color set, border color is calculated by
     * using the darker gradients of the main color. If border color is specified, the same logic for border color
     * generation is used as for main state color generation.
     */
    protected void prepareColors() {
        // set the new values
        mParamColor.setWithParameter(mParameters.parameterForName(PDEButton.PDEButtonParameterColor));
        mParamBorderColor.setWithParameter(mParameters.parameterForName(PDEButton.PDEButtonParameterBorderColor));


        // debug output
        if (DEBUGPARAMS) {
            mParamColor.debugOut("Color before building");
            mParamBorderColor.debugOut("Border before building");
        }

        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamColor, PDEButtonLayerBackgroundIndicativeGlobalColorDefault,
                "DTTransparentBlack",
                PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // calculate color agent states
        if (mDarkStyle) {
            if(DEBUGPARAMS){
                Log.d(LOG_TAG,"DARKSTYLE");
            }
            PDEComponentHelpers.buildColors(mParamBorderColor, PDEButtonLayerBackgroundIndicativeGlobalBorderDarkDefault,
                    mParamColor, null, PDEAgentHelper.PDEAgentHelperAnimationInteractive);
        } else {
            if(DEBUGPARAMS){
                Log.d(LOG_TAG,"NOT DARKSTYLE");
            }
            PDEComponentHelpers.buildColors(mParamBorderColor, PDEButtonLayerBackgroundIndicativeGlobalBorderLightDefault,
                    mParamColor, null, PDEAgentHelper.PDEAgentHelperAnimationInteractive);
        }

        // if border color for a state is still not defined, calculate it from main agent colors (use gradient darker step)
        PDEComponentHelpers.fillBorderColors(mParamBorderColor, mParamColor);

        // debug output
        if (DEBUGPARAMS) {
            mParamColor.debugOut("Color after building");
            mParamBorderColor.debugOut("Border after building");
        }

        // and apply once
        updateColors();
    }



//----- non-animated parameters: preparation and application -----------------------------------------------------------


    /**
     * @brief Prepare corner radius parameter set.
     *
     * Corner radius is a non-animated parameter at the moment.
     */
    protected void prepareCornerRadius() {
        float radius;

        // create a copy of the new corner radius
        radius = mParameters.parameterFloatForName(PDEButton.PDEButtonParameterCornerRadius,
                                                   (float) PDEBuildingUnits.oneThirdBU());

        // check for changes
        if (radius == mCornerRadius) {
            return;
        }

        // remember
        mCornerRadius = radius;


        // apply
        getMainDrawable().setElementCornerRadius(mCornerRadius);
        mBorderLineDrawable.setElementCornerRadius(mCornerRadius);
    }


//----- graphical properties -------------------------------------------------------------------------------------------


    /**
     * @brief Set button outline.
     *
     * For high resolution displays use a subpixel width.
     */
    public void setOutlineWidth(float width) {
        // check for changes
        if (width == mOutlineWidth) {
            return;
        }

        // remember
        mOutlineWidth = width;

        // apply
//        mBorderLineView.setElementBorderWidth(mOutlineWidth);
        mBorderLineDrawable.setElementBorderWidth(mOutlineWidth);
    }


    /**
     * @brief Set the dark style hint.
     */
    public void setDarkStyle(boolean darkStyle) {
        // any change?
        if (mDarkStyle == darkStyle) {
            return;
        }

        // remember
        mDarkStyle = darkStyle;

        // color needs to be updated
        prepareColors();
    }


//----- animated parameter updates -------------------------------------------------------------------------------------


    /**
     * @brief Update colors (fully animated).
     */
    protected void updateColors() {
        PDEColor mainColor,  borderColor;

        // interpolate colors by calling complex logic color interpolation helper
        mainColor = PDEComponentHelpers.interpolateColor(mParamColor, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);
        borderColor = PDEComponentHelpers.interpolateColor(mParamBorderColor, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);

        // set the gradient and border colors
        getMainDrawable().setElementBackgroundColor(mainColor.getIntegerColor());
        mBorderLineDrawable.setElementBorderColor(borderColor.getIntegerColor());
    }


//----- layout ---------------------------------------------------------------------------------------------------------


    /**
     * @brief We have a special text color on transparent background.
     *
     * Transparent background is our default. Note that we're using the dark style hint to
     * set this. This will only work reliably if the dark style hint is set as a user hint.
     */
    @Override
    public void collectHints(PDEDictionary hints) {
        //hints.put(PDEButton.PDEButtonHint3DStyle, new Boolean(true));
        // default text color depends on style
        if (mDarkStyle){
            hints.put(PDEButton.PDEButtonHintTextOnTransparentColor,
                      new PDEColor(PDEColor.valueOf("DTDarkUIIndicativeText")));
        } else {
            hints.put(PDEButton.PDEButtonHintTextOnTransparentColor,
                      new PDEColor(PDEColor.valueOf("DTLightUIIndicativeText")));
        }
        // we also have to hint that the default color is transparent (otherwise the others won't be able to correctly determine
        // the default color if nothing is set and assume DTUIInteractive as default)
        hints.put(PDEButton.PDEButtonHintDefaultColor, new PDEColor(PDEColor.valueOf("DTTransparentBlack")));
    }

    /**
     * @brief Set hints.
     *
     * Extract the interesting hints and set them directly as parameters.
     */
    @Override
    public void setHints(PDEDictionary hints) {
        // extract dark style (background) hint
        setDarkStyle(PDEComponentHelpers.extractDarkStyleHint(hints));
    }


    public void collectButtonPaddingRequest(PDEButtonPadding padding){

    }

//----- view layout ----------------------------------------------------------------------------------------------------

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "onSizeChanged "+w+", "+h);
        }


        mMainDrawable.getWrapperView().setViewLayoutRect(new Rect(0, 0, w, h));
        mBorderLineDrawable.getWrapperView().setViewLayoutRect(new Rect(0, 0, w, h));
        mBorderLineDrawable.setElementShapeRoundedRect(mCornerRadius);
        mBorderLineDrawable.getWrapperView().measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                                                     MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
    }



//----- Properties Setter/Getter ---------------------------------------------------------------------------------------


    /**
     * @brief Get view of main background
     */
    public PDEViewWrapper getMainView() {
        return mMainDrawable.getWrapperView();
    }


    /**
     * @brief Set new drawable of main background
     */
    public void setMainDrawable(PDEDrawableShape drawable) {
        if(drawable == mMainDrawable || drawable == null) return;
        // remove old view
        if (mMainDrawable != null) removeView(mMainDrawable.getWrapperView());
        // remember
        mMainDrawable = drawable;
        // add view
        addView(mMainDrawable.getWrapperView());
    }

    /**
     * @brief Get drawable of main background
     */
    public PDEDrawableShape getMainDrawable(){
        return mMainDrawable;
    }
}


