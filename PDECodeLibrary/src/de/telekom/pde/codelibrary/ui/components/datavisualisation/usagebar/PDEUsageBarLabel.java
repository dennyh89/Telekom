/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.datavisualisation.usagebar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDETextView;
import de.telekom.pde.codelibrary.ui.elements.text.PDELayerText;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDEString;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;


/**
 * @brief Textual label of PDEUsageBar.
 *
 * The PDEUsageBar has to show some text, like the current usage value, the maximum/total usage value and the unit.
 * All this text has to be properly formated and layouted.
 * This configurable label object takes care, that the information is shown in the defined way.
 */
public class PDEUsageBarLabel extends RelativeLayout {

    @SuppressWarnings("unused")
    private final static String LOG_TAG = PDEUsageBarLabel.class.getName();

    // configurations
    protected final static int CONFIGURATION_SIDE_PADDING_FULL_BAR = 2 * PDEBuildingUnits.BU();
    protected final static int CONFIGURATION_SIDE_PADDING_SMALL_BAR = PDEBuildingUnits.BU();
    protected final static int CONFIGURATION_BOTTOM_PADDING_FULL_BAR = PDEBuildingUnits.BU();
    protected final static int CONFIGURATION_BOTTOM_PADDING_SMALL_BAR = 2 * PDEBuildingUnits.BU();
    protected final static String CONFIGURATION_DEFAULT_FILL_COLOR = "DTDVLightBlue";
    protected final static PDEUsageBar.PDEUsageBarStyle CONFIGURATION_DEFAULT_BAR_STYLE
            = PDEUsageBar.PDEUsageBarStyle.PDEUsageBarStyleFullBar;
    protected final static float CONFIGURATION_DEFAULT_FONT_SIZE_NUMBERS = PDEBuildingUnits.BU() * 2.0f;

    //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
    // --- do NOT delete this out commented code ----
    /*
    public enum PDEUsageBarLabelMaskingMode {
        PDEUsageBarLabelMaskingModeNone,
        PDEUsageBarLabelMaskingModeShow,
        PDEUsageBarLabelMaskingModeHide
    }
    */

    // colors
    protected PDEColor mTextColor;
    protected PDEColor mBarFillColor;
    protected PDEColor mTextColorCustom;
    // font sizes
    protected float mFontSizeOfNumbersTextView;
    protected float mFontSizeOfUnitTextView;
    // visual style of bar
    protected PDEUsageBar.PDEUsageBarStyle mBarStyle;
    // customization options
    protected int mNumberOfDecimalPlaces;
    protected boolean mTotalTextEnabled;
    protected boolean mUnitTextEnabled;
    // content values
    protected float mCurrentFillValue;
    protected float mTotalValue;
    protected String mUnitText;
    // text views
    protected PDETextView mNumbersTextView;
    protected PDETextView mUnitTextView;
    // sizes of the text views
    protected int mNumbersTextViewWidth;
    protected int mNumbersTextViewHeight;
    protected int mUnitTextViewWidth;
    protected int mUnitTextViewHeight;

    //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
    // --- do NOT delete this out commented code ----
    /*
    protected PDEUsageBarLabelMaskingMode mMaskingMode;
    protected Path mMaskPath;
    */


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEUsageBarLabel(Context context) {
        super(context);
        init(context, null);
    }


