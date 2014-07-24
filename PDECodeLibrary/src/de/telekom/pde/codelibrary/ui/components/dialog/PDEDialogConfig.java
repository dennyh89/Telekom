/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.dialog;


import de.telekom.pde.codelibrary.ui.PDEConstants;

import android.os.Parcel;
import android.os.Parcelable;


//----------------------------------------------------------------------------------------------------------------------
//  PDEDialogConfig
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief Dialog configuration data class.
 *
 * This class stores all data that is needed to configure a constructDialog. Since the actual visual representation of the
 * dialog is wrapped into a separate activity this class is parcelable in order to deliver an easy way to pass the
 * configuration data (encapsulated in an object) along by an intent.
 */
public class PDEDialogConfig implements Parcelable{

    // style
    protected PDEConstants.PDEContentStyle mStyle;

    // texts
    protected String mTitle;
    protected String mMessage;
    protected String mButton1Text;
    protected String mButton2Text;

    // enable hardware BackButton?
    protected boolean mAndroidHardwareBackButtonEnabled;

    // unique id for directed communication between two distinct activities (they share the same id)
    protected String mBroadcastID;

    // colors
    protected Integer mTitleTextColor;
    protected Integer mMessageTextColor;
    protected Integer mButton1BackgroundColor;
    protected Integer mButton2BackgroundColor;
    protected Integer mDialogBackgroundColor;
    protected Integer mDialogOutlineColor;
    protected Integer mSeparatorColor;

    // font sizes
    protected float mTitleFontSize;
    protected float mMessageFontSize;

    // font names
    protected String mTitleTypefaceName;
    protected String mMessageTypefaceName;
    protected String mButton1TypefaceName;
    protected String mButton2TypefaceName;




    /**
     * @brief standard constructor
     */
    public PDEDialogConfig() {
        // init
        mTitle = "";
        mMessage = "";
        mButton1Text = "";
        mButton2Text = "";
        mBroadcastID = "";
        mAndroidHardwareBackButtonEnabled = true;
        mStyle = PDEConstants.PDEContentStyle.PDEContentStyleFlat;
        mTitleTextColor = null;
        mMessageTextColor = null;
        mButton1BackgroundColor = null;
        mButton2BackgroundColor = null;
        mTitleFontSize = -1.0f;
        mMessageFontSize = -1.0f;
        mDialogBackgroundColor = null;
        mDialogOutlineColor = null;
        mSeparatorColor = null;
        mTitleTypefaceName = PDEConstants.sPDEDefaultFontName;
        mMessageTypefaceName = PDEConstants.sPDEDefaultFontName;
        mButton1TypefaceName = PDEConstants.sPDEDefaultFontName;
        mButton2TypefaceName = PDEConstants.sPDEDefaultFontName;
    }


    /**
     * @brief Describe the kinds of special objects contained in this Parcelable's marshalled representation.
     *
     * Implementation of abstract Parcelable method. We don't need this here, so it's not meaningful implemented. (Just
     * returns 0).
     *
     * @return 0
     */
    @Override
    public int describeContents(){
        // ignored
        return 0;
    }


    /**
     * @brief Flatten this object in to a Parcel.
     *
     * Implementation of abstract Parcelable method. Serializes all members into the delivered Parcel.
     *
     * @param pc The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written. May be 0 or PARCELABLE_WRITE_RETURN_VALUE.
     */
    @Override
    public void writeToParcel(Parcel pc, int flags){
        pc.writeString(mStyle.name());
        pc.writeString(mTitle);
        pc.writeString(mMessage);
        pc.writeString(mButton1Text);
        pc.writeString(mButton2Text);
        pc.writeInt(mAndroidHardwareBackButtonEnabled ? 1 : 0);
        pc.writeValue(mTitleTextColor);
        pc.writeValue(mMessageTextColor);
        pc.writeString(mBroadcastID);
        pc.writeValue(mButton1BackgroundColor);
        pc.writeValue(mButton2BackgroundColor);
        pc.writeFloat(mTitleFontSize);
        pc.writeFloat(mMessageFontSize);
        pc.writeValue(mDialogBackgroundColor);
        pc.writeValue(mDialogOutlineColor);
        pc.writeValue(mSeparatorColor);
        pc.writeString(mTitleTypefaceName);
        pc.writeString(mMessageTypefaceName);
        pc.writeString(mButton1TypefaceName);
        pc.writeString(mButton2TypefaceName);
    }


