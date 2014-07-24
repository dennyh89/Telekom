/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.animation;

// framework includes
//


// local includes
//


//----------------------------------------------------------------------------------------------------------------------
//  Configuration
//----------------------------------------------------------------------------------------------------------------------


// debug configuration
//


//----------------------------------------------------------------------------------------------------------------------
//  PDEParametricCurveAnimation
//----------------------------------------------------------------------------------------------------------------------


import android.util.Log;

/**
 * @brief Private methods and properties
 */
@SuppressWarnings("unused")
public class PDEParametricCurveAnimation extends PDEAnimation {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEParametricCurveAnimation.class.getName();
    private final static boolean DEBUG_SHOW = false;


    public enum PDEParametricCurveAnimationMode {
        ModeLinear,
        ModeB3,
        ModeB5
    }


    public enum PDEParametricCurveAnimationTimeMode {
        TimeModeConstant,
        TimeModeLinear,
        TimeModeExponential,
        TimeModeAdaptive
    }


    public enum PDEParametricCurveAnimationEase {
        EaseSoft,
        EaseLinear,
        EaseSpeedOnly
    }


    /**
     * @brief Overshooting control. Does not allow overshoot if a curve is freshly started (this is for
     * completeness only. Under normal circumstances does nothing, since there are no functions
     * where you can supply manual starting parameters. This flag is set per default.
     */
    public static final int FLAG_NONE = 0;
    /**
     * @brief Overshooting control. Does not allow overshoot if a curve is freshly started (this is for
     * completeness only. Under normal circumstances does nothing, since there are no functions
     * where you can supply manual starting parameters. This flag is set per default.
     */
    public static final int FlagNoOvershootStart = 1;
    /**
     * @brief Overshooting control. Does not allow overshoot if an animation is continued with a
     * new target value in the same direction as it's currently moving. This flag is set per default.
     */
    public static final int FlagNoOvershootContinue = 1 << 1;
    /**
     * @brief Overshooting control. Does not allow overshoot if an animation is continued with a
     * new target value in the opposite direction as it's currently moving. This
     * flag is normally not set, since we want the overshoot on the turnaround.
     */
    public static final int FlagNoOvershootTurnaround = 1 << 2;
    /**
     * @brief Overshooting control. Does not allow overshoot at the end of the animation. This flag is
     * set per default.
     */
    public static final int FlagNoOvershootEnd = 1 << 3;
    /**
     * @brief Timing behaviour. Limit time to distance specified in base distance - all longer moves
     * timings are then the same as baseTime. This flag is not default.
     */
    public static final int FlagLimitTime = 1 << 4;
    /**
     * @brief Continue start speed even in hard limited settings.
     */
    public static final int FlagForceSpeedContinuity = 1 << 5;
    /**
     * @brief Continue start acceleration even in hard limited settings.
     *
     * If this flag is set, PDEParametricCurveAnimationFlagForceSpeedContinuity is implied and automatically used.
     */
    public static final int FlagForceAccelerationContinuity = 1 << 6;

    //
    // helper values
    protected double mStartValue;
    protected double mStartSpeed;
    protected double mStartAcceleration;
    protected double mTargetSpeed;
    protected double mTargetAcceleration;
    // bezier values for easier calculations
    protected double[] mBezierControl;

    protected boolean mLimitedStart;
    protected boolean mLimitedEnd;
    protected double mBoundsStart;
    protected double mBoundsEnd;


    // variables with getters and setters
    protected double mValue;
    protected double mTarget;
    protected long mDuration; // == m_moveDuration
    protected double mSpeed;
    protected double mAcceleration;
    protected double mAccelerationDerivative;


    protected long mBaseTime;
    protected double mBaseDistance;
    protected double mTimeExponent;
    protected double mTurnAroundFactor;

    protected PDEParametricCurveAnimationMode mCurveMode;
    protected PDEParametricCurveAnimationTimeMode mTimeMode;
    protected int mFlags;
    protected PDEParametricCurveAnimationEase mEaseInType;
    protected PDEParametricCurveAnimationEase mEaseOutType;


    /**
     * @brief Constructor.
     */
    public PDEParametricCurveAnimation() {
        // init
        init();
    }


    /**
     * @brief Constructor.
     */
    protected void init() {

        if (DEBUG_SHOW) {
            Log.d(LOG_TAG, "init");
        }

        // init
        mValue = 0.0;
        mTarget = 0.0;
        mStartValue = 0.0;
        mDuration = 0;
        mSpeed = 0.0;

        mBezierControl = new double[6];


        mTargetSpeed = 0.0;
        mTargetAcceleration = 0.0;
        for (int i = 0; i < 6; i++) {
            mBezierControl[i] = 0.0;
        }
        mBoundsStart = 0.0;
        mBoundsEnd = 0.0;
        mLimitedStart = false;
        mLimitedEnd = false;
        mValue = 0.0;
        mSpeed = 0.0;
        mAcceleration = 0.0;
        mAccelerationDerivative = 0.0;

        // default configuration
        mCurveMode = PDEParametricCurveAnimationMode.ModeB5;
        mTimeMode = PDEParametricCurveAnimationTimeMode.TimeModeAdaptive;
        mFlags = FlagNoOvershootStart | FlagNoOvershootContinue | FlagNoOvershootEnd;
        mBaseTime = 1000;
        mBaseDistance = 1.0;
        mTimeExponent = 2.0;
        mEaseInType = PDEParametricCurveAnimationEase.EaseSoft;
        mEaseOutType = PDEParametricCurveAnimationEase.EaseSoft;
        mTurnAroundFactor = 0.25;
    }


    public PDEParametricCurveAnimationMode getCurveMode() {
        return mCurveMode;
    }


