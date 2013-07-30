/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.sliders;


//----------------------------------------------------------------------------------------------------------------------
//  PDESliderController
//----------------------------------------------------------------------------------------------------------------------

/// @cond CLASS_UNDER_DEVELOPMENT__NOT_RELEASED

/**
 * @brief   This is used to control Sliders.
 *
 *          Every change of it's properties sends a Event with id -1 and all stored values to listening Sliders.
 *          The Slider will decide, what he wants to do with the sended information.
 *
 *          Also a user has the possibility to define his own range of values, he wants to set to a controller.
 *          For default the range is 0...1.
 *          The controller will transform given values into 0..1 range according to the User's defined range.
 *
 *          !!! If you are working with your own defined range,
 *          values that are matching your max value will be converted to 1!
 *          This causes, that you will have to set the position of your scrollbar handle to your max value,
 *          if you want it to be at the end of the range.
 *
 **/
public class PDESliderController {


    /**
     * @brief Event mask for all actions.
     */
    public static final String PDE_SLIDER_CONTROLLER_EVENT_MASK_ACTION = "PDESliderController.action.*";

}

/// @endcond CLASS_UNDER_DEVELOPMENT__NOT_RELEASED