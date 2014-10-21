/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */


package de.telekom.pde.codelibrary.ui.components.datavisualisation.usagecircle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import de.telekom.pde.codelibrary.ui.animation.PDEAnimationRoot;
import de.telekom.pde.codelibrary.ui.animation.PDEParametricCurveAnimation;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.datavisualisation.PDEUsageEvent;
import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDEString;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;

import java.util.ArrayList;


//----------------------------------------------------------------------------------------------------------------------
//  PDEUsageCircle
//----------------------------------------------------------------------------------------------------------------------


public class PDEUsageCircle extends View implements PDEIEventSource {

    // debug control
    private final static boolean SHOW_DEBUG_MEASURE_LOGS = false;
    private final static boolean SHOW_DEBUG_LAYOUT_AND_DRAW_LOGS = false;
    private final static boolean SHOW_DEBUG_CORE_FUNCTIONS_LOGS = false;

    private final static String LOG_TAG = PDEUsageCircle.class.getSimpleName();

    // secondary color has 30 % alpha of primary color (0.3f * 255)
    protected final static int CONFIGURATION_RELATION_PRIMARY_TO_SECONDARY_COLOR_ALPHA = 77;
    // set default fill duration to 2 seconds
    protected final static int CONFIGURATION_DEFAULT_FILL_DURATION = 2000;
    // set default total fill value to 10
    protected final static float CONFIGURATION_DEFAULT_TOTAL_FILL_VALUE = 0.0f;


    /**
     * @brief Style of the PDEUsageCircle.
     */
    public enum PDEUsageCircleStyle {
        PDEUsageCircleStyleDefault,
        PDEUsageCircleStyleLeadCircle,
        PDEUsageCircleStyleSlimCircle,
        PDEUsageCircleStyleSegmented,
        PDEUsageCircleStyleDivided
    }


    private float mCurrentFillValue;
    private PDEColor mColor;
    private PDEColor mUsageCircleSecondaryBackgroundColor;
    private Paint mPrimaryPaint;
    private Paint mSecondaryPaint;
    private int mFillDurationToTotalFillValue;
    private float mTargetFillValue;
    private float mTotalFillValue;
    private int mNumberOfSegments;
    private int mMaximumNumberOfSegments;
    private int mCurrentPathNumberOfSegments;
    private float mCurrentPathRadius;
    private int mNumberOfDecimalPlaces;
    private PDEUsageCircleStyle mCircleStyle;
    private String mUnitText;
    private Boolean mCircleEnabled;
    private Boolean mTextViewsEnabled;
    private Boolean mUnitTextEnabled;
    private Boolean mTotalTextEnabled;
    private Boolean mStartAnimationAtOnceEnabled;
    protected boolean mVisibleOnScreen;

    private PDEParametricCurveAnimation mAnimation;
    private float mRadius;

    private Paint mTextPaint1, mTextPaint2, mUnitPaint1, mUnitPaint2, mLinePaint, mLeadPaint;
    private Paint.FontMetrics mTextMetrics;
    private String mText2, mFormatString;
    private float mTextHeight, mTextWidth2, mGapWidth, mCapHeight;
    private Path mInnerCirclePath, mSegmentPath, mFillCirclePath;
    private RectF mCircleRect;

    /**
     * @brief PDEEventSource instance that provides the event sending behaviour.
     */
    private PDEEventSource mEventSource;
    protected ArrayList<Object> mStrongPDEEventListenerHolder;


    /**
     * @brief Constructor.
     */
    public PDEUsageCircle(Context context) {
        super(context);
        init(context, null);
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEUsageCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEUsageCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    /**
     * @brief Initialize.
     */
    protected void init(Context context, AttributeSet attrs) {
        //Log.d(LOG_TAG, "init");

        mCurrentFillValue = 0.0f;
        mTargetFillValue = 0.0f;
        mTotalFillValue = CONFIGURATION_DEFAULT_TOTAL_FILL_VALUE;
        mFillDurationToTotalFillValue = CONFIGURATION_DEFAULT_FILL_DURATION;
        mCircleEnabled = true;
        mTextViewsEnabled = true;
        mUnitTextEnabled = true;
        mTotalTextEnabled = true;
        mStartAnimationAtOnceEnabled = false;
        mNumberOfDecimalPlaces = 0;
        mMaximumNumberOfSegments = 0;
        mCurrentPathNumberOfSegments = -1;
        mCurrentPathRadius = -1;
        mCircleStyle = PDEUsageCircleStyle.PDEUsageCircleStyleDefault;
        mVisibleOnScreen = false;

        mColor = new PDEColor();
        mColor.setColor(PDEColor.valueOf("DTDVLightBlue").getIntegerColor());
        mUsageCircleSecondaryBackgroundColor
                = mColor.newColorWithCombinedAlpha(CONFIGURATION_RELATION_PRIMARY_TO_SECONDARY_COLOR_ALPHA);

        mPrimaryPaint = new Paint();
        mSecondaryPaint = new Paint();
        mPrimaryPaint.setColor(mColor.getIntegerColor());
        mSecondaryPaint.setColor(mUsageCircleSecondaryBackgroundColor.getIntegerColor());

        // set the paints to antiAlias - only cheap devices we have otherwise have white pixels
        mPrimaryPaint.setAntiAlias(true);
        mSecondaryPaint.setAntiAlias(true);

        mAnimation = new PDEParametricCurveAnimation();
        PDEAnimationRoot.addSubAnimationStatic(mAnimation);
        mAnimation.setDidChangeTarget(this, "timeAnimations");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // turn off hardware acceleration (clipPath)
            PDEUtils.setLayerTypeSoftwareToView(this);

        }

        // init event source
        mEventSource = new PDEEventSource();
        // set ourselves as the default sender (optional)
        mEventSource.setEventDefaultSender(this, true);
        mStrongPDEEventListenerHolder = new ArrayList<Object>();

        setAttributes(context, attrs);
    }


