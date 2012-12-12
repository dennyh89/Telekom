/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;

import android.graphics.Rect;
import android.util.Log;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonLayoutHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEDictionary;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShape;
import de.telekom.pde.codelibrary.ui.elements.common.PDEViewBackground;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;


//----------------------------------------------------------------------------------------------------------------------
//  PDEButtonLayerBackgroundRect
//----------------------------------------------------------------------------------------------------------------------



/**
 * @brief Background for an Rect button.
 *
 * Color gradient, frame, inner shadow on pressed.
 */
public class PDEButtonLayerBackgroundRect extends Object implements PDEButtonLayerInterface {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEButtonLayerBackgroundRect.class.getName();
    // debug messages switch
    private final static boolean DEBUGPARAMS = false;

    // local parameters needed
    PDEParameterDictionary mParameters;
    PDEParameter mParamColor;

    // views
    PDEViewBackground mMainView;

    protected PDEButtonLayoutHelper mLayout;

    // agent helpers
    PDEAgentHelper mAgentHelper;

    // layout that holds the  main backgroundview
    PDEAbsoluteLayout mLayer;



    // global variables
    //
    public static PDEDictionary PDEButtonLayerBackgroundRectGlobalColorDefault = null;





    /**
     * @brief Class initialization.
     */
    PDEButtonLayerBackgroundRect() {
        // read default dictionaries
        PDEButtonLayerBackgroundRectGlobalColorDefault = PDEComponentHelpers.readDefaultColorDictionary(
                "dt_button_flat_color_defaults");

        // init
        mParameters = null;
        mLayout = new PDEButtonLayoutHelper();

        // set empty complex parameters
        mParamColor = new PDEParameter();

        // agent helper
        mAgentHelper = new PDEAgentHelper();

        mLayer = new PDEAbsoluteLayout(PDECodeLibrary.getInstance().getApplicationContext());
        mLayer.setClipChildren(false);
        mMainView = new PDEViewBackground(PDECodeLibrary.getInstance().getApplicationContext(),new PDEDrawableShape());
        mLayer.addView(mMainView);

        // further rasterization... -> not now, do more tests and only turn it on if the button is stable / not animated
        //mCollectionLayer.rasterizationScale = [PDEBuildingUnits scale];
        //mCollectionLayer.shouldRasterize = YES;

        // forced set of parameter -> this sets defaults
        setParameters(new PDEParameterDictionary(), true);
    }




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
    public void agentEvent(PDEEvent event) {
        boolean needsUpdate;

        // pass on agent events to agent helper
        needsUpdate = mAgentHelper.processAgentEvent(event);

        // update if necessary
        if (needsUpdate) {
            // update animatable parameters on change
            updateColors();
        }
    }


    /**
     * @brief Set button parameters.
     *
     * Check for changes; if changed, determine missing parameters and fill them with defaults, then apply.
     */
    @Override
    public void setParameters(PDEParameterDictionary parameters, boolean force) {
        PDEParameterDictionary oldParams;

        // for local change management keep the old params for a while
        oldParams = mParameters;

        // completely copy the new ones to have a reference for further change management
        mParameters = parameters.copy();

        // check for color or border changes (all in one go)
        if (force || !PDEParameterDictionary.areParametersEqual(mParameters, oldParams, PDEButton.PDEButtonParameterColor)) {
            prepareColors();
        }
    }


//----- animated parameters: preparation -------------------------------------------------------------------------------


    /**
     * @brief Prepare color parameter set. This prepares main and border color.
     *
     * Note that main color is quite sophisticated (automatic color generation). If border color is not
     * specified, a default color set is searched. If there is no default color set, border color is calculated by
     * using the darker gradients of the main color. If border color is specified, the same logic for border color
     * generation is used as for main state color generation.
     */
    protected void prepareColors() {
        // set the new values
        mParamColor.setWithParameter(mParameters.parameterForName(PDEButton.PDEButtonParameterColor));

        // debug output
        if (DEBUGPARAMS) {
            mParamColor.debugOut("Color before building");
        }

        // calculate color agent states
        PDEComponentHelpers.buildColors(mParamColor, PDEButtonLayerBackgroundRectGlobalColorDefault,
                "DTTransparentBlack",
                PDEAgentHelper.PDEAgentHelperAnimationInteractive);

        // debug output
        if (DEBUGPARAMS) {
            mParamColor.debugOut("Color after building");
        }

        // and apply once
        updateColors();
    }



//----- animated parameter updates -------------------------------------------------------------------------------------


