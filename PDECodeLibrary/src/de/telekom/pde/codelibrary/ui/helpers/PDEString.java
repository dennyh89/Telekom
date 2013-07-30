package de.telekom.pde.codelibrary.ui.helpers;


import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;
import de.telekom.pde.codelibrary.ui.color.PDEColor;


//----------------------------------------------------------------------------------------------------------------------
//  PDEString
//----------------------------------------------------------------------------------------------------------------------



public class PDEString {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEString.class.getName();


    //----- fast access ----------------------------------------------------------------------------------------------------


    /**
     * @brief Quick access to character.
     */
    public static char firstCharacter(String string)
    {
        // safety
        if ( TextUtils.isEmpty(string) ) return 0;

        // return it
        return string.charAt(0);
    }


    /**
     * @brief Quick access to character.
     */
    public static char lastCharacter(String string)
    {
        // safety
        if (TextUtils.isEmpty(string)) return 0;

        // return it
        return string.charAt(string.length() - 1);
    }


//----- String comparison ----------------------------------------------------------------------------------------------


    /**
     * @brief Compare two strings, case sensitive.
     *
     * For nil strings, special rules apply: nil strings are the same as empty strings, so a nil string compared
     * to a zero-length string returns YES.
     *
     * Using literal comparison for unicode strings, which is not quite as exact, but a lot faster.
     */
    public static boolean isEqual(String string1, String string2)
    {
        return TextUtils.equals(string1, string2);
    }


    /**
     * @brief Compare two strings, case insensitive.
     *
     * For nil strings, special rules apply: nil strings are the same as empty strings, so a nil string compared
     * to a zero-length string returns YES.
     *
     * Using literal comparison for unicode strings, which is not quite as exact, but a lot faster.
     */
    public static boolean isEqualCaseIndependent(String string1, String string2)
    {
        // special cases
        if (string1 == null) {
            if (string2 == null || string2.length()==0) {
                return true;
            }
            else return false;
        }
        if (string2 == null) {
            if (string1.length() == 0) {
                return true;
            }
            else return false;
        }

        // case insensitive string comparison
        // normal string comparison
        if ( string1.equalsIgnoreCase(string2) ){
            return true;
        }
        else return false;
    }


    /**
     * @brief Check for the empty string.
     *
     * Zero length strings and nil strings are empty.
     */
    public static boolean isEmpty(String string)
    {
        return TextUtils.isEmpty(string);
    }


//----- string tests ---------------------------------------------------------------------------------------------------


    /**
     * @brief Check if the string starts with the given partial string.
     */
    public static boolean startsWith(String string, String startWithString)
    {
        // if startWithString is empty, the string always starts with it.
        if (isEmpty(startWithString)) return true;

        // if string is empty, it cannot start with the given string
        if (isEmpty(string)) return false;

        // check it
        return string.startsWith(startWithString);
    }


    /**
     * @brief Check if the string ends with the given partial string.
     */
    public static boolean endsWith(String string, String endsWithString)
    {
        // if endsWithString is empty, the string always ends with it.
        if (isEmpty(endsWithString)) return true;

        // if string is empty, it cannot end with the given string
        if (isEmpty(string)) return false;

        // check it
        return string.endsWith(endsWithString);
    }


    /**
     * @brief Check if the string contains the given partial string.
     */
    public static boolean contains(String string, String containString)
    {
        // if containString is empty, the string always contains it.
        if (isEmpty(containString)) return true;

        // if string is empty, it cannot contains the given string
        if (isEmpty(string)) return false;

        // check it
        if ( string.contains(containString) ){
            return true;
        }
        else return false;
    }


//----- character tests ------------------------------------------------------------------------------------------------


    /**
     * @brief Check if the string contains the character.
     */
    public static boolean containsCharacter(String string, char containCharacter)
    {
        //security
        if( isEmpty(string) ) return false;

        int i;
        char c;

        // check all characters
        for (i=0; i<string.length(); i++) {
            // get character
            c = string.charAt(i);
            // is it the sought character?
            if (c == containCharacter) return true;
        }

        // not contained
        return false;
    }


