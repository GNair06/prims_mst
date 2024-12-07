import javax.swing.*;
import java.awt.*;
//import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrafficOptimization extends JFrame {
    private int[][] graph;
    private int vertices;
    private JPanel graphPanel;
    private List<int[]> mstEdges; // To store MST edges

    public TrafficOptimization() {
        setTitle("Traffic Optimization using Prim's Algorithm");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(1, 3));
        JLabel verticesLabel = new JLabel("Number of Intersections (Vertices):");
        JTextField verticesField = new JTextField();
        JButton generateButton = new JButton("Generate Graph");
        JButton optimizeButton = new JButton("Optimize Traffic");

        inputPanel.add(verticesLabel);
        inputPanel.add(verticesField);
        inputPanel.add(generateButton);
        inputPanel.add(optimizeButton);

        // Graph panel for visualization
        graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGraph(g);
            }
        };
        graphPanel.setBackground(Color.WHITE);

        add(inputPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);

        // Event handlers
        generateButton.addActionListener(e -> {
            try {
                vertices = Integer.parseInt(verticesField.getText());
                generateRandomGraph(vertices);
                mstEdges = new ArrayList<>(); // Reset MST edges
                repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        optimizeButton.addActionListener(e -> {
            if (graph != null && vertices > 0) {
                runPrimAlgorithm();
                repaint(); // Redraw the graph to highlight MST edges
            } else {
                JOptionPane.showMessageDialog(this, "Please generate a graph first.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void generateRandomGraph(int vertices) {
        graph = new int[vertices][vertices];
        Random rand = new Random();

        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {
                graph[i][j] = graph[j][i] = rand.nextInt(20) + 1; // Random weight between 1 and 20
            }
        }
    }

    private void runPrimAlgorithm() {
        boolean[] visited = new boolean[vertices];
        int[] key = new int[vertices];
        int[] parent = new int[vertices];
        mstEdges = new ArrayList<>();
        for (int i = 0; i < vertices; i++) key[i] = Integer.MAX_VALUE;
        key[0] = 0;
        parent[0] = -1;

        for (int count = 0; count < vertices - 1; count++) {
            int u = findMinKeyVertex(key, visited);
            visited[u] = true;

            for (int v = 0; v < vertices; v++) {
                if (graph[u][v] != 0 && !visited[v] && graph[u][v] < key[v]) {
                    parent[v] = u;
                    key[v] = graph[u][v];
                }
            }
        }

        // Add edges to MST list
        for (int i = 1; i < vertices; i++) {
            mstEdges.add(new int[]{parent[i], i});
        }
    }

    private int findMinKeyVertex(int[] key, boolean[] visited) {
        int min = Integer.MAX_VALUE, minIndex = -1;

        for (int v = 0; v < vertices; v++) {
            if (!visited[v] && key[v] < min) {
                min = key[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    private void drawGraph(Graphics g) {
        if (graph == null || vertices == 0) return;

        int radius = 200; // Radius for circular layout
        int centerX = graphPanel.getWidth() / 2;
        int centerY = graphPanel.getHeight() / 2;
        int[] x = new int[vertices];
        int[] y = new int[vertices];

        // Calculate vertex positions
        for (int i = 0; i < vertices; i++) {
            x[i] = (int) (centerX + radius * Math.cos(2 * Math.PI * i / vertices));
            y[i] = (int) (centerY + radius * Math.sin(2 * Math.PI * i / vertices));
        }

        // Draw edges
        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {
                if (graph[i][j] != 0) {
                    g.setColor(Color.BLACK); // Default color
                    for (int[] edge : mstEdges) {
                        if ((edge[0] == i && edge[1] == j) || (edge[0] == j && edge[1] == i)) {
                            g.setColor(Color.RED); // MST edges in red
                            break;
                        }
                    }
                    g.drawLine(x[i], y[i], x[j], y[j]); // Draw edge
                    int midX = (x[i] + x[j]) / 2;
                    int midY = (y[i] + y[j]) / 2;
                    g.setColor(Color.BLACK);
                    g.drawString(String.valueOf(graph[i][j]), midX, midY); // Display edge weight
                }
            }
        }

        // Draw vertices and their labels
        for (int i = 0; i < vertices; i++) {
            g.setColor(Color.BLUE);
            g.fillOval(x[i] - 15, y[i] - 15, 30, 30); // Draw vertex
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(i), x[i] - 5, y[i] + 5); // Label vertex
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TrafficOptimization app = new TrafficOptimization();
            app.setVisible(true);
        });
    }
}