    /**
     * @brief Load XML attributes.
     */
    private void setAttributes(Context context, AttributeSet attrs) {
        // valid?
        if (attrs == null || context == null) return;

        TypedArray sa;

        sa = context.obtainStyledAttributes(attrs, R.styleable.PDEUsageCircle);

        if (sa != null) {
            // set total fill value
            if (sa.hasValue(R.styleable.PDEUsageCircle_pde_totalFillValue)) {
                setTotalFillValue(sa.getFloat(R.styleable.PDEUsageCircle_pde_totalFillValue,
                                              CONFIGURATION_DEFAULT_TOTAL_FILL_VALUE));
            }

            // set current fill value
            if (sa.hasValue(R.styleable.PDEUsageCircle_pde_currentFillValue)) {
                setCurrentFillValue(sa.getFloat(R.styleable.PDEUsageCircle_pde_currentFillValue, 0.0f));
            }

            // set fill duration
            if (sa.hasValue(R.styleable.PDEUsageCircle_pde_fillDuration)) {
                setFillDurationToTotalFillValue(sa.getInteger(R.styleable.PDEUsageCircle_pde_fillDuration,
                                                              CONFIGURATION_DEFAULT_FILL_DURATION));
            }

            // set number of decimal places
            if (sa.hasValue(R.styleable.PDEUsageCircle_pde_numberOfDecimalPlaces)) {
                setNumberOfDecimalPlaces(sa.getInteger(R.styleable.PDEUsageCircle_pde_numberOfDecimalPlaces, 0));
            }

            // set max segments
            if (sa.hasValue(R.styleable.PDEUsageCircle_pde_numberOfSegments)) {
                setNumberOfSegments(sa.getInteger(R.styleable.PDEUsageCircle_pde_numberOfSegments, 0));
            }

            // set circle enabled
            if (sa.hasValue(R.styleable.PDEUsageCircle_pde_circleEnabled)) {
                setCircleEnabled(sa.getBoolean(R.styleable.PDEUsageCircle_pde_circleEnabled, true));
            }

            // set total text enabled
            if (sa.hasValue(R.styleable.PDEUsageCircle_pde_textViewsEnabled)) {
                setTextViewsEnabled(sa.getBoolean(R.styleable.PDEUsageCircle_pde_textViewsEnabled, true));
            }

            // set unit text enabled
            if (sa.hasValue(R.styleable.PDEUsageCircle_pde_unitTextEnabled)) {
                setUnitTextEnabled(sa.getBoolean(R.styleable.PDEUsageCircle_pde_unitTextEnabled, true));
            }

            // set show current value only
            if (sa.hasValue(R.styleable.PDEUsageCircle_pde_totalTextEnabled)) {
                setTotalTextEnabled(sa.getBoolean(R.styleable.PDEUsageCircle_pde_totalTextEnabled, true));
            }

            // set start animation at once
            if (sa.hasValue(R.styleable.PDEUsageCircle_pde_startAnimationAtOnceEnabled)) {
                setStartAnimationAtOnceEnabled(
                        sa.getBoolean(R.styleable.PDEUsageCircle_pde_startAnimationAtOnceEnabled, false));
            }

            // set unit text
            if (sa.hasValue(R.styleable.PDEUsageCircle_pde_unitText)) {
                setUnitText(sa.getString(R.styleable.PDEUsageCircle_pde_unitText));
            }

            // set color
            if (sa.hasValue(R.styleable.PDEUsageCircle_pde_color)) {
                int resourceID = sa.getResourceId(R.styleable.PDEUsageCircle_pde_color, 0);
                if (resourceID != 0) {
                    setColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setColor(sa.getColor(R.styleable.PDEUsageCircle_pde_color, R.color.DTDVLightBlue));
                }
            }

            // set style
            if (sa.hasValue(R.styleable.PDEUsageCircle_pde_circleStyle)) {
                setCircleStyle(sa.getInteger(R.styleable.PDEUsageCircle_pde_circleStyle, 0));
            }

            // set target fill value
            if (sa.hasValue(R.styleable.PDEUsageCircle_pde_targetFillValue)) {
                setTargetFillValue(sa.getFloat(R.styleable.PDEUsageCircle_pde_targetFillValue, 0.0f));
            }

            sa.recycle();
        }
    }


    /**
     * @brief Set main color of the usage circle.
     * <p/>
     * The usage circle generally has a main color. This is the color which shows the filled part of the circle.
     * However, other parts of the circle are colored with colors which are derived directly from this main color. So
     * only this base color can be set, the derived colors are calculated and set automatically.
     */
    public void setColor(PDEColor color) {
        // anything to do?
        if (color == mColor) return;

        // remember
        mColor = color;
        mUsageCircleSecondaryBackgroundColor
                = color.newColorWithCombinedAlpha(CONFIGURATION_RELATION_PRIMARY_TO_SECONDARY_COLOR_ALPHA);

        mPrimaryPaint.setColor(mColor.getIntegerColor());
        mSecondaryPaint.setColor(mUsageCircleSecondaryBackgroundColor.getIntegerColor());

        //update
        doOneTimeLayoutOperations();
        invalidate();
    }


