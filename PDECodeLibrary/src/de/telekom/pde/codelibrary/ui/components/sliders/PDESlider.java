
/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.sliders;



//----------------------------------------------------------------------------------------------------------------------
//  PDESlider
//----------------------------------------------------------------------------------------------------------------------


import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;


/// @cond CLASS_UNDER_DEVELOPMENT__NOT_RELEASED

/**
 * @brief   This is a Slider, controlled by slider events.
 *
 * The slider does nothing itself, it just manages contents which do the actual animation.
 * PDESlider offers a few predefined content variants as preset, but does not prevent you using your own graphics.
 */
public class PDESlider extends PDEAbsoluteLayout {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDESlider.class.getName();
    // debug messages switch
    private final static boolean DEBUGPARAMS = false;




    //----------------------------------------------------------------------------------------------------------------------
    //  PDESlider constants
    //----------------------------------------------------------------------------------------------------------------------


    //----- constants -----


    /**
     * @brief Telekom well known slider contents.
     */
    public enum PDESliderContentType {
        /* ATTENTION: IF YOU ADD A NEW TYPE HERE, DON'T FORGET TO ADD IT IN ATTRS.XML AS WELL!!!!!
            AND IF YOU ADD IT IN BETWEEN, YOU HAVE TO ADJUST THE INDEXES OF ALL FOLLOWING TYPES IN ATTRS.XML AS WELL!!!
         */
        ProgressBar,
    }


    //----- properties -----

    // content
    private PDESliderContentInterface mSliderContent;
    private PDESliderContentType mSliderContentType;

    // controller
    private ArrayList<PDESliderController> mSliderControllerBag;

    // Eventsource
    protected PDEEventSource mEventSource;

    // helper
    private PDESliderContentInterface mSliderContentToInitialize;


    public PDESlider(android.content.Context context){
        super(context);
        init(null);
    }


    public PDESlider(android.content.Context context, android.util.AttributeSet attrs){
        super(context,attrs);
        if(DEBUGPARAMS){
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                Log.d(LOG_TAG, "PDEButton-Attr(" + i + "): " + attrs.getAttributeName(i) + " => " + attrs.getAttributeValue(i));
            }
        }
        init(attrs);
    }


    public PDESlider(android.content.Context context, android.util.AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(attrs);
    }


    /**
     * @brief Internal initialisation.
     *
     * Create necessary graphics, start with a default configuration.
     */
    protected void init(AttributeSet attrs){

        // init
        mEventSource = new PDEEventSource();
        mSliderContent = null;
        mSliderControllerBag = new ArrayList<PDESliderController>();

        setClipChildren(true);
        //setBackgroundColor(Color.BLUE);

        LayoutInflater.from(getContext()).inflate(R.layout.pdeslider, this, true);

        // set Slider Content
        if (attrs != null) {
            TypedArray sa = getContext().obtainStyledAttributes(attrs, R.styleable.PDESlider);

            // create content
            if (sa.hasValue(R.styleable.PDESlider_contentType)) {

                setSliderWithContentType(sa.getInt(R.styleable.PDESlider_contentType, 0));
            }
            
            sa.recycle();
        }
    }


    //----- content handling --------------------------------------------------------------------------------------------------


    /**
     * @brief Set Slider Content with Integer Content type
     *
     * @param contentType
     */
    private void setSliderWithContentType(int contentType) {
        setSliderWithContentType(PDESliderContentType.values()[contentType]);
    }


    /**
     * @brief   Set the new slider content. Using the content Type
     *          Removes the old content.
     *
     * @param   contentType A slider content type constant
     */

    public void setSliderWithContentType(PDESliderContentType contentType){
        PDESliderContentInterface content = null;

        // any change?
        if (contentType == mSliderContentType) {
            return;
        }

        // remove old content from the layer if it exists
        if (mSliderContent != null) {
            ((ViewGroup)mSliderContent.getLayer().getParent()).removeView(mSliderContent.getLayer());
        }

        // create and set the new content, remember type
        switch (contentType) {
            case ProgressBar:
                content = new PDESliderContentProgressBar(getContext());
                break;
            default:
                //error
                content = null;
                break;
        }

        // success ?
        if (content == null) {
            return;
        }

        // remember
        mSliderContent = content;
        mSliderContentType = contentType;

        // add the content to the layer
        ((ViewGroup)findViewById(R.id.pdeslider_content_slot)).addView(mSliderContent.getLayer(),new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // send complete initialization
        //mSliderContentToInitialize = layer;
        //mAgentAdapter.getEventSource().requestOneTimeInitialization(this, "cbAgentControllerSingle",
        //        PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_MASK_ANIMATION);
        //mSliderContentToInitialize = null;
    }

    // REMOVE - only for testing !!
    public void controllHelp(PDESliderControllerState slideEvent) {
        if (mSliderContent != null) {
            mSliderContent.agentEvent(slideEvent);
        }
    }
}

/// @endcond CLASS_UNDER_DEVELOPMENT__NOT_RELEASED