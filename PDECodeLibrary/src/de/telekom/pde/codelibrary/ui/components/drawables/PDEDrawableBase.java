/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.drawables;

//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableBase
//----------------------------------------------------------------------------------------------------------------------

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.elements.wrapper.PDEViewWrapper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/*******************************************************************************************************************
 *
 * !!! CAUTION !!!
 *
 * Please don't add any special functionality to this base class that would make our PDE Drawable that special,
 * that they would be incompatible to the android Drawables. We want to keep up the possibility to mix PDE Drawables
 * and standard android Drawables within our PDEDrawableMultilayer class. So adding special behaviour here and taking
 * advantage of it in our Multilayer is absolutely no option!!!!!
 *
 * !!! CAUTION !!!
 *
 ********************************************************************************************************************/



/**
 * @brief Base Class for our PDE Drawables.
 *
 * This base class contains the functionality we use in all our Graphic Primitive Drawables.
 * So we save a lot of redundant code which would have to maintain in all classes otherwise.
 * The main aspects of this base class:
 * 1) It defines its own update-chain.
 * 2) It contains all of our self-defined Layout functions.
 * 3) It delivers default-code for all the overrides that have to be done when we inherit from the android Drawable
 * class (like alpha, dither, colorfilter, draw...).
 *
 * Since we had a lot of trouble with different drawing behaviour of hardware-accelerated and
 * non-hardware-accelerated devices we use a work-around to enforce the same results. This work-around is to draw all
 * contents into a bitmap first and then drawing this bitmap to the canvas within the draw-function. The positive
 * side-effect of this work-around is, that we don't have to run through possibly complex drawing code every time when
 * the draw-function is triggered. We only have to do it when the content changes in any way. So it does a kind of
 * buffering.
 */


public abstract class PDEDrawableBase extends Drawable implements PDEDrawableInterface {

    // drawable basics
    protected int mAlpha;
    protected ColorFilter mColorFilter;
    protected boolean mDither;
    protected Bitmap mDrawingBitmap;
    protected PDEViewWrapper mWrapperView;

    // needed for correct pixelalignement with antialias
    protected float mPixelShift;
    // (antialised) clipping
    protected Path mClipPath;
//    protected Bitmap mClipBitmap;
//    protected Paint mClipPaint;
//    protected Paint mXferPaint;
//    protected boolean mAntialiasedClipping;
//    protected Bitmap mClipOutputBitmap;
    // layout helper
    protected int mNeededPadding;

//----- init -----------------------------------------------------------------------------------------------------------

    /**
     * @brief Constructor for the base attributes.
     */
    public PDEDrawableBase() {
        // init drawable basics
        mAlpha = 0xFF;
        mColorFilter = null;
        mDither = false;
        mDrawingBitmap = null;
        mWrapperView = null;
        mPixelShift = 0.5f;
        mClipPath = null;
//        mClipBitmap = null;
//        mClipPaint = null;
//        mXferPaint = null;
//        mAntialiasedClipping = false;
//        mClipOutputBitmap = null;
        mNeededPadding = 0;
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
    public void setLayoutWidth(int width) {
        // anything to do?
        if (width == getBounds().width()) {
            return;
        }
        setLayoutSize(new Point(width, getBounds().height()));
    }


    /**
     * @brief Set height of the element.
     *
     * Convenience function.
     *
     * @param height The new height of the element.
     */
    public void setLayoutHeight(int height) {
        // anything to do?
        if (height == getBounds().height()) {
            return;
        }
        setLayoutSize(new Point(getBounds().width(), height));
    }


    /**
     * @brief Set the size of the layer.
     *
     * @param size The new size of the element. size.x == width, size.y == height
     */
    public void setLayoutSize(Point size) {
        setLayoutSize(size.x,size.y);
    }

    /**
     * @brief Set the size of the layer.
     *
     * @param width new width of the layer
     * @param height new height of the layer
     */
    public void setLayoutSize (int width, int height) {
        // get current bounds
        Rect bounds = getBounds();

        // anything to do?
        if (bounds.width() == width && bounds.height() == height) {
            return;
        }

        // remember
        setBounds(bounds.left, bounds.top, bounds.left + width, bounds.top + height);
    }


    /**
     * @brief Set the offest of the layer.
     *
     * @param offset The new offset of the element.
     */
    public void setLayoutOffset(Point offset) {
        setLayoutOffset(offset.x,offset.y);
    }

    /**
     * @brief Set the offest of the layer.
     *
     * @param x new x-position of the layer
     * @param y new y-position of the layer
     */
    public void setLayoutOffset(int x, int y){
        // get current bounds
        Rect bounds = getBounds();

        // anything to do?
        if (x == bounds.left && y == bounds.top) {
            return;
        }

        // remember
        setBounds(x, y, x + bounds.width(), y + bounds.height());
    }


    /**
     * @brief Set the layout of the layer.
     *
     * Sets the offset and size by the rect values
     *
     * @param rect The new layout rect of the element.
     */
    public void setLayoutRect(Rect rect) {
        setLayoutOffset(rect.left, rect.top);
        setLayoutSize(rect.width(), rect.height());
    }


    /**
     * @brief Called when the bounds changed.
     *
     * When the bounds of our drawable change, we have to update a few things. With the bounds also the size of our
     * DrawingBitmap changes, so we need to recreate it. Possibly we have some subelements or sublayers that also
     * require a new layout after the bounds changed. Finally after we recreated our DrawingBitmap,
     * we also need to redraw it.
     */
    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        // we've got a new bounds size, so our bitmap also needs a new size
        createDrawingBitmap();
        // do layout of all sublayers
        doLayout();
        // trigger redraw
        update();
    }


