import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.util.Base64;


public class FileManager {
    private static final FileManager instance = new FileManager();
    private FileInputStream inStream;
    private FileOutputStream outStream;
    private static Cipher cipher;

    protected FileManager() {
        try {
            cipher = Cipher.getInstance("AES");
        } catch(Exception e) {
            System.err.println("Error while getting AES algorithm: " + e);
        }
    }

    public String readFile(String fileName) {

        String fileContent = "";

        try {
            inStream = new FileInputStream(fileName);
            int fileSize = inStream.available();
            for(int i = 0; i < fileSize; i++) {
                fileContent += (char) inStream.read();
            }

        } catch(Exception e) {
            System.err.println("File not found: " + fileName);
        } finally {
            try {
                if(inStream != null) {
                    inStream.close();
                }
            } catch(Exception ex) {
                System.err.println("Error while closing File I/O: " + ex);
            }
        }

        return fileContent;
    }

    public void writeFile(String fileName, String fileContent) {
        try {
            outStream = new FileOutputStream(fileName);

            byte[] fileContentBytes = fileContent.getBytes();

            outStream.write(fileContentBytes);

        } catch(Exception e) {
            System.err.println("Error while writing into file " + fileName + ": " + e);
        } finally {
            try {
                if(outStream != null) {
                    outStream.close();
                }
            } catch(Exception ex) {
                System.err.println("Error while closing File I/O: " + ex);
            }
        }
    }

    public String getFileName(String filePath) {

        String[] split =  filePath.split("\\/");

        return split[split.length - 1];
    }


    public String encryptFile(String plainText, Key secretKey) {
        byte[] plainTextByte = plainText.getBytes();

        String encryptedText = "";

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedByte = cipher.doFinal(plainTextByte);

            Base64.Encoder encoder = Base64.getEncoder();

            encryptedText = encoder.encodeToString(encryptedByte);

        } catch(Exception e) {
            System.err.println("Error while initializing Cipher while encrypting text: " + e);
        }

        return encryptedText;
    }


    public String decryptFile(String encryptedText, Key secretKey) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        String decryptedText = "";
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
            decryptedText = new String(decryptedByte);
        } catch(Exception e) {
            System.err.println("Error while initializing Cipher while decrypting text: " + e);
        }

        return decryptedText;
    }

    public static FileManager getInstance() {
        return instance;
    }
}
