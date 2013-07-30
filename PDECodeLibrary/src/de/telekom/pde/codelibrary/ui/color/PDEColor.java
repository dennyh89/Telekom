/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.color;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;


//----------------------------------------------------------------------------------------------------------------------
//  PDEColor
//----------------------------------------------------------------------------------------------------------------------

@SuppressWarnings("unused")
public class PDEColor {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEColor.class.getName();

    private float mRed;
    private float mGreen;
    private float mBlue;
    private float mAlpha;

    private float mHue;
    private float mSaturation;
    // luminance
    private float mValue;

    public static final String DTUIText = "DTUITextAuto";
    public static final String DTUIBackground = "DTUIBackgroundAuto";
    public static final String DTUIInteractive = "DTUIInteractiveAuto";
    public static final String DTUIIndicativeText = "DTUIIndicativeTextAuto";

    // define parsing character sets as regular expression
    final static String PDEColorGlobal_hexCharacterSet = "[0-9a-fA-F]*";
    final static String PDEColorGlobal_digitCommaCharacterSet = "[0-9, ]*";
    final static String PDEColorGlobal_digitPointCommaCharacterSet = "[0-9., ]*";


    public PDEColor() {
        init();
    }


    public PDEColor(PDEColor color){
        if( color!= null ){
            //use rgba values direct instead of getIntegerColor to avoid calculation differences!!!!!
            //the same int color values(with getIntegerColor) can have some different rgba float values (this is more accurate for step calculation in hsv mode)
            init(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }else {
            init();
        }
    }

    public PDEColor(int color){
        init(color, false);
    }


    //only used by valueOf(int,boolean)
    private PDEColor(int color, boolean ignoreAlpha){
       init(color, ignoreAlpha);
    }

    public PDEColor(float red, float green, float blue, float alpha){
        init(red, green, blue, alpha);
    }

    private void init() {
        init(0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * @brief Create text color depending on the current dark/light style setting of the library.
     */
    static public PDEColor DTUITextColor(){
        return PDEColor.valueOf(DTUIText);
    }


    /**
     * @brief Create background color depending on the current dark/light style setting of the library.
     */
    static public PDEColor DTUIBackgroundColor(){
        return PDEColor.valueOf(DTUIBackground);
    }

    /**
     * @brief Create interactive color depending on the current dark/light style setting of the library.
     */
    static public PDEColor DTUIInteractiveColor(){
        return PDEColor.valueOf(DTUIInteractive);
    }

    /**
     * @brief Create interactive text color depending on the current dark/light style setting of the library.
     */
    static public PDEColor DTUIIndicativeTextColor(){
        return PDEColor.valueOf(DTUIIndicativeText);
    }


    private void init(int color){
        init(color, false);
    }

    private void init(int color, boolean ignoreAlpha) {
        float a, r, g, b;

        a = 1.0f;

        // extract values
        if(!ignoreAlpha) {
            a = ((color >> 24) & 0xff) / 255.0f;
        }
        r = ((color >> 16) & 0xff) / 255.0f;
        g = ((color >> 8) & 0xff) / 255.0f;
        b = (color & 0xff) / 255.0f;

        init(r, g, b, a);
    }

    private void init(float red, float green, float blue, float alpha){
        mRed = red;
        mGreen = green;
        mBlue = blue;
        mAlpha = alpha;
    }

    /**
     * @brief Internal color resolving.
     */
   // + (BOOL) resolveColor:(DTColorData *)colorData forString:(NSString *)colorString recursionDepth:(int)depth
    private void resolveColor(String colorString)
    {
        int pos;
        String colorCode;
        String colorOperations;
        char c;

        if( TextUtils.isEmpty(colorString) ) return;

        // split off operations
        pos = colorString.indexOf("!");
        if (pos == -1) {
            colorCode = colorString;
            colorOperations = "";
        } else {
            colorCode = colorString.substring(0,pos);
            colorOperations = colorString.substring(pos+1);
        }

        // trim the color code
        colorCode = colorCode.trim();

        // a quick error check
        if ( TextUtils.isEmpty(colorCode) ) return;

        // now look what we want to do with our color code
        c = colorCode.charAt(0);
        if (c == '#') {
            // seems to be a hex code
            resolveColorForHexString(colorCode);
        } else if ( Character.isLetter(c) ){
            // seems to be a symbolic name
            resolveColorForSymbolicName(colorCode);
        } else if ( Character.isDigit(c) ) {
            // can be either a collection of floats or decimals as color code
            pos = colorCode.indexOf(".");
            if (pos == -1) {
                // no decimal point, seems to be an integer color code
                resolveColorForIntString(colorCode);
            } else {
                // seems to be a float color code
                resolveColorForFloatString(colorCode);
            }
        } else if (c=='.') {
            // also seems to be a float color code
            resolveColorForFloatString(colorCode);
        } else {
            // unknown code
            return;
        }

        // now it's time to perform the operations (if any)
        colorOperations = colorOperations.trim();
        if ( !TextUtils.isEmpty(colorOperations) ) {
            // todo Implement! (isn't there on iOS as well)
        }
    }


    /**
     * @brief just little helper for resolveColorForSymbolicName function
     */
    private int getColorNameIdentifier(String symbolicName)
    {
        Context context = PDECodeLibrary.getInstance().getApplicationContext();
        return context.getResources().getIdentifier(symbolicName, "color", context.getPackageName() );
    }

    /**
     * @brief Resolve a symbolic color name.
     *
     * Symbolic colors may also contain references to other colors, and expressions, so be recursive up to a given
     */
    private void resolveColorForSymbolicName(String symbolicName){
        int resourceID;
        Context context = PDECodeLibrary.getInstance().getApplicationContext();

        resourceID = getColorNameIdentifier(symbolicName);
        if( resourceID == 0 ){
            // some colors depend on the library color setting
            //!!!!!!!!
            //!!!! no recursion like on ios -> just try to get identifier with new name!!!!
            //!!!!!!!!
            if (symbolicName.equals(DTUIText) ) {
                if ( PDECodeLibrary.getInstance().isDarkStyle() ){
                    resourceID = getColorNameIdentifier("DTDarkUIText");
                }
                else {
                    resourceID = getColorNameIdentifier("DTLightUIText");
                }
            } else if ( symbolicName.equals(DTUIInteractive) ) {
                if ( PDECodeLibrary.getInstance().isDarkStyle() ){
                    resourceID = getColorNameIdentifier("DTDarkUIInteractive");
                }
                else {
                    resourceID = getColorNameIdentifier("DTLightUIInteractive");
                }
            } else if ( symbolicName.equals(DTUIBackground) ) {
                if ( PDECodeLibrary.getInstance().isDarkStyle() ){
                    resourceID = getColorNameIdentifier("DTDarkUIBackground");
                }
                else {
                    resourceID = getColorNameIdentifier("DTLightUIBackground");
                }
            } else if ( symbolicName.equals(DTUIIndicativeText) ) {
                if ( PDECodeLibrary.getInstance().isDarkStyle() ){
                    resourceID = getColorNameIdentifier("DTDarkUIIndicativeText");
                }
                else {
                    resourceID = getColorNameIdentifier("DTLightUIIndicativeText");
                }
            }
        }

        if( resourceID!=0){
            init( context.getResources().getColor(resourceID ));
        }
    }

    /**
     * @brief Resolve a hex color.
     *
     * Color is web format with optional alpha: "#rrggbb" or "#aarrggbb", all digits are hex digits.
     */
    private void resolveColorForHexString(String hexString){
        long value;
        String hexValues;

        //security
        if( TextUtils.isEmpty(hexString) ){
            return;
        }

        // check the string: must be either 7 or 9 bytes long
        if (hexString.length() != 7 && hexString.length() != 9) return;

        // first character must be '#'
        if (hexString.charAt(0) != '#') return;

        hexValues = hexString.substring(1);

        // the rest must only contain a hex set
        if( !hexValues.matches(PDEColorGlobal_hexCharacterSet) ) return;

        // convert to hex value by scanning
        value = Long.parseLong(hexValues, 16);

        // split into colors, normalize
        if (hexString.length() == 7) {
            // RGB with no alpha
            mRed = (((value >> 16) & 0xff) / 255.0f);
            mGreen = (((value >> 8) & 0xff) / 255.0f);
            mBlue = ((value & 0xff) / 255.0f);
            mAlpha = 1.0f;
        } else {
            // RGB with alpha
            mAlpha = (((value >> 24) & 0xff) / 255.0f);
            mRed = (((value >> 16) & 0xff) / 255.0f);
            mGreen = (((value >> 8) & 0xff) / 255.0f);
            mBlue = ((value & 0xff) / 255.0f);
        }
    }

    /**
     * @brief Resolve a int color.
     *
     * Color is a series of int values (they are clamped to 0..255). The following mappings apply:
     *   - "grey" : a grey value
     *   - "grey,alpha" : a grey value with alpha
     *   - "red,green,blue" : a RGB color
     *   - "red,green,blue,alpha": RGB with alpha
     */
    private void resolveColorForIntString(String intString){
        String subStrings[];
        String str;
        int i,value; //we only have values between 0 and 255 so we can use ints instead of floats like in resolveColorForHexString
        float values[] = new float[4];

        if( TextUtils.isEmpty(intString) ) return;

        // check the string: must only contain digits and commas (whitespace is still allowed)
        if ( !intString.matches(PDEColorGlobal_digitCommaCharacterSet) ) return;

        // now split into substrings
        subStrings = intString.split(",");
        if (subStrings.length > 4) return;

        // convert into numbers
        for (i=0; i < subStrings.length; i++) {
            str = subStrings[i].trim();
            value = Integer.parseInt(str);
            if (value < 0) value = 0;
            if (value > 255) value = 255;
            values[i] = value / 255.0f;
        }

        // assign depending on pattern
        switch (subStrings.length) {
            case 1:
                init(values[0], values[0], values[0], 1.0f);
                break;
            case 2:
                init(values[0], values[0], values[0], values[1]);
                break;
            case 3:
                init(values[0], values[1], values[2], 1.0f);
                break;
            case 4:
                init(values[0], values[1], values[2], values[3]);
                break;
        }
    }


    /**
     * @brief Resolve a float color.
     *
     * Color is a series of float values (they are clamped to 0.0..1.0). The following mappings apply:
     *   - "grey" : a grey value
     *   - "grey,alpha" : a grey value with alpha
     *   - "red,green,blue" : a RGB color
     *   - "red,green,blue,alpha": RGB with alpha
     */
    private void resolveColorForFloatString(String intString){
        String subStrings[];
        String str;
        int i;
        float value;
        float values[] = new float[4];

        if( TextUtils.isEmpty(intString) ) return;

        // check the string: must only contain digits and commas (whitespace is still allowed)
        if ( !intString.matches(PDEColorGlobal_digitPointCommaCharacterSet) ) return;

        // now split into substrings
        subStrings = intString.split(",");
        if (subStrings.length > 4) return;

        // convert into numbers
        for (i=0; i<subStrings.length; i++) {
            str = subStrings[i].trim();
            value = Float.parseFloat(str);
            if (value < 0.0f) value = 0.0f;
            if (value > 1.0f) value = 1.0f;
            values[i] = value;
        }

        // assign depending on pattern
        switch (subStrings.length) {
            case 1:
                init(values[0],values[0],values[0],1.0f);
                break;
            case 2:
                init(values[0],values[0],values[0],values[1]);
                break;
            case 3:
                init(values[0],values[1],values[2],1.0f);
                break;
            case 4:
                init(values[0],values[1],values[2],values[3]);
                break;
        }
    }


    public void setColor(int color){
        init(color,false);
    }

    /**
     * @brief Color conversion UIColor to 32bit unsigned int.
     *
     * Always takes alpha into account.
     */
    public int getIntegerColor(){
        int ir,ig,ib,ia;

        // to int, clamp
        ir = (int) (mRed * 255.0f); if (ir < 0) ir = 0; if (ir > 255) ir = 255;
        ig = (int) (mGreen * 255.0f); if (ig < 0) ig = 0; if (ig > 255) ig = 255;
        ib = (int) (mBlue * 255.0f); if (ib < 0) ib = 0; if (ib > 255) ib = 255;
        ia = (int) (mAlpha * 255.0f); if (ia < 0) ia = 0; if (ia > 255) ia = 255;

        // make a color from this
        return (ia << 24) | (ir << 16) | (ig << 8) | ib;
    }


    /**
     * @brief Calculate luminance.
     *
     * Currently a really simple algorithm.
     */
    public float luminance(){
        // calculate simple liminance
        return (mRed + mGreen + mBlue) / 3;
    }


    /**
     * @brief Check if it's a dark color.
     *
     * Globally defined so the whole project uses the same definition.
     */
    public boolean isDarkColor()
    {
        // check against fixed threshold
        return luminance() < 0.5f;
    }


    /**
     * @brief Check if it's a transparent color (so the transparent scheme is applied).
     *
     * Globally defined so the whole project uses the same definition.
     */
     public boolean isTransparentColor()
    {
        // check against fixed threshold
        return getAlpha() < 0.5f;
    }

    /**
     * @brief Mix two colors in RGB space.
     *
     * This function will change - it currently does not take gamma into account; and does not use a more human-adjusted
     * color space. Also think about what to do with alpha -> should it really blend like this?
     */
    public PDEColor mixColors(PDEColor colorToMix,float blend) {
        float newRed,newBlue,newGreen,newAlpha;

        if( colorToMix==null ) return null;

        // clamp blend value
        if (blend<0.0f) blend=0.0f;
        if (blend>1.0f) blend=1.0f;

        // mix values
        newRed = (float)((colorToMix.getRed() * blend) + (getRed() * (1.0 - blend)));
        newGreen =  (float)((colorToMix.getGreen() * blend) + (getGreen() * (1.0 - blend)));
        newBlue = (float)((colorToMix.getBlue() * blend) + (getBlue() * (1.0 - blend)));
        newAlpha = (float)((colorToMix.getAlpha() * blend) + (getAlpha() * (1.0 - blend)));

        // create color from new values
        return new PDEColor(newRed, newGreen, newBlue, newAlpha);
    }


    /**
     * @brief Primitive color math. Calculate a darker color.
     *
     * DT usually uses the HSV model and changes V (value/brightness) to obtain darker and lighter colors, so
     * we do the same.
     *
     * Alpha stays unchanged in the calculation.
     */
    public PDEColor darkerColor(@SuppressWarnings("SameParameterValue") float step) {
        return lighterColor(-step);
    }

    /**
    * @brief Primitive color math. Calculate a lighter color.
            *
            * DT usually uses the HSV model and changes V (value/brightness) to obtain darker and lighter colors, so
    * we do the same.
            *
            * Alpha stays unchanged in the calculation.
            */
    public PDEColor lighterColor(float step){
        PDEColor newColor = new PDEColor( this );


        newColor.convertRGBToHSV();

        // adjust
        newColor.setValue(newColor.getValue() + step);
        if (newColor.getValue() < 0.0f) newColor.setValue(0.0f);
        if (newColor.getValue() > 1.0f) newColor.setValue(1.0f);

        // convert back and return the resulting color
        newColor.convertHSVToRGB();

        return newColor;
    }


    /**
     * @brief Styleguide color math. Darker gradient color.
     *
     * The darker gradient color is obtained by using HSV space and decreasing V (value/brightness) by 0.1
     */
    public PDEColor styleguideGradientDarkerColor(){
        // it's the same as the darker color with defined step
        return darkerColor(0.1f);
    }


/**
 * @brief Styleguide color math. Lighter gradient color.
 *
 * The lighter gradient color is obtained by using HSV space,
 * increasing V (value/brightness) by 0.1 and decreasing S (saturation) by 0.2
 */
    public PDEColor styleguideGradientLighterColor(){
        PDEColor newColor = new PDEColor( this );

        // convert to HSV
        newColor.convertRGBToHSV();

        // adjust
        newColor.setValue(newColor.getValue()+0.1f);

        if (newColor.getValue() > 1.0f) newColor.setValue(1.0f);
        newColor.setSaturation(newColor.getSaturation()-0.2f);
        if (newColor.getSaturation() < 0.0f) newColor.setSaturation(0.0f);

        // convert back and return the resulting color
        newColor.convertHSVToRGB();

        return newColor;
    }


/**
 * @brief Styleguide color math. Border color.
 *
 * The color used for borders seems to be the same color as the gradient darker color.
 */
    public PDEColor styleguideBorderColor() {
        return styleguideGradientDarkerColor();
    }


    /**
     * @brief Styleguide color math. Bright color (used as textfield background).
     *
     * Go to a really almost white color: saturation to 5%, value to 95% (unless original saturation
     * is lower, or value is higher)
     */
    public PDEColor styleguideBrightColor()
    {
        PDEColor newColor = new PDEColor( this );

        // convert to HSV
        newColor.convertRGBToHSV();

        // adjust
        if (newColor.getValue() < 0.98f) newColor.setValue(0.98f);
        if (newColor.getSaturation() > 0.03f) newColor.setSaturation(0.03f);

        // convert back and return the resulting color
        newColor.convertHSVToRGB();
        return newColor;
    }


    /**
     * @brief Styleguide color math. Agent state color scheme.
     *
     * Agent states run along idle -> focus -> takinginput, and get darker every step. The darker step is
     * defined as decreasing V (value/brightness) by 0.075 each step.
     *
     * Additionally we increase alpha by 0.10 each step. This gives transparent colors an appearance in higher
     * agent states and is used for plate backgrounds (the usually used color for this is DTTransparentBlack).
     */
    public PDEColor styleguideAgentStateColor(float step) {
        PDEColor newColor = new PDEColor( this );

        // convert to HSV
        newColor.convertRGBToHSV();

        // adjust
        newColor.setValue(newColor.getValue() - (step * 0.075f));
        if (newColor.getValue() < 0.0f) newColor.setValue(0.0f);
        if (newColor.getValue() > 1.0f) newColor.setValue(1.0f);

        // also adjust alpha
        newColor.setAlpha(newColor.getAlpha() + (step * 0.075f));
        if (newColor.getAlpha() >= 1.0f) newColor.setAlpha(1.0f);

        // convert back and return the resulting color
        newColor.convertHSVToRGB();

        return newColor;
    }


    /**
     * @brief Convert RGB color space to HSV color space (H is scaled 0.0..6.0).
     */
    public void convertRGBToHSV() {

        float rgbmin,rgbmax,chroma,hue,luminance,saturation;

        // calulate rgbmin and max
        rgbmin = Math.min ( Math.min (mRed,mGreen),mBlue);
        rgbmax = Math.max ( Math.max (mRed,mGreen),mBlue);

        // calculate chroma
        chroma=rgbmax-rgbmin;

        // now calculate hue
        if (chroma <= 0.0f) {
            // undefined, use 0.0
            hue = 0.0f;
        } else if (rgbmax == mRed) {
            // red area
            hue = (mGreen - mBlue)/chroma;
            if (hue < 0.0f) hue += 6.0f;
        } else if (rgbmax == mGreen) {
            // green area
            hue = (mBlue - mRed) / chroma + 2.0f;
        } else {
            // blue area
            hue = (mRed - mGreen) / chroma + 4.0f;
        }

        // simple luminance: highest value
        luminance = rgbmax;

        // scale chroma to saturation
        if (chroma <= 0.0f) {
            // undefined, use 0.0
            saturation = 0.0f;
        } else {
            // scale it
            saturation = chroma / luminance;
        }

        // now set the values in the colordata (leave alpha as it is)
        mHue = hue;
        mSaturation = saturation;
        mValue = luminance;
    }


    /**
     * @brief Convert HSV color space to RGB color space.
     */
    public void convertHSVToRGB(){
        float chroma,red,green,blue,white;

        // init
        red = 0.0f;
        green = 0.0f;
        blue = 0.0f;

        // get base color (some arrangements for out of range hues due to rounding errors)
        if (mHue < 0.0f) {
            // out of range red section, blue decreasing
            red = 1.0f;
            blue = -mHue;
        } else if (mHue < 1.0f) {
            // red section, green increasing
            red = 1.0f;
            green = mHue;
        } else if (mHue < 2.0f) {
            // green section, red decreasing
            green = 1.0f;
            red = 2.0f - mHue;
        } else if (mHue < 3.0f) {
            // green section, blue increasing
            green = 1.0f;
            blue = mHue - 2.0f;
        } else if (mHue < 4.0f) {
            // blue section, green decreasing
            blue = 1.0f;
            green = 4.0f - mHue;
        } else if (mHue < 5.0f) {
            // blue section, red increasing
            blue = 1.0f;
            red = mHue - 4.0f;
        } else if (mHue <= 6.0f) {
            // red section, blue decreasing
            red = 1.0f;
            blue = 6.0f - mHue;
        } else {
            // out of range red section, green increasing
            red = 1.0f;
            green = mHue - 6.0f;
        }

        // get chroma
        chroma = mValue * mSaturation;

        // get base color with chroma
        red *= chroma;
        green *= chroma;
        blue *= chroma;

        // add white component
        white = mValue - chroma;
        red += white;
        green += white;
        blue += white;

        // store (keep alpha the same)
        mRed = red;
        mGreen = green;
        mBlue =  blue;
    }

    /**
     * @brief Convert an UI color to the form "r.r,g.g,b.b,a.a". This always uses alpha.
     */
    public String getColorString(){
        // return formatted string
        return String.format("%01f,%01f,%01f,%01f", mRed, mGreen, mBlue, mAlpha);
    }


    /**
     * @brief Convert an UI color to the hex form "#aarrggbb". This always uses alpha.
     */
    public String getHexColorString() {
        // return hex formatted string
        return String.format("#%08x", getIntegerColor());
    }


    public void setRed(float red) {
        mRed = red;
    }


    public void setGreen(float green) {
        mGreen = green;
    }


    public void setBlue(float blue) {
        mBlue = blue;
    }


    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }


    public float getRed() {
        return mRed;
    }


    public float getGreen() {
        return mGreen;
    }


    public float getBlue() {
        return mBlue;
    }


    public float getAlpha() {
        return mAlpha;
    }

    public void setHue(float hue) {
        mHue = hue;
    }


    public void setSaturation(float saturation) {
        mSaturation = saturation;
    }


    public void setValue(float value) {
        mValue = value;
    }

    public float getHue() {
        return mHue;
    }


    public float getSaturation() {
        return mSaturation;
    }


    public float getValue() {
        return mValue;
    }


    /**
     * @brief Resolve colors.
     *
     * + (PDEColor *) colorWithString:(NSString *)colorString
     */
    static public PDEColor valueOf(String colorString){
        PDEColor newColor = new PDEColor();

        // parse the string into a color (this resolves to black if there's an error)
        newColor.resolveColor(colorString);

        return newColor;
    }


    /**
     * @brief Color conversion 32bit unsigned int to UIColor.
     *
     * Alpha channel is used -> int format is 0xaarrggbb.
     */
    public static PDEColor valueOf(int color){
        return PDEColor.valueOf(color, false);
    }


    /**
     * @brief Color conversion 32bit unsigned int to UIColor.
     *
     * Alpha channel is ignored -> int format is 0xxrrggbb.
     * Alpha channel is used -> int format is 0xaarrggbb.
     */
    public static PDEColor valueOf(int color, @SuppressWarnings("SameParameterValue") boolean ignoreAlpha){
        return new PDEColor(color,ignoreAlpha);
    }


    /**dt_codecomponents:color
     * @brief Convert an int color to the form "#aarrggbb". This always uses alpha.
     */
    public static String stringFromIntColor(int color){
        // return formatted string
        return String.format("#%02x%02x%02x%02x",(color>>24)&0xff,(color>>16)&0xff,(color>>8)&0xff,color&0xff);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PDEColor)) return false;

        return mRed == ((PDEColor) o).getRed() &&
                mGreen == ((PDEColor) o).getGreen() &&
                mBlue == ((PDEColor) o).getBlue() &&
                mAlpha == ((PDEColor) o).getAlpha() &&
                mHue == ((PDEColor) o).getHue() &&
                mSaturation == ((PDEColor) o).getSaturation() &&
                mValue == ((PDEColor) o).getValue();

    }

