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

import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEDictionary;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableCornerBox;
import de.telekom.pde.codelibrary.ui.elements.wrapper.PDEViewWrapper;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;


//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerBackgroundPlate
//----------------------------------------------------------------------------------------------------------------------



/**
 * @brief Background for an Plate button.
 *
 * Color gradient, frame, inner shadow on pressed.
 */
class PDEButtonLayerBackgroundPlate extends PDEAbsoluteLayout implements PDEButtonLayerInterface {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEButtonLayerBackgroundPlate.class.getName();

    // debug messages switch
    private final static boolean DEBUGPARAMS = false;

    // local parameters needed
    PDEParameterDictionary mParameters;
    PDEParameter mParamColor;

    // drawables
    //PDEDrawableShape mMainDrawable;
    PDEDrawableCornerBox mMainDrawable;

    // additional helper variables
    float mCornerRadius;

    // agent helpers
    PDEAgentHelper mAgentHelper;



    // global variables
    //
    public static PDEDictionary PDEButtonLayerBackgroundPlateGlobalColorDefault = null;

    public PDEButtonLayerBackgroundPlate(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PDEButtonLayerBackgroundPlate(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * @brief Class initialization.
     */
    public PDEButtonLayerBackgroundPlate(Context context) {

        super(context);
        init(context);

    }

    private void init(Context context) {
        // read default dictionaries
        PDEButtonLayerBackgroundPlateGlobalColorDefault = PDEComponentHelpers.readDefaultColorDictionary(
                "dt_button_flat_color_defaults");

        // init
        mParameters = null;

        // set empty complex parameters
        mParamColor = new PDEParameter();

        // agent helper
        mAgentHelper = new PDEAgentHelper();


        setClipChildren(false);
        //mMainDrawable = new PDEDrawableShape();
        mMainDrawable = new PDEDrawableCornerBox();
        addView(mMainDrawable.getWrapperView());
        //mMainDrawable.setElementRoundedCornerConfiguration(PDEDrawableCornerBox.PDEDrawableCornerBoxAllCorners);
        mMainDrawable.setElementBorderColor(PDEColor.valueOf("DTTransparentBlack"));

        // further rasterization... -> not now, do more tests and only turn it on if the button is stable / not animated
        //mCollectionLayer.rasterizationScale = [PDEBuildingUnits scale];
        //mCollectionLayer.shouldRasterize = YES;

        // take over the default parameters from layer (iOS defaults of CALayer)
        mCornerRadius = 0.0f;

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
        if (force || !PDEParameterDictionary.areParametersEqual(mParameters, oldParams, PDEButton.PDEButtonParameterColor)) {
            prepareColors();
        }

        // non-animated parameters are simpler to handle: change management is in internal functions.
        prepareCornerRadius();
        prepareRoundedCornerConfiguration();
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

        // debug output
        if (DEBUGPARAMS) {
            mParamColor.debugOut("Color before building");
        }

        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamColor, PDEButtonLayerBackgroundPlateGlobalColorDefault,
                "DTTransparentBlack",
                PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // debug output
        if (DEBUGPARAMS) {
            mParamColor.debugOut("Color after building");
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
    }

    /**
     * @brief Prepare corner configuration parameter set.
     *
     * Corner configuration is a non-animated parameter at the moment.
     */
    protected void prepareRoundedCornerConfiguration() {
        int config;

        // create a copy of the new corner radius
        config = (int) mParameters.parameterFloatForName(PDEButton.PDEButtonParameterRoundedCornerConfiguration,
                PDEDrawableCornerBox.PDEDrawableCornerBoxAllCorners);

        // check for changes
        if (config == getMainDrawable().getElementRoundedCornerConfiguration()) {
            return;
        }

        // remember & apply
        getMainDrawable().setElementRoundedCornerConfiguration(config);
    }





//----- animated parameter updates -------------------------------------------------------------------------------------


    /**
     * @brief Update colors (fully animated).
     */
    protected void updateColors() {
        PDEColor mainColor;

        // interpolate colors by calling complex logic color interpolation helper
        mainColor = PDEComponentHelpers.interpolateColor(mParamColor, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);

        // set the background color
        //getMainDrawable().setElementBackgroundColor(mainColor.getIntegerColor());
        getMainDrawable().setElementBackgroundColor(mainColor);
    }



//----- layout ---------------------------------------------------------------------------------------------------------



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (DEBUGPARAMS) {
            Log.d(LOG_TAG, "onSizeChanged "+w+", "+h);
        }

        mMainDrawable.getWrapperView().setViewLayoutRect(new Rect(0, 0, w, h));
        mMainDrawable.getWrapperView().measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                                               MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
    }


    /**
    * @brief Collect hints.
    *
    * Transparent background is our default -> this needs to be hinted to other layers.
    */
    @Override
    public void collectHints(PDEDictionary hints) {
        // we also have to hint that the default color is transparent (otherwise the others won't be able to correctly determine
        // the default color if nothing is set and assume DTUIInteractive as default)
        hints.put(PDEButton.PDEButtonHintDefaultColor, new PDEColor(PDEColor.valueOf("DTTransparentBlack")));
    }


    /**
     * @brief Set hints.
     *
     * Empty implementation; No hints to set.
     */
    @Override
    public void setHints(PDEDictionary hints) {
    }


    public void collectButtonPaddingRequest(PDEButtonPadding padding){

    }


//----- Properties Setter/Getter -----------------------------------------------------------------------------------


    /**
     * @brief Get view of main background
     */
    public PDEViewWrapper getMainView() {
        return mMainDrawable.getWrapperView();
    }


    /**
     * @brief Set new drawable of main background
     */
    public void setMainDrawable(PDEDrawableCornerBox drawable) {
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
    public PDEDrawableCornerBox getMainDrawable(){
        return mMainDrawable;
    }

}
