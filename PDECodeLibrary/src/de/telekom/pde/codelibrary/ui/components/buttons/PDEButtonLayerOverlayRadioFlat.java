/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;




//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerOverlayRadioFlat
//----------------------------------------------------------------------------------------------------------------------


import android.content.Context;
import android.util.AttributeSet;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableArea;



/**
 * @brief Background for a flat radio button.
 */

class PDEButtonLayerOverlayRadioFlat extends PDEButtonLayerOverlayRadioBase {


    /**
     * @brief Global tag for log outputs.
     */
    @SuppressWarnings("unused")
    private final static String LOG_TAG = PDEButtonLayerOverlayRadioHaptic.class.getName();


    /**
     * @brief Class initialization.
     */
    @SuppressWarnings("unused")
    public PDEButtonLayerOverlayRadioFlat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * @brief Class initialization.
     */
    @SuppressWarnings("unused")
    public PDEButtonLayerOverlayRadioFlat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * @brief Class initialization.
     */
    public PDEButtonLayerOverlayRadioFlat(Context context) {
        super (context);
    }


    /**
     * @brief Factory function that decides which background we use.
     *
     */
    protected PDEDrawableArea createDrawableArea(){
        PDEDrawableArea area;

        area = new PDEDrawableArea();
        return area;
    }

}
