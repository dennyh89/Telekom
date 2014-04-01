/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.modules.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.components.dialog.PDEDialog;
import de.telekom.pde.codelibrary.ui.components.inputfields.PDEInputField;
import de.telekom.pde.codelibrary.ui.components.inputfields.PDEInputFieldEvent;
import de.telekom.pde.codelibrary.ui.components.notification.PDEInfoFlagView;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.PDEDictionary;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;


//----------------------------------------------------------------------------------------------------------------------
//  PDEOneIDMLoginScreenActivity
//----------------------------------------------------------------------------------------------------------------------



/**
 * @brief OneIDM Login.
 *
 * The activity implements the login pattern mobile for android in order to connect to the OneIDM service. You can start
 * this activity by sending an intent (startActivityForResult). This intent needs to supply some information in the
 * extra bundle. E.g clientId and scope. It handles the login and returns the access token (and refresh token, further
 * information) to the calling app.
 */
public class PDEOneIDMLoginScreenActivity
        extends PDEBaseLoginScreenActivity
        implements DialogInterface.OnCancelListener {

    // private members

    // because scope is unused in the current library version -> suppress warnings 
    @SuppressWarnings("unused")
    private String mScope;
    
    private boolean mPersistentRequest;
    private PDEInfoFlagView mUsernameInfoToolTip;
    private Runnable mUsernameShowInfoRunnable;
    private Handler mHandler;
    private ProgressIndicatorFragment mProgressIndicatorFragment;

    // Intent key definitions
    public final static String ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_CLIENT_ID
            = "PDEOneIDMLoginScreenActivity.Extra.ClientID";
    public final static String ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_SCOPE
            = "PDEOneIDMLoginScreenActivity.Extra.Scope";
    public final static String ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_URL
            = "PDEOneIDMLoginScreenActivity.Extra.URL";
    public final static String ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_SHOW_STAY_SIGNED_IN_CHECKBOX
            = "PDEOneIDMLoginScreenActivity.Extra.ShowSignedInCheckbox";
    public final static String ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_USERNAME
            = "PDEOneIDMLoginScreenActivity.Extra.User.UserName";
    public final static String ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_USER_PASSWORD
            = "PDEOneIDMLoginScreenActivity.Extra.User.Password";
    public final static String ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_SHOW_T_BRAND_LOGO
            = "PDEOneIDMLoginScreenActivity.Extra.ShowTBrandLogo";
    public final static String ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_CONTENTSTYLE
            = "PDEOneIDMLoginScreenActivity.Extra.ContentStyle";
    public final static String ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_CONTENTSTYLE_VALUE_HAPTIC
            = "HAPTIC";
    public final static String ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_CONTENTSTYLE_VALUE_FLAT
            = "FLAT";

    public final static String ONE_IDM_LOGIN_SCREEN_RETURNED_INTENT_EXTRA_RESULT
            = "PDEOneIDMLoginScreenActivity.Return.Extra.Result";

    // live server
    private final static String DEFAULT_AUTH_URL = "https://logint3.idm.toon.sul.t-online.de/oauth2/tokens";
    // test server
    //private final static String DEFAULT_AUTH_URL = "https://accounts.login00.idm.ver.sul.t-online.de/oauth2/tokens";


    // debug
    private final static boolean DEBUG_SHOW_IDM_VALUES = false;
    private final static boolean DEBUG_SHOW_FUNCTION_LOGS = false;

    public void onCreate(Bundle savedInstanceState) {
        Intent startIntent = getIntent();
        if (startIntent.hasExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_CONTENTSTYLE)) {
            if (ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_CONTENTSTYLE_VALUE_HAPTIC
                    .equals(startIntent.getStringExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_CONTENTSTYLE))) {
                mStyle = PDEConstants.PDEContentStyle.PDEContentStyleHaptic;
            }
        }

        super.onCreate(savedInstanceState);

        String url = DEFAULT_AUTH_URL;
        String clientID = "";

        String email = "";
        String password = "";
        mScope = "";
        mPersistentRequest = false;
        mUsernameShowInfoRunnable = null;
        mHandler = new Handler();


        if (startIntent.hasExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_CLIENT_ID)) {
            clientID = startIntent.getStringExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_CLIENT_ID);
        } else {
            Log.d(LOG_TAG, "no client id in the intent");
        }
        if (startIntent.hasExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_SCOPE)) {
            mScope = startIntent.getStringExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_SCOPE);
        } else {
            Log.d(LOG_TAG, "no scope in the intent");
        }
        if (startIntent.hasExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_URL)) {
            url = startIntent.getStringExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_URL);
        } else {
            // no url might be ok, we use the default
        }
        // if we read the service documents correctly there is no client secret ever in the OneIDM case.
