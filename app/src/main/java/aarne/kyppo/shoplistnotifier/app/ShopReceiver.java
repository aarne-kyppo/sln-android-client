package aarne.kyppo.shoplistnotifier.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by aarnek on 28.5.2014.
 */
public class ShopReceiver extends ResultReceiver {
    private Receiver mreceiver;

    public ShopReceiver(Handler handler)
    {
        super(handler);
    }

    public void setReceiver(Receiver r)
    {
        mreceiver = r;
    }
    public interface Receiver {
        public void onReceiveResult(int status, Bundle b);
    }
    protected void onReceiveResult(int status, Bundle data)
    {
        if(mreceiver != null)
        {
            mreceiver.onReceiveResult(status,data);
        }
    }
}
