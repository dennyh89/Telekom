/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2014 Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.sectionedbuttons;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Locale;

import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.PDEConstants.PDEAlignment;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton.PDEButtonIconAlignment;
import de.telekom.pde.codelibrary.ui.components.buttons.PDEButton.PDEButtonLayerType;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.common.PDECornerConfigurations;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;


//----------------------------------------------------------------------------------------------------------------------
//  PDESectionedButton
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief The PDESectionedButton is a simple button type with 1 or more sections positioned horizontally next to each
 * other.
 * Internally it works with the normal PDEButton, so the events are the same as the PDEButton has, the only difference
 * at the moment is that the PDESectionedButtonEvent has additional information about the index of the button which
 * was clicked.
 *
 * You can dynamically insert or set each section by hand in the code, or use a xml array with predefined sections.
 *
 * XML information:
 *
 * At first you have do define every sections with 4 items
 * 1. Text in the section -> String (With or without ") or string reference
 * 2. Icon in the same section -> drawable reference, string path (With or without ") ,
 * iconstring (z.b "#p"; need " in this case) ## default is null
 * 3. Colored -> boolean reference, false, true, 0, 1 ## default is false
 * 4. Enabled -> boolean reference, false, true, 0, 1 ## default is true
 *
 * If one information is not needed (because you want the default value) , you can keep
 * it empty "<item></item>", or remove it completely from the array if the following items
 * are also not needed because they are the same as the default values.
 *
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * !!! You can't have different order -> Icon,colored,enabled,text !!!
 * !!!! If you do this you will have unexpected behaviour!!!!!!!!!!!!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 *
 * Valid Examples:
 * <array name="test">
 * <item>First</item> //Text
 * <item>icon.png</item> //icon
 * <item>true</item> // icon colored
 * </array>
 *
 * <array name="test">
 * <item>First</item> // Text
 * <item></item> // no icon
 * <item></item> // not colored -> default
 * <item>false</item> // disabled
 * </array>
 *
 * <array name="test">
 * <item>First</item> // Text
 * </array>
 *
 * <array name="test">
 * <item>First</item>
 * <item>"#p"</item>
 * </array>
 *
 *
 * XML Example:
 *
 * <resources>
 * <array name="SectionedButton_sample_default_section_1">
 * <item>First</item><!--rext:string or string reference-->
 * <item></item><!--icon:drawable reference,iconstring,string path # default is null-->
 * <item></item><!--colored:boolean # default is false-->
 * <item></item><!--enabled # default is true-->
 * </array>
 * <array name="SectionedButton_sample_default_section_2">
 * <item>Second</item><!--ritle:string or string reference-->
 * <item></item><!--icon:drawable reference,iconstring,string path # default is null-->
 * <item></item><!--colored:boolean # default is false-->
 * <item></item><!--enabled # default is true-->
 * </array>
 * <array name="SectionedButton_sample_default_section_3">
 * <item>Third</item><!--ritle:string or string reference-->
 * <item></item><!--icon:drawable reference,iconstring,string path # default is null-->
 * <item></item><!--colored:boolean # default is false-->
 * <item></item><!--enabled # default is true-->
 * </array>
 *
 * <!--this is the part where the different sections are included in the sectionedbutton-->
 * <array name="SectionedButton_sample_default">
 * <item>@array/SectionedButton_sample_default_section_1</item>
 * <item>@array/SectionedButton_sample_default_section_2</item>
 * <item>@array/SectionedButton_sample_default_section_3</item>
 * </array>
 * </resources>
 */
@SuppressWarnings("unused")
public class PDESectionedButton extends LinearLayout implements PDEIEventSource {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDESectionedButton.class.getName();
    // debug messages switch
    private final static boolean DEBUG_OUTPUT = false;


    public final static int PDESectionedButtonNoSectionSelected = -1;  // section index for no selected section button
    public final static String PDESectionedButtonDefaultSelectedColor = "DTGrey237_TakingInput";


    // the sectioned button's parameters
    private PDEParameterDictionary mParameters;

    // the internal used buttons
    private ArrayList<PDEButton> mInternalButtons;

    protected PDEEventSource mEventSource;
    protected ArrayList<Object> mStrongPDEEventListenerHolder;

    private Rect mMinButtonPadding;
    private boolean mMomentary;
    private boolean mToggle;
    private boolean mEnabled;
    private int mSelectedSectionIndex;


    /**
     * @brief Constructor.
     */
    public PDESectionedButton(android.content.Context context) {
        super(context);
        init(context, null);
    }


    /**
     * @brief Constructor.
     */
    public PDESectionedButton(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        if (DEBUG_OUTPUT) {
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                Log.d(LOG_TAG, "PDEButton-Attr(" + i + "): " + attrs.getAttributeName(i)
                               + " => " + attrs.getAttributeValue(i));
            }
        }
        init(context, attrs);
    }


    /**
     * @brief Internal initialisation.
     *
     * Create necessary graphics, start with a default configuration.
     */
    protected void init(Context context, AttributeSet attrs) {

        //init event handling
        mEventSource = new PDEEventSource();
        mStrongPDEEventListenerHolder = new ArrayList<Object>();

        // init button array
        mInternalButtons = new ArrayList<PDEButton>();

        // set some default settings
        setMomentary(false);
        setToggle(false);
        setEnabled(true);
        mMinButtonPadding = new Rect(0, 0, 0, 0);

        // create an empty parameter set (layers have their own default logic)
        mParameters = new PDEParameterDictionary();

        // set selected color
        setSelectedColor(PDESectionedButtonDefaultSelectedColor);

        // smaller horizontal padding
        setHorizontalPadding(PDEBuildingUnits.BU());

        // now set no section selected
        setSelectedSection(PDESectionedButtonNoSectionSelected);

        // set horizontal orientation
        setOrientation(LinearLayout.HORIZONTAL);

        // check xml attributes
        if (attrs != null) {
            TypedArray sa = context.obtainStyledAttributes(attrs, R.styleable.PDESectionedButton);

            // first create layer if wanted by xml, use default flat
            if (sa != null && sa.hasValue(R.styleable.PDESectionedButton_pde_backgroundType)) {
                setButtonBackgroundLayerWithLayerType(sa.getInt(R.styleable.PDESectionedButton_pde_backgroundType, 0));
            } else {
                setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.BackgroundFlat);
            }

            if (sa != null) {
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_textColor)) {
                    //to have dark/light style use PDEColor with color id
                    int resourceID = sa.getResourceId(R.styleable.PDESectionedButton_pde_textColor, 0);
                    if (resourceID != 0) {
                        setTextColor(PDEColor.valueOfColorID(resourceID));
                    } else {
                        setTextColor(sa.getColor(R.styleable.PDESectionedButton_pde_textColor, R.color.DTBlack));
                    }
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_buttonColor)) {
                    //to have dark/light style use PDEColor with color id
                    int resourceID = sa.getResourceId(R.styleable.PDESectionedButton_pde_buttonColor, 0);
                    if (resourceID != 0) {
                        setColor(PDEColor.valueOfColorID(resourceID));
                    } else {
                        setColor(sa.getColor(R.styleable.PDESectionedButton_pde_buttonColor, R.color.DTBlue));
                    }
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_buttonSelectedColor)) {
                    //to have dark/light style use PDEColor with color id
                    int resourceID = sa.getResourceId(R.styleable.PDESectionedButton_pde_buttonSelectedColor, 0);
                    if (resourceID != 0) {
                        setSelectedColor(PDEColor.valueOfColorID(resourceID));
                    } else {
                        setSelectedColor(sa.getColor(R.styleable.PDESectionedButton_pde_buttonSelectedColor,
                                                     R.color.DTBlue));
                    }
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_textSelectedColor)) {
                    //to have dark/light style use PDEColor with color id
                    int resourceID = sa.getResourceId(R.styleable.PDESectionedButton_pde_textSelectedColor, 0);
                    if (resourceID != 0) {
                        setSelectedTextColor(PDEColor.valueOfColorID(resourceID));
                    } else {
                        setSelectedTextColor(sa.getColor(R.styleable.PDESectionedButton_pde_textSelectedColor,
                                                         R.color.DTBlack));
                    }
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_textSize)) {
                    String text_size = sa.getString(R.styleable.PDEButton_pde_textSize);
                    setFontSize(text_size);
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_typeface)) {
                    setFont(PDETypeface.createByName(sa.getString(R.styleable.PDESectionedButton_pde_typeface)));
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_momentary)) {
                    setMomentary(sa.getBoolean(R.styleable.PDESectionedButton_pde_momentary, false));
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_toggle)) {
                    setToggle(sa.getBoolean(R.styleable.PDESectionedButton_pde_toggle, false));
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_textAlignment)) {
                    setAlignment(PDEAlignment.values()[sa.getInt(R.styleable.PDESectionedButton_pde_textAlignment, 0)]);
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_iconAlignment)) {
                    setIconAlignment(PDEButtonIconAlignment.values()[
                                             sa.getInt(R.styleable.PDESectionedButton_pde_iconAlignment, 0)]);
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_cornerRadius)) {
                    setCornerRadius(sa.getDimension(R.styleable.PDESectionedButton_pde_cornerRadius,
                                                    (float) PDEBuildingUnits.oneThirdBU()));
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_borderColor)) {
                    //to have dark/light style use PDEColor with color id
                    int resourceID = sa.getResourceId(R.styleable.PDESectionedButton_pde_borderColor, 0);
                    if (resourceID != 0) {
                        setBorderColor(PDEColor.valueOfColorID(resourceID));
                    } else {
                        setBorderColor(sa.getColor(R.styleable.PDESectionedButton_pde_borderColor, R.color.DTBlack));
                    }
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_numberOfSections)) {
                    setNumberOfSections(sa.getInteger(R.styleable.PDESectionedButton_pde_numberOfSections, 0));
                }

                if (sa.hasValue(R.styleable.PDESectionedButton_pde_sections)) {
                    insertSectionsFromResourceArray(sa.getResourceId(R.styleable.PDESectionedButton_pde_sections, 0));
                }

                if (sa.hasValue(R.styleable.PDESectionedButton_pde_selectedSection)) {
                    setSelectedSection(sa.getInteger(R.styleable.PDESectionedButton_pde_selectedSection,
                                                     PDESectionedButtonNoSectionSelected));
                }

                if (sa.hasValue(R.styleable.PDESectionedButton_pde_minButtonPadding)) {
                    setMinButtonPadding(sa.getDimensionPixelOffset(R.styleable.PDESectionedButton_pde_minButtonPadding, 0),
                                        sa.getDimensionPixelOffset(R.styleable.PDESectionedButton_pde_minButtonPadding, 0),
                                        sa.getDimensionPixelOffset(R.styleable.PDESectionedButton_pde_minButtonPadding, 0),
                                        sa.getDimensionPixelOffset(R.styleable.PDESectionedButton_pde_minButtonPadding, 0));
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_minButtonPaddingLeft)) {
                    setMinButtonPadding(sa.getDimensionPixelOffset(R.styleable.PDESectionedButton_pde_minButtonPaddingLeft,
                                                                   0),
                                        mMinButtonPadding.top,
                                        mMinButtonPadding.right, mMinButtonPadding.bottom);
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_minButtonPaddingTop)) {
                    setMinButtonPadding(mMinButtonPadding.left,
                                        sa.getDimensionPixelOffset(R.styleable.PDESectionedButton_pde_minButtonPaddingTop,
                                                                   0),
                                        mMinButtonPadding.right, mMinButtonPadding.bottom);
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_minButtonPaddingRight)) {
                    setMinButtonPadding(mMinButtonPadding.left, mMinButtonPadding.top,
                                        sa.getDimensionPixelOffset(R.styleable.PDESectionedButton_pde_minButtonPaddingRight,
                                                                   0),
                                        mMinButtonPadding.bottom);
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_minButtonPaddingBottom)) {
                    setMinButtonPadding(mMinButtonPadding.left, mMinButtonPadding.top,
                                        mMinButtonPadding.right,
                                        sa.getDimensionPixelOffset(R.styleable.PDESectionedButton_pde_minButtonPaddingBottom,
                                                                   0)
                    );
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_horizontalPadding)) {
                    setHorizontalPadding((int) sa.getDimension(R.styleable.PDESectionedButton_pde_horizontalPadding,
                                                               PDEBuildingUnits.pixelFromBU(2.0f)));
                }
                if (sa.hasValue(R.styleable.PDESectionedButton_pde_iconToTextHeightRatio)) {
                    setIconToTextHeightRatio(
                            sa.getFloat(R.styleable.PDESectionedButton_pde_iconToTextHeightRatio,
                                        PDEConstants.DefaultPDEButtonIconToTextHeightRatio)
                    );
                }
                //Don't forget this
                sa.recycle();
            }
        }
    }


