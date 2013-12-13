/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.helpers;


//----------------------------------------------------------------------------------------------------------------------
//  PDEParser
//----------------------------------------------------------------------------------------------------------------------

public class PDEParser {

    private String mString;
    private int mPosition;



// synthesized properties
//
    private boolean mWhitespaceIgnored;


//----- initialization and configuration -------------------------------------------------------------------------------

    @SuppressWarnings("unused")
    public PDEParser(String string) {
        // init
        this(string,true);
    }

    public PDEParser(String string, boolean ignoreWhitespace) {
        // init
        mString = string;
        mPosition = 0;
        setWhitespaceIgnored(ignoreWhitespace);
    }


    /**
     * @brief Class creation convenience.
     */
    public static PDEParser parserWithString(String string)
    {
        // create and init
        return new PDEParser(string,true);
    }


    /**
     * @brief Class creation convenience.
     */
    @SuppressWarnings("unused")
    public static PDEParser parserWithString(String string, boolean ignoreWhitespace)
    {
        // create and init
        return new PDEParser(string, ignoreWhitespace);
    }


    /**
     * @brief Set whitespace ignore mode.
     *
     * If whitespace is from now on ignored, skip any whitespace immediately.
     */
    public void setWhitespaceIgnored(boolean ignore)
    {
        // remember
        mWhitespaceIgnored = ignore;

        // skip whitespace if required
        if (mWhitespaceIgnored) {
            skipWhitespace();
        }
    }


//----- state ----------------------------------------------------------------------------------------------------------


    /**
     * @brief Check if parser is at end of string (and thus done).
     */
    public boolean isEnd()
    {
        return (mPosition >= mString.length());
    }


    /**
     * @brief Return the remaining number of characters to parse.
     */
    public int length()
    {
        // border case
        if (isEnd()) return 0;

        // calculate
        return mString.length() - mPosition;
    }


//----- character access -----------------------------------------------------------------------------------------------


    /**
     * @brief Read the current character without advancing the position.
     */
    public char getCharacter()
    {
        // safety
        if (isEnd()) return 0;

        // get it
        return mString.charAt(mPosition);
    }


    /**
     * @brief Skip the current character.
     *
     * Eventually skip whitespace after this.
     */
    public void skip()
    {
        // safety
        if (isEnd()) return;

        // skip one
        mPosition++;

        // eventually skip whitespace
        if (mWhitespaceIgnored) {
            skipWhitespace();
        }
    }


    /**
     * @brief Read the current character (advance the position).
     *
     * Eventually skip whitespace after this.
     */
    @SuppressWarnings("unused")
    public char readCharacter()
    {
        char c;

        // safety
        if (isEnd()) return 0;

        // remember it
        c=getCharacter();

        // skip it (and eventual whitespace)
        skip();

        // done
        return c;
    }


//----- non-modifying string access ------------------------------------------------------------------------------------


    /**
     * @brief Get head of current String.
     *
     * Don't modify any positions. Ignore whitespace at end of string if required.
     */
    @SuppressWarnings("unused")
    public String getHead(int count)
    {
        // limit characters to get
        if (count > length()) {
            count = length();
        }

        // retrieve the substring
        return getPartial(mPosition, mPosition + count);
    }


//----- reading parts --------------------------------------------------------------------------------------------------


    /**
     * @brief Read to separator.
     *
     * Eventually ignore whitespace directly before the separator. Don't overread the separator.
     */
    @SuppressWarnings("unused")
    public String readToSeparator(char separator)
    {
        int start;

        // remember start position
        start = mPosition;

        // use skipping
        skipToSeparator(separator);

        // cutout the string (this takes care of eventual whitespace at the end)
        return getPartial(start, mPosition);
    }


    /**
     * @brief Read to separator (any from the set).
     *
     * Eventually ignore whitespace directly before the separator. Don't overread the separator.
     */
    public String readToSeparatorSet(PDECharacterSet charset)
    {
        int start;

        // remember start position
        start = mPosition;

        // use skipping
        skipToSeparatorSet(charset);

        // cutout the string (this takes care of eventual whitespace at the end)
        return getPartial(start, mPosition);
    }


    /**
     * @brief Read to separator, and skip it (only one separator in case there are several).
     *
     * Eventually ignore whitespace directly before and after the separator.
     */
    public String readToSeparatorAndSkip(char separator)
    {
        int start;
        String result;

        // remember start position
        start = mPosition;

        // use skipping
        skipToSeparator(separator);

        // cutout the string (this takes care of eventual whitespace at the end)
        result = getPartial(start, mPosition);

        // skip the separator (this also skips whitespace if necessary)
        skip();

        // done
        return result;
    }


