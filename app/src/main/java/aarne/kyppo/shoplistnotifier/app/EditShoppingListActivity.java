package aarne.kyppo.shoplistnotifier.app;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;


public class EditShoppingListActivity extends ActionBarActivity {

    Button start_time;
    Button end_time;
    Button edit_items_button;
    Switch is_active;
    TextView titleview;
    boolean start_selected;

    String starttime;
    String endtime;
    String title;

    ShoppingList currentlist;
    DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shopping_list);
        int id = 0;
        if(getIntent().hasExtra("shoppinglist"))
        {
            id = getIntent().getExtras().getInt("shoppinglist");
        }
        else
        {
            finish();
        }

        start_time = (Button) findViewById(R.id.edit_start_time_button);
        end_time = (Button) findViewById(R.id.edit_end_time_button);
        is_active = (Switch) findViewById(R.id.edit_is_active);
        titleview = (TextView) findViewById(R.id.edit_title);
        edit_items_button = (Button) findViewById(R.id.edit_items_button);

        helper = new DBHelper(this);
        currentlist = helper.getShoppingList(id);
        starttime = currentlist.getStartString();
        endtime = currentlist.getEndString();
        title = currentlist.getTitle();


        setStartTime();
        setEndTime();
        setTitle();
        setIsActive(currentlist.isActive());

        Log.d("EDITSHOPPINGLIST",currentlist.getTitle());
    }

    public void startTimeDialog(View v)
    {
        start_selected = true;
        String[] parts = starttime.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        TimePickerDialog dlg = new TimePickerDialog(this,timelistener,hours,minutes,true);
        dlg.show();
    }
    public void endTimeDialog(View v)
    {
        start_selected = false;
        String[] parts = endtime.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        TimePickerDialog dlg = new TimePickerDialog(this,timelistener,hours,minutes,true);
        dlg.show();
    }

    public void editItems(View v)
    {
        Intent intent = new Intent(this,ShoppingListItemActivity.class);
        intent.putExtra("list_id",currentlist.getId());
        startActivity(intent);
    }

    public void setEndTime()
    {
        end_time.setText(endtime);
    }
    public void setStartTime()
    {
        start_time.setText(starttime);
    }
    public void setIsActive(boolean active)
    {
        is_active.setChecked(active);
    }
    public void setTitle()
    {
        titleview.setText(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_shopping_list, menu);
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

    private TimePickerDialog.OnTimeSetListener timelistener = new TimePickerDialog.OnTimeSetListener(){

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String strhour = (hourOfDay > 9) ? "" + hourOfDay : "0" + hourOfDay;
            String strminute = (minute > 9) ? "" + minute : "0" + minute;
            String newtime = strhour + ":" + strminute;
            if(start_selected)
            {
                starttime = newtime;
                start_time.setText(newtime);
            }
            else
            {
                endtime = newtime;
                end_time.setText(newtime);
            }
        }
    };
}