    /**
     * @brief Update layout of all sublayers / subelements.
     *
     * If there are layout updates / sizing calculations needed for sublayers or subelements trigger them here.
     * Sublayers are further drawables like e.g. an outer shadow.
     */
    protected void doLayout() {
        // todo: implement in derived class
    }

    /**
     * @brief Set a clip path for the drawable if needed.
     *
     * @param clip The new clip path. Reset with null.
     */
    public void setElementClipPath(Path clip){
//        setElementClipPath(clip,false);
        // remember
        mClipPath = clip;
        // update
        update();

    }


//    /**
//     * @brief Set a clip path for the drawable if needed.
//     *
//     * The antialias option is quite expensive. Use it only if really needed.
//     * If you use remember to take care of the pixelshift while creating the clip-path.
//     * It isn't possible to correct the pixelshift here because the class is already created then.
//     *
//     * @param clip The new clip path. Reset with null.
//     * @param antialias Uses an antialiased version of the path for clipping if true.
//     */
//    public void setElementClipPath(Path clip, boolean antialias){
//        Rect bounds;
//        bounds = getBounds();
//
//        // remember
//        mClipPath = clip;
//        mAntialiasedClipping = antialias;
//
//        // make antialiased clip path (much more expensive)
//        if (mAntialiasedClipping) {
//            // todo: check if we have to take care when this setter is called (before / after setting of bounds)
//            // todo: maybe we have to draw this again when the size changes...
//            if (bounds.width() > 0 && bounds.height() > 0) {
//                // create a bitmap in the size of the Drawable
//                if (mClipBitmap != null) mClipBitmap.recycle();
//                mClipBitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
//                Canvas c = new Canvas(mClipBitmap);
//                // init paints if not done yet
//                if (mClipPaint == null) initClipPaints();
//                // draw antialiased path into bitmap
//                c.drawPath(mClipPath, mClipPaint);
//                if (mClipOutputBitmap == null) createDrawingBitmap();
//            }
//        }
//
//        // update
//        update();
//    }

//    /**
//     * @brief Initialize Paints that are needed just for antialiased clipping.
//     */
//    protected void initClipPaints(){
//        mClipPaint = new Paint();
//        mClipPaint.setColor(Color.RED);
//        mClipPaint.setStrokeWidth(1);
//        mClipPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        mClipPaint.setAntiAlias(true);
//        mXferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mXferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//    }

    /**
     * @brief Set additional padding that is needed to display things outside of the element like e.g. outer shadow.
     *
     * @param padding
     */
    public void setNeededPadding(int padding){
        // anything to do?
        if (mNeededPadding == padding) {
            return;
        }

        // remember
        mNeededPadding = padding;
    }

    /**
     * @brief Returns the padding the element needs to be displayed correctly.
     *
     * Some things like an outer shadow have to be drawn outside of the element bounds.
     * So the View that holds the element has to be sized bigger than the element bounds.
     * For proper layouting the view must be extended to each direction by the value delivered by
     * this function.
     *
     * @return the needed padding
     */
    public int getNeededPadding(){
        return mNeededPadding;
    }


//---------------------------------------------------------------------------------------------------------------------
// ----- Drawable overrides ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Draws our prepared Bitmap to canvas.
     *
     * @param canvas the canvas to draw into.
     */
    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();



