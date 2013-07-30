/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.helpers;


//----------------------------------------------------------------------------------------------------------------------
//  PDEFontMetricsHolder
//----------------------------------------------------------------------------------------------------------------------


public class PDEFontMetricsHolder {
    private float exactDescent;
    private float exactAscent;
    private float exactLineHeight;
    private float exactCapHeight;
    private float exactXHeight;

    public float getAscent() {
        return exactAscent;
    }

    public float getCapHeight() {
        return exactCapHeight;
    }

    public float getDescent() {
        return exactDescent;
    }

    public float getLineHeight() {
        return exactLineHeight;
    }

    public float getXHeight() {
        return exactXHeight;
    }

    public float getExactAscent() {
        return exactAscent;
    }

    public float getExactCapHeight() {
        return exactCapHeight;
    }

    public float getExactDescent() {
        return exactDescent;
    }

    public float getExactLineHeight() {
        return exactLineHeight;
    }

    public float getExactXHeight() {
        return exactXHeight;
    }

    public void setExactAscent(float exactAscent) {
        this.exactAscent = exactAscent;
    }

    public void setExactCapHeight(float exactCapHeight) {
        this.exactCapHeight = exactCapHeight;
    }

    public void setExactDescent(float exactDescent) {
        this.exactDescent = exactDescent;
    }

    public void setExactLineHeight(float exactLineHeight) {
        this.exactLineHeight = exactLineHeight;
    }

    public void setExactXHeight(float exactXHeight) {
        this.exactXHeight = exactXHeight;
    }
}
