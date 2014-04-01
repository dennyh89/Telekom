/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 *
 * kdanner - 21.06.13 : 14:28
 */

package de.telekom.pde.codelibrary.ui.helpers;


import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


//----------------------------------------------------------------------------------------------------------------------
//  PDEUtils
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Class which collects functions which are more often used.
 *
 */
@SuppressWarnings("unused")
public class PDEUtils {
    private final static String LOG_TAG = PDEUtils.class.getName();

    private final static float	GOLDEN_RATIO_PERCENTAGE	= 0.61803398875f;


    /**
     * @brief Set a drawable as background to a view using reflection.
     * The setBackground or setBackgroundDrawable do basically the same, but in order to avoid deprecation warnings we
     * call the by reflection
     * @param view which background shall be drawn by the drawable
     * @param drawable for the background
     */
    public static void setViewBackgroundDrawable(final View view, final Drawable drawable) {
        Method method;

        // valid?
        if (view == null) return;

        try {
            //try to use the setBackground function which was introduced in android 4.1 (api level 16)
            method = view.getClass().getMethod("setBackground", new Class[] {Drawable.class});
            method.invoke(view, drawable);
            return;
        } catch (NoSuchMethodException e) {
            // function not available
        } catch (IllegalAccessException e) {
            // function not available
        } catch (InvocationTargetException e) {
            // function not available
        }

        // setBackground function not found -> use old one (setBackgroundDrawable)
        try {
            //try to use the setBackgroundDrawable which is deprecated in android 4.1
            method = view.getClass().getMethod("setBackgroundDrawable", new Class[] {Drawable.class});
            method.invoke(view, drawable);
            return;
        } catch (NoSuchMethodException e) {
            // function not available
        } catch (IllegalAccessException e) {
            // function not available
        } catch (InvocationTargetException e) {
            // function not available
        }

        // everything goes wrong -> SHOULD NOT HAPPEN
        Log.e(LOG_TAG, "there is no setBackground or setBackgroundDrawable function for this object:"
                + view.getClass().toString());
    }


    /**
     * @brief API-Version save setAlpha function.
     * View.setAlpha is only in API Version 11+; for Versions before this it uses an AlphaAnimation.
     * @param view View to be changed
     * @param alpha Alpha value to be set
     */
    @SuppressLint("NewApi")
    public static void setViewAlpha(View view, float alpha) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setAlpha(alpha);
        } else {
            AlphaAnimation alphaAnim = new AlphaAnimation(alpha, alpha);
            alphaAnim.setDuration(0); // Make animation instant
            alphaAnim.setFillAfter(true); // Tell it to persist after the animation ends
            view.startAnimation(alphaAnim);
        }

    }


    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static Point extractDisplayDimension(final Display display) {
        final Point dimension = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(dimension);
        } else {
            dimension.x = display.getWidth();
            dimension.y = display.getHeight();
        }

        return dimension;
    }


    public static Display getDisplay(final Context context)  {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }


    private static float getGoldenRatioYFor(final View view, final Display display)  {
        final int screenHeight = getScreenHeight(display);
        final float goldenRatioHeight = screenHeight * GOLDEN_RATIO_PERCENTAGE;

        float newYPosition = screenHeight - goldenRatioHeight;

        view.measure(0, 0);
        newYPosition -= view.getMeasuredHeight() * 0.5;

        return Math.max(newYPosition, 0);
    }


    private static int getScreenHeight(final Display display)  {
        final Point dimension = extractDisplayDimension(display);
        return dimension.y;
    }


    public static Point getDisplayDimension(final Context context) {
        return extractDisplayDimension(getDisplay(context));
    }


    /**
     *
     * @return the following value depending on the current orientation:
     *         Configuration.ORIENTATION_SQUARE, Configuration.ORIENTATION_PORTRAIT or Configuration.ORIENTATION_LANDSCAPE
     */
    @SuppressWarnings("deprecation")
    private static int getScreenOrientation(final Display display)  {
        final Point dimension = extractDisplayDimension(display);
        int orientation;

        if (dimension.x == dimension.y) {
            orientation = Configuration.ORIENTATION_SQUARE;
        } else {
            if (dimension.x < dimension.y) {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            } else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }


    /**
     * Sets the y position of the given view to the golden ratio depending on the screen resolution and orientation
     * (landscape/portrait)
     *
     * @param view
     *            to set the golden ratio to. Note: the parent of this view has to be a RelativeLayout(!)
     * @param display
     *            needed to get screen dimensions
     * @return the modified view which was given as parameter
     */
    public static View setGoldenRatioTo( final View view, final Display display) {
        if(view == null)  throw new NullPointerException("view is NULL!!!");
        if(display == null)  throw new NullPointerException("display is NULL!!!");

        if (view.getParent() == null) {
            throw new IllegalStateException("parent of view is no RelativeLayout");
        }
        if(!(RelativeLayout.class.isAssignableFrom(view.getParent().getClass()))) {
            throw new IllegalStateException("parent of view is no RelativeLayout");
        }

        final float newYPosition = getGoldenRatioYFor(view, display);

        final ViewGroup.LayoutParams source = view.getLayoutParams();

        if (source == null) {
            throw new IllegalStateException("no layout");
        }

        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(source);

        params.topMargin = (int) newYPosition;
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        view.setLayoutParams(params);
        return view;
    }


    /**
     * @brief Get status bar height bei status bar resource dimension.
     * @param context Context
     * @return height of status bar in pixel.
     */
    public static int getStatusBarHeight(final Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * @brief Fetches dynamically a predefined String from the android resources.
     *
     * Fetches a distinct String out of the various string*.xml files of the Android resources. You have to deliver the
     * name of the String. This function is especially useful if you don't know the name of the wanted String at
     * compile-time and have to fetch it dynamically at runtime.
     *
     * @param name name of the desired String
     * @return the desired String.
     */
    public static String loadStringFromResources(String name){
        Context c;
        int id;
        String result;

        if (PDEString.isEmpty(name)) return "";

        // get application context
        c = PDECodeLibrary.getInstance().getApplicationContext();

        // resolve id
        id = c.getResources().getIdentifier(name, "string", c.getPackageName() );
        // get string for id
        if (id == 0) result = "";
        else result = c.getResources().getString(id);

        return result;
    }
}
