/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.lists.viewbinders;

import android.annotation.SuppressLint;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.CursorWrapper;
import android.os.Build;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;

import java.lang.reflect.Field;

import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDEIconView;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDETextView;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.metaphors.PDEPhotoFrameView;


//----------------------------------------------------------------------------------------------------------------------
// PDESimpleCursorAdapterViewBinder
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Custom View Binder which enables the use of various PDE components for a SimpleCursorAdapter.
 *
 * A standard Android SimpleCursorAdapter can only handle some default Android UI components. But the
 * SimpleCursorAdapter offers the possibility to use custom ViewBinders. With this ViewBinder interface it is possible
 * to add further UI components and to determine how they will be filled with data. So this ViewBinder handles
 * PDE UI components which are commonly used within list element layouts.
 *
 * Recommendation: The PDESimpleCursorAdapter automatically makes use of this ViewBinder. It comes along with some list
 * element highlighting logic in the style of the styleguide. So we recommend to simply use the PDESimpleCursorAdapter.
 */
public class PDESimpleCursorAdapterViewBinder implements SimpleCursorAdapter.ViewBinder {
    // Hint: Since Honeycomb, the Android cursor class offers a quite useful support for the data types of the columns
    // of the database. It offers type constants and a practical getType(columnIndex) function which returns the type of
    // a distinct column. Since we have to support SDKs before Honeycomb, we had to implement our own version of
    // getType() which is called when the app runs on an older SDK.

    // The same constants like the ones Cursor uses since Honeycomb
    static final int FIELD_TYPE_NULL = 0;
    static final int FIELD_TYPE_INTEGER = 1;
    static final int FIELD_TYPE_FLOAT = 2;
    static final int FIELD_TYPE_STRING = 3;
    static final int FIELD_TYPE_BLOB = 4;


    /**
     * @brief Binds the Cursor column defined by the specified index to the specified view.
     *
     * When binding is handled by this ViewBinder, this method must return true.
     * If this method returns false, SimpleCursorAdapter will attempts to handle
     * the binding on its own.
     *
     * @param view the view to bind the data to
     * @param cursor the cursor to get the data from
     * @param columnIndex the column at which the data can be found in the cursor
     *
     * @return true if the data was bound to the view, false otherwise
     */
    @SuppressLint("NewApi") // For the problematic function we provide an alternative when API lower than honeycomb
    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        String text;
        int data;
        int type;

        // check the current SDK version and take the built-in getView method when we're above Honeycomb or our own
        // version otherwise
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // built-in getView()
            type = cursor.getType(columnIndex);
        } else {
            // our own getView()
            type = getType(cursor, columnIndex);
        }
        text = "";
        data = 0;

        // check if the current column has a type which we can handle.
        if (type == Cursor.FIELD_TYPE_STRING) {
            text = cursor.getString(columnIndex);
            if (text == null) text = "";
        } else if (type == Cursor.FIELD_TYPE_INTEGER) {
            data = cursor.getInt(columnIndex);
        } else {
            // ToDo: really good idea to skip all other types?
            return false;
        }

        // views derived from Checkable
        if (view instanceof Checkable) {
            // ToDo: Don't know if this is really correct for checkable
            // If the view is derived from PDETextView, it is probably the title/label of the Checkable and we can use
            // the delivered string value as data.
            if (view instanceof PDETextView && type == FIELD_TYPE_STRING) {
                setViewText((PDETextView) view, text);
                return true;
            }
        }
        // views derived from PDEButton
        else if (view instanceof PDEButton) {
            // since we have no boolean type for a column, we assume that an integer with 0 means false and an
            // integer != 0 means true and is used to determine the selected state of the button
            // (radio button / checkbox)
            if (type == Cursor.FIELD_TYPE_INTEGER) { // really good idea?
                view.setSelected(data != 0);
                return true;
            }
            // if it is no integer type, it is a string type and it is probably the title/label of the button.
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
            // delivered data is a resource ID
            if (type == FIELD_TYPE_INTEGER) {
                setViewIcon((PDEIconView) view, data);
                return true;
            }
            // delivered data is a icon character
            else {
                setViewIcon((PDEIconView) view, text);
                return true;
            }
        }
        // PDEPhotoFrameView (or views derived from PDEPhotoFrameView)
        else if (view instanceof PDEPhotoFrameView) {
            // a resource ID is delivered
            if (type == FIELD_TYPE_INTEGER) {
                setViewPhotoFrame((PDEPhotoFrameView) view, data);
                return true;
            }
            // a path is delivered
            else {
                setViewPhotoFrame((PDEPhotoFrameView) view, text);
                return true;
            }
        }
        // Delivered view is unknown or the data type is unusable; Couldn't bind view.
        return false;
    }


    /**
     * @brief Returns the data type of the given database column.
     *
     * This is the alternative getView() implementation for SDKs lower than Honeycomb.
     *
     * @param cursor the database cursor
     * @param columnIndex the index of the column we're interested in
     *
     * @return type id of the data type.
     */
    @SuppressWarnings("deprecation")
    protected int getType(Cursor cursor, int columnIndex) {
        Field mCursor;
        AbstractWindowedCursor abstractWindowedCursor;
        CursorWrapper cw = (CursorWrapper) cursor;

        Class<?> cursorWrapper = CursorWrapper.class;
        try {
            // get the cursor data field of the cursor wrapper class.
            mCursor = cursorWrapper.getDeclaredField("mCursor");
        } catch (NoSuchFieldException e) {
            Log.d("PDESimpleCursorAdapterViewBinder", "Couldn't find field!");
            return FIELD_TYPE_NULL; // correct?
        }
        // make the cursor field accessible (it's private by default)
        mCursor.setAccessible(true);
        try {
            abstractWindowedCursor = (AbstractWindowedCursor) mCursor.get(cw);
        } catch (IllegalAccessException e) {
            Log.d("PDESimpleCursorAdapterViewBinder", "Access prohibited!");
            return FIELD_TYPE_NULL;
        }
        // get cursor window
        CursorWindow cursorWindow = abstractWindowedCursor.getWindow();
        // get the current position of the cursor in the row set
        int pos = abstractWindowedCursor.getPosition();
        // check all available types, return type ID when the correct type was found
        if (cursorWindow.isNull(pos, columnIndex)) {
            return FIELD_TYPE_NULL;
        } else if (cursorWindow.isLong(pos, columnIndex)) {
            return FIELD_TYPE_INTEGER;
        } else if (cursorWindow.isFloat(pos, columnIndex)) {
            return FIELD_TYPE_FLOAT;
        } else if (cursorWindow.isString(pos, columnIndex)) {
            return FIELD_TYPE_STRING;
        } else if (cursorWindow.isBlob(pos, columnIndex)) {
            return FIELD_TYPE_BLOB;
        }
        return FIELD_TYPE_NULL;
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
