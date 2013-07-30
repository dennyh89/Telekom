/*
 * Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.boxes;

//----------------------------------------------------------------------------------------------------------------------
// PDEDrawableNotificationFrame
//----------------------------------------------------------------------------------------------------------------------

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import de.telekom.pde.codelibrary.ui.buildingunits.PDEBuildingUnits;
import de.telekom.pde.codelibrary.ui.color.PDEColor;
import de.telekom.pde.codelibrary.ui.components.drawables.PDEDrawableBase;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableShapedShadow;


/**
 * @brief Graphics primitive - a notification frame.
 *
 * Looks like a speechbubble and is used for tooltips or info_flags.
 */
@SuppressWarnings("unused")
public class PDEDrawableNotificationFrame extends PDEDrawableBase {

    /**
     * @brief Global tag for log outputs.
     */
    private final static String LOG_TAG = PDEDrawableNotificationFrame.class.getName();

//----- constants ----------------------------------------------------------------------------------------------------
    public enum TriangleSide {
        SideTop,
        SideRight,
        SideBottom,
        SideLeft
    }

    public enum TrianglePosition {
        Left,
        Center,
        Right,
        Top,
        Bottom
    }

//-----  properties ---------------------------------------------------------------------------------------------------

    // basic properties
    // colors
    protected PDEColor mElementBackgroundColor;
    protected PDEColor mElementBorderColor;
    // measurements
    protected float mElementBorderWidth;
    protected float mElementCornerRadius;

    // triangle properties
    protected int mElementTriangleWidth;
    protected int mElementTriangleTipDistance;
    protected int mElementTriangleMargin;
    protected boolean mElementTriangleEnabled;

    // derived triangle properties
    protected float mElementTriangleTipPosition;
    protected TriangleSide mElementTriangleSide;

    // private helpers
    private float mElementWantedTriangleTipPosition;
    private int mElementWantedTriangleWidth;
    private int mElementWantedTriangleTipDistance;
    private float mElementWantedCornerRadius;
    private boolean mElementTriangleWantedTipPositionIsRelative;
    private Path mElementPath;
    private PDEDrawableShapedShadow mElementShadowDrawable;

    private Paint mBorderPaint;
    private Paint mBackgroundPaint;



//----- init -----------------------------------------------------------------------------------------------------------

    // initialization
    public PDEDrawableNotificationFrame() {
        // init drawable basics
        super();
        // init to PDE defaults
        mElementBackgroundColor = PDEColor.valueOf("DTBlack");
        mElementBorderColor = PDEColor.valueOf("DTBlack");
        mElementBorderWidth = 1.0f;
        mElementWantedCornerRadius = PDEBuildingUnits.twoThirdsBU();
        mElementCornerRadius = 0.0f;
        mElementWantedTriangleWidth = PDEBuildingUnits.pixelFromBU(1.16666f);
        mElementTriangleWidth = 0;
        mElementWantedTriangleTipDistance = PDEBuildingUnits.BU();
        mElementTriangleTipDistance = 0;
        mElementWantedTriangleTipPosition = PDEBuildingUnits.pixelFromBU(1.3333f);
        mElementTriangleTipPosition = 0;
        mElementTriangleSide = TriangleSide.SideBottom;
        mElementPath = new Path();
        mElementTriangleEnabled = true;
        mElementTriangleMargin = PDEBuildingUnits.BU();
        mAlpha = Math.round(0.9f * 0xFF);

        // shadow is created on demand
        mElementShadowDrawable = null;

        //init paints for drawing
        update(true);
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
    public Drawable createElementShadow() {
        // already created?
        if (mElementShadowDrawable != null) {
            return mElementShadowDrawable;
        }
        // init shadow drawable
        mElementShadowDrawable = new PDEDrawableShapedShadow();
        mElementShadowDrawable.setElementShapeOpacity(0.25f);
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
    public Drawable getElementShadow() {
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
            Path shadowPath;

            // set shadow bounds
            frame = new Rect(bounds.left, bounds.top, bounds.left + elementSize.x,
                             bounds.top + elementSize.y);
            mElementShadowDrawable.setBounds(frame);
            // make a copy of the original element path for the shadow
            shadowPath = new Path(mElementPath);
            // offset by blur radius of shadow
            shadowPath.offset(mElementShadowDrawable.getElementBlurRadius(),mElementShadowDrawable.getElementBlurRadius());
            mElementShadowDrawable.setElementShapePath(shadowPath);
        }
    }




//---------------------------------------------------------------------------------------------------------------------
// ----- general setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Set fill (background) color.
     *
     * @param color The new backgroundcolor of the speech bubble.
     */
    public void setElementBackgroundColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementBackgroundColor.getIntegerColor()) {
            return;
        }

