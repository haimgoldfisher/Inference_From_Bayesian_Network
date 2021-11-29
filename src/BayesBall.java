import java.io.IOException;
import java.util.*;

public class BayesBall {
    public static boolean bayes_ball(String query, HashMap<String, Node> vars)
    {
        String[] first = query.split("\\|"); // split the query from the given nodes
        //System.out.println(Arrays.toString(first));
        String[] ind = first[0].split("-"); // first the two elements of the query
        String[] given = new String[0];
        if (first.length > 1) // else, we have no given nodes in this query
            given = first[1].split(","); // we can have more than one given
        return independence(vars.get(ind[0]), vars.get(ind[1]), given, vars); // after filtering
    }

    private static boolean independence(Node source, Node destination, String[] given, HashMap<String,Node> vars)
    {
        if (given.length > 0) // else, we have no evidence nodes
            markEvidences(given, vars); // an inner function to mark all evidence nodes as colored
        if (INDsearch(vars.get(source.key), destination.key, vars) == destination)
            return false;
        return true;
    }

    /**
     This is the Main function of Bayes Ball ALGO. It depends on the algo which we have learned
     with BFS Algo implementation on Graph of nodes. each node has VISIT and COLOR parameters,
     so the algo checks the VISIT & COLOR of the node and add the relevant nodes th the queue.
     Notice that we have already marked the evidence nodes (= COLORED).
     @param source - the source node.
     @param target - the target node.
     @param vars - our Bayesian Network.
     @return the target if the algo could reach it from the source node. Else, return NULL
     */
    public static Node INDsearch(Node source, String target, HashMap<String,Node> vars)
    {
        Queue<Node> toVisit = new LinkedList<Node>();
        Node curr = source;
        source.visit = Node.VISIT_FROM_CHILD;
        toVisit.add(curr);
        while (!toVisit.isEmpty()) { // stop condition - the queue is empty
            curr = toVisit.remove(); // point at the top of the queue and remove it
            if (Objects.equals(curr.key, target))
                return vars.get(target); // the Algo had reached to the node, return it!
            if (curr.color == Node.UNCOLORED && curr.visit == Node.VISIT_FROM_CHILD) { // case 1
                if (curr.hasChild())
                    for (Node child : curr.next)
                        if (child.visit == Node.UNVISITED) {
                            child.visit = Node.VISIT_FROM_PARENT;
                            toVisit.add(child);
                        }
                if (curr.hasParent())
                    for (Node parent : curr.parents)
                        if (parent.visit == Node.UNVISITED){
                            parent.visit = Node.VISIT_FROM_CHILD;
                            toVisit.add(parent);
                        }
            }
            else if (curr.color == Node.UNCOLORED && curr.visit == Node.VISIT_FROM_PARENT) { // case 2
                if (curr.hasChild())
                    for (Node child : curr.next)
                        if (child.visit != Node.VISIT_FROM_PARENT) {
                            child.visit = Node.VISIT_FROM_PARENT;
                            toVisit.add(child);
                        }
            }
            else if (curr.color == Node.COLORED && curr.visit == Node.VISIT_FROM_PARENT) { // case 3
                if (curr.hasParent())
                    for (Node parent : curr.parents)
                        if (parent.visit != Node.VISIT_FROM_CHILD){
                            parent.visit = Node.VISIT_FROM_CHILD;
                            toVisit.add(parent);
                        }
            }
            // case 4: (curr.color == COLORED && curr.visit == VISIT_FROM_CHILD) - DO NOTHING!
        }
        return null; // the Algo couldn't reach the node - return NULL
    }

    public static void markEvidences(String[] given, HashMap<String,Node> vars)
    {
        String[] givenDiv = new String[0];
        for (String s : given) {
            givenDiv = s.split("=");
            if (vars.get(givenDiv[0]) != null) {
                vars.get(givenDiv[0]).color = Node.COLORED;
            } // mark every evidence node as colored
        }
    }

    public static void resetVars(HashMap <String,Node> myNetwork)
    { // a simple function that restart the color & visit params of the network
        for (Map.Entry<String, Node> val : myNetwork.entrySet()) {
            val.getValue().color = Node.UNCOLORED;
            val.getValue().visit = Node.UNVISITED;
        }
    }
}