//----- insert section content handling --------------------------------------------------------------------------------


    /**
     * @brief Function to set the sections via array from xml file
     * @param arrayResourceID The id of the array resource for the section definition.
     */
    public void insertSectionsFromResourceArray(int arrayResourceID) {
        int i;
        TypedArray sections;

        sections = getContext().getResources().obtainTypedArray(arrayResourceID);

        // valid?
        if (sections == null) return;

        // read out sections
        for (i = 0; i < sections.length(); i++) {
            int n;
            String text;
            Object icon;
            Boolean colored;
            Boolean enabled;
            TypedValue textValue;
            TypedValue iconValue;
            TypedValue coloredValue;
            TypedValue enabledValue;
            TypedArray sectionInformation;

            textValue = new TypedValue();
            iconValue = new TypedValue();
            coloredValue = new TypedValue();
            enabledValue = new TypedValue();
            sectionInformation = getContext().getResources().obtainTypedArray(sections.getResourceId(i, 0));

            text = null;
            icon = null;
            colored = false;
            enabled = true;

            // valid?
            if (sectionInformation == null) continue;

            // get values in specific order
            for (n = 0; n < sectionInformation.length(); n++) {
                CharSequence tmpTextString, tmpIconString, tmpColoredString, tmpEnabledString;

                switch (n) {
                    // first -> check text
                    case 0:
                        sectionInformation.getValue(0, textValue);
                        tmpTextString = textValue.coerceToString();
                        text = (tmpTextString == null || tmpTextString.length() == 0) ? null : tmpTextString.toString();
                        break;
                    // second -> check icon
                    case 1:
                        sectionInformation.getValue(1, iconValue);
                        if (iconValue.resourceId != 0) {
                            icon = getContext().getResources().getDrawable(iconValue.resourceId);
                        } else {
                            tmpIconString = iconValue.coerceToString();
                            icon = (tmpIconString == null || tmpIconString.length() == 0)
                                   ? null
                                   : tmpIconString.toString();
                        }
                        break;
                    // third -> icon colored?
                    case 2:
                        sectionInformation.getValue(2, coloredValue);
                        tmpColoredString = coloredValue.coerceToString();
                        colored = (tmpColoredString == null || tmpColoredString.length() == 0)
                                  ? colored
                                  : Boolean.valueOf(tmpColoredString.toString());
                        break;
                    // fourth -> section enabled?
                    case 3:
                        sectionInformation.getValue(3, enabledValue);
                        tmpEnabledString = enabledValue.coerceToString();
                        enabled = (!(tmpEnabledString != null && tmpEnabledString.length() != 0)) ? enabled
                                                                        : Boolean.valueOf(tmpEnabledString.toString());
                        break;
                }
            }
            sectionInformation.recycle();

            if (i < mInternalButtons.size()) {
                // lets insert values
                setSection(text, icon, colored, i);
            } else {
                // lets set values
                insertSection(text, icon, colored, i);
            }
            setSectionEnabled(enabled, i);
        }
        sections.recycle();
    }


    /**
     * @brief Insert a new section with a text at index position.
     * If the position is bigger as the sections in the sectioned button, the new section is always added at the end.
     */
    public void insertSection(String text, int index) {
        insertSection(text, (Drawable) null, false, index);
    }


    /**
     * @brief Insert a new section with a icon (which  could be colored) at index position.
     * If the position is bigger as the sections in the sectioned button, the new section is always added at the end.
     */
    public void insertSection(Drawable icon, boolean colored, int index) {
        insertSection(null, icon, colored, index);
    }


    /**
     * @brief Insert a new section with a icon (which  could be colored) at index position.
     * If the position is bigger as the sections in the sectioned button, the new section is always added at the end.
     */
    public void insertSection(String iconString, boolean colored, int index) {
        insertSection(null, iconString, colored, index);
    }


    /**
     * @brief Insert a new section with a text and icon (which  could be colored) at index position.
     * If the position is bigger as the sections in the sectioned button, the new section is always added at the end.
     * If the position is smaller then 0, it is added at the beginning.
     */
    public void insertSection(String text, Drawable icon, boolean colored, int index) {
        PDEButton newSectionButton;

        // create new button
        newSectionButton = new PDEButton(getContext());
        newSectionButton.setText(text);
        newSectionButton.setIcon(icon, colored);

        // insert button into array and add so superview
        insertButton(newSectionButton, index);
    }


    /**
     * @brief Private helper function to insert a new section with a text and icon (Drawable or string) at index
     * position.
     */
    private void insertSection(String text, Object icon, boolean colored, int index) {
        if (icon instanceof String) {
            insertSection(text, (String) icon, colored, index);
        } else if (icon instanceof Drawable) {
            insertSection(text, (Drawable) icon, colored, index);
        } else {
            insertSection(text, (Drawable) null, colored, index);
        }
    }


    /**
     * @brief Insert a new section with a text and icon (which  could be colored)  at index position.
     * If the position is bigger as the sections in the sectioned button, the new section is always added at the end.
     * If the position is smaller then 0, it is added at the beginning.
     */
    public void insertSection(String text, String iconString, boolean colored, int index) {
        PDEButton newSectionButton;

        // create new button
        newSectionButton = new PDEButton(getContext());
        newSectionButton.setText(text);
        newSectionButton.setIcon(iconString, colored);

        // insert button into array and add so superview
        insertButton(newSectionButton, index);
    }


    /**
     * @brief Insert a new button at the index position to the internal array, sets the corner configuration and adds
     * view.
     */
    private void insertButton(PDEButton newButton, int index) {
        LinearLayout.LayoutParams buttonLayoutParams;

        // security
        if (index > mInternalButtons.size()) index = mInternalButtons.size();

        // add listener
        newButton.addListener(this, "sectionButtonEventReceived", PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK);
        newButton.mergeParameters(mParameters);
        newButton.setEnabled(isEnabled());

        // add to internal button array
        mInternalButtons.add(index, newButton);

        // create layout params with weight
        buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                           ViewGroup.LayoutParams.MATCH_PARENT);
        buttonLayoutParams.weight = 1;

        // add view
        addView(newButton, index, buttonLayoutParams);

        setWeightSum(mInternalButtons.size());

        // set corner configuration after added to array
        setButtonsRoundedCornerConfiguration();
    }


