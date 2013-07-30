/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.components.drawables;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;


// todo: This class is rather outdated. Solved the purpose better with a simple clippath. The
// enhanced flexibility concept (alignment stuff) doesn't work as intended once. It simply doesn't fit,
// when the content already has offsets of its own. So it has no real benefits compared to a simple clippath.

//----------------------------------------------------------------------------------------------------------------------
//  PDERoundCornerMaskDrawable
//----------------------------------------------------------------------------------------------------------------------

/**
 * @brief Helper to available rounded masking of a drawable.
 */
public class PDERoundCornerMaskDrawable extends Drawable {

    /**
     * @brief Global tag for log outputs.
     */
	@SuppressWarnings("unused")
    private final static String LOG_TAG = PDERoundCornerMaskDrawable.class.getName();

//----- constants -----
//    public static final int PDERoundCornerMaskAlignmentLeft = 1 << 0;
//    public static final int PDERoundCornerMaskAlignmentRight = 1 << 1;
//    public static final int PDERoundCornerMaskAlignmentTop = 1 << 2;
//    public static final int PDERoundCornerMaskAlignmentBottom = 1 << 3;
//    public static final int PDERoundCornerMaskAlignmentVerticalCenter = 1 << 4;
//    public static final int PDERoundCornerMaskAlignmentHorizontalCenter = 1 << 5;

//----- properties -----
    private float mTopLeftRadius = 0.0f;
    private float mTopRightRadius = 0.0f;
    private float mBottomRightRadius = 0.0f;
    private float mBottomLeftRadius = 0.0f;
    private Drawable mContent = null;
    private Bitmap mBitmap = null;
    private Path mPath = null;
    private int mAlpha = 0xFF;
    private Paint mPaint = null;
    private boolean mDither = false;
    private ColorFilter mColorFilter;
    //private int mContentAlignment;


//----- init -----------------------------------------------------------------------------------------------------------

    // initialization
    public PDERoundCornerMaskDrawable(){
        mPaint = new Paint();
        mPath = new Path();
        mBitmap = null;
        mContent = null;
        mPaint.setAlpha(mAlpha);
//        mContentAlignment = PDERoundCornerMaskAlignmentTop | PDERoundCornerMaskAlignmentLeft;
    }




//---------------------------------------------------------------------------------------------------------------------
// ----- setter / getter ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------



    /**
     * @brief setter for CornerRadius
     *
     * @param cornerRadius the new corner radius for the rounding
     */
    public void setCornerRadius(float cornerRadius) {
        if (mTopRightRadius == cornerRadius && mTopLeftRadius == cornerRadius
            && mBottomRightRadius == cornerRadius && mBottomLeftRadius == cornerRadius) {
            return;
        }
        mTopLeftRadius = cornerRadius;
        mTopRightRadius = cornerRadius;
        mBottomRightRadius = cornerRadius;
        mBottomLeftRadius = cornerRadius;
    }

    /**
     * @brief setter for CornerRadius
     *
     * @param topLeftRadius corner radius for rounding of top left corner
     * @param topRightRadius corner radius for rounding of top right corner
     * @param bottomRightRadius corner radius for rounding of bottom right corner
     * @param bottomLeftRadius corner radius for rounding of bottom left corner
     */
    public void setCornerRadii(float topLeftRadius, float topRightRadius, float bottomRightRadius,
                               float bottomLeftRadius) {
        // something to do?
        if (mTopRightRadius == topRightRadius && mTopLeftRadius == topLeftRadius
            && mBottomRightRadius == bottomRightRadius && mBottomLeftRadius == bottomLeftRadius) {
            return;
        }
        // save radi
        mTopLeftRadius = topLeftRadius;
        mTopRightRadius = topRightRadius;
        mBottomRightRadius = bottomRightRadius;
        mBottomLeftRadius = bottomLeftRadius;
    }


    /**
     * @brief This function sets the content drawable which should become rounded.
     *
     * @param content the drawable that should become rounded.
     */
    public void setContentDrawable(Drawable content) {
        mContent = content;
        invalidateSelf();
    }


