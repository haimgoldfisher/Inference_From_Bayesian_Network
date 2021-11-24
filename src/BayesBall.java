import java.io.File;
import java.util.*;

public class BayesBall {
    public static boolean bayes_ball(String query, HashMap<String, Node> vars)
    {
        String[] first = query.split("\\|"); // split the query from the given nodes
        System.out.println(Arrays.toString(first));
        String[] ind = first[0].split("-"); // first the two elements of the query
        String[] given = new String[0];
        if (first[1] != null) // else, we have no given nodes in this query
            given = first[1].split(","); // we can have more than one given
        return independence(ind[0], ind[1], given, vars); // after filtering
    }

    private static boolean independence(String source, String destination, String[] given, HashMap<String,Node> vars)
    {
        String[] givenDiv = new String[0];
        for (String s : given) {
            givenDiv = s.split("=");
            if (vars.get(givenDiv[0]) != null) {
                    vars.get(givenDiv[0]).outcomeSearch(givenDiv[1]);
                }
            }
        clearColors(vars); // after running the algo, we would like to reset the color of each node
        return true;
    }

    public static void clearColors(HashMap <String,Node> myNetwork)
    {
        int UNCOLORED = 0;
        for (Map.Entry<String, Node> val : myNetwork.entrySet())
            val.getValue().color = UNCOLORED;
    }

    public static void main(String[] args) {
        String q1 = "B-E|";
        String q2 = "B-E|J=T";

    }
}
