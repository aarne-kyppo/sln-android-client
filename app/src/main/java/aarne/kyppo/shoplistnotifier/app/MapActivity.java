package aarne.kyppo.shoplistnotifier.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class MapActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Bundle extras = getIntent().getExtras();
        String data = "";
        if(extras != null)
        {
            data = extras.getString("data");
        }

        ArrayList<ShopNotification> notifications = parseData(data);

        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment)).getMap();

        LatLng mylocation = new LatLng(notifications.get(0).latitude, notifications.get(0).longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation,13));
        notifications.remove(0);

        for(ShopNotification n : notifications)
        {
            LatLng shoplocation = new LatLng(n.latitude,n.longitude);
            Log.d("LATITUDE",n.latitude + "");
            Log.d("LONGITUDE",n.longitude + "");
            map.addMarker(new MarkerOptions().title(n.shoptitle).position(shoplocation));
        }

        map.setMyLocationEnabled(true);

    }

    private ArrayList<ShopNotification> parseData(String data)
    {
        Log.d("DATA_BEFORE_PARSING",data);
        String[] notifications = data.split(";");
        Log.d("PART1",notifications[0]);
        Log.d("PART2",notifications[1]);
        ArrayList<ShopNotification> notification_objects = new ArrayList<ShopNotification>();
        for(String tmp : notifications)
        {
            ShopNotification n = new ShopNotification(tmp);
            notification_objects.add(n);
        }
        return notification_objects;
    }


    public class ShopNotification
    {
        double latitude;
        double longitude;
        String shoptitle;
        public ShopNotification(String data)
        {
            String[] particles = data.split("[+]");
            longitude = Double.parseDouble(particles[0]);
            latitude = Double.parseDouble(particles[1]);
            shoptitle = particles[2];
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
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
}
