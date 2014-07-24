/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.buildingunits;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.R;




//----------------------------------------------------------------------------------------------------------------------
//  PDEBuildingUnits
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Helper class for handling Styleguide Building Units.
 *
 * Class calculates Pixel from Building Units und also holds some often used static members for this purpose. How many
 * pixels are in a building unit depends on the screen density. The intention is that a component / layout based on
 * Building Units has the same size on all devices.
 */
@SuppressWarnings("unused")
public class PDEBuildingUnits {

    // debug messages switch
    private final static boolean DEBUG_PARAMS = false;


    private final static String LOG_TAG = PDEBuildingUnits.class.getSimpleName();

    private static float BU;
    private static float BU_1_6;
    private static float BU_1_4;
    private static float BU_1_3;
    private static float BU_1_2;
    private static float BU_2_3;
    private static float BU_1_12;

    static {
        BU_1_6 = PDECodeLibrary.getInstance().getApplicationContext().getResources().getDimension(R.dimen.Telekom_BU_1_6);
        BU_1_4 = PDECodeLibrary.getInstance().getApplicationContext().getResources().getDimension(R.dimen.Telekom_BU_1_4);
        BU_1_3 = PDECodeLibrary.getInstance().getApplicationContext().getResources().getDimension(R.dimen.Telekom_BU_1_3);
        BU_1_2 = PDECodeLibrary.getInstance().getApplicationContext().getResources().getDimension(R.dimen.Telekom_BU_1_2);
        BU_2_3 = PDECodeLibrary.getInstance().getApplicationContext().getResources().getDimension(R.dimen.Telekom_BU_2_3);
        BU_1_12 = PDECodeLibrary.getInstance().getApplicationContext().getResources().getDimension(R.dimen.Telekom_BU_1_12);
        BU = PDECodeLibrary.getInstance().getApplicationContext().getResources().getDimension(R.dimen.Telekom_BU);
    }

    /**
     * @brief Convert BuildingUnits (BU) to pixels depending on the class of device, returns a directly calcualated value.
     *
     * Convert BUs to pixel.
     */
    public static float exactPixelFromBU (float bu) {
        // exact conversion
        return bu * BU;
    }


    /**
     * @brief Convert BuildingUnits (BU) to pixels depending on the class of device, aligned to pixels.
     *
     * Convert BUs to pixel.
     */
    public static int pixelFromBU (float bu) {
        // calculate exact and then round
        return (int) Math.floor(exactPixelFromBU(bu) + 0.5f);
    }


    /**
     * @brief Convert pixels to BuildingUnits (BU)
     */
    public static float buildingUnitsFromPixel(float px) {
        return px / BU;
    }


    /**
     * @brief Convenience function to get the pixels for 1 BU.
     *
     * @return pixels for 1-BU
     */
    public static int BU () {
        return Math.round(BU);
    }


    /**
     * @brief Convenience function to get the pixels for 1/3 BU.
     *
     * @return pixels for 1/3-BU
     */
    public static int oneThirdBU () {
        return Math.round(BU_1_3);
    }


    /**
     * @brief Convenience function to get the pixels for 1/2 BU.
     *
     * @return pixels for 1/2-BU
     */
    public static int oneHalfBU () {
        return Math.round(BU_1_2);
    }


    /**
     * @brief Convenience function to get the pixels for 2/3 BU.
     *
     * @return pixels for 2/3-BU
     */
    public static int twoThirdsBU () {
        return Math.round(BU_2_3);
    }


    /**
     * @brief Convenience function to get the pixels for 1/4 BU.
     *
     * @return pixels for 1/4-BU
     */
    public static int oneFourthBU () {
        return Math.round(BU_1_4);
    }

    /**
     * @brief Convenience function to get the pixels for 1/6 BU.
     *
     * @return pixels for 1/6-BU
     */
    public static int oneSixthBU () {
        return Math.round(BU_1_6);
    }


    /**
     * @brief Convenience function to get the pixels for 1/12 BU.
     *
     * @return pixels for 1/12-BU
     */
    public static int oneTwelfthsBU() {
        return Math.round(BU_1_12);
    }


    /**
     * @brief Convenience function to get the pixels for 1 BU.
     *
     * @return pixels for 1-BU
     */
    public static float exactBU () {
        return BU;
    }


    /**
     * @brief Convenience function to get the pixels for 1/3 BU.
     *
     * @return pixels for 1/3-BU
     */
    public static float exactOneThirdBU () {
        return BU_1_3;
    }


    /**
     * @brief Convenience function to get the pixels for 1/2 BU.
     *
     * @return pixels for 1/2-BU
     */
    public static float exactOneHalfBU () {
        return BU_1_2;
    }


    /**
     * @brief Convenience function to get the pixels for 2/3 BU.
     *
     * @return pixels for 2/3-BU
     */
    public static float exactTwoThirdsBU () {
        return BU_2_3;
    }


    /**
     * @brief Convenience function to get the pixels for 1/4 BU.
     *
     * @return pixels for 1/4-BU
     */
    public static float exactOneForthBU () {
        return BU_1_4;
    }


