/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentController;
import de.telekom.pde.codelibrary.ui.agents.PDEAgentControllerAdapterView;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonLayoutHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEDictionary;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

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
public class PDEButton extends PDEAbsoluteLayout implements PDEIEventSource {//PDEAbsoluteLayout {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEButton.class.getName();
    // debug messages switch
    private final static boolean DEBUGPARAMS = false;


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




//----------------------------------------------------------------------------------------------------------------------
//  PDEButton constants
//----------------------------------------------------------------------------------------------------------------------


    // parameter strings
    //
    public static final String PDEButtonParameterTitle = "title";
    public static final String PDEButtonParameterTitleColor = "titleColor";
    public static final String PDEButtonParameterColor = "color";
    public static final String PDEButtonParameterIcon = "icon";
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



    //----- constants -----


    /**
     * @brief Well known layer ids.
     */
    public enum PDEButtonLayerId {
        Background,
        Overlay,
        Foreground
    };

    /**
     * @brief Telekom well known button layer types.
     */
    public enum PDEButtonLayerType {
        None,
        User,
        ForegroundNone,
        ForegroundUser,
        ForegroundIconText,
        BackgroundNone,
        BackgroundUser,
        BackgroundFlat,
        BackgroundBeveled,
        BackgroundEmbossed,
        BackgroundIndicative,
        BackgroundPlate,
        BackgroundRect,
        OverlayNone,
        OverlayUser,
        OverlayCheckbox,
        OverlayRadio
    };


    /**
     * @brief Alignment of Icon within the button.
     */
    public enum PDEButtonIconAlignment {
        PDEButtonIconAlignmentLeft,
        PDEButtonIconAlignmentRight,
        PDEButtonIconAlignmentLeftAttached,
        PDEButtonIconAlignmentRightAttached
    };




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
    PDEButtonLayoutHelper mLayout;
    Rect mMinButtonPadding;



    // android specific - the context
    Context mContext;

    protected PDEEventSource mEventSource;

    protected PDEButtonPadding mButtonPadding;


    public PDEButton(android.content.Context context){
        super(context);
        init(context, null);
    }


