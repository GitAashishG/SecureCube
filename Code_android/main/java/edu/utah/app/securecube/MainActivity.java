package edu.utah.app.securecube;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.security.MessageDigest;

import haibison.android.lockpattern.LockPatternActivity;
import haibison.android.lockpattern.utils.AlpSettings;

public class MainActivity extends AppCompatActivity {

    static final int REQ_CREATE_PATTERN = 1;
    static final int REQ_ENTER_PATTERN = 2;
    char[] savedPattern;
    MyReceiver myReceiver;
    static String token;
    char[] pattern;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlpSettings.Security.setAutoSavePattern(getApplicationContext(), true);


        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(communicationService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);

        //Start our own service
        intent = new Intent(this, communicationService.class);
        //startService(intent);

        LockPatternActivity.IntentBuilder.newPatternComparator(getApplicationContext(), savedPattern)
                .startForResult(MainActivity.this, REQ_ENTER_PATTERN);
    }

    /*protected void onStart() {
        // TODO Auto-generated method stub

        //Register BroadcastReceiver
        //to receive event from our service

        super.onStart();
    }*/

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        unregisterReceiver(myReceiver);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String msg = "JYOTI ";

        switch (requestCode) {
            case REQ_CREATE_PATTERN: {
                if (resultCode == RESULT_OK) {
                    pattern = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
                    String patternStr = new String(pattern);
                    savedPattern = pattern;
                    startService(intent);
                   // String password = "password";
                    //String message = "hello world";
                    /*MessageDigest digest = null;
                    try {
                        digest = MessageDigest.getInstance("SHA256");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    digest.reset();
                    while(token == null){}
                    String message = digest.digest((patternStr+token).getBytes()).toString();
                    Log.e("PANKAJ", message);

                    Intent intent = new Intent();
                    intent.setAction(communicationService.MY_ACTION);

                    intent.putExtra("DATAREPLY", message);

                    sendBroadcast(intent);*/
                }

                break;
            }// REQ_CREATE_PATTERN
            case REQ_ENTER_PATTERN: {
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e(msg, "PASSED");
                        break;
                    case RESULT_CANCELED:
                        // The user cancelled the task
                        break;
                    case LockPatternActivity.RESULT_FAILED:
                        // The user failed to enter the pattern
                        break;
                    case LockPatternActivity.RESULT_FORGOT_PATTERN:
                        // The user forgot the pattern and invoked your recovery Activity.
                        break;
                }

                /**
                 * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
                 * the number of tries that the user did.
                 */
                int retryCount = data.getIntExtra(LockPatternActivity.EXTRA_RETRY_COUNT, 0);

                break;
            }// REQ_ENTER_PATTERN
        }
    }
}
