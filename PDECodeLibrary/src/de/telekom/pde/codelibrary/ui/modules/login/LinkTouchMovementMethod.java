/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 * 
 * kdanner - 04.07.13
 */

package de.telekom.pde.codelibrary.ui.modules.login;

import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

/// @cond INTERNAL_CLASS

//----------------------------------------------------------------------------------------------------------------------
//  LinkTouchMovementMethod
//----------------------------------------------------------------------------------------------------------------------


class LinkTouchMovementMethod extends LinkMovementMethod
{

    boolean stateDown = false;
    TouchableURLSpan currentSpan;
    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN||
                action == MotionEvent.ACTION_CANCEL) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            TouchableURLSpan[] link = buffer.getSpans(off, off, TouchableURLSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onTouch(widget, event);
                    if (stateDown) {
                        link[0].onClick(widget);
                    }
                    stateDown = false;
                } else if (action == MotionEvent.ACTION_CANCEL) {
                    link[0].onTouch(widget,event);
                    stateDown = false;
                } else if (action == MotionEvent.ACTION_DOWN) {
                    link[0].onTouch(widget,event);
                    stateDown = true;
                    currentSpan = link[0];
                }

                return true;
            } else {
                if (stateDown) {
                    MotionEvent mo = MotionEvent.obtain(event);
                    mo.setAction(MotionEvent.ACTION_CANCEL);
                    currentSpan.onTouch(widget, mo);
                    stateDown = false;
                }
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    /// @endcond

}