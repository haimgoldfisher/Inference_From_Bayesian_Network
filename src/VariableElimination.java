import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class VariableElimination {
    public static String variable_elimination(String query, HashMap<String, Node> vars)
    {
        double ans = 0; // the final answer
        int mulOpers = 0; // multiplication operations
        int addOpers = 0; // addition  operations
        String[] first = query.split(" ");
        first[0] = first[0].substring(2, first[0].length()-1);
        String[] queryStr = first[0].split("\\|");
        String[] given = new String[0];
        if (queryStr.length > 1) // else, we have no given nodes in this query
            given = first[1].split(",");
        BayesBall.markEvidences(given, vars);
        // eliminate by using bayes ball unnecessary nodes (dont clear color until create the list)
        // create a list of CPT elements
        // sort the list -> from smallest to biggest (if equals - sort by ASCII value of the key)
        // Elinite / Join until find the answer!!!
        // for each eliminate ADD++, for each join MUL++
        // update the answer
        return Math.round(ans*100000.0)/100000.0+","+addOpers+","+mulOpers;
    }

    public static void main(String[] args) {
        String q = "P(B=T|J=T,M=T) A-E";
    }
}
