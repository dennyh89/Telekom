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
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.PDEConstants.PDEContentStyle;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.complex.PDEDrawableProgressBar;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;


//----------------------------------------------------------------------------------------------------------------------
//  PDESliderContentProgressBar
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Progressbar Content for a Slider
 *
 * A simple progress bar with optional Preloader. It is controlled by Slider Events.
 * Use sliderControllerId 0 to change the Progressbar Value, use sliderControllerId 1 to change the Preloader Value.
 *
 */
public  class PDESliderContentProgressBar extends View implements PDESliderContentInterface {


    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDESliderContentProgressBar.class.getName();

    // drawable
    private PDEDrawableProgressBar mProgressBarLayer;



    /**
     * @brief Constructor for PDESliderContentProgressbar.
     *
     * @param context Used context.
     * @param attributeSet Used attribute set.
     */
    @SuppressWarnings("unused")
    public PDESliderContentProgressBar (Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(PDEContentStyle.PDEContentStyleFlat);
    }


    /**
     * @brief Constructor for PDESliderContentProgressbar.
     *
     * @param context Used context.
     * @param attributeSet Used attribute set.
     * @param defStyle defStyle.
     */
    @SuppressWarnings("unused")
    public PDESliderContentProgressBar(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init(PDEContentStyle.PDEContentStyleFlat);
    }


    /**
     * @brief Constructor for PDESliderContentProgressbar.
     *
     * @param context Used Context.
     */
    @SuppressWarnings("unused")
    public PDESliderContentProgressBar(Context context) {
        super(context);
        init(PDEContentStyle.PDEContentStyleFlat);
    }


    /**
     * @brief Constructor for PDESliderContentProgressbar.
     *
     * @param context Used Context.
     */
    public PDESliderContentProgressBar(Context context, PDEContentStyle style) {
        super(context);
        init(style);
    }


    /**
     * @brief Initialisation of the Progressbar Content.
     */
    private void init(PDEContentStyle style) {

        // set the default progressBar height
        setProgressBarHeight(PDEBuildingUnits.pixelFromBU(1.0f));

        // create the  progress Bar layer
        mProgressBarLayer = new PDEDrawableProgressBar();


        if (style == PDEContentStyle.PDEContentStyleFlat) {
            if (!PDECodeLibrary.getInstance().isDarkStyle()) {
                // light style
                mProgressBarLayer.setElementBackgroundColor(PDEColor.valueOf("Black7Alpha")); // 7% black
                mProgressBarLayer.setElementOuterShadowColor(PDEColor.valueOf(0x00000000));
                mProgressBarLayer.setElementInnerShadowColor(PDEColor.valueOf(0x00000000));
//                mProgressBarLayer.setElementBorderColor(PDEColor.valueOf("Black24Alpha")); // 24% black
                mProgressBarLayer.setElementBorderColor(PDEColor.valueOf("DTGrey237_Idle_Border"));
            } else {
                // dark style
                mProgressBarLayer.setElementBackgroundColor(PDEColor.valueOf("Black10Alpha")); // 7% black
                mProgressBarLayer.setElementOuterShadowColor(PDEColor.valueOf(0x00000000));
                mProgressBarLayer.setElementInnerShadowColor(PDEColor.valueOf(0x00000000));
                mProgressBarLayer.setElementBorderColor(PDEColor.valueOf("Black70Alpha")); // 24% black
            }
        } else if (style == PDEContentStyle.PDEContentStyleHaptic) {
            if (!PDECodeLibrary.getInstance().isDarkStyle()) {
                // light style
                mProgressBarLayer.setElementBackgroundColor(PDEColor.valueOf("Black7Alpha")); // 7% black
                mProgressBarLayer.setElementOuterShadowColor(PDEColor.valueOf(0x00000000));
                mProgressBarLayer.setElementInnerShadowColor(PDEColor.valueOf("Black40Alpha"));
//                mProgressBarLayer.setElementBorderColor(PDEColor.valueOf("Black24Alpha")); // 24% black
                mProgressBarLayer.setElementBorderColor(PDEColor.valueOf("DTGrey237_Idle_Border"));
            } else {
                // dark style
                mProgressBarLayer.setElementBackgroundColor(PDEColor.valueOf("Black10Alpha")); // 7% black
                mProgressBarLayer.setElementOuterShadowColor(PDEColor.valueOf("White10Alpha"));
                //mProgressBarLayer.setElementOuterShadowColor(PDEColor.valueOf(0xffffffff));
                mProgressBarLayer.setElementInnerShadowColor(PDEColor.valueOf("Black75Alpha"));
                mProgressBarLayer.setElementBorderColor(PDEColor.valueOf("Black70Alpha")); // 24% black
            }
        } else {
            Log.d(LOG_TAG, "PDESliderContentProgressBar: unknown PDESliderContentStyle");
        }

        // set drawable as background
        PDEUtils.setViewBackgroundDrawable(this, mProgressBarLayer);
    }


    /**
     * Set the custom height of the progressBar.
     * @param height The new height of the progressBar
     */
    public void setProgressBarHeight(int height) {

        // update the Layout Params
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));

        // mark as invalidated
        invalidate();
    }


    /**
     * Get the height of the progressBar.
     */
    @SuppressWarnings("unused")
    public int getProgressBarHeight() {
        LayoutParams lp = getLayoutParams();

        if (lp==null) return 0;

        return lp.height;
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
     * @brief Process sliding events
     *
     * @param event Slider controller state event
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

            // set preloadbar position value
            case 1:
                // get Value from Event
                preloadValue = slideEvent.getSliderPosition();

                // set value to layer
                mProgressBarLayer.setElementPreloadValue(preloadValue);
                break;
            default:
                Log.d(LOG_TAG, "Unhandled Case in PDESliderControllerProgressbar :: sliderEvent changeID:"+ sliderChangeID);
                break;
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


    /**
     * @brief Get information about the handle frame.
     */
    public Rect getHandleFrame() {
        return new Rect();
    }


    /**
     * @brief Get information about the handle frame.
     */
    public Rect getContentFrame() {
        return new Rect();
    }
}
