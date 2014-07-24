/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 */


package de.telekom.pde.codelibrary.ui.elements.common;


import android.graphics.*;
import android.graphics.drawable.Drawable;
import de.telekom.pde.codelibrary.ui.color.PDEColor;

import java.util.ArrayList;

//import de.telekom.pde.codelibrary.ui.components.helpers.OnPDEBoundsChangeListener;


//----------------------------------------------------------------------------------------------------------------------
//  PDEDrawableMultilayer
//----------------------------------------------------------------------------------------------------------------------


/*******************************************************************************************************************
 *
 * !!! CAUTION !!!
 *
 * Please don't add any special functionality to the PDEDrawableBase class and try to take advantage of it within
 * this Multilayer class. We want to keep up the possibility to handle both PDE Drawables and standard android
 * Drawables with this Multilayer class (even mixed at the same multilayer).
 * So don't add any special features to PDEDrawableBase that would make the both types of Drawables incompatible to
 * each other.
 *
 * !!! CAUTION !!!
 *
 ********************************************************************************************************************/


@SuppressWarnings("unused")
public class PDEDrawableMultilayer extends Drawable implements Drawable.Callback, PDEDrawableInterface {


    public interface OnPDEBoundsChangeListener {
        public void onPDEBoundsChange(Drawable source, Rect bounds);
    }

    /**
     * @brief Global tag for log outputs.
     */
    @SuppressWarnings("unused")
    private final static String LOG_TAG = PDEDrawableMultilayer.class.getName();

    //----- properties -----



    // private variables
    private ArrayList<Drawable> mDrawableArray;
    private boolean mClipToBounds;
    private Paint mBackgroundPaint;
    private OnPDEBoundsChangeListener mOnBoundsChangeListener = null;
    // layout helper
    protected int mNeededPadding;


    /**
     * @brief Constructor.
     */
    public PDEDrawableMultilayer() {
        mDrawableArray = new ArrayList<Drawable>();
        mBackgroundPaint = null;
        mNeededPadding = 0;
        // enable clip to bounds by default
        setClipToBounds(true);
    }


    /**
     * @brief Sets a background color for this multilayer (drawn with the cliprect bounds).
     */
    public void setMultilayerBackgroundColor(PDEColor color) {
        if (color == null) {
            mBackgroundPaint = null;
        }
        else {
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(color.getIntegerColor());
        }
    }


    /**
     * @brief Returns a copy of the color used for the background.
     */
    public PDEColor getMultilayerBackgroundColor() {
        return PDEColor.valueOf(mBackgroundPaint.getColor());
    }


    /**
     * @brief Constructor.
     */
    public void addLayer(Drawable layer) {
        layer.setCallback(this);
        mDrawableArray.add(layer);
        invalidateSelf();
    }


    /**
     * @brief Insert a drawable at the specified position.
     *
     * If index is out of bounds, the drawable is added either at the head or tail.
     */
    public void insertLayerAtIndex(Drawable layer, int index) {
        // safety: modify negative indices; change too high indices to add
        if (index < 0) index=0;
        if (index >= mDrawableArray.size()){
            addLayer(layer);
            return;
        }

        // append to our list of layout elements
        layer.setCallback(this);
        mDrawableArray.add(index,layer);
        invalidateSelf();
    }


    /**
     * @brief Insert a drawable after the reference object.
     *
     * If reference object cannot be found, insert at tail.
     */
    public void insertLayerAfter(Drawable newLayer, Drawable referenceLayer) {
        int index;

        // seek the element
        index = getIndexOfLayer(referenceLayer);

        // if not found, add to tail
        if (index == -1) {
            addLayer(newLayer);
            return;
        }

        // insert after specified index
        insertLayerAtIndex(newLayer, index + 1);
    }


    /**
     * @brief Insert a drawable before the reference object.
     *
     * If reference object cannot be found, insert at tail.
     */
    public void insertLayerBefore(Drawable newLayer, Drawable referenceLayer) {
        int index;

        // seek the element
        index = getIndexOfLayer(referenceLayer);

        // if not found, add to tail
        if (index == -1) {
            addLayer(newLayer);
            return;
        }

        // insert before specified index
        insertLayerAtIndex(newLayer, index);
    }


