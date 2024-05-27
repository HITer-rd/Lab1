package org.DirectedGraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class DirectedGraph {
    private static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> Graph;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        String fileName = "input.txt";
        List<String> words = readWordsFromFile(fileName);
        Graph = buildGraph(words);

        while (true) {
            System.out.println("请选择功能：");
            System.out.println("1. 展示有向图");
            System.out.println("2. 查询桥接词");
            System.out.println("3. 生成新文本");
            System.out.println("4. 计算最短路径");
            System.out.println("5. 随机游走");
            System.out.println("6. 退出");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showDirectedGraph(Graph);
                    break;
                case "2":
                    System.out.println("请输入第一个单词：");
                    String word1 = scanner.nextLine();
                    System.out.println("请输入第二个单词：");
                    String word2 = scanner.nextLine();
                    String bridgeWordsResult = queryBridgeWords(word1, word2);
                    System.out.println(bridgeWordsResult);
                    break;
                case "3":
                    System.out.println("请输入文本：");
                    String inputText = scanner.nextLine();
                    String newText = generateNewText(inputText);
                    System.out.println("New text with bridge words: " + newText);
                    break;
                case "4":
                    System.out.println("请输入起始单词：");
                    word1 = scanner.nextLine();
                    System.out.println("请输入目标单词：");
                    word2 = scanner.nextLine();
                    String shortestPathResult = calcShortestPath(word1, word2);
                    System.out.println(shortestPathResult);
                    break;
                case "5":
                    String randomWalkResult = randomWalk();
                    System.out.println(randomWalkResult);
                    break;
                case "6":
                    System.out.println("退出程序。");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("无效的选择，请重新输入。");
            }
        }
    }

    private static List<String> readWordsFromFile(String fileName) {
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.replaceAll("[^a-zA-Z ]", " ").toLowerCase().split("\\s+");
                words.addAll(Arrays.asList(tokens));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    // 构建加权有向图
    private static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> buildGraph(List<String> words) {
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        for (int i = 0; i < words.size() - 1; i++) {
            String currentWord = words.get(i);
            String nextWord = words.get(i + 1);

            if (!graph.containsVertex(currentWord)) {
                graph.addVertex(currentWord);
            }
            if (!graph.containsVertex(nextWord)) {
                graph.addVertex(nextWord);
            }

            DefaultWeightedEdge edge = graph.getEdge(currentWord, nextWord);
            if (edge == null) {
                edge = graph.addEdge(currentWord, nextWord);
                graph.setEdgeWeight(edge, 1.0);  // 初始权重设为1
            } else {
                double currentWeight = graph.getEdgeWeight(edge);
                graph.setEdgeWeight(edge, currentWeight + 1.0);  // 权重加1
            }
        }
        return graph;
    }

    private static void showDirectedGraph(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph) throws IOException {
        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter = new JGraphXAdapter<>(graph);

        // 为每条边设置标签为边的权重
        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            double weight = graph.getEdgeWeight(edge);
            graphAdapter.getEdgeToCellMap().get(edge).setValue(weight);
        }

        // 创建一个样式表
        mxStylesheet stylesheet = new mxStylesheet();
        // 设置边的样式，显示边的值
        Map<String, Object> edgeStyle = stylesheet.getDefaultEdgeStyle();
        edgeStyle.put(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER);
        edgeStyle.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE);
        // 将样式表应用于图形适配器
        graphAdapter.setStylesheet(stylesheet);
        // 执行布局
        mxIGraphLayout layout = new mxHierarchicalLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());
        // 创建图像
        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        // 将图像保存到文件
        File imgFile = new File("graph.png");
        ImageIO.write(image, "PNG", imgFile);
        System.out.println("图像已保存到：" + imgFile.getAbsolutePath() + "\n");

        JFrame frame = new JFrame();
        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);
        frame.getContentPane().add(graphComponent);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static List<String> findBridgeWords(String word1, String word2) {
        List<String> bridgeWords = new ArrayList<>();

        if (!Graph.containsVertex(word1) || !Graph.containsVertex(word2)) {
            return bridgeWords;
        }

        Set<DefaultWeightedEdge> outgoingEdges = Graph.outgoingEdgesOf(word1);
        for (DefaultWeightedEdge edge : outgoingEdges) {
            String intermediateWord = Graph.getEdgeTarget(edge);
            if (Graph.containsEdge(intermediateWord, word2)) {
                bridgeWords.add(intermediateWord);
            }
        }

        return bridgeWords;
    }

    private static String queryBridgeWords(String word1, String word2) {
        if (!Graph.containsVertex(word1) && !Graph.containsVertex(word2)) {
            return "No \"" + word1 + "\" and \"" + word2 + "\" in the graph!";
        } else if (!Graph.containsVertex(word1)) {
            return "No \"" + word1 + "\" in the graph!";
        } else if (!Graph.containsVertex(word2)) {
            return "No \"" + word2 + "\" in the graph!";
        }

        List<String> bridgeWords = findBridgeWords(word1, word2);

        if (bridgeWords.isEmpty()) {
            return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        } else {
            int size = bridgeWords.size();
            if (size == 1) {
                return "The bridge word from \"" + word1 + "\" to \"" + word2 + "\" is: " + bridgeWords.get(0) + ".";
            } else {
                StringBuilder sb = new StringBuilder("The bridge words between \"" + word1 + "\" and \"" + word2 + "\" are: ");
                for (int i = 0; i < size - 1; i++) {
                    sb.append(bridgeWords.get(i));
                    if (i == size - 2) {
                        sb.append(", and ");
                    } else if (i < size - 2) {
                        sb.append(", ");
                    }
                }
                System.out.println(sb.toString());
                return sb.toString();
            }
        }
    }

    private static String generateNewText(String inputText) {
        String[] words = inputText.replaceAll("[^a-zA-Z ]", " ").toLowerCase().split("\\s+");
        StringBuilder newTextWithBridges = new StringBuilder();

        Random random = new Random();

        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];

            newTextWithBridges.append(word1).append(" ");

            List<String> bridgeWords = findBridgeWords(word1, word2);

            if (!bridgeWords.isEmpty()) {
                String bridgeWord = bridgeWords.get(random.nextInt(bridgeWords.size()));
                newTextWithBridges.append(bridgeWord).append(" ");
            }
        }

        // Append the last word
        newTextWithBridges.append(words[words.length - 1]);

        String newText = newTextWithBridges.toString();

        return newText;
    }

    private static String calcShortestPath(String word1, String word2) throws IOException {
        if (word1 != null && !word1.isEmpty() && !Graph.containsVertex(word1)) {
            return "No \"" + word1 + "\" in the graph!";
        }
        if (word2 != null && !word2.isEmpty() && !Graph.containsVertex(word2)) {
            return "No \"" + word2 + "\" in the graph!";
        }

        if (word1 != null && !word1.isEmpty() && (word2 == null || word2.isEmpty())) {
            return calcAllShortestPathsFrom(word1);
        } else if (word2 != null && !word2.isEmpty() && (word1 == null || word1.isEmpty())) {
            return calcAllShortestPathsFrom(word2);
        } else {
            return calcShortestPathBetween(word1, word2);
        }
    }

    private static String calcShortestPathBetween(String word1, String word2) throws IOException {
        if((word1 == null || word1.isEmpty()) && (word2 == null || word2.isEmpty())) {
            return "输入为空";
        }
        if (word1.equals(word2)) {
            return "两个单词相同，无需计算路径。";
        }

        // 使用自定义的Dijkstra算法计算最短路径
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previousNodes = new HashMap<>();
        PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        for (String vertex : Graph.vertexSet()) {
            if (vertex.equals(word1)) {
                distances.put(vertex, 0.0);
            } else {
                distances.put(vertex, Double.MAX_VALUE);
            }
            priorityQueue.add(vertex);
        }

        while (!priorityQueue.isEmpty()) {
            String current = priorityQueue.poll();

            if (current.equals(word2)) {
                break;
            }

            for (DefaultWeightedEdge edge : Graph.outgoingEdgesOf(current)) {
                String neighbor = Graph.getEdgeTarget(edge);
                double newDist = distances.get(current) + Graph.getEdgeWeight(edge);

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previousNodes.put(neighbor, current);
                    priorityQueue.remove(neighbor);
                    priorityQueue.add(neighbor);
                }
            }
        }

        if (!previousNodes.containsKey(word2)) {
            return "No path from \"" + word1 + "\" to \"" + word2 + "\"!";
        } else {
            List<String> path = new ArrayList<>();
            for (String at = word2; at != null; at = previousNodes.get(at)) {
                path.add(at);
            }
            Collections.reverse(path);

            StringBuilder result = new StringBuilder("Shortest path from \"" + word1 + "\" to \"" + word2 + "\" is: ");
            for (int i = 0; i < path.size(); i++) {
                result.append(path.get(i));
                if (i < path.size() - 1) {
                    result.append(" -> ");
                }
            }
            result.append(", the length is ").append(distances.get(word2));

            highlightPath(path);

            return result.toString();
        }
    }

    private static String calcAllShortestPathsFrom(String word) {
        // 使用自定义的Dijkstra算法计算从word到所有其他单词的最短路径
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previousNodes = new HashMap<>();
        PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        for (String vertex : Graph.vertexSet()) {
            if (vertex.equals(word)) {
                distances.put(vertex, 0.0);
            } else {
                distances.put(vertex, Double.MAX_VALUE);
            }
            priorityQueue.add(vertex);
        }

        while (!priorityQueue.isEmpty()) {
            String current = priorityQueue.poll();

            for (DefaultWeightedEdge edge : Graph.outgoingEdgesOf(current)) {
                String neighbor = Graph.getEdgeTarget(edge);
                double newDist = distances.get(current) + Graph.getEdgeWeight(edge);

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previousNodes.put(neighbor, current);
                    priorityQueue.remove(neighbor);
                    priorityQueue.add(neighbor);
                }
            }
        }

        // 构建并返回所有路径
        StringBuilder result = new StringBuilder();
        for (String target : Graph.vertexSet()) {
            if (!target.equals(word)) {
                List<String> path = new ArrayList<>();
                for (String at = target; at != null; at = previousNodes.get(at)) {
                    path.add(0, at);
                }
                if (path.size() > 1) {
                    result.append("从 ").append(word).append(" 到 ").append(target).append(" 的最短路径：").append(String.join("->", path)).append("\n");
                    //highlightPath(path); // 突出显示每条路径
                }
            }
        }

        if (result.length() == 0) {
            return "No path from " + word + " to other words!";
        }

        return result.toString();
    }


    private static void highlightPath(List<String> path) throws IOException {
        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter = new JGraphXAdapter<>(Graph);

        // 设置边的样式，显示边的值
        for (DefaultWeightedEdge edge : Graph.edgeSet()) {
            double weight = Graph.getEdgeWeight(edge);
            graphAdapter.getEdgeToCellMap().get(edge).setValue(weight);
        }

        mxStylesheet stylesheet = new mxStylesheet();
        Map<String, Object> edgeStyle = stylesheet.getDefaultEdgeStyle();
        edgeStyle.put(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER);
        edgeStyle.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE);
        graphAdapter.setStylesheet(stylesheet);

        mxIGraphLayout layout = new mxHierarchicalLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        // 高亮显示最短路径
        for (int i = 0; i < path.size() - 1; i++) {
            DefaultWeightedEdge edge = Graph.getEdge(path.get(i), path.get(i + 1));
            if (edge != null) {
                Object cell = graphAdapter.getEdgeToCellMap().get(edge);
                graphAdapter.getModel().setStyle(cell, "strokeColor=red;strokeWidth=3");
            }
        }

        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("shortest_path.png");
        ImageIO.write(image, "PNG", imgFile);
        System.out.println("最短路径图像已保存到：" + imgFile.getAbsolutePath());

        JFrame frame = new JFrame();
        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);
        frame.getContentPane().add(graphComponent);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static String randomWalk() {
        if (Graph.vertexSet().isEmpty()) {
            return "图中没有节点。";
        }

        Random random = new Random();
        List<String> vertices = new ArrayList<>(Graph.vertexSet());
        String startNode = vertices.get(random.nextInt(vertices.size()));
        List<String> path = new ArrayList<>();
        Set<DefaultWeightedEdge> visitedEdges = new HashSet<>();
        path.add(startNode);

        String currentNode = startNode;
        boolean userInterrupted = false;

        while (true) {
            Set<DefaultWeightedEdge> outgoingEdges = Graph.outgoingEdgesOf(currentNode);
            if (outgoingEdges.isEmpty()) {
                break;
            }

            List<DefaultWeightedEdge> edgesList = new ArrayList<>(outgoingEdges);
            DefaultWeightedEdge selectedEdge = edgesList.get(random.nextInt(edgesList.size()));

            if (visitedEdges.contains(selectedEdge)) {
                currentNode = Graph.getEdgeTarget(selectedEdge);
                path.add(currentNode);
                break;
            }

            System.out.println("当前节点: " + currentNode + "。输入'q'停止遍历，或按Enter键继续...");
            String userInput = scanner.nextLine();
            if (userInput.equalsIgnoreCase("q")) {
                userInterrupted = true;
                break;
            }

            visitedEdges.add(selectedEdge);
            currentNode = Graph.getEdgeTarget(selectedEdge);
            path.add(currentNode);
        }

        String result = String.join(" ", path);
        try {
            Files.write(Paths.get("random_walk.txt"), result.getBytes());
            System.out.println("遍历结果已保存到文件：random_walk.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (userInterrupted) {
            return "用户中断遍历，遍历结果：" + result;
        } else {
            return "遍历完成，遍历结果：" + result;
        }
    }
}
