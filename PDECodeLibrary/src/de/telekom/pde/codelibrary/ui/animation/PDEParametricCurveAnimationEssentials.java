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
public class PDEParametricCurveAnimationEssentials extends PDEAnimation {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEParametricCurveAnimationEssentials.class.getName();
    private final static boolean DEBUG_SHOW = false;

    // bezier values for easier calculations
    protected double[] mBezierControl;

    // variables with getters and setters
    protected double mValue;
    protected double mTarget;
    protected long mDuration; // == m_moveDuration
    protected double mSpeed;
    protected double mAcceleration;

    protected long mBaseTime;
    protected double mBaseDistance;


    /**
     * @brief Constructor.
     */
    public PDEParametricCurveAnimationEssentials() {
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
        mDuration = 0;
        mSpeed = 0.0;

        mBezierControl = new double[6];

        for (int i = 0; i < 6; i++) {
            mBezierControl[i] = 0.0;
        }
        mValue = 0.0;
        mSpeed = 0.0;
        mAcceleration = 0.0;

        // default configuration
        mBaseTime = 1000;
        mBaseDistance = 1.0;
    }


    /**
     * @brief Set base time to be used.
     * <p/>
     * Base distance is unchanged. For TimeModeConstant, setting the base time is enough. For
     * all other time modes, use setBaseTimeAndDistance() and see flags documentation.
     * For behaviour of the different timing modes, see time constants documentation.
     * <p/>
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
     * <p/>
     * Base time is unchanged.
     * <p/>
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


    /**
     * @brief Directly set the value.
     * <p/>
     * This stops any running animations.
     * <p/>
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
     * <p/>
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
     * <p/>
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
            limitStart = true;
        } else if (mSpeed > 0.0) {
            if (target >= mValue) {
                limitStart = true;
            } else {
                limitStart = false;
            }
        } else {
            if (target <= mValue) {
                limitStart = true;
            } else {
                limitStart = false;
            }
        }
        limitEnd = true;

        // remember target and duration
        mTarget = target;
        mDuration = duration;

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
        double dist, baseSpeed, baseAcceleration, tempTime, tempDist, startSpeed;
        boolean turnAround;

        // bailout / set directly on boundary conditions
        if (mBaseTime <= 0.0) {
            setValueImmediate(target);
            return;
        }
        if (mBaseDistance <= 0) {
            setValueImmediate(target);
            return;
        }

        // calculate time depending on timing mode
        // adaptive is (roughly) oriented towards a physical model. Split in two halves, calculate
        // time to move in each half depending on hard/soft easing. Only assume constant acceleration.
        // for ease-in, also adjust to current speed which might be continued. Formula used is
        // x=1/2at^2 in case of soft easings. For linear-in/-out, adaptive timing conforms to linear timing,
        // for soft-in/-out, adaptive timing conforms to exponential timing with the default exponent of 2.
        // get base values to use
        baseSpeed = mBaseDistance / mBaseTime;
        baseAcceleration = 4.0 * mBaseDistance / Math.pow(mBaseTime, 2.0);
        // classify some use cases for later
        if ((target >= mValue && mSpeed < 0) || (target <= mValue && mSpeed > 0)) {
            turnAround = true;
        } else {
            turnAround = false;
        }
        // some values to use later
        dist = Math.abs(target - mValue);
        startSpeed = Math.abs(mSpeed);
        // ease-in calculation
        // we have a current speed, calculate the time and distance it would take to get to zero
        tempTime = startSpeed / baseAcceleration;
        tempDist = 0.5 * startSpeed * tempTime;
        // which case?
        if (turnAround) {
            // we don't want to go the full distance (this looks too sluggish). Modify time, recalculate
            // distance
            tempTime *= 0.25; // hardcoded turnaround factor of 0.25 (now no longer parametrized)
            tempDist = 0.5 * startSpeed * tempTime;
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
            time = (2.0 * dist / startSpeed);
            // no distance left for ease-out
            dist = 0.0;
        }
        // ease-out calculation: we have a rest distance to go, depending on mode
        // decelerate the rest of the distance
        time += Math.sqrt(2.0 * dist / baseAcceleration);

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
            // also reset current segment
            mTarget = mValue;
            // and intermediate information
            for (i = 0; i < 6; i++) {
                mBezierControl[i] = mValue;
            }

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

        // Bezier5
        x = q * q * q * q * q * mBezierControl[0]
                + 5.0 * u * q * q * q * q * mBezierControl[1]
                + 10.0 * u * u * q * q * q * mBezierControl[2]
                + 10.0 * u * u * u * q * q * mBezierControl[3]
                + 5.0 * u * u * u * u * q * mBezierControl[4]
                + u * u * u * u * u * mBezierControl[5];

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

        // speed b5
        vx = -5.0 * q * q * q * q * mBezierControl[0]
                + 5.0 * (-4.0 * u + q) * q * q * q * mBezierControl[1]
                + 10.0 * (-3.0 * u + 2.0 * q) * u * q * q * mBezierControl[2]
                + 10.0 * (-2.0 * u + 3.0 * q) * u * u * q * mBezierControl[3]
                + 5.0 * (-u + 4.0 * q) * u * u * u * mBezierControl[4]
                + 5.0 * u * u * u * u * mBezierControl[5];

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

        // acceleration b5
        ax = 20.0 * q * q * q * mBezierControl[0]
                + 5.0 * (3.0 * u - 2.0 * q) * 4.0 * q * q * mBezierControl[1]
                + 10.0 * (3.0 * u * u - 6.0 * u * q + q * q) * 2.0 * q * mBezierControl[2]
                + 10.0 * (u * u - 6.0 * u * q + 3.0 * q * q) * 2.0 * u * mBezierControl[3]
                + 5.0 * (-2.0 * u + 3.0 * q) * 4.0 * u * u * mBezierControl[4]
                + 20.0 * u * u * u * mBezierControl[5];

        // normalize acceleration to duration
        return ax / Math.pow(mDuration, 2.0);
    }


    /**
     * @brief Get the remaining duration of the animation.
     * <p/>
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
     * <p/>
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
        // b5: start points (see derivations for how we arrive at these values)
        mBezierControl[0] = mValue;
        mBezierControl[1] = mValue + 1.0 / 5.0 * mSpeed * mDuration;
        mBezierControl[2] = mValue + 2.0 / 5.0 * mSpeed * mDuration + 1.0 / 20.0 * mAcceleration
                * Math.pow(mDuration, 2.0);
        // end points
        mBezierControl[3] = mTarget;
        mBezierControl[4] = mTarget;
        mBezierControl[5] = mTarget;
    }


    /**
     * @brief Internal. Limit bezier values (they obey the bounding box property) to avoid overscolls.
     * <p/>
     * The method used is quite restrictive (the bezier must obey the bounding box property, but usually does not touch
     * it). Better methods would be harder to implement (and may not be available as closed forms, so have to be
     * calculated iterative).
     */
    protected void bezierLimitedStart(boolean start, boolean end) {

        // start overscroll
        if (start) {
            // which direction?
            if (mBezierControl[5] >= mBezierControl[0]) {
                for (int i = 1; i <= 4; i++) {
                    if (mBezierControl[i] < mBezierControl[0]) {
                        mBezierControl[i] = mBezierControl[0];
                    }
                }
            }
            if (mBezierControl[5] <= mBezierControl[0]) {
                for (int i = 1; i <= 4; i++) {
                    if (mBezierControl[i] > mBezierControl[0]) {
                        mBezierControl[i] = mBezierControl[0];
                    }
                }
            }
        }
        // end overscroll
        if (end) {
            // which direction?
            if (mBezierControl[5] >= mBezierControl[0]) {
                for (int i = 1; i <= 4; i++) {
                    if (mBezierControl[i] > mBezierControl[5]) {
                        mBezierControl[i] = mBezierControl[5];
                    }
                }

            }
            if (mBezierControl[5] <= mBezierControl[0]) {
                for (int i = 1; i <= 4; i++) {
                    if (mBezierControl[i] < mBezierControl[5]) {
                        mBezierControl[i] = mBezierControl[5];
                    }
                }
            }
        }
    }
}

