package de.telekom.pde.codelibrary.ui.helpers;


import android.text.TextUtils;


//----------------------------------------------------------------------------------------------------------------------
//  PDECharacterSet
//----------------------------------------------------------------------------------------------------------------------


public class PDECharacterSet {

    private String m_CharSet = null;

    /**
     * @brief Character set with digits.
     *
     * Contains "0123456789".
     */
    @SuppressWarnings("unused")
    public static final PDECharacterSet PDECharacterSetGlobalDigitCharacterSet = new PDECharacterSet("01234567890");


    /**
     * @brief Character set with hex digits.
     *
     * Contains "0123456789abcdefABCDEF".
     */
    public static final PDECharacterSet PDECharacterSetGlobalHexCharacterSet = new PDECharacterSet("01234567890abcdefABCDEF");


    /**
     * @brief Character set with int number digits (contains signs).
     *
     * When reading integer numbers by using the character set, be aware that malformed integers (e.g. 12+-3) might
     * also be returned. These integers might not be converted correctly to into numbers.
     *
     * Contains "+-0123456789".
     */
    @SuppressWarnings("unused")
    public static final PDECharacterSet PDECharacterSetGlobalIntCharacterSet = new PDECharacterSet("+-01234567890");


    /**
     * @brief Character set with float number digits (contains signs, decimal dot, and exponent marker).
     *
     * When reading float numbers by using the character set, be aware that malformed floats (e.g. 1.3.0ee+-17) might
     * also be returned. These floats might not be converted correctly to into numbers.
     *
     * Contains "+-0123456789.eE".
     */
    @SuppressWarnings("unused")
    public static final PDECharacterSet PDECharacterSetGlobalFloatCharacterSet = new PDECharacterSet("+-01234567890eE.");


    /**
     * @brief Character set with simple float number digits. Exponential notation is not included.
     *
     * When reading float numbers by using the character set, be aware that malformed floats (e.g. 1.3.0) might
     * also be returned. These floats might not be converted correctly to into numbers.
     *
     * Contains "+-0123456789.".
     */
    @SuppressWarnings("unused")
    public static final PDECharacterSet PDECharacterSetGlobalSimpleFloatCharacterSet = new PDECharacterSet("+-01234567890.");


    /**
     * @brief Character set with basic whitespace (no newlines, only pure space and tab).
     *
     * Contains " \t".
     */
    @SuppressWarnings("unused")
    public static final PDECharacterSet PDECharacterSetGlobalWhitespaceCharacterSet = new PDECharacterSet(" \t");


    /**
     * @brief Character set with newline characters.
     *
     * Contains "\r\n".
     */
    public static final PDECharacterSet PDECharacterSetGlobalNewlineCharacterSet = new PDECharacterSet("\r\n");


    /**
     * @brief Character set with whitespace and newline characters.
     *
     * Contains whitespace and newline character sets combined.
     */
    public static final PDECharacterSet PDECharacterSetGlobalWhitespaceNewlineCharacterSet = new PDECharacterSet(" \t\r\n");


    /**
     * @brief Character set with upper and lowercase simple letters.
     *
     * No special language specific letters (umlauts etc.).
     */
    @SuppressWarnings("unused")
    public static final PDECharacterSet PDECharacterSetGlobalLetterCharacterSet = new PDECharacterSet("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");


    public PDECharacterSet(String charSet){
        if (charSet == null) throw new NullPointerException();
        m_CharSet = charSet;
    }

    public boolean characterIsMember(char character){
        if (TextUtils.isEmpty(m_CharSet)) return false;

        return m_CharSet.indexOf(character) != -1;
    }
}
