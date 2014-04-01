/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */
package de.telekom.pde.codelibrary.ui.elements.metaphor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableBase;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;


//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawablePhotoFrameImage
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows picture With Frame.
 */
public class PDEDrawablePhotoFrameImage extends PDEDrawableBase {

    //-----  properties ---------------------------------------------------------------------------------------------------
    private PDEConstants.PDEContentStyle mStyle;

    private Drawable mPicture;
    private PDEDrawableShapedShadow mElementShadowDrawable;

    private Rect mOutlineRect;
    private Paint mOutlineFlatPaint;
    private PDEColor mOutlineFlatColor;
    private Rect mPictureRect;
    private boolean mDarkStyle;

    private PDEColor mPictureBackgroundColor;
    private Paint mPictureBackgroundPaint;
    private Paint mOutlineHapticPaint;
    private PDEColor mOutlineHapticColor;
    private Rect mBorderRect;
    private Paint mBorderPaint;
    private PDEColor mBorderColor;




//----- init -----------------------------------------------------------------------------------------------------------
    /**
     * @brief Constructor
     *
     * @param drawable Image to be shown
     */
    public PDEDrawablePhotoFrameImage(Drawable drawable) {
        // init drawable basics
        super();
        mStyle = PDEConstants.PDEContentStyle.PDEContentStyleFlat;

        // init PDE defaults
        mPicture = drawable;


        // shadow is created on demand
        mElementShadowDrawable = null;

        //set outline color
        mOutlineFlatColor = new PDEColor();
        mOutlineFlatColor.setColor(PDEColor.valueOf("Black45Alpha").getIntegerColor());

        //set border color
        mBorderColor = PDEColor.valueOf("DTWhite");
        //set outline color
        mOutlineHapticColor = PDEColor.valueOf("Black30Alpha");
        //picture background color
        mPictureBackgroundColor = new PDEColor();
        mPictureBackgroundColor.setColor(PDEColor.valueOf("DTWhite").getIntegerColor());

        // update paints
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
        setNeededPadding(0);
        mElementShadowDrawable = null;
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
    public Drawable getElementPicture()
    {
        return mPicture;
    }


    /**
     * @brief Set Border Color
     */
    public void setElementBorderColor(PDEColor color) {
        //any change?
        if (color == mBorderColor) return;

        //remember
        mBorderColor = color;

        //redraw
        createBorderPaint();
        update();
    }


    /**
     * @brief Get Border Color
     */
    public PDEColor getElementBorderColor() {
        return mBorderColor;
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
     * @brief Internal helper function to calculate border size
     *
     * @param  size Size of the element.
     * @return Width of the border around the picture
     */
    private int elementCalcBorderSize(Rect size) {
        if (size == null) return 0;
        int y = Math.max(size.width(), size.height());
        int res = Math.round((5.0f / 21.0f + (float) y / 105.0f));

        //minimum border width 0.2 BU
        if (res < 0.2* PDEBuildingUnits.BU()) {
            res = (int)Math.round(0.2 * PDEBuildingUnits.BU());
        }
        //or border width 0.2 BU when bounds < 5 BU
        //if (y < 5*PDEBuildingUnits.BU()) res = (int)Math.round(0.2 * PDEBuildingUnits.BU());
        return res;
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
        //calculate sizes
        mOutlineRect = new Rect(Math.round(mPixelShift), Math.round(mPixelShift),
                Math.round(bounds.width() - mPixelShift), Math.round(bounds.height() - mPixelShift));
        mPictureRect = new Rect(mOutlineRect.left + 2, mOutlineRect.top + 2,
                mOutlineRect.right - 2, mOutlineRect.bottom - 2);

        if (mPicture != null) {
            mPicture.setBounds(mPictureRect);
        }
    }


    /**
     * @brief Perform the layout calculations that are needed before the next drawing phase (because bounds have
     * changed).
     */
    private void performLayoutCalculationsHaptic(Rect bounds){
        int borderWidth;

        //calculate sizes
        Rect frame = new Rect(Math.round(mPixelShift),
                Math.round(mPixelShift),
                Math.round(bounds.width() - mPixelShift),
                Math.round(bounds.height() - mPixelShift));

        mOutlineRect = new Rect(Math.round(frame.left), Math.round(frame.top),
                Math.round(frame.right),Math.round(frame.bottom));
        mBorderRect = new Rect(mOutlineRect.left + 1, mOutlineRect.top + 1, mOutlineRect.right - 1,
                mOutlineRect.bottom - 1);
        borderWidth = elementCalcBorderSize(mBorderRect);
        mPictureRect = new Rect(Math.round(mBorderRect.left) + borderWidth,
                Math.round(mBorderRect.top) + borderWidth,
                Math.round(mBorderRect.right) - borderWidth,
                Math.round(mBorderRect.bottom) - borderWidth);

        if (mPicture != null) {
            mPicture.setBounds(mPictureRect);
        }
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- Drawing Bitmap ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Calls function to update our drawing bitmap and trigger a redraw of this element based on flat
     * or haptic style.
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

        //draw outline
        c.drawRect(mOutlineRect, mOutlineFlatPaint);
        c.drawRect(mPictureRect, mPictureBackgroundPaint);

        //draw picture
        // set the picture rect
        if (mPicture != null) {
            if (mPicture.getBounds().width() == 0 && mPicture.getBounds().height() == 0) {
                performLayoutCalculations(getBounds());
                mPicture.setBounds(mPictureRect);
            }
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

        //draw outline
        c.drawRect(mOutlineRect, mOutlineHapticPaint);
        //draw border
        c.drawRect(mBorderRect, mBorderPaint);
        c.drawRect(mPictureRect, mPictureBackgroundPaint);
        //draw picture
        // set the picture rect
        if (mPicture != null) {
            if (mPicture.getBounds().width() == 0 && mPicture.getBounds().height() == 0) {
                performLayoutCalculations(getBounds());
                mPicture.setBounds(mPictureRect);
            }
            mPicture.draw(c);
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
        createBorderPaint();
        createOutlineHapticPaint();
        createPictureBackgroundPaint();
    }


    /**
     * @brief create paint for the outline.
     */
    private void createOutlineFlatPaint() {
        mOutlineFlatPaint = new Paint();
        mOutlineFlatPaint.setAntiAlias(true);
        mOutlineFlatPaint.setColorFilter(mColorFilter);
        mOutlineFlatPaint.setDither(mDither);
        mOutlineFlatPaint.setColor(mOutlineFlatColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief create paint for the border.
     */
    private void createBorderPaint() {
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColorFilter(mColorFilter);
        mBorderPaint.setDither(mDither);
        mBorderPaint.setColor(mBorderColor.newIntegerColorWithCombinedAlpha(mAlpha));
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
     * @brief create paint for the outline.
     */
    private void createOutlineHapticPaint() {
        mOutlineHapticPaint = new Paint();
        mOutlineHapticPaint.setAntiAlias(true);
        mOutlineHapticPaint.setColorFilter(mColorFilter);
        mOutlineHapticPaint.setDither(mDither);
        mOutlineHapticPaint.setColor(mOutlineHapticColor.newIntegerColorWithCombinedAlpha(mAlpha));
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
}

