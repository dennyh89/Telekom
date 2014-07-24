/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.modules.login;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.text.Html;
import android.text.InputType;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Locale;

import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.activity.PDEActionBarActivity;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton;
import de.telekom.pde.codelibrary.ui.components.inputfields.PDEInputField;
import de.telekom.pde.codelibrary.ui.components.inputfields.PDEInputFieldEvent;
import de.telekom.pde.codelibrary.ui.components.notification.PDEInfoFlag;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableCornerBox;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableNotificationFrame;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableRoundedBox;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableDelimiter;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.GridBackgroundDrawable;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDEString;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;
import de.telekom.pde.codelibrary.ui.layout.PDEBoundedRelativeLayout;

//----------------------------------------------------------------------------------------------------------------------
//  PDEBaseLoginScreenActivity
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Sample screen for login (work in progress).
 *
 * The activity implements (when finished) the login pattern mobile for android.
 */
@SuppressWarnings("unused")
public abstract class PDEBaseLoginScreenActivity extends PDEActionBarActivity {

    // debugging log tag
    protected final static String LOG_TAG = PDEBaseLoginScreenActivity.class.getName();
    protected final static boolean DEBUG_SHOW_GRID_BACKGROUND = false;

    // private members
    protected PDEButton mCheckbox;
    protected PDEButton mButtonLogin;
    protected PDEInputField mUsernameInputField;
    protected PDEInputField mPasswordInputField;
    protected PDEInfoFlag mLoginScreenPositionableInfoFlag;

    protected View mLoginScreenHeaderArea;
    protected View mLoginScreenDescriptionArea;
    protected View mLoginScreenFooterArea;


    // calculate all used font sizes
    protected final static float FONTSIZE_HEADER = PDEFontHelpers.calculateFontSizeByPercent(PDETypeface.sDefaultFont,
                                                                                             133);
    protected final static float FONTSIZE_LARGE = PDEFontHelpers.calculateFontSize(PDETypeface.sDefaultFont,
                                                                                   PDEBuildingUnits.pixelFromBU(
                                                                                           7.0f / 6.0f));
    protected final static float FONTSIZE_DEFAULT = PDEFontHelpers.calculateFontSize(PDETypeface.sDefaultFont,
                                                                                     PDEBuildingUnits.BU());
    protected final static float FONTSIZE_SMALL = PDEFontHelpers.calculateFontSize(PDETypeface.sDefaultFont,
                                                                                   PDEBuildingUnits.pixelFromBU(
                                                                                           5.0f / 6.0f));
    protected final static float FONTSIZE_SMALLER = PDEFontHelpers.calculateFontSizeByPercent(PDETypeface.sDefaultFont,
                                                                                              75);

    // the default typeface
    protected final static Typeface DEFAULT_TYPEFACE = PDETypeface.sDefaultFont.getTypeface();

    protected final static int DTUI_LINKCOLOR = 0xff427BAB; //DTDarkBlue

    protected PDEConstants.PDEContentStyle mStyle = PDEConstants.PDEContentStyle.PDEContentStyleFlat;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent callIntent = getIntent();
        if (callIntent != null) {
            String text = callIntent.getStringExtra(PDEConstants.PDE_CODELIB_EXTRA_CONTENTSTYLE);
            if (text != null && text.length() != 0) {
                if (PDEString.contains(text.toUpperCase(Locale.US), "haptic".toUpperCase(Locale.US))) {
                    mStyle = PDEConstants.PDEContentStyle.PDEContentStyleHaptic;
                } else if (PDEString.contains(text.toUpperCase(Locale.US), "flat".toUpperCase(Locale.US))) {
                    mStyle = PDEConstants.PDEContentStyle.PDEContentStyleFlat;
                }
            }
        }

        int textColor = PDEColor.DTUITextColor().getIntegerColor();

        // load layout from xml
        this.setContentView(R.layout.pde_module_login_base_screen);

        mLoginScreenPositionableInfoFlag = (PDEInfoFlag) findViewById(R.id.LoginScreenPositionableInfoFlag);

