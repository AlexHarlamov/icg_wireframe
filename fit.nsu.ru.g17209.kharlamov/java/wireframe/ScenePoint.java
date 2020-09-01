package wireframe;

import util.matrix.Matrix;

public class ScenePoint {
    public double x;
    public double y;
    public double z;
    public double w;

    public ScenePoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1;
    }

    public Matrix getVector(){
        double[] vectorValues = { x, y, z, w };
        return new Matrix(4,1, vectorValues);
    }
}
