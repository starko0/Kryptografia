import java.math.BigInteger;
import java.security.*;
import java.io.*;

public class Schnorr {
    SecureRandom random = new SecureRandom();
    public BigInteger p, q, a, s, v, r, x, e, y, xPrim;
    private MessageDigest sha512;
    private int keyLength = 1024;
    private int qLength = 512;

    public Schnorr() {
        try {
            sha512 = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        generateKey();
    }

    public void generateKey() {
        q = BigInteger.probablePrime(qLength, random);
        BigInteger candidateForP, helper;
        do {
            candidateForP = BigInteger.probablePrime(keyLength, random);
            helper = candidateForP.subtract(BigInteger.ONE);
            //make sure that p-1 is divisible by q
            candidateForP = candidateForP.subtract(helper.remainder(q));
        }//function returns that p is prime with probability of 1-(1/2)^25
        while (!candidateForP.isProbablePrime(25));
        p = candidateForP;
        a = new BigInteger(keyLength - 2, random);
        //calculate a with fermat's little theorem
        a = a.modPow(p.subtract(BigInteger.ONE).divide(q), p);

        //generate random integer s which is user`s private key
        do {
            s = new BigInteger(qLength - 2, new SecureRandom());
        } while (s.compareTo(BigInteger.ZERO) <= 0 || s.compareTo(q) >= 0);

        //generate v which is user`s public key
        v = a.modPow(s.negate(), p);
    }

    public BigInteger[] sign(byte[] message) {
        do {
            r = new BigInteger(q.bitLength(), random);
        } while (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(q) >= 0);
        x = a.modPow(r, p);

        byte xBytes[] = x.toByteArray();
        byte passingToHash[] = new byte[message.length + xBytes.length];
        for (int i = 0; i < message.length; i++)
            passingToHash[i] = message[i];
        for (int i = 0; i < xBytes.length; i++)
            passingToHash[message.length + i] = xBytes[i];
        e = new BigInteger(1, sha512.digest(passingToHash));

        y = r.add(s.multiply(e)).mod(q);
        BigInteger signature[] = new BigInteger[2];
        signature[0] = e;
        signature[1] = y;
        return signature;
    }

    public BigInteger[] sign(String message) {
        do {
            r = new BigInteger(q.bitLength(), random);
        } while (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(q) >= 0);
        x = a.modPow(r, p);
        String xBytes = message;
        xBytes += x.toString();
        e = new BigInteger(1, sha512.digest(xBytes.getBytes()));

        y = r.add(s.multiply(e)).mod(q);
        BigInteger signature[] = new BigInteger[2];
        signature[0] = e;
        signature[1] = y;
        return signature;
    }

    public boolean verify(byte[] message, BigInteger[] signature) {
        xPrim = a.modPow(signature[1], p).multiply(v.modPow(signature[0], p)).mod(p);
        String xPrimBytes = new String(message);
        xPrimBytes += xPrim.toString();
        BigInteger hash = new BigInteger(1, sha512.digest(xPrimBytes.getBytes()));
        return hash.compareTo(signature[0]) == 0;
    }

public void saveKeysToFile(String filePath) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        writer.write("q=" + q.toString(16));
        writer.newLine();
        writer.write("a=" + a.toString(16));
        writer.newLine();
        writer.write("v=" + v.toString(16));
        writer.newLine();
        writer.write("s=" + s.toString(16));
        writer.newLine();
        writer.write("p=" + p.toString(16));
    } catch (IOException e) {
        throw new IOException("Błąd zapisu kluczy do pliku: " + e.getMessage());
    }
}

    public void loadKeysFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    switch (key) {
                        case "q":
                            q = new BigInteger(value, 16);
                            System.out.println("Loaded q: " + q.toString(16));
                            break;
                        case "v":
                            v = new BigInteger(value, 16);
                            System.out.println("Loaded v: " + v.toString(16));
                            break;
                        case "s":
                            s = new BigInteger(value, 16);
                            System.out.println("Loaded s: " + s.toString(16));
                            break;
                        case "p":
                            p = new BigInteger(value, 16);
                            System.out.println("Loaded p: " + p.toString(16));
                            break;
                        case "a":
                            a = new BigInteger(value, 16);
                            System.out.println("Loaded a: " + a.toString(16));
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("Błąd odczytu kluczy z pliku: " + e.getMessage());
        }
    }


}
