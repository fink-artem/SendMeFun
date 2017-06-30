package com.fink.sendmefun.crypt;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Base64;

import com.fink.sendmefun.net.Constant;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void encrypt(String key, String algorithm, String filePath, String text, String charset, Activity activity) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        byte[] encodedKey = Base64.decode(key, Base64.DEFAULT);
        SecretKey mKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, algorithm);
        Cipher encipher = Cipher.getInstance(algorithm);
        encipher.init(Cipher.ENCRYPT_MODE, mKey);
        try (FileOutputStream fileOutputStream = activity.openFileOutput(filePath, Context.MODE_PRIVATE)) {
            try (CipherOutputStream cos = new CipherOutputStream(fileOutputStream, encipher)) {
                cos.write(text.getBytes(charset));
            }
        }
    }
}
