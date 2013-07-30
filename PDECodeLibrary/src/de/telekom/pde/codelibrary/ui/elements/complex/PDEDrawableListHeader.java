/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.complex;


import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.drawables.PDEDrawableMultilayer;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableDelimiter;
import de.telekom.pde.codelibrary.ui.elements.text.PDELayerText;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontMetricsHolder;
import de.telekom.pde.codelibrary.ui.helpers.PDEString;

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableListHeader
//----------------------------------------------------------------------------------------------------------------------


public class PDEDrawableListHeader extends PDEDrawableMultilayer {


    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEDrawableListHeader.class.getName();

    private final static boolean DEBUG = false;

//-----  constants ---------------------------------------------------------------------------------------------------

    public static final float PDEDrawableListHeaderFontSizeMainLabelInPercent = 133.0f;
    public static final float PDEDrawableListHeaderFontSizeSubLabelInPercent = 116.0f;

//-----  properties ---------------------------------------------------------------------------------------------------
    // colors
    protected PDEColor mElementBackgroundColor;

    protected PDEConstants.PDEAlignment mElementAlignment;

    protected PDELayerText mElementMainLabel;
    protected PDELayerText mElementSubLabel;
    protected PDEDrawableDelimiter mElementDelimiter;

    private int mHorizontalMargin;
    private int mVerticalMargin;
    private int mLabelPaddingHorizontal;
    private int mDelimiterHeight;
    private Paint.FontMetrics mFontMetricsMainLabel;
    private Paint.FontMetrics mFontMetricsSubLabel;
    private float mTextWidthMainLabel;
    private float mTextWidthSubLabel;


    /**
     * @brief Constructor
     */
    public PDEDrawableListHeader() {
        // init sublayers
        initLayers();

        // init distances to PDE defaults
        mHorizontalMargin = PDEBuildingUnits.pixelFromBU(0.7f);
        mLabelPaddingHorizontal = PDEBuildingUnits.oneFourthBU();
        mVerticalMargin = PDEBuildingUnits.oneHalfBU();
        mDelimiterHeight = 1;

        // init main label to PDE defaults
        mElementMainLabel.setElementText("");
        mElementMainLabel.setElementHorizontalAlignment(PDEConstants.PDEAlignment.PDEAlignmentLeft);
        mElementMainLabel.setElementTextColor(PDEColor.valueOf("DTGrey5"));
        mElementMainLabel.setElementEllipsize(true);
        mElementMainLabel.setLayoutOffset(0,0);
        mElementMainLabel.setLayoutSize(0,0);
        mElementMainLabel.setElementTextSize(
                PDEFontHelpers.calculateFontSizeByPercent(mElementMainLabel.getElementTypeface(),
                                                          PDEDrawableListHeaderFontSizeMainLabelInPercent));
        mElementMainLabel.setElementPaddingAll(mLabelPaddingHorizontal,0,mLabelPaddingHorizontal,0);
        mFontMetricsMainLabel = PDEFontHelpers.getFontMetrics(mElementMainLabel.getElementTypeface(),
                                                              mElementMainLabel.getElementTextSize());
        mTextWidthMainLabel = mElementMainLabel.getTextWidth();

        // init sub label to PDE defaults
        mElementSubLabel.setElementHorizontalAlignment(PDEConstants.PDEAlignment.PDEAlignmentLeft);
        mElementSubLabel.setElementTextColor(PDEColor.valueOf("DTGrey140"));
        mElementSubLabel.setElementEllipsize(true);
        mElementSubLabel.setElementText("");
        mElementSubLabel.setElementTextSize(
                PDEFontHelpers.calculateFontSizeByPercent(mElementSubLabel.getElementTypeface(),
                                                           PDEDrawableListHeaderFontSizeSubLabelInPercent));
        mElementSubLabel.setElementPaddingAll(mLabelPaddingHorizontal,0,mLabelPaddingHorizontal,0);
        mFontMetricsSubLabel = PDEFontHelpers.getFontMetrics(mElementSubLabel.getElementTypeface(),
                                                             mElementSubLabel.getElementTextSize());
        mTextWidthSubLabel = mElementSubLabel.getTextWidth();

        // init delimiter to PDE defaults
        mElementDelimiter.setElementBackgroundColor(PDEColor.valueOf("DTGrey1"));

        // init backgroundcolor and alignment
        mElementBackgroundColor = PDEColor.valueOf("DTTransparentBlack");
        mElementAlignment = PDEConstants.PDEAlignment.PDEAlignmentCenter;

        // ensure all layout parameters are initialized once with valid values
        doLayout();
    }


