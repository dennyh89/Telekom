/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.sliders;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.PDEConstants.PDEContentStyle;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableRoundedGradientBox;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableMultilayer;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableMultilayer.OnPDEBoundsChangeListener;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedOuterShadow;
import de.telekom.pde.codelibrary.ui.elements.complex.PDEDrawableProgressBar;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.PDEDictionary;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;


//----------------------------------------------------------------------------------------------------------------------
//  PDESliderContentSliderBar
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Slider bar Content for a Slider
 *
 * A simple sliderBar with medium sized slider handle. It is controlled by Slider Events.
 * Use sliderControllerId 0 to change the Position Value.
 *
 */
public class PDESliderContentSliderBar extends View implements PDESliderContentInterface, OnPDEBoundsChangeListener {


    /**
     * @brief Global tag for log outputs.
     */
    @SuppressWarnings("unused")
    private final static String LOG_TAG = PDESliderContentSliderBar.class.getName();

    private PDEDrawableMultilayer mBackgroundMultiLayer;
    private PDEDrawableProgressBar mSliderBar;
    private PDEDrawableShapedOuterShadow mSliderBarShadow;
    private PDEDrawableRoundedGradientBox mSliderBarHandle;

    // layout constants
    private final int mSliderBarHeight = PDEBuildingUnits.pixelFromBU(1.0f);
    private Point mSliderBarHandleSize;
    private static int mSliderBarShadowOffset;

    // color animation
    private PDEAgentHelper mAgentHelper;
    private static PDEParameter PDESliderBarHandleGlobalParamColor;
    private static PDEParameter PDESliderBarHandleGlobalParamBorderColor;



    // style
    private PDEContentStyle mStyle = PDEContentStyle.PDEContentStyleFlat;

    // padding and wanted height information
    private Rect mContentPadding;
    private int mWantedHeight;


    /**
     * @brief Constructor for PDESliderContentSliderBar.
     *
     * @param context Used context.
     * @param attributeSet Used attribute text.
     */
    @SuppressWarnings("unused")
    public PDESliderContentSliderBar(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        init(PDEContentStyle.PDEContentStyleFlat);
    }


