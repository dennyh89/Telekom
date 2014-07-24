/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;

import android.view.View;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.PDEDictionary;

//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerInterface
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief Interface for basic button layers
 */
public interface PDEButtonLayerInterface {

    // drawable access
    public View getLayer();

    // agent control
    public void agentEvent(PDEEvent event);

    // common data setting
    //public void setButtonData(PDEButtonData data);
    public void setParameters(PDEParameterDictionary parameters, boolean force);

    // hinting
    public void collectHints(PDEDictionary hints);
    public void setHints(PDEDictionary hints);
    public void collectButtonPaddingRequest(PDEButtonPadding padding);
}
