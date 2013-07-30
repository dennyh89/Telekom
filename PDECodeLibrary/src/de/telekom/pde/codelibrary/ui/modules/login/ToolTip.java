/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 * 
 * kdanner - 05.07.13
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
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableNotificationFrame;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;

/// @cond INTERNAL_CLASS

//----------------------------------------------------------------------------------------------------------------------
//  ToolTip
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Shows a speech bubble shaped tooltip.
 * The class is currently very simple and has not many function (e.g. you cannot controll the drawable which draws the
 * bubble), and it should be also based on PDELayerTextView (To follow the styleguide distance requirement). So it is
 * not intended to be used elsewhere. But we definitly should write this component.
 */
@SuppressWarnings("unused")
class ToolTip extends TextView {

    private PDEDrawableNotificationFrame mFrameDrawable;
    private boolean mHiding;


    public ToolTip(Context context) {
        super(context);
        init(context);
    }


    public ToolTip(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public ToolTip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(Context context) {
        mFrameDrawable = new PDEDrawableNotificationFrame();
        mFrameDrawable.setElementTriangleEnabled(true);
        mFrameDrawable.setElementTriangleTipPositionPredefined(PDEDrawableNotificationFrame.TrianglePosition.Center,
                PDEDrawableNotificationFrame.TriangleSide.SideBottom);
        PDEUtils.setViewBackgroundDrawable(this, mFrameDrawable);
        setPadding(PDEBuildingUnits.twoThirdsBU(),
                PDEBuildingUnits.oneThirdBU(),
                PDEBuildingUnits.twoThirdsBU(),
                PDEBuildingUnits.twoThirdsBU() + mFrameDrawable.getElementWantedTriangleTipDistance());
        setMinimumHeight((int)(mFrameDrawable.getElementWantedCornerRadius() * 2.0f
                + mFrameDrawable.getElementWantedTriangleTipDistance()));
        setMinimumWidth((int)(mFrameDrawable.getElementWantedCornerRadius() * 2.0f
                + mFrameDrawable.getElementWantedTriangleWidth()
                + mFrameDrawable.getElementTriangleMargin() * 2.0f));


        setGravity(Gravity.LEFT);

        setTextColor(PDEColor.valueOf("#ffffff").getIntegerColor());

    }

    /**
     * Show the speech bubble for some time (with the specified text).
     * @param text
     * @param duration of visibility in milli seconds (the time where it is fully visible, without the blending time)
     */
    public void showNotification(String text, int duration) {

        int blendingTime = 150;

        if (getVisibility()== View.VISIBLE){
            return;
        }

        setText(text);

        setVisibility(VISIBLE);

        // create and initialize the whole AnimationSet
        AnimationSet as = new AnimationSet(false);
        as.setFillAfter(true);

        // create and add the showing AlphaAnimation
        AlphaAnimation a01 = new AlphaAnimation(0f, 1.0f);
        a01.setDuration(blendingTime);
        as.addAnimation(a01);

        // create and add the hiding AlphaAnimation
        AlphaAnimation a10 = new AlphaAnimation(1f, 0.0f);
        a10.setDuration(blendingTime);
        a10.setStartOffset(duration + blendingTime);
        as.addAnimation(a10);

        // register listener to set the view to gone when the animation is finished
        as.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ToolTip.this.setVisibility(View.GONE);
                ToolTip.this.clearAnimation();

            }
        });
        startAnimation(as);
    }


    /**
     * @brief Show the tooltip with some text.
     * There is a alphaanimation to show it.
     * It will not disappear on its own, use the hide function.
     * @param text Text to show.
     */
    public void show(String text) {
        setText(text);
        show();
    }


    /**
     * @brief Show the tooltip with the already set text.
     * There is a alphaanimation to show it.
     * It will not disappear on its own, use the hide function.
     */
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


    /**
     * @brief Hide the tooltip.
     * There is an alpha animation to hide it.
     */
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
                ToolTip.this.setVisibility(View.GONE);
                ToolTip.this.clearAnimation();
            }
        });
        startAnimation(as);
    }


    /**
     * Set the tooltip triangle position.
     * @param position
     * @param side
     */
    public void setElementTriangleTipPositionAbsolute(float position, PDEDrawableNotificationFrame.TriangleSide side) {
        mFrameDrawable.setElementTriangleTipPositionAbsolute(position, side);

    }

}

/// @endcond INTERNAL_CLASS