    /**
     * @brief Remove a drawable specified by the index from the layout.
     *
     * Take care that the linkage is properly cleaned up.
     */
    public void removeLayerAtIndex(int index) {
        // if index is invalid do nothing
        if (index < 0 || index >= mDrawableArray.size()) return;

        // forget element itself and its callback
        mDrawableArray.get(index).setCallback(null);
        mDrawableArray.remove(index);
        invalidateSelf();
    }


    /**
     * @brief Remove a layout element from the layout.
     *
     * Take care that the linkage is properly cleaned up.
     */
    public void removeLayer(Drawable layer) {
        int index;

        // find in list of elements
        index = getIndexOfLayer(layer);
        if (index == -1) return;

        // remove via index
        removeLayerAtIndex(index);
    }


    /**
     * @brief remove all drawables from the layout.
     */
    public void clearLayers() {
        int i;
        for (i = mDrawableArray.size()-1; i >= 0; i--) {
            removeLayerAtIndex(i);
        }
    }


    /**
     * @brief Get the index of the reference object.
     *
     * If reference object cannot be found, insert at tail.
     */
    private int getIndexOfLayer(Drawable referenceLayer) {
        int i;
        for (i = 0; i < mDrawableArray.size(); i++) {
            if (mDrawableArray.get(i) == referenceLayer) {
                return i;
            }
        }
        // not found
        return -1;
    }


    /**
     * @brief Get the index of the reference object.
     *
     * If reference object cannot be found, insert at tail.
     */
    public Drawable getLayerAtIndex(int index) {
        if (mDrawableArray.size() == 0 || index >= mDrawableArray.size()) return null;

        return mDrawableArray.get(index);
    }


    /**
     * @brief Returns the number of layers contained within this.
     */
    public int getNumberOfLayers() {
        return mDrawableArray.size();
    }


    /**
     * @brief Enable/disable clipping.
     */
    public void setClipToBounds(boolean clip) {
        mClipToBounds = clip;
    }


    /**
     * @brief Returns true/false of clipping flag.
     */
    public boolean getClipToBounds() {
        return mClipToBounds;
    }


    /**
     * @brief Draw all childs.
     */
    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        // save canvas
        canvas.save();

        // clipping?
        if(mClipToBounds){
            canvas.clipRect(bounds);
        }
        //check background color -> draw background
        if(mBackgroundPaint!=null) {
            canvas.drawRect(bounds, mBackgroundPaint);
        }

        // translate canvas matrix to have relative positions in sub-drawables
        canvas.translate(bounds.left,bounds.top);

        // set for all sub-drawables
        for (Drawable tmpDrawable : mDrawableArray) {
            tmpDrawable.draw(canvas);
        }

