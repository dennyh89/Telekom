/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.animation;

//----------------------------------------------------------------------------------------------------------------------
// PDEAnimation
//----------------------------------------------------------------------------------------------------------------------


import android.util.Log;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @brief The base class for the animation system.
 *
 * The animation system differs from the builtin animation system a number of ways. Notably, there's some change
 * management builtin which goes beyond KeyValue setting. It's necessary when a display-system-unrelated animated
 * class needs to send out animation updates in a clearly defined bunch at the right time. There's also support for
 * endless running animations that can be changed smoothly while they are running.
 *
 * Be careful with the times supplied in the animation system. They are only valid for the current instance of the
 * animation object and should never be compared to any other animation times. All animation timings are
 * ultimately derived from PDEFrameTiming times, but they may be modified by subsequent animations in the
 * animation tree.
 */
public class PDEAnimation {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEAnimation.class.getName();

    // running  (internal)
    /**
     * @brief Flag that shows if we're registered at the parent for running.
     */
    protected boolean mRunningRegisteredWithParent;

    // change management (internal)
    /**
     * @brief Changed flag.
     */
    public boolean mChanged;
    /**
     * @brief Flag that shows if we're registered at the parent for changes.
     */
    protected boolean mChangedRegisteredWithParent;

    // timing internals  (internal)
    /**
     * @brief Our local time offset.
     */
    public long mTimeOffset;
    /**
     * @brief cached time of parent.
     */
    public long mCachedParentTime;

    // timing and animation
    /**
     * @brief factor to modify the animation speed.
     */
    protected double mTimeFactor;
    /**
     * @brief running flag
     */
    protected boolean mRunning;


    // Animation tree structure
    /**
     * @brief parent of animation
     */
    protected PDEAnimationGroup mParentAnimation;

    // Change handling
    /**
     * @brief Object that listens for changes of this animation.
     */
    protected WeakReference<Object> mDidChangeTarget;
    /**
     * @brief Method of listener object that will be called when change takes place.
     */
    protected Method mDidChangeMethod;

    // animation loop handling (internal)
    /**
     * @brief Flag that shows if we're within the animation phase.
     */
    protected boolean mInAnimation;


    // initialization
    public PDEAnimation() {
        // init
        mParentAnimation = null;
        mRunning = false;
        mTimeFactor = 1.0;
        mRunningRegisteredWithParent = false;
        mCachedParentTime = 0;
        mTimeOffset = 0;
        mChanged = false;
        mDidChangeTarget = null;
        mDidChangeMethod = null;
        mInAnimation = false;
    }


    /**
     * @brief Link to the animation's parent, which must be an animation group.
     *
     * This property must only be set by the system internally. Never set the parent directly, use the supplied
     * addSubAnimation, removeSubAnimation and removeFromParentAnimation functions.
     */
    public PDEAnimationGroup getParentAnimation() {
        return mParentAnimation;
    }

    /**
     * @brief The target for change notification.
     */
    public Object getDidChangeTarget() {
        if (mDidChangeTarget != null && mDidChangeTarget.get() != null) {
            return mDidChangeTarget.get();
        } else {
            return null;
        }
    }

    /**
     * @brief The method called for change notification.
     */
    @SuppressWarnings("unused")
    public Method getDidChangeMethod() {
        return mDidChangeMethod;
    }


    /**
     * @brief Get running-registration-state.
     *
     * If we want this animation to be running/timed, we have to register ourselves for running at the parent.
     * This method tells if we're already registered or not for running.
     *
     * @return True if we're registered at the parent for running.
     */
    public boolean isRunningRegisteredWithParent() {
        return mRunningRegisteredWithParent;
    }

    /**
     * @brief Get change handling registration state.
     *
     * If we want this animation to have explicit change handling by the parent, we have to register for this at the
     * parent. This method tells if we're already registered for explicit change handling.
     *
     * @return True if we're registered at the parent for explicit change handling.
     */
    public boolean isChangedRegisteredWithParent() {
        return mChangedRegisteredWithParent;
    }


    /**
     * @brief Tells if we're currently in the animation phase.
     *
     * Within the animate() method the attributes of the object we want to animate are changed/updated. During the
     * processing of this method the InAnimation property is set to true.
     *
     * @return True if we're currently within the animation phase.
     */
    public boolean isInAnimation() {
        return mInAnimation;
    }

