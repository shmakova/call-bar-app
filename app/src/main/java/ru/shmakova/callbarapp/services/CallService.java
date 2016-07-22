package ru.shmakova.callbarapp.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import ru.shmakova.callbarapp.receivers.CallReceiver;

/**
 * Created by shmakova on 22.07.16.
 */

public class CallService extends Service {
    private CallReceiver callReceiver;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        callReceiver = new CallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(callReceiver, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service starting", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(callReceiver);
        Toast.makeText(this, "Service done", Toast.LENGTH_SHORT).show();
    }
}