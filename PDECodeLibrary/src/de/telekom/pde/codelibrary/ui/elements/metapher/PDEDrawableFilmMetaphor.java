/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
* Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
* https://www.design.telekom.com/myaccount/terms-of-use/
*
* Copyright (c) 2012. Neuland Multimedia GmbH.
*/
package de.telekom.pde.codelibrary.ui.elements.metapher;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.components.drawables.PDEDrawableBase;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableFilmMetaphor
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows Film Metaphor
 */
@SuppressWarnings("unused")
public class PDEDrawableFilmMetaphor extends PDEDrawableBase {

//-----  properties ---------------------------------------------------------------------------------------------------
    private Drawable mPicture;
    private final static float CONST_ASPECTRATIO = 20.0f / 29.0f;
    private PDEColor mOutlineColor;
    private Paint mOutlinePaint;
    private PDEColor mOutlineFillingColor;
    private Paint mOutlineFillingPaint;
    private PDEColor mLeftLineColor;
    private Paint mLeftLinePaint;
    private Paint mBlurPaint;
    private PDEColor mHandleColor;
    private Paint mHandlePaint;
    private PDEColor mHandleFillingColor;
    private Paint mHandleFillingPaint;
    private PDEColor mShapeColor;
    private Paint mShapePaint;
    private PDEColor mElementBlurLeftColor;
    private PDEColor mElementBlurMainColor;
    private PDEColor mElementBlurRightColor;
    private Path mShapePath;
    private float mOuterCornerRadius;
    private float mHandleCornerRadius;
    private Rect mPictureRect;
    private RectF mOuterRect;
    private RectF mHandleRect;
    private LinearGradient mGradient;
    protected int mColors[];
    // outer shadow
    private PDEDrawableShapedShadow mElementShadowDrawable;


//----- init -----------------------------------------------------------------------------------------------------------
    /**
     * @brief Constructor
     */
    public PDEDrawableFilmMetaphor(Drawable drawable)
    {
        // init drawable basics
        super();
        // init PDE defaults
        mPicture = drawable;
        // shadow is created on demand
        mElementShadowDrawable = null;

        //define colors

        //color of outline
        mOutlineColor = new PDEColor();
        mOutlineColor.setColor(PDEColor.valueOf("#d0d0d0").getIntegerColor());
        //background color of border
        mOutlineFillingColor = new PDEColor();
        mOutlineFillingColor.setColor(PDEColor.valueOf("#f2f2f2").getIntegerColor());
        mOutlineFillingColor = mOutlineFillingColor.newColorWithCombinedAlpha(77);
        //color of the line at the left
        mLeftLineColor = new PDEColor();
        mLeftLineColor.setColor(PDEColor.valueOf("#000000").getIntegerColor());
        mElementBlurLeftColor = PDEColor.valueOf("#FF7C7C7C");
        mElementBlurMainColor = PDEColor.valueOf("#887C7C7C");
        mElementBlurRightColor = PDEColor.valueOf("#007C7C7C");
        mColors = new int[]{mElementBlurLeftColor.getIntegerColor(),
                mElementBlurMainColor.getIntegerColor(),
                mElementBlurRightColor.getIntegerColor()};
        //color of border of handle
        mHandleColor = new PDEColor();
        mHandleColor.setColor(PDEColor.valueOf("#d0d0d0").getIntegerColor());
        //background color of the handle
        mHandleFillingColor = new PDEColor();
        mHandleFillingColor.setColor(PDEColor.valueOf("#f2f2f2").getIntegerColor());
        mHandleFillingColor = mOutlineFillingColor.newColorWithCombinedAlpha(128);
        //color of the shape over the case
        mShapeColor = new PDEColor();
        mShapeColor.setColor(PDEColor.valueOf("#ffffff").getIntegerColor());
        mShapeColor = mShapeColor.newColorWithCombinedAlpha(31);

        // update all paints
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
        updateElementShadowDrawable(new Point(getBounds().width(),getBounds().height()));
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
            frame = new Rect(bounds.left, bounds.top, bounds.left + elementSize.x, bounds.top + elementSize.y);
            mElementShadowDrawable.setBounds(frame);
        }
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Set Picture
     */
    public void setElementPicture(Drawable picture) {
        //any change?
        if (picture == mPicture) return;
        //remember
        mPicture = picture;
        //redraw
        update();
    }


    /**
     * @brief Get Picture
     */
    public Drawable getElementPicture() {
        return mPicture;
    }


    /**
     * @brief Create Shape Path for the white Area over the Cover
     *
     * @param bounds Size of the element
     * @return Path of the overlay
     */
    private Path elementCreateShapePath(Rect bounds) {
        Path shapepath = new Path();
        //start at top left
        shapepath.moveTo(bounds.left, bounds.top);
        //move down
        shapepath.lineTo(bounds.left, (16.5f / 29.0f) * bounds.height());
        //move to upper right
        shapepath.lineTo(bounds.right, (5.5f / 29.0f) * bounds.height());
        //move to top right
        shapepath.lineTo(bounds.right, bounds.top);
        //close path
        shapepath.close();

        return shapepath;
    }