    /**
     * @brief Setter for InAnimation property.
     *
     * internal use only.
     */
    protected void setInAnimation(boolean inAnimation) {
        mInAnimation = inAnimation;
    }


//----- structure handling --------------------------------------------------------------------------------------------

    /**
     * @brief Change the parent animation.
     *
     * Internal use only. Don't call this function directly. The function is called by the parent class while linking
     * or unlinking the animation. If the animation is already linked to another animation, the link is properly cleared.
     *
     * In addition to remembering the new parent, this function also adjusts the timebase and registers the
     * animation for running or change management as necessary.
     *
     * @param parentAnimation The new parent animation.
     */
    protected void setParentAnimation(PDEAnimationGroup parentAnimation) {
        // unlink from old parent if we have one
        if (getParentAnimation() != null) {
            // clear all changes without sending notifications
            clearChanges();
            // become inactive
            setRunningRegisteredWithParent(false);
            // rebase our time. Use current parent time (update). The cached parent time afterwards is zero.
            mCachedParentTime = getParentAnimation().getTime();
            mTimeOffset += Math.round(mCachedParentTime * getTimeFactor());
            mCachedParentTime = 0;
        }

        mParentAnimation = parentAnimation;

        // and eventually init the new parent
        if (getParentAnimation() != null) {
            // rebase our time to the parent animation's current time. We have to directly access the parent's time,
            // since we might be running (and the time received would then be just the cached time)
            mCachedParentTime = getParentAnimation().getTime();
            mTimeOffset -= Math.round(mCachedParentTime * getTimeFactor());
            // tell the new parent of our running status
            setRunningRegisteredWithParent(isRunningWithSubAnimations());
        }
    }

    /**
     * @brief Called internally when the parent is being destructed.
     *
     * Internal use only. Simply set ourself to a clean state. No parent cleanup / modifying functions must be called,
     * because the parent is already in the process of destruction, and is cleaning itself. Any parent functions called
     * might result in unexpected behaviour.
     */
    @SuppressWarnings("unused")
    protected void forgetParentAnimation() {
        // forget the parent
        mParentAnimation = null;

        // reset running registrations
        mRunningRegisteredWithParent = false;

        // clear changes
        clearChanges();

        // and rebase our time based on cached timing value. Note: unless the cached parent time is up to date,
        // this will lead to errors. The destruction code calling this will will update the time manually for
        // performance reasons.
        mTimeOffset += Math.round(mCachedParentTime * getTimeFactor());
        mCachedParentTime = 0;
    }

    /**
     * @brief Remove the animation from the parent.
     */
    protected void removeFromParentAnimation() {
        if (getParentAnimation() != null) {
            getParentAnimation().removeSubAnimation(this);
        }
    }

//----- running status -------------------------------------------------------------------------------------------------

    /**
     * @brief Get the animation's current running status.
     *
     * Change this (with setRunning) to become running. Unless running, the timeDidChange function will never be
     * called. When this property changes, no additional functions will be called and no notifications will be given.
     * This property must never be written to externally, it should only be set by the animation itself in response
     * to the animation's state.
     */
    public boolean isRunning() {
        return mRunning;
    }

    /**
     * @brief Change the running status of the animation.
     *
     * If the animation is started, no immediate action is taken. From then on,
     * parentTimeDidChange is called during every animation loop until the animation is stopped.
     *
     * The running status is propagated up through the animation tree, until the animation root eventually links
     * into the display timing. If running is set, this does not necessarily mean that the animation is
     * really running. The animation must be linked into the tree up to the root element, and all parent
     * animations must be running and not paused in order to actually deliver regular timings.
     *
     * @param running true if we're running.
     */
    public void setRunning(boolean running) {
        // any change? ( note the bool logic)
        if (mRunning == running) {
            return;
        }

        // remember new state
        mRunning = running;

        // check running state
        checkRunning();

        // changing the running state is a change!
        setChanged();
    }

