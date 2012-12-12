/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.events;

//----------------------------------------------------------------------------------------------------------------------
//  PDEIEventSource
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief Interface for Classes that use PDEEventSource.
 *
 * All Classes that want to send events have to implement the PDEIEventSource interface. Since Java doesn't support
 * multiple inheritance this interface is designed to deliver an instance of PDEEventSource,
 * which provides the needed event sending logic. So every class that implements PDEIEventSource has to create and hold
 * an instance of PDEEventSource which is delivered by the only method getEventSource()
 */

public interface PDEIEventSource {

    /**
     * @brief Delivers instance of PDEEventSource.
     *
     * PDEEventSource provides the needed behaviour for event sending. Classes that need this behaviour should
     * implement PDEIEventSource and create & store a member of type PDEEventSource which is delivered by this method.
     */
    public abstract PDEEventSource getEventSource();

    public abstract Object addListener(Object target, String methodName);

    public abstract Object addListener(Object target, String methodName, String eventMask);



}
