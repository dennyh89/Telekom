/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.PDEConstants.PDEAlignment;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentControllerAdapterView;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableCornerBox;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;
import de.telekom.pde.codelibrary.ui.helpers.PDEDictionary;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

import java.util.ArrayList;
import java.util.Locale;

//----------------------------------------------------------------------------------------------------------------------
//  PDEButton
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Basic button, controlled by agent states.
 *
 * The button does nothing itself, it just manages sublayers which do the actual animation. Buttons can (but must
 * not be) separated into background, foreground and overlay layers.
 *
 * PDEButton offers a few predefined button variants as preset, but does not prevent you using your own graphics.
 */
@SuppressWarnings("unused")
public class PDEButton extends PDEAbsoluteLayout implements PDEIEventSource {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEButton.class.getName();
    // debug messages switch
    private final static boolean DEBUG_OUTPUT = false;




//----------------------------------------------------------------------------------------------------------------------
//  PDEButton helper classes
//----------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Holder class for layer data.
     */
    protected class PDEButtonLayerHolder {

        // data
        public PDEButtonLayerInterface mLayer;
        public PDEButtonLayerId mLayerId;
        public PDEButtonLayerType mLayerType;
    }


    /**
     * @brief Parcelable class for storing button states
     */
    static class SavedState extends BaseSavedState {
        boolean checked;
        String mainState;

        /**
         * Constructor called from {@link PDEButton#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            checked = (in.readInt() == 1);
            mainState = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(checked);
            out.writeValue(mainState);
        }

        @Override
        public String toString() {
            return "PDEButton.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checked=" + checked
                    + " mainState=" + mainState +
                    "}";
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


//----------------------------------------------------------------------------------------------------------------------
//  PDEButton constants
//----------------------------------------------------------------------------------------------------------------------


    // parameter strings
    //
    public static final String PDEButtonParameterTitle = "title";
    public static final String PDEButtonParameterTitleColor = "titleColor";
    public static final String PDEButtonParameterColor = "color";
    public static final String PDEButtonParameterIcon = "Icon";
    public static final String PDEButtonParameterIconColored = "iconColored";
    public static final String PDEButtonParameterAlignment = "textIconLayerTextAlignment";
    public static final String PDEButtonParameterIconAlignment = "textIconLayerIconAlignment";
    public static final String PDEButtonParameterBorderColor = "borderColor";
    public static final String PDEButtonParameterCornerRadius = "cornerRadius";
    public static final String PDEButtonParameterFont = "font";
    public static final String PDEButtonParameterFontSize = "fontSize";
    public static final String PDEButtonParameterBackground = "background";
    public static final String PDEButtonParameterCheckboxAlignment ="checkboxAlignment";
    public static final String PDEButtonParameterCheckboxSize ="checkboxSize";
    public static final String PDEButtonParameterRadioAlignment ="radioAlignment";
    public static final String PDEButtonParameterRadioSize ="radioSize";
    public static final String PDEButtonParameterRoundedCornerConfiguration ="roundedCornerConfiguration";
    public static final String PDEButtonParameterIconToTextHeightRatio ="iconToTextHeightRatio";
    public static final String PDEButtonParameterHorizontalPadding = "horizontalPadding";

    // well known parameter data strings
    //
    public static final String PDEButtonParameterValueSizeAuto = "auto";
    public static final String PDEButtonParameterValueSizeAutomatic = "automatic";
    public static final String PDEButtonParameterValueSizeStyleguide = "styleguide";


    // well known hints
    //
    public static final String PDEButtonHintDarkStyle = "darkStyle";
    public static final String PDEButtonHint3DStyle = "3DStyle";
    public static final String PDEButtonHintDefaultColor = "defaultColor";
    public static final String PDEButtonHintBackgroundColor = "backgroundColor";
    public static final String PDEButtonHintTextOnTransparentColor = "textOnTransparentColor";


    // well known state strings
    //
    public static final String PDEButtonStateDefault = "default";
    public static final String PDEButtonStateSelected = "selected";
    public static final String PDEButtonStateDefaultIdle = "default.idle";


    // well known agent state strings
    //
    public static final String PDEButtonAgentStateIdle = "idle";
    public static final String PDEButtonAgentStateDown = "down";
    public static final String PDEButtonAgentStateFocus = "focus";
    public static final String PDEButtonAgentStateTakingInput = "takinginput";
    public static final String PDEButtonColorSuffixLighter = ".lighter";
    public static final String PDEButtonColorSuffixDarker = ".darker";



    private OnClickListener mOnClickListener;
    private Object mAgentStateListenWillBeSelected;
    protected ArrayList<Object> mStrongPDEEventListenerHolder;

    //----- constants -----


    /**
     * @brief Well known layer ids.
     */
    public enum PDEButtonLayerId {
        Background,
        Overlay,
        Foreground
    }

    /**
     * @brief Telekom well known button layer types.
     */
    public enum PDEButtonLayerType {
        /* ATTENTION: IF YOU ADD A NEW TYPE HERE, DON'T FORGET TO ADD IT IN ATTRS.XML AS WELL!!!!!
            AND IF YOU ADD IT IN BETWEEN, YOU HAVE TO ADJUST THE INDEXES OF ALL FOLLOWING TYPES IN ATTRS.XML AS WELL!!!
         */
        None,
        User,
        ForegroundNone,
        ForegroundUser,
        ForegroundIconText,
        BackgroundNone,
        BackgroundUser,
        BackgroundFlat,
        BackgroundHaptic,
        BackgroundBeveled,      // deprecated; Use BackgroundHaptic instead;
        BackgroundText,
        BackgroundTextFlat,
        BackgroundTextHaptic,
        OverlayNone,
        OverlayUser,
        OverlayCheckbox,    // deprecated; Use OverlayCheckboxFlat instead;
        OverlayRadio,       // deprecated; Use OverlayRadioFlat instead;
        OverlayCheckboxHaptic,
        OverlayCheckboxFlat,
        OverlayRadioHaptic,
        OverlayRadioFlat
    }


    /**
     * @brief Alignment of Icon within the button.
     */
    public enum PDEButtonIconAlignment {
        PDEButtonIconAlignmentLeft,
        PDEButtonIconAlignmentRight,
        PDEButtonIconAlignmentLeftAttached,
        PDEButtonIconAlignmentRightAttached
    }


    //----- properties -----

    // the button's parameters
    PDEParameterDictionary mParameters;

    // layers
    ArrayList<PDEButtonLayerHolder> mButtonLayers;

    // hints
    PDEDictionary mHints;

    // type properties for parameters
    PDEDictionary mAcceptedTypes;

    // local properties
    protected PDEDictionary mLayerHints;
    protected PDEDictionary mMergedHints;

    // private variables
    // agent controller and helpers
    private PDEAgentController mAgentController;
    private PDEAgentControllerAdapterView mAgentAdapter;
    private PDEButtonLayerInterface mButtonLayerToInitialize;


    // layout stuff
    Rect mMinButtonPadding;

    protected PDEEventSource mEventSource;

    protected PDEButtonPadding mButtonPadding;

    PDEAgentHelper mAgentHelper;


    public PDEButton(android.content.Context context){
        super(context);
        init(null);
    }


    public PDEButton(android.content.Context context, android.util.AttributeSet attrs){
        super(context,attrs);
        if(DEBUG_OUTPUT){
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                Log.d(LOG_TAG, "PDEButton-Attr("+i+"): "+attrs.getAttributeName(i)+" => "+attrs.getAttributeValue(i));
            }
        }
        init(attrs);
    }


    public PDEButton(android.content.Context context, android.util.AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(attrs);
    }


    /**
     * @brief Internal initialisation.
     *
     * Create necessary graphics, start with a default configuration.
     */
    protected void init(AttributeSet attrs){
        String titleText;

        // don't do the init when shown in developer tool (IDE)
        if (isInEditMode()) {
            this.setBackgroundColor(0xedededed);
            // create a TextView as a child, to show at least the title.
            TextView tv = new TextView(getContext());
            tv.setGravity(Gravity.CENTER);
            this.addView(tv, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));


            if (attrs != null) {
                TypedArray sa = getContext().obtainStyledAttributes(attrs, R.styleable.PDEButton);
                titleText = sa.getString(R.styleable.PDEButton_text);
                if (TextUtils.isEmpty(titleText)) {
                    // try to get "android:text" attribute instead

                    // first check if it is a resource id ...
                    int resourceId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "text", -1);
                    if (resourceId > 0) {
                        titleText = getResources().getString(resourceId);
                    } else {
                        // otherwise handle it as string
                        titleText = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
                    }
                }

                if (titleText != null) {
                    tv.setText(titleText);
                }

            }

