/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.events;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;


//----------------------------------------------------------------------------------------------------------------------
//  PDEEventSource
//----------------------------------------------------------------------------------------------------------------------


// ToDo add more description here

/**
 * @brief Event source.
 *
 * The event source class manages the distribution of events. Listeners are registered here. Listeners
 * are methods of other classes getting sent the events and handling them. All
 * listeners must conform to the method signature pattern void <functionname> (PDEEvent event).
 *
 * If a class wants to send events, it should create it's own event source and expose it to other
 * classes to register their listeners. All events are then sent to the owned event source and get
 * automatically distributed.
 *
 */
public class PDEEventSource implements PDEIEventSource {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEEventSource.class.getName();

    // ToDo check if Doxygen doku is needed here due to private class

    /**
     * @brief Helper class for holding a listener reference (weak), the callback-method and additional data.
     */
    private class Listener {
        // @new
        /**
         * @brief Source of this listener.
         *
         * When listeners are removed, they are declared invalid by clearing their event source.
         * Sometimes, listeners are used for keeping track of the objects listened to.
         * This pattern is more lightweight than sending destroy messages all the time.
         */
        //@property (readwrite,nonatomic,unsafe_unretained) PDEEventSource *source;
        // for now:
        WeakReference<PDEEventSource> mSource;


        /**
         * @brief Reference to the listener class.
         *
         *  A weak reference, which gets cleared automatically when the listener no longer exists.
         *  Nonexisting listeners are completely removed from the EventSource the next time an event is sent.
         *  Until then, the helper class still exists as leftover.
         */
        WeakReference<Object> mTarget;

        /**
         * @brief The method to be called.
         */
        Method mMethod;

        /**
         * @brief An event mask against which the events get filtered.
         *
         * Only events matching the mask get sent to the target/method.
         *
         * The event mask does not store the trailing wildcard '*', which is stored in a seperate property
         * wildcard. This helps with easier/speedier implementation.
         */
        String mEventMask;

        /**
         * @brief Marker if the mask should be compared with wildcards.
         *
         * True of the eventmask is a wildcard and as such is compared to the first characters of an actual event.
         * False if the eventmask must match exactly.
         */
        boolean mWildcard;

        /**
         * @brief Constructor
         *
         * @param target Reference to the listener class.
         * @param method The method to be called.
         * @param eventMask An event mask against which the events get filtered.
         * @param source Source of this listener
         */
        public Listener(Object target, Method method, String eventMask, PDEEventSource source) {
            mTarget = new WeakReference<Object>(target);
            mMethod = method;

            // @new
            mSource = new WeakReference<PDEEventSource>(source);

            // check eventMask for wildcard
            if (eventMask.length() != 0 && eventMask.charAt(eventMask.length() - 1) == '*') {
                // wildcard present, store string without wildcard
                mEventMask = eventMask.substring(0, eventMask.length() - 1);
                // and remember that there was a wildcard
                mWildcard = true;
            } else {
                // no wildcard present
                mEventMask = eventMask;
                mWildcard = false;
            }
        }
    }


    /**
     * @brief The array of listeners.
     *
     * Holds PDEEventSourceListener objects pointing to the listeners.
     */
    private LinkedList<Listener> mListeners = null;

    // @new
    /**
     * @brief The array of listeners.
     *
     * Holds listeners we're using to forward events from other classes.
     */
    private LinkedList<Listener> mForwardEventSources = null;

    /**
     * @brief An optional delegate which gets notified when listeners are added.
     */
    private WeakReference<PDEIEventSourceDelegate> mEventSourceDelegate;

    /**
     * @brief Default event source. If an event is sent with no sender set, this sender is used.
     *
     *  This is mainly a convenience for easier composing of events. If an event source is
     *  used by exactly one class, the defaultSource can be set. This has to be done manually by calling
     *  setEventDefaultSender(this) during the constructor of the class that uses the event source.
     */
    private WeakReference<Object> mEventDefaultSender;

    // @new
    private boolean mEventOverrideSender;


