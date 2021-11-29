import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class myXMLreader {

    public static HashMap<String, Node> XMLreader(String path) throws FileNotFoundException, IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader("src/"+path));
        String currLine; // for each line of the XML file
        if (!Objects.equals(currLine = reader.readLine(), "<NETWORK>")) {
            throw new IllegalArgumentException(); // the first line must be in format
        }
        ArrayList <Node> vars = new ArrayList<Node>();
        HashMap<String, Node> varsMap = new HashMap<String, Node>();
        String toAdd = "";
        int start, end;
        start = end = 0;
        while ((currLine = reader.readLine()) != null) {
            if (currLine.startsWith("<VARIABLE>")) {
                Node newNode = new Node(toAdd); // we have a new node - the variable
                while (!currLine.contains("</VARIABLE>")) {
                    if (currLine.startsWith("\t<NAME>")) { // should happen only once - var has one name
                        start = 7; // without <NAME>
                        end = currLine.length() - 7; // without </NAME>
                        toAdd = currLine.substring(start, end);
                        newNode.key = toAdd; // update the name of the node
                        varsMap.put(toAdd, newNode);
//                        vars.add(newNode);
                    }
                    if (currLine.startsWith("\t<OUTCOME>")) {
                        start = 10; // without <OUTCOME>
                        end = currLine.length() - 10; // without </OUTCOME>
                        toAdd = currLine.substring(start, end);
                        newNode.outcome.add(toAdd); // add this outcome value to the outcome list of the node
                    }
                    currLine = reader.readLine(); // move to the next line
                }
            }
            if (currLine.startsWith("<DEFINITION>")) {
                String name = "";
                Node currNode = null;
                while (!currLine.contains("</DEFINITION>")) {
                    if (currLine.startsWith("\t<FOR>")) {
                        start = 6;
                        end = currLine.length() - 6;
                        name = currLine.substring(start, end); // update the name of the node
//                        currNode = searchNode(vars, name);
                        currNode = varsMap.get(name);
                    }
                    if (currLine.startsWith("\t<GIVEN>")) {
                        start = 8;
                        end = currLine.length() - 8;
                        toAdd = currLine.substring(start, end);
//                        Node parent = searchNode(vars, toAdd); // catch the parent
                        Node parent = varsMap.get(toAdd);
                        if (parent != null)
                            parent.next.add(currNode); // and add the curr node as his child
                        if (currNode != null)
                            currNode.parents.add(parent); // add the parent as the curr's parent
                    }
                    if (currLine.startsWith("\t<TABLE>")) {
                        start = 8;
                        end = currLine.length() - 8;
                        toAdd = currLine.substring(start, end);
                        addTableValues(currNode, toAdd);
                    }
                    currLine = reader.readLine(); // move to the next line
                }
                if (currNode != null)
                    currNode.parents = reverseGiven(currNode.parents); // for the CPT
            }
            if (currLine.contains("</NETWORK>")) { // must be the last line of the XML
                reader.close();
                return varsMap;
            }
        }
        //reader.close();
        throw new IOException(); // if the last row isn't "</NETWORK>"
    }

    private static ArrayList<Node> reverseGiven(ArrayList<Node> parents)
    { // a simple func to reverse the order of the parents array (for CPT)
        ArrayList<Node> res = new ArrayList<Node>();
        for (int i = parents.size()-1; i > -1; i--)
            res.add(parents.get(i));
        return res;
    }

    private static void addTableValues(Node currNode, String toAdd)
    { // a simple inner function to add the table values into the node's table
        for (String value : toAdd.split(" ")) {
            currNode.table.add(Float.parseFloat(value)); // casting from String into double values
        }
    }

    public static void main(String[] args) throws IOException {
        HashMap<String, Node> vars = XMLreader("src/alarm_net.xml");
        //System.out.println(searchNode(vars, "M").next.size());
        //System.out.println(searchNode(vars, "A").next.get(1).key);
        for (Map.Entry<String, Node> a : vars.entrySet())
            System.out.println(a.getValue());
    }
}