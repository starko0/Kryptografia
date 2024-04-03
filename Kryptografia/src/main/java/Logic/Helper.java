package Logic;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

public class Helper {
    public static byte[] generateKey() {
        SecureRandom randomKey = new SecureRandom();
        byte[] key = new byte[8];
        randomKey.nextBytes(key);
        return key;
    }

    public static void saveKeyToFile(byte[] key, String fileName) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(key);
        } catch (IOException e) {
            System.err.println("Błąd podczas zapisywania klucza do pliku: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static byte[] loadKeyFromFile(String fileName) {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            byte[] key = new byte[8];
            fis.read(key);
            return key;
        } catch (IOException e) {
            System.err.println("Błąd podczas wczytywania klucza z pliku: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
