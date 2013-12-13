
/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.sliders;


import android.graphics.Rect;
import android.view.View;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;


/**
 * @brief Interface for basic slider contents
 */
public interface PDESliderContentInterface {

    // ----- constants -----

    /**
     * @brief Slider Content Orientation
     */
    public enum PDESliderContentOrientation {
        PDESliderContentOrientationHorizontal,
        PDESliderContentOrientationVertical
    }

    // drawable access
    public View getLayer();

    // slider control
    public void sliderEvent(PDEEvent event);

    // padding
    public Rect getSliderContentPadding();

    /**
     * @brief Get handle frame.
     * Relative to the PDESlider.
     * @return the frame or ZeroRect if the content has no handle.
     */
    public Rect getHandleFrame();


    /**
     * @brief Get content frame.
     * Relative to the PDESlider
     */
    public Rect getContentFrame();
}

