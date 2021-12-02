import java.io.*;
import java.util.HashMap;
public class Ex1 {
    /**
     *  Main Class
     */
    public static void main(String[] args) throws IOException
    {
        String strFromUser = "input.txt";
        BufferedWriter output = new BufferedWriter(new FileWriter("output.txt"));
        readTXT(strFromUser, output);
        output.close();
    }

    private static void readTXT(String strFromUser, BufferedWriter outputTXT)
    {
        try {
            BufferedReader txt = new BufferedReader(new FileReader(strFromUser));
            String currLine; // read by every line
            currLine = txt.readLine(); // read the first line of the txt
            if (!currLine.endsWith(".xml")) { // every txt must start with xml file of the network
                txt.close();
                throw new IllegalArgumentException(); // end the program
            }
// since the name of each variable is unique, we can add each node to an hashmap of our graph nodes
            HashMap<String, Node> networkVars = myXMLreader.XMLreader(currLine);
            while ((currLine = txt.readLine()) != null) { // we should check if the query is valid
                // after running the algo, reset color & visit of each node
                if (currLine.startsWith("P(")) {// it's a Variable Elimination query
                    variableEliminationWriter(currLine, networkVars, outputTXT);
                }
                else { // it's a Bayes Ball query
                    bayesBallWriter(currLine, networkVars, outputTXT);
                }
            //  after running the desired algo, reset the color & visit of each node:
                BayesBall.resetVars(networkVars);
            }
            txt.close(); // end of the txt input
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void variableEliminationWriter(String query, HashMap<String, Node> vars, BufferedWriter output) throws IOException
    { // this func calls to VE algo and write it answer on the output txt
        output.write(VariableElimination.variable_elimination(query, vars)+"\n");
    }

    public static void bayesBallWriter(String query, HashMap<String, Node> vars, BufferedWriter output) throws IOException
    { // this func calls to BB algo and write it answer on the output txt
        if (BayesBall.bayes_ball(query, vars))
            output.write("yes\n"); // the variables are Independent
        else
            output.write("no\n"); // the variables are Dependent
    }
}
