package aarne.kyppo.shoplistnotifier.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by aarnek on 28.5.2014.
 */
public class LocationService extends Service {

    static final int REGISTER_CLIENT = 0;
    static final int UNREGISTER_CLIENT = 2;
    static final int INCOMING_DATA = 1;
    static final int EARLIEST_LIST = 3;
    static final int STORE_FOUND = 4;

    PrintWriter out;

    LocationManager man;
    LocationListener ls;
    LocationProvider p;
    Messenger client;
    Thread client_thread;
    Socket socket;
    int index = 0;
    int delay = 1000 * 1;
    int earliestid;

    boolean locating = false;
    Handler h = new Handler();

    private static final String SERVER_IP = "37.59.50.16";
    private static final int PORT = 1234;


    public void onDestroy() {
        Log.d("ONDESTROY", "sdopgbkfdpogk");
        try {
            out.println("endService");
            man.removeUpdates(ls);
            client_thread.stop();
            socket.close();
            Log.d("UNREGISTER", "sdfsd");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("DDD", "fdsgfdg");
    }

    class MessageHandler extends Handler {
        public void handleMessage(Message msg) {
            Log.d("MSG", msg.what + "");
            switch (msg.what) {
                case REGISTER_CLIENT:
                    client = msg.replyTo; //For ShoppingListActivity
                    Log.d("SERVICE", "client registered.");
                    client_thread = new Thread(new ClientSocket());
                    client_thread.start();
                    //Setting location listener. On location changed, callback send coordinates to server. NO PRODUCTION USE WITHOUT ENCRYPTION!
                    ls = new LocationListener() {
                        public void onLocationChanged(Location loc) {
                            String txt = "(" + loc.getLatitude() + "," + loc.getLongitude() + ")";
                            Message m = Message.obtain(null, INCOMING_DATA);
                            Bundle b = new Bundle();
                            Log.d("SSSSSSSS", "opfgkjdfpogkpodf");
                            out.println(loc.getLatitude() + ";" + loc.getLongitude()); //Sending location to server.
                        }

                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onProviderDisabled(String provider) {
                        }

                    };

                    break;
                case EARLIEST_LIST://Earliest list for timeout. Not timecounting for every list.
                    EndLocating();
                    earliestid = msg.getData().getInt("earliest");
                    Log.d("Earliestid",earliestid + "");
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            StartLocating();
                        }
                    },1000);//At first using static value.
            }
        }
    }

    final Messenger messenger = new Messenger(new MessageHandler());

    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    public void EndLocating()
    {
        if(locating)
        {
            man.removeUpdates(ls);
        }
        locating = false;
    }

    public void StartLocating() {
        man = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //providertxt = man.getBestProvider(criteria,true);
        p = man.getProvider(LocationManager.GPS_PROVIDER);
        Log.d("Here", "I am");

        int delay = 1000 * 60;
        man.requestLocationUpdates(LocationManager.GPS_PROVIDER, delay, 0, ls);
        locating = true;
    }

    public class ClientSocket implements Runnable {

        @Override
        public void run() {
            InetAddress server = null;
            BufferedReader in = null;
            try {
                server = InetAddress.getByName(SERVER_IP);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                socket = new Socket(server, PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            } catch (IOException e) {
                Log.d("OUTPUT", "FAILED");
                e.printStackTrace();
            }

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            char[] buffer = new char[2048];
            int charsred = 0;
            try {
            while((charsred = in.read(buffer)) != -1) {
                String str = new String(buffer).substring(0,charsred);
                Log.d("OHOHOHOH", str);
                Message m = Message.obtain(null, STORE_FOUND);
                Bundle b = new Bundle();
                b.putString("store",str);
                b.putInt("listid",earliestid);
                m.setData(b);
                try {
                    client.send(m);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
