/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.sliders;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.elements.complex.PDEDrawableScrollBarIndicative;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.PDEDictionary;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;


//----------------------------------------------------------------------------------------------------------------------
//  PDESliderContentScrollBar
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Scrollbar Content for a Slider
 *
 * This is a scrollbar in horizontal direction. It is controlled by Slider Events.
 * Use ID 0 to set the position and the page size.
 *
 */
public class PDESliderContentScrollBar extends View implements PDESliderContentInterface {


    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDESliderContentScrollBar.class.getName();
    // debug messages switch
    private final static boolean DEBUGPARAMS = false;


    // scrollbar type
    private PDESliderContentOrientation mContentOrientation;

    // handle stuff
    private boolean mHandleOnly;
    private PDEColor mDefaultScrollBarBorderColor;

    // drawable
    private PDEDrawableScrollBarIndicative mScrollBarLayer;

    // helper variables
    private float mLastPageSize;

    // layout
    private static final int mScrollBarThickness = PDEBuildingUnits.pixelFromBU(0.5f);

    // color animation
    private PDEAgentHelper mAgentHelper;
    private static PDEParameter PDEScrollBarHandleGlobalParamColor;


    /**
     * @brief Constructor for PDESliderContentScrollBar.
     *
     * @param context Used context.
     * @param attributeSet Used attribute set.
     */
    @SuppressWarnings("unused")
    public PDESliderContentScrollBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(PDESliderContentOrientation.PDESliderContentOrientationVertical);
    }


    /**
     * @brief Constructor for PDESliderContentScrollBar.
     *
     * @param context Used context.
     * @param attributeSet Used attribute set.
     * @param defStyle defStyle.
     */
    @SuppressWarnings("unused")
    public PDESliderContentScrollBar(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init(PDESliderContentOrientation.PDESliderContentOrientationVertical);
    }


    /**
     * @brief Constructor for PDESliderContentScrollBar.
     *
     * @param context Used context.
     */
    public PDESliderContentScrollBar(Context context) {
        super(context);
        init(PDESliderContentOrientation.PDESliderContentOrientationVertical);
    }


    /**
     * @brief Constructor for PDESliderContentScrollBar.
     *
     * @param context Used context.
     */
    public PDESliderContentScrollBar(Context context, PDESliderContentOrientation contentScrollBarTyp) {
        super(context);
        init(contentScrollBarTyp);
    }


    /**
     * @brief Initialisation of the ScrollBar Content.
     */
    private void init(PDESliderContentOrientation contentScrollBarType) {
        PDEDictionary PDEScrollBarHandleGlobalColorDefault;

        // create the ScrollBar layer in horizontal direction
        mScrollBarLayer = new PDEDrawableScrollBarIndicative();

        //set current scrollBar type and handle visibility, remember border color
        mContentOrientation = contentScrollBarType;
        mDefaultScrollBarBorderColor = mScrollBarLayer.getElementBorderColor();
        setHandleOnly(false);

        // check scrollBar type and set layout params
        if (mContentOrientation == PDESliderContentOrientation.PDESliderContentOrientationVertical) {
            // set Layout Params
            setLayoutParams(new LinearLayout.LayoutParams(mScrollBarThickness, LayoutParams.MATCH_PARENT));
        } else if (mContentOrientation == PDESliderContentOrientation.PDESliderContentOrientationHorizontal) {
            // set Layout Params
            setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mScrollBarThickness));
        }

        // check scrollbar type and set element type
        if (mContentOrientation == PDESliderContentOrientation.PDESliderContentOrientationVertical) {
            mScrollBarLayer.setElementScrollbarType(PDEDrawableScrollBarIndicative.PDEDrawableScrollbarIndicativeType.
                                                            PDEDrawableScrollbarIndicativeTypeVertical);
        } else if (mContentOrientation == PDESliderContentOrientation.PDESliderContentOrientationHorizontal) {
            mScrollBarLayer.setElementScrollbarType(PDEDrawableScrollBarIndicative.PDEDrawableScrollbarIndicativeType.
                                                            PDEDrawableScrollbarIndicativeTypeHorizontal);
        }

        PDEUtils.setViewBackgroundDrawable(this, mScrollBarLayer);

        // set background color transparent
        mScrollBarLayer.setElementBackgroundColor(PDEColor.valueOf("DTTransparentBlack"));

        // setup agent helper
        mAgentHelper = new PDEAgentHelper();

        // get color table for background
        PDEScrollBarHandleGlobalColorDefault = PDEComponentHelpers.readDefaultColorDictionary(
                R.xml.dt_button_flat_color_defaults);

        // initialize global color to empty (default)
        PDEScrollBarHandleGlobalParamColor = new PDEParameter();

        // calculate color agent states
        PDEComponentHelpers.buildColors(PDEScrollBarHandleGlobalParamColor, PDEScrollBarHandleGlobalColorDefault,
                                        "DTGrey1", PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // update colors once
        updateColors();
    }