    @Override
    public int hashCode() {
        int result =205;
        result *= 37 + Float.floatToIntBits(mRed);
        result *= 37 + Float.floatToIntBits(mGreen);
        result *= 37 + Float.floatToIntBits(mAlpha);
        result *= 37 + Float.floatToIntBits(mHue);
        result *= 37 + Float.floatToIntBits(mSaturation);
        result *= 37 + Float.floatToIntBits(mValue);
        return result;
    }


    /**
     * @brief Helper function to get new pdecolor of this color combined with a extra alpha
     */
    public PDEColor newColorWithCombinedAlpha(int extraAlpha) {
        return new PDEColor(newIntegerColorWithCombinedAlpha(extraAlpha));
    }


    /**
     * @brief Helper function to get new integer color of this color combined with a extra alpha
     */
    public int newIntegerColorWithCombinedAlpha(int extraAlpha) {
        int color = getIntegerColor();

        return getIntegerColorCombinedWithAlpha(color,extraAlpha);
    }


    /**
     * @brief Helper function to get a given integer color combined with a extra alpha
     */
    public static int getIntegerColorCombinedWithAlpha(int integerColor,int extraAlpha){
        int alpha, red, green, blue;

        // anything to calculate?
        if (extraAlpha >= 255) return integerColor;

        // split in color components
        alpha = Color.alpha(integerColor);
        red = Color.red(integerColor);
        green = Color.green(integerColor);
        blue = Color.blue(integerColor);

        // create new color with colorAlpha and globalAlpha mixed
        return Color.argb(Math.round(alpha*(extraAlpha/255.0f)),red,green,blue);
    }


    //----- debugging ------------------------------------------------------------------------------------------------------


/**
 * @brief Retrieve debug string containing several color representations.
 */
    public String debug(){
        PDEColor newColor = new PDEColor(this);

        String rgbString;
        float h,s,v;

        // prepare values: HSV
        newColor.convertRGBToHSV();

        // prepare the different outputs (normalize as necessary)
        rgbString = getHexColorString();
        h = newColor.getHue() * 60.0f;
        s = newColor.getSaturation() * 100.0f;
        v = newColor.getValue() * 100.0f;

        // compose and return
        return String.format("RGB=%s; H=%.1f S=%.1f V=%.1f",rgbString,h,s,v);
    }
}
