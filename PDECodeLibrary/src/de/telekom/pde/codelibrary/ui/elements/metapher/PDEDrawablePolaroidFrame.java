/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
        * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
        * https://www.design.telekom.com/myaccount/terms-of-use/
        *
        * Copyright (c) 2012. Neuland Multimedia GmbH.
        */
package de.telekom.pde.codelibrary.ui.elements.metapher;


import android.graphics.*;
import android.graphics.drawable.Drawable;

import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.drawables.PDEDrawableMultilayer;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableRoundedBox;
import de.telekom.pde.codelibrary.ui.elements.boxes.PDEDrawableRoundedGradientBox;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedInnerShadow;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;
import de.telekom.pde.codelibrary.ui.elements.icon.PDEDrawableIconImage;

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawablePolaroidFrame
//----------------------------------------------------------------------------------------------------------------------

@SuppressWarnings("unused")
public class PDEDrawablePolaroidFrame extends PDEDrawableMultilayer {

    // basic properties
    private final static float CONST_ASPECTRATIO = 185.0f/255.0f;
    // colors
    protected PDEColor mElementBackgroundColor;
    protected PDEColor mElementPolaroidFrameColor;

    // measurements
    protected float mElementCornerRadius;

    // layers
    private PDEDrawableShapedShadow mElementShadowDrawable;
    private PDEDrawableShapedInnerShadow mElementInnerShadowDrawable;
    private PDEDrawableRoundedBox mElementPolaroidDrawable;
    private PDEDrawableRoundedGradientBox mElementGradientDrawable;
    private PDEDrawableRoundedGradientBox mElementImageGradientDrawable;

    private PDEDrawableIconImage mElementImageDrawable;

    // initialization
    public PDEDrawablePolaroidFrame(Drawable drawable)
    {
        mElementImageDrawable = new PDEDrawableIconImage(drawable);
        mElementPolaroidFrameColor = PDEColor.valueOf("DTWhite");

        // init sublayers
        initLayers();

        mElementPolaroidDrawable.setElementBackgroundColor(PDEColor.valueOf("DTTransparentBlack"));
    }

    /**
     * @brief internal initial helper to init needed layers. shadow is not created here, it must
     *        inited by createShadow separately
     */
    private void initLayers() {
        // initialize polaroid frame layer
        mElementPolaroidDrawable = new PDEDrawableRoundedBox();
        mElementPolaroidDrawable.setBounds(0, 0, 0, 0);

        // create gradient layer for polaroid
        mElementGradientDrawable = new PDEDrawableRoundedGradientBox();

        // init gradient layer for image and layer it to imagelayer
        mElementImageGradientDrawable = new PDEDrawableRoundedGradientBox();

        // create inner shadow for image
        mElementInnerShadowDrawable = new PDEDrawableShapedInnerShadow();
        mElementInnerShadowDrawable.setBounds(0, 0, 0, 0);
        mElementInnerShadowDrawable.setElementShapeColor(PDEColor.valueOf("DTBlack"));
        mElementInnerShadowDrawable.setElementLightIncidenceOffset(new PointF(0.0f, 0.0f));
        mElementInnerShadowDrawable.setElementShapeOpacity(0.2f);

        // set shadow to null, create it on request
        mElementShadowDrawable = null;

        // hang in main layers
        addLayer(mElementPolaroidDrawable);
        addLayer(mElementGradientDrawable);
        addLayer(mElementImageDrawable);
        addLayer(mElementImageGradientDrawable);
        addLayer(mElementInnerShadowDrawable);
    }


    /**
     * @brief Helper function to get aspect ratio
     *
     * @return Current valid aspect ratip
     */
    private float getElementAspectRatio() {
        if (mElementImageDrawable == null) return CONST_ASPECTRATIO;

        if (mElementImageDrawable.getIntrinsicWidth() >= mElementImageDrawable.getIntrinsicHeight()){
            return CONST_ASPECTRATIO;
        } else {
            return 1 / CONST_ASPECTRATIO;
        }
    }

//---------------------------------------------------------------------------------------------------------------------
// ----- layout / sizing ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Set width of the element.
     *
     * Convenience function.
     *
     * @param width The new width of the element.
     */
    @Override
    public void setLayoutWidth(int width) {
        setLayoutSize(new Point(width, Math.round((float) width / getElementAspectRatio())));
    }


