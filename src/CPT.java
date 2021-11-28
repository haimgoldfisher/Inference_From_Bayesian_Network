import java.util.*;

public class CPT {
    /** CPT table as columns and rows. our class will represent a CPT table:
     *  Rows: a linked list of hashmaps: the key is an arraylist with the outcome values.
     *  the value is the double value (last value in the row)
     *  Columns: a linked list of String names, which every String represent a column
     */
    LinkedHashMap<ArrayList<String>,Double> tableRows; // the order of outcomes as key, value as value
    LinkedList<String> varsNames; // the order of the columns

    public CPT(Node n)
    {
        this.tableRows = new LinkedHashMap<ArrayList<String>,Double>();
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
    of arraylists which is the cartesian product of all outcomes of each variable (name + given).
    of course, we will add the outcomes key to each double value by the correct order.
     */
    private ArrayList<ArrayList<String>> cartesianProd(Node curr) {
        int cols = this.varsNames.size()-1; // num of cols = size of varsNames - values col
        ArrayList<ArrayList<String>> myOutcomesSets = new ArrayList<ArrayList<String>>(cols);
        myOutcomesSets.add(curr.outcome);
        int size = curr.outcome.size(); // init num of combs to # outcome values of this node
        for (Node parent: curr.parents) {
            myOutcomesSets.add(parent.outcome);
            size *= parent.outcome.size();
        }
//  the variable # outcome list size * each one of it parents outcome list size = # of combinations
        ArrayList<ArrayList<String>> allCombinations = new ArrayList<ArrayList<String>>();
        for(int i = 0; i < size; i++) {
            ArrayList<String> combination = new ArrayList<String>(cols);
            int j = 1;
            for(ArrayList<String> sub : myOutcomesSets) {
                combination.add(sub.get((i / j) % sub.size()));
                j *= sub.size();
            }
            allCombinations.add(combination);
        }
        return allCombinations;
    }

    public CPT join(CPT f)
    {
        return this;
    }
    public CPT eliminate(String name)
    {
        return this;
    }

    public static void main(String[] args)
    {

    }
}
