import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class EncryptDecryptFIle {
    public static void encryptFile(String inputFile, String outputFile, byte[] key) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[8];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] encryptedBuffer = DES.encrypt(Arrays.copyOf(buffer, bytesRead), key);
                fos.write(encryptedBuffer);
            }
        }
    }

    public static void decryptFile(String inputFile, String outputFile, byte[] key) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[8];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] decryptedBuffer = DES.decrypt(Arrays.copyOf(buffer, bytesRead), key);
                fos.write(decryptedBuffer);
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas odszyfrowywania pliku: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


