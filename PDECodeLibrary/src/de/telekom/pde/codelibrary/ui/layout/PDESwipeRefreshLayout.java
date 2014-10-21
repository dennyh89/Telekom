package de.telekom.pde.codelibrary.ui.layout;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;

/*
 * This code has been modified by Neuland Multimedia GmbH and is based on
 * SwipeRefreshLayout from the Android Open Source Project
 *  - This code is not a contribution -
 *
 *
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * The PDESwipeRefreshLayout should be used whenever the user can refresh the
 * contents of a view via a vertical swipe gesture. The activity that
 * instantiates this view should add an OnRefreshListener to be notified
 * whenever the swipe to refresh gesture is completed. The SwipeRefreshLayout
 * will notify the listener each and every time the gesture is completed again;
 * the listener is responsible for correctly determining when to actually
 * initiate a refresh of its content. If the listener determines there should
 * not be a refresh, it must call setRefreshing(false) to cancel any visual
 * indication of a refresh. If an activity wishes to show just the progress
 * animation, it should call setRefreshing(true). To disable the gesture and progress
 * animation, call setEnabled(false) on the view.
 *
 * <p> This layout should be made the parent of the view that will be refreshed as a
 * result of the gesture and can only support one direct child. This view will
 * also be made the target of the gesture and will be forced to match both the
 * width and the height supplied in this layout. The SwipeRefreshLayout does not
 * provide accessibility events; instead, a menu item must be provided to allow
 * refresh of the content wherever this gesture is used.</p>
 */
public class PDESwipeRefreshLayout extends ViewGroup {

    private static final long RETURN_TO_ORIGINAL_POSITION_TIMEOUT = 300;
    private static final float ACCELERATE_INTERPOLATION_FACTOR = 1.5f;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int CIRCLE_HEIGHT = PDEBuildingUnits.BU() * 4;
    private static final float MAX_SWIPE_DISTANCE_FACTOR = .6f;
    private static final int REFRESH_TRIGGER_DISTANCE = 120;
    private static final float ARC_RADIUS = 117.0f;

    // Default progress animation color.
    private final static int COLOR = PDEColor.valueOf("DTGrey2").getIntegerColor();
    private final static int WHITE_COLOR = PDEColor.valueOf("DTWhite").getIntegerColor();