    /**
     * @brief Update colors (fully animated).
     */
    protected void updateColors() {
        PDEColor mainColor;

        // interpolate colors by calling complex logic color interpolation helper
        mainColor = PDEComponentHelpers.interpolateColor(mParamColor, mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationInteractive, null);

        // set the gradient and border colors
        getMainDrawable().setBackgroundColor(mainColor.getIntegerColor());
    }



//----- layout ---------------------------------------------------------------------------------------------------------


    /**
     * @brief Apply new layout when set from the outside.
     */
    @Override
    public void setLayout(PDEButtonLayoutHelper layout) {
        // debug
        if(DEBUGPARAMS){
            Log.d(LOG_TAG,
                  "screenRect left " + layout.mButtonRect.left + " top " + layout.mButtonRect.top + " right " + layout.mButtonRect.right +
                  " bottom " + layout.mButtonRect.bottom);
            Log.d(LOG_TAG,
                  "outlineRect left " + layout.mLayoutRect.left + " top " + layout.mLayoutRect.top + " right " + layout.mLayoutRect.right +
                  "  bottom " + layout.mLayoutRect.bottom);
        }

        // any change & valid size?
        if ((layout.mButtonRect.left == mLayout.mButtonRect.left) &&
            (layout.mButtonRect.top == mLayout.mButtonRect.top) &&
            (layout.mButtonRect.right == mLayout.mButtonRect.right) &&
            (layout.mButtonRect.bottom == mLayout.mButtonRect.bottom) &&
            (layout.mLayoutRect.left == mLayout.mLayoutRect.left) &&
            (layout.mLayoutRect.top == mLayout.mLayoutRect.top) &&
            (layout.mLayoutRect.right == mLayout.mLayoutRect.right) &&
            (layout.mLayoutRect.bottom == mLayout.mLayoutRect.bottom) |
            layout.mButtonRect.height() <=0 | layout.mButtonRect.width()<=0 |
            layout.mLayoutRect.height()<=0 | layout.mLayoutRect.width()<=0 ) {
            return;
        }

        // remember
        mLayout = new PDEButtonLayoutHelper(layout);

        // update main background
        getMainDrawable().setBoundingRect(mLayout.mLayoutRect);

        // ToDo: Not sure if this update is really needed, so it's commented out for now until we find a reason to
        // use it again.
//        // just to be sure, that all children get the layout changes
//        mLayer.updateLayout(true, mOutlineRect.left, mOutlineRect.top, mOutlineRect.right,
//                            mOutlineRect.bottom);
    }

    /**
     * @brief Collect hints.
     *
     * Transparent background is our default -> this needs to be hinted to other layers.
     */
    @Override
    public void collectHints(PDEDictionary hints) {
        // we also have to hint that the default color is transparent (otherwise the others won't be able to correctly determine
        // the default color if nothing is set and assume DTUIInteractive as default)
        hints.put(PDEButton.PDEButtonHintDefaultColor, new PDEColor(PDEColor.valueOf("DTTransparentBlack")));
    }


    /**
     * @brief Set hints.
     *
     * Empty implementation; No hints to set.
     */
    @Override
    public void setHints(PDEDictionary hints) {
    }


    public void collectButtonPaddingRequest(PDEButtonPadding padding){

    }



//----- Properties Setter/Getter -----------------------------------------------------------------------------------




    /**
     * @brief Remember display size of background (internal helper).
     *
     * The outer shadow isn't considered in this size.
     */
    protected void setLayoutRect(Rect layoutRect){
        mLayout.mLayoutRect = layoutRect;
    }

    /**
     * @brief Get display size of background.
     *
     * The outer shadow isn't considered in this size.
     */
    public Rect getLayoutRect(){
        return mLayout.mLayoutRect;
    }

    /**
     * @brief Get view of main background
     */
    public PDEViewBackground getMainView() {
        return mMainView;
    }

    /**
     * @brief Set new view of main background
     */
    public void setMainView(PDEViewBackground view) {
        mMainView = view;
    }

    /**
     * @brief Set new drawable of main background
     */
    public void setMainDrawable(PDEDrawableShape drawable) {
        mMainView.setDrawable(drawable);
    }

    /**
     * @brief Get drawable of main background
     */
    public PDEDrawableShape getMainDrawable(){
        if (mMainView.getDrawable() instanceof PDEDrawableShape){
            return (PDEDrawableShape) mMainView.getDrawable();
        } else {
            return null;
        }
    }
}

