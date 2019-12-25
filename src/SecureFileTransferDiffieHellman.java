import Utilities.DHUtil;

import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Scanner;


public class SecureFileTransferDiffieHellman {
    private static final int MAXBYTE = 256;

    public static void main(String[] args) throws Exception{
        Scanner scan = new Scanner(System.in);
        int selection = 0;
        while(selection != 3) {

            System.out.println("Welcome.");
            System.out.println("Please select operation.");
            System.out.println("1. Send File");
            System.out.println("2. Receive File");
            System.out.println("3. Exit");
            System.out.print("> ");

            selection = scan.nextInt();

            switch (selection) {
                case 1:
                    System.out.println("Listening port:");
                    int port = scan.nextInt();
                    System.out.println("File to send: ");
                    String filePath = scan.next();
                    ServerSocket serverSocket = new ServerSocket(port);
                    String fileContent = FileManager.getInstance().readFile(filePath);
                    if(!fileContent.equalsIgnoreCase("")) {
                        String fileName = FileManager.getInstance().getFileName(filePath);
                        System.out.println("Server mode initiated. Serving clients on port " + port);
                        while(true) {
                            Socket clientSocket = serverSocket.accept();
                            Thread thread = new Thread(new ServerProtocol(clientSocket, fileName, fileContent));
                            thread.start();
                        }
                    } else {
                        System.err.println("File not found.");
                    }

                    break;

                case 2:
                    System.out.println("Server host:");
                    String server = scan.next();
                    System.out.println("Server port:");
                    int servPort = scan.nextInt();
                    Socket clientSocket = new Socket(server, servPort);

                    DataInputStream fromServer = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());

                    BigInteger p = new BigInteger(fromServer.readUTF());
                    // receive prime number generator from server
                    BigInteger g = new BigInteger(fromServer.readUTF());
                    int bitLength = 256;
                    DHUtil ClientDH = new DHUtil(p, g, bitLength);
                    ClientDH.chooseNumber();
                    ClientDH.calculateValueToSend();
                    BigInteger B = ClientDH.getValueCalculate();

                    // receive A from server
                    BigInteger A = new BigInteger(fromServer.readUTF());
                    ClientDH.recvSideKey(A);
                    ClientDH.calculateFinalKey();

                    // send B to server
                    toServer.writeUTF(B.toString());

                    // calculate secret key
                    BigInteger decryptionKeyClient = ClientDH.getFinalKey();

                    System.out.println("Calculated key: " + decryptionKeyClient);

                    // generate AES key
                    Key key = generateKey(decryptionKeyClient.toByteArray());

                    // continue below...
                    System.out.println("Waiting for file.");

                    try {
                        // read filename from server
                        String fName = fromServer.readUTF();
                        fName = Paths.get(fName).getFileName().toString();

                        String encryptedFile = "";
                        String line;
                        while (!(line = fromServer.readUTF()).equalsIgnoreCase("")) {
                            encryptedFile += line;
                            if (line.isEmpty()) {
                                break;
                            }
                        }

                        String decryptedFile = FileManager.getInstance().decryptFile(encryptedFile, key);
                        FileManager.getInstance().writeFile(fName, decryptedFile);

                        System.out.println("File download complete. Saved in ./" + fName + "\n");

                    } catch (Exception e) {
                        System.err.println("Error while creating/reading server socket: " + e);
                    }

                    break;
                case 3:
                    System.out.println("Bye bye.");
                    break;
                default:
                    System.out.println("Select 1, 2 or 3.");
                    break;
            }

        }

    }


    private static Key generateKey(byte[] sharedKey)
    {
        byte[] byteKey = new byte[16];
        for(int i = 0; i < 16; i++) {
            byteKey[i] = sharedKey[i];
        }

        try {
            Key key = new SecretKeySpec(byteKey, "AES");

            return key;
        } catch(Exception e) {
            System.err.println("Error while generating key: " + e);
        }

        return null;
    }


}