    private int mAnimationDuration = 1120;
    private View mTarget; //the content that gets pulled down
    private int mOriginalOffsetTop;
    private OnRefreshListener mListener;
    private MotionEvent mDownEvent;
    private int mFrom;
    private boolean mRefreshing = false;
    private int mTouchSlop;
    private float mDistanceToTriggerSync = -1;
    private float mPrevY;
    private int mMediumAnimationDuration;
    private float mFromPercentage = 0;
    private float mCurrPercentage = 0;
    private int mCircleHeight;
    private int mCurrentTargetOffsetTop;
    // Target is returning to its start offset because it was cancelled or a
    // refresh was triggered.
    private boolean mReturningToStart;
    private final DecelerateInterpolator mDecelerateInterpolator;
    private final AccelerateInterpolator mAccelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.enabled
    };

    private Paint mPaint;
    private Paint mCirclePaint;
    private Paint mWhitePaint;
    private float mTriggerPercentage;
    private long mStartTime;
    private boolean mRunning;
    private Path mCirclePath;

    private Rect mBounds = new Rect();


    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop = 0;
            if (mFrom != mOriginalOffsetTop) {
                targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
            }
            int offset = targetTop - mTarget.getTop();
            final int currentTop = mTarget.getTop();
            if (offset + currentTop < 0) {
                offset = 0 - currentTop;
            }

            setTargetOffsetTopAndBottom(offset);
        }
    };

    private Animation mShrinkTrigger = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            float percent = mFromPercentage + ((0 - mFromPercentage) * interpolatedTime);
            setTriggerPercentage(percent);
        }
    };

    private final AnimationListener mReturnToStartPositionListener = new BaseAnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            // Once the target content has returned to its start position, reset
            // the target offset to 0
            mCurrentTargetOffsetTop = 0;
        }
    };

    private final AnimationListener mShrinkAnimationListener = new BaseAnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            mCurrPercentage = 0;
        }
    };

    private final Runnable mReturnToStartPosition = new Runnable() {
        @Override
        public void run() {
            mReturningToStart = true;
            animateOffsetToStartPosition(mCurrentTargetOffsetTop + getPaddingTop(),
                                         mReturnToStartPositionListener);
        }

    };

    // Cancel the refresh gesture and animate everything back to its original state.
    private final Runnable mCancel = new Runnable() {

        @Override
        public void run() {
            mReturningToStart = true;
            // Timeout fired since the user last moved their finger; animate the
            // trigger to 0 and put the target back at its original position
            mFromPercentage = mCurrPercentage;
            mShrinkTrigger.setDuration(mMediumAnimationDuration);
            mShrinkTrigger.setAnimationListener(mShrinkAnimationListener);
            mShrinkTrigger.reset();
            mShrinkTrigger.setInterpolator(mDecelerateInterpolator);
            startAnimation(mShrinkTrigger);

            animateOffsetToStartPosition(mCurrentTargetOffsetTop + getPaddingTop(),
                                         mReturnToStartPositionListener);
        }

    };


    /**
     * @brief Simple constructor to use when creating a SwipeRefreshLayout from code.
     */
    @SuppressWarnings("unused")
    public PDESwipeRefreshLayout(Context context) {
        this(context, null);
    }


    /**
     * @brief Constructor that is called when inflating SwipeRefreshLayout from XML.
     */
    public PDESwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        setWillNotDraw(false);
        mCircleHeight = CIRCLE_HEIGHT;
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mAccelerateInterpolator = new AccelerateInterpolator(ACCELERATE_INTERPOLATION_FACTOR);

        mPaint = new Paint();
        mCirclePaint = new Paint();
        setCircleColor(COLOR);
        mWhitePaint = new Paint();
        mWhitePaint.setColor(WHITE_COLOR);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();

        setAttributes(context, attrs);
    }


    /**
     * @brief Load XML attributes.
     */
    private void setAttributes(Context context, AttributeSet attrs) {
        // valid?
        if (attrs == null || context == null) return;

        TypedArray sa;

        sa = context.obtainStyledAttributes(attrs, R.styleable.PDESwipeRefreshLayout);

        if (sa != null) {
            // set animation duration
            if (sa.hasValue(R.styleable.PDESwipeRefreshLayout_pde_animationDuration)) {
                setAnimationDuration(sa.getInteger(R.styleable.PDESwipeRefreshLayout_pde_animationDuration,
                                                   1120));
            }

            // set circle color
            if (sa.hasValue(R.styleable.PDESwipeRefreshLayout_pde_color)) {
                int resourceID = sa.getResourceId(R.styleable.PDESwipeRefreshLayout_pde_color, 0);
                if (resourceID != 0) {
                    setCircleColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setCircleColor(sa.getColor(R.styleable.PDESwipeRefreshLayout_pde_color, R.color.DTMagenta));
                }
            }

            sa.recycle();
        }
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        removeCallbacks(mCancel);
        removeCallbacks(mReturnToStartPosition);
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mReturnToStartPosition);
        removeCallbacks(mCancel);
    }


    private void animateOffsetToStartPosition(int from, AnimationListener listener) {
        mFrom = from;
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(mMediumAnimationDuration);
        mAnimateToStartPosition.setAnimationListener(listener);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mTarget.startAnimation(mAnimateToStartPosition);
    }


    /**
     * @brief Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }


    private void setTriggerPercentage(float percent) {
        if (percent == 0f) {
            // No-op. A null trigger means it's uninitialized, and setting it to zero-percent
            // means we're trying to reset state, so there's nothing to reset in this case.
            mCurrPercentage = 0;
            return;
        }

        mCurrPercentage = percent;
        mTriggerPercentage = percent;
        mStartTime = 0;
        ViewCompat.postInvalidateOnAnimation(this);


    }


    /**
     * @brief Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (mRefreshing != refreshing) {
            ensureTarget();
            mCurrPercentage = 0;
            mRefreshing = refreshing;
            if (mRefreshing) {
                start();

            } else {
                stop();

                animateOffsetToStartPosition(mCurrentTargetOffsetTop + getPaddingTop(),
                                             mReturnToStartPositionListener);
            }
        }
    }


    /**
     * @brief Set circle color.
     */
    public void setCircleColor(int color) {
        mPaint.setColor(color);
        mCirclePaint.setColor(color);
    }


    public void setCircleColor(PDEColor color) {
        setCircleColor(color.getIntegerColor());
    }


    /**
     * @brief Set Animation Duration.
     *
     * @param duration Animation Duration in ms.
     */
    public void setAnimationDuration(int duration) {
        mAnimationDuration = duration;
    }


    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     *         progress.
     */
    @SuppressWarnings("unused")
    public boolean isRefreshing() {
        return mRefreshing;
    }


    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid out yet.
        if (mTarget == null) {
            if (getChildCount() > 1 && !isInEditMode()) {
                throw new IllegalStateException(
                        "SwipeRefreshLayout can host only one direct child");
            }
            mTarget = getChildAt(0);
            mOriginalOffsetTop = mTarget.getTop() + getPaddingTop();
        }
        if (mDistanceToTriggerSync == -1) {
            if (getParent() != null && ((View) getParent()).getHeight() > 0) {
                final DisplayMetrics metrics = getResources().getDisplayMetrics();
                mDistanceToTriggerSync = (int) Math.min(
                        ((View) getParent()).getHeight() * MAX_SWIPE_DISTANCE_FACTOR,
                        REFRESH_TRIGGER_DISTANCE * metrics.density);
            }
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        setBounds(0, 0, width, mCircleHeight);

        if (getChildCount() == 0) {
            return;
        }
        final View child = getChildAt(0);
        final int childLeft = getPaddingLeft();
        final int childTop = mCurrentTargetOffsetTop + getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() > 1 && !isInEditMode()) {
            throw new IllegalStateException("PDESwipeRefreshLayout can host only one direct child");
        }
        if (getChildCount() > 0) {
            getChildAt(0).measure(
                    MeasureSpec.makeMeasureSpec(
                            getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                            MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(
                            getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),
                            MeasureSpec.EXACTLY));
        }
    }


    /**
     * @return Whether it is possible for the child view of this layout to
     *         scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                       && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                                                                                   .getTop()
                                                                        < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        boolean handled = false;
        if (mReturningToStart && ev.getAction() == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
        if (isEnabled() && !mReturningToStart && !canChildScrollUp()) {
            handled = onTouchEvent(ev);
        }
        return !handled ? super.onInterceptTouchEvent(ev) : true;
    }


    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // Nope.
    }


    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final int action = event.getAction();
        boolean handled = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mCurrPercentage = 0;
                mDownEvent = MotionEvent.obtain(event);
                mPrevY = mDownEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mDownEvent != null && !mReturningToStart) {
                    final float eventY = event.getY();
                    float yDiff = eventY - mDownEvent.getY();
                    if (yDiff > mTouchSlop) {
                        // User velocity passed min velocity; trigger a refresh
                        if (yDiff > mDistanceToTriggerSync) {
                            // User movement passed distance; trigger a refresh
                            startRefresh();
                            handled = true;
                            break;
                        } else {
                            // Just track the user's movement
                            setTriggerPercentage(
                                    mAccelerateInterpolator.getInterpolation(
                                            yDiff / mDistanceToTriggerSync));
                            float offsetTop = yDiff;
                            if (mPrevY > eventY) {
                                offsetTop = yDiff - mTouchSlop;
                            }
                            updateContentOffsetTop((int) (offsetTop));
                            if (mPrevY > eventY && (mTarget.getTop() < mTouchSlop)) {
                                // If the user puts the view back at the top, we
                                // don't need to. This shouldn't be considered
                                // cancelling the gesture as the user can restart from the top.
                                removeCallbacks(mCancel);
                            } else {
                                updatePositionTimeout();
                            }
                            mPrevY = event.getY();
                            handled = true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mDownEvent != null) {
                    mDownEvent.recycle();
                    mDownEvent = null;
                }
                break;
        }
        return handled;
    }


    private void startRefresh() {
        removeCallbacks(mCancel);
        mReturnToStartPosition.run();
        setRefreshing(true);
        mListener.onRefresh();
    }


    private void updateContentOffsetTop(int targetTop) {
        final int currentTop = mTarget.getTop();
        if (targetTop > mDistanceToTriggerSync) {
            targetTop = (int) mDistanceToTriggerSync;
        } else if (targetTop < 0) {
            targetTop = 0;
        }
        setTargetOffsetTopAndBottom(targetTop - currentTop);
    }


    private void setTargetOffsetTopAndBottom(int offset) {
        if (mRefreshing) offset += mCircleHeight;
        mTarget.offsetTopAndBottom(offset);
        mCurrentTargetOffsetTop = mTarget.getTop();
    }


    private void updatePositionTimeout() {
        removeCallbacks(mCancel);
        postDelayed(mCancel, RETURN_TO_ORIGINAL_POSITION_TIMEOUT);
    }


    /**
     * @brief Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnRefreshListener {
        public void onRefresh();
    }


    /**
     * @brief Simple AnimationListener to avoid having to implement unneeded methods in
     * AnimationListeners.
     */
    private class BaseAnimationListener implements AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }


        @Override
        public void onAnimationEnd(Animation animation) {
        }


        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }





    /**
     * @brief Start showing the progress animation.
     */
    void start() {
        if (!mRunning) {
            mTriggerPercentage = 0;
            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            mRunning = true;
            drawCirclePath();
            postInvalidate();
        }
    }

    /**
     * @brief Stop showing the progress animation.
     */
    void stop() {
        if (mRunning) {
            mTriggerPercentage = 0;
            mRunning = false;
            postInvalidate();
        }
    }


    /**
     * @return Return whether the progress animation is currently running.
     */
    boolean isRunning() {
        return mRunning;
    }



    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);

        int width = mBounds.width();
        int height = mBounds.height();
        int cx = width / 2;
        int cy = height / 2;

        int restoreCount = canvas.save();
        canvas.clipRect(mBounds);

        if (mRunning) {
            long now = AnimationUtils.currentAnimationTimeMillis();
            long elapsed = (now - mStartTime) % mAnimationDuration;
            float currentAngle = (elapsed / (mAnimationDuration / 100f))*3.6f;

            canvas.drawCircle(cx, cy, PDEBuildingUnits.BU(), mPaint);

            canvas.save();
            canvas.rotate(currentAngle,cx,cy);
            canvas.drawPath(mCirclePath, mWhitePaint);
            canvas.restore();

            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            // Otherwise if we're in the middle of a trigger, draw that.
            if (mTriggerPercentage > 0 && mTriggerPercentage <= 1.0) {
                drawTrigger(canvas, cx, cy);
            }
        }
        canvas.restoreToCount(restoreCount);
    }


    /**
     * @brief Draw Circle Path
     */
    private void drawCirclePath() {
        int width = mBounds.width();
        int height = mBounds.height();
        int cx = width / 2;
        int cy = height / 2;

        float innerRadius = 0.7f*PDEBuildingUnits.BU();
        float outerRadius = 0.9f*PDEBuildingUnits.BU();

        mCirclePath = new Path();
        mCirclePath.moveTo(cx, cy - innerRadius);
        mCirclePath.lineTo(cx, cy - outerRadius);

        mCirclePath.arcTo(new RectF(cx - outerRadius, cy - outerRadius, cx + outerRadius, cy + outerRadius),
                          -90.0f, ARC_RADIUS);

        mCirclePath.lineTo((float) (cx
                                    + Math.sin(degreesToRadians(ARC_RADIUS)) * innerRadius),
                           (float) (cy
                                    - Math.cos(degreesToRadians(ARC_RADIUS)) * innerRadius));


        mCirclePath.arcTo(new RectF(cx - innerRadius, cy - innerRadius,
                                    cx + innerRadius, cy + innerRadius),
                          -90.0f + ARC_RADIUS, -ARC_RADIUS);
    }


    private void drawTrigger(Canvas canvas, int cx, int cy) {
        float innerRadius;
        float outerRadius;
        Path circlePath;


        if (mTriggerPercentage < 0.05f) return;

        innerRadius = 0.7f*PDEBuildingUnits.BU();
        outerRadius = 0.9f*PDEBuildingUnits.BU();

        canvas.drawCircle(cx, cy, PDEBuildingUnits.BU(), mPaint);

        circlePath = new Path();

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

        canvas.drawPath(circlePath, mWhitePaint);

    }


    /**
     * @brief Set the drawing bounds of this SwipeProgressCircle.
     */
    void setBounds(int left, int top, int right, int bottom) {
        mBounds.left = left;
        mBounds.top = top;
        mBounds.right = right;
        mBounds.bottom = bottom;
        drawCirclePath();
    }


    /**
     * @brief Helper function to transform degrees to radians
     */
    private float degreesToRadians(float deg) {
        return (float) ((deg / 360.f) * 2.0f * Math.PI);
    }
}




