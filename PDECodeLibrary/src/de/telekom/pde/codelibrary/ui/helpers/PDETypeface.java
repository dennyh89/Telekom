/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.helpers;

// imports


//----------------------------------------------------------------------------------------------------------------------
//  PDETypeface
//----------------------------------------------------------------------------------------------------------------------

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.util.LinkedHashMap;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;

/**
 * @brief Helper class to have additional information to android typeface.
 *
 */
public class PDETypeface {
    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDETypeface.class.getName();
    private final static boolean DEBUG_CACHING = false;

    private class TypefaceInfo {
        public float fontAscenderHeight;
        @SuppressWarnings("unused")
		public float fontTopHeight;
        public float fontCapHeight;
        public float fontOverallHeight;
        public float fontTopOverallHeight;

        /**
         * @brief Public constructor.
         */
        public TypefaceInfo() {
            init();
        }


        /**
         * @brief Private init function to set the default values.
         */
        private void init() {
            // call reset to set default values
            reset();
        }


        /**
         * @brief Reset the TextInfo to the default values.
         */
        public void reset() {
            fontAscenderHeight = 0.0f;
            fontTopHeight = 0.0f;
            fontCapHeight = 0.0f;
            fontOverallHeight = 0.0f;
            fontTopOverallHeight = 0.0f;
        }
    }

    //variables
    private String mFilename = "";
    private Typeface mTypeface = null;

    public final static String sAssetFontFolderPath = "fonts/";
    // PDEDefaultFont is the TeleGrotesk font
    public final static PDETypeface sDefaultFont;
    public final static PDETypeface sDefaultNormal;
    public final static PDETypeface sDefaultBold;
    public final static PDETypeface sDefaultSemiBold;
    public final static PDETypeface sDefaultUltra;
    public final static PDETypeface sIconFont;

    public final static float sTeleGroteskDefaultSize;
    public final static float sOtherFontsDefaultSize;

    /** An <code>LruCache</code> for previously loaded typefaces. */
    private static final LinkedHashMap<String, PDETypeface> mTypefaceCache;

    private SparseArray<Float> mCapHeightToSizeCache;
    private SparseArray<TypefaceInfo> mTypefaceInfoForSizeCache;

    private final static int TELE_GROTESK_STRINGRESOURCE_ID = R.array.tele_grotesk;


    // static initialize
    static {
        // load default font
        Context c = PDECodeLibrary.getInstance().getApplicationContext();
        mTypefaceCache = new LinkedHashMap<String, PDETypeface>();
        PDETypeface tempTypeface;

        try {
            // load default (TeleGrotesk) font from the assets
            // the font must be with the right name in the asset in the "font"-folder of the "user" project
            tempTypeface = PDETypeface.createFromAsset(PDEConstants.sPDEDefaultFontName);
        } catch (Exception e) {
            tempTypeface = null;
        }

        // we were not able to load the TeleGrotesk font! -> initialize with default system font
        if (tempTypeface == null) {
            tempTypeface = PDETypeface.createByNameAndTypeface(Typeface.DEFAULT.toString(), Typeface.DEFAULT);
        }

        sDefaultNormal = tempTypeface;
        sDefaultFont = sDefaultNormal;
        sDefaultSemiBold = tempTypeface;

        try {
            // load default (TeleGrotesk) font from the assets
            // the font must be with the right name in the asset in the "font"-folder of the "user" project
            tempTypeface = PDETypeface.createFromAsset(c.getResources().getString(R.string.Tele_GroteskUlt));
        } catch (Exception e) {
            tempTypeface = null;
        }

        // we were not able to load the TeleGrotesk font! -> initialize with default system font
        if (tempTypeface == null) {
            tempTypeface = PDETypeface.createByNameAndTypeface(Typeface.DEFAULT_BOLD.toString(), Typeface.DEFAULT_BOLD);
        }
        sDefaultUltra = tempTypeface;
        sDefaultBold = tempTypeface;

        sTeleGroteskDefaultSize = PDECodeLibrary.getInstance().getApplicationContext().getResources().
                getDimension(R.dimen.TeleGroteskDefaultSize);
        sOtherFontsDefaultSize = PDECodeLibrary.getInstance().getApplicationContext().getResources().
                getDimension(R.dimen.OtherFontsDefaultSize);


        try {
            // load default (TeleGrotesk) font from the assets
            // the font must be with the right name in the asset in the "font"-folder of the "user" project
            tempTypeface = PDETypeface.createFromAsset(c.getResources().getString(R.string.Tele_Iconfont));
        } catch (Exception e) {
            tempTypeface = null;
        }

        // we were not able to load the TeleGrotesk font! -> initialize with default system font
        if (tempTypeface == null) {
            tempTypeface = sDefaultFont;
        }

        sIconFont = tempTypeface;

        // init the capHeight-cache with some default values
        sDefaultFont.querySizeForCapHeight(PDEBuildingUnits.BU());
        sDefaultFont.querySizeForCapHeight(sTeleGroteskDefaultSize);

        sDefaultSemiBold.querySizeForCapHeight(PDEBuildingUnits.BU());
        sDefaultSemiBold.querySizeForCapHeight(sTeleGroteskDefaultSize);

        sDefaultBold.querySizeForCapHeight(PDEBuildingUnits.BU());
        sDefaultBold.querySizeForCapHeight(sTeleGroteskDefaultSize);
    }


