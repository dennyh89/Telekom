/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.drawables.PDEDrawableBase;

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableShape
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Draws various shapes like rectangle, rounded rectangle, oval or custom path.
 *
 * Does only fill and no stroke (borderline) so far. Maybe we should extend this to save another class (like
 * PDEDrawableShape).
 */
public class PDEDrawableShape extends PDEDrawableBase {

//-----  properties ---------------------------------------------------------------------------------------------------
    protected float mElementCornerRadius;
    protected int mElementBackgroundColor;
    private Paint mBackgroundPaint = null;
    private int mShapeType;
    private Path mShapePath;

//----- init -----------------------------------------------------------------------------------------------------------

    // initialization
    public PDEDrawableShape(){
        mElementCornerRadius = 0.0f;
        mElementBackgroundColor = 0xFF000000;
        mBackgroundPaint = new Paint();
        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        mShapePath = new Path();

        //init paints for drawing
        update(true);
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Set corner radius.
     *
     * @param radius
     */
    public void setElementCornerRadius(float radius){
        // any change?
        if (radius == mElementCornerRadius) return;
        // remember
        mElementCornerRadius = radius;
        // update
        update();
    }


    /**
     * @brief Get corner radius.
     *
     * @return corner radius.
     */
    public float getElementCornerRadius(){
        return mElementCornerRadius;
    }


    /**
     * @brief Set background color of the element.
     *
     * @param color background color.
     */
    public void setElementBackgroundColor(int color){
        // any change?
        if (color == mElementBackgroundColor) return;
        // remember
        mElementBackgroundColor = color;
        // update
        update(true);
    }


    /**
     * @brief Get background color.
     *
     * @return background color.
     */
    public int getElementBackgroundColor(){
        return mElementBackgroundColor;
    }


    /**
     * @brief Set a path as custom shape.
     *
     * @param path
     */
    public void setElementShapePath(Path path) {
        // ToDo: Don't know if equals() is meaningful overriden here.
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
     * @brief Get custom shaped path.
     *
     * @return
     */
    public Path getElementShapePath() {
        return mShapePath;
    }


    /**
     * @brief Set a rectangular path
     */
    public void setElementShapeRect() {
        mShapeType = PDEAvailableShapes.SHAPE_RECT;
        // update
        update();
    }


    /**
     * @brief Set a rectangular path with rounded corners.
     */
    public void setElementShapeRoundedRect(float cornerRadius) {
        mElementCornerRadius = cornerRadius;
        mShapeType = PDEAvailableShapes.SHAPE_ROUNDED_RECT;
        // update
        update();
    }


    /**
     * @brief Set a oval inscribing the rect. Use a square to get a circle
     */
    public void setElementShapeOval() {
        mShapeType = PDEAvailableShapes.SHAPE_OVAL;
        // update
        update();
    }


    /**
     * @brief Convenience function.
     *
     * @param opacity
     */
    public void setElementShapeOpacity(float opacity){
        int alpha = Math.round(opacity*255);
        setAlpha(alpha);
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
        mBackgroundPaint.setColor(PDEColor.getIntegerColorCombinedWithAlpha(mElementBackgroundColor,mAlpha));
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

        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) return;
        // normalized
        RectF frame = new RectF(0,0,bounds.width(),bounds.height());

        // ToDo: other shapes than roundedRect need testing
        switch (mShapeType) {
            case PDEAvailableShapes.SHAPE_RECT:
                c.drawRect(frame, mBackgroundPaint);
                break;
            case PDEAvailableShapes.SHAPE_ROUNDED_RECT:
                c.drawRoundRect(frame, mElementCornerRadius, mElementCornerRadius,mBackgroundPaint);
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
