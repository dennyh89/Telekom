/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;




//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerOverlayCheckboxBase
//----------------------------------------------------------------------------------------------------------------------


import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableArea;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.PDEDictionary;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;




/**
 * @brief Overlay for an Checkbox button.
 */
public abstract class PDEButtonLayerOverlayCheckboxBase extends PDEAbsoluteLayout implements PDEButtonLayerInterface {


    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEButtonLayerOverlayCheckboxBase.class.getName();
    // debug messages switch
    private final static boolean DEBUG_PARAMS = false;
    private final static boolean SHOW_DEBUG_LOGS = false;


    // size modes
    enum PDEButtonLayerOverlayCheckboxSizeMode {
        PDEButtonLayerOverlayCheckboxSizeModeUndefined,
        PDEButtonLayerOverlayCheckboxSizeModeAutomatic,
        PDEButtonLayerOverlayCheckboxSizeModeStyleguide,
        PDEButtonLayerOverlayCheckboxSizeModeFixed
    }

    // global variables
    //
    static PDEDictionary PDEButtonLayerOverlayCheckboxGlobalColorDefault
            = PDEComponentHelpers.readDefaultColorDictionary(R.xml.dt_button_check_radio_color_defaults);
    static PDEDictionary PDEButtonLayerOverlayCheckboxGlobalBorderDefault
            = PDEComponentHelpers.readDefaultColorDictionary(R.xml.dt_button_border_color_defaults);

    // parameters needed
    protected PDEParameterDictionary mParameters;
    protected PDEParameter mParamColor;
    protected PDEParameter mParamBorderColor;
    protected PDEParameter mParamState;
    private int mHorizontalPadding;

    // content layers
    protected PDEDrawableArea mAreaDrawable;
    protected ImageView mIconLayer;

    // configuration
    protected float mCheckboxSize;
    protected PDEButtonLayerOverlayCheckboxSizeMode mCheckboxSizeMode;
    protected PDEConstants.PDEAlignment mCheckboxAlignment;

    // internal configuration
    protected float mCheckboxUsedSize;
    protected String mIconFile;
    protected Drawable mIcon;
    protected float mHeightUsedForSizeCalculation;

    // agent helpers
    protected PDEAgentHelper mAgentHelper;

    // darkstyle
    protected boolean mDarkStyle = false;


    /**
     * @brief Class initialization.
     */
    public PDEButtonLayerOverlayCheckboxBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    /**
     * @brief Class initialization.
     */
    public PDEButtonLayerOverlayCheckboxBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    /**
     * @brief Class initialization.
     */
    public PDEButtonLayerOverlayCheckboxBase(Context context) {
        super (context);
        init(context);
    }


    /**
     * @brief Internal init function - called by all constructors.
     */
    protected void init(Context context) {
        // init
        mParameters = null;
        mCheckboxUsedSize = 0.0f;
        mIcon = null;
        mDarkStyle = PDECodeLibrary.getInstance().isDarkStyle();

        // default configuration
        mCheckboxAlignment = PDEConstants.PDEAlignment.PDEAlignmentCenter;
        mCheckboxSizeMode = PDEButtonLayerOverlayCheckboxSizeMode.PDEButtonLayerOverlayCheckboxSizeModeStyleguide;
        mCheckboxSize = 0.0f;
        mHorizontalPadding = PDEBuildingUnits.pixelFromBU(2.0f);

        // agent helper
        mAgentHelper = new PDEAgentHelper();

        // create sunken area drawable and add it with it's own wrapper view
        mAreaDrawable = createDrawableArea();
        mAreaDrawable.setElementShapeRoundedRect(PDEBuildingUnits.oneThirdBU());
        addView(mAreaDrawable.getWrapperView());

        // create Icon layer
        mIconLayer = new ImageView(context);
        addView(mIconLayer);

        // set empty complex parameters
        mParamColor = new PDEParameter();
        mParamBorderColor = new PDEParameter();
        mParamState = new PDEParameter();

        // forced set of parameter -> this sets defaults
        setParameters(new PDEParameterDictionary(), true);

        //setBackgroundColor(0xff0000ff);
    }