    /**
     * @brief Constructor.
     */
    public PDEUsageBarLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEUsageBarLabel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    /**
     * @brief Initialization
     */
    protected void init(Context context, android.util.AttributeSet attrs) {
        // general init
        mNumberOfDecimalPlaces = 0;
        mCurrentFillValue = 0.0f;
        mTotalValue = 0.0f;
        mFontSizeOfNumbersTextView = CONFIGURATION_DEFAULT_FONT_SIZE_NUMBERS;
        mFontSizeOfUnitTextView = mFontSizeOfNumbersTextView / 2.0f;
        mBarStyle = CONFIGURATION_DEFAULT_BAR_STYLE;
        mTextColorCustom = null;
        mBarFillColor = PDEColor.valueOf(CONFIGURATION_DEFAULT_FILL_COLOR);
        mTotalTextEnabled = true;
        mUnitTextEnabled = true;
        mUnitText = "";

        // create and configure the text view that shows the numbers
        mNumbersTextView = new PDETextView(context, attrs);
        mNumbersTextView.setVerticalAlignment(PDEConstants.PDEVerticalAlignment.PDEAlignmentBottom);
        mNumbersTextView.setAlignmentMode(PDELayerText.PDELayerTextAlignmentMode.PDELayerTextAlignmentModeCapHeight);
        mNumbersTextView.setTextSize(PDEFontHelpers.calculateFontSize(mNumbersTextView.getTypeface(),
                                                                      mFontSizeOfNumbersTextView));
        if (mBarStyle == PDEUsageBar.PDEUsageBarStyle.PDEUsageBarStyleFullBar) {
            mNumbersTextView.setPaddingAll(CONFIGURATION_SIDE_PADDING_FULL_BAR, mNumbersTextView.getPaddingTop(),
                                           mNumbersTextView.getPaddingRight(), CONFIGURATION_BOTTOM_PADDING_FULL_BAR);
        } else {
            mNumbersTextView.setPaddingAll(CONFIGURATION_SIDE_PADDING_SMALL_BAR, mNumbersTextView.getPaddingTop(),
                                           mNumbersTextView.getPaddingRight(), CONFIGURATION_BOTTOM_PADDING_SMALL_BAR);
        }
        mNumbersTextView.setEllipsize(true);
        mNumbersTextView.setMaxLines(1);
        mNumbersTextView.setId(R.id.pde_usage_bar_numbers_text_view);

        // create and configure the text view that shows the unit
        mUnitTextView = new PDETextView(context, attrs);
        mUnitTextView.setVerticalAlignment(PDEConstants.PDEVerticalAlignment.PDEAlignmentBottom);
        mUnitTextView.setAlignmentMode(PDELayerText.PDELayerTextAlignmentMode.PDELayerTextAlignmentModeCapHeight);
        mUnitTextView.setTextSize(PDEFontHelpers.calculateFontSize(mUnitTextView.getTypeface(),
                                                                   mFontSizeOfUnitTextView));
        if (mBarStyle == PDEUsageBar.PDEUsageBarStyle.PDEUsageBarStyleFullBar) {
            mUnitTextView.setPaddingAll(mUnitTextView.getPaddingLeft(), mUnitTextView.getPaddingTop(),
                                        mUnitTextView.getPaddingRight(), CONFIGURATION_BOTTOM_PADDING_FULL_BAR);
        } else {
            mUnitTextView.setPaddingAll(mUnitTextView.getPaddingLeft(), mUnitTextView.getPaddingTop(),
                                        mUnitTextView.getPaddingRight(), CONFIGURATION_BOTTOM_PADDING_SMALL_BAR);
        }
        mUnitTextView.setEllipsize(true);
        mUnitTextView.setMaxLines(1);

        //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
        // --- do NOT delete this out commented code ----
        /*
        mMaskingMode = PDEUsageBarLabelMaskingMode.PDEUsageBarLabelMaskingModeNone;
        mMaskPath = new Path();
        this.setWillNotDraw(false);
        */

        // add the views
        addView(mNumbersTextView);
        addView(mUnitTextView);

        // set texts
        mNumbersTextView.setText(getNumbersString());
        mUnitTextView.setText(mUnitText);

        // updates
        updateTextColor();
        calculateSizeOfTextView(mNumbersTextView);
        calculateSizeOfTextView(mUnitTextView);

        // set the layout params of the text views
        RelativeLayout.LayoutParams relParams = (RelativeLayout.LayoutParams) mNumbersTextView.getLayoutParams();
        if (relParams != null) {
            relParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
            relParams.height = LayoutParams.MATCH_PARENT;
            mNumbersTextView.setLayoutParams(relParams);
        }
        relParams = (RelativeLayout.LayoutParams) mUnitTextView.getLayoutParams();
        if (relParams != null) {
            relParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
            relParams.height = LayoutParams.MATCH_PARENT;
            relParams.addRule(RelativeLayout.RIGHT_OF, mNumbersTextView.getId());
            mUnitTextView.setLayoutParams(relParams);
        }
    }


    /**
     * @brief Set the text color of the numbers text layer and the unit text layer (always the same color).
     */
    protected void setTextColor(PDEColor color) {
        // remember
        mTextColor = color;
        // update
        mNumbersTextView.setTextColor(mTextColor);
        mUnitTextView.setTextColor(mTextColor);
    }


