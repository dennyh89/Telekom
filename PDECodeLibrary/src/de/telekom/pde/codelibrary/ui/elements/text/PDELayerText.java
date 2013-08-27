/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */
package de.telekom.pde.codelibrary.ui.elements.text;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.drawables.PDEDrawableBase;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;

//----------------------------------------------------------------------------------------------------------------------
//  PDELayerText
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief PDELayerText
 */
public class PDELayerText extends PDEDrawableBase {
    //alignment modes
    public enum PDELayerTextAlignmentMode {
        PDELayerTextAlignmentModeStandard,
        PDELayerTextAlignmentModeBaseLine,
        PDELayerTextAlignmentModeCapHeight
    }

    //properties
    private String mText;
    private final static String EllipsizeString = "...";
    private int mMaxLines = -1;

    private Paint mTextPaint;
    private PDEColor mTextColor;
    private Paint mBackgroundPaint;
    private PDEColor mBackgroundColor;
    private PDELayerTextAlignmentMode mAlignmentMode;
    private PDEConstants.PDEAlignment mHorizontalAlignment;
    private PDEConstants.PDEVerticalAlignment mVerticalAlignment;
    private float mLineDistanceFactor;

    private int mAlpha;
    private boolean mDither;
    private ColorFilter mColorFilter;

    private Bitmap mDrawingBitmap;

    private float mTextSize;
    private PDETypeface mTypeface;
    private boolean mEllipsize = false;
    private int mMetricsTopDistance;
    private int mMetricsBottomDistance;
    private int mBoundsLeft;
    private int mBaseLine;
    private int mInternalBaseLine;
    private int mOffset;
    private int mCapHeight;
    private Paint.FontMetrics mMetrics;

    private int mTextHeight = 0;
    private int mTextOffset = 0;

    private float mShadowOffsetX;
    private float mShadowOffsetY;
    private float mShadowAlpha;

    private Paint mShadowPaint = null;
    private PDEColor mShadowColor;
    private boolean mShadowEnabled;

    private int mBoundsWidth;
    private int mBoundsHeight;
    private int mPaddingTop;
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingBottom;


