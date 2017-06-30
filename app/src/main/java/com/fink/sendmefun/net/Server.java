package com.fink.sendmefun.net;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.util.Base64;

import com.fink.sendmefun.BusProvider;
import com.fink.sendmefun.crypt.Decrypt;
import com.fink.sendmefun.model.message.ReceivedMessage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

public class Server {

    private static final int BUFFER_SIZE = 1024;
    private static final String FILE_PATH = "server_file.txt";

    public static Observable.OnSubscribe<String> createServer(final Activity activity) {
        return new Observable.OnSubscribe<String>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void call(Subscriber<? super String> subscriber) {
                BluetoothServerSocket bluetoothServerSocket = null;
                while (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                }
                try {bluetoothServerSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(UUID.randomUUID().toString(), UUID.fromString(Constant.NAME));
                    while (true) {
                        try (BluetoothSocket bluetoothSocket = bluetoothServerSocket.accept()) {
                            try (InputStream in = bluetoothSocket.getInputStream()) {
                                try (OutputStream out = bluetoothSocket.getOutputStream()) {
                                    byte[] buffer = new byte[BUFFER_SIZE];
                                    try(FileOutputStream fileOutputStream = activity.openFileOutput(FILE_PATH, Context.MODE_PRIVATE)){
                                        int size = in.read(buffer);
                                        fileOutputStream.write(buffer, 0, size);
                                    }
                                    String message = Decrypt.decrypt(Constant.KEY, Constant.ALGORITHM, FILE_PATH, Constant.CHARSET, activity);
                                    subscriber.onNext(message);
                                    out.write(Constant.SUCCESS.getBytes(Constant.CHARSET));
                                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ignored) {
                                }
                            }
                        }
                    }
                } catch (IOException ignored) {
                } finally {
                    try {
                        if (bluetoothServerSocket != null) {
                            bluetoothServerSocket.close();
                        }
                    } catch (IOException ignored) {
                    }
                }
            }
        };
    }

    public static Observer<String> createServerSubscriber() {
        return new Observer<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onNext(String message) {
                BusProvider.getInstance().post(new ReceivedMessage(message));
            }
        };
    }
}
