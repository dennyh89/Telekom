/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.color;


public class PDEButtonStateColorDefinition {
    protected int mMainColor;
    protected int mGradientLighterColor;
    protected int mGradientDarkerColor;
    protected int mBorderColor;
    protected int mTextColor;

    public PDEButtonStateColorDefinition(){

    }

    public PDEButtonStateColorDefinition(int mainColor, int gradientLighterColor, int gradientDarkerColor,
                                         int borderColor, int textColor){
        setMainColor(mainColor);
        setGradientLighterColor(gradientLighterColor);
        setGradientDarkerColor(gradientDarkerColor);
        setBorderColor(borderColor);
        setTextColor(textColor);
    }

    public void setMainColor(int mainColor){
        mMainColor = mainColor;
    }

    public void setGradientLighterColor(int gradientLighterColor){
        mGradientLighterColor = gradientLighterColor;
    }

    public void setGradientDarkerColor(int gradientDarkerColor){
        mGradientDarkerColor = gradientDarkerColor;
    }

    public void setBorderColor(int borderColor){
        mBorderColor = borderColor;
    }

    public void setTextColor(int textColor){
        mTextColor = textColor;
    }

    public int getMainColor(){
        return mMainColor;
    }

    public int getGradientLighterColor(){
        return mGradientLighterColor;
    }

    public int getGradientDarkerColor(){
        return mGradientDarkerColor;
    }

    public int getBorderColor(){
        return mBorderColor;
    }

    public int getmTextColorColor(){
        return mTextColor;
    }
}