    /**
     * @brief Interface that must be implemented and provided as a public CREATOR field that generates instances of your Parcelable class from a Parcel.
     *
     * Static field used to regenerate object, individually or as arrays
     */
    public static final Creator<PDEDialogConfig> CREATOR = new Creator<PDEDialogConfig>() {

        /**
         * @brief Create a new instance of the Parcelable class, instantiating it from the given Parcel whose data had previously been written by Parcelable.writeToParcel().
         *
         * @param pc The Parcel to read the object's data from.
         * @return Returns a new instance of the Parcelable class.
         */
        public PDEDialogConfig createFromParcel(Parcel pc){
            return new PDEDialogConfig(pc);
        }


        /**
         * @brief Create a new array of the Parcelable class.
         *
         * @param size Size of the array.
         * @return Returns an array of the Parcelable class, with every entry initialized to null.
         */
        public PDEDialogConfig[] newArray(int size){
            return new PDEDialogConfig[size];
        }
    };


    /**
     * @brief Constructor from Parcel, reads back fields IN THE ORDER they were written.
     *
     * @param pc The Parcel to read the object's data from.
     */
    public PDEDialogConfig(Parcel pc){
        mStyle = PDEConstants.PDEContentStyle.valueOf(pc.readString());
        mTitle = pc.readString();
        mMessage = pc.readString();
        mButton1Text = pc.readString();
        mButton2Text = pc.readString();
        mAndroidHardwareBackButtonEnabled = (pc.readInt() == 1) ;
        mTitleTextColor = (Integer) pc.readValue(null);
        mMessageTextColor = (Integer) pc.readValue(null);
        mBroadcastID = pc.readString();
        mButton1BackgroundColor = (Integer) pc.readValue(null);
        mButton2BackgroundColor = (Integer) pc.readValue(null);
        mTitleFontSize = pc.readFloat();
        mMessageFontSize = pc.readFloat();
        mDialogBackgroundColor = (Integer) pc.readValue(null);
        mDialogOutlineColor = (Integer) pc.readValue(null);
        mSeparatorColor = (Integer) pc.readValue(null);
        mTitleTypefaceName = pc.readString();
        mMessageTypefaceName = pc.readString();
        mButton1TypefaceName = pc.readString();
        mButton2TypefaceName = pc.readString();
    }




//----------------- Setter / Getter ------------------------------------------------------------------------------------


    /**
     * @brief Set content style.
     *
     * @param style content style
     */
    public void setStyle(PDEConstants.PDEContentStyle style){
        mStyle = style;
    }


    /**
     * @brief Get content style.
     */
    public PDEConstants.PDEContentStyle getStyle(){
        return mStyle;
    }


    /**
     * @brief Set title text.
     *
     * @param title title text
     */
    public void setTitle(String title) {
        mTitle = title;
    }


    /**
     * @brief Get title text.
     */
    public String getTitle() {
        return mTitle;
    }


    /**
     * @brief Set message text.
     */
    public void setMessage(String msg) {
        mMessage = msg;
    }


    /**
     * @brief Get message text.
     */
    public String getMessage() {
        return mMessage;
    }


    /**
     * @brief Set text of button1 (leftmost).
     */
    public void setButton1Text(String txt){
        mButton1Text = txt;
    }


    /**
     * @brief Get text of button1 (leftmost).
     */
    public String getButton1Text() {
        return mButton1Text;
    }


    /**
     * @brief Set text of button2.
     */
    public void setButton2Text(String txt){
        mButton2Text = txt;
    }


    /**
     * @brief Get text of button2.
     */
    public String getButton2Text(){
        return mButton2Text;
    }


    /**
     * @brief Set ID for directed IPC between two distinct activities by broadcast.
     */
    public void setBroadcastID(String id){
        mBroadcastID = id;
    }


    /**
     * @brief Get ID for directed IPC between two distinct activities by broadcast.
     */
    public String getBroadcastID(){
        return mBroadcastID;
    }


