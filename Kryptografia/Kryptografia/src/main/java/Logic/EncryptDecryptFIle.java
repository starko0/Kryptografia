package Logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class EncryptDecryptFIle {
    public static void encryptFile(String inputFile, String outputFile, byte[] key) {
        Path inputPath = Paths.get(inputFile);
        Path outputPath = Paths.get(outputFile);

        try (InputStream inputStream = Files.newInputStream(inputPath);
             OutputStream outputStream = Files.newOutputStream(outputPath)) {

            byte[] inputBytes = inputStream.readAllBytes();
            byte[] encryptedBytes = DES.encrypt(inputBytes, key);
            String encryptedContent = Base64.getEncoder().encodeToString(encryptedBytes);
            byte[] outputBytes = encryptedContent.getBytes();
            outputStream.write(outputBytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void decryptFile(String inputFile, String outputFile, byte[] key) {
        Path inputPath = Paths.get(inputFile);
        Path outputPath = Paths.get(outputFile);

        try (InputStream inputStream = Files.newInputStream(inputPath);
             OutputStream outputStream = Files.newOutputStream(outputPath)) {

            byte[] inputBytes = inputStream.readAllBytes();
            String encryptedContent = new String(inputBytes);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedContent);
            byte[] decryptedBytes = DES.decrypt(encryptedBytes, key);
            outputStream.write(decryptedBytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