    public PDEButton(android.content.Context context, android.util.AttributeSet attrs){
        super(context,attrs);
        if(DEBUGPARAMS){
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                Log.d(LOG_TAG, "PDEButton-Attr("+i+"): "+attrs.getAttributeName(i)+" => "+attrs.getAttributeValue(i));
            }
        }
        init(context, attrs);
    }

    public PDEButton(android.content.Context context, android.util.AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    /**
     * @brief Internal initialisation.
     *
     * Create necessary graphics, start with a default configuration.
     */
    protected void init(Context context, android.util.AttributeSet attrs){
        String title;

        //init
        mEventSource = new PDEEventSource();

        // init
        // remember context
        mContext = context;
        mButtonLayerToInitialize = null;
        mLayout = new PDEButtonLayoutHelper();
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

        if(PDECodeLibrary.getInstance().isSoftwareRenderingButton() && Build.VERSION.SDK_INT>=11){
            if(DEBUGPARAMS) Log.d(LOG_TAG,"Switching Hardware acceleration OFF!!!!!!!!");
            setLayerType(LAYER_TYPE_SOFTWARE,null);
        }

        // android specific initialization
        // we need to be clickable otherwise we are not a button.
        setClickable(true);

        // we also should be focusable
        setFocusable(true);

        // avoid clipping for drawing of outer shadow
//        setClipChildren(false);
//        setClipToPadding(false);

        // we have our own agent controller
        initAgent();

        // Define type properties for parameters
        if (mAcceptedTypes != null) {
            // Define possible classes
            Class stringClass = String.class;
            Class numberClass = Number.class;
            Class colorClass = PDEColor.class;
            Class imageClass = Drawable.class;


            // Set accepted types for every parameter - target class is first element!
            mAcceptedTypes = new PDEDictionary(
                    Arrays.asList(stringClass), PDEButtonParameterTitle,
                    Arrays.asList(colorClass, stringClass), PDEButtonParameterTitleColor,
                    Arrays.asList(colorClass, stringClass), PDEButtonParameterColor,
                    Arrays.asList(imageClass, stringClass), PDEButtonParameterIcon,
                    Arrays.asList(colorClass, stringClass), PDEButtonParameterBorderColor,
                    Arrays.asList(numberClass, stringClass), PDEButtonParameterCornerRadius,
                    Arrays.asList(stringClass), PDEButtonParameterFont,
                    Arrays.asList(stringClass, numberClass), PDEButtonParameterBackground);
        }

        // start out with default configration: text and beveled background
        if (attrs==null) setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.BackgroundBeveled);
        setButtonForegroundLayerWithLayerType(PDEButtonLayerType.ForegroundIconText);

        if (attrs != null) {
            TypedArray sa = getContext().obtainStyledAttributes(attrs, R.styleable.PDEButton);

            // first create layer if wanted by xml, use default beveled
            if (sa.hasValue(R.styleable.PDEButton_background)) {
                setButtonBackgroundLayerWithLayerType(sa.getInt(R.styleable.PDEButton_background,0));
            } else setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.BackgroundBeveled);

            // set title
            title = sa.getString(R.styleable.PDEButton_text);
            if (TextUtils.isEmpty(title)) {
                title = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "");
            }
            setTitle(title);

            // set other attributes
            if (sa.hasValue(R.styleable.PDEButton_icon_colored)) {
                setIconColored(sa.getBoolean(R.styleable.PDEButton_icon_colored, false));
            }
            if (sa.hasValue(R.styleable.PDEButton_icon_alignment)) {
                setIconAlignment(PDEButtonIconAlignment.values()[sa.getInt(R.styleable.PDEButton_icon_alignment, 0)]);
            }
            if (sa.hasValue(R.styleable.PDEButton_border_color)) {
                setBorderColorWithInt(sa.getColor(R.styleable.PDEButton_border_color, R.color.DTBlack));
            }
            if (sa.hasValue(R.styleable.PDEButton_title_color)) {
                setTitleColorWithInt(sa.getColor(R.styleable.PDEButton_title_color, R.color.DTBlack));
            }
            if (sa.hasValue(R.styleable.PDEButton_button_color)) {
                setColorWithInt(sa.getColor(R.styleable.PDEButton_button_color,R.color.DTBlue));
            }
            if (sa.hasValue(R.styleable.PDEButton_src)) {
                setIcon(mContext.getResources().getDrawable(sa.getResourceId(R.styleable.PDEButton_src,-1)));
            } else {
                int res = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android","src",-1);
                if (res != -1) {
                    setIcon(context.getResources().getDrawable(res));
                }
            }
            if (sa.hasValue(R.styleable.PDEButton_text_alignment)) {
                setAlignment(PDEConstants.PDEAlignment.values()[sa.getInt(R.styleable.PDEButton_text_alignment, 0)]);
            }
            if( sa.hasValue(R.styleable.PDEButton_text_size)) {
                String text_size = sa.getString(R.styleable.PDEButton_text_size);
                setFontSize(text_size);
            }
            if( sa.hasValue(R.styleable.PDEButton_typeface)) {
                setFont(PDETypeface.createByName(sa.getString(R.styleable.PDEButton_typeface)));
            }

            if( sa.hasValue(R.styleable.PDEButton_min_button_padding)) {
                setMinButtonPadding(sa.getDimensionPixelOffset(R.styleable.PDEButton_min_button_padding,0),
                                    sa.getDimensionPixelOffset(R.styleable.PDEButton_min_button_padding,0),
                                    sa.getDimensionPixelOffset(R.styleable.PDEButton_min_button_padding,0),
                                    sa.getDimensionPixelOffset(R.styleable.PDEButton_min_button_padding,0));
            }
            if( sa.hasValue(R.styleable.PDEButton_min_button_padding_left)) {
                setMinButtonPadding(sa.getDimensionPixelOffset(R.styleable.PDEButton_min_button_padding_left,0),
                                    mMinButtonPadding.top,
                                    mMinButtonPadding.right,mMinButtonPadding.bottom);
            }
            if( sa.hasValue(R.styleable.PDEButton_min_button_padding_top)) {
                setMinButtonPadding(mMinButtonPadding.left,
                                    sa.getDimensionPixelOffset(R.styleable.PDEButton_min_button_padding_top,0),
                                    mMinButtonPadding.right,mMinButtonPadding.bottom);
            }
            if( sa.hasValue(R.styleable.PDEButton_min_button_padding_right)) {
                setMinButtonPadding(mMinButtonPadding.left,mMinButtonPadding.top,
                                    sa.getDimensionPixelOffset(R.styleable.PDEButton_min_button_padding_right,0),
                                    mMinButtonPadding.bottom);
            }
            if( sa.hasValue(R.styleable.PDEButton_min_button_padding_bottom)) {
                setMinButtonPadding(mMinButtonPadding.left,mMinButtonPadding.top,
                                    mMinButtonPadding.right,
                                    sa.getDimensionPixelOffset(R.styleable.PDEButton_min_button_padding_bottom,0));
            }

            if (sa.hasValue(R.styleable.PDEButton_overlay)) {
                setButtonOverlayLayerWithLayerType(sa.getInt(R.styleable.PDEButton_overlay,0));
            }

            if (sa.hasValue(R.styleable.PDEButton_selected)) {
                setSelected(sa.getBoolean(R.styleable.PDEButton_selected, false));
            }
            if (sa.hasValue(R.styleable.PDEButton_checkbox_alignment)) {
                setCheckboxAlignment(PDEConstants.PDEAlignment.values()[sa.getInt(R.styleable.PDEButton_checkbox_alignment, 0)]);
            }
            if (sa.hasValue(R.styleable.PDEButton_radio_alignment)) {
                setRadioAlignment(PDEConstants.PDEAlignment.values()[sa.getInt(R.styleable.PDEButton_radio_alignment, 0)]);
            }


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
        mEventSource.forwardEvents((PDEIEventSource) mAgentAdapter,
                PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK_ACTION);
        mEventSource.setEventDefaultSender(this, true);
    }

    // needs to be public otherwise it cannot be called from eventsource (11.10.2012)

    /**
     * @brief Called on changes from agentController.
     */
    public void cbAgentController(PDEEvent event) {
        if (event.isType(PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK_ANIMATION)) {
            // tell all layers
            for (Iterator<PDEButtonLayerHolder> iterator = mButtonLayers.iterator(); iterator.hasNext(); ) {
                sendAgentEvent(event, iterator.next().mLayer);
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


    //----- layer handling --------------------------------------------------------------------------------------------------


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
                // insert layer in correct z-order
                if (layer.getLayer() != null) {
                    LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0, 0);
                    addView(layer.getLayer(), i, lp);
                }
                inserted = true;
                // done the loop
                break;
            }
        }
        if (!inserted) {
            mButtonLayers.add(holder);
            if (layer.getLayer() != null) {
                LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0, 0);
                addView(layer.getLayer(), lp);
            }
        }

