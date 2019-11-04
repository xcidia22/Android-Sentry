package com.example.meeko.sentry;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {

    public static boolean wasScreenOn = true;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.e("LOBXCD","onReceive");
        Intent intent1 = new Intent(context, SOSButton.class);
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // do whatever you need to do here
            Log.e("LOBXCD","onReceive1");
            wasScreenOn = false;

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            wasScreenOn = true;
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
            Log.e("LOBXCD","onReceive2");



        }else if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            Log.e("LOBXCD","onReceive3");
        }
    }
}