    /**
     * @brief Check if the string contains a character from the set.
     */
    public static boolean  containsCharacterSet(String string, PDECharacterSet containCharset)
    {
        int i;
        char c;

        // check all characters
        for (i=0; i<string.length(); i++) {
            // get character
            c = string.charAt(i);
            // is it the sought character?
            if ( containCharset.characterIsMember(c)) return true;
        }

        // not contained
        return false;
    }


    /**
     * @brief Check if every character from the string is contained in the given character set
     */
    public static boolean isInCharacterSet(String string, PDECharacterSet isInCharset)
    {
        int i;
        char c;

        // check all characters
        for (i=0; i<string.length(); i++) {
            // get character
            c = string.charAt(i);
            // fail if not in character set?
            if ( !isInCharset.characterIsMember(c) ) return false;
        }

        // all characters are good
        return true;
    }


    /**
     * @brief Count the occurences of the character.
     */
    public static int countCharacter(String string, char character)
    {
        int i,count;
        char c;

        // init
        count = 0;

        // check all characters
        for (i=0; i<string.length(); i++) {
            // get character
            c = string.charAt(i);
            // is it the sought character? then count it
            if (c == character) count++;
        }

        // done
        return count;
    }


//----- substrings -----------------------------------------------------------------------------------------------------


    /**
     * @brief Trim whitespace on both ends.
     *
     * Whitespace used is the complete whitespace set including newlines.
     */
    public static String trim(String string)
    {
        String trimmedString = string;
        char c;
        int i;

        // safety: null strings remain null
        if (string==null) return null;

        // check all characters at start
        for (i=0; i<trimmedString.length(); i++) {
            // get character
            c = trimmedString.charAt(i);
            // is it the sought character?
            if ( !PDECharacterSet.PDECharacterSetGlobalWhitespaceNewlineCharacterSet.characterIsMember(c) )
                break;
            trimmedString = trimmedString.substring(1);
        }

        // check all characters at the end
        for (i=trimmedString.length()-1; i>=0; i--) {
            // get character
            c = trimmedString.charAt(i);
            // is it the sought character?
            if ( !PDECharacterSet.PDECharacterSetGlobalWhitespaceNewlineCharacterSet.characterIsMember(c) )
                break;
            trimmedString = trimmedString.substring(0,i);
        }


        // trim
        return trimmedString;
    }


    /**
     * @brief Cut off characters at the beginning of the string.
     */
    public static String trimHead(String string, int count)
    {
        // empty?
        if (count >= string.length()) return "";

        // create substring
        return string.substring(0,count);
    }


    /**
     * @brief Cut off characters at the end of the string.
     */
    public static String trimTail(String string, int count)
    {
        // empty?
        if (count >= string.length()) return "";

        // create substring
        return string.substring(string.length()-count,string.length());
    }


    /**
     * @brief Get first characters of a string.
     */
    public static String head(String string, int count)
    {
        // simple case
        if (count == 0) return "";

        // safety
        if (count > string.length()) {
            count = string.length();
        }

        // create substring
        return string.substring(0,count);
    }


    /**
     * @brief Get last characters of a string.
     */
    public static String tail(String string, int count)
    {
        // simple case
        if (count == 0) return "";

        // safety
        if (count > string.length()) {
            count = string.length();
        }

        // create substring
        return string.substring(string.length()-count,string.length());
    }


    /**
     * @brief Get last characters of a string.
     */
    public static String range(String string, int start, int end)
    {
        // limit start and end
        if (start < 0) start = 0;
        if (start > string.length()) start=string.length();
        if (end < 0) end = 0;
        if (end > string.length()) end=string.length();

        // safety
        if (end <= start) return "";

        // create substring
        return string.substring(start,end);
    }


//----- other operations -----------------------------------------------------------------------------------------------


/**
 * @brief Concatenate two strings.
 */
    public static String concatenate(String string1, String string2)
    {
        // use NSString functions
        return string1.concat(string2);
    }


/**
 * @brief Concatenate two strings.
 */
    public static String concatenate(String string, char character)
    {
        // use NSString functions
        return string.concat(String.valueOf(character));
    }


//----- conversions ----------------------------------------------------------------------------------------------------


