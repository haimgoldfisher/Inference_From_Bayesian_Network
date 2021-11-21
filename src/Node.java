import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class Node {
    final int UNEXPLORED = 0, EXPLORED = 1;
    String key; // the name of the node
    int visit; // color - it checks if we already checked this node at searching
//  double[][] of P(X) values
    ArrayList<Node> parents; // a pointer to the parent of the node
    ArrayList<Node> next; // array list of the children of the node
    ArrayList<String> outcome;
    ArrayList<Double> table;

    public Node(String key)
    {
        this.key = key;
        this.visit = 0;
        this.parents = new ArrayList<Node>();
        this.next = new ArrayList<Node>();
        this.outcome = new ArrayList<String>();
        this.table = new ArrayList<Double>();
    }
    public void addNode(String key)
    {
        Node n = new Node(key);
        this.next.add(n);
        n.parents.add(this);
    }

    public String bfs(String toFind)
    {
        Queue<Node> Q = new LinkedList<Node>(); // bfs algo works on queue
        Node v = null;
        this.visit = EXPLORED;
        Q.add(this);
        while (!Q.isEmpty()) {
            v = Q.peek();
            if (Objects.equals(v.key, toFind))
                return v.key;
            for (Node target : v.next) { // all the nodes that this node is their parent
                if (target.visit == UNEXPLORED) { // since we check only unexplored nodes
                    target.visit = EXPLORED;
                    Q.add(target);
                }
            }
            Q.remove();
        }
        return null;
    }
}
