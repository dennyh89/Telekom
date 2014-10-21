/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */


package de.telekom.pde.codelibrary.ui.elements.complex;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.R;


//----------------------------------------------------------------------------------------------------------------------
//  PDEActivityIndicator
//----------------------------------------------------------------------------------------------------------------------


public class PDEActivityIndicator extends View {

    private final static String LOG_TAG = PDEActivityIndicator.class.getSimpleName();

    /**
     * @brief Mode of the PDEActivityIndicator, spinning or trigger.
     */
    public enum PDEActivityIndicatorMode {
        PDEActivityIndicatorModeSpinning,
        PDEActivityIndicatorModeTrigger
    }


    // animation duration
    private final static int ANIMATION_DURATION = 1120;
    private final static float DIAMETER = 2.0f*PDEBuildingUnits.BU();
    private final static float ARC_RADIUS = 117.0f;

    // colors
    private final static int COLOR = PDEColor.valueOf("DTGrey2").getIntegerColor();
    private final static int WHITE_COLOR = PDEColor.valueOf("DTWhite").getIntegerColor();

    private boolean mRunning;
    private PDEActivityIndicatorMode mMode;
    private int mAnimationDuration;
    private float mCurrentAngle;
    private long mTimeDifference;

    private Paint mPaint = new Paint();
    private Paint mWhitePaint = new Paint();
    private float mTriggerPercentage;
    private float mDiameter;
    private long mStartTime;
    Path mCirclePath = new Path();
    RectF mCircleRect = new RectF();


