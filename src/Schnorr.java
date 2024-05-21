import java.math.BigInteger;
import java.security.*;

public class Schnorr {
    SecureRandom random = new SecureRandom();
    private BigInteger p, q, a, s, v, r, x, e, y, xPrim;
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
        byte xPrimBytes[] = xPrim.toByteArray();
        byte passingToHash[] = new byte[message.length + xPrimBytes.length];
        for (int i = 0; i < message.length; i++)
            passingToHash[i] = message[i];
        for (int i = 0; i < xPrimBytes.length; i++)
            passingToHash[message.length + i] = xPrimBytes[i];
        //it`s e value for signature checking, just different name
        BigInteger hash = new BigInteger(1, sha512.digest(passingToHash));
        if (hash.compareTo(signature[0]) == 0)
            return true;
        else return false;
    }

}
