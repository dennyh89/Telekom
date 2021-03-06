/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
* Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
* https://www.design.telekom.com/myaccount/terms-of-use/
*
* Copyright (c) 2012. Neuland Multimedia GmbH.
*/


package de.telekom.pde.codelibrary.ui.elements.boxes;

//----------------------------------------------------------------------------------------------------------------------
// PDEDrawableRoundedGradientBox
//----------------------------------------------------------------------------------------------------------------------

import android.graphics.*;
import android.graphics.drawable.Drawable;

import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableBase;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;


/**
 * @brief Graphics primitive - a box in the shape of a rounded rectangle with a gradient background.
 */
@SuppressWarnings("unused")
public class PDEDrawableRoundedGradientBox extends PDEDrawableBase {

    //-----  properties ---------------------------------------------------------------------------------------------------
    // basic properties
    // colors
    protected PDEColor mElementBackgroundTopColor;
    protected PDEColor mElementBackgroundMainColor;
    protected PDEColor mElementBackgroundBottomColor;
    protected PDEColor mElementBorderColor;
    protected int mColors[];
    protected float mDistributionPositions[];
    // measurements
    protected float mElementBorderWidth;
    protected float mElementCornerRadius;
    // drawable helpers
    private PDEDrawableShapedShadow mElementShadowDrawable;
    private Paint mBorderPaint;
    private Paint mBackgroundPaint;


    // initialization
    public PDEDrawableRoundedGradientBox() {
        // init drawable basics
        super();
        // init to PDE defaults
        mElementBackgroundTopColor = PDEColor.valueOf("DTGrey237_Idle_GradientLighter");
        mElementBackgroundMainColor = PDEColor.valueOf("DTGrey237_Idle_GradientCenter");
        mElementBackgroundBottomColor = PDEColor.valueOf("DTGrey237_Idle_GradientDarker");
        mElementBorderColor = PDEColor.valueOf("DTGrey237_Idle_Border");
        mElementBorderWidth = 1.0f;
        mElementCornerRadius = PDEBuildingUnits.twoThirdsBU();
        mColors = new int[]{mElementBackgroundTopColor.getIntegerColor(),
                            mElementBackgroundMainColor.getIntegerColor(),
                            mElementBackgroundBottomColor.getIntegerColor()};

        mDistributionPositions = null;
        // shadow is created on demand
        mElementShadowDrawable = null;


        //init paints for drawing
        update(true);
    }

//---------------------------------------------------------------------------------------------------------------------
// ----- optional shadow ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief init shadow drawable.
     *
     * Creates and delivers the outer shadow drawable.
     *
     * @return The outer shadow drawable.
     */
    public Drawable createElementShadow() {
        // already created?
        if (mElementShadowDrawable != null) return mElementShadowDrawable;
        // init shadow drawable
        mElementShadowDrawable = new PDEDrawableShapedShadow();
        mElementShadowDrawable.setElementShapeOpacity(0.25f);
        setNeededPadding(PDEBuildingUnits.oneHalfBU());
        updateElementShadowDrawable(new Point(getBounds().width(), getBounds().height()));
        // return
        return mElementShadowDrawable;
    }


    /**
     * @brief shadow getter
     *
     * @return drawable of outer shadow
     */
    public Drawable getElementShadow() {
        // return
        return mElementShadowDrawable;
    }


    /**
     * @brief forget shadow drawable.
     */
    public void clearElementShadow() {
        mElementShadowDrawable = null;
        setNeededPadding(0);
    }


    /**
     * @brief Update the shadow drawable if we've got one.
     */
    private void updateElementShadowDrawable(Point elementSize) {

        // check if we have a shadow set
        if (mElementShadowDrawable != null) {

            // keep current shadow position, just update the size
            Rect frame;
            Rect bounds = mElementShadowDrawable.getBounds();
            frame = new Rect(bounds.left,
                             bounds.top,
                             bounds.left + elementSize.x + (2 * (int) mElementShadowDrawable.getElementBlurRadius()),
                             bounds.top + elementSize.y + (2 * (int) mElementShadowDrawable.getElementBlurRadius()));
            mElementShadowDrawable.setBounds(frame);
            mElementShadowDrawable.setElementShapeRoundedRect(mElementCornerRadius);
        }
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Set top color of gradient background.
     *
     * @param color The new upper color of the vertical gradient background.
     */
    public void setElementBackgroundTopColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementBackgroundTopColor.getIntegerColor()) return;

        // remember
        mElementBackgroundTopColor = color;