    /**
     * @brief Check the running state and eventually register with parent.
     *
     * Internal use only. This function checks the running state whenever something changes which might affect it.
     * Reimplement the isRunningWithSubAnimations method to provide information if your animation should
     * actually be running.
     */
    protected void checkRunning() {
        // just propagate our run state to the parent
        setRunningRegisteredWithParent(isRunningWithSubAnimations());
    }

    /**
     * @brief Internal, propagate running state to parent.
     *
     * Internal use only. The real running state (determined by checkRunning) is propagated to the parent, which
     * in turn might change it's running state.
     *
     * There's different levels of running. If we just set the running flag, this is only a request to be run.
     * Registering with the parent tells the tree, and if the linking up to the root is correct, we're run.
     *
     * @param running true if we should be running.
     */
    protected void setRunningRegisteredWithParent(boolean running) {
        // check for parent
        if (getParentAnimation() == null) {
            // don't accept it
            mRunningRegisteredWithParent = false;
            return;
        }

        // any change?
        if (mRunningRegisteredWithParent == running) {
            return;
        }

        // remember
        mRunningRegisteredWithParent = running;

        // and tell the parent
        if (mRunningRegisteredWithParent) {
            // register ourself
            getParentAnimation().registerSubAnimationForRunning(this);
            // after registering, update parent time to current value so further timing calls get a correct time.
            mCachedParentTime = getParentAnimation().getTime();
        } else {
            // we're not running any more, but we might have a pending change (only register ourselves if we're not
            // currently processing an animation, in which case change management will be called anyway)
            if (mChanged && !isInAnimation()) {
                setChangedRegisteredWithParent();
            }
            // unregister ourself
            getParentAnimation().unregisterSubAnimationForRunning(this);
        }
    }

    /**
     * @brief Helper value for run handling in tree.
     *
     * This function tells the base logic if the instance actually should be running. This class should be overloaded
     * if running behaviour changes (e.g. groups should also run if any subanimation is running).
     *
     * @return true if the instance is actually running and should be timed.
     */
    boolean isRunningWithSubAnimations() {
        // the base class has no access to trees -> use running flag only.
        return isRunning();
    }


//----- time & animation management ------------------------------------------------------------------------------------

    /**
     * @brief Access the animation's current time.
     *
     * This time is relative to the animation. It stays the same when the animation switches parents. The time can
     * also be set in the animation to adjust it's own base time. This change does not affect the other timing,
     * or when timing/animation will be called.
     *
     *  Time is measured in milliseconds
     *
     *  The implementation concept of timing is to always cache the time when animating - since it will be accessed
     *  quite often then. If we're not animating, we're usually not actively querying the time. However,
     *  if we query the time, we need a valid value (e.g. used when the animation is done and we want to know how
     *  long it's done). In this case, the timing recursively bubbles, eventually up to top level if necessary. This
     *  is less efficient, but saves updating the time regularly while no animationis running.
     *
     * Time is constructed from parent's current time and our local timeoffset. Parent's time is cached when active,
     * otherwise the current value is fetched dynamically from the parent. The cached parent time gets updated during
     * animation functions, and when activation/parent properties change.
     *
     * @return Local time in milliseconds.
     */
    public long getTime() {
        // calculate used time from parent time, time factor and time offset
        return Math.round(getParentTime() * getTimeFactor()) + mTimeOffset;
    }

    /**
     * @brief Access the parent time (internal function).
     *
     * Use cached time if we're running, or if we have no parent animation. Otherwise get dynamic time.
     *
     * @return Parent time (local to parent) in milliseconds.
     */
    public long getParentTime() {
        if (isRunningWithSubAnimations() || getParentAnimation() == null) {
            // we use the cached time
            return mCachedParentTime;
        } else {
            // get time dynamically from parent
            return getParentAnimation().getTime();
        }
    }

    /**
     * @brief Modify the instance's local time.
     *
     * This modifies the offset our local time has. It does not change anything about our timing, animation status
     * etc. It just shifts the timebase. This function can be of use if you're deriving from PDEAnimation and building
     * a custom animation. Instead of remembering when the animation started (which makes calculations more complex)
     * just reset the local time to zero.
     *
     * @param time New local time in milliseconds.
     */
    public void setTime(long time) {
        long oldtime;

        // get current time
        oldtime = getTime();

        // any changes?
        if (time == oldtime) {
            return;
        }

        // adjust our time offset so that the composite time is the desired time.
        mTimeOffset += time - oldtime;
    }


