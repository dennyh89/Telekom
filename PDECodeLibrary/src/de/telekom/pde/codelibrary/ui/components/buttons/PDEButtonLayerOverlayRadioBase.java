/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;




//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerOverlayRadioBase
//----------------------------------------------------------------------------------------------------------------------


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
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
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShape;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.PDEDictionary;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;


/**
 * @brief Background for an Checkbox button.
 */

public abstract class PDEButtonLayerOverlayRadioBase extends PDEAbsoluteLayout implements PDEButtonLayerInterface {


    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEButtonLayerOverlayRadioBase.class.getName();
    // debug messages switch
    private final static boolean DEBUGPARAMS = false;


    // size modes
    enum PDEButtonLayerOverlayRadioSizeMode {
        PDEButtonLayerOverlayRadioSizeModeUndefined,
        PDEButtonLayerOverlayRadioSizeModeAutomatic,
        PDEButtonLayerOverlayRadioSizeModeStyleguide,
        PDEButtonLayerOverlayRadioSizeModeFixed
    }


    // parameters needed
    protected PDEParameterDictionary mParameters;
    protected PDEParameter mParamColor;
    protected PDEParameter mParamBorderColor;
    protected PDEParameter mParamState;

    // content layers
    protected PDEDrawableArea mAreaDrawable;
    protected PDEDrawableShape mMarkerDrawable;

    // configuration
    protected float mRadioSize;
    protected PDEButtonLayerOverlayRadioSizeMode mRadioSizeMode;
    protected PDEConstants.PDEAlignment mRadioAlignment;

    // configuration
    protected float mRadioUsedSize;
    protected float mMarkerUsedSize;
    protected float mHeightUsedForPrepareSize;
    protected float mHeightUsedForUpdateSize;

    // agent helpers
    protected PDEAgentHelper mAgentHelper;

    // darkstyle
    protected boolean mDarkStyle = false;

    // global variables
    //
    static PDEDictionary PDEButtonLayerOverlayRadioGlobalColorDefault = PDEComponentHelpers.readDefaultColorDictionary("dt_button_check_radio_color_defaults");
    static PDEDictionary PDEButtonLayerOverlayRadioGlobalBorderDefault = PDEComponentHelpers.readDefaultColorDictionary("dt_button_border_color_defaults");

    /**
     * @brief Class initialization.
     */
    public PDEButtonLayerOverlayRadioBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * @brief Class initialization.
     */
    public PDEButtonLayerOverlayRadioBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * @brief Class initialization.
     */
    public PDEButtonLayerOverlayRadioBase(Context context) {
        super (context);
        init();
    }


    protected void init() {
        // init
        mParameters = null;
        mRadioUsedSize = 0.0f;
        mMarkerUsedSize = 0.0f;
        mDarkStyle = PDECodeLibrary.getInstance().isDarkStyle();

        // default configuration
        mRadioAlignment = PDEConstants.PDEAlignment.PDEAlignmentCenter;
        mRadioSizeMode = PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeStyleguide;
        mRadioSize = 0.0f;

        // agent helper
        mAgentHelper = new PDEAgentHelper();

        // create sunken area drawable and add it with it's own wrapper view
        mAreaDrawable = createDrawableArea();
        addView(mAreaDrawable.getWrapperView());

        // create marker circle layer
        mMarkerDrawable = new PDEDrawableShape();
        mMarkerDrawable.setElementBackgroundColor(PDEColor.valueOf("DTMagenta").getIntegerColor());
        addView(mMarkerDrawable.getWrapperView());

        // set empty complex parameters
        mParamColor = new PDEParameter();
        mParamBorderColor = new PDEParameter();
        mParamState = new PDEParameter();

        mHeightUsedForPrepareSize = -1;
        mHeightUsedForUpdateSize = -1;

        // forced set of parameter -> this sets defaults
        setParameters( new PDEParameterDictionary(),true);

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

        // non-animatable properties do their change management internally
        prepareAlignment();
        //prepareSize();
        requestLayout();
    }


//----- animated parameters: preparation -------------------------------------------------------------------------------


