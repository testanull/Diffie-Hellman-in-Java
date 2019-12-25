package diffie.hellman;

import Utilities.DHUtil;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;

public class DiffieHellman {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SecureRandom rnd = new SecureRandom();

        //Input the bits
        System.out.print("Input the bit size: ");
        int bitLength = sc.nextInt();

        //Random prime (main arguments)
        BigInteger p = BigInteger.probablePrime(bitLength, rnd);
        BigInteger g = BigInteger.probablePrime(bitLength, rnd);

        //Build people that know the main arguments
        DHUtil Alice = new DHUtil(p, g, bitLength);
        DHUtil Bob = new DHUtil(p, g, bitLength);

        //The people choose a number
        Alice.chooseNumber();
        Bob.chooseNumber();

        //The people calculate the number
        Alice.calculateValueToSend();
        Bob.calculateValueToSend();

        //The people receive the value sent
        Alice.recvSideKey(Bob.getValueCalculate());
        Bob.recvSideKey(Alice.getValueCalculate());

        //The peopple calculate the final key
        Alice.calculateFinalKey();
        Bob.calculateFinalKey();

        //Print results
        System.out.println("=========================================================");
        String pSelected = "The value of p selected:";
        String gSelected = "The value of g selected:";
        String aSelected = "The value of a selected by Alice:";
        String bSelected = "The value of b selected by Bob:";
        String ASelected = "The value of A sent to Bob by Alice:";
        String BSelected = "The value of B sent to Alice by Bob:";
        String AlicesName = "Alice:";
        String BobsName = "Bob:";

        System.out.printf("%-50s %-15s %n", pSelected, p);
        System.out.printf("%-50s %-15s %n", gSelected, g);
        System.out.printf("%-50s %-15s %n", aSelected, Alice.getNumberChoosen());
        System.out.printf("%-50s %-15s %n", bSelected, Bob.getNumberChoosen());
        System.out.printf("%-50s %-15s %n", ASelected, Bob.getNumberReceived());
        System.out.printf("%-50s %-15s %n", BSelected, Alice.getNumberReceived());
        System.out.println("The value of key shared between Alice and Bob: ");
        System.out.printf("%-50s %-15s %n", AlicesName, Alice.getFinalKey());
        System.out.printf("%-50s %-15s %n", BobsName, Bob.getFinalKey());
        System.out.println("==========================================================");
        
        if (Alice.getFinalKey().equals(Bob.getFinalKey())) {
            System.out.println(">>>  SUCCESS ALICE AND BOB HAVE THE SAME KEY!  :D <<<");
        } else {
            System.err.println(">>>  ERROR ALICE AND BOB NOT HAVE THE SAME KEY!  :( <<<");
        }
        
    }

}