    /**
     * @brief Adjust timing by a factor.
     *
     * Mostly useful for debugging. You can slow down or speed up animations or groups of animations. This might enable
     * you to see what really happens, or might be used to simulate a slower device by speeding up internal timing.
     *
     * Use a factor <1.0 for slowing down, a factor >1.0 for speedup. Default is 1.0.
     *
     * 0.0 can be temporarily used to pause - the time is not changed any more. Be careful with this feature, since
     * this method of pausing everything does not unlink the animations from regular timing. The animation system still
     * uses power while paused as usual.
     */
    public double getTimeFactor() {
        return mTimeFactor;
    }

    /**
     * @brief Modify the time factor.
     *
     * Adjust the local timing speed. The parent time is scaled with the time factor. The default is 1.0, which
     * gives an identity timing. When adjusting the time factor, the local time does not change.
     *
     * To pause an animation, the time factor can be set to 0.0. However, this method of pausing an animation is
     * at the moment not recommended, since it doesn't change the running state, and this consumes resources
     * while paused. This behaviour might change in the future.
     *
     * @param timeFactor The time factor to set. Default is 1.0.
     */
    public void setTimeFactor(double timeFactor) {
        long time;

        // get current parent time
        time = getParentTime();

        // adjust time (using old factor for time calculation)
        mTimeOffset += Math.round(time * (mTimeFactor - timeFactor));

        mTimeFactor = timeFactor;
    }


    /**
     * @brief Timing propagation.
     *
     * Internal use only. The function is called when something in the parent time did change. Only called when we're
     * running. The usual behaviour is to cache the parent time (to simplify timing lookups in deep trees later), and
     * then call the (overloaded) animate function to calculate the actual animation. After calculations, change management
     * messages will be sent.
     *
     * The function is at the moment also called if a parent's time base changed. This behaviour will change - parent
     * time base changes should not affect subanimations, so a different adjustment function is necessary.
     */
    protected void parentTimeDidChange() {
        // don't do anything if not running
        if (!isRunningWithSubAnimations()) {
            return;
        }

        // cache new parent time. Manually access it (otherwise we would just get the cached time)
        if (getParentAnimation() != null) {
            mCachedParentTime = getParentAnimation().getTime();
        }

        // remember we're in an animation loop (-> so we don't register for special updates)
        setInAnimation(true);

        // animate
        animate();

        // no longer in animation loop
        setInAnimation(false);

        // and send out change notifications
        sendChanges();
    }

    /**
     * @brief Implement this function in derived classes.
     *
     * When this function is called, the time value did change and we're running. If the time
     * value changes for another reason (e.g. because someone changed the timebase manually), animate won't get called.
     * This is per design, because mostly the timebase gets changed by the animation itself when reconfiguring - and
     * we wouldn't want an explicit animate in this case.
     */
    public void animate() {

    }

//----- change management ----------------------------------------------------------------------------------------------

    /**
     * @brief Set changed flag.
     *
     * Be sure to call this function whenever something in your animation changes. Pure timing changes are not changes
     * in this sense (unless you're specifically building an animation whose output is the current time).
     *
     * When the changed flag is set, changed notifications will be sent out after everything is animated, in one continuous
     * block. Subanimation changes will be sent before parent animation changes. Any other ordering guarantee is not
     * given.
     *
     * Be aware that the changed flag only gets set if there's actually someone listening for changes. If not, it doesn't
     * get set, to avoid the eventually expensive resetting of the changed flags when the animation/change management
     * phase is done. That's the reason why there's no explicit isChanged method to check for changes.
     */
    public void setChanged() {
        // change management is disabled when we're not linked
        if (getParentAnimation() == null) {
            return;
        }

        // are we already changed?
        if (mChanged) {
            return;
        }

        // change management while not running is expensive. So only remember the change if we're either running
        // (->then change management is cheap), or if there's someone actually listening.
        if (isRunningWithSubAnimations() || getDidChangeTarget() != null) {
            // now remember ourself as changed
            mChanged = true;
            // and register ourself for explicit change management if we're not running,
            // and if we're not inside an animation loop (then we'll called anyway,
            // so we can save on the additional effort)
            if (!isRunningWithSubAnimations() && !isInAnimation()) {
                setChangedRegisteredWithParent();
            }
        }

        // we always need to tell the parent that something inside us has changed (maybe someone is listening on the
        // parent, but we're ourself not remembering because we're ??????
        if (getParentAnimation() != null) {
            getParentAnimation().setChanged();
        }
    }


