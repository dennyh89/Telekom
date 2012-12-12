/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.layout.PDEAbsoluteLayout;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;


public class PDELayerSunken extends PDEAbsoluteLayout {

    protected PDEViewBorderLine mBorderLine;
    protected PDEViewShapedInnerShadow mInnerShadow;
    protected PDEViewBackground mBackground;

    protected PDEColor mSunkenBackgroundColor;
    protected PDEColor mSunkenBorderColor;
    protected PDEColor mInnerShadowColor;

    protected float mInnerShadowOpacity;
    protected PointF mInnerShadowOffset;
    protected float mInnerShadowBlurRadius;


    /**
     * Constructor
     *
     * @param context {@link android.content.Context}
     */
    public PDELayerSunken(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor
     *
     * @param context {@link Context}
     * @param attrs {@link android.util.AttributeSet}
     */
    public PDELayerSunken(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Constructor
     *
     * @param context {@link Context}
     * @param attrs {@link AttributeSet}
     * @param defStyle int
     */
    public PDELayerSunken(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    protected void init(){
        mSunkenBackgroundColor = PDEColor.valueOf("DTWhite");
        mSunkenBorderColor = PDEColor.valueOf("DTUIBorder");
        mInnerShadowColor = PDEColor.valueOf("DTBlack");
        // Todo: Shape path

        mBorderLine = new PDEViewBorderLine(PDECodeLibrary.getInstance().getApplicationContext());
        mInnerShadow = new PDEViewShapedInnerShadow(PDECodeLibrary.getInstance().getApplicationContext());
        mBackground = new PDEViewBackground(PDECodeLibrary.getInstance().getApplicationContext(),
                                           new PDEDrawableShape());

        addView(mBackground);
        addView(mBorderLine);
        addView(mInnerShadow);

        mBorderLine.setBorderColor(mSunkenBorderColor.getIntegerColor());
        mBorderLine.setBorderWidth(1.0f);
        getBackgroundDrawable().setBackgroundColor(mSunkenBackgroundColor.getIntegerColor());


        //mInnerShadowOpacity = 0.24f;
        //mInnerShadowBlurRadius = (float) PDEBuildingUnits.oneFourthBU();
        mInnerShadowOffset = new PointF(1.0f,1.0f);

        mInnerShadow.setShapeColor(mInnerShadowColor);
        mInnerShadow.setShapeOffset(mInnerShadowOffset);
        //mInnerShadow.setBlurRadius(mInnerShadowBlurRadius);
        //mInnerShadow.setShapeOpacity(mInnerShadowOpacity);

    }


    public PDEColor getSunkenBackgroundColor(){
        return mSunkenBackgroundColor;
    }

    public void setSunkenBackgroundColor(PDEColor color){
        if (color.getIntegerColor() == mSunkenBackgroundColor.getIntegerColor()) return;
        mSunkenBackgroundColor = color;
        getBackgroundDrawable().setBackgroundColor(mSunkenBackgroundColor.getIntegerColor());
    }


    public PDEColor getSunkenBorderColor(){
        return mSunkenBorderColor;
    }

    public void setSunkenBorderColor(PDEColor color){
        if (color.getIntegerColor() == mSunkenBorderColor.getIntegerColor()) return;
        mSunkenBorderColor = color;
        mBorderLine.setBorderColor(mSunkenBorderColor.getIntegerColor());
    }

    public PDEColor getInnerShadowColor(){
        return mInnerShadowColor;
    }

    public void setInnerShadowColor(PDEColor color){
        if (color.getIntegerColor() == mInnerShadowColor.getIntegerColor()) return;
        mInnerShadowColor = color;
        mInnerShadow.setShapeColor(mInnerShadowColor);
    }


    public float getInnerShadowOpacity(){
        return mInnerShadowOpacity;
    }

    public void setInnerShadowOpacity(float opacity){
        if (mInnerShadowOpacity == opacity) return;
        mInnerShadowOpacity = opacity;
        mInnerShadow.setShapeOpacity(mInnerShadowOpacity);
    }

    public PointF getInnerShadowOffset(){
        return mInnerShadowOffset;
    }

    public void setInnerShadowOffset(PointF offset){
        if (mInnerShadowOffset.equals(offset.x,offset.y)) return;
        mInnerShadowOffset = offset;
        mInnerShadow.setShapeOffset(mInnerShadowOffset);
    }



    public float getInnerShadowBlurRadius(){
        return mInnerShadowBlurRadius;
    }

    public void setInnerShadowBlurRadius(float radius){
        if (mInnerShadowBlurRadius == radius) return;
        mInnerShadowBlurRadius = radius;
        mInnerShadow.setShapeOpacity(mInnerShadowOpacity);
    }

    // ToDo: setShapePath


    public void setShapeRect(RectF rect){
        getBackgroundDrawable().setShapeRect(rect);
        mInnerShadow.setShapeRect(new RectF(rect.left,rect.top,rect.right-2.0f,rect.bottom-2.0f));
        mBorderLine.setShapeRect(rect);
    }

    public void setShapeRoundedRect(RectF rect, float cornerRadius) {
        getBackgroundDrawable().setShapeRoundedRect(rect,cornerRadius);
        mInnerShadow.setShapeRoundedRect(new RectF(rect.left,rect.top,rect.right-2.0f,rect.bottom-2.0f),
                                         cornerRadius-1.0f);
        mBorderLine.setShapeRoundedRect(rect,cornerRadius);
    }

    public void setShapeOval(RectF rect) {
        getBackgroundDrawable().setShapeOval(rect);
        mBorderLine.setShapeOval(rect);
        mInnerShadow.setShapeOval(new RectF(rect.left,rect.top,rect.right-2.0f,rect.bottom-2.0f));
    }

    protected PDEDrawableShape getBackgroundDrawable(){
        if(mBackground.getDrawable() instanceof PDEDrawableShape){
            return (PDEDrawableShape)mBackground.getDrawable();
        }
        return null;
    }

}
