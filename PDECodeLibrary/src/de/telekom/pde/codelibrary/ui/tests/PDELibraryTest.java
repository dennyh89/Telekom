/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */


package de.telekom.pde.codelibrary.ui.tests;

// imports

import android.util.Log;


//----------------------------------------------------------------------------------------------------------------------
//  PDELibraryTest
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Test class for PDECodeLibrary library.
 *
 * Instantiate this class in your project and check if the functions / properties can be accessed.
 */
public class PDELibraryTest extends Object {

    // member variables
    private String mTestString;
    private final static String LOGTAG = PDELibraryTest.class.getName();

    /**
     * @brief Initialization of the object.
     */
    public PDELibraryTest() {
        // initialize member variables
        mTestString = "PDELibraryTest-String";

        // debug
        Log.d(LOGTAG, "PDELibraryTest.PDELibraryTest: called");
    }

    /**
     * @brief Finalization of the object.
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        // debug
        Log.d(LOGTAG, "PDELibraryTest.finalize: called");
    }

    /**
     * @brief Delivers the testString.
     *
     * @return testString property
     */
    public String getTestString() {
        return mTestString;
    }

    /**
     * @brief Call this test function from outside.
     *
     * Test function takes the property and attaches given string at the end.
     *
     * @param postfix String to be attached.
     * @return Processed string.
     */
    public String testFunction(String postfix) {
        String temp;

        //debug
        Log.d(LOGTAG, "PDELibraryTest.testFunction: called");

        // create a new string by concatenating testString and given string
        temp = mTestString.concat(postfix);

        // return result
        return temp;
    }

}
