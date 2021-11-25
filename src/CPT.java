import java.util.HashMap;
import java.util.LinkedList;

public class CPT {
    LinkedList<HashMap<String, Double>> table;

    public CPT(HashMap<String,Node> vars)
    {
        this.table = null;
    }
    public CPT join(CPT f)
    {
        return this;
    }
    public CPT eliminate(String name)
    {
        return this;
    }
}
