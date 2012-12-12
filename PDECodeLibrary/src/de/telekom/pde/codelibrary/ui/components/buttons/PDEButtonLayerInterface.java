/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;

import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonLayoutHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEDictionary;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

/**
 * @brief Interface for basic button layers
 */
public interface PDEButtonLayerInterface {


    // drawable access
    public PDEAbsoluteLayout getLayer();

    // agent control
    public void agentEvent(PDEEvent event);

    // common data setting
    //public void setButtonData(PDEButtonData data);
    public void setParameters(PDEParameterDictionary parameters, boolean force);
    public void setLayout(PDEButtonLayoutHelper layout);

    // hinting
    public void collectHints (PDEDictionary hints);
    public void setHints (PDEDictionary hints);
    public void collectButtonPaddingRequest(PDEButtonPadding padding);
}
