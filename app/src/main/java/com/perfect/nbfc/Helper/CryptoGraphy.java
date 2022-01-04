package com.perfect.nbfc.Helper;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CryptoGraphy {
    private static volatile CryptoGraphy cryptography;

    public static CryptoGraphy getInstance() {
        if (cryptography == null)
            cryptography = new CryptoGraphy();
        return cryptography;
    }

//    static {
//        System.loadLibrary("native-lib");
//    }

    private String reverseString(String input) {
        return new StringBuilder(input).reverse().toString();
    }

    private String shiftString(String input) {
        // String temp = getAscii("asdf");
        int middleLength;
        int stringLength = input.length();
        if (stringLength % 2 == 0) {
            middleLength = stringLength / 2;
        } else {
            middleLength = (stringLength - 1) / 2;
        }

        input = input.substring(middleLength, stringLength) +
                input.substring(0, middleLength);

        return reverseString(input);
    }

    public String hashing(List<String> param) {

        String finalString = "";
        String mergeString;
        StringBuilder mergeStringBuilder = new StringBuilder();
        for (String item : param) {

            mergeStringBuilder.append(shiftString(item));

        }
        mergeString = mergeStringBuilder.toString();

        byte[] asciiBytes;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            asciiBytes = mergeString.getBytes(StandardCharsets.US_ASCII);
        } else {
            asciiBytes = mergeString.getBytes();
        }

        StringBuilder resultStringBuilder = new StringBuilder();

        for (int individualAsciiValue : asciiBytes) {

            String stringPadded = String.format(Locale.ENGLISH, "%03d", individualAsciiValue);
            int length = stringPadded.length();
            char lastChar = stringPadded.charAt( length - 1 );
            char secondLastChar = stringPadded.charAt( length - 2 );

            int sumOfChar = Character.getNumericValue( lastChar ) + Character.getNumericValue( secondLastChar );
            int diffOfChar = Math.abs( lastChar - secondLastChar );
            int lengthSumOfChar = String.valueOf( sumOfChar ).length();

            String tempFinalString;

            if (lengthSumOfChar == 1) {
                tempFinalString = String.valueOf(diffOfChar) + String.valueOf(diffOfChar) + String.valueOf(sumOfChar);
            } else {
                tempFinalString = String.valueOf(diffOfChar) + String.valueOf(sumOfChar);
            }
            resultStringBuilder.append(tempFinalString);
        }

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] encryptedByte = resultStringBuilder.toString().getBytes();
            byte[] hash = messageDigest.digest(encryptedByte);

            BigInteger bigInteger = new BigInteger(1, hash);
            finalString = String.format("%0" + (hash.length << 1) + "X", bigInteger);


        } catch (NoSuchAlgorithmException ignored) {

        }

        return finalString;
    }

    public String binToAscii() {

        String input = "0100010101111000011000010110110101000101011110000110000101101101";
        StringBuilder output = new StringBuilder();

        for (int i = 0; i <= input.length() - 8; i += 8) {
            int k = Integer.parseInt(input.substring(i, i + 8), 2);
            output.append((char) k);
        }

        return output.toString();
    }

    public String randomNumber(String agentId) {
        long currentTime = Calendar.getInstance().getTimeInMillis();

        /*String[] selection = {"1111", "1212", "31312", "12121"};
        int random = new Random().nextInt(selection.length);*/
        return agentId + Long.toString(currentTime)/* + selection[random]*/;
    }

//    public native String getAscii(String t);
}
