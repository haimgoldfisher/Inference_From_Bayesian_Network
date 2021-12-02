import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/** myXMLreader Class: The purpose of this class is to read the XML file, that represents the
 *  Bayesian Network, as required. It works by reading line by line. It builds the network
 *  (represented by an HashMap) by matching each variable to its node. If it finds an error
 *  in the given line it will stop the reading, throw an exception and close the XML file.
 */

public class myXMLreader {

    public static HashMap<String, Node> XMLreader(String path) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader("src/"+path));
        String currLine; // for each line of the XML file
        if (!Objects.equals(currLine = reader.readLine(), "<NETWORK>")) {
            throw new IllegalArgumentException(); // the first line must be in format
        }
        HashMap<String, Node> varsMap = new HashMap<String, Node>(); // will contain our network nodes
        String toAdd = ""; // each line has a string which we will add to a node
        int start, end; // for substring of each line of the xml file
        start = end = 0;
        while ((currLine = reader.readLine()) != null) {
            if (currLine.startsWith("<VARIABLE>")) {
                Node newNode = new Node(toAdd); // we have a new node - the variable
                while (!currLine.contains("</VARIABLE>")) {
                    if (currLine.startsWith("\t<NAME>")) { // happen only once, var has one name
                        start = 7; // without <NAME>
                        end = currLine.length() - 7; // without </NAME>
                        toAdd = currLine.substring(start, end);
                        newNode.key = toAdd; // update the name of the node
                        varsMap.put(toAdd, newNode);
                    }
                    else if (currLine.startsWith("\t<OUTCOME>")) { // outcome list of the variable
                        start = 10; // without <OUTCOME>
                        end = currLine.length() - 10; // without </OUTCOME>
                        toAdd = currLine.substring(start, end);
                        newNode.outcome.add(toAdd); // add this outcome value to the outcome list of the node
                    }
                    else {

                    }
                    currLine = reader.readLine(); // move to the children line
                }
            }
            else if (currLine.startsWith("<DEFINITION>")) { // definition for variables (parents + table)
                String name = "";
                Node currNode = null;
                while (!currLine.contains("</DEFINITION>")) {
                    if (currLine.startsWith("\t<FOR>")) {
                        start = 6;
                        end = currLine.length() - 6;
                        name = currLine.substring(start, end); // update the name of the node
                        currNode = varsMap.get(name);
                    }
                    else if (currLine.startsWith("\t<GIVEN>")) { // parents for the variable
                        start = 8;
                        end = currLine.length() - 8;
                        toAdd = currLine.substring(start, end);
                        Node parent = varsMap.get(toAdd);
                        if (parent != null)
                            parent.children.add(currNode); // and add the curr node as his child
                        if (currNode != null)
                            currNode.parents.add(parent); // add the parent as the curr's parent
                    }
                    else if (currLine.startsWith("\t<TABLE>")) {
                        start = 8;
                        end = currLine.length() - 8;
                        toAdd = currLine.substring(start, end);
                        addTableValues(currNode, toAdd);
                    }
                    currLine = reader.readLine(); // move to the children line
                }
                if (currNode != null)
                    currNode.parents = reverseGiven(currNode.parents); // for the CPT
            }
            else if (currLine.contains("</NETWORK>")) { // must be the last line of the XML
                reader.close();
                return varsMap;
            }
        }
        reader.close();
        throw new IOException(); // if the last row isn't "</NETWORK>" or illegal row
    }

    private static ArrayList<Node> reverseGiven(ArrayList<Node> parents)
    { // a simple func to reverse the order of the parents array (for CPT using)
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
}