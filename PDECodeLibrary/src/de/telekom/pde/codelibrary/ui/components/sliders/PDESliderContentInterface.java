
/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.sliders;


import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

/// @cond CLASS_UNDER_DEVELOPMENT__NOT_RELEASED

/**
 * @brief Interface for basic slider contents
 */
public interface PDESliderContentInterface {


    // drawable access
    public PDEAbsoluteLayout getLayer();

    // agent control
    public void agentEvent(PDEEvent event);
}

/// @endcond CLASS_UNDER_DEVELOPMENT__NOT_RELEASED