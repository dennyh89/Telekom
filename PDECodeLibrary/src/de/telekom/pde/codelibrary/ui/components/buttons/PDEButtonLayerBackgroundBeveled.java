/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;

//History
// 10.10.2012 - only changed the innershadow parameter things.


import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonLayoutHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEDictionary;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.common.*;
import de.telekom.pde.codelibrary.ui.elements.common.PDEViewShapedInnerShadow;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;


//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerBackgroundBeveled
//----------------------------------------------------------------------------------------------------------------------



/**
 * @brief Background for an embossed button.
 *
 * Color gradient, frame, inner shadow on pressed.
 */
public class PDEButtonLayerBackgroundBeveled extends Object implements PDEButtonLayerInterface {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEButtonLayerBackgroundEmbossed.class.getName();
    // debug messages switch
    private final static boolean DEBUGPARAMS = false;


    // local parameters needed
    PDEParameterDictionary mParameters;
    PDEParameter mParamColor;
    PDEParameter mParamBorderColor;
    PDEParameter mParamInnerShadowStrength;

    // configuration
    PDEColor mDefaultColor;
    float mInnerShadowOpacity;

    // views
    PDEViewShapedInnerShadow mInnerShadowView;
    PDEViewBackground mMainView;
    PDEViewBorderLine mBorderLineView;

    // additional helper variables
    float mCornerRadius;
    float mOutlineWidth;

    // size of the background
    protected PDEButtonLayoutHelper mLayout;

    // agent helpers
    PDEAgentHelper mAgentHelper;

    // layout that holds the outer shadow-, main background- & inner shadow-view in correct z-order
    PDEAbsoluteLayout mCollectionLayer;



    // global variables
    //
    public static PDEDictionary PDEButtonLayerBackgroundBeveledGlobalColorDefault = null;
    public static PDEDictionary PDEButtonLayerBackgroundBeveledGlobalBorderDefault = null;




    /**
     * @brief Class initialization.
     */
    PDEButtonLayerBackgroundBeveled() {
        // read default dictionaries
        PDEButtonLayerBackgroundBeveledGlobalColorDefault = PDEComponentHelpers.readDefaultColorDictionary(
                "dt_button_gradient_color_defaults");
        PDEButtonLayerBackgroundBeveledGlobalBorderDefault = PDEComponentHelpers.readDefaultColorDictionary(
                "dt_button_border_color_defaults");

        // init
        mParameters = null;
        mLayout = new PDEButtonLayoutHelper();
        mDefaultColor = PDEColor.valueOf("DTUIInteractive");

        // constants for derivation in parameter setting
        mInnerShadowOpacity = 0.28f;

        // set empty complex parameters
        mParamColor = new PDEParameter();
        mParamBorderColor = new PDEParameter();
        mParamInnerShadowStrength = new PDEParameter();

        // agent helper
        mAgentHelper = new PDEAgentHelper();

        // create the layer structure
        mCollectionLayer = new PDEAbsoluteLayout(PDECodeLibrary.getInstance().getApplicationContext());
        mCollectionLayer.setClipChildren(false);

        // create & add main background
        mMainView = new PDEViewBackground(PDECodeLibrary.getInstance().getApplicationContext(),new PDEDrawableGradientShape());
        mCollectionLayer.addView(mMainView);
        // create & add border line
        mBorderLineView = new PDEViewBorderLine(PDECodeLibrary.getInstance().getApplicationContext());
        mCollectionLayer.addView(mBorderLineView);
        // create & add inner shadow
        mInnerShadowView = new PDEViewShapedInnerShadow(PDECodeLibrary.getInstance().getApplicationContext());
        mCollectionLayer.addView(mInnerShadowView);


        // take over the default parameters from layer (iOS defaults of CALayer)
        mCornerRadius = 0.0f;
        mOutlineWidth = 0.0f;

        // apply currently nonparametrized default configuration
        // border line
        setOutlineWidth(1.0f);

        // inner shadow
        mInnerShadowView.setShapeColor(PDEColor.valueOf("DTBlack"));
        mInnerShadowView.setBlurRadius(PDEBuildingUnits.exactOneForthBU());
        mInnerShadowView.setShadowOffset(new PointF(0.0f,PDEBuildingUnits.oneTwelfthsBU()));

        // forced set of parameter -> this sets defaults
        setParameters(new PDEParameterDictionary(), true);
    }