    /**
     * @brief Set circle color by integer.
     */
    public void setColor(int color) {
        setColor(PDEColor.valueOf(color));
    }


    /**
     * @brief Get circle color.
     */
    @SuppressWarnings("unused")
    public PDEColor getColor() {
        return mColor;
    }


    /**
     * @brief Set the target value (absolute) at which the animation should end.
     * <p/>
     * The task of the usage circle is to show which amount of a given resource has been used, yet. The target fill
     * value is this amount of used units. So the filling animation will run until it reaches this "target" value.
     */
    public void setTargetFillValue(float targetValue) {
        // anything to do?
        if (targetValue == mTargetFillValue) return;

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

        // start animation when auto start is enabled
        if (mStartAnimationAtOnceEnabled && !mAnimation.isRunning()) {
            startAnimation();
        }
    }


    /**
     * @brief Get target fill value.
     */
    @SuppressWarnings("unused")
    public float getTargetFillValue() {

        return mTargetFillValue;
    }


    /**
     * @brief Set the total number (absolute) of available resource units.
     * <p/>
     * The total fill value stands for 100% of the resource units that can probably be used/consumed. So this is the
     * maximum (absolute) value of the usage circle.
     */
    public void setTotalFillValue(float totalValue) {
        // anything to do?
        if (totalValue == mTotalFillValue) return;

        // security checks
        if (totalValue < 0.0f) {
            totalValue = 0.0f;
        }

        if ((totalValue > 9999 && mNumberOfDecimalPlaces == 0) ||
            (totalValue > 999 && mNumberOfDecimalPlaces == 1) ||
            (totalValue > 99 && mNumberOfDecimalPlaces == 2) ||
            (totalValue > 9 && mNumberOfDecimalPlaces == 3)) {
            Log.w(LOG_TAG, "setTotalFillValue: Value including decimals cannot have more than 4 digits!");
            return;
        }

        if (totalValue < mTargetFillValue) {
            mTargetFillValue = totalValue;
        }

        if (totalValue < mCurrentFillValue) {
            mCurrentFillValue = totalValue;
        }

        // remember
        mTotalFillValue = totalValue;

        //send event
        sendUsageEvent(PDEUsageEvent.EVENT_ACTION_NEW_TOTAL_VALUE, mCurrentFillValue, mTotalFillValue);

        //update
        doOneTimeLayoutOperations();
        invalidate();

        if (mAnimation.isRunning()) {
            // remember the target
            float rememberTargetValue = mTargetFillValue;
            // this will reset to 0 and stop the animation
            setCurrentFillValue(0);
            mTargetFillValue = rememberTargetValue;
            startAnimation();
        }
    }


    /**
     * @brief Get total fill value.
     */
    @SuppressWarnings("unused")
    public float getTotalFillValue() {
        return mTotalFillValue;
    }


    /**
     * @brief Set target- and total-value with one function call.
     * <p/>
     * This is a convenience function in order to set target- and total value at once with one function call.
     */
    @SuppressWarnings("unused")
    public void setTargetAndTotalFillValue(float targetVal, float totalVal) {
        setTotalFillValue(totalVal);
        setTargetFillValue(targetVal);
    }


    /**
     * @brief Set the duration the animation should take to fill the usage circle from 0% to 100%.
     * <p/>
     * The time period which can be set with this function is the duration the animation should take to fill the usage
     * circle from zero to the maximum value. Generally the target fill value will be below the maximum value. In these
     * cases the animation duration will automatically be scaled down accordingly.
     * @param duration in milliseconds
     */
    public void setFillDurationToTotalFillValue(int duration) {
        // anything to do?
        if (duration == mFillDurationToTotalFillValue) return;

        // security check
        if (duration < 0) {
            Log.w(LOG_TAG, "setFillDurationToTotalFillValue: Duration value must be greater or equal zero");
            return;
        }
        // remember
        mFillDurationToTotalFillValue = duration;
    }


    /**
     * @brief Get fill duration to total fill value.
     *
     * @return in milliseconds
     */
    @SuppressWarnings("unused")
    public int getFillDurationToTotalFillValue() {
        return mFillDurationToTotalFillValue;
    }


    /**
     * @brief Set amount of segments the circle is divided into in segmented and divided style.
     * <p/>
     * If set to zero the amount of segments is equal to the total fill value. Amount cannot be greater than a
     * maximum amount depending on the radius of the circle, to make sure the segments are not too small. The maximum
     * amount is calculated automatically.
     */
    public void setNumberOfSegments(int segmentsAmount) {
        // changed?
        if (mNumberOfSegments == segmentsAmount) return;

        // security
        if (segmentsAmount < 0) {
            segmentsAmount = 0;
        }

        // update
        stopAnimation();
        mNumberOfSegments = segmentsAmount;

        //update
        doOneTimeLayoutOperations();
        invalidate();
    }


    /**
     * @brief Get number of segments.
     *
     * If 0 then the number of segments is equal to the total fill value.
     */
    @SuppressWarnings("unused")
    public int getNumberOfSegments() {
        return mNumberOfSegments;
    }


