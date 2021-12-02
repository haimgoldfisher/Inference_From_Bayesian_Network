import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class tester {
    HashMap<String,Node> alarm = myXMLreader.XMLreader("alarm_net.xml");
    HashMap<String,Node> bigNet = myXMLreader.XMLreader("big_net.xml");

    public tester() throws IOException {
    }

    @Test
    void Bayes_Ball_Test() throws IOException {
        String basic_c1 = "B-J|"; // no
        String basic_c2 = "J-B|A=F"; // yes
        String basic_c3 = "E-M|A=T"; // yes
        String q1 = "B-E|"; // yes
        String q2 = "B-E|J=T"; // no
        assertFalse(BayesBall.bayes_ball(basic_c1, alarm));
        assertTrue(BayesBall.bayes_ball(basic_c2, alarm));
        assertTrue(BayesBall.bayes_ball(basic_c3, alarm));
        assertTrue(BayesBall.bayes_ball(q1, alarm));
        assertFalse(BayesBall.bayes_ball(q2, alarm));
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

    @Test
    void CPT_Test()
    {
        alarm.get("A").cpt = new CPT(alarm.get("A"));
        alarm.get("B").cpt = new CPT(alarm.get("B"));
        System.out.println(alarm.get("B").table);
        System.out.println(alarm.get("B").cpt.varsNames);
        System.out.println(alarm.get("B").cpt.tableRows);
        System.out.println();
        System.out.println(alarm.get("A").table);
        System.out.println(alarm.get("A").cpt.varsNames);
        System.out.println(alarm.get("A").cpt.tableRows);
        System.out.println();
        bigNet.get("C1").cpt = new CPT(bigNet.get("C1"));
        System.out.println(bigNet.get("C1").table);
        System.out.println(bigNet.get("C1").cpt.varsNames);
        System.out.println(bigNet.get("C1").cpt.tableRows);
    }

    @Test
    void sortFactors_Test()
    {
        alarm.get("A").cpt = new CPT(alarm.get("A"));
        alarm.get("E").cpt = new CPT(alarm.get("E"));
        alarm.get("M").cpt = new CPT(alarm.get("M"));
        alarm.get("J").cpt = new CPT(alarm.get("J"));
        CPT f1 = alarm.get("A").cpt;
        CPT f2 = alarm.get("E").cpt;
        CPT f3 = alarm.get("M").cpt;
        CPT f4 = alarm.get("J").cpt;
        System.out.println(f1.tableRows.size()+","+f2.tableRows.size()+","+f3.tableRows.size()+","+f4.tableRows.size());
        Vector<CPT> factors = new Vector<CPT>();
        factors.add(f1);
        factors.add(f3);
        factors.add(f4);
        factors.add(f2);
        VariableElimination.sortFactors(factors);
        for (CPT factor : factors) {
            System.out.println(factor.tableRows.size());
        }

    }

    @Test
    void join_elim_Test()
    {
        alarm.get("A").cpt = new CPT(alarm.get("A"));
        alarm.get("E").cpt = new CPT(alarm.get("E"));
        alarm.get("M").cpt = new CPT(alarm.get("M"));
        alarm.get("J").cpt = new CPT(alarm.get("J"));
        CPT f1 = alarm.get("A").cpt;
        CPT f2 = alarm.get("E").cpt;
        CPT f3 = alarm.get("M").cpt;
        CPT f4 = alarm.get("J").cpt;
        System.out.println(f1.tableRows.size()+","+f2.tableRows.size()+","+f3.tableRows.size()+","+f4.tableRows.size());
        Vector<CPT> factors = new Vector<CPT>();
        factors.add(f1);
        factors.add(f3);
        factors.add(f4);
        factors.add(f2);
        VariableElimination.sortFactors(factors);
        while (factors.size()>1){
            factors.get(0).join(factors.get(1), new AtomicInteger(0), alarm, factors);
            VariableElimination.sortFactors(factors);
        }
        System.out.println(factors.size());
        System.out.println(factors.get(0).varsNames+" : "+factors.get(0).tableRows);
    }

    @Test
    void join_Test()
    {
        alarm.get("A").cpt = new CPT(alarm.get("A"));
        alarm.get("E").cpt = new CPT(alarm.get("E"));
        alarm.get("M").cpt = new CPT(alarm.get("M"));
        alarm.get("J").cpt = new CPT(alarm.get("J"));
        CPT f1 = alarm.get("A").cpt;
        CPT f2 = alarm.get("E").cpt;
        CPT f3 = alarm.get("M").cpt;
        CPT f4 = alarm.get("J").cpt;
        Vector<CPT> factors = new Vector<CPT>();
        factors.add(f1);
        factors.add(f2);
        System.out.println(f1.varsNames+": "+f1.tableRows);
        System.out.println(f2.varsNames+": "+f2.tableRows);
        System.out.println(f3.varsNames+": "+f3.tableRows);
        System.out.println(f4.varsNames+": "+f4.tableRows);
        VariableElimination.sortFactors(factors);
        CPT f5 = f2.join(f1, new AtomicInteger(0), alarm, factors);
        System.out.println(f5.varsNames+": "+f5.tableRows);
        System.out.println(factors.size());
        f5.eliminate("A", new AtomicInteger(0));
        System.out.println(f5.varsNames+": "+f5.tableRows);
    }

    @Test
    void Variable_Elimination_Test()
    {
        String q1 = "P(B=T|J=T,M=T) A-E";
        String q2 = "P(B=T|J=T,M=T) E-A";
        String q3 = "P(J=T|B=T) A-E-M";
        String q4 = "P(J=T|B=T) M-E-A";

        assertEquals(VariableElimination.variable_elimination(q1, alarm), "0.28417,7,16");
        assertEquals(VariableElimination.variable_elimination(q2, alarm), "0.28417,7,16");
        assertEquals(VariableElimination.variable_elimination(q3, alarm), "0.84902,7,12");
        assertEquals(VariableElimination.variable_elimination(q4, alarm), "0.84902,5,8");

        String q5 = "P(A2=T|) D1-B3-C1-B0-A1-B1-A3-C3-B2-C2";
        String q6 = "P(B0=v3|C3=T,B2=F,C2=v3) A2-D1-B3-C1-A1-B1-A3";
        String q7 = "P(A2=T|C2=v1) D1-C1-B0-A1-B1-A3-C3-B2-B3";
        String q8 = "P(D1=T|C2=v1,C3=F) A2-C1-B0-A1-B1-A3-B2-B3";
        String q9 = "P(D1=T|C2=v1,C3=F) A2-C1-B0-A1-B1-A3-B2-B3";

        assertEquals(VariableElimination.variable_elimination(q5, bigNet), "0.09,0,0");
        assertEquals(VariableElimination.variable_elimination(q6, bigNet), "0.42307,10,21");
        assertEquals(VariableElimination.variable_elimination(q7, bigNet), "0.0936,9,18");
        assertEquals(VariableElimination.variable_elimination(q8, bigNet), "0.37687,83,168");
        assertEquals(VariableElimination.variable_elimination(q9, bigNet), "0.37687,83,168");
    }
}
