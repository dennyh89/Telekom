/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.complex.PDEDrawableScrollbarInteractive;

import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.drawables.PDEDrawableMultilayer;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawablePath;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

//----------------------------------------------------------------------------------------------------------------------
// PDEDrawableScrollbarInteractive
//----------------------------------------------------------------------------------------------------------------------

public class PDEDrawableScrollbarInteractiveArrowButton extends PDEDrawableMultilayer {

    /**
     * @brief Global tag for log outputs.
     */
	@SuppressWarnings("unused")
    private final static String LOG_TAG = PDEDrawableScrollbarInteractiveArrowButton.class.getName();

//----- constants ----------------------------------------------------------------------------------------------------
    public enum PDEDrawableScrollbarInteractiveArrowButtonType {
        Top,
        Bottom,
        Left,
        Right
    }


//-----  properties ---------------------------------------------------------------------------------------------------

    protected PDEColor mElementBackgroundColor;
    protected PDEColor mElementBorderColor;
    protected PDEColor mElementArrowColor;
    protected float mElementBackgroundCornerRadius;
    protected PDEDrawableScrollbarInteractiveArrowButtonType mElementButtonType;

    // pixel shift (to avoid anti alias bug)
    private float mPixelShift = 0.5f;

    // sub layers
    private PDEDrawablePath mElementBackgroundDrawable;
    private PDEDrawablePath mElementArrowDrawable;


//----- init -----------------------------------------------------------------------------------------------------------

    // initialization
    public PDEDrawableScrollbarInteractiveArrowButton() {
        // init sub layers
        initLayers();

        // init PDE values
        setLightStyle();
        mElementBackgroundCornerRadius = PDEBuildingUnits.pixelFromBU(0.25f);
        mElementButtonType = PDEDrawableScrollbarInteractiveArrowButtonType
                .Top;
    }