    //----- basic functions ------------------------------------------------------------------------------------------------


    /**
     * @brief Layer access.
     */
    @Override
    public PDEAbsoluteLayout getLayer() {
        return this;
    }


    /**
     * @brief Process agent events.
     */
    @Override
    public void agentEvent(PDEEvent event)
    {
        boolean needsUpdate;

        // pass on agent events to agent helper
        needsUpdate = mAgentHelper.processAgentEvent(event);

        // update if necessary
        if (needsUpdate) {
            // update animateable parameters on change
            updateColors();
            updateState();
        }
    }


//----- property handling ----------------------------------------------------------------------------------------------


    /**
     * @brief Set button basic data.
     *
     * Should only be called from main button class, it handles the state, and also does change management.
     */
    @Override
    public void setParameters(PDEParameterDictionary parameters, boolean force)
    {
        // completely copy the new ones for further change management
        mParameters = parameters.copy();

        // color is a fixed parameter right now
        if (force) {
            prepareColor();
        }

        // state is a fixed parameter right now
        if (force) {
            prepareState();
        }

        // non-animateable properties do their change management internally
        prepareAlignment();
        prepareHorizontalPadding();
        //prepareSize();
        requestLayout();
    }



//----- animated parameters: preparation -------------------------------------------------------------------------------


    /**
     * @brief Set the (animateable) title color.
     *
     * If no title color is given, the title color is calculated from a) the main color, and b) the hints (indicative
     * buttons have a special color), and c) the system preset (dark or light system).
     *
     * The shadow color is also calculated; the shadow color depends solely on the main color (and does only change
     * in-between states)
     */
    protected void prepareColor() {
        // debug output
        if (DEBUG_PARAMS){
            mParamColor.debugOut("Color before building");
            mParamBorderColor.debugOut("Border before building");
        }

        // basic propagation of colors (no agent states required), convert to colors
        PDEComponentHelpers.fillStateBaseValues(mParamColor, null);
        mParamColor.convertToColor();

        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamColor, PDEButtonLayerOverlayCheckboxGlobalColorDefault,
                                        PDEColor.valueOf("DTUIInteractive"),
                                        PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamBorderColor, PDEButtonLayerOverlayCheckboxGlobalBorderDefault,
                                        mParamColor, null, PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // if border color for a state is still not defined, calculate it from main agent colors
        // (use gradient darker step)
        PDEComponentHelpers.fillBorderColors(mParamBorderColor, mParamColor);

        // debug output
        if(DEBUG_PARAMS){
            mParamColor.debugOut("Color after building");
            mParamBorderColor.debugOut("Border after building");
        }

        // and apply once
        updateColors();
    }


    /**
     * @brief Prepare shadow strength parameter set.
     *
     * Shadow strength animates the shadow as a whole. The range is 0.0..1.0.
     */
    protected void prepareState() {
        // set the new values
        mParamState.removeAllObjects();

        // create fixed defaults (old way for old compilers, dictionary literal would be better readable)
        mParamState.setWithDictionary(new PDEDictionary("default", "0.0", "selected", "1.0"));

        // debug output
        if(DEBUG_PARAMS){
            mParamState.debugOut("State before building");
        }

        // calculate agent states
        PDEComponentHelpers.buildValues(mParamState, null, null, PDEAgentHelper.PDEAgentHelperAnimationStateOnly);

        // and convert to number
        mParamState.convertToNumber();

        // debug output
        if (DEBUG_PARAMS) {
            mParamState.debugOut("State after building");
        }

        // apply once
        updateState();
    }


//----- non-animated parameters: preparation and application -----------------------------------------------------------


