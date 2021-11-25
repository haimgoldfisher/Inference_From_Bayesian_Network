import java.io.IOException;
import java.util.*;

public class BayesBall {
    public static boolean bayes_ball(String query, HashMap<String, Node> vars)
    {
        String[] first = query.split("\\|"); // split the query from the given nodes
        System.out.println(Arrays.toString(first));
        String[] ind = first[0].split("-"); // first the two elements of the query
        String[] given = new String[0];
        if (first.length > 1) // else, we have no given nodes in this query
            given = first[1].split(","); // we can have more than one given
        return independence(vars.get(ind[0]), vars.get(ind[1]), given, vars); // after filtering
    }

    private static boolean independence(Node source, Node destination, String[] given, HashMap<String,Node> vars)
    {
        if (given.length > 0) // else, we have no evidence nodes
            markEvidences(given, vars); // an inner function to mark all evidence nodes as colored
        if (vars.get(source.key).INDsearch(destination.key, vars) == destination) {
            resetVars(vars);
            return false;
        }
        resetVars(vars); // after running the algo, we would like to reset the color of each node
        return true;
    }

    private static void markEvidences(String[] given, HashMap<String,Node> vars)
    {
        String[] givenDiv = new String[0];
        for (String s : given) {
            givenDiv = s.split("=");
            if (vars.get(givenDiv[0]) != null) {
                vars.get(givenDiv[0]).color = Node.COLORED;
            } // mark every evidence node as colored
        }
    }


    public static void resetVars(HashMap <String,Node> myNetwork)
    {
        for (Map.Entry<String, Node> val : myNetwork.entrySet()) {
            val.getValue().color = Node.UNCOLORED;
            val.getValue().visit = Node.UNVISITED;
        }
    }


    public static void main(String[] args) throws IOException {
        String q1 = "B-E|";
        String q2 = "B-E|J=T";
        HashMap<String,Node> vars = myXMLreader.XMLreader("src/alarm_net.xml");
        System.out.println(bayes_ball(q1, vars));
        System.out.println(bayes_ball(q2, vars));
    }
}
