package wireframe;

import util.matrix.Matrix;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    public double objectsPosition = 5;
    List<SceneObject> objects = new ArrayList<>();
    List<ViewPort> viewPorts = new ArrayList<>();

    public Scene(double objectsPositionModifier) {
        if(objectsPositionModifier > 5)
            this.objectsPosition = objectsPositionModifier;
        else{
            this.objectsPosition = 5 + objectsPositionModifier/2;
        }
    }

    public void addObjectTo(SceneObject object, double x, double y, double z){
        double[] translateMatrixValues = {
                1, 0, 0, x,
                0, 1, 0, y,
                0, 0, 1, z,
                0, 0, 0, 1
        };
        Matrix translateMatrix = new Matrix(4,4,translateMatrixValues);
        object.translate(translateMatrix);
        objects.add(object);
    }
    public void registerViewPort(ViewPort viewPort){
        viewPort.setOwnerScene(this);
        viewPorts.add(viewPort);
    }
    public void rotateAllObjectsX(double angle){
        angle = Math.toRadians(angle);
        double[] xRotationMatrixValues = {
                1, 0, 0, 0,
                0, Math.cos(angle), -1*Math.sin(angle), 0,
                0, Math.sin(angle), Math.cos(angle), 0,
                0, 0, 0, 1
        };
        Matrix xRotationMatrix = new Matrix(4, 4, xRotationMatrixValues);
        translateAllObjectsIntoMiddle();
        for (SceneObject object:objects
             ) {
            object.rotate(xRotationMatrix);
        }
        translateAllObjectsBackToPosition();
    }
    public void rotateAllObjectsY(double angle){
        angle = Math.toRadians(angle);
        double[] yRotationMatrixValues = {
                Math.cos(angle), 0, Math.sin(angle), 0,
                0, 1, 0, 0,
                -1*Math.sin(angle), 0, Math.cos(angle), 0,
                0, 0, 0, 1
        };
        Matrix yRotationMatrix = new Matrix(4, 4, yRotationMatrixValues);
        translateAllObjectsIntoMiddle();
        for (SceneObject object:objects
        ) {
            object.rotate(yRotationMatrix);
        }
        translateAllObjectsBackToPosition();
    }
    public void translateAllObjectsIntoMiddle(){
        double[] zTranslationMatrix = {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, -1*objectsPosition,
                0, 0, 0, 1
        };
        Matrix translateMatrix = new Matrix(4, 4, zTranslationMatrix);
        for (SceneObject object:objects
        ) {
            object.rotate(translateMatrix);
        }
    }
    public void translateAllObjectsBackToPosition(){
        double[] zTranslationMatrix = {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, objectsPosition,
                0, 0, 0, 1
        };
        Matrix translateMatrix = new Matrix(4, 4, zTranslationMatrix);
        for (SceneObject object:objects
        ) {
            object.rotate(translateMatrix);
        }
    }
}
