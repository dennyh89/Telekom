/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.errorhandling;

import java.lang.RuntimeException;import java.lang.String;import java.lang.Throwable;


//----------------------------------------------------------------------------------------------------------------------
//  PDERuntimeException
//----------------------------------------------------------------------------------------------------------------------


/**
 * PDERuntimeExceptions are thrown e.g. if the PDECodeComponent Library is used without
 * initialization.
 *
 * @author kdanner
 * @see RuntimeException
 */
@SuppressWarnings("unused")
public class PDERuntimeException extends RuntimeException {

	/**
     * @see RuntimeException#RuntimeException()
	 */
	public PDERuntimeException() {

	}

	/**
	 * @param detailMessage
     * @see RuntimeException#RuntimeException(String)
	 */
	public PDERuntimeException(String detailMessage) {
		super(detailMessage);

	}

	/**
	 * @param throwable
     * @see RuntimeException#RuntimeException(Throwable)
	 */
	public PDERuntimeException(Throwable throwable) {
		super(throwable);

	}

	/**
	 * @param detailMessage
	 * @param throwable
     * @see RuntimeException#RuntimeException(String, Throwable)
	 */
	public PDERuntimeException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);

	}

}