//----- set section content handling -----------------------------------------------------------------------------------


    /**
     * @brief Sets the text of the section at the index position.
     */
    public void setSection(String text, int index) {
        PDEButton button;

        // security
        if (index >= mInternalButtons.size()) return;

        // get button at index
        button = mInternalButtons.get(index);

        // security
        if (button != null) {
            button.setText(text);
        }
    }


    /**
     * @brief Sets the icon (which  could be colored ) of the section at the index position.
     */
    public void setSection(Drawable icon, boolean colored, int index) {
        PDEButton button;

        // security
        if (index >= mInternalButtons.size()) return;

        // get button at index
        button = mInternalButtons.get(index);

        // security
        if (button != null) {
            button.setIcon(icon, colored);
        }
    }


    /**
     * @brief Sets the icon (which  could be colored ) of the section at the index position.
     */
    public void setSection(String iconString, boolean colored, int index) {
        PDEButton button;

        // security
        if (index >= mInternalButtons.size()) return;

        // get button at index
        button = mInternalButtons.get(index);

        // security
        if (button != null) {
            button.setIcon(iconString, colored);
        }
    }


    /**
     * @brief Sets the text and the icon (which could be colored ) of the section at the index position.
     */
    public void setSection(String text, Drawable icon, boolean colored, int index) {
        PDEButton button;

        // security
        if (index >= mInternalButtons.size()) return;

        // get button at index
        button = mInternalButtons.get(index);

        // security
        if (button != null) {
            button.setText(text);
            button.setIcon(icon, colored);
        }
    }


    /**
     * @brief Sets the text and the icon (which  could be colored ) of the section at the index position.
     */
    public void setSection(String text, String iconString, boolean colored, int index) {
        PDEButton button;

        // security
        if (index >= mInternalButtons.size()) return;

        // get button at index
        button = mInternalButtons.get(index);

        // security
        if (button != null) {
            button.setText(text);
            button.setIcon(iconString, colored);
        }
    }


    /**
     * @brief Sets the text and the icon (which  could be colored ) of the section at the index position.
     */
    private void setSection(String text, Object icon, boolean colored, int index) {
        if (icon instanceof String) {
            setSection(text, (String) icon, colored, index);
        } else if (icon instanceof Drawable) {
            setSection(text, (Drawable) icon, colored, index);
        } else {
            setSection(text, (Drawable) null, colored, index);
        }
    }