    /**
     * @brief Read to separator (any from the set), and skip it (only one separator in case there are several).
     *
     * Eventually ignore whitespace directly before and after the separator.
     */
    @SuppressWarnings("unused")
    public String readToSeparatorSetAndSkip(PDECharacterSet charset)
    {
        int start;
        String result;

        // remember start position
        start = mPosition;

        // use skipping
        skipToSeparatorSet(charset);

        // cutout the string (this takes care of eventual whitespace at the end)
        result = getPartial(start, mPosition);

        // skip the separator (this also skips whitespace if necessary)
        skip();

        // done
        return result;
    }


    /**
     * @brief Read to separator.
     *
     * Eventually ignore whitespace directly before the separator. Don't overread the separator.
     */
    @SuppressWarnings("unused")
    public String readToLastSeparator(char separator)
    {
        int start;

        // remember start position
        start = mPosition;

        // use skipping
        skipToLastSeparator(separator);

        // cutout the string (this takes care of eventual whitespace at the end)
        return getPartial(start, mPosition);
    }


    /**
     * @brief Read to separator (any from the set).
     *
     * Eventually ignore whitespace directly before the separator. Don't overread the separator.
     */
    @SuppressWarnings("unused")
    public String readToLastSeparatorSet(PDECharacterSet charset)
    {
        int start;

        // remember start position
        start = mPosition;

        // use skipping
        skipToSeparatorSet(charset);

        // cutout the string (this takes care of eventual whitespace at the end)
        return getPartial(start, mPosition);
    }


    /**
     * @brief Read to separator, and skip it (only one separator in case there are several).
     *
     * Eventually ignore whitespace directly before and after the separator.
     */
    @SuppressWarnings("unused")
    public String readToLastSeparatorAndSkip(char separator)
    {
        int start;
        String result;

        // remember start position
        start = mPosition;

        // use skipping
        skipToLastSeparator(separator);

        // cutout the string (this takes care of eventual whitespace at the end)
        result = getPartial(start, mPosition);

        // skip the separator (this also skips whitespace if necessary)
        skip();

        // done
        return result;
    }


    /**
     * @brief Read to separator (any from the set), and skip it (only one separator in case there are several).
     *
     * Eventually ignore whitespace directly before and after the separator.
     */
    @SuppressWarnings("unused")
    public String readToLastSeparatorSetAndSkip(PDECharacterSet charset)
    {
        int start;
        String result;

        // remember start position
        start = mPosition;

        // use skipping
        skipToLastSeparatorSet(charset);

        // cutout the string (this takes care of eventual whitespace at the end)
        result = getPartial(start, mPosition);

        // skip the separator (this also skips whitespace if necessary)
        skip();

        // done
        return result;
    }


    /**
     * @brief Read fixed number of characters.
     *
     * If the characters end with whitespace, eventually return less of them if whitespace should be ignored.
     * Skip this whitespace then anyway.
     */
    @SuppressWarnings("unused")
    public String readCharacters(int count)
    {
        int start;

        // remember start position
        start = mPosition;

        // use skipping
        skipCharacters(count);

        // cutout the string (this takes care of eventual whitespace at the end)
        return getPartial(start, mPosition);
    }


    /**
     * @brief Read characters as long as they are in the given set.
     *
     * Skip any whitespace afterwards if required. If the set contains whitespace, whitespace is read and
     * returned regardless if the ignoreWhitespace mode is turned on.
     */
    @SuppressWarnings("unused")
    public String readCharactersInSet(PDECharacterSet charset)
    {
        int start;

        // remember start position
        start = mPosition;

        // use skipping
        skipCharactersInSet(charset);

        // cutout the string (this takes care of eventual whitespace at the end)
        return getPartial(start, mPosition);
    }


    /**
     * @brief Read one line.
     *
     * If whitespace is ignored, empty lines are automatically skipped by this function (it's emerging behaviour
     * of the whitespace autoskipping logic which skips newlines automatically)
     */
    @SuppressWarnings("unused")
    public String readLine()
    {
        String result;

        // read to newline set
        result = readToSeparatorSet(PDECharacterSet.PDECharacterSetGlobalNewlineCharacterSet);

        // skip the line (skip single \r and \n; and skip the DOS line separator \r\n)
        if (getCharacter() == '\r') {
            // skip it
            skip();
            // skip an additional '\n' (note: if whitespace is ignored, the '\n' is already skipped, which is ok)
            if (getCharacter() == '\n') {
                skip();
            }
        } else {
            // just skip one
            skip();
        }

        // done
        return result;
    }


    /**
     * @brief Read the rest of the string contained in the parser.
     *
     * Trim whitespace if configured to do so.
     */
    @SuppressWarnings("unused")
    public String readToEnd()
    {
        int start;

        // remember start position
        start = mPosition;

        // position to the end
        mPosition = mString.length();

        // cutout the string (this takes care of eventual whitespace at the end)
        return getPartial(start, mPosition);
    }


//----- skipping parts -------------------------------------------------------------------------------------------------


