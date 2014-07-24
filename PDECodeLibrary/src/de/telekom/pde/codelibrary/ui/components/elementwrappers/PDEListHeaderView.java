package de.telekom.pde.codelibrary.ui.components.elementwrappers;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.complex.PDEDrawableListHeader;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;

//----------------------------------------------------------------------------------------------------------------------
//  PDEListHeaderView
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Wrapper class hosting a PDEListHeaderView for usage in Layouts
 */
public class PDEListHeaderView extends View {

    private PDEDrawableListHeader mHeader;


    public PDEListHeaderView(Context context) {
        super(context);
        init(context, null);
    }


    public PDEListHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public PDEListHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    /**
     * @brief Initialize
     */
    protected void init(Context context, AttributeSet attrs) {

        if (isInEditMode()) return;

        mHeader = new PDEDrawableListHeader();

        // adapt to light / dark style
        if (PDECodeLibrary.getInstance().isDarkStyle()) {
            mHeader.setDelimiterBackgroundColor(PDEColor.valueOf("Black30Alpha"));
            mHeader.setElementBackgroundColor(PDEColor.valueOf("DTTransparentBlack"));
            mHeader.setElementTextColor(PDEColor.valueOf("DTDarkUIText"));
            mHeader.setElementSubTextColor(PDEColor.valueOf("DTGrey1"));
        } else {
            mHeader.setDelimiterBackgroundColor(PDEColor.valueOf("Black30Alpha"));
            mHeader.setElementBackgroundColor(PDEColor.valueOf("DTTransparentBlack"));
            mHeader.setElementTextColor(PDEColor.valueOf("DTLightUIText"));
            mHeader.setElementSubTextColor(PDEColor.valueOf("DTLightUIIndicativeText"));
        }

        PDEUtils.setViewBackgroundDrawable(this, mHeader);
        setAttributes(context, attrs);
    }


    /**
     * @brief Load XML attributes
     */
    private void setAttributes(Context context, AttributeSet attrs) {
        // valid?
        if (attrs == null) return;

        TypedArray sa = context.obtainStyledAttributes(attrs, R.styleable.PDEListHeaderView);

        // set text
        if (sa != null && sa.hasValue(R.styleable.PDEListHeaderView_text)) {
            setText(sa.getString(R.styleable.PDEListHeaderView_text));
        } else {
            String text = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
            if (!TextUtils.isEmpty(text)) {
                setText(text);
            }
        }

        if (sa != null) {
            // set subtext
            if (sa.hasValue(R.styleable.PDEListHeaderView_subText)) {
                setSubText(sa.getString(R.styleable.PDEListHeaderView_subText));
            }

            //set horizontal alignment
            if (sa.hasValue(R.styleable.PDEListHeaderView_horizontalAlignment)) {
                setHorizontalAlignment(sa.getInteger(R.styleable.PDEListHeaderView_horizontalAlignment, 0));
            }

            // set text color
            if (sa.hasValue(R.styleable.PDEListHeaderView_textColor)) {
                //to have dark/light style use PDEColor with color id
                int resourceID = sa.getResourceId(R.styleable.PDEListHeaderView_textColor, 0);
                if (resourceID != 0) {
                    setTextColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setTextColor(sa.getColor(R.styleable.PDEListHeaderView_textColor, R.color.DTBlack));
                }
            }

            // set subtext color
            if (sa.hasValue(R.styleable.PDEListHeaderView_subTextColor)) {
                //to have dark/light style use PDEColor with color id
                int resourceID = sa.getResourceId(R.styleable.PDEListHeaderView_subTextColor, 0);
                if (resourceID != 0) {
                    setSubTextColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setSubTextColor(sa.getColor(R.styleable.PDEListHeaderView_subTextColor, R.color.DTBlack));
                }
            }

            // set background color
            if (sa.hasValue(R.styleable.PDEListHeaderView_backgroundColor)) {
                //to have dark/light style use PDEColor with color id
                int resourceID = sa.getResourceId(R.styleable.PDEListHeaderView_backgroundColor, 0);
                if (resourceID != 0) {
                    setBackgroundColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setBackgroundColor(sa.getColor(R.styleable.PDEListHeaderView_backgroundColor, R.color.DTBlack));
                }
            }

            // set delimiter background color
            if (sa.hasValue(R.styleable.PDEListHeaderView_delimiterBackgroundColor)) {
                //to have dark/light style use PDEColor with color id
                int resourceID = sa.getResourceId(R.styleable.PDEListHeaderView_delimiterBackgroundColor, 0);
                if (resourceID != 0) {
                    setDelimiterBackgroundColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setDelimiterBackgroundColor(sa.getColor(R.styleable.PDEListHeaderView_delimiterBackgroundColor,
                                                            R.color.DTBlack));
                }
            }

            sa.recycle();
        }
    }


