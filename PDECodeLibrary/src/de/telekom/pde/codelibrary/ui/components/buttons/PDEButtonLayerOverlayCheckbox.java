/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;




//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerOverlayCheckbox
//----------------------------------------------------------------------------------------------------------------------


import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonLayoutHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEDictionary;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.common.PDELayerSunken;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @brief Background for an Checkbox button.
 */
public class PDEButtonLayerOverlayCheckbox extends Object implements PDEButtonLayerInterface {


    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEButtonLayerOverlayCheckbox.class.getName();
    // debug messages switch
    private final static boolean DEBUGPARAMS = false;


    // size modes
    enum PDEButtonLayerOverlayCheckboxSizeMode {
        PDEButtonLayerOverlayCheckboxSizeModeUndefined,
        PDEButtonLayerOverlayCheckboxSizeModeAutomatic,
        PDEButtonLayerOverlayCheckboxSizeModeStyleguide,
        PDEButtonLayerOverlayCheckboxSizeModeFixed
    } ;

    // global variables
    //
    static PDEDictionary PDEButtonLayerOverlayCheckboxGlobalColorDefault = null;
    static PDEDictionary PDEButtonLayerOverlayCheckboxGlobalBorderDefault = null;

    // parameters needed
    private PDEParameterDictionary mParameters;
    private PDEParameter mParamColor;
    private PDEParameter mParamBorderColor;
    private PDEParameter mParamState;

    // content layers
    private PDELayerSunken mSunkenLayer;
    private ImageView mIconLayer;

    // configuration
    private float mCheckboxSize;
    private PDEButtonLayerOverlayCheckboxSizeMode mCheckboxSizeMode;
    private PDEConstants.PDEAlignment mCheckboxAlignment;

    // internal configuration
    private float mCheckboxUsedSize;
    private String mIconFile;
    private Drawable mIcon;

    // layout info
    private PDEButtonLayoutHelper mLayout;

    // agent helpers
    private PDEAgentHelper mAgentHelper;


    // the layer
    protected PDEAbsoluteLayout mLayer = null;

    private static Method sImageViewSetAlphaMethod = null;


    /**
     * @brief Class initialization.
     */
    PDEButtonLayerOverlayCheckbox(Context context) {
        // read default dictionaries
        PDEButtonLayerOverlayCheckboxGlobalColorDefault = PDEComponentHelpers.readDefaultColorDictionary("dt_button_flat_color_defaults");
        PDEButtonLayerOverlayCheckboxGlobalBorderDefault = PDEComponentHelpers.readDefaultColorDictionary("dt_button_border_color_defaults");

        // init
        mParameters = null;
        mCheckboxUsedSize = 0.0f;
        mIcon = null;

        mLayout = new PDEButtonLayoutHelper();

        // default configuration
        mCheckboxAlignment = PDEConstants.PDEAlignment.PDEAlignmentCenter;
        mCheckboxSizeMode = PDEButtonLayerOverlayCheckboxSizeMode.PDEButtonLayerOverlayCheckboxSizeModeStyleguide;
        mCheckboxSize = 0.0f;

        // create the layer
        mLayer = new PDEAbsoluteLayout(context);
        //mLayer.masksToBounds = YES;

        // agent helper
        mAgentHelper = new PDEAgentHelper();

        // create sunken layer
        mSunkenLayer = new PDELayerSunken(PDECodeLibrary.getInstance().getApplicationContext());
        mLayer.addView(mSunkenLayer);

        // create icon layer
        mIconLayer = new ImageView(context);
        mLayer.addView(mIconLayer);

        // set empty complex parameters
        mParamColor = new PDEParameter();
        mParamBorderColor = new PDEParameter();
        mParamState = new PDEParameter();

        // forced set of parameter -> this sets defaults
        setParameters(new PDEParameterDictionary(),true);
    }

    //----- basic functions ------------------------------------------------------------------------------------------------


