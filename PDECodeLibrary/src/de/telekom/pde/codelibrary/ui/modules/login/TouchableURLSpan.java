/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 * 
 * kdanner - 04.07.13
 */

package de.telekom.pde.codelibrary.ui.modules.login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Browser;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/// @cond INTERNAL_CLASS

/**
 * If an object of this type is attached to the text of a TextView
 * with a movement method of LinkTouchMovementMethod, the affected spans of
 * text can be selected.  If touched, the {@link #onTouch} method will
 * be called.
 */
class TouchableURLSpan extends CharacterStyle implements UpdateAppearance     {

	@SuppressWarnings("unused")
    private final static String LOG_TAG = "TouchableURLSpan";

    private final String mURL;
    public boolean mIsToolTip = false;

    public TouchableURLSpan(String url) {
        mURL = url;
    }

    @SuppressWarnings("unused")
    public TouchableURLSpan(Parcel src) {
        mURL = src.readString();
    }

    @SuppressWarnings("unused")
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("unused")
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mURL);
    }

    /**
     * Performs the touch action associated with this span.
     */
    public boolean onTouch(View widget, MotionEvent m) {
        return false;
    }

    /**
     * Could make the text underlined or change link color.
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ds.linkColor);
    }

    public void onClick(View widget) {
        if (mIsToolTip) {
            Toast.makeText(widget.getContext(),"Implement when needed",Toast.LENGTH_SHORT).show();
        } else {
            Uri uri = Uri.parse(getURL());
            Context context = widget.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            context.startActivity(intent);
        }
    }

    public String getURL() {
        return mURL;
    }

}

/// @endcond INTERNAL_CLASS