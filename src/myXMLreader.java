import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;


public class myXMLreader {

    public static ArrayList<Node> XMLreader(String path) throws FileNotFoundException, IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String currLine; // for each line of the XML file
        if (!Objects.equals(currLine = reader.readLine(), "<NETWORK>")) {
            throw new IllegalArgumentException(); // the first line must be in format
        }
        ArrayList <Node> vars = new ArrayList<Node>();
        //HashMap<String, Node> varsMap = new HashMap<String, Node>(); instead of arrayList (O(1))
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
                        vars.add(newNode);
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
                        currNode = searchNode(vars, name);
                    }
                    if (currLine.startsWith("\t<GIVEN>")) {
                        start = 8;
                        end = currLine.length() - 8;
                        toAdd = currLine.substring(start, end);
                        Node parent = searchNode(vars, toAdd); // catch the parent
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
            }
            if (currLine.contains("</NETWORK>")) { // must be the last line of the XML
                reader.close();
                return vars;
            }
        }
        //reader.close();
        throw new IOException(); // if the last row isn't "</NETWORK>"
    }

    public static Node searchNode(ArrayList<Node> vars, String name)
    { // a simple function to search a node in a variables array list - by its name
        for (Node var : vars) {
            if (var.key.equals(name))
                return var;
        }
        return null; // if didn't find the desired node's name - return null
    }

    private static void addTableValues(Node currNode, String toAdd)
    { // a simple inner function to add the table values into the node's table
        for (String value : toAdd.split(" ")) {
            currNode.table.add(Double.parseDouble(value)); // casting from String into double values
        }
    }

    public static void main(String[] args) throws IOException {
        ArrayList <Node> vars = XMLreader("src/alarm_net.xml");
        System.out.println(searchNode(vars, "M").next.size());
        //System.out.println(searchNode(vars, "A").next.get(1).key);
    }
}