    public void setCurveMode(PDEParametricCurveAnimationMode curveMode) {
        // stop current
        stopAnimation();

        // ensure valid content
        if (curveMode != PDEParametricCurveAnimationMode.ModeLinear
            && curveMode != PDEParametricCurveAnimationMode.ModeB3
            && curveMode != PDEParametricCurveAnimationMode.ModeB5) {
            curveMode = PDEParametricCurveAnimationMode.ModeB5;
        }

        this.mCurveMode = curveMode;
    }


    public PDEParametricCurveAnimationTimeMode getTimeMode() {
        return mTimeMode;
    }


    public void setTimeMode(PDEParametricCurveAnimationTimeMode timeMode) {
        this.mTimeMode = timeMode;
    }


    public PDEParametricCurveAnimationEase getEaseInType() {
        return mEaseInType;
    }


    public void setEaseInType(PDEParametricCurveAnimationEase easeInType) {
        this.mEaseInType = easeInType;
    }


    public PDEParametricCurveAnimationEase getEaseOutType() {
        return mEaseOutType;
    }


    public void setEaseOutType(PDEParametricCurveAnimationEase easeOutType) {
        this.mEaseOutType = easeOutType;
    }


    public int getFlags() {
        return mFlags;
    }


    public void setFlags(int flags) {
        this.mFlags = flags;
    }


    /**
     * @brief Adds configuration flags. For flag functionality, see flag documentation.
     *
     * Current animation can be kept (although it's unmodified).
     */
    public void addFlags(int flags) {
        // remember
        mFlags = mFlags | flags;
    }


    /**
     * @brief Removes configuration flags. For flag functionality, see flag documentation.
     *
     * Current animation can be kept (although it's unmodified).
     */
    public void removeFlags(int flags) {
        // remember
        mFlags = mFlags & (~flags);
    }


    /**
     * @brief Set base time to be used.
     *
     * Base distance is unchanged. For TimeModeConstant, setting the base time is enough. For
     * all other time modes, use setBaseTimeAndDistance() and see flags documentation.
     * For behaviour of the different timing modes, see time constants documentation.
     *
     * Current animation can be kept (although it's unmodified).
     */
    public void setBaseTime(long baseTime) {
        // ensure positive value
        if (baseTime < 0) baseTime = 0;

        // remember
        mBaseTime = baseTime;
    }


    public long getBaseTime() {
        return mBaseTime;
    }


    /**
     * @brief Set base distance to be used..
     *
     * Base time is unchanged.
     *
     * Current animation can be kept (although it's unmodified).
     */
    public void setBaseDistance(double baseDistance) {
        // ensure positive value
        if (baseDistance < 0.0) baseDistance = 0.0;

        // remember
        mBaseDistance = baseDistance;
    }


    public double getBaseDistance() {
        return mBaseDistance;
    }


    public void setTimeExponent(double timeExponent) {
        this.mTimeExponent = timeExponent;
    }


    public double getTimeExponent() {
        return mTimeExponent;
    }


    /**
     * @brief Sets turnaround influence for adaptive timing.
     *
     * 1.0 means to keep it physically correct (but it seems sluggish then). 0.0 means no time adjustment in
     * this case. Default is 0.25.
     */
    public void setTurnAroundFactor(double factor) {
        // limit between 0 and 1
        if (factor < 0.0) factor = 0.0;
        if (factor > 1.0) factor = 1.0;

        // remember
        mTurnAroundFactor = factor;
    }


    public double getTurnAroundFactor() {
        return mTurnAroundFactor;
    }


    public double getTarget() {
        return mTarget;
    }


    public double getValue() {
        return mValue;
    }


    public long getDuration() {
        return mDuration;
    }


    public double getSpeed() {
        return mSpeed;
    }


    public double getAcceleration() {
        return mAcceleration;
    }


    public double getAccelerationDerivative() {
        return mAccelerationDerivative;
    }


    /**
     * @brief Directly set the value.
     *
     * This stops any running animations.
     *
     * Note the naming: We cannot use setValue: as function name, since this is already defined as
     * setter function for the value property. Simply setting the value shouldn't affect the animation
     * (since we're using the value setting ourself). We also want to stay compliant with KeyValueCoding
     * for the moment.
     */
    public void setValueImmediate(double value) {
        // check for change; if we're stable, do nothing. The time we reached the finished state must
        // not change in this case
        if (!isRunning() && value == mValue) {
            return;
        }

        // set to safe parameters
        mStartValue = value;
        mTarget = value;
        mDuration = 0;
        setSpeed(0.0);

        // set value immediately
        setValue(value);

        // rebase ourself time to zero, stop running
        setTime(0);
        setRunning(false);
    }


    /**
     * @brief Stop the animation immediately.
     *
     * Just sets to the current value and stops running
     */
    public void stopAnimation() {
        // are we running?
        if (!isRunning()) return;

        // set the current output value immediately, this stops.
        setValueImmediate(mValue);
    }