//----- common button properties setters -------------------------------------------------------------------------------


    /**
     * @brief Set main color.
     *
     * The selected color/text color is reset to the default color.
     * No change handling here, this has to be done in the child implementations.
     */
    public void setColor(PDEColor color) {
        // set the parameters
        setParameter(PDEButton.PDEButtonParameterColor, color);

        // color changed -> set default selected colors again because this is a sub parameter
        setSelectedTextColor((String)null);
        setSelectedColor(PDESectionedButtonDefaultSelectedColor);
    }


    /**
     * @brief Set main color.
     *
     * The selected color/text color is reset to the default color.
     * No change handling here, this has to be done in the child implementations.
     */
    public void setColor(String color) {
        // set the parameters
        setParameter(PDEButton.PDEButtonParameterColor, color);

        // color changed -> set default selected colors again because this is a sub parameter
        setSelectedTextColor((String)null);
        setSelectedColor(PDESectionedButtonDefaultSelectedColor);
    }


    /**
     * @brief Set main color.
     *
     * The selected color/text color is reset to the default color.
     * No change handling here, this has to be done in the child implementations.
     */
    public void setColor(int color) {
        String colorStr;

        // create a string from the color to integrate into parameters
        colorStr = PDEColor.stringFromIntColor(color);

        setColor(colorStr);
    }


    /**
     * @brief Set border color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setBorderColor(PDEColor color) {
        // set the parameters
        setParameter(PDEButton.PDEButtonParameterBorderColor, color);
    }


    /**
     * @brief Set border color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setBorderColor(int color) {
        String colorStr;

        // create a string from the color to integrate into parameters
        colorStr = PDEColor.stringFromIntColor(color);

        // set the parameters
        setParameter(PDEButton.PDEButtonParameterBorderColor, colorStr);
    }


    /**
     * @brief Set border color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setBorderColor(String color) {
        // set the parameters
        setParameter(PDEButton.PDEButtonParameterBorderColor, color);
    }


    /**
     * @brief Set text color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setTextColor(PDEColor color) {
        // set the parameters
        setParameter(PDEButton.PDEButtonParameterTitleColor, color);
    }


    /**
     * @brief Set text color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setTextColor(String color) {
        // set the parameters
        setParameter(PDEButton.PDEButtonParameterTitleColor, color);
    }


    /**
     * @brief Set text color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setTextColor(int color) {
        String colorStr;

        // create a string from the color to integrate into parameters
        colorStr = PDEColor.stringFromIntColor(color);

        // call set text color with string
        setTextColor(colorStr);
    }


    /**
     * @brief Set font.
     *
     * If nothing else is set the size of the font will be calculated fitting to the size of the button.
     * For fixed size (from the font you set with this function) call setFontSize
     */
    public void setFont(PDETypeface font) {
        // set the parameters
        setParameter(PDEButton.PDEButtonParameterFont, font);
    }


    /**
     * @brief Set the font name.
     */
    public void setFontName(String fontName) {
        // set the parameters
        setParameter(PDEButton.PDEButtonParameterFont, fontName);
    }


    /**
     * @brief Set the font size directly, exactly like it would be done for the UIFont.
     */
    public void setFontSize(float size) {
        // set directly as number
        setParameter(PDEButton.PDEButtonParameterFontSize, size);
    }


    /**
     * @brief Set the font size bei size String.
     *
     * The string must follow the format float[unit]. Unit is optional but if present valid values are "BU", "%" and
     * "Caps".
     * It is also possible to set the strings "auto" or "automatic", and to "styleguide"
     */
    public void setFontSize(String sizeString) {
        // set the string
        setParameter(PDEButton.PDEButtonParameterFontSize, sizeString);
    }


    /**
     * @brief Set corner radius
     */
    public void setCornerRadius(float cornerRadius) {
        // set the parameters
        setParameter(PDEButton.PDEButtonParameterCornerRadius, String.format(Locale.ENGLISH, "%.02f", cornerRadius));
    }


    /**
     * @brief Set the horizontal alignment of the used ForegroundIconTextLayer or ForegroundIconFontTextLayer.
     * @param alignment left, right and center alignment are available. center is the default.
     */
    public void setAlignment(PDEConstants.PDEAlignment alignment) {
        String parameterString = null;

        // set as string
        if (alignment == PDEConstants.PDEAlignment.PDEAlignmentLeft) {
            parameterString = PDEConstants.PDEAlignmentStringLeft;
        } else if (alignment == PDEConstants.PDEAlignment.PDEAlignmentCenter) {
            parameterString = PDEConstants.PDEAlignmentStringCenter;
        } else if (alignment == PDEConstants.PDEAlignment.PDEAlignmentRight) {
            parameterString = PDEConstants.PDEAlignmentStringRight;
        }

        setParameter(PDEButton.PDEButtonParameterAlignment, parameterString);
    }


    /**
     * @brief Set the alignment of the icon within the ForegroundIconTextLayer/ForegroundIconFontTextLayer of the button.
     * @param alignment left, right and left-attached, right-attached alignment are available. leftAttached is the default.
     */
    public void setIconAlignment(PDEButtonIconAlignment alignment) {
        String parameterString;
        // set as string
        if (alignment == PDEButtonIconAlignment.PDEButtonIconAlignmentLeft) {
            parameterString = PDEConstants.PDEAlignmentStringLeft;
        } else if (alignment == PDEButtonIconAlignment.PDEButtonIconAlignmentRight) {
            parameterString = PDEConstants.PDEAlignmentStringRight;
        } else if (alignment == PDEButtonIconAlignment.PDEButtonIconAlignmentRightAttached) {
            parameterString = PDEConstants.PDEAlignmentStringRightAttached;
        } else {
            parameterString = PDEConstants.PDEAlignmentStringLeftAttached;
        }
        // set as number
        setParameter(PDEButton.PDEButtonParameterIconAlignment, parameterString);
    }


    /**
     * @brief Set the distance between the border and the icon / text.
     * The default is 2 BU. Do not use a distance less than 1 BU (the styleguide says that is too less)
     */
    public void setHorizontalPadding(float distance) {
        setParameter(PDEButton.PDEButtonParameterHorizontalPadding, distance);
    }


    /**
     * @brief Set the number of sections in the sectionedButton.
     * If there are already some sections, the button will be filled up with the rest sections to reach the limit.
     */
    public void setNumberOfSections(int numberOfSections) {
        int i;

        for (i = mInternalButtons.size(); i < numberOfSections; i++) {
            insertSection(null, i);
        }
    }


    /**
     * @brief Set ratio of icon height to text height.
     */
    public void setIconToTextHeightRatio(float ratio) {
        // set the parameters
        setParameter(PDEButton.PDEButtonParameterIconToTextHeightRatio, ratio);
    }


