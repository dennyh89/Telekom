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
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableNotificationFrame.TrianglePosition;
import de.telekom.pde.codelibrary.ui.elements.text.PDELayerText;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;
import de.telekom.pde.codelibrary.ui.layout.PDEBoundedRelativeLayout;

import android.annotation.SuppressLint;
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

//----------------------------------------------------------------------------------------------------------------------
// PDENotificationBase
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Notification Base View.
 *
 * Base class for the Notification types Tool Tip and Info Flag.
 */
public class PDENotificationBase extends PDEBoundedRelativeLayout {

    protected final static float FONTSIZE_TITLE = PDEFontHelpers.calculateFontSize(PDETypeface.sDefaultFont,
                                                                                PDEBuildingUnits.BU());
    protected final static float FONTSIZE_MESSAGE = PDEFontHelpers.calculateFontSize(PDETypeface.sDefaultFont,
                                                                                  PDEBuildingUnits.pixelFromBU(
                                                                                          5.0f / 6.0f));
    protected final int SIDE_PADDING = PDEBuildingUnits.BU();

    protected static final int FADE_TIME = 150;

    // visibility flag
    protected boolean mHiding;
    // speech bubble background
    protected PDEDrawableNotificationFrame mSpeechBubble;
    // basic layout for the notification
    protected LinearLayout mNotification;
    // text views for title and message
    protected PDETextView mTitle;
    protected PDETextView mMessage;

    // wanted padding
    protected int mWantedSpeechBubblePaddingTop;
    protected int mWantedSpeechBubblePaddingLeft;
    protected int mWantedSpeechBubblePaddingBottom;
    protected int mWantedSpeechBubblePaddingRight;

    // minimum sizes (needed because the getters can't be used before API Lvl 16)
    protected int mMinimumHeight;
    protected int mMinimumWidth;


    /**
     * @brief Constructor.
     */
    public PDENotificationBase(Context context) {
        super(context);
        init(context, null);
    }


    /**
     * @brief Constructor.
     */
    public PDENotificationBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    /**
     * @brief Init members.
     *
     * @param attrs xml attributes
     */
    @SuppressLint("InflateParams")
    protected void init(Context context, AttributeSet attrs) {
        // if in developer tool (IDE) stop here
        if (isInEditMode()) return;
        // notification is hidden at beginning
        mHiding = true;
        setVisibility(GONE);

        // get needed Views / Layouts from xml without root view
        // ( i think this is intended, so we add @SuppressLint("InflateParams"))
        mNotification = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.pde_notification_base, null);
        if (mNotification == null) {
            // that should not happen
            return;
        }
        addView(mNotification);
        mTitle = (PDETextView) mNotification.findViewById(R.id.NotificationTitle);
        mTitle.setTextSize(FONTSIZE_TITLE);
        mTitle.setAlignmentMode(PDELayerText.PDELayerTextAlignmentMode.PDELayerTextAlignmentModeCapHeight);
        mMessage = (PDETextView) mNotification.findViewById(R.id.NotificationMessage);
        mMessage.setTextSize(FONTSIZE_MESSAGE);
        mMessage.setAlignmentMode(PDELayerText.PDELayerTextAlignmentMode.PDELayerTextAlignmentModeCapHeight);

        // init wanted padding
        mWantedSpeechBubblePaddingBottom = 0;
        mWantedSpeechBubblePaddingLeft = 0;
        mWantedSpeechBubblePaddingRight = 0;
        mWantedSpeechBubblePaddingTop = 0;

        // init min sizes
        mMinimumHeight = 0;
        mMinimumWidth = 0;

        // set speech bubble for background
        mSpeechBubble = new PDEDrawableNotificationFrame();
        mSpeechBubble.setElementTriangleEnabled(true);
        mSpeechBubble.setElementTriangleTipPositionPredefined(PDEDrawableNotificationFrame.TrianglePosition.Center,
                                                              PDEDrawableNotificationFrame.TriangleSide.SideBottom);
        PDEUtils.setViewBackgroundDrawable(mNotification, mSpeechBubble);

        // set wanted padding from the edges to the text
        setSpeechBubblePadding(SIDE_PADDING, 0, SIDE_PADDING, 0);

