/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.animation;


//----------------------------------------------------------------------------------------------------------------------
//  PDELinearAnimation
//----------------------------------------------------------------------------------------------------------------------


import android.util.Log;

/**
 * @brief Animation helper for PDEAgentController
 *
 * At the moment, a simple linear animation, reversible, externally timed.
 * This should become a common class with more functionality some time, with a proper
 * global timing and linkage options. Currently, the local time is always used, which
 * might lead to animations started in the same frame running slightly shifted.
 *
 * We're not using CoreAnimation here because we need always available, dynamically reversible
 * Animations.
 */
public class PDELinearAnimation extends PDEAnimation {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDELinearAnimation.class.getName();
    private final static boolean DEBUGPARAMS = false;

    // base properties
    /**
     * @brief current value of animation
     */
    protected double mValue;
    /**
     * @brief target value of animation
     */
    protected double mTarget;
    /**
     * @brief duration of animation
     */
    protected long mDuration;
    /**
     * @brief speed of animation
     */
    protected double mSpeed;

    /**
     * @brief start value of animation
     */
    private double mStartValue;




    /**
     * @brief Constructor.
     */
    public PDELinearAnimation() {
        // init
        mValue = 0.0;
        mTarget = 0.0;
        mStartValue = 0.0;
        mDuration = 0;
        mSpeed = 0.0;
    }

