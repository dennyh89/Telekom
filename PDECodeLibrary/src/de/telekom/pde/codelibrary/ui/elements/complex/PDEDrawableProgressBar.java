/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.complex;

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableProgressBar
//----------------------------------------------------------------------------------------------------------------------

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableRoundedBox;
import de.telekom.pde.codelibrary.ui.elements.common.*;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDEViewWrapper;


/**
 * @brief Graphics primitive - a progress bar.
 */
public class PDEDrawableProgressBar extends PDEDrawableMultilayer {


//-----  properties ----------------------------------------------------------------------------------------------------

    // colors
    protected PDEColor mElementOuterShadowColor;
    protected PDEColor mElementBorderShadowColor;
    protected PDEColor mElementMarkerColor;
    // properties
    protected float mElementCornerRadius;
    protected float mElementProgressValue;
    protected float mElementPreloadValue;
    protected float mElementProgressStartValue;
    protected float mElementPreloadStartValue;
    protected int mElementNumMarkers;
    // layers
    private PDEDrawableShapedOuterShadow mElementShadowDrawable;
    private PDEDrawableRoundedBox mElementFrameDrawable;
    // todo possibly we have to add mElementFrameDrawableBorderShadow (white line)
    private PDEDrawableMultilayer mElementMarkerMultilayer;
    private PDEDrawableShapedInnerShadow mElementInnerShadowDrawable;
    private PDEDrawableShape mElementProgressDrawable;
    private PDEDrawableShape mElementPreloadDrawable;
    protected PDEViewWrapper mWrapperView;



//----- init -----------------------------------------------------------------------------------------------------------

    // initialization
    public PDEDrawableProgressBar() {
        // init properties defaults
        mElementOuterShadowColor = PDEColor.valueOf("DTBlack");
        mElementBorderShadowColor = PDEColor.valueOf("DTWhite");
        mElementMarkerColor = PDEColor.valueOf("DTDarkMagenta");
        mElementCornerRadius = PDEBuildingUnits.oneThirdBU();
        mElementProgressValue = 0.0f;
        mElementPreloadValue = 0.0f;
        mElementProgressStartValue = 0.0f;
        mElementPreloadStartValue = 0.0f;
        mElementNumMarkers = 0;

        // init sub-layers
        initLayers();

        // forward init values to sub-layers
        setElementBackgroundColor(PDEColor.valueOf("Black7Alpha"));
//        setElementBorderColor(PDEColor.valueOf("DTGrey6"));
        setElementBorderColor(PDEColor.valueOf("DTGrey237_Idle_Border"));
        setElementProgressValueColor(PDEColor.valueOf("DTMagenta"));
        setElementPreloadValueColor(PDEColor.valueOf(0x3f000000));
        setElementInnerShadowColor(PDEColor.valueOf(0xaf000000));
        setElementOuterShadowColor(mElementOuterShadowColor);

        mWrapperView = null;
    }


