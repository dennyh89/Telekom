/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

//
//  Event base class for PDE CoreComponents library.
//

package de.telekom.pde.codelibrary.ui.events;


//----------------------------------------------------------------------------------------------------------------------
//  PDEEvent
//----------------------------------------------------------------------------------------------------------------------



/**
 * @brief Event data base class.
 *
 * Events (and Event sources) provide communication between classes, using a weak coupling. Events
 * can be sent to arbitrary methods of the receiving class, and can be filtered that a method only
 * receives events of a single type, or events of a group. An event is a one-to-many form of
 * communication, it has exactly one point of origin (the sender), and can have several (or none)
 * registered handler functions. Handler functions are called in the order they were registered.
 *
 * Instances of the event class get sent to the receiving method every time an event is sent.
 * The basic PDEEvent holds the type and a reference to the sender. It can be used as base for events,
 * but event senders are free to derive their events from this class and pass on additional event
 * specific data. The client should check for the event type (if not sure that the methods only receives
 * events of a known type) and cast the event accordingly. The event's data can be changed by
 * handlers while processing, remaining handlers will then receive the modified data. Never change
 * the event type on the fly, the event will not be filtered and distributed again correctly.
 *
 * Additionally, events provide functionality for returning a result to the sender (if sent synchronously),
 * and for marking an event as processed. If processed, an event is not sent on to any handlers
 * remaining in the queue. If you define events, make sure to include documentation about the
 * actual data class sent with the event, about what results are expected (if any), and if the event
 * should be marked as processed when handler, or should be left for others as information to
 * receive. For rules about how to compose an event type, see the documentation of the type property.
 *
 * The event sender set in the event should always the the instance actually sending the event, not
 * for example some helper class like EventSourceSample. The receiver of an event should be able to
 * easily identify the sender by comparing pointers to known objects with the sender.
 *
 */


public class PDEEvent {

    /**
     * @brief properties.
     */

    /**
     * @brief Event type.
     *
     * Event types should conform to a hierarchical dot-notation: "class.event" to make filtering
     * possible. It's allowed to introduce subcategories like "class.category.event" to filter for
     * groups of events. The only filter allowed is to filter for a common prefix of the type,
     * e.g. "class.category.*" will filter for all events of a defined category.
     *
     * If you define an event, define the event name as global static member string of the class
     * sending the event. Always use this type to check for an event, and use the isType function
     * of the event. Do not use hardcoded event strings in handling code.
     *
     * Never change the event type while processing.
     */
    private String mType;

    /**
     * @brief Event sender.
     *
     * The sender of the event. This should be used to check the event's origin when a handler
     * function might receive events from different instances.
     *
     * When composing and sending an event, set to the actual creator and sender of the event, not to a
     * helper class like PDEEventSource. It should be easy for the handler of the event to determine
     * where the event originated by checking against instances it already knows (instead of comparing
     * against subimplementations etc.)
     */
    private Object mSender;

    /**
     * @brief Event result.
     *
     * Initially empty. Some events are asking for a return value, which is stored here. The type
     * of the expected result value depends on the event type. Use setResultWithInt: and
     * setResultWithDouble: to simplify handling of common results. Use resultAsInt and
     * resultAsDouble to retrieve these values.
     */
    private Object mResult;
    private int mFlags;

    /**
     * @brief PDEEvent flag bitfield constants.
     */
    public static final int FLAG_NONE = 0;
    public static final int FLAG_PROCESSED = 1;
    public static final int FLAG_DISTRIBUTE_TO_ALL = 1 << 1;


    /**
     * @brief Constructor.
     */
    public PDEEvent() {
        // init members
        mType = "";
        mSender = null;
        mResult = null;
        mFlags = FLAG_NONE;
    }

    /**
     * @brief Check if the event type matches.
     *
     * Also checks wildcards - often all events of a group send the same data. Be sure to verify that this
     * is actually the case for all events sent.
     *
     * @param type The type to check against.
     * @return true if the event is of the specified type.
     */
    public boolean isType(String type) {
        // wildcard?
        if(type.charAt(type.length()-1)=='*'){
            // wildcard check
            return getType().startsWith(type.substring(0, type.length()-1));
        } else {
            // direct string comparison
            return mType.equalsIgnoreCase(type);
        }

    }


    /**
     * @brief Set the event type.
     *
     * Set the type of your event. The event handlers filter for the event types they intend to process.
     *
     * @param type The type of the event.
     */
    public void setType(String type) {
        mType = type;
    }

    /**
     * @brief Get the type of the event.
     *
     * @return The event type.
     */
    public String getType() {
        return mType;
    }

    /**
     * @brief Set object that sends the event.
     *
     * The event sender set in the event should always the the instance actually sending the event, not
     * for example some helper class like EventSourceSample. The receiver of an event should be able to
     * easily identify the sender by comparing pointers to known objects with the sender.
     *
     * @param sender The object that sends the event.
     */
    public void setSender(Object sender) {
        mSender = sender;
        //Log.d("setSender: ",(mSender!=null ? mSender.toString() : "null"));
    }

