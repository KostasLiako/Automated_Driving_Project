package com.example.android_app;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.android_app.connectivity.NetworkChangeReceiver;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MAIN_ACTIVITY";

    private BroadcastReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        mNetworkReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! We are ok.
                } else {
                    // permission denied, boo!
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkReceiver);
    }

    private String getServerUrl() {
        EditText mServerIPEdit = (EditText) findViewById(R.id.server_ip_text);
        String serverIP = mServerIPEdit.getText().toString();

        EditText mServerPortEdit = (EditText) findViewById(R.id.server_port_text);
        String serverPort = mServerPortEdit.getText().toString();

        String serverUrl = "tcp://" + serverIP + ":" + serverPort;

        Log.i(TAG, "Server url is: " + serverUrl);
        return serverUrl;
    }

    private Integer getMaxTime() {
        Integer seconds;
        EditText mTimeEdit = (EditText) findViewById(R.id.max_time_text);

        if (mTimeEdit.getText().toString().isEmpty()) { // Case for not specified max time
            seconds = -1;
        } else {
            seconds = Integer.parseInt(mTimeEdit.getText().toString());
        }
        Log.i(TAG, "Max time is: " + seconds);
        return seconds;
    }

    /*
    Proceeds to maps showing predictions and real position in map
     */
    public void connect(View view) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        intent.putExtra("SERVER_URL", getServerUrl());
        intent.putExtra("MAX_TIME", getMaxTime().toString());
        startActivity(intent);
    }

    /*
    Verify that user wants to exit and exit
     */
    public void exit(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit");
        builder.setMessage("Do you want to exit ?");
        builder.setPositiveButton("Yes. Exit now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}