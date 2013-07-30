/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

// ToDo Try to use dictionary instead of LinkedList for linked subanimations

package de.telekom.pde.codelibrary.ui.animation;


import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;


//----------------------------------------------------------------------------------------------------------------------
//  PDEAnimationGroup
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief An animation group for grouping other animations.
 *
 * Animations can be grouped by using PDEAnimationGroup. Groups are useful to structure your animations. All
 * subanimations run on the group's timebase, which can be easily changed by modifying the group's time factor.
 * Animation groups also offer consolidated change management. The didChange: callback of an animation group gets
 * called if at least one of the subanimations did change during the animation phase, so it's easy to check for changes
 * after everything has actually been changed by listening on the group.
 *
 * Animation groups inherit functionality from PDEAnimation so they can be linked into other groups, building a
 * tree of animations. There's one special animation group, PDEAnimationRoot, which is the root instance of all
 * animations in the system and is linked into frame timing. Link an animation or group to PDEAnimationRoot to
 * start getting animation updates. Unlinked animations will never be updated or changing.
 *
 * Animation groups are optimized to save battery and processing power. They have a consolidated internal running
 * state and only set themselves to running if any of the subanimations is running. If an animation group is not
 * running, it goes completely idle and does not participate in the regular animation cycle.
 *
 * Animation groups are usually not derived from. There's not much to be changed inside an animation group. The
 * timing and animation behaviour should not be modified by a derived class.
 */
public class PDEAnimationGroup extends PDEAnimation {

    /**
     * @brief Global tag for log outputs.
     */
	@SuppressWarnings("unused")
    private final static String LOG_TAG = PDEAnimationGroup.class.getName();

    /**
     * @brief Helper class that offers us the possibility to store child-animations in weak or strong references.
     *
     * Animation groups are used to create tree structures. Normally the user stores the animation he creates in an
     * own (strong) references, before he adds it to a group. The system should have the possibility to get rid of
     * the animation object after the user has set his own (strong) reference to null. So by default we use just weak
     * references to store the child-animations. In some cases it might be useful that the tree structure itself
     * ensures, that the child-animation-object is not garbage collected. So this helper class provides the
     * possibility to set additionally a strong reference on the linked animation.
     */
    private class AnimationHolder {
        PDEAnimation mAnimationStrong;
        WeakReference<PDEAnimation> mAnimationWeak;

        /**
         * @brief constructor
         */
        AnimationHolder() {
            mAnimationStrong = null;
            mAnimationWeak = null;
        }

        /**
         * @brief Set the animation that should be stored and decide if we also hold an additional strong reference.
         *
         * @param animation Subanimation to be stored.
         * @param strong True if we want an additional strong reference on the SubAnimation.
         */
        void setAnimation(PDEAnimation animation, boolean strong) {
            mAnimationWeak = new WeakReference<PDEAnimation>(animation);
            if (strong) {
                mAnimationStrong = animation;
            }
        }

        /**
         * @brief Get stored SubAnimation.
         * @return stored SubAnimation.
         */
        PDEAnimation getAnimation() {
            if (mAnimationWeak != null){
                PDEAnimation animation;
                animation = mAnimationWeak.get();
                if (animation != null) {
                    return animation;
                }
            }
            return null;
        }
    }

    private class PDEAnimationGroupOperation {

        private WeakReference<Object> mObject;
        private int mOperation;

        PDEAnimationGroupOperation(Object object, int operation){
            setObject(object);
            setOperation(operation);
        }


        public void setObject(Object object){
            mObject = new WeakReference<Object>(object);
        }

        public Object getObject(){
            if(mObject != null){
                return mObject.get();
            } else {
                return null;
            }
        }

        public void setOperation(int operation){
            mOperation = operation;
        }

        public int getOperation(){
            return mOperation;
        }


    }

    // private constants
    protected  final int OPERATION_ADD = 0;
    protected final int OPERATION_REMOVE = 1;


    /**
     * @brief All SubAnimations that are linked to this group.
     */
    protected LinkedList<AnimationHolder> mSubAnimations;
    /**
     * @brief All SubAnimations that are registered for running.
     */
    protected LinkedList<WeakReference<PDEAnimation>> mRunningSubAnimations;
    /**
     * @brief All SubAnimations that are registered for explicit change management.
     */
    protected LinkedList<WeakReference<PDEAnimation>> mChangedSubAnimations;

    protected LinkedList<PDEAnimationGroupOperation> mRunningSubAnimationsPendingOperations;


