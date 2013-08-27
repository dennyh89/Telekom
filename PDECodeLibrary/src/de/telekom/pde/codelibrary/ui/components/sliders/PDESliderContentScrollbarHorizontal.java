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
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.complex.PDEDrawableScrollbarIndicative;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;


/// @cond CLASS_UNDER_DEVELOPMENT__NOT_RELEASED

//----------------------------------------------------------------------------------------------------------------------
//  PDESliderContentScrollbarHorizontal
//----------------------------------------------------------------------------------------------------------------------



/**
 * @brief Scrollbar Content for a Slider
 *
 * This is a scrollbar in horizontal direction. It is controled by Slider Events.
 * Use ID 0 to set the position and the page size.
 *
 */
class PDESliderContentScrollbarHorizontal extends PDEAbsoluteLayout implements PDESliderContentInterface {


    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDESliderContentScrollbarHorizontal.class.getName();
    // debug messages switch
    private final static boolean DEBUGPARAMS = false;

    // drawable
    private PDEDrawableScrollbarIndicative mScrollbarLayer;

    // helper variables
    private float mScrollbarViewWidth;
    private float mLastPageSize;

    // layout
    private static int mScrollbarHeight = PDEBuildingUnits.pixelFromBU(0.5f);


    /**
     * @brief Constructor for PDESliderContentScrollbarHorizontal
     *
     * @param context
     * @param attrs
     */
    public PDESliderContentScrollbarHorizontal (Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context);
    }


    /**
     * @brief Constructor for PDESliderContentScrollbarHorizontal
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public PDESliderContentScrollbarHorizontal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    /**
     * @brief Constructor for PDESliderContentScrollbarHorizontal
     *
     * @param context
     */
    public PDESliderContentScrollbarHorizontal(Context context) {
        super(context);
        init(context);
    }


    /**
     * @brief Initialisation of the Scrollbar Content
     *
     * @param context
     */
    private void init(Context context) {

        // create the Scrollbar layer in horizontal direction
        mScrollbarLayer = new PDEDrawableScrollbarIndicative();
        mScrollbarLayer.setElementScrollbarType(PDEDrawableScrollbarIndicative.PDEDrawableScrollbarIndicativeType.
                                                PDEDrawableScrollbarIndicativeTypeHorizontal);
        addView(mScrollbarLayer.getWrapperView());

        // set background color transparent
        mScrollbarLayer.setElementBackgroundColor(PDEColor.valueOf("DTTransparentBlack"));
    }


//----- PDESliderContentInterface implementation -----------------------------------------------------------------------


    /**
     * @brief Layer access.
     */
    @Override
    public PDEAbsoluteLayout getLayer() {
        return this;
    }


    /**
     * @brief Process sliding events
     *
     * @param event
     */
    @Override
    public void sliderEvent(PDEEvent event) {

        // handle slider events
        if (event.isType(PDESliderController.PDE_SLIDER_CONTROLLER_EVENT_MASK_ACTION)) {
            processSliderEventAction(event);
        }
    }


//----- Handle Slider Events -------------------------------------------------------------------------------------------


    /**
     * @brief Handle Slider Action Events.
     *
     * @param event
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
     * @param pageSize is out of range 0...1. This size must be the result of pagesize/contentsize.
     */
    private void updatePageSize(float pageSize) {

        float minSize;

        // initialize the min size
        if (mScrollbarViewWidth <= PDEBuildingUnits.BU()) minSize = 0;
        else minSize = PDEBuildingUnits.BU()/ mScrollbarViewWidth;

        // limitate the size
        if (pageSize > 1) pageSize = 1;
        if (pageSize < minSize) pageSize = minSize;
        if (minSize == 0) pageSize = minSize;

        // get absolut pixel values
        pageSize *= mScrollbarViewWidth;

        // remember
        mLastPageSize = pageSize;

        // set the Values of the Scrollbar
        mScrollbarLayer.setElementScrollPageSize(pageSize);
    }


    /**
     * @brief Update the position of the slider handle
     *
     * @param position value is out of range 0...1
     */
    private void updateSliderHandlePosition(float position) {

        float scrollerSizeFactor;

        // limitate
        if (position > 1) position = 1;
        if (position < 0) position = 0;

        // setup a scroll size factor to turn our 0..1 range back into pixel values
        scrollerSizeFactor = mScrollbarViewWidth - mLastPageSize;

        // get absolute Position values
        position *= scrollerSizeFactor;

        // set new position
        mScrollbarLayer.setElementScrollPos(position);
    }


//----- view layout ----------------------------------------------------------------------------------------------------


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        int width;

        if (DEBUGPARAMS) {
            Log.d(LOG_TAG, "onMeasure "+MeasureSpec.toString(widthMeasureSpec)+" x "+MeasureSpec.toString(heightMeasureSpec));
        }

        // measure the children (otherwise e.g. the sunkenlayer will not be visible!)
        measureChildren(widthMeasureSpec,heightMeasureSpec);

        // use stored height
        height = mScrollbarHeight;

        // use parameter to set width
        width = MeasureSpec.getSize(widthMeasureSpec);

        // return the values
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));

        if (DEBUGPARAMS) {
            Log.d(LOG_TAG, "onMeasure result: "+getMeasuredWidth()+" x "+getMeasuredHeight());
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        int centerVertical;

        super.onSizeChanged(w, h, oldw, oldh);

        // any change
        if (oldw == w && oldh == h) return;

        // message
        if (DEBUGPARAMS) {
            Log.d(LOG_TAG, "onSizeChanged " + w + ", " + h);
        }

        // calculate vertical position
        centerVertical = (h-mScrollbarHeight)/2;

        // store width
        mScrollbarViewWidth = w;

        // work with frame width relative values
        mScrollbarLayer.setElementScrollContentSize(mScrollbarViewWidth);

        // is height big enough? -> center vertical
        if (centerVertical < 0) {
            mScrollbarLayer.getWrapperView().setViewLayoutRect(new Rect(0, 0, w, h));
            mScrollbarLayer.getWrapperView().measure( MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
        }

        else {
            mScrollbarLayer.getWrapperView().setViewLayoutRect(new Rect(0, centerVertical, w,centerVertical+ mScrollbarHeight));
            mScrollbarLayer.getWrapperView().measure( MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mScrollbarHeight, MeasureSpec.EXACTLY));
        }
    }
}

/// @endcond CLASS_UNDER_DEVELOPMENT__NOT_RELEASED
