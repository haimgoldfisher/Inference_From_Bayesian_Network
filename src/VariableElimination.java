import java.io.File;
import java.io.IOException;
import java.util.*;

public class VariableElimination {
    public static String variable_elimination(String query, HashMap<String, Node> vars)
    {
        float ans = 0; // the final answer
        int mulOpers = 0; // multiplication operations
        int addOpers = 0; // addition  operations
        String[] first = query.split(" "); // [query|evidence, hidden(order)]
        first[0] = first[0].substring(2, first[0].length()-1);
        String[] queryStr = first[0].split("\\|"); // [query | evidence]
        String[] given = new String[0];
        String[] hidden = first[1].split("-");
        ArrayList<String> hiddenVars = new ArrayList<String>(Arrays.asList(hidden));
        if (queryStr.length > 1) // else, we have no given nodes in this query
            given = queryStr[1].split(",");
        String queryVar = queryStr[0];
        BayesBall.markEvidences(given, vars);
        HashMap<String, Node> queryVariables = new HashMap<String, Node>(); // copy of the hashmap
        queryVariables.putAll(vars); // put all keys and values from the network in the list
        // apply bayes ball and get rid of the nodes which don't affect the query

        // init factors

        Vector<CPT> factors = new Vector<CPT>();
        for (Node q : queryVariables.values()) // linked hashmap of factors
            factors.add(new CPT(queryVariables.get(q.key)));
        // end of init factors

        // eliminated vars by evidence
        // ...
        // end of eliminated vars by evidence
        for (CPT factor : factors)
            for (String evidence : given)
                if (factor.varsNames.contains(evidence.substring(0,1)))
                    factor.eliminateEvidence(evidence);
        sortFactors(factors);

        // hidden while
        String toEliminate = "";
        while (!hiddenVars.isEmpty()) {
            toEliminate = hiddenVars.remove(0);
            for (int i = 0; i < factors.size(); i++)
                if (factors.get(i).varsNames.contains(toEliminate))
                    for (int j = i + 1; j < factors.size(); j++)
                        if (factors.get(j).varsNames.contains(toEliminate) && factors.get(i).varsNames.contains(toEliminate)) {
                            factors.add(factors.get(i).join(factors.get(j), mulOpers, vars, factors));
                            sortFactors(factors);
                            i=0;
                            j=0;
                        }
            removeFactor(factors);
            for (CPT factor : factors)
                if (factor.varsNames.contains(toEliminate))
                    factor.eliminate(toEliminate, addOpers);
        }
            removeFactor(factors);
            sortFactors(factors);

        while (factors.size() > 1){ // query var join
            factors.add(factors.get(0).join(factors.get(1), mulOpers, vars, factors));
        }
        ans = normalize(factors.get(0), queryVar, addOpers);

        return Math.round(ans*100000.0)/100000.0+","+addOpers+","+mulOpers;
    }

    public static float normalize(CPT factor, String query, int addOpers)
    {
        String[] querySplit = query.split("=");
        String queryVar = querySplit[0];
        String outcome = querySplit[1];
        ArrayList<String> key = new ArrayList<String>();
        key.add(outcome);
        float up = 0;
        float down = 0;
        for (Map.Entry<ArrayList<String>, Float> row : factor.tableRows.entrySet()) {
            down += row.getValue();
            addOpers++;
            if (row.getKey().equals(key))
                up = row.getValue();
        }
//        for (float val : factor.tableRows.values()) {
//            down += val;
//            addOpers++;
//        }
        return up/down;
    }

    public static void sortFactors(Vector<CPT> factors) // O(n^2) -> O(nlogn)
    {
        for (int i = 0; i < factors.size(); i++)
            for (int j = i+1; j < factors.size(); j++) {
                if (factors.get(i).tableRows.size() > factors.get(j).tableRows.size())
                    swap(factors, i, j);
                else if (factors.get(i).tableRows.size() == factors.get(j).tableRows.size())
                    sortByASCII(factors, i, j);
            }
    }

    private static void sortByASCII(Vector<CPT> factors, int factorA, int factorB)
    {
        int VarsValues_A = 0;
        int VarsValues_B = 0;
        for (String str : factors.get(factorA).varsNames)
            VarsValues_A += str.charAt(0);
        for (String str : factors.get(factorB).varsNames)
            VarsValues_B += str.charAt(0);
        if (VarsValues_A > VarsValues_B)
            swap(factors, factorA, factorB);
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

    private static void removeFactor(Vector<CPT> factors)
    { // every factor with a size of 1 or less is being removed!
        factors.removeIf(factor -> factor.tableRows.size() < 2);
    }

    public static void main(String[] args) throws IOException {
//        String q = "P(B=T|J=T,M=T) A-E";
//        HashMap<String,Node> alarm = myXMLreader.XMLreader("alarm_net.xml");
//        variable_elimination(q, alarm);
    }
}
