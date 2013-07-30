/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.layout;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


//----------------------------------------------------------------------------------------------------------------------
//  PDESquareRelativeLayout
//----------------------------------------------------------------------------------------------------------------------


public class PDESquareRelativeLayout extends RelativeLayout {

    /**
     * @brief Global tag for log outputs.
     */
	@SuppressWarnings("unused")
    private static String LOG_TAG = PDESquareRelativeLayout.class.getName();


    /**
     * @brief Constructor.
     */
    public PDESquareRelativeLayout(Context context) {
        super(context);
    }


    /**
     * @brief Constructor.
     */
    public PDESquareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * @brief Constructor.
     */
    public PDESquareRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * @brief OnMeasure function to calculate view size.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int choosenHeightSize = heightSize;
        int choosenWidthSize = widthSize;

        if ( !(heightMode == MeasureSpec.EXACTLY && widthMode == MeasureSpec.EXACTLY) ) {
            //don't be bigger then the AT_MOST size
            choosenWidthSize = widthMode==MeasureSpec.AT_MOST?Math.min(widthSize,heightSize):heightSize;
            choosenHeightSize = heightMode==MeasureSpec.AT_MOST?Math.min(widthSize,heightSize):widthSize;
            if ( heightMode == MeasureSpec.EXACTLY ) {
                // Width dynamic, height fixed
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(choosenWidthSize, MeasureSpec.EXACTLY);
            } else if ( widthMode == MeasureSpec.EXACTLY ) {
                //Width fixed, height dynamic
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(choosenHeightSize, MeasureSpec.EXACTLY);
            } else {
                // Both width and height dynamic, limit to smaller size
                if (widthSize < heightSize) {
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(choosenHeightSize, MeasureSpec.EXACTLY);
                } else {
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(choosenWidthSize, MeasureSpec.EXACTLY);
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
