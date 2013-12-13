/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.boxes;


import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableSunkenArea;

public class PDEDrawableCutoutHaptic extends PDEDrawableSunkenArea {


    public PDEDrawableCutoutHaptic(){
        super();
        setElementInnerShadowOpacity(1.0f);
        init();
    }

    public void init(){
        // configure
        if(PDECodeLibrary.getInstance().isDarkStyle()){
            setElementBackgroundColor(PDEColor.valueOf("Black30Alpha"));
            setElementBorderColor(PDEColor.valueOf("Black70Alpha"));
            setElementInnerShadowColor(PDEColor.valueOf("Black75Alpha"));
        } else {
            setElementBackgroundColor(PDEColor.valueOf("Black7Alpha"));
            setElementBorderColor(PDEColor.valueOf("DTGrey237_Idle_Border"));
            setElementInnerShadowColor(PDEColor.valueOf("Black40Alpha"));
        }

        setElementInnerShadowBlurRadius(PDEBuildingUnits.oneHalfBU());
        setElementShapeRoundedRect(PDEBuildingUnits.oneThirdBU());
    }
}