    /**
     * @brief Constructor.
     */
    public PDEEventSource() {
        // init variables
        mListeners = new LinkedList<Listener>();
        // @new
        mForwardEventSources = new LinkedList<Listener>();
        mEventSourceDelegate = null;
        mEventDefaultSender = null;
        // @new
        mEventOverrideSender = false;
    }

    @Override
    public PDEEventSource getEventSource() {
        return this;
    }

    /**
     * @brief Add a listener to the list.
     *
     * A listener consists of an object and a method signature conforming to void <functionname>(PDEEvent event)
     *
     * Listeners handle events. They get sent the events processed by the event source. Listeners are
     * remembered in the order they were added, and events get send to the listeners in this order.
     * There's no duplicate checking, so multiply added listeners will be called multiple times.
     *
     * This function adds a listener for all events. No filtering is performed.
     *
     * @param target The target class events get sent to. Only a weak reference is held.
     * @param methodName The name of the method to be called. It's a method of the target-Object. Method must
     *                   conform to void <methodName> (PDEEvent event)
     * @return Returns an internal class identifying the listener added. This reference can be used
     *         to remove the listener later.
     */
    public Object addListener(Object target, String methodName) {
        // add this listener as a listener for all events
        return addListener(target, methodName, "*");
    }

    /**
     * @brief Add a listener to the list, with a filter for the events that should be sent.
     *
     * Events names are structured like "<classname>.<event>", eventually with an additional group
     * as in "<classname>.<group>.<event>". Events can be filtered my matching the event name exactly,
     * or by wildcard filtering matching the beginning of the event name. For example the filter string
     * "<classname>.*" would only pass through events starting with "<classname>.".
     *
     * See addListener: for additional information about listener order.
     *
     * @param target The target class events get sent to. Only a weak reference is held.
     * @param methodName The name of the method to be called. It's a method of the target-Object. Method must
     *                   conform to void <methodName> (PDEEvent event)
     * @return Returns an internal class identifying the listener added. This reference can be used
     *         to remove the listener later.
     */
    public Object addListener(Object target, String methodName, String eventMask) {

        Method method = null;
        //security
        if (target == null || mListeners == null) {
            Log.w(LOG_TAG, "listener array or target is null!");
            //error
            return null;
        }

        try {
            // check if the given method really is declared for target object

            method = target.getClass().getMethod(methodName, new Class[] {PDEEvent.class});

        } catch (NoSuchMethodException e) {
            // try to solve in the next block
        }

        if (method == null) {
            try {
                // check if the given method really is declared for target object
                method = target.getClass().getMethod(methodName);
            } catch (NoSuchMethodException e) {
                // try to solve in the next block
            }
        }


        if (method != null) {

            // create listener helper structure & fill in data
            Listener newListener = new Listener(target, method, eventMask,this);
            if (mListeners.add(newListener)) {
                // tell the delegate
                // @new
                /*if (mEventSourceDelegate != null && mEventSourceDelegate.get() != null
                    && mEventSourceDelegate.get() instanceof PDEIEventSourceDelegate) {
                    mEventSourceDelegate.get().eventSourceDidAddListener(newListener);
                } */
                requestInitializationForListener(newListener);
                // return the internal listener object
                return newListener;
            } else {
                //error
                return null;
            }
        } else {
            Log.e(LOG_TAG, "addListener: method '"+methodName+"' for target "+target.toString( )+ " was not found!");
            return null;
        }
    }


    /**
     * @brief Remove the specified listener from the list.
     *
     * @param listener The listener reference returned by addListener:
     * @return Returns whether we have found & removed the listener or not
     */
    public boolean removeListener(Object listener) {
        boolean removed = false;
        try {
            // iterate through list
            for (Iterator<Listener> iterator = mListeners.iterator(); iterator.hasNext(); ) {
                Listener element = iterator.next();
                // check equality of references
                if (element == listener) {
                    // request deinitialization -> this tells our delegate, and all listeners we're forwarding from
                    requestDeinitializationForListener(element);
                    // mark this listener as no longer valid (other classes might use it for this purpose)
                    element.mSource = null;
                    // listener was found; remove it and stop further searching
                    iterator.remove();
                    // remember
                    removed = true;
                    break;
                }
            }
        } catch (ConcurrentModificationException e) {
            Log.w(LOG_TAG, "List of Listeners changed during iteration!");
        }

        return removed;
    }



