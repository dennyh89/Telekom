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
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableBorderLine;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShape;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDEViewWrapper;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.PDEDictionary;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;



//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerBackgroundFlat
//----------------------------------------------------------------------------------------------------------------------



/**
 * @brief Background for an Flat button.
 *
 * Color gradient, frame, inner shadow on pressed.
 */
class PDEButtonLayerBackgroundFlat extends PDEAbsoluteLayout implements PDEButtonLayerInterface {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEButtonLayerBackgroundFlat.class.getName();
    // debug messages switch
    private final static boolean DEBUGPARAMS = false;
    private final static boolean SHOW_DEBUG_LOGS = false;

    // local parameters needed
    PDEParameterDictionary mParameters;
    PDEParameter mParamColor;
    PDEParameter mParamBorderColor;


    // configuration
    PDEColor mDefaultColor;

    // Drawables
    PDEDrawableShape mMainDrawable;
    PDEDrawableBorderLine mBorderLineDrawable;

    // additional helper variables
    float mCornerRadius;
    float mOutlineWidth;

    // agent helpers
    PDEAgentHelper mAgentHelper;





    // global variables
    //
    public static PDEDictionary PDEButtonLayerBackgroundFlatGlobalColorDefault = PDEComponentHelpers.readDefaultColorDictionary("dt_button_flat_color_defaults");
    public static PDEDictionary PDEButtonLayerBackgroundFlatGlobalBorderDefault = PDEComponentHelpers.readDefaultColorDictionary("dt_button_border_color_defaults");

    @SuppressWarnings("unused")
    public PDEButtonLayerBackgroundFlat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressWarnings("unused")
    public PDEButtonLayerBackgroundFlat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * @brief Class initialization.
     */
    public PDEButtonLayerBackgroundFlat(Context context) {
        super(context);
        init(context);

    }

    @SuppressWarnings("unused")
    private void init(Context context){
        // init
        mParameters = null;
        mDefaultColor = PDEColor.DTUIInteractiveColor();

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

        //!!!ANDY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!! CODE BELOW ONLY FOR 3.0 RELEASE - NEED BETTER SOLUTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        PDEColor color = mParameters.parameterColorForName(PDEButton.PDEButtonParameterColor);
        if(color==null)color = mDefaultColor;
        if(color!=null && !PDEButtonLayerBackgroundFlatGlobalBorderDefault.containsKey(color.getHexColorString())) {
            PDEDictionary dict = (PDEDictionary)PDEButtonLayerBackgroundFlatGlobalBorderDefault.get(PDEColor.valueOfColorID(R.color.DTGrey237).getHexColorString());
            PDEButtonLayerBackgroundFlatGlobalBorderDefault.put(color.getHexColorString(),dict);
        }
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!! CODE ABOVE ONLY FOR 3.0 RELEASE - NEED BETTER SOLUTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        // debug output
        if (DEBUGPARAMS) {
            mParamColor.debugOut("Color before building");
            mParamBorderColor.debugOut("Border before building");
        }

        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamColor, PDEButtonLayerBackgroundFlatGlobalColorDefault,
                mDefaultColor,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamBorderColor, PDEButtonLayerBackgroundFlatGlobalBorderDefault,
                mParamColor, null, PDEAgentHelper.PDEAgentHelperAnimationInteractive);

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
    public void setDefaultColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mDefaultColor.getIntegerColor()) {
            return;
        }

        // remember
        mDefaultColor = color;

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
     * @brief Collect hints.
     *
     * Empty implementation; No hints to collect.
     */
    @Override
    public void collectHints(PDEDictionary hints) {
    }


    /**
     * @brief Set hints.
     *
     * Extract the interesting hints and set them directly as parameters.
     */
    @Override
    public void setHints(PDEDictionary hints) {
        // extract dark style (background) hint
        setDefaultColor(PDEComponentHelpers.extractDefaultColorHint(hints));
    }


    public void collectButtonPaddingRequest(PDEButtonPadding padding){

    }

//----- view layout ----------------------------------------------------------------------------------------------------

    /**
     *  @brief Size changed.
     *
     * @param w New width.
     * @param h New height.
     * @param oldW Old width.
     * @param oldH Old height.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "onSizeChanged " + w + ", " + h);
        }


        PDEAbsoluteLayoutHelper.setViewRect(mMainDrawable.getWrapperView(),new Rect(0, 0, w, h));
        PDEAbsoluteLayoutHelper.setViewRect(mBorderLineDrawable.getWrapperView(),new Rect(0, 0, w, h));
        mBorderLineDrawable.setElementShapeRoundedRect(mCornerRadius);
        mBorderLineDrawable.getWrapperView().measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                                                     MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
        mMainDrawable.getWrapperView().measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                                               MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
    }




//----- Properties Setter/Getter -----------------------------------------------------------------------------------


    /**
     * @brief Get view of main background
     */
    @SuppressWarnings("unused")
    public PDEViewWrapper getMainView() {
        return mMainDrawable.getWrapperView();
    }


    /**
     * @brief Set new drawable of main background
     */
    @SuppressWarnings("unused")
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

