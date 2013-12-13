/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.activity;


import android.app.Activity;
import android.os.Bundle;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.inflater.PDEInflaterUtils;

public class PDEActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PDECodeLibrary.getInstance().isAssignmentOfDefaultFontToTextViewsEnabled()) {
            PDEInflaterUtils.setFontFactory(getLayoutInflater());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //add code here

        //(re-)start PDECodeLibrary animation

    }

    @Override
    protected void onPause() {
        //add code here

        //pause PDECodeLibrary animation

        super.onPause();
    }
}
