import java.util.*;

/** Node Class: every variable of our Bayesian Network is implemented as a Node with the above
 *  attributes - key(name), color, visit, parents, children, outcome, table and CPT.
 *  Explanations for each of the features can be seen next to each of them.
 */

public class Node {
    final static int UNCOLORED = 0, COLORED = 1;
    final static int UNVISITED = 0, VISIT_FROM_CHILD = 1, VISIT_FROM_PARENT = 2;
    String key; // the name of the node
    int color; // color - for BFS searching algo & evidence marking for Bayes Ball algo
    int visit; // for Bayes Ball algo
    ArrayList<Node> parents; // array list of pointers to the parents of the node
    ArrayList<Node> children; // array list of pointers to the children of the node
    ArrayList<String> outcome; // outcomes of the node
    ArrayList<Float> table; // table values of the node
    CPT cpt; // the CPT of the node (key, parents, )

    public Node(String key) // constructor
    {
        this.key = key;
        this.color = UNCOLORED; // for evidence
        this.visit = UNVISITED; // for search
        this.parents = new ArrayList<Node>();
        this.children = new ArrayList<Node>();
        this.outcome = new ArrayList<String>();
        this.table = new ArrayList<Float>();
        this.cpt = new CPT(this);
    }

    public Node bfs(String toFind) // a BFS algo implementation (search algo) - on CHILDREN only
    {
        Queue<Node> Q = new LinkedList<Node>(); // bfs algo works on a queue
        Node v = null;
        this.color = COLORED; // mark this source node as discovered
        Q.add(this); // add the source node to the queue
        while (!Q.isEmpty()) {
            v = Q.remove(); // remove & return the first node in the queue
            if (Objects.equals(v.key, toFind))
                return v; // when find the desired node - return it!
            if (v.hasChild())
                for (Node target : v.children) { // all the nodes that this node is their parent
                    if (target.color == UNCOLORED) { // since we check only UNEXPLORED nodes
                        target.color = COLORED; // every new node we discover change it color to COLORED
                        Q.add(target); // add the discovered node to the queue
                    }
                }
        }
        return null; // when couldn't find the desired node - return NULL
    }

    boolean hasParent()
    { // it returns if a node has a parent
        return this.parents.size() > 0;
    }

    boolean hasChild()
    { // it returns if a node has a child
        return this.children.size() > 0;
    }
}