    /**
     * @brief Set height of the element.
     *
     * Convenience function.
     *
     * @param height The new height of the element.
     */
    @Override
    public void setLayoutHeight(int height) {
        setLayoutSize(new Point(Math.round(height * getElementAspectRatio()), height));
    }


    /**
     * @brief Called when bounds set via rect.
     */
    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(elementCalculateAspectRatioBounds(bounds));
    }


    /**
     * @brief Called when bounds set via left/top/right/bottom values.
     */
    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        Rect aspectRatioBounds = elementCalculateAspectRatioBounds(new Rect(left, top, right, bottom));
        super.setBounds(aspectRatioBounds.left, aspectRatioBounds.top, aspectRatioBounds.right, aspectRatioBounds.bottom);
    }


    /**
     * @brief Calculate the correct aspect ratio bounds.
     *
     * @param  bounds Available Space for the element
     * @return Rect with the correct aspect ratio, fitting in available space
     */
    private Rect elementCalculateAspectRatioBounds(Rect bounds) {
        Rect newBounds;

        if ((float)bounds.width() / (float)bounds.height() > getElementAspectRatio() ) {
            newBounds = new Rect(bounds.left, bounds.top, 0, bounds.bottom);
            newBounds.right = newBounds.left + Math.round(newBounds.height() * getElementAspectRatio());
        } else {
            newBounds = new Rect(bounds.left, bounds.top, bounds.right, 0);
            newBounds.bottom = newBounds.top + Math.round(newBounds.width() / getElementAspectRatio());
        }

        return newBounds;
    }


    /**
     * @brief Function where the multilayer reacts on bound changes.
     */
    @Override
    protected void doLayout() {
        Rect bounds = getBounds();

        // update Polaroid layer
        updatePolaroidDrawable(bounds);

        // update Image Layer
        updateElementImageDrawable(bounds);
        // update shadow drawable
        updateElementShadowDrawable (new Point(bounds.width(),bounds.height()));
        //inform this layer about changes
        invalidateSelf();
    }




//---------------------------------------------------------------------------------------------------------------------
// ----- optional shadow ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief init shadow drawable.
     *
     * Creates and delivers the outer shadow drawable.
     *
     * @return The outer shadow drawable.
     */
    public PDEDrawableShapedShadow createElementShadow() {
        // already created?
        if (mElementShadowDrawable != null) return mElementShadowDrawable;
        // init shadow drawable
        mElementShadowDrawable = new PDEDrawableShapedShadow();
        mElementShadowDrawable.setElementShapeOpacity(0.25f);
        mElementShadowDrawable.setElementBlurRadius(5.0f);
        setNeededPadding(PDEBuildingUnits.oneHalfBU());
        updateElementShadowDrawable(new Point(getBounds().width(),getBounds().height()));
        // return
        return mElementShadowDrawable;
    }


    /**
     * @brief shadow getter
     *
     * @return drawable of outer shadow
     */
    public PDEDrawableShapedShadow getElementShadow() {
        // return
        return mElementShadowDrawable;
    }


    /**
     * @brief forget shadow drawable.
     */
    public void clearElementShadow() {
        mElementShadowDrawable = null;
        setNeededPadding(0);
    }


    /**
     * @brief Update the shadow drawable if we've got one.
     */
    private void updateElementShadowDrawable(Point elementSize) {
    // check if we have a shadow set
        if (mElementShadowDrawable != null) {

            // keep current shadow position, just update the size
            Rect frame;
            Rect bounds = mElementShadowDrawable.getBounds();
            frame = new Rect(bounds.left, bounds.top, bounds.left + elementSize.x, bounds.top + elementSize.y);
            //mElementShadowDrawable.setLayoutRect(frame);
            mElementShadowDrawable.setBounds(frame);
            mElementShadowDrawable.setElementShapeRoundedRect(mElementCornerRadius);
        }
    }


