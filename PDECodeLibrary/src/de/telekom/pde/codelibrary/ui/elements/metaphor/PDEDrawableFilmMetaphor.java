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
//  PDEDrawableFilmMetaphor
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows Film Metaphor
 */
public class PDEDrawableFilmMetaphor extends PDEDrawableBase {

//-----  properties ---------------------------------------------------------------------------------------------------
    private PDEConstants.PDEContentStyle mStyle;

    private Drawable mPicture;
    private final static float CONST_ASPECTRATIO = 20.0f / 29.0f;
    private boolean mMiddleAligned;
    private int mOriginalHeight;
    private int mOriginalWidth;
    private Rect mSetBounds;

    private boolean mShadowEnabled;
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
    public PDEDrawableFilmMetaphor(Drawable drawable)
    {
        // init drawable basics
        super();

        // init PDE defaults
        mStyle = PDEConstants.PDEContentStyle.PDEContentStyleFlat;

        mMiddleAligned = false;
        mPicture = drawable;

        mOriginalHeight = 0;
        mOriginalWidth = 0;
        if (mPicture != null) {
            mOriginalHeight = mPicture.getIntrinsicHeight();
            mOriginalWidth = mPicture.getIntrinsicWidth();
        }

        // shadow is created on demand
        mElementShadowDrawable = null;
        mShadowEnabled = false;

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
            Rect frame;
            if (mShadowEnabled == false) {
                // keep current shadow position, just update the size
                Rect bounds = mElementShadowDrawable.getBounds();
                frame = new Rect(bounds.left, bounds.top, bounds.left + elementSize.x+(2*(int)mElementShadowDrawable.getElementBlurRadius()),
                        bounds.top + elementSize.y+(2*(int)mElementShadowDrawable.getElementBlurRadius()));
                mElementShadowDrawable.setBounds(frame);
            } else {
                if (mOuterRect == null) return;
                mElementShadowDrawable.setBounds(mFrame);
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
    public void setElementPicture(Drawable picture) {
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
     * @brief Get Picture
     */
    @SuppressWarnings("unused")
    public Drawable getElementPicture() {

        return mPicture;
    }

    /**
     * @brief Set middle aligned
     *
     * Middle aligned is for use in View, when true the element is aligned in the middle of the border,
     * when false in the upper left
     */
    public void setElementMiddleAligned(boolean aligned) {
        if (mMiddleAligned == aligned) return;
        mMiddleAligned = aligned;
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
     * @brief Calculate element height haptic
     */
    private int calculateElementHeightFlat() {
        if ((float)mOriginalWidth / (float)mOriginalHeight > CONST_ASPECTRATIO) {
            return Math.round(((float)mOriginalWidth / CONST_ASPECTRATIO) + 4);
        } else {
            return mOriginalHeight + 4;
        }

    }

    /**
     * @brief Calculate element width flat
     */
    private int calculateElementWidthFlat() {
        if ((float)mOriginalWidth/(float)mOriginalHeight > CONST_ASPECTRATIO) {
            return mOriginalWidth + 4;
        } else {
            return Math.round(((float)mOriginalHeight * CONST_ASPECTRATIO) + 4);
        }
    }



    /**
     * @brief Calculate element height haptic
     */
    protected int calculateElementHeightHaptic() {
        float height;
        int shadowWidth = 0;
        if (mShadowEnabled) {
            shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
        }

        if ((float)mOriginalWidth / (float)mOriginalHeight > CONST_ASPECTRATIO) {
            height =  (float)mOriginalWidth / CONST_ASPECTRATIO;
        } else {
            height =  mOriginalHeight;
        }

        return Math.round(height / (28.0f/29.0f)) + 2*shadowWidth;
    }

    /**
     * @brief Calculate element width haptic
     */
    protected int calculateElementWidthHaptic() {
        float width;
        int shadowWidth = 0;
        if (mShadowEnabled) {
            shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
        }

        if ((float)mOriginalWidth/(float)mOriginalHeight > CONST_ASPECTRATIO) {
            width = mOriginalWidth;
        } else {
            width = (float)mOriginalHeight * CONST_ASPECTRATIO;
        }

        return Math.round(width / (19.5f / 20.0f)) + 2*shadowWidth;
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
     * @brief Perform the layout calculations for flat style that are needed before the next drawing phase (because bounds have
     * changed).
     */
    private void performLayoutCalculationsFlat(Rect bounds){
        Rect frame = new Rect(Math.round(mPixelShift), Math.round(mPixelShift),
                Math.round(bounds.width() - mPixelShift), Math.round(bounds.height() - mPixelShift));
        //outline size
        mOuterRect = new RectF(frame.left, frame.top, frame.right, frame.bottom);
        //picture size
        mPictureRect = new Rect(Math.round(frame.left + 2),
                Math.round(frame.top + 2),
                Math.round(frame.right - 2),
                Math.round(frame.bottom - 2));
    }



    /**
     * @brief Perform the layout calculations for haptic style that are needed before the next drawing phase (because bounds have
     * changed).
     */
    private void performLayoutCalculationsHaptic(Rect bounds){
        mFrame = new Rect(Math.round(mPixelShift), Math.round(mPixelShift), Math.round(bounds.width() - mPixelShift),
                Math.round(bounds.height() - mPixelShift));

        //adjust frame when shadow is enabled
        if (mShadowEnabled) {
            mElementShadowDrawable.setBounds(mFrame);
            int shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
            mFrame = new Rect(mFrame.left + shadowWidth, mFrame.top + shadowWidth - PDEBuildingUnits.oneTwelfthsBU(),
                    mFrame.right - shadowWidth, mFrame.bottom - shadowWidth - PDEBuildingUnits.oneTwelfthsBU());
        }

        //outline size
        mOuterRect = new RectF(Math.round(mFrame.left - (0.5f / 20.0f) * mFrame.width()), mFrame.top, mFrame.right, mFrame.bottom);
        mOuterCornerRadius = (0.5f / 20.0f) * mFrame.width();
        //picture size
        int borderWidth = Math.round((0.5f / 29.0f) * mFrame.height());
        mPictureRect = new Rect(mFrame.left, borderWidth, mFrame.right - borderWidth, mFrame.bottom -borderWidth);
        //handle size
        mHandleCornerRadius = (0.3f / 20.0f) * mFrame.width();
        mHandleRect = new RectF(Math.round((19.0f / 20.0f) * mFrame.width()),
                Math.round((12.5f / 29.0f) * mFrame.height()),
                mFrame.right+Math.round((0.5f / 20.0f) * mFrame.width()),
                mFrame.bottom-Math.round((12.5f / 29.0f) * mFrame.height()));
        //shape size
        mShapePath = elementCreateShapePath(mFrame);
        //blur radius on the left
        updateBlurColors();
        mGradient = new LinearGradient( mFrame.left,
                (mFrame.top - mFrame.bottom) / 2, (0.75f / 20.0f) * mFrame.width() ,
                (mFrame.top - mFrame.bottom) / 2,mColors,null,
                Shader.TileMode.MIRROR);
    }




    /**
     * @brief Calculate the correct aspect ratio bounds.
     *
     * @param bounds Available space
     * @return  Rect with correct aspect ratio, fitting in available space
     */
    private Rect elementCalculateAspectRatioBounds(Rect bounds) {
        Rect newBounds;

        //calculate size, based on aspect ratio
        if ((float)bounds.width() / (float)bounds.height() > CONST_ASPECTRATIO ) {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);
            newBounds.right = newBounds.left + Math.round(((float)newBounds.height() * CONST_ASPECTRATIO));

            if (mMiddleAligned) {
                int horizontalshift = (bounds.width()-newBounds.width())/2;
                newBounds.left += horizontalshift;
                newBounds.right += horizontalshift;
            }

        } else {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);
            newBounds.bottom = newBounds.top + Math.round((float)newBounds.width() / CONST_ASPECTRATIO);

            if (mMiddleAligned) {
                int verticalshift = (bounds.height()-newBounds.height())/2;
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

        //calculate size, based on aspect ratio
        if (width / height > CONST_ASPECTRATIO ) {
            newBounds = new Rect(0, 0, Math.round(width), Math.round(height));
            newBounds.right = newBounds.left + Math.round(((float)newBounds.height() * CONST_ASPECTRATIO));
        } else {
            newBounds = new Rect(0, 0, Math.round(width), Math.round(height));
            newBounds.bottom = newBounds.top + Math.round((float)newBounds.width() / CONST_ASPECTRATIO);
        }

        mSetBounds = newBounds;
    }



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

        //draw shadow
        if (mShadowEnabled && mElementShadowDrawable != null) {
            mElementShadowDrawable.draw(c);
        }

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