    /**
     * @brief Create and add all sublayers.
     */
    protected void initLayers() {
       // create
        mElementMainLabel = new PDELayerText("");
        mElementSubLabel = new PDELayerText("");
        mElementDelimiter = new PDEDrawableDelimiter();

        // add
        addLayer(mElementDelimiter);
        addLayer(mElementMainLabel);
        addLayer(mElementSubLabel);
    }


    /**
     * @brief Set text of main label.
     *
     * @param text The new text of the main label.
     */
    public void setElementText(String text){
        // any change?
        if (text == mElementMainLabel.getElementText()) {
            return;
        }

        // set text to element
        mElementMainLabel.setElementText(text);

        // get new text width
        mTextWidthMainLabel = mElementMainLabel.getTextWidth();

        // update
        doLayout();
    }

    /**
     * @brief Get text of main label.
     *
     * @return text of main label.
     */
    public String getElementText() {
        return mElementMainLabel.getElementText();
    }

    /**
     * @brief Set text of sub label.
     *
     * @param text The new text of the sub label.
     */
    public void setElementSubText(String text) {
        // any change?
        if (text == mElementSubLabel.getElementText()) {
            return;
        }

        // set the new text to the label
        mElementSubLabel.setElementText(text);

        // get new text width
        mTextWidthSubLabel = mElementSubLabel.getTextWidth();

        // update
        doLayout();
    }


    /**
     * @brief Get text of sub label.
     *
     * @return text of sub label.
     */
    public String getElementSubText() {
        return mElementSubLabel.getElementText();
    }


