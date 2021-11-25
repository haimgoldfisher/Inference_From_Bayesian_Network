import java.util.*;

public class Node {
    final static int UNCOLORED = 0, COLORED = 1;
    final static int UNVISITED = 0, VISIT_FROM_CHILD = 1, VISIT_FROM_PARENT = 2;
    String key; // the name of the node
    int color; // color - it checks if we already checked this node at searching
    int visit; //
//  double[][] of P(X) values
    ArrayList<Node> parents; // a pointer to the parent of the node
    ArrayList<Node> next; // array list of the children of the node
    ArrayList<String> outcome;
    ArrayList<Double> table;

    public Node(String key)
    {
        this.key = key;
        this.color = UNCOLORED;
        this.visit = UNVISITED;
        this.parents = new ArrayList<Node>();
        this.next = new ArrayList<Node>();
        this.outcome = new ArrayList<String>();
        this.table = new ArrayList<Double>();
    }
    public Node addNode(String key)
    {
        Node n = new Node(key);
        this.next.add(n);
        n.parents.add(this);
        return n;
    }

    public Node bfs(String toFind)
    {
        Queue<Node> Q = new LinkedList<Node>(); // bfs algo works on queue
        Node v = null;
        this.color = COLORED;
        Q.add(this);
        while (!Q.isEmpty()) {
            v = Q.peek();
            if (Objects.equals(v.key, toFind))
                return v;
            if (v.hasChild())
                for (Node target : v.next) { // all the nodes that this node is their parent
                    if (target.color == UNCOLORED) { // since we check only unexplored nodes
                        target.color = COLORED;
                        Q.add(target);
                    }
                }
            Q.remove();
        }
        return null;
    }

    public Node INDsearch(String target, HashMap<String,Node> vars){
        Queue<Node> toVisit = new LinkedList<Node>();
        Node v = this;
        this.visit = VISIT_FROM_CHILD;
        toVisit.add(v);
        while (!toVisit.isEmpty()){
            v = toVisit.remove();
            if (Objects.equals(v.key, target))
                return vars.get(target);
            if (v.color == UNCOLORED && v.visit == VISIT_FROM_CHILD){
                if (v.hasChild())
                    for (Node child : v.next)
                        if (child.visit == UNVISITED) {
                            child.visit = VISIT_FROM_PARENT;
                            toVisit.add(child);
                        }
                if (v.hasParent())
                    for (Node parent : v.parents)
                        if (parent.visit == UNVISITED){
                            parent.visit = VISIT_FROM_CHILD;
                            toVisit.add(parent);
                        }
            }
            else if (v.color == UNCOLORED && v.visit == VISIT_FROM_PARENT){
                if (v.hasChild())
                    for (Node child : v.next)
                        if (child.visit != VISIT_FROM_PARENT) {
                            child.visit = VISIT_FROM_PARENT;
                            toVisit.add(child);
                        }
            }
            else if (v.color == COLORED && v.visit == VISIT_FROM_PARENT){
                if (v.hasParent())
                    for (Node parent : v.parents)
                        if (parent.visit != VISIT_FROM_CHILD){
                            parent.visit = VISIT_FROM_CHILD;
                            toVisit.add(parent);
                        }
            }
        }
        return null;
    }

    public Node dfs(String toFind)
    {
        Stack<Node> S = new Stack<Node>();
        Node v = null;
        S.push(this);
        while (!S.isEmpty()){
            v = S.pop();
            if (v.key.equals(toFind))
                return v;
            if (v.color != COLORED){
                v.color = COLORED;
                for (Node w : v.next)
                    S.push(w);
            }
        }
        return null;
    }

    private boolean hasParent()
    {
        return this.parents.size() > 0;
    }

    private boolean hasChild()
    {
        return this.next.size() > 0;
    }

    public String outcomeSearch(String str)
    {
        for (String s : this.outcome) {
            if (s.equals(str))
                return s;
        }
        return str;
    }
}