    /**
     * @brief Set text.
     */
    public void setText(String text) {
        mHeader.setElementText(text);
    }


    /**
     * @brief Get text.
     */
    public String getText() {
        return mHeader.getElementText();
    }


    /**
     * @brief Set text from resource ID.
     */
    @SuppressWarnings("unused")
    public void setTextFromID(int id) {
        if (getResources() != null) {
            setText(getResources().getString(id));
        }
    }


    /**
     * @brief Set subtext.
     */
    public void setSubText(String subtext) {
        mHeader.setElementSubText(subtext);
    }


    /**
     * @brief Get subtext
     */
    @SuppressWarnings("unused")
    public String getSubText() {
        return mHeader.getElementSubText();
    }


    /**
     * @brief Set subtext from resource ID.
     */
    @SuppressWarnings("unused")
    public void setSubTextFromID(int id) {
        if (getResources() != null) {
            setSubText(getResources().getString(id));
        }
    }


    /**
     * @brief Set text color.
     */
    public void setTextColor(int color) {
        mHeader.setElementTextColor(PDEColor.valueOf(color));
    }


    public void setTextColor(PDEColor color) {
        mHeader.setElementTextColor(color);
    }


    /**
     * @brief Get text color
     */
    public PDEColor getTextColor() {
        return mHeader.getElementTextColor();
    }


    /**
     * @brief Set subtext color.
     */
    public void setSubTextColor(int color) {
        mHeader.setElementSubTextColor(PDEColor.valueOf(color));
    }


    public void setSubTextColor(PDEColor color) {
        mHeader.setElementSubTextColor(color);
    }


    /**
     * @brief Get subtext color
     */
    @SuppressWarnings("unused")
    public PDEColor getSubTextColor() {
        return mHeader.getElementSubTextColor();
    }


    /**
     * @brief Set background color.
     */
    public void setBackgroundColor(int color) {
        mHeader.setElementBackgroundColor(PDEColor.valueOf(color));
    }


    public void setBackgroundColor(PDEColor color) {
        mHeader.setElementBackgroundColor(color);
    }


    /**
     * @brief Get background color
     */
    public PDEColor getBackgroundColor() {
        return mHeader.getElementBackgroundColor();
    }


    /**
     * @brief Set delimiter background color.
     */
    public void setDelimiterBackgroundColor(int color) {
        mHeader.setDelimiterBackgroundColor(PDEColor.valueOf(color));
    }


    public void setDelimiterBackgroundColor(PDEColor color) {
        mHeader.setDelimiterBackgroundColor(color);
    }


    /**
     * @brief Get delimiter background color
     */
    @SuppressWarnings("unused")
    public PDEColor getDelimiterBackgroundColor() {
        return mHeader.getDelimiterBackgroundColor();
    }


    /**
     * @brief Set horizontal alignment
     */
    @SuppressWarnings("unused")
    public void setHorizontalAlignment(PDEConstants.PDEAlignment alignment) {
        mHeader.setElementAlignment(alignment);
        requestLayout();
    }


    public void setHorizontalAlignment(int alignment) {
        PDEConstants.PDEAlignment hAlignment;
        try {
            hAlignment = PDEConstants.PDEAlignment.values()[alignment];
        } catch (Exception e) {
            hAlignment = PDEConstants.PDEAlignment.PDEAlignmentLeft;
        }
        mHeader.setElementAlignment(hAlignment);
        requestLayout();
    }


    /**
     * @brief Get horizontal alignment
     */
    @SuppressWarnings("unused")
    public PDEConstants.PDEAlignment getHorizontalAlignment() {
        return mHeader.getElementAlignment();
    }


}

