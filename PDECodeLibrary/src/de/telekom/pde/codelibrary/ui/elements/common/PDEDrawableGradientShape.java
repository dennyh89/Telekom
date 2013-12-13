/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

import android.graphics.*;
import de.telekom.pde.codelibrary.ui.color.PDEColor;


//----------------------------------------------------------------------------------------------------------------------
// PDEDrawableGradientShape
//----------------------------------------------------------------------------------------------------------------------



// todo: Check if we couldn't replace the use of this class by PDEDrawableRoundedGradientBox (or vice versa). They seem to be VERY similar.
//

public class PDEDrawableGradientShape extends PDEDrawableBase {
    protected float mCornerRadius;
    protected int mColors[];
    protected int mElementBackgroundTopColor;
    protected int mElementBackgroundMainColor;
    protected int mElementBackgroundBottomColor;
    private Paint mBackgroundPaint = null;
    private int mShapeType;
    private Path mShapePath;

    // initialization
    public PDEDrawableGradientShape(){
        // init drawable basics
        super();
        //todo initialize with telekom default values
        mCornerRadius = 10.0f;
        mColors = new int[]{0,0,0};
        mBackgroundPaint = new Paint();
        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        mShapePath = new Path();
        update(true);
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Set corner radius (rounded rect).
     *
     * @param radius
     */
    public void setElementCornerRadius(float radius){
        // any change?
        if (mCornerRadius == radius) return;
        // remember
        mCornerRadius = radius;
        // update
        update();
    }


    /**
     * @brief Get corner radius (rounded rect).
     *
     * @return corner radius (rounded rect).
     */
    public float getElementCornerRadius(){
        return mCornerRadius;
    }


    /**
     * @brief Set all colors of the gradient at once.
     *
     * @param topColor
     * @param middleColor
     * @param bottomColor
     */
    public void setElementColors(int topColor, int middleColor, int bottomColor){
        // any change?
        if (topColor == mElementBackgroundTopColor && middleColor == mElementBackgroundMainColor &&
            bottomColor == mElementBackgroundBottomColor) return;
        // remember
        mElementBackgroundTopColor = topColor;
        mElementBackgroundMainColor = middleColor;
        mElementBackgroundBottomColor = bottomColor;
        // update
        update(true);
    }


    /**
     * @brief Set the top color of the gradient.
     *
     * @param color the top color.
     */
    public void setElementTopColor(int color){
        // any change?
        if (mElementBackgroundTopColor == color) return;
        // remember
        mElementBackgroundTopColor = color;
        // update
        update(true);
    }


    /**
     * @brief Set the middle color of the gradient.
     *
     * @param color the middle color.
     */
    public void setElementMiddleColor(int color){
        // any change?
        if (mElementBackgroundMainColor == color) return;
        // remember
        mElementBackgroundMainColor=color;
        // update
        update(true);
    }


    /**
     * @brief Set the bottom color of the gradient.
     *
     * @param color the bottom color.
     */
    public void setElementBottomColor(int color){
        // any change?
        if (mElementBackgroundBottomColor == color) return;
        // remember
        mElementBackgroundBottomColor = color;
        update(true);
    }


    /**
     * @brief Get top color of gradient.
     *
     * @return top color of gradient.
     */
    public int getElementTopColor(){
        return mElementBackgroundTopColor;
    }


    /**
     * @brief Get middle color of gradient.
     *
     * @return middle color of gradient.
     */
    public int getElementMiddleColor(){
        return mElementBackgroundMainColor;
    }


    /**
     * @brief Get bottom color of gradient.
     *
     * @return bottom color of gradient.
     */
    public int getElementBottomColor(){
        return mElementBackgroundBottomColor;
    }

    /**
     * @brief Set custom shape as a path.
     *
     * @param path the custom shape of the element.
     */
    public void setElementShapePath(Path path) {
        // ToDo: Don't know if equals() is meaningful overridden here.
        // any change?
        if (mShapePath.equals(path)) {
            return;
        }

        // store the path
        mShapePath = path;
        mShapeType = PDEAvailableShapes.SHAPE_CUSTOM_PATH;
        // update
        update();
    }

    /**
     * @brief Get custom shape as a path.
     *
     * @return custom path
     */
    public Path getElementShapePath() {
        return mShapePath;
    }

    /**
     * @brief Set a rectangular path
     */
    public void setElementShapeRect() {
        mShapeType = PDEAvailableShapes.SHAPE_RECT;
        update();
    }


    /**
     * @brief Set a rectangular path with rounded corners.
     */
    public void setElementShapeRoundedRect(float cornerRadius) {
        mCornerRadius = cornerRadius;
        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        update();
    }

    /**
     * @brief Set a oval inscribing the rect. Use a square to get a circle
     */
    public void setElementShapeOval() {
        mShapeType = PDEAvailableShapes.SHAPE_OVAL;
        update();
    }

    /**
     * @brief Private helper that applies all new colors.
     */
    private void updateColors() {
        // set colors
        mColors = new int[]{PDEColor.getIntegerColorCombinedWithAlpha(mElementBackgroundTopColor,mAlpha),
                            PDEColor.getIntegerColorCombinedWithAlpha(mElementBackgroundMainColor, mAlpha),
                            PDEColor.getIntegerColorCombinedWithAlpha(mElementBackgroundBottomColor, mAlpha)};
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- Helpers ------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief update background and border paint values
     */
    protected void updateAllPaints() {
        createBackgroundPaint();
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
        // normalized and pixelshifted
        frame = new RectF(mPixelShift,mPixelShift,bounds.width() - mPixelShift,bounds.height() - mPixelShift);
        mBackgroundPaint.setShader(new LinearGradient((frame.right - frame.left) / 2, frame.top,
                                                      (frame.right - frame.left) / 2, frame.bottom, mColors,
                                                      null,Shader.TileMode.MIRROR));
        // ToDo: other shapes than roundedRect need testing
        switch (mShapeType) {
            case PDEAvailableShapes.SHAPE_RECT:
                c.drawRect(frame, mBackgroundPaint);
                break;
            case PDEAvailableShapes.SHAPE_ROUNDED_RECT:
                c.drawRoundRect(frame, mCornerRadius, mCornerRadius, mBackgroundPaint);
                break;
            case PDEAvailableShapes.SHAPE_OVAL:
                c.drawOval(frame, mBackgroundPaint);
                break;
            case PDEAvailableShapes.SHAPE_CUSTOM_PATH:
                c.drawPath(mShapePath, mBackgroundPaint);
                break;
        }
    }

}



