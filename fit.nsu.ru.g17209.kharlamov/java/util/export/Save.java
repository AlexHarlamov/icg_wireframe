package util.export;

import bspline.BSplineSettings;
import util.DPoint;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Save {
    public static void save(File file, BSplineSettings splineSettings){
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(""+splineSettings.N);
            fileWriter.write("\n"+splineSettings.splineColor.getRGB());
            fileWriter.write("\n"+splineSettings.formingLineColor.getRGB());
            fileWriter.write("\n"+splineSettings.points.size());
            for (DPoint point:splineSettings.points
                 ) {
                fileWriter.write("\n"+point.x+","+point.y);
            }
            fileWriter.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(new JFrame(), "File not saved : "+e.getMessage(), "IO error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