    // some helpers
    protected boolean mInTimePropagation;
    public void setInTimePropagation(boolean inTime){
        mInTimePropagation = inTime;
    }
    public boolean isInTimePropagation(){
        return mInTimePropagation;
    }

    protected boolean mInSendingChanges;
    public void setInSendingChanges(boolean inSendingChanges){
        mInSendingChanges = inSendingChanges;
    }
    public boolean isInSendingChanges(){
        return mInSendingChanges;
    }



    /**
     * @brief constructor
     */
    public PDEAnimationGroup() {
        // init elements. Note: We want the array to hold pointers without referencing them.
        mSubAnimations = new LinkedList<AnimationHolder>();
        mRunningSubAnimations = new LinkedList<WeakReference<PDEAnimation>>();
        mChangedSubAnimations = new LinkedList<WeakReference<PDEAnimation>>();
        mRunningSubAnimationsPendingOperations = new LinkedList<PDEAnimationGroupOperation>();
        mInTimePropagation = false;
        mInSendingChanges = false;

    }

    /**
     * @brief Helper method to add an animation to a given list. Prevents code duplication.
     *
     * @param animation Animation to be added.
     * @param list The list we want to add the animation to.
     */
    private void addAnimationToCustomList(PDEAnimation animation, LinkedList<WeakReference<PDEAnimation>> list) {
        list.add(new WeakReference<PDEAnimation>(animation));
    }


    /**
     * @brief Helper method to remove an animation from a given list. Prevents code duplication.
     *
     * @param animation Animation to be removed.
     * @param list The list we want to remove the animation from.
     */
    private void removeAnimationFromCustomList(PDEAnimation animation, LinkedList<WeakReference<PDEAnimation>> list) {
        PDEAnimation anim;

        WeakReference<PDEAnimation> item;
        Iterator<WeakReference<PDEAnimation>> it = list.iterator();
        while (it.hasNext()) {
            item = it.next();
            if (item != null){
                anim = item.get();
                if (anim != null) {
                    if (anim == animation) {
                        // found given animation in list; remove it and quit
                        it.remove();
                        return;
                    }
                }
            } else {
                // found dead link, remove it
                it.remove();
            }
        }
    }


//----- structure handling ---------------------------------------------------------------------------------------------

    /**
     * @brief Add a sub-animation to the animation group.
     *
     * This method stores the SubAnimation just in a weak reference. If you want the SubAnimation to be stored in a
     * strong reference (for a good reason), call addSubAnimation(PDEAnimation animation, boolean strong) instead.
     *
     * @param animation Subanimation to add to the group.
     */
    public void addSubAnimation(PDEAnimation animation) {
        // all non-retained
        addSubAnimation(animation, false);
    }


    /**
     * @brief Add a sub-animation to the group.
     *
     * The subanimation is optionally retained and lives until explicitly removed or the group is released.
     *
     * @param animation Subanimation to add to the group.
     * @param strong true if the animation should be additionally stored in a strong reference.
     */
    public void addSubAnimation(PDEAnimation animation, boolean strong) {
        AnimationHolder holder;

        // is it already linked to us? then we don't need to do anything
        if (animation.getParentAnimation() == this) {
            return;
        }

        // linked to someone else??
        if (animation.getParentAnimation() != null) {
            // use proper removal, we need to remove it from the current parent
            animation.removeFromParentAnimation();
        }

        // remember in our list of animations
        holder = new AnimationHolder();
        holder.setAnimation(animation, strong);
        mSubAnimations.add(holder);

        // and set the parent of the animation, this also manages running state etc.
        animation.setParentAnimation(this);
    }


    /**
     * @brief Remove a sub-animation from this group.
     */
    public void removeSubAnimation(PDEAnimation animation) {
        PDEAnimation anim;

        // is it really linked to us?
        if (animation.getParentAnimation() != this) {
            return;
        }

        // clear parent (this manages running state etc.)
        animation.setParentAnimation(null);

        // and remove from the list
        for (Iterator<AnimationHolder> iterator = mSubAnimations.iterator(); iterator.hasNext(); ) {
            AnimationHolder item = iterator.next();

            // animation is probably not existing anymore
            if (item != null){
                anim = item.getAnimation();
                if (anim != null) {
                    if (anim == animation) {
                        // found element in list, remove it and stop searching
                        iterator.remove();
                        return;
                    }
                }
            } else {
                // found dead link in list, remove it
                iterator.remove();
            }
        }
    }

//----- run management -------------------------------------------------------------------------------------------------

