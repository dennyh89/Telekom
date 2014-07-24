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
//  PDEDrawableFilmMetaphorImage
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows Film Metaphor
 */
public class PDEDrawableFilmMetaphorImage extends PDEDrawableBase {

    //-----  properties ---------------------------------------------------------------------------------------------------
    private PDEConstants.PDEContentStyle mStyle;
    private final static float CONST_ASPECT_RATIO = 20.0f / 29.0f;

    private Drawable mPicture;
    public boolean mMiddleAligned;

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
    private Rect mFrame;
    private RectF mHandleRect;
    private LinearGradient mGradient;
    private int mColors[];
    // outer shadow
    private PDEDrawableShapedShadow mElementShadowDrawable;

//----- init -----------------------------------------------------------------------------------------------------------
    /**
     * @brief Constructor
     */
    public PDEDrawableFilmMetaphorImage(Drawable drawable)
    {
        // init drawable basics
        super();

        // init PDE defaults
        mStyle = PDEConstants.PDEContentStyle.PDEContentStyleFlat;

        mMiddleAligned = false;
        mPicture = drawable;

        // shadow is created on demand
        mElementShadowDrawable = null;

        //background color of border
        mOutlineFlatColor = new PDEColor();
        mOutlineFlatColor.setColor(PDEColor.valueOf("Black45Alpha").getIntegerColor());
        //picture background color
        mPictureBackgroundColor = new PDEColor();
        mPictureBackgroundColor.setColor(PDEColor.valueOf("DTWhite").getIntegerColor());
        //color of outline
        mOutlineColor = new PDEColor();
        mOutlineColor.setColor(PDEColor.valueOf("Black30Alpha").getIntegerColor());
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
        mHandleColor.setColor(PDEColor.valueOf("Black30Alpha").getIntegerColor());
        //background color of the handle
        mHandleFillingColor = new PDEColor();
        mHandleFillingColor.setColor(PDEColor.valueOf("#f2f2f2").getIntegerColor());
        mHandleFillingColor = mOutlineFillingColor.newColorWithCombinedAlpha(128);
        //color of the shape over the case
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
     * @brief Set DarkStyle color
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


    /**
     * @brief Get Picture
     */
    @SuppressWarnings("unused")
    public Drawable getElementPicture() {

        return mPicture;
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
     * @brief Perform the layout calculations for flat style that are needed before the next drawing phase
     * (because bounds have changed).
     */
    private void performLayoutCalculationsFlat(Rect bounds){
        Rect frame = elementCalculateAspectRatioBounds(new Rect(Math.round(mPixelShift),
                Math.round(mPixelShift),
                Math.round(bounds.width() - mPixelShift),
                Math.round(bounds.height() - mPixelShift)));
        //outline size
        mOuterRect = new RectF(frame.left, frame.top, frame.right, frame.bottom);
        //picture size
        mPictureRect = new Rect(Math.round(frame.left + 2),
                Math.round(frame.top + 2),
                Math.round(frame.right - 2),
                Math.round(frame.bottom - 2));
    }


    /**
     * @brief Perform the layout calculations for haptic style that are needed before the next drawing phase
     * (because bounds have changed).
     */
    private void performLayoutCalculationsHaptic(Rect bounds){
        mFrame = elementCalculateAspectRatioBounds(new Rect(Math.round(mPixelShift),
                Math.round(mPixelShift),
                Math.round(bounds.width() - mPixelShift),
                Math.round(bounds.height() - mPixelShift)));

        //outline size
        mOuterRect = new RectF(Math.round(mFrame.left - (0.5f / 20.0f) * mFrame.width()),
                mFrame.top,
                mFrame.right,
                mFrame.bottom);
        mOuterCornerRadius = (0.5f / 20.0f) * mFrame.width();
        //picture size
        int borderWidth = Math.round((0.5f / 29.0f) * mFrame.height());
        // no inspection because the border width is just the space on the sides
        // noinspection SuspiciousNameCombination
        mPictureRect = new Rect(mFrame.left, borderWidth, mFrame.right - borderWidth, mFrame.bottom - borderWidth);
        // handle size
        mHandleCornerRadius = (0.3f / 20.0f) * mFrame.width();
        mHandleRect = new RectF(Math.round((19.0f / 20.0f) * mFrame.width()),
                Math.round((12.5f / 29.0f) * mFrame.height()),
                mFrame.right+Math.round((0.5f / 20.0f) * mFrame.width()),
                mFrame.bottom-Math.round((12.5f / 29.0f) * mFrame.height()));
        // shape size
        mShapePath = elementCreateShapePath(mFrame);
        // blur radius on the left
        updateBlurColors();
        mGradient = new LinearGradient( mFrame.left,
                (mFrame.top - mFrame.bottom) / 2, (0.75f / 20.0f) * mFrame.width() ,
                (mFrame.top - mFrame.bottom) / 2, mColors, null,
                Shader.TileMode.MIRROR);
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
        createBlurPaint();
        createHandleFillingPaint();
        createHandlePaint();
        createLeftLinePaint();
        createOutlineFillingPaint();
        createOutlinePaint();
        createShapePaint();
        createPictureBackgroundPaint();
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


    /**
     * @brief create paint for outline filling.
     */
    private void createOutlineFlatPaint() {
        mOutlineFlatPaint = new Paint();
        mOutlineFlatPaint.setAntiAlias(true);
        mOutlineFlatPaint.setColorFilter(mColorFilter);
        mOutlineFlatPaint.setDither(mDither);
        mOutlineFlatPaint.setColor(mOutlineFlatColor.newIntegerColorWithCombinedAlpha(mAlpha));
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
        mHandleFillingPaint.setColor(mHandleFillingColor.newIntegerColorWithCombinedAlpha(mAlpha));
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
        // security
        if (!paintPropertiesChanged) return;
        if (mPicture == null) return;

        mPicture.setAlpha(mAlpha);
        mPicture.setDither(mDither);
        mPicture.setColorFilter(mColorFilter);
    }


    /**
     * @brief Create Shape Path for the white Area over the Cover
     *
     * @param bounds Size of the element
     * @return Path of the overlay
     */
    private Path elementCreateShapePath(Rect bounds) {
        Path shapePath = new Path();
        //start at top left
        shapePath.moveTo(bounds.left, bounds.top);
        //move down
        shapePath.lineTo(bounds.left, (16.5f / 29.0f) * bounds.height());
        //move to upper right
        shapePath.lineTo(bounds.right, (5.5f / 29.0f) * bounds.height());
        //move to top right
        shapePath.lineTo(bounds.right, bounds.top);
        //close path
        shapePath.close();

        return shapePath;
    }


    /**
     * @brief Calculate the correct aspect ratio bounds.
     *
     * @param bounds Available space
     * @return  Rect with correct aspect ratio, fitting in available space
     */
    public Rect elementCalculateAspectRatioBounds(Rect bounds) {
        Rect newBounds;

        //calculate size, based on aspect ratio
        if ((float)bounds.width() / (float)bounds.height() > CONST_ASPECT_RATIO) {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);
            newBounds.right = newBounds.left + Math.round(((float)newBounds.height() * CONST_ASPECT_RATIO));

            if (mMiddleAligned) {
                int horizontalShift = (bounds.width()-newBounds.width())/2;
                newBounds.left += horizontalShift;
                newBounds.right += horizontalShift;
            }

        } else {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);
            newBounds.bottom = newBounds.top + Math.round((float)newBounds.width() / CONST_ASPECT_RATIO);

            if (mMiddleAligned) {
                int verticalShift = (bounds.height()-newBounds.height())/2;
                newBounds.top += verticalShift;
                newBounds.bottom += verticalShift;
            }
        }

        return newBounds;
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

        //draw outline
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
        //draws case if width > 5 BUs, only picture else
        if (bounds.width() >= PDEBuildingUnits.pixelFromBU(5)) {
            //draw outline
            c.drawRoundRect(mOuterRect, mOuterCornerRadius, mOuterCornerRadius, mOutlineFillingPaint);
            c.drawRoundRect(mOuterRect, mOuterCornerRadius, mOuterCornerRadius, mOutlinePaint);
            c.drawRect(mPictureRect, mPictureBackgroundPaint);
            //draw picture
            if (mPicture != null) {
                mPicture.setBounds(mPictureRect);
                mPicture.draw(c);
            }
            //draw line on the left
            mBlurPaint.setShader(mGradient);
            c.drawRect(mFrame.left, mFrame.top, (0.75f/20.0f) * mFrame.width(), mFrame.bottom, mBlurPaint);
            c.drawLine(mFrame.left, mFrame.top, mFrame.left, mFrame.bottom, mLeftLinePaint);
            //draw handle
            c.drawRoundRect(mHandleRect, mHandleCornerRadius, mHandleCornerRadius, mHandleFillingPaint);
            c.drawRoundRect(mHandleRect, mHandleCornerRadius, mHandleCornerRadius, mHandlePaint);
            //draw shape over the case
            c.clipPath(mShapePath);
            c.drawRoundRect(mOuterRect, mOuterCornerRadius, mOuterCornerRadius, mShapePaint);
        } else  {
            if (mPicture != null) {
                c.drawRect(mFrame, mPictureBackgroundPaint);
                mPicture.setBounds(mFrame);
                mPicture.draw(c);
            }

        }
    }
}
