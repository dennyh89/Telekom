/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.complex;


import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDETextView;
import de.telekom.pde.codelibrary.ui.components.lists.internal.SectionHeaderAndListAdapter;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDEString;

//----------------------------------------------------------------------------------------------------------------------
//  PDEListHeaderLayout
//----------------------------------------------------------------------------------------------------------------------


public class PDEListHeaderLayout extends RelativeLayout {

    /**
     * @brief Type of listHeader.
     */
    public enum PDEListHeaderType {
        PDEListHeaderTypeHeadline,
        PDEListHeaderTypeClusterHeadline
    }

    //-----  constants -------------------------------------------------------------------------------------------------

    public static final float PDEDrawableListHeaderFontSizeMainLabelInPercent = 133.0f;
    public static final float PDEDrawableListHeaderFontSizeSubLabelInPercent = 116.0f;

    //-----  properties ------------------------------------------------------------------------------------------------

    protected View mContentView;
    private LayoutInflater mLayoutInflater;

    protected PDETextView mMainLabel;
    protected PDETextView mSubLabel;

    private String mMainText;
    private String mSubText;
    private View mDelimiter;
    private PDEListHeaderType mType;
    private boolean mShowDelimiter;

    private PDEColor mTextColorMain;
    private PDEColor mTextColorSub;


