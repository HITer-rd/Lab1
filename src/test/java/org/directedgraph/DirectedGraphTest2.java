package org.directedgraph;


import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class DirectedGraphTest2 {

    @Test
    void testRandomWalk_1() {
        DirectedGraph graph = new DirectedGraph();
        String result = graph.randomWalk(graph.buildGraph(graph.readWordsFromFile("input_test.txt")));
        assertEquals("图中没有节点。", result);
    }

    @Test
    void testRandomWalk_2() {
        DirectedGraph graph = new DirectedGraph();
        String result = graph.randomWalk(graph.buildGraph(graph.readWordsFromFile("input_test.txt")));
        assertEquals("遍历完成，遍历结果：" + "hello", result);
    }

    @Test
    void testRandomWalk_3() {
        //模拟用户输入，下面输入意思是继续游走
        String s = "r\nr\nr";
        ByteArrayInputStream strIn = new ByteArrayInputStream(s.getBytes());
        System.setIn(strIn);

        DirectedGraph graph = new DirectedGraph();

        String result = graph.randomWalk(graph.buildGraph(graph.readWordsFromFile("input_test.txt")));
        List<String> validResults = Arrays.asList(
                "遍历完成，遍历结果：world hello world hello",
                "遍历完成，遍历结果：hello world hello world"
        );
        assertTrue("结果应该是有效结果之一", validResults.contains(result));
    }

    @Test
    void testRandomWalk_4() {
        //模拟用户输入，下面输入意思是继续游走
        String s = "q";
        ByteArrayInputStream strIn = new ByteArrayInputStream(s.getBytes());
        System.setIn(strIn);

        DirectedGraph graph = new DirectedGraph();

        String result = graph.randomWalk(graph.buildGraph(graph.readWordsFromFile("input_test.txt")));
        List<String> validResults = Arrays.asList(
                "用户中断遍历，遍历结果：hello",
                "用户中断遍历，遍历结果：world"
        );
        assertTrue("结果应该是有效结果之一", validResults.contains(result));
    }

    @Test
    void testRandomWalk_5() {
        //模拟用户输入，下面输入意思是继续游走
        String s = "r\nq\nq\n";
        ByteArrayInputStream strIn = new ByteArrayInputStream(s.getBytes());
        System.setIn(strIn);

        DirectedGraph graph = new DirectedGraph();

        String result = graph.randomWalk(graph.buildGraph(graph.readWordsFromFile("input_test.txt")));
        List<String> validResults = Arrays.asList(
                "用户中断遍历，遍历结果：hello world",
                "用户中断遍历，遍历结果：world hello"
        );
        assertTrue("结果应该是有效结果之一", validResults.contains(result));
    }

    @Test
    void testRandomWalk_6() {
        //模拟用户输入，下面输入意思是继续游走
        String s = "r\nr\nr";
        ByteArrayInputStream strIn = new ByteArrayInputStream(s.getBytes());
        System.setIn(strIn);

        DirectedGraph graph = new DirectedGraph();

        String result = graph.randomWalk(graph.buildGraph(graph.readWordsFromFile("input_test.txt")));
        List<String> validResults = Arrays.asList(
                "遍历完成，遍历结果：world hello world hello",
                "遍历完成，遍历结果：hello world hello world"
        );
        assertTrue("结果应该是有效结果之一", validResults.contains(result));
    }
}