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
import de.telekom.pde.codelibrary.ui.elements.complex.PDEDrawableProgressBar;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

/// @cond CLASS_UNDER_DEVELOPMENT__NOT_RELEASED

//----------------------------------------------------------------------------------------------------------------------
//  PDESliderContentProgressBar
//----------------------------------------------------------------------------------------------------------------------



/**
 * @brief Progressbar Content for a Slider
 *
 * A simple progress bar with optional Preloader. It is controled by Slider Events.
 * Use sliderControllerId 0 to change the Progressbar Value, use sliderControllerId 1 to change the Preloader Value.
 *
 */

class PDESliderContentProgressBar extends PDEAbsoluteLayout implements PDESliderContentInterface {


    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDESliderContentProgressBar.class.getName();
    // debug messages switch
    private final static boolean DEBUGPARAMS = false;

    // drawable
    private PDEDrawableProgressBar mProgressBarLayer;

    // layout
    private static int mProgressBarHeight = PDEBuildingUnits.pixelFromBU(1.0f);


    /**
     * @brief Constructor for PDESliderContentProgressbar
     *
     * @param context
     * @param attrs
     */
    public PDESliderContentProgressBar (Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context);
    }


    /**
     * @brief Constructor for PDESliderContentProgressbar
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public PDESliderContentProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    /**
     * @brief Constructor for PDESliderContentProgressbar
     *
     * @param context
     */
    public PDESliderContentProgressBar(Context context) {
        super(context);
        init(context);
    }


    /**
     * @brief Initialisation of the Progressbar Content
     *
     * @param context
     */
    private void init(Context context) {

        // create the layer structure
        //setClipChildren(false);

        // create the  progress Bar layer
        mProgressBarLayer = new PDEDrawableProgressBar();
        addView(mProgressBarLayer.getWrapperView());
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

        PDEEventSliderControllerState slideEvent;
        float progressValue, preloadValue;
        int sliderChangeID;

        // check if event is sliding action event
        if (!event.isType(PDESliderController.PDE_SLIDER_CONTROLLER_EVENT_MASK_ACTION)) return;

        // process the event
        slideEvent = (PDEEventSliderControllerState) event;
        sliderChangeID = slideEvent.getSliderControllerId();

        // handle different change types
        switch (sliderChangeID) {

            // set progressbar position value
            case 0:
                // get Value from Event
                progressValue = slideEvent.getSliderPosition();

                // set value to layer
                mProgressBarLayer.setElementProgressValue(progressValue);
                break;

            // set preloadbar postion value
            case 1:
                // get Value from Event
                preloadValue = slideEvent.getSliderPosition();

                // set value to layer
                mProgressBarLayer.setElementPreloadValue(preloadValue);
                break;
            default:
                Log.d(LOG_TAG,"Unhandled Case in PDESliderControllerProgressbar :: sliderEvent changeID:"+ sliderChangeID);
                break;
        }
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
        height = mProgressBarHeight;

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
        centerVertical = (h-mProgressBarHeight)/2;

        // is height big enough? -> center vertical
        if (centerVertical < 0) {
            mProgressBarLayer.getWrapperView().setViewLayoutRect(new Rect(0, 0, w, h));
            mProgressBarLayer.getWrapperView().measure( MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                                                        MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
        }

        else {
            mProgressBarLayer.getWrapperView().setViewLayoutRect(new Rect(0, centerVertical, w,centerVertical+ mProgressBarHeight));
            mProgressBarLayer.getWrapperView().measure( MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                                                        MeasureSpec.makeMeasureSpec(mProgressBarHeight, MeasureSpec.EXACTLY));
        }
    }
}


/// @endcond CLASS_UNDER_DEVELOPMENT__NOT_RELEASED