    /**
     * @brief protected function - prepare internal layout.
     *
     * IconOnRightSide and IconTextAlignment are non-animated and uses the base parameter
     */
    protected void prepareAlignment() {
        String alignmentString;
        PDEConstants.PDEAlignment alignment;

        // get the title from the parameters
        alignmentString = mParameters.parameterValueForNameWithDefault(PDEButton.PDEButtonParameterCheckboxAlignment,
                PDEConstants.PDEAlignmentStringLeft);

        // parse value
        if (alignmentString.equals(PDEConstants.PDEAlignmentStringLeft)) {
            alignment = PDEConstants.PDEAlignment.PDEAlignmentLeft;
        } else if (alignmentString.equals(PDEConstants.PDEAlignmentStringCenter)) {
            alignment = PDEConstants.PDEAlignment.PDEAlignmentCenter;
        } else if (alignmentString.equals(PDEConstants.PDEAlignmentStringRight)) {
            alignment = PDEConstants.PDEAlignment.PDEAlignmentRight;
        } else {
            alignment = PDEConstants.PDEAlignment.PDEAlignmentLeft;
        }

        // any change?
        if (alignment == mCheckboxAlignment) {
            return;
        }

        // remember
        mCheckboxAlignment = alignment;

        // relayout
        relayout();
    }


    /**
     * @brief protected function - prepare the font.
     *
     * Preparation function for the font which evaluates the parameters. Note that we need to
     */
    protected void prepareSize(float height) {
        Object sizeObject;
        PDEButtonLayerOverlayCheckboxSizeMode checkboxMode;
        float checkboxSize;
        String str;

        // get font parameters
        sizeObject = mParameters.parameterObjectForName(PDEButton.PDEButtonParameterFontSize);

        // parse font size
        if (sizeObject instanceof  Number) {
            // use given size directly
            checkboxMode = PDEButtonLayerOverlayCheckboxSizeMode.PDEButtonLayerOverlayCheckboxSizeModeFixed;
            checkboxSize = ((Number)sizeObject).floatValue();
        } else if (sizeObject instanceof String) {
            // extract string
            str = (String)sizeObject;
            // button specific mode / fixed size?
            if (str.equals(PDEButton.PDEButtonParameterValueSizeAuto)
                    || str.equals(PDEButton.PDEButtonParameterValueSizeAutomatic)) {
                // automatic sizing
                checkboxMode = PDEButtonLayerOverlayCheckboxSizeMode.PDEButtonLayerOverlayCheckboxSizeModeAutomatic;
                checkboxSize = 0.0f;
            } else if (str.equals(PDEButton.PDEButtonParameterValueSizeStyleguide)) {
                // styleguide defined sizes sizing
                checkboxMode = PDEButtonLayerOverlayCheckboxSizeMode.PDEButtonLayerOverlayCheckboxSizeModeStyleguide;
                checkboxSize = 0.0f;
            } else {
                // not a button specific value -> use float value (2do: BU parsing)
                checkboxMode = PDEButtonLayerOverlayCheckboxSizeMode.PDEButtonLayerOverlayCheckboxSizeModeFixed;
                checkboxSize = PDEBuildingUnits.parseSize(str);
            }
        } else {
            // nothing set -> use default
            checkboxMode = PDEButtonLayerOverlayCheckboxSizeMode.PDEButtonLayerOverlayCheckboxSizeModeStyleguide;
            checkboxSize = 0.0f;
        }

        // changed?
        if (checkboxMode == mCheckboxSizeMode
                && checkboxSize == mCheckboxSize
                && height == mHeightUsedForSizeCalculation) {
            return;
        }

        // remember
        mCheckboxSizeMode = checkboxMode;
        mCheckboxSize = checkboxSize;
        mHeightUsedForSizeCalculation = height;

        // update size, and relayout
        updateSize(height);
        relayout();
    }


    /**
     * @brief Prepare HorizontalPadding.
     */
    private void prepareHorizontalPadding() {
        int distance;

        distance = mParameters.parameterIntForName(PDEButton.PDEButtonParameterHorizontalPadding,
                PDEBuildingUnits.pixelFromBU(2.0f));

        // anything to do??
        if (distance == mHorizontalPadding) return;

        // remember
        mHorizontalPadding = distance;

        // update
        relayout();
    }


//----- fixed parameters -----------------------------------------------------------------------------------------------


//----- animated parameter updates -------------------------------------------------------------------------------------


    /**
     * @brief protected function - update all colors.
     *
     * Title colors, shadow colors. All colors are pre-calculated and only animated.
     */
    protected void updateColors() {
        PDEColor color, border;

        // interpolate colors by calling complex logic color interpolation helper
        color = PDEComponentHelpers.interpolateColor(mParamColor, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);
        border = PDEComponentHelpers.interpolateColor(mParamBorderColor, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);

        // set color
        mAreaDrawable.setElementBackgroundColor(color);
        mAreaDrawable.setElementBorderColor(border);
    }


