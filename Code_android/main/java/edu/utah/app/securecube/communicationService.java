package edu.utah.app.securecube;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;

public class communicationService extends Service {
    ServerSocket s;
    Socket s_a;
    BufferedReader br;
    static BufferedWriter bsender;
    final static String MY_ACTION = "MY_ACTION";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onCreate(){
        try {
            Thread t = new Thread(new Test());
            t.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    class Test implements Runnable {

        @Override
        public void run() {
            try {
                s = new ServerSocket(4040);
                s.setReuseAddress(true);

            }
            catch (Exception e){
                e.printStackTrace();
            }
            try {
                s_a = s.accept();
                br = new BufferedReader(new InputStreamReader(s_a.getInputStream()));
                bsender = new BufferedWriter(new OutputStreamWriter(s_a.getOutputStream()));
                String command;
                if((command = br.readLine())!=null)
                    System.out.println(command);

                Intent intent = new Intent();
                intent.setAction(MY_ACTION);

                intent.putExtra("DATAPASSED", command);

                sendBroadcast(intent);

                //
                String token = "pattern";
                String message;
                MessageDigest digest = null;
                try {
                    digest = MessageDigest.getInstance("SHA256");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    digest.reset();
                    // while(token == null){}
                    message = digest.digest((command + token).getBytes()).toString();
                    Log.e("PANKAJ", message);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                    /*Intent intent = new Intent();
                    intent.setAction(communicationService.MY_ACTION);

                    intent.putExtra("DATAREPLY", message);

                    sendBroadcast(intent);*/
                br.close();
                Log.e("Pankaj", command);
                //MyReceiver.onReceive();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //private class MyReceiver extends BroadcastReceiver {


    //}
}