    /**
     * @brief Set fill (background) color.
     *
     * @param color The new backgroundcolor of the list header. Use null for transparent background (default).
     */
    public void setElementBackgroundColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementBackgroundColor.getIntegerColor()) {
            return;
        }

        // remember
        mElementBackgroundColor = color;

        // apply
        setMultilayerBackgroundColor(mElementBackgroundColor);
    }

    /**
     * @brief Get background color.
     */
    public PDEColor getElementBackgroundColor() {
        return mElementBackgroundColor;
    }


    /**
     * @brief Set (horizontal) alignment of the text-labels
     *
     * This (horizontal) alignment setting applies for both labels at once. They're always shown
     * next to each other (if there is set text for both). It's not intended to align them
     * independently from each other.
     *
     * @param elementAlignment This constant defines the desired horizontal alignment.
     */
    public void setElementAlignment(PDEConstants.PDEAlignment elementAlignment) {
        // any change?
        if (elementAlignment == mElementAlignment) {
            return;
        }

        // remember
        mElementAlignment = elementAlignment;

        // update
        doLayout();
    }


    /**
     * @brief Get (horizontal) alignment of the text-labels
     *
     * @return horizontal alignment of the text labels
     */
    public PDEConstants.PDEAlignment getElementAlignment(){
        return mElementAlignment;
    }


    /**
     * @brief Set fill (background) color of delimiter.
     *
     * @param color The new backgroundcolor of the delimiter.
     */
    public void setDelimiterBackgroundColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementDelimiter.getElementBackgroundColor().getIntegerColor()) {
            return;
        }

        // remember
        mElementDelimiter.setElementBackgroundColor(color);

        // update
        doLayout();
    }


    /**
     * @brief Get background color of the delimiter.
     *
     * @return background color of the delimiter
     */
    public PDEColor getDelimiterBackgroundColor() {
        return mElementDelimiter.getElementBackgroundColor();
    }


    /**
     * @brief Set text color of the main text.
     *
     * @param color The new text color of the main text.
     */
    public void setElementTextColor(PDEColor color) {
        // forward value
        mElementMainLabel.setElementTextColor(color);
    }


    /**
     * @brief Get text color of the main text.
     *
     * @return text color of the main text.
     */
    public PDEColor getElementTextColor() {
        return mElementMainLabel.getElementTextColor();
    }


    /**
     * @brief Set text color of the sub text.
     *
     * @param color The new text color of the sub text.
     */
    public void setElementSubTextColor(PDEColor color)
    {
        // forward value
        mElementSubLabel.setElementTextColor(color);
    }


    /**
     * @brief Get text color of the sub text.
     *
     * @return text color of the sub text.
     */
    public PDEColor getElementSubTextColor() {
        return mElementSubLabel.getElementTextColor();
    }


    /**
     * @brief update function. Set sizes and positions of all layers
     */
    protected void doLayout() {
        // get current bounds
        Rect bounds = getBounds();
        if (DEBUG) Log.d(LOG_TAG,"bounds: left "+bounds.left+" top "+bounds.top+" right "+bounds.right+" bottom " +
                                 ""+bounds.bottom);
        // update delimiter
        mElementDelimiter.setLayoutRect(new Rect(0,bounds.height()-1,bounds.width(),bounds.height()));
        // update the text labels
        calculateLayout(bounds);
    }

    /**
     * @brief Calculate the layout settings depending on the size of the whole element.
     *
     * Depending on the size of the whole element the labels have to be positioned and in some
     * cases even abbreviated. This function does all the needed calculations and settings.
     *
     * @param bounds The bounds of the whole element.
     */
    protected void calculateLayout(Rect bounds) {
        int yPositionMain, yPositionSub, yMainBaseline, availableWidth, baselineMainOffset, baselineSubOffset;
        int remainingSpace, wantedWidthMain, wantedHeightMain, wantedWidthSub, wantedHeightSub;
        boolean mainHasText, subHasText;
        Rect paddingMain, paddingSub;

        // init
        yPositionMain = 0;
        yPositionSub = 0;
        mainHasText = false;
        subHasText = false;

        // skip if element has no height
        if (bounds.height() == 0) {
            return;
        }

        // determine which label is filled with text
        if (!PDEString.isEmpty(mElementMainLabel.getElementText())) {
            mainHasText = true;
        }
        if (!PDEString.isEmpty(mElementSubLabel.getElementText())) {
            subHasText = true;
        }

        // get padding
        paddingMain = mElementMainLabel.getElementPaddingRect();
        paddingSub = mElementSubLabel.getElementPaddingRect();

        // calculate rhe wanted sizes of the labels
        // these are the sizes we would have to show the text without cutting or abbrewiation with the current
        // settings. In the following steps we adapt these wanted sizes to the available element size.
        wantedWidthMain = Math.round(mTextWidthMainLabel) + paddingMain.left + paddingMain.right
                          + 2 * mLabelPaddingHorizontal;
        wantedHeightMain =
                Math.round(mElementMainLabel.getElementInternalBaseLine() + mFontMetricsMainLabel.descent)
                                                              + paddingMain.top + paddingMain.bottom;
        wantedWidthSub = Math.round(mTextWidthSubLabel) + paddingSub.left + paddingSub.right
                         + 2 * mLabelPaddingHorizontal;
        wantedHeightSub =
                Math.round(mElementSubLabel.getElementInternalBaseLine() + mFontMetricsSubLabel.descent)
                + paddingSub.top + paddingSub.bottom;

        // set the wanted sizes 0 if there's no text in the label
        if (!mainHasText) {
            wantedHeightMain = 0;
            wantedWidthMain = 0;
        }

        if (!subHasText){
            wantedHeightSub = 0;
            wantedWidthSub = 0;
        }

        // calculate the effective width for the labels ( without the margins)
        availableWidth = bounds.width() - 2 * mHorizontalMargin;
        if (availableWidth < 0) {
            availableWidth = 0;
        }

        // shrink the wanted widths if there's not enough space
        if (wantedWidthMain + wantedWidthSub > availableWidth){
            // check if the main label fits in easily
            if ( availableWidth - wantedWidthMain >0) {
                // we keep the complete main label, we only have to abbreviate the sub label
                wantedWidthSub = availableWidth - wantedWidthMain;
            } else if (availableWidth > 0) {
                // there's no space left for the sub label, from now on we have to abbreviate the main label
                wantedWidthSub = 0;
                wantedWidthMain = availableWidth;
            } else {
                // there's no space left at all, let both labels disappear
                wantedWidthMain = 0;
                wantedWidthSub = 0;
            }
        }

        // calculate y-offset
        if (mainHasText) {
            // the labels stick to the lower end of the element. The main label always keeps the same distance (vertical margin)
            // to the delimiter. So to get the y-offset of the main label we subtract from the lower end of the element the distances.
            yPositionMain = bounds.height() - mDelimiterHeight - mVerticalMargin - wantedHeightMain;
            if (DEBUG) Log.d(LOG_TAG, "yPosMain: "+yPositionMain+" wantedHeightMain "+wantedHeightMain);
            // is there also a sublabel?
            if (subHasText) {
                // If both labels are visible, the position of the sub-label always depends on the main-label.
                // The problem is, that we have two different font sizes for the label, but we want to have all characters
                // on the same baseline. So if we want that both baselines meet on the same level, we have to shift the
                // y-position of the sub-label so that the sub-baseline fits to the main-baseline.

                // calculate the baseline offsets of the labels
                baselineMainOffset =
                        wantedHeightMain - Math.round(mFontMetricsMainLabel.descent) - paddingMain.bottom;
                baselineSubOffset =
                        wantedHeightSub - Math.round(mFontMetricsSubLabel.descent) - paddingSub.bottom;
                // calculate the y-positions of the baselines
                yMainBaseline = yPositionMain + baselineMainOffset;
                yPositionSub = yMainBaseline - baselineSubOffset;
            }
        } else {
            // if there is only the sub-label visible, it sticks to the lower end of the element the same way as the main-label would do.
            yPositionSub = bounds.height() - mDelimiterHeight - mVerticalMargin - wantedHeightSub;
        }


        // set the offsets
        // just put the labels next to each other in x-direction. The horizontal padding is chosen wisely to take care for the needed gap.
        if (mElementAlignment == PDEConstants.PDEAlignment.PDEAlignmentLeft){
            mElementMainLabel.setLayoutOffset(mHorizontalMargin, yPositionMain);
            mElementSubLabel.setLayoutOffset(mElementMainLabel.getBounds().left + wantedWidthMain, yPositionSub);
        }
        else if (mElementAlignment == PDEConstants.PDEAlignment.PDEAlignmentCenter) {
            remainingSpace = bounds.width() - wantedWidthMain - wantedWidthSub;
            mElementMainLabel.setLayoutOffset(Math.round(remainingSpace / 2), yPositionMain);
            mElementSubLabel.setLayoutOffset(mElementMainLabel.getBounds().left + wantedWidthMain, yPositionSub);
        }
        else if (mElementAlignment == PDEConstants.PDEAlignment.PDEAlignmentRight){
            mElementSubLabel.setLayoutOffset(bounds.width() - mHorizontalMargin - wantedWidthSub, yPositionSub);
            mElementMainLabel.setLayoutOffset(mElementSubLabel.getBounds().left - wantedWidthMain, yPositionMain);
        }

        // set calculated sizes
        mElementMainLabel.setLayoutSize(wantedWidthMain,wantedHeightMain);
        mElementSubLabel.setLayoutSize(wantedWidthSub,wantedHeightSub);
    }
}
