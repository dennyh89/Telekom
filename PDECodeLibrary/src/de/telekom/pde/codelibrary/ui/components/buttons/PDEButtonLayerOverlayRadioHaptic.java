/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;




//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerOverlayRadioHaptic
//----------------------------------------------------------------------------------------------------------------------


import android.content.Context;
import android.util.AttributeSet;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableArea;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableSunkenArea;


/**
 * @brief Background for a haptic radio button.
 */
class PDEButtonLayerOverlayRadioHaptic extends PDEButtonLayerOverlayRadioBase {


    /**
     * @brief Global tag for log outputs.
     */
    @SuppressWarnings("unused")
    private final static String LOG_TAG = PDEButtonLayerOverlayRadioHaptic.class.getName();


    /**
     * @brief Class initialization.
     */
    @SuppressWarnings("unused")
    public PDEButtonLayerOverlayRadioHaptic(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * @brief Class initialization.
     */
    @SuppressWarnings("unused")
    public PDEButtonLayerOverlayRadioHaptic(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * @brief Class initialization.
     */
    public PDEButtonLayerOverlayRadioHaptic(Context context) {
        super (context);
    }


    /**
     * @brief Factory function that decides which background we use.
     *
     */
    protected PDEDrawableArea createDrawableArea(){
        PDEDrawableSunkenArea area;

        area = new PDEDrawableSunkenArea();

        area.setElementInnerShadowOpacity(1.0f);
        if (mDarkStyle) {
            area.setElementInnerShadowColor(PDEColor.valueOf("Black75Alpha"));
        } else {
            area.setElementInnerShadowColor(PDEColor.valueOf("Black40Alpha"));
        }

        return area;
    }

}