    /**
     * @brief Constructor.
     */
    public PDEListHeaderLayout(Context context) {
        super(context);
        init(context, null);
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEListHeaderLayout(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    /**
     * @brief Constructor.
     */
    @SuppressWarnings("unused")
    public PDEListHeaderLayout(Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    /**
     * @brief initialize
     */
    private void init(Context context, android.util.AttributeSet attrs) {
        mLayoutInflater = LayoutInflater.from(context);
        mMainText = "";
        mSubText = "";
        setShowDelimiter(false);


        // adapt to light / dark style
        if (PDECodeLibrary.getInstance().isDarkStyle()) {
            mTextColorMain = (PDEColor.valueOf("DTDarkUIText"));
            mTextColorSub = (PDEColor.valueOf("DTGrey1"));
        } else {
            mTextColorMain = (PDEColor.valueOf("DTLightUIText"));
            mTextColorSub = (PDEColor.valueOf("DTLightUIIndicativeText"));
        }

        setType(PDEListHeaderType.PDEListHeaderTypeHeadline);
        if (attrs != null) setAttributes(context, attrs);
    }


    /**
     * @brief Load XML attributes
     */
    private void setAttributes(Context context, AttributeSet attrs) {
        // valid?
        if (attrs == null) return;

        TypedArray sa = context.obtainStyledAttributes(attrs, R.styleable.PDEListHeaderLayout);

        // set text
        if (sa != null && sa.hasValue(R.styleable.PDEListHeaderLayout_pde_text)) {
            setText(sa.getString(R.styleable.PDEListHeaderLayout_pde_text));
        } else {
            String text = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
            if (!TextUtils.isEmpty(text)) {
                setText(text);
            }
        }

        if (sa != null) {
            // set subtext
            if (sa.hasValue(R.styleable.PDEListHeaderLayout_pde_subText)) {
                setSubText(sa.getString(R.styleable.PDEListHeaderLayout_pde_subText));
            }

            // set type
            if (sa.hasValue(R.styleable.PDEListHeaderLayout_pde_type)) {
                setType(sa.getInteger(R.styleable.PDEListHeaderLayout_pde_type, 0));
            }

            // set template
            if (sa.hasValue(R.styleable.PDEListHeaderLayout_pde_template)) {
                setTemplate(sa.getInteger(R.styleable.PDEListHeaderLayout_pde_template, -1));
            }

            // draw the delimiter or not
            if (sa.hasValue(R.styleable.PDEListHeaderLayout_pde_showDelimiter)) {
                setShowDelimiter(sa.getBoolean(R.styleable.PDEListHeaderLayout_pde_showDelimiter, false));
            }

            sa.recycle();
        }
    }


    /**
     * @brief Set XML template for layout of list item.
     *
     * @param layoutResourceID ID that addresses the layout resource (xml).
     */
    public void setTemplate(int layoutResourceID) {
        if (layoutResourceID == -1) return;

        View layoutView;
        int labelPaddingHorizontal;
        int horizontalMargin;

        // inflate item layout
        layoutView = mLayoutInflater.inflate(layoutResourceID, null);
        // remember inflated view
        setContentView(layoutView);

        mMainLabel = ((PDETextView) findViewById(R.id.PDEListHeaderLayout_HeaderText));
        mSubLabel = ((PDETextView) findViewById(R.id.PDEListHeaderLayout_HeaderSubText));
        mDelimiter = (findViewById(R.id.PDEListHeaderLayout_Delimiter));

        // init distances to PDE defaults
        labelPaddingHorizontal = PDEBuildingUnits.oneFourthBU();
        horizontalMargin = PDEBuildingUnits.pixelFromBU(0.7f);

        // init main label to PDE defaults
        if (layoutResourceID == R.layout.pde_list_header_layout) {
            mMainLabel.setTextSize(
                    PDEFontHelpers.calculateFontSizeByPercent(mMainLabel.getTypeface(),
                                                              PDEDrawableListHeaderFontSizeMainLabelInPercent));
            mMainLabel.setTextColor(mTextColorMain);
            mSubLabel.setTextColor(mTextColorSub);
        } else {
            mMainLabel.setTextSize(
                    PDEFontHelpers.calculateFontSizeByPercent(mMainLabel.getTypeface(),
                                                              PDEDrawableListHeaderFontSizeSubLabelInPercent));
            mMainLabel.setTextColor(mTextColorSub);
            mSubLabel.setTextColor(mTextColorSub);
        }


        mMainLabel.setPaddingAll(labelPaddingHorizontal + horizontalMargin, 0,
                                 labelPaddingHorizontal, PDEBuildingUnits.BU());

        // init sub label to PDE defaults
        mSubLabel.setTextSize(
                PDEFontHelpers.calculateFontSizeByPercent(mSubLabel.getTypeface(),
                                                          PDEDrawableListHeaderFontSizeSubLabelInPercent));
        mSubLabel.setPaddingAll(labelPaddingHorizontal, 0,
                                labelPaddingHorizontal + horizontalMargin, PDEBuildingUnits.BU());

        updateTexts();
    }


    /**
     * @brief Update Texts in Labels.
     */
    private void updateTexts() {
        mMainLabel.setText(mMainText);
        mSubLabel.setText(mSubText);
    }


    /**
     * @brief Set Type of the header, headline or cluster headline.
     */
    public void setType(PDEListHeaderType type) {
        if (type == mType) return;

        mType = type;

        if (mType == PDEListHeaderType.PDEListHeaderTypeHeadline) {
            setTemplate(R.layout.pde_list_header_layout);
        } else {
            setTemplate(R.layout.pde_list_header_layout_cluster);
        }
    }


    public void setType(int type) {
        PDEListHeaderType t;
        try {
            t = PDEListHeaderType.values()[type];
        } catch (Exception e) {
            t = PDEListHeaderType.PDEListHeaderTypeHeadline;
        }
        setType(t);
    }

    @SuppressWarnings("unused")
    public void setTypeByString(String typeString) {
        PDEListHeaderType type = PDEListHeaderType.PDEListHeaderTypeHeadline;

        if (PDEString.isEqualCaseIndependent(typeString, "headline") ||
            PDEString.isEqualCaseIndependent(typeString, "PDEListHeaderTypeHeadline")) {
            type = PDEListHeaderType.PDEListHeaderTypeHeadline;
        } else if (PDEString.isEqualCaseIndependent(typeString, "clusterHeadline") ||
                   PDEString.isEqualCaseIndependent(typeString, "PDEListHeaderTypeClusterHeadline")) {
            type = PDEListHeaderType.PDEListHeaderTypeClusterHeadline;
        }

        setType(type);
    }


    /**
     * @brief Get Header type.
     */
    public PDEListHeaderType getType() {
        return mType;
    }


    /**
     * @brief Set text of main label.
     *
     * @param text The new text of the main label.
     */
    public void setText(String text) {
        mMainText = text;

        // set text to element
        updateTexts();
    }


    /**
     * @brief Get text of main label.
     *
     * @return text of main label.
     */
    public String getText() {
        return mMainText;
    }


    /**
     * @brief Set text of sub label.
     *
     * @param text The new text of the sub label.
     */
    public void setSubText(String text) {
        mSubText = text;

        // set the new text to the label
        updateTexts();
    }


    /**
     * @brief Get text of sub label.
     *
     * @return text of sub label.
     */
    @SuppressWarnings("unused")
    public String getSubText() {
        return mSubLabel.getText();
    }


    /**
     * @brief Set the already content row view.
     * @brief Set if delimiter is shown.
     */
    public void setShowDelimiter(boolean show) {
        if (mDelimiter != null) {
            if (show) {
                mDelimiter.setVisibility(VISIBLE);
            } else {
                mDelimiter.setVisibility(INVISIBLE);
            }
        }

        mShowDelimiter = show;
    }


    /**
     * @brief Get if delimiter is shown.
     */
    @SuppressWarnings("unused")
    public boolean getShowDelimiter() {
        return mShowDelimiter;
    }


    /**
     * @brief Set the already layouted row view.
     *
     * The adapter creates a view that holds the data of a list item and puts it into the desired design. The
     * view is handed over from the adapter and we wrap it into this PDEListItem in order to add the agent state
     * behaviour.
     *
     * @param view The content row view.
     */
    public void setContentView(View view) {
        // anything to do?
        if (mContentView == view) return;

        // if we already had a subview, remove it
        if (mContentView != null) removeView(mContentView);

        // remember new subview
        mContentView = view;

        // add new subview
        addView(mContentView);
    }


    /**
     * @brief Deliver the already row view.
     *
     * @return The already row view.
     */
    @SuppressWarnings("unused")
    public View getContentView() {
        return mContentView;
    }


    /**
     * @brief Function to fill in the data of the header.
     *
     * Feel free to override to fit your needs. This is just the default implementation.
     */
    public void fillItem(Object data) {
        SectionHeaderAndListAdapter.SectionInfo info;

        if (data instanceof SectionHeaderAndListAdapter.SectionInfo) {
            info = (SectionHeaderAndListAdapter.SectionInfo) data;
            setText(info.getTitle());

            if (info.getCountSupplier() != null && info.getCountSupplier().showCount()) {
                setSubText("(" + info.getCountSupplier().getCountShownInHeader() + ")");
            }
        }
    }

}
