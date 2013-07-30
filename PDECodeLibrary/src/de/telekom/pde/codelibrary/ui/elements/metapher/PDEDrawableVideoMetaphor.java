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

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableVideoMetaphor
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows Video Scene with black Bars and Time
 */
@SuppressWarnings("unused")
public class PDEDrawableVideoMetaphor extends PDEDrawableBase {

//-----  properties ---------------------------------------------------------------------------------------------------
    private Drawable mScene;
    private boolean  m169Format;
    private String mTimeString;
    private RectF mBackgroundRect;
    private Rect mPictureRect;
    private RectF mTimeBarRect;
    private Paint mOutlinePaint;
    private PDEColor mOutlineColor;
    private Paint mTimeBarPaint;
    private PDEColor mTimeBarColor;
    private Paint mTimeStringPaint;
    private PDEColor mTimeStringColor;
    private int mBarHeight;



//----- init -----------------------------------------------------------------------------------------------------------
    /**
     * @brief Constructor
     */
    public PDEDrawableVideoMetaphor(Drawable drawable, String timestring) {
        // init drawable basics
        super();
        // init
        mScene = drawable;
        m169Format = true;
        mTimeString = timestring;
        mBarHeight = 0;

        //define colors
        //outline color
        mOutlineColor = new PDEColor();
        mOutlineColor.setColor(PDEColor.valueOf("#000000").getIntegerColor());
        //color of the bottom bar
        mTimeBarColor = new PDEColor();
        mTimeBarColor.setColor(PDEColor.valueOf("#000000").getIntegerColor());
        mTimeBarColor = mTimeBarColor.newColorWithCombinedAlpha(179);
        //color of the text
        mTimeStringColor = new PDEColor();
        mTimeStringColor.setColor(PDEColor.valueOf("#ffffff").getIntegerColor());
        // update paints
        update(true);
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Set Scene
     */
    public void setElementScene(Drawable scene) {
        //any change?
        if (scene == mScene) return;
        //remember
        mScene = scene;
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
    }


    /**
     * @brief Get if Format is 16:9
     */
    public boolean getElementFormat169() {
        return m169Format;
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
        super.setBounds(aspectRatioBounds.left, aspectRatioBounds.top, aspectRatioBounds.right,
                        aspectRatioBounds.bottom);
    }


    /**
     * @brief Calculate the correct aspect ratio bounds.
     *
     * @param bounds Available space
     * @return Rect with correct aspect ratio, fitting in available space
     */
    private Rect elementCalculateAspectRatioBounds(Rect bounds) {
        Rect newBounds = new Rect();

        if ((float)bounds.width() / (float)bounds.height() > getElementAspectRatio() ) {
            newBounds = new Rect(bounds.left, bounds.top, 0, bounds.bottom);
            newBounds.right = newBounds.left + Math.round(newBounds.height() * getElementAspectRatio());
        } else {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, 0);
            newBounds.bottom = newBounds.top + Math.round(newBounds.width() / getElementAspectRatio());
        }
        return newBounds;
    }


    /**
     * @brief Calculate scene image rect,
     *
     * @param bounds Size of the element
     * @return Rect with size of the video scene picture
     */
    private Rect elementCalculateSceneImageRect(Rect bounds) {
        Rect pictureBounds;

        float imageAspectRatio;
        if (mScene == null) {
            pictureBounds = new Rect(0, 0, 0, 0);
        } else {
            if (m169Format) {
                imageAspectRatio = (float)mScene.getIntrinsicWidth() / (float)mScene.getIntrinsicHeight();

                if (imageAspectRatio >= 16.0f / 9.0f) {
                    int y = Math.round(((float)bounds.height() - ((float)bounds.width() / imageAspectRatio)) / 2.0f);
                    pictureBounds = new Rect(bounds.left + 1, bounds.top + y, bounds.right - 1, bounds.bottom - y);
                } else {
                    int x = Math.round(((float) bounds.width() - (float) bounds.height() * imageAspectRatio) / 2.0f);
                    pictureBounds = new Rect(bounds.left + x,bounds.top + 1, bounds.right - x, bounds.bottom - 1 );
                }
            } else {
                pictureBounds = new Rect(bounds.left + 1, bounds.top + 1, bounds.right - 1, bounds.bottom - 1);
            }
        }
        return pictureBounds;
    }


    /**
     * @brief Update all of my sublayers.
     */
    @Override
    protected void doLayout() {
        performLayoutCalculations(new Rect(getBounds()));
    }


    /**
     * @brief Perform the layout calculations that are needed before the next drawing phase (because bounds have
     * changed).
     */
    private void performLayoutCalculations(Rect bounds){
        // background and picture size
        mBackgroundRect = new RectF(mPixelShift, mPixelShift, bounds.width() - mPixelShift,
                                    bounds.height() - mPixelShift);
        mPictureRect = elementCalculateSceneImageRect(bounds);
        mScene.setBounds(mPictureRect);

        // if width > 8 size of the bar at the bottom
        if (bounds.width() > PDEBuildingUnits.pixelFromBU(8))
        {
            // time bar
            float sizefactor = (1.8f / 50.0f) * (PDEBuildingUnits.buildingUnitsFromPixel(bounds.width()) - 8.0f);
            mBarHeight = Math.round(PDEBuildingUnits.exactPixelFromBU(2.0f) + sizefactor);
            mTimeBarRect = new RectF(bounds.left + mPixelShift, bounds.bottom - mBarHeight,
                                     bounds.right - mPixelShift, bounds.bottom - mPixelShift);
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
        createOutlinePaint();
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
    private void createOutlinePaint() {
        mOutlinePaint = new Paint();
        mOutlinePaint.setAntiAlias(true);
        mOutlinePaint.setColorFilter(mColorFilter);
        mOutlinePaint.setDither(mDither);
        mOutlinePaint.setColor(mOutlineColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief Changes of paint properties should also affect the scene, so use the update hook for this.
     *
     * @param paintPropertiesChanged shows if an update of the used Paint-Instances is needed.
     */
    @Override
    protected void updateHook(boolean paintPropertiesChanged) {
        if (!paintPropertiesChanged) return;
        mScene.setAlpha(mAlpha);
        mScene.setDither(mDither);
        mScene.setColorFilter(mColorFilter);
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
        c.drawRect(mBackgroundRect,mOutlinePaint);
        //draw scene image
        mScene.draw(c);

        //draw time bar and time string when width > 8 BUs
        if (bounds.width() > PDEBuildingUnits.pixelFromBU(8)) {
            //time bar
            c.drawRect(mTimeBarRect, mTimeBarPaint);
            //time string
            c.drawText(mTimeString, bounds.right - 6, bounds.bottom - (Math.round(mBarHeight *  (1.0f / 4.0f))),
                       mTimeStringPaint);
        }
    }
}
