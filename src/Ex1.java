import java.io.*;

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
            File xml = new File(xmlPath);
            while ((currLine = txt.readLine()) != null) { // we should check id the query is valid
                if (currLine.startsWith("P("))
                    VariableElimination.variable_elimination(currLine, xml, outputTXT); // it's a Variable Elimination query
                else
                    BayesBall.bayes_ball(currLine, xml, outputTXT); // it's a Bayes Ball query
            }
            txt.close();
        }
        catch (IOException e) { // or FileNotFoundException
            e.printStackTrace();
        }
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