        // set bold typeface for title
        mTitle.setTypeface(PDETypeface.sDefaultBold);
        // process xml attributes
        setAttributes(context, attrs);
        // update minimum sizes
        updateMinSizes();
    }


    /**
     * @brief Load XML attributes.
     */
    protected void setAttributes(Context context, AttributeSet attrs) {
        String title = null;
        String message = null;

        // security
        if (attrs == null) return;

        TypedArray sa = context.obtainStyledAttributes(attrs, R.styleable.PDENotificationBase);

        if (sa != null) {
            title = sa.getString(R.styleable.PDENotificationBase_pde_title);
            message = sa.getString(R.styleable.PDENotificationBase_pde_message);
        }
        // set title
        if (!TextUtils.isEmpty(title)) setTitleText(title);

        // set message
        if (TextUtils.isEmpty(message)) {
            // try to get "android:text" attribute instead

            // first check if it is a resource id ...
            int resourceId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "text", -1);
            if (resourceId > 0) {
                message = context.getResources().getString(resourceId);
            } else {
                // otherwise handle it as string
                message = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
            }
        }

        if (!TextUtils.isEmpty(message)) {
            setMessageText(message);
        }

        if (sa != null) {
            // set the color of the bubble background
            if (sa.hasValue(R.styleable.PDENotificationBase_pde_backgroundColor)) {
                //to have dark/light style use PDEColor with color id
                int resourceID = sa.getResourceId(R.styleable.PDENotificationBase_pde_backgroundColor, 0);
                if (resourceID != 0) {
                    setBackgroundColorNotification(PDEColor.valueOfColorID(resourceID));
                } else {
                    setBackgroundColorNotification(
                            sa.getColor(R.styleable.PDENotificationBase_pde_backgroundColor, R.color.DTBlack));
                }
            }

            // set the border color
            if (sa.hasValue(R.styleable.PDENotificationBase_pde_borderColor)) {
                //to have dark/light style use PDEColor with color id
                int resourceID = sa.getResourceId(R.styleable.PDENotificationBase_pde_borderColor, 0);
                if (resourceID != 0) {
                    setBorderColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setBorderColor(sa.getColor(R.styleable.PDENotificationBase_pde_borderColor, R.color.DTBlack));
                }
            }

            // set the border width
            if (sa.hasValue(R.styleable.PDENotificationBase_pde_borderWidth)) {
                setBorderWidth(sa.getDimension(R.styleable.PDENotificationBase_pde_borderWidth, 1.0f));
            }

            // set the color of the title text
            if (sa.hasValue(R.styleable.PDENotificationBase_pde_titleTextColor)) {
                //to have dark/light style use PDEColor with color id
                int resourceID = sa.getResourceId(R.styleable.PDENotificationBase_pde_titleTextColor, 0);
                if (resourceID != 0) {
                    setTitleTextColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setTitleTextColor(sa.getColor(R.styleable.PDENotificationBase_pde_titleTextColor, R.color.DTWhite));
                }
            }

            // set the color of the message text
            if (sa.hasValue(R.styleable.PDENotificationBase_pde_messageTextColor)) {
                //to have dark/light style use PDEColor with color id
                int resourceID = sa.getResourceId(R.styleable.PDENotificationBase_pde_messageTextColor, 0);
                if (resourceID != 0) {
                    setMessageTextColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setMessageTextColor(sa.getColor(R.styleable.PDENotificationBase_pde_messageTextColor, R.color.DTWhite));
                }
            }

            // set the side of the rounded box on which the triangle should be shown
            if (sa.hasValue(R.styleable.PDENotificationBase_pde_triangleSide)) {
                setTriangleSide(sa.getInt(R.styleable.PDENotificationBase_pde_triangleSide,
                                          PDEDrawableNotificationFrame.TriangleSide.SideBottom.ordinal()));
            }

            // set triangle predefined position
            if (sa.hasValue(R.styleable.PDENotificationBase_pde_trianglePredefinedPosition)) {
                setTrianglePredefinedPosition(sa.getInt(R.styleable.PDENotificationBase_pde_trianglePredefinedPosition,
                                                        TrianglePosition.Center.ordinal()));
            }

            // set triangle tip position (absolute pixel value)
            if (sa.hasValue(R.styleable.PDENotificationBase_pde_triangleTipPositionAbsolute)) {
                setTriangleTipPositionAbsolute(
                        sa.getFloat(R.styleable.PDENotificationBase_pde_triangleTipPositionAbsolute, 0.0f));
            }

            // set triangle tip position (relative value 0.0f - 1.0f)
            if (sa.hasValue(R.styleable.PDENotificationBase_pde_triangleTipPositionRelative)) {
                setTriangleTipPositionRelative(
                        sa.getFloat(R.styleable.PDENotificationBase_pde_triangleTipPositionRelative, 0.5f));
            }

            // set corner radius
            if (sa.hasValue(R.styleable.PDENotificationBase_pde_cornerRadius)) {
                setCornerRadius(sa.getFloat(R.styleable.PDENotificationBase_pde_cornerRadius,
                                            PDEDrawableNotificationFrame.DEFAULT_CORNER_RADIUS));
            }

            // set triangle tip width
            if (sa.hasValue(R.styleable.PDENotificationBase_pde_triangleWidth)) {
                setTriangleWidth((int) sa.getDimension(R.styleable.PDENotificationBase_pde_triangleWidth,
                                                       PDEDrawableNotificationFrame.DEFAULT_TRIANGLE_WIDTH));
            }

            // set triangle tip distance
            if (sa.hasValue(R.styleable.PDENotificationBase_pde_triangleTipDistance)) {
                setTriangleTipDistance((int) sa.getDimension(R.styleable.PDENotificationBase_pde_triangleTipDistance,
                                                             PDEDrawableNotificationFrame.DEFAULT_TRIANGLE_TIP_DISTANCE));
            }

            // set triangle margin
            if (sa.hasValue(R.styleable.PDENotificationBase_pde_triangleMargin)) {
                setTriangleMargin((int) sa.getDimension(R.styleable.PDENotificationBase_pde_triangleMargin,
                                                        PDEDrawableNotificationFrame.DEFAULT_TRIANGLE_MARGIN));
            }

            // enable / disable triangle
            if (sa.hasValue(R.styleable.PDENotificationBase_pde_triangleEnabled)) {
                setTriangleEnabled(sa.getBoolean(R.styleable.PDENotificationBase_pde_triangleEnabled, true));
            }

            // show/hide at startup
            if (sa.hasValue(R.styleable.PDENotificationBase_pde_show)) {
                if (sa.getBoolean(R.styleable.PDENotificationBase_pde_show, false)) {
                    show(0);
                }
            }

            sa.recycle();
        }
    }


    /**
     * @brief Show the notification for some time (with the specified text).
     *
     * @param duration of visibility in milli seconds (the time where it is fully visible, without the blending time)
     */
    public void showNotification(String title, String message, int duration) {

        int blendingTime = FADE_TIME;

        // security
        if (getVisibility() == View.VISIBLE) {
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
                PDENotificationBase.this.setVisibility(View.GONE);
                PDENotificationBase.this.clearAnimation();
                // remember that we're hidden again
                PDENotificationBase.this.mHiding = true;

            }
        });

        // start animation set
        startAnimation(as);
    }


    /**
     * @brief Show the notification with some text.
     *
     * There is a alpha-animation to show it.
     * It will not disappear on its own, use the hide function.
     */
    @SuppressWarnings("unused")
    public void showNotification(String title, String message) {
        mTitle.setText(title);
        mMessage.setText(message);
        show();
    }


    /**
     * @brief Show the notification with the already set text.
     *
     * There is a alpha-animation to show it.
     * It will not disappear on its own, use the hide function.
     */
    public void show() {
        show(FADE_TIME);
    }


    /**
     * @brief Show the notification with the already set text with given time to fade.
     *
     * There is a alpha-animation to show it.
     * It will not disappear on its own, use the hide function.
     */
    public void show(int fadeTime) {
        // security
        if (getVisibility() == View.VISIBLE) {
            return;
        }

        // we're not hidden any more
        mHiding = false;

        setVisibility(VISIBLE);
        //  set up fade in animation
        AnimationSet as = new AnimationSet(false);
        as.setFillAfter(true);

        AlphaAnimation a01 = new AlphaAnimation(0f, 1.0f);
        a01.setDuration(fadeTime);
        as.addAnimation(a01);

        // start fade in animation
        startAnimation(as);
    }


    /**
     * @brief Show the notification with the already set text for a defined duration.
     *
     * There is a alpha-animation to show it.
     * It will automatically disappear.
     */
    @SuppressWarnings("unused")
    public void showForDuration(int duration) {
        showNotification(getTitleText(), getMessageText(), duration);
    }


    /**
     * @brief Hide the notification.
     *
     * There is an alpha animation to hide it.
     */
    public void hide() {
        hide(FADE_TIME);
    }


    /**
     * @brief Hide the notification in the given time.
     *
     * There is an alpha animation to hide it.
     */
    public void hide(int fadeTime) {
        // security
        if (getVisibility() == View.GONE) {
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
        a10.setDuration(fadeTime);
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
                // set the visibility of the view to gone after the end of the animation
                PDENotificationBase.this.setVisibility(View.GONE);
                PDENotificationBase.this.clearAnimation();
            }
        });

        // start fadeout animation
        startAnimation(as);
    }