    /**
     * @brief Remove all entries pointing to the target class.
     *
     * All existing listeners pointing to the target are removed. If an individual target/method combination
     * should be removed, remember the specific listener reference returned when adding the listener and
     * use removeListener:.
     *
     * @param target Target for which the listeners should be removed.
     * @return Returns false if no listener was removed. (Otherwise true)
     */
    public boolean removeListenersForTarget(Object target) {
        boolean removed = false;

        // security
        if (target == null || mListeners == null) {
            return removed;
        }

        try {
            for (Iterator<Listener> iterator = mListeners.iterator(); iterator.hasNext(); ) {
                Listener element = iterator.next();
                // does it match?
                if (element.mTarget.get() == target) {
                    // @new
                    // request deinitialization
                    requestDeinitializationForListener(element);
                    // mark this listener as no longer valid (other classes might use it for this purpose)
                    element.mSource = null;
                    iterator.remove();
                    // remember
                    removed = true;
                }
            }

        } catch (ConcurrentModificationException e) {
            Log.w(LOG_TAG, "List of Listeners changed during iteration!");
        }

        return removed;
    }


    // @new
    /**
     * @brief Internal function (mainly). Request initialization for a newly created listener.
     *
     * The request is sent to our own delegate, and propagated to all event sources we're listening on. If the
     * event sources are already destroyed (we're loosely coupled), they are removed in the process.
     */
    protected void requestInitializationForListener(Object listener){
        Listener l;
        boolean needsCleanup;
        int i;
        PDEEventSource source;

        // tell our delegate
        if (getEventSourceDelegate() != null){
            getEventSourceDelegate().eventSourceDidAddListener(listener);
        }

        // request from all event sources we're listening on
        needsCleanup = false;
        for (Iterator<Listener> iterator = mForwardEventSources.iterator(); iterator.hasNext(); ) {
            l = iterator.next();
            // still valid?
            if(l.mSource != null){
                source = l.mSource.get();
                if (source != null){
                    // request initialization from forward source
                    source.requestInitializationForListener(listener);
                }
            } else {
                // remember that we need cleanup
                needsCleanup = true;
            }
        }

        // do cleanup if necessary
        if(needsCleanup){
            // go through all array elements backwards
            for (i=mForwardEventSources.size()-1; i>=0; i--){
                // get the listener
                l = mForwardEventSources.get(i);
                // we can forget it if the event source we're listening on no longer exists.
                if(!(l.mSource != null && l.mSource.get() != null) ){
                    // forget remove it
                    mForwardEventSources.remove(i);
                }
            }
        }

    }

    // @new
    /**
     * @brief Internal function (mainly). Request initialization for a newly created listener.
     *
     * The request is sent to our own delegate, and propagated to all event sources we're listening on. If the
     * event sources are already destroyed (we're loosely coupled), they are removed in the process.
     */
    protected void requestDeinitializationForListener(Object listener){
        Listener l;
        boolean needsCleanup;
        int i;
        PDEEventSource source;

        // tell our delegate
        if ( getEventSourceDelegate() != null
             && getEventSourceDelegate() instanceof PDEIEventSourceDelegate){
            getEventSourceDelegate().eventSourceWillRemoveListener(listener);
        }

        // request from all event sources we're listening on
        needsCleanup = false;
        for (Iterator<Listener> iterator = mForwardEventSources.iterator(); iterator.hasNext(); ) {
            l = iterator.next();
            // still valid?
            if (l.mSource != null){
                source = l.mSource.get();
                if (source != null){
                    // request initialization from forward source
                    source.requestDeinitializationForListener(listener);
                }
            } else {
                // remember that we need cleanup
                needsCleanup = true;
            }
        }

        // do cleanup if necessary
        if(needsCleanup){
            // go through all array elements backwards
            for (i=mForwardEventSources.size()-1; i>=0; i--){
                // get the listener
                l = mForwardEventSources.get(i);
                // we can forget it if the event source we're listening on no longer exists.
                if(!(l.mSource != null && l.mSource.get() != null) ){
                    // forget remove it
                    mForwardEventSources.remove(i);
                }
            }
        }
    }