    /**
     * @brief Called when bounds set via rect.
     */
    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(elementCalculateAspectRatioBounds(bounds));
    }


    /**
     * @brief Called when bounds set via left/top/right/bottom values.
     */
    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        Rect aspectRatioBounds = elementCalculateAspectRatioBounds(new Rect(left, top, right, bottom));
        super.setBounds(aspectRatioBounds.left, aspectRatioBounds.top, aspectRatioBounds.right, aspectRatioBounds.bottom);
    }


    /**
     * @brief Calculate the correct aspect ratio bounds.
     *
     * @param bounds Available space
     * @return  Rect with correct aspect ratio, fitting in available space
     */
    private Rect elementCalculateAspectRatioBounds(Rect bounds) {
        Rect newBounds = null;

        //calculate size, based on aspect ratio
        if ((float)bounds.width() / (float)bounds.height() > CONST_ASPECTRATIO ) {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);
            newBounds.right = newBounds.left + Math.round(((float)newBounds.height() * CONST_ASPECTRATIO));
        } else {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);
            newBounds.bottom = newBounds.top + Math.round((float)newBounds.width() / CONST_ASPECTRATIO);
        }

        return newBounds;
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- layout / sizing ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Set width of the element.
     *
     * Convenience function.
     *
     * @param width The new width of the element.
     */
    @Override
    public void setLayoutWidth(int width) {
        setLayoutSize(new Point(width, Math.round((float) width / CONST_ASPECTRATIO)));
    }


    /**
     * @brief Set height of the element.
     *
     * Convenience function.
     *
     * @param height The new height of the element.
     */
    @Override
    public void setLayoutHeight(int height) {
        setLayoutSize(new Point(Math.round(height * CONST_ASPECTRATIO), height));
    }


    /**
     * @brief Update all of my sublayers.
     */
    @Override
    protected void doLayout() {
        Point elementSize;

        // get the rect we're using for layouting
        elementSize = new Point(getBounds().width(), getBounds().height());
        // do needed layout calculations.
        performLayoutCalculations(new Rect(getBounds()));
        // update shadow drawable
        updateElementShadowDrawable(elementSize);
    }


    /**
     * @brief Perform the layout calculations that are needed before the next drawing phase (because bounds have
     * changed).
     */
    private void performLayoutCalculations(Rect bounds){
        RectF frame = new RectF(mPixelShift, mPixelShift, bounds.width() - mPixelShift,
                                bounds.height() - mPixelShift);
        //outline size
        mOuterRect = new RectF(frame.left - (0.5f / 20.0f) * frame.width(), frame.top + 1, frame.right, frame.bottom);
        mOuterCornerRadius = (0.5f / 20.0f) * frame.width();
        //picture size
        mPictureRect = new Rect(Math.round(frame.left),
                                Math.round((0.5f / 29.0f) * frame.height()),
                                Math.round((19.5f / 20.0f) * frame.width()),
                                Math.round(frame.bottom - (0.5f / 29.0f) * frame.height()));
        //handle size
        mHandleCornerRadius = (0.3f / 20.0f) * frame.width();
        mHandleRect = new RectF(Math.round((19.0f / 20.0f) * frame.width()),
                                Math.round((12.5f / 29.0f) * frame.height()),
                                frame.right+Math.round((0.5f / 20.0f) * frame.width()),
                                frame.bottom-Math.round((12.5f / 29.0f) * frame.height()));
        //shape size
        mShapePath = elementCreateShapePath(new Rect(Math.round(frame.left), Math.round(frame.top),
                                                     Math.round(frame.right), Math.round(frame.bottom)));
        //blur radius on the left
        updateBlurColors();
        mGradient = new LinearGradient( frame.left,
                                        (frame.top - frame.bottom) / 2, (0.75f / 20.0f) * frame.width() ,
                                        (frame.top - frame.bottom) / 2,mColors,null,
                                        Shader.TileMode.MIRROR);
    }


    /**
     * @brief Private helper that applies all new colors.
     */
    private void updateBlurColors() {
        // set colors
        mColors = new int[]{mElementBlurLeftColor.newIntegerColorWithCombinedAlpha(mAlpha),
                            mElementBlurMainColor.newIntegerColorWithCombinedAlpha(mAlpha),
                            mElementBlurRightColor.newIntegerColorWithCombinedAlpha(mAlpha)};
    }




