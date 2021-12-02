import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VariableElimination {
    public static String variable_elimination(String query, HashMap<String, Node> vars)
    {
        float ans = 0; // the final answer
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

        // Init Factors:

        Vector<CPT> factors = new Vector<CPT>();
        for (Node q : allVariables.values()) // linked hashmap of factors
            factors.add(new CPT(allVariables.get(q.key)));

        // Evidence Outcomes Update:

        for (CPT factor : factors)
            for (String evidence : given)
                if (factor.varsNames.contains(evidence.split("=")[0]))
                    factor.eliminateEvidence(evidence);

        // at this moment, the factors contain only query & hidden vars

//  Unnecessary Factors Elimination:

        ArrayList<String> querEvid = new ArrayList<String>();
        for (String name : vars.keySet()) {
            if (!hiddenVars.contains(name) && !querEvid.contains(name))
                querEvid.add(name);
        }

        // Quick Answer without Join/Elimination and Normalization case checking:

        if (querEvid.size()==1)
            return quickAnswer(factors, querEvid, queryVar, addOpers, mulOpers);

        // Eliminate Factors of h Vars Which q\e Vars Aren't Ancestor of Them

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

        // Eliminate Factors with h Vars Which Are Independent in Query:

        for (int i = 0; i < hiddenVars.size(); i++ ){
            BayesBall.resetVars(vars);
            if (BayesBall.independence(vars.get(hiddenVars.get(i)), vars.get(queryVar.split("=")[0]), given, vars)){
                hiddenToRemove.add(hiddenVars.get(i));
                hiddenVars.remove((hiddenVars.get(i)));
                i = 0;
            }
        }

        for (String hid : hiddenToRemove){
            for (int i = 0; i < factors.size(); i++)
                if (factors.get(i).varsNames.contains(hid)){
                    factors.remove(i);
                    i = 0;
                }
        }
        sortFactors(factors);

//  Hidden Vars while until no more Hidden vars in the factors:

        String toEliminate = "";
        while (!hiddenVars.isEmpty())
        {
            toEliminate = hiddenVars.remove(0);
//  join on each hidden var - until only 1 factor contain it, then eliminate it from that factor:
            for (int i = 0; i < factors.size(); i++)
                if (factors.get(i).varsNames.contains(toEliminate))
                    for (int j = i + 1; j < factors.size(); j++)
                        if (factors.get(j).varsNames.contains(toEliminate) && factors.get(i).varsNames.contains(toEliminate)) {
                            factors.add(factors.get(i).join(factors.get(j), mulOpers, vars, factors));
                            sortFactors(factors);
                            i=0;
                            j=0;
                        }
            for (int i = 0; i < factors.size(); i++) // eliminate the curr hidden var from its factor:
                if (factors.get(i).varsNames.contains(toEliminate)) {
                    factors.get(i).eliminate(toEliminate, addOpers);
                    removeFactor(factors);
                    i = 0;
                }
        }
    // at this moment, we have only factor(s) with the query variable

//  Join on Query:

        while (factors.size() > 1)
            factors.add(factors.get(0).join(factors.get(1), mulOpers, vars, factors));

//  Normalization & Answer Returning:

        ans = normalize(factors.get(0), queryVar, addOpers);
        return Math.round(ans*100000.0)/100000.0+","+addOpers+","+mulOpers;
    }

    private static String quickAnswer(Vector<CPT> factors, ArrayList<String> querEvid, String queryVar, AtomicInteger addOpers, AtomicInteger mulOpers)
    { // the func returns the answer of the query when the answer is already given to us
        float ans = 0;
        for (CPT factor : factors)
            if (factor.varsNames.size()==1 && factor.varsNames.contains(querEvid.get(0)))
                ans = ansWithoutNormalize(factor, queryVar);
        return Math.round(ans*100000.0)/100000.0+","+addOpers+","+mulOpers;
    }

    private static float normalize(CPT factor, String query, AtomicInteger addOpers)
    {
        String[] querySplit = query.split("=");
        String outcome = querySplit[1];
        ArrayList<String> key = new ArrayList<String>();
        key.add(outcome);
        float up = 0;
        float down = 0;
        for (Map.Entry<ArrayList<String>, Float> row : factor.tableRows.entrySet()) {
            if (down == 0)
                down = row.getValue(); // to avoid addition operation
            else {
                down += row.getValue();
                addOpers.addAndGet(1);
            }
            if (row.getKey().equals(key))
                up = row.getValue();
        }
        return up/down;
    }

    private static float ansWithoutNormalize(CPT factor, String query)
    { // answer calculation when normalization is no needed
        String[] querySplit = query.split("=");
        String outcome = querySplit[1];
        ArrayList<String> key = new ArrayList<String>();
        key.add(outcome);
        float res = 0;
        for (Map.Entry<ArrayList<String>, Float> row : factor.tableRows.entrySet()) {
            if (row.getKey().equals(key)) {
                res = row.getValue();
                break;
            }
        }
        return res;
    }

    static void sortFactors(Vector<CPT> factors)
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
    { // when two factors are equal - sort them by ASCII values of the vars
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
    { // a simple func to swap between 2 factors in the Factors Vector:
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
}