    /**
     * @brief Receive the object the event was originally sent from.
     *
     * @return Source-Object of the event.
     */
    public Object getSender() {
        return mSender;
    }


    /**
     * @brief Set the event as processed.
     *
     * Used by event handling functions to mark the event as processed. A processed event is not distributed
     * any more along the chain of handling functions, unless distribution to all handler was explicitly
     * requested by the sender.
     *
     * An event cannot be set to unprocessed again.
     */
    public void setProcessed() {
        mFlags |= FLAG_PROCESSED;
    }

    /**
     * @brief Check if event is marked as processed.
     *
     * IsProcessed can be checked by the sender to see if the event was marked as processed by any
     * of the handling functions.
     *
     * @return true if the event was processed. FALSE if there were no handlers listening to this event,
     *         or if the event was not marked as processed during sending.
     */
    public boolean isProcessed() {
        //check flags
        return ((mFlags & FLAG_PROCESSED) == FLAG_PROCESSED);
    }

    /**
     * @brief Force distribution of the event to all handlers.
     *
     * Should be set by the sender prior to sending the event. If set, the processed flag is ignored
     * and the event gets sent to all handlers. The default behaviour is not to distribute the event to
     * all.
     *
     * If necessary, this flag can be cleared by handlers during processing.
     */
    @SuppressWarnings("unused")
    public void setDistributeToAll() {
        // add to flags
        mFlags |= FLAG_DISTRIBUTE_TO_ALL;
    }

    /**
     * @brief Clear the distribute to all behaviour.
     *
     * The default setting. The event is only distributed to handlers until the processed flag gets set.
     * This function can be used during event handling to modify the event's behaviour, but be careful
     * when modifying event behaviour. Other event handlers might need to receive the event to
     * function correctly.
     */
    @SuppressWarnings("unused")
    public void clearDistributeToAll() {
        // add to flags
        mFlags &= ~FLAG_DISTRIBUTE_TO_ALL;
    }

    /**
     * @brief Check if event is marked for distribution to all handlers.
     *
     * @return true if distribution to all is turned on.
     */
    public boolean isDistributeToAll() {
        // check flags
        return ((mFlags & FLAG_DISTRIBUTE_TO_ALL) == FLAG_DISTRIBUTE_TO_ALL) ;
    }

    /**
     * @brief Check if there is a result.
     *
     * @return true if a result (other than null) was set.
     */
    @SuppressWarnings("unused")
    public boolean hasResult() {
        return (mResult != null) ;
    }


    /**
     * @brief Set Object instance as result.
     *
     * The intended results are int or double values. Box the value inside of a Integer- or Double-Object and
     * deliver this object as parameter.
     *
     * @param resultObject The value to be set, boxed in an object of appropriate type (Integer/Double).
     */
    public void setResult(Object resultObject) {
        mResult = resultObject;
    }

    /**
     * @brief Set int value as result.
     *
     *
     * Boxes the value into an Integer instance and stores it. Use getIntResult to retrieve
     * the value again.
     *
     * @param value The value to set.
     */
    public void setResult(int value) {
        // convert to Integer-Object
        mResult = value;
    }

    /**
     * @brief Set double value as result.
     *
     * Boxes the value into a Double instance and stores it. Use getDoubleResult to retrieve
     * the value again.
     *
     * @param value The value to set.
     */
    public void setResult(double value) {
        // convert to Double-Object
        mResult = value;
    }


    /**
     * @brief Retrieve the result as an Object.
     *
     * @return The stored result object. The intended results are int or double values. We store them boxed in
     * Integer- or Double-Objects. So the returned Object has to be casted correctly before use. Alternatively use
     * the more specialized methods getIntResult or getDoubleResult.
     */
    public Object getResult() {
        return mResult;
    }


    /**
     * @brief Retrieve the result as an int.
     *
     * @return The stored int result value. If no result is set, or if the result is whether of type Integer nor
     * Double, return -1 as default/error value. If original type is Double it will be converted to int.
     */
    @SuppressWarnings("unused")
    public int getIntResult() {
        // do we have a result?
        if (mResult == null) {
            return -1;
        }

        // check class
        if (mResult instanceof Integer) {
            return ((Integer) mResult).intValue();
        } else if (mResult instanceof Double) {
            return (int) ((Double) mResult).doubleValue();
        } else {
            return -1;
        }
    }


    /**
     * @brief Retrieve the result as a double.
     *
     * @return The stored double result value. If no result is set, or if the result is whether of type Double nor
     * Integer, return 0.0 as default value. If original type is Integer it will be converted to double.
     */
    @SuppressWarnings("unused")
    public double getDoubleResult() {
        // do we have a result?
        if (mResult == null) {
            return 0.0;
        }

        // check class
        if (mResult instanceof Double) {
            return ((Double) mResult).doubleValue();
        } else if (mResult instanceof Integer) {
            return (double) ((Integer) mResult).intValue();
        } else {
            return 0.0;
        }
    }
}
