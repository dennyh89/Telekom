/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.datavisualisation.usagebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.animation.PDEAnimationRoot;
import de.telekom.pde.codelibrary.ui.animation.PDEParametricCurveAnimation;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.datavisualisation.PDEUsageEvent;
import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDEString;

/**
 * @brief A bar that shows the usage of a certain amount of commodity.
 *
 * The bar can show a certain fill level. The filling of the bar is animated.
 * It also has a textual label that shows the currently used amount, the total amount and the unit of the commodity.
 * The bar has also two different visual styles.
 */
public class PDEUsageBar extends RelativeLayout implements PDEIEventSource {

    // log tag
    private final static String LOG_TAG = PDEUsageBar.class.getName();

    // configurations
    protected final static int CONFIGURATION_MIN_HEIGHT_FULL_BAR = PDEBuildingUnits.BU() * 4;
    protected final static int CONFIGURATION_MIN_HEIGHT_SMALL_BAR = PDEBuildingUnits.BU();
    protected final static int CONFIGURATION_DEFAULT_DURATION = 2000;
    protected final static String CONFIGURATION_DEFAULT_FILL_COLOR = "DTDVLightBlue";
    protected final static PDEUsageBarStyle CONFIGURATION_DEFAULT_BAR_STYLE
            = PDEUsageBarStyle.PDEUsageBarStyleFullBar;

    //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
    // --- do NOT delete this out commented code ----
    //protected final static float LUMA_THRESHOLD = 0.2f;


    // available visual bar styles
    public enum PDEUsageBarStyle {
        PDEUsageBarStyleFullBar,
        PDEUsageBarStyleSmallBar
    }


    // colors
    protected PDEColor mColor;
    protected PDEColor mTextColorCustom;
    // font size (of the text with the numbers)
    protected float mFontSize;
    // visual bar style
    protected PDEUsageBarStyle mBarStyle;
    // customization options
    protected int mNumberDecimalPlaces;
    protected boolean mLabelEnabled;
    protected boolean mTotalTextEnabled;
    protected boolean mUnitTextEnabled;
    protected boolean mStartAnimationAtOnceEnabled;
    // fill settings
    protected float mCurrentFillValue;
    protected float mTargetFillValue;
    protected float mTotalFillValue;
    // duration
    protected long mFillDurationToTotalFillValue;
    // unit text
    protected String mUnitText;
    // background view that shows the filling level
    protected PDEUsageBarBackground mBackgroundView;
    // label view that shows the texts properly formated and layouted
    protected PDEUsageBarLabel mLabel;
    // animation for the visual filling of the bar
    protected PDEParametricCurveAnimation mAnimation;
    // helper flag
    protected boolean mVisibleOnScreen;

    //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
    // --- do NOT delete this out commented code ----
    //protected PDEColor mAlternativeTextColorCustom;
    //protected PDEUsageBarLabel mLabelAlternativeColor;
    //protected boolean mAlternativeLabelActive;
    //protected boolean mLabelColorShiftMode;


    /**
     * @brief PDEEventSource instance that provides the event sending behaviour.
     */
    private PDEEventSource mEventSource;
    protected ArrayList<Object> mStrongPDEEventListenerHolder;


    /**
     * @brief Constructor.
     */
    public PDEUsageBar(Context context) {
        super(context);
        init(context, null);
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEUsageBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEUsageBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    /**
     * @brief Initialization
     */
    protected void init(Context context, android.util.AttributeSet attrs) {
        // if in developer tool (IDE) stop here
        if (isInEditMode()) return;
        // init
        mBarStyle = CONFIGURATION_DEFAULT_BAR_STYLE;
        mBackgroundView = new PDEUsageBarBackground(context, attrs);
        mLabelEnabled = true;
        mStartAnimationAtOnceEnabled = false;
        mVisibleOnScreen = false;

        // init event source
        mEventSource = new PDEEventSource();
        // set ourselves as the default sender (optional)
        mEventSource.setEventDefaultSender(this, true);
        mStrongPDEEventListenerHolder = new ArrayList<Object>();

        // create label
        mLabel = new PDEUsageBarLabel(context, attrs);

        // fetch default values
        mNumberDecimalPlaces = mLabel.getNumberOfDecimalPlaces();
        mFontSize = mLabel.getFontSizeOfNumbersTextView();
        mUnitText = mLabel.getUnitText();
        mTotalTextEnabled = mLabel.isTotalTextEnabled();
        mUnitTextEnabled = mLabel.isUnitTextEnabled();

        // set main color
        setColor(PDEColor.valueOf(CONFIGURATION_DEFAULT_FILL_COLOR));

        // text custom color
        mTextColorCustom = null;

        // add views
        addView(mBackgroundView);
        addView(mLabel);

        // for now
        mCurrentFillValue = 0.0f;
        mTargetFillValue = 0.0f;
        mTotalFillValue = 10.0f;
        mFillDurationToTotalFillValue = CONFIGURATION_DEFAULT_DURATION;

        // create and configure animation
        mAnimation = new PDEParametricCurveAnimation();
        PDEAnimationRoot.addSubAnimationStatic(mAnimation);
        mAnimation.setDidChangeTarget(this, "timeAnimations");

        // set layout params of background view
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBackgroundView.getLayoutParams();
        if (params != null) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mBackgroundView.setLayoutParams(params);
        }
        // set layout params of label
        params = (RelativeLayout.LayoutParams) mLabel.getLayoutParams();
        if (params != null) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mLabel.setLayoutParams(params);
        }

