public class BasicProbOpers {
    /**
     *  The purpose of this class is to contain simple probability operators.
     */

    public static double not(double A)
    {
        return 1 - A;
    }

    public static double A_and_B(double A, double B ,double B_given_A)
    {
        return (B_given_A*A)/B;
    }

    public static double A_given_B(double A, double B ,double B_given_A)
    {
        return (A_and_B(A,B,B_given_A) / B);
    }
}