        // reset canvas
        canvas.restore();
    }


    /**
     * @brief Returns the number of layers contained within this.
     */
    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);

        // set for all sub-drawables
        for (Drawable tmpDrawable : mDrawableArray) {
            tmpDrawable.setVisible(visible, restart);
        }

        invalidateSelf();
        return changed;
    }


    /**
     * @brief Returns the opacity of all sublayers.
     */
    @Override
    public int getOpacity() {
        int numLayers = mDrawableArray.size();
        int op = numLayers > 0 ? mDrawableArray.get(0).getOpacity() : PixelFormat.TRANSPARENT;

        // get opacity for all sub-drawables
        for (Drawable tmpDrawable : mDrawableArray) {
            op = Drawable.resolveOpacity(op, tmpDrawable.getOpacity());
        }
        return op;
    }


    /**
     * @brief Set dither of all layers.
     */
    @Override
    public void setDither(boolean dither) {

        // set for all sub-drawables
        for (Drawable tmpDrawable : mDrawableArray) {
            tmpDrawable.setDither(dither);
        }
        invalidateSelf();
    }


    /**
     * @brief Set alpha of all layers.
     */
    @Override
    public void setAlpha(int alpha) {

        // set for all sub-drawables
        for (Drawable tmpDrawable : mDrawableArray) {
            tmpDrawable.setAlpha(alpha);
        }
        invalidateSelf();
    }


    /**
     * @brief Set color filter of all layers.
     */
    @Override
    public void setColorFilter(ColorFilter cf) {
        // set for all sub-drawables
        for (Drawable tmpDrawable : mDrawableArray) {
            tmpDrawable.setColorFilter(cf);
        }
        invalidateSelf();
    }


    /**
     * @brief Set Multilayer Height
     */
    public void setLayoutHeight(int height)
    {
        // anything to do?
        if (height == getBounds().height()) return;
        setLayoutSize(getBounds().width(), height);
    }


    /**
     * @brief Set Multilayer Width
     */
    public void setLayoutWidth (int width)
    {
        // anything to do?
        if (width == getBounds().width()) return;
        setLayoutSize(width,getBounds().height());
    }


    /**
     * @brief Set Multilayer Size
     *
     * Convenience function
     *
     * @param size new size of the multilayer. size.x == width, size.y == height
     */
    public void setLayoutSize (Point size) {
        setLayoutSize(size.x,size.y);
    }


    /**
     * @brief Set Multilayer Size
     *
     * @param width new width of the multilayer
     * @param height new height of the multilayer
     */
    public void setLayoutSize (int width, int height)
    {
        Rect bounds = getBounds();

        // anything to do?
        if (bounds.width() == width && bounds.height() == height) {
            return;
        }
        setBounds(bounds.left, bounds.top, bounds.left + width, bounds.top + height);
    }


    /**
     * @brief Set Multilayer Offset
     *
     * @param offset new offset of the multilayer
     */
    public void setLayoutOffset(Point offset) {
        setLayoutOffset(offset.x,offset.y);
    }

    /**
     * @brief Set Multilayer Offset
     *
     * @param x new x-position of the multilayer
     * @param y new y-position of the multilayer
     */
    public void setLayoutOffset(int x, int y)
    {
        Rect bounds = getBounds();
        // anything to do?
        if (x == bounds.left && y == bounds.top) {
            return;
        }
        // remember
        setBounds(x, y, x + bounds.width(), y + bounds.height());
    }


    /**
     * @brief Set the layout rectangle of the Multilayer.
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
     * @brief Get the layout rectangle.
     * Rect contains x,y, and max x and max y. Different to ios
     */
    public Rect getLayoutRect() {
        // get current bounds
        return getBounds();
    }


    /**
     * @brief Get the layout size.
     */
    public Point getLayoutSize() {
        // get current bounds
        Rect bounds = getBounds();
        return new Point(bounds.width(),bounds.height());
    }


    /**
     * @brief Get the layout offset.
     */
    public Point getLayoutOffset() {
        // get current bounds
        Rect bounds = getBounds();
        return new Point(bounds.left,bounds.top);
    }


    /**
     * @brief Get the layout width.
     */
    public int getLayoutWidth() {
        // get current bounds
        Rect bounds = getBounds();
        return bounds.width();
    }


    /**
     * @brief Get the layout height.
     */
    public int getLayoutHeight() {
        // get current bounds
        Rect bounds = getBounds();
        return bounds.height();
    }


    /**
     * @brief React on bounds change (draw all sublayers).
     */
    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (mOnBoundsChangeListener != null) {
            mOnBoundsChangeListener.onPDEBoundsChange(this, bounds);
        }
        doLayout(/*bounds*/);
    }


    /**
     * @brief Set Listener for onBoundsChangeEvent
     */
    public void setOnBoundsChangeListener(OnPDEBoundsChangeListener listener) {
        mOnBoundsChangeListener = listener;
    }

    /**
     * @brief Function where the multilayer reacts on bound changes.
     */
    protected void doLayout() {
        //inform this layer about changes
        invalidateSelf();
    }


    /**
     * @brief Called when drawable changed/needs to be redrawn.
     *
     *  Override from Drawable.Callback
     */
    @Override
    public void invalidateDrawable(Drawable drawable) {
        //inform this layer about changes
        invalidateSelf();
    }


    /**
     * @brief Called when drawable schedule the next frame of its animation. NOT USED
     *
     *  Override from Drawable.Callback
     */
    @Override
    public void scheduleDrawable(Drawable drawable, Runnable runnable, long l) {
        // nothing to do
    }


    /**
     * @brief Called when drawable unschedule an action previously  scheduled with scheduleDrawable. NOT USED
     *
     *  Override from Drawable.Callback
     */
    @Override
    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        // nothing to do
    }


    /**
     * @brief Set additional padding that is needed to display things outside of the element like e.g. outer shadow.
     *
     * @param padding additional padding
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
}