    /**
     * @brief Set the number of decimal places we want to show.
     * <p/>
     * Possible amount is limited to make sure there are not more than 4 digits.
     */
    public void setNumberOfDecimalPlaces(int decimals) {
        // anything to do?
        if (decimals == mNumberOfDecimalPlaces) return;

        if (decimals > 3 || decimals < 0) {
            Log.w(LOG_TAG, "setNumberOfDecimalPlaces: The number of decimal places must be between 0 and 3!");
            return;
        }

        if ((mTotalFillValue > 9999 && decimals == 0)
            || (mTotalFillValue > 999 && decimals == 1)
            || (mTotalFillValue > 99 && decimals == 2)
            || (mTotalFillValue > 9 && decimals == 3)) {
            Log.w(LOG_TAG, "setNumberOfDecimalPlaces: Value including decimals cannot have more than 4 digits!");
            return;
        }

        // remember
        mNumberOfDecimalPlaces = decimals;

        //update
        doOneTimeLayoutOperations();
        invalidate();
    }


    /**
     * @brief Get number of decimal places.
     */
    @SuppressWarnings("unused")
    public int getNumberOfDecimalPlaces() {
        return mNumberOfDecimalPlaces;
    }


    /**
     * @brief Set optical style of the circle.
     * <p/>
     * Possible values are PDEUsageCircleStyleDefault, PDEUsageCircleStyleLeadCircle,  PDEUsageCircleStyleSlimCircle,
     * PDEUsageCircleStyleSegmented, PDEUsageCircleStyleDivided
     */
    public void setCircleStyle(PDEUsageCircleStyle circleStyle) {
        //changed?
        if (mCircleStyle == circleStyle) return;

        // remember
        stopAnimation();
        mCircleStyle = circleStyle;


        if (circleStyle == PDEUsageCircleStyle.PDEUsageCircleStyleSegmented) {
            mAnimation.setEaseInType(PDEParametricCurveAnimation.PDEParametricCurveAnimationEase.EaseLinear);
            mAnimation.setEaseOutType(PDEParametricCurveAnimation.PDEParametricCurveAnimationEase.EaseLinear);
        } else {
            mAnimation.setEaseInType(PDEParametricCurveAnimation.PDEParametricCurveAnimationEase.EaseSoft);
            mAnimation.setEaseOutType(PDEParametricCurveAnimation.PDEParametricCurveAnimationEase.EaseSoft);
        }

        //update
        doOneTimeLayoutOperations();
        invalidate();
    }


    /**
     * @brief Set optical style by number.
     */
    public void setCircleStyle(int circleStyle) {
        PDEUsageCircleStyle style;
        try {
            style = PDEUsageCircleStyle.values()[circleStyle];
        } catch (Exception e) {
            style = PDEUsageCircleStyle.PDEUsageCircleStyleDefault;
        }
        setCircleStyle(style);
    }


    /**
     * @brief Set optical style using a string of the circle.
     * <p/>
     * Possible values are PDEUsageCircleStyleDefault, PDEUsageCircleStyleLeadCircle,  PDEUsageCircleStyleSlimCircle,
     * PDEUsageCircleStyleSegmented, PDEUsageCircleStyleDivided or Default, LeadCircle, SlimCircle,
     * Segmented, Divided.
     */
    @SuppressWarnings("unused")
    public void setCircleStyleByString(String circleStyleString) {
        PDEUsageCircleStyle style = PDEUsageCircleStyle.PDEUsageCircleStyleDefault;

        if (PDEString.isEqualCaseIndependent(circleStyleString, "Default") ||
            PDEString.isEqualCaseIndependent(circleStyleString, "PDEUsageCircleStyleDefault")) {
            style = PDEUsageCircleStyle.PDEUsageCircleStyleDefault;
        } else if (PDEString.isEqualCaseIndependent(circleStyleString, "Segmented") ||
                   PDEString.isEqualCaseIndependent(circleStyleString, "PDEUsageCircleStyleSegmented")) {
            style = PDEUsageCircleStyle.PDEUsageCircleStyleSegmented;
        } else if (PDEString.isEqualCaseIndependent(circleStyleString, "LeadCircle") ||
                   PDEString.isEqualCaseIndependent(circleStyleString, "PDEUsageCircleStyleLeadCircle")) {
            style = PDEUsageCircleStyle.PDEUsageCircleStyleLeadCircle;
        } else if (PDEString.isEqualCaseIndependent(circleStyleString, "SlimCircle") ||
                   PDEString.isEqualCaseIndependent(circleStyleString, "PDEUsageCircleStyleSlimCircle")) {
            style = PDEUsageCircleStyle.PDEUsageCircleStyleSlimCircle;
        } else if (PDEString.isEqualCaseIndependent(circleStyleString, "Divided") ||
                   PDEString.isEqualCaseIndependent(circleStyleString, "PDEUsageCircleStyleDivided")) {
            style = PDEUsageCircleStyle.PDEUsageCircleStyleDivided;
        }

        setCircleStyle(style);
    }


    /**
     * @brief Get Circle Style.
     */
    @SuppressWarnings("unused")
    public PDEUsageCircleStyle getCircleStyle() {
        return mCircleStyle;
    }


    /**
     * @brief Set Unit text.
     */
    public void setUnitText(String unitText) {
        // any change?
        if (PDEString.isEqual(unitText, mUnitText)) return;

        // we only accept units up to 3 letters
        if (unitText.length() > 3) {
            Log.w(LOG_TAG,
                  "setUnitText The Unit String is too long! Must be smaller than 4 letters! " +
                  "Rest of the string will be cut."
            );
            unitText = unitText.substring(0, 3);
        }

        // remember
        mUnitText = unitText;

        //update
        doOneTimeLayoutOperations();
        invalidate();
    }