    /**
     * @brief Constructor for PDESliderContentSliderBar.
     *
     * @param context  Used context.
     * @param attrs Used attribute set.
     * @param defStyle defStye.
     */
    @SuppressWarnings("unused")
    public PDESliderContentSliderBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(PDEContentStyle.PDEContentStyleFlat);
    }


    /**
     * @brief Constructor for PDESliderContentSliderBar.
     *
     * @param context Used context.
     */
    @SuppressWarnings("unused")
    public PDESliderContentSliderBar(Context context) {
        super(context);
        init(PDEContentStyle.PDEContentStyleFlat);
    }


    /**
     * @brief Constructor for PDESliderContentSliderBar.
     *
     * @param context Used context.
     */
    public PDESliderContentSliderBar(Context context, PDEContentStyle style) {
        super(context);
        init(style);
    }


    /**
     * @brief Initialisation of the SliderBar Content.
     */
    private void init(PDEContentStyle style) {
        PDEDictionary PDESliderBarHandleGlobalBorderColorDefault;
        PDEDictionary PDESliderBarHandleGlobalColorDefault;
        // initialize
        mBackgroundMultiLayer = null;
        mSliderBar = null;
        mSliderBarShadow = null;
        mSliderBarHandle = null;
        mContentPadding = new Rect();
        mSliderBarHandleSize = new Point(PDEBuildingUnits.pixelFromBU(1.5f),PDEBuildingUnits.pixelFromBU(1.5f));
        mSliderBarShadowOffset = 0;

        // remember style
        mStyle = style;

        // create container drawables
        mBackgroundMultiLayer = new PDEDrawableMultilayer();
        mBackgroundMultiLayer.setOnBoundsChangeListener(this);

        if (style == PDEContentStyle.PDEContentStyleFlat) {
            // create the  slider progressBar  layer
            mSliderBar = new PDEDrawableProgressBar();

            if (!PDECodeLibrary.getInstance().isDarkStyle()) {
                // light style
                mSliderBar.setElementBackgroundColor(PDEColor.valueOf("Black7Alpha")); // 7% black
                mSliderBar.setElementOuterShadowColor(PDEColor.valueOf(0x00000000));
                mSliderBar.setElementInnerShadowColor(PDEColor.valueOf(0x00000000));
                mSliderBar.setElementBorderColor(PDEColor.valueOf("DTGrey237_Idle_Border"));
            } else {
                // dark style
                mSliderBar.setElementBackgroundColor(PDEColor.valueOf("Black10Alpha")); // 7% black
                mSliderBar.setElementOuterShadowColor(PDEColor.valueOf(0x00000000));
                mSliderBar.setElementInnerShadowColor(PDEColor.valueOf(0x00000000));
                mSliderBar.setElementBorderColor(PDEColor.valueOf("Black70Alpha")); // 24% black
            }
            mSliderBarShadowOffset = 0;

            // add slider to container
            mBackgroundMultiLayer.addLayer(mSliderBar);

            // setup slider bar handle and add it to container
            mSliderBarHandle = new PDEDrawableRoundedGradientBox();
            mSliderBarHandle.setElementBorderColor(PDEColor.valueOf("DTGrey237_Idle_Border"));
            mSliderBarHandle.setElementCornerRadius(PDEBuildingUnits.oneThirdBU());
            mBackgroundMultiLayer.addLayer(mSliderBarHandle);
        } else if (style == PDEContentStyle.PDEContentStyleHaptic) {
            // create the  slider progressBar  layer
            mSliderBar = new PDEDrawableProgressBar();

            if (!PDECodeLibrary.getInstance().isDarkStyle()) {
                // light style
                mSliderBar.setElementBackgroundColor(PDEColor.valueOf("Black7Alpha")); // 7% black
                mSliderBar.setElementOuterShadowColor(PDEColor.valueOf(0x00000000));
                mSliderBarShadowOffset = 0;
                mSliderBar.setElementInnerShadowColor(PDEColor.valueOf("Black40Alpha"));
                mSliderBar.setElementBorderColor(PDEColor.valueOf("DTGrey237_Idle_Border"));
            } else {
                // dark style
                mSliderBar.setElementBackgroundColor(PDEColor.valueOf("Black10Alpha")); // 7% black
                mSliderBar.setElementOuterShadowColor(PDEColor.valueOf("White10Alpha"));
                mSliderBarShadowOffset = 1;
                //create slider bar shadow
                mSliderBarShadow = mSliderBar.createElementShadow();
                mBackgroundMultiLayer.addLayer(mSliderBarShadow);
                mSliderBar.setElementInnerShadowColor(PDEColor.valueOf("Black75Alpha"));
                mSliderBar.setElementBorderColor(PDEColor.valueOf("Black70Alpha")); // 24% black
            }

            // add slider bar drawables to container
            mBackgroundMultiLayer.addLayer(mSliderBar);

            // setup slider bar handle
            mSliderBarHandle = new PDEDrawableRoundedGradientBox();
            mSliderBarHandle.setLayoutSize(mSliderBarHandleSize.x,mSliderBarHandleSize.y);
            mSliderBarHandle.setElementBorderColor(PDEColor.valueOf("DTGrey237_Idle_Border"));

            mSliderBarHandle.setElementCornerRadius(PDEBuildingUnits.oneThirdBU());

            // add handle drawable to container
            mBackgroundMultiLayer.addLayer(mSliderBarHandle);
        } else {
            Log.d(LOG_TAG, "PDESliderContentProgressBar: unknown PDESliderContentStyle");
        }

        // setup agent helper
        mAgentHelper = new PDEAgentHelper();

        // get color table for background
        PDESliderBarHandleGlobalColorDefault =
                PDEComponentHelpers.readDefaultColorDictionary(R.xml.dt_button_gradient_color_defaults);
        PDESliderBarHandleGlobalBorderColorDefault =
                PDEComponentHelpers.readDefaultColorDictionary(R.xml.dt_button_border_color_defaults);

        // initialize global color to empty (default)
        PDESliderBarHandleGlobalParamColor = new PDEParameter();
        PDESliderBarHandleGlobalParamBorderColor = new PDEParameter();

        // calculate color agent states
        PDEComponentHelpers.buildColors(PDESliderBarHandleGlobalParamColor, PDESliderBarHandleGlobalColorDefault,
                PDEColor.valueOf("DTUIInteractive"), PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // calculate missing gradient colors
        PDEComponentHelpers.fillGradientColors(PDESliderBarHandleGlobalParamColor);

        // calculate border agent states
        PDEComponentHelpers.buildColors(PDESliderBarHandleGlobalParamBorderColor,
                PDESliderBarHandleGlobalBorderColorDefault,
                PDESliderBarHandleGlobalParamColor,
                null,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // if border for a state is still not defined, calculate it from main colors
        PDEComponentHelpers.fillBorderColors(PDESliderBarHandleGlobalParamBorderColor,
                PDESliderBarHandleGlobalParamColor);

        // set multilayer as background
        PDEUtils.setViewBackgroundDrawable(this,mBackgroundMultiLayer);

        // initial update of the slider to get the wanted height
        calculateWantedHeight();

        // update colors once
        updateColors();

    }


    /**
     * Bounds changed, so inform sublayers.
     *
     * @param source The drawable which is changed its bounds.
     * @param bounds The new bounds of the source drawable.
     */
    @Override
    public void onPDEBoundsChange(Drawable source, Rect bounds) {
            updateSlider(bounds);
    }


    /**
     * @brief Set the size of the used handle.
     *
     * @param size The new size of the handle.
     */
    public void setHandleSize(Point size) {
        mSliderBarHandleSize = size;
        //force slider update to remember new wanted size before new measuring
        calculateWantedHeight();
        // mark as invalidated
        invalidate();
    }


    /**
     * Calculate the wanted height of the slider
     */
    private void calculateWantedHeight() {
        mWantedHeight = updateSlider(new Rect()).height();
    }


    /**
     * @brief Update our internal layout.
     * Returns the needed rect (wanted rect).
     */
    private Rect updateSlider(Rect bounds) {
        Rect sliderBarRect,sliderBarHandleRect,completeSliderContentRect;
        Point sliderBarShadowOffset;

        completeSliderContentRect = new Rect();
        sliderBarRect = new Rect(0,0,bounds.width(),mSliderBarHeight);
        sliderBarHandleRect = new Rect(0,0,mSliderBarHandleSize.x,mSliderBarHandleSize.y);

        sliderBarRect.offsetTo(bounds.left,bounds.top+(bounds.height()/2-sliderBarRect.height()/2));
        sliderBarHandleRect.offsetTo(
                sliderBarRect.left + (int)((sliderBarRect.width() - sliderBarHandleRect.width()) * mSliderBar.getElementProgressValue()),
                sliderBarRect.top + (sliderBarRect.height()/2) - sliderBarHandleRect.height()/2);
        sliderBarShadowOffset = new Point(sliderBarRect.left,sliderBarRect.top+mSliderBarShadowOffset);

        // set new layout to create new shadow information
        mSliderBar.setLayoutRect(sliderBarRect);
        mSliderBarHandle.setLayoutRect(sliderBarHandleRect);
        if(mSliderBarShadow != null) {
            mSliderBarShadow.setLayoutOffset(sliderBarShadowOffset);
        }

        // lets calculate the complete slider content rect
        completeSliderContentRect.union(mSliderBar.getLayoutRect());
        completeSliderContentRect.union(mSliderBarHandle.getLayoutRect());
        if(mSliderBarShadow!=null){
            completeSliderContentRect.union(mSliderBarShadow.getLayoutRect());
        }

        // also check handle visibility
        if(bounds.height() < mSliderBarHandle.getLayoutHeight() || bounds.width() < mSliderBarHandle.getLayoutWidth() ) {
            mSliderBarHandle.setAlpha(0);
        } else {
            mSliderBarHandle.setAlpha(255);
        }

        // return needed content rect
        return completeSliderContentRect;
    }


//----- PDESliderContentInterface implementation -----------------------------------------------------------------------


    /**
     * @brief Layer access.
     */
    @Override
    public View getLayer() {
        return this;
    }


    /**
     * @brief Process sliding events.
     *
     * @param event The slider event.
     */
    @Override
    public void sliderEvent(PDEEvent event) {

        // handle slider events
        if (event.isType(PDESliderController.PDE_SLIDER_CONTROLLER_EVENT_MASK_ACTION)) {
            processSliderEventAction(event);
        }

        // handle agent events
        if (event.isType(PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK_ANIMATION)) {
            processAgentEventAnimation(event);
        }
    }


    /**
     * @brief Get needed Padding of this content.
     */
    @Override
    public Rect getSliderContentPadding() {
        return mContentPadding;
    }


    /**
     * @brief Get information about the handle frame relative to the slider view.
     */
    public Rect getHandleFrame() {
        Rect handleRect =  mSliderBarHandle.getLayoutRect();
        handleRect.offset(mBackgroundMultiLayer.getLayoutOffset().x, mBackgroundMultiLayer.getLayoutOffset().y);
        handleRect.offset(getLeft(),getTop());
        return handleRect;
    }


    /**
     * @brief Get information about the handle frame to the slider view.
     */
    public Rect getContentFrame() {
        Rect contentRect =  mSliderBar.getLayoutRect();
        contentRect.offset(mBackgroundMultiLayer.getLayoutOffset().x, mBackgroundMultiLayer.getLayoutOffset().y);
        contentRect.offset(getLeft(),getTop());
        return contentRect;
    }


//----- Handle Slider Events -------------------------------------------------------------------------------------------


    /**
     * @brief Handle Slider Action Events.
     *
     * @param event The slider controller state event.
     */
    private void processSliderEventAction(PDEEvent event) {
        PDEEventSliderControllerState slideEvent;
        float sliderPosition, preloadValue;
        int sliderChangeID;
        Point handlePosition;

        // process the event
        slideEvent = (PDEEventSliderControllerState) event;
        sliderChangeID = slideEvent.getSliderControllerId();

        // only on id 0 change the position
        if (sliderChangeID == 0) {
            // get Information
            sliderPosition = slideEvent.getSliderPosition();

            // security check
            if (sliderPosition < 0) sliderPosition = 0;
            if (sliderPosition > 1) sliderPosition = 1;

            handlePosition = mSliderBarHandle.getLayoutOffset();

            // calculate new Position for the Handle
            handlePosition.x = (int) ((mSliderBar.getLayoutWidth() - mSliderBarHandle.getLayoutWidth()) * sliderPosition);
            // add slider bar offset
            handlePosition.x += mSliderBar.getLayoutOffset().x;

            // set position of handle and handle shaddow if there is one
            mSliderBarHandle.setLayoutOffset(handlePosition);

            // set progress value
            mSliderBar.setElementProgressValue(sliderPosition);
        } else if (sliderChangeID == 1) {
            // get Information
            preloadValue = slideEvent.getSliderPosition();

            // security check
            if (preloadValue < 0) preloadValue = 0;
            if (preloadValue > 1) preloadValue = 1;

            mSliderBar.setElementPreloadValue(preloadValue);


        } else {
            Log.e(LOG_TAG, "Unhandled case in PDESliderContentProgressbar::sliderEvent sliderChangeID: " + sliderChangeID);
        }
    }


// ----- Handle Agent Events -------------------------------------------------------------------------------------------


    /**
     * @brief Handles agent state animation events.
     */
    private void processAgentEventAnimation(PDEEvent event) {

        boolean needsUpdate;

        // check event type
        if (event.isType(PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK_ANIMATION)) {

            // pass on agent events to agent helper (this one checks for relevant changes)
            needsUpdate = mAgentHelper.processAgentEvent(event);

            // update if necessary
            if (needsUpdate) {

                // update animatable parameters on change
                updateColors();
            }
        }
    }


    /**
     * @brief Update colors fully animated
     */
    private void updateColors() {

        PDEColor topColor, mainColor, bottomColor, borderColor;

        if (mStyle == PDEContentStyle.PDEContentStyleHaptic) {
            // haptic
            // interpolate colors by calling complex logic color interpolation helper
            topColor = PDEComponentHelpers.interpolateColor(PDESliderBarHandleGlobalParamColor, mAgentHelper,
                    PDEAgentHelper.PDEAgentHelperAnimationInteractive, PDEButton.PDEButtonColorSuffixLighter);
            mainColor = PDEComponentHelpers.interpolateColor(PDESliderBarHandleGlobalParamColor, mAgentHelper,
                    PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);
            bottomColor = PDEComponentHelpers.interpolateColor(PDESliderBarHandleGlobalParamColor, mAgentHelper,
                    PDEAgentHelper.PDEAgentHelperAnimationInteractive, PDEButton.PDEButtonColorSuffixDarker);

        } else {
            // flat
            // interpolate colors by calling complex logic color interpolation helper
            bottomColor = topColor = mainColor = PDEComponentHelpers.interpolateColor(PDESliderBarHandleGlobalParamColor, mAgentHelper,
                    PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);

        }
        borderColor = PDEComponentHelpers.interpolateColor(PDESliderBarHandleGlobalParamBorderColor, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);

        // set the gradient and border colors
        mSliderBarHandle.setElementBackgroundBottomColor(bottomColor);
        mSliderBarHandle.setElementBackgroundMainColor(mainColor);
        mSliderBarHandle.setElementBackgroundTopColor(topColor);
        mSliderBarHandle.setElementBorderColor(borderColor);
    }


//----- view layout ----------------------------------------------------------------------------------------------------


    /**
     * Sets the wanted height and calculate the width and height.
     * @param widthMeasureSpec int
     * @param heightMeasureSpec int
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(resolveSize(getMeasuredWidth(), widthMeasureSpec),
                    resolveSize(Math.max(getMeasuredHeight(), mWantedHeight), heightMeasureSpec));
    }
}
