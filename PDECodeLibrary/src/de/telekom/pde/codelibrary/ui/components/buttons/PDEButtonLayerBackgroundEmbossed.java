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
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
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
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableBorderLine;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableGradientShape;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedInnerShadow;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;
import de.telekom.pde.codelibrary.ui.elements.wrapper.PDEViewWrapper;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;


//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerBackgroundEmbossed
//----------------------------------------------------------------------------------------------------------------------


/**
* @brief Background for an embossed button.
*
* Color gradient, frame, shadow, inner shadow on pressed.
*/
class PDEButtonLayerBackgroundEmbossed extends PDEAbsoluteLayout implements PDEButtonLayerInterface {

    /**
     * @brief Log tag.
     */
    private final static String LOG_TAG = PDEButtonLayerBackgroundEmbossed.class.getName();
    // debug messages switch
    private final static boolean DEBUGPARAMS = false;
    private final static boolean SHOW_DEBUG_LOGS = false;


    private final static int sLeftPadding =  PDEBuildingUnits.oneHalfBU();
    private final static int sTopPadding =  PDEBuildingUnits.oneHalfBU();
    private final static int sRightPadding =  PDEBuildingUnits.oneHalfBU();
    private final static int sBottomPadding =  PDEBuildingUnits.oneHalfBU();

    // local parameters needed
    PDEParameterDictionary mParameters;
    PDEParameter mParamColor;
    PDEParameter mParamBorderColor;
    PDEParameter mParamShadowStrength;
    PDEParameter mParamInnerShadowStrength;

    // configuration
    PDEColor mDefaultColor;
    Point mShadowOffset;
    float mShadowBlur;
    float mInnerShadowOpacity;

    // drawables
    PDEDrawableGradientShape mMainDrawable;
    PDEDrawableBorderLine mBorderLineDrawable;
    PDEDrawableShapedInnerShadow mInnerShadowDrawable;
    PDEDrawableShapedShadow mShadowDrawable;

    // additional helper variables
    float mCornerRadius;
    float mOutlineWidth;

    // size of the background
    //protected PDEButtonLayoutHelper mLayout;
    RectF mButtonRect;

    // agent helpers
    PDEAgentHelper mAgentHelper;



    // global variables
    //
    public static PDEDictionary PDEButtonLayerBackgroundEmbossedGlobalColorDefault = null;
    public static PDEDictionary PDEButtonLayerBackgroundEmbossedGlobalBorderDefault = null;


