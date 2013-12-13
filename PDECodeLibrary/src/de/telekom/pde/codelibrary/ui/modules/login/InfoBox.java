/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 * 
 * kdanner - 10.07.13
 */

package de.telekom.pde.codelibrary.ui.modules.login;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.TextView;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableRoundedBox;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;

/// @cond INTERNAL_CLASS


//----------------------------------------------------------------------------------------------------------------------
//  InfoBox
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Special info box which was intended to show some Information on the login screen, but wasn't used finally.
 * It should be also based on the PDELayerTextView to follow Styleguide distances.
 * The class is also not really sophisticated, so not ready to use as a PDEComponent.
 */
@SuppressWarnings("unused")
class InfoBox extends TextView {


    private boolean mHiding;

    public InfoBox(Context context) {
        super(context);
        init();
    }


    public InfoBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public InfoBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        PDEDrawableRoundedBox boxDrawable;

        boxDrawable = new PDEDrawableRoundedBox();

        // todo check where does this color come from?
        boxDrawable.setElementBackgroundColor(PDEColor.valueOf("#e0f1fa"));
        boxDrawable.setElementBorderColor(PDEColor.valueOf("#e0f1fa"));

        PDEUtils.setViewBackgroundDrawable(this, boxDrawable);
        setPadding(PDEBuildingUnits.twoThirdsBU(),
                PDEBuildingUnits.twoThirdsBU(),
                PDEBuildingUnits.twoThirdsBU(),
                PDEBuildingUnits.twoThirdsBU());

        setMinimumHeight((int) (boxDrawable.getElementCornerRadius() * 2.0f));
        setMinimumWidth((int)(boxDrawable.getElementCornerRadius() * 2.0f));


        setGravity(Gravity.CENTER);

        setTextColor(PDEColor.valueOf("#000000").getIntegerColor());
    }


    public void show(String text) {
        setText(text);
        show();
    }


    public void show() {
        int blendingTime = 150;

        if (getVisibility()== View.VISIBLE){
            return;
        }

        mHiding = false;

        setVisibility(VISIBLE);
        AnimationSet as = new AnimationSet(false);
        as.setFillAfter(true);

        AlphaAnimation a01 = new AlphaAnimation(0f, 1.0f);
        a01.setDuration(blendingTime);
        as.addAnimation(a01);

        startAnimation(as);
    }


    public void hide() {
        int blendingTime = 150;

        if (getVisibility()== View.GONE){
            return;
        }

        if (mHiding) return;
        mHiding = true;

        AnimationSet as = new AnimationSet(false);
        as.setFillAfter(true);

        AlphaAnimation a10 = new AlphaAnimation(1f, 0.0f);
        a10.setDuration(blendingTime);
        as.addAnimation(a10);

        as.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                InfoBox.this.setVisibility(View.GONE);
                InfoBox.this.clearAnimation();
            }
        });

        startAnimation(as);
    }


    public void showNotification(String text, int duration) {
        int blendingTime = 150;

        if (getVisibility()== View.VISIBLE){
            return;
        }

        setText(text);

        setVisibility(VISIBLE);
        AnimationSet as = new AnimationSet(false);
        as.setFillAfter(true);

        AlphaAnimation a01 = new AlphaAnimation(0f, 1.0f);
        a01.setDuration(blendingTime);
        as.addAnimation(a01);

        AlphaAnimation a10 = new AlphaAnimation(1f, 0.0f);
        a10.setDuration(blendingTime);
        a10.setStartOffset(duration + blendingTime);
        as.addAnimation(a10);

        as.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                InfoBox.this.setVisibility(View.GONE);
                InfoBox.this.clearAnimation();

            }
        });
        startAnimation(as);
    }
}

/// @endcond INTERNAL_CLASS