/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.events;

//----------------------------------------------------------------------------------------------------------------------
//  PDEIEventSourceDelegate
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief Delegate interface for PDEEventSource.
 *
 * Implement this protocol in your classes and set the delegate property of PDEEventSource if it's necessary that
 * newly added event listeners are sent a hello or initialization message.
 */

public interface PDEIEventSourceDelegate {
    /**
     * @brief Delegate function called when a new listener was added to the event source.
     *
     * Can be used to send initialization events to the new listener.
     *
     * @param listener The internal listener reference which can be used to directly send events.
     */
    public abstract void eventSourceDidAddListener(Object listener);

    // @new
    public abstract void eventSourceWillRemoveListener(Object listener);

}