    /**
     * @brief Convert string to an int.
     *
     * Returns 0 on malformed strings.
     */
    public static int stringToInt(String string)
    {
        // use string function
        return Integer.parseInt(string);
    }


    /**
     * @brief Convert string to a float.
     *
     * Returns 0.0f on malformed strings.
     */
    public static float stringToFloat(String string)
    {
        // use string function
        return Float.parseFloat(string);
    }


    /**
     * @brief Convert string to a double.
     *
     * Returns 0.0 on malformed strings.
     */
    public static double stringToDouble(String string)
    {
        // use string function
        return Double.parseDouble(string);
    }


    /**
     * @brief Convert string to a bool.
     *
     * Valid bool values are yes, no, true, false and simple digit numbers. Case independent.
     * All numbers other than zero are returned as YES, however, the check is quick,
     * so a leading zero will always be a NO, whereas a leading digit will always be a
     * YES (e.g. 0123 will be NO)
     */
    public static boolean stringToBool(String string)
    {
        char c;

        // empty string is false
        if (TextUtils.isEmpty(string)) return false;

        // get first character for quick check
        c=string.charAt(0);

        // check possibly true cases
        if (c=='y' || c=='Y') {
            // simple "Y"
            if (string.length()==1) return true;
            // "yes"
            return PDEString.isEqualCaseIndependent(string,"yes");
        } else if (c=='t' || c=='T') {
            // simple "T"
            if (string.length()==1) return true;
            // "true"
            return PDEString.isEqualCaseIndependent(string,"true");
        } else if (c>='1' && c<='9') {
            // starts with integer nonzero number, assume it to be true
            return true;
        }

        // when here, we're a NO.
        return false;
    }


    /**
     * @brief Convert a hex-string to Int.
     *
     * The hex-string is pure (no prefix 0x or anything).
     */
    public static int hexStringToInt(String string)
    {
        int i,result;
        char c;
        boolean sign;

        // safety
        if (string.length()==0) return 0;

        // init
        sign = false;
        result = 0;
        i = 0;

        // check for negative numbers -> max 1 sign
        c = string.charAt(0);
        if (c=='+' || c=='-') {
            // skip first one
            i++;
            // check sign
            if (c == '-') sign=true;
        }

        // walk the string
        while (i<string.length()) {
            // get character
            c = string.charAt(i);
            // stop if not hex
            if ( !PDECharacterSet.PDECharacterSetGlobalHexCharacterSet.characterIsMember(c)) break;
            // shift the intermediate result by one nibble
            result <<= 4;
            // add the character
            if (c>='0' && c<='9') {
                result |= c-'0';
            } else if (c>='a' && c<='f') {
                result |= c-'a'+10;
            } else if (c>='A' && c<='F') {
                result |= c-'A'+10;
            }
            // next one
            i++;
        }

        // done, apply sign
        if (sign)
            return -result;
        else return result;
    }


    /**
     * @brief Convert string to a color.
     *
     * For detailed conversion rules see PDEColor (which routines are used). Color formats recognized are
     * integer (r,g,b) with range 0-255, float (r.r,g.g,b.b) with range 0.0-1.0, and hex-like (#rrggbb)
     * with their corresponding alpha formats (r,g,b,a), (r.r,g.g,b.b,a.a) and (#aarrggbb).
     */
    public static PDEColor stringToColor(String string)
    {
        // use PDEColor's implementation (it's better located there)
        return PDEColor.valueOf(string);
    }


    /**
     * @brief Convert a string to an integer number.
     *
     * String might be a simple number (optionally signed; signs are processed ourself), and might
     * be a hex number according to C conventions (0xabcd).
     */
    public static int stringToIntNumber(String string)
    {
        int pos,result;
        char c;
        boolean sign;
        String subString;

        // safety
        if (string.length() == 0) return 0;

        // init
        sign = false;
        pos = 0;

        // check for negative numbers -> max 1 sign
        c = string.charAt(0);
        if (c=='+' || c=='-') {
            // skip first one
            pos++;
            // check sign
            if (c == '-') sign=true;
        }

        // check for "0x"
        if (string.length() >= pos+2) {
            // check first one for '0'
            if (string.charAt(pos) == '0') {
                // read second one
                c = string.charAt(pos+1);
                // check for 'x' or 'X'
                if (c=='x' || c=='X') {
                    // substring for hex conversion
                    subString = string.substring(pos+2);
                    // get hex result
                    result = PDEString.hexStringToInt(subString);
                    // apply sign and stop
                    if (sign)
                        return -result;
                    else return result;
                }
            }
        }

        // normal conversion
        return PDEString.stringToInt(string);
    }


