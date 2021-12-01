import java.io.*;
import java.util.HashMap;

public class Ex1 {
    /**
     *  Main Class
     */
    public static void main(String[] args) throws IOException {
        String strFromUser = "src/input2.txt"; // scanner???
        //File output = createFile(); // maybe the name of the output must be familiar with the input
        BufferedWriter output = new BufferedWriter(new FileWriter("output"));
        readTXT(strFromUser, output);
        output.close();
    }
    private static void readTXT(String strFromUser, BufferedWriter outputTXT)
    {
        try {
            BufferedReader txt = new BufferedReader(new FileReader(strFromUser));
            String currLine;
            String xmlPath;
            currLine = txt.readLine();
            if (!currLine.endsWith(".xml"))
                return;
// since the name of each variable is unique, we can add each node to an hashmap of our graph nodes
            HashMap<String, Node> networkVars = myXMLreader.XMLreader(currLine);
            while ((currLine = txt.readLine()) != null) { // we should check if the query is valid
                if (currLine.startsWith("P(")) {// it's a Variable Elimination query
                    variableEliminationWriter(currLine, networkVars, outputTXT);
                    BayesBall.resetVars(networkVars); // after running the algo, reset color & visit of each node
                }
                else { // it's a Bayes Ball query
                    bayesBallWriter(currLine, networkVars, outputTXT);
                    BayesBall.resetVars(networkVars); // after running the algo, reset color & visit of each node
                }
            }
            txt.close();
        }
        catch (IOException e) { // or FileNotFoundException
            e.printStackTrace();
        }
    }

    public static void variableEliminationWriter(String query, HashMap<String, Node> vars, BufferedWriter output) throws IOException
    {
        output.write(VariableElimination.variable_elimination(query, vars)+"\n");
    }

    public static void bayesBallWriter(String query, HashMap<String, Node> vars, BufferedWriter output) throws IOException
    {
        if (BayesBall.bayes_ball(query, vars))
            output.write("yes\n");
        else
            output.write("no\n");
    }

    private static File createFile()
    {
        try {
            String path = "output.txt";
            int add = 1;
            File txtOutput = new File(path);
            while (!txtOutput.createNewFile()) {
                txtOutput = new File(path + String.valueOf(add));
            }
            return txtOutput;
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }

}
