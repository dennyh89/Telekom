/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * @brief ImageView which can draw a shadow for a BitmapDrawable.
 *
 * If enabled the imagedrawable will be copied, colored and drawn with an offset before the original drawable is drawn.
 */
public class PDEShadowImageView extends ImageView {

    private boolean mShadowEnabled = false;
    private Point mOffset = new Point();
    private int mColor;

    public PDEShadowImageView(Context context) {
        super(context);
        init(context);
    }

    public PDEShadowImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PDEShadowImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        // so far nothing to do here

    }

    public void enableShadow(boolean enable) {
        mShadowEnabled = enable;
    }

    public boolean isShadowEnabled () {
        return mShadowEnabled;
    }

    public void setShadowColor(int color) {
        mColor = color;
    }

    public void setOffset (int x, int y) {
        mOffset.set(x, y);
    }

    /**
     * @brief here the shadow is painted.
     * @param canvas
     */
    private void onDrawShadow(Canvas canvas) {
        BitmapDrawable sd;

        if (!mShadowEnabled) return;

        Drawable drawable = getDrawable();
        if (drawable == null) return;

        Rect bounds = drawable.getBounds();
        int w = bounds.right - bounds.left;
        int h = bounds.bottom - bounds.top;

        if (w == 0 || h == 0) return; // nothing to draw

        if (drawable instanceof BitmapDrawable) {
            sd = new BitmapDrawable(getContext().getResources(), ((BitmapDrawable)drawable).getBitmap());
            sd.setBounds(drawable.copyBounds());
            sd.setGravity(((BitmapDrawable) drawable).getGravity());

            sd.mutate().setColorFilter(mColor, PorterDuff.Mode.SRC_ATOP) ;

            Rect rect = canvas.getClipBounds();
            rect.right += mOffset.x;
            rect.left += mOffset.x;
            rect.top += mOffset.y;
            rect.bottom += mOffset.y;
            canvas.clipRect(rect,Region.Op.REPLACE);

            canvas.translate(mOffset.x,mOffset.y);
            sd.draw(canvas);
            canvas.translate(-mOffset.x,-mOffset.y);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw shadow before the real object is drawn.
        onDrawShadow(canvas);
        // do the 0815 drawing of this view
        super.onDraw(canvas);
    }


}
