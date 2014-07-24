/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.timing;

//----------------------------------------------------------------------------------------------------------------------
//  PDEFrameTimingOrdinary
//----------------------------------------------------------------------------------------------------------------------


public class PDEFrameTimingOrdinary extends PDEFrameTiming {

    protected boolean mPostiesRunning = false;


    protected PDEFrameTimingOrdinary() {
        super();
        mPostiesRunning = false;
    }


    public synchronized void setActive(boolean active) {
        if (active == mActive)
            return;

        mActive = active;

        if (mActive && !mPostiesRunning) {
            updateFrameTime();

            newPosty(0);

        }
    }


    public long getFrameTime() {
        if (!isActive() && !mPostiesRunning) {
            //update the frametime to current time
            updateFrameTime();

            //start posty once (didn't set it active)
            newPosty(0);
        }

        return mFrameTime;

    }


    private void newPosty(long delay) {
        mPostiesRunning = true;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isActive()) {

                    sendTimings();

                    updateFrameTime();

                    if (mFrameTime - mLastFrameTime > FRAME_TIME_DELTA_THRESHOLD) {
                        newPosty(0);
                    } else {
                        newPosty(FRAME_IDLE_DELAY);
                    }
                } else {
                    mPostiesRunning = false;
                }
            }
        };

        if (delay == 0) {
            mHandler.post(runnable);
        } else {
            mHandler.postDelayed(runnable, delay);
        }
    }


    private void updateFrameTime() {
        mLastFrameTime = mFrameTime;
        mFrameTime = System.currentTimeMillis();

    }


}
