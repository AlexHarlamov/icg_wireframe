package util.imports;

import bspline.BSplineSettings;
import util.DPoint;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Open {
    public static BSplineSettings open(File file){
        try {
            Scanner scanner = new Scanner(file);
            if(scanner.hasNextLine()){
                int N = Integer.parseInt(scanner.nextLine());
                if(scanner.hasNextLine()){
                    Color splineColor = new Color(Integer.parseInt(scanner.nextLine()));
                    if (scanner.hasNextLine()){
                        Color formingLineColor = new Color(Integer.parseInt(scanner.nextLine()));;
                        if(scanner.hasNextLine()){
                            int size = Integer.parseInt(scanner.nextLine());
                            BSplineSettings bSplineSettings = new BSplineSettings(N);
                            bSplineSettings.setSplineColor(splineColor);
                            bSplineSettings.setFormingLineColor(formingLineColor);
                            for (int i = 0; i < size; i++) {
                                if(scanner.hasNextLine()){
                                    String[] values = scanner.nextLine().split(",");
                                    if(values.length < 2){
                                        Open.showFileFormatError("points data corrupted");
                                        return null;
                                    }else{
                                        double x = Double.parseDouble(values[0]);
                                        double y = Double.parseDouble(values[1]);
                                        DPoint point = new DPoint(x,y);
                                        bSplineSettings.addPoint(point);
                                    }
                                }else{
                                    Open.showFileFormatError("points data corrupted");
                                    return null;
                                }
                            }
                            return bSplineSettings;
                        }else{
                            Open.showFileFormatError("no forming line color");
                        }
                    }else{
                        Open.showFileFormatError("no spline color");
                    }
                }else{
                    Open.showFileFormatError("save file is too short");
                }
            }else{
                Open.showFileFormatError("sva file is empty");
            }

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(new JFrame(), "File not opened : "+e.getMessage(), "IO error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e){
            showFileFormatError("File format error");
        }
        return null;
    }

    public static void showFileFormatError(String message){
        JOptionPane.showMessageDialog(new JFrame(), "Input file format error: "+message, "IO error",
                JOptionPane.ERROR_MESSAGE);
    }
}
