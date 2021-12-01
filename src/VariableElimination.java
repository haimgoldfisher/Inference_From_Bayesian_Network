import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VariableElimination {
    public static String variable_elimination(String query, HashMap<String, Node> vars)
    {
        float ans = 0; // the final answer
//        int mulOpers = 0; // multiplication operations
//        int addOpers = 0; // addition operations
        AtomicInteger mulOpers = new AtomicInteger(0); // multiplication operations
        AtomicInteger addOpers = new AtomicInteger(0); // addition operations
        String[] first = query.split(" "); // [query|evidence, hidden(order)]
        first[0] = first[0].substring(2, first[0].length()-1);
        String[] queryStr = first[0].split("\\|"); // [query | evidence]
        String[] given = new String[0];
        String[] hidden = first[1].split("-");
        ArrayList<String> hiddenVars = new ArrayList<String>(Arrays.asList(hidden));
        if (queryStr.length > 1) // else, we have no given nodes in this query
            given = queryStr[1].split(",");
        String queryVar = queryStr[0];
        HashMap<String, Node> allVariables = new HashMap<String, Node>(); // copy of the hashmap
        allVariables.putAll(vars); // put all keys and values from the network in the list

        Vector<CPT> factors = new Vector<CPT>();
        for (Node q : allVariables.values()) // linked hashmap of factors
            factors.add(new CPT(allVariables.get(q.key)));

        for (CPT factor : factors)
            for (String evidence : given)
                if (factor.varsNames.contains(evidence.split("=")[0]))
                    factor.eliminateEvidence(evidence);
        sortFactors(factors);

        ArrayList<String> querEvid = new ArrayList<String>();
        for (String name : vars.keySet()) {
            if (!hiddenVars.contains(name) && !querEvid.contains(name))
                querEvid.add(name);
        }
        ArrayList<String> hiddenToRemove = new ArrayList<String>();
        for (int i = 0; i < hiddenVars.size(); i++ ){
            int counter = 0;
            for(String qeurOrEviVar : querEvid) {
                BayesBall.resetVars(vars);
                if (vars.get(hiddenVars.get(i)).bfs(qeurOrEviVar) == null)
                    counter++;
                if (counter == querEvid.size()) {
                    hiddenToRemove.add(hiddenVars.get(i));
                    hiddenVars.remove((hiddenVars.get(i)));
                    counter = 0;
                }
            }
        }
        for (int i = 0; i < hiddenVars.size(); i++ ){
            BayesBall.resetVars(vars);
            if (BayesBall.independence(vars.get(hiddenVars.get(i)), vars.get(queryVar.split("=")[0]), given, vars)){
                hiddenToRemove.add(hiddenVars.get(i));
                hiddenVars.remove((hiddenVars.get(i)));
                i = 0;
            }
        }

        for (String evid : hiddenToRemove){
            for (int i = 0; i < factors.size(); i++)
                if (factors.get(i).varsNames.contains(evid)){
                    factors.remove(i);
                    i = 0;
                }
        }
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
        while (factors.get(0).varsNames.size()>1) {
            for (int i = 0; i < factors.get(0).varsNames.size(); i++)
                if (!Objects.equals(factors.get(0).varsNames.get(i), queryVar.split("=")[0])) {
                    factors.get(0).eliminate(factors.get(0).varsNames.get(i), addOpers);
                    i = 0;
                }
        }

        ans = normalize(factors.get(0), queryVar, addOpers);
        return Math.round(ans*100000.0)/100000.0+","+addOpers+","+mulOpers;
    }

    public static float normalize(CPT factor, String query, AtomicInteger addOpers)
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
            addOpers.addAndGet(1);
            if (row.getKey().equals(key))
                up = row.getValue();
        }
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
