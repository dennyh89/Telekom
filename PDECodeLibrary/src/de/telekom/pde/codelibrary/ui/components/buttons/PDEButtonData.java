/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;

import android.graphics.Bitmap;
import de.telekom.pde.codelibrary.ui.color.PDEButtonColorDefinition;

/**
 * @brief Common state stuff for buttons.
 *
 * Holds some common stuff. This is mostly convenience for standard buttons. Button drawables don't need to
 * react to these and can completely build on their own functionality. However, if these parameters fit,
 * it's more easy for the user to actually use the button.
 */
public class PDEButtonData {
    protected String mTitle;
    protected Bitmap mIcon;
    protected PDEButtonColorDefinition mColorDefinition;

    @SuppressWarnings("unused")
    public PDEButtonData(){
        mTitle = "";
        mIcon = null;
        mColorDefinition = null;
    }

    @SuppressWarnings("unused")
    public PDEButtonData(PDEButtonData data){
        this.mTitle = data.mTitle;
        this.mIcon = data.mIcon;
        this.mColorDefinition = data.mColorDefinition;
    }

    public void setTitle(String title){
        mTitle = title;
    }

    public String getTitle(){
        return mTitle;
    }

    public void setIcon(Bitmap icon){
        mIcon = icon;
    }

    public Bitmap getIcon(){
        return mIcon;
    }

    @SuppressWarnings("unused")
    public void setColorDefinition(PDEButtonColorDefinition definition){
        mColorDefinition = definition;
    }

    @SuppressWarnings("unused")
    public PDEButtonColorDefinition getColorDefinition(){
        return mColorDefinition;
    }


}