//        if (startIntent.hasExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_CLIENT_SECRET)) {
//            clientSecret = startIntent.getStringExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_CLIENT_SECRET);
//        } else {
//            // no secret might be ok
//        }
        if (startIntent.hasExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_USERNAME)) {
            email = startIntent.getStringExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_USERNAME);
        }
        if (startIntent.hasExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_USER_PASSWORD)) {
            password = startIntent.getStringExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_USER_PASSWORD);
        }

        // there is a splash screen in this kind of apps - so no logo needed
        setTBrandLogoVisible(startIntent.getBooleanExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_SHOW_T_BRAND_LOGO, false));
        // show or hide stay signed in checkbox
        setStaySignedInVisible(startIntent
                .getBooleanExtra(ONE_IDM_LOGIN_SCREEN_INTENT_EXTRA_SHOW_STAY_SIGNED_IN_CHECKBOX, true));


        // set the right content
        setDescriptionHTML(getResources().getString(R.string.login_screen_screen_description_idm));
        setRegisterHereHTML(getResources().getString(R.string.login_screen_register_now_link));
        setForgotPasswordHTML(getResources().getString(R.string.login_screen_password_forgotten_link));
        setRegisterAreaVisible(true);

        // get username info tooltip
        mUsernameInfoToolTip = ((PDEInfoFlagView)findViewById(R.id.LoginScreenUsernameInfoToolTip));

        // register listener to username input field in order to show info tooltip
        mUsernameInputField.addListener(this, "onUsernameInputFieldAction");

        // set the optional username and password fields
        if (!TextUtils.isEmpty(email)) {
            setUsername(email);
            if (!TextUtils.isEmpty(password)) {
                setPassword(password);
            }
        }
        updateLoginState();
    }


    /**
     * @brief Login-Button was pressed by the user - fire the login to the server.
     */
    @Override
    public void loginButtonClicked(final String email, final String password, final boolean staySignedIn) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLoginFailNotification("Start validating the credentials with OneIDM!",
                                          "email: %1$s\npassword: %2$s\nstaySignedIn: %3$s",
                                           email,password,(staySignedIn ? "true" : "false"));

//                showLoginFailNotification("Start validating the credentials with OneIDM!","email: " + email
//                        + "\npassword: " + password
//                        + "\nstaySignedIn: " + (staySignedIn ? "true" : "false"));


//                Toast.makeText(PDEOneIDMLoginScreenActivity.this, "Start validating the credentials with OneIDM!"
//                        + "\nemail: " + email
//                        + "\npassword: " + password
//                        + "\nstaySignedIn: " + (staySignedIn ? "true" : "false"),
//                        Toast.LENGTH_SHORT)
//                        .show();
            }
        });

        // since the OneIDMModule is not approved by the Group for Internet Security (GIS) we cannot show it!!