////----------------------------------------------------------------------------------------------------------------------
////----- getter / setter functions --------------------------------------------------------------------------------
////----------------------------------------------------------------------------------------------------------------------
    /**
     * @brief polaroid color setting function
     */
    public void setElementPolaroidFrameColor(PDEColor color) {
        float red, green, blue, alpha,
              red2, green2, blue2, alpha2;
        // validation
        if (color==null) return;

        red = color.getRed();
        green = color.getGreen();
        blue = color.getBlue();
        alpha = color.getAlpha();
        red2 = mElementPolaroidFrameColor.getRed();
        green2 = mElementPolaroidFrameColor.getGreen();
        blue2 = mElementPolaroidFrameColor.getBlue();
        alpha2 = mElementPolaroidFrameColor.getAlpha();

        // changed?
        if (red == red2 && green==green2 && blue==blue2 && alpha==alpha2) return;
        // remember new value
        mElementPolaroidFrameColor = color;
       // mElementPolaroidDrawable.setElementBackgroundColor(mElementPolaroidFrameColor);
        // update
        invalidateSelf();
    }

    /**
    * @brief Get ElementPolaroidFrameColor
    */
    public PDEColor getElementPolaroidFrameColor() {
        return mElementPolaroidFrameColor;
    }

    /**
     * @brief Set Picture
     */
    public void setElementImage(Drawable image) {
        //any change?
        if (image == mElementImageDrawable.getElementImage()) return;
        //remember
        mElementImageDrawable.setElementImage(image);
        //redraw
        doLayout();
    }

    /**
    * @brief Get Picture
    */
    public Drawable getmElementImageDrawable() {
        return mElementImageDrawable.getElementImage();
    }


    /**
     * @brief Calculate value relative to polaroid's size
     */
    private float polaroidRelativeValue(float value) {
        return mElementPolaroidDrawable.getBounds().width() * (value/185.0f);
    }

