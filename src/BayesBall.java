import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class BayesBall {
    public static void bayes_ball(String row, File xml, File outputTXT)
    {
        if (Undependable())
            System.out.println("T\n"); // File write "T\n"
        else
            System.out.println("F\n"); // File write "T\n"
    }

    private static boolean Undependable() {
        return true;
    }

}
