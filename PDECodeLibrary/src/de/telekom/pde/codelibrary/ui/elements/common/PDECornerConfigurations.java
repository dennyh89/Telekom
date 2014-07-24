/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2012. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.elements.common;

//----------------------------------------------------------------------------------------------------------------------
//  PDECornerConfigurations
//----------------------------------------------------------------------------------------------------------------------


import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * @brief Collection of all possible corner configurations for buttons and rounded boxes
 * and method to draw a path based on these
 */

public class PDECornerConfigurations {
    //corner configurations
    public static final int PDECornerConfigurationTopLeft = 1;
    public static final int PDECornerConfigurationTopRight = 1 << 1;
    public static final int PDECornerConfigurationBottomLeft = 1 << 2;
    public static final int PDECornerConfigurationBottomRight = 1 << 3;
    public static final int PDECornerConfigurationAllCorners = ~0;
    @SuppressWarnings("unused")
    public static final int PDECornerConfigurationNoCorners = 0;



    /**
     * @brief Creates and delivers the path that should be drawn.
     *
     * @param cornerConfiguration The rounded corner configuration.
     * @param elementSize The size of the whole element.
     * @param cornerRadius The corner radius.
     * @param startPoint The start point.
     * @return the shape of the element as a drawing path.
     */
    public static Path createDrawingPath(int cornerConfiguration, Point elementSize, float cornerRadius, PointF startPoint) {
        Path path;
        PointF destination, currentPoint;

        // init
        path = new Path();

        if ((cornerConfiguration & PDECornerConfigurations.PDECornerConfigurationTopLeft) != 0 ) {
            // TopLeftCorner
            startPoint =  new PointF(startPoint.x + cornerRadius,  startPoint.y);
        }


        // move to start point, which is the left end of top-line
        path.moveTo(startPoint.x,startPoint.y);
        if ((cornerConfiguration & PDECornerConfigurations.PDECornerConfigurationTopRight) == 0) {
            if ((cornerConfiguration & PDECornerConfigurations.PDECornerConfigurationTopLeft) == 0) {
                path.lineTo(startPoint.x+elementSize.x ,startPoint.y);
                currentPoint = new PointF(startPoint.x+elementSize.x ,startPoint.y);
            } else {
                path.lineTo(startPoint.x+elementSize.x - cornerRadius,startPoint.y);
                currentPoint = new PointF(startPoint.x+elementSize.x - cornerRadius,startPoint.y);
            }
            // calculate right end of top-line
            destination = new PointF(currentPoint.x,currentPoint.y + elementSize.y);
        } else {
            if ((cornerConfiguration & PDECornerConfigurations.PDECornerConfigurationTopLeft) == 0) {
                path.lineTo(startPoint.x+elementSize.x - cornerRadius,startPoint.y);
                currentPoint = new PointF(startPoint.x+elementSize.x - cornerRadius,startPoint.y);
            } else {
                path.lineTo(startPoint.x+elementSize.x - 2 * cornerRadius,startPoint.y);
                currentPoint = new PointF(startPoint.x+elementSize.x - 2 * cornerRadius,startPoint.y);
            }
            // draw left upper corner
            path.arcTo(new RectF(currentPoint.x - cornerRadius, currentPoint.y,
                    currentPoint.x + cornerRadius, currentPoint.y + 2* cornerRadius),
                    270.0f, 90.0f);
            currentPoint = new PointF(currentPoint.x+cornerRadius,currentPoint.y+cornerRadius);
            destination = new PointF(currentPoint.x,currentPoint.y + elementSize.y-cornerRadius);
        }

        if ((cornerConfiguration & PDECornerConfigurations.PDECornerConfigurationBottomRight) == 0) {
            path.lineTo(destination.x,destination.y );
            currentPoint = new PointF(destination.x,destination.y);
            destination = new PointF(currentPoint.x - elementSize.x  ,currentPoint.y );
        } else {
            path.lineTo(destination.x,destination.y -cornerRadius);
            currentPoint = new PointF(destination.x,destination.y -cornerRadius);
            // draw right upper corner
            path.arcTo(new RectF(currentPoint.x - 2* cornerRadius, currentPoint.y -cornerRadius,
                    currentPoint.x , currentPoint.y + cornerRadius),
                    0.0f, 90.0f);
            currentPoint = new PointF(currentPoint.x - cornerRadius,currentPoint.y + cornerRadius);
            destination = new PointF(currentPoint.x - elementSize.x + cornerRadius,currentPoint.y );
        }

        if ((cornerConfiguration & PDECornerConfigurations.PDECornerConfigurationBottomLeft) == 0) {
            path.lineTo(destination.x ,destination.y);
            currentPoint = new PointF(destination.x ,destination.y);
            destination = new PointF(currentPoint.x  ,currentPoint.y - elementSize.y +cornerRadius);
        } else {
            path.lineTo(destination.x + cornerRadius,destination.y );
            currentPoint = new PointF(destination.x + cornerRadius,destination.y );
            // draw right-lower corner
            path.arcTo(new RectF(currentPoint.x - cornerRadius, currentPoint.y - 2* cornerRadius,
                    currentPoint.x + cornerRadius, currentPoint.y ),
                    90.0f, 90.0f);
            currentPoint = new PointF(currentPoint.x - cornerRadius,currentPoint.y - cornerRadius);
            destination = new PointF(currentPoint.x ,currentPoint.y - elementSize.y + 2* cornerRadius);
        }

        if(( cornerConfiguration & PDECornerConfigurations.PDECornerConfigurationTopLeft) == 0)
        {
            path.lineTo(destination.x ,destination.y);


        } else {
            path.lineTo(destination.x,destination.y + cornerRadius);
            // draw left-lower corner
            path.arcTo(new RectF(destination.x , destination.y - cornerRadius,
                    destination.x + 2* cornerRadius, destination.y +  cornerRadius),
                    180.0f, 90.0f);
        }

        path.close();
        return path;
    }

}
