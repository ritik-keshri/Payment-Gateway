package com.example.paymentgateway;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HashGenrationUtils {
    public static String genrateFromServer(String hashData, String salt, String merchantSecretKey) {

         if (merchantSecretKey.isEmpty())
             return calculateHash(hashData+salt);
        else
            return calculateHmacSha1(hashData, merchantSecretKey);
    }

    /**
     * Function to calculate the SHA-512 hash
     * @param hashString hash string for hash calculation
     * @return Post Data containig the
     * */
    private static String calculateHash(String hashString) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messageDigest.update(hashString.getBytes());
        byte[] mdbytes = messageDigest.digest();
        return getHexString(mdbytes);
    }

    private static String calculateHmacSha1(String hashString, String key) {
        try {
            String type = "HmacSHA1";
            SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
            Mac mac = Mac.getInstance(type);
            mac.init(secret);
            byte[] bytes = mac.doFinal(hashString.getBytes());
            return getHexString(bytes);
        } catch (Exception e){
            return null;
        }
    }

    private static String getHexString(byte[] data) {
        // Create Hex String
        StringBuilder hexString = new StringBuilder();
        for (byte aMessageDigest:  data) {
            String h = Integer.toHexString(0xFF & (int) aMessageDigest);
            while (h.length() < 2)
                h = "0$h";
            hexString.append(h);
        }
        return hexString.toString();
    }
}
