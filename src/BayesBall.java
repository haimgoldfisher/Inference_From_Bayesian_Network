import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class BayesBall {
    public static boolean bayes_ball(String query, ArrayList<Node> vars)
    {
        String[] first = query.split("\\|");
        System.out.println(Arrays.toString(first));
        String[] ind = first[0].split("-");
        String[] given = new String[0];
        if (first[1] != null)
            given = first[1].split(",");
        return independence(ind[0], ind[1], given, vars);
    }

    private static boolean independence(String source, String destination, String[] given, ArrayList<Node> vars)
    {
        String[] givenDiv = new String[0];
        for (String s : given) {
            givenDiv = s.split("=");
            if (myXMLreader.searchNode(vars, givenDiv[0]) != null) {
                    myXMLreader.searchNode(vars, givenDiv[0]).outcomeSearch(givenDiv[1]);
                }
            }
        }
    }

    public static void main(String[] args) {
        String q1 = "B-E|";
        String q2 = "B-E|J=T";

    }
}