    /**
     * @brief Set the font size of the numbers text view.
     */
    public void setFontSizeOfNumbersTextView(float size) {
        // anything to do?
        if (mFontSizeOfNumbersTextView == size) return;
        // remember
        mFontSizeOfNumbersTextView = size;
        // update
        mNumbersTextView.setTextSize(PDEFontHelpers.calculateFontSize(mNumbersTextView.getTypeface(),
                                                                      mFontSizeOfNumbersTextView));
        calculateSizeOfTextView(mNumbersTextView);
    }


    /**
     * @brief Get the font size of the text view that shows the numbers.
     */
    @SuppressWarnings("unused")
    public float getFontSizeOfNumbersTextView() {
        return mFontSizeOfNumbersTextView;
    }


    /**
     * @brief Set the font size of the unit text view.
     */
    public void setFontSizeOfUnitTextView(float size) {
        // anything to do?
        if (mFontSizeOfUnitTextView == size) return;
        // remember
        mFontSizeOfUnitTextView = size;
        // update
        mUnitTextView.setTextSize(PDEFontHelpers.calculateFontSize(mUnitTextView.getTypeface(),
                                                                   mFontSizeOfUnitTextView));
        calculateSizeOfTextView(mUnitTextView);
    }


    /**
     * @brief Get the font size of the text view that shows the unit.
     */
    @SuppressWarnings("unused")
    public float getFontSizeOfUnitTextView() {
        return mFontSizeOfUnitTextView;
    }


    /**
     * @brief Set the number of decimal places we want to show.
     */
    public void setNumberOfDecimalPlaces(int decimals) {
        // anything to do?
        if (decimals == mNumberOfDecimalPlaces) return;
        // remember
        mNumberOfDecimalPlaces = decimals;
        // update
        mNumbersTextView.setText(getNumbersString());
        calculateSizeOfTextView(mNumbersTextView);
    }


    /**
     * @brief Get the number of decimal places we're showing right now.
     */
    @SuppressWarnings("unused")
    public int getNumberOfDecimalPlaces() {
        return mNumberOfDecimalPlaces;
    }


    /**
     * @brief Set the visual style of the usage bar.
     *
     * The visual style of the usage bar has influence on the text color and the layout of the label. So, the label also
     * needs to know when the visual bar style changes.
     */
    public void setBarStyle(PDEUsageBar.PDEUsageBarStyle barStyle) {
        // anything to do?
        if (mBarStyle == barStyle) return;
        // remember
        mBarStyle = barStyle;
        // update the paddings according to the visual bar style
        if (mBarStyle == PDEUsageBar.PDEUsageBarStyle.PDEUsageBarStyleFullBar) {
            mNumbersTextView.setPaddingAll(CONFIGURATION_SIDE_PADDING_FULL_BAR, mNumbersTextView.getPaddingTop(),
                                           mNumbersTextView.getPaddingRight(), CONFIGURATION_BOTTOM_PADDING_FULL_BAR);
            mUnitTextView.setPaddingAll(mUnitTextView.getPaddingLeft(), mUnitTextView.getPaddingTop(),
                                        mUnitTextView.getPaddingRight(), CONFIGURATION_BOTTOM_PADDING_FULL_BAR);
        } else {
            mNumbersTextView.setPaddingAll(CONFIGURATION_SIDE_PADDING_SMALL_BAR, mNumbersTextView.getPaddingTop(),
                                           mNumbersTextView.getPaddingRight(), CONFIGURATION_BOTTOM_PADDING_SMALL_BAR);
            mUnitTextView.setPaddingAll(mUnitTextView.getPaddingLeft(), mUnitTextView.getPaddingTop(),
                                        mUnitTextView.getPaddingRight(), CONFIGURATION_BOTTOM_PADDING_SMALL_BAR);
        }

        // further updates
        updateTextColor();
        calculateSizeOfTextView(mNumbersTextView);
        calculateSizeOfTextView(mUnitTextView);
    }


    /**
     * @brief Get the current visual style of the bar label.
     */
    @SuppressWarnings("unused")
    public PDEUsageBar.PDEUsageBarStyle getBarStyle() {
        return mBarStyle;
    }