    /**
     * @brief Register a sub-animation at the parent as running.
     *
     * Internal use only. Note that we must not change the running subanimations while animating; so for this
     * case we keep a list of animations to be added or removed.
     *
     * @param animation Animation to add to the parent's running list.
     */
    void registerSubAnimationForRunning(PDEAnimation animation) {
        // busy?
        if (isInTimePropagation()) {
            // add to creation list
            mRunningSubAnimationsPendingOperations.add(new PDEAnimationGroupOperation(animation,OPERATION_ADD));
            // stop here
            return;
        }

        // simply add to the list
        addAnimationToCustomList(animation, mRunningSubAnimations);

        // check run state
        checkRunning();
    }


    /**
     * @brief Unregister a sub-animation from the parent's list of running animations
     *
     * Internal use only.
     *
     * @param animation Animation to remove from the parent's running list.
     */
    void unregisterSubAnimationForRunning(PDEAnimation animation) {
        // busy?
        if (isInTimePropagation()) {
            // add to removal list
            mRunningSubAnimationsPendingOperations.add(new PDEAnimationGroupOperation(animation,OPERATION_REMOVE));
            // stop here
            return;
        }

        // remove from the list
        removeAnimationFromCustomList(animation, mRunningSubAnimations);

        // check run state
        checkRunning();
    }