    /**
     * @brief Skip to separator.
     *
     * Don't overread the separator.
     */
    public void skipToSeparator(char separator)
    {
        // read until we encounter the separator or the end of string
        while (!isEnd()) {
            // separator?
            if (getCharacter() == separator) break;
            // next one
            mPosition++;
        }
    }


    /**
     * @brief Skip to separator (any from the set).
     *
     * Don't overread the separator.
     */
    public void skipToSeparatorSet(PDECharacterSet charset)
    {
        // read until we encounter the separator or the end of string
        while (!isEnd()) {
            // separator?
            if (charset.characterIsMember(getCharacter())) break;
            // next one
            mPosition++;
        }
    }


    /**
     * @brief Skip to separator, and skip it (only one separator in case there are several).
     *
     * Eventually ignore whitespace after the separator.
     */
    @SuppressWarnings("unused")
    public void skipToSeparatorAndSkip(char separator)
    {
        // use basic skip
        skipToSeparator(separator);

        // skip the separator (this also skips whitespace if necessary)
        skip();
    }


    /**
     * @brief Skip to separator (any from the set), and skip it (only one separator in case there are several).
     *
     * Eventually ignore whitespace after the separator.
     */
    @SuppressWarnings("unused")
    public void skipToSeparatorSetAndSkip(PDECharacterSet charset)
    {
        // use basic skip
        skipToSeparatorSet(charset);

        // skip the separator (this also skips whitespace if necessary)
        skip();
    }


    /**
     * @brief Skip to separator.
     *
     * Don't overread the separator.
     */
    public void skipToLastSeparator(char separator)
    {
        int pos;

        // find last one; if there is no one, we skip to the end.
        pos = mString.length()-1;
        while (pos >= mPosition) {
            // have we found it?
            if (mString.charAt(pos) == separator) {
                mPosition = pos;
                break;
            }
            // try next one
            pos--;
        }

        // not found if we have run through all characters
        if (pos < mPosition) {
            // set to end
            mPosition = mString.length();
        }
    }


    /**
     * @brief Skip to separator (any from the set).
     *
     * Don't overread the separator.
     */
    public void skipToLastSeparatorSet(PDECharacterSet charset)
    {
        int pos;

        // find last one; if there is no one, we skip to the end.
        pos = mString.length()-1;
        while (pos >= mPosition) {
            // have we found it?
            if (charset.characterIsMember(mString.charAt(pos))) {
                mPosition = pos;
                break;
            }
            // try next one
            pos--;
        }

        // not found if we have run through all characters
        if (pos < mPosition) {
            // set to end
            mPosition = mString.length();
        }
    }


    /**
     * @brief Skip to separator, and skip it (only one separator in case there are several).
     *
     * Eventually ignore whitespace after the separator.
     */
    @SuppressWarnings("unused")
    public void skipToLastSeparatorAndSkip(char separator)
    {
        // use basic skip
        skipToLastSeparator(separator);

        // skip the separator (this also skips whitespace if necessary)
        skip();
    }


    /**
     * @brief Skip to separator (any from the set), and skip it (only one separator in case there are several).
     *
     * Eventually ignore whitespace after the separator.
     */
    @SuppressWarnings("unused")
    public void skipToLastSeparatorSetAndSkip(PDECharacterSet charset)
    {
        // use basic skip
        skipToLastSeparatorSet(charset);

        // skip the separator (this also skips whitespace if necessary)
        skip();
    }


    /**
     * @brief Skip fixed number of characters.
     *
     * If required skip any whitespace afterwards
     */
    public void skipCharacters(int count)
    {
        // read the number of characters (limit to the string length)
        if (count > length()) {
            count = length();
        }
        mPosition += count;

        // eventually skip whitespace
        if (mWhitespaceIgnored) {
            skipWhitespace();
        }
    }


    /**
     * @brief Skip characters as long as they are in the given set.
     *
     * Skip any whitespace afterwards if required.
     */
    public void skipCharactersInSet(PDECharacterSet charset)
    {
        // read until we encounter the separator or the end of string
        while (!isEnd()) {
            // stop if character is no longer in set.
            if (!charset.characterIsMember(getCharacter())) break;
            // next one
            mPosition++;
        }

        // eventually skip whitespace
        if (mWhitespaceIgnored) {
            skipWhitespace();
        }
    }


    /**
     * @brief Skip one line.
     *
     * If whitespace is ignored, empty lines are automatically skipped by this function (it's emerging behaviour
     * of the whitespace autoskipping logic which skips newlines automatically)
     */
    @SuppressWarnings("unused")
    public void skipLine()
    {
        // skip to newline set
        skipToSeparatorSet(PDECharacterSet.PDECharacterSetGlobalNewlineCharacterSet);

        // skip the line (skip single \r and \n; and skip the DOS line separator \r\n)
        if (getCharacter() == '\r') {
            // skip it
            skip();
            // skip an additional '\n' (note: if whitespace is ignored, the '\n' is already skipped, which is ok)
            if (getCharacter() == '\n') {
                skip();
            }
        } else {
            // just skip one
            skip();
        }
    }


