/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.elementwrappers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.text.PDELayerText;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//----------------------------------------------------------------------------------------------------------------------
//  PDETextView
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief Wrapper class hosting a PDELayerText for usage in Layouts.
 */
@SuppressWarnings("unused")
public class PDETextView extends ImageView {

    private final static String LOG_TAG = PDETextView.class.getName();
    private final static Boolean DEBUG_OUTPUT_MEASUREMENT = false;

    protected PDELayerText mLayerText;
    protected Drawable mBackgroundDrawable;


    /**
     * @brief Constructor.
     */
    public PDETextView(Context context){
        super(context);
        init(null);
    }


    /**
     * @brief Constructor.
     */
    public PDETextView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(attrs);
    }


    /**
     * @brief Constructor.
     */
    public PDETextView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(attrs);
    }


    /**
     * @brief Initialize.
     *
     * @param attrs Attribute set from the constructor.
     */
    protected void init(AttributeSet attrs){
        // initialize PDELayerText

        if (isInEditMode()) {
            // special case when View is shown in developer tool (IDE)

            // cannot do much here, e.g. we cannot show text

            return;
        }

        mLayerText = new PDELayerText("");
        mBackgroundDrawable = null;

//        boolean clippingDrawableSet = false;
//        Method method;

        //PDEUtils.setViewBackgroundDrawable(this, mLayerText);
        setImageDrawable(mLayerText);

        setAttributes(attrs);
    }


    /**
     * @brief Load XML attributes.
     *
     *
     */
    private void setAttributes(AttributeSet attrs) {
        String text;

        // valid?
        if (attrs == null) return;

		TypedArray sa = getContext().obtainStyledAttributes(attrs, R.styleable.PDETextView);

        // set text
        text = sa.getString(R.styleable.PDETextView_text);
        if (TextUtils.isEmpty(text)) {
            // try to get "android:text" attribute instead

            // first check if it is a resource id ...
            int resourceId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "text", -1);
            if (resourceId > 0) {
                text = getResources().getString(resourceId);
            } else {
                // otherwise handle it as string
                text = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
            }
        }

        if (text != null) {
            setText(text);
        }


        // set typeface (font)
        if (sa.hasValue(R.styleable.PDETextView_typeface)) {
            setTypeface(PDETypeface.createByName(sa.getString(R.styleable.PDETextView_typeface)));
        }

        // set the font size
        // we first try whether the inserted value is a dimension, if this fails we evaluate as string
        if (sa.hasValue(R.styleable.PDETextView_textSize)) {
            try {
                setTextSize(sa.getDimensionPixelSize(R.styleable.PDETextView_textSize, 50));
            }
            catch (Exception e) {
                setTextSize(parseDimension(sa.getString(R.styleable.PDETextView_textSize)));
            }
        }

        // set line distance factor
        if (sa.hasValue(R.styleable.PDETextView_lineDistanceFactor)) {
            setLineDistanceFactor(sa.getFloat(R.styleable.PDETextView_lineDistanceFactor,1.0f));
        }

        // set color of text
        if (sa.hasValue(R.styleable.PDETextView_textColor)) {
            //to have dark/light style use PDEColor with color id
            int resourceID = sa.getResourceId(R.styleable.PDETextView_textColor, 0);
            if (resourceID!=0) {
                setTextColor(PDEColor.valueOfColorID(resourceID));
            } else {
                setTextColor(sa.getColor(R.styleable.PDETextView_textColor, R.color.DTBlack));
            }
        }

        // set background color
        if (sa.hasValue(R.styleable.PDETextView_backgroundColor)) {
            //to have dark/light style use PDEColor with color id
            int resourceID = sa.getResourceId(R.styleable.PDETextView_backgroundColor, 0);
            if (resourceID!=0) {
                setBackgroundColor(PDEColor.valueOfColorID(resourceID));
            } else {
                setBackgroundColor(sa.getColor(R.styleable.PDETextView_backgroundColor, R.color.DTTransparentWhite));
            }
        }

        // set shadow color
        if (sa.hasValue(R.styleable.PDETextView_shadowColor)) {
            //to have dark/light style use PDEColor with color id
            int resourceID = sa.getResourceId(R.styleable.PDETextView_shadowColor,0);
            if (resourceID!=0) {
                setShadowColor(PDEColor.valueOfColorID(resourceID));
            } else {
                setShadowColor(sa.getColor(R.styleable.PDETextView_shadowColor, R.color.DTWhite));
            }
        }

        // set shadow enabled
        if (sa.hasValue(R.styleable.PDETextView_shadowEnabled)) {
            setShadowEnabled(sa.getBoolean(R.styleable.PDETextView_shadowEnabled, false));
        }

        // set shadow offset x
        if (sa.hasValue(R.styleable.PDETextView_shadowOffsetX)) {
            setShadowOffsetX(sa.getFloat(R.styleable.PDETextView_shadowOffsetX, 0.0f));
        }

        // set shadow offset y
        if (sa.hasValue(R.styleable.PDETextView_shadowOffsetY)) {
            setShadowOffsetY(sa.getFloat(R.styleable.PDETextView_shadowOffsetY, 1.0f));
        }

        // set max lines
        if (sa.hasValue(R.styleable.PDETextView_maxLines)) {
            setMaxLines(sa.getInteger(R.styleable.PDETextView_maxLines, -1));
        }

        // set alignment mode
        if (sa.hasValue(R.styleable.PDETextView_alignmentMode)) {
            setAlignmentMode(sa.getInteger(R.styleable.PDETextView_alignmentMode, 0));
        }

        // set vertical alignment
        if (sa.hasValue(R.styleable.PDETextView_verticalAlignment)) {
            setVerticalAlignment(sa.getInteger(R.styleable.PDETextView_verticalAlignment, 0));
        }

        // set horizontal alignment
        if (sa.hasValue(R.styleable.PDETextView_horizontalAlignment)) {
            setHorizontalAlignment(sa.getInteger(R.styleable.PDETextView_horizontalAlignment, 0));
        }

        // set baseline
        if (sa.hasValue(R.styleable.PDETextView_baseline)) {
            setBaseLine(sa.getDimensionPixelSize(R.styleable.PDETextView_baseline, 0));
        }

        // set ellipsize
        if(sa.hasValue(R.styleable.PDETextView_ellipsizeText)) {
            setEllipsize(sa.getBoolean(R.styleable.PDETextView_ellipsizeText, true));
        }

        // set line distance factor
        if (sa.hasValue(R.styleable.PDETextView_lineDistanceFactor)) {
            setLineDistanceFactor(sa.getFloat(R.styleable.PDETextView_lineDistanceFactor, 1.0f));
        }

        // set left padding
        if( sa.hasValue(R.styleable.PDETextView_paddingLeft)) {
            setPaddingLeft(sa.getDimensionPixelSize(R.styleable.PDETextView_paddingLeft, 0));
        } else {
            String dim = attrs.getAttributeValue("http://schemas.android.com/apk/res/android","paddingLeft");
            if (!TextUtils.isEmpty(dim)) {
                setPaddingLeft(parseDimension(dim));
            }
        }

        // set top padding
        if (sa.hasValue(R.styleable.PDETextView_paddingTop)) {
            setPaddingTop(sa.getDimensionPixelSize(R.styleable.PDETextView_paddingTop, 0));
        } else {
            String dim = attrs.getAttributeValue("http://schemas.android.com/apk/res/android","paddingTop");
            if (!TextUtils.isEmpty(dim)) {
                setPaddingTop(parseDimension(dim));
            }
        }


        // set right padding
        if (sa.hasValue(R.styleable.PDETextView_paddingRight)) {
            setPaddingRight(sa.getDimensionPixelSize(R.styleable.PDETextView_paddingRight,0));
        } else {
            String dim = attrs.getAttributeValue("http://schemas.android.com/apk/res/android","paddingRight");
            if (!TextUtils.isEmpty(dim)) {
                setPaddingRight(parseDimension(dim));
            }
        }

        // set bottom padding
        if (sa.hasValue(R.styleable.PDETextView_paddingBottom)) {
            setPaddingBottom(sa.getDimensionPixelSize(R.styleable.PDETextView_paddingBottom,0));
        } else {
            String dim = attrs.getAttributeValue("http://schemas.android.com/apk/res/android","paddingBottom");
            if (!TextUtils.isEmpty(dim)) {
                setPaddingBottom(parseDimension(dim));
            }
        }

        // set the padding
        if (sa.hasValue(R.styleable.PDETextView_padding)) {
            setPaddingAll(sa.getDimensionPixelSize(R.styleable.PDETextView_padding,0));
        } else {
            String dim = attrs.getAttributeValue("http://schemas.android.com/apk/res/android","padding");
            if (!TextUtils.isEmpty(dim)) {
                setPaddingAll(parseDimension(dim));
            }
        }


        sa.recycle();
    }


    /**
     * @brief Set text.
     */
    public void setText(String text) {
        mLayerText.setElementText(text);
        requestLayout();
    }


    /**
     * @brief Set text from resource ID.
     */
    public void setTextFromID(int id) {
        setText(getResources().getString(id));
        requestLayout();
    }


    /**
     * @brief Get text.
     */
    public String getText() {
        return mLayerText.getElementText();
    }


    /**
     * @brief Set maximum lines.
     */
    public void setMaxLines(int lines) {
        mLayerText.setElementMaximumLines(lines);
        requestLayout();
    }


    /**
     * @brief Get maximum lines.
     */
    public int getMaxLines() {
        return mLayerText.getElementMaximumLines();
    }


    /**
     * @brief Set ellipsize enabled.
     */
    public void setEllipsize(boolean e) {
        mLayerText.setElementEllipsize(e);
        requestLayout();
    }


    /**
     * @brief Get if ellipsize is enabled.
     */
    public boolean getEllipsize() {
        return mLayerText.getElementEllipsize();
    }


    /**
     * @brief Set alignment mode.
     *
     * Possible values are Standard Mode (default), where there is a little distance over and under the text,
     * Baseline Mode where one has to set the baseline and CapHeight Mode, where there is no distance over and
     * under the text.
     */
    public void setAlignmentMode (PDELayerText.PDELayerTextAlignmentMode mode) {
        mLayerText.setElementAlignmentMode(mode);
        requestLayout();
    }


    /**
     * @brief Set alignment mode.
     *
     * Possible values are 0 for Standard Mode (default), where there is a little distance over and under the text,
     * 1 for Baseline Mode where one has to set the baseline and 2 for CapHeight Mode, where there is no distance over
     * and under the text.
     */
    public void setAlignmentMode(int mode) {
        PDELayerText.PDELayerTextAlignmentMode alignmentMode;
        try {
            alignmentMode = PDELayerText.PDELayerTextAlignmentMode.values()[mode];
        } catch (Exception e) {
            alignmentMode = PDELayerText.PDELayerTextAlignmentMode.PDELayerTextAlignmentModeStandard;
        }
        mLayerText.setElementAlignmentMode(alignmentMode);
        requestLayout();
    }


    /**
     * @brief Get alignment mode.
     */
    public PDELayerText.PDELayerTextAlignmentMode getAlignmentMode() {
        return mLayerText.getAlignmentMode();
    }


    /**
     * @brief Set horizontal alignment.
     */
    public void setHorizontalAlignment(PDEConstants.PDEAlignment alignment) {
        mLayerText.setElementHorizontalAlignment(alignment);
        requestLayout();
    }


    /**
     * @brief Set horizontal alignment.
     */
    public void setHorizontalAlignment(int alignment) {
        PDEConstants.PDEAlignment hAlignment;
        try {
            hAlignment = PDEConstants.PDEAlignment.values()[alignment];
        } catch (Exception e) {
            hAlignment = PDEConstants.PDEAlignment.PDEAlignmentLeft;
        }
        mLayerText.setElementHorizontalAlignment(hAlignment);
        requestLayout();
    }


    /**
     * @brief Get horizontal alignment.
     */
    public PDEConstants.PDEAlignment getHorizontalAlignment() {
        return mLayerText.getElementHorizontalAlignment();
    }


    /**
     * @brief Set vertical alignment.
     */
    public void setVerticalAlignment(PDEConstants.PDEVerticalAlignment alignment) {
        mLayerText.setElementVerticalAlignment(alignment);
        requestLayout();
    }


    /**
     * @brief Set vertical alignment.
     */
    public void setVerticalAlignment(int alignment) {
        PDEConstants.PDEVerticalAlignment vAlignment;
        try {
            vAlignment = PDEConstants.PDEVerticalAlignment.values()[alignment];
        } catch (Exception e) {
            vAlignment = PDEConstants.PDEVerticalAlignment.PDEAlignmentTop;
        }
        mLayerText.setElementVerticalAlignment(vAlignment);
        requestLayout();
    }


    /**
     * @brief Get vertical alignment.
     */
    public PDEConstants.PDEVerticalAlignment getVerticalAlignment() {
        return mLayerText.getElementVerticalAlignment();
    }


    /**
     * @brief Set same padding on all sides.
     */
    public void setPaddingAll(int padding) {
        mLayerText.setElementPaddingAll(padding);
        requestLayout();
    }


    /**
     * @brief Set paddings on all sides.
     */
    public void setPaddingAll(int left, int top, int right, int bottom) {
        mLayerText.setElementPaddingAll(left, top, right, bottom);
        requestLayout();
    }




    /**
     * @brief Get padding rect.
     */
    public Rect getPaddingRect() {
        return mLayerText.getElementPaddingRect();
    }


    /**
     * @brief Set left padding.
     */
    public void setPaddingLeft(int padding) {
        mLayerText.setElementPaddingLeft(padding);
        requestLayout();
    }


    /**
     * @brief Get left padding.
     */
    @Override
    public int getPaddingLeft() {
        return mLayerText.getElementPaddingLeft();
    }


    /**
     * @brief Set top padding.
     */
    public void setPaddingTop(int padding) {
        mLayerText.setElementPaddingTop(padding);
        requestLayout();
    }


    /**
     * @brief Get top padding.
     */
    @Override
    public int getPaddingTop() {
        return mLayerText.getElementPaddingTop();
    }


    /**
     * @brief Set right padding.
     */
    public void setPaddingRight(int padding) {
        mLayerText.setElementPaddingRight(padding);
        requestLayout();
    }


    /**
     * @brief Get right padding.
     */
    @Override
    public int getPaddingRight() {
        return mLayerText.getElementPaddingRight();
    }


    /**
     * @brief Set bottom padding.
     */
    public void setPaddingBottom(int padding) {
        mLayerText.setElementPaddingBottom(padding);
        requestLayout();
    }


    /**
     * @brief Get bottom padding.
     */
    @Override
    public int getPaddingBottom() {
        return mLayerText.getElementPaddingBottom();
    }


    /**
     * @brief Set baseline.
     */
    public void setBaseLine(int baseline) {
        mLayerText.setElementBaseLine(baseline);
        requestLayout();
    }


    /**
     * @brief Get baseline.
     */
    public float getBaseLine() {
        return mLayerText.getElementSetBaseLine();
    }


    /**
     * @brief Set text color.
     */
    public void setTextColor(int color) {
        mLayerText.setElementTextColor(PDEColor.valueOf(color));
        requestLayout();
    }


    /**
     * @brief Set text color.
     */
    public void setTextColor(PDEColor color) {
        mLayerText.setElementTextColor(color);
        requestLayout();
    }


    /**
     * @brief Get text color
     */
    public PDEColor getTextColor() {
        return mLayerText.getElementTextColor();
    }


    /**
     * @brief Set background color.
     */
    public void setBackgroundColor(int color) {
        mLayerText.setElementBackgroundColor(PDEColor.valueOf(color));
        requestLayout();
    }


    /**
     * @brief Set background color.
     */
    public void setBackgroundColor(PDEColor color) {
        mLayerText.setElementBackgroundColor(color);
        requestLayout();
    }


    /**
     * @brief Get background color
     */
    public PDEColor getBackgroundColor() {
        return mLayerText.getElementBackgroundColor();
    }


    /**
     * @brief Set a custom background drawable for our text view.
     *
     * @param bg the drawable which should be used as background.
     */
    public void setBackgroundDrawableCustom(Drawable bg) {
        if (bg == null) return;
        mBackgroundDrawable = bg;
        PDEUtils.setViewBackgroundDrawable(this,mBackgroundDrawable);
    }


    /**
     * @brief Get the drawable which is currently used as background.
     *
     * If there's currently no drawable used as background this returns null.
     *
     * @return the drawable which is currently used as background.
     */
    public Drawable getBackgroundDrawableCustom(){
        return mBackgroundDrawable;
    }


    /**
     * @brief Set shadow color.
     */
    public void setShadowColor(int color) {
        mLayerText.setElementShadowColor(PDEColor.valueOf(color));
        requestLayout();
    }


    /**
     * @brief Set shadow color.
     */
    public void setShadowColor(PDEColor color) {
        mLayerText.setElementShadowColor(color);
        requestLayout();
    }


    /**
     * @brief Get shadow color.
     */
    public PDEColor getShadowColor() {
        return mLayerText.getElementShadowColor();
    }


    /**
     * @brief Set shadow x offset.
     */
    public void setShadowOffsetX(float offset) {
        mLayerText.setElementShadowXOffset(offset);
        requestLayout();
    }


    /**
     * @brief Get shadow x offset.
     */
    public float getShadowOffsetX() {
        return mLayerText.getElementShadowXOffset();
    }


    /**
     * @brief Set shadow y offset.
     */
    public void setShadowOffsetY(float offset) {
        mLayerText.setElementShadowYOffset(offset);
        requestLayout();
    }


    /**
     * @brief Get shadow y offset.
     */
    public float getShadowOffsetY() {
        return mLayerText.getElementShadowYOffset();
    }


    /**
     * @brief Set shadow enabled.
     */
    public void setShadowEnabled(boolean enabled) {
        mLayerText.setElementShadowEnabled(enabled);
        requestLayout();
    }


    /**
     * @brief Get if shadow is enabled.
     */
    public boolean getShadowEnabled() {
        return mLayerText.getElementShadowEnabled();
    }


    /**
     * @brief Set text size.
     */
    public void setTextSize(float textsize) {
        mLayerText.setElementTextSize(textsize);
        requestLayout();
    }


    /**
     * @brief Get text size.
     */
    public float getTextSize() {
        return mLayerText.getElementTextSize();
    }


    /**
     * @brief Set line distance factor.
     *
     * Standard distance is 1, for 0 there is no distance between lines.
     */
    public void setLineDistanceFactor(float distanceFactor) {
        mLayerText.setElementLineDistanceFactor(distanceFactor);
        requestLayout();
    }


    /**
     * @brief Get line distance factor.
     */
    public float getLineDistanceFactor() {
        return mLayerText.getElementDistanceFactor();
    }


    /**
     * @brief Set typeface.
     */
    public void setTypeface(PDETypeface typeface) {
        mLayerText.setElementTypeface(typeface);
        requestLayout();
    }


    /**
     * @brief Get typeface.
     */
    public PDETypeface getTypeface() {
        return mLayerText.getElementTypeface();
    }


    /**
     * @brief Get baseline, resulting from set baseline, padding and vertical alignment.
     */
    public float getElementInternalBaseLine() {
        return mLayerText.getElementInternalBaseLine();
    }


    /**
     * @brief Get text offset.
     */
    public float getTextOffset() {
        return mLayerText.getTextOffset();
    }


    /**
     * @brief Get text height.
     */
    public float getTextHeight() {
        return mLayerText.getElementTextHeight();
    }


    /**
     * @brief Returns necessary height for text, based on given width.
     */
    public float getTextHeightForWidth(String text, float width) {
        return mLayerText.getTextHeightForWidth(text, width);
    }


    /**
     * @brief Returns necessary height of element, which consists of text height and padding bottom.
     */
    public float getElementHeightForWidth(String text, float width) {
        return super.getPaddingBottom() + super.getPaddingTop() + mLayerText.getElementHeightForWidth(text, width);
    }


    /**
     * @brief Returns text width for given text, typeface and fontsize.
     */
    public float getTextWidth(String text, PDETypeface font, float fontsize) {
        return mLayerText.getTextWidth(text, font, fontsize);
    }


    /**
     * @brief Returns text width for given text.
     */
    public float getTextWidth(String text) {
        return mLayerText.getTextWidth(text);
    }


    /**
     * @brief Returns text width for set text.
     */
    public float getTextWidth() {
        return mLayerText.getTextWidth();
    }


    /**
     * @brief Returns width of the PDELayerText, including padding.
     */
    public float getElementWidth() {
        return mLayerText.getElementWidth();
    }


    /**
     * @brief returns width of PDELayerText for a given text, including padding
     */
    public float getElementWidth(String text) {
        return super.getPaddingLeft() + super.getPaddingRight() + mLayerText.getElementWidth(text);
    }


    /**
     * @brief Determine layout size of element.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        int width;
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);

        if (isInEditMode()) {
            // special case when View is shown in developer tool (IDE)
            setMeasuredDimension(resolveSize(100, widthMeasureSpec),
                    resolveSize(100, heightMeasureSpec));

            return;
        }

        if (DEBUG_OUTPUT_MEASUREMENT) {
            Log.d(LOG_TAG, "onMeasure " + MeasureSpec.toString(widthMeasureSpec) + " - " + MeasureSpec.toString(heightMeasureSpec));
        }

        // take height/width from the parameter ...
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);

        int newWidth = PDEBuildingUnits.roundUpToScreenCoordinates(getElementWidth(getText()));

        if (DEBUG_OUTPUT_MEASUREMENT) {
            Log.d(LOG_TAG, "result width: "+newWidth);
        }
        if (newWidth < width) {
            width = newWidth;
        }

        if (widthSpecMode == MeasureSpec.UNSPECIFIED && width == 0) {
            width = newWidth;
        }

        int newHeight = PDEBuildingUnits.roundUpToScreenCoordinates(getElementHeightForWidth(getText(), width));
        if (DEBUG_OUTPUT_MEASUREMENT) {
            Log.d(LOG_TAG, "result height: "+newHeight);
        }
        if (newHeight < height) {
            height = newHeight;
        }

        if (heightSpecMode == MeasureSpec.UNSPECIFIED && height == 0) {
            height = newHeight;
        }

        // return the values
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));

        if (DEBUG_OUTPUT_MEASUREMENT) {
            Log.d(LOG_TAG, "onMeasure end: "+getMeasuredWidth()+" "+getMeasuredHeight());
        }
    }


    /**
     * @brief Takes dimension string from resources and transforms it in pixel value.
     */
    public int parseDimension(String dimensionString)
    {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float size = Float.NaN;
        int endOfFloatIndex = -1;

        Pattern p = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
        Matcher m = p.matcher(dimensionString);

        if (m.find()) {
            if (m.start() == 0) {
                // float only at the beginning
                size = Float.valueOf(dimensionString.substring(m.start(),
                        m.end()));
                endOfFloatIndex = m.end();
            }
        }

        if (!Float.isNaN(size) && endOfFloatIndex > -1 &&
                endOfFloatIndex < dimensionString.length()) {
            String unitPart = dimensionString.substring(endOfFloatIndex);
            if (unitPart.compareToIgnoreCase("%") == 0) {
                //percent of default copy size (styleguide definition)
                size = PDEFontHelpers.calculateFontSizeByPercent(PDETypeface.sDefaultFont, size);
            } else if (unitPart.compareToIgnoreCase("BU") == 0) {
                size = PDEFontHelpers.calculateFontSize(PDETypeface.sDefaultFont, PDEBuildingUnits.exactPixelFromBU(size));
            } else if (unitPart.compareToIgnoreCase("px") == 0) {
                size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size, metrics);
            } else if (unitPart.compareToIgnoreCase("dp") == 0 ||
                    unitPart.compareToIgnoreCase("dip") == 0) {
                size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, metrics);
            } else if (unitPart.compareToIgnoreCase("sp") == 0) {
                size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, metrics);
            } else if (unitPart.compareToIgnoreCase("dt") == 0) {
                size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, size, metrics);
            } else if (unitPart.compareToIgnoreCase("in") == 0) {
                size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, size, metrics);
            } else if (unitPart.compareToIgnoreCase("mm") == 0) {
                size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, size, metrics);
            }
        }
        return Math.round(size);
    }
}