//        String scope = mScope;
//        if (staySignedIn) {
//            scope += " persistent";
//            mPersistentRequest = true;
//        }
//
//        boolean result = false;
//        try {
//            result = mOneIDM.requestAccessToken(email, password, scope);
//            if (!result) {
//                showLoginFailNotification("", getResources().getString(R.string.login_screen_oneidm_activity_token_request_active_msg));
//            }
//        } catch (NetworkErrorException e) {
//            showLoginFailNotification(getResources().getString(R.string.login_screen_oneidm_activity_error_no_network_title), getResources().getString(R.string.login_screen_oneidm_activity_error_no_network_msg));
//        }
//
//        if (result) {
//            // disable user interaction components
//            enableUserInteractionFields(false);
//
//            // show progress dialog
//            showProgressIndicator();
//        }
    }


    /**
     * @brief Callback from the OneIDMModule, evaluates the response.
     */
    @SuppressWarnings("unused")
    public void oneIDMEvent (PDEEvent event) {
        if (DEBUG_SHOW_FUNCTION_LOGS) {
            Log.d(LOG_TAG, "onIDMEvent received");
        }

        // cancel the progress dialog
        hideProgressIndicator();

        if (DEBUG_SHOW_IDM_VALUES) {
            PDEDictionary resultDict = new PDEDictionary();
            if (event.getResult() instanceof PDEDictionary)
            {
                resultDict = (PDEDictionary)event.getResult();
            }

            String resultString = "";
            for(String key : resultDict.keySet()) {
                if (!TextUtils.isEmpty(resultString)) resultString += "\n";
                resultString += key + ": "+resultDict.get(key);
            }
            Log.d(LOG_TAG, "oneIDMEvent received event "+event.getType());
            Log.d(LOG_TAG, "oneIDMEvent received values "+resultString);
        }

        enableUserInteractionFields(true);

    }


    private void enableUserInteractionFields(final Boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (enable) {
                    mPasswordInputField.setEnabled(true);
                    mUsernameInputField.setEnabled(true);
                    mButtonLogin.setEnabled(true);
                    mCheckbox.setEnabled(true);
                    PDEUtils.setViewAlpha(mPasswordInputField, 1.0f);
                    PDEUtils.setViewAlpha(mUsernameInputField, 1.0f);
                    PDEUtils.setViewAlpha(mButtonLogin, 1.0f);
                    PDEUtils.setViewAlpha(mCheckbox, 1.0f);
                } else {
                    mPasswordInputField.setEnabled(false);
                    mUsernameInputField.setEnabled(false);
                    mButtonLogin.setEnabled(false);
                    mCheckbox.setEnabled(false);
                    PDEUtils.setViewAlpha(mPasswordInputField, 0.5f);
                    PDEUtils.setViewAlpha(mUsernameInputField, 0.5f);
                    PDEUtils.setViewAlpha(mButtonLogin, 0.5f);
                    PDEUtils.setViewAlpha(mCheckbox, 0.5f);

                }
            }
        });
    }





    /**
     * @brief Create log output of a failed login, to provide information for the developer.
     *
     * @param event Event which was received from the PDEOneIDMModule
     */
    void logLoginFail(PDEEvent event) {
        Log.d(LOG_TAG, "oneIDMEvent Error received: "+event.getType());

        if (event.getResult() instanceof PDEDictionary)
        {
            PDEDictionary resultDict = (PDEDictionary) event.getResult();
            String resultString = "";
            for(String key : resultDict.keySet()) {
                if (!TextUtils.isEmpty(resultString)) resultString += "\n";
                resultString += key + ": "+resultDict.get(key);
            }
            // this log output is intended and needed by a developer - don't disable it.
            Log.d(LOG_TAG, "oneIDMEvent received values:\n"+resultString);
        }
    }


    /**
     * @brief Handle back button on our own.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    /**
     * @brief Hide tooltips if touch events come through.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mUsernameInfoToolTip.getVisibility() == View.VISIBLE) {
            mUsernameInfoToolTip.hide();
        }
        if (mLoginScreenPositionableInfoFlag.getVisibility() == View.VISIBLE) {
            mLoginScreenPositionableInfoFlag.hide();
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * @brief Helper function to remove the runnable which should have shown the username info tooltip.
     */
    private boolean removeUsernameShowInfoRunnable(){
        boolean found = false;
        if (mUsernameShowInfoRunnable != null) {
            mHandler.removeCallbacks(mUsernameShowInfoRunnable);
            mUsernameShowInfoRunnable = null;
            found = true;
        }
        return found;
    }


    /**
     * @brief Helper function to re-/start the runnable which shows the username info tooltip.
     */
    private void reStartUsernameShowInfoRunnable(){
        removeUsernameShowInfoRunnable();
        mUsernameShowInfoRunnable = new Runnable() {
            @Override
            public void run() {
                mUsernameInfoToolTip.show();
                // if the "Telkom-Dienste" ToolTip is also visible -> hide it
                if (mLoginScreenPositionableInfoFlag.getVisibility() == View.VISIBLE) {
                    mLoginScreenPositionableInfoFlag.hide();
                }
                mUsernameShowInfoRunnable = null;
            }
        };
        mHandler.postDelayed(mUsernameShowInfoRunnable, 3000);
    }


    /**
     * @brief Shows an OKButton - to report login errors.
     */
    protected void showLoginFailNotification(String title, final String message, Object... msgVarargs) {
        if (TextUtils.isEmpty(title)) {
            title = getResources().getString(R.string.login_screen_oneidm_activity_error_login_failed_title);
        }
        PDEDialog.constructDialog(title, message, getResources().getString(R.string.dialog_btn_close)).setStyleCustom(mStyle).
                setMessageFormatParameters(msgVarargs).show(this);
    }


    /**
     * @brief Callback for the Username PDEInputField -> used to control the info tooltip.
     */
    @SuppressWarnings("unused")
    public void onUsernameInputFieldAction(PDEEvent ev) {
        PDEInputFieldEvent inputFieldEvent = (PDEInputFieldEvent)ev;
        if (inputFieldEvent.getType().equals(PDEInputField.PDE_INPUTFIELD_EVENT_ACTION_GOT_FOCUS)) {
            if (mUsernameInputField.getText().length() == 0) {
                //start timer
                reStartUsernameShowInfoRunnable();
            }
        } else if (inputFieldEvent.getType().equals(PDEInputField.PDE_INPUTFIELD_EVENT_ACTION_LOST_FOCUS)) {
            removeUsernameShowInfoRunnable();
        } else if (inputFieldEvent.getType().equals(PDEInputField.PDE_INPUTFIELD_EVENT_ACTION_AFTER_TEXT_CHANGED)) {
            if (inputFieldEvent.getCurrentText().length() == 0) {
                reStartUsernameShowInfoRunnable();
            } else {
                removeUsernameShowInfoRunnable();
                mUsernameInfoToolTip.hide();
            }
        }
    }


    /**
     * @brief show the progress dialog.
     */
    @SuppressWarnings("unused")
    void showProgressIndicator() {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        hideProgressIndicator();
        ft.addToBackStack(null);

        // Create and show the dialog.
        mProgressIndicatorFragment = ProgressIndicatorFragment.newInstance();
        mProgressIndicatorFragment.setOnCancelListener(this);
        mProgressIndicatorFragment.show(ft, "progressIndicator");
    }


    /**
     * @brief Hide the progress dialog.
     */
    void hideProgressIndicator() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("progressIndicator");
        if (prev != null) {
            ft.remove(prev);
        }
        if (mProgressIndicatorFragment != null) {
            mProgressIndicatorFragment.dismiss();
            mProgressIndicatorFragment = null;
        }
    }


    /**
     * @brief onCancelListener of the progress indicator fragment, cancels the OneIDM request.
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        // cancel the currently running OneIDM request (the user has pressed back during the execution)
        // mOneIDM.cancelRequest();
    }
}