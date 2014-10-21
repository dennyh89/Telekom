/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.buttons;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import de.telekom.pde.codelibrary.ui.PDEConstants;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.elementwrappers.PDEIconView;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEAgentHelper;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEButtonPadding;
import de.telekom.pde.codelibrary.ui.components.helpers.PDEComponentHelpers;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameter;
import de.telekom.pde.codelibrary.ui.components.helpers.parameters.PDEParameterDictionary;
import de.telekom.pde.codelibrary.ui.elements.icon.PDEDrawableIcon;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.helpers.PDEDictionary;
import de.telekom.pde.codelibrary.ui.helpers.PDEFontHelpers;
import de.telekom.pde.codelibrary.ui.helpers.PDETrace;
import de.telekom.pde.codelibrary.ui.helpers.PDETypeface;
import de.telekom.pde.codelibrary.ui.helpers.PDEUtils;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

/**
 * @brief Button-Layer which shows icon and/or text in the PDEButton.
 *
 *  Extends absolute layout because of our complex layouting process, where we calculate our sizes and position
 *  ourselves. Using the Android Linear Layout was slower, due to other calculation processes.
 */
class PDEButtonLayerForegroundIconText extends PDEAbsoluteLayout implements PDEButtonLayerInterface {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEButtonLayerForegroundIconText.class.getSimpleName();
    private final static boolean DEBUG_PARAMS = false;
    private final static boolean SHOW_DEBUG_LOGS = false;
    private final static boolean DEBUG_SHADOWS = false;
    private final static boolean DEBUG_SHOW_COMPONENTS = false;


    // font modes
    public enum PDEButtonLayerForegroundIconTextFontMode {
        Undefined,
        Automatic,
        Styleguide,
        Fixed
    }


    @SuppressWarnings("unused")
    private class MetaFontSize {
        public float size = 0.0f;
        public boolean isCapHeight = false;
        public float pointSize = 0.0f;
    }


    @SuppressWarnings("unused")
    private class TextInfo {
        public String base_text;
        public float base_fontPixelSize;
        public float textWidthFirstLetterPlusEllipsis;
        public float textWidthFirstLetter;
        public Rect textSizeFull;
        public String firstLetterEllipsis;
        public String firstLetter;


        /**
         * @brief Public constructor.
         */
        public TextInfo() {
            init();
        }


        /**
         * @brief Private init function to set the default values.
         */
        private void init() {
            // call reset to set default values
            reset();
        }


        /**
         * @brief Reset the TextInfo to the default values.
         */
        public void reset() {
            base_text = "";
            base_fontPixelSize = 0.0f;
            textWidthFirstLetterPlusEllipsis = 0.0f;
            textWidthFirstLetter = 0.0f;
            textSizeFull = new Rect(0, 0, 0, 0);
            firstLetterEllipsis = "";
            firstLetter = "";
        }
    }


    private class FontModeSize {
        PDEButtonLayerForegroundIconTextFontMode mode = PDEButtonLayerForegroundIconTextFontMode.Undefined;
        float size = 0.0f;
    }


    // parameters needed
    private PDEParameterDictionary mParameters;
    private PDEParameter mParamColor;
    private PDEParameter mParamTitleColor;
    private PDEParameter mParamTitleShadowColor;
    private PDEParameter mParamTitleShadowOffset;


    // content layers
    private PDEDrawText mTextView;
    private PDEIconView mDrawableIconWrapperView;

    // configuration
    private PDEColor mDefaultColor;

    // Icon configuration

    // basic button configuration
    private float mIconToTextHeightRatio;
    private Boolean mIconColored;

    // text configuration
    private String mTitle;
    private PDETypeface mFont;

    private boolean mTextHasShadow;
    private PDEColor mTextOnTransparentColor;
    private PDEColor mBackgroundColor;
    private PDEConstants.PDEAlignment mAlignment;
    private PDEButton.PDEButtonIconAlignment mIconAlignment;
    private int mHorizontalPadding;

    // internal font and title configuration
    private MetaFontSize mFontMetaSize;
    private TextInfo mTextInfo;

    private FontModeSize mFontModeSizeFromParameters;

    // layout info
    private float mDisplayWidth;
    private float mDisplayHeight;

    // agent helpers
    private PDEAgentHelper mAgentHelper;

    private Context mContext;

    private LruCache<Integer, Integer> mTextWidthSizeCache;


    /**
     * Constructor
     */
    @SuppressWarnings("unused")
    public PDEButtonLayerForegroundIconText(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }


    /**
     * Constructor
     */
    @SuppressWarnings("unused")
    public PDEButtonLayerForegroundIconText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }


    /**
     * Constructor
     */
    @SuppressWarnings("unused")
    public PDEButtonLayerForegroundIconText(Context context) {
        super(context);
        init(context);
    }


    /**
     * @brief Internal init function.
     */
    private void init(Context context) {
        // init
        mParameters = null;
        mDisplayWidth = 0.0f;
        mDisplayHeight = 0.0f;
        mTextHasShadow = false;
        mIconColored = false;
        mIconToTextHeightRatio = PDEConstants.DefaultPDEButtonIconToTextHeightRatio;
        mHorizontalPadding = PDEBuildingUnits.pixelFromBU(2.0f);

        mDefaultColor = PDEColor.DTUIInteractiveColor();
        mTextOnTransparentColor = PDEColor.DTUITextColor();
        mBackgroundColor = PDEColor.DTUIBackgroundColor();

        mTextWidthSizeCache = new LruCache<Integer, Integer>(5);

        if (DEBUG_SHOW_COMPONENTS) {
            setBackgroundColor(0x9900ffff);
        }

        // default button data
        mTitle = "";
        mFontModeSizeFromParameters = new FontModeSize();
        mFontMetaSize = new MetaFontSize();
        mTextInfo = new TextInfo();

        mAlignment = null;
        mIconAlignment = null;

        mContext = context;

        // set layer attributes
        setClipChildren(true);
        setClipToPadding(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            PDEUtils.setLayerTypeSoftwareToView(this);
        }

        // agent helper
        mAgentHelper = new PDEAgentHelper();

        // create label layer
        mTextView = new PDEDrawText(context);
        mTextView.setBackgroundColor(Color.TRANSPARENT);
        if (DEBUG_SHOW_COMPONENTS) {
            mTextView.setBackgroundColor(0x9900ff00);
        }
        mTextView.setClickable(false);
        mTextView.setFocusable(false);

        // add it
        addView(mTextView);

        // create icon view (we hold view all the time, only the icon in the view(drawable) is replaced
        mDrawableIconWrapperView = new PDEDrawableIcon().getWrapperView();
        //mDrawableIconWrapperView.setBackgroundColor(0x66ff00ff);
        addView(mDrawableIconWrapperView, 0, 0);

        // shadow is initially disabled on both text and Icon
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
        return this;
    }


    /**
     * @brief Process agent events.
     */
    public void agentEvent(PDEEvent event) {
        boolean needsUpdate;

        // pass on agent events to agent helper
        needsUpdate = mAgentHelper.processAgentEvent(event);

        // update if necessary
        if (needsUpdate) {
            // update animated parameters on change
            updateColors();
        }
    }