        //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
        /*  --- do NOT delete this out commented code ----
        mLabelColorShiftMode = false;
        mAlternativeLabelActive = false;
        mLabelAlternativeColor = new PDEUsageBarLabel(context,attrs);
        mLabelAlternativeColor.setMaskingMode(PDEUsageBarLabel.PDEUsageBarLabelMaskingMode.PDEUsageBarLabelMaskingModeShow);
        mLabelAlternativeColor.setVisibility(GONE);
        setAlternativeTextColorCustom(PDEColor.valueOf("DTGrey1"));
        addView(mLabelAlternativeColor);

        params = (RelativeLayout.LayoutParams)mLabelAlternativeColor.getLayoutParams();
        if (params != null ){
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mLabelAlternativeColor.setLayoutParams(params);
        }
        */

        // process xml-attributes
        setAttributes(attrs);
    }


    /**
     * @brief Load XML attributes.
     */
    private void setAttributes(AttributeSet attrs) {
        // valid?
        if (attrs == null) return;

        TypedArray sa = null;
        Context context = getContext();

        if (context != null) {
            sa = context.obtainStyledAttributes(attrs, R.styleable.PDEUsageBar);
        }

        if (sa != null) {
            // set total fill value
            if (sa.hasValue(R.styleable.PDEUsageBar_totalFillValue)) {
                setTotalFillValue(sa.getFloat(R.styleable.PDEUsageBar_totalFillValue, 10.0f));
            }

            // set current fill value
            if (sa.hasValue(R.styleable.PDEUsageBar_currentFillValue)) {
                setCurrentFillValue(sa.getFloat(R.styleable.PDEUsageBar_currentFillValue, 0.0f));
            }

            // set fill duration
            if (sa.hasValue(R.styleable.PDEUsageBar_fillDuration)) {
                setFillDurationToTotalFillValue(sa.getInteger(R.styleable.PDEUsageBar_fillDuration,
                                                              CONFIGURATION_DEFAULT_DURATION));
            }

            // set number of decimal places
            if (sa.hasValue(R.styleable.PDEUsageBar_numberOfDecimalPlaces)) {
                setNumberOfDecimalPlaces(sa.getInteger(R.styleable.PDEUsageBar_numberOfDecimalPlaces, 0));
            }

            // set label enabled
            if (sa.hasValue(R.styleable.PDEUsageBar_labelEnabled)) {
                setLabelEnabled(sa.getBoolean(R.styleable.PDEUsageBar_labelEnabled, true));
            }

            // set unit text enabled
            if (sa.hasValue(R.styleable.PDEUsageBar_unitTextEnabled)) {
                setUnitTextEnabled(sa.getBoolean(R.styleable.PDEUsageBar_unitTextEnabled, true));
            }

            // set total text enabled
            if (sa.hasValue(R.styleable.PDEUsageBar_totalTextEnabled)) {
                setTotalTextEnabled(sa.getBoolean(R.styleable.PDEUsageBar_totalTextEnabled, true));
            }

            // set start animation at once
            if (sa.hasValue(R.styleable.PDEUsageBar_startAnimationAtOnceEnabled)) {
                setStartAnimationAtOnceEnabled(
                        sa.getBoolean(R.styleable.PDEUsageBar_startAnimationAtOnceEnabled, false));
            }

            // set unit text
            if (sa.hasValue(R.styleable.PDEUsageBar_unitText)) {
                setUnitText(sa.getString(R.styleable.PDEUsageBar_unitText));
            }

            // set color
            if (sa.hasValue(R.styleable.PDEUsageBar_color)) {
                int resourceID = sa.getResourceId(R.styleable.PDEUsageBar_color, 0);
                if (resourceID != 0) {
                    setColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setColor(sa.getColor(R.styleable.PDEUsageBar_color, R.color.DTDVLightBlue));
                }
            }

            // set text color custom
            if (sa.hasValue(R.styleable.PDEUsageBar_textColorCustom)) {
                int resourceID = sa.getResourceId(R.styleable.PDEUsageBar_textColorCustom, 0);
                if (resourceID != 0) {
                    setTextColorCustom(PDEColor.valueOfColorID(resourceID));
                } else {
                    setTextColorCustom(sa.getColor(R.styleable.PDEUsageBar_textColorCustom, R.color.DTBlack));
                }
            }

            //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
            // --- do NOT delete this out commented code ----
            /*
            // set alternative text color custom
            if (sa.hasValue(R.styleable.PDEUsageBar_alternativeTextColorCustom)) {
                int resourceID = sa.getResourceId(R.styleable.PDEUsageBar_alternativeTextColorCustom, 0);
                if (resourceID != 0) {
                    setAlternativeTextColorCustom(PDEColor.valueOfColorID(resourceID));
                } else {
                    setAlternativeTextColorCustom(
                            sa.getColor(R.styleable.PDEUsageBar_alternativeTextColorCustom, R.color.DTBlack));
                }
            }
            */

            // set visual bar style
            if (sa.hasValue(R.styleable.PDEUsageBar_barStyle)) {
                setBarStyle(sa.getInteger(R.styleable.PDEUsageBar_barStyle, 0));
            }

            // set target fill value
            if (sa.hasValue(R.styleable.PDEUsageBar_targetFillValue)) {
                setTargetFillValue(sa.getFloat(R.styleable.PDEUsageBar_targetFillValue, 0.0f));
            }

            // set the font size
            // we first try whether the inserted value is a dimension, if this fails we evaluate as string
            if (sa.hasValue(R.styleable.PDEUsageBar_textSize)) {
                try {
                    setFontSize(sa.getDimensionPixelSize(R.styleable.PDEUsageBar_textSize, 50));
                } catch (Exception e) {
                    setFontSize(parseDimension(sa.getString(R.styleable.PDEUsageBar_textSize)));
                }
            }

            sa.recycle();
        }
    }


    /**
     * @brief Parse dimension helper.
     */
    protected int parseDimension(String dimensionString) {
        if (getResources() == null) return 0;

        return Math.round(PDEFontHelpers.parseFontSize(dimensionString,
                                                       mLabel.getTypeface(),
                                                       getResources().getDisplayMetrics()));
    }