    /**
     * @brief Register ourself for explicit change management in parent.
     *
     * Only register once, don't register again when we're already registered. By registering ourself
     * we notify the parent that we need a change management phase and are called back later.
     * This flag is completely independent from the changed flag itself - it manages if we're actually
     * called, not if we're changed.
     */
    public void setChangedRegisteredWithParent() {
        // don't accept if we have no parent
        if (getParentAnimation() == null) {
            return;
        }

        // are we already registered?
        if (mChangedRegisteredWithParent) {
            return;
        }

        // remember
        mChangedRegisteredWithParent = true;

        // if we're now changed, we have to register
        getParentAnimation().registerSubAnimationForChanged(this);
    }


    /**
     * @brief Set target and method called when something was changed.
     *
     * The target is not stored by a strong reference.
     *
     * Change management only happens if an animation is running and fully linked.
     * Otherwise, changes (probably manually initiated) are ignored.
     *
     * Note: This behaviour might change. At the moment we're trying to keep the logic easy.
     *
     * @param target Instance to be called.
     * @param methodName Name of method to be called.
     */

    public void setDidChangeTarget(Object target, String methodName) {
        // no target? clear out all.
        if (target == null) {
            clearDidChangeTarget();
            return;
        }

        // remember
        mDidChangeTarget = new WeakReference<Object>(target);

        // does the target really respond to the specified method
        try {
            //mDidChangeMethod = target.getClass().getMethod(methodName, new Class[] {Object.class});
        	// changed 10.10.2012 kd - no parameter for time this method is called from PDEAgentController:init()
        	mDidChangeMethod = target.getClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    /**
     * @brief Clear the target link. The notification on change be sent any more.
     */
    public void clearDidChangeTarget() {
        // clear it
        mDidChangeTarget = null;
        mDidChangeMethod = null;
    }

    /**
     * @brief Send out all pending change notifications.
     *
     * Internal function. After eventually sending out a change notification, clear all change handling flags.
     * We don't need to unregister from parent changed list (which would be expensive); the caller is responsible
     * for properly cleaning the list of registered subanimations.
     */
    protected void sendChanges() {
        // send change notification if we're really changed. The change sending might have been processed earlier.
        if (mChanged) {
            // send our own change
            sendDidChange();
        }

        // clear flags anyway (especially  the registered flag)
        mChanged = false;
        mChangedRegisteredWithParent = false;
    }

    /**
     * @brief Send the didChange notification to the stored target/method pair.
     *
     * Helper function. No change to the flags. Just do the send call to the target.
     */
    void sendDidChange() {
        Object target;

        // get and hold target (it might get cleaned up during function run time)
        target = getDidChangeTarget();

        // do we have a target?
        if (target == null) {
            return;
        }

        if (mDidChangeMethod != null) {
            try {
                // then send it
                mDidChangeMethod.invoke(target);
            } catch (IllegalAccessException e) {
                Log.e(LOG_TAG, "sendDidChange IllegalAccessException target: "+target.toString()+ " method:"+mDidChangeMethod.getName());
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                Log.e(LOG_TAG, "sendDidChange InvocationTargetException target: "+target.toString()+ " method:"+mDidChangeMethod.getName());
                e.printStackTrace();
            }
        }
    }


    /**
     * @brief Clear all pending change notifications.
     *
     * Also unregister ourselves from the parent's class. This function is used to shutdown the change notification
     * system upon unlinking - so we have to accept the possibly expensive manual cleanup.
     */
    void clearChanges() {
        // remove function from parent
        if (isChangedRegisteredWithParent() && getParentAnimation() != null) {
            getParentAnimation().unregisterSubAnimationForChanged(this);
        }

        // clear flags anyway
        mChanged = false;
        mChangedRegisteredWithParent = false;
    }

}