    /**
     * @brief Set the (animatable) title color.
     *
     * If no title color is given, the title color is calculated from a) the main color, and b) the hints (indicative
     * buttons have a special color), and c) the system preset (dark or light system).
     *
     * The shadow color is also calculated; the shadow color depends solely on the main color (and does only change
     * inbetween states)
     */
    protected void prepareColor()
    {
        // debug output
        if( DEBUGPARAMS ){
            mParamColor.debugOut("Color before building");
            mParamBorderColor.debugOut("Border before building");
        }

        // basic propagation of colors (no agent states required), convert to colors
        PDEComponentHelpers.fillStateBaseValues(mParamColor, null);
        mParamColor.convertToColor();

        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamColor, PDEButtonLayerOverlayRadioGlobalColorDefault,
                                        PDEColor.valueOf("DTUIInteractive"),
                                        PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamBorderColor, PDEButtonLayerOverlayRadioGlobalBorderDefault, mParamColor,
                                        null, PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // if border color for a state is still not defined, calculate it from main agent colors (use gradient darker step)
        PDEComponentHelpers.fillBorderColors(mParamBorderColor, mParamColor);

        // debug output
        if( DEBUGPARAMS ){
            mParamColor.debugOut("Color after building");
            mParamBorderColor.debugOut("Border after building");
        }

        // and apply once
        updateColors();
    }


