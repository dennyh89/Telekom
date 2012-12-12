/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.activity;


import android.app.Activity;

public class PDEActivity extends Activity {


    @Override
    protected void onResume() {
        super.onResume();
        //TODO add code here

        //(re-)start PDECodeLibrary animation

    }

    @Override
    protected void onPause() {
        //TODO add code here

        //pause PDECodeLibrary animation

        super.onPause();
    }
}
