package aarne.kyppo.shoplistnotifier.app;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by aarnek on 28.8.2014.
 */
public class ShoppingList {
    private int id;
    private Date start;
    private String start_str;
    private String end_str;
    private Date end;

    private String title;

    public String getTitle() {
        if(this.title != null)
        {
            return this.title;
        }
        else
        {
            return "";
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStart() {
        return start;
    }
    public String getStartString()
    {
        if(start_str != null)
        {
            return this.start_str;
        }
        else
        {
            return "No start time given";
        }
    }
    public String getEndString()
    {
        if(end_str != null)
        {
            return this.end_str;
        }
        else
        {
            return "No end time given";
        }
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setStart(int hours, int minutes)
    {
        String minute;
        String hour;
        if(minutes < 10)
        {
            minute = "0" + minutes;
        }
        else
        {
            minute = minutes + "";
        }
        if(hours < 10)
        {
            hour = "0" + hours;
        }
        else
        {
            hour = hours + "";
        }

        this.start_str = hour + ":" + minute;
    }

    public void setEnd(int hours, int minutes)
    {
        String minute;
        String hour;
        if(minutes < 10)
        {
            minute = "0" + minutes;
        }
        else
        {
            minute = minutes + "";
        }
        if(hours < 10)
        {
            hour = "0" + hours;
        }
        else
        {
            hour = hours + "";
        }

        this.end_str = hour + ":" + minute;
    }

    public void setStart(String start)
    {
        this.start_str = start;
    }

    public void setEnd(String end)
    {
        this.end_str = end;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
