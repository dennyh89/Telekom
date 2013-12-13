/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.color;


public class PDEButtonColorDefinition {
    protected int mMainColor;
    protected PDEButtonStateColorDefinition mNormal;
    protected PDEButtonStateColorDefinition mFocus;
    protected PDEButtonStateColorDefinition mTakingInput;
    protected PDEButtonStateColorDefinition mInputDone;

    @SuppressWarnings("unused")
    public PDEButtonColorDefinition(){
        mNormal=null;
        mFocus=null;
        mTakingInput=null;
        mInputDone=null;
    }

    @SuppressWarnings("unused")
    public PDEButtonColorDefinition(int mainColor, PDEButtonStateColorDefinition normal,
                                    PDEButtonStateColorDefinition focus, PDEButtonStateColorDefinition takingInput,
                                    PDEButtonStateColorDefinition inputDone){
        setMainColor(mainColor);
        setNormalStateColorDefinition(normal);
        setFocusStateColorDefinition(focus);
        setTakingInputStateColorDefinition(takingInput);
        setInputDoneStateColorDefinition(inputDone);

    }

    public void setMainColor(int mainColor){
        mMainColor = mainColor;
    }

    public void setNormalStateColorDefinition(PDEButtonStateColorDefinition normal){
        mNormal = normal;
    }

    public void setFocusStateColorDefinition(PDEButtonStateColorDefinition focus){
        mFocus = focus;
    }

    public void setTakingInputStateColorDefinition(PDEButtonStateColorDefinition takingInput){
        mTakingInput = takingInput;
    }


    public void setInputDoneStateColorDefinition(PDEButtonStateColorDefinition inputDone){
        mInputDone = inputDone;
    }

    public int getMainColor(){
        return mMainColor;
    }

    @SuppressWarnings("unused")
    public PDEButtonStateColorDefinition getNormalStateColorDefinition(){
        return mNormal;
    }

    @SuppressWarnings("unused")
    public PDEButtonStateColorDefinition getFocusStateColorDefinition(){
        return mFocus;
    }

    @SuppressWarnings("unused")
    public PDEButtonStateColorDefinition getTakingInputStateColorDefinition(){
        return mTakingInput;
    }

    @SuppressWarnings("unused")
    public PDEButtonStateColorDefinition getInputDoneStateColorDefinition(){
        return mInputDone;
    }
}