    /**
     * @brief Constructor.
     *
     */
    public PDELayerText(String text) {
        super();

        //initialize
        mAlpha = 255;
        mDither = false;
        mColorFilter = null;
        mText = text;
        mDrawingBitmap = null;
        mTypeface = null;
        mLineDistanceFactor = 1;

        mTextSize = PDETypeface.sTeleGroteskDefaultSize;
        mTypeface = PDETypeface.sDefaultFont;
        mHorizontalAlignment = PDEConstants.PDEAlignment.PDEAlignmentLeft;
        mVerticalAlignment = PDEConstants.PDEVerticalAlignment.PDEAlignmentTop;
        mAlignmentMode = PDELayerTextAlignmentMode.PDELayerTextAlignmentModeStandard;
        mBaseLine = 0;

        mTextPaint = new Paint();
        mTextColor = new PDEColor();
        mTextColor.setColor(PDEColor.valueOf("DTBlack").getIntegerColor());
        createTextPaint();

        mBackgroundPaint = new Paint();
        mBackgroundColor = new PDEColor();
        mBackgroundColor.setColor(PDEColor.valueOf("DTTransparentWhite").getIntegerColor());

        mShadowPaint = null;
        mShadowColor = new PDEColor();
        mShadowColor.setColor(PDEColor.valueOf("DTWhite").getIntegerColor());

        mPaddingLeft = 0;
        mPaddingTop = 0;
        mPaddingRight = 0;
        mPaddingBottom = 0;
        mShadowOffsetX = 0.0f;
        mShadowOffsetY = 1.0f;
        mShadowAlpha = 255;
        mShadowEnabled = false;

        refreshMetrics();

        update(true);
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Set Text.
     */
    public void setElementText(String text)
    {
        //any change?
        if (text.equals(mText)) return;

        //remember
        mText = text;

        update();
    }


    /**
     * @brief Get Text.
     */
    public String getElementText()
    {
        return mText;
    }


    /**
     * @brief Set maximum number of lines.
     */
    public void setElementMaximumLines(int lines)
    {
        //any change?
        if (lines == mMaxLines) return;
        if (lines < -1) return;
        if (lines == 0) return;

        //remember
        mMaxLines = lines;

        update();
    }


    /**
     * @brief Get maximum number of lines.
     */
    public int getElementMaximumLines() {
        return mMaxLines;
    }


    /**
     * @brief Set the typeface.
     *
     * @param typeface
     */
    public void setElementTypeface(PDETypeface typeface){
        // security
        if (typeface == null) return;

        // check for same value
        if (mTypeface == typeface) return;

        mTypeface = typeface;

        //update for paints necessary
        createTextPaint();
        createShadowPaint();

        update();
    }


    /**
     * @brief Get typeface.
     */
    public PDETypeface getElementTypeface() {
        return mTypeface;
    }


    /**
     * @brief Set alignment mode.
     *
     * Possible values are Standard Mode (default), where there is a little distance over and under the text,
     * Baseline Mode where one has to set the baseline and CapHeight Mode, where there is no distance over and
     * under the text.
     */
    public void setElementAlignmentMode(PDELayerTextAlignmentMode mode) {
        // check for same value
        if (mAlignmentMode == mode) return;
        //remember
        mAlignmentMode = mode;

        //on cap height mode set top and bottom padding to 1 BU
        if (mAlignmentMode == PDELayerTextAlignmentMode.PDELayerTextAlignmentModeCapHeight) {
            setElementPaddingTop(PDEBuildingUnits.BU());
            setElementPaddingBottom(PDEBuildingUnits.BU());
        }

        refreshMetrics();

        //update
        update();
    }


    /**
     * @brief Get alignment mode.
     */
    public PDELayerTextAlignmentMode getAlignmentMode() {
        return mAlignmentMode;
    }


    /**
     *  @brief Set text size in pixel.
     * @param size size of the text in pixel units
     */
    public void setElementTextSize(float size) {
        if (mTextSize == size) return;

        mTextSize = size;

        createTextPaint();
        createShadowPaint();
        update();
    }


    /**
     * @brief Get text size in pixel
     */
    public float getElementTextSize() {
        return mTextSize;
    }


    /**
     * @brief Set Distance between lines by factor, standard is 1
     *
     * Standard is 0.2*text height, so if set to 2, distance is 0.4*text height
     */
    public void setElementLineDistanceFactor(float distancefactor) {
        if (mLineDistanceFactor == distancefactor) return;
        if (distancefactor <0 ) return;

        mLineDistanceFactor = distancefactor;
        update();
    }

    /**
     * @brief Return distance factor between lines
     */
    public float getElementDistanceFactor() {
        return mLineDistanceFactor;
    }


    /**
     * @brief Set all paddings
     */
    public void setElementPaddingAll(int padding) {
        //any change?
        if (padding == mPaddingLeft && padding == mPaddingTop && padding == mPaddingRight && padding == mPaddingBottom) {
            return;
        }

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
     * @brief Set baseline
     */
    public void setElementBaseLine(int baseline) {
        //any change?
        if (baseline == mBaseLine) return;
        //remember
        mBaseLine = baseline;
        //redraw
        update();
    }


    /**
     * @brief Get set baseline
     */
    public int getElementSetBaseLine() {
        return mBaseLine;
    }


    /**
     * @brief Get baseline, resulting from set baseline, padding and vertical aligment
     */
    public float getElementInternalBaseLine() {
        return mInternalBaseLine;
    }


    /**
     * @brief Get text height
     */
    public float getElementTextHeight() {
        return mTextHeight;
    }


    /**
     * @brief Get text offset
     */
    public float getTextOffset() {
        return mTextOffset;
    }


    /**
     * @brief Set Text Color
     */
    public void setElementTextColor(PDEColor color) {
        //any change?
        if (color == mTextColor) return;
        //remember
        mTextColor = color;
        createTextPaint();
        //redraw
        update();
    }


    /**
     * @brief Get text Color
     */
    public PDEColor getElementTextColor() {
        return mTextColor;
    }


    /**
     * @brief Set background color
     */
    public void setElementBackgroundColor(PDEColor color) {
        //any change?
        if (color == mBackgroundColor) return;
        //remember
        mBackgroundColor = color;
        createBackgroundPaint();
        //redraw
        update();
    }


    /**
     * @brief Get background color
     */
    public PDEColor getElementBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * @brief Set shadow color
     */
    public void setElementShadowColor(PDEColor color) {
        //any change?
        if (color == mShadowColor) return;
        //remember
        mShadowColor = color;
        createShadowPaint();
        //redraw
        update();
    }


    /**
     * @brief Get shadow color
     */
    public PDEColor getElementShadowColor() {
        return mShadowColor;
    }

    /**
     * @brief Enable shadow
     */
    public void setElementShadowEnabled(boolean enabled) {
        //any change?
        if (enabled == mShadowEnabled) return;
        //remember
        mShadowEnabled = enabled;
        createShadowPaint();
        //redraw
        update();
    }

    /**
     * @brief Get if shadow is enabled
     */
    public boolean getElementShadowEnabled() {
        return mShadowEnabled;
    }


    /**
     * @brief Set shadow x offset
     */
    public void setElementShadowXOffset(float offset) {
        //any change?
        if (offset == mShadowOffsetX) return;
        //remember
        mShadowOffsetX = offset;
        //redraw
        update();
    }

    /**
     * @brief Get shadow x offset
     */
    public float getElementShadowXOffset() {
        return mShadowOffsetX;
    }


    /**
     * @brief Set shadow y offset
     */
    public void setElementShadowYOffset(float offset) {
        //any change?
        if (offset == mShadowOffsetY) return;
        //remember
        mShadowOffsetY = offset;
        //redraw
        update();
    }

    /**
     * @brief Get shadow y offset
     */
    public float getElementShadowYOffset() {
        return mShadowOffsetY;
    }

    /**
     * @brief Set shadow alpha
     */
    public void setElementShadowAlpha(float alpha) {
        //any change?
        if (alpha == mShadowAlpha) return;
        //remember
        mShadowAlpha = alpha;
        //redraw
        update();
    }

    /**
     * @brief Get shadow alpha
     */
    public float getElementShadowAlpha() {
        return  mShadowAlpha;
    }

    /**
     * @brief Set ellipsize
     * @param ellipsize
     */
    public void setElementEllipsize (boolean ellipsize) {
        //any change?
        if (ellipsize == mEllipsize) return;
        //remember
        mEllipsize = ellipsize;
        //redraw
        update();
    }

    /**
     * @brief Get ellipsize
     */
    public boolean getElementEllipsize() {
        return mEllipsize;
    }

    /**
     * @brief Set horizontal alignment
     */
    public void setElementHorizontalAlignment(PDEConstants.PDEAlignment alignment) {
        //any change?
        if (alignment == mHorizontalAlignment) return;
        //remember
        mHorizontalAlignment = alignment;
        //redraw
        update();
    }

    /**
     * @brief Get horizontal alignment
     */
    public PDEConstants.PDEAlignment getElementHorizontalAlignment() {
        return mHorizontalAlignment;
    }

    /**
     * @brief Set vertical alignment
     */
    public void setElementVerticalAlignment(PDEConstants.PDEVerticalAlignment alignment) {
        //any change?
        if (alignment == mVerticalAlignment) return;
        //remember
        mVerticalAlignment = alignment;
        //redraw
        update();
    }

    /**
     * @brief Get vertical alignment
     */
    public PDEConstants.PDEVerticalAlignment getElementVerticalAlignment() {
        return mVerticalAlignment;
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- layout / sizing ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


//    /**
//     * @brief Set Element Height
//     */
//   public void setElementLayoutHeight(int height)
//    {
//        setElementLayoutSize(Math.round(height*getAspectRatio()), height);
//    }
//
//
//    /**
//     * @brief Set Element Width
//     */
//    public void setElementLayoutWidth (int width)
//    {
//        setElementLayoutSize(width, Math.round(width/getAspectRatio()));
//    }
//
//
//    /**
//     * @brief Set Size
//     */
//    public void setElementLayoutSize (int iconWidth, int iconHeight)
//    {
//        Rect bounds = getBounds();
//        setBounds(bounds.left, bounds.top, bounds.left + iconWidth, bounds.top + iconHeight);
//    }
//
//
//    /**
//     * @brief Set Offset
//     */
//    public void setElementLayoutOffset(int x, int y)
//    {
//        Rect bounds = getBounds();
//        setBounds(bounds.left + x, bounds.top + y, bounds.right + x, bounds.bottom + y);
//    }

    /**
     * @brief Set the offest of the layer.
     *
     * @param offset The new offset of the element.
     */
    public void setLayoutOffset(Point offset) {
        // get current bounds
        Rect bounds = getBounds();

        // anything to do?
        if (offset.x == bounds.left && offset.y == bounds.top) {
            return;
        }

        // remember
        setBounds(offset.x, offset.y, offset.x + bounds.width(), offset.y + bounds.height());
    }


    /**
     * @brief Set the layout of the layer.
     *
     * Sets the offset and size by the rect values
     *
     * @param rect The new layout rect of the element.
     */
    public void setLayoutRect(Rect rect) {
        setLayoutOffset(new Point(rect.left, rect.top));
        setLayoutSize(new Point(rect.right, rect.bottom));
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- Helpers ------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief update background and border paint values
     */
    protected void updateAllPaints() {
        createTextPaint();
        createBackgroundPaint();
        createShadowPaint();
        update();
    }


    /**
     * @brief create text paint
     */
    private void createTextPaint() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColorFilter(mColorFilter);
        mTextPaint.setDither(mDither);
        mTextPaint.setColor(mTextColor.newIntegerColorWithCombinedAlpha(mAlpha));
        if (mTypeface == null) {
            mTextPaint.setTypeface(PDETypeface.sDefaultFont.getTypeface());
        } else {
            mTextPaint.setTypeface(mTypeface.getTypeface());
        }
        mTextPaint.setTextSize(mTextSize);

        refreshMetrics();
    }


    /**
     * @brief create background paint
     */
    private void createBackgroundPaint() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColorFilter(mColorFilter);
        mBackgroundPaint.setDither(mDither);
        mBackgroundPaint.setColor(mBackgroundColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }

    /**
     * @brief create shadow paint
     */
    private void createShadowPaint() {
        if (mShadowAlpha > 0.0f) {
            mShadowPaint = new Paint();
            mShadowPaint.setAntiAlias(true);
            mShadowPaint.setColorFilter(mColorFilter);
            mShadowPaint.setDither(mDither);
            mShadowPaint.setColor(mShadowColor.newIntegerColorWithCombinedAlpha(mAlpha));
            if (mTypeface == null) {
                mShadowPaint.setTypeface(PDETypeface.sDefaultFont.getTypeface());
            } else {
                mShadowPaint.setTypeface(mTypeface.getTypeface());
            }
            mShadowPaint.setTextSize(mTextSize);
        } else {
            mShadowPaint = null;
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
        //text paint and metrics parameters
        refreshMetrics();

        Rect originalbounds = new Rect(Math.round(mPixelShift), Math.round(mPixelShift),
                Math.round(bounds.width()-mPixelShift), Math.round(bounds.height()-mPixelShift));

        Rect newbounds;
        mBoundsWidth = originalbounds.width();
        mBoundsHeight = originalbounds.height();

        // security
        if (originalbounds.width()<=0 || originalbounds.height() <= 0) return;

        //draw background
        if (mBackgroundPaint != null) {
            c.drawRect(new Rect(0,0, mBoundsWidth, mBoundsHeight),mBackgroundPaint);
        }

        //create bounds with padding
        newbounds = new Rect(originalbounds.left+mPaddingLeft, originalbounds.top+mPaddingTop,
                originalbounds.right-mPaddingRight, originalbounds.bottom-mPaddingBottom);

        // security
        if (newbounds.width()<=0 || newbounds.height() <= 0) return;

        mBoundsLeft = -newbounds.left+2*mPaddingLeft;

        //draw text based on alignment
        if (mAlignmentMode != PDELayerTextAlignmentMode.PDELayerTextAlignmentModeBaseLine){
            //draw text for standard and capheight alignment mode
            //draws text and text shadow on top if vertical alignment is top
            if (mVerticalAlignment == PDEConstants.PDEVerticalAlignment.PDEAlignmentTop) {
                if (mShadowEnabled) drawMultilineText(mText, mBoundsLeft+mShadowOffsetX, mMetricsTopDistance +mShadowOffsetY,
                        c, newbounds, mShadowPaint, true);

                mTextHeight = drawMultilineText(mText, mBoundsLeft, mMetricsTopDistance, c, newbounds, mTextPaint, true);

            } else {
                //if vertical alignment is not top, first text height is calculated
                mTextHeight = drawMultilineText(mText, mBoundsLeft, mMetricsTopDistance, c, newbounds, mTextPaint, false);

                //draws text in center
                if (mVerticalAlignment == PDEConstants.PDEVerticalAlignment.PDEAlignmentVerticalCenter) {
                    mOffset = mBoundsHeight -mTextHeight-(mBoundsHeight -mTextHeight)/2+mCapHeight;
                    mTextOffset = mOffset - mCapHeight;
                    mInternalBaseLine = mOffset;

                    if (mShadowEnabled) drawMultilineText(mText, mBoundsLeft+mShadowOffsetX, mOffset+mShadowOffsetY,
                            c, newbounds, mShadowPaint, true);

                    drawMultilineText(mText, mBoundsLeft, mOffset, c, newbounds, mTextPaint, true);
                }

                //draws text at bottom
                if (mVerticalAlignment == PDEConstants.PDEVerticalAlignment.PDEAlignmentBottom) {
                    mOffset = mBoundsHeight -mTextHeight + mCapHeight -mPaddingBottom;
                    mTextOffset = mOffset - mCapHeight;
                    mInternalBaseLine = mBoundsHeight-mPaddingBottom;

                    if (mShadowEnabled) drawMultilineText(mText, mBoundsLeft+mShadowOffsetY, mOffset+mShadowOffsetY,
                            c, newbounds, mShadowPaint, true);

                    drawMultilineText(mText, mBoundsLeft, mOffset, c, newbounds, mTextPaint, true);
                }
            }
        } else {
            //draw for BaseLine alignment mode
            mOffset = 0;

            //define offset based on alignment, baseline and padding
            if (mVerticalAlignment == PDEConstants.PDEVerticalAlignment.PDEAlignmentTop) {
                newbounds = new Rect(newbounds.left, newbounds.top, newbounds.right, Math.round(newbounds.bottom-mBaseLine+mCapHeight));
                mOffset = mBaseLine+mPaddingTop;
                mInternalBaseLine = mOffset;
            } else if (mVerticalAlignment == PDEConstants.PDEVerticalAlignment.PDEAlignmentVerticalCenter) {
                mOffset = mBaseLine + mBoundsHeight /2;
                newbounds = new Rect(newbounds.left, newbounds.top, newbounds.right, Math.round(newbounds.bottom-(mBaseLine + mBoundsHeight /2.0f)+mCapHeight));
                mInternalBaseLine = mOffset;
            } else if (mVerticalAlignment == PDEConstants.PDEVerticalAlignment.PDEAlignmentBottom) {
                newbounds = new Rect(newbounds.left, newbounds.top, newbounds.right, Math.round(newbounds.bottom+mBaseLine));
                mTextHeight = drawMultilineText(mText, mBoundsLeft, mMetricsTopDistance, c, newbounds, mTextPaint, false);
                mOffset = mBaseLine + mBoundsHeight -mTextHeight-mPaddingBottom;
                mInternalBaseLine = mBaseLine + mBoundsHeight-mPaddingBottom;
            }

            mTextOffset = mOffset - mCapHeight;

            //draw text
            if (mShadowEnabled) drawMultilineText(mText, mBoundsLeft+mShadowOffsetY, mOffset+mShadowOffsetY,
                    c, newbounds, mShadowPaint, true);

            drawMultilineText(mText, mBoundsLeft, mOffset, c, newbounds, mTextPaint, true);
        }
    }


    /**
     * @brief Function which draws the text in the textfield, returns height of the drawn text
     *
     * @param text text to be written
     * @param x x-position of the text
     * @param y y-position of the text
     * @param canvas canvas to be drawn on
     * @param drawSpace available space in which is drawn
     * @param draw sets if text is really drawn, if false no text is drawn, only height of the text is measured
     * @return
     */
    private int drawMultilineText(String text, float x, float y, Canvas canvas, Rect drawSpace, Paint paint, boolean draw) {
        if (paint == null) return 0;
        int linebottom, totalHeight,  lineTop, lineHeight;
        float lineWidth, lineStart;
        int lineoffset = 0;
        int linecount = 1;

        //horizontal alignment parameters
        boolean left = false;
        if (draw && mHorizontalAlignment == PDEConstants.PDEAlignment.PDEAlignmentLeft) left = true;
        boolean center = false;
        if (draw && mHorizontalAlignment == PDEConstants.PDEAlignment.PDEAlignmentCenter) center = true;
        boolean right = false;
        if (draw && mHorizontalAlignment == PDEConstants.PDEAlignment.PDEAlignmentRight) right = true;

        //splits text in array of single words
        String[] lines = text.split(" ");

        // set line height
        lineHeight = Math.round(-mMetrics.ascent) + Math.round(mMetrics.descent);
        linebottom = lineHeight-mCapHeight;

        //set line distance based on linedistancefactor, when linedistancefactor is 1 standard line distance is used
        lineHeight = lineHeight - Math.round(1 - mLineDistanceFactor)*linebottom;

        lineTop = 0;
        //if Alignment Mode is standard, distance over and below the text must be added to textsize,
        //distance below is given by mMetricsBottomDistance, distance above by lineTop
        if (mAlignmentMode == PDELayerTextAlignmentMode.PDELayerTextAlignmentModeStandard) {
            lineTop = Math.round(-mMetrics.top)-mCapHeight;
        }

        // draws text, word for word
        String line = "";
        for (int i = 0; i < lines.length; ++i) {
            if (i==0) {
                //starts at first word
                line = lines[i];
            } else if(calculateTextWidth(line + " " + lines[i], paint) <= drawSpace.width()){
                //adds one more word if there is enough space in line
                line = line + " " + lines[i];
            } else if (calculateTextWidth(line, paint) <= drawSpace.width()) {
                //draws words if line is full
                linecount++;

                //if there is no space for a another line writes line and returns
                if ((lineHeight * linecount) - linebottom + mMetricsBottomDistance > drawSpace.height() || (mMaxLines != -1 && linecount > mMaxLines)) {
                    //adds next word, which gets ellipsized, but prevents that whole word is cut off instead of using
                    //remaining available space
                    line = line + " " + lines[i];

                    //write
                    if (left) canvas.drawText(getEllipsisString(line, drawSpace.width(), paint), x, y + lineoffset, paint);

                    if (center || right) {
                        lineWidth = calculateTextWidth(getEllipsisString(line, drawSpace.width(), paint), paint);
                        if (center) {
                            lineStart = (drawSpace.width() - lineWidth)/2.0f;
                        } else {
                            lineStart = drawSpace.width() - lineWidth;
                        }
                        canvas.drawText(getEllipsisString(line, drawSpace.width(), paint), x + lineStart, y + lineoffset, paint);
                    }

                    totalHeight = (linecount-2)*lineHeight + mCapHeight + mMetricsBottomDistance + lineTop;
                    return totalHeight;
                } else {
                    //writes line and goes to new line
                    if (!line.equals("")){
                        if (left) canvas.drawText(line, x, y + lineoffset, paint);

                        if (center || right) {
                            lineWidth = calculateTextWidth(line, paint);
                            if (center) {
                                lineStart = (drawSpace.width() - lineWidth)/2.0f;
                            } else {
                                lineStart = drawSpace.width() - lineWidth;
                            }
                            canvas.drawText(line, x + lineStart, y + lineoffset, paint);
                        }

                        lineoffset = lineoffset + lineHeight;
                    }
                    line = lines[i];
                }
            } else {
                // if line is too narrow for a whole word, writes single chars and linewraps inbetween word
                String word = "";
                for (int j = 0; j < line.length(); ++j) {
                    //adds another char if there is enough space
                    if (word.length() == 0 || calculateTextWidth(word + line.charAt(j), paint) <= drawSpace.width()) {
                        word = word + line.charAt(j);
                    } else {
                        //writes chars
                        linecount++;

                        if ((lineHeight * linecount) - linebottom + mMetricsBottomDistance > drawSpace.height() || (mMaxLines != -1 && linecount > mMaxLines)) {
                            //writes chars and returns if there is no space for a new line
                            if (left) canvas.drawText(getEllipsisString(word, drawSpace.width(), paint), x, y + lineoffset, paint);

                            if (center || right) {
                                lineWidth = calculateTextWidth(getEllipsisString(word, drawSpace.width(), paint), paint);
                                if (center) {
                                    lineStart = (drawSpace.width() - lineWidth)/2.0f;
                                } else {
                                    lineStart = drawSpace.width() - lineWidth;
                                }
                                canvas.drawText(getEllipsisString(word, drawSpace.width(), paint), x + lineStart, y + lineoffset, paint);
                            }

                            totalHeight = (linecount-2)*lineHeight + mCapHeight + mMetricsBottomDistance + lineTop;
                            return totalHeight;
                        } else {
                            //writes chars and goes to new line
                            if (left) canvas.drawText(word, x, y + lineoffset, paint);

                            if (center || right) {
                                lineWidth = calculateTextWidth(word, paint);
                                if (center) {
                                    lineStart = (drawSpace.width() - lineWidth)/2.0f;
                                } else {
                                    lineStart = drawSpace.width() - lineWidth;
                                }
                                canvas.drawText(word, x + lineStart, y + lineoffset, paint);
                            }

                            lineoffset = lineoffset + lineHeight;
                            word = "" + line.charAt(j);
                        }
                    }
                }

                //add remaining chars to new line
                if (calculateTextWidth(word + " " + lines[i], paint) > drawSpace.width()) {
                    //writes only remaining chars, if not enough space for next word is available
                    linecount++;

                    if ((lineHeight * linecount) - linebottom + mMetricsBottomDistance > drawSpace.height() || (mMaxLines != -1 && linecount > mMaxLines)) {
                        //returns if there is no more space for another line
                        if (left) canvas.drawText(getEllipsisString(word, drawSpace.width(), paint), x, y + lineoffset, paint);

                        if (center || right) {
                            lineWidth = calculateTextWidth(getEllipsisString(word, drawSpace.width(), paint), paint);
                            if (center) {
                                lineStart = (drawSpace.width() - lineWidth)/2.0f;
                            } else {
                                lineStart = drawSpace.width() - lineWidth;
                            }
                            canvas.drawText(getEllipsisString(word, drawSpace.width(), paint), x + lineStart, y + lineoffset, paint);
                        }

                        totalHeight = (linecount-2)*lineHeight + mCapHeight + mMetricsBottomDistance + lineTop;
                        return totalHeight;
                    } else {
                        //writes chars and goes to next line
                        if (left) canvas.drawText(word, x, y + lineoffset, paint);

                        if (center || right) {
                            lineWidth = calculateTextWidth(word, paint);
                            if (center) {
                                lineStart = (drawSpace.width() - lineWidth)/2.0f;
                            } else {
                                lineStart = drawSpace.width() - lineWidth;
                            }
                            canvas.drawText(word, x + lineStart, y + lineoffset, paint);
                        }

                        lineoffset = lineoffset + lineHeight;
                        line = lines[i];
                    }

                } else {
                    //adds next word if enough space is available
                    line = word + " " + lines[i];
                }
            }
        }

        //draws remaining text (last word)
        //tests if last word fits in line, else writes single chars
        if (calculateTextWidth(line, paint) <= drawSpace.width())
        {
            //writes line
            if (left) canvas.drawText(line, x, y + lineoffset, paint);

            if (center || right) {
                lineWidth = calculateTextWidth(line, paint);
                if (center) {
                    lineStart = (drawSpace.width() - lineWidth)/2.0f;
                } else {
                    lineStart = drawSpace.width() - lineWidth;
                }
                canvas.drawText(line, x + lineStart, y + lineoffset, paint);
            }
        } else {
            //if line does not fit, writes as many single chars as fit, then writes remaining chars in next lines
            String word = "";

            for (int j = 0; j < line.length(); ++j) {
                //adds another char if there is enough space
                if (word.length() == 0 || calculateTextWidth(word + line.charAt(j), paint) <= drawSpace.width()) {
                    word = word + line.charAt(j);
                } else {
                    //writes chars
                    linecount++;

                    if ((lineHeight * linecount) - linebottom + mMetricsBottomDistance > drawSpace.height() || (mMaxLines != -1 && linecount > mMaxLines)) {
                        //writes chars and returns if there is no space for a new line
                        if (left) canvas.drawText(getEllipsisString(word, drawSpace.width(), paint), x, y + lineoffset, paint);

                        if (center || right) {
                            lineWidth = calculateTextWidth(getEllipsisString(word, drawSpace.width(), paint), paint);
                            if (center) {
                                lineStart = (drawSpace.width() - lineWidth)/2.0f;
                            } else {
                                lineStart = drawSpace.width() - lineWidth;
                            }
                            canvas.drawText(getEllipsisString(word, drawSpace.width(), paint), x + lineStart, y + lineoffset, paint);
                        }

                        totalHeight = (linecount-2)*lineHeight + mCapHeight + mMetricsBottomDistance + lineTop;
                        return totalHeight;
                    } else {
                        //writes chars and goes to new line
                        if (left) canvas.drawText(word, x, y + lineoffset, paint);

                        if (center || right) {
                            lineWidth = calculateTextWidth(word, paint);
                            if (center) {
                                lineStart = (drawSpace.width() - lineWidth)/2.0f;
                            } else {
                                lineStart = drawSpace.width() - lineWidth;
                            }
                            canvas.drawText(word, x + lineStart, y + lineoffset, paint);
                        }

                        lineoffset = lineoffset + lineHeight;
                        word = "" + line.charAt(j);
                    }
                }
            }

            //writes remaining chars
            if (left) canvas.drawText(word, x, y + lineoffset, paint);

            if (center || right) {
                lineWidth = calculateTextWidth(word, paint);
                if (center) {
                    lineStart = (drawSpace.width() - lineWidth)/2.0f;
                } else {
                    lineStart = drawSpace.width() - lineWidth;
                }
                canvas.drawText(word, x + lineStart, y + lineoffset, paint);
            }
        }

        totalHeight = (linecount - 1)*lineHeight + mCapHeight + mMetricsBottomDistance + lineTop;
        return totalHeight;
    }


    /**
     * @brief Help function to determine text width of a given text, depending on text paint
     */
    private float calculateTextWidth(String text, Paint paint) {
        return paint.measureText(text);
    }


    /**
     * @brief Help function to determine text height of a given text, depending on text paint
     */
    private float calculateTextHeight(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return (float) Math.ceil( bounds.height());
    }

    /**
     * @brief Help function to get ellipsized string of a given text, fitting in a given width
     *
     * If ellipsize is enabled mEllipsize string is added to input string.
     */
    private String getEllipsisString(String input, float width, Paint paint) {
        if (input.length() == 0) return "";
        String output;

        if (mEllipsize) {
            output = input + EllipsizeString;
            if (calculateTextWidth(output,paint) <= width) return output;

            while (output.length() > 1 && calculateTextWidth(output, paint) > width) {
                if (input.length() > 1) {
                    input = input.substring(0, input.length() - 1);
                    output = input + EllipsizeString;
                } else if (input.length() == 1) {
                    output = EllipsizeString;
                    input = "";
                } else {
                    output = output.substring(0, output.length() - 1);
                }
            }
        } else {
            output = input;
            while (output.length() > 1 && calculateTextWidth(output,paint) > width) {
                output = output.substring(0, output.length()-1);
            }
        }
        return output;
    }


    /**
     * @brief Returns necessary height for text, based on given width
     *
     * Caution, paddings are not considered in this calculation, for text height with paddings use
     * getElementHeightForWidth(String text, float width)
     */
    public float getTextHeightForWidth(String text, float width) {
        Rect bounds = new Rect(0,0,Math.round(width),10000);
        float boundsleft = 0;

        refreshMetrics();

        //get height from draw function
        return drawMultilineText(text, boundsleft, mMetricsTopDistance, null, bounds, mTextPaint, false);
    }

    /**
     * @brief Returns necessary height of element, which consists of text height and paddings
     */
    public float getElementHeightForWidth(String text, float width) {
        //1.0f is added because of pixel shift in PDELayerText, which reduces the bounds by 1 pixel
        return mPaddingTop + getTextHeightForWidth(text, width - mPaddingLeft - mPaddingRight) + mPaddingBottom +1.0f;
    }


    /**
     * @brief returns text width for given text, typeface and fontsize
     */
    public float getTextWidth(String text, PDETypeface font, float fontsize) {
        Paint testpaint = new Paint();
        if (font == null) {
            testpaint.setTypeface(PDETypeface.sDefaultFont.getTypeface());
        } else {
            testpaint.setTypeface(font.getTypeface());
        }
        testpaint.setTextSize(fontsize);

        return calculateTextWidth(text, testpaint);
    }


    /**
     * @brief returns text width for given text
     */
    public float getTextWidth(String text) {
        refreshMetrics();
        return calculateTextWidth(text, mTextPaint);
    }


    /**
     * @brief returns text width for text
     */
    public float getTextWidth() {
        refreshMetrics();
        return calculateTextWidth(mText, mTextPaint);
    }


    /**
     * @brief get width of element, meaning width of text plus padding left and right
     */
    public float getElementWidth() {
        //1.0f is added because of pixel shift in PDELayerText, which reduces the bounds by 1 pixel
        return mPaddingLeft + getTextWidth() + mPaddingRight  + 1.0f;
    }


    /**
     * @brief get width of element for given text, meaning width of text plus padding left and right
     */
    public float getElementWidth(String text) {
        //1.0f is added because of pixel shift in PDELayerText, which reduces the bounds by 1 pixel
        return mPaddingLeft + getTextWidth(text) + mPaddingRight  + 1.0f;
    }


    /**
     * @brief Returns height of a line
     */
    @Override
    public int getIntrinsicHeight() {
        int ret;
        Double d;

        if (mAlignmentMode == PDELayerTextAlignmentMode.PDELayerTextAlignmentModeStandard) {
            Paint.FontMetrics metrics;
            metrics = mTextPaint.getFontMetrics();
            d = Math.ceil(metrics.top);
            ret = d.intValue();
            d = Math.ceil(metrics.bottom);
            ret += d.intValue();
        } else {
            d = Math.ceil(calculateTextHeight("F", mTextPaint));
            ret = d.intValue();
        }
        return ret;
    }


    /**
     * @brief Returns width of text in one line

    @Override
    public int getIntrinsicWidth() {
        Double d = Math.ceil(calculateTextWidth(mText, mTextPaint));
        return d.intValue();
    }
     */

    /**
     * @brief Help function to refresh metrics
     */
    private void refreshMetrics() {
        mMetrics = mTextPaint.getFontMetrics();
        mCapHeight = Math.round(calculateTextHeight("F", mTextPaint));

        if (mAlignmentMode == PDELayerTextAlignmentMode.PDELayerTextAlignmentModeStandard) {
            mMetricsTopDistance = Math.round(-mMetrics.top)+mPaddingTop;
            mMetricsBottomDistance =  Math.round(mMetrics.bottom);
            mTextOffset = mMetricsTopDistance - mCapHeight;
            mInternalBaseLine = mMetricsTopDistance;
        } else if (mAlignmentMode == PDELayerTextAlignmentMode.PDELayerTextAlignmentModeCapHeight) {
            mMetricsTopDistance = mCapHeight + mPaddingTop;
            mMetricsBottomDistance =  0;
            mTextOffset = mPaddingTop;
            mInternalBaseLine = mMetricsTopDistance;
        } else if (mAlignmentMode == PDELayerTextAlignmentMode.PDELayerTextAlignmentModeBaseLine) {
            mMetricsTopDistance = mCapHeight + mPaddingTop;
            mMetricsBottomDistance =  0;
            mTextOffset = mBaseLine+mPaddingTop - mCapHeight;
            mInternalBaseLine = mBaseLine+mPaddingTop;
        }

    }
}




