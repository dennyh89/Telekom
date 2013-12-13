/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;

//History
// 10.10.2012 - only changed the innershadow parameter things.


import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableBorderLine;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableGradientShape;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedInnerShadow;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDEViewWrapper;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.PDEDictionary;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;


//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerBackgroundHaptic
//----------------------------------------------------------------------------------------------------------------------



/**
 * @brief Background for a haptic button.
 *
 * Color gradient, frame, inner shadow on pressed.
 */
class PDEButtonLayerBackgroundHaptic extends PDEAbsoluteLayout implements PDEButtonLayerInterface {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEButtonLayerBackgroundHaptic.class.getName();
    // debug messages switch
    private final static boolean DEBUGPARAMS = false;
    private final static boolean SHOW_DEBUG_LOGS = false;


    // local parameters needed
    PDEParameterDictionary mParameters;
    PDEParameter mParamColor;
    PDEParameter mParamBorderColor;
    PDEParameter mParamInnerShadowStrength;

    // configuration
    PDEColor mDefaultColor;
    float mInnerShadowOpacity;

    // drawables
    PDEDrawableGradientShape mMainDrawable;
    PDEDrawableBorderLine mBorderLineDrawable;
    PDEDrawableShapedInnerShadow mInnerShadowDrawable;

    // additional helper variables
    float mCornerRadius;
    float mOutlineWidth;


    // agent helpers
    PDEAgentHelper mAgentHelper;




    // global variables
    //
    public static PDEDictionary PDEButtonLayerBackgroundHapticGlobalColorDefault =  PDEComponentHelpers.readDefaultColorDictionary("dt_button_gradient_color_defaults");
    public static PDEDictionary PDEButtonLayerBackgroundHapticGlobalBorderDefault = PDEComponentHelpers.readDefaultColorDictionary("dt_button_border_color_defaults");


