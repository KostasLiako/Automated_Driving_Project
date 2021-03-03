package com.example.android_app.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {
    static final String TAG = "NETWORK_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isConnected(context)) {
            Log.i(TAG, "Device connected to network!");
            //Toast.makeText(context, "Network connected", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "Device NOT connected!");
            Toast.makeText(context, "Network connection lost", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isConnected(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = (nInfo != null && nInfo.isAvailable() && nInfo.isConnected());
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

}