    /**
     * @brief Prepare shadow strength parameter set.
     *
     * Shadow strengs animates the shadow as a whole. The range is 0.0..1.0.
     */
    protected void prepareState()
    {
        // set the new values
        mParamState.removeAllObjects();

        // create fixed defaults (old way for old compilers, dictionary literal would be better readable)
        mParamState.setWithDictionary(new PDEDictionary("default","0.0","selected","1.0"));

        // debug output
        if( DEBUGPARAMS ){
            mParamState.debugOut("State before building");
        }

        // calculate agent states
        PDEComponentHelpers.buildValues(mParamState, null, null, PDEAgentHelper.PDEAgentHelperAnimationStateOnly);

        // and convert to number
        mParamState.convertToNumber();

        // debug output
        if( DEBUGPARAMS ){
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
    protected void prepareAlignment()
    {
        String alignmentString;
        PDEConstants.PDEAlignment alignment;

        // get the title from the parameters
        alignmentString = mParameters.parameterValueForNameWithDefault(PDEButton.PDEButtonParameterRadioAlignment, PDEConstants.PDEAlignmentStringLeft);

        // parse value
        if ( alignmentString.equals(PDEConstants.PDEAlignmentStringLeft) ) alignment = PDEConstants.PDEAlignment.PDEAlignmentLeft;
        else if ( alignmentString.equals(PDEConstants.PDEAlignmentStringCenter) ) alignment = PDEConstants.PDEAlignment.PDEAlignmentCenter;
        else if ( alignmentString.equals(PDEConstants.PDEAlignmentStringRight) ) alignment = PDEConstants.PDEAlignment.PDEAlignmentRight;
        else alignment = PDEConstants.PDEAlignment.PDEAlignmentLeft;

        // any change?
        if (alignment == mRadioAlignment) return;

        // remember
        mRadioAlignment = alignment;

        // relayout
        relayout();
    }


    /**
     * @brief protected function - prepare the font.
     *
     * Preparation function for the font which evaluates the parameters. Note that we need to
     */
    protected void prepareSize(float height)
    {
        Object sizeObject;
        PDEButtonLayerOverlayRadioSizeMode radioMode;
        float radioSize;
        String str;

        // get font parameters
        sizeObject = mParameters.parameterObjectForName(PDEButton.PDEButtonParameterFontSize);

        // parse font size
        if ( sizeObject instanceof Number ) {
            // use given size directly
            radioMode = PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeFixed;
            radioSize = ((Number)sizeObject).floatValue();
        } else if ( sizeObject instanceof String ) {
            // extract string
            str = (String)sizeObject;
            // button specific mode / fixed size?
            if ( str.equals(PDEButton.PDEButtonParameterValueSizeAuto) || str.equals(PDEButton.PDEButtonParameterValueSizeAutomatic) ) {
                // automatic sizing
                radioMode = PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeAutomatic;
                radioSize = 0.0f;
            } else if ( str.equals(PDEButton.PDEButtonParameterValueSizeStyleguide)) {
                // styleguide defined sizes sizing
                radioMode = PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeStyleguide;
                radioSize = 0.0f;
            } else {
                // not a button specific value -> use float value (2do: BU parsing)
                radioMode = PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeFixed;
                radioSize = PDEBuildingUnits.parseSize(str);
            }
        } else {
            // nothing set -> use default
            radioMode = PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeStyleguide;
            radioSize = 0.0f;
        }

        // changed?
        if (radioMode == mRadioSizeMode && radioSize == mRadioSize && mHeightUsedForPrepareSize == height) return;

        // remember
        mRadioSizeMode = radioMode;
        mRadioSize = radioSize;
        mHeightUsedForPrepareSize = height;

        // update size, and relayout
        updateSize(height);
        relayout();
    }


//----- fixed parameters -----------------------------------------------------------------------------------------------


//----- animated parameter updates -------------------------------------------------------------------------------------


    /**
     * @brief protected function - update all colors.
     *
     * Title colors, shadow colors. All colors are precalculated and only animated.
     */
    protected void updateColors()
    {
        PDEColor color,border;

        // interpolate colors by calling complex logic color interpolation helper
        color = PDEComponentHelpers.interpolateColor(mParamColor, mAgentHelper, PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);
        border = PDEComponentHelpers.interpolateColor(mParamBorderColor, mAgentHelper, PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);

        // set color
        mAreaDrawable.setElementBackgroundColor(color);
        mAreaDrawable.setElementBorderColor(border);
    }


    /**
     * @brief protected function - update state, show marker if required.
     */
    protected void updateState()
    {
        float alpha;

        // interpolate colors by calling complex logic color interpolation helper
        alpha = PDEComponentHelpers.interpolateFloat(mParamState, mAgentHelper, PDEAgentHelper.PDEAgentHelperAnimationStateOnly, null);

        // show/hide marker
        mMarkerDrawable.setElementShapeOpacity(alpha);
    }


    /**
     * @brief protected function - calculate the new size and apply it.
     *
     * The layouting positions the graphical element; the sizing of red dot and sunken layer is done here.
     */
    protected void updateSize(float height)
    {
        if (DEBUGPARAMS) {
            Log.d(LOG_TAG, "updateSize(" + height + ")");
        }
        float radioSize,markerSize;

        // which mode?
        if (mRadioSizeMode == PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeAutomatic) {
            // marker height is 1 third of button height, rounded; outline height is double this size
            markerSize = PDEBuildingUnits.roundToScreenCoordinates(height/3.0f);
            radioSize = markerSize * 2.0f;
        } else if (mRadioSizeMode == PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeStyleguide) {
            // standardized heights: buttons defined are 4BU, 3BU and 2.5BU; use the nearest one to calculate
            if (height > PDEBuildingUnits.exactPixelFromBU(3.5f)) {
                // display height > 3,5*BU -> use 4 BU as reference size
                markerSize = PDEBuildingUnits.roundToScreenCoordinates(PDEBuildingUnits.pixelFromBU(4.0f)/3.0f);
                radioSize = markerSize * 2.0f;
            } else if (height > PDEBuildingUnits.exactPixelFromBU(2.75f)) {
                // 2,75*BU < display height <= 3,5*BU -> use 3 BU as reference size
                markerSize = PDEBuildingUnits.roundToScreenCoordinates(PDEBuildingUnits.pixelFromBU(3.0f)/3.0f);
                radioSize = markerSize * 2.0f;
            } else {
                // display height < 2,75*BU -> use 2.5 BU as reference size
                markerSize = PDEBuildingUnits.roundToScreenCoordinates(PDEBuildingUnits.pixelFromBU(2.5f)/3.0f);
                radioSize = markerSize * 2.0f;
            }
        } else if (mRadioSizeMode == PDEButtonLayerOverlayRadioSizeMode.PDEButtonLayerOverlayRadioSizeModeFixed) {
            // fixed height, marker is half the height
            radioSize = mRadioSize;
            markerSize = radioSize / 2.0f;
        } else {
            // unknown mode -> do nothing
            return;
        }

        // compare with last value, stop if not changed
        if (markerSize == mMarkerUsedSize && radioSize == mRadioUsedSize && height == mHeightUsedForUpdateSize) return;

        if (DEBUGPARAMS) {
            Log.d(LOG_TAG, "updateSize(" + height + ") - with changes");
        }

        // remember
        mRadioUsedSize = radioSize;
        mMarkerUsedSize = markerSize;
        mHeightUsedForUpdateSize = height;


    }


//----- layout ---------------------------------------------------------------------------------------------------------


    /**
     * @brief Relayout.
     *
     * Use the stored layout. This is not correct since it does not update dependent layers. When we have change
     * management we must trigger a complete button layout here.
     */
    protected void relayout()
    {
        ViewGroup innerLayout = null;
        ViewGroup overlaySlot = null;

        overlaySlot = (ViewGroup) getParent();

        if (overlaySlot != null) {
            innerLayout = (ViewGroup) overlaySlot.getParent().getParent();
        }

        // check alignment, adjusts layout for further children
        if (mRadioAlignment == PDEConstants.PDEAlignment.PDEAlignmentLeft) {
            if (overlaySlot != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_left) {
                // remove from old slot
                overlaySlot.removeView(this);
            }

            if (innerLayout != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_left) {
                ((ViewGroup)innerLayout.findViewById(R.id.pdebutton_overlay_slot_left)).addView(this,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else if (mRadioAlignment == PDEConstants.PDEAlignment.PDEAlignmentCenter) {
            if (overlaySlot != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_center) {
                // remove from old slot
                overlaySlot.removeView(this);
            }

            if (innerLayout != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_center) {
                ((ViewGroup)innerLayout.findViewById(R.id.pdebutton_overlay_slot_center)).addView(this,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

        } else if (mRadioAlignment == PDEConstants.PDEAlignment.PDEAlignmentRight) {
            if (overlaySlot != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_right) {
                // remove from old slot
                overlaySlot.removeView(this);
            }

            if (innerLayout != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_right) {
                ((ViewGroup)innerLayout.findViewById(R.id.pdebutton_overlay_slot_right)).addView(this,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
    }

    @Override
    public void collectHints(PDEDictionary hints) {
        // hints are not needed in this element
    }

    @Override
    public void setHints(PDEDictionary hints) {
        // hints are not needed in this element
    }

    @Override
    public void collectButtonPaddingRequest(PDEButtonPadding padding) {
        // no padding in this element
    }


    //----- view layout ------------------------------------------------------------------------------------------------


    /**
     * @brief Determine layout size of element.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        int width;

        // measure the children (otherwise e.g. the sunkenlayer will not be visible!)
        measureChildren(widthMeasureSpec,heightMeasureSpec);

        // take the height from the parameter ...
        height = MeasureSpec.getSize(heightMeasureSpec);

        // .. and use it to calculate the sizes
        prepareSize(height);

        // now mRadioUsedSize contains the size (width = height) of the checkbox background
        width = PDEBuildingUnits.roundToScreenCoordinates(PDEBuildingUnits.BU() + mRadioUsedSize);

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
        float xOffset,yOffset,radioX,radioY,markerX,markerY;
        if (DEBUGPARAMS) {
            Log.d(LOG_TAG, "onSizeChanged("+width+","+height+","+oldWidth+","+oldHeight+")");
        }

        super.onSizeChanged(width, height, oldWidth, oldHeight);


        // commonly used data (adjusted for our special clipping logic)
        xOffset = 0;
        yOffset = 0;

        updateSize(height);

        // center checkbox in Y, round to screen
        radioY = yOffset + PDEBuildingUnits.roundToScreenCoordinates((height - mRadioUsedSize) / 2.0f);

        ViewGroup innerLayout = null;
        ViewGroup overlaySlot = null;

        overlaySlot = (ViewGroup) getParent();

        if (overlaySlot != null) {
            innerLayout = (ViewGroup) overlaySlot.getParent();
        }

        // check alignment, adjusts layout for further children
        if (mRadioAlignment == PDEConstants.PDEAlignment.PDEAlignmentLeft) {
            // left, one BU from border

            if (overlaySlot != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_left) {
                // remove from old slot
                overlaySlot.removeView(this);
            }

            if (innerLayout != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_left) {
                ((ViewGroup)innerLayout.findViewById(R.id.pdebutton_overlay_slot_left)).addView(this,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            // left, one BU from border
            radioX = PDEBuildingUnits.BU();

        } else if (mRadioAlignment == PDEConstants.PDEAlignment.PDEAlignmentCenter) {
            // centered, aligned to screen pixels


            if (overlaySlot != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_center) {
                // remove from old slot
                overlaySlot.removeView(this);
            }

            if (innerLayout != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_center) {
                ((ViewGroup)innerLayout.findViewById(R.id.pdebutton_overlay_slot_center)).addView(this,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }


            // centered, aligned to screen pixels
            radioX = xOffset + PDEBuildingUnits.roundToScreenCoordinates((width - mRadioUsedSize) / 2.0f);

        } else if (mRadioAlignment == PDEConstants.PDEAlignment.PDEAlignmentRight) {
            // right, one BU from border
            if (overlaySlot != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_right) {
                // remove from old slot
                overlaySlot.removeView(this);
            }

            if (innerLayout != null && overlaySlot.getId() != R.id.pdebutton_overlay_slot_right) {
                ((ViewGroup)innerLayout.findViewById(R.id.pdebutton_overlay_slot_right)).addView(this,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            // right, one BU from border
            radioX = 0.0f;
        } else {
            // unknown, default
            radioX = 0.0f;
        }

        // directly apply to the layers
        mAreaDrawable.getWrapperView().setViewLayoutRect(new Rect(0, 0, Math.round(mRadioUsedSize),
                                                                    Math.round(mRadioUsedSize)));
        mAreaDrawable.setElementShapeOval();

        if (DEBUGPARAMS) {
            Log.d(LOG_TAG, "onSizeChanged markerUsedSize: "+mMarkerUsedSize + " radioUsedSize: "+mRadioUsedSize);
        }

        // create new shape
        mMarkerDrawable.setElementShapeOval();
        mMarkerDrawable.getWrapperView().setViewLayoutRect(new Rect(0 , 0, Math.round(mMarkerUsedSize),
                                                                    Math.round(mMarkerUsedSize)));
        mMarkerDrawable.getWrapperView().measure(MeasureSpec.makeMeasureSpec(Math.round(mMarkerUsedSize), MeasureSpec.EXACTLY),
                                                 MeasureSpec.makeMeasureSpec(Math.round(mMarkerUsedSize), MeasureSpec.EXACTLY));

        // set positions
        mAreaDrawable.getWrapperView().setViewOffset(radioX,radioY);
        mAreaDrawable.getWrapperView().measure(MeasureSpec.makeMeasureSpec(
                mAreaDrawable.getWrapperView().getLayoutParams().width, MeasureSpec.EXACTLY),
                                                 MeasureSpec.makeMeasureSpec(mAreaDrawable.getWrapperView().getLayoutParams().height, MeasureSpec.EXACTLY) );

        PDEAbsoluteLayout.LayoutParams markerLayerParams =
                (PDEAbsoluteLayout.LayoutParams) mMarkerDrawable.getWrapperView().getLayoutParams();
        // center marker Y inside radio, round to screen
        markerY = radioY + PDEBuildingUnits.roundToScreenCoordinates((mRadioUsedSize-mMarkerUsedSize)/2.0f);
        // marker X centered inside radio, round to screen
        markerX = radioX + PDEBuildingUnits.roundToScreenCoordinates((mRadioUsedSize-mMarkerUsedSize)/2.0f);

        markerLayerParams.x = PDEBuildingUnits.roundToScreenCoordinates(markerX);
        markerLayerParams.y = PDEBuildingUnits.roundToScreenCoordinates(markerY);
        // also width & height???
        mMarkerDrawable.getWrapperView().setLayoutParams(markerLayerParams);
    }



    /**
     * @brief Factory function that decides which background we use.
     *
     */
    protected abstract PDEDrawableArea createDrawableArea();

}