    /**
     * @brief Go to the value in the specified time.
     *
     * Animation is always performed starting with the current value, and using the specified duration,
     * even if there's nothing to animate - there's no check for value changes.
     */
    public void goToValue(double target, long duration) {
        boolean limitStart, limitEnd;

        // special case: no duration
        if (duration == 0.0) {
            // no animation: directly set the target value
            setValueImmediate(target);
            // and stop here
            return;
        }


        // get overshoot mode (depends on current animation => use speed, not the actual old block,
        // since we might be in a previous overshooting range and might have already turned around)
        // it's up to debate if we really need a start overshooting flag (we might also treat it
        // as continuation)
        if (mSpeed == 0.0) {
            limitStart = ((mFlags & FlagNoOvershootStart) == FlagNoOvershootStart);
        } else if (mSpeed > 0.0) {
            if (target >= mValue) {
                limitStart = (mFlags & FlagNoOvershootContinue) == FlagNoOvershootContinue;
            } else {
                limitStart = (mFlags & FlagNoOvershootTurnaround) == FlagNoOvershootTurnaround;
            }
        } else {
            if (target <= mValue) {
                limitStart = (mFlags & FlagNoOvershootContinue) == FlagNoOvershootContinue;
            } else {
                limitStart = (mFlags & FlagNoOvershootTurnaround) == FlagNoOvershootTurnaround;
            }
        }
        limitEnd = (mFlags & FlagNoOvershootEnd) == FlagNoOvershootEnd;

        // remember current value as start value
        mStartValue = mValue;

        // remember target and duration
        mTarget = target;
        mDuration = duration;

        // calculate start/stop speed and acceleration depending on easing type.
        calculateEasing();

        // calculate bezier curve values
        bezierInit();

        // apply flags (limitation). Special case when we're continuing in the
        // same direction (we might need an additional start-limit here)
        bezierLimitedStart(limitStart, limitEnd);


        // start animation by resetting the timebase to zero and activating
        setTime(0);
        setRunning(true);

        animate();

        // debug
        if (DEBUG_SHOW) {
            Log.d(LOG_TAG, "Going fixed-time from " + mValue + " to " + target + " in " + duration);
        }
    }


    public void goToValue(double target) {
        double time;
        double dist, baseSpeed, baseAcceleration, tempTime, tempDist;
        boolean linearIn, linearOut;
        boolean turnAround;

        // bailout / set directly on boundary conditions
        if (mBaseTime <= 0.0) {
            setValueImmediate(target);
            return;
        }
        if (mTimeMode != PDEParametricCurveAnimationTimeMode.TimeModeConstant && mBaseDistance <= 0) {
            setValueImmediate(target);
            return;
        }

        // calculate time depending on timing mode
        switch (mTimeMode) {
            case TimeModeConstant:
                // fixed time. If case of no changes, time is zero
                if (target == mValue) {
                    time = 0;
                } else {
                    time = mBaseTime;
                }
                break;
            case TimeModeLinear:
                // time proportional to distance
                dist = Math.abs(mValue - target);
                // normalize distance
                dist /= mBaseDistance;
                // proportional time
                time = (long) dist * mBaseTime;
                break;
            case TimeModeExponential:
                // time proportional to distance, exponential growth
                dist = Math.abs(mValue - target);
                // normalize distance
                dist /= mBaseDistance;
                // proportional time, exponential growth
                time = (long) Math.pow(dist, 1.0 / mTimeExponent) * mBaseTime;
                break;
            case TimeModeAdaptive:
                // adaptive is (roughly) oriented towards a physical model. Split in two halves, calculate
                // time to move in each half depending on hard/soft easing. Only assume constant acceleration.
                // for ease-in, also adjust to current speed which might be continued. Formula used is
                // x=1/2at^2 in case of soft easings. For linear-in/-out, adaptive timing conforms to linear timing,
                // for soft-in/-out, adaptive timing conforms to exponential timing with the default exponent of 2.
                // get base values to use


                baseSpeed = mBaseDistance / mBaseTime;
                baseAcceleration = 4.0 * mBaseDistance / Math.pow(mBaseTime, 2.0);
                // classify some use cases for later
                if (mCurveMode == PDEParametricCurveAnimationMode.ModeLinear
                    || (mEaseInType == PDEParametricCurveAnimationEase.EaseLinear
                        && !((mFlags & FlagForceSpeedContinuity) == FlagForceSpeedContinuity
                             || (mFlags & FlagForceAccelerationContinuity) == FlagForceAccelerationContinuity))) {
                    linearIn = true;
                } else {
                    linearIn = false;
                }
                if (mCurveMode == PDEParametricCurveAnimationMode.ModeLinear
                    || mEaseOutType == PDEParametricCurveAnimationEase.EaseLinear) {
                    linearOut = true;
                } else {
                    linearOut = false;
                }
                if ((target >= mValue && mSpeed < 0) || (target <= mValue && mSpeed > 0)) {
                    turnAround = true;
                } else {
                    turnAround = false;
                }
                // some values to use later
                dist = Math.abs(target - mValue);
                if (linearIn) {
                    mStartSpeed = 0.0;
                } else {
                    mStartSpeed = Math.abs(mSpeed);
                }
                // ease-in calculation
                if (linearIn) {
                    // linear in (no continuation etc) => move half the distance
                    time = (dist / 2.0 / baseSpeed);
                    // keep half the distance for ease-out
                    dist /= 2.0;
                } else {
                    // we have a current speed, calculate the time and distance it would take to get to zero
                    tempTime = mStartSpeed / baseAcceleration;
                    tempDist = 0.5 * mStartSpeed * tempTime;
                    // which case?
                    if (turnAround) {
                        // we don't want to go the full distance (this looks too sluggish). Modify time, recalculate
                        // distance
                        tempTime *= mTurnAroundFactor;
                        tempDist = 0.5 * mStartSpeed * tempTime;
                        // we start by breaking
                        time = tempTime;
                        // after this, we have to move some more back
                        dist += tempDist;
                        // calculate first half of backward move
                        time += Math.sqrt(2.0 * (dist / 2.0) / baseAcceleration);
                        // keep half the distance for ease-out
                        dist /= 2.0;
                    } else if (tempDist <= dist / 2.0) {
                        // we still need to accelerate a bit. Add distance we already accelerated, then take the time for
                        // the full distance, and subtract the time we already did.
                        time = Math.sqrt(2.0 * (tempDist + dist / 2.0) / baseAcceleration);
                        time -= tempTime;
                        // keep half the distance for ease-out
                        dist /= 2.0;
                    } else if (tempDist < dist) {
                        // we have the time distance it takes to break fully from our current speed. Subtract this from
                        // the total distance, let the ease-out do the rest of the distance
                        time = tempTime;
                        dist -= tempDist;
                    } else {
                        // we're overshooting anyway. Forget continuity, calculate the time it takes to break with constant
                        // deceleration
                        time = (2.0 * dist / mStartSpeed);
                        // no distance left for ease-out
                        dist = 0.0;
                    }
                }
                // ease-out calculation: we have a rest distance to go, depending on mode
                if (linearOut) {
                    // move the rest of the distance
                    time += dist / baseSpeed;
                } else {
                    // decelerate the rest of the distance
                    time += Math.sqrt(2.0 * dist / baseAcceleration);
                }
                break;
            default:
                Log.d(LOG_TAG, "PDEParametricCurveAnimation goToValue unimplemented case of TimeMode");
                time = mBaseTime;
                break;
        }

        // eventually limit time
        if ((mFlags & FlagLimitTime) == FlagLimitTime && time > mBaseTime) {
            time = mBaseTime;
        }

        // use timed function (this does the rest)
        goToValue(target, (long) time);
    }


