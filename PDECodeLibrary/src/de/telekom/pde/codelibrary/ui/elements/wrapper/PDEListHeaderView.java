package de.telekom.pde.codelibrary.ui.elements.wrapper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.elements.complex.PDEDrawableListHeader;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//----------------------------------------------------------------------------------------------------------------------
//  PDEListHeaderView
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief Wrapper class hosting a PDEListHeaderView for usage in Layouts
 */
public class PDEListHeaderView extends View {

    private PDEDrawableListHeader mHeader;


    public PDEListHeaderView(Context context){
        super(context);
        init(null);
    }


    public PDEListHeaderView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(attrs);
    }


    public PDEListHeaderView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        init(attrs);
    }


    /**
     * @brief Initialize
     *
     * @param attrs
     */
    protected void init(AttributeSet attrs){
        mHeader = new PDEDrawableListHeader();

        boolean clippingDrawableSet = false;
        Method method;


        // setBackgroundDrawable is marked as deprecated in api level 16, in order to avoid the warning use reflection.
        try {
            //try to use the setBackground function which was introduced in android 4.1 (api level 16)
            method = getClass().getMethod("setBackground", new Class[] {Drawable.class});
            method.invoke(this,mHeader);
            clippingDrawableSet = true;
        } catch (NoSuchMethodException e) {
            // function not available
        } catch (IllegalAccessException e) {
            // function not available
        } catch (InvocationTargetException e) {
            // function not available
        }

        if (!clippingDrawableSet) {
            try {
                //try to use the setBackgroundDrawable which is deprecated in android 4.1
                method = getClass().getMethod("setBackgroundDrawable", new Class[] {Drawable.class});
                method.invoke(this,mHeader);
                clippingDrawableSet = true;
            } catch (NoSuchMethodException e) {
                // function not available
            } catch (IllegalAccessException e) {
                // function not available
            } catch (InvocationTargetException e) {
                // function not available
            }
        }
        setAttributes(attrs);
    }


    /**
     * @brief Load XML attributs
     *
     * @param attrs
     */
    private void setAttributes(AttributeSet attrs) {
        // valid?
        if(attrs==null) return;

        TypedArray sa = getContext().obtainStyledAttributes(attrs, R.styleable.PDEListHeaderView);

        // set text
        if (sa.hasValue(R.styleable.PDEListHeaderView_text)) {
            setText(sa.getString(R.styleable.PDEListHeaderView_text));
        } else {
            String text = attrs.getAttributeValue("http://schemas.android.com/apk/res/android","text");
            if (!TextUtils.isEmpty(text)) {
                setText(text);
            }
        }

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
            // check if we have a light/dark style dependent symbolic color.
            int symbolicColor;
            String text = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","textColor");
            if (text != null && text.startsWith("@")) {
                symbolicColor = Integer.valueOf(text.substring(1));
                if (symbolicColor == R.color.DTUIText) {
                    setTextColor(PDEColor.DTUITextColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIBackground) {
                    setTextColor(PDEColor.DTUIBackgroundColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIInteractive) {
                    setTextColor(PDEColor.DTUIInteractiveColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIIndicative) {
                    setTextColor(PDEColor.DTUIIndicativeTextColor().getIntegerColor());
                }
                // ToDo: ggf. noch DTUITextHighlight und DTUITextCursor abfragen, sobald in PDEColor nachgezogen (Andy)
                // It seems it was no symbolic color, so just set it.
            } else {
                setTextColor(sa.getColor(R.styleable.PDEIconView_iconColor, R.color.DTBlack));
            }
        }

        // set subtext color
        if (sa.hasValue(R.styleable.PDEListHeaderView_subTextColor)) {
            // check if we have a light/dark style dependent symbolic color.
            int symbolicColor;
            String text = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","subTextColor");
            if (text != null && text.startsWith("@")) {
                symbolicColor = Integer.valueOf(text.substring(1));
                if (symbolicColor == R.color.DTUIText) {
                    setSubTextColor(PDEColor.DTUITextColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIBackground) {
                    setSubTextColor(PDEColor.DTUIBackgroundColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIInteractive) {
                    setSubTextColor(PDEColor.DTUIInteractiveColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIIndicative) {
                    setSubTextColor(PDEColor.DTUIIndicativeTextColor().getIntegerColor());
                }
                // ToDo: ggf. noch DTUITextHighlight und DTUITextCursor abfragen, sobald in PDEColor nachgezogen (Andy)
                // It seems it was no symbolic color, so just set it.
            } else {
                setSubTextColor(sa.getColor(R.styleable.PDEIconView_iconColor, R.color.DTBlack));
            }
        }

        // set background color
        if (sa.hasValue(R.styleable.PDEListHeaderView_backgroundColor)) {
            // check if we have a light/dark style dependent symbolic color.
            int symbolicColor;
            String text = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","backgroundColor");
            if (text != null && text.startsWith("@")) {
                symbolicColor = Integer.valueOf(text.substring(1));
                if (symbolicColor == R.color.DTUIText) {
                    setBackgroundColor(PDEColor.DTUITextColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIBackground) {
                    setBackgroundColor(PDEColor.DTUIBackgroundColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIInteractive) {
                    setBackgroundColor(PDEColor.DTUIInteractiveColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIIndicative) {
                    setBackgroundColor(PDEColor.DTUIIndicativeTextColor().getIntegerColor());
                }
                // ToDo: ggf. noch DTUITextHighlight und DTUITextCursor abfragen, sobald in PDEColor nachgezogen (Andy)
                // It seems it was no symbolic color, so just set it.
            } else {
                setBackgroundColor(sa.getColor(R.styleable.PDEIconView_iconColor, R.color.DTBlack));
            }
        }

        // set delimiter background color
        if (sa.hasValue(R.styleable.PDEListHeaderView_delimiterBackgroundColor)) {
            // check if we have a light/dark style dependent symbolic color.
            int symbolicColor;
            String text = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","delimiterBackgroundColor");
            if (text != null && text.startsWith("@")) {
                symbolicColor = Integer.valueOf(text.substring(1));
                if (symbolicColor == R.color.DTUIText) {
                    setDelimiterBackgroundColor(PDEColor.DTUITextColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIBackground) {
                    setDelimiterBackgroundColor(PDEColor.DTUIBackgroundColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIInteractive) {
                    setDelimiterBackgroundColor(PDEColor.DTUIInteractiveColor().getIntegerColor());
                } else if (symbolicColor == R.color.DTUIIndicative) {
                    setDelimiterBackgroundColor(PDEColor.DTUIIndicativeTextColor().getIntegerColor());
                }
                // ToDo: ggf. noch DTUITextHighlight und DTUITextCursor abfragen, sobald in PDEColor nachgezogen (Andy)
                // It seems it was no symbolic color, so just set it.
            } else {
                setDelimiterBackgroundColor(sa.getColor(R.styleable.PDEIconView_iconColor, R.color.DTBlack));
            }
        }
        
        sa.recycle();
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
    public void setTextFromID(int id) {
        setText(getResources().getString(id));
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
    public String getSubText() {
        return mHeader.getElementSubText();
    }


    /**
     * @brief Set subtext from resource ID.
     */
    public void setSubTextFromID(int id) {
        setSubText(getResources().getString(id));
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
    public PDEColor getDelimiterBackgroundColor() {
        return mHeader.getDelimiterBackgroundColor();
    }


    /**
     * @brief Set horizontal alignment
     */
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
    public PDEConstants.PDEAlignment getHorizontalAlignment() {
        return mHeader.getElementAlignment();
    }


    public void setViewSize(float width, float height){
        PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        layerParams.width = Math.round(width);
        layerParams.height = Math.round(height);
        setLayoutParams(layerParams);
    }


    public void setViewOffset(float x, float y){
        PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        layerParams.x = Math.round(x);
        layerParams.y = Math.round(y);
        setLayoutParams(layerParams);
    }


    public void setViewLayoutRect(Rect rect) {
        PDEAbsoluteLayout.LayoutParams layerParams = (PDEAbsoluteLayout.LayoutParams) getLayoutParams();
        layerParams.x = rect.left;
        layerParams.y = rect.top;
        layerParams.width = rect.width();
        layerParams.height = rect.height();

        setLayoutParams(layerParams);
    }
}

