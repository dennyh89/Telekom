/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */
package de.telekom.pde.codelibrary.ui.elements.metaphor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableBase;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;


//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawablePhotoFrame
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows picture With Frame.
 */
public class PDEDrawablePhotoFrame extends PDEDrawableBase {

//-----  properties ---------------------------------------------------------------------------------------------------
    private PDEConstants.PDEContentStyle mStyle;

    private Drawable mPicture;
    private final static float CONST_ASPECTRATIO = 1.5f;
    private int mOriginalWidth;
    private int mOriginalHeight;
    private Rect mOriginalBounds;
    private Rect mSetBounds;

    private int mPaddingTop;
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingBottom;

    private boolean mShadowEnabled;

    // drawing helpers
    private boolean mMiddleAligned;
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
    public PDEDrawablePhotoFrame(Drawable drawable) {
        // init drawable basics
        super();
        mStyle = PDEConstants.PDEContentStyle.PDEContentStyleFlat;
        mMiddleAligned = false;
        // init PDE defaults
        mPicture = drawable;

        mOriginalHeight = 0;
        mOriginalWidth = 0;
        if (mPicture != null) {
            mOriginalHeight = mPicture.getIntrinsicHeight();
            mOriginalWidth = mPicture.getIntrinsicWidth();
        }

        mPaddingLeft = 0;
        mPaddingTop = 0;
        mPaddingRight = 0;
        mPaddingBottom = 0;

        // shadow is created on demand
        mElementShadowDrawable = null;
        mShadowEnabled = false;

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
     * @brief Update the shadow drawable if we've got one
     */
    private void updateElementShadowDrawable(Point elementSize) {
        // check if we have a shadow set
        if (mElementShadowDrawable != null) {
            Rect frame;
            if (mShadowEnabled == false) {
                // keep current shadow position, just update the size
                Rect bounds = mElementShadowDrawable.getBounds();
                frame = new Rect(bounds.left, bounds.top, bounds.left + elementSize.x+(2*(int)mElementShadowDrawable.getElementBlurRadius()),
                        bounds.top + elementSize.y+(2*(int)mElementShadowDrawable.getElementBlurRadius()));
                mElementShadowDrawable.setBounds(frame);
            } else {
                if (mOutlineRect == null) return;
                frame = new Rect(Math.round(mPixelShift), Math.round(mPixelShift), Math.round(getBounds().width() - mPixelShift),
                        Math.round(getBounds().height() - mPixelShift));
                mElementShadowDrawable.setBounds(frame);
            }
        }
    }

    /**
     * @brief Activate shadow.
     */
    public void setElementShadowEnabled(boolean enabled) {
        //any change?
        if (enabled == mShadowEnabled) return;
        //remember
        mShadowEnabled = enabled;

        if (mShadowEnabled) {
            createElementShadow();
        } else {
            clearElementShadow();
        }

        update();
    }

    /**
     * @brief Get if shadow is activated.
     */
    public boolean getElementShadowEnabled() {
        return mShadowEnabled;
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
    public void setElementPicture(Drawable picture)
    {
        //any change?
        if (picture == mPicture) return;
        //remember
        mPicture = picture;

        mOriginalHeight = 0;
        mOriginalWidth = 0;
        if (mPicture != null) {
            mOriginalHeight = mPicture.getIntrinsicHeight();
            mOriginalWidth = mPicture.getIntrinsicWidth();
        }

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


    /**
     * @brief Set middle aligned
     *
     * Middle aligned is for use in PDEPhotoFrameView, when true the picture is aligned in the middle of the border,
     * when false in the upper left
     */
    public void setElementMiddleAligned(boolean aligned) {
        if (mMiddleAligned == aligned) return;
        mMiddleAligned = aligned;
    }

    /**
     * @brief Set all paddings
     */
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    public int getElementPaddingLeft() {
        return mPaddingLeft;
    }

    /**
     * @brief Get top padding
     */
    @SuppressWarnings("unused")
    public int getElementPaddingTop() {
        return mPaddingTop;
    }

    /**
     * @brief Get right padding
     */
    @SuppressWarnings("unused")
    public int getElementPaddingRight() {
        return mPaddingRight;
    }

    /**
     * @brief Get left padding
     */
    @SuppressWarnings("unused")
    public int getElementPaddingBottom() {
        return mPaddingBottom;
    }

    /**
     * @brief Get padding rect
     */
    @SuppressWarnings("unused")
    public Rect getElementPaddingRect() {
        return new Rect(mPaddingLeft,mPaddingTop,mPaddingRight,mPaddingBottom);
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
        if (mSetBounds != null && mSetBounds.height() != 0) {
            return mSetBounds.height();
        }

        if (mOriginalHeight >= 0) {
            if (mStyle == PDEConstants.PDEContentStyle.PDEContentStyleFlat) {
                return calculateElementHeightFlat();
            } else {
                return calculateElementHeightHaptic();
            }

        }
        return 0;
    }



    /**
     * @brief Get element width.
     */
    public int getElementWidth() {
        if (mSetBounds != null && mSetBounds.width() != 0) {
            return mSetBounds.width();
        }

        if (mOriginalWidth >= 0) {
            if (mStyle == PDEConstants.PDEContentStyle.PDEContentStyleFlat) {
                return calculateElementWidthFlat();
            } else {
                return calculateElementWidthHaptic();
            }

        }
        return 0;
    }



    /**
     * @brief Calculate element height.
     */
    private int calculateElementHeightFlat() {
        return mOriginalHeight + 4;
    }



    /**
     * @brief Calculate element width.
     */
    private int calculateElementWidthFlat() {
        return mOriginalWidth + 4;
    }


    /**
     * @brief Calculate element height.
     */
    private int calculateElementHeightHaptic() {
        int shadowWidth = 0;
        if (mShadowEnabled) {
            shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
        }

        int y = Math.max(mOriginalWidth, mOriginalHeight);
        int res = Math.round((5.0f / 21.0f + (float) y / 105.0f));
        //minimum border width 0.2 BU
        if (res < 0.2* PDEBuildingUnits.BU()) res = (int)Math.round(0.2 * PDEBuildingUnits.BU());

        return mOriginalHeight + 2*res + 2*shadowWidth;
    }



    /**
     * @brief Calculate element width.
     */
    private int calculateElementWidthHaptic() {
        int shadowWidth = 0;
        if (mShadowEnabled) {
            shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
        }

        int y = Math.max(mOriginalWidth, mOriginalHeight);
        int res = Math.round((5.0f / 21.0f + (float) y / 105.0f));
        //minimum border width 0.2 BU
        if (res < 0.2* PDEBuildingUnits.BU()) res = (int)Math.round(0.2 * PDEBuildingUnits.BU());

        return mOriginalWidth + 2*res + 2*shadowWidth;
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
        // do needed layout calculations.
        performLayoutCalculations(new Rect(getBounds()));

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

        mSetBounds = newBounds;
        return newBounds;
    }

    /**
     * @brief Help function for use in views for layouting, to make sure Wrap_content works correctly.
     *
     * Does essentially the same as elementCalculateAspectRatioBounds but with less use of resources, for cheaper
     * use in views.
     *
     */
    public void setInternalBounds(float width, float height) {
        Rect newBounds;
        //substract padding
        Rect boundsWithPadding = new Rect(mPaddingLeft, mPaddingTop,
                Math.round(width) - mPaddingRight, Math.round(height) - mPaddingBottom);

        //safety
        if (boundsWithPadding.height() <= 0 || boundsWithPadding.width() <= 0) return;

        if ((float)boundsWithPadding.width() / (float)boundsWithPadding.height() > getElementAspectRatio() ) {
            newBounds = new Rect(boundsWithPadding.left, boundsWithPadding.top, 0, boundsWithPadding.bottom);
            newBounds.right = newBounds.left + Math.round(newBounds.height() * getElementAspectRatio());
        } else {
            newBounds = new Rect(boundsWithPadding.left, boundsWithPadding.top, boundsWithPadding.right, 0);
            newBounds.bottom = newBounds.top + Math.round(newBounds.width() / getElementAspectRatio());
        }

        mSetBounds = newBounds;
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
        if (res < 0.2* PDEBuildingUnits.BU()) res = (int)Math.round(0.2 * PDEBuildingUnits.BU());
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
        Rect frame = new Rect(Math.round(mPixelShift), Math.round(mPixelShift), Math.round(bounds.width() - mPixelShift),
                Math.round(bounds.height() - mPixelShift));

        //adjust frame when shadow is enabled
        if (mShadowEnabled)
        mElementShadowDrawable.setBounds(frame);{
            int shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
            frame = new Rect(frame.left + shadowWidth, frame.top + shadowWidth -PDEBuildingUnits.oneTwelfthsBU(),
                    frame.right - shadowWidth, frame.bottom - shadowWidth - PDEBuildingUnits.oneTwelfthsBU());


        }

        mOutlineRect = new Rect(Math.round(frame.left), Math.round(frame.top),
                Math.round(frame.right),Math.round(frame.bottom));
        mBorderRect = new Rect(mOutlineRect.left + 1, mOutlineRect.top + 1, mOutlineRect.right - 1,
                mOutlineRect.bottom - 1);
        borderWidth = elementCalcBorderSize(mBorderRect);
        mPictureRect = new Rect(Math.round(mBorderRect.left) + borderWidth, Math.round(mBorderRect.top) + borderWidth,
                Math.round(mBorderRect.right) - borderWidth, Math.round(mBorderRect.bottom) - borderWidth);

        if (mPicture != null) {
            mPicture.setBounds(mPictureRect);
        }

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
        c.drawRect(mOutlineRect, mOutlineFlatPaint);
        c.drawRect(mPictureRect, mPictureBackgroundPaint);

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
     * @brief Updates our drawing bitmap and triggers a redraw of this element.
     *
     * If a drawing parameter changes, we need to call this function in order to update our drawing-bitmap and
     * in order to trigger the draw of our updated bitmap to the canvas.
     */
    private void updateDrawingBitmapHaptic(Canvas c, Rect bounds) {
        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) return;
        //draw shadow
        if (mShadowEnabled && mElementShadowDrawable != null) {
            mElementShadowDrawable.draw(c);
        }

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
            correctBounds();
            mPicture.draw(c);
        }
    }

    /**
     * @brief Corrects bounds when these are not properly updated, for example when view is used in lists
     */
    protected void correctBounds() {
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