        // security
        if (bounds.width() <= 0 || bounds.height() <= 0  || mDrawingBitmap == null) return;
        // save canvas
        canvas.save();

        // translate canvas matrix to have relative positions
        canvas.translate(bounds.left,bounds.top);

//        // expensive clipping activated?
//        if (mAntialiasedClipping && mClipOutputBitmap != null){
//            Canvas outputCanvas;
//            // get canvas for the intersection result
//            outputCanvas = new Canvas(mClipOutputBitmap);
//            // draw content into result-bitmap
//            outputCanvas.drawBitmap(mDrawingBitmap,0,0, new Paint());
//            // intersect with antialiased clipping path
//            outputCanvas.drawBitmap(mClipBitmap,0,0, mXferPaint);
////            outputCanvas.drawBitmap(mClipBitmap,0,0, new Paint());
//            // draw result to canvas
//            canvas.drawBitmap(mClipOutputBitmap,0,0,new Paint());
//        } else {
////            // cheap clipping activated?
////            if (mClipPath != null){
////                canvas.clipPath(mClipPath);
////            }
//            // draw content
//            canvas.drawBitmap(mDrawingBitmap,0,0, new Paint());
//        }

        // draw content
        canvas.drawBitmap(mDrawingBitmap,0,0, new Paint());

