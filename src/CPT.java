import java.io.IOException;
import java.util.*;

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

    private void fillCPT(Node n) // it fills the varsName & the tableRows of the CPT
    {
        this.varsNames.add(n.key); // first col = the node
        for (Node parent : n.parents)
            this.varsNames.add(parent.key); // the parents of the node
        this.varsNames.add("Value"); // last col = the value of P(X|Y_1,...,Y_n)
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
        int cols = this.varsNames.size()-1; // num of cols = size of varsNames - values col
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

    public CPT join(CPT f)
    {
        return this;
    }

    public void eliminate(String toEliminate, int additionOper) {
        int eliminateCol = 0;
        for (String name : this.varsNames)
            if (name.equals(toEliminate))
                break;
        eliminateCol++;
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
                        additionOper++;
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

    public static void main(String[] args) throws IOException {
        String q = "P(B=T|J=T,M=T) A-E";
        HashMap<String, Node> alarm = myXMLreader.XMLreader("src/alarm_net.xml");
        alarm.get("A").cpt = new CPT(alarm.get("A"));
        System.out.println(alarm.get("A").cpt.varsNames);
        System.out.println(alarm.get("A").cpt.tableRows.keySet());
        alarm.get("A").cpt.eliminate("B", 0);
        System.out.println(alarm.get("A").cpt.varsNames);
        System.out.println(alarm.get("A").cpt.tableRows.keySet());
        int same = 0;
        System.out.println(alarm.get("A").cpt.tableRows.entrySet());
        for (Map.Entry<ArrayList<String>, Float> a : alarm.get("A").cpt.tableRows.entrySet()) {
            System.out.println(a.getKey());
            System.out.println(a.getValue());
        }
    }
    }