//----------------------------------------------------------------------------------------------------------------------
//  Setter / Getter
//------------------------------------------------------------------------------------0---------------------------------


    /**
     * @brief Set the text of the title.
     *
     * @param txt text of the title.
     */
    public void setTitleText(String txt) {
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
    public String getMessageText() {
        return mMessage.getText();
    }


    /**
     * @brief Check if the notification is currently shown on screen.
     *
     * @return true -> visible; false -> invisible
     */
    public boolean isShowing() {
        return !mHiding;
    }


    /**
     * @brief Set the color of the background (of the speech-bubble).
     *
     * @param color the color of the background (of the speech-bubble).
     */
    public void setBackgroundColorNotification(PDEColor color) {
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
    public PDEColor getBackgroundColorNotification() {
        return mSpeechBubble.getElementBackgroundColor();
    }


    /**
     * @brief Set the color of the border.
     *
     * @param color the color of the border
     */
    public void setBorderColor(PDEColor color) {
        mSpeechBubble.setElementBorderColor(color);
    }


    /**
     * @brief Set the color of the border.
     *
     * @param color the color of the border
     */
    public void setBorderColor(int color) {
        setBorderColor(PDEColor.valueOf(color));
    }


    /**
     * @brief Get the color of the border.
     *
     * @return the color of the border
     */
    @SuppressWarnings("unused")
    public PDEColor getBorderColor() {
        return mSpeechBubble.getElementBorderColor();
    }


    /**
     * @brief Set the width of the border.
     *
     * @param width the width of the border
     */
    public void setBorderWidth(float width) {
        mSpeechBubble.setElementBorderWidth(width);
    }


    /**
     * @brief Get the width of the border.
     *
     * @return the width of the border
     */
    @SuppressWarnings("unused")
    public float getBorderWidth() {
        return mSpeechBubble.getElementBorderWidth();
    }


    /**
     * @brief Set the color of the title text.
     *
     * @param color the color of the title text
     */
    public void setTitleTextColor(PDEColor color) {
        setTitleTextColor(color.getIntegerColor());
    }


    /**
     * @brief Set the color of the title text.
     *
     * @param color the color of the title text
     */
    public void setTitleTextColor(int color) {
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
    public void setMessageTextColor(PDEColor color) {
        setMessageTextColor(color.getIntegerColor());
    }


    /**
     * @brief Set the color of the message text.
     *
     * @param color color of the message text
     */
    public void setMessageTextColor(int color) {
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
     * @brief Defines on which side/edge of the rounded rect the triangle should be drawn. (internal)
     *
     * This function is only used internally for xml-attribute
     * setting. It translates the xml-value to the public setTriangleSide function.
     *
     * @param side the side / edge of the rounded rect where the triangle should be drawn.
     */
    protected void setTriangleSide(int side) {
        PDEDrawableNotificationFrame.TriangleSide tSide;
        tSide = PDEDrawableNotificationFrame.TriangleSide.values()[side];
        setTriangleSide(tSide);
    }


    /**
     * @brief Defines on which side/edge of the rounded rect the triangle should be drawn.
     *
     * @param side the side / edge of the rounded rect where the triangle should be drawn.
     */
    public void setTriangleSide(PDEDrawableNotificationFrame.TriangleSide side) {
        // Check if current tip position is a relative or an absolute value and call the appropriate function
        if (mSpeechBubble.isElementTriangleTipPositionRelative()) {
            setTriangleTipPositionRelative(mSpeechBubble.getElementWantedTriangleTipPosition(), side);
        } else {
            setTriangleTipPositionAbsolute(mSpeechBubble.getElementWantedTriangleTipPosition(), side);
        }
    }


    /**
     * @brief Get the side of the rounded rect the triangle is attached to.
     *
     * @return the side of the rounded rect the triangle is attached to.
     */
    @SuppressWarnings("unused")
    public PDEDrawableNotificationFrame.TriangleSide getTriangleSide() {
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
    protected void setTrianglePredefinedPosition(int position) {
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
    public void setTrianglePredefinedPosition(PDEDrawableNotificationFrame.TrianglePosition position) {
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
     * @param side     The side of the rounded rect on which the triangle is attached.
     */
    public void setTrianglePredefinedPosition(PDEDrawableNotificationFrame.TrianglePosition position,
                                              PDEDrawableNotificationFrame.TriangleSide side) {
        mSpeechBubble.setElementTriangleTipPositionPredefined(position, side);
        // trigger update, because side might have changed
        updateSizes();
    }


    /**
     * @brief Get the absolute pixel position of the triangle tip.
     *
     * @return the absolute pixel position of the triangle tip.
     */
    @SuppressWarnings("unused")
    public float getTriangleTipPosition() {
        return mSpeechBubble.getElementTriangleTipPosition();
    }


    /**
     * @brief Get the wanted absolute pixel position of the triangle tip.
     *
     * @return the wanted absolute pixel position of the triangle tip.
     */
    @SuppressWarnings("unused")
    public float getWantedTriangleTipPosition() {
        return mSpeechBubble.getElementWantedTriangleTipPosition();
    }


    /**
     * @brief Checks current triangle position is a relative or an absolute value.
     *
     * @return true-> relative position, false -> absolute position
     */
    @SuppressWarnings("unused")
    public boolean isTriangleTipPositionRelative() {
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
    public void setTriangleTipPositionAbsolute(float positionAbsolute) {
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
     * @param side             The side of the rounded rect on which the triangle is attached.
     */
    public void setTriangleTipPositionAbsolute(float positionAbsolute, PDEDrawableNotificationFrame.TriangleSide side) {
        // set new values
        mSpeechBubble.setElementTriangleTipPositionAbsolute(positionAbsolute, side);
        // trigger update, because side might have changed
        updateSizes();
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
    public void setTriangleTipPositionRelative(float positionRelative) {
        setTriangleTipPositionRelative(positionRelative, mSpeechBubble.getElementTriangleSide());
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
     * @param side             The side of the rounded rect on which the triangle is attached.
     * @param positionRelative The relative position of the triangle tip.
     */
    public void setTriangleTipPositionRelative(float positionRelative, PDEDrawableNotificationFrame.TriangleSide side) {
        // set new values
        mSpeechBubble.setElementTriangleTipPositionRelative(positionRelative, side);
        // trigger update, because side might have changed
        updateSizes();
    }


    /**
     * @brief Set corner radius.
     *
     * @param radius The new radius of the rounded corners.
     */
    public void setCornerRadius(float radius) {
        mSpeechBubble.setElementCornerRadius(radius);
    }


    /**
     * @brief Get corner radius.
     *
     * @return The radius of the rounded corners.
     */
    @SuppressWarnings("unused")
    public float getCornerRadius() {
        return mSpeechBubble.getElementCornerRadius();
    }


    /**
     * @brief Get wanted corner radius.
     *
     * @return The wanted radius of the rounded corners.
     */
    @SuppressWarnings("unused")
    public float getWantedCornerRadius() {
        return mSpeechBubble.getElementWantedCornerRadius();
    }


    /**
     * @brief Set width of the triangle.
     *
     * @param width The width/length of the triangle side (base line) which is attached to the rounded rect.
     */
    public void setTriangleWidth(int width) {
        mSpeechBubble.setElementTriangleWidth(width);
        updateMinSizes();
    }


    /**
     * @brief Get width of the triangle.
     *
     * @return The width/length of the triangle side (base line) which is attached to the rounded rect.
     */
    @SuppressWarnings("unused")
    public int getTriangleWidth() {
        return mSpeechBubble.getElementTriangleWidth();
    }


    /**
     * @brief Get wanted width of the triangle.
     *
     * @return The width/length of the triangle side (base line) which is attached to the rounded rect.
     */
    @SuppressWarnings("unused")
    public int getWantedTriangleWidth() {
        return mSpeechBubble.getElementWantedTriangleWidth();
    }


    /**
     * @brief Set distance between triangle base line and triangle tip.
     *
     * @param distance The distance between the base line of the triangle and the triangle tip.
     */
    public void setTriangleTipDistance(int distance) {
        mSpeechBubble.setElementTriangleTipDistance(distance);
        updateSizes();
    }


    /**
     * @brief Get distance between triangle base line and triangle tip.
     *
     * @return The distance between the base line of the triangle and the triangle tip.
     */
    @SuppressWarnings("unused")
    public int getTriangleTipDistance() {
        return mSpeechBubble.getElementTriangleTipDistance();
    }


    /**
     * @brief Get the wanted distance between triangle base line and triangle tip.
     *
     * @return The wanted distance between the base line of the triangle and the triangle tip.
     */
    @SuppressWarnings("unused")
    public int getWantedTriangleTipDistance() {
        return mSpeechBubble.getElementWantedTriangleTipDistance();
    }


    /**
     * @brief Set margin which is kept between the triangle and the rounded corners.
     *
     * @param margin The margin between triangle and rounded corners.
     */
    public void setTriangleMargin(int margin) {
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
    public int getTriangleMargin() {
        return mSpeechBubble.getElementTriangleMargin();
    }


    /**
     * @brief Enable / Disable the small triangle.
     *
     * @param enabled true-> triangle visible, false -> triangle invisible
     */
    public void setTriangleEnabled(boolean enabled) {
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
    public boolean isTriangleEnabled() {
        return mSpeechBubble.isElementTriangleEnabled();
    }


// ----------------- padding handling -----------------------------------------


    // ToDo: Before finalizing the interface of the class, check if we want to keep the word "wanted" in some of the names.


    /**
     * @brief Set the internal padding from text content to the borders of the speech-bubble background.
     *
     * @param left   padding from the left edge to the text
     * @param top    padding from the top edge to the text
     * @param right  padding from the right edge to the text
     * @param bottom padding from the bottom edge to the text
     */
    public void setSpeechBubblePadding(int left, int top, int right, int bottom) {
        // set wanted paddings
        setWantedSpeechBubblePaddingLeft(left);
        setWantedSpeechBubblePaddingTop(top);
        setWantedSpeechBubblePaddingRight(right);
        setWantedSpeechBubblePaddingBottom(bottom);
    }


    /**
     * @brief Set the internal padding from text content to the top border of the speech-bubble background.
     */
    public void setWantedSpeechBubblePaddingTop(int padding) {
        // anything to change?
        if (mWantedSpeechBubblePaddingTop == padding) return;
        // security
        if (padding < 0) return;

        // remember
        mWantedSpeechBubblePaddingTop = padding;

        // update
        updateNotificationPadding();
    }


    /**
     * @brief Set the internal padding from text content to the bottom border of the speech-bubble background.
     */
    public void setWantedSpeechBubblePaddingBottom(int padding) {
        // anything to change?
        if (mWantedSpeechBubblePaddingBottom == padding) return;
        // security
        if (padding < 0) return;

        // remember
        mWantedSpeechBubblePaddingBottom = padding;

        // update
        updateNotificationPadding();
    }


    /**
     * @brief Set the internal padding from text content to the left border of the speech-bubble background.
     */
    public void setWantedSpeechBubblePaddingLeft(int padding) {
        // anything to change?
        if (mWantedSpeechBubblePaddingLeft == padding) return;
        // security
        if (padding < 0) return;

        // remember
        mWantedSpeechBubblePaddingLeft = padding;

        // update
        updateNotificationPadding();
    }


    /**
     * @brief Set the internal padding from text content to the right border of the speech-bubble background.
     */
    public void setWantedSpeechBubblePaddingRight(int padding) {
        // anything to change?
        if (mWantedSpeechBubblePaddingRight == padding) return;
        // security
        if (padding < 0) return;

        // remember
        mWantedSpeechBubblePaddingRight = padding;

        // update
        updateNotificationPadding();
    }


    /**
     * @brief Delivers the triangle contribution to the top notification padding.
     *
     * Helper for the calculation of the notification padding. If the triangle is visible and on the top side, the top
     * notification padding has to be enlarged by the tip distance. So this function delivers the tip distance in
     * this case, otherwise it delvers zero.
     */
    protected int getTriangleTopPadding() {
        if (mSpeechBubble.isElementTriangleEnabled()
            && mSpeechBubble.getElementTriangleSide() == PDEDrawableNotificationFrame.TriangleSide.SideTop) {
            return mSpeechBubble.getElementWantedTriangleTipDistance();
        } else {
            return 0;
        }
    }


    /**
     * @brief Delivers the triangle contribution to the bottom notification padding.
     *
     * Helper for the calculation of the notification padding. If the triangle is visible and on the bottom side, the bottom
     * notification padding has to be enlarged by the tip distance. So this function delivers the tip distance in
     * this case, otherwise it delvers zero.
     */
    protected int getTriangleBottomPadding() {
        if (mSpeechBubble.isElementTriangleEnabled()
            && mSpeechBubble.getElementTriangleSide() == PDEDrawableNotificationFrame.TriangleSide.SideBottom) {
            return mSpeechBubble.getElementWantedTriangleTipDistance();
        } else {
            return 0;
        }
    }


    /**
     * @brief Delivers the triangle contribution to the left notification padding.
     *
     * Helper for the calculation of the notification padding. If the triangle is visible and on the left side, the left
     * notification padding has to be enlarged by the tip distance. So this function delivers the tip distance in
     * this case, otherwise it delvers zero.
     */
    protected int getTriangleLeftPadding() {
        if (mSpeechBubble.isElementTriangleEnabled()
            && mSpeechBubble.getElementTriangleSide() == PDEDrawableNotificationFrame.TriangleSide.SideLeft) {
            return mSpeechBubble.getElementWantedTriangleTipDistance();
        } else {
            return 0;
        }
    }


    /**
     * @brief Delivers the triangle contribution to the right notification padding.
     *
     * Helper for the calculation of the notification padding. If the triangle is visible and on the right side, the right
     * notification padding has to be enlarged by the tip distance. So this function delivers the tip distance in
     * this case, otherwise it delvers zero.
     */
    protected int getTriangleRightPadding() {
        if (mSpeechBubble.isElementTriangleEnabled()
            && mSpeechBubble.getElementTriangleSide() == PDEDrawableNotificationFrame.TriangleSide.SideRight) {
            return mSpeechBubble.getElementWantedTriangleTipDistance();
        } else {
            return 0;
        }
    }


    /**
     * @brief Delivers the bottom notification padding due to the current settings.
     *
     * This function delivers the needed bottom notification padding due to the current settings.
     * It takes into consideration the wanted bottom padding and the current position, size and visibility of the
     * triangle.
     */
    public int getNotificationPaddingBottom() {
        return mWantedSpeechBubblePaddingBottom + getTriangleBottomPadding();
    }


    /**
     * @brief Delivers the top notification padding due to the current settings.
     *
     * This function delivers the needed top notification padding due to the current settings.
     * It takes into consideration the wanted bottom padding and the current position, size and visibility of the
     * triangle.
     */
    public int getNotificationPaddingTop() {
        return mWantedSpeechBubblePaddingTop + getTriangleTopPadding();
    }


    /**
     * @brief Delivers the left notification padding due to the current settings.
     *
     * This function delivers the needed left notification padding due to the current settings.
     * It takes into consideration the wanted bottom padding and the current position, size and visibility of the
     * triangle.
     */
    public int getNotificationPaddingLeft() {
        return mWantedSpeechBubblePaddingLeft + getTriangleLeftPadding();
    }


    /**
     * @brief Delivers the right notification padding due to the current settings.
     *
     * This function delivers the needed right notification padding due to the current settings.
     * It takes into consideration the wanted bottom padding and the current position, size and visibility of the
     * triangle.
     */
    public int getNotificationPaddingRight() {
        return mWantedSpeechBubblePaddingRight + getTriangleRightPadding();
    }


//---------------------------------------------------------------------------------------------------------------------
//  Updating
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Checks and updates notification paddings if necessary.
     *
     * The notification padding is the distance between the edges (respectively the triangle tip) of the speech bubble
     * and the text content. The paddings result from the current settings for the wantedSpeechBubblePaddings, and
     * the current settings for the triangle (side, tipDistance, visibility, etc.). This function determines the
     * paddings that are needed for the current settings, checks if they differ from the current notification paddings
     * and updates the notification paddings if need be.
     */
    protected void updateNotificationPadding() {
        int left, top, right, bottom;

        // get the paddings that result from the current settings
        left = getNotificationPaddingLeft();
        top = getNotificationPaddingTop();
        right = getNotificationPaddingRight();
        bottom = getNotificationPaddingBottom();

        // anything to do?
        if (left == mNotification.getPaddingLeft()
            && top == mNotification.getPaddingTop()
            && right == mNotification.getPaddingRight()
            && bottom == mNotification.getPaddingBottom()) return;
        // set new values
        mNotification.setPadding(left, top, right, bottom);
    }


    /**
     * @brief Update the minimum sizes of the view which are needed to show the notification correctly.
     *
     * This function calculates the minimum width and height this view must have to show the notification correctly
     * depending on all the current size/configuration settings.
     */
    protected void updateMinSizes() {
        PDEDrawableNotificationFrame.TriangleSide side = mSpeechBubble.getElementTriangleSide();
        float cornerRadius;
        int minHeight, minWidth;

        // get radius
        cornerRadius = getCornerRadius();

        // distinguish between vertical and horizontal sides
        if (side == PDEDrawableNotificationFrame.TriangleSide.SideTop ||
            side == PDEDrawableNotificationFrame.TriangleSide.SideBottom) {
            minHeight = (int) (cornerRadius * 2.0f + getCurrentTriangleTipDistance());
            minWidth = (int) (/*cornerRadius * 2.0f +*/ getCurrentTriangleWidth() + getCurrentTriangleMargin() * 2.0f);
        } else {
            minHeight = (int) (/*cornerRadius * 2.0f + */getCurrentTriangleWidth() + getCurrentTriangleMargin() * 2.0f);
            minWidth = (int) (cornerRadius * 2.0f + getCurrentTriangleTipDistance());
        }

        // anything to do?
        if (mMinimumHeight == minHeight && mMinimumWidth == minWidth) return;
        // remember new min sizes
        mMinimumWidth = minWidth;
        mMinimumHeight = minHeight;
        // set new min sizes
        mNotification.setMinimumHeight(mMinimumHeight);
        mNotification.setMinimumWidth(mMinimumWidth);
    }


    /**
     * @brief Delivers current triangle margin for the minimum size calculation.
     *
     * Helper for the calculation of the minimum height / minimum width. It considers the visibility of the triangle.
     * This means it delivers the real value if the triangle is enabled, otherwise it delivers zero.
     */
    protected int getCurrentTriangleMargin() {
        if (isTriangleEnabled()) {
            return getTriangleMargin();
        } else {
            return 0;
        }
    }


    /**
     * @brief Delivers current triangle tip distance for the minimum size calculation.
     *
     * Helper for the calculation of the minimum height / minimum width. It considers the visibility of the triangle.
     * This means it delivers the real value if the triangle is enabled, otherwise it delivers zero.
     */
    protected int getCurrentTriangleTipDistance() {
        if (isTriangleEnabled()) {
            return getTriangleTipDistance();
        } else {
            return 0;
        }
    }


    /**
     * @brief Delivers current triangle width for the minimum size calculation.
     *
     * Helper for the calculation of the minimum height / minimum width. It considers the visibility of the triangle.
     * This means it delivers the real value if the triangle is enabled, otherwise it delivers zero.
     */
    protected int getCurrentTriangleWidth() {
        if (isTriangleEnabled()) {
            return getTriangleWidth();
        } else {
            return 0;
        }
    }


    /**
     * @brief Update padding and min-sizes.
     *
     * internal update helper
     */
    protected void updateSizes() {
        // check if the settings have changed in a way that we also have to change the current paddings
        // (e.g. the triangle has moved to a different side)
        updateNotificationPadding();
        // check if settings have changed in a way that result in a new minimum width or height
        updateMinSizes();
    }
}