//----- common button properties getters -------------------------------------------------------------------------------


    /**
     * @brief Get main color.
     *
     * Only retrieves basic parameters, and only if a parameter was set. Default
     * parameters defined in the layers are never received.
     */
    public PDEColor getColor() {
        // retrieve parameter main value
        return mParameters.parameterColorForName(PDEButton.PDEButtonParameterColor);
    }


    /**
     * @brief Get border color.
     *
     * Only retrieves basic parameters, and only if a parameter was set. Default
     * parameters defined in the layers are never received.
     */
    public PDEColor getBorderColor() {
        // retrieve parameter main value
        return mParameters.parameterColorForName(PDEButton.PDEButtonParameterBorderColor);
    }


    /**
     * @brief Get text color.
     *
     * Only retrieves basic parameters, and only if a parameter was set. Default
     * parameters defined in the layers are never received.
     */
    public PDEColor getTextColor() {
        // retrieve parameter main value
        return mParameters.parameterColorForName(PDEButton.PDEButtonParameterTitleColor);
    }


    /**
     * @brief Get the font.
     *
     * Only retrieves basic parameters, and only if a font was explicitly set before.
     */
    public PDETypeface getFont() {
        Object object;

        // get the object
        object = mParameters.parameterObjectForName(PDEButton.PDEButtonParameterFont);

        // type check
        if (object == null || !(object instanceof PDETypeface)) return null;

        // done
        return (PDETypeface) object;
    }


    /**
     * @brief Get font name.
     *
     * Only retrieves basic parameters, and only if a parameter was set -> default parameters are never retrieved.
     */
    public String getFontName() {
        // retrieve parameter main value
        return mParameters.parameterValueForName(PDEButton.PDEButtonParameterFont);
    }


    /**
     * @return the set font size in float or 0.0f if the parameter was not set or was set as a string.
     * @brief Get font size (float).
     *
     * Only retrieves basic parameters, and only if a float was directly set.
     * If you used setFontSizeWithString use fontSizeString to get the font size.
     */
    public float getFontSize() {
        if (mParameters.parameterObjectForName(PDEButton.PDEButtonParameterFontSize) instanceof Float) {
            return mParameters.parameterFloatForName(PDEButton.PDEButtonParameterFontSize);
        }

        return 0.0f;
    }


    /**
     * @brief Get font size string.
     *
     * Only retrieves basic parameters, and only if a string was previously set
     */
    public String getFontSizeString() {
        // retrieve parameter main value
        return mParameters.parameterValueForName(PDEButton.PDEButtonParameterFontSize);
    }


    /**
     * @brief Get corner radius
     */
    public float getCornerRadius() {
        // retrieve base parameter as float
        return mParameters.parameterFloatForName(PDEButton.PDEButtonParameterCornerRadius);
    }


    /**
     * @brief Get icon alignment of ForegroundIconTextLayer/ForegroundIconFontTextLayer
     */
    public PDEButtonIconAlignment getIconAlignment() {

        String iconAlignmentString = mParameters.parameterValueForNameWithDefault(
                PDEButton.PDEButtonParameterIconAlignment,
                PDEConstants.PDEAlignmentStringCenter);
        PDEButtonIconAlignment iconAlignment;

        // parse value
        if (iconAlignmentString.equals(PDEConstants.PDEAlignmentStringLeft)) {
            iconAlignment = PDEButtonIconAlignment.PDEButtonIconAlignmentLeft;
        } else if (iconAlignmentString.equals(PDEConstants.PDEAlignmentStringRight)) {
            iconAlignment = PDEButtonIconAlignment.PDEButtonIconAlignmentRight;
        } else if (iconAlignmentString.equals(PDEConstants.PDEAlignmentStringLeftAttached)) {
            iconAlignment = PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached;
        } else if (iconAlignmentString.equals(PDEConstants.PDEAlignmentStringRightAttached)) {
            iconAlignment = PDEButtonIconAlignment.PDEButtonIconAlignmentRightAttached;
        } else {
            iconAlignment = PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached;
        }

        return iconAlignment;
    }


    /**
     * @brief Get horizontal alignment of used ForegroundIconTextLayer or ForegroundIconFontTextLayer
     */
    public PDEConstants.PDEAlignment getAlignment() {
        String textAlignmentString = mParameters.parameterValueForNameWithDefault(PDEButton.PDEButtonParameterAlignment,
                                                                                  PDEConstants.PDEAlignmentStringCenter);
        PDEConstants.PDEAlignment textAlignment;

        // parse value
        if (textAlignmentString.equals(PDEConstants.PDEAlignmentStringLeft)) {
            textAlignment = PDEConstants.PDEAlignment.PDEAlignmentLeft;
        } else if (textAlignmentString.equals(PDEConstants.PDEAlignmentStringCenter)) {
            textAlignment = PDEConstants.PDEAlignment.PDEAlignmentCenter;
        } else if (textAlignmentString.equals(PDEConstants.PDEAlignmentStringRight)) {
            textAlignment = PDEConstants.PDEAlignment.PDEAlignmentRight;
        } else {
            textAlignment = PDEConstants.PDEAlignment.PDEAlignmentCenter;
        }

        return textAlignment;
    }


    /**
     * @brief Get the set parameter value for the horizontal padding.
     */
    public int getIconTextHorizontalPadding() {
        // retrieve parameter
        return mParameters.parameterIntForName(PDEButton.PDEButtonParameterHorizontalPadding);
    }


    /**
     * @brief returns the number of sections in this sectionedButton.
     */
    public int getNumberOfSections() {
        return mInternalButtons.size();
    }


    /**
     * @brief Get ratio of icon height to text height.
     */
    public float getIconToTextHeightRatio() {
        if (mParameters.parameterObjectForName(PDEButton.PDEButtonParameterIconToTextHeightRatio) instanceof Float) {
            return mParameters.parameterFloatForName(PDEButton.PDEButtonParameterIconToTextHeightRatio);
        }

        return 0.0f;
    }


    /**
     * Get the current selected Section index. Could be PDESectionedButtonNoSectionSelected if nothing is selected.
     * @return The current index.
     */
    public int getSelectionSection() {
        return mSelectedSectionIndex;
    }


