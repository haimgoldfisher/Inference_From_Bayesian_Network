import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class tester {

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
        BayesBall.resetVars(network);
        assertNotEquals(b.bfs("F"), f); // should return NULL
        BayesBall.resetVars(network);
        assertEquals(f.dfs("G"), g); // should find "G"
        BayesBall.resetVars(network);
        assertNotEquals(b.dfs("F"), f); // should return NULL
        BayesBall.resetVars(network);
        assertEquals(f.dfs("G"), g); // should find "G"
        BayesBall.resetVars(network);
    }

    @Test
    void Bayes_Ball_Test() throws IOException {
        String basic_c1 = "B-J|"; // no
        String basic_c2 = "J-B|A=F"; // yes
        String basic_c3 = "E-M|A=T"; // yes
        String q1 = "B-E|"; // yes
        String q2 = "B-E|J=T"; // no
        HashMap<String,Node> alarm = myXMLreader.XMLreader("src/alarm_net.xml");
        assertFalse(BayesBall.bayes_ball(basic_c1, alarm));
        assertTrue(BayesBall.bayes_ball(basic_c2, alarm));
        assertTrue(BayesBall.bayes_ball(basic_c3, alarm));
        assertTrue(BayesBall.bayes_ball(q1, alarm));
        assertFalse(BayesBall.bayes_ball(q2, alarm));
        HashMap<String,Node> bigNet = myXMLreader.XMLreader("src/big_net.xml");
        String q3 = "A2-B3|C3=T,B2=F,C2=v3"; // yes
        String q4 = "A2-D1|C3=T,B2=F,C2=v3"; // no
        String q5 = "A2-C1|C3=T,B2=F,C2=v3"; // no
        String q6 = "A2-B0|C3=T,B2=F,C2=v3"; // no
        String q7 = "A2-A1|C3=T,B2=F,C2=v3"; // no
        String q8 = "C2-A3|B3=T,C1=T"; // yes
        String q9 = "B0-C2|A2=T,A3=T"; // yes
        String q10 = "A1-D1|C3=T,B2=F,B3=F"; // no
        assertTrue(BayesBall.bayes_ball(q3, bigNet));
        assertFalse(BayesBall.bayes_ball(q4, bigNet));
        assertFalse(BayesBall.bayes_ball(q5, bigNet));
        assertFalse(BayesBall.bayes_ball(q6, bigNet));
        assertFalse(BayesBall.bayes_ball(q7, bigNet));
        assertTrue(BayesBall.bayes_ball(q8, bigNet));
        assertTrue(BayesBall.bayes_ball(q9, bigNet));
        assertFalse(BayesBall.bayes_ball(q10, bigNet));
    }

}
