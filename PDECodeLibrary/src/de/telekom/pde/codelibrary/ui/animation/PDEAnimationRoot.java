/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

// ToDo Rework runloop stuff after PDEFrameTiming-class has finished

package de.telekom.pde.codelibrary.ui.animation;


//----------------------------------------------------------------------------------------------------------------------
//  PDEAnimationRoot
//----------------------------------------------------------------------------------------------------------------------


import de.telekom.pde.codelibrary.ui.timing.PDEFrameTiming;

/**
 * @brief The root animation.
 *
 * The root animation is a singleton (although you could create several root animations, which would then
 * operate in parallel and have different controlling parameters like timing factors, this is not usually done).
 * The static function members all link to the root singleton.
 */

public class PDEAnimationRoot extends PDEAnimationGroup {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEAnimationRoot.class.getName();
    private final static boolean DEBUGPARAMS = false;

    protected boolean mRegisteredWithRunloop;

    // private helper class for putting a function call at end of runloop
    // ToDo Maybe this is only called when rest of message loop is idle; What shall we do if loop is quite busy?
   /* private class RunloopTimingHandler implements MessageQueue.IdleHandler {
        //private Object mDummy;

        //public RunloopTimingHandler(Object dummy) {
        public RunloopTimingHandler() {
            //mDummy = dummy;
        }

        public boolean queueIdle() {
            //runloopTiming(mDummy);
            runloopTiming();
            // ToDo this takes it out of the runloop is that correct? or should we react dynamically based on a
            // retValue?
            return false;
        }
    }
    */


    /**
     * @brief SingletonHolder is loaded on the first execution of PDECodeLibrary.getRoot()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        public static final PDEAnimationRoot INSTANCE = new PDEAnimationRoot();
    }


    /**
     * @brief Private Constructor prevents instantiation from other classes (singleton design
     * pattern).
     */
    private PDEAnimationRoot() {
        // adjust time offset once
        mTimeOffset = -(Math.round(PDEFrameTiming.getInstance().getFrameTime() * getTimeFactor()));
        mRegisteredWithRunloop = false;
    }

//    /**
//     * @brief destructor
//     *
//     * We don't have a parent, so we must make sure that we also get cleaned up (unregistered) properly.
//     */
//    protected void finalize() throws Throwable {
//       // clear all changes without sending notifications
//        clearChanges();
//
//        // we're no longer registered with the parent
//        setRunningRegisteredWithParent(false);
//    }

    /**
     * @brief Delivers the singleton instance of PDEAnimationRoot.
     *
     * Use this method instead of the constructor (which is private anyway).
     * It returns the singleton instance of this class.
     *
     * @return singleton instance of PDEAnimationRoot.
     */
    public static PDEAnimationRoot getRoot() {
        return SingletonHolder.INSTANCE;
    }


    /**
     * @brief Add animation to root singleton.
     */
    public static void addSubAnimationStatic(PDEAnimation animation) {
        getRoot().addSubAnimation(animation);
    }


    /**
     * @brief Add animation to root singleton and decide to store in strong or weak reference.
     */
    public static void addSubAnimationStatic(PDEAnimation animation, boolean strong) {
        getRoot().addSubAnimation(animation, strong);
    }


    /**
     * @brief Remove animation from root singleton.
     */
    public static void removeSubAnimationStatic(PDEAnimation animation) {
        getRoot().removeSubAnimation(animation);
    }


    /**
     * @brief Retrieve root singleton's time.
     */
    public static long getTimeStatic() {
        return getRoot().getTime();
    }

    /**
     * @brief Retrieve root singleton's time factor.
     */
    public static double getTimeFactorStatic() {
        return getRoot().getTimeFactor();
    }

    /**
     * @brief Set root singleton's time factor.
     */
    public static void setTimeFactorStatic(double timeFactor) {
        getRoot().setTimeFactor(timeFactor);
    }


//----- run management -------------------------------------------------------------------------------------------------

