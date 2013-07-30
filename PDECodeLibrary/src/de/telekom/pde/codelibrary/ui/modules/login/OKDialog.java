/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 * 
 * kdanner - 09.07.13
 */

package de.telekom.pde.codelibrary.ui.modules.login;

import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.activity.PDEActivity;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton;
import de.telekom.pde.codelibrary.ui.components.drawables.PDEDrawableMultilayer;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableRoundedBox;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableDelimiter;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;
import de.telekom.pde.codelibrary.ui.elements.wrapper.PDELayerTextView;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;
import de.telekom.pde.codelibrary.ui.layout.PDEBoundedRelativeLayout;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

/// @cond INTERNAL_CLASS


//----------------------------------------------------------------------------------------------------------------------
//  OKDialog
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief Simple OKDialog - just an implementation for the OneIDMLogin Module, it is not yet sophisticated enough to be
 * a real component.
 * The content and the functionality may change completely in a future release. So don't rely on it.
 *
 */
public class OKDialog extends PDEActivity {

    public final static String PDE_OK_DIALOG_INTENT_EXTRA_TITLE = "PDE.OKDialog.Extra.Title";
    public final static String PDE_OK_DIALOG_INTENT_EXTRA_MESSAGE = "PDE.OKDialog.Extra.Message";
    public final static String PDE_OK_DIALOG_INTENT_EXTRA_OKBUTTON_TEXT = "PDE.OKDialog.Extra.OKButtonText";

    // the default typeface
    //protected final static Typeface DEFAULT_TYPEFACE = PDETypeface.sDefaultFont.getTypeface();
    protected final static PDETypeface DEFAULT_TYPEFACE = PDETypeface.sDefaultFont;

    protected final static float FONTSIZE_LARGE = PDEFontHelpers.calculateFontSize(PDETypeface.sDefaultFont, PDEBuildingUnits.pixelFromBU(7.0f / 6.0f));
    protected final static float FONTSIZE_DEFAULT = PDEFontHelpers.calculateFontSize(PDETypeface.sDefaultFont, PDEBuildingUnits.BU());

    protected final static float CONFIG_MAXIMUM_WIDTH_IN_BU = 28.0f;

    private final static String LOG_TAG = OKDialog.class.getName();


    /**
     * Sets the content layout.
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // there is no dark style OKDialog
        int textColor = PDEColor.valueOf("DTLightUIText").getIntegerColor();
        String title = "";
        String message = "";
        String buttonText = "";

        // get the intent
        Intent intent = this.getIntent();

        if (intent != null) {
            // get values from intent
            if (intent.hasExtra(PDE_OK_DIALOG_INTENT_EXTRA_TITLE)) {
                title = intent.getStringExtra(PDE_OK_DIALOG_INTENT_EXTRA_TITLE);
            }
            if (intent.hasExtra(PDE_OK_DIALOG_INTENT_EXTRA_MESSAGE)) {
                message = intent.getStringExtra(PDE_OK_DIALOG_INTENT_EXTRA_MESSAGE);
            }
            if (intent.hasExtra(PDE_OK_DIALOG_INTENT_EXTRA_TITLE)) {
                buttonText = intent.getStringExtra(PDE_OK_DIALOG_INTENT_EXTRA_OKBUTTON_TEXT);
            }
        }

        // load the layout
        setContentView(R.layout.okdialog);

        // set general configuration of the layout - which can't be set in the XML


        PDEDrawableMultilayer multilayer = new PDEDrawableMultilayer();
        final PDEDrawableRoundedBox roundedBox = new PDEDrawableRoundedBox();
        final PDEDrawableShapedShadow shadow = (PDEDrawableShapedShadow)roundedBox.createElementShadow();
        shadow.setElementBlurRadius(shadow.getElementBlurRadius()*2.0f);
        multilayer.addLayer(shadow);
        multilayer.addLayer(roundedBox);

        multilayer.setOnBoundsChangeListener(new PDEDrawableMultilayer.OnPDEBoundsChangeListener() {
            @Override
            public void onPDEBoundsChange(Drawable source, Rect bounds) {
                int padding = roundedBox.getNeededPadding();
                roundedBox.setBounds(new Rect(bounds.left + padding, bounds.top + padding, bounds.right - padding,
                        bounds.bottom -padding));

//                shadow.setLayoutOffset(new Point(roundedBox.getBounds().left,
//                        roundedBox.getBounds().top + PDEBuildingUnits.oneTwelfthsBU()));

                  shadow.setLayoutOffset(new Point(roundedBox.getBounds().left,
                        roundedBox.getBounds().top + PDEBuildingUnits.oneSixthBU()));
            }
        });

        PDEUtils.setViewBackgroundDrawable(findViewById(R.id.okdialog_boundedplane), multilayer);
        PDEUtils.setViewBackgroundDrawable(findViewById(R.id.okdialog_title_delimiter), new PDEDrawableDelimiter());

        // set all sizes an margins corrected by shadowPadding
        int shadowPadding = roundedBox.getNeededPadding();
        findViewById(R.id.okdialog_linearlayout).setPadding(shadowPadding, shadowPadding, shadowPadding, shadowPadding);
        // set maximum width for LoginPane
        PDEBoundedRelativeLayout boundedPane = (PDEBoundedRelativeLayout)findViewById(R.id.okdialog_boundedplane);
        boundedPane.setMaxWidth(PDEBuildingUnits.pixelFromBU(CONFIG_MAXIMUM_WIDTH_IN_BU) + 2 * shadowPadding);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)boundedPane.getLayoutParams();
        lp.leftMargin -= shadowPadding;
        lp.rightMargin -= shadowPadding;
        lp.topMargin -= shadowPadding;
        lp.bottomMargin -= shadowPadding;
        boundedPane.setLayoutParams(lp);

        // set title attributes
        PDELayerTextView titleTextView = (PDELayerTextView)findViewById(R.id.okdialog_title);
        titleTextView.setTypeface(DEFAULT_TYPEFACE);
        titleTextView.setTextSize(FONTSIZE_LARGE);
        titleTextView.setTextColor(textColor);

        // set message attributes
        PDELayerTextView messageTextView = (PDELayerTextView)findViewById(R.id.okdialog_message);
        messageTextView.setTypeface(DEFAULT_TYPEFACE);
        messageTextView.setTextSize(FONTSIZE_DEFAULT);
        messageTextView.setTextColor(textColor);

        // register listener for the button
        PDEButton exitButton = (PDEButton)findViewById(R.id.okdialog_exit);
        exitButton.addListener(this, "onExitButton", PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED);

        if (TextUtils.isEmpty(title)) {
            // no title hide the whole area
            findViewById(R.id.okdialog_title_area).setVisibility(View.GONE);
        } else {
            // set title text
            titleTextView.setText(title);
        }

        if (!TextUtils.isEmpty(message)) {
            messageTextView.setText(message);
        }

        if (!TextUtils.isEmpty(buttonText)) {
            Log.d(LOG_TAG, "setButtonText");
            exitButton.setText(buttonText);
        }
    }


    /**
     * @brief Listener callback function for the exit button.
     * Finishes the dialog.
     * @param event
     */
    public void onExitButton(PDEEvent event) {
        finish();
    }

    /// @endcond
}