    /**
     * @brief Convert a string to a double number.
     *
     * String might be a double number (optionally signed; signs are processed ourself), or might
     * be a hex number according to C conventions (0xabcd). In this case the hex is read as integer and converted
     * to a double.
     */
    public static double stringToDoubleNumber(String string)
    {
        int pos,result;
        char c;
        boolean sign;
        String subString;

        // safety
        if (string.length() == 0) return 0;

        // init
        sign = false;
        pos = 0;

        // check for negative numbers -> max 1 sign
        c = string.charAt(0);
        if (c=='+' || c=='-') {
            // skip first one
            pos++;
            // check sign
            if (c == '-') sign=true;
        }

        // check for "0x"
        if (string.length() >= pos+2) {
            // check first one for '0'
            if (string.charAt(pos) == '0') {
                // read second one
                c = string.charAt(pos+1);
                // check for 'x' or 'X'
                if (c=='x' || c=='X') {
                    // substring for hex conversion
                    subString = string.substring(pos+2);
                    // get hex result
                    result = PDEString.hexStringToInt(subString);
                    // apply sign and stop
                    if (sign)
                        return -(double)result;
                    else return (double)result;
                }
            }
        }

        // normal conversion
        return PDEString.stringToDouble(string);
    }


    /**
     * @brief Convert a string to a point.
     *
     * Reads two float components (may be hex), separated by comma.
     */
    public static PointF stringToPoint(String string)
    {
        PDEParser parser;
        String subString;
        float value1,value2;

        // init parsing
        parser = PDEParser.parserWithString(string);

        // read first coordinate
        subString = parser.readToSeparatorAndSkip(',');
        value1 = (float) PDEString.stringToDoubleNumber(subString);

        // read second coordinate
        subString = parser.readToSeparatorAndSkip(',');
        value2 = (float) PDEString.stringToDoubleNumber(subString);

        // compose result
        return new PointF (value1,value2);
    }


    /**
     * @brief Convert a string to a size.
     *
     * Reads two float components (may be hex), separated by comma.
     */
    public static PointF stringToSize(String string)
    {
        PDEParser parser;
        String subString;
        float value1,value2;

        // init parsing
        parser = PDEParser.parserWithString(string);

        // read first coordinate
        subString = parser.readToSeparatorAndSkip(',');
        value1 = (float) PDEString.stringToDoubleNumber(subString);

        // read second coordinate
        subString = parser.readToSeparatorAndSkip(',');
        value2 = (float) PDEString.stringToDoubleNumber(subString);

        // compose result
        return new PointF (value1,value2);
    }


    /**
     * @brief Convert a string to a rect.
     *
     * Reads four float components (may be hex), separated by comma. Rects are given in
     * x1,y1,x2,y2 form (left,top,right,bottom edges).
     */
    public static RectF stringToRect(String string)
    {
        PDEParser parser;
        String subString;
        double value1,value2,value3,value4;

        // init parsing
        parser = PDEParser.parserWithString(string);

        // read first coordinate
        subString = parser.readToSeparatorAndSkip(',');
        value1 = PDEString.stringToDoubleNumber(subString);

        // read second coordinate
        subString = parser.readToSeparatorAndSkip(',');
        value2 = PDEString.stringToDoubleNumber(subString);

        // read third coordinate
        subString = parser.readToSeparatorAndSkip(',');
        value3 = PDEString.stringToDoubleNumber(subString);

        // read fourth coordinate
        subString = parser.readToSeparatorAndSkip(',');
        value4 = PDEString.stringToDoubleNumber(subString);

        // compose result
        return new RectF ((float)value1,(float)value2,(float)(value3-value1),(float)(value4-value2));
    }


