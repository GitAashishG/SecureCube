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
        Log.e("SERVICE", "Inside onStartCommand");
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onCreate(){
        try {
            Log.e("SERVICE", "Inside onCreate");
            Thread t = new Thread(new Test());
            t.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        onDestroy();
    }
    @Override
    public void onDestroy() {
        Log.e("SERVICE", "Inside onDestroy()");
        super.onDestroy();
        Toast.makeText(this, "Response Sent", Toast.LENGTH_LONG).show();
    }

    class Test implements Runnable {

        @Override
        public void run() {
            Log.e("SERVICE", "Inside run");
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

                String command;
                if((command = br.readLine())!=null)
                    System.out.println(command);

                Log.e("COMMAND", command);
                String message;
                MessageDigest digest = null;
                try {
                    digest = MessageDigest.getInstance("SHA256");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    digest.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String p = "Pank\n";
                //message = digest.digest((command + MainActivity.mypattern).getBytes()).toString();
                message = digest.digest((command + MainActivity.mypattern).getBytes()).toString();
                //message = digest.digest((command).getBytes().toString());
                Log.e("PANKAJ", message);
                bsender = new BufferedWriter(new OutputStreamWriter(s_a.getOutputStream()));
                bsender.write(message, 0,message.length());
                bsender.newLine();
                bsender.flush();
                br.close();
                bsender.close();
                Log.e("Pankaj", p);
                s_a.close();
                s.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            //onDestroy();
        }
    }

}
