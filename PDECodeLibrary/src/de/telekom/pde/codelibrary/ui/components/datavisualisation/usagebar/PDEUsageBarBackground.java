/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.datavisualisation.usagebar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import de.telekom.pde.codelibrary.ui.color.PDEColor;

/**
 * @brief Background View of PDEUsageBar.
 *
 * This is just a simple view that shows two rectangles next to each other, one filled with the primary (fill) color
 * and one with the secondary (empty) color. They share the same space, so when the width of the left rectangle grows,
 * the width of the right rectangle shrinks. This is an easy way to visualize a fill bar.
 */
public class PDEUsageBarBackground extends View {

    // configuration
    protected final static String CONFIGURATION_DEFAULT_FILL_COLOR = "DTDVLightBlue";
    protected final static float CONFIGURATION_RELATION_FILL_TO_EMPTY_COLOR_ALPHA_RELATIVE = 0.3f;

    // fill color
    protected PDEColor mFillColor;
    // empty color (the color of the non-filled part of the bar)
    protected PDEColor mEmptyColor;
    protected float mCurrentFillValue;
    protected Paint mFillPaint;
    protected Paint mEmptyPaint;


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEUsageBarBackground(Context context) {
        super(context);
        init(context, null);
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEUsageBarBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEUsageBarBackground(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    /**
     * @brief Initialization
     */
    @SuppressWarnings("unused")
    protected void init(Context context, android.util.AttributeSet attrs) {
        // init
        mFillColor = new PDEColor();
        mEmptyColor = new PDEColor();
        mFillPaint = new Paint();
        mFillPaint.setAntiAlias(true);
        mFillPaint.setStyle(Paint.Style.FILL);
        mEmptyPaint = new Paint();
        mEmptyPaint.setAntiAlias(true);
        mEmptyPaint.setStyle(Paint.Style.FILL);
        setColor(PDEColor.valueOf(CONFIGURATION_DEFAULT_FILL_COLOR));
        mCurrentFillValue = 0.0f;
    }


    /**
     * @brief Set the main color of the bar.
     *
     * Sets the main color of the bar. This color is used directly as fill color. The color of the non-filled part
     * (empty color) is derived from that color.
     */
    public void setColor(PDEColor color) {
        // set fill color
        setFillColor(color);
        // derive empty color by combining the main color with a different alpha value
        setEmptyColor(color.newColorWithCombinedAlpha(
                Math.round(CONFIGURATION_RELATION_FILL_TO_EMPTY_COLOR_ALPHA_RELATIVE * 255.0f)));
        // update
        updatePaints();
        invalidate();
    }


    /**
     * @brief Set fill color.
     */
    protected void setFillColor(PDEColor color) {
        // anything to do?
        if (mFillColor.getIntegerColor() == color.getIntegerColor()) return;
        // remember
        mFillColor = color;
    }


    /**
     * @brief Set empty color.
     *
     * Empty color is the color of the non-filled part of the bar.
     */
    protected void setEmptyColor(PDEColor color) {
        // anything to do?
        if (mEmptyColor.getIntegerColor() == color.getIntegerColor()) return;
        // remember
        mEmptyColor = color;
    }


    /**
     * @brief Update the colors of the paints.
     */
    protected void updatePaints() {
        mFillPaint.setColor(mFillColor.getIntegerColor());
        mEmptyPaint.setColor(mEmptyColor.getIntegerColor());
    }


    /**
     * @param relVal the new fill ratio as relative value.
     * @brief Set the current fill ratio.
     *
     * We want to keep this view as simple as possible, so it don't has to know anything about the absolute values. We
     * only want to draw a ratio, so the relative fill value is enough.
     */
    public void setCurrentFillValueRelative(float relVal) {
        // anything to do?
        if (mCurrentFillValue == relVal) return;
        // remember
        mCurrentFillValue = relVal;
        // update
        invalidate();
    }


    /**
     * @brief Draws the bar background.
     */
    public void draw(@NonNull Canvas canvas) {
        int width, height;

        // get width and height of the view
        width = this.getWidth();
        height = this.getHeight();

        // draw the filled part
        canvas.drawRect(0.0f, 0.0f, width * mCurrentFillValue, height, mFillPaint);
        // draw the non-filled part
        canvas.drawRect(width * mCurrentFillValue, 0.0f, width, height, mEmptyPaint);
    }
}