//----------------------------------------------------------------------------------------------------------------------
//----- helper functions --------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------


    /**
     * @brief update helper function for the polaroid bg layer
     */
    private void updatePolaroidDrawable(Rect bounds) {
        Rect frameRect = new Rect(0, 0, bounds.width(), bounds.height());
        PDEColor highColor;
        PDEColor lowColor;
        mElementCornerRadius = bounds.width() * 0.07f;
        // update Frame
        mElementPolaroidDrawable.setBounds(frameRect);
        mElementPolaroidDrawable.setElementCornerRadius(mElementCornerRadius);
        mElementPolaroidDrawable.setElementBorderColor(mElementPolaroidFrameColor);
        // todo: this border stuff does not work properly!!! So we take a workaround.
        //mElementPolaroidDrawable.setElementBorderWidth(polaroidRelativeValue(2.5f));
        int border = Math.round(polaroidRelativeValue(2.5f));
        mElementGradientDrawable.setBounds(frameRect.left + border, frameRect.top + border, frameRect.right - border,
                                           frameRect.bottom - border);

        float red, green, blue;
        red = mElementPolaroidFrameColor.getRed();
        blue = mElementPolaroidFrameColor.getBlue();
        green = mElementPolaroidFrameColor.getGreen();

        highColor = new PDEColor(red,green,blue,0.0f);
        lowColor = new PDEColor(red-0.33f,green-0.33f,blue-0.33f,0.75f);
        mElementGradientDrawable.setElementBackgroundGradientColors(highColor,highColor,lowColor);
        mElementGradientDrawable.setElementGradientDistributionPositions(0.0f, 0.85f, 1.0f);

        // set other layer data
        mElementPolaroidDrawable.setElementBackgroundColor(mElementPolaroidFrameColor);
        mElementGradientDrawable.setElementCornerRadius(mElementCornerRadius);
        mElementGradientDrawable.setElementBorderColor(PDEColor.valueOf("DTTransparentBlack"));
    }


    /**
     * @brief update function for the imagelayer (image self, image gradient, image innershadow)
     */
    private void updateElementImageDrawable(Rect bounds) {
        PDEColor highColor, mainColor;
        PDEColor lowColor;
        float placeHolderFrameDistance;
        Rect imgBounds;

        //int
//        placeHolderFrameDistance = (bounds.width() - polaroidRelativeValue(176.5f) )/2.0f + Math.round(polaroidRelativeValue(2.5f));
//        Rect frameRect = new Rect((int)placeHolderFrameDistance, (int)placeHolderFrameDistance,
//                                  (int)placeHolderFrameDistance + (int)polaroidRelativeValue(176.5f),
//                                  (int)placeHolderFrameDistance + (int)polaroidRelativeValue(176.5f));
        int border = Math.round(polaroidRelativeValue(2.5f));
//        Rect frameRect = new Rect(bounds.left + border,bounds.top + border,bounds.right - border,
//                                  bounds.bottom - border);

        Rect frameRect = new Rect(0, 0, bounds.width(), bounds.height());
        //mElementImageDrawable.enableAspectRatio(false);
//        mElementImageDrawable.setBounds(frameRect.left + border, frameRect.top + border, frameRect.right - 2 * border,
//                                        frameRect.right - border);



        mElementImageDrawable.setLayoutOffset(frameRect.left + 2* border, frameRect.top + 2 * border);
        mElementImageDrawable.setLayoutWidth(frameRect.width() - 4* border);

        Path clipPath;
        clipPath = new Path();
        float radius = mElementPolaroidDrawable.getElementCornerRadius() - border / 2.0f;
        // calculate clip path
//        clipPath.addRoundRect(new RectF(mElementImageDrawable.getBounds().left /*+ border*/,
//                                        mElementImageDrawable.getBounds().top /*+ border*/,
//                                        mElementImageDrawable.getBounds().right,
//                                        mElementImageDrawable.getBounds().bottom),radius, radius,
//                Path.Direction.CW);

        clipPath.addRoundRect(new RectF(0.5f /*+ border*/,
                                        0.5f /*+ border*/,
                                        mElementImageDrawable.getBounds().width()-0.5f,
                                        mElementImageDrawable.getBounds().height()-0.5f),radius, radius,
                Path.Direction.CW);


        clipPath.close();
//        mElementImageDrawable.setElementClipPath(clipPath,true);
        mElementImageDrawable.setElementClipPath(clipPath);

        // Add some gradient to the image view.
        highColor = new PDEColor(1.0f,1.0f,1.0f,0.5f);
        mainColor = new PDEColor(0.5f,0.5f,0.5f,0.25f);
        lowColor = new PDEColor(0.0f,0.0f,0.0f,0.0f);

//        highColor = new PDEColor(1.0f,0.0f,0.0f,0.5f);
//        mainColor = new PDEColor(1.0f,0.0f,0.0f,0.5f);
//        lowColor = new PDEColor(1.0f,0.0f,0.0f,0.5f);

        imgBounds = mElementImageDrawable.getBounds();
        mElementImageGradientDrawable.setLayoutRect(new Rect(imgBounds.left-1, imgBounds.top-1,
                                                             imgBounds.right+1, imgBounds.bottom+1));

        mElementImageGradientDrawable.setElementBorderColor(PDEColor.valueOf("DTTransparentBlack"));
        mElementImageGradientDrawable.setElementCornerRadius(radius);
        mElementImageGradientDrawable.setElementBackgroundGradientColors(highColor,mainColor,lowColor);

        // set inner shadow on image
        mElementInnerShadowDrawable.setElementBlurRadius(polaroidRelativeValue(8.0f));
        RectF layoutRect = new RectF(mElementImageDrawable.getBounds().left, mElementImageDrawable.getBounds().top,
                mElementImageDrawable.getBounds().right,
                mElementImageDrawable.getBounds().bottom);
        mElementInnerShadowDrawable.setLayoutRect(mElementImageGradientDrawable.getBounds());
        mElementInnerShadowDrawable.setElementShapeRoundedRect(radius);
    }
}
