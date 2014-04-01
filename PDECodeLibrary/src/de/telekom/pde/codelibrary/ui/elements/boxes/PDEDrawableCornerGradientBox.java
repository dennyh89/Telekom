/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
* Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
* https://www.design.telekom.com/myaccount/terms-of-use/
*
* Copyright (c) 2012. Neuland Multimedia GmbH.
*/
package de.telekom.pde.codelibrary.ui.elements.boxes;

//----------------------------------------------------------------------------------------------------------------------
// PDEDrawableCornerGradientBox
//----------------------------------------------------------------------------------------------------------------------

import android.graphics.*;
import android.graphics.drawable.Drawable;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableBase;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;

/**
 * @brief Graphics primitive - a box with solid background where you can configure which corners are rounded and which
 * not.
 */
public class PDEDrawableCornerGradientBox extends PDEDrawableBase {

    //-----  properties ------------------------------------------------------------------------------------------------
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
    protected int mElementRoundedCornerConfiguration;
    // drawable helpers
    private PDEDrawableShapedShadow mElementShadowDrawable;
    private Paint mBorderPaint;
    private Paint mBackgroundPaint;
    private Path mElementPath;


    // initialization
    public PDEDrawableCornerGradientBox()
    {
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
        mElementPath = new Path();
        mElementRoundedCornerConfiguration = 0;
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
        if (mElementShadowDrawable != null) {
            return mElementShadowDrawable;
        }
        // init shadow drawable
        mElementShadowDrawable = new PDEDrawableShapedShadow();
        mElementShadowDrawable.setElementShapeOpacity(0.25f);
        setNeededPadding(PDEBuildingUnits.oneHalfBU());
        updateElementShadowDrawable(new Point(getBounds().width(),getBounds().height()));
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
                    bounds.left + elementSize.x + (2 * (int)mElementShadowDrawable.getElementBlurRadius()),
                    bounds.top + elementSize.y + (2 * (int)mElementShadowDrawable.getElementBlurRadius()));
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
     * @brief Set top color of gradient background.
     *
     * @param color The new upper color of the vertical gradient background.
     */
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    public PDEColor getElementBackgroundTopColor() {
        return mElementBackgroundTopColor;
    }


    /**
     * @brief Get background main color.
     *
     * @return The color of the main background.
     */
    @SuppressWarnings("unused")
    public PDEColor getElementBackgroundMainColor() {
        return mElementBackgroundMainColor;
    }


    /**
     * @brief Get background bottom color.
     *
     * @return The color of the bottom background.
     */
    @SuppressWarnings("unused")
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


