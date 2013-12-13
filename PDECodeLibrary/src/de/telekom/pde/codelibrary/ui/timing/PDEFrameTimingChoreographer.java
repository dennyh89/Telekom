/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.timing;



import android.annotation.SuppressLint;
import android.util.Log;
import android.view.Choreographer;

//----------------------------------------------------------------------------------------------------------------------
//  PDEFrameTimingChoreographer
//----------------------------------------------------------------------------------------------------------------------

@SuppressLint("NewApi")
@SuppressWarnings("unused")
public class PDEFrameTimingChoreographer extends PDEFrameTiming implements Choreographer.FrameCallback{

    /**
     * @brief Global tag for log outputs.
     */
    protected final static String LOG_TAG = PDEFrameTimingChoreographer.class.getName();
    private final static boolean DEBUG_PARAMS = false;


    protected boolean mFrameCallbackSet = false;

    /**
     * @brief Constructor - protected since it is instantiated as singleton via PDEFrameTiming
     */
    protected PDEFrameTimingChoreographer() {
        super();

        //init member variables
        mFrameCallbackSet = false;
    }


    /**
     * @brief Active or deactivate the frame timing.
     * Take care to deactivate frame timing again when it isn't needed anymore.
     *
     * @param active true start frame timing, false deactivate frame timing
     */
    @Override
    public void setActive(boolean active) {

        if (active == mActive)
            return;
        if (DEBUG_PARAMS){
            Log.d(LOG_TAG, "setActive "+(active?"active":"inactive"));
        }

        mActive = active;

        if (mActive && !mFrameCallbackSet) {
            // ensure that the FrameCallback is executed in the mainloop

            mHandler.postAtFrontOfQueue(new Runnable() {
                @Override
                public void run() {
                    postFrameCallback();
                }
            });

        }
    }


    /**
     * @brief get current frame time
     * @return current frame time in milliseconds
     */
    @Override
    public long getFrameTime() {
        // kd: if we do this then we get an endless loop since after postFrameCallback, sendTiming is called which triggers
        // getFrameTime again (in PDEAnimationGroup)
//        if (!isActive()) {
//            postFrameCallback();
//
//        }

        if (!isActive()) {
            //save in milliseconds
            mFrameTime = System.nanoTime() / 1000000;
        }

        return mFrameTime;
    }


    /**
     * Function will be called by Android Choreographer on each frame
     * @param frameTimeNanos current Frame Time in nano seconds
     */
    @Override
    public void doFrame(long frameTimeNanos) {
        if (DEBUG_PARAMS){
            Log.d(LOG_TAG, "doFrame "+frameTimeNanos);
        }
        //frame callback is removed automatically
        mFrameCallbackSet = false;
        //save in milliseconds
        mFrameTime = frameTimeNanos / 1000000;
        if (isActive()) {
            postFrameCallback();
        } else {
            if(DEBUG_PARAMS){
                Log.d(LOG_TAG, "notActive anymore");
            }
        }
        sendTimings();
    }


    /**
     * @brief set callback for choreographer which will be called at next frame time.
     * Also remember that callback is set.
     * Note: the callback is only executed once and then forgotten, thus it needs to be setted again each time it is
     * called
     */
    private void postFrameCallback() {
        if (!mFrameCallbackSet) {
            mFrameCallbackSet = true;
            Choreographer.getInstance().postFrameCallback(this);
        }
    }


    /**
     * @brief remove callback from choreographer.
     * This function is normally not needed since the callback is only executed once and then forgotten.
     */
    private void removeFrameCallback() {
        mFrameCallbackSet = false;
        Choreographer.getInstance().removeFrameCallback(this);
    }
}
