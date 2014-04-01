/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.notification;

import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDETextView;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableNotificationFrame;
import de.telekom.pde.codelibrary.ui.elements.text.PDELayerText;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;
import de.telekom.pde.codelibrary.ui.layout.PDEBoundedRelativeLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.LinearLayout;

/// @cond INTERNAL_CLASS

//----------------------------------------------------------------------------------------------------------------------
// PDENotificationBaseView
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Notification Base View.
 *
 * Base class for the Notification types Tool Tip and Info Flag.
 */
public class PDENotificationBaseView extends PDEBoundedRelativeLayout {

    public final static float FONTSIZE_TITLE = PDEFontHelpers.calculateFontSize(PDETypeface.sDefaultFont,
                                                                                  PDEBuildingUnits.BU());
    public final static float FONTSIZE_MESSAGE = PDEFontHelpers.calculateFontSize(PDETypeface.sDefaultFont,
                                                                             PDEBuildingUnits.pixelFromBU(5.0f / 6.0f));
    // visibility flag
    protected boolean mHiding;
    // speechbubble background
    protected PDEDrawableNotificationFrame mSpeechBubble;
    // basic layout for the notification
    protected LinearLayout mNotification;
    // textviews for title and message
    protected PDETextView mTitle;
    protected PDETextView mMessage;

    // wanted paddings
    protected int mWantedPaddingTop;
    protected int mWantedPaddingLeft;
    protected int mWantedPaddingBottom;
    protected int mWantedPaddingRight;

    /**
     * @brief Constructor.
     */
    public PDENotificationBaseView(Context context){
        super(context);
        init(null);
    }