    public PDEButtonLayerBackgroundEmbossed(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PDEButtonLayerBackgroundEmbossed(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * @brief Class initialization.
     */
    public PDEButtonLayerBackgroundEmbossed(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        // read default dictionaries
        PDEButtonLayerBackgroundEmbossedGlobalColorDefault = PDEComponentHelpers.readDefaultColorDictionary(
                "dt_button_gradient_color_defaults");
        PDEButtonLayerBackgroundEmbossedGlobalBorderDefault = PDEComponentHelpers.readDefaultColorDictionary(
                "dt_button_border_color_defaults");

        // init
        mParameters = null;
        //mLayout = new PDEButtonLayoutHelper();
        mButtonRect = new RectF();
        mDefaultColor = PDEColor.DTUIInteractiveColor();

        // set empty complex parameters
        mParamColor = new PDEParameter();
        mParamBorderColor = new PDEParameter();
        mParamShadowStrength = new PDEParameter();
        mParamInnerShadowStrength = new PDEParameter();

        // agent helper
        mAgentHelper = new PDEAgentHelper();

        // create the layer structure
        setClipChildren(false);
//        mCollectionLayer.setClipToPadding(false);

        // create & add outer shadow
        mShadowDrawable = new PDEDrawableShapedShadow();
        addView(mShadowDrawable.getWrapperView());
        // create & add main background
        mMainDrawable = new PDEDrawableGradientShape();
        addView(mMainDrawable.getWrapperView());

        mBorderLineDrawable = new PDEDrawableBorderLine();
        addView(mBorderLineDrawable.getWrapperView());

        // create & add inner shadow
        mInnerShadowDrawable = new PDEDrawableShapedInnerShadow();
        addView(mInnerShadowDrawable.getWrapperView());


        // take over the default parameters from layer (iOS defaults of CALayer)
        mCornerRadius = 0.0f;
        mOutlineWidth = 0.0f;

        // constants for derivation in parameter setting
        // outer shadow
        mShadowOffset = new Point(0, PDEBuildingUnits.oneTwelfthsBU());
        mShadowBlur = (float) PDEBuildingUnits.oneSixthBU();

        // inner shadow
        mInnerShadowOpacity = 0.28f;


        // apply currently nonparametrized default configuration which is not set during parameter setting
        // border line
        setOutlineWidth(1.0f);

        // outer shadow
        mShadowDrawable.setElementShapeColor(PDEColor.valueOf("DTBlack"));
        mShadowDrawable.setElementShapeOpacity(0.25f);


        // inner shadow
        mInnerShadowDrawable.setElementShapeColor(PDEColor.valueOf("DTBlack"));
        mInnerShadowDrawable.setElementBlurRadius((float) PDEBuildingUnits.oneFourthBU());
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
            updateShadow();
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

        // check for shadow changes
        if (force) {
            prepareShadowStrength();
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
        PDEComponentHelpers.buildColors(mParamColor, PDEButtonLayerBackgroundEmbossedGlobalColorDefault, mDefaultColor,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // calculate missing gradient colors
        PDEComponentHelpers.fillGradientColors(mParamColor);

        // calculate border agent states
        PDEComponentHelpers.buildColors(mParamBorderColor, PDEButtonLayerBackgroundEmbossedGlobalBorderDefault,
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
     * Shadow strengths animates the shadow as a whole. The range is 0.0..1.0.
     */
    protected void prepareShadowStrength() {
        // set the new values
        mParamShadowStrength.removeAllObjects();


        // create fixed defaults (old way for old compilers, dictionary literal would be better readable)
        mParamShadowStrength.setWithDictionary(new PDEDictionary("default.idle", "1.0",
                                                                "default.down", "0.0"));

        // debug output
        if (DEBUGPARAMS) {
            mParamShadowStrength.debugOut("Shadow before building");
        }

        // calculate agent states
        PDEComponentHelpers.buildValues(mParamShadowStrength, null, null, PDEAgentHelper.PDEAgentHelperAnimationDown);

        // and convert to number
        mParamShadowStrength.convertToNumber();

        // debug output
        if (DEBUGPARAMS) {
            mParamShadowStrength.debugOut("Shadow after building");
        }

        // and apply once
        updateShadow();
    }


    /**
     * @brief Prepare shadow strength parameter set.
     *
     * Shadow strengs animates the shadow as a whole. The range is 0.0..1.0.
     */
    protected void prepareInnerShadowStrength() {
        // set the new values
        mParamInnerShadowStrength.removeAllObjects();

        // (ToDo: skipped "default.focus", "0.0", here)
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

        // corner radius also affects the shadow paths
        updatePaths();
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

        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG,"updateColors");
        }

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
    protected void updateShadow() {
        float shadowFactor;

        // interpolate colors by calling complex logic color interpolation helper
        shadowFactor = PDEComponentHelpers.interpolateFloat(mParamShadowStrength, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationDown, null);

        // set offset and blur
        PointF offset = new PointF(mButtonRect.left + mShadowOffset.x * shadowFactor,
                                 mButtonRect.top + mShadowOffset.y * shadowFactor);
        getShadowView().setViewOffset(offset.x,offset.y);
        mShadowDrawable.setElementBlurRadius(mShadowBlur * shadowFactor);
        getShadowView().measure(MeasureSpec.makeMeasureSpec(getShadowView().getLayoutParams().width,
                                MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(getShadowView().getLayoutParams().height,
                                MeasureSpec.EXACTLY));
        // todo: Discuss with Klaus D if this hack is avoidable by correct meassuring?
        // DIRTY-RECT-HACK
        Log.d(LOG_TAG,"Dirty-Rect-Sizes: w "+this.getWidth()+" h "+this.getHeight());
        invalidate(0,0,getWidth(),getHeight());
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
    protected void updatePaths() {
        Rect rect;

        rect = new Rect((int)mButtonRect.left + mShadowOffset.x,
                        (int)mButtonRect.top + mShadowOffset.y,
                        (int)mButtonRect.right+ mShadowOffset.x,
                        (int)mButtonRect.bottom + mShadowOffset.y);
        getShadowView().setViewLayoutRect(rect);

        // set the shadow's path
        mShadowDrawable.setElementShapeRoundedRect(mCornerRadius);
        getShadowView().measure(MeasureSpec.makeMeasureSpec((int)mButtonRect.width(),MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int)mButtonRect.height(),MeasureSpec.EXACTLY));

        // inner shadow rect is main rect minus the border (bordersize is always 1)
        rect = new Rect((int)mButtonRect.left-1, (int)mButtonRect.top-1, (int)mButtonRect.right-1, (int)mButtonRect.bottom-1);

        mInnerShadowDrawable.getWrapperView().setViewLayoutRect(rect);
        mInnerShadowDrawable.setElementShapeRoundedRect(mCornerRadius - 1.0f);
        // set the offset to 1,1 so the border completely surrounds the inner shadow
        //getInnerShadowView().setShapeOffset(new PointF(rect.left + 1.0f, rect.top + 1.0f));
        mInnerShadowDrawable.getWrapperView().measure(MeasureSpec.makeMeasureSpec(rect.width(),MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(rect.height(),MeasureSpec.EXACTLY));
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
        // ToDo: smarter logic that depends on actual outer shadow size
        padding.putPaddingRequest(sLeftPadding, sTopPadding,
                                  sRightPadding, sBottomPadding);
    }



//----- Properties Setter/Getter -----------------------------------------------------------------------------------


    /**
     * @brief Get Collection Layer
     */
    public PDEAbsoluteLayout getCollectionLayer() {
        return this;
    }


    /**
     * @brief Get view of outer shadow
     */
    public PDEViewWrapper getShadowView() {
        return mShadowDrawable.getWrapperView();
    }


    /**
     * @brief Get drawable of outer shadow
     */
    public PDEDrawableShapedShadow getShadowDrawable() {
        return mShadowDrawable;
    }


    /**
     * @brief Get view of main background
     */
    public PDEViewWrapper getMainView() {
        return mMainDrawable.getWrapperView();
    }


// setting of new drawables makes no sense because we would change the order of layers
// if we want to do this we have to store the Views again seperately.

//    /**
//     * @brief Set new drawable of main background
//     */
//    public void setMainDrawable(PDEDrawableGradientShape drawable) {
//        if(drawable == mMainDrawable || drawable == null) return;
//        // remove old view
//        if (mMainDrawable != null) removeView(mMainDrawable.getWrapperView());
//        // remember
//        mMainDrawable = drawable;
//        // add view
//        addView(mMainDrawable.getWrapperView());
//    }


    /**
     * @brief Get drawable of main background
     */
    public PDEDrawableGradientShape getMainDrawable(){
       return mMainDrawable;
    }


    /**
     * @brief Get drawable of inner shadow
     */
    public PDEDrawableShapedInnerShadow getInnerShadowDrawable() {
        return mInnerShadowDrawable;
    }

    /**
     * @brief Get view of inner shadow
     */
    public PDEViewWrapper getInnerShadowView() {
        return mInnerShadowDrawable.getWrapperView();
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // ensure measure of the children
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "onMeasure result: "+getMeasuredWidth()+" x "+getMeasuredHeight());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "onSizeChanged "+w+", "+h+" was "+oldw+", "+oldh);
        }
        super.onSizeChanged(w, h, oldw, oldh);    //To change body of overridden methods use File | Settings | File Templates.

        //todo AlKi: pls. get this running!
        // to see frames set a background(-color) in pdebutton.xml for element: id/pdebutton_inner_layout. that shows the
        // size the elements should have w and y minus the "embossed" padding.

        if (w != oldw || h != oldh) {
            mButtonRect = new RectF(0+sTopPadding,0+sTopPadding,w-sRightPadding,h-sBottomPadding);

            // update main background
            getMainView().setViewLayoutRect(new Rect((int)mButtonRect.left, (int)mButtonRect.top, (int)mButtonRect.right, (int)mButtonRect.bottom));
            getMainView().measure(MeasureSpec.makeMeasureSpec((int)mButtonRect.width(),MeasureSpec.EXACTLY),
                                  MeasureSpec.makeMeasureSpec((int)mButtonRect.height(),MeasureSpec.EXACTLY));

            // set the size for the border (view)
            mBorderLineDrawable.getWrapperView().setViewLayoutRect(new Rect((int) mButtonRect.left,
                                                                            (int) mButtonRect.top,
                                                                            (int) mButtonRect.right,
                                                                            (int) mButtonRect.bottom));
            // measure view, so that setMeasuredSize is valid
            mBorderLineDrawable.getWrapperView().measure(
                    MeasureSpec.makeMeasureSpec((int) mButtonRect.width(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec((int) mButtonRect.height(), MeasureSpec.EXACTLY));


            // all other views should be handled the same...

            // update the shadow path and inner shadow path
            updatePaths();
        }

    }
}