//----- parameter update -----------------------------------------------------------------------------------------------


    /**
     * @brief Set parameter, distribute changes.
     */
    private void setParameter(String name, String value) {
        // set it
        mParameters.setParameter(name, value);

        // distribute to all internal buttons
        sendParametersToSectionButtons();
    }


    /**
     * @brief Set parameter, distribute changes.
     */
    private void setParameter(String name, Object object) {
        // set it
        mParameters.setParameter(name, object);

        // distribute to all internal buttons
        sendParametersToSectionButtons();
    }


    /**
     * @brief Set parameter, distribute changes.
     */
    private void mergeParameter(String name, String value, String key) {
        // set it
        mParameters.mergeParameter(name, value, key);

        // distribute to all internal buttons
        sendParametersToSectionButtons();
    }


    /**
     * @brief Set parameter, distribute changes.
     */
    private void mergeParameter(String name, Object object, String key) {
        // set it
        mParameters.mergeParameter(name, object, key);

        // distribute to all internal buttons
        sendParametersToSectionButtons();
    }


    /**
     * @brief Helper function. Distribute the parameters to all sub buttons.
     *
     * The sub-components are responsible to extract the things they need and for change management.
     */
    private void sendParametersToSectionButtons() {
        // do for all
        for (PDEButton tmpButton : mInternalButtons) {
            // go through all of them
            for (String key : mParameters.getParameters().keySet()) {
                PDEParameter tmpParam;

                tmpParam = mParameters.parameterForName(key);
                // check if parameter was set with empty value
                // if empty -> setParameter to be sure it is set (merge could keep old one)
                if (tmpParam.isEmpty()) {
                    tmpButton.setParameter(key, tmpParam);
                } else {
                    // merge parameters to keep button internal parameters
                    tmpButton.mergeParameter(key, tmpParam);
                }
            }
        }
    }


    //----- remove section content handling --------------------------------------------------------------------------------


    public void removeSectionAtIndex(int index) {
        PDEButton button;

        // security
        if (index >= mInternalButtons.size()) return;

        // get button and remove listener
        button = mInternalButtons.get(index);
        button.removeListener(this);

        // remove from superview
        removeView(button);

        // remove from array
        mInternalButtons.remove(index);
    }


    public void removeAllSections() {
        int i;

        // remove all buttons
        for (i = mInternalButtons.size() - 1; i >= 0; i--) {
            removeSectionAtIndex(i);
        }
    }


//----- commonly used layer access -------------------------------------------------------------------------------------


    /**
     * @brief Select one of the default backgrounds
     */
    public void setButtonBackgroundLayerWithLayerType(PDEButtonLayerType layerType) {
        setParameter(PDEButton.PDEButtonParameterBackground, layerType);
    }


    /**
     * @brief Select one of the default backgrounds
     */
    public void setButtonBackgroundLayerWithLayerType(int layerType) {
        setParameter(PDEButton.PDEButtonParameterBackground, layerType);
    }


    /**
     * @brief Set default backgrounds by string
     */
    public void setButtonBackgroundLayerWithLayerTypeString(String layerType) {
        setParameter(PDEButton.PDEButtonParameterBackground, layerType);
    }


//----- enabled/disabled information --------------------------------------------------------------------------------------


    /**
     * @brief Enabled state (overridden from iOS).
     * Disables/Enables all internal buttons.
     */
    public void setEnabled(boolean enabled) {
        int i;

        // change?
        if (mEnabled == enabled) return;

        mEnabled = enabled;

        // loop through button list
        for (i = 0; i < mInternalButtons.size(); i++) {
            setSectionEnabled(mEnabled, i);
        }
    }


    /**
     * @brief Check enabled state (overridden from iOS).
     */
    public boolean isEnabled() {
        return mEnabled;
    }


    /**
     * @brief Enables/Disables specific section.
     */
    public void setSectionEnabled(boolean enabled, int index) {
        PDEButton button;

        // security
        if (index >= mInternalButtons.size()) return;

        // if disabled it cant be set to enabled
        if (!isEnabled() && enabled) return;

        // get button
        button = mInternalButtons.get(index);

        // security
        if (button != null) {
            if (button.isSelected() && enabled && !isToggle()) {
                button.setVisualEnabled(true);
            } else {
                button.setEnabled(enabled);
            }
        }
    }


    /**
     * @brief Checks enabled state at specific section.
     */
    public boolean isSectionEnabled(int index) {
        PDEButton button;

        // security
        if (index >= mInternalButtons.size()) return false;

        // if completely disabled, every section is disabled
        if (!isEnabled()) return false;

        // get button
        button = mInternalButtons.get(index);

        // security
        if (button != null) {
            return button.isSelected() ? button.isVisualEnabled() : button.isEnabled();
        }

        // no button -> not enabled
        return false;
    }