//----- Animation ------------------------------------------------------------------------------------------------------


    /**
     * @brief Starts the filling animation.
     */
    public void startAnimation() {
        // check if we are already running -> nothing to do
        if (mAnimation.isRunning()) return;

        // check if there is a value change
        if (mCurrentFillValue == mTargetFillValue) return;

        // check if the component is visible
        if (!mVisibleOnScreen) return;

        // configure animation
        if (mAnimation.getValue() != mCurrentFillValue) {
            // be sure that the animation always has the same current value before it starts.
            mAnimation.setValueImmediate(mCurrentFillValue);
        }
        mAnimation.setBaseTime(mFillDurationToTotalFillValue);
        mAnimation.setBaseDistance(mTotalFillValue);
        mAnimation.goToValue(mTargetFillValue);
    }


    /**
     * @brief Timing function of animation.
     *
     * Don't call this manually. It's only public because callback functions have to be public.
     */
    @SuppressWarnings("unused")
    public void timeAnimations() {
        // update the current fill value by the animation
        updateCurrentFillValue((float) mAnimation.getValue());
        // check if animation has finished now
        if (!mAnimation.isRunning()) {
            // send event
            sendEvent(PDEUsageEvent.EVENT_ACTION_ANIMATION_FINISHED);
        }
    }


    /**
     * @brief Stop animation.
     */
    public void stopAnimation() {
        //stop animations
        mAnimation.stopAnimation();
    }

    //----- Visual Appearance ------------------------------------------------------------------------------------------


    /**
     * @brief Set the style of the usage bar.
     *
     * There are two visual variants defined for the usage bar. One variant with a big bar (named full bar) and the
     * label within the bar and one variant with a small bar and the label above it. The two different styles differ in
     * some further details.
     */
    public void setBarStyle(PDEUsageBarStyle barStyle) {
        // anything to do?
        if (mBarStyle == barStyle) return;

        // remember
        mBarStyle = barStyle;

        // update
        mLabel.setBarStyle(mBarStyle);
        //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
        // --- do NOT delete this out commented code ----
        // also update alternative label
        //mLabelAlternativeColor.setBarStyle(mBarStyle);
        //checkActivationOfAlternativeLabel();

        // adapt background height to visual bar style
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBackgroundView.getLayoutParams();
        if (params != null) {
            if (mBarStyle == PDEUsageBarStyle.PDEUsageBarStyleFullBar) {
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                params.height = PDEBuildingUnits.BU();
            }
        }
        mBackgroundView.setLayoutParams(params);
    }


    /**
     * @brief Get visual bar style.
     */
    public PDEUsageBarStyle getBarStyle() {
        return mBarStyle;
    }


    /**
     * @brief Set visual bar style by number.
     */
    public void setBarStyle(int barStyle) {
        PDEUsageBarStyle style;
        try {
            style = PDEUsageBarStyle.values()[barStyle];
        } catch (Exception e) {
            // fallback
            style = PDEUsageBarStyle.PDEUsageBarStyleFullBar;
        }
        setBarStyle(style);
    }


    /**
     * @param barStyleString valid values  "FullBar" / "PDEUsageBarStyleFullBar" or  "SmallBar" / "PDEUsageBarStyleSmallBar"
     * @brief Set visual bar style by String.
     */
    @SuppressWarnings("unused")
    public void setBarStyle(String barStyleString) {
        PDEUsageBarStyle style = PDEUsageBarStyle.PDEUsageBarStyleFullBar;

        if (PDEString.isEqualCaseIndependent(barStyleString, "FullBar")
            || PDEString.isEqualCaseIndependent(barStyleString, "PDEUsageBarStyleFullBar")) {
            style = PDEUsageBarStyle.PDEUsageBarStyleFullBar;
        } else if (PDEString.isEqualCaseIndependent(barStyleString, "SmallBar")
                   || PDEString.isEqualCaseIndependent(barStyleString, "PDEUsageBarStyleSmallBar")) {
            style = PDEUsageBarStyle.PDEUsageBarStyleSmallBar;
        }

        setBarStyle(style);
    }


    /**
     * @brief Set main color of the usage bar.
     *
     * The usage bar generally has a main color. This is the color which shows the filled part of the bar. However,
     * several other parts of the bar are colored with colors which are derived directly from this main color. So only
     * this base color can be set, the derived colors are calculated and set automatically.
     */
    public void setColor(PDEColor color) {
        // anything to do?
        if (mColor != null && color.getIntegerColor() == mColor.getIntegerColor()) return;

        // remember
        mColor = color;

        // inform background
        mBackgroundView.setColor(mColor);
        // inform label
        mLabel.setBarFillColor(mColor);

        //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
        // --- do NOT delete this out commented code ----
        /*
        mLabelAlternativeColor.setBarFillColor(mColor);
        // check if we need the multicolor mode; We need it when the fill color is too dark to make a good contrast to the
        // color of the label text
        if (mColor.calculateLuma() < LUMA_THRESHOLD) {
            setLabelColorShiftModeEnabled(true);
        } else {
            setLabelColorShiftModeEnabled(false);
        }
        */
    }


    /**
     * @brief Set main color of Usage Bar by integer.
     */
    public void setColor(int color) {
        setColor(PDEColor.valueOf(color));
    }


    /**
     * @brief Set main color by string.
     *
     * Convenience function.
     */
    @SuppressWarnings("unused")
    public void setColorWithString(String colorString) {
        setColor(PDEColor.valueOf(colorString));
    }


    /**
     * @brief Get main color of usage bar.
     */
    @SuppressWarnings("unused")
    public PDEColor getColor() {
        return mColor;
    }


    /**
     * @brief Set custom text color by integer.
     */
    public void setTextColorCustom(int color) {
        setTextColorCustom(PDEColor.valueOf(color));
    }


    /**
     * @brief Set custom text color by string.
     *
     * Convenience function.
     */
    @SuppressWarnings("unused")
    public void setTextColorCustomWithString(String colorString) {
        setTextColorCustom(PDEColor.valueOf(colorString));
    }


    /**
     * @brief Set a custom color for the label text.
     *
     * The different bar styles have their own default text colors defined. The big bar variant always has a gray
     * multiplied with the fill color, the small bar variant always uses the fill color.
     * If the user wants to change this behaviour he can set a custom label text color here, but then he has to take
     * care for himself, that the color is reasonable (in matters of contrast) for the bar style he uses. The default
     * colors are switched off then.
     */
    public void setTextColorCustom(PDEColor color) {
        // remember
        mTextColorCustom = color;

        //update
        mLabel.setTextColorCustom(mTextColorCustom);
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
     * @brief Set number of decimal digits (behind the dot).
     */
    @SuppressWarnings("unused")
    public void setNumberOfDecimalPlaces(int number) {
        // anything to do?
        if (number == getNumberOfDecimalPlaces()) return;

        // remember
        mNumberDecimalPlaces = number;

        // update
        mLabel.setNumberOfDecimalPlaces(mNumberDecimalPlaces);

        //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
        // --- do NOT delete this out commented code ----
        //mLabelAlternativeColor.setNumberOfDecimalPlaces(mNumberDecimalPlaces);
    }


    /**
     * @brief Get number of decimal digits (behind the dot).
     */
    public int getNumberOfDecimalPlaces() {
        return mNumberDecimalPlaces;
    }


    /**
     * @brief Set font size of the text layer that shows the numbers.
     *
     * For now the font size of the unit text is automatically set to be half of the numbers font size.
     * This might change in future.
     */
    @SuppressWarnings("unused")
    public void setFontSize(float size) {
        // anything to do
        if (size == mFontSize) return;

        // remember
        mFontSize = size;

        // update
        mLabel.setFontSizeOfNumbersTextView(mFontSize);
        mLabel.setFontSizeOfUnitTextView(0.5f * mFontSize);
        //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
        // --- do NOT delete this out commented code ----
        //mLabelAlternativeColor.setFontSizeOfNumbersTextView(mFontSize);
        //mLabelAlternativeColor.setFontSizeOfUnitTextView(0.5f * mFontSize);
    }


    /**
     * @brief Get font size of the text layer that shows the numbers.
     */
    @SuppressWarnings("unused")
    public float getFontSize() {
        return mFontSize;
    }


    /**
     * @brief Turn text label on/off.
     */
    @SuppressWarnings("unused")
    public void setLabelEnabled(boolean labelEnabled) {
        // anything to do?
        if (mLabelEnabled == labelEnabled) return;
        // remember
        mLabelEnabled = labelEnabled;
        // update
        if (mLabelEnabled) {
            // label is now enabled, so make it visible
            mLabel.setVisibility(VISIBLE);
        } else {
            // label is now disabled so set it GONE
            mLabel.setVisibility(GONE);
        }
        //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
        // --- do NOT delete this out commented code ----
        //checkActivationOfAlternativeLabel();
    }


    /**
     * @brief Check if label is turned on or off.
     */
    @SuppressWarnings("unused")
    public boolean isLabelEnabled() {
        return mLabelEnabled;
    }


    /**
     * @brief Turn on/off the part of the text label that shows the total number.
     */
    @SuppressWarnings("unused")
    public void setTotalTextEnabled(boolean totalTextEnabled) {
        // anything to do?
        if (mTotalTextEnabled == totalTextEnabled) return;

        // remember
        mTotalTextEnabled = totalTextEnabled;

        // update
        mLabel.setTotalTextEnabled(mTotalTextEnabled);
        //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
        // --- do NOT delete this out commented code ----
        //mLabelAlternativeColor.setTotalTextEnabled(mTotalTextEnabled);
    }


    /**
     * @brief Check if the part of the text label that shows the total value is turned on or off.
     */
    public boolean isTotalTextEnabled() {
        return mTotalTextEnabled;
    }


    /**
     * @brief Turn on/off the part of the text label that shows the unit.
     */
    @SuppressWarnings("unused")
    public void setUnitTextEnabled(boolean unitTextEnabled) {
        // anything to do?
        if (mUnitTextEnabled == unitTextEnabled) return;

        // remember
        mUnitTextEnabled = unitTextEnabled;

        // update
        mLabel.setUnitTextEnabled(mUnitTextEnabled);

        //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
        // --- do NOT delete this out commented code ----
        //mLabelAlternativeColor.setUnitTextEnabled(mUnitTextEnabled);
    }


    /**
     * @brief Check if the part of the textg label that shows the unit is turned on or off.
     */
    public boolean isUnitTextEnabled() {
        return mUnitTextEnabled;
    }


    /**
     * @brief Set the flag that shows if the bar is already shown on screen.
     *
     * In some situations it's interesting to know if the bar is already visible on screen. One of these situations is e.g.
     * the automatic start of an animation. So when this property is set to YES and automatic animation start is configured
     * the animation is started. This results in an animation that starts running when the screen is entered and visible.
     */
    protected void setVisibleOnScreen(boolean visible) {
        // anything to do?
        if (mVisibleOnScreen == visible) return;

        // remember
        mVisibleOnScreen = visible;

        // update
        if (mVisibleOnScreen && mStartAnimationAtOnceEnabled && mCurrentFillValue != mTargetFillValue) {
            startAnimation();
        }
    }


    /**
     * @brief Turn on/off automatic start of the animation.
     *
     * There are two reasonable scenarios for the start of the fill animation:
     * 1) The new target value for the bar is set and the fill animation is started manually at a later point in time.
     * 2) The fill animation is started automatically in the instant when a new target value is set.
     * This class supports both scenarios. The manual start is the default for now.
     * If the user prefers the second scenario he has to switch autostart on with this function.
     */
    public void setStartAnimationAtOnceEnabled(boolean autoStartEnabled) {
        // anything to do?
        if (mStartAnimationAtOnceEnabled == autoStartEnabled) return;

        // remember
        mStartAnimationAtOnceEnabled = autoStartEnabled;
    }


    /**
     * @brief Checks if the auto start of the animation is currently enabled.
     */
    @SuppressWarnings("unused")
    public boolean isStartAnimationAtOnceEnabled() {
        return mStartAnimationAtOnceEnabled;
    }

//----- Value-Setters --------------------------------------------------------------------------------------------------


    /**
     * @brief Set new fill value of the usage bar.
     *
     * This is for manually setting of a new current fill value by the user. In this way the user also can set an initial
     * start value from which the animation should start. If there's already an animation running, it will be stopped.
     * It also resets the target value to the new current value.
     */
    public void setCurrentFillValue(float currFillValue) {
        // anything to do?
        if (currFillValue == mCurrentFillValue) return;

        // security
        if (currFillValue > mTotalFillValue) currFillValue = mTotalFillValue;
        if (currFillValue < 0.0f) currFillValue = 0.0f;

        // stop the animation if it is running
        if (mAnimation.isRunning()) {
            stopAnimation();
        }

        // update value
        updateCurrentFillValue(currFillValue);
        mTargetFillValue = currFillValue;

        // update animation
        mAnimation.setValueImmediate(currFillValue);
    }


    /**
     * @brief Get the current fill value of the usage bar.
     */
    public float getCurrentFillValue() {
        return mCurrentFillValue;
    }


    /**
     * @brief Update fill value of the usage bar.
     *
     * This update function for the current fill value is for internal use only. It does not stop a running animation
     * and it doesn't set a new target value (like the setter does). So when the animation needs to update the current
     * fill value it will use this function instead of the setter.
     */
    protected void updateCurrentFillValue(float fillValue) {
        // anything to do?
        if (fillValue == mCurrentFillValue) return;

        // security
        if (fillValue > mTotalFillValue) fillValue = mTotalFillValue;
        if (fillValue < 0.0f) fillValue = 0.0f;

        // remember
        mCurrentFillValue = fillValue;

        // update
        updateChildren();

        // send event
        sendEvent(PDEUsageEvent.EVENT_ACTION_NEW_FILL_VALUE);
    }


    /**
     * @brief Set the target value at which the animation should end.
     *
     * The task of the usage bar is to show which amount of a given resource has been used, yet. The target fill value
     * is this amount of used units. So the filling animation will run until it reaches this "target" value.
     */
    public void setTargetFillValue(float targetValue) {
        // anything to do?
        if (targetValue == mTargetFillValue && mCurrentFillValue == mTargetFillValue) return;

        // security checks
        if (targetValue < 0.0f) {
            targetValue = 0.0f;
        }
        if (targetValue > mTotalFillValue) {
            targetValue = mTotalFillValue;
        }

        // remember
        mTargetFillValue = targetValue;

        // update running animation - so don't stop it
        if (mAnimation.isRunning()) {
            mAnimation.goToValue(mTargetFillValue);
        }

        // update
        if (mStartAnimationAtOnceEnabled && !mAnimation.isRunning()) {
            startAnimation();
        }
    }


    /**
     * @brief Get the current target value.
     */
    public float getTargetFillValue() {
        return mTargetFillValue;
    }


    /**
     * @brief Set the total amount of available resource units.
     *
     * The total fill value stands for 100% of the resource units that can probably be used/consumed. So this is the
     * maximum value of the usage bar.
     */
    public void setTotalFillValue(float totalValue) {
        // anything to do?
        if (totalValue == mTotalFillValue) return;

        // security
        if (totalValue < 0.0f) {
            totalValue = 0.0f;
        }
        if (totalValue < mCurrentFillValue) {
            mCurrentFillValue = totalValue;
        }
        if (totalValue < mTargetFillValue) {
            mTargetFillValue = totalValue;
        }

        // remember
        mTotalFillValue = totalValue;

        // send event
        sendEvent(PDEUsageEvent.EVENT_ACTION_NEW_TOTAL_VALUE);

        // update
        mLabel.setTotalValue(mTotalFillValue);
        //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
        // --- do NOT delete this out commented code ----
        //mLabelAlternativeColor.setTotalValue(mTotalFillValue);

        if (mAnimation.isRunning()) {
            // if animation is running: stop animation, reset to zero and start with old target
            float target = mTargetFillValue;
            setCurrentFillValue(0);
            mTargetFillValue = target;
            startAnimation();
        } else {
            // otherwise just do a hard repaint
            updateChildren();
        }
    }


    /**
     * @brief Get the maximum value of the usage bar.
     */
    public float getTotalFillValue() {
        return mTotalFillValue;
    }


    /**
     * @brief Overwritten onAttachToWindow function is used for automatic start of animations when entering the screen.
     */
    @Override
    protected void onAttachedToWindow() {
        setVisibleOnScreen(true);
        super.onAttachedToWindow();
    }


    /**
     * @param duration in milliseconds
     * @brief Set the duration the animation should take to fill the usage bar from 0% to 100%.
     *
     * The time period which can be set with this function is the duration the animation should take to fill the usage
     * bar from zero to the maximum value. Generally the target fill value will be below the maximum value. In these
     * cases the animation duration will automatically be scaled down accordingly.
     */
    @SuppressWarnings("unused")
    public void setFillDurationToTotalFillValue(long duration) {
        // anything to do?
        if (duration == mFillDurationToTotalFillValue) return;

        // security check
        if (duration < 0) {
            Log.d(LOG_TAG, "ERROR: Duration value must be greater or equal zero");
            return;
        }

        // remember
        mFillDurationToTotalFillValue = duration;
    }


    /**
     * @brief Get the fill duration of the bar.
     */
    @SuppressWarnings("unused")
    public long getFillDurationToTotalFillValue() {
        return mFillDurationToTotalFillValue;
    }


    /**
     * @brief Set text that should be shown in the unit part of the label.
     *
     * It was defined that the unit text may not have more than three letters. All given strings that exceed this length
     * will be cut.
     */
    public void setUnitText(String text) {
        // anything to do?
        if (PDEString.isEqual(mUnitText, text)) return;

        // we only accept units up to 3 letters
        if (text.length() > 3) {
            Log.d(LOG_TAG, "The Unit String is too long! Must be less than 4 letters!" +
                           " More then 3 letters will be cut off.");
            text = text.substring(0, 3);
        }

        // remember
        mUnitText = text;

        // update
        mLabel.setUnitText(mUnitText);
        //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
        // --- do NOT delete this out commented code ----
        //mLabelAlternativeColor.setUnitText(mUnitText);
    }


    /**
     * @brief Get the unit text.
     */
    @SuppressWarnings("unused")
    public String getUnitText() {
        return mUnitText;
    }

//----- Measurement ----------------------------------------------------------------------------------------------------


    /**
     * @brief Needed for proper measurement/layouting.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredHeight = measureMinimumHeight();
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;

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

        // because we know our size, tell the children exactly how big they should be
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(resolveSize(width, widthMeasureSpec), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(resolveSize(height, heightMeasureSpec), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    /**
     * @brief Measure and deliver the maximum width of the label.
     *
     * The maximum width of the label is the width it has when the usage bar is completely filled. In this moment the
     * largest available numbers for this usage bar should be used which results in the maximum width of the label.
     * If the label is deactivated the result is zero.
     */
    @SuppressWarnings("unused")
    public int measureLabelMaxWidth() {
        if (mLabelEnabled) {
            return mLabel.measureMaxWidth();
        } else {
            return 0;
        }
    }


    /**
     * @brief Measures the height the bar should have in order to display the content correctly.
     */
    public int measureMinimumHeight() {
        int labelHeight, barHeight;

        // if the label is activated, the label needs a certain height in order to show the text uncut.
        if (isLabelEnabled()) {
            labelHeight = mLabel.measureHeight();
        } else {
            labelHeight = 0;
        }
        if (mBarStyle == PDEUsageBarStyle.PDEUsageBarStyleFullBar && labelHeight < CONFIGURATION_MIN_HEIGHT_FULL_BAR) {
            barHeight = CONFIGURATION_MIN_HEIGHT_FULL_BAR;
        } else if (mBarStyle == PDEUsageBarStyle.PDEUsageBarStyleSmallBar
                   && labelHeight < CONFIGURATION_MIN_HEIGHT_SMALL_BAR) {
            barHeight = CONFIGURATION_MIN_HEIGHT_SMALL_BAR;
        } else {
            barHeight = labelHeight;
        }

        return barHeight;
    }


    /**
     * @brief Helper that updates label and background.
     */
    protected void updateChildren() {
        // prevent divide by zero
        mBackgroundView.setCurrentFillValueRelative((mTotalFillValue > 0)
                                                    ? (mCurrentFillValue / mTotalFillValue)
                                                    : 0.0f);
        mLabel.setCurrentFillValue(mCurrentFillValue);
        //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
        // --- do NOT delete this out commented code ----
        //mLabelAlternativeColor.setCurrentFillValue(mCurrentFillValue);
    }

    //----------------------------- PDE Events -------------------------------------------------------------------------


    /**
     * @return PDEEventSource
     * @brief Get the eventSource which is responsible for sending PDEEvents events.
     * Most of the events are coming form the PDEAgentController.
     */
    @Override
    public PDEEventSource getEventSource() {
        return mEventSource;
    }


    /**
     * @param target     Object which will be called in case of an event.
     * @param methodName Function in the target object which will be called.
     *                   The method must accept one parameter of the type PDEEvent
     * @return Object which can be used to remove this listener
     * @brief Add event Listener - hold strong pointer to it.
     * <p/>
     * PDEIEventSource Interface implementation, with additional local storage of (strong) pointer to it.
     * @see de.telekom.pde.codelibrary.ui.events.PDEEventSource#addListener
     */
    @Override
    public Object addListener(Object target, String methodName) {
        mStrongPDEEventListenerHolder.add(target);
        return mEventSource.addListener(target, methodName);
    }


    /**
     * @param target     Object which will be called in case of an event.
     * @param methodName Function in the target object which will be called.
     *                   The method must accept one parameter of the type PDEEvent
     * @param eventMask  PDEAgentController event mask.
     *                   Will be most of the time PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED or
     *                   PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED
     * @return Object which can be used to remove this listener
     * @brief Add event Listener - hold strong pointer to it.
     * <p/>
     * PDEIEventSource Interface implementation, with additional local storage of (strong) pointer to it.
     * @see de.telekom.pde.codelibrary.ui.events.PDEEventSource#addListener
     */
    @Override
    public Object addListener(Object target, String methodName, String eventMask) {
        mStrongPDEEventListenerHolder.add(target);
        return mEventSource.addListener(target, methodName, eventMask);
    }


    /**
     * @param listener the event listener that should be removed
     * @return Returns whether we have found & removed the listener or not
     * @brief Remove event listener that was added before.
     * <p/>
     * Also deletes local strong pointer.
     */
    @SuppressWarnings("unused")
    public boolean removeListener(Object listener) {
        mStrongPDEEventListenerHolder.remove(listener);
        return mEventSource.removeListener(listener);
    }


    /**
     * @brief Helper for sending events of the delivered type.
     */
    protected void sendEvent(String type) {
        PDEUsageEvent usageEvent;
        usageEvent = new PDEUsageEvent();
        usageEvent.setType(type);
        usageEvent.setSender(this);
        usageEvent.setCurrentFillValue(mCurrentFillValue);
        usageEvent.setTotalValue(mTotalFillValue);
        getEventSource().sendEvent(usageEvent);
    }

//----------------------------- MultiColor Label functions ------------------------------------------------------------
    /// @cond MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET

    //// > MULTICOLOR_LABEL_CODE_IS_NOT_RELEASED_YET <
    // --- do NOT delete this out commented code ----
    /*
    protected void checkActivationOfAlternativeLabel()
    {
        if (isLabelColorShiftModeEnabled() && getBarStyle() == PDEUsageBarStyle.PDEUsageBarStyleFullBar && isLabelEnabled()) {
            if (!mAlternativeLabelActive) {
                //addView(mLabelAlternativeColor);
                mLabelAlternativeColor.setVisibility(VISIBLE);
                mLabel.setMaskingMode(PDEUsageBarLabel.PDEUsageBarLabelMaskingMode.PDEUsageBarLabelMaskingModeHide);
                mAlternativeLabelActive = true;
            }
        } else {
            if (mAlternativeLabelActive) {
                //removeView(mLabelAlternativeColor);
                mLabelAlternativeColor.setVisibility(GONE);
                mLabel.setMaskingMode(PDEUsageBarLabel.PDEUsageBarLabelMaskingMode.PDEUsageBarLabelMaskingModeNone);
                mAlternativeLabelActive = false;
            }
        }
    }

    protected void setLabelColorShiftModeEnabled(boolean labelColorShiftMode){
        // anything to do?
        if (mLabelColorShiftMode == labelColorShiftMode) return;
        // remember
        mLabelColorShiftMode = labelColorShiftMode;

        checkActivationOfAlternativeLabel();
    }


    protected boolean isLabelColorShiftModeEnabled(){
        return mLabelColorShiftMode;
    }


    public void setAlternativeTextColorCustom(PDEColor color)
    {
        // remember
        mAlternativeTextColorCustom = color;

        //update
        mLabelAlternativeColor.setTextColorCustom(mAlternativeTextColorCustom);
    }

    public void setAlternativeTextColorCustom(int color) {
        setAlternativeTextColorCustom(PDEColor.valueOf(color));
    }




    @SuppressWarnings("unused")
    public PDEColor getAlternativeTextColorCustom() {
        return mAlternativeTextColorCustom;
    }


    @SuppressWarnings("unused")
    public void setAlternativeTextColorCustomWithString(String colorString)
    {
        setAlternativeTextColorCustom(PDEColor.valueOf(colorString));
    }
*/
    /// @endcond
}
