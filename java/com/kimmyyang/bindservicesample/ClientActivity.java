package com.kimmyyang.bindservicesample;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ClientActivity extends Activity {

    private final String TAG = "ClientActivity";
    private Messenger mService = null;
    private Messenger mClient = null;
    TextView mText = null;

    private class ClientHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            Log.d(TAG,"handleMessage msg = "+msg.what);
            switch (msg.what){
                case SampleService.EVENT_REGISTER_DONE:
                    mText = (TextView)findViewById(R.id.textView);
                    mText.setText(String.valueOf(System.currentTimeMillis()));
                    break;
                default:
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected");
            mService = new Messenger(service);
            mClient = new Messenger(new ClientHandler());
            Message msg = Message.obtain();
            msg.what = SampleService.EVENT_REGISTER;
            msg.replyTo = mClient;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        Button bind_button = (Button)findViewById(R.id.bind_button);
        bind_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientActivity.this, SampleService.class);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }
        });
        final Button unbind_button = (Button)findViewById(R.id.unbind_button);
        unbind_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mService!=null){
                    unbindService(mConnection);
                }
                mService = null;
                mText.setText("Unbind Success");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
