/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.helpers;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;

//----------------------------------------------------------------------------------------------------------------------
//  PDECharacterSet
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Private helper class for grid drawable in the background.
 */
public class GridBackgroundDrawable extends Drawable {

    private float mBoxSize;
    private PDEColor mColor;
    private Paint mPaint;
    private int mAlpha;


    /**
     * @brief Constructor.
     */
    public GridBackgroundDrawable(PDEColor color) {
        mBoxSize = PDEBuildingUnits.BU();
        mColor = color;
        mPaint = new Paint();
        mPaint.setColor(mColor.getIntegerColor());
        mAlpha = 255;
    }


    /**
     * @brief Called when drawable should be drawn.
     */
    @Override
    public void draw(Canvas canvas) {
        boolean shift = true;
        Rect bounds;
        float boxPositionX,boxPositionX2,boxPositionY,boxPositionY2;

        bounds = getBounds();

        //init start position
        boxPositionX = bounds.left;
        boxPositionY = bounds.top;

        // lets draw the grid
        while (boxPositionY<bounds.bottom) {
            boxPositionY2= Math.min(boxPositionY+mBoxSize,bounds.bottom);
            while (boxPositionX<bounds.right) {
                boxPositionX2= Math.min(boxPositionX+mBoxSize,bounds.right);
                canvas.drawRect(boxPositionX, boxPositionY, boxPositionX2, boxPositionY2, mPaint);
                //increment x Position
                boxPositionX+=mBoxSize*2;
            }
            //increment y position
            boxPositionY = boxPositionY+mBoxSize;

            //every second row starts with right shift
            if(shift){
                boxPositionX = bounds.left + mBoxSize;
            } else {
                boxPositionX = bounds.left;
            }
            shift = !shift;
        }
    }


    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        // inform listener/parent about changes
        invalidateSelf();
    }

    
    /**
     * @brief Sets the alpha of the drawable (not used).
     */
    @Override
    public void setAlpha(int alpha) {
        if(mAlpha!=alpha) {
            mAlpha=alpha;
            // change paint alpha value
            mPaint.setAlpha((int)((mColor.getAlpha()*255)*((float)mAlpha/255)));
            // inform listener/parent about changes
            invalidateSelf();
        }
    }


    /**
     * @brief Sets the color filter of the drawable (not used).
     */
    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // nothing to do
    }


    /**
     * @brief Gets the opacity of the drawable (default is 0).
     */
    @Override
    public int getOpacity() {
        return 0;
    }
}