    /**
     * @brief protected function - update state, show Icon if required.
     */
    protected void updateState() {
        float alpha;

        // interpolate colors by calling complex logic color interpolation helper
        alpha = PDEComponentHelpers.interpolateFloat(mParamState, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationStateOnly, null);

        PDEUtils.setViewAlpha(mIconLayer, alpha);
    }


    /**
     * @brief protected function - calculate the new size and apply it.
     *
     * The layouting positions the graphical element; the sizing of red dot and sunken layer is done here.
     */
    protected void updateSize(float height) {
        Context context = PDECodeLibrary.getInstance().getApplicationContext();
        int resourceID;
        float checkboxSize;
        String iconFile;

        // commonly used data
        //height = mLayout.mLayoutRect.height();
        //height = 0.0f; //TO BE REMOVED

        //which mode?
        if (mCheckboxSizeMode == PDEButtonLayerOverlayCheckboxSizeMode.PDEButtonLayerOverlayCheckboxSizeModeAutomatic) {
            // height is 2/3 button size
            checkboxSize = PDEBuildingUnits.roundToScreenCoordinates(height * 2.0f / 3.0f);
        } else if (mCheckboxSizeMode
                    == PDEButtonLayerOverlayCheckboxSizeMode.PDEButtonLayerOverlayCheckboxSizeModeStyleguide) {
            // standardized heights: buttons defined are 4BU, 3BU and 2.5BU; use the nearest one to calculate
            if (height > PDEBuildingUnits.exactPixelFromBU(3.5f) ) {
                // display height > 3,5*BU -> use 4 BU as reference size
                checkboxSize = PDEBuildingUnits.roundToScreenCoordinates(
                        PDEBuildingUnits.pixelFromBU(4.0f) * 2.0f / 3.0f);
            } else if (height > PDEBuildingUnits.exactPixelFromBU(2.75f) ) {
                // 2,75*BU < display height <= 3,5*BU -> use 3 BU as reference size
                checkboxSize = PDEBuildingUnits.roundToScreenCoordinates(
                        PDEBuildingUnits.pixelFromBU(3.0f) * 2.0f / 3.0f);
            } else {
                // display height < 2,75*BU -> use 2.5 BU as reference size
                checkboxSize = PDEBuildingUnits.roundToScreenCoordinates(
                        PDEBuildingUnits.pixelFromBU(2.5f) * 2.0f / 3.0f);
            }
        } else if (mCheckboxSizeMode
                    == PDEButtonLayerOverlayCheckboxSizeMode.PDEButtonLayerOverlayCheckboxSizeModeFixed) {
            // fixed height
            checkboxSize = mCheckboxSize;
        } else {
            // unknown mode -> do nothing
            return;
        }

        //determine which Icon to use from checkbox size. We have only two icons (button sizes 3 BU and 4 BU, giving
        //checkbox sizes of 2 and 8/3 BU, with the dividing size in the middle
        if (checkboxSize <= PDEBuildingUnits.exactPixelFromBU(7.0f / 3.0f) ) {
            iconFile = "checkmark_light_m.png";
        } else {
            iconFile = "checkmark_light_l.png";
        }

        //compare with last value, stop if not changed
        if (iconFile.equals(mIconFile) && checkboxSize == mCheckboxUsedSize) {
            return;
        }

        // remember
        mCheckboxUsedSize = checkboxSize;
        mIconFile = iconFile;

        // load the Icon
//        resourceID = R.drawable.checkmark_light_m;//context.getResources().getIdentifier(mIconFile, "drawable",context.getPackageName() );
        // this seems not to work -> workaround
        if(mIconFile.equals("checkmark_light_m.png")){
            resourceID = R.drawable.checkmark_light_m;
        } else {
            resourceID = R.drawable.checkmark_light_l;
        }
        mIcon = context.getResources().getDrawable(resourceID);


        //directly apply to the layers
//        PDEAbsoluteLayoutHelper.setViewRect(mSunkenDrawable.getWrapperView(),new Rect(0,0,Math.round(mCheckboxUsedSize),
//                                                    Math.round(mCheckboxUsedSize)));
//        mSunkenDrawable.setElementShapeRoundedRect(PDEBuildingUnits.oneThirdBU());

        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG,"mSunkenDrawable.setElementShapeRoundedRect "+mCheckboxUsedSize);
        }

        // set Icon and bounds
        if (mIcon != null) {
            mIconLayer.setImageDrawable(mIcon);
        } else {
            mIconLayer.setImageDrawable(null);
        }
    }


