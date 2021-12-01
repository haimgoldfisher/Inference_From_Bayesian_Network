import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CPT {
    /** CPT table as columns and rows. our class will represent a CPT table:
     *  Rows: a linked list of hashmaps: the key is an arraylist with the outcome values.
     *  the value is the double value (last value in the row)
     *  Columns: a linked list of String names, which every String represent a column
     */
    LinkedHashMap<ArrayList<String>,Float> tableRows; // the order of outcomes as key, value as value
    LinkedList<String> varsNames; // the order of the columns

    public CPT(Node n)
    {
        this.tableRows = new LinkedHashMap<ArrayList<String>,Float>();
        this.varsNames = new LinkedList<String>();
        fillCPT(n);
    }

    public CPT(LinkedHashMap<ArrayList<String>,Float> table, LinkedList<String> vars)
    {
        this.tableRows = table;
        this.varsNames = vars;
    }

    private void fillCPT(Node n) // it fills the varsName & the tableRows of the CPT
    {
        this.varsNames.add(n.key); // first col = the node
        for (Node parent : n.parents)
            this.varsNames.add(parent.key); // the parents of the node
        ArrayList<ArrayList<String>> allCombinations = cartesianProd(n);
        int index = 0; // since the size of node's table is equal to allCombinations size:
        for (ArrayList<String> sub : allCombinations) {
            this.tableRows.put(sub, n.table.get(index)); // key = the comb, value = table's value
            index++;
        }
    }

    /** this function creates the key of each row in the CPT table. it creates an arraylist
    of arraylists which is the cartesian product of all outcomes of each variable (var & given vars).
    of course, we will add the outcomes key to each double value by the correct order.
    the order: The variable, then the given variable in REVERSED order (I uploaded them in this way)
    for example: key(arraylist{T,T,F}) -> value({0.95}).
     */
    private ArrayList<ArrayList<String>> cartesianProd(Node curr)
    {
        int cols = this.varsNames.size(); // num of cols = size of varsNames
        ArrayList<ArrayList<String>> myOutcomesSets = new ArrayList<ArrayList<String>>(cols);
        myOutcomesSets.add(curr.outcome);
        int size = curr.outcome.size(); // init num of combs to # outcome values of this node
        for (Node parent: curr.parents) {
            myOutcomesSets.add(parent.outcome);
            size *= parent.outcome.size();
        }
//  the variable # outcome list size * each one of it parents outcome list size = # of combinations
        ArrayList<ArrayList<String>> allKeys = new ArrayList<ArrayList<String>>();
        for(int i = 0; i < size; i++) {
            ArrayList<String> combination = new ArrayList<String>(cols);
            int j = 1;
            for(ArrayList<String> sub : myOutcomesSets) {
                combination.add(sub.get((i / j) % sub.size()));
                j *= sub.size();
            }
            allKeys.add(combination);
        }
        return allKeys;
    }

    public void eliminate2(HashMap<String, Node> vars, String toEliminate, AtomicInteger additionOper) {
        ArrayList<String> resVars = new ArrayList<String>();
        resVars.addAll(this.varsNames);
        LinkedHashMap<ArrayList<String>, Float> resTable = new LinkedHashMap<ArrayList<String>, Float>();
        ArrayList<ArrayList<String>> combs = new ArrayList<ArrayList<String>>();
        for (ArrayList<String> key: this.tableRows.keySet()) {
            ArrayList<String> toAdd = new ArrayList<String>();
            for (int i = 0; i < key.size(); i++)
                toAdd.add(key.get(i));
            combs.add(toAdd);
        }
        int col = 0;
        while (!Objects.equals(this.varsNames.get(col), toEliminate))
            col++;
        resVars.remove(col);
        for (ArrayList<String> key : combs)
            key.remove(col);
        for (int i = 0; i < combs.size(); i++) {
            for (int j = i+1; j < combs.size(); j++) {
                if (combs.get(i).equals(combs.get(j))) {
                    combs.remove(j);
                }
            }
        }
        float res = 0;
        for (ArrayList<String> c : combs) {
            for (String outcome : vars.get(toEliminate).outcome) {
                ArrayList<String> key = new ArrayList<String>();
                for (int i = 0; i < this.varsNames.size(); i++) {
                    if (i < resVars.size() && this.varsNames.get(i).equals(resVars.get(i)))
                        key.add(c.get(i));
                    else
                        key.add(outcome);
                }
                for (Map.Entry<ArrayList<String>, Float> row : this.tableRows.entrySet()) {
                    if (row.getKey().equals(key)) {
                        res += row.getValue();
                        additionOper.addAndGet(1);
                    }
                }
            }
            resTable.put(c, res);
        }
        additionOper.addAndGet(-1);
        this.tableRows = resTable;
        this.varsNames.remove(col);
    }

    public void eliminate(String toEliminate, AtomicInteger additionOper) {
        int eliminateCol = 0;
        for (String name : this.varsNames) {
            if (name.equals(toEliminate))
                break;
            eliminateCol++;
        }
        // merge rows that are the same expect of toEliminate
        LinkedHashMap<ArrayList<String>, Float> copy = new LinkedHashMap<ArrayList<String>, Float>();
        copy.putAll(this.tableRows);
        boolean same = true;
        for (Map.Entry<ArrayList<String>, Float> i : this.tableRows.entrySet()) {
            ArrayList<String> key_i = new ArrayList<String>();
            key_i.addAll(i.getKey());
            key_i.remove(eliminateCol);
            same = true;
            for (Map.Entry<ArrayList<String>, Float> j : this.tableRows.entrySet()) {
                ArrayList<String> key_j = new ArrayList<String>();
                key_j.addAll(j.getKey());
                key_j.remove(eliminateCol);
                if (key_i.equals(key_j))
                    if (!same) { // the first meet with the same key is always the same value - ignore
                        float x = i.getValue();
                        float y = j.getValue();
                        copy.put(i.getKey(), x + y); // add the values of the same key
                        additionOper.addAndGet(1);
                        copy.remove(j.getKey()); // eliminate the other row
                    }
                    else same = false; // after met the same value, the next meeting will be with other
            }
        }
        for (ArrayList<String> key : copy.keySet())
            key.remove(eliminateCol);
        this.tableRows = copy;
        this.varsNames.remove(eliminateCol);
    }

    public void eliminateEvidence(String evidence)
    {
        String[] evid = evidence.split("=");
        String variable = evid[0]; // the evidence variable
        String outcome = evid[1]; // the given outcome of this variable
        int varCol = 0;
        for (String var : this.varsNames) {
            if (var.equals(variable)) // then we found the index of the wanted variable
                break;
            varCol++;
        }
        if (varCol >= this.varsNames.size())
            return; // because the evidence doesn't appear in this table
        LinkedHashMap<ArrayList<String>, Float> resTable = new LinkedHashMap<ArrayList<String>, Float>();
        for (ArrayList<String> key : this.tableRows.keySet()) {
            if (key.get(varCol).equals(outcome)) { // we will keep only rows of the evidence
                ArrayList<String> newKey = new ArrayList<String>(key); // copy of the key
                newKey.remove(varCol); // the key without the removed column
                resTable.put(newKey, this.tableRows.get(key)); // the old value in the new key
            }
        }
        this.varsNames.remove(varCol); // update the columns list
        this.tableRows = resTable; // update the rows of the new factor
    }

    public CPT join(CPT f, AtomicInteger mulOper, HashMap<String, Node> vars, Vector<CPT> factors) // this = the smaller, f = the bigger
    {
        LinkedHashMap<ArrayList<String>, Float> resTable = new LinkedHashMap<ArrayList<String>, Float>();
        LinkedList<String> resVars = new LinkedList<String>();
        // the first is the smaller
        int counter = 0;
        for (String name : this.varsNames)
            for (String name_ot : f.varsNames)
                if (name.equals(name_ot))
                    counter++;
        if (this.varsNames.size() == counter) { // no unique vars
            return joinWithEqual(f, mulOper, vars, factors);
        }
        return joinWithUnique(f, mulOper, vars, factors); // for the other case - unique vars
    }

    private CPT joinWithEqual(CPT f, AtomicInteger mulOper, HashMap<String,Node> vars, Vector<CPT> factors)
    {
        LinkedHashMap<ArrayList<String>, Float> resTable = new LinkedHashMap<ArrayList<String>, Float>();
        LinkedList<String> resVars = new LinkedList<String>();
        resVars.addAll(f.varsNames);
        float x = 0;
        float y = 0;
        for (Map.Entry<ArrayList<String>, Float> dst : f.tableRows.entrySet()) {
            x = 0;
            x += dst.getValue();
            y = findVal(dst.getKey(), this, f.varsNames);
            resTable.put(dst.getKey(), x * y);
            mulOper.addAndGet(1);
        }
        factors.remove(f);
        factors.remove(this);
        return new CPT(resTable, resVars); // the result factor
    }

// maybe this is the original form of JOIN func, and I should delete the rest
    public CPT joinWithUnique(CPT f, AtomicInteger mulOper, HashMap<String,Node> vars, Vector<CPT> factors)
    {
        LinkedHashMap<ArrayList<String>,Float> resTable = new LinkedHashMap<ArrayList<String>,Float>();
        LinkedList<String> resVars = new LinkedList<String>();
        Node merged = new Node(f.varsNames.get(0));
        merged.outcome = vars.get(merged.key).outcome;
        for (String name : f.varsNames)
            if (!Objects.equals(name, merged.key))
                merged.parents.add(vars.get(name));
        for (String name : this.varsNames)
            if (!merged.parents.contains(vars.get(name)) && !Objects.equals(name, merged.key) )
                merged.parents.add(vars.get(name));
        resVars.add(merged.key);
        for (Node par : merged.parents)
            resVars.add(par.key);
        ArrayList<ArrayList<String>> allCombinations = cartesianProd(merged);
//  we will add every new comb to the merged factor and will add the correct value to it by a func
        for (ArrayList<String> sub : allCombinations)
            resTable.put(sub, findCurrectVal(sub,f,resVars,mulOper));
        factors.remove(f);
        factors.remove(this);
        return new CPT(resTable, resVars);
    }

    /**
     * the purpose of this inner function is to find the correct key from each former CPT,
     * to save it as a float vat and then to multiply them by each other, in order to return
     * a correct value to the given key
     * @param sub - the combination of outcome which we need to find a correct value to it
     * @param other - the second CPT which we operate the joining with it
     * @param mergedVars - the columns list of the merged factor
     * @param mulOper - the amount of multiplication operation which we did so far
     * @return the correct value of the given sub key in the merged factor
     */

    private float findCurrectVal(ArrayList<String> sub, CPT other, LinkedList<String> mergedVars, AtomicInteger mulOper)
    {
        float x = findVal(sub, other, mergedVars);
        float y = findVal(sub, this, mergedVars);
        mulOper.addAndGet(1); // since we use a multiplication operation
        return x*y;
    }

    private float findVal(ArrayList<String> sub, CPT src, LinkedList<String> mergedVars)
    { // this func add to the current key the relevant outcomes comb from the sub key.
      // then, it turns to the wanted CPT and by using the key - it gets the correct value
        ArrayList<String> key = new ArrayList<String>();
        int index = 0; // so we can take the correct outcome from the sub key
        for (String var : src.varsNames) {
            index = 0; // init the index to 0 every new src outcome
            for (String mergedVar : mergedVars) {
                if (var.equals(mergedVar)) {
                    key.add(sub.get(index));
                    break;
                }
                index++;
            }
        }
        // now, we have the wanted key, we can take its value from the former CPT
        for (Map.Entry<ArrayList<String>, Float> row : src.tableRows.entrySet()) {
            if (row.getKey().equals(key))
                return row.getValue();
        }
        return src.tableRows.get(key);
    }
}