    /**
     * @brief Layer access.
     */
    @Override
    public PDEAbsoluteLayout getLayer() {
        return mCollectionLayer;
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


        // debug output
        if (DEBUGPARAMS) {
            mParamColor.debugOut("Color before building");
            mParamBorderColor.debugOut("Border before building");
        }

        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamColor, PDEButtonLayerBackgroundBeveledGlobalColorDefault, mDefaultColor,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // calculate missing gradient colors
        PDEComponentHelpers.fillGradientColors(mParamColor);

        // calculate border agent states
        PDEComponentHelpers.buildColors(mParamBorderColor, PDEButtonLayerBackgroundBeveledGlobalBorderDefault,
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
        getMainDrawable().setCornerRadius(mCornerRadius);

        mBorderLineView.setCornerRadius(mCornerRadius);
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
        mBorderLineView.setBorderWidth(mOutlineWidth);
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
        getMainDrawable().setColors(topColor.getIntegerColor(), mainColor.getIntegerColor(),
                                    bottomColor.getIntegerColor());
        mBorderLineView.setBorderColor(borderColor.getIntegerColor());
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
        getInnerShadowView().setShapeOpacity(innerShadowFactor * mInnerShadowOpacity);
    }


    /**
     * @brief Update paths for shadow layers.
     */
    protected void updatePaths() {
        RectF rect;

        // inner shadow rect is main rect minus the border (bordersize is always 1)
        rect = new RectF(mLayout.mLayoutRect);
        rect.set(rect.left, rect.top, rect.right - 2.0f, rect.bottom - 2.0f);
        getInnerShadowView().setShapeRoundedRect(rect, mCornerRadius-1.0f);
        // set the offset to 1,1 so the border completely surrounds the inner shadow
        getInnerShadowView().setShapeOffset(new PointF(1.0f, 1.0f));
    }


//----- layout ---------------------------------------------------------------------------------------------------------


    /**
     * @brief Apply new layout when set from the outside.
     */
    @Override
    public void setLayout(PDEButtonLayoutHelper layout) {
        // debug
        if(DEBUGPARAMS){
            Log.d(LOG_TAG,
                  "screenRect left " + layout.mButtonRect.left + " top " + layout.mButtonRect.top + " right " + layout.mButtonRect.right +
                  " bottom " + layout.mButtonRect.bottom);
            Log.d(LOG_TAG,
                  "outlineRect left " + layout.mLayoutRect.left + " top " + layout.mLayoutRect.top + " right " + layout.mLayoutRect.right +
                  " bottom " + layout.mLayoutRect.bottom);
        }

        // any change & valid size?
        if ((layout.mButtonRect.left == mLayout.mButtonRect.left) &&
            (layout.mButtonRect.top == mLayout.mButtonRect.top) &&
            (layout.mButtonRect.right == mLayout.mButtonRect.right) &&
            (layout.mButtonRect.bottom == mLayout.mButtonRect.bottom) &&
            (layout.mLayoutRect.left == mLayout.mLayoutRect.left) &&
            (layout.mLayoutRect.top == mLayout.mLayoutRect.top) &&
            (layout.mLayoutRect.right == mLayout.mLayoutRect.right) &&
            (layout.mLayoutRect.bottom == mLayout.mLayoutRect.bottom) |
            layout.mButtonRect.height() <=0 | layout.mButtonRect.width()<=0 |
            layout.mLayoutRect.height()<=0 | layout.mLayoutRect.width()<=0 ) {
            return;
        }

        // remember
        mLayout = new PDEButtonLayoutHelper(layout);

        // update main background
        getMainDrawable().setBoundingRect(mLayout.mLayoutRect);
        mBorderLineView.setBoundingRect(mLayout.mLayoutRect);

        // update the shadow path and inner shadow path
        updatePaths();

        // ToDo: Not sure if this update is really needed, so it's commented out for now until we find a reason to
        // use it again.
//        // just to be sure, that all children get the layout changes
//        mCollectionLayer.updateLayout(true, mOutlineRect.left, mOutlineRect.top, mOutlineRect.right,
//                                      mOutlineRect.bottom);
    }

    /**
     * @brief Set hints for other layers.
     *
     * We are a 3D-Style background.
     */
    @Override
    public void collectHints(PDEDictionary hints) {
        hints.put(PDEButton.PDEButtonHint3DStyle, new Boolean(true));
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
     * @brief Set new Collection Layer
     */
    public void setCollectionLayer(PDEAbsoluteLayout layer) {
        mCollectionLayer = layer;
    }

    /**
     * @brief Get Collection Layer
     */
    public PDEAbsoluteLayout getCollectionLayer() {
        return mCollectionLayer;
    }


    /**
     * @brief Get view of main background
     */
    public PDEViewBackground getMainView() {
        return mMainView;
    }

    /**
     * @brief Set new view of main background
     */
    public void setMainView(PDEViewBackground view) {
        mMainView = view;
    }

    /**
     * @brief Set new drawable of main background
     */
    public void setMainDrawable(PDEDrawableGradientShape drawable) {
        mMainView.setDrawable(drawable);
    }

    /**
     * @brief Get drawable of main background
     */
    public PDEDrawableGradientShape getMainDrawable(){
        if (mMainView.getDrawable() instanceof PDEDrawableGradientShape){
            return (PDEDrawableGradientShape) mMainView.getDrawable();
        } else {
            return null;
        }
    }

    /**
     * @brief Get drawable of main background
     */
    public PDEDrawableBorderLine getBorderLineDrawable(){
        if (mBorderLineView.getDrawable() instanceof PDEDrawableBorderLine){
            return (PDEDrawableBorderLine) mBorderLineView.getDrawable();
        } else {
            return null;
        }
    }


    /**
     * @brief Set new drawable of inner shadow
     */
    public void setInnerShadowDrawable(PDEDrawableShapedInnerShadow drawable) {
        mInnerShadowView.setDrawable(drawable);
    }

    /**
     * @brief Get drawable of inner shadow
     */
    public PDEDrawableShapedInnerShadow getInnerShadowDrawable() {
        return mInnerShadowView.getDrawable();
    }

    /**
     * @brief Get view of inner shadow
     */
    public PDEViewShapedInnerShadow getInnerShadowView() {
        return mInnerShadowView;
    }

    /**
     * @brief Set new view of inner shadow
     */
    public void setInnerShadowView(PDEViewShapedInnerShadow view) {
        mInnerShadowView = view;
    }


    /**
     * @brief Remember display size of background (internal helper).
     *
     * The outer shadow isn't considered in this size.
     */
    protected void setLayoutRect(Rect layoutRect){
        mLayout.mLayoutRect = layoutRect;
    }

    /**
     * @brief Get display size of background.
     *
     * The outer shadow isn't considered in this size.
     */
    public Rect getLayoutRect(){
        return mLayout.mLayoutRect;
    }
}



