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

import java.util.LinkedHashMap;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.R;

/**
 * @brief Helper class to have additional information to android typeface.
 *
 */
public class PDETypeface {
    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDETypeface.class.getName();

    //variables
    private String mFilename = "";
    private Typeface mTypeface = null;

    public final static String sAssetFontFolderPath = "fonts/";
    // PDEDefaultFont is teleGrotesk font
    public static PDETypeface sDefaultFont;
    public final static float sTeleGroteskDefaultSize;
    public final static float sOtherFontsDefaultSize;

    /** An <code>LruCache</code> for previously loaded typefaces. */
    private static LinkedHashMap<String, PDETypeface> mTypefaceCache;

    private static int TELE_GROTESK_STRINGRESOURCE_ID = R.array.tele_grotesk;

    // static initialize
    static {
        // load default font
        Context c = PDECodeLibrary.getInstance().getApplicationContext();
        mTypefaceCache = new LinkedHashMap<String, PDETypeface>();
        sDefaultFont=null;
        try {
            // load default (tele grotesk) font from the assets
            // the font must be with the right name in the asset in the "font"-folder of the "user" project
            sDefaultFont = PDETypeface.createFromAsset(PDEConstants.sPDEDefaultFontName) ;
        } catch (Exception e) {
            sDefaultFont=null;
        }

        // we were not able to load the tele grotesk font! -> initialize with default system font
        if (sDefaultFont == null) {
            sDefaultFont = PDETypeface.createByNameAndTypeface(Typeface.DEFAULT.toString(), Typeface.DEFAULT);
        }

        sTeleGroteskDefaultSize = PDECodeLibrary.getInstance().getApplicationContext().getResources().
                getDimension(R.dimen.TeleGroteskDefaultSize);
        sOtherFontsDefaultSize  = PDECodeLibrary.getInstance().getApplicationContext().getResources().
                getDimension(R.dimen.OtherFontsDefaultSize);


    }


    /**
     * @brief constructro to create dttypeface object with name and android typeface object
     * Only the filename is saved, not the complete path
     * @param typefaceName name of the font
     * @param typeface android typefaceobject containing to the filename
     */
    private PDETypeface(String typefaceName, Typeface typeface) {
        init(typefaceName,typeface);
    }


    /**
     * @brief init function to create dttypeface object with name and android typeface object
     * Only the filename is saved, not the complete path
     * @param filepath name of the font
     * @param typeface android typefaceobject belongs to the filename
     */
    private void init(String filepath, Typeface typeface) throws NullPointerException{
        if( filepath==null || typeface==null) throw new NullPointerException();
        mFilename = getFilename(filepath);
        mTypeface = typeface;
        PDETypeface.saveTypeface(this,filepath);
    }


    static private void saveTypeface(PDETypeface typeface, String name) {
        mTypefaceCache.put(name,typeface);
    }

    static private PDETypeface loadTypeface(String name) {
        return mTypefaceCache.get(name);
    }

    /**
     * @brief static function to create dttypeface object, from a file by the pathname
     * Only the filename is saved, not the complete path
     * @param path pathname of the font
     * @return created PDETypeface object or null if there is no font at the path
     */
    static public PDETypeface createFromFile(String path){
        return createFromFile(path,true);
    }


    /**
     * @brief static function to create dttypeface object, from a file by the pathname
     * Only the filename is saved, not the complete path
     * @param filepath pathname of the font
     * @param showExceptionMessage show exception message to inform user
     * @return created PDETypeface object or null if there is no font at the path
     */
    static private PDETypeface createFromFile(String filepath, boolean showExceptionMessage){
        PDETypeface newFont = null;
        try {
            newFont = PDETypeface.loadTypeface(filepath);
            if(newFont==null) {
                newFont = new PDETypeface(filepath,Typeface.createFromFile(filepath));
            }
            return newFont;
        } catch(Exception exception){
            if(showExceptionMessage) {
                Log.e(LOG_TAG,"Error in:createFromFile(String path)");
                exception.printStackTrace();
            }
            return null;
        }
    }


    /**
     * @brief static function to create dttypeface object, from a "assets/fonts" folder by its name
     * Only the filename is saved, not the complete path
     * @param filename name of the font
     * @return created PDETypeface object or null if there is no font at the path
     */
    static public PDETypeface createFromAsset(String filename){
       return createFromAsset(filename,true);
    }


