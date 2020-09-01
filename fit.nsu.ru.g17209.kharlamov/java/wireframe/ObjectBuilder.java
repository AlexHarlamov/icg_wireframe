package wireframe;

import util.DPoint;
import util.matrix.Matrix;

import java.util.ArrayList;
import java.util.List;

public class ObjectBuilder {

    public static SceneObject convertSplineToSceneObject(List<DPoint> objectPoints2D, double angle){
        angle = Math.toRadians(angle);
        double[] rotationMatrixValues = {
                Math.cos(angle), -1*Math.sin(angle),
                Math.sin(angle), Math.cos(angle)
        };
        Matrix rotationMatrix = new Matrix(2,2, rotationMatrixValues);
        List<ScenePoint> objectPoints = new ArrayList<>();
        for (DPoint point2D:objectPoints2D
        ) {
            objectPoints.add(rotatePoint(point2D, rotationMatrix));
        }

        return new SceneObject(objectPoints);
    }

    public static SceneObject createEdge(DPoint seed, double sectorAngle, int smoothLevel){
        List<ScenePoint> objectPoints = new ArrayList<>();
        double newSectorAngle = sectorAngle/smoothLevel;
        for (int i = 0; i <= (int)(360/newSectorAngle) ; i++) {
            double rotAngle = newSectorAngle*i;
            rotAngle = Math.toRadians(rotAngle);
            double[] rotationMatrixValues = {
                    Math.cos(rotAngle), -1*Math.sin(rotAngle),
                    Math.sin(rotAngle), Math.cos(rotAngle)
            };
            Matrix rotationMatrix = new Matrix(2,2, rotationMatrixValues);
            objectPoints.add(rotatePoint(seed, rotationMatrix));
        }
        return new SceneObject(objectPoints);
    }

    private static ScenePoint rotatePoint(DPoint point2D, Matrix rotationMatrix){
        double z = 0;
        double[] coords = {point2D.x,z};
        Matrix coordsVector = new Matrix(2,1,coords);
        Matrix resultVector = rotationMatrix.multiMatrix(coordsVector);
        return new ScenePoint(resultVector.values[0], point2D.y, resultVector.values[1]);
    }
}
