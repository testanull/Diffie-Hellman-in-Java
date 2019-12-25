import Utilities.DHUtil;

import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.Key;
import java.security.SecureRandom;


public class ServerProtocol implements Runnable {
    private static final int MAXBYTE = 256;
    private Socket clientSocket;
    private String fileName;
    private String fileContent;

    public ServerProtocol(Socket socket,  String fName, String fContent) {
        this.clientSocket = socket;
        this.fileName = fName;
        this.fileContent = fContent;
    }


    public static void handleClient(Socket clientSocket,  String fileName, String fileContent) {
        try {
            DataOutputStream toClient = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream fromClient = new DataInputStream(clientSocket.getInputStream());

            SecureRandom rnd = new SecureRandom();
            int bitLength = 256;
            BigInteger p = BigInteger.probablePrime(bitLength, rnd);
            BigInteger g = BigInteger.probablePrime(bitLength, rnd);
            DHUtil ServerDH = new DHUtil(p, g, bitLength);
            ServerDH.chooseNumber();
            ServerDH.calculateValueToSend();

            BigInteger A = ServerDH.getValueCalculate();

            toClient.writeUTF(p.toString());
            toClient.writeUTF(g.toString());
            toClient.writeUTF(A.toString());

            BigInteger B = new BigInteger(fromClient.readUTF());

            ServerDH.recvSideKey(B);
            ServerDH.calculateFinalKey();

            BigInteger encryptionKeyServer = ServerDH.getFinalKey();

            System.out.println("Calculated key: " + encryptionKeyServer);
            Key key = generateKey(encryptionKeyServer.toByteArray());
            toClient.writeUTF(fileName);
            String encryptedFile = FileManager.getInstance().encryptFile(fileContent, key);
            byte[][] split;
            if((split = chunkArray(encryptedFile.getBytes(), MAXBYTE)) != null) {
                for(int i = 0; i < split.length; i++) {
                    toClient.writeUTF(new String(split[i]));
                }
            }
            toClient.writeUTF("");
            toClient.flush();

        } catch(Exception e) {

        }

    }

    public void run() {
        handleClient(clientSocket, fileName, fileContent);
    }

    public static byte[][] chunkArray(byte[] array, int chunkSize) {
        int numOfChunks = (int)Math.ceil((double)array.length / chunkSize);
        byte[][] output = new byte[numOfChunks][];

        for(int i = 0; i < numOfChunks; ++i) {
            int start = i * chunkSize;
            int length = Math.min(array.length - start, chunkSize);

            byte[] temp = new byte[length];

            System.arraycopy(array, start, temp, 0, length);

            output[i] = temp;
        }

        return output;
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
