package aarne.kyppo.shoplistnotifier.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity{

    TextView display;
    Button startloc;
    ShopReceiver r;
    boolean bound = false;
    Messenger mservice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display = (TextView) findViewById(R.id.display);
        startloc = (Button) findViewById(R.id.startloc);
    }

    public void start_locating(View v)
    {
        ToggleService();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class IncomingHandler extends Handler
    {
        boolean mapdisplayed = true;
        public void handleMessage(Message msg)
        {
            Log.d("ON_HANDLER","ooajfoisdjfgovfd");
            switch (msg.what)
            {
                case LocationService.INCOMING_DATA:
                    String data = msg.getData().getString("data");
                    display.setText(data);
                    if(!mapdisplayed)
                    {
                        Intent i = new Intent(MainActivity.this,MapActivity.class);
                        i.putExtra("data",data);
                        startActivity(i);
                        mapdisplayed = true;
                    }
            }
        }
    }
    final Messenger messenger = new Messenger(new IncomingHandler());

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mservice = new Messenger(iBinder);
            Message m = Message.obtain(null, LocationService.REGISTER_CLIENT);
            m.replyTo = messenger;
            try {
                mservice.send(m);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Message m = Message.obtain(null, LocationService.UNREGISTER_CLIENT);
            m.replyTo = messenger;
            try {
                mservice.send(m);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.d("UNREGISTER FAILED","");
            }
            Log.d("FFF","fsdogikjdfijfk");
        }
    };
    private void ToggleService()
    {
        if(bound)
        {
            unbindService(conn);
            startloc.setText("Bind service");
            bound = false;
        }
        else
        {
            bindService(new Intent(MainActivity.this,LocationService.class),conn,Context.BIND_AUTO_CREATE);
            startloc.setText("Unbind service");
            bound = true;
        }
    }
}