    /**
     * @brief constructor to create PDETypeFace object with name and android typeface object
     * Only the filename is saved, not the complete path
     * @param typefaceName name of the font
     * @param typeface android typeface-object containing the filename
     */
    private PDETypeface(String typefaceName, Typeface typeface) {
        init(typefaceName, typeface);
    }


    /**
     * @brief init function to create PDETypeFace object with name and android typeface object
     * Only the filename is saved, not the complete path
     * @param filePath name of the font
     * @param typeface android typeface-object belongs to the filename
     */
    private void init(String filePath, Typeface typeface) throws NullPointerException {
        if (filePath == null || typeface == null) throw new NullPointerException();
        mFilename = getFilename(filePath);
        mTypeface = typeface;
        mCapHeightToSizeCache = new SparseArray<Float>();
        mTypefaceInfoForSizeCache = new SparseArray<TypefaceInfo>();
        PDETypeface.saveTypeface(this, filePath);
    }


    static private void saveTypeface(PDETypeface typeface, String name) {
        mTypefaceCache.put(name, typeface);
    }


    static private PDETypeface loadTypeface(String name) {
        return mTypefaceCache.get(name);
    }


    /**
     * @brief static function to create PDETypeFace object, from a file by the pathname
     * Only the filename is saved, not the complete path
     * @param path pathname of the font
     * @return created PDETypeface object or null if there is no font at the path
     */
    @SuppressWarnings("unused")
    static public PDETypeface createFromFile(String path) {
        return createFromFile(path, true);
    }


    /**
     * @brief static function to create PDETypeFace object, from a file by the pathname
     * Only the filename is saved, not the complete path
     * @param filePath pathname of the font
     * @param showExceptionMessage show exception message to inform user
     * @return created PDETypeface object or null if there is no font at the path
     */
    static private PDETypeface createFromFile(String filePath, boolean showExceptionMessage) {
        PDETypeface newFont;
        try {
            newFont = PDETypeface.loadTypeface(filePath);
            if (newFont == null) {
                newFont = new PDETypeface(filePath, Typeface.createFromFile(filePath));
            }
            return newFont;
        } catch (Exception exception) {
            if (showExceptionMessage) {
                Log.e(LOG_TAG, "Error in:createFromFile(String path)");
                exception.printStackTrace();
            }
            return null;
        }
    }


    /**
     * @brief static function to create PDETypeFace object, from a "assets/fonts" folder by its name
     * Only the filename is saved, not the complete path
     * @param filename name of the font
     * @return created PDETypeface object or null if there is no font at the path
     */
    static public PDETypeface createFromAsset(String filename) {
        return createFromAsset(filename, true);
    }


    /**
     * @brief static function to create PDETypeFace object, from a "assets/fonts" folder by its name
     * Only the filename is saved, not the complete path
     * @param filename name of the font
     * @param showExceptionMessage show exception message to inform user
     * @return created PDETypeface object or null if there is no font at the path
     */
    static private PDETypeface createFromAsset(String filename, boolean showExceptionMessage) {
        String filePath = sAssetFontFolderPath + filename;
        PDETypeface newFont;
        try {
            newFont = PDETypeface.loadTypeface(filePath);
            if (newFont == null) {
                newFont = new PDETypeface(filePath,
                                          Typeface.createFromAsset(PDECodeLibrary.getInstance()
                                                                                 .getApplicationContext()
                                                                                 .getAssets(),
                                                                   filePath)
                );
            }
            return newFont;
        } catch (Exception exception) {
            if (showExceptionMessage) {
                Log.e(LOG_TAG, "##########################################");
                Log.e(LOG_TAG, "Error in:createFromAsset(" + filename + ")");
                Log.e(LOG_TAG, "##########################################");
                exception.printStackTrace();
            }
            return null;
        }
    }