    /**
     * @brief internal initial helper to init needed layers. shadow is not created here, it must
     *        inited by createShadow seperatly
     */
    private  void initLayers() {
        // initialize layers
        mElementBackgroundDrawable = new PDEDrawablePath();
        mElementArrowDrawable = new PDEDrawablePath();

        // hang in sub layers
        addLayer(mElementBackgroundDrawable);
        addLayer(mElementArrowDrawable);
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief function to set orientation type.
     *
     * @param type new button type / orientation
     */
    public void setElementButtonType(PDEDrawableScrollbarInteractiveArrowButtonType type) {
        // any change?
        if (mElementButtonType == type) return;

        // only allow valid types
        if (mElementButtonType != PDEDrawableScrollbarInteractiveArrowButtonType
                .Bottom &&
                mElementButtonType != PDEDrawableScrollbarInteractiveArrowButtonType
                        .Left &&
                mElementButtonType != PDEDrawableScrollbarInteractiveArrowButtonType
                        .Right &&
                mElementButtonType != PDEDrawableScrollbarInteractiveArrowButtonType
                        .Top) {
            return;
        }

        // remember
        mElementButtonType = type;

        // update
        doLayout();
    }


    /**
     * @brief Set fill (background) color.
     *
     * It's the background color for the bar, not for the handle.
     *
     * @param color The new background color.
     */
    public void setElementBackgroundColor(PDEColor color) {
        // security
        if (mElementBackgroundDrawable == null) {
            return;
        }

        // forward
        mElementBackgroundDrawable.setElementBackgroundColor(color);
    }


    /**
     * @brief Get background color of the scrollbar.
     *
     * @return The color of the background.
     */
    public PDEColor getElementBackgroundColor() {
        // security
        if (mElementBackgroundDrawable == null) {
            return null;
        }
        return mElementBackgroundDrawable.getElementBackgroundColor();
    }


    /**
     * @brief Set border color.
     *
     * @param color The new color of the outline.
     */
    public void setElementBorderColor(PDEColor color) {
        // security
        if (mElementBackgroundDrawable == null) {
            return;
        }
        // forward
        mElementBackgroundDrawable.setElementBorderColor(color);
    }


    /**
     * @brief Get outline color.
     *
     * @return The color of the outline.
     */
    public PDEColor getElementBorderColor() {
        // security
        if (mElementBackgroundDrawable == null) {
            return null;
        }
        return mElementBackgroundDrawable.getElementBorderColor();
    }


    /**
     * @brief function to set colors for light style
     */
    public void setLightStyle() {
        // light style properties
        mElementBackgroundColor = PDEColor.valueOf("DTGrey237");
        mElementBorderColor = PDEColor.valueOf("DTGrey208");
        mElementArrowColor = PDEColor.valueOf("DTGrey75");
        doLayout();
    }


    /**
     * @brief function to set colors for dark style
     */
    public void setDarkStyle() {
        // dark style properties
        mElementBackgroundColor = PDEColor.valueOf("DTGrey82");
        mElementBorderColor = PDEColor.valueOf("DTGrey50");
        mElementArrowColor = PDEColor.valueOf("DTWhite");
        doLayout();
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
        // ignore top bottom width setting
        if (mElementButtonType != PDEDrawableScrollbarInteractiveArrowButtonType.Top
                && mElementButtonType != PDEDrawableScrollbarInteractiveArrowButtonType.Bottom) {
            return;
        }

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
    @Override
    public void setLayoutHeight(int height) {
        // ignore left right height setting
        if (mElementButtonType != PDEDrawableScrollbarInteractiveArrowButtonType.Left
                && mElementButtonType != PDEDrawableScrollbarInteractiveArrowButtonType.Right) {
            return;
        }

        // anything to do?
        if (height == getBounds().height()) {
            return;
        }
        setLayoutSize(new Point(getBounds().width(), height));
    }




//---------------------------------------------------------------------------------------------------------------------
// ----- update / path creating ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief global update function.
     */
    @Override
    protected void doLayout(){
        // get current bounds
        Rect bounds = getBounds();

        // update sub layers
        updateBackgroundDrawable(bounds);
        updateArrowDrawable(bounds);
    }


    /**
     * @brief update function for background drawable to set size, position and color.
     *
     * @param bounds the new bounding rect
     */
    private void updateBackgroundDrawable(Rect bounds) {
        Rect fr;
        Path path;

        // security
        if (mElementBackgroundDrawable == null) return;

        // set bounds
        fr = new Rect(0,0,bounds.width(),bounds.height());

        // set back new values
        mElementBackgroundDrawable.setBounds(fr);
        mElementBackgroundDrawable.setElementBorderWidth(1.0f);
        mElementBackgroundDrawable.setElementBackgroundColor(mElementBackgroundColor);
        mElementBackgroundDrawable.setElementBorderColor(mElementBorderColor);
        // set drawing path
        path = createDrawingPath(fr);
        mElementBackgroundDrawable.setElementPath(path);
    }


    /**
     * @brief update function for arrow drawable.
     *
     * @param bounds the new bounding rect.
     */
    private void updateArrowDrawable(Rect bounds) {
        float triangleSide;
        Path path;
        Point point;
        PointF offset;

        //RectF pathBounds = new RectF(bounds.left,bounds.top,bounds.right,bounds.bottom);
        offset = getPixelShiftedOffset();

        // security
        if (mElementArrowDrawable == null) return;

        // create path
        path = new Path();

        // todo: Can't find an optimal solution for pixel shift problem. It works either in portrait or in landscape
        // orientation correctly. Try to solve this later on.

        mElementArrowDrawable.setElementBorderWidth(1.0f);
        mElementArrowDrawable.setElementBackgroundColor(mElementArrowColor);
        mElementArrowDrawable.setElementBorderColor(mElementArrowColor);

        // calculate arrow-drawable size
        if (mElementButtonType == PDEDrawableScrollbarInteractiveArrowButtonType.Top
                || mElementButtonType == PDEDrawableScrollbarInteractiveArrowButtonType.Bottom) {
            // get needed triangle side size
            triangleSide = bounds.width() / 2.0f - (2 * mPixelShift);
        } else if (mElementButtonType == PDEDrawableScrollbarInteractiveArrowButtonType.Left ||
                   mElementButtonType ==  PDEDrawableScrollbarInteractiveArrowButtonType.Right) {
            // get needed triangle side size
            triangleSide = bounds.height() / 2.0f - (2 * mPixelShift);
        } else return;

        // for down and right rotate it 180 degrees
        if (mElementButtonType == PDEDrawableScrollbarInteractiveArrowButtonType.Top) {
            // create triangle path
            path.moveTo(offset.x, offset.y + triangleSide);
            path.lineTo(offset.x +triangleSide, offset.y + triangleSide);
            path.lineTo(offset.x + triangleSide / 2.0f, offset.y);
            path.lineTo(offset.x, offset.y + triangleSide);
        } else if (mElementButtonType == PDEDrawableScrollbarInteractiveArrowButtonType.Bottom) {
            // create triangle path
            path.moveTo(offset.x,offset.y);
            path.lineTo(offset.x + triangleSide,offset.y);
            path.lineTo(offset.x + triangleSide / 2.0f, offset.y + triangleSide);
            path.lineTo(offset.x, offset.y);
        } else if (mElementButtonType == PDEDrawableScrollbarInteractiveArrowButtonType.Right) {
            // create triangle path
            path.moveTo(offset.x, offset.y);
            path.lineTo(offset.x, offset.y + triangleSide);
            path.lineTo(offset.x + triangleSide,offset.y + triangleSide/2.0f);
            path.lineTo(offset.x, offset.y);
        } else if (mElementButtonType == PDEDrawableScrollbarInteractiveArrowButtonType.Left) {
            // create triangle path
            path.moveTo(offset.x + triangleSide, offset.y);
            path.lineTo(offset.x + triangleSide, offset.y + triangleSide);
            path.lineTo(offset.x, offset.y + triangleSide/2.0f);
            path.lineTo(offset.x + triangleSide, offset.y);
        }

        path.close();

        mElementArrowDrawable.setElementPath(path);

        // center position
        point = new Point(Math.round((bounds.width() - triangleSide)/2.0f),
                          Math.round((bounds.height() -triangleSide)/2.0f));

        // set new bounds
        mElementArrowDrawable.setBounds(point.x,point.y,Math.round(point.x + triangleSide) +Math.round(2 * mPixelShift),
                                        Math.round(point.y +triangleSide) + Math.round(2* mPixelShift));
    }


    /**
     * @brief Creates and delivers the path that should be drawn.
     *
     * @param elementRect the new bounding rect
     * @return the Path that should be drawn.
     */
    private Path createDrawingPath(Rect elementRect){
        Path path = new Path();
        PointF destination, currentPoint;
        RectF pathBounds;
        PointF offset = getPixelShiftedOffset();

        pathBounds = new RectF(elementRect.left, elementRect.top, elementRect.right - 2 * mPixelShift,
                               elementRect.bottom -2 * mPixelShift);

        if (mElementButtonType == PDEDrawableScrollbarInteractiveArrowButtonType.Left){
            // move to start point, which is the left end of top-line
            destination =  new PointF(offset.x + mElementBackgroundCornerRadius, offset.y);
            path.moveTo(destination.x, destination.y);

            // calculate right end of top-line
            destination = new PointF(offset.x + pathBounds.width(), offset.y);

            // draw line to right end of top-line
            path.lineTo(destination.x, destination.y);
            currentPoint = new PointF(destination.x, destination.y);

            // now we're at the upper end of right-line, so calculate lower end of right-line
            destination = new PointF(currentPoint.x, currentPoint.y + pathBounds.height());

            // draw line to lower end of right-line
            path.lineTo(destination.x, destination.y);
            currentPoint = new PointF(destination.x, destination.y);

            // now we're at the right end of the bottom-line,so calculate left end of bottom-line
            destination =  new PointF(currentPoint.x - pathBounds.width() + mElementBackgroundCornerRadius,
                                      currentPoint.y);

            // draw line tp left end of bottom-line
            path.lineTo(destination.x, destination.y);

            // draw left-lower corner
            path.arcTo(new RectF(destination.x - mElementBackgroundCornerRadius,
                                 destination.y - 2 * mElementBackgroundCornerRadius,
                                 destination.x + mElementBackgroundCornerRadius, destination.y), 90.0f, 90.0f);

            // now we're at the lower end of the left-line, so calculate upper end of left-line
            destination = new PointF(offset.x, offset.y + mElementBackgroundCornerRadius);

            // draw line to upper end of left-line
            path.lineTo(destination.x, destination.y);

            // draw left-upper corner
            path.arcTo(new RectF(offset.x, offset.y, offset.x + 2 * mElementBackgroundCornerRadius,
                                 offset.y + 2 * mElementBackgroundCornerRadius),
                       180.0f,90.0f);
        } else if (mElementButtonType == PDEDrawableScrollbarInteractiveArrowButtonType.Right) {
            // move to start point, which is the left end of top-line
            destination = new PointF(offset.x, offset.y);
            path.moveTo(destination.x, destination.y);

            // calculate right end of top-line
            destination = new PointF(offset.x + pathBounds.width() - mElementBackgroundCornerRadius, offset.y);

            // draw line to right end of top-line
            path.lineTo(destination.x, destination.y);

            // draw right upper corner
            path.arcTo(new RectF(destination.x - mElementBackgroundCornerRadius, destination.y,
                                 destination.x + mElementBackgroundCornerRadius,
                                 destination.y + 2 * mElementBackgroundCornerRadius),270.0f,90.0f);

            // now we're at the upper end of right-line, so calculate lower end of right-line
            destination = new PointF(offset.x + pathBounds.width(),
                                     offset.y + pathBounds.height() - mElementBackgroundCornerRadius);

            // draw line to lower end of right-line
            path.lineTo(destination.x, destination.y);

            // draw right-lower corner
            path.arcTo(new RectF(destination.x - 2 * mElementBackgroundCornerRadius,
                                 destination.y - mElementBackgroundCornerRadius,
                                 destination.x,
                                 destination.y + mElementBackgroundCornerRadius), 0.0f, 90.0f);

            // now we're at the right end of the bottom-line, so calculate left end of bottom-line
            destination = new PointF(offset.x, offset.y + pathBounds.height());

            // draw line to left end of bottom-line
            path.lineTo(destination.x, destination.y);

            // now we're at the lower end of the left-line, so calculate upper end of left-line
            destination = new PointF(offset.x,offset.y);

            // draw line to upper end of left-line
            path.lineTo(destination.x, destination.y);
        } else if (mElementButtonType == PDEDrawableScrollbarInteractiveArrowButtonType.Top) {
            // move to start point, which is the left end of top-line
            destination =  new PointF(offset.x + mElementBackgroundCornerRadius, offset.y);
            path.moveTo(destination.x, destination.y);

            // calculate right end of top-line
            destination = new PointF(offset.x + pathBounds.width()-mElementBackgroundCornerRadius, offset.y);

            // draw line to right end of top-line
            path.lineTo(destination.x, destination.y);

            // draw right upper corner
            path.arcTo(new RectF(destination.x - mElementBackgroundCornerRadius, destination.y,
                                 destination.x + mElementBackgroundCornerRadius,
                                 destination.y + 2 * mElementBackgroundCornerRadius), 270.0f, 90.0f);

            // now we're at the upper end of right-line, so calculate lower end of right-line
            destination = new PointF(offset.x + pathBounds.width(), offset.y + pathBounds.height());

            // draw line to lower end of right-line
            path.lineTo(destination.x, destination.y);

            // now we're at the right end of the bottom-line, so calculate left end of bottom-line
            destination = new PointF(offset.x ,offset.y + pathBounds.height());

            // draw line to left end of bottom-line
            path.lineTo(destination.x, destination.y);

            // now we're at the lower end of the left-line, so calculate upper end of left-line
            destination = new PointF(offset.x, offset.y + mElementBackgroundCornerRadius);

            // draw line to upper end of left-line
            path.lineTo(destination.x, destination.y);

            // draw left-upper corner
            path.arcTo(new RectF(offset.x,offset.y,destination.x + 2 * mElementBackgroundCornerRadius,
                                 destination.y + mElementBackgroundCornerRadius),180.0f, 90.0f);

        } else if (mElementButtonType == PDEDrawableScrollbarInteractiveArrowButtonType
                .Bottom) {
            // move to start point, which is the left end of top-line
            destination = new PointF(offset.x,offset.y);
            path.moveTo(destination.x, destination.y);

            // calculate right end of top-line
            destination = new PointF(offset.x + pathBounds.width(), offset.y);

            // draw line to right end of top-line
            path.lineTo(destination.x, destination.y);

            // now we're at the upper end of right-line, so calculate lower end of right-line
            destination = new PointF(offset.x + pathBounds.width(),
                                     offset.y + pathBounds.height() - mElementBackgroundCornerRadius);

            // draw line to lower end of right-line
            path.lineTo(destination.x, destination.y);

            // draw right-lower corner
            path.arcTo(new RectF(destination.x - 2 * mElementBackgroundCornerRadius,
                                 destination.y - mElementBackgroundCornerRadius, destination.x,
                                 destination.y + mElementBackgroundCornerRadius), 0.0f, 90.0f);

            // now we're at the right end of the bottom-line, so calculate left end of bottom-line
            destination = new PointF(offset.x + mElementBackgroundCornerRadius, offset.y + pathBounds.height());

            // draw line to left end of bottom-line
            path.lineTo(destination.x, destination.y);

            // draw left-lower corner
            path.arcTo(new RectF(offset.x, destination.y - 2 * mElementBackgroundCornerRadius,
                                 destination.x + mElementBackgroundCornerRadius, destination.y), 90.0f, 90.0f);

            // now we're at the lower end of the left-line, so calculate upper end of left-line
            destination = new PointF(offset.x, offset.y);

            // draw line to upper end of left-line
            path.lineTo(destination.x, destination.y);
        }

        // path is finished now
        path.close();

        // done
        return path;
    }


    /**
     * @brief Returns the pixel shifted offset for path construction.
     *
     * There is an antialias bug in android when lines are not pixel aligned.
     * With this shift of half an pixel of the offset, we get the path pixel aligned.
     *
     * @return pixel shifted offset
     */
    PointF getPixelShiftedOffset(){
        return new PointF(mPixelShift, mPixelShift);
    }
}
