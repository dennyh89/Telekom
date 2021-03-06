/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
* Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
* https://www.design.telekom.com/myaccount/terms-of-use/
*
* Copyright (c) 2012. Neuland Multimedia GmbH.
*/
package de.telekom.pde.codelibrary.ui.elements.boxes;

//----------------------------------------------------------------------------------------------------------------------
// PDEDrawableCornerBox
//----------------------------------------------------------------------------------------------------------------------

import android.graphics.*;
import android.graphics.drawable.Drawable;

import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableBase;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;
import de.telekom.pde.codelibrary.ui.elements.common.PDECornerConfigurations;

/**
 * @brief Graphics primitive - a box with solid background where you can configure which corners are rounded and which
 * not.
 */
public class PDEDrawableCornerBox extends PDEDrawableBase {

    //-----  properties ---------------------------------------------------------------------------------------------------
    // basic properties
    // colors
    protected PDEColor mElementBackgroundColor;
    protected PDEColor mElementBorderColor;
    // measurements
    protected float mElementBorderWidth;
    protected float mElementCornerRadius;
    protected int mElementRoundedCornerConfiguration;
    // drawable helpers
    private PDEDrawableShapedShadow mElementShadowDrawable;
    private Paint mBorderPaint;
    private Paint mBackgroundPaint;
    private Path mElementPath;


    // initialization
    public PDEDrawableCornerBox() {
        // init to PDE defaults
        mElementBackgroundColor = PDEColor.valueOf("DTWhite");
        mElementBorderColor = PDEColor.valueOf("DTGrey237_Idle_Border");
        mElementBorderWidth = 1.0f;
        mElementCornerRadius = PDEBuildingUnits.twoThirdsBU();
        mElementPath = new Path();
        mElementRoundedCornerConfiguration = 0;
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
        if (mElementShadowDrawable != null) {
            return mElementShadowDrawable;
        }
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
    @SuppressWarnings("unused")
    public Drawable getElementShadow() {
        // return
        return mElementShadowDrawable;
    }


    /**
     * @brief forget shadow drawable.
     */
    @SuppressWarnings("unused")
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
            Path shadowPath;

            // set shadow bounds
            frame = new Rect(bounds.left,
                             bounds.top,
                             bounds.left + elementSize.x + (2 * (int) mElementShadowDrawable.getElementBlurRadius()),
                             bounds.top + elementSize.y + (2 * (int) mElementShadowDrawable.getElementBlurRadius()));
            mElementShadowDrawable.setBounds(frame);
            // make a copy of the original element path for the shadow
            shadowPath = new Path(mElementPath);
            // offset by blur radius of shadow
            shadowPath.offset(mElementShadowDrawable.getElementBlurRadius(),
                              mElementShadowDrawable.getElementBlurRadius());
            mElementShadowDrawable.setElementShapePath(shadowPath);
        }
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Set fill (background) color.
     *
     * @param color The new background color of the speech bubble.
     */
    public void setElementBackgroundColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementBackgroundColor.getIntegerColor()) {
            return;
        }

        // remember
        mElementBackgroundColor = color;
        mBackgroundPaint.setColor(mElementBackgroundColor.newIntegerColorWithCombinedAlpha(mAlpha));

        // update
        update();
    }


    /**
     * @brief Get background color.
     *
     * @return The color of the background.
     */
    public PDEColor getElementBackgroundColor() {
        return mElementBackgroundColor;
    }


    /**
     * @brief Set border color.
     *
     * @param color The new color of the outline.
     */
    public void setElementBorderColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementBorderColor.getIntegerColor()) {
            return;
        }

        // remember
        mElementBorderColor = color;
        mBorderPaint.setColor(mElementBorderColor.newIntegerColorWithCombinedAlpha(mAlpha));

        // update
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


//---------------------------------------------------------------------------------------------------------------------
// ----- layout / sizing ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Get border width.
     *
     * @return The width of the outline.
     */
    @SuppressWarnings("unused")
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
        if (radius == mElementCornerRadius) {
            return;
        }

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
        if (width == mElementBorderWidth) {
            return;
        }

        // remember
        mElementBorderWidth = width;
        mBorderPaint.setStrokeWidth(mElementBorderWidth);

        // update
        update();
    }


    /**
     * @brief Sets the complete path for being drawn
     *
     * @param path The path that should be drawn on screen.
     */
    private void setElementPath(Path path) {
        // change?
        if (mElementPath == path) {
            return;
        }

        // store a copy of the path
        mElementPath = path;
    }


    /**
     * @brief Sets the configuration of the rounded corners
     *
     * @param cornerConfiguration configuration of which corners should be rounded.
     */
    public void setElementRoundedCornerConfiguration(int cornerConfiguration) {
        // change?
        if (mElementRoundedCornerConfiguration == cornerConfiguration) {
            return;
        }

        // remember
        mElementRoundedCornerConfiguration = cornerConfiguration;

        // update
        doLayout();
        update();
    }


    /**
     * @brief Gets the configuration of the rounded corners.
     */
    @SuppressWarnings("unused")
    public int getElementRoundedCornerConfiguration() {
        return mElementRoundedCornerConfiguration;
    }


    /**
     * @brief Update all of my sublayers.
     */
    protected void doLayout() {
        Point elementSize;
        Path path;

        // get the rect we're using for layouting
        elementSize = new Point(getBounds().width() - 1, getBounds().height() - 1);

        // create the drawing path
        path = PDECornerConfigurations.createDrawingPath(mElementRoundedCornerConfiguration, elementSize,
                                                         mElementCornerRadius, getStartPoint());

        // update
        setElementPath(path);

        // update shadow
        updateElementShadowDrawable(elementSize);
    }


    /**
     * @brief Get drawing start point.
     *
     * Calculate & deliver the start point of the drawing path.
     * We start drawing at the left start point of the top edge.
     * The used pixelShift is needed to avoid an antialiasing bug which appears if we're not correctly pixel-aligned.
     *
     * @return start point of drawing
     */
    private PointF getStartPoint() {
        /*
        if ((mElementRoundedCornerConfiguration & PDECornerConfigurations.PDECornerConfigurationTopLeft) != 0 ) {
            // TopLeftCorner
            return new PointF(mElementCornerRadius + mPixelShift,  mPixelShift);
        } else {
            // default
            return new PointF(mPixelShift, mPixelShift);
        }*/
        return new PointF(mPixelShift, mPixelShift);
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- Helpers ------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief update background and border paint values
     */
    @Override
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
        mBackgroundPaint.setColor(mElementBackgroundColor.newIntegerColorWithCombinedAlpha(mAlpha));

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
    @Override
    protected void updateDrawingBitmap(Canvas c, Rect bounds) {
        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) {
            return;
        }
        c.drawPath(mElementPath, mBackgroundPaint);
        c.drawPath(mElementPath, mBorderPaint);
    }

}
