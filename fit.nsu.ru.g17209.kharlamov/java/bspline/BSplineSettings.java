package bspline;

import util.DPoint;
import util.export.Save;
import util.imports.Open;
import util.matrix.Matrix;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BSplineSettings {
    public Matrix M;

    public List<DPoint> points = new ArrayList<>();

    public int N;

    public double vMax = 5;
    public double uMax = 5;

    public Color splineColor = Color.RED;

    public Color formingLineColor = Color.BLUE;

    public BSplineSettings(int n){
        double[] mValues = {
                -1, 3,-3, 1,
                 3,-6, 3, 0,
                -3, 0, 3, 0,
                 1, 4, 1, 0
        };
        M = new Matrix(4,4, mValues);
        M = M.multiScalar(1.0/6);
        N = n;
    }

    public void setN(int n){
        N = n;
    }

    public void setFormingLineColor(Color formingLineColor) {
        this.formingLineColor = formingLineColor;
    }

    public void setSplineColor(Color splineColor) {
        this.splineColor = splineColor;
    }

    public void addPoint(DPoint p){
        points.add(p);
    }
    public void removePoint(DPoint p){
        points.remove(p);
    }
    public void exportSettings(File file){
        Save.save(file,this);
    }
    public void importSettings(File file){
        BSplineSettings settings = Open.open(file);
        if( settings != null){
            points = settings.points;
            N = settings.N;
            splineColor = settings.splineColor;
            formingLineColor = settings.formingLineColor;
        }
    }
}
