package org.directedgraph;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DirectedGraphTest {

    @Test
    void testQueryBridgeWords_1() {
        DirectedGraph graph = new DirectedGraph();
        String result = graph.queryBridgeWords(graph.buildGraph(graph.readWordsFromFile("input.txt")),"hurt", "owl");
        assertEquals("The bridge word from \"hurt\" to \"owl\" is: deer.", result);
    }

    @Test
    void testQueryBridgeWords_2() {
        DirectedGraph graph = new DirectedGraph();
        String result = graph.queryBridgeWords(graph.buildGraph(graph.readWordsFromFile("input.txt")),"alex", "clear");
        assertEquals("No bridge words from \"alex\" to \"clear\"!", result);
    }

    @Test
    void testQueryBridgeWords_3() {
        DirectedGraph graph = new DirectedGraph();
        String result = graph.queryBridgeWords(graph.buildGraph(graph.readWordsFromFile("input.txt")),"alex", "river");
        assertEquals("No \"river\" in the graph!", result);
    }

    @Test
    void testQueryBridgeWords_4() {
        DirectedGraph graph = new DirectedGraph();
        String result = graph.queryBridgeWords(graph.buildGraph(graph.readWordsFromFile("input.txt")),"alex", "walks.deep");
        assertEquals("No \"walks.deep\" in the graph!", result);
    }
}