//----- property handling ----------------------------------------------------------------------------------------------


    /**
     * @brief Set hints.
     * <p/>
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
     * <p/>
     * Should only be called from main button class, it handles the state, and also does change management.
     */
    public void setParameters(PDEParameterDictionary parameters, boolean force) {
        PDETrace.beginSection("setParameters");
        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "setParameters begin");
        }
        PDEParameterDictionary oldParams;

        // for change management, keep around the old params
        oldParams = mParameters;

        // completely copy the new ones for further change management
        mParameters = parameters.copy();

        // check for color or text color changes
        if (force
            || !PDEParameterDictionary.areParametersEqual(mParameters,
                                                          oldParams,
                                                          PDEButton.PDEButtonParameterColor)
            || !PDEParameterDictionary.areParametersEqual(mParameters,
                                                          oldParams,
                                                          PDEButton.PDEButtonParameterTitleColor)) {
            this.prepareTitleColor();
        }

        // non-animated properties that nevertheless need external change management
        if (force
            || !PDEParameterDictionary.areParametersEqual(mParameters,
                                                          oldParams,
                                                          PDEButton.PDEButtonParameterIcon)) {
            this.prepareIcon();
        }

        // non-animated properties do their change management internally
        this.prepareTitle();
        this.prepareFont();
        this.prepareIconToTextHeightRatio();
        this.prepareInternalLayout();
        this.prepareIconColored();
        this.prepareHorizontalPadding();

        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "setParameters end");
        }

        PDETrace.endSection();
    }


    @Override
    public void collectHints(PDEDictionary hints) {
        // no hints to collect for this layer

    }


    public void collectButtonPaddingRequest(PDEButtonPadding padding) {
        //no additional padding for the text layer - it is an internal element only!
    }

