

/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.sliders;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @brief  Helper Class to associate a Controller to a specific ID.
 */
public class PDESliderControllerAssociator implements Parcelable {

    // data
    public int sliderControllerId;
    public PDESliderController sliderController;

    public PDESliderControllerAssociator() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(sliderControllerId);
        out.writeSerializable(sliderController);
    }

    private PDESliderControllerAssociator(Parcel in) {
        sliderControllerId = in.readInt();
        sliderController = (PDESliderController) in.readSerializable();
    }

    public static final Parcelable.Creator<PDESliderControllerAssociator> CREATOR
            = new Parcelable.Creator<PDESliderControllerAssociator>() {
        public PDESliderControllerAssociator createFromParcel(Parcel in) {
            return new PDESliderControllerAssociator(in);
        }

        public PDESliderControllerAssociator[] newArray(int size) {
            return new PDESliderControllerAssociator[size];
        }
    };
}
