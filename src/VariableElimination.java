import java.io.File;
import java.io.IOException;
import java.util.*;

public class VariableElimination {
    public static String variable_elimination(String query, HashMap<String, Node> vars)
    {
        double ans = 0; // the final answer
        int mulOpers = 0; // multiplication operations
        int addOpers = 0; // addition  operations
        String[] first = query.split(" "); // [query|evidence, hidden(order)]
        first[0] = first[0].substring(2, first[0].length()-1);
        String[] queryStr = first[0].split("\\|"); // [query | evidence]
        String[] given = new String[0];
        String[] listOfOrder = first[1].split("-");
        if (queryStr.length > 1) // else, we have no given nodes in this query
            given = queryStr[1].split(",");
        BayesBall.markEvidences(given, vars);
        HashMap<String, Node> queryVariables = new HashMap<String, Node>(); // copy of the hashmap
        queryVariables.putAll(vars); // put all keys and values from the network in the list
        // apply bayes ball and get rid of the nodes which don't affect the query
        Vector<CPT> factors = new Vector<CPT>();
        for (Node q : queryVariables.values()) // linked hashmap of factors
            factors.add(new CPT(queryVariables.get(q.key)));
        sortFactors(factors);
        String toEliminate = "";
        while (!factors.isEmpty()) {
            while (listOfOrder.length > 0) {
                toEliminate = listOfOrder[0];
            }
        }
        // eliminate by using bayes ball unnecessary nodes (dont clear color until create the list)
        // create a list of CPT elements
        // sort the list -> from smallest to biggest (if equals - sort by ASCII value of the key)
        // if a factor size == 1 : remove it
        // Eliminate / Join until find the answer!!!
        // for each eliminate ADD++, for each join MUL++
        // update the answer
        return Math.round(ans*100000.0)/100000.0+","+addOpers+","+mulOpers;
    }

    private static void sortFactors(Vector<CPT> factors) // O(n^2) -> O(nlogn)
    {
        for (int i = 0; i < factors.size(); i++)
            for (int j = i+1; j < factors.size(); j++) {
                if (factors.get(i).tableRows.size() > factors.get(j).tableRows.size())
                    swap(factors, i, j);
                else if (factors.get(i).tableRows.size() == factors.get(j).tableRows.size())
                    sortByASCII(factors, i, j);
            }
    }

    private static void sortByASCII(Vector<CPT> factors, int i, int j)
    {
        int a = 0;
        int b = 0;
        for (String str : factors.get(i).varsNames)
            if (!str.equals("Value"))
                a += str.charAt(0);
        for (String str : factors.get(j).varsNames)
            if (!str.equals("Value"))
                b += str.charAt(0);
        if (a > b)
            swap(factors, i, j);
    }

    private static void swap(Vector<CPT> factors, int i, int j)
    {
        LinkedHashMap<ArrayList<String>, Float> temp = factors.get(i).tableRows;
        LinkedList<String> temp_key = factors.get(i).varsNames;
        factors.get(i).varsNames = factors.get(j).varsNames;
        factors.get(i).tableRows = factors.get(j).tableRows;
        factors.get(j).tableRows = temp;
        factors.get(j).varsNames = temp_key;
    }

    public static void main(String[] args) throws IOException {
        String q = "P(B=T|J=T,M=T) A-E";
        HashMap<String,Node> alarm = myXMLreader.XMLreader("src/alarm_net.xml");
        variable_elimination(q, alarm);
    }
}
