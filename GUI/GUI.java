import java.util.logging.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;

import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;

public class Main {
    private static Logger log = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(
                                                         Main.class.getResourceAsStream("../logging.properties"));
        } catch (Exception e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }
        try{
            MyForm win = new MyForm();
        }
        catch(Exception e) {
            log.log(Level.SEVERE, "Error: ", e);
        }
    }
}

class MyForm{
    private static Logger log = Logger.getLogger(MyForm.class.getName());
    private String inputData = "";
    private boolean flagButton = false;
    private int numVertices = 0;
    private JPanel gridBefore = new JPanel();
    private JPanel gridAfter = new JPanel();
    private WarshallAlgorithm data;
    public MyForm(){
        log.fine("Creating main window");
        JFrame frame  = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        JPanel pan = new JPanel(new BorderLayout(10, 10));
        pan.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setPreferredSize(new Dimension(1400, 30));
        buttonPanel.setMaximumSize(new Dimension(1400, 30));
        
        JButton fileButton = new JButton("Считать из файла");
        GridBagConstraints constraints2 = new GridBagConstraints();
        constraints2.gridwidth = 2;
        constraints2.gridheight = 1;
        buttonPanel.add(fileButton, constraints2);
        
        JButton firstButton = new JButton("Ввод исходных данных");
        GridBagConstraints constraints1 = new GridBagConstraints();
        constraints1.gridwidth = 2;
        constraints1.gridheight = 1;
        buttonPanel.add(firstButton, constraints1);
        pan.add(buttonPanel, BorderLayout.NORTH);
        
        JPanel lastPanel = new JPanel(new BorderLayout());
        lastPanel.setPreferredSize(new Dimension(1400, 60));
        lastPanel.setMinimumSize(new Dimension(1400, 60));
        lastPanel.setMaximumSize(new Dimension(1400, 60));
        JPanel pane = new JPanel(new GridLayout(1, 4, 150, 0));
        pane.setPreferredSize(new Dimension(1400, 30));
        pane.setMinimumSize(new Dimension(1400, 30));
        
        JButton prevBut = new JButton("Предыдущий шаг");
        JButton nextBut = new JButton("Следующий шаг");
        JButton finishBut = new JButton("Окончательный результат");
        JButton initBut = new JButton("К начальному состоянию");
        pane.add(prevBut);
        pane.add(initBut);
        pane.add(finishBut);
        pane.add(nextBut);
        lastPanel.add(new JLabel("Разработчики: Бергалиев Марлен, Прокопенко Надежда, Тян Екатерина"), BorderLayout.NORTH);
        lastPanel.add(pane, BorderLayout.SOUTH);
        pan.add(lastPanel, BorderLayout.SOUTH);
        
        JPanel panelMatrix = new JPanel();
        panelMatrix.setLayout(new BoxLayout(panelMatrix, BoxLayout.PAGE_AXIS));
        panelMatrix.setPreferredSize(new Dimension(600, 640));
        panelMatrix.setMinimumSize(new Dimension(600, 640));
        panelMatrix.setMaximumSize(new Dimension(600, 710));
        JScrollPane scroll = new JScrollPane(panelMatrix);
        scroll.setPreferredSize(new Dimension(600, 640));
        scroll.setMinimumSize(new Dimension(600, 640));
        scroll.setMaximumSize(new Dimension(600, 710));
        pan.add(scroll, BorderLayout.EAST);
        
        JLabel labelInitial = new JLabel();
        labelInitial.setSize(580, 30);
        labelInitial.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelMatrix.add(labelInitial);
        panelMatrix.add(Box.createRigidArea(new Dimension(0, 10)));
        panelMatrix.add(gridBefore);
        panelMatrix.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel labelTerminal = new JLabel();
        labelTerminal.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelTerminal.setSize(580, 30);
        panelMatrix.add(labelTerminal);
        panelMatrix.add(Box.createRigidArea(new Dimension(0, 10)));
        panelMatrix.add(gridAfter);
        
        JPanel panelGraph = new JPanel();
        panelGraph.setLayout(new BoxLayout(panelGraph, BoxLayout.PAGE_AXIS));
        panelGraph.setPreferredSize(new Dimension(630, 640));
        panelGraph.setMinimumSize(new Dimension(630, 640));
        panelGraph.setMaximumSize(new Dimension(650, 710));
        
        JPanel paintedGraphPanel = new JPanel();
        paintedGraphPanel.setPreferredSize(new Dimension(630, 590));
        paintedGraphPanel.setMaximumSize(new Dimension(650, 650));
        paintedGraphPanel.setMinimumSize(new Dimension(630, 590));
        
        JLabel labelGraph = new JLabel();
        labelGraph.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelGraph.setSize(630, 30);
        JScrollPane scrollG = new JScrollPane(panelGraph);
        scrollG.setPreferredSize(new Dimension(630, 590));
        scrollG.setMinimumSize(new Dimension(630, 590));
        scrollG.setMaximumSize(new Dimension(650, 650));
        panelGraph.add(labelGraph);
        panelGraph.add(paintedGraphPanel);
        pan.add(scrollG, BorderLayout.WEST);
        
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.fine("Choosing file");
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                                                             "Text files", "txt");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    String filePath = "" + chooser.getCurrentDirectory();
                    String fileName = chooser.getSelectedFile().getName();
                    File mainFile = new File(filePath, fileName);
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new FileReader(mainFile.getAbsolutePath()));
                        StringBuilder builder = new StringBuilder();
                        String currentLine = reader.readLine();
                        while (currentLine != null) {
                            builder.append(currentLine);
                            builder.append("\n");
                            currentLine = reader.readLine();
                        }
                        reader.close();
                        inputData = builder.toString();
                        flagButton = true;
                        panelGraph.setVisible(flagButton);
                        labelInitial.setText("Матрица на предыдущем шаге:");
                        labelTerminal.setText("Матрица после применения шага алгоритма:");
                        labelGraph.setText("Граф после применения шага алгоритма:");
                        initBut.setVisible(flagButton);
                        finishBut.setVisible(flagButton);
                        nextBut.setVisible(flagButton);
                        prevBut.setVisible(flagButton);
                        labelTerminal.setVisible(flagButton);
                        gridAfter.setVisible(flagButton);
                        lastPanel.setVisible(flagButton);
                        scroll.setVisible(flagButton);
                        scrollG.setVisible(flagButton);
                        try {
                            data = new WarshallAlgorithm(inputData);
                            data.setGraphData(inputData);
                            int count = data.verticesCount();
                            BoolMatrix m = new BoolMatrix(count, count);
                            for (int i = 0; i < count; i++) {
                                for (int j = 0; j < count; j++) {
                                    m.set(i, j, false);
                                }
                            }
                            setMatrix(m, data.getMatrix(), count, gridBefore);
                            BoolMatrix tmpMatr = new BoolMatrix(data.getMatrix());
                            data.stepUp();
                            tmpMatr.sub(data.getMatrix());
                            setMatrix(tmpMatr, data.getMatrix(), count, gridAfter);
                            numVertices = count;
                            GraphDraw(paintedGraphPanel, new Dimension(630, 590));
                            frame.getContentPane().revalidate();
                            frame.getContentPane().repaint();
                            frame.pack();
                        }catch(IllegalArgumentException i){
                            log.log(Level.INFO, "Exception: ", i);
                            JOptionPane.showMessageDialog(frame, "Wrong input data.");
                        }
                    } catch (FileNotFoundException ex) {
                        log.log(Level.INFO, "Exception: ", ex);
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        log.log(Level.WARNING, "Exception: ", ex);
                        ex.printStackTrace();
                    }
                    
                }
            }
        });
        
        firstButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                log.fine("Input data entered");
                JDialog dialog = createDialog(frame);
                dialog.setVisible(true);
                panelGraph.setVisible(flagButton);
                labelInitial.setText("Матрица на предыдущем шаге:");
                labelTerminal.setText("Матрица после применения шага алгоритма:");
                labelGraph.setText("Граф после применения шага алгоритма:");
                initBut.setVisible(flagButton);
                finishBut.setVisible(flagButton);
                nextBut.setVisible(flagButton);
                prevBut.setVisible(flagButton);
                labelTerminal.setVisible(flagButton);
                gridAfter.setVisible(flagButton);
                lastPanel.setVisible(flagButton);
                scroll.setVisible(flagButton);
                scrollG.setVisible(flagButton);
                try{
                    data = new WarshallAlgorithm(inputData);
                    data.setGraphData(inputData);
                    int count = data.verticesCount();
                    BoolMatrix m = new BoolMatrix(count, count);
                    for(int i = 0; i < count; i++){
                        for(int j = 0; j < count; j++){
                            m.set(i, j, false);
                        }
                    }
                    setMatrix(m, data.getMatrix(), count, gridBefore);
                    BoolMatrix tmpMatr = new BoolMatrix(data.getMatrix());
                    data.stepUp();
                    tmpMatr.sub(data.getMatrix());
                    setMatrix(tmpMatr, data.getMatrix(), count, gridAfter);
                    numVertices = count;
                    GraphDraw(paintedGraphPanel, new Dimension(630, 590));
                    frame.getContentPane().revalidate();
                    frame.getContentPane().repaint();
                    frame.pack();
                }catch(IllegalArgumentException i){
                    log.log(Level.INFO, "Exception: ", i);
                    JOptionPane.showMessageDialog(frame, "Wrong input data.");
                }
                
            }
        });
        
        
        nextBut.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                log.fine("Next button pressed");
                labelInitial.setText("Матрица на предыдущем шаге:");
                labelTerminal.setText("Матрица после применения шага алгоритма:");
                labelGraph.setText("Граф после применения шага алгоритма:");
                initBut.setVisible(true);
                prevBut.setVisible(true);
                labelTerminal.setVisible(true);
                gridAfter.setVisible(true);
                fileButton.setVisible(true);
                BoolMatrix m = new BoolMatrix(numVertices, numVertices);
                initBut.setVisible(true);
                for(int i = 0; i < numVertices; i++){
                    for(int j = 0; j < numVertices; j++){
                        m.set(i, j, false);
                    }
                }
                setMatrix(m, data.getMatrix(), numVertices, gridBefore);
                BoolMatrix matr = new BoolMatrix(data.getMatrix());
                data.stepUp();
                matr.sub(data.getMatrix());
                setMatrix(matr, data.getMatrix(), numVertices, gridAfter);
                GraphDraw(paintedGraphPanel, new Dimension(630, 610));
                WarshallAlgorithm tmp = new WarshallAlgorithm(inputData);
                tmp.toFinalResult();
                if(tmp.getMatrix().equals(data.getMatrix())){
                    nextBut.setVisible(false);
                }
            }
        });
        
        prevBut.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                log.fine("Prev button pressed");
                labelInitial.setText("Матрица на предыдущем шаге:");
                labelTerminal.setText("Матрица после применения шага алгоритма:");
                labelGraph.setText("Граф после применения шага алгоритма:");
                initBut.setVisible(true);
                finishBut.setVisible(true);
                nextBut.setVisible(true);
                labelTerminal.setVisible(true);
                gridAfter.setVisible(true);
                BoolMatrix matr = new BoolMatrix(data.getMatrix());
                BoolMatrix otherMatr = new BoolMatrix(data.getMatrix());
                data.stepDown();
                BoolMatrix m = new BoolMatrix(numVertices, numVertices);
                for(int i = 0; i < numVertices; i++){
                    for(int j = 0; j < numVertices; j++){
                        m.set(i, j, false);
                    }
                }
                matr.sub(data.getMatrix());
                finishBut.setVisible(true);
                setMatrix(matr, otherMatr, numVertices, gridAfter);
                setMatrix(m, data.getMatrix(), numVertices, gridBefore);
                GraphDraw(paintedGraphPanel, new Dimension(630, 610));
                WarshallAlgorithm tmp = new WarshallAlgorithm(inputData);
                tmp.toStart();
                if(tmp.getMatrix().equals(data.getMatrix())){
                    prevBut.setVisible(false);
                }
            }
        });
        
        finishBut.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                log.fine("Finish button pressed");

                labelInitial.setText("Матрица смежности окончательного результата:");

                labelGraph.setText("Граф окончательного результата:");
                labelTerminal.setVisible(false);
                gridAfter.setVisible(false);
                nextBut.setVisible(false);
                finishBut.setVisible(false);
                initBut.setVisible(true);
                prevBut.setVisible(true);
                BoolMatrix m = new BoolMatrix(numVertices, numVertices);
                for(int i = 0; i < numVertices; i++){
                    for(int j = 0; j < numVertices; j++){
                        m.set(i, j, false);
                    }
                }
                data.toFinalResult();
                GraphDraw(paintedGraphPanel, new Dimension(630, 610));
                setMatrix(m, data.getMatrix(), numVertices, gridBefore);
            }
        });
        
        initBut.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                log.fine("Init button pressed");
                finishBut.setVisible(true);
                nextBut.setVisible(true);
                BoolMatrix m = new BoolMatrix(numVertices, numVertices);
                for(int i = 0; i < numVertices; i++){
                    for(int j = 0; j < numVertices; j++){
                        m.set(i, j, false);
                    }
                }
                data.toStart();
                setMatrix(m, data.getMatrix(), numVertices, gridBefore);
                GraphDraw(paintedGraphPanel, new Dimension(630, 610));
                labelTerminal.setVisible(false);
                labelInitial.setText("Матрица смежности начального состояния:");
                labelGraph.setText("Граф начального состояния:");
                prevBut.setVisible(false);
                initBut.setVisible(false);
                gridAfter.setVisible(false);
            }
        });
        
        panelGraph.setVisible(flagButton);
        scroll.setVisible(flagButton);
        scrollG.setVisible(flagButton);
        lastPanel.setVisible(flagButton);
        pan.revalidate();
        pan.repaint();
        pan.setOpaque(true);
        frame.setContentPane(pan);
        frame.pack();
        frame.setVisible(true);
    }
    
    public void GraphDraw(JPanel mainPanel, Dimension DEFAULT_SIZE){
        log.fine("Drawing graph");
        mainPanel.removeAll();
        mainPanel.revalidate();
        mainPanel.repaint();
        
        mainPanel.setPreferredSize(DEFAULT_SIZE);
        mainPanel.setMinimumSize(DEFAULT_SIZE);
        mainPanel.setMaximumSize(DEFAULT_SIZE);
        
        ListenableGraph <String, DefaultEdge> g = new DefaultListenableGraph<>(new DefaultDirectedGraph<>(DefaultEdge.class));
        
        JGraphXAdapter<String, DefaultEdge> jgxAdapter = new JGraphXAdapter<>(g);
        
        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        component.createHorizontalScrollBar();
        component.createVerticalScrollBar();
        mainPanel.add(component);
        
        
        String[] verteces = new String[numVertices];
        for(int i = 0; i < numVertices; i++){
            String v = "\t" + data.getVertex(i) + "\t";
            verteces[i] = v;
            g.addVertex(v);
        }
        
        for(int i = 0; i < numVertices; i++){
            for(int j = 0; j < numVertices; j++){
                if(data.getMatrix().get(i, j) == true){
                    g.addEdge(verteces[i], verteces[j]);
                }
            }
        }
        
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        int radius = 250;
        layout.setX0(DEFAULT_SIZE.width / 16.0);
        layout.setY0(DEFAULT_SIZE.height / 16.0);
        layout.setRadius(radius);
        layout.setMoveCircle(true);
        
        layout.execute(jgxAdapter.getDefaultParent());
        log.fine("Finished drawing graph");
    }
    
    
    
    private void setMatrix(BoolMatrix matr, BoolMatrix otherMatr, int count, JPanel grid){
        log.fine("Setting Matrix");
        grid.removeAll();
        grid.revalidate();
        grid.repaint();
        grid.setPreferredSize(new Dimension(600, 270));
        grid.setMaximumSize(new Dimension(600, 320));
        grid.setMinimumSize(new Dimension(600, 270));
        grid.setBackground(Color.white);
        grid.setLayout(new GridLayout(count + 1, count + 1));
        grid.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JLabel labelEmpty = new JLabel("");
        labelEmpty.setHorizontalAlignment(JLabel.CENTER);
        labelEmpty.setVerticalAlignment(JLabel.CENTER);
        labelEmpty.setBorder(BorderFactory.createLineBorder(Color.black));
        grid.add(labelEmpty);
        
        for(int j = 0; j < count; j++){
            char c = data.getVertex(j);
            JLabel label = new JLabel("" + c);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            label.setBorder(BorderFactory.createLineBorder(Color.black));
            grid.add(label);
            
        }
        
        for(int i = 0; i < count; i++){
            char c = data.getVertex(i);
            JLabel labelVert = new JLabel("" + c);
            labelVert.setHorizontalAlignment(JLabel.CENTER);
            labelVert.setVerticalAlignment(JLabel.CENTER);
            labelVert.setBorder(BorderFactory.createLineBorder(Color.black));
            grid.add(labelVert);
            for(int j = 0; j < count; j++){
                int k = otherMatr.get(i, j) ? 1 : 0;
                JLabel label = new JLabel("" + k);
                if(matr.get(i, j) == true){
                    label.setBackground(Color.red);
                    label.setForeground(Color.white);
                    label.setOpaque(true);
                }
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setVerticalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createLineBorder(Color.black));
                grid.add(label);
                
            }
        }
        return;
    }
    
    private JDialog createDialog(JFrame frame){
        log.fine("Creating input dialog");
        JDialog dialog = new JDialog(frame);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setModal(true);
        dialog.setSize(500, 500);
        
        JTextArea area = new JTextArea(20, 30);
        
        JLabel label = new JLabel("<html><div style='text-align: center;'>" + "Введите ребра в виде пар вершин через пробел: " + "<br>" + " вершина_из_которой_исходит_ребро вершина_в_которую_входит_ребро." + "</div></html>");
        label.setPreferredSize(new Dimension(480, 60));
        label.setMaximumSize(new Dimension(480, 60));
        label.setMinimumSize(new Dimension(480, 60));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setOpaque(true);
        
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setMaximumSize(new Dimension(480, 480));
        panel.setMinimumSize(new Dimension(480, 480));
        panel.setPreferredSize(new Dimension(480, 480));
        
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(480,10)));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(480, 360));
        scroll.setMaximumSize(new Dimension(480, 360));
        scroll.setMinimumSize(new Dimension(480, 360));
        scroll.setOpaque(true);
        panel.add(scroll);
        
        JButton firstButton = new JButton("ОК");
        panel.setMaximumSize(new Dimension(60, 30));
        panel.setMinimumSize(new Dimension(60, 30));
        panel.setPreferredSize(new Dimension(60, 30));
        panel.add(firstButton);
        firstButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                inputData = area.getText();
                if(inputData.length() > 0){
                    flagButton = true;
                }
                dialog.dispose();
            }
        });
        
        panel.add(Box.createGlue());
        panel.setOpaque(true);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
        return dialog;
    }
};