    /**
     * @brief Convenience function to get the pixels for 1/6 BU.
     *
     * @return pixels for 1/6-BU
     */
    public static float exactOneSixthBU () {
        return BU_1_6;
    }


    /**
     * @brief Convenience function to get the pixels for 1/12 BU.
     *
     * @return pixels for 1/12-BU
     */
    public static float exactOneTwelfthsBU() {
        return BU_1_12;
    }


    /**
     * @brief Check if the device is a tablet.
     *
     * The android screenLayout of configuration is evaluated. Every device with Configuration.SCREENLAYOUT_SIZE_LARGE
     * or bigger is considered as a tablet.
     *
     * @return true if the device is a tablet
     */
    public static boolean isTablet() {
        //we assume that we are on a tablet if the screen size is above large

        boolean tablet = false;

        Context appContext = PDECodeLibrary.getInstance().getApplicationContext();
        if (appContext != null) {
            int screenLayoutSizeMask = appContext.getResources().getConfiguration().screenLayout
                                            & Configuration.SCREENLAYOUT_SIZE_MASK;
            if (screenLayoutSizeMask == Configuration.SCREENLAYOUT_SIZE_NORMAL
                    || screenLayoutSizeMask == Configuration.SCREENLAYOUT_SIZE_SMALL
                    || screenLayoutSizeMask == Configuration.SCREENLAYOUT_SIZE_UNDEFINED
                    ) {
                tablet = false;

            } else if ( screenLayoutSizeMask == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                tablet = true;
             }else if (screenLayoutSizeMask == 4 ) {
                // Configuration.SCREENLAYOUT_SIZE_XLARGE == 4
                tablet = true;
            } else {
                //e.g. a new screen-layout size? XXL?
                tablet = true;
            }
        }
        return tablet;
    }
    

    //function is just dummy on android, always returns 1 !!! -> Don't use it!
    @SuppressWarnings("SameReturnValue")
    @Deprecated
    public static int nativePixel() {
        return 1;

    }


    // not needed in Android since Android uses always integer screen coordinates
    public static float exactRoundToScreenCoordinates(float coordinate) {
        return (float) Math.floor(coordinate + 0.5f);
    }


    /**
     * @brief Mathematically round to screen pixel coordinates.
     *
     * Pixels are always at whole numbers in android.
     *
     * @param coordinate size of coordinate as float value
     * @return rounded value of coordinate
     */
    public static int roundToScreenCoordinates(float coordinate) {
        return (int)Math.floor(coordinate + 0.5f);
    }


    /**
     * @brief Mathematically round up to screen pixel coordinates.
     *
     * @param coordinate size or coordinate as float value
     * @return ceil of the coordinate
     */
    public static int roundUpToScreenCoordinates(float coordinate) {
        return (int)Math.ceil(coordinate);
    }


    //----- parsing helpers --------------------------------------------------------------------------------------------


    /**
     * @brief Parse a string, may optionally contain a unit.
     */
    public static float parseSize(String sizeString) {
        float size = Float.NaN;
        int endOfFloatIndex;

        DisplayMetrics metrics = PDECodeLibrary.getInstance().getApplicationContext().getResources().getDisplayMetrics();
        Pattern p = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
        Matcher m = p.matcher(sizeString);

        if (m.find()) {
            if (DEBUG_PARAMS){
                Log.d(LOG_TAG,
                      "start " + m.start(0) + " " + m.end(0) + " " + sizeString.substring(m.start(0), m.end(0)));
            }
            if (m.start() == 0) {
                // float only at the beginning
                size = Float.valueOf(sizeString.substring(m.start(), m.end()));
                endOfFloatIndex = m.end();

                if (!Float.isNaN(size) && endOfFloatIndex > -1 && endOfFloatIndex < sizeString.length()) {
                    String unitPart = sizeString.substring(endOfFloatIndex);
                    if (unitPart.compareToIgnoreCase("BU") == 0) {
                        // convert caps height (defined in BU) in real font size
                        size = PDEBuildingUnits.exactPixelFromBU(size);
                    } else if (unitPart.compareToIgnoreCase("px") == 0) {
                        size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size, metrics);
                    } else if (unitPart.compareToIgnoreCase("dp") == 0 || unitPart.compareToIgnoreCase("dip") == 0) {
                        size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, metrics);
                    } else if (unitPart.compareToIgnoreCase("sp") == 0) {
                        size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, metrics);
                    } else if (unitPart.compareToIgnoreCase("dt") == 0) {
                        size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, size, metrics);
                    } else if (unitPart.compareToIgnoreCase("in") == 0) {
                        size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, size, metrics);
                    } else if (unitPart.compareToIgnoreCase("mm") == 0) {
                        size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, size, metrics);
                    }
                }
            } else if (m.start() == 1 && sizeString.startsWith("@")) {
                // a resource
                size = PDECodeLibrary.getInstance().getApplicationContext().getResources()
                                     .getDimensionPixelSize(Integer.parseInt(sizeString.substring(m.start(), m.end())));
            }
        }

        return size;
    }
}