            return;
        }

        //init event handling
        mEventSource = new PDEEventSource();
        mStrongPDEEventListenerHolder = new ArrayList<Object>();

        // init
        mButtonLayerToInitialize = null;
        mButtonPadding = new PDEButtonPadding();
        mMinButtonPadding = new Rect(0,0,0,0);


        // create elements
        mButtonLayers = new ArrayList<PDEButtonLayerHolder>();

        // create an empty parameter set (layers have their own default logic)
        mParameters = new PDEParameterDictionary();

        // create empty hint sets
        mHints = new PDEDictionary();
        mLayerHints = new PDEDictionary();
        mMergedHints = new PDEDictionary();

        mAgentHelper = new PDEAgentHelper();

        // switch hardware acceleration off -> shadows are not updated correctly
        if (PDECodeLibrary.getInstance().isSoftwareRenderingButton()
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            if (DEBUG_OUTPUT) Log.d(LOG_TAG,"Switching Hardware acceleration OFF!!!!!!!!");
            setLayerType(LAYER_TYPE_SOFTWARE,null);
        }

        // android specific initialization
        // we need to be clickable otherwise we are not a button.
        setClickable(true);

        // we also should be focusable
        setFocusable(true);

        setClipChildren(true);

        setClipToPadding(false);

        LayoutInflater.from(getContext()).inflate(R.layout.pdebutton, this, true);

        //LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        //addView(RelativeLayout.inflate(getContext(), R.layout.pdebutton, this), lp);

        // we have our own agent controller
        initAgent();

        // Define type properties for parameters
        if (mAcceptedTypes != null) {
            // Define possible classes
            Class<String> stringClass = String.class;
            Class<Number> numberClass = Number.class;
            Class<PDEColor> colorClass = PDEColor.class;
            Class<Drawable> imageClass = Drawable.class;
            
            // Create lists for dictionary
            ArrayList<Class<?>> parameterListTitle = new ArrayList<Class<?>>();
            ArrayList<Class<?>> parameterListTitleColor = new ArrayList<Class<?>>();
            ArrayList<Class<?>> parameterListColor = new ArrayList<Class<?>>();
            ArrayList<Class<?>> parameterListIcon = new ArrayList<Class<?>>();
            ArrayList<Class<?>> parameterListBorderColor = new ArrayList<Class<?>>();
            ArrayList<Class<?>> parameterListCornerRadius = new ArrayList<Class<?>>();
            ArrayList<Class<?>> parameterListFont = new ArrayList<Class<?>>();
            ArrayList<Class<?>> parameterListBackground = new ArrayList<Class<?>>();
            ArrayList<Class<?>> parameterListHorizontalPadding = new ArrayList<Class<?>>();

            // add accepted types for parameter title
            parameterListTitle.add(stringClass);
            
            // add accepted types for parameter title color
            parameterListTitleColor.add(colorClass);
            parameterListTitleColor.add(stringClass);
            
            // add accepted types for parameter color
            parameterListColor.add(colorClass);
            parameterListColor.add(stringClass);
            
            // add accepted types for parameter icon
            parameterListIcon.add(imageClass);
            parameterListIcon.add(stringClass);
            
            // add accepted types for parameter border color
            parameterListBorderColor.add(colorClass);
            parameterListBorderColor.add(stringClass);
            
            // add accepted types for parameter corner radius
            parameterListCornerRadius.add(numberClass);
            parameterListCornerRadius.add(stringClass);

            // add accepted types for parameter font
            parameterListFont.add(stringClass);
            
            // add accepted types for parameter background
            parameterListBackground.add(numberClass);
            parameterListBackground.add(stringClass);

            // add accepted types for parameter horizontal padding
            parameterListHorizontalPadding.add(numberClass);
            
            // Set accepted types for every parameter - target class is first element!
            mAcceptedTypes = new PDEDictionary(
                    parameterListTitle, PDEButtonParameterTitle,
                    parameterListTitleColor, PDEButtonParameterTitleColor,
                    parameterListColor, PDEButtonParameterColor,
                    parameterListIcon, PDEButtonParameterIcon,
                    parameterListBorderColor, PDEButtonParameterBorderColor,
                    parameterListCornerRadius, PDEButtonParameterCornerRadius,
                    parameterListFont, PDEButtonParameterFont,
                    parameterListBackground, PDEButtonParameterBackground,
                    parameterListHorizontalPadding, PDEButtonParameterHorizontalPadding);
        }

        // no attributes -> start out with default configuration: text and flat background
        if (attrs == null) {
            setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.BackgroundFlat);
        }

        //first set default foreground layer
        setButtonForegroundLayerWithLayerType(PDEButtonLayerType.ForegroundIconText);
        if (attrs != null) {
            TypedArray sa = getContext().obtainStyledAttributes(attrs, R.styleable.PDEButton);

            // first create layer if wanted by xml, use default flat
            if (sa.hasValue(R.styleable.PDEButton_backgroundType)) {
                setButtonBackgroundLayerWithLayerType(sa.getInt(R.styleable.PDEButton_backgroundType, 0));
            } else {
                setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.BackgroundFlat);
            }

            // set text
            titleText = sa.getString(R.styleable.PDEButton_text);
            if (TextUtils.isEmpty(titleText)) {
                // try to get "android:text" attribute instead

                // first check if it is a resource id ...
                int resourceId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android",
                                                                 "text", -1);
                if (resourceId > 0) {
                    titleText = getResources().getString(resourceId);
                } else {
                    // otherwise handle it as string
                    titleText = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
                }
            }
            setText(titleText);

            // set other attributes
            if (sa.hasValue(R.styleable.PDEButton_iconAlignment)) {
                setIconAlignment(PDEButtonIconAlignment.values()[sa.getInt(R.styleable.PDEButton_iconAlignment, 0)]);
            }
            if (sa.hasValue(R.styleable.PDEButton_borderColor)) {
                //to have dark/light style use PDEColor with color id
                int resourceID = sa.getResourceId(R.styleable.PDEButton_borderColor,0);
                if (resourceID!=0) {
                    setBorderColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setBorderColorWithInt(sa.getColor(R.styleable.PDEButton_borderColor, R.color.DTBlack));
                }
            }
            // check text color attribute, if there is none use title color attribute if there is one
            // both do the same, but setTextColor is more Android style
            if (sa.hasValue(R.styleable.PDEButton_textColor)) {
                //to have dark/light style use PDEColor with color id
                int resourceID = sa.getResourceId(R.styleable.PDEButton_textColor,0);
                if (resourceID !=0 ) {
                    setTextColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setTextColorWithInt(sa.getColor(R.styleable.PDEButton_textColor, R.color.DTBlack));
                }
            } else if (sa.hasValue(R.styleable.PDEButton_titleColor)) {
                //to have dark/light style use PDEColor with color id
                int resourceID = sa.getResourceId(R.styleable.PDEButton_titleColor,0);
                if (resourceID != 0) {
                    setTitleColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setTitleColorWithInt(sa.getColor(R.styleable.PDEButton_titleColor, R.color.DTBlack));
                }
            }
            if (sa.hasValue(R.styleable.PDEButton_buttonColor)) {
                //to have dark/light style use PDEColor with color id
                int resourceID = sa.getResourceId(R.styleable.PDEButton_buttonColor,0);
                if (resourceID!=0) {
                    setColor(PDEColor.valueOfColorID(resourceID));
                } else {
                    setColorWithInt(sa.getColor(R.styleable.PDEButton_buttonColor, R.color.DTBlue));
                }
            }

            //check icon source or string
            if (sa.hasValue(R.styleable.PDEButton_src)) {
                //check if this is a resource value
                int resourceID = sa.getResourceId(R.styleable.PDEButton_src,0);
                if(resourceID == 0){
                    setIcon(sa.getString(R.styleable.PDEButton_src));
                } else {
                    setIcon(getContext().getResources().getDrawable(resourceID));
                }
            } else {
                int res = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "src", -1);
                if (res != -1) {
                    setIcon(getContext().getResources().getDrawable(res));
                }
            }
            //is icon colored?
            if (sa.hasValue(R.styleable.PDEButton_iconColored)) {
                setIconColored(sa.getBoolean(R.styleable.PDEButton_iconColored, false));
            }
            if (sa.hasValue(R.styleable.PDEButton_textAlignment)) {
                setAlignment(PDEAlignment.values()[sa.getInt(R.styleable.PDEButton_textAlignment, 0)]);
            }
            if (sa.hasValue(R.styleable.PDEButton_textSize)) {
                String text_size = sa.getString(R.styleable.PDEButton_textSize);
                setFontSize(text_size);
            }
            if (sa.hasValue(R.styleable.PDEButton_typeface)) {
                setFont(PDETypeface.createByName(sa.getString(R.styleable.PDEButton_typeface)));
            }
            if (sa.hasValue(R.styleable.PDEButton_minButtonPadding)) {
                setMinButtonPadding(sa.getDimensionPixelOffset(R.styleable.PDEButton_minButtonPadding, 0),
                                    sa.getDimensionPixelOffset(R.styleable.PDEButton_minButtonPadding, 0),
                                    sa.getDimensionPixelOffset(R.styleable.PDEButton_minButtonPadding, 0),
                                    sa.getDimensionPixelOffset(R.styleable.PDEButton_minButtonPadding, 0));
            }
            if (sa.hasValue(R.styleable.PDEButton_minButtonPaddingLeft)) {
                setMinButtonPadding(sa.getDimensionPixelOffset(R.styleable.PDEButton_minButtonPaddingLeft, 0),
                                    mMinButtonPadding.top,
                                    mMinButtonPadding.right, mMinButtonPadding.bottom);
            }
            if (sa.hasValue(R.styleable.PDEButton_minButtonPaddingTop)) {
                setMinButtonPadding(mMinButtonPadding.left,
                                    sa.getDimensionPixelOffset(R.styleable.PDEButton_minButtonPaddingTop, 0),
                                    mMinButtonPadding.right, mMinButtonPadding.bottom);
            }
            if (sa.hasValue(R.styleable.PDEButton_minButtonPaddingRight)) {
                setMinButtonPadding(mMinButtonPadding.left, mMinButtonPadding.top,
                                    sa.getDimensionPixelOffset(R.styleable.PDEButton_minButtonPaddingRight, 0),
                                    mMinButtonPadding.bottom);
            }
            if (sa.hasValue(R.styleable.PDEButton_minButtonPaddingBottom)) {
                setMinButtonPadding(mMinButtonPadding.left, mMinButtonPadding.top,
                                    mMinButtonPadding.right,
                                    sa.getDimensionPixelOffset(R.styleable.PDEButton_minButtonPaddingBottom, 0));
            }
            if (sa.hasValue(R.styleable.PDEButton_overlay)) {
                setButtonOverlayLayerWithLayerType(sa.getInt(R.styleable.PDEButton_overlay, 0));
            }
            if (sa.hasValue(R.styleable.PDEButton_selected)) {
                setSelected(sa.getBoolean(R.styleable.PDEButton_selected, false));
            }
            if (sa.hasValue(R.styleable.PDEButton_checkboxAlignment)) {
                setCheckboxAlignment(PDEAlignment.values()[sa.getInt(R.styleable.PDEButton_checkboxAlignment, 0)]);
            }
            if (sa.hasValue(R.styleable.PDEButton_radioAlignment)) {
                setRadioAlignment(PDEAlignment.values()[sa.getInt(R.styleable.PDEButton_radioAlignment, 0)]);
            }
            if (sa.hasValue(R.styleable.PDEButton_roundedCornerConfiguration)) {
                setRoundedCornerConfiguration(sa.getInt(R.styleable.PDEButton_roundedCornerConfiguration,
                        PDEDrawableCornerBox.PDEDrawableCornerBoxAllCorners));
            }
            if (sa.hasValue(R.styleable.PDEButton_iconToTextHeightRatio)) {
                setIconToTextHeightRatio(sa.getFloat(R.styleable.PDEButton_iconToTextHeightRatio, 2.0f));
            }
            if (sa.hasValue(R.styleable.PDEButton_cornerRadius)) {
                setCornerRadius(sa.getDimension(R.styleable.PDEButton_cornerRadius,
                        (float) PDEBuildingUnits.oneThirdBU()));
            }
            if (sa.hasValue(R.styleable.PDEButton_horizontalPadding)) {
                setHorizontalPadding((int) sa.getDimension(R.styleable.PDEButton_horizontalPadding,
                        PDEBuildingUnits.pixelFromBU(2.0f)));
            }


            sa.recycle();

            //TODO add Font functions for xml ###2DO
        }
    }