    /**
     * @brief Animate ourself.
     *
     * Calculate the current value if necessary, use system time for now. Timing must be triggered
     * externally for now by the owning class until a global Animation system with proper frame times
     * is in place.
     */
    @Override
    public void animate() {
        double value;
        long time;

        // do we need to do anything?
        if (!isRunning()) {
            return;
        }

        // get current time for calculations
        time = getTime();
        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "Time: "+time+" "+this);
        }

        // time could be negative (if parent time changes) -> don't get over the boundary
        if (time < 0) {
            time = 0;
        }

        // should we stop?
        if (time >= getDuration()) {
            if(DEBUGPARAMS){
                Log.d(LOG_TAG, "Exceeded the Time: "+time+" duration:"+getDuration()+" "+this);
            }
            // we're over the time, set to target value
            setValue(getTarget());
            // stop running, rebase time to zero
            setTime(0);
            setRunning(false);
            // and stop here
            return;
        }

        // calculate interpolated value
        value = mStartValue + (getTarget() - mStartValue) * (((double) time) / getDuration());

        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "Start value: "+mStartValue+" End value: "+getTarget()+" "+this);
            Log.d(LOG_TAG, "Calculated value: "+value+" "+this);
        }

        // avoid rounding errors: limit
        if (mStartValue < getTarget()) {
            if (value < mStartValue) {
                value = mStartValue;
            }
            if (value > getTarget()) {
                value = getTarget();
            }
        } else {
            if (value > mStartValue) {
                value = mStartValue;
            }
            if (value < getTarget()) {
                value = getTarget();
            }
        }

        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "Value to be set: "+value+" "+this);
        }

        // and set
        setValue(value);
    }

    /**
     * @brief Setter with change management.
     */
    public void setValue(double value) {
        // any change?
        if (value == mValue) {
            return;
        }

        // remember
        mValue = value;

        // remember as changed
        setChanged();
    }

    /**
     * @brief Getter for current value of animation.
     *
     * @return current value of animation.
     */
    public double getValue() {
        return mValue;
    }

    /**
     * @brief Setter for target value.
     *
     * @param target The value the animation has to reach till the end of the animation.
     */
    public void setTarget(double target) {
        mTarget = target;
    }

    /**
     * @brief Getter of target value.
     *
     * @return target value.
     */
    public double getTarget() {
        return mTarget;
    }

    /**
     * @brief Setter for duration of animation.
     *
     * @param duration The time the animation will need to animate from start to target value.
     */
    public void setDuration(long duration) {
        mDuration = duration;
    }

    /**
     * @brief Getter for duration.
     *
     * @return Duration of animation.
     */
    public long getDuration() {
        return mDuration;
    }

    /**
     * @brief Setter for speed of animation.
     *
     * @param speed Speed which is measured in units/millisecond.
     */
    public void setSpeed(double speed) {
        mSpeed = speed;
    }

    /**
     * @brief Getter for speed of animation.
     *
     * @return Speed which is measured in units/millisecond.
     */
    public double getSpeed() {
        return mSpeed;
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
        // check for change; if we're stable, do nothing. The time we reached the finished state must not change in
        // this case
        if (!isRunning() && value == mValue) {
            return;
        }

        // set to safe parameters
        mStartValue = value;
        setTarget(value);
        setDuration(0);
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
        if (!isRunning()) {
            return;
        }

        // set the current output value immediately, this stops.
        setValueImmediate(getValue());
    }

    /**
     * @brief Go to the value in the specified time.
     *
     * Animation is always performed starting with the current value, and using the specified duration,
     * even if there's nothing to animate - there's no check for value changes.
     */
    public void goToValueWithDuration(double target, long duration) {
        // special case; no duration
        if (duration == 0) {
            // no animation: directly set the target value
            setValueImmediate(target);
            // and stop here
            return;
        }

        // remember current value as start value
        mStartValue = mValue;

        // remember target and duration
        setTarget(target);
        setDuration(duration);
        setSpeed(Math.abs(getTarget() - mStartValue) / getDuration());

        // start animation by resetting the timebase to zero and activating
        setTime(0);
        setRunning(true);

        // debug
        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "Going fixed-time from " + getValue() + " to " + target + " in " + duration + "s " +
                           ""+this);
        }
    }


    /**
     * @brief Go to the specified value by using the defined speed.
     *        Speed is measured in units/millisecond.
     */
    public void goToValueWithSpeed(double target, double speed) {
        double dist;
        long duration;

        // safety: zero speed doesn't do anything.
        if (speed == 0.0) {
            // stop animation: set the current value
            setValueImmediate(getValue());
            // and stop here
            return;
        }

        // if we don't need to move, just stop immediately
        if (target == getValue()) {
            // no animation: directly set the target value
            setValueImmediate(target);
            // and stop here
            return;
        }

        // if the target and speed do not change, do not change anything
        if (target == getTarget() && Math.abs(speed) == getSpeed()) {
            // just stop
            return;
        }

        // calculate distance
        dist = Math.abs(target - getValue());

        // calculate the time
        duration = Math.round(dist / Math.abs(speed));

        // and go there
        goToValueWithDuration(target, duration);

        // debug
        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "Going fixed-speed from " + getValue() + " to " + target + " in " + duration + "s " +
                           " current time: "+getTime()+" "+this);
        }

        // override the speed again - avoid rounding errors
        setSpeed(Math.abs(speed));
    }


    /**
     * @brief Go to the specified value by using a specified time for moving a specified distance.
     *
     * It works like the goto with speed function. The speed is calculated from the duration
     * it takes to move a specified distance. If the duration is zero, the animation sets the
     * value directly. Speed is measured in units/millisecond.
     */
    public void goToValueWithDurationForDistance(double target, long duration, double distance) {
        // safety: zero duration sets immediately
        if (duration == 0) {
            // stop animation: set the current value
            setValueImmediate(target);
            // and stop here
            return;
        }

        // use the speed function, calculating the speed
        goToValueWithSpeed(target, Math.abs(distance) / duration);
    }


    /**
     * @brief Go to the specified value by using a specified time for moving a specified distance. If the distance to move
     *        is greater than the time, use the time to go there.
     *
     * Note the subtle difference to goToValue:withDuration:atDistance. If the distance gets greater,
     * we take longer time.
     * The intent for this function is to arrive in a defined time if the distances are greater, while allowing to turn
     * around in a small area without having to spend the whole time.
     *
     * Be aware that the speed used changes when we're in limiting mode, so the behaviour of not changing the animation when
     * called with the same duration/distance parameters no longer holds. The caller must ensure that the animation is not
     * restarted when no change should happen!
     */
    public void goToValueWithDurationLimitedAtDistance(double target, long duration, double distance) {
        double dist;

        // safety: zero duration sets immediately
        if (duration == 0) {
            // stop animation: set the current value
            setValueImmediate(target);
            // and stop here
            return;
        }

        // if we don't need to move, just stop immediately
        if (target == getValue()) {
            // no animation: directly set the target value
            setValueImmediate(target);
            // and stop here
            return;
        }

        // calculate distance to move
        dist = Math.abs(target - getValue());

        // what should we do?
        if (dist >= Math.abs(distance)) {
            // we're over or at the maximum distance -> use the allowed time
            goToValueWithDuration(target, duration);
        } else {
            // use the speed version
            goToValueWithDurationForDistance(target, duration, distance);
        }
    }


    /**
     * @brief Get the remaining duration of the animation.
     *
     * If the animation is stopped, the remaining duration is zero.
     */
    public long getRemainingDuration() {
        long remaining;

        // zero if we're not running
        if (!isRunning()) {
            return 0;
        }

        // calculate
        remaining = getDuration() - getTime();

        // no negative values!
        if (remaining < 0) {
            return 0;
        }

        // done
        return remaining;
    }


    /**
     * @brief Get the time that has elapsed since the animation was done.
     *
     * If we're still running, the time will be negative meaning the animation will be done sometime
     * in the future.
     */
    public long getTimeSinceDone() {
        // If we're running, return negative remaining duration
        if (isRunning()) {
            return -getRemainingDuration();
        }

        // we reset the time when we're done -> we can just use it.
        return getTime();
    }
}