    /**
     * @brief Setter with change management.
     */
    protected void setValue(double value) {
        // any change?
        if (value == mValue) return;

        // remember
        mValue = value;

        // remember as changed
        setChanged();
    }


    /**
     * @brief Setter with change management.
     */
    protected void setSpeed(double speed) {
        // any change?
        if (speed == mSpeed) return;

        // remember
        mSpeed = speed;

        // remember as changed
        setChanged();
    }


    /**
     * @brief Setter with change management.
     */
    protected void setAcceleration(double acceleration) {
        // any change?
        if (acceleration == mAcceleration) return;

        // remember
        mAcceleration = acceleration;

        // remember as changed
        setChanged();
    }


    /**
     * @brief Setter with change management.
     */
    protected void setAccelerationDerivative(double accelerationDerivative) {
        // any change?
        if (accelerationDerivative == mAccelerationDerivative) return;

        // remember
        mAccelerationDerivative = accelerationDerivative;

        // remember as changed
        setChanged();
    }


    /**
     * @brief Movement implementation: calculation of new value.
     */
    @Override
    public void animate() {
        long time;
        int i;

        if (DEBUG_SHOW) {
            Log.d(LOG_TAG, "animate");
        }

        // do we need to do anything?
        if (!isRunning()) return;

        // get current time for calculations
        time = getTime();

        // time could be negative (if parent time changed) -> don't go over the boundary
        if (time < 0) time = 0;

        // anything to animate?
        if (mDuration == 0.0) {
            // we might still get timing (one frame to keep speed and acceleration), reset if we're done
            setSpeed(0.0);
            setAcceleration(0.0);
            setAccelerationDerivative(0.0);
            // also reset current segment
            mStartValue = mValue;
            mTarget = mValue;
            mStartSpeed = 0.0;
            mTargetSpeed = 0.0;
            mStartAcceleration = 0.0;
            mTargetAcceleration = 0.0;
            // and intermediate information
            for (i = 0; i < 6; i++) {
                mBezierControl[i] = mValue;
            }
            mBoundsStart = mValue;
            mBoundsEnd = mValue;
            mLimitedStart = false;
            mLimitedEnd = false;

            // stop timing
            setTime(0);
            setRunning(false);
            return;
        }

        // should we stop?
        if (time >= mDuration) {
            // finished?
            // directly take end value to avoid rounding errors
            setValue(mTarget);
            // always calc speed and acceleration (they might have gone through limiting)
            setSpeed(bezierSpeedAtTime(time));
            setAcceleration(bezierAccelerationAtTime(time));
            setAccelerationDerivative(bezierAccelerationDerivativeAtTime(time));
            // reset active, but keep timing for one frame (this keeps current speed and current acceleration
            // for readout; which is useful when displaying debugging curves - or immediately stitching animations.
            // start and end values are also kept for turnaround decisions; duration is reset as a signal that
            // we're finished.
            mDuration = 0;

            // animation will run one more time and then finish

        } else {
            // calculate all values
            setValue(bezierValueAtTime(time));
            setSpeed(bezierSpeedAtTime(time));
            setAcceleration(bezierAccelerationAtTime(time));
            setAccelerationDerivative(bezierAccelerationDerivativeAtTime(time));
        }
    }


    /**
     * @brief Internal. Bezier curve calculation.
     */
    protected double bezierValueAtTime(long time) {
        double q, u, x;

        // limit value
        if (time < 0) time = 0;
        if (time > (double) mDuration) time = mDuration;

        // unified values (range 0-1)
        u = (double) time / (double) mDuration;
        q = 1 - u;

        // mode?
        switch (mCurveMode) {
            case ModeLinear:
                // Linear
                x = q * mBezierControl[0]
                    + u * mBezierControl[1];
                break;
            case ModeB3:
                // Bezier3
                x = q * q * q * mBezierControl[0]
                    + 3.0 * u * q * q * mBezierControl[1]
                    + 3.0 * u * u * q * mBezierControl[2]
                    + u * u * u * mBezierControl[3];
                break;
            case ModeB5:
                // Bezier5
                x = q * q * q * q * q * mBezierControl[0]
                    + 5.0 * u * q * q * q * q * mBezierControl[1]
                    + 10.0 * u * u * q * q * q * mBezierControl[2]
                    + 10.0 * u * u * u * q * q * mBezierControl[3]
                    + 5.0 * u * u * u * u * q * mBezierControl[4]
                    + u * u * u * u * u * mBezierControl[5];
                break;
            default:
                Log.d(LOG_TAG, "PDEParametricCurveAnimation in bezierValueAtTime unimplemented curveMode value");
                x = 0;
                break;
        }

        if (DEBUG_SHOW) {
            Log.d(LOG_TAG, "bezierValueAtTime(" + time + ") = " + x);
        }

        // done
        return x;
    }


