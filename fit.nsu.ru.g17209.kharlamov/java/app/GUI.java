package app;

import bspline.BSplineBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GUI extends JFrame {

    BSplineBuilder bSplineBuilder;

    public GUI(){

        //Basic setup of the frame
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(GUIBasicConfigurator.W_MIN_WIDTH, GUIBasicConfigurator.W_MIN_HEIGHT));
        setResizable(true);
        setTitle("ICG WireFrame");
        setLayout(new BorderLayout());

        //Setting up menu bar
        JMenuBar menuBar = new JMenuBar();

        //Setting up application description
        ApplicationDescription description = new ApplicationDescription();
        JMenu applicationMenu = new JMenu("Application");
        applicationMenu.add(description.getMenuItem());
        menuBar.add(applicationMenu);

        setJMenuBar(menuBar);

        bSplineBuilder = new BSplineBuilder(this);
        add(bSplineBuilder.getMainPanel());

        setLocationRelativeTo(null);

        pack();
        setVisible(true);

        bSplineBuilder.constructResultArea();

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                bSplineBuilder.renderImage(null);
            }
        });
    }
}