    /**
     * @brief Layer access.
     */
    @Override
    public PDEAbsoluteLayout getLayer() {
        return mLayer;
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
        PDEParameterDictionary oldParams;

        // for change management, keep around the old params
        oldParams = mParameters;

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
        prepareSize();
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
    private void prepareColor()
    {
        // create fixed defaults (old way for old compilers, dictionary literal would be better readable)
        mParamColor.removeAllObjects();
        mParamColor.setBaseValue("DTWhite");
        mParamBorderColor.removeAllObjects();

        // debug output
        if( DEBUGPARAMS ){
            mParamColor.debugOut("Color before building");
            mParamBorderColor.debugOut("Border before building");
        }

        // basic propagation of colors (no agent states required), convert to colors
        PDEComponentHelpers.fillStateBaseValues(mParamColor, null);
        mParamColor.convertToColor();

        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamColor, PDEButtonLayerOverlayCheckboxGlobalColorDefault, null, PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamBorderColor, PDEButtonLayerOverlayCheckboxGlobalBorderDefault, mParamColor, null, PDEAgentHelper.PDEAgentHelperAnimationInteractive);

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
    private void prepareState()
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
     * @brief Private function - prepare internal layout.
     *
     * IconOnRightSide and IconTextAlignment are non-animated and uses the base parameter
     */
    private void prepareAlignment()
    {
        String alignmentString;
        PDEConstants.PDEAlignment alignment;

        // get the title from the parameters
        alignmentString = mParameters.parameterValueForNameWithDefault(PDEButton.PDEButtonParameterCheckboxAlignment, PDEConstants.PDEAlignmentStringLeft);

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
     * @brief: Private function - prepare the font.
     *
     * Preparation function for the font which evaluates the parameters. Note that we need to
     */
    private void prepareSize()
    {
        Object sizeObject;
        PDEButtonLayerOverlayCheckboxSizeMode checkboxMode;
        float checkboxSize;
        String str;

        // get font parameters
        sizeObject = mParameters.parameterObjectForName(PDEButton.PDEButtonParameterFontSize);

        // parse font size
        if ( sizeObject instanceof  Number ) {
            // use given size directly
            checkboxMode = PDEButtonLayerOverlayCheckboxSizeMode.PDEButtonLayerOverlayCheckboxSizeModeFixed;
            checkboxSize = ((Number)sizeObject).floatValue();
        } else if ( sizeObject instanceof String ) {
            // extract string
            str = (String)sizeObject;
            // button specific mode / fixed size?
            if ( str.equals(PDEButton.PDEButtonParameterValueSizeAuto) || str.equals(PDEButton.PDEButtonParameterValueSizeAutomatic) ) {
                // automatic sizing
                checkboxMode = PDEButtonLayerOverlayCheckboxSizeMode.PDEButtonLayerOverlayCheckboxSizeModeAutomatic;
                checkboxSize = 0.0f;
            } else if ( str.equals(PDEButton.PDEButtonParameterValueSizeStyleguide) ) {
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
        if (checkboxMode == mCheckboxSizeMode && checkboxSize == mCheckboxSize) {
            return;
        }

        // remember
        mCheckboxSizeMode = checkboxMode;
        mCheckboxSize = checkboxSize;

        // update size, and relayout
        updateSize();
        relayout();
    }


//----- fixed parameters -----------------------------------------------------------------------------------------------


//----- animated parameter updates -------------------------------------------------------------------------------------


    /**
     * @brief Private function - update all colors.
     *
     * Title colors, shadow colors. All colors are precalculated and only animated.
     */
    private void updateColors()
    {
        PDEColor color,border;

        // interpolate colors by calling complex logic color interpolation helper
        color = PDEComponentHelpers.interpolateColor(mParamColor, mAgentHelper, PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);
        border = PDEComponentHelpers.interpolateColor(mParamBorderColor, mAgentHelper, PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);

        // set color
        mSunkenLayer.setSunkenBackgroundColor(color);
        mSunkenLayer.setSunkenBorderColor(border);
    }


    /**
     * @brief Private function - update state, show icon if required.
     */
    private void updateState()
    {
        float alpha;
        Method method = null;

        // interpolate colors by calling complex logic color interpolation helper
        alpha = PDEComponentHelpers.interpolateFloat(mParamState, mAgentHelper, PDEAgentHelper.PDEAgentHelperAnimationStateOnly, null);

        // show/hide icon
        // since the setAlpha(int) method is deprecated in API 16 we go by reflection.
        if (sImageViewSetAlphaMethod == null) {
            try {
                method = mIconLayer.getClass().getMethod("setImageAlpha",new Class[] {int.class});
            } catch (NoSuchMethodException e) {
                try {
                    method = mIconLayer.getClass().getMethod("setAlpha",new Class[] {int.class});
                } catch (NoSuchMethodException e2) {
                    //that should never happen!
                    Log.e(LOG_TAG,"no method for setting alpha for ImageView");
                }
            }
            sImageViewSetAlphaMethod = method;
        }

        if (sImageViewSetAlphaMethod != null) {
            try {
                sImageViewSetAlphaMethod.invoke(mIconLayer,Integer.valueOf((int)alpha*255));
            } catch (IllegalAccessException e) {
                Log.e(LOG_TAG,"can't invoke method for changing alpha of imageView - IllegalAccessException");
            } catch (InvocationTargetException e) {
                Log.e(LOG_TAG,"can't invoke method for changing alpha of imageView - InvocationTargetException");
            }
        }
    }


    /**
     * @brief Private function - calculate the new size and apply it.
     *
     * The layouting positions the graphical element; the sizing of red dot and sunken layer is done here.
     */
    private void updateSize()
    {
        Context context = PDECodeLibrary.getInstance().getApplicationContext();
        int resourceID = 0;
        float height,checkboxSize;
        String iconFile;

        // commonly used data
        height = mLayout.mLayoutRect.height();
        //height = 0.0f; //TO BE REMOVED

        //which mode?
        if (mCheckboxSizeMode == PDEButtonLayerOverlayCheckboxSizeMode.PDEButtonLayerOverlayCheckboxSizeModeAutomatic) {
            // height is 2/3 button size
            checkboxSize = PDEBuildingUnits.roundToScreenCoordinates(height * 2.0f / 3.0f);
        } else if (mCheckboxSizeMode == PDEButtonLayerOverlayCheckboxSizeMode.PDEButtonLayerOverlayCheckboxSizeModeStyleguide) {
            // standardized heights: buttons defined are 4BU, 3BU and 2.5BU; use the nearest one to calculate
            if (height > PDEBuildingUnits.exactPixelFromBU(3.5f) ) {
                // display height > 3,5*BU -> use 4 BU as reference size
                checkboxSize = PDEBuildingUnits.roundToScreenCoordinates(PDEBuildingUnits.pixelFromBU(4.0f) * 2.0f / 3.0f);
            } else if (height > PDEBuildingUnits.exactPixelFromBU(2.75f) ) {
                // 2,75*BU < display height <= 3,5*BU -> use 3 BU as reference size
                checkboxSize = PDEBuildingUnits.roundToScreenCoordinates(PDEBuildingUnits.pixelFromBU(3.0f) * 2.0f / 3.0f);
            } else {
                // display height < 2,75*BU -> use 2.5 BU as reference size
                checkboxSize = PDEBuildingUnits.roundToScreenCoordinates(PDEBuildingUnits.pixelFromBU(2.5f) * 2.0f / 3.0f);
            }
        } else if (mCheckboxSizeMode == PDEButtonLayerOverlayCheckboxSizeMode.PDEButtonLayerOverlayCheckboxSizeModeFixed) {
            // fixed height
            checkboxSize = mCheckboxSize;
        } else {
            // unknown mode -> do nothing
            return;
        }

        //determine which icon to use from checkbox size. We have only two icons (button sizes 3 BU and 4 BU, giving
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

        // load the icon
//        resourceID = R.drawable.checkmark_light_m;//context.getResources().getIdentifier(mIconFile, "drawable",context.getPackageName() );
        // this seems not to work -> workaround
        if(mIconFile=="checkmark_light_m.png"){
            resourceID = R.drawable.checkmark_light_m;
        } else {
            resourceID = R.drawable.checkmark_light_l;
        }
        mIcon = context.getResources().getDrawable(resourceID);

        //directly apply to the layers
        mSunkenLayer.setShapeRoundedRect(new RectF(0,0,mCheckboxUsedSize,mCheckboxUsedSize), PDEBuildingUnits.oneThirdBU());

        // set icon and bounds
        if (mIcon!=null) {
            //TODO maybe we have to set the iconlayer size to the icon size
            //mIconLayer.bounds = CGRectMake (0,0,mIcon.getIntrinsicWidth(),mIcon.getIntrinsicHeight());
            mIconLayer.setImageDrawable(mIcon);
        } else {
            mIconLayer.setImageDrawable(null);
        }
    }


//----- layout ---------------------------------------------------------------------------------------------------------


    /**
     * @brief Apply new layout when set from the outside.
     */
    @Override
    public void setLayout(PDEButtonLayoutHelper layout)
    {
        // any change?
        // make a own equals method in the PDEButtonLayoutHelper class if it exists after adjustments!!!!!!
        if (mLayout.equals(layout)) {
            return;
        }

        // remember
        mLayout = new PDEButtonLayoutHelper(layout);

        // update the size
        updateSize();
// ToDo find out what to do with cliprect stuff
//
//        // set bounds and offset of our layer; we're clipping
//        self.layer.anchorPoint = CGPointMake(0.0f,0.0f);
//        self.layer.frame = layout->mClipRect;

        // and perform a new layout (-> this might adjust further layout rects)
        performLayout(layout);
    }


    /**
     * @brief Relayout.
     *
     * Use the stored layout. This is not correct since it does not update dependent layers. When we have change
     * management we must trigger a complete button layout here.
     */
    private void relayout()
    {
        PDEButtonLayoutHelper layout;

        // create a copy (the layout is changed during layouting)
        layout = new PDEButtonLayoutHelper(mLayout);

        // perform layout with stored layout data
        performLayout(layout);
    }


    /**
     * @brief Perform the actual layouting tasks.
     *
     * Called when anything has changed. Performs the new layout for all elements and sets it
     * accordingly. The member variables are already set correctly outside.
     */
    private void performLayout(PDEButtonLayoutHelper layout)
    {
        float xoffset,yoffset,width,height,checkboxX,checkboxY,iconX,iconY,dist,oldvalue;

        // commonly used data (adjusted for our special clipping logic)
        xoffset = layout.mLayoutRect.left - layout.mClipRect.left;
        yoffset = layout.mLayoutRect.top - layout.mClipRect.top;
        width = layout.mLayoutRect.width();
        height = layout.mLayoutRect.height();

        // center checkbox in Y, round to screen
        checkboxY = yoffset + PDEBuildingUnits.roundToScreenCoordinates((height - mCheckboxUsedSize) / 2.0f);



        // ToDo check if correct; replaced width by right and origin.x by left and origin.y by top
        // check alignment, adjusts layout for further children
        if (mCheckboxAlignment == PDEConstants.PDEAlignment.PDEAlignmentLeft) {
            // left, one BU from border
            checkboxX = xoffset + PDEBuildingUnits.BU();
            // adjusting distance
            dist = PDEBuildingUnits.BU() + mCheckboxUsedSize;
            // adjust layout rect
            layout.mLayoutRect.left += dist;
            layout.mLayoutRect.right -= dist; // ToDo: check if this is correct; original code was: width -=dist;
            // and adjust clip rect
            oldvalue = layout.mClipRect.left;
            //on ios [PDEBuildingUnits nativePixel] -> 1.0f on android
            layout.mClipRect.left = layout.mLayoutRect.left + 1;
            layout.mClipRect.right -= layout.mClipRect.left - oldvalue;  // ToDo: check if this is correct;original code was: width -= ..;
        } else if (mCheckboxAlignment == PDEConstants.PDEAlignment.PDEAlignmentCenter) {
            // centered, aligned to screen pixels
            checkboxX = xoffset + PDEBuildingUnits.roundToScreenCoordinates((width - mCheckboxUsedSize) / 2.0f);
        } else if (mCheckboxAlignment == PDEConstants.PDEAlignment.PDEAlignmentRight) {
            // right, one BU from border
            checkboxX = xoffset + width - PDEBuildingUnits.BU() - mCheckboxUsedSize;
            // adjusting distance
            dist = PDEBuildingUnits.BU() + mCheckboxUsedSize;
            // adjust layout rect
            layout.mLayoutRect.right -= dist; // ToDo check if correct; replaced width by right
            // and adjust clip rect
            oldvalue = layout.mLayoutRect.left + layout.mLayoutRect.width();
            layout.mClipRect.right = (int) oldvalue - 1 - layout.mClipRect.left;  // ToDo check if correct; replaced width by right
        } else {
            // unknown, default
            checkboxX = 0.0f;
        }

        // set positions
        PDEAbsoluteLayout.LayoutParams sunkenLayerParams = (PDEAbsoluteLayout.LayoutParams) mSunkenLayer.getLayoutParams();
        sunkenLayerParams.x = PDEBuildingUnits.roundToScreenCoordinates(checkboxX);
        sunkenLayerParams.y = PDEBuildingUnits.roundToScreenCoordinates(checkboxY);
        // also width & height???
        mSunkenLayer.setLayoutParams(sunkenLayerParams);

        PDEAbsoluteLayout.LayoutParams iconLayerParams = (PDEAbsoluteLayout.LayoutParams) mIconLayer.getLayoutParams();
        // measure to get width & height of icon correctly
        mIconLayer.measure(0,0);
        // icon X centered inside checkbox, round to screen
        iconX = checkboxX
                + PDEBuildingUnits.roundToScreenCoordinates((mCheckboxUsedSize-mIconLayer.getMeasuredWidth()) / 2.0f);
        // center icon Y inside checkbox, round to screen
        iconY = checkboxY
                + PDEBuildingUnits.roundToScreenCoordinates((mCheckboxUsedSize-mIconLayer.getMeasuredHeight())
                / 2.0f);
        iconLayerParams.x = PDEBuildingUnits.roundToScreenCoordinates(iconX);
        iconLayerParams.y = PDEBuildingUnits.roundToScreenCoordinates(iconY);
        // also width & height???
        mIconLayer.setLayoutParams(iconLayerParams);
    }


    @Override
    public void collectHints(PDEDictionary hints) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void setHints(PDEDictionary hints) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void collectButtonPaddingRequest(PDEButtonPadding padding) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