        // remember
        mElementBackgroundColor = color;
        mBackgroundPaint.setColor(mElementBackgroundColor.newIntegerColorWithCombinedAlpha(mAlpha));

        // update
        update();
    }


    /**
     * @brief Get background color.
     *
     * @return The color of the background.
     */
    public PDEColor getElementBackgroundColor() {
        return mElementBackgroundColor;
    }


    /**
     * @brief Set border color.
     *
     * @param color The new color of the outline.
     */
    public void setElementBorderColor(PDEColor color) {
        // any change?
        if (color.getIntegerColor() == mElementBorderColor.getIntegerColor()) {
            return;
        }

        // remember
        mElementBorderColor = color;
        mBorderPaint.setColor(mElementBorderColor.newIntegerColorWithCombinedAlpha(mAlpha));

        // update
        update();
    }


    /**
     * @brief Get outline color.
     *
     * @return The color of the outline.
     */
    public PDEColor getElementBorderColor() {
        return mElementBorderColor;
    }


    /**
     * @brief Set border width.
     *
     * @param width The new width of the outline.
     */
    public void setElementBorderWidth(float width) {
        // any change?
        if (width == mElementBorderWidth) {
            return;
        }

        // remember
        mElementBorderWidth = width;
        mBorderPaint.setStrokeWidth(mElementBorderWidth);
        // update
        update();
    }


    /**
     * @brief Get border width.
     *
     * @return The width of the outline.
     */
    public float getElementBorderWidth() {
        return mElementBorderWidth;
    }


    /**
     * @brief Set corner radius.
     *
     * @param radius The new radius of the rounded corners.
     */
    public void setElementCornerRadius(float radius) {
        // any change?
        if (radius == mElementWantedCornerRadius) {
            return;
        }

        // remember
        mElementWantedCornerRadius = radius;

        // update
        doLayout();
        update();
    }


    /**
     * @brief Get corner radius.
     *
     * @return The radius of the rounded corners.
     */
    public float getElementCornerRadius() {
        return mElementCornerRadius;
    }


    /**
     * @brief Get wanted corner radius.
     *
     * @return The wanted radius of the rounded corners.
     */
    public float getElementWantedCornerRadius() {
        return mElementWantedCornerRadius;
    }


    /**
     * @brief Set opacity of whole element.
     *
     * @param opacity new element opacity.
     */
    public void setElementOpacity(float opacity) {
        int alpha = Math.round(opacity * 255);
        setAlpha(alpha);
    }


    /**
     * @brief Get opacity of whole element.
     *
     * @return element opacity.
     */
    public float getElementOpacity() {
        return mAlpha / 255;
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- triangle setters and getters ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Set width of the triangle.
     *
     * @param width The width/length of the triangle side (base line) which is attached to the rounded rect.
     */
    public void setElementTriangleWidth(int width) {
        // any change?
        if (width == mElementWantedTriangleWidth) {
            return;
        }

        // remember
        mElementWantedTriangleWidth = width;

        // update
        doLayout();
        update();
    }


    /**
     * @brief Get width of the triangle.
     *
     * @return The width/length of the triangle side (base line) which is attached to the rounded rect.
     */
    public int getElementTriangleWidth() {
        return mElementTriangleWidth;
    }


    /**
     * @brief Get wanted width of the triangle.
     *
     * @return The width/length of the triangle side (base line) which is attached to the rounded rect.
     */
    public int getElementWantedTriangleWidth() {
        return mElementWantedTriangleWidth;
    }


    /**
     * @brief Set distance between triangle base line and triangle tip.
     *
     * @param distance The distance between the base line of the triangle and the triangle tip.
     */
    public void setElementTriangleTipDistance(int distance) {
        // any change?
        if (distance == mElementWantedTriangleTipDistance) {
            return;
        }

        // remember
        mElementWantedTriangleTipDistance = distance;

        // update
        doLayout();
        update();
    }


    /**
     * @brief Get distance between triangle base line and triangle tip.
     *
     * @return The distance between the base line of the triangle and the triangle tip.
     */
    public int getElementTriangleTipDistance() {
        return mElementTriangleTipDistance;
    }

    /**
     * @brief Get the wanted distance between triangle base line and triangle tip.
     *
     * @return The wanted distance between the base line of the triangle and the triangle tip.
     */
    public int getElementWantedTriangleTipDistance() {
        return mElementWantedTriangleTipDistance;
    }

    /**
     * @brief Set margin which is kept between the triangle and the rounded corners.
     *
     * @param margin The margin between triangle and rounded corners.
     */
    public void setElementTriangleMargin(int margin) {
        // any change?
        if (margin == mElementTriangleMargin) {
            return;
        }

        // remember
        mElementTriangleMargin = margin;

        // update
        doLayout();
        update();
    }


    /**
     * @brief Get margin which is kept between the triangle and the rounded corners.
     *
     * @return The margin between triangle and rounded corners.
     */
    public int getElementTriangleMargin() {
        return mElementTriangleMargin;
    }


    /**
     * @brief Enable / Disable the small triangle.
     *
     * @param enabled true-> triangle visible, false -> triangle invisible
     */
    public void setElementTriangleEnabled(boolean enabled) {
        // any change?
        if (enabled == mElementTriangleEnabled) {
            return;
        }

        // remember
        mElementTriangleEnabled = enabled;

        // update
        doLayout();
        update();
    }


    /**
     * @brief Checks if small triangle is enabled or disabled.
     *
     * @return true-> triangle visible, false -> triangle invisible
     */
    public boolean isElementTriangleEnabled() {
        return mElementTriangleEnabled;
    }


    /**
     * @brief Get the absolute pixel position of the triangle tip.
     *
     * @return the absolute pixel position of the triangle tip.
     */
    public float getElementTriangleTipPosition() {
        return mElementTriangleTipPosition;
    }

    /**
     * @brief Get the side of the rounded rect the triangle is attached to.
     *
     * @return the side of the rounded rect the triangle is attached to.
     */
    public TriangleSide getElementTriangleSide() {
        return mElementTriangleSide;
    }


    /**
     * @brief Set absolute pixel position of triangle tip and the side at which the triangle should be drawn.
     *
     * The triangle can be attached to each of the four sides (top/right/bottom/left) of the rounded rect and on every
     * valid pixel position along the particular side. With this function it's possible to define the side and the pixel
     * position of the triangle tip at once. The point of origin for the pixel positions is on the left for the horizontal
     * sides and on top for the vertical sides.
     *
     * @param position The absolute pixel position of the triangle tip.
     * @param side The side of the rounded rect on which the triangle is attached.
     */
    public void setElementTriangleTipPositionAbsolute(float position, TriangleSide side) {

        // any change?
        if (side != mElementTriangleSide) {
            // remember
            mElementTriangleSide = side;
        }

        // remember
        mElementWantedTriangleTipPosition = position;
        mElementTriangleWantedTipPositionIsRelative = false;

        // update
        doLayout();
        update();
    }


    /**
     * @brief Set relative position of triangle tip and the side at which the triangle should be drawn
     *
     * The triangle can be attached to each of the four sides (top/right/bottom/left) of the rounded rect and on every
     * valid position along the particular side. With this function it's possible to define the side and the relative
     * position of the triangle tip at once. The positioning is relative to the length of the particular side.
     * The point of origin for the pixel positions is on the left for the horizontal sides and on top for the vertical sides.
     *
     * @param position The relative position of the triangle tip.
     * @param side The side of the rounded rect on which the triangle is attached.
     */
    public void setElementTriangleTipPositionRelative(float position, TriangleSide side) {

        // any change?
        if (side != mElementTriangleSide) {
            // remember
            mElementTriangleSide = side;
        }

        // remember
        mElementWantedTriangleTipPosition = position;
        mElementTriangleWantedTipPositionIsRelative = true;

        // update
        doLayout();
        update();
    }


    /**
     * @brief Set Triangle at predefined positions (convenience function)
     *
     * The styleguide predefines 12 positions of the triangle. Left, Center, Right for the horizontal edges and Top, Center, Bottom for the vertical edges.
     * With this function these positions can easily be set by delivering the side on which the triangle should be drawn and the desired position at this side.
     *
     * @param position A predefined triangle position (left/center/right/top/bottom).
     * @param side The side of the rounded rect on which the triangle is attached.
     */
    public void setElementTriangleTipPositionPredefined(TrianglePosition position,
                                                        TriangleSide side) {
        // "left" and "top" are handled the same way
        if (position ==
            TrianglePosition.Left ||
            position ==
            TrianglePosition.Top) {
            setElementTriangleTipPositionRelative(0.0f, side);
        }

        // "right" and "bottom" are handled the same way
        else if (position ==
                 TrianglePosition.Right ||
                 position ==
                 TrianglePosition.Bottom) {
            setElementTriangleTipPositionRelative(1.0f, side);
        } else if (position ==
                   TrianglePosition.Center) {
            setElementTriangleTipPositionRelative(0.5f, side);
        }
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- measurement helpers ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------



    /**
     * @brief Helper that delivers the first drawing position of the triangle.
     *
     * For drawing the triangle, we start at the first corner of the triangle, line to
     * the tip and then we line from the tip to the last corner. If we draw our triangle e.g.
     * on the top side of the rounded rect the triangle corner left to the tip is the first
     * triangle position which is delivered by this function.
     *
     * @param tipPosition The desired position of the triangle tip.
     * @return The first corner position of the triangle seen in drawing direction.
     */
    private float getTriangleFirstPosition(float tipPosition) {
        // return
        return (tipPosition - mElementTriangleWidth / 2);
    }


    /**
     * @brief Helper that delivers the last drawing position of the triangle.
     *
     * For drawing the triangle, we start at the first corner of the triangle, line to
     * the tip and then we line from the tip to the last corner. If we draw our triangle e.g.
     * on the top side of the rounded rect the triangle corner right to the tip is the last
     * triangle position which is delivered by this function.
     *
     * @param tipPosition The desired position of the triangle tip.
     * @return The last corner position of the triangle seen in drawing direction.
     */
    private float getTriangleLastPosition(float tipPosition) {
        // return
        return (tipPosition + mElementTriangleWidth / 2);
    }


    /**
     * @brief Delivers the effective width of the top/bottom edge (minus rounded corners) of the rounded rect.
     *
     * @param elementSize The current size of the whole element.
     * @return width of the horizontal edges of the rounded rect (minus rounded corners).
     */
    private float getElementEdgeWidth(Point elementSize) {
        if (mElementTriangleSide ==
            TriangleSide.SideTop ||
            mElementTriangleSide ==
            TriangleSide.SideBottom ||
            !mElementTriangleEnabled) {
            // return
            return elementSize.x - 2 * mElementCornerRadius;
        } else {
            // we're smaller because the triangle needs space
            return elementSize.x - mElementTriangleTipDistance - 2 * mElementCornerRadius;
        }
    }


    /**
     * @brief Delivers the effective height of the left/right edge (minus rounded corners) of the rounded rect.
     *
     * @param elementSize The current size of the whole element.
     * @return height of the vertical edges of the rounded rect (minus rounded corners).
     */
    private float getElementEdgeHeight(Point elementSize) {
        if (mElementTriangleSide ==
            TriangleSide.SideLeft ||
            mElementTriangleSide ==
            TriangleSide.SideRight ||
            !mElementTriangleEnabled) {
            // return
            return elementSize.y - 2 * mElementCornerRadius;
        } else {
            // we're smaller because the triangle needs space
            return elementSize.y - mElementTriangleTipDistance - 2 * mElementCornerRadius;
        }
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- path drawing ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Get drawing start point
     *
     * Calculate & deliver the start point of the drawing path.
     * We start drawing at the left startpoint of the top edge.
     * The used pixelShift is needed to avoid an antialiasing bug which appears if we're not correctly pixelaligned.
     *
     * @return start point of drawing
     */
    private PointF getElementDrawingStartPoint() {
        if (!mElementTriangleEnabled) {
            // no triangle, so side setting doesn't matter
            return new PointF(mElementCornerRadius + mPixelShift, 0.0f+ mPixelShift);
        } else if (mElementTriangleSide ==
                   TriangleSide.SideLeft) {
            // take place into account that is needed for the triangle on the left side (additional x-Offset)
            return new PointF(mElementTriangleTipDistance + mElementCornerRadius+ mPixelShift, 0.0f+ mPixelShift);
        } else if (mElementTriangleSide ==
                   TriangleSide.SideTop) {
            // take place into account that is needed for the triangle on the top side (additional y-Offset)
            return new PointF(mElementCornerRadius+ mPixelShift, mElementTriangleTipDistance+ mPixelShift);
        } else {
            // default
            return new PointF(mElementCornerRadius+ mPixelShift, 0.0f+ mPixelShift);
        }
    }


    /**
     * @brief Attaches the triangle to the given path.
     *
     * Draws a line from the current point of the path to the first triangle point, then a line from this point to
     * the triangle tip and then a line from the tip to the last triangle point.
     *
     * @param path The drawing path to which we add the triangle with this function.
     * @param elementSize The size of the whole element.
     */
    private void drawTriangle(Path path, Point elementSize, PointF currentPoint) {
        PointF firstTrianglePoint, tipTrianglePoint, lastTrianglePoint;
        PointF lineSegmentStartPoint, lineSegmentEndPoint;

        // first check if triangle is enabled
        if (!mElementTriangleEnabled) {
            return;
        }

        if (elementSize.x < (2 * mElementWantedCornerRadius + 2 * mElementTriangleMargin) ||
            elementSize.y < (2 * mElementWantedCornerRadius + 2 * mElementTriangleMargin)) {
            return;
        }


        switch (mElementTriangleSide) {
            case SideTop:
                // at the beginning we're always placed at the startpoint of the current edge
                lineSegmentStartPoint = currentPoint;
                // add the effective width of the edge to get its endpoint
                lineSegmentEndPoint = new PointF(lineSegmentStartPoint.x + getElementEdgeWidth(elementSize),
                                                 lineSegmentStartPoint.y);
                // x-position of first triangle point is tip - triangleWidth / 2; y-position is the same as the
                // baseline
                firstTrianglePoint = new PointF(mElementTriangleTipPosition - mElementTriangleWidth / 2,
                                                currentPoint.y);
                // the y-position of the tip is baseline - tip distance
                tipTrianglePoint = new PointF(mElementTriangleTipPosition,
                                              currentPoint.y - mElementTriangleTipDistance);
                // x-position of first triangle point is tip + triangleWidth / 2; y-position is the same as the baseline
                lastTrianglePoint = new PointF(mElementTriangleTipPosition + mElementTriangleWidth / 2,
                                               currentPoint.y);

                // check bounds
                if (tipTrianglePoint.y < 0 ||
                    firstTrianglePoint.x < lineSegmentStartPoint.x ||
                    lastTrianglePoint.x > lineSegmentEndPoint.x) {
                    Log.e(LOG_TAG, "Triangle values out of bounds!");
                    // abort
                    return;
                }
                break;

            case SideRight:
                // hint: we draw the right line from top to bottom, so starting points etc. are set accordingly
                // at the beginning we're always placed at the starting point of the current edge
                lineSegmentStartPoint = currentPoint;
                // add the effective height of the edge to get its endpoint
                lineSegmentEndPoint = new PointF(lineSegmentStartPoint.x,
                                                 lineSegmentStartPoint.y + getElementEdgeHeight(elementSize));
                // x-position of first triangle point is the same as the baseline; y-position is tip - triangleWidth/2
                firstTrianglePoint = new PointF(currentPoint.x,
                                                mElementTriangleTipPosition - mElementTriangleWidth / 2);
                // the x-position of the tip is baseline + tip distance
                tipTrianglePoint = new PointF(currentPoint.x + mElementTriangleTipDistance,
                                              mElementTriangleTipPosition);
                // x-position of first triangle point is the same as the baseline; y-position is tip + triangleWidth /2
                lastTrianglePoint = new PointF(currentPoint.x,
                                               mElementTriangleTipPosition + mElementTriangleWidth / 2);

                // check bounds
                if (firstTrianglePoint.x > lineSegmentStartPoint.x || lastTrianglePoint.x < lineSegmentEndPoint.x) {
                    Log.e(LOG_TAG, "Triangle values out of bounds!");
                    return;
                }
                break;

            case SideBottom:
                // hint: we draw the left line from bottom to top, so starting points etc. are set accordingly
                // at the beginning we're always placed at the starting point of the current edge
                lineSegmentStartPoint = currentPoint;
                // subtract the effective width of the edge to get its endpoint
                lineSegmentEndPoint = new PointF(lineSegmentStartPoint.x - getElementEdgeWidth(elementSize),
                                                 lineSegmentStartPoint.y);
                // x-position of the first triangle point is tip + triangleWidth / 2; y-position is the same as the
                // baseline
                firstTrianglePoint = new PointF(mElementTriangleTipPosition + mElementTriangleWidth / 2,
                                                currentPoint.y);
                // the y-position of the tip is baseline + tip distance
                tipTrianglePoint = new PointF(mElementTriangleTipPosition,
                                              currentPoint.y + mElementTriangleTipDistance);
                // x-position of first triangle point is tip - triangleWidth / 2; y-position is the same as the
                // baseline
                lastTrianglePoint = new PointF(mElementTriangleTipPosition - mElementTriangleWidth / 2, currentPoint.y);

                // check bounds
                if (firstTrianglePoint.x > lineSegmentStartPoint.x || lastTrianglePoint.x < lineSegmentEndPoint.x) {
                    Log.e(LOG_TAG, "Triangle values out of bounds!");
                    return;
                }
                break;

            case SideLeft:
                // hint: we draw the left line from bottom to top, so starting points etc. are set accordingly
                // at the beginning we're always placed at the starting point of the current edge
                lineSegmentStartPoint = currentPoint;
                // subtract the effective height of the edge to get its endpoint
                lineSegmentEndPoint = new PointF(lineSegmentStartPoint.x,
                                                 lineSegmentStartPoint.y - getElementEdgeHeight(elementSize));
                // x-position of first triangle point is the same as the baseline; y-position is tip + triangleWidth/2
                firstTrianglePoint = new PointF(currentPoint.x,
                                                mElementTriangleTipPosition + mElementTriangleWidth / 2);
                // the x-position of the tip is baseline - tip distance
                tipTrianglePoint = new PointF(currentPoint.x - mElementTriangleTipDistance,
                                              mElementTriangleTipPosition);
                // x-position of first triangle point is the same as the baseline; y-position is tip - triangleWidth /2
                lastTrianglePoint = new PointF(currentPoint.x,
                                               mElementTriangleTipPosition - mElementTriangleWidth / 2);

                // check bounds
                if (firstTrianglePoint.y > lineSegmentStartPoint.y || lastTrianglePoint.y < lineSegmentEndPoint.y) {
                    Log.e(LOG_TAG, "Triangle values out of bounds!");
                    return;
                }
                break;

            default:
                firstTrianglePoint = new PointF(0.0f, 0.0f);
                tipTrianglePoint = new PointF(0.0f, 0.0f);
                lastTrianglePoint = new PointF(0.0f, 0.0f);
                break;
        }

        // draw the triangle
        path.lineTo(firstTrianglePoint.x, firstTrianglePoint.y);
        path.lineTo(tipTrianglePoint.x, tipTrianglePoint.y);
        path.lineTo(lastTrianglePoint.x, lastTrianglePoint.y);
    }


    /**
     * @brief Creates and delivers the path that should be drawn.
     *
     * @param elementSize The size of the whole element.
     * @return the shape of the element as a drawing path.
     */
    private Path createDrawingPath(Point elementSize) {
        Path path;
        PointF destination, currentPoint;

        // init
        path = new Path();

        // move to start point, which is the left end of top-line
        destination = getElementDrawingStartPoint();
        path.moveTo(destination.x, destination.y);
        currentPoint = new PointF(destination.x, destination.y);

        // calculate right end of top-line
        destination = new PointF(currentPoint.x + getElementEdgeWidth(elementSize), currentPoint.y);
        // if triangle on top-side add it in
        if (mElementTriangleSide ==
            TriangleSide.SideTop) {
            drawTriangle(path, elementSize, currentPoint);
        }
        // draw line to right end of top-line
        path.lineTo(destination.x, destination.y);
        // draw right upper corner
        path.arcTo(new RectF(destination.x - mElementCornerRadius, destination.y,
                             destination.x + mElementCornerRadius, destination.y + 2 * mElementCornerRadius),
                   270.0f, 90.0f);
        // calculate new currentPoint
        currentPoint = new PointF(destination.x + mElementCornerRadius, destination.y + mElementCornerRadius);

        // now we're at the upper end of right-line, so calculate lower end of right-line
        destination = new PointF(currentPoint.x, currentPoint.y + getElementEdgeHeight(elementSize));
        // if triangle on right side, add it in
        if (mElementTriangleSide ==
            TriangleSide.SideRight) {
            drawTriangle(path, elementSize, currentPoint);
        }
        // draw line to lower end of right-line
        path.lineTo(destination.x, destination.y);
        // draw right-lower corner
        path.arcTo(new RectF(destination.x - 2 * mElementCornerRadius, destination.y - mElementCornerRadius,
                             destination.x, destination.y + mElementCornerRadius),
                   0.0f, 90.0f);
        // calculate new currentPoint
        currentPoint = new PointF(destination.x - mElementCornerRadius, destination.y + mElementCornerRadius);

        // now we're at the right end of the bottom-line, so calculate left end of bottom-line
        destination = new PointF(currentPoint.x - getElementEdgeWidth(elementSize), currentPoint.y);
        // if triangle is on bottom side, add it in
        if (mElementTriangleSide ==
            TriangleSide.SideBottom) {
            drawTriangle(path, elementSize, currentPoint);
        }
        // draw line to left end of bottom line
        path.lineTo(destination.x, destination.y);
        // draw left-lower corner
        path.arcTo(new RectF(destination.x - mElementCornerRadius, destination.y - 2 * mElementCornerRadius,
                             destination.x + mElementCornerRadius, destination.y),
                   90.0f, 90.0f);
        // calculate new currentPoint
        currentPoint = new PointF(destination.x - mElementCornerRadius, destination.y - mElementCornerRadius);

        // now we're at the lower end of the left-line, so calculate upper end of left-line
        destination = new PointF(currentPoint.x, currentPoint.y - getElementEdgeHeight(elementSize));
        // if triangle on left side, add it in
        if (mElementTriangleSide ==
            TriangleSide.SideLeft) {
            drawTriangle(path, elementSize, currentPoint);
        }
        // draw line to upper end of left-line
        path.lineTo(destination.x, destination.y);
        // draw left-upper corner
        path.arcTo(new RectF(destination.x, destination.y - mElementCornerRadius,
                             destination.x + 2 * mElementCornerRadius, destination.y + mElementCornerRadius),
                   180.0f, 90.0f);

        // path is finished now
        path.close();

        // done
        return path;
    }




//---------------------------------------------------------------------------------------------------------------------
// ----- Addaption of space requirements ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Adapt wanted size values to the given space.
     *
     * Most values that concern size/layout are considered as "wanted values". They're applied as given, if the given
     * space for the notification element is big enough. If there's not enough space to fulfill all wishes, we have to
     * adapt the wanted values meaningful.
     *
     * @param elementSize The size of the whole element.
     */
    private void measureTriangleValues(Point elementSize) {
        calculateTriangleWidth(elementSize);
        calculateTriangleTipDistance(elementSize);
        calculateCornerRadius(elementSize);
        calculateTriangleTipPosition(elementSize);
    }


    /**
     * @brief Adapt wanted position of the triangle tip to real a valid position.
     *
     * The styleguide defines the area in which the triangle is allowed to be drawn.
     * The triangle can't be drawn in the area of the rounded corners, so it can only be drawn
     * along the straight edges. But after each corner there there's also a margin (of 1 BU)
     * defined. So we take care that the wanted position is shifted accordingly so that it won't
     * be drawn in the forbidden areas.
     *
     * @param elementSize The size of the whole element.
     */
    private void calculateTriangleTipPosition(Point elementSize) {
        float position;
        float edgeFirstPos, edgeLastPos, distance;

        // copy value, so we can change it
        position = mElementWantedTriangleTipPosition;

        // check if we got a relative or an absolute value
        if (mElementTriangleWantedTipPositionIsRelative) {
            // security
            if (position < 0) {
                position = 0;
            } else if (position > 1) {
                position = 1;
            }

            // make relative to absolute value
            if (mElementTriangleSide ==
                TriangleSide.SideTop ||
                mElementTriangleSide ==
                TriangleSide.SideBottom) {
                position *= elementSize.x;
            } else {
                position *= elementSize.y;
            }
        }

//        // round
//        position = PDEBuildingUnits.roundToScreenCoordinates(position);

        // -- limit to valid bounds
        // the triangle can only be drawn along the straight edges, not in the area of the rounded corners
        // so we have to take care, that the tip position is set accordingly.

        // the following code depends on which side the triangle is drawn
        // so on top/bottom side 'position' is an x-value and 'edgeFirstPos' is the left end of the horizontal edge
        // on left/right side 'position' is an y-value and 'edgeFirstPos' is the upper end of the vertical edge
        edgeFirstPos = mElementCornerRadius;
        // depending on the drawing side we have to add width or height to get the right/lower end of the edge
        if (mElementTriangleSide ==
            TriangleSide.SideTop ||
            mElementTriangleSide ==
            TriangleSide.SideBottom) {
            edgeLastPos = edgeFirstPos + getElementEdgeWidth(elementSize);
        } else {
            edgeLastPos = edgeFirstPos + getElementEdgeHeight(elementSize);
        }

        // check left / upper bounds
        if ((distance = (edgeFirstPos + mElementTriangleMargin) - getTriangleFirstPosition(position)) > 0) {
            // start triangle at left / upper bound of the edge
            position += distance;
        }

        // check right / lower bounds
        if ((distance = getTriangleLastPosition(position) - (edgeLastPos - mElementTriangleMargin)) > 0) {
            // start triangle at right / lower bound of the edge
            position -= distance;
        }

        // any change?
        if (position != mElementTriangleTipPosition) {
            // remember
            mElementTriangleTipPosition = position;
        }
    }


    /**
     * @brief Adapt wanted tip distance to valid value.
     *
     * If there is quite little space available for notification element, we reduce the distance of the tip in order
     * to save more space for the content area.
     *
     * @param elementSize The size of the whole element.
     */
    private void calculateTriangleTipDistance(Point elementSize) {
        int spaceForTriangle;

        if (mElementTriangleSide ==
            TriangleSide.SideTop ||
            mElementTriangleSide ==
            TriangleSide.SideBottom) {
            // calculate space that is left for the triangle
            spaceForTriangle = Math.round(elementSize.y - (2 * mElementCornerRadius + 2 * mElementTriangleMargin));

            // is the wanted tip distance larger than the available space?
            if (mElementWantedTriangleTipDistance > spaceForTriangle) {
                if (spaceForTriangle > 0) {
                    mElementTriangleTipDistance = spaceForTriangle;
                } else {
                    mElementTriangleTipDistance = 0;
                }
            } else {
                mElementTriangleTipDistance = mElementWantedTriangleTipDistance;
            }
        } else {
            // calculate space that is left for the triangle
            spaceForTriangle = Math.round(elementSize.x - (2 * mElementCornerRadius + 2 * mElementTriangleMargin));

            // is the wanted tip distance larger than the available space?
            if (mElementWantedTriangleTipDistance > spaceForTriangle) {
                if (spaceForTriangle > 0) {
                    mElementTriangleTipDistance = spaceForTriangle;
                } else {
                    mElementTriangleTipDistance = 0;
                }
            } else {
                mElementTriangleTipDistance = mElementWantedTriangleTipDistance;
            }
        }
    }


    /**
     * @brief Adapt wanted triangle width to valid value.
     *
     * If there is less valid drawing space left than the wanted triangle width, we have to reduce the value.
     *
     * @param elementSize The size of the whole element.
     */
    private void calculateTriangleWidth(Point elementSize) {
        int allowedTriangleDrawingArea;

        // limit triangle width to meaningful value if necessary
        if (mElementTriangleSide ==
            TriangleSide.SideTop ||
            mElementTriangleSide ==
            TriangleSide.SideBottom) {
            // is wanted width bigger than the allowed drawing area?
            if (mElementWantedTriangleWidth >
                (allowedTriangleDrawingArea = Math.round(getElementEdgeWidth(elementSize) -
                                                         2 * mElementTriangleMargin))) {
                if (allowedTriangleDrawingArea > 0) {
                    mElementTriangleWidth = allowedTriangleDrawingArea;
                } else {
                    mElementTriangleWidth = 0;
                }
            } else {
                mElementTriangleWidth = mElementWantedTriangleWidth;
            }
        } else {
            // is wanted width bigger than the allowed drawing area?
            if (mElementWantedTriangleWidth >
                (allowedTriangleDrawingArea = Math.round(getElementEdgeHeight(elementSize) -
                                                         2 * mElementTriangleMargin))) {
                if (allowedTriangleDrawingArea > 0) {
                    mElementTriangleWidth = allowedTriangleDrawingArea;
                } else {
                    mElementTriangleWidth = 0;
                }
            } else {
                mElementTriangleWidth = mElementWantedTriangleWidth;
            }
        }
    }


    /**
     * @brief Adapt wanted corner radius to valid values.
     *
     * If there's not enough space left to draw two opposing corners next to each other we have to reduce
     * the radius of the corners to be able to draw them in the given space.
     *
     * @param elementSize The size of the whole element.
     */
    private void calculateCornerRadius(Point elementSize) {
        float widthDiff, heightDiff, radius;

        // init
        widthDiff = 0;
        heightDiff = 0;

        // check if elementRect is big enough for wanted corner radius
        if (elementSize.x < 2 * mElementWantedCornerRadius) {
            widthDiff = mElementWantedCornerRadius - elementSize.x / 2;
        }

        if (elementSize.y < 2 * mElementWantedCornerRadius) {
            heightDiff = mElementWantedCornerRadius - elementSize.y / 2;
        }

        if (widthDiff > heightDiff) {
            radius = mElementWantedCornerRadius - widthDiff;
        } else {
            radius = mElementWantedCornerRadius - heightDiff;
        }

        if (radius > 0) {
            mElementCornerRadius = radius;
        } else {
            mElementCornerRadius = 0;
        }
    }



    /**
     * @brief Update all of my sublayers.
     */
    @Override
    protected void doLayout() {
        Point elementSize;
        Path path;

        // get the rect we're using for layouting  (we draw from 0 to bounds - 1)
        elementSize = new Point(getBounds().width()-1, getBounds().height()-1);

        // adapt space requirements (wanted values ) to given space
        measureTriangleValues(elementSize);

        // create the drawing path
        path = createDrawingPath(elementSize);

        // update main layer
        updateElement(path);

        // update shadow drawable
        updateElementShadowDrawable(elementSize);
    }


    /**
     * @brief Update this layer to the given drawing path.
     *
     * @param path The path that should be drawn.
     */
    private void updateElement(Path path) {
        // remember
        mElementPath = path;
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- Helpers ------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief update background and border paint values
     */
    @Override
    protected void updateAllPaints() {
        createBackgroundPaint();
        createBorderPaint();
    }


    /**
     * @brief create background paint for drawing
     */
    private void createBackgroundPaint() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColorFilter(mColorFilter);
        mBackgroundPaint.setDither(mDither);
        mBackgroundPaint.setColor(mElementBackgroundColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }


    /**
     * @brief create border paint for drawing
     */
    private void createBorderPaint() {
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mElementBorderWidth);
        mBorderPaint.setColorFilter(mColorFilter);
        mBorderPaint.setDither(mDither);
        mBorderPaint.setColor(mElementBorderColor.newIntegerColorWithCombinedAlpha(mAlpha));
    }



//---------------------------------------------------------------------------------------------------------------------
// ----- Drawing Bitmap ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------


    /**
     * @brief Updates our drawing bitmap and triggers a redraw of this element.
     *
     * If a drawing parameter changes, we need to call this function in order to update our drawing-bitmap and
     * in order to trigger the draw of our updated bitmap to the canvas.
     */
    protected void updateDrawingBitmap(Canvas c, Rect bounds) {
        // security
        if (bounds.width()<=0 || bounds.height() <= 0 || mDrawingBitmap == null) return;
        c.drawPath(mElementPath, mBackgroundPaint);
        c.drawPath(mElementPath, mBorderPaint);
    }

}