//----- agent linkage --------------------------------------------------------------------------------------------------


    /**
     * @brief Create and link agent controller.
     */
    private void initAgent() {
        // create agent controller
        mAgentController = new PDEAgentController();

        // link it via appropriate adapter
        mAgentAdapter = new PDEAgentControllerAdapterView();
        mAgentAdapter.linkAgent(mAgentController, this);

        // catch agent controller events for animation
        mAgentAdapter.getEventSource().addListener(this, "cbAgentController",
                                                   PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK_ANIMATION);

        // pass on agent adapter events to ourself, override the sender
        mEventSource.forwardEvents(mAgentAdapter,
                PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK_ACTION);
        mEventSource.setEventDefaultSender(this, true);
    }

    // needs to be public otherwise it cannot be called from eventsource (11.10.2012)

    /**
     * @brief Called on changes from agentController.
     */
    public void cbAgentController(PDEEvent event) {
        boolean needsUpdate;

        if (event.isType(PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK_ANIMATION)) {
            // do your own (for visual disabled)
            needsUpdate = mAgentHelper.processAgentEvent(event);
            if(needsUpdate) {
                // update animateable parameters on change
                updateAlpha();
            }
            // tell all layers
            for (PDEButtonLayerHolder mButtonLayer : mButtonLayers) {
                sendAgentEvent(event, mButtonLayer.mLayer);
            }
        }
    }

    // needs to be public otherwise it cannot be called from eventsource (11.10.2012)

    /**
     * @brief Called on specially requested initializations from agent controller.
     *
     * Only sends the event on to the specified layer.
     */
    public void cbAgentControllerSingle(PDEEvent event) {
        // no nothing if no initialization layer is defined
        if (mButtonLayerToInitialize == null) {
            return;
        }

        // send it to this layer if it's an event we want to listen on
        if (event.isType(PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK_ANIMATION)) {
            // tell all layers
            sendAgentEvent(event, mButtonLayerToInitialize);
        }
    }


    /**
     * @brief Send an agent event to a single layer.
     */
    private void sendAgentEvent(PDEEvent event, PDEButtonLayerInterface layer) {
        // does it exist and have the selector?
        if (layer == null) {
            return;
        }

        // send it
        layer.agentEvent(event);
    }


    //----- layer handling ---------------------------------------------------------------------------------------------


    /**
     * @brief Set the layer.
     *
     */
    public void setButtonLayer(PDEButtonLayerInterface layer, PDEButtonLayerId layerId) {
        // call internal layer setting with user type
        setButtonLayer(layer, layerId, PDEButtonLayerType.User);
    }


    /**
     * @brief Set the layer (internal function, for default types).
     *
     * Remove a layer of the same type if one exists. If the layer set is nil, the layer is permanently removed.
     */
    private void setButtonLayer(PDEButtonLayerInterface layer, PDEButtonLayerId layerId,
                                PDEButtonLayerType layerType) {
        PDEButtonLayerHolder holder;
        boolean inserted;
        int i;

        // remove the old one
        clearButtonLayerForLayerId(layerId);

        // stop if we don't have a valid layer
        if (layer == null) {
            return;
        }

        // create and fill holder for layer
        holder = new PDEButtonLayerHolder();
        holder.mLayer = layer;
        holder.mLayerId = layerId;
        holder.mLayerType = layerType;

        // add to list sorted; priorities cannot overlap since old layers are removed before
        inserted = false;
        for (i = 0; i < mButtonLayers.size(); i++) {
            // can we insert before the current layer?
            if (holder.mLayerId.ordinal() < mButtonLayers.get(i).mLayerId.ordinal()) {
                // insert in list
                mButtonLayers.add(i, holder);

                inserted = true;
                // done the loop
                break;
            }
        }
        if (!inserted) {
            mButtonLayers.add(holder);
        }

        if (holder.mLayerId == PDEButtonLayerId.Background) {
            ((ViewGroup)findViewById(R.id.pdebutton_background_slot)).addView(layer.getLayer(),
                    new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        } else if (holder.mLayerId == PDEButtonLayerId.Foreground) {
            ((ViewGroup)findViewById(R.id.pdebutton_foreground_slot)).addView(layer.getLayer(),
                    new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        } else if (holder.mLayerId == PDEButtonLayerId.Overlay) {
            ((ViewGroup)findViewById(R.id.pdebutton_overlay_slot_left)).addView(layer.getLayer(),
                    new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        } else {
            Log.e(LOG_TAG, "should not happen");
        }

        // hints might have changed -> collect and send them again
        collectHintsFromLayers();
        sendHintsToLayers();

        // initialize: send parameters
        sendParameters(mParameters, layer);

        resolveButtonPadding();

        // send complete agent initialization
        mButtonLayerToInitialize = layer;
        mAgentAdapter.getEventSource().requestOneTimeInitialization(this, "cbAgentControllerSingle",
                                                          PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK_ANIMATION);
        mButtonLayerToInitialize = null;
    }


    /**
     * @brief Remove the layer.
     */
    public void clearButtonLayerForLayerId(PDEButtonLayerId layerId) {
        PDEButtonLayerHolder layer;
        int i;

        // seek it
        for (i = 0; i < mButtonLayers.size(); i++) {
            // get layer
            layer = mButtonLayers.get(i);
            // layer match?
            if (layer.mLayerId == layerId) {
                // remove from visuals
                if (layer.mLayer.getLayer() != null) {
                    ((ViewGroup)layer.mLayer.getLayer().getParent()).removeView(layer.mLayer.getLayer());
                }
                // and forget
                mButtonLayers.remove(i);

                // hints might have changed -> collect and send them again
                collectHintsFromLayers();
                sendHintsToLayers();

                // done
                return;
            }
        }
    }


    /**
     * @brief Retrieve the type for a layer. If the layer is not present, a type of DTButtonLayerTypeNone is returned.
     */
    private PDEButtonLayerType getButtonLayerTypeForLayerId(PDEButtonLayerId layerId) {
        // seek the layer
        for (PDEButtonLayerHolder holder : mButtonLayers) {
            // check layer id
            if (holder.mLayerId == layerId) {
                // retrieve type
                return holder.mLayerType;
            }
        }
        // not found
        return PDEButtonLayerType.None;
    }


    /**
     * @brief Set the background layer.
     */
    public void setButtonBackgroundLayer(PDEButtonLayerInterface layer) {
        // call generic function
        setButtonLayer(layer, PDEButtonLayerId.Background);
    }


    /**
     * @brief Set the foreground layer.
     *
     * Foreground layer usually holds stuff like text and icons. In most cases, the foreground layer
     * is static.
     */
    public void setButtonForegroundLayer(PDEButtonLayerInterface layer) {
        // call generic function
        setButtonLayer(layer, PDEButtonLayerId.Foreground);
    }


    /**
     * @brief Set the overlay layer.
     *
     * Overlay layer holds additional information like e.g. checkbox state etc.
     */
    public void setButtonOverlayLayer(PDEButtonLayerInterface layer) {
        // call generic function
        setButtonLayer(layer, PDEButtonLayerId.Overlay);
    }


    /**
     * @brief Select one of the backgrounds by its int id.
     */
    private void setButtonBackgroundLayerWithLayerType(int layerType) {
        setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.values()[layerType]);
    }


    /**
     * @brief Select one of the default backgrounds
     */
    public void setButtonBackgroundLayerWithLayerType(PDEButtonLayerType layerType) {
        PDEButtonLayerInterface layer = null;
        PDEButtonLayerType type;

        // user type cannot be set through this function -> use NULL layer for this
        if (layerType == PDEButtonLayerType.BackgroundUser || layerType == PDEButtonLayerType.User) {
            layerType = PDEButtonLayerType.BackgroundNone;
        }

        // get type of existing layer
        type = getButtonLayerTypeForLayerId(PDEButtonLayerId.Background);

        // any change?
        if (type == layerType) {
            return;
        }

        // create and set the new layer, remember type
        switch (layerType) {
            case BackgroundNone:
                break;
            case BackgroundUser:
                break;
            case BackgroundFlat:
                layer = new PDEButtonLayerBackgroundFlat(getContext());
                break;
            case BackgroundHaptic:
            // Beveled was renamed in Haptic, this is just for downward compatibility reasons
            case BackgroundBeveled:
                layer = new PDEButtonLayerBackgroundHaptic(getContext());
                break;
            case BackgroundTextHaptic:
                layer = new PDEButtonLayerBackgroundTextHaptic(getContext());
                break;
            case BackgroundTextFlat:
            case BackgroundText:
                layer = new PDEButtonLayerBackgroundTextFlat(getContext());
                break;
            default:
                //error
                layer = null;
                break;
        }

        // now set it (this removes if it already exists)
        setButtonLayer(layer, PDEButtonLayerId.Background, layerType);
    }


    /**
     * @brief Set one of default backgrounds by string
     */
    public void setButtonBackgroundLayerWithLayerTypeString(String layerType) {
        try {
            // Check whether string represents int value and set backgroundLayerType directly with int

            int number = Integer.parseInt(layerType);
            // set by number
            setButtonBackgroundLayerWithLayerType(number);
            return;
        } catch (NumberFormatException e) {
            // string isn't a number -> evaluate the string;
        }

        if (layerType.equalsIgnoreCase("PDEButtonBackgroundFlat")
                || layerType.equalsIgnoreCase("Flat")) {
            setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.BackgroundFlat);
        } else if (layerType.equalsIgnoreCase("PDEButtonBackgroundHaptic")
                || layerType.equalsIgnoreCase("Haptic")) {
            setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.BackgroundHaptic);
        } else if (layerType.equalsIgnoreCase("PDEButtonBackgroundText")
                || layerType.equalsIgnoreCase("Text")
                || layerType.equalsIgnoreCase("PDEButtonBackgroundTextFlat")
                || layerType.equalsIgnoreCase("TextFlat")) {
            setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.BackgroundTextFlat);
        } else if (layerType.equalsIgnoreCase("PDEButtonBackgroundTextHaptic")
                || layerType.equalsIgnoreCase("TextHaptic")) {
            setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.BackgroundTextHaptic);
        }
    }


    /**
     * @brief Select one of the default foregrounds
     */
    private void setButtonForegroundLayerWithLayerType(int layerType) {
        setButtonForegroundLayerWithLayerType(PDEButtonLayerType.values()[layerType]);
    }


    /**
     * @brief Select one of the default foregrounds
     */
    public void setButtonForegroundLayerWithLayerType(PDEButtonLayerType layerType) {
        PDEButtonLayerInterface layer;
        PDEButtonLayerType type;

        // user type cannot be set through this function -> use NULL layer for this
        if (layerType == PDEButtonLayerType.User || layerType == PDEButtonLayerType.ForegroundUser) {
            layerType = PDEButtonLayerType.ForegroundNone;
        }

        // get type of existing layer
        type = getButtonLayerTypeForLayerId(PDEButtonLayerId.Foreground);

        // any change?
        if (type == layerType) {
            return;
        }

        // create and set the new layer, remember type
        switch (layerType) {
            case ForegroundIconText:
                layer = new PDEButtonLayerForegroundIconText(getContext());
                break;
            default:
                layer = null;
                break;
        }

        // now set it (this removes if it already exists)
        setButtonLayer(layer, PDEButtonLayerId.Foreground, layerType);
    }


    public void setButtonOverlayLayerWithLayerType(int layerType) {
        setButtonOverlayLayerWithLayerType(PDEButtonLayerType.values()[layerType]);
    }


    /**
     * @brief Select one of the default foregrounds
     */
    public void setButtonOverlayLayerWithLayerType(PDEButtonLayerType layerType) {
        PDEButtonLayerInterface layer;
        PDEButtonLayerType type;

        // user type cannot be set through this function -> use NULL layer for this
        if (layerType == PDEButtonLayerType.User) layerType = PDEButtonLayerType.ForegroundNone;

        // get type of existing layer
        type = getButtonLayerTypeForLayerId(PDEButtonLayerId.Overlay);

        // any change?
        if (type == layerType) return;

        // create and set the new layer, remember type
        switch (layerType) {
            case OverlayCheckboxHaptic:
            layer = new PDEButtonLayerOverlayCheckboxHaptic(PDECodeLibrary.getInstance().getApplicationContext());
                break;
            case OverlayCheckboxFlat:
            // In version 2.x we had only one style for checkbox-buttons; The default style is now Flat;  this is just
            // for downward compatibility reasons
            case OverlayCheckbox:
                layer = new PDEButtonLayerOverlayCheckboxFlat(PDECodeLibrary.getInstance().getApplicationContext());
                break;
            case OverlayRadioHaptic:
                layer = new PDEButtonLayerOverlayRadioHaptic(PDECodeLibrary.getInstance().getApplicationContext());
                break;
            case OverlayRadioFlat:
            // In version 2.x we had only one style for radio-buttons; The default style is now Flat;  this is just
            // for downward compatibility reasons
            case OverlayRadio:
                layer = new PDEButtonLayerOverlayRadioFlat(PDECodeLibrary.getInstance().getApplicationContext());
                break;
            default:
                layer = null;
                break;
        }

        // now set it (this removes if it already exists)
        setButtonLayer(layer, PDEButtonLayerId.Overlay, layerType);
    }


