/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableArea;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.PDEDictionary;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;



//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerBackgroundTextHaptic
//----------------------------------------------------------------------------------------------------------------------


class PDEButtonLayerBackgroundTextFlat extends PDEAbsoluteLayout implements PDEButtonLayerInterface {

    /**
     * @brief Global tag for log outputs.
     */
	@SuppressWarnings("unused")
    private final static String LOG_TAG = PDEButtonLayerBackgroundTextFlat.class.getName();
    private final static boolean DEBUG_PARAMS = false;


    // parameters needed
    private PDEParameterDictionary mParameters;
    private PDEParameter mParamColor;
    private PDEParameter mParamBorderColor;
    private PDEParameter mParamFillColor;

    // agent helpers
    private PDEAgentHelper mAgentHelper;
    private PDEDrawableArea mAreaDrawable;

    // global variables
    //
    public static PDEDictionary PDEButtonLayerBackgroundTextFlatGlobalColorDefault
            = PDEComponentHelpers.readDefaultColorDictionary(R.xml.dt_button_flat_color_defaults);
    public static PDEDictionary PDEButtonLayerBackgroundTextFlatGlobalBorderDefault
            = PDEComponentHelpers.readDefaultColorDictionary(R.xml.dt_button_border_color_defaults);


    /**
     * @brief Constructor.
     */
    public PDEButtonLayerBackgroundTextFlat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * @brief Constructor.
     */
    public PDEButtonLayerBackgroundTextFlat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init (context);
    }


    /**
     * @brief Constructor.
     */
    public PDEButtonLayerBackgroundTextFlat(Context context) {
        super(context);
        init(context);
    }


    private void init(Context context) {
        mParameters = null;

        mAreaDrawable = new PDEDrawableArea();
        addView(mAreaDrawable.getWrapperView());


        // set empty complex parameters
        mParamColor = new PDEParameter();
        mParamBorderColor = new PDEParameter();
        mParamFillColor = new PDEParameter();

        // agent helper
        mAgentHelper = new PDEAgentHelper();

        // forced set of parameter -> this sets defaults
        setParameters(new PDEParameterDictionary(), true);
    }

//----- PDEButtonLayerInterface implementation -------------------------------------------------------------------------


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
                            PDEButton.PDEButtonParameterColor)
                || !PDEParameterDictionary.areParametersEqual(mParameters, oldParams,
                            PDEButton.PDEButtonParameterBorderColor)) {
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
        // call super
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        PDEAbsoluteLayoutHelper.setViewRect(mAreaDrawable.getWrapperView(), new Rect(0, 0, width, height));
        mAreaDrawable.getWrapperView().measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        mAreaDrawable.setElementShapeRoundedRect(PDEBuildingUnits.oneThirdBU());
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
        if (color != null
                && !PDEButtonLayerBackgroundTextFlatGlobalBorderDefault.containsKey(color.getHexColorString())) {
            PDEDictionary dict = (PDEDictionary) PDEButtonLayerBackgroundTextFlatGlobalBorderDefault.get(
                    PDEColor.valueOfColorID(R.color.DTGrey237).getHexColorString());
            PDEButtonLayerBackgroundTextFlatGlobalBorderDefault.put(color.getHexColorString(),dict);
        }
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!! CODE ABOVE ONLY FOR 3.0 RELEASE - NEED BETTER SOLUTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        // debug output
        if (DEBUG_PARAMS) {
            mParamColor.debugOut("Color before building");
            mParamBorderColor.debugOut("Border before building");
            mParamFillColor.debugOut("Fill before building");
        }

        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamColor, PDEButtonLayerBackgroundTextFlatGlobalColorDefault,
                "DTLightUIInteractive", PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // calculate color border agent states
        PDEComponentHelpers.buildColors(mParamBorderColor, PDEButtonLayerBackgroundTextFlatGlobalBorderDefault,
                mParamColor, null, PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // if border color for a state is still not defined, calculate it from main agent colors
        // (use gradient darker step)
        PDEComponentHelpers.fillBorderColors(mParamBorderColor, mParamColor);

        // convert fill colors to colors (explicit for now, integrate it into helpers later)
        mParamFillColor.convertToColor();

        // calculate brightened colors first
        PDEComponentHelpers.brightenColors(mParamFillColor);

        // calculate fill color agent states (after brighten step). Default color when nothing set is white here
        PDEComponentHelpers.buildColors(mParamFillColor, null, "DTWhite",
                PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // debug output
        if (DEBUG_PARAMS) {
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
        mAreaDrawable.setElementBackgroundColor(mainColor);
        mAreaDrawable.setElementBorderColor(borderColor);
    }
}
