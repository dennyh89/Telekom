/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;


import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.text.TextUtils;
import android.util.FloatMath;
import android.util.Log;
import android.view.View;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonLayoutHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEDictionary;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.common.PDEShadowImageView;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import android.graphics.drawable.Drawable;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

import java.lang.reflect.Method;


public class PDEButtonLayerForegroundIconText implements PDEButtonLayerInterface {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEButtonLayerForegroundIconText.class.getName();
    private final static boolean DEBUGPARAMS = false;

    // font modes
    enum PDEButtonLayerForegroundIconTextFontMode {
        Undefined,
        Automatic,
        Styleguide,
        Fixed
    };

    // parameters needed
    private PDEParameterDictionary mParameters;
    private PDEParameter mParamColor;
    private PDEParameter mParamTitleColor;
    private PDEParameter mParamTitleShadowColor;
    private PDEParameter mParamTitleShadowOffset;

    // content layers
    PDEDrawText mTextView;
    PDEShadowImageView mImageView;

    // configuration
    PDEColor mDefaultColor;

    // icon configuration

    // basic button configuration
    Drawable mIcon; //try to use Bitmap instead of UIImage *mIcon;
    Boolean mIconColored;

    // text configuration
    String mTitle;
    private PDETypeface mFont;
    PDEButtonLayerForegroundIconTextFontMode mFontMode;
    float mFontSize;
    boolean mTextHasShadow;
    PDEColor mTextOnTransparentColor;
    PDEColor mBackgroundColor;
    PDEConstants.PDEAlignment mAlignment;
    PDEButton.PDEButtonIconAlignment mIconAlignment;

    // internal font and title configuration
    float mFontUsedSize;
    boolean mFontUsesCaps;
    float mTextWidthFirstLetterPlusEllipsis;
    float mTextWidthFirstLetter;
    Rect mTextSizeFull;
    float mFontAscenderHeight;
    float mFontTopHeight;
    float mFontCapHeight;
    float mFontOverallHeight;
    float mFontTopOverallHeight;
    String mFirstLetterEllipsis;
    String mFirstLetter;

    // layout info
    float mDisplayOffsetX;
    float mDisplayOffsetY;
    float mDisplayWidth;
    float mDisplayHeight;

    // agent helpers
    PDEAgentHelper mAgentHelper;

    Context mContext;

    protected PDEAbsoluteLayout mLayer = null;