    /**
     * @brief Get unit text.
     */
    @SuppressWarnings("unused")
    public String getUnitText() {
        return mUnitText;
    }


    /**
     * @brief Set whether circle is enabled.
     */
    public void setCircleEnabled(Boolean circleEnabled) {
        // anything to do?
        if (mCircleEnabled == circleEnabled) return;

        // remember
        mCircleEnabled = circleEnabled;

        //update
        invalidate();
    }


    /**
     * @brief Get whether circle is enabled.
     */
    @SuppressWarnings("unused")
    public Boolean isCircleEnabled() {
        return mCircleEnabled;
    }


    /**
     * @brief Set whether text is shown.
     */
    public void setTextViewsEnabled(Boolean textViewsEnabled) {
        // anything to do?
        if (mTextViewsEnabled == textViewsEnabled) return;

        // remember
        mTextViewsEnabled = textViewsEnabled;

        //update
        invalidate();
    }


    /**
     * @brief Get whether text is shown.
     */
    @SuppressWarnings("unused")
    public Boolean isTextViewsEnabled() {
        return mTextViewsEnabled;
    }


    /**
     * @brief Set whether unit is shown.
     */
    public void setUnitTextEnabled(Boolean unitTextEnabled) {
        // anything to do?
        if (mUnitTextEnabled == unitTextEnabled) return;

        // remember
        mUnitTextEnabled = unitTextEnabled;

        //update
        doOneTimeLayoutOperations();
        invalidate();
    }


    /**
     * @brief Get whether unit is shown.
     */
    @SuppressWarnings("unused")
    public Boolean isUnitTextEnabled() {
        return mUnitTextEnabled;
    }


    /**
     * @brief Turn on/off the part of the text label that shows the total number.
     */
    public void setTotalTextEnabled(Boolean enabled) {
        // anything to do?
        if (mTotalTextEnabled == enabled) return;

        // remember
        mTotalTextEnabled = enabled;

        //update
        invalidate();
    }


    /**
     * @brief Check if the part of the text label that shows the total value is turned on or off.
     */
    @SuppressWarnings("unused")
    public Boolean isTotalTextEnabled() {
        return mTotalTextEnabled;
    }


    /**
     * @brief Enable auto start of animation when there is a new value.
     */
    public void setStartAnimationAtOnceEnabled(Boolean autoStartEnabled) {
        // anything to do?
        if (mStartAnimationAtOnceEnabled == autoStartEnabled) return;

        // remember
        mStartAnimationAtOnceEnabled = autoStartEnabled;
    }


    /**
     * @brief Get whether auto start of the animation is enabled.
     */
    @SuppressWarnings("unused")
    public Boolean isStartAnimationAtOnceEnabled() {
        return mStartAnimationAtOnceEnabled;
    }


    /**
     * @brief Timing function of animation.
     */
    @SuppressWarnings("unused")
    public void timeAnimations() {
        updateCurrentFillValue((float) mAnimation.getValue());

        if (!mAnimation.isRunning()) {
            sendUsageEvent(PDEUsageEvent.EVENT_ACTION_ANIMATION_FINISHED, mCurrentFillValue, mTotalFillValue);
        }
    }


    /**
     * @brief Stop all animations.
     */
    public void stopAnimation() {
        //stop animations
        mAnimation.stopAnimation();
    }


    /**
     * @brief Starts the filling animation.
     */
    public void startAnimation() {
        // check if the animation is already running.
        if (mAnimation.isRunning()) return;

        // check if there is a value change
        if (mCurrentFillValue == mTargetFillValue) return;

        // check if the component is visible
        if (!isVisibleOnScreen()) return;

        if (mAnimation.getValue() != mCurrentFillValue) {
            mAnimation.setValueImmediate(mCurrentFillValue);
        }
        mAnimation.setBaseTime((long) mFillDurationToTotalFillValue);
        mAnimation.setBaseDistance(mTotalFillValue);
        mAnimation.goToValue(mTargetFillValue);
    }


    /**
     * @brief Set new fill value of the usage circle.
     */
    public void setCurrentFillValue(float currFillValue) {
        // security
        if (currFillValue > mTotalFillValue) currFillValue = mTotalFillValue;
        if (currFillValue < 0.0f) currFillValue = 0.0f;

        //stop the animation if it is running
        if (mAnimation.isRunning()) {
            stopAnimation();
        }

        updateCurrentFillValue(currFillValue);
        mTargetFillValue = currFillValue;

        mAnimation.setValueImmediate(currFillValue);
    }


    /**
     * @brief Set new fill value of the usage circle.
     */
    private void updateCurrentFillValue(float currFillValue) {

        // anything to do?
        if (currFillValue == mCurrentFillValue) return;

        // security
        if (currFillValue > mTotalFillValue) currFillValue = mTotalFillValue;
        if (currFillValue < 0.0f) currFillValue = 0.0f;

        // remember
        mCurrentFillValue = currFillValue;

        //send event
        sendUsageEvent(PDEUsageEvent.EVENT_ACTION_NEW_FILL_VALUE, mCurrentFillValue, mTotalFillValue);

        // update
        invalidate();
    }


    /**
     * Calculate maximum amount of segments based on radius, so the segments are at least 2 times as wide as the space
     * between them.
     */
    private void calculateMaximumNumberOfSegments(float radius) {
        if (radius == 0) return;

        // gap is 4, so for ratio where segments are double as wide as gaps
        float ratio = 12;
        mMaximumNumberOfSegments = (int) Math.floor((2.0f * radius * Math.PI) / ratio);
    }