    /**
     * @brief Internal. Bezier curve calculation.
     */
    protected double bezierSpeedAtTime(long time) {
        double vx, q, u;

        // limit time
        if (time < 0) time = 0;
        if (time > mDuration) time = mDuration;

        // unified values (range 0-1)
        u = (double) time / (double) mDuration;
        q = 1 - u;

        // mode?
        switch (mCurveMode) {
            case ModeLinear:
                // speed linear
                vx = mBezierControl[1] - mBezierControl[0];
                break;
            case ModeB3:
                // speed b3
                vx = -3.0 * q * q * mBezierControl[0]
                     + 3.0 * (-2.0 * u + q) * q * mBezierControl[1]
                     + 3.0 * (-u + 2.0 * q) * u * mBezierControl[2]
                     + 3.0 * u * u * mBezierControl[3];
                break;
            case ModeB5:
                // speed b5
                vx = -5.0 * q * q * q * q * mBezierControl[0]
                     + 5.0 * (-4.0 * u + q) * q * q * q * mBezierControl[1]
                     + 10.0 * (-3.0 * u + 2.0 * q) * u * q * q * mBezierControl[2]
                     + 10.0 * (-2.0 * u + 3.0 * q) * u * u * q * mBezierControl[3]
                     + 5.0 * (-u + 4.0 * q) * u * u * u * mBezierControl[4]
                     + 5.0 * u * u * u * u * mBezierControl[5];
                break;
            default:
                Log.d(LOG_TAG, "PDEParametricCurveAnimation in bezierSpeedAtTime unimplemented curveMode value");
                vx = 0;
                break;
        }

        // normalize speed to duration
        return vx / mDuration;
    }


    /**
     * @brief Internal. Bezier curve calculation.
     */
    protected double bezierAccelerationAtTime(long time) {
        double ax, q, u;

        // limit time
        if (time < 0.0) time = 0;
        if (time > mDuration) time = mDuration;

        // unified values (range 0-1)
        u = (double) time / (double) mDuration;
        q = 1 - u;

        // mode?
        switch (mCurveMode) {
            case ModeLinear:
                // no acceleration
                ax = 0.0;
                break;
            case ModeB3:
                // acceleration b3
                ax = 6.0 * q * mBezierControl[0]
                     + 3.0 * (u - 2.0 * q) * 2.0 * mBezierControl[1]
                     + 3.0 * (-2.0 * u + q) * 2.0 * mBezierControl[2]
                     + 6.0 * u * mBezierControl[3];
                break;
            case ModeB5:
                // acceleration b5
                ax = 20.0 * q * q * q * mBezierControl[0]
                     + 5.0 * (3.0 * u - 2.0 * q) * 4.0 * q * q * mBezierControl[1]
                     + 10.0 * (3.0 * u * u - 6.0 * u * q + q * q) * 2.0 * q * mBezierControl[2]
                     + 10.0 * (u * u - 6.0 * u * q + 3.0 * q * q) * 2.0 * u * mBezierControl[3]
                     + 5.0 * (-2.0 * u + 3.0 * q) * 4.0 * u * u * mBezierControl[4]
                     + 20.0 * u * u * u * mBezierControl[5];
                break;
            default:
                Log.d(LOG_TAG, "PDEParametricCurveAnimation in bezierAccelerationAtTime unimplemented curveMode value");
                ax = 0;
                break;
        }

        // normalize acceleration to duration
        return ax / Math.pow(mDuration, 2.0);
    }


    /**
     * @brief Internal. Bezier curve calculation.
     */
    private double bezierAccelerationDerivativeAtTime(long time) {
        double dax, q, u;

        // limit time
        if (time < 0) time = 0;
        if (time > mDuration) time = mDuration;

        // unified values (range 0-1)
        u = (double) time / (double) mDuration;
        q = 1 - u;

        // mode?
        switch (mCurveMode) {
            case ModeLinear:
                // no derivative
                dax = 0.0;
                break;
            case ModeB3:
                // derivative is fixed
                dax = -6.0 * mBezierControl[0]
                      + 3.0 * 6.0 * mBezierControl[1]
                      + 3.0 * -6.0 * mBezierControl[2]
                      + 6.0 * mBezierControl[3];
                break;
            case ModeB5:
                // acceleration derivative b5
                dax = -60.0 * q * q * mBezierControl[0]
                      + 5.0 * (-2.0 * u + 3.0 * q) * 12.0 * q * mBezierControl[1]
                      + 10.0 * (-u * u + 6.0 * u * q - 3.0 * q * q) * 6.0 * mBezierControl[2]
                      + 10.0 * (3 * u * u - 6.0 * u * q + q * q) * 6.0 * mBezierControl[3]
                      + 5.0 * (-3.0 * u + 2.0 * q) * 12.0 * u * mBezierControl[4]
                      + 60.0 * u * u * mBezierControl[5];
                break;
            default:
                Log.d(LOG_TAG, "PDEParametricCurveAnimation in bezierAccelerationDerivativeAtTime unimplemented " +
                               "curveMode value");
                dax = 0;
                break;
        }

        // normalize derivative to duration
        return dax / Math.pow(mDuration, 2.0) / mDuration;
    }


    /**
     * @brief Get the remaining duration of the animation.
     *
     * If the animation is stopped, the remaining duration is zero.
     */
    public long remainingDuration() {
        long remaining;

        // zero if we're not running
        if (!isRunning()) {
            return 0;
        }

        // calculate
        remaining = mDuration - getTime();

        // no negative values!
        if (remaining < 0) return 0;

        // done
        return remaining;
    }