    /**
     * @brief Set the color that fills the usage bar.
     *
     * The small bar visual style uses this color directly as text color. The full bar visual style also needs this color
     * in order to calculate the final text color. If there's a custom text color set, both styles use the custom color.
     */
    public void setBarFillColor(PDEColor color) {
        // remember
        mBarFillColor = color;
        // the text colors generally depend on this color, so trigger an update.
        updateTextColor();
    }


    /**
     * @brief Get the current fill color
     */
    @SuppressWarnings("unused")
    public PDEColor getBarFillColor() {
        return mBarFillColor;
    }


    /**
     * @brief Set a custom color for the text.
     *
     * Generally there is a default logic which text color is used for the full bar variant and the small bar variant.
     * The full bar variant automatically uses gray multiplied with the fill color and the small bar variant automatically
     * uses the fill color of the bar.
     * With this function the user can set his own custom color for the text. But the use of a custom color switches off
     * the color logic described above. So the user is responsible for his own to choose a color with a reasonable contrast
     * for the currently used variant.
     */
    public void setTextColorCustom(PDEColor color) {
        mTextColorCustom = color;
        updateTextColor();
    }


    /**
     * @brief Gets the current custom text color, if there's one set.
     *
     * Returns null if there's no custom text color set (default).
     */
    @SuppressWarnings("unused")
    public PDEColor getTextColorCustom() {
        return mTextColorCustom;
    }


    /**
     * @brief Finds out which text color should be used according to the current settings.
     *
     * Generally the full bar variant uses gray multiplied with the fill color and the small bar variant uses the fill color.
     * But if there's a custom color set, it has top priority.
     */
    protected PDEColor determineTextColor() {
        if (mTextColorCustom != null) return mTextColorCustom;
        if (mBarStyle == PDEUsageBar.PDEUsageBarStyle.PDEUsageBarStyleFullBar) return getTextColorFullBar();
        else return mBarFillColor;
    }


    /**
     * @brief Triggers update of the text color in respect of the current settings.
     */
    protected void updateTextColor() {
        setTextColor(determineTextColor());
    }


    /**
     * @brief Calculates and delivers the text color of the full bar.
     */
    protected PDEColor getTextColorFullBar() {
        return mBarFillColor.multiplyWithColor(PDEColor.valueOf("DTGrey2"));
    }


    /**
     * @brief Switch the total number in the label on/off.
     */
    public void setTotalTextEnabled(boolean totalTextEnabled) {
        // anything to do?
        if (mTotalTextEnabled == totalTextEnabled) return;
        // remember
        mTotalTextEnabled = totalTextEnabled;
        // update
        mNumbersTextView.setText(getNumbersString());
        calculateSizeOfTextView(mNumbersTextView);
    }


    /**
     * @brief Check if the total number part of the label is currently switched on or off.
     */
    public boolean isTotalTextEnabled() {
        return mTotalTextEnabled;
    }


    /**
     * @brief Switch the unit text in the label on/off.
     */
    public void setUnitTextEnabled(boolean unitTextEnabled) {
        // anything to do?
        if (mUnitTextEnabled == unitTextEnabled) return;
        // remember
        mUnitTextEnabled = unitTextEnabled;
        // update
        if (mUnitTextEnabled) {
            // unit text is now enabled, so make the unit text view visible
            mUnitTextView.setVisibility(View.VISIBLE);
        } else {
            // unit text is now disabled, so set the unit text view to GONE
            mUnitTextView.setVisibility(View.GONE);
        }
        calculateSizeOfTextView(mUnitTextView);
    }


    /**
     * @brief Check if the unit text of the label is currently switched on or off.
     */
    public boolean isUnitTextEnabled() {
        return mUnitTextEnabled;
    }

    //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
    // --- do NOT delete this out commented code ----
    /*
    public void setMaskingMode(PDEUsageBarLabelMaskingMode maskingMode) {
        // anything to do?
        if (mMaskingMode == maskingMode) return;
        // remember new mode
        mMaskingMode = maskingMode;
    }

    public PDEUsageBarLabelMaskingMode getMaskingMode() {
        return mMaskingMode;
    }
    */