    /**
     * @brief Determine if hardware back button should react when dialog is shown or not (default).
     */
    public void setAndroidHardwareBackButtonEnabled(boolean enabled){
        mAndroidHardwareBackButtonEnabled = enabled;
    }


    /**
     * @brief Check if hardware back button reacts when dialog is shown.
     */
    public boolean isAndroidHardwareBackButtonEnabled(){
        return mAndroidHardwareBackButtonEnabled;
    }


    /**
     * @brief Set color of title text.
     */
    public void setTitleTextColor(int color){
        mTitleTextColor = color;
    }


    /**
     * @brief Get color of title text.
     */
    public Integer getTitleTextColor(){
        return mTitleTextColor;
    }


    /**
     * @brief Set color of message text.
     */
    public void setMessageTextColor(int color){
        mMessageTextColor = color;
    }


    /**
     * @brief Get color of message text.
     */
    public Integer getMessageTextColor(){
        return mMessageTextColor;
    }


    /**
     * @brief Set color of message text.
     */
    public void setButton1BackgroundColor(int color){
        mButton1BackgroundColor = color;
    }


    /**
     * @brief Get color of message text.
     */
    public Integer getButton1BackgroundColor(){
        return mButton1BackgroundColor;
    }


    /**
     * @brief Set color of message text.
     */
    public void setButton2BackgroundColor(int color){
        mButton2BackgroundColor = color;
    }


    /**
     * @brief Get color of message text.
     */
    public Integer getButton2BackgroundColor(){
        return mButton2BackgroundColor;
    }



    /**
     * @brief Set color of dialog plate.
     */
    public void setDialogBackgroundColor(int color){
        mDialogBackgroundColor = color;
    }


    /**
     * @brief Get color of dialog plate.
     */
    public Integer getDialogBackgroundColor(){
        return mDialogBackgroundColor;
    }


    /**
     * @brief Set color of dialog outline.
     */
    public void setDialogOutlineColor(int color){
        mDialogOutlineColor = color;
    }


    /**
     * @brief Get color of dialog outline.
     */
    public Integer getDialogOutlineColor(){
        return mDialogOutlineColor;
    }


    /**
     * @brief Set color of separator.
     */
    public void setSeparatorColor(int color){
        mSeparatorColor = color;
    }


    /**
     * @brief Get color of separator.
     */
    public Integer getSeparatorOutlineColor(){
        return mSeparatorColor;
    }

    /**
     * @brief Set font size of title.
     */
    public void setTitleFontSize(float size){
        mTitleFontSize = size;
    }

    /**
     * @brief Get font size of title.
     */
    public float getTitleFontSize(){
        return mTitleFontSize;
    }


    /**
     * @brief Set font size of message.
     */
    public void setMessageFontSize(float size){
        mMessageFontSize = size;
    }


    /**
     * @brief Get font size of message.
     */
    public float getMessageFontSize(){
        return mMessageFontSize;
    }




    /**
     * @brief Set name of the typeface the title text should use.
     */
    public void setTitleTypefaceName(String typefaceName) {
        mTitleTypefaceName = typefaceName;
    }


    /**
     * @brief Get name of typeface used by title text.
     */
    public String getTypefaceNameTitle() {
        return mTitleTypefaceName;
    }

    /**
     * @brief Set name of the typeface the message text should use.
     */
    public void setMessageTypefaceName(String typefaceName) {
        mMessageTypefaceName = typefaceName;
    }


    /**
     * @brief Get name of typeface used by message text.
     */
    public String getTypefaceNameMessage() {
        return mMessageTypefaceName;
    }

    /**
     * @brief Set name of the typeface the button1 label should use.
     */
    public void setButton1TypefaceName(String typefaceName) {
        mButton1TypefaceName = typefaceName;
    }


    /**
     * @brief Get name of typeface used by button1 label.
     */
    public String getTypefaceNameButton1() {
        return mButton1TypefaceName;
    }

    /**
     * @brief Set name of the typeface the button2 label should use.
     */
    public void setButton2TypefaceName(String typefaceName) {
        mButton2TypefaceName = typefaceName;
    }


    /**
     * @brief Get name of typeface used by button2 label.
     */
    public String getTypefaceNameButton2() {
        return mButton2TypefaceName;
    }
}
