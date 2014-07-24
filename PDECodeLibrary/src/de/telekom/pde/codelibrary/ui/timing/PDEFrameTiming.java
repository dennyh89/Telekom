/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.timing;


import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;


//----------------------------------------------------------------------------------------------------------------------
//  PDEFrameTiming
//----------------------------------------------------------------------------------------------------------------------


public abstract class PDEFrameTiming {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEFrameTiming.class.getName();
    private final static boolean DEBUGPARAMS = false;


    protected final static long FRAME_TIME_DELTA_THRESHOLD = 2;
    protected final static long FRAME_IDLE_DELAY = 5;


    protected long mFrameTime;
    protected long mLastFrameTime;
    protected boolean mActive;
    protected boolean mInTiming;
    protected boolean mNeedsCleanup;

    protected boolean mLoopLocked;
    protected Handler mHandler;

    protected LinkedList<PDEFrameTimingListener> mListener = null;
    protected LinkedList<PostExecuteFunctionHolder> mRunnableList = null;

//----- Private helper classes -----------------------------------------------------------------------------------------

    private static class SingletonHolder {
        public static final PDEFrameTiming INSTANCE = createInstance();
    }

    private class PostExecuteFunctionHolder {
        public WeakReference<Runnable> runnable;
        public Object target;

        @SuppressWarnings("unused")
        private PostExecuteFunctionHolder(Runnable runnable, Object target) {
            this.runnable = new WeakReference<Runnable>(runnable);
            this.target = target;
        }

        private PostExecuteFunctionHolder(Object target) {

            this.target = target;
        }

        private void setRunnable(Runnable runnable) {
            this.runnable = new WeakReference<Runnable>(runnable);
        }
    }

    private class PDEFrameTimingListener {
        /**
         * @brief Reference to the listener class.
         *
         *  A weak reference, which gets cleared automatically when the listener no longer exists.
         *  Non-existing listeners are completely removed from the EventSource the next time an event is sent.
         *  Until then, the helper class still exists as leftover.
         */
        WeakReference<Object> mWeakTarget;

        /**
         * @brief Reference to the listener class.
         *
         *  A weak reference, which gets cleared automatically when the listener no longer exists.
         *  Non-existing listeners are completely removed from the EventSource the next time an event is sent.
         *  Until then, the helper class still exists as leftover.
         */
        Object mStrongTarget;


        /**
         * @brief The method to be called.
         */
        Method mMethod;

        /**
         * @brief Helper for removal during timing.
         */
        boolean mNeedsRemoval;

        /**
         * @brief Constructor
         *
         * @param target Reference to the listener class.
         * @param method The method to be called.
         */
        PDEFrameTimingListener(Object target, Method method, boolean strongReference) {
            mNeedsRemoval = false;
            mMethod = method;

            if (strongReference) {
                mStrongTarget = target;
                mWeakTarget = null;
            } else {
                mWeakTarget = new WeakReference<Object>(target);
                mStrongTarget = null;
            }
        }


        public Object getTarget() {
            if (mStrongTarget != null) {
                return mStrongTarget;
            } else {
                return mWeakTarget.get();
            }
        }
    }


//----- Singleton ------------------------------------------------------------------------------------------------------