    /**
     * @brief Constructor.
     */
    public PDENotificationBaseView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(attrs);
    }


    /**
     * @brief Init members.
     *
     * @param attrs xml attributes
     */
    protected void init(AttributeSet attrs){
        // if in developer tool (IDE) stop here
        if (isInEditMode()) return;
        // notification is hidden at beginning
        mHiding = true;
        setVisibility(GONE);

        // get needed Views / Layouts from xml
        mNotification = (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.pde_notification_base, null);
        addView(mNotification);
        mTitle = (PDETextView) mNotification.findViewById(R.id.NotificationTitle);
        mTitle.setTextSize(FONTSIZE_TITLE);
        mTitle.setAlignmentMode(PDELayerText.PDELayerTextAlignmentMode.PDELayerTextAlignmentModeCapHeight);
        mMessage = (PDETextView) mNotification.findViewById(R.id.NotificationMessage);
        mMessage.setTextSize(FONTSIZE_MESSAGE);
        mMessage.setAlignmentMode(PDELayerText.PDELayerTextAlignmentMode.PDELayerTextAlignmentModeCapHeight);

        // init wanted paddings
        mWantedPaddingBottom = 0;
        mWantedPaddingLeft = 0;
        mWantedPaddingRight = 0;
        mWantedPaddingTop = 0;

        // set speechbubble for background
        mSpeechBubble = new PDEDrawableNotificationFrame();
        mSpeechBubble.setElementTriangleEnabled(true);
        mSpeechBubble.setElementTriangleTipPositionPredefined(PDEDrawableNotificationFrame.TrianglePosition.Center,
                                                              PDEDrawableNotificationFrame.TriangleSide.SideBottom);
        PDEUtils.setViewBackgroundDrawable(mNotification, mSpeechBubble);

        // set wanted padding from the edges to the text
        setNotificationPadding(PDEBuildingUnits.BU(), 0, PDEBuildingUnits.BU(), 0, false);

        // set bold typeface for title
        mTitle.setTypeface(PDETypeface.sDefaultBold);
        // process xml attributes
        setAttributes(attrs);
        // update minimum sizes
        updateMinSizes();
    }


    /**
     * @brief Load XML attributes.
     *
     *
     */
    protected void setAttributes(AttributeSet attrs){
        String title, message;
        // security
        if (attrs == null) return;
        TypedArray sa = getContext().obtainStyledAttributes(attrs, R.styleable.PDENotificationBaseView);

        // set title
        title = sa.getString(R.styleable.PDENotificationBaseView_title);
        if (!TextUtils.isEmpty(title)) setTitleText(title);

        // set message
        message = sa.getString(R.styleable.PDENotificationBaseView_message);
        if (TextUtils.isEmpty(message)) {
            // try to get "android:text" attribute instead

            // first check if it is a resource id ...
            int resourceId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "text", -1);
            if (resourceId > 0) {
                message = getResources().getString(resourceId);
            } else {
                // otherwise handle it as string
                message = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
            }
        }

        if (!TextUtils.isEmpty(message)){
            setMessageText(message);
        }

        // set the color of the bubble background
        if (sa.hasValue(R.styleable.PDENotificationBaseView_backgroundColor)) {
            //to have dark/light style use PDEColor with color id
            int resourceID = sa.getResourceId(R.styleable.PDENotificationBaseView_backgroundColor, 0);
            if (resourceID !=0) {
                setBackgroundColorNotification(PDEColor.valueOfColorID(resourceID));
            } else {
                setBackgroundColorNotification(
                        sa.getColor(R.styleable.PDENotificationBaseView_backgroundColor, R.color.DTBlack));
            }
        }

        // set the border color
        if (sa.hasValue(R.styleable.PDENotificationBaseView_borderColor)) {
            //to have dark/light style use PDEColor with color id
            int resourceID = sa.getResourceId(R.styleable.PDENotificationBaseView_borderColor, 0);
            if (resourceID !=0) {
                setBorderColor(PDEColor.valueOfColorID(resourceID));
            } else {
                setBorderColor(sa.getColor(R.styleable.PDENotificationBaseView_borderColor, R.color.DTBlack));
            }
        }

        // set the border width
        if (sa.hasValue(R.styleable.PDENotificationBaseView_borderWidth)) {
            setBorderWidth(sa.getDimension(R.styleable.PDENotificationBaseView_borderWidth, 1.0f));
        }

        // set the color of the title text
        if (sa.hasValue(R.styleable.PDENotificationBaseView_titleTextColor)) {
            //to have dark/light style use PDEColor with color id
            int resourceID = sa.getResourceId(R.styleable.PDENotificationBaseView_titleTextColor, 0);
            if (resourceID !=0) {
                setTitleTextColor(PDEColor.valueOfColorID(resourceID));
            } else {
                setTitleTextColor(sa.getColor(R.styleable.PDENotificationBaseView_titleTextColor, R.color.DTWhite));
            }
        }

        // set the color of the message text
        if (sa.hasValue(R.styleable.PDENotificationBaseView_messageTextColor)) {
            //to have dark/light style use PDEColor with color id
            int resourceID = sa.getResourceId(R.styleable.PDENotificationBaseView_messageTextColor,0);
            if (resourceID !=0 ) {
                setMessageTextColor(PDEColor.valueOfColorID(resourceID));
            } else {
                setMessageTextColor(sa.getColor(R.styleable.PDENotificationBaseView_messageTextColor, R.color.DTWhite));
            }
        }

        // set the side of the rounded box on which the triangle should be shown
        if (sa.hasValue(R.styleable.PDENotificationBaseView_triangleSide)) {
            setTriangleSide(sa.getInt(R.styleable.PDENotificationBaseView_triangleSide, 2));
        }

        // set triangle predefined position
        if (sa.hasValue(R.styleable.PDENotificationBaseView_trianglePredefinedPosition)) {
            setTrianglePredefinedPosition(sa.getInt(R.styleable.PDENotificationBaseView_trianglePredefinedPosition, 1));
        }

        // set triangle tip position (absolute pixel value)
        if (sa.hasValue(R.styleable.PDENotificationBaseView_triangleTipPositionAbsolute)) {
            setTriangleTipPositionAbsolute(
                    sa.getFloat(R.styleable.PDENotificationBaseView_triangleTipPositionAbsolute, 0.0f));
        }

        // set triangle tip position (relative value 0.0f - 1.0f)
        if (sa.hasValue(R.styleable.PDENotificationBaseView_triangleTipPositionRelative)) {
            setTriangleTipPositionRelative(
                    sa.getFloat(R.styleable.PDENotificationBaseView_triangleTipPositionRelative, 0.5f));
        }

        // set corner radius
        if (sa.hasValue(R.styleable.PDENotificationBaseView_cornerRadius)) {
            setCornerRadius(sa.getFloat(R.styleable.PDENotificationBaseView_cornerRadius, 0.5f));
        }

        // set triangle tip width
        if (sa.hasValue(R.styleable.PDENotificationBaseView_triangleWidth)) {
            setTriangleWidth((int) sa.getDimension(R.styleable.PDENotificationBaseView_triangleWidth, 2));
        }

        // set triangle tip distance
        if (sa.hasValue(R.styleable.PDENotificationBaseView_triangleTipDistance)) {
            setTriangleTipDistance((int) sa.getDimension(R.styleable.PDENotificationBaseView_triangleTipDistance, 2));
        }

        // set triangle margin
        if (sa.hasValue(R.styleable.PDENotificationBaseView_triangleMargin)) {
            setTriangleMargin((int)sa.getDimension(R.styleable.PDENotificationBaseView_triangleMargin, 2));
        }

        // enable / disable triangle
        if (sa.hasValue(R.styleable.PDENotificationBaseView_triangleEnabled)) {
            setTriangleEnabled(sa.getBoolean(R.styleable.PDENotificationBaseView_triangleEnabled, true));
        }
    }


    /**
     * @brief Show the notification for some time (with the specified text).
     * @param duration of visibility in milli seconds (the time where it is fully visible, without the blending time)
     */
    public void showNotification(String title, String message, int duration) {

        int blendingTime = 150;

        // security
        if (getVisibility()== View.VISIBLE){
            return;
        }

        // set texts
        mTitle.setText(title);
        mMessage.setText(message);

        // we're not hidden any more
        setVisibility(VISIBLE);
        mHiding = false;

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
                PDENotificationBaseView.this.setVisibility(View.GONE);
                PDENotificationBaseView.this.clearAnimation();
                // remember that we're hidden again
                PDENotificationBaseView.this.mHiding = true;

            }
        });

        // start animation set
        startAnimation(as);
    }

    /**
     * @brief Show the notification with some text.
     * There is a alpha-animation to show it.
     * It will not disappear on its own, use the hide function.
     */
    public void show(String title, String message) {
        mTitle.setText(title);
        mMessage.setText(message);
        show();
    }


    /**
     * @brief Show the notification with the already set text.
     * There is a alpha-animation to show it.
     * It will not disappear on its own, use the hide function.
     */
    public void show() {
        int blendingTime = 150;

        // security
        if (getVisibility()== View.VISIBLE){
            return;
        }

        // we're not hidden any more
        mHiding = false;

        setVisibility(VISIBLE);
        //  set up fade in animation
        AnimationSet as = new AnimationSet(false);
        as.setFillAfter(true);

        AlphaAnimation a01 = new AlphaAnimation(0f, 1.0f);
        a01.setDuration(blendingTime);
        as.addAnimation(a01);

        // start fade in animation
        startAnimation(as);
    }


    /**
     * @brief Hide the notification.
     * There is an alpha animation to hide it.
     */
    public void hide() {
        int blendingTime = 150;

        // security
        if (getVisibility()== View.GONE){
            return;
        }

        // security
        if (mHiding) return;

        // remember that we're hidden now
        mHiding = true;

        // set up fadeout animation
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
                // set the vvisibility of the view to gone after the end of the animation
                PDENotificationBaseView.this.setVisibility(View.GONE);
                PDENotificationBaseView.this.clearAnimation();
            }
        });

        // start fadeout animation
        startAnimation(as);
    }


    /**
     * @brief Set the internal padding from text content to the borders of the speech-bubble background.
     *
     * @param left padding from the left edge to the text
     * @param top padding from the top edge to the text
     * @param right padding from the right edge to the text
     * @param bottom padding from the bottom edge to the text
     * @param force force an update, even if the wanted paddings didn't change. This is e.g. needed when the triangle side changes.
     */
    public void setNotificationPadding(int left, int top, int right, int bottom, boolean force) {
        // remember current triangle side and tip distance
        PDEDrawableNotificationFrame.TriangleSide side = mSpeechBubble.getElementTriangleSide();
        int tipDistance = mSpeechBubble.getElementWantedTriangleTipDistance();

        // anything to change?
        if (mWantedPaddingLeft == left
                && mWantedPaddingTop == top
                && mWantedPaddingRight == right
                && mWantedPaddingBottom == bottom
                && !force) {
            return;
        }

        // remember wanted padding
        mWantedPaddingLeft = left;
        mWantedPaddingTop = top;
        mWantedPaddingRight = right;
        mWantedPaddingBottom = bottom;

        // If the triangle should be shown, add the distance from the edge of the rounded rect to the triangle tip to the
        // wanted padding of the side on which the triangle should be shown.
        if (mSpeechBubble.isElementTriangleEnabled()){
            if (side == PDEDrawableNotificationFrame.TriangleSide.SideLeft) left += tipDistance;
            else if (side == PDEDrawableNotificationFrame.TriangleSide.SideTop) top += tipDistance;
            else if (side == PDEDrawableNotificationFrame.TriangleSide.SideRight) right += tipDistance;
            else if (side == PDEDrawableNotificationFrame.TriangleSide.SideBottom) bottom += tipDistance;
        }

        // set the calculated padding
        mNotification.setPadding(left, top, right, bottom);
    }


    /**
     * @brief Update the minimum sizes of the view which are needed to show the notification correctly.
     *
     * This function calculates the minimum width and height this view must have to show the notification correctly
     * depending on all the current size/configuration settings.
     */
    protected void updateMinSizes(){
        PDEDrawableNotificationFrame.TriangleSide side = mSpeechBubble.getElementTriangleSide();
        int triangleWidth, triangleMargin, triangleTipDistance;
        float cornerRadius;

        // consider triangle measures only if it is visible
        if (mSpeechBubble.isElementTriangleEnabled()){
            triangleMargin = mSpeechBubble.getElementTriangleMargin();
            triangleTipDistance = mSpeechBubble.getElementWantedTriangleTipDistance();
            triangleWidth = mSpeechBubble.getElementWantedTriangleWidth();
        } else {
            triangleMargin = triangleTipDistance = triangleWidth = 0;
        }
        cornerRadius = mSpeechBubble.getElementWantedCornerRadius();

        // distinguish between vertical and horizontal sides
        if (side == PDEDrawableNotificationFrame.TriangleSide.SideTop ||
            side == PDEDrawableNotificationFrame.TriangleSide.SideBottom) {
            mNotification.setMinimumHeight((int) (cornerRadius * 2.0f + triangleTipDistance));
            mNotification.setMinimumWidth((int) (cornerRadius * 2.0f + triangleWidth + triangleMargin * 2.0f));
        } else {
            mNotification.setMinimumHeight((int) (cornerRadius * 2.0f + triangleWidth + triangleMargin * 2.0f));
            mNotification.setMinimumWidth((int) (cornerRadius * 2.0f + triangleTipDistance));
        }
    }


    /**
     * @brief Update padding and min-sizes if triangle side has changed.
     *
     * Internal helper. Checks if the side of the rounded rect on which the triangle should be drawn has changed.
     * If this is the case we need to update the padding and the min-sizes in order to be drawn correctly.
     *
     * @param oldSide former triangle side.
     */
    protected void updateIfTriangleSideChanged(PDEDrawableNotificationFrame.TriangleSide oldSide){
        // has side changed?
        if (oldSide != mSpeechBubble.getElementTriangleSide()){
            // side of triangle changed -> force padding & minsize update
            updateSizes();
        }
    }


    /**
     * @brief Update padding and min-sizes.
     *
     * internal helper
     */
    protected void updateSizes(){
        setNotificationPadding(mWantedPaddingLeft, mWantedPaddingTop, mWantedPaddingRight, mWantedPaddingBottom, true);
        updateMinSizes();
    }



