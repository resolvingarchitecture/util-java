package ra.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;

public class HashUtil {

    private static Logger LOG = Logger.getLogger(HashUtil.class.getName());

    private static String DEL = "_";

    public static String generateFingerprint(byte[] contentToFingerprint, String algorithm) throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] hash = md.digest(contentToFingerprint);
            return toHex(hash);
    }

    public static String generateHash(String contentToHash, String algorithm) throws NoSuchAlgorithmException {
        if("PBKDF2WithHmacSHA1".equals(algorithm))
            return generatePasswordHash(contentToHash);
        else
            return generateHash(getSalt(), contentToHash.getBytes(), algorithm);
    }
    /**
     * Generate Hash using supplied bytes and specified Algorithm
     * @param contentToHash
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String generateHash(byte[] contentToHash, String algorithm) throws NoSuchAlgorithmException {
        if("PBKDF2WithHmacSHA1".equals(algorithm))
            return generatePasswordHash(new String(contentToHash));
        else
            return generateHash(getSalt(), contentToHash, algorithm);
    }

    private static String generateHash(byte[] salt, byte[] contentToHash, String algorithm) throws NoSuchAlgorithmException {
        if("PBKDF2WithHmacSHA1".equals(algorithm))
            return generatePasswordHash(salt, new String(contentToHash));
        else {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(salt);
            byte[] hash = md.digest(contentToHash);
            return Base64.getEncoder().encodeToString(hash) + DEL + Base64.getEncoder().encodeToString(salt);
        }
    }

    public static Boolean verifyHash(String contentToVerify, String hashToVerify, String algorithm) throws NoSuchAlgorithmException {
        if("PBKDF2WithHmacSHA1".equals(algorithm))
            return verifyPasswordHash(contentToVerify, hashToVerify);
        else {
            String[] parts = hashToVerify.split(DEL);
            byte[] hash = Base64.getDecoder().decode(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            String hashStringToVerify = generateHash(salt, contentToVerify.getBytes(), algorithm);
            String[] partsToVerify = hashStringToVerify.split(DEL);
            byte[] hashToVerifyBytes = Base64.getDecoder().decode(partsToVerify[0]);
            return Arrays.equals(hash, hashToVerifyBytes);
        }
    }

    public static String generatePasswordHash(String passwordToHash) throws NoSuchAlgorithmException {
        return generatePasswordHash(getSalt(), passwordToHash);
    }

    public static String generatePasswordHash(byte[] salt, String passwordToHash) throws NoSuchAlgorithmException {
        int iterations = 1000;
        byte[] hash;
        try {
            PBEKeySpec spec = new PBEKeySpec(passwordToHash.toCharArray(), salt, iterations, 64 * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = skf.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        String hashString = iterations + DEL + toHex(salt) + DEL + toHex(hash);
        return iterations + DEL + Base64.getEncoder().encodeToString(salt) + DEL + Base64.getEncoder().encodeToString(hash);
    }

    public static Boolean verifyPasswordHash(String contentToVerify, String hashToVerify) throws NoSuchAlgorithmException {
//        String hashString = Base64.decodeToString(hashToVerify.getHash());
        String[] parts = hashToVerify.split(DEL);
        int iterations = Integer.parseInt(parts[0]);
//        byte[] salt = fromHex(parts[1]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
//        byte[] hash = fromHex(parts[2]);
        byte[] hash = Base64.getDecoder().decode(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(contentToVerify.toCharArray(), salt, iterations, 64 * 8);
        byte[] testHash;
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            testHash = skf.generateSecret(spec).getEncoded();
        } catch (Exception e) {

            return null;
        }

        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++)
        {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    public static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
            hex = String.format("%0"  +paddingLength + "d", 0) + hex;
        hex = hex.toUpperCase();
        StringBuilder sb = new StringBuilder();
        char[] ch = hex.toCharArray();
        int i = 1;
        for(char c : ch) {
            sb.append(c);
            if((i++%4)==0 && i<ch.length) {
                sb.append(":");
            }
        }
        return sb.toString();
    }

    public static byte[] fromHex(String hex)
    {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    public static void main(String[] args) {
        try {
            String passwordHash = HashUtil.generatePasswordHash("1234");
            System.out.println("Alias Password Hash: "+passwordHash);
            Boolean aliasVerified = HashUtil.verifyPasswordHash("1234", passwordHash);
            System.out.println("Alias Password Hash Verified: "+aliasVerified);
            String aliasHash = HashUtil.generateHash("Alice", "SHA-1");
            System.out.println("Alias Hash: "+aliasHash);
            Boolean shortHashVerified = HashUtil.verifyHash("Alice", aliasHash, "SHA-1");
            System.out.println("Alias Hash Verified: "+shortHashVerified);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