    /**
     * @brief set alignment of the content within the rounding mask
     */
//    public void setContentAlignment(int alignment) {
//        // any change?
//        if (alignment == mContentAlignment) {
//            return;
//        }
//
//        // verification
//        if(((alignment & PDERoundCornerMaskAlignmentLeft ) & PDERoundCornerMaskAlignmentRight) != 0 ||
//           ((alignment & PDERoundCornerMaskAlignmentLeft ) & PDERoundCornerMaskAlignmentHorizontalCenter) != 0  ||
//           ((alignment & PDERoundCornerMaskAlignmentHorizontalCenter ) & PDERoundCornerMaskAlignmentRight) != 0 ) {
//            Log.e(LOG_TAG,"Several different horizontal alignments at once make no sense! Setting ignored!");
//            return;
//        }
//        if(((alignment & PDERoundCornerMaskAlignmentTop) & PDERoundCornerMaskAlignmentBottom) != 0 ||
//           ((alignment & PDERoundCornerMaskAlignmentTop) & PDERoundCornerMaskAlignmentVerticalCenter) != 0 ||
//           ((alignment & PDERoundCornerMaskAlignmentVerticalCenter) & PDERoundCornerMaskAlignmentBottom) != 0) {
//            Log.e(LOG_TAG,"Several different vertical alignments at once make no sense! Setting ignored!");
//            return;
//        }
//
//        // remember
//        mContentAlignment = alignment;
//        invalidateSelf();
//    }


//---------------------------------------------------------------------------------------------------------------------
// ----- overrides ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Overriden draw function
     */
    @Override
    public void draw(Canvas canvas) {
        if (mContent == null) {
            return;
        }

        //Bitmap prepareBitmap = null;
        float left,right,top,bottom;
        //Canvas canvas1, 
        Canvas canvas2, rounderCanvas;
        Bitmap rounder, output;
        Paint xferPaint;

        // init
        Rect contentBounds = mContent.getBounds();

        // security
        if (contentBounds.width() <= 0 || contentBounds.height() <= 0) {
            return;
        }

        // --- draw content drawable in a bitmap
        //prepareBitmap = Bitmap.createBitmap(contentBounds.width()+1, contentBounds.height()+1,
        //                                    Bitmap.Config.ARGB_8888);
        //canvas1 = new Canvas(prepareBitmap);
        //mContent.draw(canvas1);

        // --- create masking path
        // first set borders
        left = getBounds().left;
        right = getBounds().right;
        top = getBounds().top;
        bottom = getBounds().bottom;

        // init radi
        float[] radii = new float[] {
                mTopLeftRadius, mTopLeftRadius, mTopRightRadius, mTopRightRadius, mBottomRightRadius,
                mBottomRightRadius, mBottomLeftRadius, mBottomLeftRadius
        };

        mPath.reset();
        mPath.addRoundRect(new RectF(left, top, right, bottom), radii, Path.Direction.CW);
        mPath.close();

        // draw masking path in a bitmap
        rounder = Bitmap.createBitmap(getBounds().width(), getBounds().height(), Bitmap.Config.ARGB_8888);
        rounderCanvas = new Canvas(rounder);
        xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xferPaint.setColor(Color.RED);
        rounderCanvas.drawPath(mPath,xferPaint);


        // in the end we only want to draw the intersection between the content bitmap and the masking bitmap.
        // So we configure the correct PorterDuff mode for this operation.
        xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        // create a bitmap that stores the result of the intersection of the other two bitmaps
        output = Bitmap.createBitmap(getBounds().width(), getBounds().height(), Bitmap.Config.ARGB_8888);
        canvas2 = new Canvas(output);


        // do the bitmap intersection
        //canvas2.drawBitmap(rounder,0,0,null);
//        Point contentOffset = new Point(0,0);
//        if ((mContentAlignment & PDERoundCornerMaskAlignmentLeft) != 0) {
//            contentOffset.x = 0;
//        } else if ((mContentAlignment & PDERoundCornerMaskAlignmentRight) != 0) {
//            contentOffset.x = rounder.getWidth() - prepareBitmap.getWidth();
//        } else if ((mContentAlignment & PDERoundCornerMaskAlignmentHorizontalCenter) != 0){
//            contentOffset.x = Math.round((rounder.getWidth() - prepareBitmap.getWidth()) / 2);
//        }
//        if ((mContentAlignment & PDERoundCornerMaskAlignmentTop) != 0) {
//            contentOffset.y = 0;
//        } else if ((mContentAlignment & PDERoundCornerMaskAlignmentBottom) != 0) {
//            contentOffset.y = rounder.getHeight() - prepareBitmap.getHeight();
//        } else if ((mContentAlignment & PDERoundCornerMaskAlignmentVerticalCenter) != 0) {
//            contentOffset.y = Math.round((rounder.getHeight() - prepareBitmap.getHeight())/2);
//        }

        // draw content into output
        mContent.draw(canvas2);
        // intersect with mask
        canvas2.drawBitmap(rounder, 0, 0, xferPaint);

        // remember
        mBitmap =  output;

        // finally draw to our canvas
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        }
        //canvas.clipPath(mPath);
    }


    /**
     * @brief abstract in Drawable, need to override
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
     * @brief Set alpha of whole element.
     */
    @Override
    public void setAlpha(int alpha) {
        // change?
        if (mAlpha == alpha) {
            return;
        }

        mAlpha = alpha;
        mPaint.setAlpha(mAlpha);
        invalidateSelf();
    }
    

    /**
     * @brief abstract in Drawable, need to override
     *
     */
    @Override
    public void setColorFilter(ColorFilter cf) {
        // change?
        if (mColorFilter == cf) {
            return;
        }

        mColorFilter = cf;
        mPaint.setColorFilter(mColorFilter);
        invalidateSelf();
    }


    /**
     * @brief Set dither of whole element.
     */
    @Override
    public void setDither(boolean dither) {
        // change?
        if (mDither == dither) {
            return;
        }

        mDither = dither;
        mPaint.setDither(mDither);
        invalidateSelf();
    }

}