    // @new
    /**
     * @brief Helper function, can be called if we need exactly one set of initialization events to a specified location.
     *
     * Creates a temporary listener and initializes it, but does not keep it around.
     */
    @SuppressWarnings("unused")
    protected void requestOneTimeInitialization(Object target, String methodName){
        // call extended function
        requestOneTimeInitialization(target,methodName,"*");
    }

    // @new
    /**
     * @brief Helper function, can be called if we need exactly one set of initialization events to a specified location.
     *
     * Creates a temporary listener and initializes it, but does not keep it around.
     */
    public void requestOneTimeInitialization(Object target, String methodName, String eventMask){
        Listener listener;

        //security
        if (target == null ) {
            Log.w(LOG_TAG, "target is null!");
            //error
            return;
        }

        try {
            // check if the given method really is declared for target object
            Method method = target.getClass().getMethod(methodName, new Class[] {PDEEvent.class});
            // create a listener helper structure
            listener = new Listener(target,method,eventMask,this);

            // request initialization -> this tells our delegate, and all listeners we're forwarding from
            requestInitializationForListener(listener);

            // cleanup the listener object (for safety, noone should keep it around)
            listener.mSource = null;
        } catch (NoSuchMethodException e) {
            // error handling, if method was not part of target-object
            e.printStackTrace();
            //error
        }
    }

    // @new
    @SuppressWarnings("unused")
    protected void requestOneTimeDeinitialization(Object target, String methodName){
        // call extended function
        requestOneTimeDeinitialization(target,methodName,"*");
    }

    // @new
    protected void requestOneTimeDeinitialization(Object target, String methodName, String eventMask){
        Listener listener;

        //security
        if (target == null ) {
            Log.w(LOG_TAG, "target is null!");
            //error
            return;
        }

        try {
            // check if the given method really is declared for target object
            Method method = target.getClass().getMethod(methodName, new Class[] {PDEEvent.class});
            // create a listener helper structure
            listener = new Listener(target,method,eventMask,this);

            // request initialization -> this tells our delegate, and all listeners we're forwarding from
            requestDeinitializationForListener(listener);

            // cleanup the listener object (for safety, noone should keep it around)
            listener.mSource = null;
        } catch (NoSuchMethodException e) {
            // error handling, if method was not part of target-object
            e.printStackTrace();
            //error
        }
    }


    /**
     * @brief Send the event to all listeners.
     *
     * The event is sent until one of the listeners marks it as processed, or is sent forcibly to
     * all listeners if the event is marked for distribution to all. The event type is filtered
     * and only sent to listeners whose filter masks matches the event type. If no sender is set
     * in the event, the defaultSender is used.
     *
     * The event sent remains under control of the sender, and can be checked for results and for
     * processing status after the sendEvent: function has been executed.
     *
     * In the case that an orphaned listener (the target is already destroyed) is encountered during
     * the send process, it is removed from the array of listeners. However, it's not guaranteed that
     * we find these orphans when an event is taken out early.
     *
     * @param event PDEEvent or derived class to be sent to the listeners.
     * @return Processing status. True if the event was marked as processed, false if the event passed all
     *         listeners without any listener setting the processed flag.
     */
    public boolean sendEvent(final PDEEvent event) {
        try {
            // go through all listeners and send it
            for (Iterator<Listener> iterator = mListeners.iterator(); iterator.hasNext(); ) {
                Listener listener = iterator.next();
                // stop if the event is already processed and we should not distribute to all
                if (event.isProcessed() && !event.isDistributeToAll()) {
                    break;
                }
                // do we still have the the target?
                if (listener.mTarget != null && listener.mTarget.get() != null) {
                    // send the event to this listener
                    sendEvent(event, listener);
                } else {
                    // mark the listener as no longer valid
                    listener.mSource = null;
                    // target doesn't exist anymore, so remove listener
                    iterator.remove();
                }
            }
        } catch (ConcurrentModificationException e) {
            Log.w(LOG_TAG, "List of Listeners changed during iteration!");
        }

        return event.isProcessed();
    }

