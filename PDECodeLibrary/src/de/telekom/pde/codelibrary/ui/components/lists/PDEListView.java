/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.lists;

import de.telekom.pde.codelibrary.ui.color.PDEColor;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

// ToDo: PDEEvents

//----------------------------------------------------------------------------------------------------------------------
// PDEListView
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief List that can deal with styleguide conform list items (those with agent states).
 *
 */
public class PDEListView extends ListView {
    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEListView.class.getName();

    private final static boolean DEBUG = false;

//-----  properties ---------------------------------------------------------------------------------------------------
    // list position of the item that received the touch-down-event
    protected int mTapDownPosition;
    // flag that indicates if the list is currently scrolling or not.
    protected boolean mIsScrolling;
    protected boolean mTapped;


//----- init -----------------------------------------------------------------------------------------------------------

    /**
     * @brief constructor
     */
    public PDEListView(Context context){
        super(context);
        init();
    }


    /**
     * @brief constructor
     */
    public PDEListView(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }


    /**
     * @brief constructor
     */
    public PDEListView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        init();
    }


    /**
     * @brief Init properties.
     */
    public void init(){
        // init
        mTapDownPosition = 0;
        mIsScrolling = false;
        mTapped = false;

        // make the native list selector invisible
        setSelector(new ColorDrawable(0));

        // set BackgroundColor
        setBackgroundColor(PDEColor.DTUIBackgroundColor().getIntegerColor());
        // set Cache Color Hint
        setCacheColorHint(PDEColor.DTUIBackgroundColor().getIntegerColor());

        // callback for scrolling in order to determine current scrolling state of the list
        this.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
               if (scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                   mIsScrolling = false;
                   if (DEBUG) Log.d(LOG_TAG,"Scrolling IDLE!");
               } else if(scrollState == OnScrollListener.SCROLL_STATE_FLING) {
                   if (DEBUG) Log.d(LOG_TAG,"Scrolling FLING!");
               } else if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                   mIsScrolling = true;
                   if (DEBUG) Log.d(LOG_TAG,"Scrolling TOUCH_SCROLL!");
               }
            }

            // empty implementation
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }


    /**
     * @brief Intercept touch events on the way down to the actual list items.
     *
     * We want to have list elements with their own agentstate-selection-behaviour when they get touched. Not the
     * native behaviour of android list elements. In order to do this, our list-elements have to be clickable and
     * have to process the touch elements themselves. This means when they consume the motion events,
     * the list itself doesn't get them anymore and therefore its OnItemClickListener won't fire anymore. So users of
     * the list wouldn't be informed anymore which item of the list was clicked.
     * In order to avoid this we intercept the motion events on their way down to the list items. When we get an
     * UP-Event we have to put it in the onTouchEvent-Listener manually to be sure,
     * that the list itself is informed about the click.
     *
     * @param ev The motion/touch event that occured on the list / list item.
     *
     * @return  If the list view handles the event itself true is returned. When we return true the list item doesn't
     * receive the original motion event anymore. Instead it receives a cancel-event. When we return false,
     * the original motion event can pass to the list item.
     */
    public boolean onInterceptTouchEvent (MotionEvent ev){
        // inform list about the up-event
        if (ev.getAction() == MotionEvent.ACTION_UP)onTouchEvent(ev);
        // continue normal processing
        return super.onInterceptTouchEvent(ev);
    }


    /**
     * @brief Intercept events before they get dispatched.
     *
     * In order to give list elements our own behaviour we have to manipulate events that are send to the list (see
     * description of onInterceptTouchEvent for more explanation).
     * Formerly we had an approach that worked only with the onInterceptTouchEvent-Handler to intercept and
     * manipulate events if necessary. This approach worked completly fine with Android 4.0+ devices,
     * but didn't really work with lower devices. On lower devices when the finger moves out of the tapped element,
     * onInterceptTouchEvent stops receiving further events and so we can't manipulate them any more. So now we
     * capture them at an earlier point and manipulate where necessary.
     *
     * @param ev The motion/touch event that occured on the list / list item.
     * @return
     */
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int currentXPosition = Math.round(ev.getX());
        int currentYPosition = Math.round(ev.getY());

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (DEBUG) Log.d(LOG_TAG,"List:DISPATCH: ActionDown");
                // remember that we're touched and at which list position.
                mTapDownPosition = pointToPosition(currentXPosition, currentYPosition);
                if (DEBUG) Log.d(LOG_TAG,"List:DISPATCH: Set scrolling false manually (HACK)!");
                mIsScrolling = false; // hack for Devices below Android 4.0
                mTapped = true;
                break;
            case MotionEvent.ACTION_CANCEL:
                if (DEBUG) Log.d(LOG_TAG,"List:DISPATCH: ActionCancel");
                // not touched any more
                mTapped=false;
                break;
            case MotionEvent.ACTION_UP:
                if (DEBUG) Log.d(LOG_TAG,"List:DISPATCH: ActionUp");
                // not touched any more
                mTapped=false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (DEBUG) Log.d(LOG_TAG,"List:DISPATCH: ActionMove");
                if (mTapped){
                    if (pointToPosition(currentXPosition, currentYPosition) != mTapDownPosition || mIsScrolling) {
                        if (DEBUG) Log.d(LOG_TAG,"List:DISPATCH: Cancel manually");
                        // get the element where the touch started
                        View tappedElement;
                        tappedElement = getChildAt(mTapDownPosition);
                        // call cancel manually (not by event) -> workaround
                        if(tappedElement!=null && tappedElement instanceof PDEListItem) {
                            if(tappedElement!=null) ((PDEListItem)tappedElement).getAgentAdapter().doCancel(tappedElement,ev);
                        }
                        // not touched any more
                        mTapped=false;
                    }
                }
                break;
        }
        // continue normal processing
        return super.dispatchTouchEvent(ev);
    }

}
