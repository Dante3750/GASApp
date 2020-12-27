package com.netconnect.sitienergy.utils;



public class CryptoHelper {

    public static void main(String args[]) throws Exception {
        String input = "Text to encryp";
        System.out.println("input:" + input);
        String encriptedValue = encrypt(input);
        System.out.println("cipher:" + encriptedValue);
        //encriptedValue = "n0c0KYNl+PM6cekV7YMHI5b0fLodT3jU27cHt7+VGrA=";
        System.out.println("cipher:" + encriptedValue);
        String decriptedValue = decrypt(encriptedValue);
        System.out.println("output:" + decriptedValue);

    }

    private javax.crypto.spec.IvParameterSpec ivspec;
    private javax.crypto.spec.SecretKeySpec   keyspec;
    private javax.crypto.Cipher               cipher;
    private final static String               SecretKey  = "1234567890abcder";//16 char secret key

    public
    CryptoHelper ( ) {
        ivspec = new javax.crypto.spec.IvParameterSpec( SecretKey.getBytes ( ));

        keyspec = new javax.crypto.spec.SecretKeySpec( SecretKey.getBytes ( ), "AES");

        try {
            cipher = javax.crypto.Cipher.getInstance ( "AES/CBC/PKCS5Padding" );
        } catch ( java.security.NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( javax.crypto.NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String encrypt(String valueToEncrypt) throws Exception {
        CryptoHelper enc = new CryptoHelper();
        return android.org.apache.commons.codec.binary.Base64.encodeBase64String ( enc.encryptInternal ( valueToEncrypt ) );
        //return Base64.encodeToString(enc.encryptInternal(valueToEncrypt), Base64.DEFAULT);
    }

    public static String decrypt(String valueToDecrypt) throws Exception {
        CryptoHelper enc = new CryptoHelper();
        return new String(enc.decryptInternal(valueToDecrypt));
    }

    private byte[] encryptInternal(String text) throws Exception {
        if (text == null || text.length() == 0) {
            throw new Exception("Empty string");
        }

        byte[] encrypted = null;
        try {
            cipher.init ( javax.crypto.Cipher.ENCRYPT_MODE, keyspec, ivspec );
            encrypted = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }
        return encrypted;
    }

    private byte[] decryptInternal(String code) throws Exception {
        if (code == null || code.length() == 0) {
            throw new Exception("Empty string");
        }

        byte[] decrypted = null;
        try {
            cipher.init ( javax.crypto.Cipher.DECRYPT_MODE, keyspec, ivspec );
            //decrypted = cipher.doFinal(Base64.decode(code,Base64.DEFAULT));
            decrypted = cipher.doFinal (
                    android.org.apache.commons.codec.binary.Base64.decodeBase64 ( code ) );
        } catch (Exception e) {
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;
    }
}