    /**
     * @brief Create segments path for PDEUsageCircleStyleClearSegment.
     * <p/>
     * Creates segment mask for circle, depending on radius and segment count.
     */
    private Path createSegmentPath(int segmentCount) {
        float currentAngle, segmentAngle, gapAngle;
        float innerRadius;
        Path showPath;
        showPath = new Path();

        // determine segment count
        if (segmentCount < 1) segmentCount = Math.round(mTotalFillValue);
        if (segmentCount < 1) return null;
        if (segmentCount > mMaximumNumberOfSegments) segmentCount = mMaximumNumberOfSegments;

        //prevent unnecessary recreation of path if segments amount and radius have not changed
        if (segmentCount == mCurrentPathNumberOfSegments && mCurrentPathRadius == mRadius) {
            return mSegmentPath;
        } else {
            mCurrentPathNumberOfSegments = segmentCount;
            mCurrentPathRadius = mRadius;
        }

        // get angle of the space between segments
        if (mRadius > 0) {
            gapAngle = (float) (2.0f / (2.0f * mRadius * Math.PI)) * 360.0f;
        } else {
            gapAngle = 0;
        }

        // starting angle
        currentAngle = gapAngle - 90.0f;

        // get segment angle
        segmentAngle = 360.0f / segmentCount - 2.0f * gapAngle;
        innerRadius = 0.86f * mRadius;

        // draw single segments
        for (int i = 0; i < segmentCount; i++) {
            showPath.moveTo((float) (mRadius + Math.sin(degreesToRadians(currentAngle + 90.0f)) * innerRadius),
                            (float) (mRadius - Math.cos(degreesToRadians(currentAngle + 90.0f)) * innerRadius));

            showPath.lineTo((float) (mRadius + Math.sin(degreesToRadians(currentAngle + 90.0f)) * mRadius),
                            (float) (mRadius - Math.cos(degreesToRadians(currentAngle + 90.0f)) * mRadius));

            showPath.arcTo(new RectF(0, 0, 2.0f * mRadius, 2.0f * mRadius), currentAngle, segmentAngle);

            showPath.lineTo((float) (mRadius
                                     + Math.sin(degreesToRadians(currentAngle + 90.0f + segmentAngle)) * innerRadius),
                            (float) (mRadius
                                     - Math.cos(degreesToRadians(currentAngle + 90.0f + segmentAngle)) * innerRadius)
            );

            showPath.arcTo(new RectF(mRadius - innerRadius, mRadius - innerRadius,
                                     mRadius + innerRadius, mRadius + innerRadius),
                           currentAngle + segmentAngle, -segmentAngle
            );


            //increase angle
            currentAngle += segmentAngle + 2.0f * gapAngle;
        }

        return showPath;
    }


    /**
     * @brief Helper function to transform degrees to radians
     */
    private float degreesToRadians(float deg) {
        return (float) ((deg / 360.f) * 2.0f * Math.PI);
    }


    /**
     * @brief Helper function to create decimal string from number of decimal places
     */
    private String getFormatString() {
        if (mNumberOfDecimalPlaces <= 0) return "%.0f";
        if (mNumberOfDecimalPlaces == 1) return "%.1f";
        if (mNumberOfDecimalPlaces == 2) return "%.2f";
        return "%.3f";
    }


