/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 * 
 * kdanner - 23.07.13
 */

package de.telekom.pde.codelibrary.ui.modules.login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.telekom.pde.codelibrary.ui.R;

/// @cond INTERNAL_CLASS

/**
 * @brief Full screen progress indicator dialog fragment.
 * Simple progress indicator with transparent background and only the rotating indicator visible.
 */
public class ProgressIndicatorFragment extends DialogFragment {

    private DialogInterface.OnCancelListener mCancelListener;

    /**
     * @brief Instance function.
     * @return
     */
    public static ProgressIndicatorFragment newInstance() {
        return new ProgressIndicatorFragment();
    }


    /**
     * @brief Overwriten onCreate function - sets translucent theme.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent);
    }


    /**
     * @brief Inflates layout progressindicatorfragment.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.progressindicatorfragment, container, false);
    }


    /**
     * @brief Notify OnCancelListener when onCancel occurs.
     * @param dialog
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        mCancelListener.onCancel(dialog);
    }


    /**
     * @brief Register OnCancelListener to be notified when the dialog is canceled (by the user).
     * @param listener
     */
    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        mCancelListener = listener;
    }

}

/// @endcond INTERNAL_CLASS