    /**
     * @brief Re-implementation to link to a different source.
     */
    @Override
    void setRunningRegisteredWithParent(boolean running) {
        // any change?
        if (running == mRunningRegisteredWithParent) {
            return;
        }

        // remember
        mRunningRegisteredWithParent = running;

        // action
        if (mRunningRegisteredWithParent) {
            // add to global frame timing
            PDEFrameTiming.getInstance().addListener(this, "frameTiming");
        } else {
            // we're not running any more, but we might have pending change (only register ourselves if we're not
            // currently processing an animation, in which case change management will be called anyway)
            if (mChanged && !isInAnimation()) {
                setChangedRegisteredWithParent();
            }
            // remove from global frame timing
            PDEFrameTiming.getInstance().removeListener(this);
        }

    }

    /**
     * @brief Register ourself for the next runloop call.
     *
     * We cannot unregister, we can just ignore the call then.
     */
    void registerWithRunloop() {
        // already registered?
        if (mRegisteredWithRunloop) {
            return;
        }

        // remember
        mRegisteredWithRunloop = true;

        // register
        PDEFrameTiming.getInstance().postExecuteFunction(this, "runloopTiming");
    }


//----- time and animation handling ------------------------------------------------------------------------------------

    /**
     * @brief Re-implementation of parent time function.
     */
    @Override
    long getParentTime() {
        // always use frame time
        return PDEFrameTiming.getInstance().getFrameTime();
    }

    /**
     * @brief Listener on global frame timing.
     *
     * If we're timed, propagate the new time throughout the animation system. Treat as parentTimeDidChange, we
     * also want to call our animate function (probably no overloaded) and want to notify any listeners of the change
     */
    public void frameTiming(Long time) {
        //ToDo Why don't we process the parameter in any way?
        // simply use the parentTimeDidChange function, this will do the rest
        parentTimeDidChange();
    }

    /**
     * @brief Delayed timing on runloop.
     *
     * We don't have to clear the changedRegisteredWithParent flag - either we're not running, then we do change
     * management here. Or it stays, then the next frameloop will do the work. If we get stopped inbetween, the unregister
     * from the frameloop will take care that runloop timing is set again.
     */
    public void runloopTiming() {
        // no longer registered in the runloop
        mRegisteredWithRunloop = false;

        // do nothing if we're still in the frame loop.
        if (isRunningRegisteredWithParent()) {
            return;
        }

        // send the pending changes
        sendChanges();
    }

//----- change management ----------------------------------------------------------------------------------------------

    /**
     * @brief Overload of base implementation.
     *
     * Pretty much the same as in PDEAnimationGroup, but we don't have an explicit parent, so leave out these checks.
     */
    @Override
    public void setChanged() {
        // are we already changed?
        if (mChanged) {
            return;
        }

        // change management while not running is expensive. So only remember the change if we or a child is running
        // (-> then change management is cheap), or if there's someone listening.
        if (isRunningWithSubAnimations() || getDidChangeTarget() != null || mChangedSubAnimations.size() > 0) {
            // now remember ourself as changed
            mChanged = true;
            // and register ourself for explicit change management if we're not running and if we're not inside an
            // animation loop (then we'll get called anyway, so we can save on the additional effort)
            if (!isRunningWithSubAnimations() && !isInAnimation()) {
                setChangedRegisteredWithParent();
            }
        }
    }


    /**
     * @brief Register ourself for explicit change management in parent.
     */
    @Override
    void setChangedRegisteredWithParent() {
        // are we already registered?
        if (mChangedRegisteredWithParent) {
            return;
        }

        // remember
        mChangedRegisteredWithParent = true;

        // if we're not registered with frametiming, we have to register for the runloop
        if (!isRunningRegisteredWithParent()) {
            // do a delayed function call
            registerWithRunloop();
        }
    }
}