    /**
     * @brief Set the text which should be shown by the unit text view.
     */
    public void setUnitText(String text) {
        // anything to change?
        if (PDEString.isEqual(text, mUnitText)) return;
        // remember
        mUnitText = text;
        mUnitTextView.setText(mUnitText);
        // update
        calculateSizeOfTextView(mUnitTextView);
    }


    /**
     * @brief Get the text of the unit text view.
     */
    public String getUnitText() {
        return mUnitText;
    }


    /**
     * @brief Get the correctly formated numbers string.
     *
     * The string that shows the progress and the total value should be of the following format:
     * "progress/total"
     * If there are a number of decimal places configured, the function also takes care that these are added into the
     * string correctly. This function uses the current values of the member variables.
     */
    protected String getNumbersString() {
        return getNumbersString(mCurrentFillValue, mTotalValue);
    }


    /**
     * @brief Get the correctly formated numbers string.
     *
     * The string that shows the progress and the total value should be generally formated in this way:
     * "progress/total"
     * If there are a number of decimal places configured, the function also takes care that these are added into the string
     * correctly. This function uses the values which are delivered by the parameters.
     */
    protected String getNumbersString(float progressValue, float totalValue) {
        String result;

        // decimal places needed?
        if (mNumberOfDecimalPlaces == 0) {
            // get the current value part of the string
            result = String.valueOf(Math.round(progressValue));
            // add total value part of the string if needed
            if (mTotalTextEnabled) {
                result += "/" + Math.round(totalValue);
            }
        } else {
            NumberFormat form;

            // get current Locale setting
            Locale current = Locale.US;
            if (getResources() != null && getResources().getConfiguration() != null) {
                current = getResources().getConfiguration().locale;
            }
            // localised format
            form = NumberFormat.getInstance(current);

            // adapt it to our needs
            form.setGroupingUsed(false);
            form.setMaximumFractionDigits(mNumberOfDecimalPlaces);
            form.setMinimumFractionDigits(mNumberOfDecimalPlaces);
            if (form instanceof DecimalFormat) {
                ((DecimalFormat) form).setDecimalSeparatorAlwaysShown(true);
            }

            // get the current value part of the string
            result = form.format(progressValue);
            // add total value part of the string if needed
            if (mTotalTextEnabled) {
                result += "/" + form.format(totalValue);
            }
        }

        return result;
    }


    /**
     * @brief Set the current fill value.
     *
     * This will be called several times while the usage bar animation counts up the fill value from 0 to the target
     * value.
     */
    public void setCurrentFillValue(float currentFillValue) {
        // anything to do
        if (currentFillValue == mCurrentFillValue || currentFillValue > mTotalValue || currentFillValue < 0.0f) return;
        // remember
        mCurrentFillValue = currentFillValue;
        // update
        mNumbersTextView.setText(getNumbersString());
        calculateSizeOfTextView(mNumbersTextView);
    }


    /**
     * @brief Get current progress/fill value.
     */
    @SuppressWarnings("unused")
    public float getProgressValue() {
        return mCurrentFillValue;
    }


    /**
     * @brief Set the new total value.
     */
    public void setTotalValue(float totalValue) {
        // anything to do?
        if (totalValue == mTotalValue) return;
        // remember
        mTotalValue = totalValue;
        // update
        mNumbersTextView.setText(getNumbersString());
        calculateSizeOfTextView(mNumbersTextView);
    }


    /**
     * @brief Get the current total value.
     */
    @SuppressWarnings("unused")
    public float getTotalValue() {
        return mTotalValue;
    }


    /**
     * @brief Calculate the size that is needed for the given text view.
     */
    protected void calculateSizeOfTextView(PDETextView textView) {
        if (textView == mNumbersTextView) {
            mNumbersTextViewWidth = measureLayerWidthOfTextView(textView);
            mNumbersTextViewHeight = measureLayerHeightOfTextView(textView);
        } else if (textView == mUnitTextView) {
            if (mUnitTextEnabled) {
                mUnitTextViewWidth = measureLayerWidthOfTextView(textView);
                mUnitTextViewHeight = measureLayerHeightOfTextView(textView);
            } else {
                // view is disabled -> no size
                mUnitTextViewWidth = 0;
                mUnitTextViewHeight = 0;
            }
        }
    }