    /**
     * @brief private function to instantiate the right instance of PDEFrameTiming.
     *
     * All functions with android api level 16 (Android 4.1) and higher use the choreographer. All older operation
     * systems a not so optimal solution.
     *
     * @return PDEFrameTiming instance
     */
    private static PDEFrameTiming createInstance() {
        boolean choreographerClassAvailable = false;
        try {
            Class<?> Choreographer;
            Choreographer = Class.forName("android.view.Choreographer");
            if (Choreographer != null) {
               choreographerClassAvailable = true;
            }
        } catch (ClassNotFoundException e) {
            // we have a fallback solution, so don't irritate the user with this stack trace
            //e.printStackTrace();
        }

        if (choreographerClassAvailable) {
            // the following code does the same as:
            // return new PDEFrameTimingChoreographer();
            // but doesn't create a dalvik error (exception)

            Class<?> frameTimeChoreographer;
            try {
                frameTimeChoreographer = Class.forName(
                        "de.telekom.pde.codelibrary.ui.timing.PDEFrameTimingChoreographer");
                if (frameTimeChoreographer != null) {
                    // create and return PDEFrameTimingChoreographer
                    return (PDEFrameTiming) frameTimeChoreographer.newInstance();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        return new PDEFrameTimingOrdinary();
    }


    /**
     * #brief getInstance of frame timing - singleton pattern.
     * @return static PDEFrameTiming object
     */
    @SuppressWarnings("SameReturnValue")
    public static PDEFrameTiming getInstance() {
        return SingletonHolder.INSTANCE;
    }

//----- Constructor ----------------------------------------------------------------------------------------------------

    /**
     * @brief Protected Constructor due to Singleton pattern.
     */
    protected PDEFrameTiming() {
        init();
    }

//----- Initialization -------------------------------------------------------------------------------------------------

    /**
     * @brief Initialization of member variables.
     */
    protected void init() {
        mListener = new LinkedList<PDEFrameTimingListener>();
        mRunnableList = new LinkedList<PostExecuteFunctionHolder>();

        mLoopLocked = false;

        mFrameTime = 0;
        mLastFrameTime = 0;
        mActive = false;
        mInTiming = false;
        mNeedsCleanup = false;

        mLoopLocked = false;

        mHandler = new Handler(Looper.getMainLooper());
    }


//----- Abstract functions ---------------------------------------------------------------------------------------------


    public abstract void setActive(boolean active);

    public abstract long getFrameTime();

//----- member functions -----------------------------------------------------------------------------------------------

    /**
     * @brief Query if timing is currently active.
     */
    public boolean isActive() {
        return mActive;
    }


//----- Frame Timing Listener Functions --------------------------------------------------------------------------------


    public Object addListener(Object target, String methodName) {
        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "ADD Listener "+mFrameTime+ " "+target.toString()+" method: "+methodName);
        }
        return addListener(target, methodName, true);
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
    public Object addListener(Object target, String methodName, boolean weakReferenceOnly) {

        PDEFrameTimingListener newListener;

        //security
        if (target == null || mListener == null || TextUtils.isEmpty(methodName)) {
            Log.w(LOG_TAG, "listener array or target is null!");
            //error
            return null;
        }

        try {
            // check if the given method really is declared for target object
            Method method = target.getClass().getMethod(methodName, new Class[] {Long.class});
            //Method method = target.getClass().getMethod(methodName);

            // create listener helper structure & fill in data
            newListener = new PDEFrameTimingListener(target, method, !weakReferenceOnly);

            if (mListener.add(newListener)) {
                if(DEBUGPARAMS){
                    Log.d(LOG_TAG, "ADDED Listener "+mFrameTime+ " "+target.toString()+" method: "+methodName);
                }
                // tell the delegate

                // we now have at least one listener, start timing
                setActive(true);

                // return the internal listener object
                return newListener;

            } else {
                //error
                return null;
            }

        } catch (NoSuchMethodException e) {
            // error handling, if method was not part of target-object
            e.printStackTrace();
            //error
            return null;
        }
    }

    /**
     * @brief Send timings to all listeners,
     */
    public void sendTimings() {
        int i;
        PDEFrameTimingListener listener;

        // init
        mInTiming = true;
        mNeedsCleanup = false;

        try {
            // go through all listeners and send it (use classical loop -> this has the ability to add on the fly)
            for (i=0;i<mListener.size(); i++){
                // which listener
                listener = mListener.get(i);
                // do we still have the target?
                if (!listener.mNeedsRemoval){
                    if (listener.mStrongTarget != null ){
                        sendTiming(listener.mStrongTarget, listener.mMethod);
                    } else {
                        Object target;
                        //weak
                        if (listener.mWeakTarget != null){
                            target = listener.mWeakTarget.get();
                            if (target!= null) {
                                sendTiming(target, listener.mMethod);
                            }
                        } else {
                            // mark ourself for later cleanup
                            mNeedsCleanup = true;
                        }
                    }
                } else {
                    // mark ourself for later cleanup
                    mNeedsCleanup = true;
                }
            }

            // we're no longer in timing
            mInTiming = false;

            // if we need cleanup, do it now
            if (mNeedsCleanup){
                for (i=mListener.size()-1; i>=0; i--){
                    listener = mListener.get(i);
                    if ((listener.mStrongTarget == null && listener.mWeakTarget == null) || listener.mNeedsRemoval){
                        mListener.remove(i);
                    }
                }

                if (mListener.size() == 0) {
                    setActive(false);
                }
            }
            mNeedsCleanup = false;
        } catch (ConcurrentModificationException e) {
            Log.w(LOG_TAG, "List of Listeners changed during iteration!");
        }

    }

    /**
     * @brief Send individual timing to the object.
     *
     * @param target object to call
     * @param method method to invoke
     */
    private void sendTiming(Object target, Method method) {
        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "sendTiming start "+mFrameTime+ " "+target.toString()+" method: "+method.getName());
        }
        try {
            // then send it
            if (method.getGenericParameterTypes().length == 1) {
                method.invoke(target, mFrameTime);
            } else {
                method.invoke(target);
            }


        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();

        }
        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "sendTiming end "+mFrameTime);
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
        int i;
        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "REMOVE Listener "+mFrameTime+ " "+listener.toString());
        }
        try {
            for (i=mListener.size()-1; i>=0; i--){
                PDEFrameTimingListener l;
                // get object
                l = mListener.get(i);

                // does it match?
                if (l.getTarget() == listener) {
                    // if we're currently in timing just mark the listener for removal ( we must not change the list)
                    if (mInTiming){
                       // mark all listener for removal, throw away reference immediately
                        l.mNeedsRemoval = true;
                        l.mStrongTarget = null;
                        l.mWeakTarget.clear();
                        // remember to actually do a cleanup step
                        mNeedsCleanup = true;
                    } else {
                        // directly remove this entry
                        mListener.remove(i);
                        // remember
                        removed = true;
                        if(DEBUGPARAMS){
                            Log.d(LOG_TAG, "REMOVED Listener "+mFrameTime+ " "+listener.toString());
                        }
                        // break?
                    }
                }
            }
        } catch (ConcurrentModificationException e) {
            Log.w(LOG_TAG, "List of Listeners changed during iteration!");
        }

        if (mListener.size() == 0) {
            setActive(false);
        }
        return removed;
    }