    /**
     * @brief Get the time that has elapsed since the animation was done.
     *
     * If we're still running, the time will be negative meaning the animation will be done sometime
     * in the future.
     */
    public long timeSinceDone() {
        // If we're running, return negative remaining duration
        if (isRunning()) {
            return -remainingDuration();
        }

        // we reset the time when we're done -> we can just use it.
        return getTime();
    }


    /**
     * @brief Internal. Calculate bezier values.
     */
    protected void bezierInit() {
        // depends on mode
        switch (mCurveMode) {
            case ModeLinear:
                // linear: no speed (this is also the unknown value type)
                mBezierControl[0] = mStartValue;
                mBezierControl[1] = mTarget;
                break;
            case ModeB3:
                // b3: simple speed (see derivations for how we arrive at these values)
                mBezierControl[0] = mStartValue;
                mBezierControl[1] = mStartValue + 1.0 / 3.0 * mStartSpeed * mDuration;
                mBezierControl[2] = mTarget - 1.0 / 3.0 * mTargetSpeed * mDuration;
                mBezierControl[3] = mTarget;
                break;
            case ModeB5:
                // b5: start points (see derivations for how we arrive at these values)
                mBezierControl[0] = mStartValue;
                mBezierControl[1] = mStartValue + 1.0 / 5.0 * mStartSpeed * mDuration;
                mBezierControl[2] = mStartValue + 2.0 / 5.0 * mStartSpeed * mDuration + 1.0 / 20.0 * mStartAcceleration
                                                                                        * Math.pow(mDuration, 2.0);
                // end points
                mBezierControl[3] = mTarget - 2.0 / 5.0 * mTargetSpeed * mDuration
                                    + 1.0 / 20.0 * mTargetAcceleration * Math.pow(mDuration, 2.0);
                mBezierControl[4] = mTarget - 1.0 / 5.0 * mTargetSpeed * mDuration;
                mBezierControl[5] = mTarget;
                break;
        }
    }


    /**
     * @brief Internal. Limit bezier values (they obey the bounding box property) to avoid overscolls.
     *
     * The method used is quite restrictive (the bezier must obey the bounding box property, but usually does not touch
     * it). Better methods would be harder to implement (and may not be available as closed forms, so have to be
     * calculated iterative).
     */
    protected void bezierLimitedStart(boolean start, boolean end) {
        int i, count = 0;

        // for debugging purposes remember the original bounds and remember if we limited
        mLimitedStart = false;
        mLimitedEnd = false;
        switch (mCurveMode) {
            case ModeLinear:
                count = 2;
                break;
            case ModeB3:
                count = 4;
                break;
            case ModeB5:
                count = 6;
                break;
        }

        mBoundsStart = mBezierControl[0];
        mBoundsEnd = mBezierControl[count - 1];
        if (mBoundsEnd >= mBoundsStart) {
            for (i = 1; i < count; i++) {
                if (mBezierControl[i] < mBoundsStart) mBoundsStart = mBezierControl[i];
            }
            for (i = 0; i < count - 1; i++) {
                if (mBezierControl[i] > mBoundsEnd) mBoundsEnd = mBezierControl[i];
            }
        } else {
            for (i = 1; i < count; i++) {
                if (mBezierControl[i] > mBoundsStart) mBoundsStart = mBezierControl[i];
            }
            for (i = 0; i < count - 1; i++) {
                if (mBezierControl[i] < mBoundsEnd) mBoundsEnd = mBezierControl[i];
            }
        }


        // depends on mode
        switch (mCurveMode) {
            case ModeLinear:
                // nothing to limit.
                break;
            case ModeB3:
                // start overscroll
                if (start) {
                    // which direction?
                    if (mBezierControl[3] >= mBezierControl[0]) {
                        if (mBezierControl[1] < mBezierControl[0]) {
                            mBezierControl[1] = mBezierControl[0];
                            mLimitedStart = true;
                        }
                        if (mBezierControl[2] < mBezierControl[0]) {
                            mBezierControl[2] = mBezierControl[0];
                            mLimitedStart = true;
                        }
                    }
                    if (mBezierControl[3] <= mBezierControl[0]) {
                        if (mBezierControl[1] > mBezierControl[0]) {
                            mBezierControl[1] = mBezierControl[0];
                            mLimitedStart = true;
                        }
                        if (mBezierControl[2] > mBezierControl[0]) {
                            mBezierControl[2] = mBezierControl[0];
                            mLimitedStart = true;
                        }
                    }
                }
                // end overscroll
                if (end) {
                    // which direction?
                    if (mBezierControl[3] >= mBezierControl[0]) {
                        if (mBezierControl[1] > mBezierControl[3]) {
                            mBezierControl[1] = mBezierControl[3];
                            mLimitedEnd = true;
                        }
                        if (mBezierControl[2] > mBezierControl[3]) {
                            mBezierControl[2] = mBezierControl[3];
                            mLimitedEnd = true;
                        }
                    }
                    if (mBezierControl[3] <= mBezierControl[0]) {
                        if (mBezierControl[1] < mBezierControl[3]) {
                            mBezierControl[1] = mBezierControl[3];
                            mLimitedEnd = true;
                        }
                        if (mBezierControl[2] < mBezierControl[3]) {
                            mBezierControl[2] = mBezierControl[3];
                            mLimitedEnd = true;
                        }
                    }
                }
                break;
            case ModeB5:
                // start overscroll
                if (start) {
                    // which direction?
                    if (mBezierControl[5] >= mBezierControl[0]) {
                        if (mBezierControl[1] < mBezierControl[0]) {
                            mBezierControl[1] = mBezierControl[0];
                            mLimitedStart = true;
                        }
                        if (mBezierControl[2] < mBezierControl[0]) {
                            mBezierControl[2] = mBezierControl[0];
                            mLimitedStart = true;
                        }
                        if (mBezierControl[3] < mBezierControl[0]) {
                            mBezierControl[3] = mBezierControl[0];
                            mLimitedStart = true;
                        }
                        if (mBezierControl[4] < mBezierControl[0]) {
                            mBezierControl[4] = mBezierControl[0];
                            mLimitedStart = true;
                        }
                    }
                    if (mBezierControl[5] <= mBezierControl[0]) {
                        if (mBezierControl[1] > mBezierControl[0]) {
                            mBezierControl[1] = mBezierControl[0];
                            mLimitedStart = true;
                        }
                        if (mBezierControl[2] > mBezierControl[0]) {
                            mBezierControl[2] = mBezierControl[0];
                            mLimitedStart = true;
                        }
                        if (mBezierControl[3] > mBezierControl[0]) {
                            mBezierControl[3] = mBezierControl[0];
                            mLimitedStart = true;
                        }
                        if (mBezierControl[4] > mBezierControl[0]) {
                            mBezierControl[4] = mBezierControl[0];
                            mLimitedStart = true;
                        }
                    }
                }
                // end overscroll
                if (end) {
                    // which direction?
                    if (mBezierControl[5] >= mBezierControl[0]) {
                        if (mBezierControl[1] > mBezierControl[5]) {
                            mBezierControl[1] = mBezierControl[5];
                            mLimitedEnd = true;
                        }
                        if (mBezierControl[2] > mBezierControl[5]) {
                            mBezierControl[2] = mBezierControl[5];
                            mLimitedEnd = true;
                        }
                        if (mBezierControl[3] > mBezierControl[5]) {
                            mBezierControl[3] = mBezierControl[5];
                            mLimitedEnd = true;
                        }
                        if (mBezierControl[4] > mBezierControl[5]) {
                            mBezierControl[4] = mBezierControl[5];
                            mLimitedEnd = true;
                        }
                    }
                    if (mBezierControl[5] <= mBezierControl[0]) {
                        if (mBezierControl[1] < mBezierControl[5]) {
                            mBezierControl[1] = mBezierControl[5];
                            mLimitedEnd = true;
                        }
                        if (mBezierControl[2] < mBezierControl[5]) {
                            mBezierControl[2] = mBezierControl[5];
                            mLimitedEnd = true;
                        }
                        if (mBezierControl[3] < mBezierControl[5]) {
                            mBezierControl[3] = mBezierControl[5];
                            mLimitedEnd = true;
                        }
                        if (mBezierControl[4] < mBezierControl[5]) {
                            mBezierControl[4] = mBezierControl[5];
                            mLimitedEnd = true;
                        }
                    }
                }
                break;
        }
    }