    /**
     * @brief static function to create dttypeface object, from a "assets/fonts" folder by its name
     * Only the filename is saved, not the complete path
     * @param filename name of the font
     * @param showExceptionMessage show exception message to inform user
     * @return created PDETypeface object or null if there is no font at the path
     */
    static private PDETypeface createFromAsset(String filename, boolean showExceptionMessage){
        String filepath = sAssetFontFolderPath+filename;
        PDETypeface newFont = null;
        try {
            newFont = PDETypeface.loadTypeface(filepath);
            if(newFont==null) {
                newFont = new PDETypeface(filename,Typeface.createFromAsset(PDECodeLibrary.getInstance().getApplicationContext().getAssets(),filepath));
            }
            return newFont;
        } catch(Exception exception){
            if(showExceptionMessage) {
                Log.e(LOG_TAG,"Error in:createFromAsset(String filename)");
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
    static public PDETypeface createByNameAndTypeface(String name, Typeface typeface){
        PDETypeface newFont = null;
        try {
            newFont = PDETypeface.loadTypeface(name);
            if(newFont==null) {
                newFont = new PDETypeface(name,typeface);
            }
            return newFont;
        }
        catch(Exception exception){
            Log.e(LOG_TAG,"Error in:createByNameAndTypeface(String name, Typeface typeface)");
            exception.printStackTrace();
            return null;
        }
    }



    /**
     * @brief Loads a font with the specified name.
     * First trys to create it from file if this goes wront -> try to get it from assests
     * Only the filename is saved, not the complete path
     * @param name
     * @return the created dttypeface or null if there was no font in asses or the path
     */
    static public PDETypeface createByName(String name){
        PDETypeface newFont = null;

        // try to load font
        newFont  = PDETypeface.createFromFile(name,false);

        // able to load
        if (newFont != null) {
            return newFont;
        }

        // try to load from assets
        newFont = PDETypeface.createFromAsset(name,false);

        // able to load
        if (newFont != null) {
            return newFont;
        }

        Log.e(LOG_TAG,"Error in:createByName(String name)\n can't create a font by name!!!! Normally the default font is now used!!!!!!");

        return newFont;
    }


    /**
     * @brief get the android typeface object
     * @return Typeface object or null;
     */
    public Typeface getTypeface() {
        return mTypeface;
    }


    /**
     * @brief get the name of the dttypeface object
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
        if( path==null) throw new NullPointerException();

        int folderIndex = path.lastIndexOf('/');
        return path.substring(folderIndex+1);
    }


    /**
     * @brief Checks if a font is in telegrotesk family.
     * Caution: throws exception if there is no tele_grotesk array resource!!!
     * @return true if its a telegrotesk font, else false
     */
    public boolean isTeleGroteskFont() throws Resources.NotFoundException{
        String[] telegroteskFonts;
        int i = 0;

        //get telegrotest array from resource
        Context context = PDECodeLibrary.getInstance().getApplicationContext();
        Resources res = context.getResources();
        telegroteskFonts = res.getStringArray(TELE_GROTESK_STRINGRESOURCE_ID);

        if( telegroteskFonts!=null ){
            //check if font is contained
            for(i=0;i<telegroteskFonts.length;i++){
                if( TextUtils.equals(telegroteskFonts[i], getName()) ){
                    //part of the tele-grotesk family
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * @brief trys to get the readable name of the font (at the moment declared in font_families.xml file).
     * Caution: throws exception if there is no tele_grotesk array resource!!!
     * @return readable name if exists in array, else the filename
     */
    public String getReadableName() throws Resources.NotFoundException{
        return getReadableName(TELE_GROTESK_STRINGRESOURCE_ID);
    }


    /**
     * @brief trys to get the readable name of the font (declared in the resourceArrayIdentifier parameter).
     * Caution: throws exception if there is no string array resource!!!
     * @param resourceArrayIdentifier the id of the array in the resource, where we have to look for the name
     * @return readable name if exists in array, else the filename
     */
    private String getReadableName(int resourceArrayIdentifier ) throws Resources.NotFoundException{
        TypedArray fontsArray;
        int i = 0;
        int id = 0;

        //get array from resource
        Context context = PDECodeLibrary.getInstance().getApplicationContext();
        Resources res = context.getResources();
        fontsArray = res.obtainTypedArray(resourceArrayIdentifier);

        if( fontsArray!=null ){
            //check if font is contained
            for(i = 0; i<fontsArray.length(); i++){
                if(TextUtils.equals(fontsArray.getText(i), getName()) ){
                    id = fontsArray.getResourceId(i,0);
                    if(id != 0){
                        return res.getResourceEntryName(id);
                    } else {
                        //break and return normal filename
                        break;
                    }
                }
            }
        }

        return getName();
    }
}