//        // now add the layer (if it has a visual)
//        if (layer.getLayer() != null) {
////            Log.d(LOG_TAG, "setButtonLayer addLayer "+layerType.name()+" "+layerId.name()+"="+layerId.ordinal()+" index: "+Math.min(layerId.ordinal(),getChildCount()));
//            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0, 0);
//            //LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//            addView(layer.getLayer(), Math.min(layerId.ordinal(),getChildCount()), lp);
//        }


        // hints might have changed -> collect and send them again
        collectHintsFromLayers();
        sendHintsToLayers();

        // initialize: send parameters
        sendParameters(mParameters, layer);

        // set layout
        //sendLayout(mButtonRect, layer);
        resolveButtonPadding();
        // complete relayout (lower layers depend on upper layers for constraints)
        performLayout();
       // sendLayout(mLayout, layer);
        //sendLayoutToLayers();  // probably other Layers are affected of this; but it works buggy right now

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
                    removeView(layer.mLayer.getLayer());
                }
                // and forget
                mButtonLayers.remove(i);
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
        for (Iterator<PDEButtonLayerHolder> iterator = mButtonLayers.iterator(); iterator.hasNext(); ) {
            PDEButtonLayerHolder holder = iterator.next();
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

    public void setButtonBackgroundLayerWithLayerType(int layerType) {
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
                layer = new PDEButtonLayerBackgroundFlat();
                break;
            case BackgroundBeveled:
                layer = new PDEButtonLayerBackgroundBeveled();
                break;
            case BackgroundEmbossed:
                layer = new PDEButtonLayerBackgroundEmbossed(/*this*/);
                break;
            case BackgroundIndicative:
                layer = new PDEButtonLayerBackgroundIndicative();
                break;
            case BackgroundPlate:
                layer = new PDEButtonLayerBackgroundPlate();
                break;
            case BackgroundRect:
                layer = new PDEButtonLayerBackgroundRect();
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

        if (layerType.equalsIgnoreCase("PDEButtonBackgroundFlat") || layerType.equalsIgnoreCase("Flat")) {
            setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.BackgroundFlat);
        } else if (layerType.equalsIgnoreCase("PDEButtonBackgroundEmbossed") || layerType.equalsIgnoreCase("Embossed")) {
            setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.BackgroundEmbossed);
        } else if (layerType.equalsIgnoreCase("PDEButtonBackgroundBeveled") || layerType.equalsIgnoreCase("Beveled")) {
            setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.BackgroundBeveled);
        } else if (layerType.equalsIgnoreCase("PDEButtonBackgroundIndicative") ||
                   layerType.equalsIgnoreCase("Indicative")) {
            setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.BackgroundIndicative);
        } else if (layerType.equalsIgnoreCase("PDEButtonBackgroundRect") || layerType.equalsIgnoreCase("Rect")) {
            setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.BackgroundRect);
        } else if (layerType.equalsIgnoreCase("PDEButtonBackgroundPlate") || layerType.equalsIgnoreCase("Plate")) {
            setButtonBackgroundLayerWithLayerType(PDEButtonLayerType.BackgroundPlate);
        }
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
                layer = new PDEButtonLayerForegroundIconText(mContext);
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
    public void setButtonOverlayLayerWithLayerType(PDEButtonLayerType layerType)
    {
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
            case OverlayCheckbox:
            layer = new PDEButtonLayerOverlayCheckbox(PDECodeLibrary.getInstance().getApplicationContext());
                break;
            case OverlayRadio:
                layer = new PDEButtonLayerOverlayRadio(PDECodeLibrary.getInstance().getApplicationContext());
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
     * @brief Set title.
     */
    public void setTitle(String title) {
        // set the parameters
        mParameters.setParameter(PDEButtonParameterTitle, title);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Get title.
     *
     * Only retrieves basic parameters, and only if a parameter was set -> default parameters are never retrieved.
     */
    public String getTitle() {
        // retrieve parameter main value
        return mParameters.parameterValueForName(PDEButtonParameterTitle);
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
        String colorstr;

        // create a string from the color to integrate into parameters
        colorstr = PDEColor.stringFromIntColor(color);

        // set the parameters
        mParameters.setParameter(PDEButtonParameterTitleColor, colorstr);

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
     * @brief Set icon.
     *
     * @param icon drawable reference or null to clear it.
     */
    public void setIcon(Drawable icon) {
        // set the parameters (objects not supported yet)
        mParameters.setParameter(PDEButtonParameterIcon, icon);

        // tell all sublayers the new parameters
        sendParametersToLayers();
    }


    /**
     * @brief Set icon.
     *
     * @param fileName complete filename (including suffix) or nil to clear it.
     */
    public void setIconFromFileName(String fileName) {
        // set the parameters
        mParameters.setParameter(PDEButtonParameterIcon, fileName);

        // tell all sublayers the new parameters
        sendParametersToLayers();
    }


    /**
     * @brief Set icon.
     *
     * @param icon drawable or nil to clear it.
     * @param colored true for a multicolor image (no coloring)
     *
     */
    public void setIcon(Drawable icon, boolean colored) {

        // set the parameters (objects not supported yet)
        mParameters.setParameter(PDEButtonParameterIcon, icon);

        // set icon colored state
        mParameters.setParameter(PDEButtonParameterIconColored, new Boolean(true));

        // tell all sublayers the new parameters
        sendParametersToLayers();
    }


    /**
     * @brief Set icon colorization mode.
     */
    public void setIconColored(boolean colored) {
        // set icon colored state
        mParameters.setParameter(PDEButtonParameterIconColored, new Boolean(true));

        // tell all sublayers the new parameters
        sendParametersToLayers();
    }

    /**
     * @brief Get icon.
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
     * @brief Get icon colored style.
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
    public void setFont(PDETypeface font)
    {
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
    public PDETypeface getFont()
    {
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
    public void setFontSize(float fontSize)
    {
        // set directly as number
        setParameter(PDEButtonParameterFontSize, fontSize);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Set the font size bei size String.
     *
     * The string must follow the format float[unit]. Unit is optional but if present valid values are "BU", "%" and "Caps".
     * It is also possible to set the strings "auto" or "automatic", and to "styleguide"
     *
     * @param sizeString
     */
    public void  setFontSize(String sizeString)
    {
        // set the string
        setParameter(PDEButtonParameterFontSize, sizeString);

        // tell all sub layers the new state
        sendParametersToLayers();
    }


    /**
     * @brief Get font size (float).
     *
     * Only retrieves basic parameters, and only if a float was directly set.
     */
    public float getFontSize()
    {
        // retrieve parameter main value
        return mParameters.parameterFloatForName(PDEButtonParameterFontSize);


    }


    /**
     * @brief Get font size string.
     *
     * Only retrieves basic parameters, and only if a string was previously set
     */
    public String getFontSizeString()
    {
        // retrieve parameter main value
        return mParameters.parameterValueForName(PDEButtonParameterFontSize);
    }

    /*
    * @brief Set corner radius
    */
    public void setCornerRadius(float cornerRadius) {
        // set the parameters
        mParameters.setParameter(PDEButtonParameterCornerRadius, String.format("%.02f", cornerRadius));

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
    public void setAlignment(PDEConstants.PDEAlignment alignment)
    {
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

    public void setCheckboxAlignment(PDEConstants.PDEAlignment alignment)
    {
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

    public void setRadioAlignment(PDEConstants.PDEAlignment alignment)
    {
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
    public PDEConstants.PDEAlignment getAlignment()
    {
        String textAlignmentString = mParameters.parameterValueForNameWithDefault(PDEButtonParameterAlignment, PDEConstants.PDEAlignmentStringCenter);
        PDEConstants.PDEAlignment textAlignment;

        // parse value
        if ( textAlignmentString.equals(PDEConstants.PDEAlignmentStringLeft)) textAlignment = PDEConstants.PDEAlignment.PDEAlignmentLeft;
        else if ( textAlignmentString.equals(PDEConstants.PDEAlignmentStringCenter)) textAlignment = PDEConstants.PDEAlignment.PDEAlignmentCenter;
        else if ( textAlignmentString.equals(PDEConstants.PDEAlignmentStringRight)) textAlignment = PDEConstants.PDEAlignment.PDEAlignmentRight;
        else textAlignment = PDEConstants.PDEAlignment.PDEAlignmentCenter;

        return textAlignment;
    }


    /**
     * @brief Set the alignment of the icon within the ForegroundIconTextLayer of the button.
     *
     * @param alignment left, right and left-attached, right-attached alignment are available. leftAttached is the default.
     */
    public void setIconAlignment(PDEButtonIconAlignment alignment)
    {
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
     * @brief Get icon alignment of ForegroundIconTextLayer
     */
    public PDEButtonIconAlignment getIconAlignment()
    {
        String iconAlignmentString = mParameters.parameterValueForNameWithDefault(PDEButton.PDEButtonParameterIconAlignment, PDEConstants.PDEAlignmentStringCenter);
        PDEButtonIconAlignment iconAlignment;

        // parse value
        if ( iconAlignmentString.equals(PDEConstants.PDEAlignmentStringLeft)) iconAlignment= PDEButtonIconAlignment.PDEButtonIconAlignmentLeft;
        else if ( iconAlignmentString.equals(PDEConstants.PDEAlignmentStringRight)) iconAlignment= PDEButtonIconAlignment.PDEButtonIconAlignmentRight;
        else if ( iconAlignmentString.equals(PDEConstants.PDEAlignmentStringLeftAttached)) iconAlignment= PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached;
        else if ( iconAlignmentString.equals(PDEConstants.PDEAlignmentStringRightAttached)) iconAlignment= PDEButtonIconAlignment.PDEButtonIconAlignmentRightAttached;
        else iconAlignment= PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached;

        return iconAlignment;
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
        for (Iterator<PDEButtonLayerHolder> iterator = mButtonLayers.iterator(); iterator.hasNext(); ) {
            // send it
            sendParameters(mParameters, iterator.next().mLayer);
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
    public boolean isSelected() {
        // check for selected state
        if (getMainState().equalsIgnoreCase(PDEButtonStateSelected)) {
            return true;
        } else {
            return false;
        }
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
        for (Iterator<PDEButtonLayerHolder> iterator = mButtonLayers.iterator(); iterator.hasNext(); ) {
            collectHints(mLayerHints, iterator.next().mLayer);
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
        for (Iterator<PDEButtonLayerHolder> iterator = mButtonLayers.iterator(); iterator.hasNext(); ) {
            // send it
            sendHints(mMergedHints, iterator.next().mLayer);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w,h;
        ViewGroup.LayoutParams lp;
        if(DEBUGPARAMS){
            int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
            int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
            int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);
            int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
            Log.d(LOG_TAG, "onMeasure (" + specWidthSize + " x " + specHeightSize + "; Modes:" + specWidthMode + " x " +
            specHeightMode + ")");
        }

        //first function to call
        // ToDo Do we also have to use mWorkLayer here? and how?
        super.onMeasure(widthMeasureSpec,
                        heightMeasureSpec);    //To change body of overridden methods use File | Settings | File Templates.

        // get current layout params
        lp = this.getLayoutParams();
        w = lp.width;
        h = lp.height;

        if (lp.width != LayoutParams.WRAP_CONTENT && lp.width != ViewGroup.LayoutParams.MATCH_PARENT && lp.width != 0) w = lp.width;
        else if (getWidth() != LayoutParams.WRAP_CONTENT && getWidth() != ViewGroup.LayoutParams.MATCH_PARENT &&  getWidth()!=0) w = getWidth();
        else if (getMeasuredWidth() != LayoutParams.WRAP_CONTENT && getMeasuredWidth() != ViewGroup.LayoutParams.MATCH_PARENT && getMeasuredWidth()!=0) w = getMeasuredWidth();

        if (lp.height != LayoutParams.WRAP_CONTENT && lp.height != ViewGroup.LayoutParams.MATCH_PARENT && lp.height!=0) h = lp.height;
        else if (getHeight() != LayoutParams.WRAP_CONTENT && getHeight() != ViewGroup.LayoutParams.MATCH_PARENT && getHeight() != 0) h = getHeight();
        else if (getMeasuredHeight() != LayoutParams.WRAP_CONTENT && getMeasuredHeight() != ViewGroup.LayoutParams.MATCH_PARENT && getMeasuredHeight()!=0) h = getMeasuredHeight();

        sizeChanged(w, h);
    }

    @Override
    protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "measureChildren (" + widthMeasureSpec + ", " + heightMeasureSpec + ")");
        }
        // ToDo Do we also have to use mWorkLayer here? and how?
        super.measureChildren(widthMeasureSpec,
                              heightMeasureSpec);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        if(DEBUGPARAMS){
            Log.d(LOG_TAG,
                  "measureChild " + child.toString() + " " + parentWidthMeasureSpec + " " + parentHeightMeasureSpec);
        }
        // ToDo Do we also have to use mWorkLayer here? and how?
        super.measureChild(child, parentWidthMeasureSpec,
                           parentHeightMeasureSpec);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
                                           int parentHeightMeasureSpec, int heightUsed) {
        if(DEBUGPARAMS){
            Log.d(LOG_TAG,
                  "measureChildWithMargins " + child.toString() + " " + parentWidthMeasureSpec + " " + widthUsed + " " +
                  parentHeightMeasureSpec + " " + heightUsed);
        }
        // ToDo Do we also have to use mWorkLayer here? and how?
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec,
                heightUsed);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "onLayout " + l + "," + t + "," + r + "," + b);
        }
        // ToDo Do we also have to use mWorkLayer here? and how?
        super.onLayout(changed, l, t, r, b);
        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "onLayout after super " + l + "," + t + "," + r + "," + b);
        }

        if (changed) {
            //sizeChanged(r - l-(getPaddingLeft()+getPaddingRight()), b - t-(getPaddingTop()+getPaddingBottom()));
            sizeChanged(r - l, b - t);
        }
        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "onLayout after end " + l + "," + t + "," + r + "," + b);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "onSizeChanged " + w + "," + h + " old:" + oldw + "," + oldh);
        }

        // todo check if this measure call here is not completly awkward. but in a change to embossed background it helps
        measure(MeasureSpec.makeMeasureSpec(w,MeasureSpec.AT_MOST),MeasureSpec.makeMeasureSpec(h,MeasureSpec.AT_MOST));
        // ToDo Do we also have to use mWorkLayer here? and how?
        super.onSizeChanged(w, h, oldw, oldh);

        //sizeChanged(w, h);
    }

    private void sizeChanged(int width, int height) {
        // check if anything changed
        if (mLayout.mButtonRect.width() == width && mLayout.mButtonRect.height() == height) {
            return;
        }
        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "sizeChanged with new size: "+width+","+height);
        }
        // remember
        mLayout.mButtonRect.left = 0;
        mLayout.mButtonRect.top = 0;
        mLayout.mButtonRect.right = width;
        mLayout.mButtonRect.bottom = height;


        resolveButtonPadding();

        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "My Screen rect: left: "+mLayout.mButtonRect.left+", top: "+mLayout.mButtonRect.top+", " +
                           "right: "+mLayout.mButtonRect.right+", bottom: "+mLayout.mButtonRect.bottom);
            Log.d(LOG_TAG, "My Outline rect: left: "+mLayout.mLayoutRect.left+", top: "+mLayout.mLayoutRect.top+", " +
                           "right: "+mLayout.mLayoutRect.right+", bottom: "+mLayout.mLayoutRect.bottom);
        }


        // distribute it to all of them
        performLayout();
    }

    /**
     * @brief Helper function. Distribute the layout to all sub components.
     */
    private void performLayout() {
        PDEButtonLayoutHelper layout = new PDEButtonLayoutHelper(mLayout);

        // do for all
        for (Iterator<PDEButtonLayerHolder> iterator = mButtonLayers.iterator(); iterator.hasNext(); ) {
            // send it
            sendLayout(layout ,iterator.next().mLayer);
        }
    }


    /**
     * @brief Set the current layout to the sublayers.
     *
     * The layout is the complete button rect (always starting at offset 0.0) right now. The layout
     * functionality might be subject to change in the future, when additional requirements (e.g. larger click
     * areas) are implemented.
     */
    private void sendLayout(PDEButtonLayoutHelper layout, PDEButtonLayerInterface layer) {
        // security
        if (layer == null || layout == null ) {
            return;
        }

        // do the layout
        layer.setLayout(layout);
    }


    /**
     * @brief Helper for offset calculations.
     *
     * To determine the absolute position of the dirty rect of an outer shadow we need to know where the content
     * area of our application starts (y-offset without status & header bar).
     */
    public int getMainLayoutTop() {
        return ((Activity) mContext).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }

    @Override
    public PDEEventSource getEventSource() {
        return mEventSource;
    }

    @Override
    public Object addListener(Object target, String methodName) {
        return mEventSource.addListener(target, methodName);
    }

    @Override
    public Object addListener(Object target, String methodName, String eventMask) {
        return mEventSource.addListener(target, methodName, eventMask);
    }

    protected void resolveButtonPadding(){
        // reset
        mButtonPadding =  new PDEButtonPadding();
        mButtonPadding.putPaddingRequest(mMinButtonPadding);

        // go through all layers and collect the requests
        for (Iterator<PDEButtonLayerHolder> iterator = mButtonLayers.iterator(); iterator.hasNext(); ) {
            collectButtonPaddingRequests(mButtonPadding, iterator.next().mLayer);
        }

        mLayout.mLayoutRect = new Rect(mLayout.mButtonRect.left+mButtonPadding.getLeft(),mLayout.mButtonRect.top+mButtonPadding.getTop(),
                mLayout.mButtonRect.right-mButtonPadding.getRight(),mLayout.mButtonRect.bottom-mButtonPadding.getBottom());
    }

    protected void collectButtonPaddingRequests(PDEButtonPadding buttonPadding,PDEButtonLayerInterface layer){
        if( layer == null){
            return;
        }
        layer.collectButtonPaddingRequest(buttonPadding);
    }

    public void setMinButtonPadding(int left,int top, int right, int bottom){
        if(left>=0 && top>=0 && right>=0 && bottom>=0 &&
           (left!=mMinButtonPadding.left || top!=mMinButtonPadding.top || right!=mMinButtonPadding.right ||
            bottom!=mMinButtonPadding.right)){
            mMinButtonPadding = new Rect(left,top,right,bottom);
            // ToDo: invalidate??? start layout???

        }
    }

    public Rect getMinButtonPadding(){
        return mMinButtonPadding;
    }
}