    /**
     * @brief Do all initializations which are only done once before the animation starts
     */
    private void doOneTimeLayoutOperations() {

        if (SHOW_DEBUG_CORE_FUNCTIONS_LOGS) {
            Log.d(LOG_TAG, "doOneTimeLayoutOperations");
        }
        mRadius = Math.min(getHeight(), getWidth()) / 2.0f;

        //security
        if (mRadius <= 0) return;

        float innerCircleRadius, textWidth, textHeight;
        Rect textBounds;

        // calculate maximum number of segments
        calculateMaximumNumberOfSegments(mRadius);

        //create circle rect
        mCircleRect = new RectF(0, 0, 2 * mRadius, 2 * mRadius);

        // create paints
        mTextPaint1 = new Paint();
        mTextPaint1.setColor(mColor.getIntegerColor());
        mTextPaint1.setTextSize(0.5f * mRadius);
        mTextPaint1.setTypeface(PDETypeface.sDefaultFont.getTypeface());

        mTextPaint2 = new Paint();
        mTextPaint2.setColor(mUsageCircleSecondaryBackgroundColor.getIntegerColor());
        mTextPaint2.setTextSize(0.5f * mRadius);
        mTextPaint2.setTypeface(PDETypeface.sDefaultFont.getTypeface());

        mUnitPaint1 = new Paint();
        mUnitPaint1.setColor(mColor.getIntegerColor());
        mUnitPaint1.setTextSize(0.25f * mRadius);
        mUnitPaint1.setTypeface(PDETypeface.sDefaultFont.getTypeface());

        mUnitPaint2 = new Paint();
        mUnitPaint2.setColor(mUsageCircleSecondaryBackgroundColor.getIntegerColor());
        mUnitPaint2.setTextSize(0.25f * mRadius);
        mUnitPaint2.setTypeface(PDETypeface.sDefaultFont.getTypeface());

        mLinePaint = new Paint();
        mLinePaint.setColor(mUsageCircleSecondaryBackgroundColor.getIntegerColor());
        mLinePaint.setStrokeWidth(2);

        // text initialization
        mFormatString = getFormatString();
        mTextMetrics = mTextPaint2.getFontMetrics();
        mText2 = String.format(mFormatString, mTotalFillValue);

        textBounds = new Rect();
        mTextPaint2.getTextBounds("0,", 0, 2, textBounds);
        mTextHeight = textBounds.height();
        if (!PDEString.isEmpty(mText2)) {
            mTextWidth2 = mTextPaint2.measureText(mText2);
        } else {
            mTextWidth2 = 0;
        }

        mGapWidth = mUnitPaint2.measureText(" ");

        // create circle mask
        if (mCircleStyle == PDEUsageCircleStyle.PDEUsageCircleStyleSlimCircle) {
            innerCircleRadius = 0.96f * mRadius;
        } else {
            innerCircleRadius = 0.86f * mRadius;
        }

        mInnerCirclePath = new Path();
        mInnerCirclePath.setFillType(Path.FillType.EVEN_ODD);
        mInnerCirclePath.addRect(0, 0, 2 * mRadius, 2 * mRadius, Path.Direction.CW);
        mInnerCirclePath.addCircle(mRadius, mRadius, innerCircleRadius, Path.Direction.CW);


        if (mCircleStyle == PDEUsageCircleStyle.PDEUsageCircleStyleSegmented ||
            mCircleStyle == PDEUsageCircleStyle.PDEUsageCircleStyleDivided) {
            mSegmentPath = createSegmentPath(mNumberOfSegments);
        }

        if (mCircleStyle == PDEUsageCircleStyle.PDEUsageCircleStyleLeadCircle) {
            mLeadPaint = new Paint();
            mLeadPaint.setColor(mColor.getIntegerColor());
            mLeadPaint.setStyle(Paint.Style.STROKE);
        }


        // calculate text widths
        textWidth = mTextWidth2 / 2.0f;
        if (mUnitTextEnabled && !PDEString.isEmpty(mUnitText))
            textWidth += mGapWidth + mUnitPaint2.measureText(mUnitText);
        textHeight = mTextHeight;
        if (!mTotalTextEnabled) textHeight = mTextHeight / 2.0f;

        if (isUnitTextEnabled() && !TextUtils.isEmpty(mUnitText)) {
            // reduce font size if necessary, to prevent unit text colliding with the circle
            // (and keep some distance from the circle - 10 % of the circle radius)
            //noinspection SuspiciousNameCombination
            while (Math.sqrt(Math.pow(textWidth, 2) + Math.pow(textHeight, 2)) > innerCircleRadius * 0.9f) {
                // reduce the text size by 5 %
                mTextPaint1.setTextSize(mTextPaint1.getTextSize() * 0.95f);
                mTextPaint2.setTextSize(mTextPaint2.getTextSize() * 0.95f);
                mUnitPaint1.setTextSize(mUnitPaint1.getTextSize() * 0.95f);
                mUnitPaint2.setTextSize(mUnitPaint2.getTextSize() * 0.95f);

                textBounds = new Rect();
                mTextPaint2.getTextBounds("0,", 0, 2, textBounds);
                mTextHeight = textBounds.height();
                if (!PDEString.isEmpty(mText2)) {
                    mTextWidth2 = mTextPaint2.measureText(mText2);
                } else {
                    mTextWidth2 = 0;
                }

                mGapWidth = mUnitPaint2.measureText(" ");

                textWidth = mTextWidth2 / 2.0f;
                if (mUnitTextEnabled && !PDEString.isEmpty(mUnitText))
                    textWidth += mGapWidth + mUnitPaint2.measureText(mUnitText);
                textHeight = mTextHeight;
                if (!mTotalTextEnabled) textHeight = mTextHeight / 2.0f;
            }
        }

        // update text metrics
        mTextMetrics = mTextPaint2.getFontMetrics();
        mCapHeight = PDEFontHelpers.getCapHeight(mTextPaint2.getTypeface(), mTextPaint2.getTextSize());

        mFillCirclePath = new Path();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (SHOW_DEBUG_LAYOUT_AND_DRAW_LOGS) {
            Log.d(LOG_TAG, "onSizeChanged (" + w + ", " + h + " - " + oldw + ", " + oldh + ")");
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (SHOW_DEBUG_LAYOUT_AND_DRAW_LOGS) {
            Log.d(LOG_TAG, "onLayout (" + (changed ? "changed" : "not changed") + ", "
                           + left + ", " + top + ", " + right + ", " + bottom + ")");
        }
    }


    /**
     * @brief Update layout.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (SHOW_DEBUG_LAYOUT_AND_DRAW_LOGS) {
            Log.d(LOG_TAG, "onDraw");
        }

        int currSegCount, usedTotalSegCount;
        float fillAngle, segmentAngle, textWidth1;
        String text1;

        // security
        if (mPrimaryPaint == null) return;
        if (mSecondaryPaint == null) return;

        // do one time layout operations when radius has changed
        if ((Math.min(getHeight(), getWidth()) / 2.0f) != mRadius) {
            doOneTimeLayoutOperations();
        }

        // security
        if (mRadius <= 0) return;

        //draw text
        if (mTextViewsEnabled) {
            text1 = String.format(mFormatString, mCurrentFillValue);
            if (!PDEString.isEmpty(text1)) {
                textWidth1 = mTextPaint1.measureText(text1);
            } else {
                textWidth1 = 0;
            }

            if (!mTotalTextEnabled) {
                canvas.drawText(text1, mRadius - 0.5f * textWidth1, mRadius + 0.5f * mTextHeight, mTextPaint1);

                if (mUnitTextEnabled && !PDEString.isEmpty(mUnitText)) {
                    canvas.drawText(mUnitText, mRadius + 0.5f * textWidth1 + mGapWidth,
                                    mRadius + 0.5f * mTextHeight, mUnitPaint1);
                }
            } else {
                // texts have same distance between divider and there CapHeight
                canvas.drawText(text1,
                                mRadius - 0.5f * textWidth1,
                                mRadius - (mTextMetrics.bottom * .66f),
                                mTextPaint1);
                canvas.drawText(mText2,
                                mRadius - 0.5f * mTextWidth2, mRadius + mCapHeight + (mTextMetrics.bottom * .66f),
                                mTextPaint2);
                canvas.drawLine(mRadius - 0.5f * mTextWidth2,
                                mRadius,
                                mRadius + 0.5f * mTextWidth2,
                                mRadius,
                                mLinePaint);

                if (mUnitTextEnabled && !PDEString.isEmpty(mUnitText)) {
                    canvas.drawText(mUnitText, mRadius + 0.5f * mTextWidth2 + mGapWidth,
                                    mRadius - (mTextMetrics.bottom * .66f), mUnitPaint1);
                    canvas.drawText(mUnitText,
                                    mRadius + 0.5f * mTextWidth2 + mGapWidth,
                                    mRadius + mCapHeight + (mTextMetrics.bottom * .66f),
                                    mUnitPaint2);
                }
            }
        }


        //draw circle
        if (mCircleEnabled) {
            if ((mCircleStyle == PDEUsageCircleStyle.PDEUsageCircleStyleSegmented ||
                mCircleStyle == PDEUsageCircleStyle.PDEUsageCircleStyleDivided) && mSegmentPath != null) {
                canvas.clipPath(mSegmentPath);
            } else {
                canvas.clipPath(mInnerCirclePath);
            }

            if (mCircleStyle == PDEUsageCircleStyle.PDEUsageCircleStyleLeadCircle) {
                canvas.drawCircle(mRadius, mRadius, mRadius, mLeadPaint);
            } else {
                canvas.drawCircle(mRadius, mRadius, mRadius, mSecondaryPaint);
            }

            fillAngle = mCurrentFillValue / mTotalFillValue * 360;

            // if segmented circle get quantified angle
            if (mCircleStyle == PDEUsageCircleStyle.PDEUsageCircleStyleSegmented) {
                if (mNumberOfSegments != 0) {
                    usedTotalSegCount = mNumberOfSegments;
                } else {
                    usedTotalSegCount = Math.round(mTotalFillValue);
                }
                if (usedTotalSegCount > mMaximumNumberOfSegments) usedTotalSegCount = mMaximumNumberOfSegments;

                segmentAngle = 360.0f / usedTotalSegCount;

                //new segment if value > value necessary for former segment
                currSegCount = (int) Math.ceil(fillAngle / segmentAngle);

                //new segment if value >=  0.5* value necessary for new segment
                //currSegCount = round(currAngle / segmentAngle);

                //new segment if value = value necessary for new segment
                //currSegCount = floor(currAngle / segmentAngle);

                fillAngle = currSegCount * segmentAngle;
            }

            // create and apply progress mask
            mFillCirclePath.reset();
            mFillCirclePath.moveTo(mRadius, mRadius);
            mFillCirclePath.lineTo(mRadius, 0);
            mFillCirclePath.addArc(mCircleRect, -90, fillAngle);
            mFillCirclePath.lineTo(mRadius, mRadius);

            canvas.drawPath(mFillCirclePath, mPrimaryPaint);
        }
    }


    /**
     * @brief Determine layout size of element.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (SHOW_DEBUG_MEASURE_LOGS) {
            Log.d(LOG_TAG, "onMeasure " + MeasureSpec.toString(widthMeasureSpec) + " x "
                           + MeasureSpec.toString(heightMeasureSpec));
        }

        int height, width, diameter;
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);

        // take height/width from the parameter ...
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);

        // if unspecified, return
        if ((widthSpecMode == MeasureSpec.UNSPECIFIED && width == 0) &&
            (heightSpecMode == MeasureSpec.UNSPECIFIED && height == 0)) {
            setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                                 resolveSize(height, heightMeasureSpec));
            return;
        }

        if (widthSpecMode == MeasureSpec.UNSPECIFIED && width == 0) {
            diameter = height;
            width = diameter;
        } else if (heightSpecMode == MeasureSpec.UNSPECIFIED && height == 0) {
            diameter = width;
            height = diameter;
        } else {
            diameter = Math.min(width, height);
            width = diameter;
            height = diameter;
        }

        // return the values
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                             resolveSize(height, heightMeasureSpec));

        if (SHOW_DEBUG_MEASURE_LOGS) {
            Log.d(LOG_TAG, "onMeasure result: " + getMeasuredWidth() + " x " + getMeasuredHeight());
        }
    }


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


    protected boolean isVisibleOnScreen() {
        return mVisibleOnScreen;
    }


    protected void onAttachedToWindow() {
        setVisibleOnScreen(true);
        super.onAttachedToWindow();
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
     * @brief Sends an event when currentFillValue has changed.
     */
    public void sendUsageEvent(String type, float currentFillValue, float total) {
        PDEUsageEvent event = new PDEUsageEvent();
        // set the information
        event.setCurrentFillValue(currentFillValue);
        event.setTotalValue(total);
        event.setType(type);
        event.setSender(this);

        // send PDEUsageEvent
        getEventSource().sendEvent(event);
    }


}



