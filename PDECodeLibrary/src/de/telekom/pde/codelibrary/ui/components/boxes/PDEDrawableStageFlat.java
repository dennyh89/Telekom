/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.boxes;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableRoundedBox;


/**
 * @brief Drawable for a stage area in flat style.
 */
public class PDEDrawableStageFlat extends PDEDrawableRoundedBox {


    public PDEDrawableStageFlat() {
        super();
        init();
    }


    protected void init() {
        if (PDECodeLibrary.getInstance().isDarkStyle()) {
            setElementBackgroundColor(PDEColor.valueOf("DTGrey100"));
            setElementBorderColor(PDEColor.valueOf("Black70Alpha"));
        } else {
            setElementBackgroundColor(PDEColor.valueOf("DTGrey4"));
            setElementBorderColor(PDEColor.valueOf("DTGrey237_Idle_Border"));
        }
    }

}
