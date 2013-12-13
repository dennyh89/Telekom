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
//  PDEDrawableVideoMetaphor
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows Video Scene with black Bars and Time
 */
public class PDEDrawableVideoMetaphor extends PDEDrawableBase {

//-----  properties ---------------------------------------------------------------------------------------------------
    private PDEConstants.PDEContentStyle mStyle;

    private Drawable mScene;
    private boolean  m169Format;
    private String mTimeString;
    private boolean mMiddleAligned;
    private int mOriginalHeight;
    private int mOriginalWidth;
    private Rect mSetBounds;
    private boolean mShadowEnabled;
    private PDEDrawableShapedShadow mElementShadowDrawable;

    private Rect mOutlineRect;
    private Rect mBackgroundRect;
    private Rect mPictureRect;
    private Rect mTimeBarRect;
    private Paint mOutlinePaintFlat;
    private PDEColor mOutlineColorFlat;
    private Paint mBackgroundPaint;
    private PDEColor mBackgroundColor;
    private Paint mTimeBarPaint;
    private PDEColor mTimeBarColor;
    private Paint mTimeStringPaint;
    private PDEColor mTimeStringColor;
    private Paint mOutlinePaintHaptic;
    private PDEColor mOutlineColorHaptic;
    private PDEColor mPictureBackgroundColor;
    private Paint mPictureBackgroundPaint;

