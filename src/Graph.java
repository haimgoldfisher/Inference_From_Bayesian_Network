import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class Graph {
    Node root;
    final int UNEXPLORED = 0, EXPLORED = 1;

    public Node bfs(Graph G, String toFind)
    {
        final int UNEXPLORED = 0, EXPLORED = 1;
        Queue<Node> Q = new LinkedList<Node>(); // bfs algo works on queue
        G.root.visit = EXPLORED;
        Q.add(G.root);
        while (!Q.isEmpty()){
            Node v = Q.remove();
            if (Objects.equals(v.key, toFind))
                return v;
            for (Node target : v.next) { // all the nodes that this node is their parent
                if (target.visit == UNEXPLORED) { // since we check only unexplored nodes
                    target.visit = EXPLORED;
                    Q.add(target);
                }
            }
        }
        return null;
    }
}