    public PDEButtonLayerForegroundIconText(Context context) {

        Drawable roundCornerDrawable;
        //PDEDrawableShape roundCornerDrawable;

        Method method;
        boolean clippingDrawableSet = false;

        // init
        mParameters = null;
        mDisplayOffsetX = 0.0f;
        mDisplayOffsetY = 0.0f;
        mDisplayWidth = 0.0f;
        mDisplayHeight = 0.0f;
        mTextHasShadow = false;
        mIconColored = false;
        mDefaultColor = PDEColor.valueOf("DTUIInteractive");
        mTextOnTransparentColor = PDEColor.valueOf("DTUIText");
        mBackgroundColor = PDEColor.valueOf("DTUIBackground");

        // default button data
        mTitle = "";
        mIcon = null;
        mFontMode = PDEButtonLayerForegroundIconTextFontMode.Undefined;
        mFontSize = 0.0f;
        mFontUsedSize = 0.0f;
        mFontUsesCaps = false;
        mTextSizeFull = new Rect(0,0,0,0);
        mFontTopHeight = 0.0f;
        mFontCapHeight = 0.0f;
        mFirstLetterEllipsis = "";
        mFirstLetter = "";
        mAlignment = null;
        mIconAlignment = null;

        mContext = context;


        // create the layer
        mLayer = new PDEAbsoluteLayout(context);
        mLayer.setClipChildren(true);
        mLayer.setClipToPadding(true);
        if(Build.VERSION.SDK_INT >= 11){
            mLayer.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        // agent helper
        mAgentHelper = new PDEAgentHelper();

        // create label layer
        mTextView = new PDEDrawText(context);
        mTextView.setBackgroundColor(Color.TRANSPARENT);
        mTextView.setClickable(false);
        mTextView.setFocusable(false);

        // add it
        mLayer.addView(mTextView);

        // create image layer
        mImageView = new PDEShadowImageView(context);
        mLayer.addView(mImageView);

        // shadow is initially disabled on both text and icon

        // set empty complex parameters
        mParamColor = new PDEParameter();
        mParamTitleColor = new PDEParameter();
        mParamTitleShadowColor = new PDEParameter();
        mParamTitleShadowOffset = new PDEParameter();

        // forced set of parameter -> this sets defaults
        this.setParameters(new PDEParameterDictionary(), true);
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
    public void agentEvent(PDEEvent event)
    {
        boolean needsUpdate;

        // pass on agent events to agent helper
        needsUpdate = mAgentHelper.processAgentEvent(event);

        // update if necessary
        if (needsUpdate) {
            // update animateable parameters on change
            updateColors();
        }
    }


//----- property handling ----------------------------------------------------------------------------------------------


    /**
     * @brief Set hints.
     *
     * Extract the interesting hints and set them directly as parameters.
     */
    @Override
    public void setHints(PDEDictionary hints) {
        // extract default color hint
        this.setDefaultColor(PDEComponentHelpers.extractDefaultColorHint(hints));

        // get text color for transparent background (this takes dark style and respective hints into account)
        this.setTextOnTransparentColor(PDEComponentHelpers.extractTextOnTransparentColorHint(hints));

        // get background color hint (used for transparent backgrounds to determine shadow color)
        this.setBackgroundColor(PDEComponentHelpers.extractBackgroundColorHint(hints));

        // get text shadow hint; the hint is called "3DStyle"
        this.setTextShadow(PDEComponentHelpers.extract3DStyleHint(hints));
    }


    /**
     * @brief Set button basic data.
     *
     * Should only be called from main button class, it handles the state, and also does change management.
     */
    public void setParameters(PDEParameterDictionary parameters, boolean force)
    {
        PDEParameterDictionary oldParams;

        // for change management, keep around the old params
        oldParams = mParameters;

        // completely copy the new ones for further change management
        mParameters = parameters.copy();

        // check for color or text color changes
        if (force
                || !PDEParameterDictionary.areParametersEqual(mParameters, oldParams, PDEButton.PDEButtonParameterColor)
                || !PDEParameterDictionary.areParametersEqual(mParameters, oldParams, PDEButton.PDEButtonParameterTitleColor)) {
            this.prepareTitleColor();
        }

        // non-animatable properties that nevertheless need external change management
        if (force
                || !PDEParameterDictionary.areParametersEqual(mParameters, oldParams, PDEButton.PDEButtonParameterIcon)) {
            this.prepareIcon();
        }

        // non-animatable properties do their change management internally
        this.prepareTitle();
        this.prepareFont();
        this.prepareInternalLayout();
        this.prepareIconColored();

    }

    @Override
    public void collectHints(PDEDictionary hints) {
        // no hints to collect for this layer

    }

    public void collectButtonPaddingRequest(PDEButtonPadding padding){

    }


//----- non-parameters: preparation -------------------------------------------------------------------------------

    /**
     * @brief Set the (animatable) title color.
     *
     * If no title color is given, the title color is calculated from a) the main color, and b) the hints (indicative
     * buttons have a special color), and c) the system preset (dark or light system).
     *
     * The shadow color is also calculated; the shadow color depends solely on the main color (and does only change
     * inbetween states)
     */
    private void prepareTitleColor()
    {
        // set the new values
        mParamColor.setWithParameter(mParameters.parameterForName(PDEButton.PDEButtonParameterColor));
        mParamTitleColor.setWithParameter(mParameters.parameterForName(PDEButton.PDEButtonParameterTitleColor));

        // shadow color and offset are at the moment internal only; clear old stuff
        mParamTitleShadowColor.removeAllObjects();
        mParamTitleShadowOffset.removeAllObjects();

        // debug output
        if (DEBUGPARAMS) {
            mParamColor.debugOut("Color before building");
            mParamTitleColor.debugOut("Title color before building");
            mParamTitleShadowColor.debugOut("Title shadow color before building");
            mParamTitleShadowOffset.debugOut("Title shadow offset before building");
        }

            // basic propagation of colors (no agent states required), convert to colors
        PDEComponentHelpers.fillStateBaseValues(mParamColor, mDefaultColor);
        mParamColor.convertToColor();

        // basic propagation of title colors
        PDEComponentHelpers.fillStateBaseValues(mParamTitleColor, null);

        // convert to colors
        mParamTitleColor.convertToColor();

        // calculate missing text colors (pass on the hints)
        PDEComponentHelpers.fillTitleColors(mParamTitleColor, mParamColor, mTextOnTransparentColor);

        // calculate missing shadow colors from colors (no base state propagation necessary, this is a virtual parameter)
        PDEComponentHelpers.fillTitleShadowColors(mParamTitleShadowColor, mParamColor, mBackgroundColor);

        // calculate missing shadow offsets from colors (no base state propagation necessary, this is a virtual parameter)
        PDEComponentHelpers.fillTitleShadowOffsets(mParamTitleShadowOffset, mParamColor, mBackgroundColor);

        // debug output
        if (DEBUGPARAMS) {
            mParamColor.debugOut("Color after building");
            mParamTitleColor.debugOut("Title color after building");
            mParamTitleShadowColor.debugOut("Title shadow after after building");
            mParamTitleShadowOffset.debugOut("Title shadow offset after building");
        }

        // and apply once
        this.updateColors();
    }


//----- non-animated parameters: preparation and application -----------------------------------------------------------


    /**
     * @brief Private function - prepare the title.
     *
     * Title is non-animated and uses the base parameter
     */
    private void prepareTitle()
    {
        String title;

        // get the title from the parameters
        title =  (String)mParameters.parameterValueForName(PDEButton.PDEButtonParameterTitle);

        if ((TextUtils.isEmpty(mTitle) && TextUtils.isEmpty(title)) || (!TextUtils.isEmpty(mTitle) && mTitle.equals(title)) ) { //nothing to do title didn't change
            return;
        }

        // remember
        mTitle = title;

        // update font size (this calculates some helper values) and relayout
        this.updateFontSize(true);
        this.performLayout();
    }

    /**
     * @brief Private function - prepare internal layout.
     *
     * IconOnRightSide and IconTextAlignment are non-animated and uses the base parameter
     */
    private void prepareInternalLayout()
    {
        PDEConstants.PDEAlignment alignment = null;
        PDEButton.PDEButtonIconAlignment iconAlignment = null;

        String textAlignmentString;
        String iconAlignmentString;


        // get the title from the parameters
        textAlignmentString = mParameters.parameterValueForNameWithDefault(PDEButton.PDEButtonParameterAlignment, PDEConstants.PDEAlignmentStringCenter);

        // parse value
        if ( textAlignmentString.equals(PDEConstants.PDEAlignmentStringLeft)) alignment= PDEConstants.PDEAlignment.PDEAlignmentLeft;
        else if ( textAlignmentString.equals(PDEConstants.PDEAlignmentStringCenter)) alignment= PDEConstants.PDEAlignment.PDEAlignmentCenter;
        else if ( textAlignmentString.equals(PDEConstants.PDEAlignmentStringRight)) alignment= PDEConstants.PDEAlignment.PDEAlignmentRight;
        else alignment= PDEConstants.PDEAlignment.PDEAlignmentCenter;


        iconAlignmentString = mParameters.parameterValueForNameWithDefault(PDEButton.PDEButtonParameterIconAlignment, PDEConstants.PDEAlignmentStringLeftAttached);

        // parse value
        if ( iconAlignmentString.equals(PDEConstants.PDEAlignmentStringLeft)) iconAlignment= PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeft;
        else if ( iconAlignmentString.equals(PDEConstants.PDEAlignmentStringRight)) iconAlignment= PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentRight;
        else if ( iconAlignmentString.equals(PDEConstants.PDEAlignmentStringRightAttached)) iconAlignment= PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentRightAttached;
        else iconAlignment= PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached;


        // any change?
        if (alignment == mAlignment && iconAlignment == mIconAlignment) return;

        // remember
        mAlignment = alignment;
        mIconAlignment = iconAlignment;

        // relayout
        performLayout();
    }

    /**
     * @brief: Private function - prepare the font.
     *
     * Preparation function for the font which evaluates the parameters. Note that we need to
     */
    private void prepareFont()
    {
        Object fontObject,fontSizeObject;
        PDEButtonLayerForegroundIconTextFontMode fontMode = PDEButtonLayerForegroundIconTextFontMode.Undefined;
        float fontSize = 0.0f;
        String str;
        PDETypeface lfont=null;

        // get font parameters
        fontObject = mParameters.parameterObjectForName(PDEButton.PDEButtonParameterFont);
        fontSizeObject = mParameters.parameterObjectForName(PDEButton.PDEButtonParameterFontSize);

        // determine font
        if (fontObject instanceof PDETypeface) {
            lfont = (PDETypeface)(fontObject);
        }

        // determine font size
        if (fontSizeObject instanceof Number) {
            fontSize = ((Number)fontSizeObject).floatValue();
            fontMode = PDEButtonLayerForegroundIconTextFontMode.Fixed;
        } else if (fontSizeObject instanceof String) {
            //todo test this part

            // extract string
            str = (String) fontSizeObject;
            // button specific mode / fixed size?
            if (str.compareToIgnoreCase(PDEButton.PDEButtonParameterValueSizeAuto)==0
            || str.compareToIgnoreCase(PDEButton.PDEButtonParameterValueSizeAutomatic) == 0) {
                // automatic sizing
                fontMode = PDEButtonLayerForegroundIconTextFontMode.Automatic;
                fontSize = 0.0f;
            } else if (str.compareToIgnoreCase(PDEButton.PDEButtonParameterValueSizeStyleguide) == 0) {
                // styleguide defined sizes sizing
                fontMode = PDEButtonLayerForegroundIconTextFontMode.Styleguide;
                fontSize = 0.0f;
            } else {
                //not a button specific value -> parse string
                fontSize = PDEFontHelpers.parseFontSize(str, lfont, mContext.getResources().getDisplayMetrics());
                if (Float.isNaN(fontSize)) {
                    // todo create a meaningful error messsage
                    Log.e(LOG_TAG,"could not parse font string correctly: "+str);
                    fontSize = 24.0f;
                }
                fontMode = PDEButtonLayerForegroundIconTextFontMode.Fixed;
            }
        } else {
            fontSize = 0.0f;
            fontMode = PDEButtonLayerForegroundIconTextFontMode.Styleguide;
        }
        // changed?
        if (fontMode==mFontMode && fontSize==mFontSize && lfont==mFont) return;
        // remember
        mFontMode = fontMode;
        mFontSize = fontSize;
        //create font independent from size, so do it once here, or take parameter font
        if (lfont == null ) {
            //use default font if we dont have a correct one !!!!!!!!!!
            mFont = PDEFontHelpers.validFont(lfont);
        } else {
            // remember external font
            mFont = lfont;
        }

        // update font size, and relayout
        updateFontSize(false);
        performLayout();
    }



    /**
     * @brief Prepare the icon.
     *
     * The icon might be animated later; right now, it's not. Icon change checks are complicated and
     * won't run without actually loading the icon, so make sure to check for parameter changes outside.
     */
    private void prepareIcon()
    {
        Drawable icon;
        Object iconObject;

        // get the object
        iconObject = mParameters.parameterObjectForName(PDEButton.PDEButtonParameterIcon);

        // depending on class, load the icon or use it directly
        if (iconObject instanceof Number) {
            // load from resources (using default resource discovery methods)
            icon = PDECodeLibrary.getInstance().getApplicationContext().getResources().getDrawable(((Number)iconObject).intValue());

        } else if (iconObject instanceof Drawable) {
            // use directly
            icon = (Drawable)iconObject;
        } else {
            // unknown type or no icon
            icon = null;
        }

        // remember (change management not feasible here	)
        mIcon = icon;


        // apply or clear
        if (icon != null) {
            // set the icon
            mImageView.setImageDrawable(mIcon);
        } else {
            // clear the icon
            mImageView.setImageDrawable(null);
        }

        // we need a relayout
        performLayout();
    }

    /**
     * @brief Prepare the icon color mode.
     *
     * It won't change, so it's a global parameter.
     */
    private void prepareIconColored()
    {
        boolean colored;

        // extract
        colored = mParameters.parameterBoolForName(PDEButton.PDEButtonParameterIconColored);

        // any change?
        if (mIconColored == colored) return;

        // remember
        mIconColored = colored;

        // clear image color if not colored
        if (!mIconColored) {
            // clear out the color
            mImageView.setColorFilter(null);
        }

        // image layer only has a shadow if it's enabled, and the image is colorized
        if (mTextHasShadow && mIconColored) {
            // turn it on
            mImageView.enableShadow(true);
        } else {
            // turn it off
            mImageView.enableShadow(false);
        }

        // if the image is colored, we also need to update the colors
        if (mIconColored) {
            // colors need to be recalculated
            prepareTitleColor();
        }
    }


//----- fixed parameters -----------------------------------------------------------------------------------------------


/**
 * @brief Set the dark style hint.
 */
    private void setDefaultColor(PDEColor color)
    {
        // any change?
        if (color.equals(mDefaultColor)) return;

        // remember
        mDefaultColor = color;

        // color needs to be updated
        prepareTitleColor();
    }


/**
 * @brief Enable/disable text shadow.
 */
    private void setTextShadow(boolean hasShadow)
    {
        // any change?
        if (hasShadow == mTextHasShadow) return;

        // remember
        mTextHasShadow = hasShadow;

        // -> can't split it up, do it in update colors
        // image layer only has a shadow if it's enabled, and the image is colorized
        if (mTextHasShadow && mIconColored) {
            mImageView.enableShadow(true);
        } else {
            // turn it off
            mImageView.enableShadow(false);
        }


        // eventually update colors to get correct shadow color
        if (mTextHasShadow) {
            updateColors();
        }
    }

/**
 * @brief Remember transparent color (from hints)
 */
    private void setBackgroundColor(PDEColor color)
    {
        // any change?
        if (color == null && mBackgroundColor == null) return;
        if (mBackgroundColor != null && mBackgroundColor.equals(color)) return;

        // remember
        mBackgroundColor = color;

        // colors need to be recalculated
        prepareTitleColor();
    }


/**
 * @brief Remember transparent color (from hints)
 */
    private void setTextOnTransparentColor(PDEColor color)
    {
        // any change?
        if (color == null  && mTextOnTransparentColor == null) return;
        if (mTextOnTransparentColor != null && mTextOnTransparentColor.equals(color)) return;

        // remember
        mTextOnTransparentColor = color;

        // colors need to be recalculated
        prepareTitleColor();
    }



//----- animated parameter updates -------------------------------------------------------------------------------------


/**
 * @brief Private function - update all colors.
 *
 * Title colors, shadow colors. All colors are precalculated and only animated.
 */
    private void updateColors()
    {
        PDEColor titleColor,shadowColor;
        PointF shadowOffset;

        // interpolate colors by calling complex logic color interpolation helper
        titleColor = PDEComponentHelpers.interpolateColor(mParamTitleColor,
                mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationStateOnly,
                null);
        shadowColor = PDEComponentHelpers.interpolateColor(mParamTitleShadowColor,
                mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationStateOnly,
                null);
        shadowOffset = PDEComponentHelpers.interpolatePosition(mParamTitleShadowOffset,
                mAgentHelper,
                PDEAgentHelper.PDEAgentHelperAnimationStateOnly,
                null);


        // set text color
        mTextView.setTextColor(titleColor.getIntegerColor());

        // set icon color if desired
        if (mIconColored) {
            mImageView.setColorFilter(titleColor.getIntegerColor());
        }

        // set shadow color only if shadow is enabled
        if (mTextHasShadow) {
            //Log.d(LOG_TAG, "shadowColor: "+shadowColor.getIntegerColor());
            // we need a (blur)radius > 0 otherwise now shadow is drawn
            // since a blur is necessary I currently don't calculate the alpha (0.25) into the color
            mTextView.setShadowLayer(shadowOffset.x, shadowOffset.y, shadowColor.getIntegerColor(), 0.25f);

        } else {
            // remove the shadow
            mTextView.setShadowLayer(0.0f, 0.0f, 0x00ffffff);
        }

        // set icon shadow if shadow is on and icon is colored
        if (mTextHasShadow && mIconColored) {
            mImageView.setOffset((int)shadowOffset.x, (int)shadowOffset.y);
            mImageView.setShadowColor(shadowColor.getIntegerColor());
        }

    }


/**
 * @brief Calculate the new font size and apply it.
 *
 * Also calculate helper values necessary later in layouting. Important note: This function
 * does not issue a relayout on font changes - ensure a relayout is done properly outside of this function.
 * This avoids recursive calls of updateFontSize and updateLayout.
 */
    private void updateFontSize(boolean force)
    {
        float fontSize;
        boolean fontInCaps;

        // automatic modes calculate font size from height
        if (mFontMode == PDEButtonLayerForegroundIconTextFontMode.Automatic) {
            // height in caps
            fontSize = mDisplayHeight / 3.0f;
            fontInCaps = true;
        } else if (mFontMode == PDEButtonLayerForegroundIconTextFontMode.Styleguide) {
            // standardize the font to 4*BU, 3*BU, 2.5*BU button size height
            if (mDisplayHeight > PDEBuildingUnits.exactPixelFromBU(3.5f)) {
                // display height > 3,5*BU -> font for display height of 4*BU
                fontSize = PDEBuildingUnits.pixelFromBU(4.0f) / 3.0f;
            } else if (mDisplayHeight > PDEBuildingUnits.exactPixelFromBU(2.75f)) {
                // 2,75*BU < display height <= 3,5*BU -> font for display height of 3*BU
                fontSize = PDEBuildingUnits.exactBU();
            } else {
                // display height < 2,75*BU -> font for display height of 2,5*BU
                fontSize = PDEBuildingUnits.pixelFromBU(2.5f) / 3.0f;
            }
            fontInCaps = true;
        } else if (mFontMode == PDEButtonLayerForegroundIconTextFontMode.Fixed) {
            // height in native font size
            fontSize = mFontSize;
            fontInCaps = false;
        } else {
            // unknown mode -> do nothing
            return;
        }

        // compare with last value, stop if not changed
        if (!force && fontSize==mFontUsedSize && fontInCaps==mFontUsesCaps) return;

        // remember
        mFontUsedSize = fontSize;
        mFontUsesCaps = fontInCaps;

        // eventually convert caps height to real height
        if (mFontUsesCaps) {
            fontSize = PDEFontHelpers.calculateFontSize(mFont, fontSize);
        }

        // assure readable font size
        fontSize = PDEFontHelpers.assureReadableFontSize(mFont, fontSize);

        // set the font and font size to the text layer
        mTextView.setTypeface(mFont.getTypeface());
        mTextView.setTextSize(fontSize);

        // do font calculations once
        // calculate necessary layouting helpers once here (they don't change necessarily)
        if (!TextUtils.isEmpty(mTitle)) {
            mFirstLetterEllipsis = String.format("%s...", mTitle.substring(0,1));
            mFirstLetter = mTitle.substring(0,1);
        } else {
            mFirstLetterEllipsis = "";
            mFirstLetter = "";
        }

        // do text calculations
        mTextWidthFirstLetterPlusEllipsis =  PDEFontHelpers.getTextViewBounds(mFirstLetterEllipsis, mFont, fontSize).width();
        mTextWidthFirstLetter = PDEFontHelpers.getTextViewBounds(mFirstLetter, mFont, fontSize).width();
        mTextSizeFull = PDEFontHelpers.getTextViewBounds(mTitle, mFont, fontSize);

        mFontTopHeight = PDEFontHelpers.getPixelsAboveBaseLine(mTitle, mFont, fontSize);
        mFontAscenderHeight = PDEFontHelpers.getPixelsBelowBaseLine(mTitle, mFont, fontSize);
        mFontCapHeight = PDEFontHelpers.getCapHeight(mFont, fontSize);
        mFontOverallHeight = PDEFontHelpers.getHeight(mFont, fontSize);
        mFontTopOverallHeight = PDEFontHelpers.getTopHeight(mFont, fontSize);

        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "mTextWidthFirstLetterPlusEllipsis "+mTextWidthFirstLetterPlusEllipsis);
            Log.d(LOG_TAG, "mTextWidthFirstLetter "+mTextWidthFirstLetter);
            Log.d(LOG_TAG, "mTextSizeFull "+mTextSizeFull.flattenToString());
            Log.d(LOG_TAG, "mFontAscenderHeight "+mFontAscenderHeight);
            Log.d(LOG_TAG, "mFontTopHeight "+mFontTopHeight);
            Log.d(LOG_TAG, "mFontCapHeight "+mFontCapHeight);
            Log.d(LOG_TAG, "mFontOverallHeight "+mFontOverallHeight);
            Log.d(LOG_TAG, "mFontTopOverallHeight "+mFontTopOverallHeight);
        }
    }


//----- layout ---------------------------------------------------------------------------------------------------------


/**
 * @brief Apply new layout when set from the outside.
 */
    @Override
    public void setLayout(PDEButtonLayoutHelper layout)
    {
        if(DEBUGPARAMS){
            Log.d(LOG_TAG, "setLayout("+layout.mButtonRect.toString()+" "+layout.mLayoutRect.toString());
        }

        // any change?
        if (layout.mLayoutRect.left == mDisplayOffsetX
            && layout.mLayoutRect.top == mDisplayOffsetY
            && layout.mLayoutRect.right - layout.mLayoutRect.left == mDisplayWidth
            && layout.mLayoutRect.bottom - layout.mLayoutRect.top == mDisplayHeight) return;

        // remember
        mDisplayOffsetX = layout.mLayoutRect.left;
        mDisplayOffsetY = layout.mLayoutRect.top;
        mDisplayWidth = layout.mLayoutRect.right - layout.mLayoutRect.left;
        mDisplayHeight = layout.mLayoutRect.bottom - layout.mLayoutRect.top;

        // position the layer at the right place within the button
        PDEAbsoluteLayout.LayoutParams lp = (PDEAbsoluteLayout.LayoutParams)mLayer.getLayoutParams();
        lp.x = PDEBuildingUnits.roundToScreenCoordinates(mDisplayOffsetX);
        lp.y = PDEBuildingUnits.roundToScreenCoordinates(mDisplayOffsetY);
        lp.width = PDEBuildingUnits.roundToScreenCoordinates(mDisplayWidth);
        lp.height = PDEBuildingUnits.roundToScreenCoordinates(mDisplayHeight);
        mLayer.setLayoutParams(lp);

        // update the font size
        updateFontSize(false);

        // and perform a new layout
        performLayout();
    }


/**
 * @brief Perform the actual layouting tasks.
 *
 * Called when anything has changed. Performs the new layout for all elements and sets it
 * accordingly. The member variables are already set correctly outside.
 */
    private void performLayout()
    {

        PointF textPosition = new PointF(.0f,.0f);
        PointF iconPosition = new PointF(.0f,.0f);

        float fullWidth; // button width
        float saveArea = PDEBuildingUnits.exactBU(); //single margin
        float leftBorder = 0.0f;

        float buttonYCenter = PDEBuildingUnits.roundToScreenCoordinates(mDisplayHeight / 2.0f);
        float availableWidthForTitle = 0.0f;
        float titleWidth = 0.0f;
        boolean titleShowEllipsis = false;
        boolean tooLittleSpaceForEverything = false;
        boolean titleSuppressed = false; //is the layout unable to show the title although it was set
        String titleToShow = "";
        int iconWidth = 0;
        int iconHeight = 0;

        if (mDisplayWidth == 0 || mDisplayHeight == 0) {
            // no display size -> can't do a real layout run
            mImageView.setVisibility(View.GONE);
            mTextView.setVisibility(View.GONE);
            return;
        }

        // get the with and the height of the icon
        if (mIcon != null) {
            // try to get the values from the icon (works e.g. for bitmaps)
            iconWidth = mIcon.getIntrinsicWidth();
            iconHeight = mIcon.getIntrinsicHeight();

            // Here we differ from iOS! Since here we can get drawables without size.
            // check if height and width were available
            if (iconWidth <= 0 || iconHeight <= 0) {
                // we didn't get a height and width for the icon (maybe a drawable?)
                // set a default! as defined in the styleguide
                if (mDisplayHeight < PDEBuildingUnits.exactPixelFromBU(3.5f)) {
                    // small or medium sized button 53% of 3 BU
                    iconWidth = iconHeight = (int) Math.min(
                            PDEBuildingUnits.exactPixelFromBU(3)*0.53f,
                            Math.min(mDisplayHeight, mDisplayWidth));
                } else if (mDisplayHeight > PDEBuildingUnits.exactPixelFromBU(4.5f)) {
                    // XL-Buttons - that is not in the styleguide!! (but small icons for big buttons don't look right)
                    // set a default: minimum of height or width of the button minus two BU space(r)
                    iconWidth = iconHeight = (int) Math.min(mDisplayHeight - PDEBuildingUnits.exactBU() * 2,
                            mDisplayWidth - PDEBuildingUnits.exactBU() * 2);

                } else {
                    // large sized button 60% of 4 BU
                    iconWidth = iconHeight = (int) (PDEBuildingUnits.exactPixelFromBU(4)*0.6f);
                }

            }
        }

        //check if we have space for the title
        if (iconWidth + 2 * saveArea < mDisplayWidth) {
            // icon doesn't fill up the space alone -> possible to show title
            if( !TextUtils.isEmpty(mTitle) ) {
                // title is set
                // calculate available size for the title
                if (iconWidth > 0) {
                    // there is an icon -> keep saveArea space to it
                    availableWidthForTitle = mDisplayWidth - 3 * saveArea - iconWidth;
                } else {
                    // no icon, only left and right save area
                    availableWidthForTitle = mDisplayWidth - 2 * saveArea;
                }

                // fit the title into available space (if necessary)
                if (availableWidthForTitle > mTextWidthFirstLetterPlusEllipsis) {
                    // sufficient space to show at least one character and "..."
                    titleToShow = mTitle; //take full title, it will be truncated at the right place
                    titleShowEllipsis = true;
                    titleWidth =  Math.min(mTextSizeFull.width(), availableWidthForTitle) ;
                } else if (availableWidthForTitle + saveArea > mTextWidthFirstLetterPlusEllipsis){
                    // to less space -> don't enforce right saveArea, but still show first char plus "..."
                    titleToShow = mFirstLetterEllipsis;
                    titleShowEllipsis = false;
                    //titleWidth =  availableWidthForTitle + saveArea ;
                    titleWidth = mTextWidthFirstLetterPlusEllipsis;
                    tooLittleSpaceForEverything = true;
                } else if (availableWidthForTitle + saveArea > mTextWidthFirstLetter) {
                    // to less space -> show only first letter
                    titleToShow = mFirstLetter;
                    titleShowEllipsis = false;
                    //titleWidth =  availableWidthForTitle + saveArea ;
                    titleWidth = mTextWidthFirstLetter;
                    tooLittleSpaceForEverything = true;
                } else{
                    // to less space even for one char. don't show title
                    titleToShow = null;
                    titleWidth = 0.0f;
                    titleShowEllipsis = false;
                    // note: don't set tooLittleSpaceForEverything in this case!
                    if (!TextUtils.isEmpty(mTitle)) {
                        titleSuppressed = true;
                    }
                }
            }
        } else {
            // no space for the title
            titleWidth = 0.0f;
            titleShowEllipsis = false;
            titleToShow = null;
            if (!TextUtils.isEmpty(mTitle)) {
                titleSuppressed = true;
            }
        }


        // calculate the width for the button content
        if (iconWidth > 0 && titleWidth > 0) {
            fullWidth = iconWidth + saveArea + titleWidth;
        } else if (iconWidth > 0) {
            fullWidth = iconWidth;
        } else {
            fullWidth = titleWidth;
        }

        if (titleSuppressed) {
            // if the title can't be shown -> center the icon
            iconPosition.x = (mDisplayWidth - iconWidth) / 2;
        } else {

            // calculate left boarder width (or left save area)
            if (tooLittleSpaceForEverything) {
                // no alignment necessary since there is no space for it
                leftBorder = saveArea;
            } else {
                // do some alignment
                if (mAlignment == PDEConstants.PDEAlignment.PDEAlignmentLeft
                        || mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeft) {
                    leftBorder = saveArea;
                } else if (mAlignment == PDEConstants.PDEAlignment.PDEAlignmentCenter
                        && (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached
                        || mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentRightAttached)) {
                    leftBorder = (mDisplayWidth - fullWidth) / 2.0f;
                } else if (mAlignment == PDEConstants.PDEAlignment.PDEAlignmentRight
                        || mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentRight) {
                    leftBorder = (mDisplayWidth - (fullWidth + saveArea));
                } else {
                    Log.d(LOG_TAG,"Should not happen!");
                }
            }

            // calc text x position
            if (mAlignment == PDEConstants.PDEAlignment.PDEAlignmentCenter) {
                if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeft) {
                    // icon very left - text centered
                    iconPosition.x = leftBorder;
                    if (iconWidth > 0) {
                        // there is an icon
                        textPosition.x = iconPosition.x + iconWidth + ((mDisplayWidth - iconPosition.x - iconWidth - titleWidth) / 2.0f) ;
                    } else {
                        // there is no icon
                        textPosition.x = (mDisplayWidth - fullWidth) / 2.0f;
                    }
                } else if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached) {
                    // text and icon centered (icon left of text)
                    iconPosition.x = leftBorder;
                    if (iconWidth > 0) {
                        // there is an icon
                        textPosition.x = iconPosition.x + iconWidth + saveArea ;
                    } else {
                        // there is no icon
                        textPosition.x = iconPosition.x;
                    }
                } else if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentRight) {
                    // icon at the very right - text centered
                    iconPosition.x = mDisplayWidth - iconWidth - saveArea;
                    textPosition.x = (iconPosition.x - titleWidth) / 2.0f;
                } else /*mIconAlignment == DTButtonLayerForegroundIconAlignmentRightAttached*/ {
                    // icon and text centered (icon at right of text)
                    iconPosition.x = leftBorder;
                    if (titleWidth > 0.0f) {
                        iconPosition.x += saveArea + titleWidth;
                    }
                    textPosition.x = leftBorder;
                }
            } else if (mAlignment == PDEConstants.PDEAlignment.PDEAlignmentLeft) {
                if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeft) {
                    // icon and text on the very left
                    iconPosition.x = leftBorder;
                    if (iconWidth > 0) {
                        // there is an icon
                        textPosition.x = iconPosition.x + iconWidth + saveArea ;
                    } else {
                        // there is no icon
                        textPosition.x = saveArea;
                    }
                } else if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached) {
                    // icon and text on the very left
                    iconPosition.x = leftBorder;
                    if (iconWidth > 0) {
                        // there is an icon
                        textPosition.x = iconPosition.x + iconWidth + saveArea ;
                    } else {
                        // there is no icon
                        textPosition.x = saveArea;
                    }
                } else if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentRight) {
                    // icon on very right, text on very left
                    iconPosition.x = mDisplayWidth - iconWidth - saveArea;
                    textPosition.x = saveArea;

                } else /*mIconAlignment == DTButtonLayerForegroundIconAlignmentRightAttached*/ {
                    // text left, icon right next to it
                    iconPosition.x = leftBorder;
                    if (titleWidth > 0.0f) {
                        iconPosition.x += saveArea + titleWidth;
                    }
                    textPosition.x = saveArea;
                }
            } else /*if (mTextAlignment == PDEConstants.PDEAlignment.PDEAlignmentRight)*/ {
                if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeft) {
                    //icon on the very left, text on the very right
                    iconPosition.x = leftBorder;
                    textPosition.x = mDisplayWidth - titleWidth - saveArea;
                } else if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached) {
                    // text on the very right, icon to the left of it
                    iconPosition.x = leftBorder;
                    textPosition.x = mDisplayWidth - titleWidth - saveArea;

                } else if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentRight) {
                    // text on the right, icon to the right of it
                    iconPosition.x = leftBorder;
                    if (titleWidth > 0.0f) {
                        iconPosition.x += saveArea + titleWidth;
                    }
                    textPosition.x = leftBorder;
                } else /*mIconAlignment == DTButtonLayerForegroundIconAlignmentRightAttached*/ {
                    // text on the right, icon to the right of it
                    // text on the right, icon to the right of it
                    iconPosition.x = leftBorder;
                    if (titleWidth > 0.0f) {
                        iconPosition.x += saveArea + titleWidth;
                    }
                    textPosition.x = leftBorder;
                }
            }
        }

        // calculate y position of the icon
        iconPosition.y = buttonYCenter - PDEBuildingUnits.roundToScreenCoordinates(iconHeight / 2.0f);

        // todo here it jumps between the two variant -> there is a small variation in the calculation (can it be corrected?)
        if (mDisplayHeight > mFontCapHeight) {
            // center font in the display - as font hight we use the capsheigt
            textPosition.y = PDEBuildingUnits.roundToScreenCoordinates(buttonYCenter - mFontTopOverallHeight + mFontCapHeight / 2.0f);
        } else {
            // available space for font is less then the space a capital character needs - align baseline with bottom edge
            // of the button / display
            textPosition.y = PDEBuildingUnits.roundToScreenCoordinates(mDisplayHeight - (mFontOverallHeight - mFontAscenderHeight));
            //textPosition.y = 0;
        }


        //----- apply values -----


        // hide or show the layers
        if (mIcon != null && iconWidth > 0 && titleWidth > 0) {
            mImageView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.VISIBLE);
        } else if (mIcon != null && iconWidth > 0) {
            mImageView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
        } else {
            mImageView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        }

        // set icon position
        PDEAbsoluteLayout.LayoutParams imageViewParams = (PDEAbsoluteLayout.LayoutParams) mImageView.getLayoutParams();
        imageViewParams.x = PDEBuildingUnits.roundToScreenCoordinates(iconPosition.x);
        imageViewParams.y = PDEBuildingUnits.roundToScreenCoordinates(iconPosition.y);
        imageViewParams.width = iconWidth;
        imageViewParams.height = iconHeight;
        mImageView.setLayoutParams(imageViewParams);


        // set text position
        PDEAbsoluteLayout.LayoutParams textViewParams = (PDEAbsoluteLayout.LayoutParams) mTextView.getLayoutParams();
        textViewParams.x =  PDEBuildingUnits.roundToScreenCoordinates(textPosition.x);
        textViewParams.y =  PDEBuildingUnits.roundToScreenCoordinates(textPosition.y);
        textViewParams.width = (int) FloatMath.ceil(titleWidth);
        textViewParams.height = PDEBuildingUnits.roundToScreenCoordinates(mFontOverallHeight)+1;

        mTextView.setLayoutParams(textViewParams);

        if(DEBUGPARAMS){
            Log.d(LOG_TAG,"textview "+textViewParams.x+", "+textViewParams.y+" - "+textViewParams.width+" by " +
                          ""+textViewParams.height+" buttonsize: "+mDisplayWidth+", "+mDisplayHeight+" textFullsize: " +
                          ""+mTextSizeFull.width());

            if(mTextSizeFull.width()>textViewParams.width){
                Log.d(LOG_TAG,"textFullsize: "+mTextSizeFull.width()+" ViewWidth: "+textViewParams.width+" savearea: " +
                              ""+saveArea);
            }
        }

        if (titleShowEllipsis) {
            // three dots at the end (if to less space)
            mTextView.setEllipsize(true);

        } else {
            // no truncation, no three dots (just clipping)
            mTextView.setEllipsize(false);
        }

        // copy in title
        mTextView.setText(titleToShow);


    }


}
