package multiplikator;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Integer A, B;
        A = scan.nextInt();
        B = scan.nextInt();
        new Multiplikator(A, B).run();
    }

}