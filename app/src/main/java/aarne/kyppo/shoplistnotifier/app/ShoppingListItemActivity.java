package aarne.kyppo.shoplistnotifier.app;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;


public class ShoppingListItemActivity extends ActionBarActivity {

    ListView lv;
    EditText add_item_widget;
    Button add_item_buttom;
    List<ShoppingListItem> items;
    int shoppinglist_id;

    DBHelper helper = new DBHelper(this);
    Adapter a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_item);

        lv = (ListView) findViewById(R.id.list_item_view);
        add_item_widget = (EditText) findViewById(R.id.add_item_widget);
        add_item_buttom = (Button) findViewById(R.id.add_item_button);

        items = new LinkedList<ShoppingListItem>();

        if(!getIntent().hasExtra("list_id"))
        {
            Toast.makeText(this,"No Shoppinglist given.",Toast.LENGTH_SHORT).show();
            finish();
        }
        shoppinglist_id = getIntent().getExtras().getInt("list_id");
        items = helper.getListItems(shoppinglist_id);

        a = new Adapter(this,android.R.layout.simple_list_item_1,items);
        lv.setAdapter(a);

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
                                ArrayAdapter<ShoppingListItem> adapter = (ArrayAdapter<ShoppingListItem>) listView.getAdapter();
                                for (int position : reverseSortedPositions) {
                                    if(helper.removeShoppingListItem(adapter.getItem(position)))
                                    {
                                        adapter.remove(adapter.getItem(position));
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
        lv.setOnTouchListener(touchListener);
    }

    protected void onStop()
    {
        super.onStop();
        helper.addListItems(items);
    }

    public void newItem(View v)
    {
        String itemtext = add_item_widget.getText().toString();
        ShoppingListItem item = new ShoppingListItem();
        item.setName(itemtext);
        item.setList_id(shoppinglist_id);
        items.add(item);
        add_item_widget.setText("");
        a.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_list_item, menu);
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

    private class Adapter extends ArrayAdapter<ShoppingListItem>
    {
        Context ctx;
        List<ShoppingListItem> list;
        public Adapter(Context context, int resource, List<ShoppingListItem> objects) {
            super(context, resource, objects);
            ctx = context;
            list = objects;
        }
        public View getView(int pos, View v, ViewGroup root)
        {
            LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowview = li.inflate(R.layout.shopping_list_item,root,false);
            TextView tv = (TextView) rowview.findViewById(R.id.shoppinglistlabel);
            tv.setText(list.get(pos).getName());
            return rowview;
        }
    }
}
