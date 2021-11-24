import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class tester {
    @Test
    void myXMLreader_Test()
    {

    }

    @Test
    void BFS_DFS_ClearColors_Test()
    {
        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");
        Node e = new Node("E");
        Node f = new Node("F");
        a.next.add(b);
        b.next.add(c);
        c.next.add(d);
        c.next.add(e);
        Node g = f.addNode("G");
        HashMap<String,Node> network = new HashMap<String,Node>();
        network.put("A",a);network.put("B",b);network.put("C",c);
        network.put("D",d);network.put("E",e);network.put("F",f);
        assertEquals(a.bfs("E"), e); // should find "E"
        BayesBall.clearColors(network);
        assertNotEquals(b.bfs("F"), f); // should return NULL
        BayesBall.clearColors(network);
        assertEquals(f.dfs("G"), g); // should find "G"
        BayesBall.clearColors(network);
        assertNotEquals(b.dfs("F"), f); // should return NULL
        BayesBall.clearColors(network);
        assertEquals(f.dfs("G"), g); // should find "G"
        BayesBall.clearColors(network);
    }

}