    //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
    // --- do NOT delete this out commented code ----
        /*
    protected void onDraw(Canvas canvas) {

        if (mMaskingMode != PDEUsageBarLabelMaskingMode.PDEUsageBarLabelMaskingModeNone) {
            float currFillWidth;
            // to do: optimize runtime
            currFillWidth = getWidth() * (getCurrentFillValue() / getTotalValue());
            mMaskPath.reset();
            if (mMaskingMode == PDEUsageBarLabelMaskingMode.PDEUsageBarLabelMaskingModeShow) {
                mMaskPath.addRect(0.0f, 0.0f, currFillWidth, getHeight(), Path.Direction.CW);
            } else if (mMaskingMode == PDEUsageBarLabelMaskingMode.PDEUsageBarLabelMaskingModeHide) {
                mMaskPath.addRect(currFillWidth, 0.0f, getWidth(), getHeight(), Path.Direction.CW);
            }
            canvas.clipPath(mMaskPath);
        }

        super.onDraw(canvas);
    }
    */


    /**
     * @brief Measure and deliver the current height of the label.
     */
    public int measureHeight() {
        return Math.max(measureLayerHeightOfTextView(mNumbersTextView), measureLayerHeightOfTextView(mUnitTextView));
    }


    /**
     * @brief Measure and deliver the current width of the label.
     *
     * The width depends on the current content of the text views and the visual bar style.
     */
    @SuppressWarnings("unused")
    public int measureWidth() {
        return measureLayerWidthOfTextView(mNumbersTextView)
               + measureLayerWidthOfTextView(mUnitTextView)
               + getCurrentSidePadding();
    }


    /**
     * @brief Measure and deliver the maximum width of the label.
     *
     * The maximum width of the label is the width it has when the usage bar is completely filled. In this moment the
     * largest available numbers for this usage bar should be used which results in the maximum width of the label.
     */
    public int measureMaxWidth() {
        String maxString;
        maxString = getNumbersString(mTotalValue, mTotalValue);
        return measureLayerWidthOfTextViewWithCustomText(mNumbersTextView, maxString)
               + measureLayerWidthOfTextView(mUnitTextView)
               + getCurrentSidePadding();
    }


    /**
     * @brief Measure and deliver the height of the given text view.
     *
     * For our configuration the text view height is the sum of the cap height and the top and bottom paddings.
     */
    protected int measureLayerHeightOfTextView(PDETextView textView) {
        int top, bottom, capHeight;

        // check for disabled text view because they have no height.
        if (textView == mUnitTextView && !mUnitTextEnabled) return 0;
        // get cap height and paddings and sum them up
        capHeight = textView.getCapHeight();
        top = textView.getPaddingTop();
        bottom = textView.getPaddingBottom();

        return capHeight + top + bottom;
    }


    /**
     * @brief Get the side padding that fits the current visual bar style setting.
     */
    protected int getCurrentSidePadding() {
        return (mBarStyle == PDEUsageBar.PDEUsageBarStyle.PDEUsageBarStyleFullBar)
               ? CONFIGURATION_SIDE_PADDING_FULL_BAR
               : CONFIGURATION_SIDE_PADDING_SMALL_BAR;
    }


    /**
     * @brief Measure and deliver the width of the given text view.
     */
    protected int measureLayerWidthOfTextView(PDETextView textView) {
        // check for disabled text view because they have no width.
        if (textView == mUnitTextView && !mUnitTextEnabled) return 0;
        // return result
        return Math.round(textView.getElementWidth());
    }


    /**
     * @brief Measure and deliver the width for the given text view with the delivered string.
     *
     * This calculates how wide the delivered text view would be (with its current settings)
     * if you would put the delivered string in it.
     */
    protected int measureLayerWidthOfTextViewWithCustomText(PDETextView textView, String customText) {
        // check for disabled text view because they have no width.
        if (textView == mUnitTextView && !mUnitTextEnabled) return 0;
        // return result
        return Math.round(textView.getElementWidth(customText));
    }


    /**
     * @brief Needed for correct measuring/layouting.
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;

        // call super implementation
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // calculate the height we would like to have in order of not being cut
        int desiredHeight = measureHeight();
        // get delivered values
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // take the width we got
        width = widthSize;

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                             resolveSize(height, heightMeasureSpec));
    }


    /**
     * @brief Get the currently used font.
     */
    public PDETypeface getTypeface() {
        return mNumbersTextView.getTypeface();
    }
}