//----- Post Execute Functions --------------------------------------------------------------------------------


    /**
     * @brief Add a call to the specified function to the end of the runloop.
     * The function is called once and then forgotten.
     *
     * Function in iOS: addDelayedCall
     *
     * @param target object which shall be called
     * @param methodName function name (no parameters in function signature)
     * @return true if call was posted, false otherwise
     */
    public boolean postExecuteFunction(final Object target, String methodName) {
        return postExecuteFunction(target, methodName, new Class[] {});
    }

    /**
     * @brief Add a call to the specified function to the end of the runloop.
     * The function is called once and then forgotten.
     *
     * Function in iOS: addDelayedCall
     *
     * @param target  object which shall be called
     * @param methodName  function name
     * @param params  signature as class array
     * @return true if call was posted, false otherwise
     */
    public boolean postExecuteFunction(final Object target, String methodName, Class<?>[] params) {
        try {
            //security
            if (target == null || TextUtils.isEmpty(methodName)) {
                return false;
            }

            // check if the given method really is declared for target object
            final Method method = target.getClass().getMethod(methodName, params);

            final PostExecuteFunctionHolder holder = new PostExecuteFunctionHolder(target);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    try {
                        method.invoke(target);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    mRunnableList.remove(holder);
                }
            };

            holder.setRunnable(runnable);



            // post runnable at end of queue
            if (mHandler.post(runnable)) {
                mRunnableList.add(holder);
                return true;
            } else {
                return false;
            }


        } catch (NoSuchMethodException e) {
            // error handling, if method was not part of target-object
            e.printStackTrace();
            //error
            return false;
        }
    }


    /**
     * @brief Remove all posted execute functions for the specified target.
     * @param target Object for which all functions shall be removed.
     */
    @SuppressWarnings("unused")
    public void removeExecuteFunctionForTarget(final Object target) {
        for (Iterator<PostExecuteFunctionHolder> iterator = mRunnableList.iterator(); iterator.hasNext(); ) {
            PostExecuteFunctionHolder element = iterator.next();
            if (element.runnable.get() == null) {
                // object which was referenced only weakly doesn't exist anymore...
                iterator.remove();
            } else if (element.target == target) {
                iterator.remove();
            }
        }
    }


}
