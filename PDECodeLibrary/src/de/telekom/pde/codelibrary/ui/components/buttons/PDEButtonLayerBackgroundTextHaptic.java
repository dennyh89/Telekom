/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableSunkenArea;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.PDEDictionary;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;



//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerBackgroundTextHaptic
//----------------------------------------------------------------------------------------------------------------------


class PDEButtonLayerBackgroundTextHaptic extends PDEAbsoluteLayout implements PDEButtonLayerInterface {

    /**
     * @brief Global tag for log outputs.
     */
	@SuppressWarnings("unused")
    private final static String LOG_TAG = PDEButtonLayerBackgroundTextHaptic.class.getName();
    private final static boolean DEBUGPARAMS = false;


    // parameters needed
    private PDEParameterDictionary mParameters;
    private PDEParameter mParamColor;
    private PDEParameter mParamBorderColor;
    private PDEParameter mParamFillColor;

    // agent helpers
    PDEAgentHelper mAgentHelper;
    PDEDrawableSunkenArea mSunkenDrawable;

    // global variables
    //
    public static PDEDictionary PDEButtonLayerBackgroundTextHapticGlobalColorDefault = PDEComponentHelpers.readDefaultColorDictionary("dt_button_gradient_color_defaults");
    public static PDEDictionary PDEButtonLayerBackgroundTextHapticGlobalBorderDefault = PDEComponentHelpers.readDefaultColorDictionary("dt_button_border_color_defaults");

    // helper
    protected boolean mDarkStyle = false;


    /**
     * @brief Constructor.
     */
    public PDEButtonLayerBackgroundTextHaptic(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * @brief Constructor.
     */
    public PDEButtonLayerBackgroundTextHaptic(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init (context);
    }


    /**
     * @brief Constructor.
     */
    public PDEButtonLayerBackgroundTextHaptic(Context context) {
        super(context);
        init(context);
    }


    private void init(Context context) {
        mParameters = null;
        mDarkStyle = PDECodeLibrary.getInstance().isDarkStyle();

        mSunkenDrawable = new PDEDrawableSunkenArea();
        if (mDarkStyle){
            mSunkenDrawable.setElementInnerShadowColor(PDEColor.valueOf("Black75Alpha"));
        } else {
            mSunkenDrawable.setElementInnerShadowColor(PDEColor.valueOf("Black40Alpha"));
        }
        addView(mSunkenDrawable.getWrapperView());


        // set empty complex parameters
        mParamColor = new PDEParameter();
        mParamBorderColor = new PDEParameter();
        mParamFillColor = new PDEParameter();

        // agent helper
        mAgentHelper = new PDEAgentHelper();

        // create the layer (holds the sunken layer for visuals)
        mSunkenDrawable.setElementInnerShadowOffset(new Point(0, 1));
        mSunkenDrawable.setElementInnerShadowBlurRadius(1.0f);
//        mSunkenDrawable.setElementInnerShadowOpacity(0.17f);
        mSunkenDrawable.setElementInnerShadowOpacity(1.0f);

        // forced set of parameter -> this sets defaults
        setParameters(new PDEParameterDictionary(), true);
    }

//----- PDEButtonLayerInterface implementation -----------------------------------------------------------------------------------


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
                || !PDEParameterDictionary.areParametersEqual(mParameters, oldParams, PDEButton.PDEButtonParameterColor)
                || !PDEParameterDictionary.areParametersEqual(mParameters, oldParams, PDEButton.PDEButtonParameterBorderColor)) {
            prepareColors();
        }
    }

    @Override
    public void collectHints(PDEDictionary hints) {
        // nothing to do in this layer
    }


    @Override
    public void setHints(PDEDictionary hints) {
        // nothing to do in this layer
    }


    @Override
    public void collectButtonPaddingRequest(PDEButtonPadding padding) {
        // nothing to do in this layer
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
        PDEAbsoluteLayoutHelper.setViewRect(mSunkenDrawable.getWrapperView(),new Rect(0, 0, width, height));
        mSunkenDrawable.getWrapperView().measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        mSunkenDrawable.setElementShapeRoundedRect(PDEBuildingUnits.oneThirdBU());
    }


    //----- animated parameters: preparation ---------------------------------------------------------------------------


    /**
     * @brief Prepare color parameter set. This prepares main and border color.
     *
     * Note that main color is quite sophisticated (automatic color generation). If border color is not
     * specified, a default color set is searched. If there is no default color set, border color is calculated by
     * using the darker gradients of the main color. If border color is specified, the same logic for border color
     * generation is used as for main state color generation.
     */
    private void prepareColors() {
        // set the new values
        mParamColor.setWithParameter(mParameters.parameterForName(PDEButton.PDEButtonParameterColor));
        mParamBorderColor.setWithParameter(mParameters.parameterForName(PDEButton.PDEButtonParameterBorderColor));

        // fill color starts out as copy from colors
        mParamFillColor.setWithParameter(mParameters.parameterForName(PDEButton.PDEButtonParameterColor));

        //!!!ANDY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!! CODE BELOW ONLY FOR 3.0 RELEASE - NEED BETTER SOLUTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        PDEColor color = mParameters.parameterColorForName(PDEButton.PDEButtonParameterColor);
        if(color!=null && !PDEButtonLayerBackgroundTextHapticGlobalBorderDefault.containsKey(color.getHexColorString())) {
            PDEDictionary dict = (PDEDictionary) PDEButtonLayerBackgroundTextHapticGlobalBorderDefault.get(PDEColor.valueOfColorID(R.color.DTGrey237).getHexColorString());
            PDEButtonLayerBackgroundTextHapticGlobalBorderDefault.put(color.getHexColorString(),dict);
        }
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!! CODE ABOVE ONLY FOR 3.0 RELEASE - NEED BETTER SOLUTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        // debug output
        if(DEBUGPARAMS) {
            mParamColor.debugOut("Color before building");
            mParamBorderColor.debugOut("Border before building");
            mParamFillColor.debugOut("Fill before building");
        }

        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamColor, PDEButtonLayerBackgroundTextHapticGlobalColorDefault,
                "DTLightUIInteractive", PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // calculate color border agent states
        PDEComponentHelpers.buildColors(mParamBorderColor, PDEButtonLayerBackgroundTextHapticGlobalBorderDefault,
                mParamColor, null, PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // if border color for a state is still not defined, calculate it from main agent colors (use gradient darker step)
        PDEComponentHelpers.fillBorderColors(mParamBorderColor, mParamColor);

        // convert fill colors to colors (explicit for now, integrate it into helpers later)
        mParamFillColor.convertToColor();

        // calculate brightened colors first
        PDEComponentHelpers.brightenColors(mParamFillColor);

        // calculate fill color agent states (after brighten step). Default color when nothing set is white here
        PDEComponentHelpers.buildColors(mParamFillColor, null, "DTWhite", PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // debug output
        if(DEBUGPARAMS) {
            mParamColor.debugOut("Color after building");
            mParamBorderColor.debugOut("Border after building");
            mParamFillColor.debugOut("Fill after building");
        }

        // and apply once
        updateColors();
    }


    /**
     * @brief Update colors (fully animated).
     */
    private void updateColors() {
        PDEColor mainColor,borderColor;

        // interpolate colors by calling complex logic color interpolation helper
        mainColor = PDEComponentHelpers.interpolateColor(mParamFillColor, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);
        borderColor = PDEComponentHelpers.interpolateColor(mParamBorderColor, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive,null);

        // set the gradient and border colors
        mSunkenDrawable.setElementBackgroundColor(mainColor);
        mSunkenDrawable.setElementBorderColor(borderColor);
    }
}
