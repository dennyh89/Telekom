/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
        * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
        * https://www.design.telekom.com/myaccount/terms-of-use/
        *
        * Copyright (c) 2012. Neuland Multimedia GmbH.
        */
package de.telekom.pde.codelibrary.ui.elements.metaphor;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableBase;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableMusicMetaphorImage
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows Music Metaphor
 */
public class PDEDrawableMusicMetaphorImage extends PDEDrawableBase {

    @SuppressWarnings("unused")
    private final static String LOG_TAG = PDEDrawableMusicMetaphorImage.class.getName();

    //-----  properties ------------------------------------------------------------------------------------------------
    private PDEConstants.PDEContentStyle mStyle;


    private Drawable mPicture;

    private PDEColor mOutlineFlatColor;
    private Paint mOutlineFlatPaint;
    private Rect mPictureRect;
    private RectF mOuterRect;
    private boolean mDarkStyle;

    private PDEColor mPictureBackgroundColor;
    private Paint mPictureBackgroundPaint;
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
    private Rect mFrame;
    private Rect mLeftBarRect;
    private RectF mHandleRect;
    private PDEDrawableShapedShadow mElementShadowDrawable;


//----- init -----------------------------------------------------------------------------------------------------------

    /**
     * @brief Constructor
     */
    public PDEDrawableMusicMetaphorImage(Drawable drawable) {
        // init drawable basics
        super();
        // init PDE defaults
        mStyle = PDEConstants.PDEContentStyle.PDEContentStyleFlat;
        mPicture = drawable;


        //define colors
        mOutlineFlatColor = new PDEColor();
        mOutlineFlatColor.setColor(PDEColor.valueOf("Black45Alpha").getIntegerColor());

        // init PDE defaults
        mElementSimple = false;
        mElementUnit = 0;
        // shadow is created on demand
        mElementShadowDrawable = null;

        //define colors
        //color of outline
        mOutlineColor = new PDEColor();
        mOutlineColor.setColor(PDEColor.valueOf("Black30Alpha").getIntegerColor());
        //picture background color
        mPictureBackgroundColor = new PDEColor();
        mPictureBackgroundColor.setColor(PDEColor.valueOf("DTWhite").getIntegerColor());
        //background color of border
        mOutlineFillingColor = new PDEColor();
        mOutlineFillingColor.setColor(PDEColor.valueOf("#f2f2f2").getIntegerColor());
        mOutlineFillingColor = mOutlineFillingColor.newColorWithCombinedAlpha(77);
        //color of the black bar at the left
        mLeftBarColor = new PDEColor();
        mLeftBarColor.setColor(PDEColor.valueOf("#262626").getIntegerColor());
        //border color of the black bar at the left
        mLeftBarBorderColor = new PDEColor();
        mLeftBarBorderColor.setColor(PDEColor.valueOf("DTBlack").getIntegerColor());
        //border color of the handle
        mHandleColor = new PDEColor();
        mHandleColor.setColor(PDEColor.valueOf("Black30Alpha").getIntegerColor());
        //background color of the handle
        mHandleFillingColor = new PDEColor();
        mHandleFillingColor.setColor(PDEColor.valueOf("#f2f2f2").getIntegerColor());
        mHandleFillingColor = mOutlineFillingColor.newColorWithCombinedAlpha(128);
        //white shape over the case
        mShapeColor = new PDEColor();
        mShapeColor.setColor(PDEColor.valueOf("DTWhite").getIntegerColor());
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
    public PDEDrawableShapedShadow createElementShadow() {
        // already created?
        if (mElementShadowDrawable != null) return mElementShadowDrawable;

        // init shadow drawable
        mElementShadowDrawable = new PDEDrawableShapedShadow();
        mElementShadowDrawable.setElementShapeOpacity(0.25f);
        setNeededPadding(PDEBuildingUnits.oneHalfBU());

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


//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Set visual style
     */
    public void setElementContentStyle(PDEConstants.PDEContentStyle style) {
        //any change?
        if (style == mStyle) return;
        //remember
        mStyle = style;

        //redraw
        update();
    }


    /**
     * @brief Get visual style
     */
    @SuppressWarnings("unused")
    public PDEConstants.PDEContentStyle getElementContentStyle() {
        return mStyle;
    }


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
    @SuppressWarnings("unused")
    public Drawable getElementPicture() {
        return mPicture;
    }


    /**
     * @brief Set darkstyle color
     */
    public void setElementDarkStyle(boolean isDarkStyle) {
        if (isDarkStyle == mDarkStyle) return;

        mDarkStyle = isDarkStyle;

        if (mDarkStyle) {
            mOutlineFlatColor.setColor(PDEColor.valueOf("DTBlack").getIntegerColor());

        } else {
            mOutlineFlatColor.setColor(PDEColor.valueOf("Black45Alpha").getIntegerColor());
        }
        // update all paints
        update(true);
    }


    /**
     * @brief Get if element is colored in dark style
     */
    @SuppressWarnings("unused")
    public boolean getElementDarkStyle() {
        return mDarkStyle;
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- layout / sizing ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Update all of my sublayers.
     */
    @Override
    protected void doLayout() {
        // do needed layout calculations.
        performLayoutCalculations(new Rect(getBounds()));
    }


    /**
     * @brief Calls function to perform layout calculations, based on flat or haptic style
     */
    private void performLayoutCalculations(Rect bounds) {
        if (mStyle == PDEConstants.PDEContentStyle.PDEContentStyleFlat) {
            performLayoutCalculationsFlat(bounds);
        } else {
            performLayoutCalculationsHaptic(bounds);
        }
    }


    /**
     * @brief Perform the layout calculations that are needed before the next drawing phase (because bounds have
     * changed).
     */
    private void performLayoutCalculationsFlat(Rect bounds){
        Rect frame = new Rect(Math.round(mPixelShift),
                Math.round(mPixelShift),
                Math.round(bounds.width() - mPixelShift),
                Math.round(bounds.height() - mPixelShift));

        //size of the outline
        mOuterRect = new RectF(frame.left, frame.top, frame.right, frame.bottom);

        //picture size
        mPictureRect = new Rect(Math.round(frame.left + 2),
                Math.round(frame.top + 2),
                Math.round(frame.right - 2),
                Math.round(frame.bottom - 2));
    }


    /**
     * @brief Perform the layout calculations that are needed before the next drawing phase (because bounds have
     * changed).
     */
    private void performLayoutCalculationsHaptic(Rect bounds){
        mFrame = new Rect(Math.round(mPixelShift), Math.round(mPixelShift), Math.round(bounds.width() - mPixelShift),
                Math.round(bounds.height() - mPixelShift));

        //calculate size unit
        mElementUnit = (1.0f / 40.0f) * mFrame.width();

        //calculates sizes if width > 5 BUs, else only the size of the picture is needed
        if (mElementSimple) {
            mPictureRect = new Rect(Math.round(mFrame.left), Math.round(mFrame.top),
                    Math.round(mFrame.right), Math.round(mFrame.bottom));
        } else {
            //size of the outline
            mOuterRect = new RectF(mFrame.left, mFrame.top, mFrame.right, mFrame.bottom);
            mOuterCornerRadius = 0.75f * mElementUnit;
            //size of the left bar
            mLeftBarRect = new Rect(mFrame.left, mFrame.top+ Math.round(0.75f * mElementUnit),
                    mFrame.left+Math.round(4.75f * mElementUnit),
                    mFrame.bottom-Math.round(0.75f * mElementUnit));
            //picture size
            mPictureRect = new Rect(Math.round(mFrame.left + 4.75f * mElementUnit),
                    Math.round(mFrame.top + 0.75f * mElementUnit),
                    Math.round(mFrame.left + 39.25f * mElementUnit),
                    Math.round(mFrame.bottom - 0.75f * mElementUnit));
            //handle sizes
            mHandleCornerRadius = 0.3f * mElementUnit;
            mHandleRect = new RectF(mFrame.left + Math.round(38.5f * mElementUnit),
                    mFrame.top + Math.round(14.0f * mElementUnit),
                    mFrame.right + Math.round(2.0f * mElementUnit),
                    mFrame.bottom - Math.round(14.0f * mElementUnit));
            //create shape
            mShapePath = elementCreateShapePath(new Rect(Math.round(mFrame.left), Math.round(mFrame.top),
                    Math.round(mFrame.right), Math.round(mFrame.bottom)));
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
        createOutlineFlatPaint();
        createHandleFillingPaint();
        createHandlePaint();
        createOutlineFillingPaint();
        createOutlinePaint();
        createLeftBarPaint();
        createLeftBarBorderPaint();
        createShapePaint();
        createPictureBackgroundPaint();
    }


    /**
     * @brief create paint for the filling of the outline.
     */
    private void createOutlineFlatPaint() {
        mOutlineFlatPaint = new Paint();
        mOutlineFlatPaint.setAntiAlias(true);
        mOutlineFlatPaint.setColorFilter(mColorFilter);
        mOutlineFlatPaint.setDither(mDither);
        mOutlineFlatPaint.setColor(mOutlineFlatColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief create paint for the filling of the handle.
     */
    private void createHandleFillingPaint() {
        mHandleFillingPaint = new Paint();
        mHandleFillingPaint.setAntiAlias(true);
        mHandleFillingPaint.setColorFilter(mColorFilter);
        mHandleFillingPaint.setDither(mDither);
        mHandleFillingPaint.setColor(mHandleFillingColor.newIntegerColorWithCombinedAlpha(mAlpha));
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
     * @brief create paint for picture background.
     */
    private void createPictureBackgroundPaint() {
        mPictureBackgroundPaint = new Paint();
        mPictureBackgroundPaint.setAntiAlias(true);
        mPictureBackgroundPaint.setColorFilter(mColorFilter);
        mPictureBackgroundPaint.setDither(mDither);
        mPictureBackgroundPaint.setColor(mPictureBackgroundColor.newIntegerColorWithCombinedAlpha(mAlpha));
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
        if (mPicture == null) return;
        mPicture.setAlpha(mAlpha);
        mPicture.setDither(mDither);
        mPicture.setColorFilter(mColorFilter);
    }


    /**
     * @brief Creates Shape Path for the white Area over the Cover.
     *
     * @param bounds Size of the element
     * @return Path of the overlay
     */
    private Path elementCreateShapePath(Rect bounds) {
        Path shapePath = new Path();
        //move to upper left
        shapePath.moveTo(bounds.left, bounds.top);
        //move down
        shapePath.lineTo(bounds.left, (21.5f / 36.0f) * bounds.height());
        //move up right
        shapePath.lineTo(bounds.right, (7.5f / 36.0f) * bounds.height());
        //move up
        shapePath.lineTo(bounds.right, bounds.top);
        //close path
        shapePath.close();

        return shapePath;
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- Drawing Bitmap ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Calls function to update our drawing bitmap and trigger a redraw of this element based on flat or haptic style.
     */
    @Override
    protected void updateDrawingBitmap(Canvas c, Rect bounds) {
        if (mStyle == PDEConstants.PDEContentStyle.PDEContentStyleFlat) {
            updateDrawingBitmapFlat(c, bounds);
        } else {
            updateDrawingBitmapHaptic(c, bounds);
        }
    }


    /**
     * @brief Updates our drawing bitmap and triggers a redraw of this element.
     *
     * If a drawing parameter changes, we need to call this function in order to update our drawing-bitmap and
     * in order to trigger the draw of our updated bitmap to the canvas.
     */
    private void updateDrawingBitmapFlat(Canvas c, Rect bounds) {
        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) return;

        //draw outline and background
        c.drawRect(mOuterRect, mOutlineFlatPaint);
        c.drawRect(mPictureRect, mPictureBackgroundPaint);

        //draw picture
        if (mPicture != null) {
            mPicture.setBounds(mPictureRect);
            mPicture.draw(c);
        }
    }


    /**
     * @brief Updates our drawing bitmap and triggers a redraw of this element.
     *
     * If a drawing parameter changes, we need to call this function in order to update our drawing-bitmap and
     * in order to trigger the draw of our updated bitmap to the canvas.
     */
    private void updateDrawingBitmapHaptic(Canvas c, Rect bounds) {
        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) return;

        c.clipRect(mFrame);

        //draws complete case when width > 5 BUs, only picture else
        if (!mElementSimple) {
            //draw outline and background
            c.drawRoundRect(mOuterRect, mOuterCornerRadius, mOuterCornerRadius, mOutlineFillingPaint);
            c.drawRoundRect(mOuterRect, mOuterCornerRadius, mOuterCornerRadius, mOutlinePaint);
            c.drawRect(mPictureRect, mPictureBackgroundPaint);
            //draw picture
            if (mPicture != null) {
                mPicture.setBounds(mPictureRect);
                mPicture.draw(c);
            }
            //draw left black bar and its outline
            c.drawRect(mLeftBarRect, mLeftBarPaint);
            c.drawRect(mLeftBarRect.left + 1,
                    mLeftBarRect.top + 1,
                    mLeftBarRect.right - 1,
                    mLeftBarRect.bottom - 1,
                    mLeftBarBorderPaint);
            //draw handle
            c.drawRoundRect(mHandleRect, mHandleCornerRadius, mHandleCornerRadius, mHandleFillingPaint);
            c.drawRoundRect(mHandleRect, mHandleCornerRadius, mHandleCornerRadius, mHandlePaint);
            //draw shape over the case
            c.clipPath(mShapePath);
            c.drawRoundRect(mOuterRect, mOuterCornerRadius, mOuterCornerRadius, mShapePaint);
        } else {
            if (mPicture != null) {
                c.drawRect(mFrame, mPictureBackgroundPaint);
                mPicture.setBounds(mFrame);
                mPicture.draw(c);
            }

        }
    }
}
