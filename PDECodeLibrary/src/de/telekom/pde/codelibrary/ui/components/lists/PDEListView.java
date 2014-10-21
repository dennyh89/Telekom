/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.lists;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;

//----------------------------------------------------------------------------------------------------------------------
// PDEListView
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief List that can deal with styleguide conform list items (those with agent states).
 */
public class PDEListView extends ListView implements PDEIEventSource {
    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEListView.class.getName();

    private final static boolean DEBUG = false;

    /**
     * @brief PDEEventSource instance that provides the event sending behaviour.
     */
    private PDEEventSource mEventSource;

    protected ArrayList<Object> mStrongPDEEventListenerHolder;


//----- init -----------------------------------------------------------------------------------------------------------


    /**
     * @brief constructor
     */
    public PDEListView(Context context) {
        super(context);
        init(context, null);
    }


    /**
     * @brief constructor
     */
    public PDEListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    /**
     * @brief constructor
     */
    public PDEListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    /**
     * @brief Init properties.
     */
    @SuppressWarnings("unused")
    private void init(Context context, android.util.AttributeSet attrs) {
        // init
        // default divider settings
        setDivider(new ColorDrawable(PDEColor.valueOf("DTGrey220").getIntegerColor()));
        setDividerHeight(1);
        // make the native list selector invisible
        setSelector(new ColorDrawable(0));

        // set BackgroundColor
        setBackgroundColor(PDEColor.DTUIBackgroundColor().getIntegerColor());
        // set Cache Color Hint
        setCacheColorHint(PDEColor.DTUIBackgroundColor().getIntegerColor());

        // init event source
        mEventSource = new PDEEventSource();
        // set ourselves as the default sender (optional)
        mEventSource.setEventDefaultSender(this, true);
        mStrongPDEEventListenerHolder = new ArrayList<Object>();

        setAttributes(attrs);
    }


    /**
     * @brief Load XML attributes.
     */
    private void setAttributes(AttributeSet attrs) {
        // valid?
        if (attrs == null) return;

        int resourceId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android",
                                                         "divider", -1);
        if (resourceId > 0 && getResources() != null) {
            final Drawable d = getResources().getDrawable(resourceId);
            if (d != null) {
                // If a divider is specified use its intrinsic height for divider height
                setDivider(d);
            }
        }

        String str = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "dividerHeight");
        if (str != null) {
            setDividerHeight((int) PDEBuildingUnits.parseSize(str));
        } else {
            setDividerHeight(1);
        }
    }


    /**
     * @param event PDEEvent which is sent by a list item.
     * @brief Listener for clicked list items.
     *
     * All PDEListItems which are added by PDEListAdapter add this listener function.
     * So it receives the PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED and
     * PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED events of any clicked PDEListItem. In this way we inform the
     * PDEListView about the click on one of its items. Now the PDEListView can publish the information about the
     * click on one of its items to the world by sending its own events. The user has the choice to listen to this
     * PDE list events by the use of addListener() or he registers the standard android OnItemClickListener.
     * Listening to the PDE events has the advantage that it is possible to distinguish between will_be_selected and
     * selected events.
     */
    @SuppressWarnings("unused")
    public void onPDEListItemClicked(PDEEvent event) {
        // check type of event
        if (TextUtils.equals(event.getType(), PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED)) {
            // debug message
            if (DEBUG) Log.d(LOG_TAG, "WILL BE SELECTED!");
            // determine list position of clicked item
            int listPosition = ((PDEListItem) event.getSender()).getListPosition();
            // check if the standard android onItemClickedListener is registered. In this case perform a click
            // programmatically in order to trigger this listener with the correct list element.
            if (getOnItemClickListener() != null) {
                performItemClick(getAdapter().getView(listPosition, null, this),
                                 listPosition,
                                 getAdapter().getItemId(listPosition));
            }
            // send PDEListItem event
            sendListEvent(PDEListItem.PDE_LIST_ITEM_EVENT_ACTION_WILL_BE_SELECTED, listPosition);
        } else if (TextUtils.equals(event.getType(), PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED)) {
            // debug message
            if (DEBUG) Log.d(LOG_TAG, "SELECTED");
            // determine list position of clicked item
            int listPosition = ((PDEListItem) event.getSender()).getListPosition();
            // send PDEListItem event
            sendListEvent(PDEListItem.PDE_LIST_ITEM_EVENT_ACTION_SELECTED, listPosition);
        }
    }


//----- Event Handling -------------------------------------------------------------------------------------------------


    /**
     * @param type         type of the event
     * @param listPosition position of the list element that triggered the sending of the event.
     * @brief Helper function for sending events.
     */
    protected void sendListEvent(String type, int listPosition) {
        // init list event
        PDEEventListItem pdeListEvent = new PDEEventListItem();
        pdeListEvent.setSender(this);
        pdeListEvent.setType(type);
        pdeListEvent.setListPosition(listPosition);
        // send event
        getEventSource().sendEvent(pdeListEvent);
    }


    /**
     * @return PDEEventSource
     * @brief Get the eventSource which is responsible for sending PDEEvents events.
     * Most of the events are coming form the PDEAgentController.
     */
    @Override
    public PDEEventSource getEventSource() {
        return mEventSource;
    }


    /**
     * @param target     Object which will be called in case of an event.
     * @param methodName Function in the target object which will be called.
     *                   The method must accept one parameter of the type PDEEvent
     * @return Object which can be used to remove this listener
     * @brief Add event Listener.
     *
     * PDEIEventSource Interface implementation.
     * @see de.telekom.pde.codelibrary.ui.events.PDEEventSource#addListener
     */
    @Override
    public Object addListener(Object target, String methodName) {
        mStrongPDEEventListenerHolder.add(target);
        return mEventSource.addListener(target, methodName);
    }


    /**
     * @param target     Object which will be called in case of an event.
     * @param methodName Function in the target object which will be called.
     *                   The method must accept one parameter of the type PDEEvent
     * @param eventMask  PDEAgentController event mask.
     *                   Will be most of the time PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED or
     *                   PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED
     * @return Object which can be used to remove this listener
     * @brief Add event Listener.
     *
     * PDEIEventSource Interface implementation.
     * @see de.telekom.pde.codelibrary.ui.events.PDEEventSource#addListener
     */
    @Override
    public Object addListener(Object target, String methodName, String eventMask) {
        mStrongPDEEventListenerHolder.add(target);
        return mEventSource.addListener(target, methodName, eventMask);
    }


    /**
     * @param listener the event listener that should be removed
     * @return Returns whether we have found & removed the listener or not
     * @brief Remove event listener that was added before.
     */
    @SuppressWarnings("unused")
    public boolean removeListener(Object listener) {
        mStrongPDEEventListenerHolder.remove(listener);
        return mEventSource.removeListener(listener);
    }
}
