package aarne.kyppo.shoplistnotifier.app;

import android.content.Context;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        ListView lv = (ListView) findViewById(R.id.list);

        final DBHelper helper = new DBHelper(this);

        shoppinglists = helper.getShoppingLists();

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
}