    /**
     * @brief Set all gradient background colors at once (convenience function).
     *
     * @param top The top color of the gradient background.
     * @param main The middle color of the gradient background.
     * @param bottom The bottom color of the gradient background.
     */
    @SuppressWarnings("unused")
    public void setElementBackgroundGradientColors(PDEColor top, PDEColor main, PDEColor bottom) {
        // any change?
        if (top.getIntegerColor() == mElementBackgroundTopColor.getIntegerColor()
                && main.getIntegerColor() == mElementBackgroundMainColor.getIntegerColor()
                && bottom.getIntegerColor() == mElementBackgroundBottomColor.getIntegerColor()) {
            return;
        }

        // remember
        mElementBackgroundTopColor = top;
        mElementBackgroundMainColor = main;
        mElementBackgroundBottomColor = bottom;

        // update
        updateColors();
        update();
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
    @SuppressWarnings("unused")
    public void setElementGradientDistributionPositions(float top, float main, float bottom) {
        // any change?
        if (mDistributionPositions != null && top == mDistributionPositions[0] &&
            main == mDistributionPositions[1] && bottom == mDistributionPositions[2]) return;

        // remember
        mDistributionPositions = new float[] {top,main,bottom};

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
        elementSize = new Point(getBounds().width()-1, getBounds().height()-1);

        // create the drawing path
        path = createDrawingPath(elementSize);

        // update
        setElementPath(path);

        // update shadow
        updateElementShadowDrawable(elementSize);
    }


    /**
     * @brief Get drawing start point
     *
     * Calculate & deliver the start point of the drawing path.
     * We start drawing at the left startpoint of the top edge.
     * The used pixelShift is needed to avoid an antialiasing bug which appears if we're not correctly pixelaligned.
     *
     * @return start point of drawing
     */
    private PointF getStartPoint(){
        if ((mElementRoundedCornerConfiguration & PDEDrawableCornerBox.PDEDrawableCornerBoxCornerTopLeft) != 0 ) {
            // TopLeftCorner
            return new PointF(mElementCornerRadius + mPixelShift,  mPixelShift);
        } else {
            // default
            return new PointF(mPixelShift, mPixelShift);
        }
    }


    /**
     * @brief Creates and delivers the path that should be drawn.
     *
     * @param elementSize The size of the whole element.
     * @return the shape of the element as a drawing path.
     */
    private Path createDrawingPath(Point elementSize) {
        Path path;
        PointF destination, currentPoint;
        PointF startPoint;

        startPoint = getStartPoint();

        // init
        path = new Path();

        // move to start point, which is the left end of top-line
        path.moveTo(startPoint.x,startPoint.y);
        if ((mElementRoundedCornerConfiguration & PDEDrawableCornerBox.PDEDrawableCornerBoxCornerTopRight ) == 0) {
            if ((mElementRoundedCornerConfiguration & PDEDrawableCornerBox.PDEDrawableCornerBoxCornerTopLeft ) == 0) {
                path.lineTo(startPoint.x+elementSize.x ,startPoint.y);
                currentPoint = new PointF(startPoint.x+elementSize.x ,startPoint.y);
            } else {
                path.lineTo(startPoint.x+elementSize.x - mElementCornerRadius,startPoint.y);
                currentPoint = new PointF(startPoint.x+elementSize.x - mElementCornerRadius,startPoint.y);
            }
            // calculate right end of top-line
            destination = new PointF(currentPoint.x,currentPoint.y + elementSize.y);
        } else {
            if ((mElementRoundedCornerConfiguration & PDEDrawableCornerBox.PDEDrawableCornerBoxCornerTopLeft ) == 0) {
                path.lineTo(startPoint.x+elementSize.x - mElementCornerRadius,startPoint.y);
                currentPoint = new PointF(startPoint.x+elementSize.x - mElementCornerRadius,startPoint.y);
            } else {
                path.lineTo(startPoint.x+elementSize.x - 2 * mElementCornerRadius,startPoint.y);
                currentPoint = new PointF(startPoint.x+elementSize.x - 2 * mElementCornerRadius,startPoint.y);
            }
            // draw left upper corner
            path.arcTo(new RectF(currentPoint.x - mElementCornerRadius, currentPoint.y,
                                 currentPoint.x + mElementCornerRadius, currentPoint.y + 2* mElementCornerRadius),
                       270.0f, 90.0f);
            currentPoint = new PointF(currentPoint.x+mElementCornerRadius,currentPoint.y+mElementCornerRadius);
            destination = new PointF(currentPoint.x,currentPoint.y + elementSize.y-mElementCornerRadius);
        }

        if ((mElementRoundedCornerConfiguration & PDEDrawableCornerBox.PDEDrawableCornerBoxCornerBottomRight ) == 0) {
            path.lineTo(destination.x,destination.y );
            currentPoint = new PointF(destination.x,destination.y);
            destination = new PointF(currentPoint.x - elementSize.x  ,currentPoint.y );
        } else {
            path.lineTo(destination.x,destination.y -mElementCornerRadius);
            currentPoint = new PointF(destination.x,destination.y -mElementCornerRadius);
            // draw right upper corner
            path.arcTo(new RectF(currentPoint.x - 2* mElementCornerRadius, currentPoint.y -mElementCornerRadius,
                                 currentPoint.x , currentPoint.y + mElementCornerRadius),
                       0.0f, 90.0f);
            currentPoint = new PointF(currentPoint.x - mElementCornerRadius,currentPoint.y + mElementCornerRadius);
            destination = new PointF(currentPoint.x - elementSize.x + mElementCornerRadius,currentPoint.y );
        }

        if ((mElementRoundedCornerConfiguration & PDEDrawableCornerBox.PDEDrawableCornerBoxCornerBottomLeft ) == 0) {
            path.lineTo(destination.x ,destination.y);
            currentPoint = new PointF(destination.x ,destination.y);
            destination = new PointF(currentPoint.x  ,currentPoint.y - elementSize.y +mElementCornerRadius);
        } else {
            path.lineTo(destination.x + mElementCornerRadius,destination.y );
            currentPoint = new PointF(destination.x + mElementCornerRadius,destination.y );
            // draw right-lower corner
            path.arcTo(new RectF(currentPoint.x - mElementCornerRadius, currentPoint.y - 2* mElementCornerRadius,
                                 currentPoint.x + mElementCornerRadius, currentPoint.y ),
                       90.0f, 90.0f);
            currentPoint = new PointF(currentPoint.x - mElementCornerRadius,currentPoint.y - mElementCornerRadius);
            destination = new PointF(currentPoint.x ,currentPoint.y - elementSize.y + 2* mElementCornerRadius);
        }

        if ((mElementRoundedCornerConfiguration & PDEDrawableCornerBox.PDEDrawableCornerBoxCornerTopLeft) == 0)
        {
            path.lineTo(destination.x, destination.y);
        } else {
            path.lineTo(destination.x,destination.y + mElementCornerRadius);
            // draw left-lower corner
            path.arcTo(new RectF(destination.x , destination.y - mElementCornerRadius,
                                 destination.x + 2 * mElementCornerRadius, destination.y +  mElementCornerRadius),
                       180.0f, 90.0f);
        }
        path.close();
        return path;
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
    @Override
    protected void updateDrawingBitmap(Canvas c, Rect bounds) {
        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) {
            return;
        }
        mBackgroundPaint.setShader(new LinearGradient((bounds.right - bounds.left) / 2, bounds.top,
                                                      (bounds.right - bounds.left) / 2, bounds.bottom, mColors,
                                                      mDistributionPositions,
                                                      Shader.TileMode.MIRROR));
        c.drawPath(mElementPath, mBackgroundPaint);
        c.drawPath(mElementPath, mBorderPaint);
    }

}
