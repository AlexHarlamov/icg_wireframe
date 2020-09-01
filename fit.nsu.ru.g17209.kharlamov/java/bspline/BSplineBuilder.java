package bspline;

import util.DPoint;
import util.matrix.Matrix;
import wireframe.SceneBuilder;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BSplineBuilder {

    int uMax = 5;
    int vMax = 5;

    int N = 10;
    int repeatNumber = 10;
    int edgesNumber = 10;
    int smoothLevel = 10;

    JPanel mainPanel = new JPanel();

    JPanel settingsPanel;
    JPanel resultArea;
    JPanel toolsPanel;

    JPanel settingsWrapper;

    BSplineSettings bSplineSettings;

    BufferedImage renderedImage;
    SplineRender splineRender = new SplineRender();

    JButton addPointButton = new JButton("add point");
    JButton saveSplineSettingsButton = new JButton("save");
    JButton loadSplineSettingsButton = new JButton("load");
    JButton updateSettingsButton = new JButton("apply changes");
    JButton renderWiredFrameButton = new JButton("render frame");

    List<DPoint> resultPoints = new ArrayList<>();

    DPoint currentEditedPoint;

    JFrame parent;

    public BSplineBuilder(JFrame parent){

        this.parent = parent;

        mainPanel.setLayout(new BorderLayout());

        bSplineSettings = new BSplineSettings(N);

        toolsPanel = new JPanel(new FlowLayout());
        addPointButton.addActionListener(e -> toggleDialog());
        saveSplineSettingsButton.addActionListener(e -> saveSettings());
        loadSplineSettingsButton.addActionListener(e -> loadSettings());
        updateSettingsButton.addActionListener(e -> updateSettings());
        renderWiredFrameButton.addActionListener(e -> showWiredFrame());
        toolsPanel.add(addPointButton);
        toolsPanel.add(saveSplineSettingsButton);
        toolsPanel.add(loadSplineSettingsButton);
        toolsPanel.add(updateSettingsButton);
        toolsPanel.add(renderWiredFrameButton);

        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        /*settingsPanel.setLayout(new SpringLayout());*/
        settingsPanel.setOpaque(true);
        settingsPanel.setBackground(Color.LIGHT_GRAY);
        constructSettingsPanel();

        resultArea = new JPanel();
        resultArea.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(currentEditedPoint != null){
                    DPoint point = convertCoords(e.getPoint());
                    currentEditedPoint.x = point.x;
                    currentEditedPoint.y = point.y;
                    constructSettingsPanel();
                    buildSplineWithSelected(currentEditedPoint);
                }
            }
        });
        resultArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                point.x = point.x-5;
                point.y = point.y-5;
                currentEditedPoint = findDPoint(point);
                buildSplineWithSelected(currentEditedPoint);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentEditedPoint = null;
                buildSpline();
            }
        });

        settingsWrapper = new JPanel(new BorderLayout());
        settingsWrapper.add(settingsPanel, BorderLayout.CENTER);
        settingsWrapper.add(toolsPanel, BorderLayout.SOUTH);

        mainPanel.add(resultArea, BorderLayout.CENTER);
        mainPanel.add(settingsWrapper, BorderLayout.SOUTH);
    }

    public void constructResultArea(){
        renderedImage = new BufferedImage(resultArea.getWidth(),resultArea.getHeight(), BufferedImage.TYPE_INT_ARGB);
        updateResultArea();
    }

    List<JTextField> textXFields = new ArrayList<>();
    List<JTextField> textYFields = new ArrayList<>();

    public void constructSettingsPanel(){
        settingsPanel.removeAll();
        textXFields = new ArrayList<>();
        textYFields = new ArrayList<>();
        int iterator = 0;
        JPanel separator = new JPanel(new FlowLayout());
        /*int onePanelWidth = 0;*/
        for (DPoint point:bSplineSettings.points
             ) {
            JLabel xLabel = new JLabel("X:");
            JLabel yLabel = new JLabel("Y:");
            JTextField xTextField = new JTextField(""+point.x);
            JTextField yTextField = new JTextField(""+point.y);
            xTextField.setColumns(5);
            yTextField.setColumns(5);
            textXFields.add(xTextField);
            textYFields.add(yTextField);
            JButton removePointButton = new JButton("X");
            removePointButton.addActionListener(e -> removePoint(point));

            JPanel panel = new JPanel(new FlowLayout());
            panel.add(xLabel);
            panel.add(xTextField);
            panel.add(yLabel);
            panel.add(yTextField);
            panel.add(removePointButton);
            if(iterator == 3){
                settingsPanel.add(separator);
                separator = new JPanel(new FlowLayout());
                iterator = 0;
            }else {
                separator.add(panel);
                iterator++;
            }
        }
        if(iterator != 0){
            settingsPanel.add(separator);
        }

        JPanel smoothSettingsPanel = new JPanel(new FlowLayout());
        smoothSettingsPanel.add(new JLabel("spline smooth(N):"));
        nTextField = new JTextField(""+N);
        nTextField.setColumns(5);
        smoothSettingsPanel.add(nTextField);
        smoothSettingsPanel.add(new JLabel("spline repeat number(M1):"));
        repeatNumberTextField = new JTextField(""+repeatNumber);
        repeatNumberTextField.setColumns(5);
        smoothSettingsPanel.add(repeatNumberTextField);
        smoothSettingsPanel.add(new JLabel("edge smooth level(M2):"));
        smoothLevelTextField = new JTextField(""+smoothLevel);
        smoothLevelTextField.setColumns(5);
        smoothSettingsPanel.add(smoothLevelTextField);
        smoothSettingsPanel.add(new JLabel("edges number:"));
        edgesNumberTextField = new JTextField(""+edgesNumber);
        edgesNumberTextField.setColumns(5);
        smoothSettingsPanel.add(edgesNumberTextField);
        settingsPanel.add(smoothSettingsPanel);

        JPanel coordsSettingsPanel = new JPanel(new FlowLayout());
        uMaxTextField = new JTextField(""+uMax);
        uMaxTextField.setColumns(5);
        coordsSettingsPanel.add(new JLabel("uMax:"));
        coordsSettingsPanel.add(uMaxTextField);
        vMaxTextField = new JTextField(""+vMax);
        vMaxTextField.setColumns(5);
        coordsSettingsPanel.add(new JLabel("vMax:"));
        coordsSettingsPanel.add(vMaxTextField);
        settingsPanel.add(coordsSettingsPanel);

        settingsPanel.revalidate();
        settingsPanel.repaint();
    }

    JTextField nTextField;
    JTextField repeatNumberTextField;
    JTextField smoothLevelTextField;
    JTextField edgesNumberTextField;

    JTextField uMaxTextField;
    JTextField vMaxTextField;

    public JPanel getMainPanel(){
        return mainPanel;
    }

    private void toggleDialog(){
        JDialog frame = new JDialog(parent,"Add point",true);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        JPanel panelX = new JPanel(new FlowLayout());
        JPanel panelY = new JPanel(new FlowLayout());
        JPanel panelAction = new JPanel(new FlowLayout());

        frame.setLocationRelativeTo(null);
        JTextField xTextField = new JTextField("0");
        JTextField yTextField = new JTextField("0");

        xTextField.setColumns(5);
        yTextField.setColumns(5);

        JLabel xLabel = new JLabel("X:");
        JLabel yLabel = new JLabel("Y:");

        JButton confirmButton = new JButton("Apply");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> {
            double x = Double.parseDouble(xTextField.getText());
            double y = Double.parseDouble(yTextField.getText());
            addPoint(x,y);
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        });

        cancelButton.addActionListener(e -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));

        panelX.add(xLabel);
        panelX.add(xTextField);
        panelY.add(yLabel);
        panelY.add(yTextField);
        panelAction.add(confirmButton);
        panelAction.add(cancelButton);

        panel.add(panelX);
        panel.add(panelY);
        panel.add(panelAction);

        panel.setMinimumSize(panel.getPreferredSize());

        frame.add(panel);

        frame.setMinimumSize(new Dimension(200,150));

        frame.setMinimumSize(new Dimension(160,90));
        frame.setPreferredSize(frame.getPreferredSize());

        frame.setVisible(true);
        frame.pack();
    }

    private void updateSettings(){
        N = Integer.parseInt(nTextField.getText());
        smoothLevel = Integer.parseInt(smoothLevelTextField.getText());
        edgesNumber = Integer.parseInt(edgesNumberTextField.getText());
        repeatNumber = Integer.parseInt(repeatNumberTextField.getText());
        uMax = Integer.parseInt(uMaxTextField.getText());
        vMax = Integer.parseInt(vMaxTextField.getText());
        bSplineSettings.uMax = uMax;
        bSplineSettings.vMax = vMax;
        bSplineSettings.N = N;
        List<DPoint> points = new ArrayList<>();
        int iterator = 0;
        for (JTextField xField:textXFields
             ) {
            JTextField yField = textYFields.get(iterator);
            points.add(new DPoint(Double.parseDouble(xField.getText()),Double.parseDouble(yField.getText())));
            iterator++;
        }
        bSplineSettings.points = points;
        constructSettingsPanel();
        buildSpline();
    }

    private void addPoint(double x, double y){
        bSplineSettings.addPoint(new DPoint(x,y));
        constructSettingsPanel();
        buildSpline();
    }

    private void removePoint(DPoint point){
        bSplineSettings.removePoint(point);
        constructSettingsPanel();
        buildSpline();
    }

    private void loadSettings(){
        JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int r = j.showSaveDialog(null);
        if (r == JFileChooser.APPROVE_OPTION){
            bSplineSettings.importSettings(j.getSelectedFile());
            N = bSplineSettings.N;
            constructSettingsPanel();
            buildSpline();
        }
    }

    private void saveSettings(){
        JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int r = j.showSaveDialog(null);
        if (r == JFileChooser.APPROVE_OPTION){
            bSplineSettings.exportSettings(j.getSelectedFile());
        }
    }

    public void renderImage(DPoint selected){
        renderedImage = splineRender.renderImage(
                bSplineSettings.vMax,
                bSplineSettings.uMax,
                resultPoints,
                bSplineSettings.splineColor,
                bSplineSettings.points,
                bSplineSettings.formingLineColor,
                resultArea.getHeight(),
                resultArea.getWidth(),
                selected
        );
        updateResultArea();
    }

    private void updateMainPanel(){
        mainPanel.revalidate();
    }
    private void updateResultArea(){
        resultArea.removeAll();
        resultArea.add(new JLabel(new ImageIcon(renderedImage)),BorderLayout.CENTER);
        resultArea.setPreferredSize(resultArea.getPreferredSize());
        resultArea.revalidate();
        updateMainPanel();
    }

    private void buildSpline(){
        findPoints();
        renderImage(null);
    }

    private void buildSplineWithSelected(DPoint selected){
        findPoints();
        renderImage(selected);
    }

    private void findPoints(){
        resultPoints = new ArrayList<>();
        if(bSplineSettings.points.size() >= 4){
            int sectorNumber = bSplineSettings.points.size() - 3;
            double simpleSectorInterval = 1.0/bSplineSettings.N;
            for (int j = 1; j <= sectorNumber; j++) {
                for (int i = 0; i < bSplineSettings.N; i++) {
                    double t = i*simpleSectorInterval;
                    double[] tData = { Math.pow(t,3),Math.pow(t,2),t,1};
                    Matrix T = new Matrix(1,4,tData);
                    Matrix Gx = buildGX(j);
                    Matrix Gy = buildGY(j);
                    Matrix Rx = T.multiMatrix(bSplineSettings.M.multiMatrix(Gx));
                    Matrix Ry = T.multiMatrix(bSplineSettings.M.multiMatrix(Gy));
                    double x = Rx.values[0];
                    double y = Ry.values[0];
                    resultPoints.add(new DPoint(x,y));
                }
            }

        }
    }

    private Matrix buildGX(int i){
        double[] values = new double[4];
        values[0] = bSplineSettings.points.get(i-1).x;
        values[1] = bSplineSettings.points.get(i).x;
        values[2] = bSplineSettings.points.get(i+1).x;
        values[3] = bSplineSettings.points.get(i+2).x;
        return new Matrix(4,1, values);
    }
    private Matrix buildGY(int i){
        double[] values = new double[4];
        values[0] = bSplineSettings.points.get(i-1).y;
        values[1] = bSplineSettings.points.get(i).y;
        values[2] = bSplineSettings.points.get(i+1).y;
        values[3] = bSplineSettings.points.get(i+2).y;
        return new Matrix(4,1, values);
    }

    private DPoint findDPoint(Point point){
        int pixelNullX = renderedImage.getWidth()/2;
        int pixelNullY = renderedImage.getHeight()/2;
        double scaleCoordsU = (double)(pixelNullX)/ bSplineSettings.uMax;
        double scaleCoordsV = (double)(pixelNullY)/ bSplineSettings.vMax;
        for (DPoint mainPoint:bSplineSettings.points
             ) {
            int x = (int) (mainPoint.x*scaleCoordsU)+pixelNullX;
            int y = (int) (mainPoint.y*scaleCoordsV)+pixelNullY;
            if(Math.abs(point.x-x) < 8){
                if(Math.abs(point.y-y) < 8){
                    return mainPoint;
                }
            }
        }
        return null;
    }

    private DPoint convertCoords(Point point){
        int pixelNullX = renderedImage.getWidth()/2;
        int pixelNullY = renderedImage.getHeight()/2;
        double scaleCoordsU = (double)(pixelNullX)/ bSplineSettings.uMax;
        double scaleCoordsV = (double)(pixelNullY)/ bSplineSettings.vMax;
        double x = (double) (point.x - pixelNullX)/scaleCoordsU;
        double y = (double) (point.y - pixelNullY)/scaleCoordsV;
        return new DPoint(x,y);
    }

    private List<DPoint> normalizePoints(){
        List<DPoint> normalizedPoints = new ArrayList<>();
        for (DPoint point:resultPoints
             ) {
            double newY = point.y;
            double newX = point.x;
            double swp = newX;
            newX = newY * -1;
            newY = swp;
            DPoint normalizedPoint = new DPoint(newX, newY);
            normalizedPoints.add(normalizedPoint);
        }
        return normalizedPoints;
    }

    private void showWiredFrame(){
        if(resultPoints == null || resultPoints.isEmpty()){
            JOptionPane.showMessageDialog(new JFrame(), "Figure is not set", "Render error",
                    JOptionPane.ERROR_MESSAGE);
        }else{
            SceneBuilder sceneBuilder = new SceneBuilder();
            sceneBuilder.displayScene(normalizePoints(),repeatNumber,edgesNumber, smoothLevel, getMostDelta());
        }
    }

    private double getMostDelta(){
        double xMax = 0;
        double xMin = 0;
        double yMax = 0;
        double yMin = 0;
        for (DPoint point:normalizePoints()
             ) {
            if(point.x < xMin){
                xMin = point.x;
            }if(point.x > xMax){
                xMax = point.x;
            }if(point.y < yMin){
                yMin = point.y;
            }if(point.y > yMax){
                yMin = point.y;
            }
        }
        double xDelta = Math.abs(xMax-xMin);
        double yDelta = Math.abs(yMax-yMin);
        return Math.max(xDelta,yDelta);
    }

}