//----- non-parameters: preparation ------------------------------------------------------------------------------------


    /**
     * @brief Set the (animated) title color.
     * <p/>
     * If no title color is given, the title color is calculated from a) the main color, and b) the hints, and c) the
     * system preset (dark or light system).
     * <p/>
     * The shadow color is also calculated; the shadow color depends solely on the main color (and does only change
     * in between states)
     */
    private void prepareTitleColor() {
        // set the new values
        mParamColor.setWithParameter(mParameters.parameterForName(PDEButton.PDEButtonParameterColor));
        mParamTitleColor.setWithParameter(mParameters.parameterForName(PDEButton.PDEButtonParameterTitleColor));

        // shadow color and offset are at the moment internal only; clear old stuff
        mParamTitleShadowColor.removeAllObjects();
        mParamTitleShadowOffset.removeAllObjects();

        // debug output
        if (DEBUG_PARAMS) {
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

        // calculate missing shadow colors from colors (no base state propagation necessary,
        // this is a virtual parameter)
        PDEComponentHelpers.fillTitleShadowColors(mParamTitleShadowColor, mParamColor, mBackgroundColor);

        // calculate missing shadow offsets from colors (no base state propagation necessary,
        // this is a virtual parameter)
        PDEComponentHelpers.fillTitleShadowOffsets(mParamTitleShadowOffset, mParamColor, mBackgroundColor);

        // debug output
        if (DEBUG_PARAMS) {
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
     * <p/>
     * Title is non-animated and uses the base parameter
     */
    private void prepareTitle() {
        String title;

        // get the title from the parameters
        title = mParameters.parameterValueForName(PDEButton.PDEButtonParameterTitle);

        if ((TextUtils.isEmpty(mTitle) && TextUtils.isEmpty(title))
            || (!TextUtils.isEmpty(mTitle) && mTitle.equals(title))) { //nothing to do title didn't change
            return;
        }

        // remember
        mTitle = title;
        mTextWidthSizeCache.evictAll();

        // update font size (this calculates some helper values) and layout it
        updateFontSize(mDisplayHeight, true, null, null);
        performLayout();
    }


    /**
     * @brief Private function - prepare internal layout.
     * <p/>
     * IconOnRightSide and IconTextAlignment are non-animated and uses the base parameter
     */
    private void prepareInternalLayout() {
        PDEConstants.PDEAlignment alignment;
        PDEButton.PDEButtonIconAlignment iconAlignment;

        String textAlignmentString;
        String iconAlignmentString;

        // get the title from the parameters
        textAlignmentString = mParameters.parameterValueForNameWithDefault(PDEButton.PDEButtonParameterAlignment,
                                                                           PDEConstants.PDEAlignmentStringCenter);

        // parse value
        if (textAlignmentString.equals(PDEConstants.PDEAlignmentStringLeft)) {
            alignment = PDEConstants.PDEAlignment.PDEAlignmentLeft;
        } else if (textAlignmentString.equals(PDEConstants.PDEAlignmentStringCenter)) {
            alignment = PDEConstants.PDEAlignment.PDEAlignmentCenter;
        } else if (textAlignmentString.equals(PDEConstants.PDEAlignmentStringRight)) {
            alignment = PDEConstants.PDEAlignment.PDEAlignmentRight;
        } else {
            alignment = PDEConstants.PDEAlignment.PDEAlignmentCenter;
        }

        iconAlignmentString = mParameters.parameterValueForNameWithDefault(PDEButton.PDEButtonParameterIconAlignment,
                                                                           PDEConstants.PDEAlignmentStringLeftAttached);

        // parse value
        if (iconAlignmentString.equals(PDEConstants.PDEAlignmentStringLeft)) {
            iconAlignment = PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeft;
        } else if (iconAlignmentString.equals(PDEConstants.PDEAlignmentStringRight)) {
            iconAlignment = PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentRight;
        } else if (iconAlignmentString.equals(PDEConstants.PDEAlignmentStringRightAttached)) {
            iconAlignment = PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentRightAttached;
        } else {
            iconAlignment = PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached;
        }

        // any change?
        if (alignment == mAlignment && iconAlignment == mIconAlignment) return;

        // remember
        mAlignment = alignment;
        mIconAlignment = iconAlignment;

        // do layout
        performLayout();
    }


    /**
     * @brief Private function - prepare the font.
     * <p/>
     * Preparation function for the font which evaluates the parameters. Note that we need to
     */
    private void prepareFont() {
        Object fontObject, fontSizeObject;
        PDEButtonLayerForegroundIconTextFontMode fontMode;
        float fontSize;
        String str;
        PDETypeface lFont = null;

        // get font parameters
        fontObject = mParameters.parameterObjectForName(PDEButton.PDEButtonParameterFont);
        fontSizeObject = mParameters.parameterObjectForName(PDEButton.PDEButtonParameterFontSize);

        // determine font
        if (fontObject instanceof PDETypeface) {
            lFont = (PDETypeface) (fontObject);
        }

        // determine font size
        if (fontSizeObject instanceof Number) {
            fontSize = ((Number) fontSizeObject).floatValue();
            fontMode = PDEButtonLayerForegroundIconTextFontMode.Fixed;
        } else if (fontSizeObject instanceof String) {
            // extract string
            str = (String) fontSizeObject;
            // button specific mode / fixed size?
            if (str.compareToIgnoreCase(PDEButton.PDEButtonParameterValueSizeAuto) == 0
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
                fontSize = PDEFontHelpers.parseFontSize(str, lFont, mContext.getResources().getDisplayMetrics());
                if (Float.isNaN(fontSize)) {
                    Log.e(LOG_TAG, "could not parse font string correctly: " + str);
                    fontSize = 24.0f;
                }
                fontMode = PDEButtonLayerForegroundIconTextFontMode.Fixed;
            }
        } else {
            fontSize = 0.0f;
            fontMode = PDEButtonLayerForegroundIconTextFontMode.Styleguide;
        }
        // changed?
        if (fontMode == mFontModeSizeFromParameters.mode
            && fontSize == mFontModeSizeFromParameters.size
            && lFont == mFont) {
            return;
        }
        // remember
        mFontModeSizeFromParameters.mode = fontMode;
        mFontModeSizeFromParameters.size = fontSize;
        //create font independent from size, so do it once here, or take parameter font
        if (lFont == null) {
            //use default font if we don't have a correct one !!!!!!!!!!
            mFont = PDETypeface.sDefaultFont;
        } else {
            // remember external font
            mFont = lFont;
        }

        mTextWidthSizeCache.evictAll();

        // update font size, and do layout
        updateFontSize(mDisplayHeight, true, null, null);
        performLayout();
    }


    /**
     * @brief Prepare the Icon.
     * <p/>
     * The Icon might be animated later; right now, it's not. Icon change checks are complicated and
     * won't run without actually loading the Icon, so make sure to check for parameter changes outside.
     */
    private void prepareIcon() {
        Object iconObject;

        // get the object
        iconObject = mParameters.parameterObjectForName(PDEButton.PDEButtonParameterIcon);

        mDrawableIconWrapperView.setIcon(iconObject);

        // update font size, and do layout
        updateIcon();
        performLayout();
    }


    private void prepareIconToTextHeightRatio() {
        float ratio;

        ratio = mParameters.parameterFloatForName(PDEButton.PDEButtonParameterIconToTextHeightRatio,
                                                  PDEConstants.DefaultPDEButtonIconToTextHeightRatio);

        // anything to do??
        if (ratio == mIconToTextHeightRatio) return;

        // remember
        mIconToTextHeightRatio = ratio;

        // update
        updateIcon();
        performLayout();
    }


    /**
     * @brief Update the Icon size
     */
    private void updateIcon() {
        PDEDrawableIcon drawableIcon = mDrawableIconWrapperView.getDrawableIcon();
        Point iconSize = new Point(0, 0);

        // Is there a icon we need to adjust
        if (drawableIcon.hasElementIcon()) {
            // does the icon-layer have a native size?
            if (drawableIcon.hasNativeSize()) {
                iconSize = drawableIcon.getNativeSize();
            } else {
                // mTextView will not have a font size until display size is set..
                if (mDisplayWidth == 0 || mDisplayHeight == 0) return;

                //calculate icon font height
                iconSize.y = PDEBuildingUnits.roundToScreenCoordinates(
                        PDEFontHelpers.getCapHeight(mFont, mTextView.getTextSize()) * mIconToTextHeightRatio);
                // the icon has the same width and height, so this assignment is correct
                // noinspection SuspiciousNameCombination
                iconSize.x = iconSize.y;
            }
        }
        //set icon size
        LayoutParams iconFontWrapperViewParams = (LayoutParams) mDrawableIconWrapperView.getLayoutParams();
        if (iconFontWrapperViewParams != null) {
            iconFontWrapperViewParams.width = PDEBuildingUnits.roundToScreenCoordinates(iconSize.x);
            iconFontWrapperViewParams.height = PDEBuildingUnits.roundToScreenCoordinates(iconSize.y);
            mDrawableIconWrapperView.setLayoutParams(iconFontWrapperViewParams);
        }
    }


    /**
     * @brief Prepare the Icon color mode.
     * <p/>
     * It won't change, so it's a global parameter.
     */
    private void prepareIconColored() {
        boolean colored;
        PDEDrawableIcon iconDrawable = mDrawableIconWrapperView.getDrawableIcon();

        if (!iconDrawable.hasElementIcon()) return;

        // extract
        colored = mParameters.parameterBoolForName(PDEButton.PDEButtonParameterIconColored);

        // any change?
        if (mIconColored == colored) return;

        // remember
        mIconColored = colored;

        // clear image color if not colored
        if (!mIconColored) {
            // clear out the color
            iconDrawable.setColorFilter(null);
        }

        // image layer only has a shadow if it's enabled, and the image is colorized
        if (mTextHasShadow && mIconColored) {
            // turn it on
            iconDrawable.setElementShadowEnabled(true);
        } else {
            // turn it off
            iconDrawable.setElementShadowEnabled(false);
        }

        // if the image is colored, we also need to update the colors
        if (mIconColored) {
            // colors need to be recalculated
            prepareTitleColor();
        }
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
        performLayout();
    }

//----- fixed parameters -----------------------------------------------------------------------------------------------


    /**
     * @brief Set the dark style hint.
     */
    private void setDefaultColor(PDEColor color) {
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
    private void setTextShadow(boolean hasShadow) {
        // any change?
        if (hasShadow == mTextHasShadow) return;

        PDEDrawableIcon iconDrawable = mDrawableIconWrapperView.getDrawableIcon();
        // remember
        mTextHasShadow = hasShadow;

        if (iconDrawable.hasElementIcon()) {
            // -> can't split it up, do it in update colors
            // image layer only has a shadow if it's enabled, and the image is colorized
            if (mTextHasShadow && mIconColored) {
                iconDrawable.setElementShadowEnabled(true);
            } else {
                // turn it off
                iconDrawable.setElementShadowEnabled(false);
            }
        }

        // eventually update colors to get correct shadow color
        if (mTextHasShadow) {
            updateColors();
        }
    }


    /**
     * @brief Remember transparent color (from hints)
     */
    private void setBackgroundColor(PDEColor color) {
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
    private void setTextOnTransparentColor(PDEColor color) {
        // any change?
        if (color == null && mTextOnTransparentColor == null) return;
        if (mTextOnTransparentColor != null && mTextOnTransparentColor.equals(color)) return;

        // remember
        mTextOnTransparentColor = color;

        // colors need to be recalculated
        prepareTitleColor();
    }

//----- animated parameter updates -------------------------------------------------------------------------------------


    /**
     * @brief Private function - update all colors.
     * <p/>
     * Title colors, shadow colors. All colors are precalculated and only animated.
     */
    private void updateColors() {
        PDEColor titleColor, shadowColor;
        PointF shadowOffset;
        PDEDrawableIcon iconDrawable;

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

        iconDrawable = mDrawableIconWrapperView.getDrawableIcon();

        // set Icon color if desired
        if (mIconColored && iconDrawable.hasElementIcon()) {
            iconDrawable.setElementIconColor(titleColor);
        }

        if (DEBUG_SHADOWS) Log.d(LOG_TAG, "shadowOffset x:" + shadowOffset.x + " y:" + shadowOffset.y);

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

        // set Icon shadow if shadow is on and Icon is colored
        if (mTextHasShadow && mIconColored && iconDrawable.hasElementIcon()) {
            iconDrawable.setElementShadowXOffset((int) shadowOffset.x);
            iconDrawable.setElementShadowYOffset((int) shadowOffset.y);
            iconDrawable.setElementShadowColor(shadowColor.newColorWithCombinedAlpha(64));
        }
    }


    /**
     * @brief Calculate the new font size and apply it.
     * <p/>
     * Also calculate helper values necessary later in the layout process. Important note: This function
     * does not issue a request layout on font changes - ensure a layout run is done properly outside of this function.
     * This avoids recursive calls of updateFontSize and updateLayout.
     */
    private void updateFontSize(float displayHeight, boolean apply, TextInfo textInfo, MetaFontSize fontMetaSize) {

        PDETrace.beginSection("updateFontSize");

        try {
            if (apply) {
                if (textInfo == null) {
                    textInfo = mTextInfo;
                }
                if (fontMetaSize == null) {
                    fontMetaSize = mFontMetaSize;
                }
            } else {
                if (fontMetaSize == null) {
                    fontMetaSize = new MetaFontSize();
                }
            }

            // ensure that textInfo isn't null
            if (textInfo == null) {
                textInfo = new TextInfo();
            }

            if (SHOW_DEBUG_LOGS) {
                Log.d(LOG_TAG, "updateFontSize");
            }

            float fontSize;
            boolean fontInCaps;
            PDEDrawableIcon drawableIcon = mDrawableIconWrapperView.getDrawableIcon();

            //only update when there is text or icon
            if (!drawableIcon.hasElementIcon() && TextUtils.isEmpty(mTitle)) {
                return;
            }

            if (displayHeight == 0.0f) {
                return;
            }

            // automatic modes calculate font size from height
            if (mFontModeSizeFromParameters.mode == PDEButtonLayerForegroundIconTextFontMode.Automatic) {
                // height in caps
                fontSize = displayHeight / 3.0f;
                fontInCaps = true;
            } else if (mFontModeSizeFromParameters.mode == PDEButtonLayerForegroundIconTextFontMode.Styleguide) {
                // standardize the font to 4*BU, 3*BU, 2.5*BU button size height
                if (displayHeight > PDEBuildingUnits.exactPixelFromBU(3.5f)) {
                    // display height > 3,5*BU -> font for display height of 4*BU
                    fontSize = PDEBuildingUnits.pixelFromBU(4.0f) / 3.0f;
                } else if (displayHeight > PDEBuildingUnits.exactPixelFromBU(2.75f)) {
                    // 2,75*BU < display height <= 3,5*BU -> font for display height of 3*BU
                    fontSize = PDEBuildingUnits.exactBU();
                } else {
                    // display height < 2,75*BU -> font for display height of 2,5*BU
                    fontSize = PDEBuildingUnits.pixelFromBU(2.5f) / 3.0f;
                }
                fontInCaps = true;
            } else if (mFontModeSizeFromParameters.mode == PDEButtonLayerForegroundIconTextFontMode.Fixed) {
                // height in native font size
                fontSize = mFontModeSizeFromParameters.size;
                fontInCaps = false;
            } else {
                // unknown mode -> do nothing
                return;
            }

            if (fontSize != fontMetaSize.size
                || fontInCaps == fontMetaSize.isCapHeight
                || TextUtils.equals(mTitle, textInfo.base_text)
                    ) {
                if (SHOW_DEBUG_LOGS) {
                    Log.d(LOG_TAG, "updateFontSize after changed return");
                }

                // remember
                fontMetaSize.size = fontSize;
                fontMetaSize.isCapHeight = fontInCaps;

                // eventually convert caps height to real height
                if (fontInCaps) {
                    fontSize = PDEFontHelpers.calculateFontSize(mFont, fontSize);
                }

                // assure readable font size
                fontSize = PDEFontHelpers.assureReadableFontSize(mFont, fontSize);
                fontMetaSize.pointSize = fontSize;

                // do font calculations once
                // calculate necessary layouting helpers once here (they don't change necessarily)
                if (TextUtils.isEmpty(mTitle)) {
                    textInfo = new TextInfo();
                } else {
                    textInfo.base_fontPixelSize = fontSize;
                    textInfo.base_text = mTitle;

                    if (mTitle.length() == 1) {
                        //if we have only one letter we don't  need an ellipsis
                        textInfo.firstLetterEllipsis = textInfo.firstLetter = mTitle;

                    } else if (mTitle.length() == 2) {
                        // don't show an ellipsis for 2 chars either
                        textInfo.firstLetter = mTitle.substring(0, 1);
                        textInfo.firstLetterEllipsis = mTitle.substring(0, 2);
                    } else {
                        textInfo.firstLetterEllipsis = String.format("%s...", mTitle.substring(0, 1));
                        textInfo.firstLetter = mTitle.substring(0, 1);
                    }

                    // do text calculations
                    textInfo.textWidthFirstLetterPlusEllipsis = PDEFontHelpers.getTextViewBounds(
                            textInfo.firstLetterEllipsis, mFont, fontSize).width();
                    textInfo.textWidthFirstLetter = PDEFontHelpers.getTextViewBounds(
                            textInfo.firstLetter, mFont, fontSize).width();
                    textInfo.textSizeFull = PDEFontHelpers.getTextViewBounds(mTitle, mFont, fontSize);

                    if (SHOW_DEBUG_LOGS) {
                        Log.d(LOG_TAG, "mTextSizeFull " + textInfo.textSizeFull.width() + " from " + mTitle + " "
                                       + fontSize + " " + mFont.getName());
                    }
                }

                if (DEBUG_PARAMS) {
                    Log.d(LOG_TAG, "mTextWidthFirstLetterPlusEllipsis " + textInfo.textWidthFirstLetterPlusEllipsis);
                    Log.d(LOG_TAG, "mTextWidthFirstLetter " + textInfo.textWidthFirstLetter);
                    Log.d(LOG_TAG, "mTextSizeFull " + textInfo.textSizeFull.flattenToString());
                }
            }

            if (apply) {
                mTextInfo = textInfo;
                mFontMetaSize = fontMetaSize;
            }
        } finally {
            PDETrace.endSection();
        }
    }


    /**
     * @brief Calculate the new font size and apply it.
     * <p/>
     * Also calculate helper values necessary later in the layout process. Important note: This function
     * does not issue a request layout on font changes - ensure a layout run is done properly outside of this function.
     * This avoids recursive calls of updateFontSize and updateLayout.
     */
    private float calculateFontSize(float displayHeight) {

        PDETrace.beginSection("updateFontSize");

        try {

            if (SHOW_DEBUG_LOGS) {
                Log.d(LOG_TAG, "updateFontSize");
            }

            float fontSize;
            boolean fontInCaps;
            PDEDrawableIcon drawableIcon = mDrawableIconWrapperView.getDrawableIcon();

            //only update when there is text or icon
            if (!drawableIcon.hasElementIcon() && TextUtils.isEmpty(mTitle)) {
                return 0.0f;
            }

            if (displayHeight == 0.0f) {
                return 0.0f;
            }

            // automatic modes calculate font size from height
            if (mFontModeSizeFromParameters.mode == PDEButtonLayerForegroundIconTextFontMode.Automatic) {
                // height in caps
                fontSize = displayHeight / 3.0f;
                fontInCaps = true;
            } else if (mFontModeSizeFromParameters.mode == PDEButtonLayerForegroundIconTextFontMode.Styleguide) {
                // standardize the font to 4*BU, 3*BU, 2.5*BU button size height
                if (displayHeight > PDEBuildingUnits.exactPixelFromBU(3.5f)) {
                    // display height > 3,5*BU -> font for display height of 4*BU
                    fontSize = PDEBuildingUnits.pixelFromBU(4.0f) / 3.0f;
                } else if (displayHeight > PDEBuildingUnits.exactPixelFromBU(2.75f)) {
                    // 2,75*BU < display height <= 3,5*BU -> font for display height of 3*BU
                    fontSize = PDEBuildingUnits.exactBU();
                } else {
                    // display height < 2,75*BU -> font for display height of 2,5*BU
                    fontSize = PDEBuildingUnits.pixelFromBU(2.5f) / 3.0f;
                }
                fontInCaps = true;
            } else if (mFontModeSizeFromParameters.mode == PDEButtonLayerForegroundIconTextFontMode.Fixed) {
                // height in native font size
                fontSize = mFontModeSizeFromParameters.size;
                fontInCaps = false;
            } else {
                // unknown mode -> do nothing
                return 0.0f;
            }

            // eventually convert caps height to real height
            if (fontInCaps) {
                fontSize = PDEFontHelpers.calculateFontSize(mFont, fontSize);
            }

            // assure readable font size
            fontSize = PDEFontHelpers.assureReadableFontSize(mFont, fontSize);

            return fontSize;

        } finally {
            PDETrace.endSection();
        }
    }

//----- layout ---------------------------------------------------------------------------------------------------------


    /**
     * @brief Perform the actual layout tasks.
     * <p/>
     * Called when anything has changed. Performs the new layout for all elements and sets it
     * accordingly. The member variables are already set correctly outside.
     */
    private void performLayout() {

        PDETrace.beginSection("performLayout");

        try {

            if (SHOW_DEBUG_LOGS) {
                Log.d(LOG_TAG, "performLayout");
            }

            PointF textPosition = new PointF(.0f, .0f);
            PointF iconPosition = new PointF(.0f, .0f);

            float fullWidth; // button width
            float saveArea = PDEBuildingUnits.exactBU(); //single margin
            float leftBorder = 0.0f;
            float outerDistanceLeft = mHorizontalPadding;
            float outerDistanceRight = mHorizontalPadding;
            float outerDistanceLeftRight;

            ViewGroup innerLayout;
            ViewGroup overlaySlot;
            overlaySlot = (ViewGroup) getParent();

            if (overlaySlot != null) {
                innerLayout = (ViewGroup) overlaySlot.getParent().getParent();

                if (((ViewGroup) innerLayout.findViewById(R.id.pdebutton_overlay_slot_left)).getChildCount() != 0) {
                    outerDistanceLeft = PDEBuildingUnits.exactBU();
                }
                if (((ViewGroup) innerLayout.findViewById(R.id.pdebutton_overlay_slot_right)).getChildCount() != 0) {
                    outerDistanceRight = PDEBuildingUnits.exactBU();
                }
            }

            outerDistanceLeftRight = outerDistanceLeft + outerDistanceRight;

            float buttonYCenter = PDEBuildingUnits.roundToScreenCoordinates(mDisplayHeight / 2.0f);
            float availableWidthForTitle;
            float titleWidth = 0.0f;
            boolean titleShowEllipsis = false;
            boolean tooLittleSpaceForEverything = false;
            boolean titleSuppressed = false; //is the layout unable to show the title although it was set
            String titleToShow = "";
            Point iconSize = new Point(0, 0);

            if (mDisplayWidth == 0 || mDisplayHeight == 0) {
                // no display size -> can't do a real layout run
                mDrawableIconWrapperView.setVisibility(View.GONE);
                mTextView.setVisibility(View.GONE);
                return;
            }

            // set the font and font size to the text layer
            mTextView.setTypeface(mFont.getTypeface());
            mTextView.setTextSize(mFontMetaSize.pointSize);

            //text changed -> update icon
            updateIcon();

            //get icon size
            LayoutParams iconFontWrapperViewParams = (LayoutParams) mDrawableIconWrapperView.getLayoutParams();
            iconSize.x = iconFontWrapperViewParams.width;
            iconSize.y = iconFontWrapperViewParams.height;

            //check if we have space for the title
            if (iconSize.x + outerDistanceLeftRight < mDisplayWidth) {
                // Icon doesn't fill up the space alone -> possible to show title
                if (!TextUtils.isEmpty(mTitle)) {
                    // title is set
                    // calculate available size for the title
                    if (iconSize.x > 0) {
                        // there is an Icon -> keep saveArea space to it
                        availableWidthForTitle = mDisplayWidth - outerDistanceLeftRight - saveArea - iconSize.x;
                    } else {
                        // no Icon, only left and right save area
                        availableWidthForTitle = mDisplayWidth - outerDistanceLeftRight;
                    }

                    // fit the title into available space (if necessary)
                    if (availableWidthForTitle >= mTextInfo.textSizeFull.width()
                        || availableWidthForTitle > mTextInfo.textWidthFirstLetterPlusEllipsis) {
                        // sufficient space to show at least one character and "..."
                        titleToShow = mTitle; //take full title, it will be truncated at the right place
                        titleShowEllipsis = true;
                        titleWidth = Math.min(mTextInfo.textSizeFull.width(), availableWidthForTitle);
                    } else if (availableWidthForTitle + outerDistanceRight
                               > mTextInfo.textWidthFirstLetterPlusEllipsis) {
                        // to less space -> don't enforce right saveArea, but still show first char plus "..."
                        titleToShow = mTextInfo.firstLetterEllipsis;
                        titleShowEllipsis = false;
                        //titleWidth =  availableWidthForTitle + saveArea ;
                        titleWidth = mTextInfo.textWidthFirstLetterPlusEllipsis;
                        tooLittleSpaceForEverything = true;
                    } else if (availableWidthForTitle + outerDistanceRight > mTextInfo.textWidthFirstLetter) {
                        // to less space -> show only first letter
                        titleToShow = mTextInfo.firstLetter;
                        titleShowEllipsis = false;
                        //titleWidth =  availableWidthForTitle + saveArea ;
                        titleWidth = mTextInfo.textWidthFirstLetter;
                        tooLittleSpaceForEverything = true;
                    } else {
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
            if (iconSize.x > 0 && titleWidth > 0) {
                fullWidth = iconSize.x + saveArea + titleWidth;
            } else if (iconSize.x > 0) {
                fullWidth = iconSize.x;
            } else {
                fullWidth = titleWidth;
            }

            if (titleSuppressed) {
                // if the title can't be shown -> center the Icon
                iconPosition.x = (mDisplayWidth - iconSize.x) / 2.0f;
            } else {

                // calculate left boarder width (or left save area)
                if (tooLittleSpaceForEverything) {
                    // no alignment necessary since there is no space for it
                    leftBorder = outerDistanceLeft;
                } else {
                    // do some alignment
                    if (mAlignment == PDEConstants.PDEAlignment.PDEAlignmentLeft
                        || mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeft) {
                        leftBorder = outerDistanceLeft;
                    } else if (mAlignment == PDEConstants.PDEAlignment.PDEAlignmentCenter
                               && (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached
                                   || mIconAlignment
                                      == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentRightAttached)) {
                        leftBorder = (mDisplayWidth - fullWidth) / 2.0f;
                    } else if (mAlignment == PDEConstants.PDEAlignment.PDEAlignmentRight
                               || mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentRight) {
                        leftBorder = (mDisplayWidth - (fullWidth + outerDistanceRight));
                    } else {
                        Log.d(LOG_TAG, "Should not happen!");
                    }
                }

                // calc text x position
                if (mAlignment == PDEConstants.PDEAlignment.PDEAlignmentCenter) {
                    if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeft) {
                        // Icon very left - text centered
                        iconPosition.x = leftBorder;
                        if (iconSize.x > 0) {
                            // there is an Icon
                            textPosition.x = iconPosition.x + iconSize.x +
                                             ((mDisplayWidth - iconPosition.x - iconSize.x - titleWidth) / 2.0f);
                        } else {
                            // there is no Icon
                            textPosition.x = (mDisplayWidth - fullWidth) / 2.0f;
                        }
                    } else if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached) {
                        // text and Icon centered (Icon left of text)
                        iconPosition.x = leftBorder;
                        if (iconSize.x > 0) {
                            // there is an Icon
                            textPosition.x = iconPosition.x + iconSize.x + saveArea;
                        } else {
                            // there is no Icon
                            textPosition.x = iconPosition.x;
                        }
                    } else if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentRight) {
                        // Icon at the very right - text centered
                        iconPosition.x = mDisplayWidth - iconSize.x - outerDistanceRight;
                        textPosition.x = (iconPosition.x - titleWidth) / 2.0f;
                    } else /*mIconAlignment == DTButtonLayerForegroundIconAlignmentRightAttached*/ {
                        // Icon and text centered (Icon at right of text)
                        iconPosition.x = leftBorder;
                        if (titleWidth > 0.0f) {
                            iconPosition.x += saveArea + titleWidth;
                        }
                        textPosition.x = leftBorder;
                    }
                } else if (mAlignment == PDEConstants.PDEAlignment.PDEAlignmentLeft) {
                    if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeft) {
                        // Icon and text on the very left
                        iconPosition.x = leftBorder;
                        if (iconSize.x > 0) {
                            // there is an Icon
                            textPosition.x = iconPosition.x + iconSize.x + saveArea;
                        } else {
                            // there is no Icon
                            textPosition.x = outerDistanceLeft;
                        }
                    } else if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached) {
                        // Icon and text on the very left
                        iconPosition.x = leftBorder;
                        if (iconSize.x > 0) {
                            // there is an Icon
                            textPosition.x = iconPosition.x + iconSize.x + saveArea;
                        } else {
                            // there is no Icon
                            textPosition.x = outerDistanceLeft;
                        }
                    } else if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentRight) {
                        // Icon on very right, text on very left
                        iconPosition.x = mDisplayWidth - iconSize.x - outerDistanceRight;
                        textPosition.x = outerDistanceLeft;

                    } else /*mIconAlignment == DTButtonLayerForegroundIconAlignmentRightAttached*/ {
                        // text left, Icon right next to it
                        iconPosition.x = leftBorder;
                        if (titleWidth > 0.0f) {
                            iconPosition.x += saveArea + titleWidth;
                        }
                        textPosition.x = leftBorder;
                    }
                } else /*if (mTextAlignment == PDEConstants.PDEAlignment.PDEAlignmentRight)*/ {
                    if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeft) {
                        //Icon on the very left, text on the very right
                        iconPosition.x = leftBorder;
                        textPosition.x = mDisplayWidth - titleWidth - outerDistanceRight;
                    } else if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentLeftAttached) {
                        // text on the very right, Icon to the left of it
                        iconPosition.x = leftBorder;
                        textPosition.x = mDisplayWidth - titleWidth - outerDistanceRight;

                    } else if (mIconAlignment == PDEButton.PDEButtonIconAlignment.PDEButtonIconAlignmentRight) {
                        // text on the right, Icon to the right of it
                        iconPosition.x = leftBorder;
                        if (titleWidth > 0.0f) {
                            iconPosition.x += saveArea + titleWidth;
                        }
                        textPosition.x = leftBorder;
                    } else /*mIconAlignment == DTButtonLayerForegroundIconAlignmentRightAttached*/ {
                        // text on the right, Icon to the right of it
                        // text on the right, Icon to the right of it
                        iconPosition.x = leftBorder;
                        if (titleWidth > 0.0f) {
                            iconPosition.x += saveArea + titleWidth;
                        }
                        textPosition.x = leftBorder;
                    }
                }
            }

            // calculate y position of the Icon
            iconPosition.y = buttonYCenter - PDEBuildingUnits.roundToScreenCoordinates(iconSize.y / 2.0f);

            // here it jumps between the two variants -> there is a small variation in the calculation (can it be corrected?)
            if (mDisplayHeight > mFont.getCapHeight(mFontMetaSize.pointSize)) {
                // center font in the display - as font height we use the cap height
                textPosition.y = PDEBuildingUnits.roundToScreenCoordinates(
                        buttonYCenter - mFont.getTopOverallHeight(mFontMetaSize.pointSize)
                        + mFont.getCapHeight(mFontMetaSize.pointSize) / 2.0f);
            } else {
                // available space for font is less then the space a capital character needs - align baseline with bottom edge
                // of the button / display
                textPosition.y = PDEBuildingUnits.roundToScreenCoordinates(mDisplayHeight
                                                                           - (
                        mFont.getTopOverallHeight(mFontMetaSize.pointSize)
                        - mFont.getAscenderHeight(mFontMetaSize.pointSize)));
                //textPosition.y = 0;
            }

            //----- apply values -----

            // hide or show the layers
            if (mDrawableIconWrapperView != null && iconSize.x > 0 && titleWidth > 0) {
                mDrawableIconWrapperView.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.VISIBLE);
            } else if (mDrawableIconWrapperView != null && iconSize.x > 0) {
                mDrawableIconWrapperView.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.GONE);
            } else {
                mDrawableIconWrapperView.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
            }

            // set Icon position
            LayoutParams tmpIconFontWrapperViewParams2 = (LayoutParams) mDrawableIconWrapperView.getLayoutParams();
            tmpIconFontWrapperViewParams2.x = PDEBuildingUnits.roundToScreenCoordinates(iconPosition.x);
            tmpIconFontWrapperViewParams2.y = PDEBuildingUnits.roundToScreenCoordinates(iconPosition.y);

            mDrawableIconWrapperView.setLayoutParams(tmpIconFontWrapperViewParams2);
            mDrawableIconWrapperView.measure(MeasureSpec.makeMeasureSpec(tmpIconFontWrapperViewParams2.width,
                                                                         MeasureSpec.EXACTLY),
                                             MeasureSpec.makeMeasureSpec(tmpIconFontWrapperViewParams2.height,
                                                                         MeasureSpec.EXACTLY));

            PDEAbsoluteLayout.LayoutParams
                    textViewParams
                    = (PDEAbsoluteLayout.LayoutParams) mTextView.getLayoutParams();
            textViewParams.x = PDEBuildingUnits.roundToScreenCoordinates(textPosition.x);
            textViewParams.y = PDEBuildingUnits.roundToScreenCoordinates(textPosition.y);
            textViewParams.width = (int) Math.ceil(titleWidth) + 2; // on some devices the text is cut off
            textViewParams.height =
                    PDEBuildingUnits.roundToScreenCoordinates(mFont.getOverallHeight(mFontMetaSize.pointSize)) + 1;

            mTextView.setLayoutParams(textViewParams);
            mTextView.measure(MeasureSpec.makeMeasureSpec(textViewParams.width, MeasureSpec.EXACTLY),
                              MeasureSpec.makeMeasureSpec(textViewParams.height, MeasureSpec.EXACTLY));

            if (DEBUG_PARAMS) {
                Log.d(LOG_TAG, "iconView " + tmpIconFontWrapperViewParams2.x + ", " + tmpIconFontWrapperViewParams2.y
                               + " - " + tmpIconFontWrapperViewParams2.width + " by "
                               + tmpIconFontWrapperViewParams2.height);

                Log.d(LOG_TAG, "textView " + textViewParams.x + ", " + textViewParams.y + " - " + textViewParams.width
                               + " by " + textViewParams.height + " button size: " + mDisplayWidth + ", "
                               + mDisplayHeight
                               + " text-full-size: " + mTextInfo.textSizeFull.width());

                if (mTextInfo.textSizeFull.width() > textViewParams.width) {
                    Log.d(LOG_TAG, "text-full-size: " + mTextInfo.textSizeFull.width() + " ViewWidth: " +
                                   textViewParams.width + " savearea: " + saveArea);
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
        } finally {
            PDETrace.endSection();
        }
    }


    protected int getTextWidth(float fontSize) {
        if (fontSize == 0f) {
            return 0;
        }

        int key = (int) Math.floor(fontSize * 100);

        Integer width = mTextWidthSizeCache.get(key);

        if (width == null) {

            width = PDEFontHelpers.getTextViewBounds(mTitle, mFont, fontSize).width();

            mTextWidthSizeCache.put(key, width);

        }
        return width;
    }


    /**
     * @brief Calculate the wanted width to show the icon (if set) and the whole text.
     */
    protected float calculateWantedWidth(int textWidth, float fontSize) {
        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "calculateWantedWidth");
        }

        PDETrace.beginSection("calculateWantedWidth");

        float wantedWidth = 0.0f;
        float saveArea = PDEBuildingUnits.exactBU(); //single margin
        int iconWidth = 0;

        float outerDistanceLeft = mHorizontalPadding;
        float outerDistanceRight = mHorizontalPadding;
        float outerDistanceLeftRight;

        ViewGroup innerLayout;
        ViewGroup overlaySlot;
        overlaySlot = (ViewGroup) getParent();

        if (overlaySlot != null && overlaySlot.getParent() != null && overlaySlot.getParent().getParent() != null) {
            innerLayout = (ViewGroup) overlaySlot.getParent().getParent();

            if (((ViewGroup) innerLayout.findViewById(R.id.pdebutton_overlay_slot_left)).getChildCount() != 0)
                outerDistanceLeft = PDEBuildingUnits.exactBU();
            if (((ViewGroup) innerLayout.findViewById(R.id.pdebutton_overlay_slot_right)).getChildCount() != 0)
                outerDistanceRight = PDEBuildingUnits.exactBU();
        }

        outerDistanceLeftRight = outerDistanceLeft + outerDistanceRight;

        // get the width and the height of the Icon
        if (mDrawableIconWrapperView != null
            && mDrawableIconWrapperView.hasElementIcon()) {
            LayoutParams tmpIconFontWrapperViewParams = (LayoutParams) mDrawableIconWrapperView.getLayoutParams();
            // try to get the values from the Icon (works e.g. for bitmaps)
            if (tmpIconFontWrapperViewParams != null) {
                iconWidth = tmpIconFontWrapperViewParams.width;
            }

            if (SHOW_DEBUG_LOGS) {
                Log.d(LOG_TAG, "calculateWantedWidth - iconWidth: " + iconWidth);
            }

            // Here we differ from iOS! Since here we can get drawables without size.
            // check if height and width were available
            if (iconWidth <= 0) {

                iconWidth = PDEBuildingUnits.roundToScreenCoordinates(mFont.getCapHeight(fontSize)
                                                                      * mIconToTextHeightRatio);

                if (SHOW_DEBUG_LOGS) {
                    Log.d(LOG_TAG, "calculateWantedWidth - calculated iconWidth: " + iconWidth);
                }
            }
        }

        // calculate wanted width
        if (!TextUtils.isEmpty(mTitle)) {
            wantedWidth = outerDistanceLeftRight + textWidth;
        }

        //check to add icon width and save areas...
        if (iconWidth > 0) {
            if (!TextUtils.isEmpty(mTitle)) {
                // there is an Icon, thus 3 saveAreas
                wantedWidth += (saveArea + iconWidth);
            } else {
                //if there is no text, horizontal distance has not be added yet
                wantedWidth += outerDistanceLeftRight + iconWidth;
            }
        }

        PDETrace.endSection();

        return wantedWidth;
    }


    /**
     * @brief Determine layout size of element.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        int width;
        float fontSize;

        PDETrace.beginSection("onMeasure");

        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "onMeasure " + MeasureSpec.toString(widthMeasureSpec) + " x "
                           + MeasureSpec.toString(heightMeasureSpec));
        }

        // measure the children (otherwise e.g. the sunkenlayer will not be visible!)
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        // take the height from the parameter ...
        height = MeasureSpec.getSize(heightMeasureSpec);

        fontSize = calculateFontSize(height);
        width = PDEBuildingUnits.roundUpToScreenCoordinates(calculateWantedWidth(getTextWidth(fontSize), fontSize));

        if (height == 0) {
            height = PDEBuildingUnits.BU() * 3;
        } else if (height > mFont.getCapHeight(fontSize) * 3.0f) {
            height = PDEBuildingUnits.roundUpToScreenCoordinates(mFont.getCapHeight(fontSize) * 3.0f);
        }

        // return the values
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                             resolveSize(height, heightMeasureSpec));

        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "onMeasure result: " + getMeasuredWidth() + " x " + getMeasuredHeight());
        }

        PDETrace.endSection();
    }


    /**
     * @param width     New width.
     * @param height    New height.
     * @param oldWidth  Old width.
     * @param oldHeight Old height.
     * @brief Size changed.
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        // we have to call performLayout here, since the new sizes are needed in onLayout. And the new sizes of the text
        // field or set here (the call of measure is also important otherwise the ViewGroup doesn't know about the size)

        PDETrace.beginSection("onSizeChanged");

        if (SHOW_DEBUG_LOGS) {
            Log.d(LOG_TAG, "onSizeChanged " + width + " x " + height + " old: " + oldWidth + " x " + oldHeight);
        }

        super.onSizeChanged(width, height, oldWidth, oldHeight);

        // remember
        mDisplayWidth = width;
        mDisplayHeight = height;

        if (width != oldWidth || height != oldHeight) {
            // update the font size
            updateFontSize(mDisplayHeight, true, mTextInfo, mFontMetaSize);

            // and perform a new layout
            performLayout();
        }

        PDETrace.endSection();
    }
}
