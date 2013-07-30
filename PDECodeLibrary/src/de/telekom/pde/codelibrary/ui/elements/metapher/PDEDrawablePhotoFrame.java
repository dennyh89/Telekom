/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */
package de.telekom.pde.codelibrary.ui.elements.metapher;

import android.graphics.*;
import android.graphics.drawable.Drawable;

import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.drawables.PDEDrawableBase;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;


//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawablePhotoFrame
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows picture With Frame.
 */
@SuppressWarnings("unused")
public class PDEDrawablePhotoFrame extends PDEDrawableBase {

//-----  properties ---------------------------------------------------------------------------------------------------
    private Drawable mPicture;
    private final static float CONST_ASPECTRATIO = 1.5f;
    private Rect mOutlineRect;
    private Paint mOutlinePaint;
    private PDEColor mOutlineColor;
    private Rect mBorderRect;
    private Paint mBorderPaint;
    private PDEColor mBorderColor;
    private Rect mPictureRect;
    private Rect mOriginalBounds;
    private int mBorderWidth;

    private int mPaddingTop;
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingBottom;

    // drawing helpers
    private PDEDrawableShapedShadow mElementShadowDrawable;
    private boolean mMiddleAligned;


//----- init -----------------------------------------------------------------------------------------------------------
    /**
     * @brief Constructor
     *
     * @param drawable Image to be shown
     */
    public PDEDrawablePhotoFrame(Drawable drawable) {
        // init drawable basics
        super();
        mMiddleAligned = false;
        // init PDE defaults
        mPicture = drawable;
        // shadow is created on demand
        mElementShadowDrawable = null;
        //set border color
        mBorderColor = PDEColor.valueOf("#ffffff");
        //set outline color
        mOutlineColor = PDEColor.valueOf("#DCDCDC");

        mPaddingLeft = 0;
        mPaddingTop = 0;
        mPaddingRight = 0;
        mPaddingBottom = 0;

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
        setNeededPadding(0);
        mElementShadowDrawable = null;
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
    public void setElementPicture(Drawable picture)
    {
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
     * @brief Set middle aligned
     *
     * Middle aligned is for use in PDEPhotoFrameView, when true the picture is aligned in the middle of the border,
     * when false in the upper left
     */
    public void setElementMiddleAligned(boolean aligned) {
        if (mMiddleAligned == aligned) return;
        mMiddleAligned = aligned;
        update();
    }

    /**
     * @brief Set all paddings
     */
    public void setElementPaddingAll(int padding) {
        //any change?
        if (padding == mPaddingLeft && padding == mPaddingTop && padding == mPaddingRight && padding == mPaddingBottom)
            return;
        //remember
        mPaddingLeft = padding;
        mPaddingTop = padding;
        mPaddingRight = padding;
        mPaddingBottom = padding;
        //redraw
        update();
    }

    /**
     * @brief Set all paddings in one function
     */
    public void setElementPaddingAll(int left, int top, int right, int bottom) {
        setElementPaddingLeft(left);
        setElementPaddingTop(top);
        setElementPaddingRight(right);
        setElementPaddingBottom(bottom);
        //redraw
        update();
    }


    /**
     * @brief Set left padding
     */
    public void setElementPaddingLeft(int padding) {
        //any change?
        if (padding == mPaddingLeft) return;
        //remember
        mPaddingLeft = padding;
        //redraw
        update();
    }

    /**
     * @brief Set top padding
     */
    public void setElementPaddingTop(int padding) {
        //any change?
        if (padding == mPaddingTop) return;
        //remember
        mPaddingTop = padding;
        //redraw
        update();
    }

    /**
     * @brief Set right padding
     */
    public void setElementPaddingRight(int padding) {
        //any change?
        if (padding == mPaddingRight) return;
        //remember
        mPaddingRight = padding;
        //redraw
        update();
    }

    /**
     * @brief Set bottom padding
     */
    public void setElementPaddingBottom(int padding) {
        //any change?
        if (padding == mPaddingBottom) return;
        //remember
        mPaddingBottom = padding;
        //redraw
        update();
    }

    /**
     * @brief Get left padding
     */
    public int getElementPaddingLeft() {
        return mPaddingLeft;
    }

    /**
     * @brief Get top padding
     */
    public int getElementPaddingTop() {
        return mPaddingTop;
    }

    /**
     * @brief Get right padding
     */
    public int getElementPaddingRight() {
        return mPaddingRight;
    }

    /**
     * @brief Get left padding
     */
    public int getElementPaddingBottom() {
        return mPaddingBottom;
    }

    /**
     * @brief Get padding rect
     */
    public Rect getElementPaddingRect() {
        return new Rect(mPaddingLeft,mPaddingTop,mPaddingRight,mPaddingBottom);
    }

    /**
     * @brief Internal helper function to calculate border size
     *
     * @param  size Size of the element.
     * @return Width of the border around the picture
     */
    private int elementCalcBorderSize(Rect size) {
        int y = Math.max(size.width(), size.height());
        int res = Math.round((5.0f / 21.0f + (float) y / 105.0f));
        //minimum border width 0.2 BU
        if (res < 0.2* PDEBuildingUnits.BU()) res = (int)Math.round(0.2 * PDEBuildingUnits.BU());
        //or border width 0.2 BU when bounds < 5 BU
        //if (y < 5*PDEBuildingUnits.BU()) res = (int)Math.round(0.2 * PDEBuildingUnits.BU());
        return res;
    }


    /**
     * @brief Helper function to get aspect ratio
     *
     * @return Current valid aspect ratio
     */
    private float getElementAspectRatio() {
        if (mPicture == null) return CONST_ASPECTRATIO;

        if (mPicture.getIntrinsicWidth() >= mPicture.getIntrinsicHeight()){
            return CONST_ASPECTRATIO;
        } else {
            return 1 / CONST_ASPECTRATIO;
        }
    }

    /**
     * @brief Helper function to get intrinsic size of the picture
     */
    public Point getNativeSize() {
        if (mPicture != null) return new Point(mPicture.getIntrinsicWidth(), mPicture.getIntrinsicHeight());
        return new Point(0, 0);
    }


    /**
     * @brief Helper function to test if photo frame has a picture in it
     */
    public boolean hasPicture() {
        return mPicture != null;
    }

    /**
     * @brief Get element height.
     */
    public int getElementHeight() {
        if (mOriginalBounds != null) return mOriginalBounds.height();
        return 0;
    }

    /**
     * @brief Get element width.
     */
    public int getElementWidth() {
        if (mOriginalBounds != null) return mOriginalBounds.width();
        return 0;
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
        setLayoutSize(new Point(width, Math.round((float) width / getElementAspectRatio())));
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
        setLayoutSize(new Point(Math.round(height * getElementAspectRatio()), height));
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
     * @param  bounds Available Space for the element
     * @return Rect with the correct aspect ratio, fitting in available space
     *
     * When mMiddleAligned is set to true, resulting bounds are shifted to the middle of the available space
     */
    private Rect elementCalculateAspectRatioBounds(Rect bounds) {
        mOriginalBounds = new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);

        //substract padding
        Rect boundsWithPadding = new Rect(bounds.left + mPaddingLeft, bounds.top + mPaddingTop,
                bounds.right - mPaddingRight, bounds.bottom - mPaddingBottom);
        Rect newBounds  = new Rect(0,0,0,0);

        //safety
        if (boundsWithPadding.height() <= 0 || boundsWithPadding.width() <= 0) return newBounds;

        if ((float)boundsWithPadding.width() / (float)boundsWithPadding.height() > getElementAspectRatio() ) {
            newBounds = new Rect(boundsWithPadding.left, boundsWithPadding.top, 0, boundsWithPadding.bottom);
            newBounds.right = newBounds.left + Math.round(newBounds.height() * getElementAspectRatio());
            //shifts bounds to center
            if (mMiddleAligned) {
                int horizontalshift = (boundsWithPadding.width()-newBounds.width())/2;
                newBounds.left += horizontalshift;
                newBounds.right += horizontalshift;
            }
        } else {
            newBounds = new Rect(boundsWithPadding.left, boundsWithPadding.top, boundsWithPadding.right, 0);
            newBounds.bottom = newBounds.top + Math.round(newBounds.width() / getElementAspectRatio());
            //shifts bounds to center
            if (mMiddleAligned) {
                int verticalshift = (boundsWithPadding.height()-newBounds.height())/2;
                newBounds.top += verticalshift;
                newBounds.bottom += verticalshift;
            }
        }

        return newBounds;
    }


    /**
     * @brief Perform the layout calculations that are needed before the next drawing phase (because bounds have
     * changed).
     */
    private void performLayoutCalculations(Rect bounds){
        //calculate sizes
        RectF frame = new RectF(mPixelShift, mPixelShift, bounds.width() - mPixelShift,
                                bounds.height() - mPixelShift);
        mOutlineRect = new Rect(Math.round(frame.left), Math.round(frame.top),
                Math.round(frame.right),Math.round(frame.bottom));
        mBorderRect = new Rect(mOutlineRect.left + 1, mOutlineRect.top + 1, mOutlineRect.right - 1,
                mOutlineRect.bottom - 1);
        mBorderWidth = elementCalcBorderSize(mBorderRect);
        mPictureRect = new Rect(Math.round(mBorderRect.left) + mBorderWidth, Math.round(mBorderRect.top) + mBorderWidth,
                                Math.round(mBorderRect.right) - mBorderWidth, Math.round(mBorderRect.bottom) - mBorderWidth);

        if (mPicture != null) {
            mPicture.setBounds(mPictureRect);
        }
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
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) return;
        //draw outline
        c.drawRect(mOutlineRect, mOutlinePaint);
        //draw border
        c.drawRect(mBorderRect, mBorderPaint);
        //draw picture
        // set the picture rect
        if (mPicture != null) {
            if (mPicture.getBounds().width() == 0 && mPicture.getBounds().height() == 0) {
                performLayoutCalculations(getBounds());
                mPicture.setBounds(mPictureRect);
            }
            correctBounds();
            mPicture.draw(c);
        }
    }


    /**
     * @brief Corrects bounds when these are not properly updated, for example when view is used in lists
     */
    private void correctBounds() {
        if ((mPicture.getIntrinsicWidth() > mPicture.getIntrinsicHeight() && getBounds().height() > getBounds().width()) ||
                (mPicture.getIntrinsicHeight() > mPicture.getIntrinsicWidth() && getBounds().width() > getBounds().height())
                ) {
              setBounds(mOriginalBounds.left, mOriginalBounds.top, Math.max(mOriginalBounds.bottom,
                      mOriginalBounds.right), Math.max(mOriginalBounds.bottom, mOriginalBounds.right));

        }

    }


//---------------------------------------------------------------------------------------------------------------------
// ----- Helpers ------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
    }


    /**
     * @brief update all used paints
     */
    @Override
    protected void updateAllPaints() {
        createBorderPaint();
        createOutlinePaint();
//        updateDrawingBitmap();
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

