package com.fink.sendmefun.net;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;

import com.fink.sendmefun.BusProvider;
import com.fink.sendmefun.crypt.Encrypt;
import com.fink.sendmefun.model.Event;
import com.fink.sendmefun.model.message.Message;
import com.fink.sendmefun.model.message.SentMessage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.UUID;

import javax.crypto.NoSuchPaddingException;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Func1;

public class Client {

    private static final int BUFFER_SIZE = 1024;
    private static final String FILE_PATH = "client_file.txt";

    public static Observer<Message> createClientSubscriber() {
        return new Observer<Message>() {
            @Override
            public void onCompleted() {
                BusProvider.getInstance().post(new Event());
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onNext(Message message) {
            }
        };
    }

    public static Observable.OnSubscribe<Message> createClient(final Message message, final Activity activity) {
        return new Observable.OnSubscribe<Message>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void call(Subscriber<? super Message> subscriber) {
                Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
                for (BluetoothDevice bluetoothDevice : pairedDevices) {
                    try (BluetoothSocket bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(Constant.NAME))) {
                        if (!bluetoothSocket.isConnected()) bluetoothSocket.connect();
                        try (InputStream in = bluetoothSocket.getInputStream()) {
                            try (OutputStream out = bluetoothSocket.getOutputStream()) {
                                Encrypt.encrypt(Constant.KEY, Constant.ALGORITHM, FILE_PATH, message.getText(), Constant.CHARSET, activity);
                                byte[] buffer = new byte[BUFFER_SIZE];
                                try (FileInputStream fileInputStream = activity.openFileInput(FILE_PATH)) {
                                    int size = fileInputStream.read(buffer);
                                    out.write(buffer, 0, size);
                                }
                                int size = in.read(buffer);
                                String mes = new String(buffer, 0, size, Constant.CHARSET);
                                if (mes.equals(Constant.SUCCESS)) {
                                    ((SentMessage) message).setStatus(SentMessage.CHECK_MARK);
                                    subscriber.onCompleted();
                                } else {
                                    ((SentMessage) message).setStatus(SentMessage.CROSS);
                                    subscriber.onError(new Error());
                                }
                            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ignored) {
                            }
                        }
                    } catch (IOException ignored) {
                    }
                }
            }
        };
    }

    public static Func1<Throwable, Message> createRequestErrorHandler(final Message message) {
        return new Func1<Throwable, Message>() {
            @Override
            public Message call(Throwable throwable) {
                ((SentMessage) message).setStatus(SentMessage.CROSS);
                BusProvider.getInstance().post(new Event());
                return null;
            }
        };
    }
}
