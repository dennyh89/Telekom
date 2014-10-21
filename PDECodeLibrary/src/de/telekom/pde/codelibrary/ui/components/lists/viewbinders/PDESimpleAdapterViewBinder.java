/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.lists.viewbinders;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Checkable;
import android.widget.SimpleAdapter;

import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDEIconView;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDETextView;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.metaphors.PDEPhotoFrameView;

//----------------------------------------------------------------------------------------------------------------------
// PDESimpleAdapterViewBinder
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Custom View Binder which enables the use of various PDE components for a SimpleAdapter.
 *
 * A standard Android SimpleAdapter can only handle some default Android UI components. But the SimpleAdapter offers the
 * possibility to use custom ViewBinders. With this ViewBinder interface it is possible to add further UI components and
 * to determine how they will be filled with data. So this ViewBinder handles PDE UI components which are commonly used
 * within list element layouts.
 *
 * Recommendation: The PDESimpleAdapter automatically makes use of this ViewBinder. It comes along with some list
 * element highlighting logic in the style of the styleguide. So we recommend to simply use the PDESimpleAdapter.
 */
public class PDESimpleAdapterViewBinder implements SimpleAdapter.ViewBinder {
    /**
     * Binds the specified data to the specified view.
     *
     * When binding is handled by this ViewBinder, this method must return true.
     * If this method returns false, the parent SimpleAdapter will attempt to handle
     * the binding on its own.
     *
     * @param view the view to bind the data to
     * @param data the data to bind to the view
     * @param text a safe String representation of the supplied data:
     *        it is either the result of data.toString() or an empty String but it
     *        is never null
     *
     * @return true if the data was bound to the view, false otherwise
     */
    @Override
    public boolean setViewValue(View view, Object data, String text) {
        // views derived from Checkable
        if (view instanceof Checkable) {
            // if the data is a boolean it is meant to set the check state on or off (true == on/ false == off)
            if (data instanceof Boolean) {
                ((Checkable) view).setChecked((Boolean) data);
                return true;
            }
            // If the view is derived from PDETextView, it is probably the title/label of the Checkable and we can use
            // the delivered text as data.
            else if (view instanceof PDETextView) {
                setViewText((PDETextView) view, text);
                return true;
            }
            return false;
        }
        // views derived from PDEButton
        else if (view instanceof PDEButton) {
            // if the data is a boolean, it is meant to set the selected state of the button (radio button /checkbox button)
            if (data instanceof Boolean) {
                view.setSelected((Boolean) data);
                return true;
            }
            // if the data is a drawable, it is probably the icon of the button.
            else if (data instanceof Drawable) {
                ((PDEButton) view).setIcon((Drawable) data);
                return true;
            }
            // otherwise it is probably the text of the button.
            else {
                ((PDEButton) view).setText(text);
                return true;
            }
        }
        // PDETextView (or views derived from PDETextView)
        else if (view instanceof PDETextView) {
            ((PDETextView) view).setText(text);
            return true;
        }
        // PDEIconView (or views derived from PDEIconView)
        else if (view instanceof PDEIconView) {
            // data == resource ID
            if (data instanceof Integer) {
                setViewIcon((PDEIconView) view, (Integer) data);
                return true;
            }
            // data == icon character
            else {
                setViewIcon((PDEIconView) view, text);
                return true;
            }
        }
        // PDEPhotoFrameView (or views derived from PDEPhotoFrameView)
        else if (view instanceof PDEPhotoFrameView) {
            // a resource ID is delivered
            if (data instanceof Integer) {
                setViewPhotoFrame((PDEPhotoFrameView) view, (Integer) data);
                return true;
            }
            // a path is delivered
            else {
                setViewPhotoFrame((PDEPhotoFrameView) view, text);
                return true;
            }
        }
        // Delivered view is unknown; Couldn't bind it.
        return false;
    }


    /**
     * Called by bindView() to set the text for a PDETextView.
     *
     * @param v PDETextView to receive text
     * @param text the text to be set for the TextView
     */
    protected void setViewText(PDETextView v, String text) {
        v.setText(text);
    }


    /**
     * Called by bindView() to set the image for an PDEIconView.
     *
     * This method is called instead of {@link #setViewIcon(PDEIconView, String)}
     * if the supplied data is an int or Integer.
     *
     * @param v PDEIconView to receive an image
     * @param value the value retrieved from the data set
     *
     * @see #setViewIcon(PDEIconView, String)
     */
    protected void setViewIcon(PDEIconView v, int value) {
        v.setIconFromID(value);
    }


    /**
     * Called by bindView() to set the image for an PDEIconView.
     *
     * This method is called instead of {@link #setViewIcon(PDEIconView, int)}
     * if the supplied data is not an int or Integer.
     *
     * @param v PDEIconView to receive an image
     * @param value the value retrieved from the data set
     *
     * @see #setViewIcon(PDEIconView, int)
     */
    protected void setViewIcon(PDEIconView v, String value) {
        v.setIconString(value);
    }


    /**
     * Called by bindView() to set the image for an PDEPhotoFrameView.
     *
     * This method is called instead of {@link #setViewPhotoFrame(PDEPhotoFrameView, String)}
     * if the supplied data is an int or Integer.
     *
     * @param v PDEPhotoFrameView to receive an image
     * @param value the value retrieved from the data set
     *
     * @see #setViewPhotoFrame(PDEPhotoFrameView, String)
     */
    protected void setViewPhotoFrame(PDEPhotoFrameView v, int value) {
        v.setPhotoFromID(value);
    }


    /**
     * Called by bindView() to set the image for an PDEPhotoFrameView.
     *
     * This method is called instead of {@link #setViewPhotoFrame(PDEPhotoFrameView, int)}
     * if the supplied data is not an int or Integer.
     *
     * @param v PDEPhotoFrameView to receive an image
     * @param value the value retrieved from the data set
     *
     * @see #setViewPhotoFrame(PDEPhotoFrameView, int)
     */
    protected void setViewPhotoFrame(PDEPhotoFrameView v, String value) {
        v.setPictureString(value);
    }
}