//---------------------------------------------------------------------------------------------------------------------
//  Setter / Getter
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Set the text of the title.
     *
     * @param txt text of the title.
     */
    public void setTitleText(String txt){
        mTitle.setText(txt);
    }


    /**
     * @brief Get the text of the title.
     *
     * @return text of the title
     */
    public String getTitleText() {
        return mTitle.getText();
    }


    /**
     * @brief Set the text of the message.
     *
     * @param txt text of the message
     */
    public void setMessageText(String txt) {
        mMessage.setText(txt);
    }

    /**
     * @brief Get the text of the message.
     *
     * @return message text
     */
    public String getMessageText(){
        return mMessage.getText();
    }


    /**
     * @brief Check if the notification is currently shown on screen.
     *
     * @return true -> visible; false -> invisible
     */
    public boolean isShowing(){
        return !mHiding;
    }


    /**
     * @brief Set the color of the background (of the speech-bubble).
     *
     * @param color the color of the background (of the speech-bubble).
     */
    public void setBackgroundColorNotification(PDEColor color){
        mSpeechBubble.setElementBackgroundColor(color);
    }


    /**
     * @brief Set the color of the background (of the speech-bubble).
     *
     * @param color the color of the background (of the speech-bubble).
     */
    public void setBackgroundColorNotification(int color) {
        setBackgroundColorNotification(PDEColor.valueOf(color));
    }


    /**
     * @brief Get the color of the background (of the speech-bubble).
     *
     * @return the color of the background
     */
    @SuppressWarnings("unused")
    public PDEColor getBackgroundColorNotification(){
        return mSpeechBubble.getElementBackgroundColor();
    }


    /**
     * @brief Set the color of the border.
     *
     * @param color the color of the border
     */
    public void setBorderColor(PDEColor color){
        mSpeechBubble.setElementBorderColor(color);
    }


    /**
     * @brief Set the color of the border.
     *
     * @param color the color of the border
     */
    public void setBorderColor(int color){
        setBorderColor(PDEColor.valueOf(color));
    }


    /**
     * @brief Get the color of the border.
     *
     * @return the color of the border
     */
    @SuppressWarnings("unused")
    public PDEColor getBorderColor(){
        return mSpeechBubble.getElementBorderColor();
    }


    /**
     * @brief Set the width of the border.
     *
     * @param width the width of the border
     */
    public void setBorderWidth(float width){
        mSpeechBubble.setElementBorderWidth(width);
    }


    /**
     * @brief Get the width of the border.
     *
     * @return the width of the border
     */
    @SuppressWarnings("unused")
    public float getBorderWidth(){
        return mSpeechBubble.getElementBorderWidth();
    }


    /**
     * @brief Set the color of the title text.
     *
     * @param color the color of the title text
     */
    public void setTitleTextColor(PDEColor color){
        setTitleTextColor(color.getIntegerColor());
    }


    /**
     * @brief Set the color of the title text.
     *
     * @param color the color of the title text
     */
    public void setTitleTextColor(int color){
        mTitle.setTextColor(color);
    }


    /**
     * @brief Get the color of the title text.
     *
     * @return the color of the title text
     */
    @SuppressWarnings("unused")
    public PDEColor getTitleTextColor() {
        return mTitle.getTextColor();
    }


    /**
     * @brief Set the color of the message text.
     *
     * @param color color of the message text
     */
    public void setMessageTextColor(PDEColor color){
        setMessageTextColor(color.getIntegerColor());
    }


    /**
     * @brief Set the color of the message text.
     *
     * @param color color of the message text
     */
    public void setMessageTextColor(int color){
        mMessage.setTextColor(color);
    }


    /**
     * @brief Get the color of the message text.
     *
     * @return the color of the message text
     */
    @SuppressWarnings("unused")
    public PDEColor getMessageTextColor() {
        return mMessage.getTextColor();
    }


    /**
     * @brief  Defines on which side/edge of the rounded rect the triangle should be drawn. (internal)
     *
     * This function is only used internally for xml-attribute
     * setting. It translates the xml-value to the public setTriangleSide function.
     *
     * @param side the side / edge of the rounded rect where the triangle should be drawn.
     */
    protected void setTriangleSide(int side){
        PDEDrawableNotificationFrame.TriangleSide tSide;
        tSide = PDEDrawableNotificationFrame.TriangleSide.values()[side];
        setTriangleSide(tSide);
    }


    /**
     * @brief  Defines on which side/edge of the rounded rect the triangle should be drawn.
     *
     * @param side the side / edge of the rounded rect where the triangle should be drawn.
     */
    public void setTriangleSide(PDEDrawableNotificationFrame.TriangleSide side){
        // It's probably better to operate with the wanted sizes here.
        if (mSpeechBubble.isElementTriangleTipPositionRelative()){
            //setTriangleTipPositionRelative(mSpeechBubble.getElementTriangleTipPosition(), side);
            setTriangleTipPositionRelative(mSpeechBubble.getElementWantedTriangleTipPosition(), side);
        } else {
            //setTriangleTipPositionAbsolute(mSpeechBubble.getElementTriangleTipPosition(), side);
            setTriangleTipPositionAbsolute(mSpeechBubble.getElementWantedTriangleTipPosition(), side);
        }
    }


    /**
     * @brief Get the side of the rounded rect the triangle is attached to.
     *
     * @return the side of the rounded rect the triangle is attached to.
     */
    @SuppressWarnings("unused")
    public PDEDrawableNotificationFrame.TriangleSide getTriangleSide(){
        return mSpeechBubble.getElementTriangleSide();
    }


    /**
     * @brief Set Triangle at predefined positions (internal).
     *
     * The styleguide predefines 12 positions of the triangle. Left, Center, Right for the horizontal edges and Top,
     * Center, Bottom for the vertical edges. With this function these positions can easily be set by delivering the
     * desired position at the side which is currently set. This function is only used internally for xml-attribute
     * setting. It translates the xml-value to the public setTrianglePredefinedPosition function.
     *
     * @param position A predefined triangle position (left/center/right/top/bottom).
     */
    protected void setTrianglePredefinedPosition(int position){
        PDEDrawableNotificationFrame.TrianglePosition tPos;
        tPos = PDEDrawableNotificationFrame.TrianglePosition.values()[position];
        setTrianglePredefinedPosition(tPos);
    }


    /**
     * @brief Set Triangle at predefined positions
     *
     * The styleguide predefines 12 positions of the triangle. Left, Center, Right for the horizontal edges and Top,
     * Center, Bottom for the vertical edges.
     * With this function these positions can easily be set by delivering the desired position at the side which is
     * currently set.
     *
     * @param position A predefined triangle position (left/center/right/top/bottom).
     */
    public void setTrianglePredefinedPosition(PDEDrawableNotificationFrame.TrianglePosition position){
        setTrianglePredefinedPosition(position, mSpeechBubble.getElementTriangleSide());
    }


    /**
     * @brief Set Triangle at predefined positions
     *
     * The styleguide predefines 12 positions of the triangle. Left, Center, Right for the horizontal edges and Top,
     * Center, Bottom for the vertical edges.
     * With this function these positions can easily be set by delivering the side on which the triangle should be
     * drawn and the desired position at this side.
     *
     * @param position A predefined triangle position (left/center/right/top/bottom).
     * @param side The side of the rounded rect on which the triangle is attached.
     */
    public void setTrianglePredefinedPosition(PDEDrawableNotificationFrame.TrianglePosition position,
                                              PDEDrawableNotificationFrame.TriangleSide side){
        PDEDrawableNotificationFrame.TriangleSide oldSide = mSpeechBubble.getElementTriangleSide();
        mSpeechBubble.setElementTriangleTipPositionPredefined(position,side);
        updateIfTriangleSideChanged(oldSide);
    }


    /**
     * @brief Get the absolute pixel position of the triangle tip.
     *
     * @return the absolute pixel position of the triangle tip.
     */
    @SuppressWarnings("unused")
    public float getTriangleTipPosition(){
        return mSpeechBubble.getElementTriangleTipPosition();
    }


    /**
     * @brief Get the wanted absolute pixel position of the triangle tip.
     *
     * @return the wanted absolute pixel position of the triangle tip.
     */
    @SuppressWarnings("unused")
    public float getWantedTriangleTipPosition(){
        return mSpeechBubble.getElementWantedTriangleTipPosition();
    }


    /**
     * @brief Checks current triangle position is a relative or an absolute value.
     *
     * @return true-> relative position, false -> absolute position
     */
    @SuppressWarnings("unused")
    public boolean isTriangleTipPositionRelative(){
        return mSpeechBubble.isElementTriangleTipPositionRelative();
    }


    /**
     * @brief Set absolute pixel position of triangle tip.
     *
     * With this function it's possible to define the pixel position of the triangle tip at once. The point of origin
     * for the pixel positions is on the left for the horizontal sides and on top for the vertical sides.
     *
     * @param positionAbsolute The absolute pixel position of the triangle tip.
     */
    public void setTriangleTipPositionAbsolute(float positionAbsolute){
        setTriangleTipPositionAbsolute(positionAbsolute, mSpeechBubble.getElementTriangleSide());
    }


    /**
     * @brief Set absolute pixel position of triangle tip and the side at which the triangle should be drawn.
     *
     * The triangle can be attached to each of the four sides (top/right/bottom/left) of the rounded rect and on every
     * valid pixel position along the particular side. With this function it's possible to define the side and the pixel
     * position of the triangle tip at once. The point of origin for the pixel positions is on the left for the
     * horizontal sides and on top for the vertical sides.
     *
     * @param positionAbsolute The absolute pixel position of the triangle tip.
     * @param side The side of the rounded rect on which the triangle is attached.
     */
    public void setTriangleTipPositionAbsolute(float positionAbsolute, PDEDrawableNotificationFrame.TriangleSide side){
        // remember former side
        PDEDrawableNotificationFrame.TriangleSide oldSide = mSpeechBubble.getElementTriangleSide();

        mSpeechBubble.setElementTriangleTipPositionAbsolute(positionAbsolute,side);
        // do update if the side changed
        updateIfTriangleSideChanged(oldSide);
    }


    /**
     * @brief Set relative position of triangle tip
     *
     * The positioning is relative to the length of the particular side.
     * The point of origin for the pixel positions is on the left for the horizontal sides and on top for the vertical
     * sides.
     *
     * @param positionRelative The relative position of the triangle tip.
     */
    public void setTriangleTipPositionRelative(float positionRelative){
        setTriangleTipPositionRelative(positionRelative,mSpeechBubble.getElementTriangleSide());
    }


    /**
     * @brief Set relative position of triangle tip and the side at which the triangle should be drawn
     *
     * The triangle can be attached to each of the four sides (top/right/bottom/left) of the rounded rect and on every
     * valid position along the particular side. With this function it's possible to define the side and the relative
     * position of the triangle tip at once. The positioning is relative to the length of the particular side.
     * The point of origin for the pixel positions is on the left for the horizontal sides and on top for the vertical
     * sides.
     *
     * @param positionRelative The relative position of the triangle tip.
     * @param side The side of the rounded rect on which the triangle is attached.
     */
    public void setTriangleTipPositionRelative(float positionRelative, PDEDrawableNotificationFrame.TriangleSide side){
        PDEDrawableNotificationFrame.TriangleSide oldSide = mSpeechBubble.getElementTriangleSide();

        mSpeechBubble.setElementTriangleTipPositionRelative(positionRelative, side);
        // some sizes have to be updated if the side changes on which the triangle should be drawn
        updateIfTriangleSideChanged(oldSide);
    }


    /**
     * @brief Set corner radius.
     *
     * @param radius The new radius of the rounded corners.
     */
    public void setCornerRadius(float radius){
        mSpeechBubble.setElementCornerRadius(radius);
    }


    /**
     * @brief Get corner radius.
     *
     * @return The radius of the rounded corners.
     */
    @SuppressWarnings("unused")
    public float getCornerRadius(){
        return mSpeechBubble.getElementCornerRadius();
    }


    /**
     * @brief Get wanted corner radius.
     *
     * @return The wanted radius of the rounded corners.
     */
    @SuppressWarnings("unused")
    public float getWantedCornerRadius(){
        return mSpeechBubble.getElementWantedCornerRadius();
    }


    /**
     * @brief Set width of the triangle.
     *
     * @param width The width/length of the triangle side (base line) which is attached to the rounded rect.
     */
    public void setTriangleWidth(int width){
        mSpeechBubble.setElementTriangleWidth(width);
        updateMinSizes();
    }


    /**
     * @brief Get width of the triangle.
     *
     * @return The width/length of the triangle side (base line) which is attached to the rounded rect.
     */
    @SuppressWarnings("unused")
    public int getTriangleWidth(){
        return mSpeechBubble.getElementTriangleWidth();
    }


    /**
     * @brief Get wanted width of the triangle.
     *
     * @return The width/length of the triangle side (base line) which is attached to the rounded rect.
     */
    @SuppressWarnings("unused")
    public int getWantedTriangleWidth(){
        return mSpeechBubble.getElementWantedTriangleWidth();
    }


    /**
     * @brief Set distance between triangle base line and triangle tip.
     *
     * @param distance The distance between the base line of the triangle and the triangle tip.
     */
    public void setTriangleTipDistance(int distance){
        mSpeechBubble.setElementTriangleTipDistance(distance);
        updateSizes();
    }


    /**
     * @brief Get distance between triangle base line and triangle tip.
     *
     * @return The distance between the base line of the triangle and the triangle tip.
     */
    @SuppressWarnings("unused")
    public int getTriangleTipDistance(){
        return mSpeechBubble.getElementTriangleTipDistance();
    }


    /**
     * @brief Get the wanted distance between triangle base line and triangle tip.
     *
     * @return The wanted distance between the base line of the triangle and the triangle tip.
     */
    @SuppressWarnings("unused")
    public int getWantedTriangleTipDistance(){
        return mSpeechBubble.getElementWantedTriangleTipDistance();
    }


    /**
     * @brief Set margin which is kept between the triangle and the rounded corners.
     *
     * @param margin The margin between triangle and rounded corners.
     */
    public void setTriangleMargin(int margin){
        mSpeechBubble.setElementTriangleMargin(margin);
        // has influence on the min sizes of the view, so update them
        updateMinSizes();
    }


    /**
     * @brief Get margin which is kept between the triangle and the rounded corners.
     *
     * @return The margin between triangle and rounded corners.
     */
    @SuppressWarnings("unused")
    public int getTriangleMargin(){
        return mSpeechBubble.getElementTriangleMargin();
    }


    /**
     * @brief Enable / Disable the small triangle.
     *
     * @param enabled true-> triangle visible, false -> triangle invisible
     */
    public void setTriangleEnabled(boolean enabled){
        mSpeechBubble.setElementTriangleEnabled(enabled);
        // the triangle needs space, so adapt our sizes
        updateSizes();
    }


    /**
     * @brief Checks if small triangle is enabled or disabled.
     *
     * @return true-> triangle visible, false -> triangle invisible
     */
    @SuppressWarnings("unused")
    public boolean isTriangleEnabled(){
        return mSpeechBubble.isElementTriangleEnabled();
    }
}


/// @endcond INTERNAL_CLASS