//---------------------------------------------------------------------------------------------------------------------
// ----- Helpers ------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief update all used paints
     */
    @Override
    protected void updateAllPaints() {
        createBlurPaint();
        createHandleFillingPaint();
        createHandlePaint();
        createLeftLinePaint();
        createOutlineFillingPaint();
        createOutlinePaint();
        createShapePaint();
    }


    /**
     * @brief create paint for blur.
     */
    private void createBlurPaint() {
        mBlurPaint = new Paint();
        mBlurPaint.setAntiAlias(true);
        mBlurPaint.setColorFilter(mColorFilter);
        mBlurPaint.setDither(mDither);
        updateBlurColors();
    }


    /**
     * @brief create paint for filling of handle.
     */
    private void createHandleFillingPaint() {
        mHandleFillingPaint = new Paint();
        mHandleFillingPaint.setAntiAlias(true);
        mHandleFillingPaint.setColorFilter(mColorFilter);
        mHandleFillingPaint.setDither(mDither);
        mHandleFillingPaint.setColor(mOutlineFillingColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief create paint for outline of left handle.
     */
    private void createHandlePaint() {
        mHandlePaint = new Paint();
        mHandlePaint.setAntiAlias(true);
        mHandlePaint.setStyle(Paint.Style.STROKE);
        mHandlePaint.setColorFilter(mColorFilter);
        mHandlePaint.setDither(mDither);
        mHandlePaint.setColor(mHandleColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief create paint for left line.
     */
    private void createLeftLinePaint() {
        mLeftLinePaint = new Paint();
        mLeftLinePaint.setAntiAlias(true);
        mLeftLinePaint.setStrokeWidth(3);
        mLeftLinePaint.setColorFilter(mColorFilter);
        mLeftLinePaint.setDither(mDither);
        mLeftLinePaint.setColor(mLeftLineColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief create paint for outline filling.
     */
    private void createOutlineFillingPaint() {
        mOutlineFillingPaint = new Paint();
        mOutlineFillingPaint.setAntiAlias(true);
        mOutlineFillingPaint.setColorFilter(mColorFilter);
        mOutlineFillingPaint.setDither(mDither);
        mOutlineFillingPaint.setColor(mOutlineFillingColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief create paint for outline.
     */
    private void createOutlinePaint() {
        mOutlinePaint = new Paint();
        mOutlinePaint.setAntiAlias(true);
        mOutlinePaint.setStyle(Paint.Style.STROKE);
        mOutlinePaint.setColorFilter(mColorFilter);
        mOutlinePaint.setDither(mDither);
        mOutlinePaint.setColor(mOutlineColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief create paint for shape.
     */
    private void createShapePaint() {
        mShapePaint = new Paint();
        mShapePaint.setAntiAlias(true);
        mShapePaint.setColorFilter(mColorFilter);
        mShapePaint.setDither(mDither);
        mShapePaint.setColor(mShapeColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief Changes of paint properties should also affect the picture, so use the update hook for this.
     *
     * @param paintPropertiesChanged shows if an update of the used Paint-Instances is needed.
     */
    @Override
    protected void updateHook(boolean paintPropertiesChanged) {
        if (!paintPropertiesChanged) return;
        mPicture.setAlpha(mAlpha);
        mPicture.setDither(mDither);
        mPicture.setColorFilter(mColorFilter);
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
        RectF frame;

        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) return;
        frame = new RectF(mPixelShift, mPixelShift, bounds.width() - mPixelShift, bounds.height() - mPixelShift);
        c.clipRect(frame);
        //draws case if width > 5 BUs, only picture else
        if (bounds.width() >= PDEBuildingUnits.pixelFromBU(5)) {
            //draw outline
            c.drawRoundRect(mOuterRect, mOuterCornerRadius, mOuterCornerRadius, mOutlineFillingPaint);
            c.drawRoundRect(mOuterRect, mOuterCornerRadius, mOuterCornerRadius, mOutlinePaint);
            //draw picture
            mPicture.setBounds(mPictureRect);
            mPicture.draw(c);
            //draw line on the left
            mBlurPaint.setShader(mGradient);
            c.drawRect(frame.left, frame.top, (0.75f/20.0f) * frame.width(), frame.bottom, mBlurPaint);
            c.drawLine(frame.left, frame.top, frame.left, frame.bottom, mLeftLinePaint);
            //draw handle
            c.drawRoundRect(mHandleRect, mHandleCornerRadius, mHandleCornerRadius, mHandleFillingPaint);
            c.drawRoundRect(mHandleRect, mHandleCornerRadius, mHandleCornerRadius, mHandlePaint);
            //draw shape over the case
            c.clipPath(mShapePath);
            c.drawRoundRect(mOuterRect, mOuterCornerRadius, mOuterCornerRadius, mShapePaint);
        } else  {
            mPicture.setBounds(Math.round(frame.left), Math.round(frame.top),
                    Math.round(frame.right), Math.round(frame.bottom));
            mPicture.draw(c);
        }
    }

}
