/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.inputfields;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButtonLayerInterface;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShape;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDEViewWrapper;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.PDEDictionary;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;


//----------------------------------------------------------------------------------------------------------------------
//  PDEInputFieldClearButtonBackground
//----------------------------------------------------------------------------------------------------------------------



/**
 * @brief Background for an Rect button.
 *
 * Color gradient, frame, inner shadow on pressed.
 */
class PDEInputFieldClearButtonBackground extends PDEAbsoluteLayout implements PDEButtonLayerInterface {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEInputFieldClearButtonBackground.class.getName();

    // debug messages switch
    private final static boolean DEBUGPARAMS = false;
    private final static boolean SHOW_DEBUG_LOGS = false;

    // local parameters needed
    private PDEParameterDictionary mParameters;
    private PDEParameter mParamColor;

    // drawables
    private PDEDrawableShape mMainDrawable;

    // agent helpers
    private PDEAgentHelper mAgentHelper;

    // global variables
    //
    public static PDEDictionary PDEButtonLayerBackgroundRectGlobalColorDefault = null;



    /**
     * @brief Class initialization.
     */
    public PDEInputFieldClearButtonBackground(Context context) {
        super (context);
        init(context);
    }


    /**
     * @brief Class initialization.
     */
    public PDEInputFieldClearButtonBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    /**
     * @brief Class initialization.
     */
    public PDEInputFieldClearButtonBackground(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(Context context) {
        // read default dictionaries
        PDEButtonLayerBackgroundRectGlobalColorDefault = PDEComponentHelpers.readDefaultColorDictionary(
                                                                                R.xml.dt_button_flat_color_defaults);

        // init
        mParameters = null;

        // set empty complex parameters
        mParamColor = new PDEParameter();

        // agent helper
        mAgentHelper = new PDEAgentHelper();

        setClipChildren(false);
        mMainDrawable = new PDEDrawableShape();
        addView(mMainDrawable.getWrapperView());

        // further rasterization... -> not now, do more tests and only turn it on if the button is stable / not animated
        //mCollectionLayer.rasterizationScale = [PDEBuildingUnits scale];
        //mCollectionLayer.shouldRasterize = YES;

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
        if (force
                || !PDEParameterDictionary.areParametersEqual(mParameters, oldParams,
                                                              PDEButton.PDEButtonParameterColor)) {
            prepareColors();
        }
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
        PDEComponentHelpers.buildColors(mParamColor, PDEButtonLayerBackgroundRectGlobalColorDefault,
                "DTTransparentBlack",
                PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // debug output
        if (DEBUGPARAMS) {
            mParamColor.debugOut("Color after building");
        }

        // and apply once
        updateColors();
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

        // set the gradient and border colors
        getMainDrawable().setElementBackgroundColor(mainColor.getIntegerColor());
    }


//----- layout ---------------------------------------------------------------------------------------------------------


    /**
     * @brief Collect hints.
     *
     * Transparent background is our default -> this needs to be hinted to other layers.
     */
    @Override
    public void collectHints(PDEDictionary hints) {
        // we also have to hint that the default color is transparent (otherwise the others won't be able to correctly
        // determine the default color if nothing is set and assume DTUIInteractive as default)
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


    //----- view layout ------------------------------------------------------------------------------------------------

    /**
     *  @brief Size changed.
     *
     * @param width New width.
     * @param height New height.
     * @param oldWidth Old width.
     * @param oldHeight Old height.
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "onSizeChanged "+width+", "+height);
        }


        PDEAbsoluteLayoutHelper.setViewRect(mMainDrawable.getWrapperView(),new Rect(0, 0, width, height));
        mMainDrawable.getWrapperView().measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                                               MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
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