        // reset canvas
        canvas.restore();
    }


    /**
     * @brief Return the opacity/transparency of this Drawable.
     */
    @Override
    public int getOpacity() {
        switch (mAlpha) {
            case 255:
                return PixelFormat.OPAQUE;
            case 0:
                return PixelFormat.TRANSPARENT;
        }
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * @brief Specify an alpha value for the drawable.
     *
     * 0 means fully transparent, and 255 means fully opaque.
     *
     * @param alpha the new alpha value for the drawable.
     */
    @Override
    public void setAlpha(int alpha) {
        // change?
        if (mAlpha == alpha) return;
        // remember
        mAlpha = alpha;
        update(true);
    }


    /**
     * @brief Specify an optional colorFilter for the drawable.
     *
     * Pass null to remove any filters.
     *
     * @param cf the new colorfilter.
     */
    @Override
    public void setColorFilter(ColorFilter cf) {
        // change?
        if (mColorFilter == cf) return;
        // remember
        mColorFilter = cf;
        // update
        update(true);
    }


    /**
     * @brief Set to true to have the drawable dither its colors when drawn to a device with fewer than 8-bits per color component.
     *
     * This can improve the look on those devices, but can also slow down the drawing a little.
     *
     * @param dither true - turn dither on
     */
    @Override
    public void setDither(boolean dither) {
        // change?
        if (mDither == dither) return;
        // remember
        mDither = dither;
        // update
        update(true);
    }




//---------------------------------------------------------------------------------------------------------------------
// ----- Wrapper View  ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Delivers its own Wrapper-View.
     *
     * If we want to use a Drawable within a ViewGroup/Layout we have to put it into some view first. We could write
     * a lot of specialised Views that simply forward all functions we need to configure the underlying drawable or
     * we can use one standardized View with only some basic functions to wrap all our custom Drawables within.
     * Configuration is then done directly on the Drawable. In order to keep a better overview and spare a lot of
     * maintenance-intensive View-Code we decided for the latter option. Every custom drawable carries its own
     * Wrapper-View which is created on demand and places the drawable within.
     *
     * @return a simple view that wraps this Drawable.
     */
    public PDEViewWrapper getWrapperView() {
        if (mWrapperView == null) {
            mWrapperView = new PDEViewWrapper(PDECodeLibrary.getInstance().getApplicationContext(),this);
        }
        return mWrapperView;
    }




//---------------------------------------------------------------------------------------------------------------------
// ----- Update Management  ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief The main update method for our own Drawables.
     *
     * This function defines our update queue which is triggered when someone calls for an update.
     * If a paint property (e.g. alpha, dither, color, etc.) changed we can trigger an update of all important
     * Paint-Instances we use for the drawing of our Drawable. This is done in step 1 (updatePaintProperties) if
     * the parameter paintPropertiesChanged tells us that the update is needed.
     * If your Drawable contains special members (like e.g. pictures) whose properties have to be updated before the
     * drawing to the canvas starts you can override the updateHook and place your update code there. You can decide
     * on your own if you need the information of the parameter paintPropertiesChanged. By default this hook contains
     * no code.
     * In the next step (prepareDrawingBitmap) the contents of the former DrawingBitmap is erased and
     * updateDrawingBitmap is called with the canvas of the DrawingBitmap. Derived classes have to override
     * updateDrawingBitmap to place the custom update code there.
     * Last step is invalidateSelf() in order to trigger the actual draw-Routine of this drawable,
     * that will draw our new DrawingBitmap to the given canvas.
     *
     * @param paintPropertiesChanged shows if an update of the used Paint-Instances is needed.
     */
    public void update(boolean paintPropertiesChanged){
        updatePaintProperties(paintPropertiesChanged);
        updateHook(paintPropertiesChanged);
        prepareDrawingBitmap();
        invalidateSelf();
    }

    /**
     * @brief Simply call this if an update is needed, but no paint properties have changed (convenience function).
     *
     * This convenience function just saves the programmers the time to write update(false) every time when paint
     * properties haven't changed.
     */
    public void update() {
        update(false);
    }


    /**
     * @brief update all used paints
     */
    protected void updatePaintProperties(boolean paintPropertiesChanged) {
        if (!paintPropertiesChanged) return;
        updateAllPaints();
    }

    /**
     * @brief Update all used Paint-Instances here.
     *
     * If global paint properties like e.g. alpha, dither, colorFilter, etc. change all important Paint-Instances that
     * are used within this drawable have to be updated. Place the code to recreate the Paints (with the new values)
     * within this method. You can also use it for updates of a distinct paint (e.g. when a distinct color has
     * changed), although this means some overhead.
     * This method HAS to be overriden by derived classes.
     */
    protected abstract void updateAllPaints();


    /**
     * @brief Handle special update stuff with this method.
     *
     * This updateHook is introduced to enhance the flexibility of the update queue. If there are some special
     * members in the derived Drawable that have to be updated before the drawing starts,
     * the update can be done within this method.
     * This method should be overriden if needed.
     *
     * @param paintPropertiesChanged shows if an update of the used Paint-Instances is needed. Decide on your own if
     *                               you need this information.
     */
    protected void updateHook(boolean paintPropertiesChanged){
        // todo: add other updating stuff here in derived classes (for e.g. other used drawables like pictures)
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- Drawing Bitmap ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Creates the bitmap in which we draw our element.
     *
     * We draw our element in a bitmap first before we draw this bitmap on the canvas.
     * The reason for this detour is that we can avoid annoying graphic acceleration bugs in this way.
     * If the size of the element changes, we have to recreate the bitmap by calling this function.
     */
    protected void createDrawingBitmap(){
        Rect bounds = getBounds();

        // security
        if (bounds.width() <= 0 || bounds.height() <= 0) return;
        // use bitmap to avoid gfx-acceleration bug
        if (mDrawingBitmap != null) mDrawingBitmap.recycle();
        mDrawingBitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
//        // expensive clipping activated?
//        if (mAntialiasedClipping) {
//            if (mClipOutputBitmap != null) mClipOutputBitmap.recycle();
//            mClipOutputBitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
//        }
    }


    /**
     * @brief Updates our drawing bitmap and triggers a redraw of this element.
     *
     * If a drawing parameter changes, we need to call this function in order to update our drawing-bitmap and
     * in order to trigger the draw of our updated bitmap to the canvas.
     */
    protected void prepareDrawingBitmap() {
        Rect bounds = getBounds();
        RectF frame;

        // security
        if (bounds.width() <= 0 || bounds.height() <= 0 || mDrawingBitmap == null) return;
        // erase former content by filling with transparent color
        mDrawingBitmap.eraseColor(Color.TRANSPARENT);

        // get canvas of the bitmap
        Canvas c = new Canvas(mDrawingBitmap);
        // do clipping here and we'll have no problems with hardware acceleration and antialiasing
        if (mClipPath != null){
            c.clipPath(mClipPath);
        }
        updateDrawingBitmap(c,bounds);
    }


    /**
     * @brief Place your drawing code here.
     *
     * This is kind of our own version of the classical draw-method. We draw all contents first into a bitmap before
     * this bitmap is drawn to the actual canvas in the draw-method. In this way we can avoid a lot of
     * hardware-acceleration-drawing-bugs we encountered so far. On the other hand we don't have to run through our
     * drawing code every time when draw is triggered from somewhere else. Then we just have to draw our already
     * prepared bitmap. The bitmap is only updated if the update is triggered from within this class.
     * This method HAS to be overriden by derived classes.
     *
     * @param c the Canvas of the DrawingBitmap we want to draw into.
     * @param bounds the current bounding rect of our Drawable.
     */
    protected abstract void updateDrawingBitmap (Canvas c, Rect bounds);


}