    /**
     * @brief internal initial helper to init needed layers. shadow is not created here, it must
     *        inited by createShadow separately
     */
    private void initLayers() {
        // frame
        mElementFrameDrawable = new PDEDrawableRoundedBox();
        mElementFrameDrawable.setBounds(0, 0, 0, 0);
        addLayer(mElementFrameDrawable);

        // innershadow
        mElementInnerShadowDrawable = new PDEDrawableShapedInnerShadow();
        mElementInnerShadowDrawable.setBounds(0, 1, 0, 0);
        mElementInnerShadowDrawable.setElementBlurRadius(PDEBuildingUnits.oneSixthBU());
//        mElementInnerShadowDrawable.setElementShapeOpacity(0.28f);
        mElementInnerShadowDrawable.setElementShapeColor(PDEColor.valueOf("Black40Alpha"));
        addLayer(mElementInnerShadowDrawable);

        // todo ElementFrameDrawableBorderShadow

        // preload drawable
        mElementPreloadDrawable = new PDEDrawableShape();
        mElementPreloadDrawable.setBounds(0, 0, 0, 0);
        addLayer(mElementPreloadDrawable);

        // progress drawable
        mElementProgressDrawable = new PDEDrawableShape();
        mElementProgressDrawable.setBounds(0, 0, 0, 0);
        addLayer(mElementProgressDrawable);

        // markers layer
        mElementMarkerMultilayer = new PDEDrawableMultilayer();
        addLayer(mElementMarkerMultilayer);
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- shadow ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief init shadow drawable.
     */
    public PDEDrawableShapedOuterShadow createElementShadow() {
        // already created?
        if (mElementShadowDrawable != null) {
            return mElementShadowDrawable;
        }
        // init shadow
        mElementShadowDrawable = new PDEDrawableShapedOuterShadow();
        updateShadowDrawable(getBounds());
        return mElementShadowDrawable;
    }


    /**
     * @brief shadow getter
     */
    @SuppressWarnings("unused")
    public PDEDrawableShapedOuterShadow getElementShadow() {
        return mElementShadowDrawable;
    }


    /**
     * @brief forget shadow layer.
     */
    @SuppressWarnings("unused")
    public void clearElementShadow() {
        mElementShadowDrawable = null;
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief function to set the outer shadow color
     *
     * @param color new shadow color
     */
    public void setElementOuterShadowColor(PDEColor color) {
        // anything to do?
        if (mElementOuterShadowColor.getIntegerColor() == color.getIntegerColor()) {
            return;
        }

        // remember
        mElementOuterShadowColor = color;

        // set
        if (mElementShadowDrawable != null) {
            mElementShadowDrawable.setElementShapeColor(mElementOuterShadowColor);
            doLayout();
        }
    }


    /**
     * @brief getter for outer shadow color.
     */
    @SuppressWarnings("unused")
    public PDEColor getElementOuterShadowColor() {
        // security
        if (mElementShadowDrawable == null) {
            return null;
        }
        return mElementShadowDrawable.getElementShapeColor();
    }


    /**
     * @brief function to set the inner shadow color
     *
     * @param color new inner shadow color
     */
    public void setElementInnerShadowColor(PDEColor color) {
        // anything to do?
        if (color == mElementInnerShadowDrawable.getElementShapeColor()) {
            return;
        }
        mElementInnerShadowDrawable.setElementShapeColor(color);
        doLayout();
    }


    /**
     * @brief getter for inner shadow color.
     */
    @SuppressWarnings("unused")
    public PDEColor getElementInnerShadowColor() {
        return mElementInnerShadowDrawable.getElementShapeColor();
    }


    /**
     * @brief function to set the color of the progress filling
     *
     * @param color new filling color for progress.
     */
    public void setElementProgressValueColor(PDEColor color) {
        // any change?
        if (mElementProgressDrawable.getElementBackgroundColor() == color.getIntegerColor()) {
            return;
        }
        // remember
        mElementProgressDrawable.setElementBackgroundColor(color.getIntegerColor());
    }


    /**
     * @brief getter for progress filling color.
     */
    @SuppressWarnings("unused")
    public PDEColor getElementProgressValueColor() {
        return PDEColor.valueOf(mElementProgressDrawable.getElementBackgroundColor());
    }


    /**
     * @brief function to set the color of the preload filling
     *
     * @param color new filling color for preload.
     */
    public void setElementPreloadValueColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementPreloadDrawable.getElementBackgroundColor()) {
            return;
        }
        // remember
        mElementPreloadDrawable.setElementBackgroundColor(color.getIntegerColor());
    }


    /**
     * @brief getter for preload filling color.
     */
    @SuppressWarnings("unused")
    public PDEColor getElementPreloadValueColor() {
        return PDEColor.valueOf(mElementPreloadDrawable.getElementBackgroundColor());
    }


