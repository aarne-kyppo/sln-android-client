package aarne.kyppo.shoplistnotifier.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ShoppingListActivity extends ActionBarActivity {

    private List<ShoppingList> shoppinglists;

    private final int DELTA = 50;
    private float prevX = Float.NaN;
    private boolean bound = false;
    private Messenger mservice;

    final DBHelper helper = new DBHelper(this);

    private ServiceConnection conn;
    public class IncomingHandler extends Handler
    {
        boolean mapdisplayed = true;
        public void handleMessage(Message msg)
        {
            Log.d("ON_HANDLER","ooajfoisdjfgovfd");
            switch (msg.what)
            {
                case LocationService.INCOMING_DATA:
                    String data = msg.getData().getString("data");
                    /*if(!mapdisplayed)
                    {
                        Intent i = new Intent(ShoppingListActivity.this,MapActivity.class);
                        i.putExtra("data",data);
                        startActivity(i);
                        mapdisplayed = true;
                    }*/
                    break;
                case LocationService.STORE_FOUND:
                    String store = msg.getData().getString("store");

                    ShoppingList sl = helper.getShoppingList(msg.getData().getInt("id"));

                    notifyOfStore("Store found",store,sl);
            }
        }
    }

    final Messenger messenger = new Messenger(new IncomingHandler());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        ListView lv = (ListView) findViewById(R.id.list);

        conn = new ServiceConnection() {
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


        shoppinglists = helper.getShoppingLists();

        if(shoppinglists.size() > 0)
        {
            BindService();
        }
        else
        {
            UnbindService();
        }

        Adapter adapter = new Adapter(this,android.R.layout.simple_list_item_1, shoppinglists);
        lv.setAdapter(adapter);

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lv,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                ArrayAdapter<ShoppingList> adapter = (ArrayAdapter<ShoppingList>) listView.getAdapter();
                                for (int position : reverseSortedPositions) {
                                    if(helper.removeShoppingList(adapter.getItem(position)))
                                    {
                                        adapter.remove(adapter.getItem(position));
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
        lv.setOnTouchListener(touchListener);

        //Inform service about earliest shoppinglist.
        if(getIntent().hasExtra("earliest")) {
            Message message = Message.obtain(null,LocationService.EARLIEST_LIST);
            Bundle b = new Bundle();
            Log.d("EARLIEST ID EXTRA",getIntent().getExtras().getInt("earliest")+"");
            b.putInt("id",getIntent().getExtras().getInt("earliest"));
            message.setData(b);
            try {
                mservice.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void newShoppingList(View v)
    {
        Intent i = new Intent(this,NewShoppingListActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shopping_list, menu);
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

    private class Adapter extends ArrayAdapter<ShoppingList>
    {
        Context ctx;
        List<ShoppingList> list;
        public Adapter(Context context, int resource, List<ShoppingList> objects) {
            super(context, resource, objects);
            ctx = context;
            list = objects;
        }
        public View getView(int pos, View v, ViewGroup root)
        {
            LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowview = li.inflate(R.layout.shopping_list_item,root,false);
            TextView tv = (TextView) rowview.findViewById(R.id.shoppinglistlabel);
            tv.setText(list.get(pos).getStartString() + " " + list.get(pos).getTitle());
            return rowview;
        }
    }
    private void UnbindService()
    {
        if(bound)
        {
            unbindService(conn);
            bound = false;
        }
    }
    private void BindService()
    {
        if(!bound)
        {
            bindService(new Intent(ShoppingListActivity.this, LocationService.class), conn, Context.BIND_AUTO_CREATE);
            bound = true;
        }
    }



    public void notifyOfStore(String title, String store, ShoppingList sl)
    {
        NotificationCompat.Builder b = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.common_signin_btn_icon_dark).setContentTitle(title).setContentText(store);
        Intent i = new Intent(this,MainActivity.class);

        TaskStackBuilder taskbuilder = TaskStackBuilder.create(this);
        taskbuilder.addParentStack(MainActivity.class);
        taskbuilder.addNextIntent(i);
        PendingIntent resultpend = taskbuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        b.setContentIntent(resultpend);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,b.build());
    }

}
