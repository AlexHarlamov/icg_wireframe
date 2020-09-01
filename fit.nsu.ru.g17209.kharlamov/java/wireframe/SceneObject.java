package wireframe;

import util.matrix.Matrix;

import java.util.List;

public class SceneObject {
    private List<ScenePoint> scenePoints;

    public SceneObject(List<ScenePoint> scenePoints) {
        this.scenePoints = scenePoints;
    }

    public List<ScenePoint> getScenePoints() {
        return scenePoints;
    }

    public void translate(Matrix translateMatrix){
        processMatrixOperationWithAllThePoints(translateMatrix);
    }
    public void rotate(Matrix rotationMatrix){
        processMatrixOperationWithAllThePoints(rotationMatrix);
    }
    public void scale(){

    }

    private void processMatrixOperationWithAllThePoints(Matrix matrix){
        for (ScenePoint point:scenePoints
        ) {
            Matrix result = matrix.multiMatrix(point.getVector());
            point.x = result.values[0];
            point.y = result.values[1];
            point.z = result.values[2];
            point.w = result.values[3];
        }
    }
}
