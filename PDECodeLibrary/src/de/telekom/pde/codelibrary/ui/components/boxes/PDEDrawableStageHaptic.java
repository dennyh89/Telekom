/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.boxes;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableRoundedGradientBox;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableMultilayer;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;


public class PDEDrawableStageHaptic extends PDEDrawableMultilayer {
    protected PDEDrawableRoundedGradientBox mStageDrawable;
    protected PDEDrawableShapedShadow mShadow;


    public PDEDrawableStageHaptic(){
        super();
        init();
    }

    protected void init() {
        // create stage drawable & configure
        mStageDrawable = new PDEDrawableRoundedGradientBox();
        if(PDECodeLibrary.getInstance().isDarkStyle()) {
            mStageDrawable.setElementBackgroundGradientColors(PDEColor.valueOf("DTGrey2"),
                                                              PDEColor.valueOf("DTGrey100"),
                                                              PDEColor.valueOf("DTGrey75"));
            mStageDrawable.setElementBorderColor(PDEColor.valueOf("Black70Alpha"));
        } else {
            mStageDrawable.setElementBorderColor(PDEColor.valueOf("DTGrey237_Idle_Border"));
        }
        // create outer shadow of stage
        mShadow = (PDEDrawableShapedShadow)mStageDrawable.createElementShadow();
        // add stage & shadow to multilayer
        addLayer(mShadow);
        addLayer(mStageDrawable);

        this.setOnBoundsChangeListener(new PDEDrawableMultilayer.OnPDEBoundsChangeListener() {
            @Override
            public void onPDEBoundsChange(Drawable source, Rect bounds) {
                notifyStageAboutBoundsChange(source, bounds);
            }
        });
    }



    /**
     * @brief Listener for bounds changes of multilayer
     *
     * @param source Drawable source
     * @param bounds the new bounds
     */
    @SuppressWarnings("unused")
    public void notifyStageAboutBoundsChange(Drawable source, Rect bounds) {

        if (mStageDrawable == null) return;
        // get padding for shadow
        int padding = mStageDrawable.getNeededPadding();
        // set new bounds of stage
        mStageDrawable.setBounds(new Rect(bounds.left + padding, bounds.top + padding, bounds.right - padding,
                                          bounds.bottom -padding));

        if (mShadow == null) return;
        // set new offset for shadow
        mShadow.setLayoutOffset(new Point(Math.round(mStageDrawable.getBounds().left-mShadow.getElementBlurRadius()),
                                          Math.round(mStageDrawable.getBounds().top-mShadow.getElementBlurRadius()) + PDEBuildingUnits.oneTwelfthsBU()));
    }
}
