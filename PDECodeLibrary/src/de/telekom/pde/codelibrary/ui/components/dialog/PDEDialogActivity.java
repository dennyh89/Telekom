/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.dialog;


import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.activity.PDEActivity;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDETextView;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableRoundedBox;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableDelimiter;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.PDEString;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;
import de.telekom.pde.codelibrary.ui.layout.PDEBoundedRelativeLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;


//----------------------------------------------------------------------------------------------------------------------
// PDEDialogActivity
//----------------------------------------------------------------------------------------------------------------------



/**
 * @brief Activity that shows a PDE-Style-Dialog above the current Activity.
 *
 */
public class PDEDialogActivity extends PDEActivity {

    // ID for log messages
    private final static String LOG_TAG = PDEDialogActivity.class.getName();

    // debug switch
    private final static boolean DEBUG_OUTPUT = false;

    // predefined names for data that is delivered by intent
    public final static String PDE_DIALOG_INTENT_EXTRA_RESULT = "PDE.Dialog.Extra.Result";
    public final static String PDE_DIALOG_INTENT_EXTRA_CONFIGURATION = "PDE.Dialog.Extra.Configuration";


    // postfixes for broadcast messages
    public final static String PDE_DIALOG_BROADCAST_POSTFIX_RESULT = "_RESULT";
    public final static String PDE_DIALOG_BROADCAST_POSTFIX_DISMISS = "_DISMISS";
    public final static String PDE_DIALOG_BROADCAST_POSTFIX_RUNNING = "_RUNNING";



    // the default typeface
    protected final static PDETypeface DEFAULT_TYPEFACE = PDETypeface.sDefaultFont;

    // maximum dialog width
    protected final static float CONFIG_MAXIMUM_WIDTH_IN_BU = 26.0f;

    // dialog text views
    protected PDETextView mTitleTextView;
    protected PDETextView mMessageTextView;

    // pde gui elements
    protected PDEDrawableRoundedBox mRoundedBox;
    protected PDEButton mButton1;
    protected PDEButton mButton2;

    // one or more buttons?
    protected boolean mMultipleButtons;
    protected boolean mTitleOnly;

    // height of button
    protected int mButtonHeight;

    // layout helper
    protected LinearLayout mButtonContainer;
    protected int mButtonSpacerWidthInBU;

    // needed to receive the dismiss command from the associated constructDialog object
    protected BroadcastReceiver mDismissReceiver;
    // dialog configuration data
    protected PDEDialogConfig mDialogConfig;


    /**
     * @brief Sets the content layout.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // default init
        mButtonHeight = PDEBuildingUnits.pixelFromBU(3.0f);
        mButtonSpacerWidthInBU = PDEBuildingUnits.pixelFromBU(0.5f);
        mMultipleButtons = false;
        mTitleOnly = false;

        // init from intent's extras
        initExtras();

        // init receiver for dismiss commands
        initDismissReceiver();

        // load the layout
        if (mTitleOnly){
            setContentView(R.layout.pde_dialog_title_only);
        } else {
            setContentView(R.layout.pde_dialog_full);
        }

        // set general configuration of the layout - which can't be set in the XML
        initDialogBackground();
        initDialogForeground();
    }


    /**
     * @brief Initializes the broadcast receiver for dismiss commands
     *
     */
    protected void initDismissReceiver() {
       // create dismiss receiver
        createDismissBroadcastReceiver();
        // register the dismiss receiver
        registerDismissBroadcastReceiver();
    }


    /**
     * @brief Initialize Dialog with the data delivered by the intent.
     *
     */
    protected void initExtras(){
        // get the intent
        Intent intent = this.getIntent();

        if (intent != null) {
            // get values from intent
            if (intent.hasExtra(PDE_DIALOG_INTENT_EXTRA_CONFIGURATION)) {
                mDialogConfig = intent.getParcelableExtra(PDE_DIALOG_INTENT_EXTRA_CONFIGURATION);

                if (!PDEString.isEmpty(mDialogConfig.getButton2Text())) mMultipleButtons = true;
                if (PDEString.isEmpty(mDialogConfig.getMessage())) mTitleOnly = true;
            }
        }
    }