    /**
     * @brief function to set the background color.
     *
     * @param color set new color of background.
     */
    public void setElementBackgroundColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementFrameDrawable.getElementBackgroundColor().getIntegerColor()) {
            return;
        }
        // remember
        mElementFrameDrawable.setElementBackgroundColor(color);
    }


    /**
     * @brief getter for background color.
     */
    public PDEColor getElementBackgroundColor() {
        return mElementFrameDrawable.getElementBackgroundColor();
    }


    /**
     * @brief function to set the color of the border.
     *
     * @param color new color of the element border.
     */
    public void setElementBorderColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementFrameDrawable.getElementBorderColor().getIntegerColor()) {
            return;
        }
        // remember
        mElementFrameDrawable.setElementBorderColor(color);
        doLayout();
    }


    /**
     * @brief getter for border color.
     */
    public PDEColor getElementBorderColor() {
        return mElementFrameDrawable.getElementBorderColor();
    }


    /**
     * @brief function to set the color of the markers.
     *
     * @param color new color of the markers.
     */
    @SuppressWarnings("unused")
    public void setElementMarkerColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementMarkerColor.getIntegerColor()) {
            return;
        }
        // remember
        mElementMarkerColor = color;
        doLayout();
    }


    /**
     * @brief getter for color of the markers.
     */
    @SuppressWarnings("unused")
    public PDEColor getElementMarkerColor() {
        return mElementMarkerColor;
    }


    /**
     * @brief function to set the corner radius.
     *
     * @param radius new corner radius.
     */
    public void setElementCornerRadius(float radius) {
        // any change?
        if (radius == mElementCornerRadius) {
            return;
        }
        // remember
        mElementCornerRadius = radius;
        doLayout();
    }


    /**
     * @brief getter for corner radius.
     */
    public float getElementCornerRadius() {
        return mElementCornerRadius;
    }


    /**
     * @brief function to set the current progress value [0..1]
     *
     * @param value the current progress in percent [0..1]
     */
    public void setElementProgressValue(float value) {
        // any change?
        if (value == mElementProgressValue) {
            return;
        }
        // make range 0..1 check
        if (value > 1) {
            value = 1;
        }
        if (value < 0) {
            value = 0;
        }
        // remember
        mElementProgressValue = value;
        doLayout();
    }


    /**
     * @brief getter for current progressvalue [0..1]
     */
    public float getElementProgressValue() {
        return mElementProgressValue;
    }


    /**
     * @brief function to set the current preload value [0..1].
     *
     * @param value the current preload value in percent [0..1].
     */
    public void setElementPreloadValue(float value) {
        // any change?
        if (value == mElementPreloadValue) {
            return;
        }
        // make range 0..1 check
        if (value > 1) {
            value = 1;
        }
        if (value < 0) {
            value = 0;
        }
        // remember
        mElementPreloadValue = value;
        doLayout();
    }


    /**
     * @brief getter for current preload value [0..1].
     */
    @SuppressWarnings("unused")
    public float getElementPreloadValue() {
        return mElementPreloadValue;
    }


    /**
     * @brief function to set the current progress start value [0..1].
     *
     * @param value the new progress start value in percent [0..1].
     */
    @SuppressWarnings("unused")
    public void setElementProgressStartValue(float value) {
        // any change?
        if (value == mElementProgressStartValue) {
            return;
        }
        // make range 0..1 check
        if (value > 1) {
            value = 1;
        }
        if (value < 0) {
            value = 0;
        }
        // remember
        mElementProgressStartValue = value;
        doLayout();
    }


    /**
     * @brief getter for current progress start value [0..1].
     */
    @SuppressWarnings("unused")
    public float getElementProgressStartValue() {
        return mElementProgressStartValue;
    }


    /**
     * @brief function to set the current preload start value [0..1].
     *
     * @param  value the new preload start value in percent [0..1].
     */
    @SuppressWarnings("unused")
    public void setElementPreloadStartValue(float value) {
        // any change?
        if (value == mElementPreloadValue) {
            return;
        }
        // make range 0..1 check
        if (value > 1) {
            value = 1;
        }
        if (value < 0) {
            value = 0;
        }
        // remember
        mElementPreloadStartValue = value;
        doLayout();
    }


    /**
     * @brief getter for current preload start value [0..1].
     */
    @SuppressWarnings("unused")
    public float getElementPreloadStartValue() {
        return mElementPreloadStartValue;
    }


    /**
     * @brief function to set the numbers of markers
     */
    @SuppressWarnings("unused")
    public void setElementNumMarkers(int num) {
        int i;
        PDEDrawableDelimiter marker;

        // any change?
        if (num == mElementNumMarkers) {
            return;
        }
        // delete unnecessary markers
        if (mElementMarkerMultilayer.getNumberOfLayers() > num) {
            // too much marker sublayers, remove unused
            for (i = mElementMarkerMultilayer.getNumberOfLayers() - 1; i >= num; i--) {
                mElementMarkerMultilayer.removeLayerAtIndex(i);
            }
        } else if (mElementMarkerMultilayer.getNumberOfLayers() < num) {
            // create new marker sublayers
            for (i = mElementMarkerMultilayer.getNumberOfLayers(); i < num; i++) {
                // create new marker
                marker = new PDEDrawableDelimiter();
                // config new marker
                marker.setElementBackgroundColor(mElementMarkerColor);
                marker.setElementType(PDEDrawableDelimiter.PDEDrawableDelimiterType.PDEDrawableDelimiterTypeVertical);
                // add new marker
                mElementMarkerMultilayer.addLayer(marker);
            }
        }
        // remember
        mElementNumMarkers = num;
        doLayout();
    }


    /**
     * @brief getter for number of markers.
     */
    @SuppressWarnings("unused")
    public int getElementNumMarkers() {
        return mElementNumMarkers;
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- update / drawing ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief update function. Set sizes and positions of all layers
     */
    protected void doLayout() {
        // get current bounds
        Rect bounds = getBounds();

        // update size and position
        updateFrameDrawable(bounds);

        // update shadow size
        updateShadowDrawable(bounds);

        // update markers
        updateMarkers(bounds);
    }


    /**
     * @brief update function for frame-layer. set size, position and color.
     *
     * @param bounds current element bounding rect.
     */
    private void updateFrameDrawable(Rect bounds) {
        Rect frameRect = new Rect(0, 0, bounds.width(), bounds.height());
        Rect progressRect, preloadRect;
        float width, height, posX, posY;
        int borderWidth;
        Path clipPath = new Path();


        // update Frame
        mElementFrameDrawable.setBounds(frameRect);
        mElementFrameDrawable.setElementCornerRadius(mElementCornerRadius);
        mElementFrameDrawable.setElementBorderWidth(1.0f);
        // remember for later use
        borderWidth = Math.round(mElementFrameDrawable.getElementBorderWidth());

        // calculate clip path
        clipPath.addRoundRect(new RectF(frameRect.left + borderWidth, frameRect.top + borderWidth,
                                        frameRect.right - borderWidth, frameRect.bottom -  borderWidth),
                              mElementCornerRadius - borderWidth, mElementCornerRadius - borderWidth,
                              Path.Direction.CW);
        clipPath.close();

        // set innershadow data
        mElementInnerShadowDrawable.setLayoutRect(new Rect(Math.round(frameRect.left + 1),
                                                           Math.round(frameRect.top + 1),
                                                           Math.round(frameRect.right - 1),
                                                           Math.round(frameRect.bottom - 1)));
        mElementInnerShadowDrawable.setElementShapeRoundedRect(mElementCornerRadius - 1);

        // update progress layer
        // determine offset
        posX = bounds.width() * mElementProgressStartValue;
        posY = 0;
        // determine width & height
        width = bounds.width() * Math.abs(mElementProgressValue - mElementProgressStartValue);
        height = bounds.height();
        // set layout rect
        progressRect = new Rect(Math.round(posX), Math.round(posY), Math.round(posX + width),
                                Math.round(posY + height));
        mElementProgressDrawable.setLayoutRect(progressRect);
        // draw it as a regular filled rect
        mElementProgressDrawable.setElementShapeRect();
        // do a rounded masking of the progress layer at the beginning and at the end of the progress bar frame
        mElementProgressDrawable.setElementClipPath(clipPath);


        // update preload layer
        // determine offset
        posX = bounds.width() * mElementPreloadStartValue;
        posY = 0;
        // determine width & height
        width = bounds.width() * Math.abs(mElementPreloadValue - mElementPreloadStartValue);
        height = bounds.height();
        // set layout rect
        preloadRect = new Rect(Math.round(posX), Math.round(posY), Math.round(posX + width), Math.round(posY + height));
        mElementPreloadDrawable.setLayoutRect(preloadRect);
        // draw it as a regular filled rect
        mElementPreloadDrawable.setElementShapeRect();
        // do a rounded masking of the preload layer at the beginning and at the end of the progress bar frame
        mElementPreloadDrawable.setElementClipPath(clipPath);
    }


    /**
     * @brief update function for shadowlayer
     *
     * @param bounds the current element bounding rect
     */
    private void updateShadowDrawable(Rect bounds) {
        Rect layoutRect;

        // already created?
        if (mElementShadowDrawable == null) {
            return;
        }
        // set color
        mElementShadowDrawable.setElementShapeColor(mElementOuterShadowColor);
        // keep current shadow position, just update the size
        layoutRect = new Rect(mElementShadowDrawable.getBounds().left, mElementShadowDrawable.getBounds().top,
                               mElementShadowDrawable.getBounds().left + bounds.width(),
                               mElementShadowDrawable.getBounds().top + bounds.height());
        mElementShadowDrawable.setLayoutRect(layoutRect);
    }


    /**
     * @brief update function for markers
     *
     * @param bounds current element bounding rect
     */
    private void updateMarkers(Rect bounds) {
        int i;
        PDEDrawableDelimiter marker;
        int pos;
        int step;

        // calc step size
        step = Math.round(mElementFrameDrawable.getBounds().width() / (mElementNumMarkers + 1));
        pos = step;
        mElementMarkerMultilayer.setBounds(bounds);
        for (i = 0; i < mElementMarkerMultilayer.getNumberOfLayers(); i++) {
            // get layer
            marker = (PDEDrawableDelimiter) mElementMarkerMultilayer.getLayerAtIndex(i);
            // set size and position
            marker.setLayoutRect(new Rect(pos,
                    Math.round(mElementFrameDrawable.getElementBorderWidth()),
                    pos + 1,
                    Math.round(mElementFrameDrawable.getBounds().height() - mElementFrameDrawable.getElementBorderWidth()
            )));

            // set color
            marker.setElementBackgroundColor(mElementMarkerColor);

            // next position
            pos += step;
        }
    }

//----------------------------------------------------------------------------------------------------------------------
// ----- Wrapper View  -------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------


    public PDEViewWrapper getWrapperView() {
        if (mWrapperView == null) {
            mWrapperView = new PDEViewWrapper(PDECodeLibrary.getInstance().getApplicationContext(), this);
        }
        return mWrapperView;
    }
}
