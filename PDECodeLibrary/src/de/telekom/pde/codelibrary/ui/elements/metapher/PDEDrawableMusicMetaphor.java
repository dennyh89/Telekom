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
//  PDEDrawableMusicMetaphor
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows Music Metaphor
 */
@SuppressWarnings("unused")
public class PDEDrawableMusicMetaphor extends PDEDrawableBase {

//-----  properties ---------------------------------------------------------------------------------------------------
    private Drawable mPicture;
    private final static float CONST_ASPECTRATIO = 20.0f / 18.0f;
    private PDEColor mOutlineColor;
    private Paint mOutlinePaint;
    private PDEColor mOutlineFillingColor;
    private Paint mOutlineFillingPaint;
    private PDEColor mLeftBarColor;
    private Paint mLeftBarPaint;
    private PDEColor mLeftBarBorderColor;
    private Paint mLeftBarBorderPaint;
    private PDEColor mHandleColor;
    private Paint mHandlePaint;
    private PDEColor mHandleFillingColor;
    private Paint mHandleFillingPaint;
    private PDEColor mShapeColor;
    private Paint mShapePaint;
    private float mElementUnit;
    private boolean mElementSimple;
    private Path mShapePath;
    private float mOuterCornerRadius;
    private float mHandleCornerRadius;
    private RectF mLeftBarRect;
    private Rect mPictureRect;
    private RectF mOuterRect;
    private RectF mHandleRect;
    private PDEDrawableShapedShadow mElementShadowDrawable;



//----- init -----------------------------------------------------------------------------------------------------------

