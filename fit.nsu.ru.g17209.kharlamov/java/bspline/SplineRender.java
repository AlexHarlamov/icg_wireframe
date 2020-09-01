package bspline;

import util.DPoint;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class SplineRender {

    double scaleCoordsU;
    double scaleCoordsV;

    int height;
    int width;

    public BufferedImage renderImage(double vMax, double uMax, List<DPoint> resultPoints, Color splineColor, List<DPoint> mainPoints, Color formingLinesColor, int height, int width, DPoint selected){

        BufferedImage result = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        scaleCoordsU = (double)(width/2)/ uMax;
        scaleCoordsV = (double)(height/2)/ vMax;
        this.height = height;
        this.width = width;
        Graphics2D graphics2D = (Graphics2D)result.getGraphics();

        graphics2D.setColor(Color.BLACK);
        graphics2D.drawLine(0,height/2,width-1,height/2);
        graphics2D.drawLine(width/2,0,width/2,height-1);
        int notchSize = 2;
        int n = (int)(width/(uMax /2));
        for (int i = 0; i < n; i++) {
            graphics2D.drawLine((int)(i*scaleCoordsU),height/2-notchSize,(int)(i*scaleCoordsU), height/2+notchSize);
        }
        n = (int)(height/(vMax /2));
        for (int i = 0; i < n; i++) {
            graphics2D.drawLine(width/2-notchSize,(int)(i*scaleCoordsV),width/2+notchSize, (int)(i*scaleCoordsV));
        }
        if(mainPoints.isEmpty())
            return result;
        graphics2D.setColor(formingLinesColor);
        DPoint prev = mainPoints.get(0);
        for (DPoint cur:mainPoints
        ) {
            Point a = prepareCoords(cur);
            Shape circle = new Ellipse2D.Double(a.x-8,a.y-8,16,16);
            if(cur.equals(selected)){
                graphics2D.fill(circle);
            }else{
                graphics2D.draw(circle);
            }
            if(!prev.equals(cur)){
                Point b = prepareCoords(prev);
                graphics2D.drawLine(
                        a.x,
                        a.y,
                        b.x,
                        b.y
                );
                prev = cur;
            }
        }

        if(resultPoints == null || resultPoints.isEmpty()){
            return result;
        }

        graphics2D.setColor(splineColor);
        prev = resultPoints.get(0);
        for (DPoint cur:resultPoints
             ) {
            if(!prev.equals(cur)){
                Point a = prepareCoords(prev);
                Point b = prepareCoords(cur);
                graphics2D.drawLine(
                        a.x,
                        a.y,
                        b.x,
                        b.y
                );
                prev = cur;
            }
        }
        return result;
    }

    private Point prepareCoords(DPoint p){
        return new Point((int)(p.x*scaleCoordsU)+width/2,(int)(p.y*scaleCoordsV)+height/2);
    }
}