    /**
     * @brief Explicit whitespace skipping.
     *
     * Newline characters also count as whitespace.
     */
    public void skipWhitespace()
    {
        // read until we encounter the end or something non-whitespace
        while (!isEnd()) {
            // stop if character is not whitespace
            if (!PDECharacterSet.PDECharacterSetGlobalWhitespaceNewlineCharacterSet.characterIsMember(getCharacter())) break;
            // next one
            mPosition++;
        }
    }


//----- helpers --------------------------------------------------------------------------------------------------------


    /**
     * @brief Check if the character is contained in the rest of the string.
     */
    @SuppressWarnings("unused")
    public boolean containsCharacter(char character)
    {
        int i;

        // seek to the end
        for (i= mPosition; i< mString.length(); i++) {
            // did we find it?
            if (character == mString.charAt(i)) return true;
        }

        // not found
        return false;
    }


    /**
     * @brief Check if one of the caracters in the set is contained in the rest of the string.
     */
    @SuppressWarnings("unused")
    public boolean containsCharacterSet(PDECharacterSet charset)
    {
        int i;

        // seek to the end
        for (i = mPosition; i < mString.length(); i++) {
            // did we find it?
            if (charset.characterIsMember(mString.charAt(i))) return true;
        }

        // not found
        return false;
    }


    /**
     * @brief Check if the character is contained in the rest of the string.
     */
    @SuppressWarnings("unused")
    public boolean containsCharacter(char character, char toSeparator)
    {
        int i;
        char c;

        // seek to the end or separator
        for (i = mPosition; i < mString.length(); i++) {
            // get character once
            c = mString.charAt(i);
            // stop at separator
            if (toSeparator == c) return true;
            // did we find it?
            if (character == c) return false;
        }

        // not found
        return false;
    }


    /**
     * @brief Check if one of the caracters in the set is contained in the rest of the string.
     */
    @SuppressWarnings("unused")
    public boolean containsCharacterSet(PDECharacterSet charset, char toSeparator)
    {
        int i;
        char c;

        // seek to the end or separator
        for (i = mPosition; i < mString.length(); i++) {
            // get character once
            c = mString.charAt(i);
            // stop at separator
            if (toSeparator == c) return false;
            // did we find it?
            if (charset.characterIsMember(c)) return true;
        }

        // not found
        return false;
    }


    /**
     * @brief Check if the character is contained in the rest of the string.
     */
    @SuppressWarnings("unused")
    public boolean containsCharacter(char character, PDECharacterSet toSeparatorSet)
    {
        int i;
        char c;

        // seek to the end or separator
        for (i = mPosition; i < mString.length(); i++) {
            // get character once
            c = mString.charAt(i);
            // stop at separator
            if (toSeparatorSet.characterIsMember(c)) return false;
            // did we find it?
            if (character == c) return true;
        }

        // not found
        return false;
    }


    /**
     * @brief Check if one of the caracters in the set is contained in the rest of the string.
     */
    @SuppressWarnings("unused")
    public boolean containsCharacterSet(PDECharacterSet charset,PDECharacterSet toSeparatorSet)
    {
        int i;
        char c;

        // seek to the end or separator
        for (i = mPosition; i < mString.length(); i++) {
            // get character once
            c = mString.charAt(i);
            // stop at separator
            if (toSeparatorSet.characterIsMember(c)) return false;
            // did we find it?
            if (charset.characterIsMember(c)) return true;
        }

        // not found
        return false;
    }


//----- internal functions ---------------------------------------------------------------------------------------------


    /**
     * @brief Retrieve a partial substring.
     *
     * Validate the coordinates. If whitespace should be ignored, ignore whitespace at the end of the string. Whitespace
     * at the beginning should not occur, this must be ensured by the caller.
     */
    private String getPartial(int start,int end)
    {
        // valid start position?
        if (start < 0 || start >= mString.length()) return "";

        // valid end position?
        if (end <= start) return "";

        // limit end position
        if (end > mString.length()) {
            end = mString.length();
        }

        // eventually cut down on whitespace at the end
        if (mWhitespaceIgnored) {
            while (end > start) {
                // non-whitespace? -> stop
                if (!PDECharacterSet
                        .PDECharacterSetGlobalWhitespaceNewlineCharacterSet
                        .characterIsMember(mString.charAt(end - 1))) {
                    break;
                }
                // take it out
                end--;
            }
        }

        // empty now?
        if (end == start) return "";

        // return substring
        return mString.substring(start, end);
    }
}