    /**
     * @brief Constructor
     */
    public PDEDrawableMusicMetaphor(Drawable drawable) {
       // init drawable basics
        super();
        // init PDE defaults
        mPicture = drawable;
        mElementSimple = false;
        mElementUnit = 0;
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
        //color of the black bar at the left
        mLeftBarColor = new PDEColor();
        mLeftBarColor.setColor(PDEColor.valueOf("#262626").getIntegerColor());
        //border color of the black bar at the left
        mLeftBarBorderColor = new PDEColor();
        mLeftBarBorderColor.setColor(PDEColor.valueOf("#000000").getIntegerColor());
        //border color of the handle
        mHandleColor = new PDEColor();
        mHandleColor.setColor(PDEColor.valueOf("#d0d0d0").getIntegerColor());
        //background color of the handle
        mHandleFillingColor = new PDEColor();
        mHandleFillingColor.setColor(PDEColor.valueOf("#f2f2f2").getIntegerColor());
        mHandleFillingColor = mOutlineFillingColor.newColorWithCombinedAlpha(128);
        //white shape over the case
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


    /*
     * @brief Get Picture
     */
    public Drawable getElementPicture() {
        return mPicture;
    }


    /**
     * @brief Creates Shape Path for the white Area over the Cover.
     *
     * @param bounds Size of the element
     * @return Path of the overlay
     */
    private Path elementCreateShapePath(Rect bounds) {
        Path shapepath = new Path();
        //move to upper left
        shapepath.moveTo(bounds.left, bounds.top);
        //move down
        shapepath.lineTo(bounds.left, (21.5f / 36.0f) * bounds.height());
        //move up right
        shapepath.lineTo(bounds.right, (7.5f / 36.0f) * bounds.height());
        //move up
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
     * @param bounds Available space for the element
     * @return Rect with correct aspect ratio, fitting in available space
     */
    private Rect elementCalculateAspectRatioBounds(Rect bounds) {
        Rect newBounds = null;

        //calculate bounds depending on aspect ratio
        if ((float)bounds.width() / (float)bounds.height() > CONST_ASPECTRATIO ) {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);
            newBounds.right = newBounds.left + Math.round(((float)newBounds.height() * CONST_ASPECTRATIO));
        } else {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);
            newBounds.bottom = newBounds.top + Math.round((float)newBounds.width() / CONST_ASPECTRATIO);
        }

        //set aspect ratio = 1 when size is under 5 BUs
        if (newBounds.width() >= PDEBuildingUnits.pixelFromBU(5)) {
            mElementSimple = false;
        } else {
            mElementSimple = true;
            newBounds.right = newBounds.height() + newBounds.left;
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
        RectF frame = new RectF(mPixelShift, mPixelShift, bounds.width() - mPixelShift,bounds.height() - mPixelShift);
        //calculate size unit
        mElementUnit = (1.0f / 40.0f) * frame.width();

        //calculates sizes if width > 5 BUs, else only the size of the picture is needed
        if (mElementSimple) {
            mPictureRect = new Rect(Math.round(frame.left), Math.round(frame.top),
                                    Math.round(frame.right), Math.round(frame.bottom));
        } else {
            //size of the outline
            mOuterRect = new RectF(frame.left, frame.top +1, frame.right, frame.bottom);
            mOuterCornerRadius = 0.75f * mElementUnit;
            //size of the left bar
            mLeftBarRect = new RectF(frame.left, frame.top+Math.round(0.75f * mElementUnit),
                                     frame.left+Math.round(4.75f * mElementUnit),
                                     frame.bottom-Math.round(0.75f * mElementUnit));
            //picture size
            mPictureRect = new Rect(Math.round(frame.left + 4.75f * mElementUnit),
                                    Math.round(frame.top + 0.75f * mElementUnit),
                                    Math.round(frame.left + 39.25f * mElementUnit),
                                    Math.round(frame.bottom - 0.75f * mElementUnit));
            //handle sizes
            mHandleCornerRadius = 0.3f * mElementUnit;
            mHandleRect = new RectF(frame.left + Math.round(38.5f * mElementUnit),
                                    frame.top + Math.round(14.0f * mElementUnit),
                                    frame.right + Math.round(2.0f * mElementUnit),
                                    frame.bottom - Math.round(14.0f * mElementUnit));
            //create shape
            mShapePath = elementCreateShapePath(new Rect(Math.round(frame.left), Math.round(frame.top),
                                                         Math.round(frame.right), Math.round(frame.bottom)));
        }
    }




//---------------------------------------------------------------------------------------------------------------------
// ----- Helpers ------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief update all used paints
     */
    @Override
    protected void updateAllPaints() {
        createHandleFillingPaint();
        createHandlePaint();
        createOutlineFillingPaint();
        createOutlinePaint();
        createLeftBarPaint();
        createLeftBarBorderPaint();
        createShapePaint();
    }


    /**
     * @brief create paint for the filling of the handle.
     */
    private void createHandleFillingPaint() {
        mHandleFillingPaint = new Paint();
        mHandleFillingPaint.setAntiAlias(true);
        mHandleFillingPaint.setColorFilter(mColorFilter);
        mHandleFillingPaint.setDither(mDither);
        mHandleFillingPaint.setColor(mOutlineFillingColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief create paint for the outline of the handle.
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
     * @brief create paint for the filling of the outline.
     */
    private void createOutlineFillingPaint() {
        mOutlineFillingPaint = new Paint();
        mOutlineFillingPaint.setAntiAlias(true);
        mOutlineFillingPaint.setColorFilter(mColorFilter);
        mOutlineFillingPaint.setDither(mDither);
        mOutlineFillingPaint.setColor(mOutlineFillingColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief create paint for the outline.
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
     * @brief create paint for the left bar.
     */
    private void createLeftBarPaint() {
        mLeftBarPaint = new Paint();
        mLeftBarPaint.setAntiAlias(true);
        mLeftBarPaint.setColorFilter(mColorFilter);
        mLeftBarPaint.setDither(mDither);
        mLeftBarPaint.setColor(mLeftBarColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief create paint for the outline of the left bar.
     */
    private void createLeftBarBorderPaint() {
        mLeftBarBorderPaint = new Paint();
        mLeftBarBorderPaint.setAntiAlias(true);
        mLeftBarBorderPaint.setStrokeWidth(2);
        mLeftBarBorderPaint.setStyle(Paint.Style.STROKE);
        mLeftBarBorderPaint.setColorFilter(mColorFilter);
        mLeftBarBorderPaint.setDither(mDither);
        mLeftBarBorderPaint.setColor(mLeftBarBorderColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief create paint for the shape.
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

        //draws complete case when width > 5 BUs, only picture else
        if (!mElementSimple) {
            //draw outline and background
            c.drawRoundRect(mOuterRect, mOuterCornerRadius, mOuterCornerRadius, mOutlineFillingPaint);
            c.drawRoundRect(mOuterRect, mOuterCornerRadius, mOuterCornerRadius, mOutlinePaint);
            //draw picture
            mPicture.setBounds(mPictureRect);
            mPicture.draw(c);
            //draw left black bar and its outline
            c.drawRect(mLeftBarRect, mLeftBarPaint);
            c.drawRect(mLeftBarRect.left+1, mLeftBarRect.top+1, mLeftBarRect.right-1, mLeftBarRect.bottom-1, mLeftBarBorderPaint);
            //draw handle
            c.drawRoundRect(mHandleRect, mHandleCornerRadius, mHandleCornerRadius, mHandleFillingPaint);
            c.drawRoundRect(mHandleRect, mHandleCornerRadius, mHandleCornerRadius, mHandlePaint);
            //draw shape over the case
            c.clipPath(mShapePath);
            c.drawRoundRect(mOuterRect, mOuterCornerRadius, mOuterCornerRadius, mShapePaint);
        } else {
            mPicture.setBounds(Math.round(frame.left), Math.round(frame.top),
                    Math.round(frame.right), Math.round(frame.bottom));
            mPicture.draw(c);
        }
    }
}