        // update
        updateColors();
        update();
    }


    /**
     * @brief Set top color of gradient background.
     *
     * @param color The new upper color of the vertical gradient background.
     */
    public void setElementBackgroundMainColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementBackgroundMainColor.getIntegerColor()) return;

        // remember
        mElementBackgroundMainColor = color;

        // update
        updateColors();
        update();
    }


    /**
     * @brief Set top color of gradient background.
     *
     * @param color The new upper color of the vertical gradient background.
     */
    public void setElementBackgroundBottomColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementBackgroundBottomColor.getIntegerColor()) return;

        // remember
        mElementBackgroundBottomColor = color;

        // update
        updateColors();
        update();
    }


    /**
     * @brief Get background top color.
     *
     * @return The color of the top background.
     */
    public PDEColor getElementBackgroundTopColor() {
        return mElementBackgroundTopColor;
    }


    /**
     * @brief Get background main color.
     *
     * @return The color of the main background.
     */
    public PDEColor getElementBackgroundMainColor() {
        return mElementBackgroundMainColor;
    }


    /**
     * @brief Get background bottom color.
     *
     * @return The color of the bottom background.
     */
    public PDEColor getElementBackgroundBottomColor() {
        return mElementBackgroundBottomColor;
    }


    /**
     * @brief Set border color.
     *
     * @param color The new color of the outline.
     */
    public void setElementBorderColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementBorderColor.getIntegerColor()) return;

        // remember
        mElementBorderColor = color;
        mBorderPaint.setColor(mElementBorderColor.newIntegerColorWithCombinedAlpha(mAlpha));

        // update
        update();
    }


    /**
     * @brief Set all gradient background colors at once (convenience function).
     *
     * @param top The top color of the gradient background.
     * @param main The middle color of the gradient background.
     * @param bottom The bottom color of the gradient background.
     */
    public void setElementBackgroundGradientColors(PDEColor top, PDEColor main, PDEColor bottom) {
        // any change?
        if (top.getIntegerColor() == mElementBackgroundTopColor.getIntegerColor()
            && main.getIntegerColor() == mElementBackgroundMainColor.getIntegerColor()
            && bottom.getIntegerColor() == mElementBackgroundBottomColor.getIntegerColor()) return;

        // remember
        mElementBackgroundTopColor = top;
        mElementBackgroundMainColor = main;
        mElementBackgroundBottomColor = bottom;

        // update
        updateColors();
        update();
    }


    /**
     * @brief Get outline color.
     *
     * @return The color of the outline.
     */
    public PDEColor getElementBorderColor() {
        return mElementBorderColor;
    }


    /**
     * @brief Private helper that applies all new colors.
     */
    private void updateColors() {
        // set colors
        mColors = new int[]{mElementBackgroundTopColor.newIntegerColorWithCombinedAlpha(mAlpha),
                            mElementBackgroundMainColor.newIntegerColorWithCombinedAlpha(mAlpha),
                            mElementBackgroundBottomColor.newIntegerColorWithCombinedAlpha(mAlpha)};
    }


    /**
     * @brief Set the positions how the gradient colors should be distributed.
     *
     * Don't set anything if you want the colors evenly distributed (default).
     *
     * @param top The top color position.
     * @param main The middle color position.
     * @param bottom The bottom color position.
     */
    public void setElementGradientDistributionPositions(float top, float main, float bottom) {
        // any change?
        if (mDistributionPositions != null
            && top == mDistributionPositions[0]
            && main == mDistributionPositions[1]
            && bottom == mDistributionPositions[2]) return;

        // remember
        mDistributionPositions = new float[]{top, main, bottom};

        // update
        update();
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- layout / sizing ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Get border width.
     *
     * @return The width of the outline.
     */
    public float getElementBorderWidth() {
        return mElementBorderWidth;
    }


    /**
     * @brief Set corner radius.
     *
     * @param radius The new radius of the rounded corners.
     */
    public void setElementCornerRadius(float radius) {
        // any change?
        if (radius == mElementCornerRadius) return;

        // remember
        mElementCornerRadius = radius;

        // update
        doLayout();
        update();
    }


    /**
     * @brief Get corner radius.
     *
     * @return The radius of the rounded corners.
     */
    public float getElementCornerRadius() {
        return mElementCornerRadius;
    }


    /**
     * @brief Set border width.
     *
     * @param width The new width of the outline.
     */
    public void setElementBorderWidth(float width) {
        // any change?
        if (width == mElementBorderWidth) return;

        // remember
        mElementBorderWidth = width;
        mBorderPaint.setStrokeWidth(mElementBorderWidth);

        // update
        update();
    }


    /**
     * @brief Update all of my sub-layers.
     */
    @Override
    protected void doLayout() {
        Point elementSize;

        // get the rect we're using for layout
        elementSize = new Point(getBounds().width(), getBounds().height());

        // update shadow drawable
        updateElementShadowDrawable(elementSize);

    }


//---------------------------------------------------------------------------------------------------------------------
// ----- Helpers ------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief update background and border paint values
     */
    protected void updateAllPaints() {
        createBackgroundPaint();
        createBorderPaint();
    }


    /**
     * @brief create background paint for drawing
     */
    private void createBackgroundPaint() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColorFilter(mColorFilter);
        mBackgroundPaint.setDither(mDither);
        updateColors();
    }


    /**
     * @brief create border paint for drawing
     */
    private void createBorderPaint() {
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mElementBorderWidth);
        mBorderPaint.setColorFilter(mColorFilter);
        mBorderPaint.setDither(mDither);
        mBorderPaint.setColor(mElementBorderColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- Drawing Bitmap ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Updates our drawing bitmap and triggers a redraw of this element.
     *
     * If a drawing parameter changes, we need to call this function in order to update our drawing-bitmap and
     * in order to trigger the draw of our updated bitmap to the canvas.
     */
    protected void updateDrawingBitmap(Canvas c, Rect bounds) {
        RectF frame;

        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) return;

        // normalized and pixel-shifted
        frame = new RectF(mPixelShift, mPixelShift, bounds.width() - mPixelShift, bounds.height() - mPixelShift);
        mBackgroundPaint.setShader(new LinearGradient((frame.right - frame.left) / 2, frame.top,
                                                      (frame.right - frame.left) / 2, frame.bottom,
                                                      mColors,
                                                      mDistributionPositions,
                                                      Shader.TileMode.MIRROR));
        c.drawRoundRect(frame, mElementCornerRadius, mElementCornerRadius, mBackgroundPaint);
        c.drawRoundRect(frame, mElementCornerRadius, mElementCornerRadius, mBorderPaint);
    }
}
