import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Ex1 {
    /**
     *  Main Class
     */
    public static void main(String[] args) {
        String strFromUser = ""; // scanner???
        File output = createFile(); // maybe the name of the output must be familiar with the input
        readTXT(strFromUser, output);
    }
    private static void readTXT(String strFromUser, File outputTXT)
    {
        try {
            BufferedReader txt = new BufferedReader(new FileReader(strFromUser));
            String currLine;
            String xmlPath;
            if (txt.readLine() != null) // try catch?
                currLine = txt.readLine(); // because we need to check if the first line is xml
            else return;
            if (currLine.endsWith(".xml"))
                xmlPath = currLine;
            else return; // try catch?
            HashMap<String, Node> networkVars = myXMLreader.XMLreader(xmlPath);
            while ((currLine = txt.readLine()) != null) { // we should check id the query is valid
                if (currLine.startsWith("P(")) // it's a Variable Elimination query
                    VariableElimination.variable_elimination(currLine, networkVars, outputTXT);
                else // it's a Bayes Ball query
                    bayesBallWriter(currLine, networkVars, outputTXT);
            }
            txt.close();
        }
        catch (IOException e) { // or FileNotFoundException
            e.printStackTrace();
        }
    }

    public static void bayesBallWriter(String query, HashMap<String, Node> vars, File output)
    {
        if (BayesBall.bayes_ball(query, vars))
            System.out.println("yes");
        else
            System.out.println("no");
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