//----- setter/getter functions ----------------------------------------------------------------------------------------


    /**
     * @brief Select the section button at the selectedSection position.
     */
    public void setSelectedSection(int selectedSectionIndex) {
        PDEButton button;

        // no selection in momentary mode
        if (isMomentary()) return;

        // no section selected
        if (selectedSectionIndex == PDESectionedButtonNoSectionSelected) {
            int i;
            for (i = 0; i < mInternalButtons.size(); i++) {
                PDEButton tmpButton;

                tmpButton = mInternalButtons.get(i);
                // check enabled state
                if (isSectionEnabled(i)) {
                    tmpButton.setUserInteractionEnabled(true);
                }
                tmpButton.setSelected(false);
            }
            // remember
            mSelectedSectionIndex = PDESectionedButtonNoSectionSelected;
            return;
        }

        // get button at index
        button = mInternalButtons.get(selectedSectionIndex);

        // security
        if (button == null) return;

        // set selections for all buttons
        for (PDEButton tmpButton : mInternalButtons) {
            if (tmpButton == button) {
                tmpButton.setSelected(isToggle() ? !tmpButton.isSelected() : true);
                // check visual enabled state to know if it was selected and visual disabled before
                if (tmpButton.isVisualEnabled()) {
                    tmpButton.setUserInteractionEnabled(isToggle());
                }
            } else if (tmpButton.isSelected()) {
                tmpButton.setSelected(false);
                // check visual enabled state to know if it was selected and visual disabled before
                if (tmpButton.isVisualEnabled()) {
                    tmpButton.setUserInteractionEnabled(true);
                }
            }
        }

        // remember
        mSelectedSectionIndex = selectedSectionIndex;
    }


    /**
     * @brief Enable/Disable toggle mode of not momentary sectioned button.
     * (Enable/Disable user interaction of the buttons internal)
     */
    public void setToggle(boolean toggle) {
        // change?
        if (mToggle == toggle) return;

        // disable input enabled of all selected buttons
        for (PDEButton tmpButton : mInternalButtons) {
            if (tmpButton.isSelected()) {
                tmpButton.setUserInteractionEnabled(toggle && !isMomentary());
            }
        }

        // remember toggle mode
        mToggle = toggle;
    }


    /**
     * @brief Enable/Disable momentary mode.
     * In this mode no selection is possible. If one section is selected, it will be deselected when momentary mode will be enabled.
     */
    public void setMomentary(boolean momentary) {
        // change?
        if (mMomentary == momentary) return;

        // disable input enabled of all selected buttons
        for (PDEButton tmpButton : mInternalButtons) {
            if (tmpButton.isSelected()) {
                tmpButton.setUserInteractionEnabled(!momentary && isToggle());
            }
        }
        // no selection in momentary mode
        setSelectedSection(PDESectionedButtonNoSectionSelected);

        // remember mode
        mMomentary = momentary;
    }


    /**
     * Get the current state of the toggle mode.
     */
    public boolean isToggle() {
        return mToggle;
    }


    /**
     * Get the current state of the momentary mode.
     */
    public boolean isMomentary() {
        return mMomentary;
    }


//----- helper for selection coloring ----------------------------------------------------------------------------------


    /**
     * @brief Helper function to set the color for the PDEButtonStateSelected state.
     * The color is reset to the default color, if the button color is changed.
     */
    public void setSelectedColor(PDEColor color) {
        mergeParameter(PDEButton.PDEButtonParameterColor, color, PDEButton.PDEButtonStateSelected);
    }


    /**
     * @brief Helper function to set the color for the PDEButtonStateSelected state.
     * * The color is reset to the default color, if the button color is changed.
     */
    public void setSelectedColor(String color) {
        mergeParameter(PDEButton.PDEButtonParameterColor, color, PDEButton.PDEButtonStateSelected);
    }


    /**
     * @brief Helper function to set the color for the PDEButtonStateSelected state.
     * * The color is reset to the default color, if the button color is changed.
     */
    public void setSelectedColor(int color) {
        String colorStr;

        // create a string from the color to integrate into parameters
        colorStr = PDEColor.stringFromIntColor(color);

        setSelectedColor(colorStr);
    }


    /**
     * @brief Get selected color.
     *
     * Only retrieves basic parameters, and only if a parameter was set. Default
     * parameters defined in the layers are never received.
     */
    public PDEColor getSelectedColor() {
        PDEParameter param;
        PDEColor selectedColor;

        selectedColor = null;

        param = mParameters.parameterForName(PDEButton.PDEButtonParameterColor);
        if (param != null) {
            selectedColor = param.getColorForKey(PDEButton.PDEButtonStateSelected);
        }

        // retrieve parameter main value
        return selectedColor;
    }


    /**
     * @brief Helper function to set the text color for the PDEButtonStateSelected state.
     */
    public void setSelectedTextColor(PDEColor color) {
        mergeParameter(PDEButton.PDEButtonParameterTitleColor, color, PDEButton.PDEButtonStateSelected);
    }


    /**
     * @brief Helper function to set the text color for the PDEButtonStateSelected state.
     */
    public void setSelectedTextColor(String color) {
        mergeParameter(PDEButton.PDEButtonParameterTitleColor, color, PDEButton.PDEButtonStateSelected);
    }


    /**
     * @brief Helper function to set the text color for the PDEButtonStateSelected state.
     */
    public void setSelectedTextColor(int color) {
        String colorStr;

        // create a string from the color to integrate into parameters
        colorStr = PDEColor.stringFromIntColor(color);

        setSelectedTextColor(colorStr);
    }


    /**
     * @brief Get selected text color.
     *
     * Only retrieves basic parameters, and only if a parameter was set. Default
     * parameters defined in the layers are never received.
     */
    public PDEColor getSelectedTextColor() {
        PDEParameter param;
        PDEColor selectedTextColor;

        selectedTextColor = null;

        param = mParameters.parameterForName(PDEButton.PDEButtonParameterTitleColor);
        if (param != null) {
            selectedTextColor = param.getColorForKey(PDEButton.PDEButtonStateSelected);
        }

        // retrieve parameter main value
        return selectedTextColor;
    }