//----- parameter handling ---------------------------------------------------------------------------------------------




    /**
     * @brief Set main color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setColor(PDEColor color) {
        // set the parameters
        mParameters.setParameter(PDEButtonParameterColor, color);

        // tell all sub layers the new state
        sendParametersToLayers();
    }

    /**
     * @brief Set main color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setColorWithString(String color) {
        // set the parameters
        mParameters.setParameter(PDEButtonParameterColor, color);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Set main color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setColorWithInt(int color) {
        String colorStr;

        // create a string from the color to integrate into parameters
        colorStr = PDEColor.stringFromIntColor(color);

        // set the parameters
        mParameters.setParameter(PDEButtonParameterColor, colorStr);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Get main color.
     *
     * Only retrieves basic parameters, and only if a parameter was set. Default
     * parameters defined in the layers are never received.
     */
    public PDEColor getColor() {
        // retrieve parameter main value
        return mParameters.parameterColorForName(PDEButtonParameterColor);
    }


    /**
     * @brief Set border color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setBorderColor(PDEColor color) {

        // set the parameters
        mParameters.setParameter(PDEButtonParameterBorderColor, color);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Set border color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setBorderColorWithInt(int color) {
        String colorStr;

        // create a string from the color to integrate into parameters
        colorStr = PDEColor.stringFromIntColor(color);

        // set the parameters
        mParameters.setParameter(PDEButtonParameterBorderColor, colorStr);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Set border color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setBorderColorWithString(String color) {
        // set the parameters
        mParameters.setParameter(PDEButtonParameterBorderColor, color);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Get border color.
     *
     * Only retrieves basic parameters, and only if a parameter was set. Default
     * parameters defined in the layers are never received.
     */
    public PDEColor getBorderColor() {
        // retrieve parameter main value
        return mParameters.parameterColorForName(PDEButtonParameterBorderColor);
    }


    /**
     * @brief Set the distance between the border and the icon / text.
     *
     * The default is 2 BU. Do not use a distance less than 1 BU (the styleguide says that is too little)
     */
    public void setHorizontalPadding(int distance) {
        //set the parameter
        mParameters.setParameter(PDEButtonParameterHorizontalPadding, distance);

        //tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Get the set parameter value for the horizontal padding.
     */
    public int getIconTextHorizontalPadding() {
        // retrieve parameter
        return mParameters.parameterIntForName(PDEButtonParameterHorizontalPadding);
    }



    /**
     * @brief Set title. (setText does the same -> use setText instead of setTitle because it is more Android like)
     */
    public void setTitle(String title) {
        // set the parameters
        mParameters.setParameter(PDEButtonParameterTitle, title);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Get title. (getText does the same -> use getText instead of getTitle because it is more Android like)
     *
     * Only retrieves basic parameters, and only if a parameter was set -> default parameters are never retrieved.
     */
    public String getTitle() {
        // retrieve parameter main value
        return mParameters.parameterValueForName(PDEButtonParameterTitle);
    }


    /**
     * @brief Set text of the button. Does the same like setTitle but naming is more Android like
     *        Just calls setTitle
     */
    public void setText(String text) {
        setTitle(text);
    }


    /**
     * @brief Get text of the button. Does the same like getTitle but naming is more Android like
     *        Just calls getTitle
     */
    public String getText() {
        return getTitle();
    }


    /**
     * @brief Set title color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setTitleColor(PDEColor color) {
        // set the parameters
        mParameters.setParameter(PDEButtonParameterTitleColor, color);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Set title color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setTitleColorWithString(String color) {
        // set the parameters
        mParameters.setParameter(PDEButtonParameterTitleColor, color);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Set title color.
     *
     * No change handling here, this has to be done in the child implementations.
     */
    public void setTitleColorWithInt(int color) {
        String colorStr;

        // create a string from the color to integrate into parameters
        colorStr = PDEColor.stringFromIntColor(color);

        // set the parameters
        mParameters.setParameter(PDEButtonParameterTitleColor, colorStr);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Get title color.
     *
     * Only retrieves basic parameters, and only if a parameter was set. Default
     * parameters defined in the layers are never received.
     */
    public PDEColor getTitleColor() {
        // retrieve parameter main value
        return mParameters.parameterColorForName(PDEButtonParameterTitleColor);
    }


    /**
     * @brief Set text color. This is only alias function for setTitleColor
     *
     */
    public void setTextColor(PDEColor color) {
        setTitleColor(color);
    }


    /**
     * @brief Set text color. This is only alias function for setTitleColorWithString
     *
     */
    public void setTextColorWithString(String color) {
        setTitleColorWithString(color);
    }


    /**
     * @brief Set text color. This is only alias function for setTitleColor
     *
     */
    public void setTextColorWithInt(int color) {
        setTitleColorWithInt(color);
    }


    /**
     * @brief Get text color. This is only alias function for getTitleColor
     *
     */
    public PDEColor getTextColor() {
        return getTitleColor();
    }


    /**
     * @brief Set Icon from file name, colored is false.
     * The icon will not be shown in text color, if this was set before it will be overwritten.
     *
     * @param fileName complete filename (including suffix) or nil to clear it.
     */
    public void setIconFromFileName(String fileName) {
        setIconFromFileName(fileName, false);
    }


    /**
     * @brief Set Icon.
     *
     * @param fileName complete filename (including suffix) or nil to clear it.
     * @param colored true for a multicolor image (no coloring)
     */
    public void setIconFromFileName(String fileName, boolean colored) {
        // set the parameters
        mParameters.setParameter(PDEButtonParameterIcon, fileName);

        // set Icon colored state
        mParameters.setParameter(PDEButtonParameterIconColored, colored);

        // tell all sublayers the new parameters
        sendParametersToLayers();
    }


    /**
     * @brief Set Icon, colored is false.
     * The icon will not be shown in text color, if this was set before it will be overwritten.
     *
     * @param icon character value of IconFont. Only one character is supported at the moment.
     */
    public void setIcon(String icon) {
       setIcon(icon, false);
    }


    /**
     * @brief Set Icon.
     *
     * @param icon character value of IconFont. Only one character is supported at the moment.
     * @param colored true if the icon shall be shown in text color
     */
    public void setIcon(String icon, boolean colored) {
        // set the parameters
        mParameters.setParameter(PDEButtonParameterIcon, icon);

        // set Icon colored state
        mParameters.setParameter(PDEButtonParameterIconColored, colored);

        // tell all sublayers the new parameters
        sendParametersToLayers();
    }


    /**
     * @brief Set Icon with drawable, colored is false.
     * The icon will not be shown in text color, if this was set before it will be overwritten.
     *
     * @param icon drawable reference or null to clear it.
     */
    public void setIcon(Drawable icon) {
       setIcon(icon, false);
    }


    /**
     * @brief Set Icon.
     *
     * @param icon drawable or nil to clear it.
     * @param colored true for a multicolor image (no coloring)
     *
     */
    public void setIcon(Drawable icon, boolean colored) {

        // set the parameters (objects not supported yet)
        mParameters.setParameter(PDEButtonParameterIcon, icon);

        // set Icon colored state
        mParameters.setParameter(PDEButtonParameterIconColored, colored);

        // tell all sublayers the new parameters
        sendParametersToLayers();
    }


    /**
     * @brief Set Icon colorization mode.
     */
    public void setIconColored(boolean colored) {
        // set Icon colored state
        mParameters.setParameter(PDEButtonParameterIconColored, true);

        // tell all sublayers the new parameters
        sendParametersToLayers();
    }


    /**
     * @brief Get Icon.
     *
     * Only retrieves basic parameters, and only if a parameter with the correct type (UIImage) was set. Default
     * parameters defined in the layers are never received.
     */
    public Drawable getIcon() {
        Object object;

        // get the object
        object = mParameters.parameterObjectForName(PDEButtonParameterIcon);

        // typecheck
        if (object == null || !(object instanceof Drawable)) {
            return null;
        }

        // done
        return (Drawable) object;
    }


    /**
     * Only retrieves basic parameters, and only if a parameter with the correct type(String) was set.
     * For example setIcon(String) or setIconFromFileName(String) called.
     * Default parameters defined in the layers are never received
     */
    public String getIconString() {
        Object object;

        // get the object
        object = mParameters.parameterObjectForName(PDEButtonParameterIcon);

        // typecheck
        if (object == null || !(object instanceof String)) {
            return null;
        }

        // done
        return (String) object;
    }

    /**
     * @brief Get Icon colored style.
     */
    public boolean isIconColored() {
        // get the value
        return mParameters.parameterBoolForName(PDEButtonParameterIconColored);
    }

    /**
     * @brief Set font.
     *
     * If nothing else is set the size of the font will be calculated fitting to the size of the button.
     * For fixed size (from the font you set with this function) call setFontSizeFromFont
     *
     * @param font for the button
     */
    public void setFont(PDETypeface font) {
        // set the parameters
        setParameter(PDEButtonParameterFont, font);

        // tell all sublayers the new parameters
        sendParametersToLayers();
    }


    /**
     * @brief Get the font.
     *
     * Only retrieves basic parameters, and only if a font was explicitly set before.
     */
    public PDETypeface getFont() {
        Object object;

        // get the object
        object = mParameters.parameterObjectForName(PDEButtonParameterFont);

        // typecheck
        if (object == null || !(object instanceof PDETypeface)) return null;

        // done
        return (PDETypeface) object;
    }


    /**
     * @brief Set the font size directly, exactly like it would be done for the UIFont.
     *
     * @param fontSize of the font (in point)
     */
    public void setFontSize(float fontSize) {
        // set directly as number
        setParameter(PDEButtonParameterFontSize, fontSize);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Set the font size bei size String.
     *
     * The string must follow the format float[unit]. Unit is optional but if present valid values are "BU", "%" and
     * "Caps".
     * It is also possible to set the strings "auto" or "automatic", and to "styleguide"
     */
    public void  setFontSize(String sizeString) {
        // set the string
        setParameter(PDEButtonParameterFontSize, sizeString);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Get font size (float).
     *
     * Only retrieves basic parameters, and only if a float was directly set.
     * If you used setFontSizeWithString use fontSizeString to get the font size.
     * @return the set font size in float or 0.0f if the parameter was not set or was set as a string.
     */
    public float getFontSize() {
        if(mParameters.parameterObjectForName(PDEButtonParameterFontSize) instanceof Float) {
            return mParameters.parameterFloatForName(PDEButtonParameterFontSize);
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
        return mParameters.parameterValueForName(PDEButtonParameterFontSize);
    }

    /*
    * @brief Set corner radius
    */
    public void setCornerRadius(float cornerRadius) {
        // set the parameters
        mParameters.setParameter(PDEButtonParameterCornerRadius, String.format(Locale.ENGLISH, "%.02f", cornerRadius));

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /*
     * @brief Get corner radius
     */
    public float getCornerRadius() {
        // retrieve base parameter as float
        return mParameters.parameterFloatForName(PDEButtonParameterCornerRadius);
    }


    /**
     * @brief Set the alignment of the ForegroundIconTextLayer.
     *
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

        setParameter(PDEButtonParameterAlignment,parameterString);

        // tell all sub layers the new state
        sendParametersToLayers();
    }

    public void setCheckboxAlignment(PDEConstants.PDEAlignment alignment) {
        String parameterString = null;

        // set as string
        if (alignment == PDEConstants.PDEAlignment.PDEAlignmentLeft) {
            parameterString = PDEConstants.PDEAlignmentStringLeft;
        } else if (alignment == PDEConstants.PDEAlignment.PDEAlignmentCenter) {
            parameterString = PDEConstants.PDEAlignmentStringCenter;
        } else if (alignment == PDEConstants.PDEAlignment.PDEAlignmentRight) {
            parameterString = PDEConstants.PDEAlignmentStringRight;
        }

        setParameter(PDEButtonParameterCheckboxAlignment,parameterString);

        // tell all sub layers the new state
        sendParametersToLayers();
    }

    public void setRadioAlignment(PDEConstants.PDEAlignment alignment) {
        String parameterString = null;

        // set as string
        if (alignment == PDEConstants.PDEAlignment.PDEAlignmentLeft) {
            parameterString = PDEConstants.PDEAlignmentStringLeft;
        } else if (alignment == PDEConstants.PDEAlignment.PDEAlignmentCenter) {
            parameterString = PDEConstants.PDEAlignmentStringCenter;
        } else if (alignment == PDEConstants.PDEAlignment.PDEAlignmentRight) {
            parameterString = PDEConstants.PDEAlignmentStringRight;
        }

        setParameter(PDEButtonParameterRadioAlignment,parameterString);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Get alignment of ForegroundIconTextLayer
     */
    public PDEConstants.PDEAlignment getAlignment() {
        String textAlignmentString = mParameters.parameterValueForNameWithDefault(PDEButtonParameterAlignment,
                                    PDEConstants.PDEAlignmentStringCenter);
        PDEConstants.PDEAlignment textAlignment;

        // parse value
        if ( textAlignmentString.equals(PDEConstants.PDEAlignmentStringLeft)) {
            textAlignment = PDEConstants.PDEAlignment.PDEAlignmentLeft;
        } else if ( textAlignmentString.equals(PDEConstants.PDEAlignmentStringCenter)) {
            textAlignment = PDEConstants.PDEAlignment.PDEAlignmentCenter;
        } else if ( textAlignmentString.equals(PDEConstants.PDEAlignmentStringRight)) {
            textAlignment = PDEConstants.PDEAlignment.PDEAlignmentRight;
        } else {
            textAlignment = PDEConstants.PDEAlignment.PDEAlignmentCenter;
        }

        return textAlignment;
    }


    /**
     * @brief Set the alignment of the Icon within the ForegroundIconTextLayer of the button.
     *
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
        setParameter(PDEButtonParameterIconAlignment,parameterString);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Get Icon alignment of ForegroundIconTextLayer
     */
    public PDEButtonIconAlignment getIconAlignment() {
        String iconAlignmentString =
                mParameters.parameterValueForNameWithDefault(PDEButton.PDEButtonParameterIconAlignment,
                        PDEConstants.PDEAlignmentStringCenter);
        PDEButtonIconAlignment iconAlignment;

        // parse value
        if ( iconAlignmentString.equals(PDEConstants.PDEAlignmentStringLeft)) {
            iconAlignment = PDEButtonIconAlignment.PDEButtonIconAlignmentLeft;
        } else if ( iconAlignmentString.equals(PDEConstants.PDEAlignmentStringRight)) {
            iconAlignment = PDEButtonIconAlignment.PDEButtonIconAlignmentRight;
        } else if ( iconAlignmentString.equals(PDEConstants.PDEAlignmentStringLeftAttached)) {
            iconAlignment = PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached;
        }  else if ( iconAlignmentString.equals(PDEConstants.PDEAlignmentStringRightAttached)) {
            iconAlignment = PDEButtonIconAlignment.PDEButtonIconAlignmentRightAttached;
        } else {
            iconAlignment = PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached;
        }

        return iconAlignment;
    }

    /**
     * @brief Get AgentController used by the button
     */
    public PDEAgentController getAgentController() {
        return mAgentController;
    }


    /**
     * @brief Set configuration which corners should be rounded.
     * This function is deprecated since it is not implemented for all configurations yet.
     */
    @Deprecated
    public void setRoundedCornerConfiguration(int config) {
        // set the parameters
        mParameters.setParameter(PDEButtonParameterRoundedCornerConfiguration, config);

        // tell all sublayers the new parameters
        sendParametersToLayers();
    }


    /**
     * @brief Set ratio of icon height to text height.
     */
    public void setIconToTextHeightRatio(float ratio) {
        // set the parameters
        mParameters.setParameter(PDEButtonParameterIconToTextHeightRatio, ratio);

        // tell all sublayers the new parameters
        sendParametersToLayers();
    }


    /**
     * @brief Get ratio of icon height to text height.
     */
    public float getIconToTextHeightRatio() {
        if(mParameters.parameterObjectForName(PDEButtonParameterIconToTextHeightRatio) instanceof Float) {
            return mParameters.parameterFloatForName(PDEButtonParameterIconToTextHeightRatio);
        }

        return 0.0f;
    }

//----- free parameter setting -----------------------------------------------------------------------------------------


    /**
     * @brief Setter for all parameters.
     *
     * Remember them and pass them to the sublayers.
     */
    public void setParameters(PDEParameterDictionary parameters) {
        // copy them
        mParameters = parameters.copy();

        // distribute to all layers
        sendParametersToLayers();
    }


    /**
     * @brief Set parameter, distribute changes.
     */
    public void setParameter(String name, String value) {
        // set it
        mParameters.setParameter(name, value);

        // distribute to all layers
        sendParametersToLayers();
    }


    /**
     * @brief Set parameter, distribute changes.
     */
    public void setParameter(String name, Object object) {
        // set it
        mParameters.setParameter(name, object);

        // distribute to all layers
        sendParametersToLayers();
    }


    /**
     * @brief Set parameter, distribute changes.
     */
    public void setParameter(String name, PDEParameter parameter) {
        // set it
        mParameters.setParameter(name, parameter);

        // distribute to all layers
        sendParametersToLayers();
    }


    /**
     * @brief Set parameter, distribute changes.
     */
    public void setParameter(String name, PDEDictionary dictionary) {
        // set it
        mParameters.setParameter(name, dictionary);

        // distribute to all layers
        sendParametersToLayers();
    }


    /**
     * @brief Set parameter, distribute changes.
     */
    public void mergeParameter(String name, String value, String subKey) {
        // set it

        mParameters.mergeParameter(name, value, subKey);
        // distribute to all layers
        sendParametersToLayers();
    }


    /**
     * @brief Set parameter, distribute changes.
     */
    public void mergeParameter(String name, Object object, String subKey) {
        // set it
        mParameters.mergeParameter(name, object, subKey);

        // distribute to all layers
        sendParametersToLayers();
    }


    /**
     * @brief Set parameter, distribute changes.
     */
    public void mergeParameter(String name, PDEParameter parameter) {
        // set it
        mParameters.mergeParameter(name, parameter);

        // distribute to all layers
        sendParametersToLayers();
    }


    /**
     * @brief Set parameter, distribute changes.
     */
    public void mergeParameter(String name, PDEDictionary dictionary) {
        // set it
        mParameters.mergeParameter(name, dictionary);

        // distribute to all layers
        sendParametersToLayers();
    }


//----- parameter update -----------------------------------------------------------------------------------------------

    /**
     * @brief Helper function. Distribute the parameters to all sub components.
     *
     * The subcomponents are responsible to extract the things they need and for change management.
     */
    private void sendParametersToLayers() {
        // do for all
        for (PDEButtonLayerHolder mButtonLayer : mButtonLayers) {
            // send it
            sendParameters(mParameters, mButtonLayer.mLayer);
        }
    }


    /**
     * @brief Send parameters to specified layer.
     */
    private void sendParameters(PDEParameterDictionary parameters, PDEButtonLayerInterface layer) {
        // valid? and responds to selector?
        if (layer == null) {
            return;
        }

        // send them
        layer.setParameters(parameters, false);
    }


//----- button state handling ------------------------------------------------------------------------------------------


    /**
     * @brief Get the button's current state.
     *
     * The current state is always the last set state. If the agent is in an animation between states, this
     * is not reflected here.
     */
    public String getMainState() {
        // map to agent controller
        return mAgentController.getState();
    }


    /**
     * @brief Get the button's current state.
     *
     * The current state is always the last set state. If the agent is in an animation between states, this
     * is not reflected here.
     */
    public void setMainState(String state) {
        // map to agent controller
        mAgentController.setState(state);
    }


    /**
     * @brief Retrieve selected state from main state.
     *
     * All non-selected states are returned as NO.
     */
    @Override
    public boolean isSelected() {
        // check for selected state
        return getMainState().equalsIgnoreCase(PDEButtonStateSelected);
    }


    /**
     * @brief Set selected state.
     *
     * Selected state is mapped to the main state "selected". If selected state is turned off, we return to
     * the "default" main state.
     *
     * This function is mainly there for convenience, if you mix mainState and selected, you might get unexpected
     * state changes.
     */
    @Override
    public void setSelected(boolean selected) {
        boolean sel;

        // get current selected state from main state
        sel = isSelected();

        // don't change unnecessarily to avoid side effects
        if (sel == selected) {
            return;
        }

        // change the main state
        if (selected) {
            setMainState(PDEButtonStateSelected);
        } else {
            setMainState(PDEButtonStateDefault);
        }
    }


//----- enabling / disabling -------------------------------------------------------------------------------------------


    /**
     * @brief Enabled state (overridden from Android).
     *
     * Sets interaction and visual state to enabled/disabled. You can also set both
     * states individually by calling the respective functions.
     */
    @Override
    public void setEnabled(boolean enabled){
        // set both
        setVisualEnabled(enabled);
        setUserInteractionEnabled(enabled);
    }


    /**
     * @brief Check enabled state (overridden from Android).
     *
     * We're truly enabled only if visual and user interaction are enabled. If you set these individually, you max
     * get inconsistent results.
     */
    @Override
    public boolean isEnabled(){
        return (isVisualEnabled() && isUserInteractionEnabled());
    }


    /**
     * @brief Visual enabled state.
     *
     * Only affects visuals - interaction state is not changed.
     */
    private void setVisualEnabled(boolean enabled){
        // pass on to agent controller
        mAgentController.setVisualEnabled(enabled);
    }


    /**
     * @brief Read visual enabled state.
     */
    private boolean isVisualEnabled() {
        // get from agent controller
        return mAgentController.isVisualEnabled();
    }


    /**
     * @brief User interaction enabled state.
     *
     * Only affects interaction - visual state is not changed.
     */
    private void setUserInteractionEnabled(boolean enabled){
        // pass on to agent controller
        mAgentController.setInputEnabled(enabled);
    }


    /**
     * @brief Read user interaction enabled state.
     */
    private boolean isUserInteractionEnabled(){
        return mAgentController.isInputEnabled();
    }


    /**
     * @brief Simple alpha update for disabled
     */
    private void updateAlpha() {
        float alpha;

        // get alpha from agent helper
        alpha = (float) mAgentHelper.getVisibilityState();

        // hardcoded values
        alpha = 0.5f + 0.5f * alpha;
        if (alpha < 0.0f) alpha = 0.0f;
        if (alpha > 1.0f) alpha = 1.0f;

        // set the gradient and border colors
        PDEUtils.setViewAlpha(this,alpha);
    }

//----- hinting --------------------------------------------------------------------------------------------------------

    /**
     * @brief Add a user-defined hint to the button.
     *
     * The hints are merged with the internal hints given by the layers on initialization, and then passed on to
     * all layers. There's no change management and optimization at the moment.
     */
    public void addHint(Object hint, String key) {
        // add or remove to the dictionary
        if (hint != null) {
            mHints.put(key, hint);
        } else {
            mHints.remove(key);
        }

        // merge with layer hints
        mergeHints();

        // distribute the combined hints dictionary
        sendHintsToLayers();
    }


    /**
     * @brief Get a hint from the merged hints.
     */
    public Object hintForKey(String key) {
        // query the merged dictionary
        return mMergedHints.get(key);
    }


    /**
     * @brief Collect hints from all layers.
     *
     * Not optimized - we query all layers in order to get the hints. Layers with lower priority come first, higher
     * priority layers override the hints. User-defined hints override everything.
     */
    public void collectHintsFromLayers() {
        // clear layer hints
        mLayerHints.clear();

        // go through all layers and collect the hints
        for (PDEButtonLayerHolder mButtonLayer : mButtonLayers) {
            collectHints(mLayerHints, mButtonLayer.mLayer);
        }

        // merge them for later use
        mergeHints();
    }


    /**
     * @brief Collect a hints from one layer.
     *
     * The hints are added to the given dictionary.
     */
    private void collectHints(PDEDictionary hints, PDEButtonLayerInterface layer) {
        // security
        if (layer == null) {
            return;
        }

        // collect them
        layer.collectHints(hints);
    }


    /**
     * @brief Merge layer hints and global user hints.
     *
     * User hints take priority.
     */
    private void mergeHints() {
        // set merged dictionary as copy of layer hints
        mMergedHints = mLayerHints.copy();

        // add user hints
        mMergedHints.addEntriesFromDictionary(mHints);
    }


    /**
     * @brief Send out hints to all layers.
     */
    private void sendHintsToLayers() {
        // go through all layers and collect the hints
        for (PDEButtonLayerHolder mButtonLayer : mButtonLayers) {
            // send it
            sendHints(mMergedHints, mButtonLayer.mLayer);
        }
    }


    /**
     * @brief Send out hints to all layers.
     */
    private void sendHints(PDEDictionary hints, PDEButtonLayerInterface layer) {
        // security
        if (layer == null) {
            return;
        }

        // set them
        layer.setHints(hints);
    }


//----- layout ---------------------------------------------------------------------------------------------------------

    /**
     * Change inner layout params according to the root layout
     * @param params new params
     */
    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        // special case TableLayout -> Here the children are always handled as MATCH_PARENT (even if something else is
        // set). So we set the right value -> otherwise we get confused.
        if (params instanceof TableRow.LayoutParams) {
            params.width = LayoutParams.MATCH_PARENT;
        }

        super.setLayoutParams(params);
        ViewGroup.LayoutParams lp = params;

        if (isInEditMode()) return;

        if (lp.width == LayoutParams.WRAP_CONTENT) {
            lp = findViewById(R.id.pdebutton_overlay_foreground_holder).getLayoutParams();
            lp.width = LayoutParams.WRAP_CONTENT;
            findViewById(R.id.pdebutton_overlay_foreground_holder).setLayoutParams(lp);

            lp = findViewById(R.id.pdebutton_inner_layout).getLayoutParams();
            lp.width = LayoutParams.WRAP_CONTENT;
            findViewById(R.id.pdebutton_inner_layout).setLayoutParams(lp);
        } else {
            lp = findViewById(R.id.pdebutton_overlay_foreground_holder).getLayoutParams();
            lp.width = LayoutParams.MATCH_PARENT;
            findViewById(R.id.pdebutton_overlay_foreground_holder).setLayoutParams(lp);

            lp = findViewById(R.id.pdebutton_inner_layout).getLayoutParams();
            lp.width = LayoutParams.MATCH_PARENT;
            findViewById(R.id.pdebutton_inner_layout).setLayoutParams(lp);
        }
    }


    /**
     * @brief Determine layout size of element.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // We do our custom on Measure here, since the background has to be the size of the button, but should not
        // influence the size.

        int heightMeasureSpecOld = heightMeasureSpec;

        if (DEBUG_OUTPUT){
            Log.d(LOG_TAG, "onMeasure "+MeasureSpec.toString(widthMeasureSpec) + " x "
                    + MeasureSpec.toString(heightMeasureSpec));
        }

        if (isInEditMode()) {
            // Find out how big everyone wants to be
            measureChildren(widthMeasureSpec, heightMeasureSpec);

            setMeasuredDimension(resolveSize(100, widthMeasureSpec),
                    resolveSize(100, heightMeasureSpec));
            return;
        }

        // now do the special case, that the background doesn't influence the size!

        int count = getChildCount();
        int maxHeight = 0;
        int maxWidth = 0;

        // workaround: In order to set the right height as early as possible
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if ( layoutParams.height > 0) {
            if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        Math.min(MeasureSpec.getSize(heightMeasureSpec),  layoutParams.height), MeasureSpec.AT_MOST);
            } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec( layoutParams.height, MeasureSpec.UNSPECIFIED);
            } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec( layoutParams.height, MeasureSpec.EXACTLY);
            }
        }

        if (DEBUG_OUTPUT) {
            Log.d(LOG_TAG, "onMeasure corrected: "+MeasureSpec.toString(widthMeasureSpec) + " x "
                    + MeasureSpec.toString(heightMeasureSpec));
        }

        // Find out how big everyone wants to be
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        // Find rightmost and bottom-most child
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            // skip invisible and background!
            if (child.getVisibility() != GONE && child.getId() != R.id.pdebutton_background_slot) {
                int childRight;
                int childBottom;

                PDEAbsoluteLayout.LayoutParams lp = (PDEAbsoluteLayout.LayoutParams) child.getLayoutParams();

                childRight = lp.x + child.getMeasuredWidth();
                childBottom = lp.y + child.getMeasuredHeight();

                maxWidth = Math.max(maxWidth, childRight);
                maxHeight = Math.max(maxHeight, childBottom);
            }
        }

        // Account for padding too
        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();

        // we want it bigger for Outer Shadow buttons!
        maxWidth += mButtonPadding.getLeft() + mButtonPadding.getRight();
        maxHeight += mButtonPadding.getTop() + mButtonPadding.getBottom();


        // Check against minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec),
                resolveSize(maxHeight, heightMeasureSpecOld));

        //update the background
        findViewById(R.id.pdebutton_background_slot).measure(
                MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));

        if (DEBUG_OUTPUT) {
            Log.d(LOG_TAG, "onMeasure result: "+getMeasuredWidth()+" x "+getMeasuredHeight());
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(DEBUG_OUTPUT){
            Log.d(LOG_TAG, "onLayout " + l + "," + t + "," + r + "," + b);
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                if (child.getId() == R.id.pdebutton_background_slot) {
                    PDEAbsoluteLayout.LayoutParams lp = (PDEAbsoluteLayout.LayoutParams) child
                            .getLayoutParams();
                    int childLeft =  lp.x;
                    int childTop = lp.y;
                    child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop
                            + child.getMeasuredHeight());
                } else {
                    PDEAbsoluteLayout.LayoutParams lp = (PDEAbsoluteLayout.LayoutParams) child
                            .getLayoutParams();
                    int childLeft = getPaddingLeft() + lp.x;
                    int childTop = getPaddingTop() + lp.y;
                    child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop
                            + child.getMeasuredHeight());
                }
            }
        }
    }


    /**
     *  @brief Size changed.
     *
     * @param width New width.
     * @param height New height.
     * @param oldWidth Old width.
     * @param oldHeight Old height.
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        if(DEBUG_OUTPUT){
            Log.d(LOG_TAG, "onSizeChanged " + width + "," + height + " old:" + oldWidth + "," + oldHeight);
        }

        super.onSizeChanged(width, height, oldWidth, oldHeight);

    }


//----- layout helper functions ----------------------------------------------------------------------------------------


    /**
     * @brief Helper for offset calculations.
     *
     * To determine the absolute position of the dirty rect of an outer shadow we need to know where the content
     * area of our application starts (y-offset without status & header bar).
     */
    public int getMainLayoutTop() {
        return ((Activity) getContext()).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }


    /**
     * @brief Returns the padding the component needs to be displayed correctly.
     *
     * Some things like an outer shadow have to be drawn outside of the layer bounds.
     * So the View that holds the element has to be sized bigger than the element bounds.
     * For proper layouting the view must be extended to each direction by the value delivered by
     * this function.
     *
     * @return the needed padding
     */
    public Rect getNeededPadding(){
        if (mButtonPadding != null) {
            return mButtonPadding.getPaddingRect();
        } else {
            return new Rect(0, 0, 0, 0);
        }
    }


    protected void resolveButtonPadding(){
        // reset
        mButtonPadding =  new PDEButtonPadding();
        mButtonPadding.putPaddingRequest(mMinButtonPadding);

        // go through all layers and collect the requests
        for (PDEButtonLayerHolder mButtonLayer : mButtonLayers) {
            collectButtonPaddingRequests(mButtonPadding, mButtonLayer.mLayer);
        }

        if (findViewById(R.id.pdebutton_inner_layout) != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) findViewById(R.id.pdebutton_inner_layout).
                    getLayoutParams();
            lp.setMargins(mButtonPadding.getLeft(),mButtonPadding.getTop(), mButtonPadding.getRight(),
                    mButtonPadding.getBottom());
            findViewById(R.id.pdebutton_inner_layout).setLayoutParams(lp);
        }
    }


    protected void collectButtonPaddingRequests(PDEButtonPadding buttonPadding,PDEButtonLayerInterface layer){
        if( layer == null){
            return;
        }
        layer.collectButtonPaddingRequest(buttonPadding);
    }


    public void setMinButtonPadding(int left,int top, int right, int bottom){
        if (left >= 0 && top >= 0 && right >= 0 && bottom >= 0 &&
                (left != mMinButtonPadding.left || top != mMinButtonPadding.top || right != mMinButtonPadding.right ||
                        bottom != mMinButtonPadding.right)){
            mMinButtonPadding = new Rect(left,top,right,bottom);
        }
    }

    public Rect getMinButtonPadding(){
        return mMinButtonPadding;
    }


//----- Event Handling -------------------------------------------------------------------------------------------------

    /**
     * @brief Get the eventSource which is responsible for sending PDEEvents events.
     * Most of the events are coming form the PDEAgentController.
     * @return PDEEventSource
     */
    @Override
    public PDEEventSource getEventSource() {
        return mEventSource;
    }


    /**
     * @brief Add event Listener - hold strong pointer to it.
     *
     * PDEIEventSource Interface implementation, with additional local storage of (strong) pointer to it.
     *
     * @param target    Object which will be called in case of an event.
     * @param methodName Function in the target object which will be called.
     *                   The method must accept one parameter of the type PDEEvent
     * @return Object which can be used to remove this listener
     *
     * @see de.telekom.pde.codelibrary.ui.events.PDEEventSource#addListener
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
     *
     * @param target    Object which will be called in case of an event.
     * @param methodName Function in the target object which will be called.
     *                   The method must accept one parameter of the type PDEEvent
     * @param eventMask PDEAgentController event mask.
     *                  Will be most of the time PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED or
     *                  PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED
     * @return Object which can be used to remove this listener
     *
     * @see de.telekom.pde.codelibrary.ui.events.PDEEventSource#addListener
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
     *
     * @param listener the event listener that should be removed
     * @return Returns whether we have found & removed the listener or not
     */
    public boolean removeListener(Object listener) {
        mStrongPDEEventListenerHolder.remove(listener);
        return mEventSource.removeListener(listener);
    }

//----- Android OnClickListener logic --------------------------------------------------------------------------------------------


    /**
     * @brief Register a android style callback to be invoked when this view is clicked - NOTE better use the more
     * flexible addListener approach.
     * The PDEButton also supports the OnClickListener approach of Android. But in order to write Telekom StyleGuide
     * compartible code you are encouraged to use the PDEEvent (PDEEventSource).
     * The onClick of the Listen will be called when the WillBeSelected PDEEvent is sent.
     * If you need to react on the Selected-Event please use the addListener approach.
     *
     * @param l The callback that will run
     */
    @Override
    public void setOnClickListener(OnClickListener l) {
        if (l == null && mOnClickListener != null) {
            // remove listener
            if (mAgentStateListenWillBeSelected != null) {
                removeListener(mAgentStateListenWillBeSelected);
            }
        } else if (l != null && mOnClickListener == null) {
            // add listener
            mAgentStateListenWillBeSelected = addListener(this, "onActionWillBeSelected",
                                                 PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED);
        }

        // remember
        mOnClickListener = l;
    }


    /**
     * Call this view's OnClickListener, if it is defined.  Performs all normal
     * actions associated with clicking: reporting accessibility event, playing
     * a sound, etc.
     *
     * @return True there was an assigned OnClickListener that was called, false
     *         otherwise is returned.
     */
    public boolean performPDEButtonClick() {
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);

        if (mOnClickListener != null) {
            playSoundEffect(SoundEffectConstants.CLICK);
            mOnClickListener.onClick(this);
            return true;
        }

        return false;
    }


    /**
     * Directly call any attached OnClickListener.  Unlike {@link #performClick()},
     * this only calls the listener, and does not do any associated clicking
     * actions like reporting an accessibility event.
     *
     * @return True there was an assigned OnClickListener that was called, false
     *         otherwise is returned.
     */
    public boolean callOnPDEButtonClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(this);
            return true;
        }
        return false;
    }


    /**
     * Return whether this view has an attached OnClickListener.  Returns
     * true if there is a listener, false if there is none.
     */
    @Override
    public boolean hasOnClickListeners() {
        return (mOnClickListener != null);
    }


    public void onActionWillBeSelected(PDEEvent event) {
        performPDEButtonClick();
    }

//----- Android Persistence --------------------------------------------------------------------------------------------


    /**
     * @brief Overwritten system function to restore button specific values
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());

        //set button specific values
        setSelected(ss.checked);
        setMainState(ss.mainState);
    }


    /**
     * @brief Overwritten system function to store button specific values.
     * E.g. for Activity restoring.
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState =  super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);

        // get button specific values, which need to be restored when the activity is reloaded
        ss.checked = isSelected();
        ss.mainState = getMainState();

        return ss;
    }

}

