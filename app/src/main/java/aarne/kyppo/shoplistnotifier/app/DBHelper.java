package aarne.kyppo.shoplistnotifier.app;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by aarnek on 28.8.2014.
 */
public class DBHelper extends SQLiteOpenHelper{

    //TODO: Separate schema-classes for database tables. Current solution is not ideal.

    public static final int SHOPPINGLIST_VERSION = 5;
    public static final String SHOPPINGLIST_TABLE_NAME = "shoppinglist";
    public static final String DATABASE = "sln";

    public DBHelper(Context ctx){
        super(ctx,DATABASE,null,SHOPPINGLIST_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + SHOPPINGLIST_TABLE_NAME + "(id integer primary key NOT NULL, start TEXT NOT NULL, end TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        for(int idx=i;idx<=i2;idx++)
        {
            Log.d("ONUPGRADE","Database version: " + idx);
            switch(idx)
            {
                case 1:
                {
                    break;
                }
                case 4: {
                    String sql = "alter table " + SHOPPINGLIST_TABLE_NAME + " add column title text";
                    //sqLiteDatabase.execSQL(sql);
                    break;
                }
                case 5:{
                    String sql = "create table listitem (id integer primary key NOT NULL, list_id INTEGER NOT NULL, name TEXT NOT NULL, quantity integer NOT NULL, checked TINYINT(1) NOT NULL)";
                }
            }
        }
    }

    public List<ShoppingList> getShoppingLists()
    {
        List<ShoppingList> list = new LinkedList<ShoppingList>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select id,start,end,title from " + SHOPPINGLIST_TABLE_NAME + " order by time(start)";
        Cursor c = db.rawQuery(query,null);
        ShoppingList shp = null;
        if(c.moveToFirst())
        {
            do{
                shp = new ShoppingList();
                shp.setId(c.getInt(0));
                shp.setStart(c.getString(1));
                shp.setEnd(c.getString(2));
                shp.setTitle(c.getString(3));
                Log.d("Start", c.getString(2));
                list.add(shp);
            }while(c.moveToNext());
        }
        return list;
    }
    public ShoppingList getShoppingList(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select id,start,end,title from " + SHOPPINGLIST_TABLE_NAME + " where id = " + id;
        Cursor c = db.rawQuery(query,null);
        ShoppingList shp = null;
        if(c.moveToFirst())
        {
            shp = new ShoppingList();
            shp.setId(c.getInt(0));
            shp.setStart(c.getString(1));
            shp.setEnd(c.getString(2));
            shp.setTitle(c.getString(3));
        }
        return shp;
    }
    public int getEarliestShoppingListID()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select id from " + SHOPPINGLIST_TABLE_NAME + " order by time(start) LIMIT 1";
        Log.d("EARLIEST LIST QUERY",query);
        Cursor c = db.rawQuery(query,null);
        int shp = -1;
        //TODO: Check for empty list
        if(c.moveToFirst())
        {
            shp = c.getInt(0);

        }
        return shp;
    }
    public void addShoppingList(ShoppingList sl)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query;
        if(sl.getTitle() == "")
        {
            query = "INSERT INTO " + SHOPPINGLIST_TABLE_NAME + " (id,start,end) values (null, \"" + sl.getStartString() + "\",\"" + sl.getEndString() + "\")";
        }
        else
        {
            query = "INSERT INTO " + SHOPPINGLIST_TABLE_NAME + " (id,start,end, title) values (null, \"" + sl.getStartString() + "\",\"" + sl.getEndString() + "\", \"" + sl.getTitle() + "\")";
        }
        Log.d("INSERT QUERY",query);
        db.execSQL(query);
    }
    public boolean removeShoppingList(ShoppingList sl)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + SHOPPINGLIST_TABLE_NAME + " where id=" + sl.getId();
        Log.d("DELETE QUERY",query);
        try {
            db.execSQL(query);
            return true;
        } catch (SQLException e) {
            Log.d("REMOVE-LIST", "SQL-EXCEPTION");
            e.printStackTrace();
            return false;
        }
    }
}
