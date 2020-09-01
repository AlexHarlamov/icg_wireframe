package wireframe;

import util.DPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SceneBuilder {

    List<SceneObject> objects = new ArrayList<>();
    Scene scene;

    ViewPort mainPort;
    JPanel mainPanel;

    int height;
    int width;

    int smoothLevel;

    Point pressed;
    Point prev;

    int edgesNumber;
    int repeatsNumber;

    double objectPositionModifier;

    List<DPoint> splinePoints;

    public void displayScene(List<DPoint> splinePoints,int repeatsNumber, int edgesNumber, int smoothLevel, double objectPositionModifier){

        scene = new Scene(objectPositionModifier);

        this.objectPositionModifier = objectPositionModifier;
        this.smoothLevel = smoothLevel;
        this.repeatsNumber = repeatsNumber;
        this.edgesNumber = edgesNumber;
        this.splinePoints = splinePoints;

        buildScene(splinePoints, repeatsNumber, edgesNumber);
        mainPort = initViewPort();
        BufferedImage viewPortImage = mainPort.getViewPortImage();

        height = viewPortImage.getHeight();
        width = viewPortImage.getWidth();

        JFrame frame= new JFrame();
        frame.setLayout(new BorderLayout());
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JLabel(new ImageIcon(viewPortImage)));
        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pressed = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = null;
            }
        });
        mainPanel.addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            double value = notches*0.1;
            changeViewPortZoom(value);
        });
        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(prev != null){
                    processRotation(e.getPoint());
                }
                prev = e.getPoint();
            }
        });
        JPanel settingsPanel = new JPanel(new FlowLayout());
        JButton resetAnglesButton = new JButton("reset angles");
        resetAnglesButton.addActionListener(e -> resetAngles());
        JButton resetCamZoom = new JButton("reset zoom");
        resetCamZoom.addActionListener(e -> resetZoom());
        JButton showPoints = new JButton("Show points");
        showPoints.addActionListener(e -> {
            mainPort.isPointHighlighted = !mainPort.isPointHighlighted;
            updateViewArea();
        });
        settingsPanel.add(resetAnglesButton);
        settingsPanel.add(resetCamZoom);
        settingsPanel.add(showPoints);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(settingsPanel, BorderLayout.SOUTH);
        frame.setPreferredSize(new Dimension(viewPortImage.getWidth(),viewPortImage.getHeight()));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.pack();
    }

    private void rebuildScene(){
        buildScene(splinePoints, repeatsNumber, edgesNumber);
        mainPort = initViewPort();
        BufferedImage viewPortImage = mainPort.getViewPortImage();

        height = viewPortImage.getHeight();
        width = viewPortImage.getWidth();
        updateViewArea();
    }

    double xAngleChanges = 0;
    double yAngleChanges = 0;

    private void resetZoom(){
        mainPort.d = 1;
        updateViewArea();
    }

    private void resetAngles(){
        objects = new ArrayList<>();
        scene = new Scene(objectPositionModifier);
        rebuildScene();
    }

    private void changeViewPortZoom(double value){
        mainPort.d += value;
        updateViewArea();
    }

    private double findXDirection(Point a){
        return prev.x < a.x ? 1: -1;
    }
    private double findYDirection(Point a){
        return prev.y < a.y ? 1: -1;
    }

    private void processRotation(Point a){
        if(prev.x != a.x){
            yAngleChanges += 1*findXDirection(a);
            scene.rotateAllObjectsY(1*findXDirection(a));
        }
        if(prev.y != a.y){
            xAngleChanges += 1*findYDirection(a);
            scene.rotateAllObjectsX(1*findYDirection(a));
        }
        updateViewArea();
    }

    private void updateViewArea(){
        BufferedImage image = mainPort.getViewPortImage();
        mainPanel.removeAll();
        mainPanel.add(new JLabel(new ImageIcon(image)));
        mainPanel.setPreferredSize(mainPanel.getPreferredSize());
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void buildScene(List<DPoint> splinePoints,int repeatsNumber, int edgesNumber){
        objects = prepareObjects(splinePoints, repeatsNumber, edgesNumber);
        for (SceneObject object:objects
             ) {
            scene.addObjectTo(object, 0,0,scene.objectsPosition);
        }
    }
    private ViewPort initViewPort(){
        ViewPort viewPort = new ViewPort(1);
        scene.registerViewPort(viewPort);
        return viewPort;
    }
    private List<SceneObject> prepareObjects(List<DPoint> seed, int repeatsNumber, int edgesNumber){
        List<SceneObject> wiredObjectComponents = new ArrayList<>();
        double rotationAngle = 360.0/repeatsNumber;
        for (int i = 0; i < repeatsNumber; i++) {
            wiredObjectComponents.add(ObjectBuilder.convertSplineToSceneObject(seed, i*rotationAngle));
        }
        int realEdgesNumber = Math.min(seed.size(), edgesNumber);
        int skipEdges = seed.size()/realEdgesNumber;

        for (int i = 0; i < realEdgesNumber; i++) {
            wiredObjectComponents.add(ObjectBuilder.createEdge(seed.get(skipEdges*i),rotationAngle,smoothLevel));
        }

        return wiredObjectComponents;
    }
}