//----- internal helper ------------------------------------------------------------------------------------------------


    /**
     * @brief Set the corner configuration of the internal buttons.
     */
    private void setButtonsRoundedCornerConfiguration() {
        // set buttons corners
        for (PDEButton tmpButton : mInternalButtons) {
            if (tmpButton == mInternalButtons.get(0)) {
                tmpButton.setRoundedCornerConfiguration(PDECornerConfigurations.PDECornerConfigurationTopLeft
                                                        | PDECornerConfigurations.PDECornerConfigurationBottomLeft);

                if (tmpButton.getLayoutParams() != null) {
                    ((LinearLayout.LayoutParams) tmpButton.getLayoutParams()).setMargins(0, 0, -1, 0);
                }
            } else if (tmpButton == mInternalButtons.get(mInternalButtons.size() - 1)) {
                tmpButton.setRoundedCornerConfiguration(PDECornerConfigurations.PDECornerConfigurationTopRight
                                                        | PDECornerConfigurations.PDECornerConfigurationBottomRight);
            } else {
                tmpButton.setRoundedCornerConfiguration(PDECornerConfigurations.PDECornerConfigurationNoCorners);
                if (tmpButton.getLayoutParams() != null) {
                    ((LinearLayout.LayoutParams) tmpButton.getLayoutParams()).setMargins(0, 0, -1, 0);
                }
            }
        }
    }


    //----- layout ---------------------------------------------------------------------------------------------------------


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // if width is not at most, just do default stuff
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.AT_MOST) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int maxWidth = 0;

        // measure with unspecified to get measured wanted size
        for (PDEButton tmpButton : mInternalButtons) {
            tmpButton.measure(
                    MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.UNSPECIFIED),
                    heightMeasureSpec);
            maxWidth = Math.max(maxWidth, tmpButton.getMeasuredWidth());
        }

        // limit max width/height
        maxWidth = Math.min(maxWidth, MeasureSpec.getSize(widthMeasureSpec) / mInternalButtons.size());

        // set the max wanted size to all components
        for (PDEButton tmpButton : mInternalButtons) {
            tmpButton.measure(
                    MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY),
                    heightMeasureSpec);
        }

        setMeasuredDimension(maxWidth * mInternalButtons.size(), MeasureSpec.getSize(heightMeasureSpec));
    }


    //----- layout helper functions ----------------------------------------------------------------------------------------


    /**
     * @return the needed padding, can be null when the used internal button padding is null or there is are 0 sections.
     * @brief Returns the padding the component needs to be displayed correctly.
     *
     * Some things like an outer shadow have to be drawn outside of the layer bounds.
     * So the View that holds the element has to be sized bigger than the element bounds.
     * For proper layouting the view must be extended to each direction by the value delivered by
     * this function.
     */
    public Rect getNeededPadding() {
        PDEButton tmpButton;

        // just get the first button, because all internal used buttons must be the same type
        try {
            tmpButton = mInternalButtons.get(0);
            return tmpButton.getNeededPadding();
        } catch (IndexOutOfBoundsException exception) {
            return new Rect(0, 0, 0, 0);
        }
    }


    /**
     * @brief Set the minimum button padding
     *
     * @param left   Left padding
     * @param top    Top padding
     * @param right  Right padding
     * @param bottom Bottom padding
     */
    public void setMinButtonPadding(int left, int top, int right, int bottom) {
        // same logic in PDEButton -> if you change this also change in PDEButton
        if (left >= 0 && top >= 0 && right >= 0 && bottom >= 0 &&
            (left != mMinButtonPadding.left
             || top != mMinButtonPadding.top
             || right != mMinButtonPadding.right
             || bottom != mMinButtonPadding.right)) {
            mMinButtonPadding = new Rect(left, top, right, bottom);
            // set buttons corners
            for (PDEButton tmpButton : mInternalButtons) {
                if (tmpButton == mInternalButtons.get(0)) {
                    // first button
                    tmpButton.setMinButtonPadding(left, top, mMinButtonPadding.right, bottom);
                } else if (tmpButton == mInternalButtons.get(mInternalButtons.size() - 1)) {
                    // last button
                    tmpButton.setMinButtonPadding(mMinButtonPadding.left, top, right, bottom);
                } else {
                    tmpButton.setMinButtonPadding(mMinButtonPadding.left, top, mMinButtonPadding.right, bottom);
                }
            }
        }
    }


    /**
     * Return the minimum button padding
     *
     * @return Rect with the padding for each side.
     */
    public Rect getMinButtonPadding() {
        return mMinButtonPadding;
    }


//----- event handling -------------------------------------------------------------------------------------------------


    /**
     * @brief Event listener function.
     */
    public void sectionButtonEventReceived(PDEEvent event) {
        int sectionIndex;
        PDESectionedButtonEvent sectionedButtonEvent;

        // get section index of specific object
        // noinspection SuspiciousMethodCalls
        sectionIndex = mInternalButtons.indexOf(event.getSender());

        if (event.getType().equals(PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED)
            && !isMomentary()) {
            setSelectedSection(sectionIndex);
        }

        // event finished
        event.setProcessed();

        // create new event with old event information
        sectionedButtonEvent = new PDESectionedButtonEvent();
        sectionedButtonEvent.setSelectedSectionIndex(sectionIndex);
        sectionedButtonEvent.setType(event.getType());
        sectionedButtonEvent.setSender(this);

        getEventSource().sendEvent(sectionedButtonEvent);
    }


//----- Event Handling -------------------------------------------------------------------------------------------------


    /**
     * @return PDEEventSource
     * @brief Get the eventSource which is responsible for sending PDEEvents events.
     * Most of the events are coming form the PDEAgentController.
     */
    @Override
    public PDEEventSource getEventSource() {
        return mEventSource;
    }


    /**
     * @brief Add event Listener - hold strong pointer to it.
     *
     * PDEIEventSource Interface implementation, with additional local storage of (strong) pointer to it.
     * @see de.telekom.pde.codelibrary.ui.events.PDEEventSource#addListener
     * @param target     Object which will be called in case of an event.
     * @param methodName Function in the target object which will be called.
     *                   The method must accept one parameter of the type PDEEvent
     * @return Object which can be used to remove this listener
     */
    @Override
    public Object addListener(Object target, String methodName) {
        mStrongPDEEventListenerHolder.add(target);
        return mEventSource.addListener(target, methodName);
    }


    /**
     * @brief Add event Listener - hold strong pointer to it.
     *
     * PDEIEventSource Interface implementation, with additional local storage of (strong) pointer to it.
     * @see de.telekom.pde.codelibrary.ui.events.PDEEventSource#addListener
     * @param target     Object which will be called in case of an event.
     * @param methodName Function in the target object which will be called.
     *                   The method must accept one parameter of the type PDEEvent
     * @param eventMask  PDEAgentController event mask.
     *                   Will be most of the time PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED or
     *                   PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED
     * @return Object which can be used to remove this listener
     */
    @Override
    public Object addListener(Object target, String methodName, String eventMask) {
        mStrongPDEEventListenerHolder.add(target);
        return mEventSource.addListener(target, methodName, eventMask);
    }


    /**
     * @brief Remove event listener that was added before.
     *
     * Also deletes local strong pointer.
     * @param listener the event listener that should be removed
     * @return Returns whether we have found & removed the listener or not
     */
    public boolean removeListener(Object listener) {
        mStrongPDEEventListenerHolder.remove(listener);
        return mEventSource.removeListener(listener);
    }

}
