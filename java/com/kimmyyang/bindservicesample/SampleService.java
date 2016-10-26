package com.kimmyyang.bindservicesample;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.Objects;

public class SampleService extends Service {

    private final String TAG = "SampleService";
    public static final int EVENT_REGISTER = 0;
    public static final int EVENT_REGISTER_DONE = 1;
    Messenger mMessenger = null;//service Messenger
    HandlerThread mThread = null;
    ServiceHandler mHandler = null;
    Messenger mClient = null;

    private class ServiceHandler extends Handler{
        ServiceHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(Message msg){
            Log.d(TAG, "handleMessage msg = " + msg.what);
            switch (msg.what){
                case EVENT_REGISTER:
                    mClient = msg.replyTo;
                    try {
                        mClient.send(obtainMessage(EVENT_REGISTER_DONE));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
            }
        }
    }

    public SampleService() {
        mThread = new HandlerThread("service_thread");
        mThread.start();//If this thread not been started, getLooper will return null
        mHandler = new ServiceHandler(mThread.getLooper());
        mMessenger = new Messenger(mHandler);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        if(mMessenger != null){
            return mMessenger.getBinder();
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