//----- PDESliderContentInterface implementation -----------------------------------------------------------------------


    /**
     * @brief Set only handle is visible or not.
     */
    public void setHandleOnly(boolean handleOnly) {
        // change?
        if (mHandleOnly == handleOnly) return;

        // remember
        mHandleOnly = handleOnly;

        // set border color
        if (mHandleOnly) {
            mScrollBarLayer.setElementBorderColor(PDEColor.valueOf("DTTransparentBlack"));
        } else {
            mScrollBarLayer.setElementBorderColor(mDefaultScrollBarBorderColor);
        }

    }


    /**
     * @brief Is only the handle visible?.
     */
    @SuppressWarnings("unused")
    public boolean isHandleOnly() {
        return mHandleOnly;
    }


    /**
     * @brief Get information about the handle frame relative to the slider view.
     */
    public Rect getHandleFrame() {
        Rect handleRect = new Rect(mScrollBarLayer.getIndicatorFrame());
        handleRect.offset(getLeft(), getTop());
        return handleRect;
    }


    /**
     * @brief Get information about the handle frame to the slider view.
     */
    public Rect getContentFrame() {
        Rect contentRect = new Rect(mScrollBarLayer.getLayoutRect());
        contentRect.offset(getLeft(), getTop());
        return contentRect;
    }


    /**
     * @brief Get information about the orientation of the content.
     */
    public PDESliderContentOrientation getContentOrientation() {
        return mContentOrientation;
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
     * @param event Slider event.
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
        // return zero rect
        return new Rect(0, 0, 0, 0);
    }


//----- Handle Slider Events -------------------------------------------------------------------------------------------


    /**
     * @brief Handle Slider Action Events.
     *
     * @param event Slider controller state event.
     */
    private void processSliderEventAction(PDEEvent event) {

        PDEEventSliderControllerState slideEvent;
        float sliderPosition, pageSize;
        int sliderChangeID;

        // process the event
        slideEvent = (PDEEventSliderControllerState) event;
        sliderChangeID = slideEvent.getSliderControllerId();

        // only on id 0 change position and page size
        if (sliderChangeID == 0) {

            // get values
            sliderPosition = slideEvent.getSliderPosition();
            pageSize = slideEvent.getSliderPageSize();

            // update page size
            updatePageSize(pageSize);

            // update slider position
            updateSliderHandlePosition(sliderPosition);
        }
    }


    /**
     * @brief Updates the scrollbars page size
     *
     * @param pageSize is out of range 0...1.
     */
    private void updatePageSize(float pageSize) {
        float minSize, tmpWidthOrHeight;

        // check scrollbar type and initialize the min size
        if (mContentOrientation == PDESliderContentOrientation.PDESliderContentOrientationVertical) {
            // get height
            tmpWidthOrHeight = this.getHeight();
            // initialize the min size
            if (tmpWidthOrHeight <= PDEBuildingUnits.BU()) {
                minSize = 0.0f;
            } else {
                minSize = PDEBuildingUnits.BU() / tmpWidthOrHeight;
            }
        } else if (mContentOrientation == PDESliderContentOrientation.PDESliderContentOrientationHorizontal) {
            // get Width
            tmpWidthOrHeight = this.getWidth();
            // initialize the min size
            if (tmpWidthOrHeight <= PDEBuildingUnits.BU()) {
                minSize = 0.0f;
            } else {
                minSize = PDEBuildingUnits.BU() / tmpWidthOrHeight;
            }
        } else {
            minSize = 0.0f;
            tmpWidthOrHeight = 0.0f;
        }

        // limitate the size
        if (pageSize > 1) pageSize = 1;
        if (pageSize < minSize) pageSize = minSize;
        if (minSize == 0) pageSize = minSize;

        // get absolut pixel values
        pageSize *= tmpWidthOrHeight;

        // remember
        mLastPageSize = pageSize;

        // set the Values of the ScrollBar
        mScrollBarLayer.setElementScrollPageSize(pageSize);
    }


    /**
     * @brief Updates the position of the slider handle.
     *
     * @param position value is out of range 0...1
     */
    private void updateSliderHandlePosition(float position) {

        float scrollerSizeFactor;

        // limitate
        if (position > 1) position = 1;
        if (position < 0) position = 0;

        // check scrollbar type and setup a scroll size factor to turn our 0..1 range back into pixel values
        if (mContentOrientation == PDESliderContentOrientation.PDESliderContentOrientationVertical) {
            scrollerSizeFactor = this.getHeight() - mLastPageSize;
        } else if (mContentOrientation == PDESliderContentOrientation.PDESliderContentOrientationHorizontal) {
            scrollerSizeFactor = this.getWidth() - mLastPageSize;
        } else {
            // should not happen
            scrollerSizeFactor = 0.0f;
        }

        // get absolute Position values
        position *= scrollerSizeFactor;

        // set new position
        mScrollBarLayer.setElementScrollPos(position);
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

        PDEColor mainColor;

        // interpolate colors by calling complex logic color interpolation helper
        mainColor = PDEComponentHelpers.interpolateColor(PDEScrollBarHandleGlobalParamColor, mAgentHelper,
                                                         PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);

        // set handle color
        mScrollBarLayer.setElementScrollValueIndicatorColor(mainColor);
    }


//----- view layout ----------------------------------------------------------------------------------------------------


    /**
     * @brief Set Scroll Content Size to view width.
     *
     * @param width New width.
     * @param height New height.
     * @param oldWidth Old width.
     * @param oldHeight Old height.
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {

        super.onSizeChanged(width, height, oldWidth, oldHeight);

        // any change
        if (oldWidth == width && oldHeight == height) return;

        // message
        if (DEBUGPARAMS) {
            Log.d(LOG_TAG, "onSizeChanged " + width + ", " + height);
        }

        // check scrollBar type and work with frame width relative values
        if (mContentOrientation == PDESliderContentOrientation.PDESliderContentOrientationVertical) {
            mScrollBarLayer.setElementScrollContentSize(height);
        } else if (mContentOrientation == PDESliderContentOrientation.PDESliderContentOrientationHorizontal) {
            mScrollBarLayer.setElementScrollContentSize(width);
        }
    }
}