package PasswordCracker;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Utility class for PasswordCracker.

public class PasswordCrackerUtil {
    private static final String PASSWORD_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";    // Possible Password symbol (NUMBER(0~9) + CHARACTER(A to Z))
    private static final int PASSWORD_LEN = 6;
    public static final long TOTAL_PASSWORD_RANGE_SIZE = (long) Math.pow(PASSWORD_CHARS.length(), PASSWORD_LEN);

    public static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot use MD5 Library:" + e.getMessage());
        }
    }

    public static String encrypt(String password, MessageDigest messageDigest) {
        messageDigest.update(password.getBytes());
        byte[] hashedValue = messageDigest.digest();
        return byteToHexString(hashedValue);
    }

    public static String byteToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                builder.append('0');
            }
            builder.append(hex);
        }
        return builder.toString();
    }

    // Tries i'th candidate (rangeBegin <= i < rangeEnd) and compares against encryptedPassword
    // If original password is found, return the password;
    // if not, return null.

    public static String findPasswordInRange(long rangeBegin, long rangeEnd, String encryptedPassword, TerminationChecker checker)
        throws IOException {

        char passwdFirstChar = encryptedPassword.charAt(0);    // Our little optimization
        int[] arrayKey = new int[PASSWORD_LEN]; // The array which holds each alpha-num item
        String passwd = null;

        // Compute first array
        long longKey = rangeBegin;
        transformDecToBase36(longKey, arrayKey);

        for (; longKey < rangeEnd && !checker.isTerminated(); longKey++) {
            String rawKey = transformIntoStr(arrayKey);
            String md5Key = encrypt(rawKey, getMessageDigest());

            // Avoid full string comparison
            if (md5Key.charAt(0) == passwdFirstChar) {
                if (encryptedPassword.equals(md5Key)) {
                    passwd = rawKey;
                    break;
                }
            }
            getNextCandidate(arrayKey);
        }

        return passwd;
    }

    /* ###  transformDecToBase36  ###
     * The transformDecToBase36 transforms decimal into numArray that is base 36 number system
     * If you don't understand, refer to the homework01 overview
    */

    private static void transformDecToBase36(long numInDec, int[] numArrayInBase36) {
        long quotient = numInDec; 
        int passwdlength = numArrayInBase36.length - 1;

        for (int i = passwdlength; quotient > 0l; i--) {
            int reminder = (int) (quotient % 36l);
            quotient /= 36l;
            numArrayInBase36[i] = reminder;
        }
    }

    private static void getNextCandidate(int[] candidateChars) {
        int i = candidateChars.length - 1;

        while(i >= 0) {
            candidateChars[i] += 1;

            if (candidateChars[i] >= 36) {
                candidateChars[i] = 0;
                i--;

            } else {
                break;
            }
        }
    }

    private static String transformIntoStr(int[] candidateChars) {
        char[] password = new char[candidateChars.length];
        for (int i = 0; i < password.length; i++) {
            password[i] = PASSWORD_CHARS.charAt(candidateChars[i]);
        }
        return new String(password);
    }
}