    @SuppressWarnings("unused")
    public PDEButtonLayerBackgroundHaptic(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressWarnings("unused")
    public PDEButtonLayerBackgroundHaptic(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * @brief Class initialization.
     */
    public PDEButtonLayerBackgroundHaptic(Context context) {
        super(context);
        init(context);
    }

    @SuppressWarnings("unused")
    private void init(Context context) {
        // init
        mParameters = null;
        mDefaultColor = PDEColor.valueOf("DTUIInteractive");

        // constants for derivation in parameter setting
        //mInnerShadowOpacity = 0.28f;
        mInnerShadowOpacity = 1.0f;

        // set empty complex parameters
        mParamColor = new PDEParameter();
        mParamBorderColor = new PDEParameter();
        mParamInnerShadowStrength = new PDEParameter();

        // agent helper
        mAgentHelper = new PDEAgentHelper();

        // create the layer structure
        setClipChildren(false);

        // create & add main background
        mMainDrawable = new PDEDrawableGradientShape();
        addView(mMainDrawable.getWrapperView());
        // create & add border line
        mBorderLineDrawable = new PDEDrawableBorderLine();
        addView(mBorderLineDrawable.getWrapperView());
        // create & add inner shadow
        mInnerShadowDrawable = new PDEDrawableShapedInnerShadow();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mInnerShadowDrawable.getWrapperView(),lp);


        // take over the default parameters from layer (iOS defaults of CALayer)
        mCornerRadius = 0.0f;
        mOutlineWidth = 0.0f;

        // apply currently nonparametrized default configuration
        // border line
        setOutlineWidth(1.0f);

        // inner shadow
        //mInnerShadowDrawable.setElementShapeColor(PDEColor.valueOf("DTBlack"));
        mInnerShadowDrawable.setElementShapeColor(PDEColor.valueOf("Black40Alpha"));
//        mInnerShadowDrawable.setElementBlurRadius(PDEBuildingUnits.exactOneForthBU());
        mInnerShadowDrawable.setElementBlurRadius(PDEBuildingUnits.oneSixthBU());
        mInnerShadowDrawable.setElementLightIncidenceOffset(new PointF(0.0f,PDEBuildingUnits.oneTwelfthsBU()));

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
            updateInnerShadow();
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

        // check for inner shadow changes
        if (force) {
            prepareInnerShadowStrength();
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
        if(color!=null && !PDEButtonLayerBackgroundHapticGlobalBorderDefault.containsKey(color.getHexColorString())) {
            PDEDictionary dict = (PDEDictionary)PDEButtonLayerBackgroundHapticGlobalBorderDefault.get(PDEColor.valueOfColorID(R.color.DTGrey237).getHexColorString());
            PDEButtonLayerBackgroundHapticGlobalBorderDefault.put(color.getHexColorString(),dict);
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
        PDEComponentHelpers.buildColors(mParamColor, PDEButtonLayerBackgroundHapticGlobalColorDefault, mDefaultColor,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // calculate missing gradient colors
        PDEComponentHelpers.fillGradientColors(mParamColor);

        // calculate border agent states
        PDEComponentHelpers.buildColors(mParamBorderColor, PDEButtonLayerBackgroundHapticGlobalBorderDefault,
                mParamColor, null, PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // if border for a state is still not defined, calculate it from main colors
        PDEComponentHelpers.fillBorderColors(mParamBorderColor, mParamColor);

        // debug output
        if (DEBUGPARAMS) {
            mParamColor.debugOut("Color after building");
            mParamBorderColor.debugOut("Border after building");
        }

        // and apply once
        updateColors();
    }


    /**
     * @brief Prepare shadow strength parameter set.
     *
     * Shadow strengs animates the shadow as a whole. The range is 0.0..1.0.
     */
    protected void prepareInnerShadowStrength() {
        // set the new values, clear old
        mParamInnerShadowStrength.removeAllObjects();

        mParamInnerShadowStrength.setWithDictionary(new PDEDictionary("default.idle", "0.0",
                                                                     "default.down", "1.0"));

        // debug output
        if (DEBUGPARAMS) {
            mParamInnerShadowStrength.debugOut("Inner shadow before building");
        }

        // calculate agent states
        PDEComponentHelpers.buildValues(mParamInnerShadowStrength, null, null, PDEAgentHelper.PDEAgentHelperAnimationDown);

        // and convert to number
        mParamInnerShadowStrength.convertToNumber();

        // debug output
        if (DEBUGPARAMS) {
            mParamInnerShadowStrength.debugOut("Inner shadow after building");
        }

        // and apply once
        updateInnerShadow();
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
        PDEColor topColor, mainColor, bottomColor, borderColor;

        // interpolate colors by calling complex logic color interpolation helper
        topColor = PDEComponentHelpers.interpolateColor(mParamColor, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive,
                PDEButton.PDEButtonColorSuffixLighter);
        mainColor = PDEComponentHelpers.interpolateColor(mParamColor, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);
        bottomColor = PDEComponentHelpers.interpolateColor(mParamColor, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive,
                PDEButton.PDEButtonColorSuffixDarker);
        borderColor = PDEComponentHelpers.interpolateColor(mParamBorderColor, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);

        // set the gradient and border colors
        getMainDrawable().setElementColors(topColor.getIntegerColor(), mainColor.getIntegerColor(),
                                           bottomColor.getIntegerColor());
        mBorderLineDrawable.setElementBorderColor(borderColor.getIntegerColor());
    }



    /**
     * @brief Update shadow based on animation.
     */
    protected void updateInnerShadow() {
        float innerShadowFactor;

        // interpolate colors by calling complex logic color interpolation helper
        innerShadowFactor = PDEComponentHelpers.interpolateFloat(mParamInnerShadowStrength, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationDown, null);

        // set opacity
        mInnerShadowDrawable.setElementShapeOpacity(innerShadowFactor * mInnerShadowOpacity);
    }


    /**
     * @brief Update paths for shadow layers.
     */
    protected void updatePaths(int width, int height) {
        Rect rect;

        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "updatePaths getWidth: "+ width + " getHeight: "+height);
        }

        // inner shadow rect is main rect minus the border (bordersize is always 1)
        //rect = new Rect(mLayout.mLayoutRect);
        rect = new Rect(0, 0, width, height);

        rect.set(rect.left, rect.top, rect.right - 2, rect.bottom - 2);
        mInnerShadowDrawable.getWrapperView().setViewLayoutRect(rect);
        mInnerShadowDrawable.setElementShapeRoundedRect(mCornerRadius - 1.0f);
        // set the offset to 1,1 so the border completely surrounds the inner shadow
        mInnerShadowDrawable.getWrapperView().setViewOffset(1.0f, 1.0f);
        mInnerShadowDrawable.getWrapperView().measure(MeasureSpec.makeMeasureSpec(rect.width(), MeasureSpec.EXACTLY),
                                                      MeasureSpec.makeMeasureSpec(rect.height(), MeasureSpec.EXACTLY));
    }


//----- layout ---------------------------------------------------------------------------------------------------------



    /**
     * @brief Set hints for other layers.
     *
     * We are a 3D-Style background.
     */
    @Override
    public void collectHints(PDEDictionary hints) {
        hints.put(PDEButton.PDEButtonHint3DStyle, true);
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



//----- Properties Setter/Getter -----------------------------------------------------------------------------------


    /**
     * @brief Get Collection Layer
     */
    @SuppressWarnings("unused")
    public PDEAbsoluteLayout getCollectionLayer() {
        return this;
    }


    /**
     * @brief Get view of main background
     */
    public PDEViewWrapper getMainView() {
        return mMainDrawable.getWrapperView();
    }


    /**
     * @brief Get drawable of main background
     */
    public PDEDrawableGradientShape getMainDrawable(){
        return mMainDrawable;
    }


    /**
     * @brief Get drawable of inner shadow
     */
    @SuppressWarnings("unused")
    public PDEDrawableShapedInnerShadow getInnerShadowDrawable() {
        return mInnerShadowDrawable;
    }

    /**
     * @brief Get view of inner shadow
     */
    @SuppressWarnings("unused")
    public PDEViewWrapper getInnerShadowView() {
        return mInnerShadowDrawable.getWrapperView();
    }



//----- View Layout -----------------------------------------------------------------------------------


    /**
     * @brief Determine layout size of element.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "onMeasure result: "+getMeasuredWidth()+" x "+getMeasuredHeight());
        }

    }


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
            Log.d(LOG_TAG, "onSizeChanged "+w+", "+h);
        }

        //getMainDrawable().setBoundingRect(new Rect(0, 0, w, h));
        getMainView().setViewLayoutRect(new Rect(0, 0, w, h));
        getMainView().measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));

        mBorderLineDrawable.getWrapperView().setViewLayoutRect(new Rect(0, 0, w, h));
        //mBorderLineView.setElementBoundingRect(new Rect(0, 0, w, h));
        mBorderLineDrawable.setElementShapeRoundedRect(mCornerRadius);
        mBorderLineDrawable.getWrapperView().measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                                                     MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));

        // update the shadow path and inner shadow path
        updatePaths(w, h);
    }
}



