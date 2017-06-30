package com.fink.sendmefun.crypt;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.util.Base64;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Decrypt {

    private static int BUFFER_SIZE = 1024;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String decrypt(String key, String algorithm, String filePath, String charset, Activity activity) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        byte[] encodedKey = Base64.decode(key, Base64.DEFAULT);
        SecretKey mKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, algorithm);
        Cipher encipher = Cipher.getInstance(algorithm);
        encipher.init(Cipher.DECRYPT_MODE, mKey);
        int size;
        byte[] buffer = new byte[BUFFER_SIZE];
        try(FileInputStream fileInputStream = activity.openFileInput(filePath)) {
            try (CipherInputStream cos = new CipherInputStream(fileInputStream, encipher)) {
                size = cos.read(buffer);
            }
        }
        return new String(buffer, 0, size, charset);
    }
}