//----- layout ---------------------------------------------------------------------------------------------------------


    /**
     * @brief Relayout.
     *
     * Use the stored layout. This is not correct since it does not update dependent layers. When we have change
     * management we must trigger a complete button layout here.
     */
    protected void relayout() {
        ViewGroup innerLayout = null;
        ViewGroup overlaySlot;

        overlaySlot = (ViewGroup) getParent();

        if (overlaySlot != null && overlaySlot.getParent() != null) {
            innerLayout = (ViewGroup) overlaySlot.getParent().getParent();
        }

        // check alignment, adjusts layout for further children
        if (mCheckboxAlignment == PDEConstants.PDEAlignment.PDEAlignmentLeft) {
            if (overlaySlot != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_left) {
                // remove from old slot
                overlaySlot.removeView(this);
            }

            if (innerLayout != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_left) {
                ((ViewGroup)innerLayout.findViewById(R.id.pdebutton_overlay_slot_left))
                        .addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                  ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else if (mCheckboxAlignment == PDEConstants.PDEAlignment.PDEAlignmentCenter) {
            if (overlaySlot != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_center) {
                // remove from old slot
                overlaySlot.removeView(this);
            }

            if (innerLayout != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_center) {
                ((ViewGroup)innerLayout.findViewById(R.id.pdebutton_overlay_slot_center))
                        .addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                  ViewGroup.LayoutParams.MATCH_PARENT));
            }

        } else if (mCheckboxAlignment == PDEConstants.PDEAlignment.PDEAlignmentRight) {
            if (overlaySlot != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_right) {
                // remove from old slot
                overlaySlot.removeView(this);
            }

            if (innerLayout != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_right) {
                ((ViewGroup)innerLayout.findViewById(R.id.pdebutton_overlay_slot_right))
                        .addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                  ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
    }


    @Override
    public void collectHints(PDEDictionary hints) {
        // no hint handling needed in this layer
    }


    @Override
    public void setHints(PDEDictionary hints) {
        // no hint handling needed in this layer
    }


    @Override
    public void collectButtonPaddingRequest(PDEButtonPadding padding) {

    }


    /**
     * @brief Determine layout size of element.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        int width;

        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "onMeasure " + MeasureSpec.toString(widthMeasureSpec) + " x "
                    + MeasureSpec.toString(heightMeasureSpec));
        }

        // measure the children (otherwise e.g. the sunken-layer will not be visible!)
        measureChildren(widthMeasureSpec,heightMeasureSpec);

        // take the height from the parameter ...
        height = MeasureSpec.getSize(heightMeasureSpec);

        // .. and use it to calculate the sizes
        prepareSize(height);

        // now mCheckboxUsedSize contains the size (width = height) of the checkbox background
        width = PDEBuildingUnits.roundToScreenCoordinates(mHorizontalPadding + mCheckboxUsedSize);

        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "onMeasure result: " + resolveSize(width, widthMeasureSpec) + " x "
                    + resolveSize(height, heightMeasureSpec) + " mCheckboxUsedSize:" + mCheckboxUsedSize);
        }

        // return the values
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                             resolveSize(height, heightMeasureSpec));
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
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        float xOffset;
        float yOffset;
        float checkboxX;
        float checkboxY;
        float iconX;
        float iconY;
        ViewGroup innerLayout = null;
        ViewGroup overlaySlot;

        // commonly used data (adjusted for our special clipping logic)
        //xOffset = layout.mLayoutRect.left - layout.mClipRect.left;
        //yOffset = layout.mLayoutRect.top - layout.mClipRect.top;
        xOffset = 0;
        yOffset = 0;

        updateSize(height);

        // center checkbox in Y, round to screen
        checkboxY = yOffset + PDEBuildingUnits.roundToScreenCoordinates((height - mCheckboxUsedSize) / 2.0f);

        overlaySlot = (ViewGroup) getParent();

        if (overlaySlot != null) {
            innerLayout = (ViewGroup) overlaySlot.getParent();
        }

        // check alignment, adjusts layout for further children
        if (mCheckboxAlignment == PDEConstants.PDEAlignment.PDEAlignmentLeft) {
            if (overlaySlot != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_left) {
                // remove from old slot
                overlaySlot.removeView(this);
            }

            if (innerLayout != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_left) {
                ((ViewGroup)innerLayout.findViewById(R.id.pdebutton_overlay_slot_left))
                        .addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                  ViewGroup.LayoutParams.MATCH_PARENT));
            }

            // left, one BU from border
            checkboxX = mHorizontalPadding;
        } else if (mCheckboxAlignment == PDEConstants.PDEAlignment.PDEAlignmentCenter) {
            if (overlaySlot != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_center) {
                // remove from old slot
                overlaySlot.removeView(this);
            }

            if (innerLayout != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_center) {
                ((ViewGroup)innerLayout.findViewById(R.id.pdebutton_overlay_slot_center))
                        .addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                  ViewGroup.LayoutParams.MATCH_PARENT));
            }

            // centered, aligned to screen pixels
            checkboxX = xOffset + PDEBuildingUnits.roundToScreenCoordinates((width - mCheckboxUsedSize) / 2.0f);

        } else if (mCheckboxAlignment == PDEConstants.PDEAlignment.PDEAlignmentRight) {
            if (overlaySlot != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_right) {
                // remove from old slot
                overlaySlot.removeView(this);
            }
            if (innerLayout != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_right) {
                ((ViewGroup)innerLayout.findViewById(R.id.pdebutton_overlay_slot_right))
                        .addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                  ViewGroup.LayoutParams.MATCH_PARENT));
            }

            // right, one BU from border
            checkboxX = 0.0f;
        } else {
            // unknown, default
            checkboxX = 0.0f;
        }

        // set positions
        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "mCheckboxUsedSize: "+mCheckboxUsedSize);
        }
        PDEAbsoluteLayoutHelper.setViewRect(mAreaDrawable.getWrapperView(), new Rect(Math.round(checkboxX),
                                                            Math.round(checkboxY),
                                                            Math.round(mCheckboxUsedSize) + Math.round(checkboxX),
                                                            Math.round(mCheckboxUsedSize) + Math.round(checkboxY)));

        mAreaDrawable.getWrapperView().measure(MeasureSpec.makeMeasureSpec(Math.round(mCheckboxUsedSize),
                        MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(Math.round(mCheckboxUsedSize), MeasureSpec.EXACTLY));

        PDEAbsoluteLayout.LayoutParams iconLayerParams = (PDEAbsoluteLayout.LayoutParams) mIconLayer.getLayoutParams();

        if (iconLayerParams != null) {
            // measure to get width & height of Icon correctly
            mIconLayer.measure(0, 0);
            // Icon X centered inside checkbox, round to screen
            iconX = checkboxX
                    + PDEBuildingUnits.roundToScreenCoordinates(
                    (mCheckboxUsedSize - mIconLayer.getMeasuredWidth()) / 2.0f);
            // center Icon Y inside checkbox, round to screen
            iconY = checkboxY
                    + PDEBuildingUnits.roundToScreenCoordinates(
                    (mCheckboxUsedSize - mIconLayer.getMeasuredHeight()) / 2.0f);
            iconLayerParams.x = PDEBuildingUnits.roundToScreenCoordinates(iconX);
            iconLayerParams.y = PDEBuildingUnits.roundToScreenCoordinates(iconY);

            // also width & height???
            mIconLayer.setLayoutParams(iconLayerParams);
        }
    }


    /**
     * @brief Factory function that decides which background we use.
     *
     */
    protected abstract PDEDrawableArea createDrawableArea();
}