    /**
     * @brief Initialize graphical background elements of the dialog.
     *
     */
    protected void initDialogBackground() {
        // the rounded box that works as background plate
        mRoundedBox = new PDEDrawableRoundedBox();
        if (mDialogConfig.getDialogBackgroundColor() != null){
            mRoundedBox.setElementBackgroundColor(PDEColor.valueOf(mDialogConfig.getDialogBackgroundColor()));
        }
        if (mDialogConfig.getDialogOutlineColor() != null){
            mRoundedBox.setElementBorderColor(PDEColor.valueOf(mDialogConfig.getDialogOutlineColor()));
        }

        // Set the rounded box as background drawable
        PDEUtils.setViewBackgroundDrawable(findViewById(R.id.pde_dialog_plane), mRoundedBox);
        // if there's a title and a message we need to show a separator
        if (!mTitleOnly) {
            PDEDrawableDelimiter delimiter = new PDEDrawableDelimiter();
            if (mDialogConfig.getSeparatorOutlineColor() != null){
                delimiter.setElementBackgroundColor(PDEColor.valueOf(mDialogConfig.getSeparatorOutlineColor()));
            }
            PDEUtils.setViewBackgroundDrawable(findViewById(R.id.pde_dialog_title_delimiter), delimiter);
        }

        // set all sizes an margins
        PDEBoundedRelativeLayout dialogPane = (PDEBoundedRelativeLayout)findViewById(R.id.pde_dialog_plane);
        dialogPane.setMaxWidth(PDEBuildingUnits.pixelFromBU(CONFIG_MAXIMUM_WIDTH_IN_BU));
        PDEBoundedRelativeLayout.LayoutParams lp = (PDEBoundedRelativeLayout.LayoutParams)dialogPane.getLayoutParams();
        // set margins
        lp.leftMargin = PDEBuildingUnits.pixelFromBU(2.0f);
        lp.rightMargin = PDEBuildingUnits.pixelFromBU(2.0f);
        dialogPane.setLayoutParams(lp);
    }


    /**
     * @brief Initialize texts and buttons.
     *
     */
    protected void initDialogForeground(){
        // set title attributes
        mTitleTextView = (PDETextView)findViewById(R.id.pde_dialog_title);
        mTitleTextView.setTypeface(DEFAULT_TYPEFACE);
        if (mDialogConfig.getTitleFontSize() >= 0) {
            mTitleTextView.setTextSize(mDialogConfig.getTitleFontSize());
        } else {
            mTitleTextView.setTextSize(PDEDialog.FONTSIZE_LARGE);
        }
        if (mDialogConfig.getTitleTextColor() != null) {
            mTitleTextView.setTextColor(mDialogConfig.getTitleTextColor());
        } else {
            mTitleTextView.setTextColor(PDEDialog.PDE_DIALOG_TITLE_TEXT_DEFAULT_COLOR);
        }

        if (!mTitleOnly){
            // set message attributes
            mMessageTextView = (PDETextView)findViewById(R.id.pde_dialog_message);
            mMessageTextView.setTypeface(DEFAULT_TYPEFACE);
            mMessageTextView.setTextSize(mDialogConfig.getMessageFontSize());
            if (mDialogConfig.getMessageFontSize() >= 0) {
                mMessageTextView.setTextSize(mDialogConfig.getMessageFontSize());
            } else {
                mMessageTextView.setTextSize(PDEDialog.FONTSIZE_DEFAULT);
            }
            if (mDialogConfig.getMessageTextColor() != null){
                mMessageTextView.setTextColor(mDialogConfig.getMessageTextColor());
            } else {
                mMessageTextView.setTextColor(PDEDialog.PDE_DIALOG_MESSAGE_TEXT_DEFAULT_COLOR);
            }
        }

        // get the button container view
        mButtonContainer = (LinearLayout) findViewById(R.id.pde_dialog_button_container);
        // create the buttons
        mButton1 = createButton(1);
        mButton2 = createButton(2);
        // set new visual style if needed
        if (mDialogConfig.getStyle() == PDEConstants.PDEContentStyle.PDEContentStyleHaptic) {
            mButton1.setButtonBackgroundLayerWithLayerType(PDEButton.PDEButtonLayerType.BackgroundHaptic);
            if (mButton2 != null) {
                mButton2.setButtonBackgroundLayerWithLayerType(PDEButton.PDEButtonLayerType.BackgroundHaptic);
            }
        }
        // add listener(s) to the button(s)
        mButton1.addListener(this, "onClickButton1", PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED);
        if (mButton2 != null) {
            mButton2.addListener(this, "onClickButton2", PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED);
        }

        // Show title and / or message text
        mTitleTextView.setText(mDialogConfig.getTitle());
        if (!mTitleOnly) {
            mMessageTextView.setText(mDialogConfig.getMessage());
        }

        // if we have multiple buttons, reduce horizontal text padding to 1 BU
        if (mMultipleButtons && mButton2 != null) {
            mButton1.setHorizontalPadding(PDEBuildingUnits.pixelFromBU(1));
            mButton2.setHorizontalPadding(PDEBuildingUnits.pixelFromBU(1));
        }

        // add button1
        if (mButton1 != null) {
            mButtonContainer.addView(mButton1);
        }
        // add button2
        if (mMultipleButtons && mButton2 != null) {
            // when we have multiple buttons, we need a spacer between them
            mButtonContainer.addView(getButtonSpacer());
            mButtonContainer.addView(mButton2);
        }
    }


    /**
     * @brief Tell our associated constructDialog object that we're running.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // tell the constructDialog object, that this activity is running now and ready to receive broadcast messages.
        sendDialogRunningBroadcast();
    }


    /**
     * @brief Listener callback function for the first button.
     */
    @SuppressWarnings("unused")
    public void onClickButton1(PDEEvent event) {
        // Report that button 1 was pressed.
        sendDialogResultBroadcast(PDEDialog.PDE_DIALOG_RESULT_BUTTON1);
    }