    /**
     * @brief static function to create PDETypeface object, by its name and give android typeface
     * Only the filename is saved, not the complete path
     * @param name name of the font
     * @param typeface android typeface belongs to the name
     * @return created PDETypeface object or null if there is no font at the path
     */
    static public PDETypeface createByNameAndTypeface(String name, Typeface typeface) {
        PDETypeface newFont;
        try {
            newFont = PDETypeface.loadTypeface(name);
            if (newFont == null) {
                newFont = new PDETypeface(name, typeface);
            }
            return newFont;
        } catch (Exception exception) {
            Log.e(LOG_TAG, "Error in:createByNameAndTypeface(String name, Typeface typeface)");
            exception.printStackTrace();
            return null;
        }
    }


    /**
     * @brief Loads a font with the specified name.
     * 
     * First tries to create it from file if this goes wrong -> try to get it from assets
     * Only the filename is saved, not the complete path
     * 
     * @param name Font name
     * @return the created PDETypeFace or null if there was no font in asses or the path
     */
    static public PDETypeface createByName(String name) {
        PDETypeface newFont;

        // try to load font
        newFont = PDETypeface.createFromFile(name, false);

        // able to load
        if (newFont != null) {
            return newFont;
        }

        // try to load from assets
        newFont = PDETypeface.createFromAsset(name, false);

        // able to load
        if (newFont != null) {
            return newFont;
        }

        Log.e(LOG_TAG,
              "Error in:createByName(String name)\n can't create a font by name!!!! Normally the default font is now used!!!!!!");

        return null;
    }


    /**
     * @brief get the android typeface object
     * @return Typeface object or null;
     */
    public Typeface getTypeface() {
        return mTypeface;
    }


    /**
     * @brief get the name of the PDETypeFace object
     * @return name or null;
     */
    public String getName() {
        return mFilename;
    }


    /**
     * @brief helper class to get only the filename out of the complete path
     * @return filename;
     */
    private String getFilename(String path) throws NullPointerException {
        if (path == null) throw new NullPointerException();

        int folderIndex = path.lastIndexOf('/');
        return path.substring(folderIndex + 1);
    }