    /**
     * @brief Internal. Calculate start/stop speed and acceleration based on easing function type.
     *
     * We try to satisfy as many constraints as possible given the curve. For curves we have the
     * following degrees of freedom:
     * - linear: 2 (start and endpoint)
     * - B3: 4 (start and endpoint, speed at start/end)
     * - B5: 6 (start and endpoint, speed and acceleration at start/end)
     * For the different easing types the constraints are:
     * - hard: speed = max, acceleration = 0 (straight line)
     * - medium: speed = max, acceleration = 0 (straight speed)
     * - soft: speed = 0, acceleration = 0 (stopping softly)
     * For the inpoints, we're using the given values as following:
     * - hard: none
     * - medium: speed
     * - soft: speed and acceleration
     * And eventually if some continuity is forced by flags add up current speed and acceleration if
     * possible. Start points are uniquely defined then, endpoints are adjusted so that max values
     * are satisfied (=> derivative of speed or acceleration must be zero)
     * Inpoints are thus uniquely defined, endpoints are calculated to satisfy constraints (in some
     * cases the start values affect the end values).
     */
    protected void calculateEasing() {
        // first it depends on curve mode
        switch (mCurveMode) {
            case ModeLinear:
                // no easing possible
                mStartSpeed = 0.0;
                mStartAcceleration = 0.0;
                mTargetSpeed = 0.0;
                mTargetAcceleration = 0.0;
                break;
            case ModeB3:
                // easein: given values (we know the constraints when fading out, so we
                // can hardcode initial them, eventually then overriding them with forced
                // continuation values and letting the curve do the rest.
                switch (mEaseInType) {
                    case EaseLinear:
                        // start speed depending on easeout for this segment (fixed factors)
                        switch (mEaseOutType) {
                            case EaseLinear:
                                mStartSpeed = (mTarget - mStartValue) / mDuration;
                                break;
                            case EaseSpeedOnly:
                            case EaseSoft:
                                mStartSpeed = 3.0 / 2.0 * (mTarget - mStartValue) / mDuration;
                                break;
                        }
                        // eventually add continuous start speed
                        if ((mFlags & FlagForceSpeedContinuity) == FlagForceSpeedContinuity
                            || (mFlags & FlagForceAccelerationContinuity) == FlagForceAccelerationContinuity) {
                            mStartSpeed += mSpeed;
                        }
                        break;
                    case EaseSpeedOnly:
                    case EaseSoft:
                        // continuous start speed (no further choices possible)
                        mStartSpeed = mSpeed;
                        break;
                }
                // easeout: base values
                switch (mEaseOutType) {
                    case EaseLinear:
                        // constraints: control4=endvalue; a(end)=0; control2 given by easein
                        // using
                        //   v(end)=-3*control3+3*control4
                        //   a(end)=6*control2-12*control3+6*control4
                        //   control2=startvalue+1/3*startspeed*duration
                        // normalized endspeed by
                        //   control3=endvalue-1/3*endspeed*duration
                        mTargetSpeed = (3.0 * mTarget - 3.0 * mStartValue - mStartSpeed * mDuration) / 2.0 / mDuration;
                        break;
                    case EaseSpeedOnly:
                    case EaseSoft:
                        // end speed zero
                        mTargetSpeed = 0.0;
                        break;
                }
                // no acceleration
                mStartAcceleration = 0.0;
                mTargetAcceleration = 0.0;
                break;
            case ModeB5:
                // easein: given values (we know the constraints when fading out, so we
                // can hardcode initial them, eventually then overriding them with forced
                // continuation values and letting the curve do the rest.
                switch (mEaseInType) {
                    case EaseLinear:
                        // start speed depending on easeout for this segment (fixed factors)
                        switch (mEaseOutType) {
                            case EaseLinear:
                                mStartSpeed = (mTarget - mStartValue) / mDuration;
                                break;
                            case EaseSpeedOnly:
                                mStartSpeed = 10.0 / 7.0 * (mTarget - mStartValue) / mDuration;
                                break;
                            case EaseSoft:
                                mStartSpeed = 5.0 / 3.0 * (mTarget - mStartValue) / mDuration;
                                break;
                        }
                        // start acceleration zero
                        mStartAcceleration = 0.0;
                        // eventually add continuous start speed and acceleration
                        if ((mFlags & FlagForceSpeedContinuity) == FlagForceSpeedContinuity
                            || (mFlags & FlagForceAccelerationContinuity) == FlagForceAccelerationContinuity) {
                            mStartSpeed += mSpeed;
                        }
                        if ((mFlags & FlagForceAccelerationContinuity) == FlagForceAccelerationContinuity) {
                            mStartAcceleration += mAcceleration;
                        }
                        break;
                    case EaseSpeedOnly:
                        // always continue speed
                        mStartSpeed = mSpeed;
                        // start acceleration depending on ease-out for this segment (fixed factors)
                        switch (mEaseOutType) {
                            case EaseLinear:
                                mStartAcceleration = 20.0 / 7.0 * (mTarget - mStartValue) / Math.pow(mDuration, 2.0);
                                break;
                            case EaseSpeedOnly:
                                mStartAcceleration = 5.0 * (mTarget - mStartValue) / Math.pow(mDuration, 2.0);
                                break;
                            case EaseSoft:
                                mStartAcceleration = 20.0 / 3.0 * (mTarget - mStartValue) / Math.pow(mDuration, 2.0);
                                break;
                        }
                        // eventually add continuous acceleration
                        if ((mFlags & FlagForceAccelerationContinuity) == FlagForceAccelerationContinuity) {
                            mStartAcceleration += mAcceleration;
                        }
                        break;
                    case EaseSoft:
                        // continuous speed and acceleration
                        mStartSpeed = mSpeed;
                        mStartAcceleration = mAcceleration;
                        break;
                }
                switch (mEaseOutType) {
                    case EaseLinear:
                        // constraints: control6=endvalue; a(end)=0; a'(end)=0, control3 given by easein
                        // using
                        //   v(end)=-5*control5+5*control6
                        //   a(end)=20*control4-40*control5+20*control6
                        //   a'(end)=-60*control3+180*control4-180*control5+60*control6
                        //   control3=startvalue+2/5*startSpeed*duration+1/20*startAcceleration*sqr(duration)
                        // normalized endspeed - control5 by
                        //   control5=endvalue-1/5*endspeed*duration
                        // seeking end speed
                        mTargetSpeed = (20.0 * mTarget - 20.0 * mStartValue - 8.0 * mStartSpeed * mDuration
                                        - mStartAcceleration * Math.pow(mDuration, 2.0)) / 12.0 / mDuration;
                        // end acceleration is zero
                        mTargetAcceleration = 0.0;
                        break;
                    case EaseSpeedOnly:
                        // constraints: control6=endvalue; v(end)=0; a'(end)=0, control3 given by easein
                        // using
                        //   v(end)=-5*control5+5*control6
                        //   a(end)=20*control4-40*control5+20*control6
                        //   a'(end)=-60*control3+180*control4-180*control5+60*control6
                        //   control3=startvalue+2/5*startSpeed*duration+1/20*startAcceleration*sqr(duration)
                        // normalized endacceleation - control4 by (with endspeed=0)
                        //   control4=endvalue-2/5*endspeed*duration+1/20*endAcceleration*sqr(duration)
                        // and seeking end acceleration
                        mTargetAcceleration = -(20.0 * mTarget - 20.0 * mStartValue - 8.0 * mStartSpeed * mDuration
                                                - mStartAcceleration * Math.pow(mDuration, 2.0)) / 3.0 / Math.pow(
                                mDuration,
                                2.0);
                        // end speed is zero
                        mTargetSpeed = 0.0;
                        break;
                    case EaseSoft:
                        // zero speed and acceleration
                        mTargetSpeed = 0.0;
                        mTargetAcceleration = 0.0;
                        break;
                }
                break;
        }
    }


///**
//* @brief Curve bounds for debugging.
//*
//* Curve bounds are the unlimited bounds where the animation would have been before limitations occurred,
//* and a flag if the curve was limited (in this case, the curve stays in the startValue/endValue range).
//*/
//public void getBoundsStart_debug(double *boundsStart,
//      (boolean *)limitedStart,
//      (double *)boundsEnd,
//      (boolean *)limitedEnd) {
//    // store values
//    *boundsStart = mBoundsStart;
//    *limitedStart = mLimitedStart;
//    *boundsEnd = mBoundsEnd;
//    *limitedEnd = mLimitedEnd;
//}


}

