package wireframe;

import util.DPoint;
import util.matrix.Matrix;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ViewPort {
    public boolean isPointHighlighted = false;
    Scene ownerScene;
    double d;
    int resultImageHeight = 600;
    int resultImageWidth = 600;

    boolean isProportionsSet = false;

    double scaleProportionX;
    double scaleProportionY;

    public ViewPort(double d) {
        this.d = d;
    }

    public void setOwnerScene(Scene ownerScene) {
        this.ownerScene = ownerScene;
    }

    public BufferedImage getViewPortImage(){
        List<ProjectionObject> objects =  projectSceneObjects();
        return render(objects);
    }

    private List<ProjectionObject> projectSceneObjects(){
        double[] projectionMatrixValues = {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 1/d, 0
        };
        Matrix projectionMatrix = new Matrix(4,4,projectionMatrixValues);
        List<ProjectionObject> projectedObjects = new ArrayList<>();
        for (SceneObject sceneObject:ownerScene.objects
             ) {
            projectedObjects.add(projectSceneObject(projectionMatrix, sceneObject));
        }
        return projectedObjects;
    }

    private ProjectionObject projectSceneObject(Matrix projectionMatrix, SceneObject sceneObject){
        List<DPoint> projectedPoints = new ArrayList<>();
        for (ScenePoint scenePoint:sceneObject.getScenePoints()
             ) {
            double[] coordsVector = {
                    scenePoint.x,
                    scenePoint.y,
                    scenePoint.z,
                    scenePoint.w
            };
            Matrix pointVector = new Matrix(4,1,coordsVector);
            Matrix resultVector = projectionMatrix.multiMatrix(pointVector);
            double Wp = resultVector.values[3];
            resultVector = resultVector.multiScalar(1/Wp);
            projectedPoints.add(new DPoint(resultVector.values[0],resultVector.values[1]));
        }
        return new ProjectionObject(projectedPoints);
    }

    private BufferedImage render(List<ProjectionObject> projectedObjects){
        if(!isProportionsSet){
            analyzeProportions(projectedObjects);
        }
        BufferedImage resultImage = new BufferedImage(resultImageWidth,resultImageHeight,BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = (Graphics2D) resultImage.getGraphics();
        graphics2D.setColor(Color.BLACK);
        for (ProjectionObject object:projectedObjects
             ) {
            renderObject(graphics2D, object);
        }
        return resultImage;
    }

    private void renderObject(Graphics2D graphics2D, ProjectionObject object){
        List<DPoint> points = object.points;
        DPoint prev = points.get(0);
        for (DPoint cur:points
             ) {
            if(!prev.equals(cur)){
                drawLine(prev,cur,graphics2D);
                prev = cur;
            }
        }
    }

    private void drawLine(DPoint prev, DPoint cur, Graphics2D graphics2D){
        Point convertedPointCur = convertCoords(cur);
        Point convertedPointPrev = convertCoords(prev);
        Shape circle = new Ellipse2D.Double(convertedPointCur.x-3,convertedPointCur.y-3,6,6);
        if(isPointHighlighted){
            graphics2D.setColor(Color.RED);
            graphics2D.fill(circle);
            graphics2D.setColor(Color.BLACK);
        }
        graphics2D.drawLine(
                convertedPointCur.x,
                convertedPointCur.y,
                convertedPointPrev.x,
                convertedPointPrev.y
        );
    }

    private Point convertCoords(DPoint dPoint){
        return new Point(resultImageWidth/2 + (int)(dPoint.x*scaleProportionX),resultImageHeight/2 + (int)(dPoint.y*scaleProportionY));
    }

    private void analyzeProportions(List<ProjectionObject> objects){
        double minX = 0;
        double minY = 0;
        double maxX = 0;
        double maxY = 0;
        for (ProjectionObject object:objects
             ) {
            for (DPoint point:object.points
                 ) {
                if(point.x < minX){
                    minX = point.x;
                }else{
                    if(point.x > maxX){
                        maxX = point.x;
                    }
                }
                if(point.y < minY){
                    minY = point.y;
                }else{
                    if(point.y > maxY){
                        maxY = point.y;
                    }
                }
            }
        }
        double deltaX = Math.abs(maxX - minX);
        double deltaY = Math.abs(maxY - minY);
        double scaleDelta = Math.max(deltaX,deltaY);
        scaleProportionX = (resultImageWidth-100)/scaleDelta;
        scaleProportionY = -1*(resultImageHeight-100)/scaleDelta;
        isProportionsSet = true;
    }
}