    /**
     * @brief Listener callback function for the second button.
     */
    @SuppressWarnings("unused")
    public void onClickButton2(PDEEvent event) {
        // Report that button 2 was pressed.
        sendDialogResultBroadcast(PDEDialog.PDE_DIALOG_RESULT_BUTTON2);
    }


    /**
     * @brief Creates button how it was specified by delivered data.
     */
    protected PDEButton createButton(int number) {
        PDEButton btn;

        // security
        if (number > 1 && !mMultipleButtons) return null;

        // create new button
        btn = new PDEButton(this);

        // default button
        LinearLayout.LayoutParams params;
        // adapt layout to the numbers of buttons
        if (mMultipleButtons) {
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,mButtonHeight);
            params.weight = 1;
        } else {
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,mButtonHeight);
        }
        params.gravity = Gravity.CENTER;
        btn.setLayoutParams(params);

        // set text label of button
        if (number == 1 && !PDEString.isEmpty(mDialogConfig.getButton1Text())) {
            btn.setText(mDialogConfig.getButton1Text());
            if (mDialogConfig.getButton1BackgroundColor() != null){
                btn.setColorWithInt(mDialogConfig.getButton1BackgroundColor());
            }
        } else if (number == 2 && !PDEString.isEmpty(mDialogConfig.getButton2Text())) {
            btn.setText(mDialogConfig.getButton2Text());
            if (mDialogConfig.getButton2BackgroundColor() != null){
                btn.setColorWithInt(mDialogConfig.getButton2BackgroundColor());
            }
        }

        return btn;
    }


    /**
     * @brief Delivers a spacer, which we need to separate multiple buttons.
     */
    protected View getButtonSpacer() {
        View spacer;
        spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(mButtonSpacerWidthInBU, 1));
        return spacer;
    }


    /**
     * @brief Overrides the reaction for the press of the hardware back button.
     */
    @Override
    public void onBackPressed() {
        if (mDialogConfig.isAndroidHardwareBackButtonEnabled()) {
            // Report that back button was pressed.
            sendDialogResultBroadcast(PDEDialog.PDE_DIALOG_RESULT_ANDROID_HARDWARE_BACK_BUTTON);
        } else {
            // ignore the press
            if (DEBUG_OUTPUT) {
                // and print out debug message
                Log.d(LOG_TAG,"Back Button disabled in Dialog!");
            }
        }
    }



//------------------- IPC Messaging ------------------------------------------------------------------------------------

    /**
     * @brief Creates the broadcast receiver that listens for the dismiss-command.
     */
    protected void createDismissBroadcastReceiver(){
        mDismissReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (DEBUG_OUTPUT) {
                    // debug
                    Log.d(LOG_TAG,"RECEIVE "+mDialogConfig.getBroadcastID()+PDE_DIALOG_BROADCAST_POSTFIX_DISMISS);
                }
                // close this activity
                PDEDialogActivity.this.finish();
                // unregister the dismiss receiver
                PDEDialogActivity.this.unregisterDismissBroadcastReceiver();
            }
        };
    }


    /**
     * @brief Registers the broadcast receiver that listens for the dismiss-command.
     */
    protected void registerDismissBroadcastReceiver(){
        LocalBroadcastManager.getInstance(PDECodeLibrary.getInstance().getApplicationContext()).
                registerReceiver(mDismissReceiver, new IntentFilter(mDialogConfig.getBroadcastID()
                                                                    + PDE_DIALOG_BROADCAST_POSTFIX_DISMISS));
    }


    /**
     * @brief Unregisters the broadcast receiver that listens for the dismiss-command.
     */
    protected void unregisterDismissBroadcastReceiver(){
        LocalBroadcastManager.getInstance(PDECodeLibrary.getInstance().getApplicationContext())
                .unregisterReceiver(mDismissReceiver);
    }


    /**
     * @brief Sends broadcast message that tells the receiver, that this dialog activity is ready to receive broadcasts.
     */
    protected void sendDialogRunningBroadcast(){
        Intent intent = new Intent(mDialogConfig.getBroadcastID()+PDE_DIALOG_BROADCAST_POSTFIX_RUNNING);
        sendBroadcastMessage(intent);
    }


    /**
     * @brief Sends broadcast message that tells the receiver the result of the dialog.
     *
     * The result is either, which button was pressed, or that the dialog was canceled by the back button.
     */
    protected void sendDialogResultBroadcast(int result){
        Intent intent = new Intent(mDialogConfig.getBroadcastID()+PDE_DIALOG_BROADCAST_POSTFIX_RESULT);
        // add data
        intent.putExtra(PDE_DIALOG_INTENT_EXTRA_RESULT, result);
        sendBroadcastMessage(intent);
    }


    /**
     * @brief General Helper for sending broadcast messages.
     *
     * @param intent intent that contains the message that should be sent by broadcast.
     */
    protected void sendBroadcastMessage(Intent intent){
        if (DEBUG_OUTPUT) {
            // debug
            Log.d(LOG_TAG, "SEND "+intent.getAction());
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