    /**
     * @brief Hacked string testing
     */
    public static void stringTest()
    {

        // simple number test
        Log.d(LOG_TAG,String.format("%d",PDEString.stringToInt("10")));
       // Log.d(LOG_TAG,String.format("%d",PDEString.stringToInt("10x"))); // Result not defined -> may go wrong
       // Log.d(LOG_TAG,String.format("%d",PDEString.stringToInt("10.2"))); // Result not defined -> may go wrong
       // Log.d(LOG_TAG,String.format("%d",PDEString.stringToInt(" 10"))); // Result not defined -> may go wrong
        Log.d(LOG_TAG,String.format("%d",PDEString.stringToInt("-10")));
        Log.d(LOG_TAG,String.format("%f",PDEString.stringToDouble("10")));
       // Log.d(LOG_TAG,String.format("%f",PDEString.stringToDouble("10x"))); // Result not defined -> may go wrong
        Log.d(LOG_TAG,String.format("%f",PDEString.stringToDouble("10.2")));
        Log.d(LOG_TAG,String.format("%f",PDEString.stringToDouble(" 10.3"))); //works // Result not defined -> may go wrong
        Log.d(LOG_TAG,String.format("%f",PDEString.stringToDouble("-10.3")));
        Log.d(LOG_TAG,String.format("%f",PDEString.stringToDouble(" -10.3")));
       // Log.d(LOG_TAG,String.format("%f",PDEString.stringToDouble("--10.3"))); // Result not defined -> may go wrong

        // boolean test
        Log.d(LOG_TAG,String.format("%b",PDEString.stringToBool("tRuE")));
        Log.d(LOG_TAG,String.format("%b",PDEString.stringToBool("YES")));
        Log.d(LOG_TAG,String.format("%b",PDEString.stringToBool("y")));
        Log.d(LOG_TAG,String.format("%b",PDEString.stringToBool("1")));
        Log.d(LOG_TAG,String.format("%b",PDEString.stringToBool("01"))); // Result not defined -> may go wrong

        // hex test
        Log.d(LOG_TAG,String.format("%x",PDEString.hexStringToInt("012a")));
        Log.d(LOG_TAG,String.format("%x",PDEString.hexStringToInt(" 012a"))); //geht ist aber 0 // Ergebnis nicht definiert => darf fehlschlagen
        Log.d(LOG_TAG,String.format("%x",PDEString.hexStringToInt("ax"))); // geht ist aber a //Ergebnis nicht definiert => darf fehlschlagen
        Log.d(LOG_TAG,String.format("%x",PDEString.hexStringToInt("-12a")));

        // numbers mixed with hex test
        Log.d(LOG_TAG,String.format("%d",PDEString.stringToIntNumber("-10")));
        Log.d(LOG_TAG,String.format("%d",PDEString.stringToIntNumber("0x10")));
        Log.d(LOG_TAG,String.format("%d",PDEString.stringToIntNumber("-0x10")));
        Log.d(LOG_TAG,String.format("%f",PDEString.stringToDoubleNumber("-10.2e-1")));
        Log.d(LOG_TAG,String.format("%f",PDEString.stringToDoubleNumber("-10.3")));
        Log.d(LOG_TAG,String.format("%f",PDEString.stringToDoubleNumber("0x10")));
        Log.d(LOG_TAG,String.format("%f",PDEString.stringToDoubleNumber("-0x10")));

        // complex string conversions
        PointF point;
        PointF size;
        RectF rect;
        point = PDEString.stringToPoint(" 1.2 , 0x12 ");
        Log.d(LOG_TAG,String.format("%f,%f",point.x,point.y));
        size = PDEString.stringToSize("1.2,3.4");
        Log.d(LOG_TAG,String.format("%f,%f",size.x,size.y));
        rect = PDEString.stringToRect("1.2,3.4,5.6,.02");
        Log.d(LOG_TAG,String.format("%f,%f,%f,%f",rect.left,rect.top,rect.right,rect.bottom));
    }


}