    private int mBarHeight;
    private boolean mDarkStyle;


//----- init -----------------------------------------------------------------------------------------------------------
    /**
     * @brief Constructor
     */
    public PDEDrawableVideoMetaphor(Drawable drawable, String timestring) {
        // init drawable basics
        super();
        // init
        mStyle = PDEConstants.PDEContentStyle.PDEContentStyleFlat;
        mScene = drawable;
        mOriginalHeight = 0;
        mOriginalWidth = 0;
        if (mScene != null) {
            mOriginalHeight = mScene.getIntrinsicHeight();
            mOriginalWidth = mScene.getIntrinsicWidth();
        }

        m169Format = true;
        mTimeString = timestring;
        mMiddleAligned = false;
        mBarHeight = 0;

        // shadow is created on demand
        mElementShadowDrawable = null;
        mShadowEnabled = false;

        //define colors
        //outline color
        mOutlineColorFlat = new PDEColor();
        mOutlineColorFlat.setColor(PDEColor.valueOf("Black45Alpha").getIntegerColor());
        //outline color
        mOutlineColorHaptic = new PDEColor();
        mOutlineColorHaptic.setColor(PDEColor.valueOf("DTBlack").getIntegerColor());
        //color of the black background
        mBackgroundColor = new PDEColor();
        mBackgroundColor.setColor(PDEColor.valueOf("DTBlack").getIntegerColor());
        //color of the bottom bar
        mTimeBarColor = new PDEColor();
        mTimeBarColor.setColor(PDEColor.valueOf("DTBlack").getIntegerColor());
        mTimeBarColor = mTimeBarColor.newColorWithCombinedAlpha(141);
        //color of the text
        mTimeStringColor = new PDEColor();
        mTimeStringColor.setColor(PDEColor.valueOf("DTWhite").getIntegerColor());
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
                if (mBackgroundRect == null) return;
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
     * @brief Set Scene
     */
    public void setElementScene(Drawable scene) {
        //any change?
        if (scene == mScene) return;
        //remember
        mScene = scene;

        mOriginalHeight = 0;
        mOriginalWidth = 0;
        if (mScene != null) {
            mOriginalHeight = mScene.getIntrinsicHeight();
            mOriginalWidth = mScene.getIntrinsicWidth();
        }

        //redraw
        update();
    }


    /**
     * @brief Get Scene
     */
    public Drawable getElementScene() {
        return mScene;
    }


    /**
     * @brief Set Time String
     *
     * @param ts The time string in the lower right corner
     */
    public void setElementTimeString(String ts) {
        //any change?
        if (ts.equals(mTimeString)) return;
        //remember
        mTimeString = ts;

        //redraw
        update();
    }


    /**
     * @brief Get Time String
     *
     * @return the time string of the lower right corner
     */
    @SuppressWarnings("unused")
    public String getElementTimeString() {
        return mTimeString;
    }


    /**
     * @brief Set if Format is 16:9, is false it is 1:1
     */
    public void setElementFormat169(boolean f169) {
        //any change?
        if (m169Format == f169) return;
        //remember
        m169Format = f169;
        // set current bounds again to update
        setBounds(getBounds());

        //redraw
        update();
    }


    /**
     * @brief Get if Format is 16:9
     */
    @SuppressWarnings("unused")
    public boolean getElementFormat169() {
        return m169Format;
    }


    /**
     * @brief Set darkstyle color
     */
    public void setElementDarkStyle(boolean isDarkStyle) {
        if (isDarkStyle == mDarkStyle) return;

        mDarkStyle = isDarkStyle;

        if (mDarkStyle) {
            mOutlineColorFlat.setColor(PDEColor.valueOf("DTBlack").getIntegerColor());

        } else {
            mOutlineColorFlat.setColor(PDEColor.valueOf("Black45Alpha").getIntegerColor());
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
     * Middle aligned is for use in View, when true the element is aligned in the middle of the border,
     * when false in the upper left
     */
    public void setElementMiddleAligned(boolean aligned) {
        if (mMiddleAligned == aligned) return;
        mMiddleAligned = aligned;
    }

    /**
     * @brief helper function to get aspect ratio
     */
    private float getElementAspectRatio() {
        if (m169Format) {
            return 16.0f / 9.0f;
        } else {
            return 1.0f;
        }
    }


    /**
     * @brief Helper function to get intrinsic size of the picture
     */
    public Point getNativeSize() {
        if (mScene != null) return new Point(mScene.getIntrinsicWidth(), mScene.getIntrinsicHeight());
        return new Point(0, 0);
    }


    /**
     * @brief Helper function to test if photo frame has a picture in it
     */
    public boolean hasPicture() {
        return mScene != null;
    }

    /**
     * @brief Get element height.
     */
    public int getElementHeight() {
        if (mSetBounds != null && mSetBounds.height() != 0) {
            return mSetBounds.height();
        }

        if (mOriginalHeight >= 0) {
            int shadowWidth = 0;
            if (mShadowEnabled) {
                shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
            }

            if ((float)mOriginalWidth/(float)mOriginalHeight > getElementAspectRatio()) {
                return Math.round(((float)mOriginalWidth / getElementAspectRatio()) + 4 + 2*shadowWidth);
            } else {
                return mOriginalHeight + 4 + 2*shadowWidth;
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
            int shadowWidth = 0;
            if (mShadowEnabled) {
                shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
            }

            if ((float)mOriginalWidth/(float)mOriginalHeight > getElementAspectRatio()) {
                return mOriginalWidth + 4 + 2*shadowWidth;
            } else {
                return Math.round((float)mOriginalHeight * getElementAspectRatio()) + 4 + 2*shadowWidth;
            }
        }

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
     * @return Rect with correct aspect ratio, fitting in available space
     */
    private Rect elementCalculateAspectRatioBounds(Rect bounds) {
        Rect newBounds;

        if ((float)bounds.width() / (float)bounds.height() > getElementAspectRatio() ) {
            newBounds = new Rect(bounds.left, bounds.top, 0, bounds.bottom);
            newBounds.right = newBounds.left + Math.round(newBounds.height() * getElementAspectRatio());

            if (mMiddleAligned) {
                int horizontalshift = (bounds.width()-newBounds.width())/2;
                newBounds.left += horizontalshift;
                newBounds.right += horizontalshift;
            }
        } else {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, 0);
            newBounds.bottom = newBounds.top + Math.round(newBounds.width() / getElementAspectRatio());

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

        if (width / height > getElementAspectRatio() ) {
            newBounds = new Rect(0, 0, Math.round(width), Math.round(height));
            newBounds.right = newBounds.left + Math.round(newBounds.height() * getElementAspectRatio());
        } else {
            newBounds = new Rect(0, 0, Math.round(width), Math.round(height));
            newBounds.bottom = newBounds.top + Math.round(newBounds.width() / getElementAspectRatio());
        }

        mSetBounds = newBounds;
    }


    /**
     * @brief Calculate scene image rect,
     *
     * @param bounds Size of the element
     * @return Rect with size of the video scene picture
     */
    protected Rect elementCalculateSceneImageRect(Rect bounds) {
        Rect pictureBounds;

        float imageAspectRatio;
        if (mScene == null) {
            pictureBounds = new Rect(0, 0, 0, 0);
        } else {
            if (m169Format) {
                imageAspectRatio = (float)mScene.getIntrinsicWidth() / (float)mScene.getIntrinsicHeight();

                if (imageAspectRatio >= 16.0f / 9.0f) {
                    int y = Math.round(((float)bounds.height() - ((float)bounds.width() / imageAspectRatio)) / 2.0f);
                    pictureBounds = new Rect(bounds.left + 2, bounds.top + y, bounds.right - 2, bounds.bottom - y);
                } else {
                    int x = Math.round(((float) bounds.width() - (float) bounds.height() * imageAspectRatio) / 2.0f);
                    pictureBounds = new Rect(bounds.left + x,bounds.top + 2, bounds.right - x, bounds.bottom - 2 );
                }
            } else {
                pictureBounds = new Rect(bounds.left + 2, bounds.top + 2, bounds.right - 2, bounds.bottom - 2);
            }
        }
        return pictureBounds;
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
        Rect frame = new Rect(Math.round(mPixelShift), Math.round(mPixelShift), Math.round(bounds.width() - mPixelShift),
                Math.round(bounds.height() - mPixelShift));

        // background and picture size
        mOutlineRect = new Rect(frame.left, frame.top, frame.right, frame.bottom);
        mBackgroundRect = new Rect(mOutlineRect.left + 2, mOutlineRect.top + 2, mOutlineRect.right - 2,
                mOutlineRect.bottom - 2);
        mPictureRect = elementCalculateSceneImageRect(new Rect(Math.round(frame.left),Math.round(frame.top),
                Math.round(frame.right),Math.round(frame.bottom)));


        // if width > 8 size of the bar at the bottom
        if (frame.width() > PDEBuildingUnits.pixelFromBU(8))
        {
            // time bar
            float sizefactor = (1.8f / 50.0f) * (PDEBuildingUnits.buildingUnitsFromPixel(frame.width()) - 8.0f);
            mBarHeight = Math.round(PDEBuildingUnits.exactPixelFromBU(2.0f) + sizefactor);
            mTimeBarRect = new Rect(mBackgroundRect.left, mBackgroundRect.bottom - mBarHeight,
                    mBackgroundRect.right, mBackgroundRect.bottom);
            // time string
            mTimeStringPaint.setTextSize(Math.round(mBarHeight * (3.0f / 4.0f)));
        }
    }


    /**
     * @brief Perform the layout calculations that are needed before the next drawing phase (because bounds have
     * changed).
     */
    private void performLayoutCalculationsHaptic(Rect bounds){
        Rect frame = new Rect(Math.round(mPixelShift), Math.round(mPixelShift), Math.round(bounds.width() - mPixelShift),
                Math.round(bounds.height() - mPixelShift));

        //adjust frame when shadow is enabled
        if (mShadowEnabled) {
            mElementShadowDrawable.setBounds(frame);
            int shadowWidth = (int)mElementShadowDrawable.getElementBlurRadius();
            frame = new Rect(frame.left + shadowWidth, frame.top + shadowWidth -PDEBuildingUnits.oneTwelfthsBU(),
                    frame.right - shadowWidth, frame.bottom - shadowWidth - PDEBuildingUnits.oneTwelfthsBU());
        }


        // background and picture size
        mBackgroundRect = new Rect(frame.left, frame.top, frame.right, frame.bottom);
        mPictureRect = elementCalculateSceneImageRect(new Rect(Math.round(frame.left),Math.round(frame.top),
                Math.round(frame.right),Math.round(frame.bottom)));


        // if width > 8 size of the bar at the bottom
        if (frame.width() > PDEBuildingUnits.pixelFromBU(8))
        {
            // time bar
            float sizefactor = (1.8f / 50.0f) * (PDEBuildingUnits.buildingUnitsFromPixel(frame.width()) - 8.0f);
            mBarHeight = Math.round(PDEBuildingUnits.exactPixelFromBU(2.0f) + sizefactor);
            mTimeBarRect = new Rect(frame.left + 2, frame.bottom - mBarHeight,
                    frame.right - 2, frame.bottom - 2);
            // time string
            mTimeStringPaint.setTextSize(Math.round(mBarHeight * (3.0f / 4.0f)));
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
        createTimeBarPaint();
        createTimeStringPaint();
        createOutlinePaintFlat();
        createBackgroundPaint();
        createOutlinePaintHaptic();
        createPictureBackgroundPaint();
    }


    /**
     * @brief Create the paint for the time bar.
     */
    private void createTimeBarPaint() {
        mTimeBarPaint = new Paint();
        mTimeBarPaint.setAntiAlias(true);
        mTimeBarPaint.setColorFilter(mColorFilter);
        mTimeBarPaint.setDither(mDither);
        mTimeBarPaint.setColor(mTimeBarColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief Create the paint for the time string.
     */
    private void createTimeStringPaint() {
        mTimeStringPaint = new Paint();
        mTimeStringPaint.setTextAlign(Paint.Align.RIGHT);
        mTimeStringPaint.setAntiAlias(true);
        mTimeStringPaint.setColorFilter(mColorFilter);
        mTimeStringPaint.setDither(mDither);
        mTimeStringPaint.setColor(mTimeStringColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief create the paint for the outline.
     */
    private void createOutlinePaintFlat() {
        mOutlinePaintFlat = new Paint();
        mOutlinePaintFlat.setAntiAlias(true);
        mOutlinePaintFlat.setColorFilter(mColorFilter);
        mOutlinePaintFlat.setDither(mDither);
        mOutlinePaintFlat.setColor(mOutlineColorFlat.newIntegerColorWithCombinedAlpha(mAlpha));
    }

    /**
     * @brief create the paint for the background.
     */
    private void createBackgroundPaint() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColorFilter(mColorFilter);
        mBackgroundPaint.setDither(mDither);
        mBackgroundPaint.setColor(mBackgroundColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }

    /**
     * @brief create the paint for the outline.
     */
    private void createOutlinePaintHaptic() {
        mOutlinePaintHaptic = new Paint();
        mOutlinePaintHaptic.setAntiAlias(true);
        mOutlinePaintHaptic.setColorFilter(mColorFilter);
        mOutlinePaintHaptic.setDither(mDither);
        mOutlinePaintHaptic.setColor(mOutlineColorHaptic.newIntegerColorWithCombinedAlpha(mAlpha));
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
     * @brief Changes of paint properties should also affect the scene, so use the update hook for this.
     *
     * @param paintPropertiesChanged shows if an update of the used Paint-Instances is needed.
     */
    @Override
    protected void updateHook(boolean paintPropertiesChanged) {
        if (!paintPropertiesChanged) return;
        if (mScene == null) return;
        mScene.setAlpha(mAlpha);
        mScene.setDither(mDither);
        mScene.setColorFilter(mColorFilter);
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
    protected void updateDrawingBitmapFlat(Canvas c, Rect bounds) {
        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) return;
        //draw outline
        c.drawRect(mOutlineRect, mOutlinePaintFlat);
        //draw background
        c.drawRect(mBackgroundRect,mBackgroundPaint);
        c.drawRect(mPictureRect, mPictureBackgroundPaint);
        //draw scene image
        if (mScene != null) {
            mScene.setBounds(mPictureRect);
            mScene.draw(c);
        }


        //draw time bar and time string when width > 8 BUs
        if (mOutlineRect.width() > PDEBuildingUnits.pixelFromBU(8)) {
            //time bar
            c.drawRect(mTimeBarRect, mTimeBarPaint);
            //time string
            c.drawText(mTimeString, mTimeBarRect.right - 6, mTimeBarRect.bottom - (Math.round(mBarHeight *  (1.0f / 4.0f))),
                    mTimeStringPaint);
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
        c.drawRect(mBackgroundRect, mOutlinePaintHaptic);
        c.drawRect(mPictureRect, mPictureBackgroundPaint);
        //draw scene image
        if (mScene != null) {
            mScene.setBounds(mPictureRect);
            mScene.draw(c);
        }

        //draw time bar and time string when width > 8 BUs
        if (mBackgroundRect.width() > PDEBuildingUnits.pixelFromBU(8)) {
            //time bar
            c.drawRect(mTimeBarRect, mTimeBarPaint);
            //time string
            c.drawText(mTimeString, mTimeBarRect.right - 6, mTimeBarRect.bottom - (Math.round(mBarHeight *  (1.0f / 4.0f))),
                    mTimeStringPaint);
        }
    }
}