        // get area as member variables
        mLoginScreenHeaderArea = findViewById(R.id.LoginScreenHeaderArea);
        mLoginScreenDescriptionArea = findViewById(R.id.LoginScreenDescriptionArea);
        mLoginScreenFooterArea = findViewById(R.id.LoginScreenFooterArea);

        // set maximum width for LoginPane
        ((PDEBoundedRelativeLayout) findViewById(R.id.LoginScreenBoundedPane))
                .setMaxWidth(PDEBuildingUnits.pixelFromBU(30.0f));

        // set default background color

        if (DEBUG_SHOW_GRID_BACKGROUND) {
            // overwrite the background with the grid to see the BU layout
            PDEUtils.setViewBackgroundDrawable(findViewById(R.id.LoginScreenRootView),
                                               new GridBackgroundDrawable(PDEColor.valueOf("#2C3366AA")));
        } else {
            findViewById(R.id.LoginScreenRootView).setBackgroundColor(PDEColor.DTUIBackgroundColor().getIntegerColor());
        }

        // description label
        // set color, font and fontsize
        final TextView loginScreenDescriptionLabel = (TextView) findViewById(R.id.LoginScreenDescriptionLabel);
        loginScreenDescriptionLabel.setTextColor(textColor);
        loginScreenDescriptionLabel.setTypeface(DEFAULT_TYPEFACE);
        loginScreenDescriptionLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FONTSIZE_LARGE);

        // email label
        // set color, font and fontsize
        final TextView loginScreenEmailLabel = (TextView) findViewById(R.id.LoginScreenUsernameLabel);
        loginScreenEmailLabel.setTextColor(textColor);
        loginScreenEmailLabel.setTypeface(DEFAULT_TYPEFACE);
        loginScreenEmailLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FONTSIZE_SMALL);

        // password label
        // set color, font and fontsize
        final TextView loginScreenPasswordLabel = (TextView) findViewById(R.id.LoginScreenPasswordLabel);
        loginScreenPasswordLabel.setTextColor(textColor);
        loginScreenPasswordLabel.setTypeface(DEFAULT_TYPEFACE);
        loginScreenPasswordLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FONTSIZE_SMALL);

        // set the drawables for the footer delimiters
        PDEUtils.setViewBackgroundDrawable(findViewById(R.id.LoginScreenFooterDelimiter), new PDEDrawableDelimiter());

        // not registered yet label
        // set color, font and fontsize
        final TextView
                loginScreenNotRegisteredYetLabel
                = (TextView) findViewById(R.id.LoginScreenNotRegisteredYetLabel);
        loginScreenNotRegisteredYetLabel.setTextColor(textColor);
        loginScreenNotRegisteredYetLabel.setTypeface(DEFAULT_TYPEFACE);
        loginScreenNotRegisteredYetLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FONTSIZE_DEFAULT);

        // register here label

        // set color, font and fontsize
        final TextView loginScreenRegisterHereLabel = (TextView) findViewById(R.id.LoginScreenRegisterHereLabel);
        loginScreenRegisterHereLabel.setLinkTextColor(DTUI_LINKCOLOR);
        loginScreenRegisterHereLabel.setTypeface(DEFAULT_TYPEFACE);
        setRegisterHereHTML(getResources().getString(R.string.login_screen_register_now_link));
        loginScreenRegisterHereLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FONTSIZE_DEFAULT);

        // email input-field
        mUsernameInputField = (PDEInputField) findViewById(R.id.LoginScreenUsernameInputField);
        if (mStyle == PDEConstants.PDEContentStyle.PDEContentStyleHaptic) {
            mUsernameInputField.setInputFieldBackgroundLayerWithLayerType(
                    PDEButton.PDEButtonLayerType.BackgroundTextHaptic);
        }
        mUsernameInputField.setHint(getResources().getString(R.string.login_screen_username_input_field_hint));
        mUsernameInputField.addListener(this,
                                        "onInputFieldEmailChanged",
                                        PDEInputField.PDE_INPUTFIELD_EVENT_MASK_ACTION);

        // password input-field
        mPasswordInputField = (PDEInputField) findViewById(R.id.LoginScreenPasswordInputField);
        if (mStyle == PDEConstants.PDEContentStyle.PDEContentStyleHaptic) {
            mPasswordInputField.setInputFieldBackgroundLayerWithLayerType(
                    PDEButton.PDEButtonLayerType.BackgroundTextHaptic);
        }

        mPasswordInputField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mPasswordInputField.setHint(getResources().getString(R.string.login_screen_password_input_field_hint));
        mPasswordInputField.addListener(this,
                                        "onInputFieldPasswordChanged",
                                        PDEInputField.PDE_INPUTFIELD_EVENT_MASK_ACTION);

        // stay signed in checkbox
        mCheckbox = (PDEButton) findViewById(R.id.LoginScreenStaySignedInCheckbox);
        if (mStyle == PDEConstants.PDEContentStyle.PDEContentStyleHaptic) {
            mCheckbox.setButtonOverlayLayerWithLayerType(PDEButton.PDEButtonLayerType.OverlayCheckboxHaptic);
        }

        mCheckbox.setTag(R.id.LoginScreenStaySignedInCheckbox);
        // add listener for the checkbox
        mCheckbox.addListener(this, "onButtonCheckboxPressed",
                              PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED);

        // login button
        mButtonLogin = (PDEButton) findViewById(R.id.LoginScreenButtonLogin);
        if (mStyle == PDEConstants.PDEContentStyle.PDEContentStyleHaptic) {
            mButtonLogin.setButtonBackgroundLayerWithLayerType(PDEButton.PDEButtonLayerType.BackgroundHaptic);
        }
        mButtonLogin.addListener(this, "onButtonLoginPressed",
                                 PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED);

        // set drawable for the brand area
        PDEUtils.setViewBackgroundDrawable(findViewById(R.id.LoginScreenBrandDelimiter), new PDEDrawableDelimiter());

        adjustLayout();

        // prevent keyboard from showing in beginning
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        updateLoginState();
    }


    /**
     * @param event information
     * @brief Callback for login button.
     */
    public void onButtonLoginPressed(final PDEEvent event) {
        loginButtonClicked(mUsernameInputField.getText().toString(), mPasswordInputField.getText().toString(),
                           isStaySignedIn());
    }


    /**
     * @param username     entered user name
     * @param password     entered password
     * @param staySignedIn has the user checked the checkbox
     * @brief Called when the user wants to log in and has clicked the login button.
     */
    public abstract void loginButtonClicked(final String username, final String password, final boolean staySignedIn);


    /**
     * @brief Set text (or html text) for the description label
     */
    public void setDescriptionHTML(final String text) {
        setTelekomTextLinkBehaviour((TextView) findViewById(R.id.LoginScreenDescriptionLabel), text);
    }


    /**
     * @brief Set text (or html text) for the register here label
     */
    protected void setRegisterHereHTML(final String text) {
        setTelekomTextLinkBehaviour((TextView) findViewById(R.id.LoginScreenRegisterHereLabel), text);
    }


    /**
     * @brief Set text (or html text) for the password forgotten label
     */
    protected void setForgotPasswordHTML(final String text) {
        setTelekomTextLinkBehaviour((TextView) findViewById(R.id.LoginScreenForgotPasswordLabel), text);
    }


    /**
     * @brief Helper function to set HTML to a TextView and replace the URLSpans in it with some custom behaviour Span.
     */
    private void setTelekomTextLinkBehaviour(final TextView textView, final String text) {
        if (textView != null) {
            // also accept html strings
            textView.setText(Html.fromHtml(text));
            // make links clickable
            textView.setMovementMethod(new LinkTouchMovementMethod());
            textView.setFocusable(false);
            textView.setClickable(false);
            textView.setLongClickable(false);
            textView.setLinkTextColor(DTUI_LINKCOLOR);
            replaceULRwithTouchableURL(textView);
        }
    }


    /**
     * @param visible false = gone; true = visible
     * @brief Show or hide register area.
     */
    public void setRegisterAreaVisible(final boolean visible) {
        if (visible) {
            findViewById(R.id.LoginScreenFooterArea).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.LoginScreenFooterArea).setVisibility(View.GONE);
        }
    }


    /**
     * @param visible false = gone; true = visible
     * @brief Show or hide t-brand logo area.
     */
    protected void setTBrandLogoVisible(final boolean visible) {
        if (visible) {
            findViewById(R.id.LoginScreenBrandArea).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.LoginScreenBrandArea).setVisibility(View.GONE);
        }
    }


    /**
     * @param visible false = gone; true = visible
     * @brief Show or hide stay signed in area.
     */
    protected void setStaySignedInVisible(final boolean visible) {
        if (visible) {
            findViewById(R.id.LoginScreenStaySignedInCheckbox).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.LoginScreenStaySignedInCheckbox).setVisibility(View.GONE);
        }
    }


    /**
     * @brief Extend the layout to fill the screen on portrait phone and show or hide the rounded
     * corner box depending on space.
     */
    protected void adjustLayout() {
        final View fInnerScrollView = findViewById(R.id.LoginScreenInnerScrollView);

        if (!PDEBuildingUnits.isTablet()) {
            int height = PDEUtils.getDisplayDimension(this).y;

            if (getSupportActionBar() != null && getTheme() != null) {
                final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                        new int[]{R.attr.actionBarSize});
                if (styledAttributes != null) {
                    height -= (int) styledAttributes.getDimension(0, 0);
                    styledAttributes.recycle();
                }
            }

            if ((getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == 0) {
                height -= PDEUtils.getStatusBarHeight(this);
            }

            findViewById(R.id.LoginScreenInnerScrollView).setMinimumHeight(height);
        }

        if (PDEUtils.getDisplayDimension(this).x > PDEBuildingUnits.pixelFromBU(30.0f)) {
            if (PDEBuildingUnits.isTablet()) {
                PDEUtils.setViewBackgroundDrawable(fInnerScrollView, new PDEDrawableRoundedBox());
            } else {
                PDEUtils.setViewBackgroundDrawable(fInnerScrollView, new PDEDrawableCornerBox());
            }
        } else {
            PDEUtils.setViewBackgroundDrawable(fInnerScrollView, null);
        }
    }


    /**
     * @brief Set default value for the username.
     */
    protected void setUsername(String username) {
        if (mUsernameInputField != null) {
            mUsernameInputField.setText(username);
        }
    }


    /**
     * @brief Set default value for the password.
     */
    protected void setPassword(String password) {
        if (mPasswordInputField != null) {
            mPasswordInputField.setText(password);
        }
    }


    /**
     * @brief Query if the stay signed in checkbox is selected.
     */
    protected boolean isStaySignedIn() {
        return mCheckbox.isSelected();
    }


    /**
     * @param event information
     * @brief Callback for "stay signed in" checkbox.
     */
    public void onButtonCheckboxPressed(final PDEEvent event) {

        // determine which checkbox
        final int tag = ((Integer) ((View) event.getSender()).getTag());

        // action depending on checkbox
        if (tag == R.id.LoginScreenStaySignedInCheckbox) {
            mCheckbox.setSelected(!mCheckbox.isSelected());
        }
    }


    /**
     * @brief Callback for email input field changes by AgentController.
     */
    public void onInputFieldEmailChanged(final PDEEvent event) {
        final PDEInputFieldEvent inputEvent = (PDEInputFieldEvent) event;

        // if text did changed check if its a valid mail or not
        if (inputEvent.isType(PDEInputField.PDE_INPUTFIELD_EVENT_ACTION_AFTER_TEXT_CHANGED)) {
            // if there is a text -> validate and set new stat
            mUsernameInputField.setMainState(PDEButton.PDEButtonStateDefault);
            updateLoginState();
        } else if (inputEvent.isType(PDEInputField.PDE_INPUTFIELD_EVENT_ACTION_DID_CLEAR_TEXT)) {
            // if text was cleared, go back to default state
            mUsernameInputField.setMainState(PDEButton.PDEButtonStateDefault);
            updateLoginState();
        }
    }


    /**
     * @brief Callback for password input field changes by AgentController.
     *
     * Called via reflection.
     */
    public void onInputFieldPasswordChanged(final PDEEvent event) {
        final PDEInputFieldEvent inputEvent = (PDEInputFieldEvent) event;

        // if text did changed check if its a valid mail or not
        if (inputEvent.isType(PDEInputField.PDE_INPUTFIELD_EVENT_ACTION_AFTER_TEXT_CHANGED)) {
            updateLoginState();
        } else if (inputEvent.isType(PDEInputField.PDE_INPUTFIELD_EVENT_ACTION_DID_CLEAR_TEXT)) {
            // if text was cleared, go back to default state
            updateLoginState();
        }
    }


    /**
     * @brief Check username validity.
     *
     * Currently is is only checked that the username isn't empty.
     */
    protected boolean checkValidUsername(final String inputString) {
        // valid?
        return (!TextUtils.isEmpty(inputString));
    }


    /**
     * @brief Check password validity.
     *
     * Currently is is only checked that the username isn't empty.
     */
    protected boolean checkValidPassword(final String inputString) {
        // valid?
        return (!TextUtils.isEmpty(inputString));
    }


    /**
     * @brief Update login button state.
     *
     * Login is only allowed if mail is valid, and if password contains at least 8 characters.
     */
    protected void updateLoginState() {
        // check (which is not sophisticated)
        if (checkValidUsername(mUsernameInputField.getText().toString())
            && checkValidPassword(mPasswordInputField.getText().toString())) {
            // show fully (not animated yet, so set directly)
            mButtonLogin.setEnabled(true);
            PDEUtils.setViewAlpha(mButtonLogin, 1.0f);
        } else {
            // hide fully (not animated yet)
            mButtonLogin.setEnabled(false);
            PDEUtils.setViewAlpha(mButtonLogin, .5f);
        }
    }


    /**
     * @param textView textview which content shall be converted.
     * @brief Subfunction for setTelekomTextLinkBehaviour which replaces URLSpan with the custom TouchableURLSpans.
     *
     * Do some very special behaviour to also show the tooltip for "tooltip:"-URLs.
     */
    private void replaceULRwithTouchableURL(TextView textView) {
        Spannable s = (Spannable) textView.getText();
        if (s != null) {
            URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
            //SpannableStringBuilder strBuilder = new SpannableStringBuilder(s);
            for (URLSpan span : spans) {
                int start = s.getSpanStart(span);
                int end = s.getSpanEnd(span);

                s.removeSpan(span);

                TouchableURLSpan tus = new TouchableURLSpan(span.getURL()) {
                    boolean pressed = false;


                    @Override
                    public boolean onTouch(View widget, MotionEvent m) {
                        pressed = (m.getAction() == MotionEvent.ACTION_DOWN);
                        widget.invalidate();
                        //Log.d(LOG_TAG, "touch " + m.getAction());
                        return false;
                    }


                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        if (pressed) {
                            ds.setUnderlineText(true);
                        } else {
                            ds.setUnderlineText(false);
                        }
                    }


                    @Override
                    public void onClick(View widget) {
                        if (mIsToolTip) {
                            String url = this.getURL();
                            String toolTipSearch = "tooltip:";
                            if (url.startsWith(toolTipSearch)) {
                                url = url.substring(toolTipSearch.length());
                            }
                            showPositionedToolTip((TextView) widget, this, url, mLoginScreenPositionableInfoFlag);
                        } else {
                            Uri uri = Uri.parse(getURL());
                            Context context = widget.getContext();
                            if (context != null) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
                                context.startActivity(intent);
                            }
                        }
                    }
                };

                if (PDEString.startsWith(span.getURL(), "tooltip:")) {
                    tus.mIsToolTip = true;
                }

                s.setSpan(tus, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textView.setText(s);
        }
    }


    /**
     * @param textView    the textView which contains the clickedText-Spannable
     * @param clickedText the clicked link
     * @param textToShow  text to show in the tooltip
     * @brief Position and show the "Telekom Dienste" ToolTip.
     */
    protected void showPositionedToolTip(TextView textView,
                                         CharacterStyle clickedText,
                                         String textToShow,
                                         PDEInfoFlag infoFlag) {

        boolean keywordIsInMultiLine = false;
        int toolTipHeight;
        int spanStartOfClickedText = 0;
        int spanEndOfClickedText = 0;
        double startXClickedText;
        double endXClickedText;
        int spanLineStart = 0;
        int spanLineEnd = 0;
        double parentTextViewYOffset;
        Rect spanRect = new Rect();
        int[] parentTextViewLocation = {0, 0};

        // Initialize values for the computing of clickedText position
        SpannableString completeText = (SpannableString) textView.getText();
        Layout textViewLayout = textView.getLayout();

        if (completeText == null || textViewLayout == null) return;

        spanStartOfClickedText = completeText.getSpanStart(clickedText);
        spanEndOfClickedText = completeText.getSpanEnd(clickedText);

        startXClickedText = textViewLayout.getPrimaryHorizontal(spanStartOfClickedText);
        endXClickedText = textViewLayout.getPrimaryHorizontal(spanEndOfClickedText);
        // Get the rectangle of the clicked text
        spanLineStart = textViewLayout.getLineForOffset(spanStartOfClickedText);
        spanLineEnd = textViewLayout.getLineForOffset(spanEndOfClickedText);
        keywordIsInMultiLine = (spanLineStart != spanLineEnd);
        textViewLayout.getLineBounds(spanLineStart, spanRect);

        // Update the rectangle position to his real position on screen
        textView.getLocationOnScreen(parentTextViewLocation);

        // get y coordinate of text view corrected by offset and padding
        parentTextViewYOffset = (parentTextViewLocation[1] - textView.getScrollY() + textView.getCompoundPaddingTop());

        // adjust offset by height of ActionBar and StatusBar
        if (getSupportActionBar() != null && getTheme() != null) {
            final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                    new int[]{R.attr.actionBarSize});
            if (styledAttributes != null) {
                parentTextViewYOffset -= (int) styledAttributes.getDimension(0, 0);
                styledAttributes.recycle();
            }
        }
        if ((getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == 0) {
            parentTextViewYOffset -= PDEUtils.getStatusBarHeight(this);
        }

        // apply action-&statusbar correction
        spanRect.top += parentTextViewYOffset;
        spanRect.bottom += parentTextViewYOffset;

        // In the case of multi line text, we have to choose what rectangle take
        if (keywordIsInMultiLine) {
            int screenHeight = PDEUtils.getDisplayDimension(this).y;
            int dyTop = spanRect.top;
            int dyBottom = screenHeight - spanRect.bottom;
            boolean onTop = dyTop > dyBottom;

            if (onTop) {
                endXClickedText = textViewLayout.getLineRight(spanLineStart);
            } else {
                spanRect = new Rect();
                textViewLayout.getLineBounds(spanLineEnd, spanRect);
                spanRect.top += parentTextViewYOffset;
                spanRect.bottom += parentTextViewYOffset;
                startXClickedText = textViewLayout.getLineLeft(spanLineEnd);
            }
        }

        spanRect.left += (
                parentTextViewLocation[0] +
                startXClickedText +
                textView.getCompoundPaddingLeft() -
                textView.getScrollX()
        );
        spanRect.right = (int) (
                spanRect.left +
                endXClickedText -
                startXClickedText
        );

        // apply calculated values

        // set the new text (for size calculation)
        infoFlag.setMessageText(textToShow);
        infoFlag.setTitleEnabled(false);
        // same width as the text view
        infoFlag.setMaxWidth(textView.getWidth());

        // measure size
        infoFlag.measure(
                View.MeasureSpec.makeMeasureSpec(textView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(spanRect.top, View.MeasureSpec.AT_MOST));

        // get height of tooltip
        toolTipHeight = infoFlag.getMeasuredHeight();

        // set the Point of the tooltip
        infoFlag.setTriangleTipPositionAbsolute(
                spanRect.left + ((spanRect.right - spanRect.left) / 2.0f) - parentTextViewLocation[0],
                PDEDrawableNotificationFrame.TriangleSide.SideBottom);

        // set the position of the tooltip
        PDEAbsoluteLayout.LayoutParams
                lp = (PDEAbsoluteLayout.LayoutParams)
                infoFlag.getLayoutParams();
        if (lp != null) {
            lp.x = parentTextViewLocation[0];
            lp.y = spanRect.top - toolTipHeight;
            infoFlag.setLayoutParams(lp);
        }

        // measure it again to ensure that the right position is shown
        infoFlag.measure(
                View.MeasureSpec.makeMeasureSpec(textView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(Math.min(toolTipHeight, spanRect.top), View.MeasureSpec.EXACTLY));

        // display the tooltip
        infoFlag.show();
    }
}