    /**
     * @brief Checks if a font is in TeleGrotesk family.
     * Caution: throws exception if there is no TeleGrotesk array resource!!!
     * @return true if its a TeleGrotesk font, else false
     */
    public boolean isTeleGroteskFont() throws Resources.NotFoundException {
        String[] telegroteskFonts;
        int i;

        //get teleGrotesk array from resource
        Context context = PDECodeLibrary.getInstance().getApplicationContext();
        Resources res = context.getResources();
        telegroteskFonts = res.getStringArray(TELE_GROTESK_STRINGRESOURCE_ID);

        if (telegroteskFonts != null) {
            // check if font is contained
            for (i = 0; i < telegroteskFonts.length; i++) {
                if (TextUtils.equals(telegroteskFonts[i], getName())) {
                    //part of the TeleGrotesk family
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * @brief Tries to get the readable name of the font (at the moment declared in font_families.xml file).
     * Caution: throws exception if there is no TeleGrotesk array resource!!!
     * @return readable name if exists in array, else the filename
     */
    @SuppressWarnings("unused")
    public String getReadableName() throws Resources.NotFoundException {
        return getReadableName(TELE_GROTESK_STRINGRESOURCE_ID);
    }


    /**
     * @brief Tries to get the readable name of the font (declared in the resourceArrayIdentifier parameter).
     * Caution: throws exception if there is no string array resource!!!
     * @param resourceArrayIdentifier the id of the array in the resource, where we have to look for the name
     * @return readable name if exists in array, else the filename
     */
    private String getReadableName(int resourceArrayIdentifier) throws Resources.NotFoundException {
        TypedArray fontsArray;
        int i;
        int id;

        //get array from resource
        Context context = PDECodeLibrary.getInstance().getApplicationContext();
        Resources res = context.getResources();
        fontsArray = res.obtainTypedArray(resourceArrayIdentifier);

        if (fontsArray != null) {
            //check if font is contained
            for (i = 0; i < fontsArray.length(); i++) {
                if (TextUtils.equals(fontsArray.getText(i), getName())) {
                    id = fontsArray.getResourceId(i, 0);
                    if (id != 0) {
                        fontsArray.recycle();
                        return res.getResourceEntryName(id);
                    } else {
                        //break and return normal filename
                        break;
                    }
                }
            }
            fontsArray.recycle();
        }
        return getName();
    }


    /**
     * @brief Get the font size to a for a requested capHeight.
     * @param wantedCapHeight the capHeight you want (e.g. 1BU)
     * @return The font size for this capHeight.
     */
    public float querySizeForCapHeight(float wantedCapHeight) {
        float size = wantedCapHeight * 1.5f;
        float dif;
        float capHeight;
        int wantedCapHeightInt = (int) Math.floor(wantedCapHeight * 100);

        Object result = mCapHeightToSizeCache.get(wantedCapHeightInt);

        if (result == null) {
            if (DEBUG_CACHING) {
                Log.d(LOG_TAG, "querySizeForCapHeight (" + wantedCapHeight + ") no cache value found ("
                               + getName() + ")");
            }

            if (wantedCapHeight == 0.0f) {
                return 0.0f;
            }

            // iterate as long as an appropriate value is found
            for (int i = 0; i < 50; i++) {
                // calc size
                capHeight = getCapHeight(size);
                // calc distance
                dif = Math.abs(capHeight - wantedCapHeight);

                // check dif getting taller
                if (dif < 0.1f) {
                    // we have our solution
                    //Log.d(LOG_TAG,"calculateFontSize found");
                    break;
                }
                // change size depending if the returned value was to big or to small
                if (capHeight - wantedCapHeight > 0) {
                    // update the tested value
                    size -= 0.1f;
                } else {
                    size += 0.1f;
                }

            }
            //Log.d(LOG_TAG,"calculateFontSize "+wantedCapHeight+" -> "+capHeight+" = size "+size);

            mCapHeightToSizeCache.append(wantedCapHeightInt, size);
        } else {
            size = (Float) result;
            if (DEBUG_CACHING) {
                Log.d(LOG_TAG, "querySizeForCapHeight (" + wantedCapHeight + ") -> " + size + " ("
                               + getName() + ")");
            }
        }

        return size;
    }


    /**
     * @brief Get the height of the bounding rect for a caption D - equals CapHeight.
     * The result will be cached and reused.
     */
    public float getCapHeight(float fontSize) {
        TypefaceInfo tfi = getTypefaceInfo(fontSize);

        if (tfi == null) return 0f;

        return tfi.fontCapHeight;
    }


    public float getOverallHeight(float fontSize) {
        TypefaceInfo tfi = getTypefaceInfo(fontSize);

        if (tfi == null) return 0f;

        return tfi.fontOverallHeight;
    }


    public float getTopOverallHeight(float fontSize) {
        TypefaceInfo tfi = getTypefaceInfo(fontSize);

        if (tfi == null) return 0f;

        return tfi.fontTopOverallHeight;
    }


    public float getAscenderHeight(float fontSize) {
        TypefaceInfo tfi = getTypefaceInfo(fontSize);

        if (tfi == null) return 0f;

        return tfi.fontAscenderHeight;
    }



    private TypefaceInfo getTypefaceInfo(float fontSize) {
        if (fontSize == 0f) {
            return null;
        }
        TypefaceInfo result;
        int key;

        key = (int) Math.floor(fontSize * 100);
        result = mTypefaceInfoForSizeCache.get(key);

        if (result == null) {
            result = new TypefaceInfo();

            result.fontCapHeight = measureCapHeight(fontSize);
            result.fontTopHeight = PDEFontHelpers.getPixelsAboveBaseLine("ÄÂ", this, fontSize);
            result.fontAscenderHeight = PDEFontHelpers.getPixelsBelowBaseLine("gp", this, fontSize);
            result.fontOverallHeight = PDEFontHelpers.getHeight(this, fontSize);
            result.fontTopOverallHeight = PDEFontHelpers.getTopHeight(this, fontSize);

            mTypefaceInfoForSizeCache.append(key, result);
        }

        return result;
    }


    /**
     * @brief Private function to getTextBound for a capital D - which is equal CapHeight.
     * @param textSize the font size which should be evaluated.
     * @return the size of CapHeight of size font-size.
     */
    private float measureCapHeight(float textSize) {
        Rect rect = PDEFontHelpers.getTextViewBounds("D", mTypeface, textSize);
        return rect.height();
    }

}