    /**
     * @brief Constructor.
     */
    public PDEActivityIndicator(Context context) {
        super(context);
        init(context, null);
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEActivityIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEActivityIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    /**
     * @brief Initialize.
     */
    protected void init(Context context, AttributeSet attrs) {
        mRunning = false;
        mAnimationDuration = ANIMATION_DURATION;
        setColor(COLOR);
        mWhitePaint.setColor(WHITE_COLOR);
        mRunning = true;
        mMode = PDEActivityIndicatorMode.PDEActivityIndicatorModeSpinning;
        mTriggerPercentage = 0;
        mCurrentAngle = 0;
        mTimeDifference = 0;
        mDiameter = DIAMETER;

        // set the paints to antiAlias - only cheap devices we have otherwise have white pixels
        mPaint.setAntiAlias(true);
        mWhitePaint.setAntiAlias(true);

        setAttributes(context, attrs);
    }


    /**
     * @brief Load XML attributes.
     */
    private void setAttributes(Context context, AttributeSet attrs) {
        // valid?
        if (attrs == null || context == null) return;

        TypedArray sa;

        sa = context.obtainStyledAttributes(attrs, R.styleable.PDEActivityIndicator );

        if (sa != null) {

            // set animation duration
            if (sa.hasValue(R.styleable.PDEActivityIndicator_pde_animationDuration)) {
                setAnimationDuration(sa.getInteger(R.styleable.PDEActivityIndicator_pde_animationDuration,
                                                   ANIMATION_DURATION));
            }

            // set diameter
            if (sa.hasValue(R.styleable.PDEActivityIndicator_pde_diameter)) {
                setDiameter(sa.getDimension(R.styleable.PDEActivityIndicator_pde_diameter, DIAMETER));
            }

            // set auto start
            if (sa.hasValue(R.styleable.PDEActivityIndicator_pde_startAnimationAtOnceEnabled)) {
                mRunning = sa.getBoolean(R.styleable.PDEActivityIndicator_pde_startAnimationAtOnceEnabled, true);
            }

            // set trigger Percentage
            if (sa.hasValue(R.styleable.PDEActivityIndicator_pde_triggerPercentage)) {
                setTriggerPercentage(sa.getFloat(R.styleable.PDEActivityIndicator_pde_triggerPercentage, 0.0f));
            }

            // set mode
            if (sa.hasValue(R.styleable.PDEActivityIndicator_pde_mode)) {
                setMode(sa.getInteger(R.styleable.PDEActivityIndicator_pde_mode, 0));
            }

            // set color
            if (sa.hasValue(R.styleable.PDEActivityIndicator_pde_color)) {
                int resourceID = sa.getResourceId(R.styleable.PDEActivityIndicator_pde_color, 0);
                if (resourceID != 0) {
                    setColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setColor(sa.getColor(R.styleable.PDEActivityIndicator_pde_color, COLOR));
                }
            }

            sa.recycle();
        }
    }


    /**
     * @brief Set Color.
     */
    public void setColor(PDEColor color) {
        setColor(color.getIntegerColor());
    }


    /**
     * @brief Set Color.
     */
    public void setColor(int color) {
        mPaint.setColor(color);
    }


    /**
     * @brief Set trigger percentage.
     */
    @SuppressWarnings("unused")
    public void setTriggerPercentage(float pct) {
        if (pct == mTriggerPercentage) return;
        if (pct < 0) pct = 0;
        if (pct > 1.00f) pct = 1.00f;

        setMode(PDEActivityIndicatorMode.PDEActivityIndicatorModeTrigger);
        mTriggerPercentage = pct;
        invalidate();
    }

    /**
     * @brief Get trigger percentage.
     */
    @SuppressWarnings("unused")
    public float getTriggerPercentage() {
        return mTriggerPercentage;
    }


    /**
     * @brief Set circle diameter.
     */
    @SuppressWarnings("unused")
    public void setDiameter(float diameter) {
        if (diameter == mDiameter) return;
        if (diameter < 0) return;

        mDiameter = diameter;
        createCirclePath();
        invalidate();
    }


    /**
     * @brief Get circle diameter.
     */
    @SuppressWarnings("unused")
    public float getDiameter() {
        return mDiameter;
    }


    /**
     * @brief Set the duration the animation.
     */
    @SuppressWarnings("unused")
    public void setAnimationDuration(int duration) {
        // anything to do?
        if (duration == mAnimationDuration) return;

        // security check
        if (duration < 0) {
            Log.w(LOG_TAG, "setAnimationDuration: Duration value must be greater or equal zero");
            return;
        }
        // remember
        mAnimationDuration = duration;
    }


    /**
     * @brief Get animation duration.
     *
     * @return in milliseconds
     */
    @SuppressWarnings("unused")
    public int getAnimationDuration() {
        return mAnimationDuration;
    }


    /**
     * @brief Set Mode.
     */
    public void setMode(PDEActivityIndicatorMode mode) {
        // anything to do?
        if (mode == mMode) return;

        // remember
        mMode = mode;
    }

    public void setMode(int mode) {
        PDEActivityIndicatorMode m;
        try {
            m = PDEActivityIndicatorMode.values()[mode];
        } catch (Exception e) {
            m = PDEActivityIndicatorMode.PDEActivityIndicatorModeSpinning;
        }
        setMode(m);
    }


    /**
     * @brief Get Mode.
     */
    @SuppressWarnings("unused")
    public PDEActivityIndicatorMode getMode() {
        return mMode;
    }


    /**
     * @brief Stop all animations.
     */
    public void stop() {
        mRunning = false;
        mTimeDifference = (AnimationUtils.currentAnimationTimeMillis() - mStartTime + mTimeDifference)
                          % mAnimationDuration;
        invalidate();
    }


    /**
     * @brief Starts the animation.
     */
    public void start() {
        mRunning = true;
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
        setMode(PDEActivityIndicatorMode.PDEActivityIndicatorModeSpinning);
        createCirclePath();
        invalidate();
    }


    /**
     * @brief Get if animation is running.
     */
    public boolean isRunning() {
        return mRunning;
    }


    /**
     * @brief Helper function to transform degrees to radians
     */
    private float degreesToRadians(float deg) {
        return (float) ((deg / 360.f) * 2.0f * Math.PI);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createCirclePath();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        createCirclePath();
    }


    /**
     * @brief Create path for the circle segment.
     */
    private void createCirclePath() {
        int width = getWidth();
        int height = getHeight();
        int cx = width / 2;
        int cy = height / 2;
        float radius = mDiameter/2.0f;

        float innerRadius = 0.7f*radius;
        float outerRadius = 0.9f*radius;

        mCirclePath = new Path();
        mCirclePath.moveTo(cx, cy - innerRadius);
        mCirclePath.lineTo(cx, cy - outerRadius);

        mCircleRect.set(cx - outerRadius, cy - outerRadius, cx + outerRadius, cy + outerRadius);
        mCirclePath.arcTo(mCircleRect, -90.0f, ARC_RADIUS);

        mCirclePath.lineTo((float) (cx
                                    + Math.sin(degreesToRadians(ARC_RADIUS)) * innerRadius),
                           (float) (cy
                                    - Math.cos(degreesToRadians(ARC_RADIUS)) * innerRadius));

        mCircleRect.set(cx - innerRadius, cy - innerRadius, cx + innerRadius, cy + innerRadius);
        mCirclePath.arcTo(mCircleRect, -90.0f + ARC_RADIUS, -ARC_RADIUS);
    }



    /**
     * @brief Update layout.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int cx = width / 2;
        int cy = height / 2;
        float radius = mDiameter/2.0f;

        canvas.drawCircle(cx, cy, radius, mPaint);

        if (mMode == PDEActivityIndicatorMode.PDEActivityIndicatorModeSpinning) {
            if (mRunning) {
                long now = AnimationUtils.currentAnimationTimeMillis();
                long elapsed = (now - mStartTime + mTimeDifference) % mAnimationDuration;
                mCurrentAngle = (elapsed / (mAnimationDuration / 100f))*3.6f;
            }

            canvas.save();
            canvas.rotate(mCurrentAngle,cx,cy);
            canvas.drawPath(mCirclePath, mWhitePaint);
            canvas.restore();

            invalidate();
        } else {
            // Otherwise if we're in the middle of a trigger, draw that.
            if (mTriggerPercentage > 0 && mTriggerPercentage <= 1.0) {
                drawTrigger(canvas, cx, cy);
            }
        }
    }


    /**
     * @brief Draw trigger.
     */
    private void drawTrigger(Canvas canvas, int cx, int cy) {
        float innerRadius;
        float outerRadius;
        Path circlePath;
        float radius = mDiameter/2.0f;

        if (mTriggerPercentage < 0.05f) return;

        innerRadius = 0.7f*radius;
        outerRadius = 0.9f*radius;

        circlePath = new Path();

        if (mTriggerPercentage == 1.00f) {
            circlePath.setFillType(Path.FillType.EVEN_ODD);
            circlePath.addCircle(cx, cy, outerRadius, Path.Direction.CW);
            circlePath.addCircle(cx, cy, innerRadius, Path.Direction.CW);
        }  else {
            circlePath.moveTo(cx, cy - innerRadius);
            circlePath.lineTo(cx, cy - outerRadius);

            circlePath.arcTo(new RectF(cx - outerRadius, cy - outerRadius, cx + outerRadius, cy + outerRadius),
                             -90.0f, mTriggerPercentage * 360.0f);

            circlePath.lineTo((float) (cx
                                       + Math.sin(degreesToRadians(mTriggerPercentage*360.0f)) * innerRadius),
                              (float) (cy
                                       - Math.cos(degreesToRadians(mTriggerPercentage*360.0f)) * innerRadius));


            circlePath.arcTo(new RectF(cx - innerRadius, cy - innerRadius,
                                       cx + innerRadius, cy + innerRadius),
                             -90.0f + mTriggerPercentage * 360.0f, -mTriggerPercentage * 360.0f);
        }

        canvas.drawPath(circlePath, mWhitePaint);

    }


    /**
     * @brief Determine layout size of element.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height;
        int width;
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);


        // take height/width from the parameter ...
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);

        int newSize = Math.round(mDiameter + 2*PDEBuildingUnits.BU());

        if (newSize < width) {
            width = newSize;
        }

        if (widthSpecMode == MeasureSpec.UNSPECIFIED && width == 0) {
            width = newSize;
        }


        if (newSize < height) {
            height = newSize;
        }

        if (heightSpecMode == MeasureSpec.UNSPECIFIED && height == 0) {
            height = newSize;
        }

        // return the values
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                             resolveSize(height, heightMeasureSpec));

    }

}