    /**
     * @brief Overload for base function.
     *
     * Take subanimations into account. A group is running if it's marked as running, or if any subanimation
     * is registered for running. The running flag is not set by the current implementation of PDEAnimationGroup,
     * but it might used by a derived class.
     *
     * @result true if this instance should actually be running.
     */
    @Override
    boolean isRunningWithSubAnimations() {
        // check our own state and the state of subanimations
        if (isRunning() || mRunningSubAnimations.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

//----- timing and animation -------------------------------------------------------------------------------------------

    /**
     * @brief Overload of time adjustments.
     *
     * If the local time changes, children are notified. No own animation is done, no listeners are notified. However,
     * children might animate during this call, and register for change notifications.
     *
     * This function completely overrides the base implementation to avoid timing issues when the time is fetched
     * multiple times.
     *
     * @param time Time to set. Unit is seconds.
     */
    @Override
    public void setTime(long time) {
        long oldtime;

        // get current time
        oldtime = getTime();

        // any changes?
        if (time == oldtime) {
            return;
        }

        //adjust our time offset so that the composite time is the desired time.
        mTimeOffset += time - oldtime;

        // and propagate the changes to subanimations.
        propagateTime();
    }


    /**
     * @brief Overload of timing propagation. Internal only.
     *
     * In addition to the base functionality, we also have to propagate the change to our children. Replacement of
     * the original function, since the child update should happen after our own animation, but before listeners
     * are notified.
     */
    @Override
    protected void parentTimeDidChange() {
        // don't do anything if not running and no children are running
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

        // propagate to children
        propagateTime();

        // no longer in animation loop
        setInAnimation(false);

        // and send out change notifications
        sendChanges();
    }


    /**
     * @brief Propagate a time change to all children.
     *
     * Time changes are only propagated to running children. All other animations are not animated, and
     * get their time dynamically as needed.
     *
     * Time propagation happens when:
     *   - we're running ourself, and the parent time has changed
     *   - if our timebase is changed
     */
    protected void propagateTime() {
        PDEAnimation animation;

        // we're in propagation
        setInTimePropagation(true);

        for (Iterator<WeakReference<PDEAnimation>> iterator = mRunningSubAnimations.iterator(); iterator.hasNext(); ) {
            WeakReference<PDEAnimation> item = iterator.next();


            if (item != null){
                animation = item.get();
                // animation is maybe not existing anymore
                if(animation != null) {
                    animation.parentTimeDidChange();
                } else {
                    // remove the already deleted animation from the list
                    iterator.remove();
                }
            }
        }

        // done with this step
        setInTimePropagation(false);

        // we might have pending operations
        for (Iterator<PDEAnimationGroupOperation> iterator = mRunningSubAnimationsPendingOperations.iterator();
             iterator.hasNext();) {
            PDEAnimationGroupOperation operation = iterator.next();
            if (operation.getOperation() == OPERATION_ADD) {
                registerSubAnimationForRunning((PDEAnimation)operation.getObject());
            } else if (operation.getOperation() == OPERATION_REMOVE) {
                unregisterSubAnimationForRunning((PDEAnimation)operation.getObject());
            }
        }

        mRunningSubAnimationsPendingOperations.clear();
    }


//----- change management ----------------------------------------------------------------------------------------------

    /**
     * @brief Set changed flag.
     *
     * Modified from base behaviour to accomodate the fact that we can have children interested in change management.
     *
     * Set the changed flag if either we are sending out notifications, or if we have clients needing a change.
     */
    @Override
    public void setChanged() {
        // change management is disabled when we're not linked
        if (getParentAnimation() == null) {
            return;
        }

        // are we already changed?
        if (mChanged) {
            return;
        }

        // change management while not running is expensive. So only remember the change if we or a child is running
        // (-> then change management is cheap), or if there's someone actually listening.
        if (isRunningWithSubAnimations() || getDidChangeTarget() != null || mChangedSubAnimations.size() > 0) {
            // now remember ourself as changed
            mChanged = true;
            // and register ourself for explicit change management if we're not running,
            // and if we're not inside an  animation loop (then we'll get called anyway,
            // so we can save on the additional effort)
            if (!isRunningWithSubAnimations() && !isInAnimation()) {
                setChangedRegisteredWithParent();
            }
        }

        // we always need to tell the parent that something inside us has changed
        if (getParentAnimation() != null) {
            getParentAnimation().setChanged();
        }
    }


    /**
     * @brief Register one of our subanimations for change handling.
     *
     * Register a subanimation for change handling. The subanimation is added to the list. After the animation
     * phase is done, the list is processed.
     *
     * @param animation Subanimation to add to the list.
     */
    void registerSubAnimationForChanged(PDEAnimation animation) {
        // ToDo: CHECK: iOS implementation logic is slightly different here, but I guess we don't need it here
        // because it only depends on Thomas's special array handling that we don't use in android

        // now add to tail and count
        addAnimationToCustomList(animation, mChangedSubAnimations);


        // if we now have change listeners, we must set ourself to changed (in case this was not already done)
        setChanged();
    }


    /**
     * @brief Unregister a child animation for changes.
     *
     * This is an expensive operation. Only use it when absolutely necessary (e.g. for cleaning up / removing animations
     * from the tree). Normally, the list is processed in one piece and then cleared afterwards.
     *
     * @param animation Subanimation to remove from the list.
     */
    void unregisterSubAnimationForChanged(PDEAnimation animation) {
        int i;

        // iOS implementation logic is slightly different here, but I guess we don't need it here
        // because it only depends on Thomas's special array handling that we don't use in android

        // are we currently processing the list?
        if(isInSendingChanges()){
            // just invalidate the object by setting it to zero (we don't change the size of the list)
            for(i=0;i<mChangedSubAnimations.size();i++){
                WeakReference<PDEAnimation> item = mChangedSubAnimations.get(i);
                if( item != null && item.get() == animation){
                    mChangedSubAnimations.set(i,null);
                    return;
                }
            }
        } else {
            // simply remove it
            removeAnimationFromCustomList(animation, mChangedSubAnimations);
        }

    }


    /**
     * @brief Clear (forget) all pending subanimations.
     */
    void clearChangedSubAnimations() {
        mChangedSubAnimations.clear();
    }


    /**
     * @brief Send out all pending change notifications.
     *
     * First processes the subanimations, then sends out our own animations to get the correct order.
     */
    @Override
    protected void sendChanges() {
        PDEAnimation animation;

        // mark as "currently sending changes"
        setInSendingChanges(true);

        for (Iterator<WeakReference<PDEAnimation>> iterator = mChangedSubAnimations.iterator(); iterator.hasNext(); ) {
            WeakReference<PDEAnimation> item = iterator.next();

            // animation is probably not existing anymore
            if (item != null){
                animation = item.get();
                if(animation != null) {
                    animation.sendChanges();
                }
            }
        }

        // done sending changes
        setInSendingChanges(false);

        // clear pending change listeners
        clearChangedSubAnimations();

        // the rest can use the base implementation
        super.sendChanges();
    }

    /**
     * @brief Clear all pending change notifications.
     *
     * Also unregister ourselves from the parent's class. This function is used to shutdown the change notification
     * system upon unlinking.
     */
    @Override
    void clearChanges() {
        // clear list of pending change listeners
        clearChangedSubAnimations();

        // the rest can be done by the base implementation
        super.clearChanges();
    }


//    void setSubAnimationStrongReferenced(PDEAnimation subAnimation, boolean strong){
//        PDEAnimation animation;
//
//
//        /*
//        for (Iterator<WeakReference<PDEAnimation>> iterator = mSubAnimations.iterator(); iterator.hasNext();){
//            WeakReference<PDEAnimation> item = iterator.next();
//
//            // animation is probably not existing anymore
//            if(item!=null && item.get()!=null){
//                animation=item.get();
//                if(animation == subAnimation){
//                    // found it
//
//                }
//          */
//    }
}