    /**
     * @brief Send the event to a specified listener.
     *
     * This method contains the actual event sending logic. It checks the event wildcard,
     * so only events actually listened to are sent. If no sender is set in the event, the defaultSender is used.
     * This method is very specialized because it sends the event only to one specific listener. Normally if an event
     * is sent all available listeners should have the chance to react on the event. So use this method only for this
     * special case and use the more convenient methods otherwise.
     *
     * @param event PDEEvent or derived class to be sent to the listeners.
     * @param listenerObject The listener reference returned by addListener:
     * @return Processing status. True if the event was marked as processed, false if the event passed all
     *         listeners without any listener setting the processed flag.
     */
    public boolean sendEvent(final PDEEvent event, Object listenerObject) {
        Object target, originalSender;
        Listener listener;
        boolean returnValue = false;

        //security
        if (listenerObject == null) {
            return returnValue;
        }
        // check listener class
        if (!(listenerObject instanceof Listener)) {
            return returnValue;
        } else {
            listener = (Listener) listenerObject;
        }


        // check if the wildcard matches
        if (!((listener.mWildcard
               && (listener.mEventMask.length() == 0 || event.getType().startsWith(listener.mEventMask)))
              || (!listener.mWildcard) && event.getType().equalsIgnoreCase(listener.mEventMask))) {
            // stop here
            return returnValue;
        }

        // remember the original sender
        originalSender = event.getSender();

        // @new
        // now is the time to fill in the default sender if there is nothing defined in the event
        /*if (event.getSender() == null && mEventDefaultSender != null && mEventDefaultSender.get() != null) {
            event.setSender(mEventDefaultSender.get());
        } */
        if(mEventOverrideSender || event.getSender() == null && mEventDefaultSender != null){
            // change the event's sender
            event.setSender(mEventDefaultSender.get());
        }


        // get target (it may get cleaned up in the meantime)
        if (listener.mTarget == null) {
            // stop here
            return returnValue;
        }
        target = listener.mTarget.get();

        // do we have a target?
        if (target == null) {
            return returnValue;
        }

        try {
            // then send it
            listener.mMethod.invoke(target, event);

            // as convenience, return the processed status
            returnValue = event.isProcessed();
        } catch (IllegalAccessException e) {
            Log.d(LOG_TAG,"sendEvent: IllegalAccessException "+e.getCause().getLocalizedMessage()+
                    "\ntarget: "+target.toString()+
                    "\nevent: "+event.getType());
            e.printStackTrace();
            returnValue = false;
        } catch (InvocationTargetException e) {
            // there was an exception in the method we called -> fail
            Log.e(LOG_TAG,"sendEvent: InvocationTargetException "+e.getCause().getLocalizedMessage()+
                    "\ntarget: "+target.toString()+
                    "\nevent: "+event.getType());
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        // back to original
        event.setSender(originalSender);

        return returnValue;
    }

    /**
     * @brief Convenience function for quick event sending.
     *
     * Composes a simple event of the given type with no additional data and sends it.
     * The event class is PDEEvent.
     *
     * @param type The event type.
     * @return Processing status. True if the event was marked as processed.
     */
    public boolean sendEvent(String type) {
        PDEEvent event;

        // create event
        event = new PDEEvent();
        event.setType(type);

        // and send it
        return sendEvent(event);
    }

    /**
     * @brief Convenience function for quick event sending.
     *
     * Composes a simple event of the given type with no data and sends it. The sender gets filled in
     * from the default sender if it is specified.
     */
    public boolean sendEvent(String type, Object listenerObject) {
        PDEEvent event;

        // create event
        event = new PDEEvent();
        event.setType(type);

        // and send it
        return sendEvent(event, listenerObject);
    }




    // ToDo check doku

    /**
     * @brief Convenience function. Forward all events from another source to all listeners on this EventSource.
     *
     * You can build event processing trees with this - just add our event source as forwarder to
     * another source and all events get routed to all our listeners. At the moment, the delegate functions
     * when a new listener is added are not passed on to the sources.
     *
     * @param eventSource The EventSource that is listened on.
     * @return The internal listener reference that can be used to remove the forwarding.
     */
    public Object forwardEvents(PDEIEventSource eventSource) {
        Object listener;

        // add a listener, remember the listener (it can be used later to determine if the listener is still linked in)
        listener = eventSource.addListener(this,"forwardEventHelper");

        // store listener in an array of forwarded event listeners
        mForwardEventSources.add((Listener)listener);

        return listener;
    }

    /**
     * @brief Convenience function. Forward events from another source through ourself.
     *
     * You can build event processing trees with this - just add our event source as forwarder to
     * another source and all events get routed to all our listeners. With the EventMask you can specify which
     * events are listened to. At the moment, the delegate functions
     * when a new listener is added are not passed on to the sources.
     *
     * @param eventSource The EventSource that is listened on.
     * @param eventMask The filter of the events that should be sent.
     * @return The internal listener identifier that can be used to remove the forwarding.
     */
    public Object forwardEvents(PDEIEventSource eventSource, String eventMask) {
        Object listener;

        // add a listener, remember the listener (it can be used later to determine if the listener is still linked in)
        listener = eventSource.addListener(this, "forwardEventHelper", eventMask);

        // store listener in an array of forwarded event listeners
        mForwardEventSources.add((Listener)listener);

        return listener;
    }


    /**
     * @brief Helper function doing the actual event forwarding
     *
     * Linked into the EventSource we want to get the events from. Just a function signature
     * adjustment. Immediately sends on the events. It's not intended to call this function directly,
     * only by a listener.
     *
     * @param event The event that was sent.
     */
    @SuppressWarnings("unused")
    public void forwardEventHelper(PDEEvent event) {
        // just send it to ourself and ignore the result code
        sendEvent(event);
    }



    /**
     * @brief Setter for default sender.
     *
     * The default sender is the object that is filled in as sender if no sender was manually specified before.
     *
     * @param eventDefaultSender The default sender object.
     */
    public void setEventDefaultSender(Object eventDefaultSender, boolean override) {
        mEventDefaultSender = new WeakReference<Object>(eventDefaultSender);
        mEventOverrideSender = override;
    }

    /**
     * @brief Getter for default sender.
     *
     * The default sender is the object that is filled in as sender if no sender was manually specified before.
     *
     * @return The default sender object.
     */
    @SuppressWarnings("unused")
    public Object getEventDefaultSender() {
        if (mEventDefaultSender == null) {
            return null;
        }
        return mEventDefaultSender.get();
    }


    // @new
    @SuppressWarnings("unused")
    public void setEventOverrideSender(boolean override){
        mEventOverrideSender = override;
    }

    // @new
    @SuppressWarnings("unused")
    public boolean getEventOverrideSender(){
        return mEventOverrideSender;
    }


    /**
     * @brief Setter for delegate.
     */
    public void setEventSourceDelegate(PDEIEventSourceDelegate delegate) {
        mEventSourceDelegate = new WeakReference<PDEIEventSourceDelegate>(delegate);
    }

    /**
     * @brief Getter for delegate.
     */
    public PDEIEventSourceDelegate getEventSourceDelegate() {
        if (mEventSourceDelegate == null) {
            return null;
        }
        return mEventSourceDelegate.get();
    }
}
