package edu.utah.app.securecube;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by pankaj on 12/5/15.
 */
public class MyReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // TODO Auto-generated method stub

        MainActivity.token = arg1.getStringExtra("DATAPASSED");

        String local_data = arg1.getStringExtra("DATAREPLY");
        int len = local_data.length();
            /*   MainActivity.this.getApplicationContext()
               */
        try {
            communicationService.bsender.write(local_data, 0, len);
        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
