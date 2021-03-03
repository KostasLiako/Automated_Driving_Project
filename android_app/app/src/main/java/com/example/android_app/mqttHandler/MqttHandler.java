package com.example.android_app.mqttHandler;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MqttHandler {
    static final String TAG = "MQTT_HANDLER";
    private final String UPDATE_MAP = "com.example.android_app.UPDATE_MAP";
    private final String UPDATE_PREDICTION_MAP = "com.example.android_app.UPDATE_PREDICTION_MAP";
    private final String CSV_FILE = getCSVFile();

    private String BASE_TOPIC = "karlialag/vehicle/" + getBaseTopic();

    private Integer maxTime;
    private Context context;
    private MqttAndroidClient client;
    private Publisher publisher;

    public MqttHandler(Context cntxt, String url, int maxSecs, final GoogleMap map) {
        maxTime = maxSecs;
        context = cntxt;
        client = new MqttAndroidClient(context, url, MqttClient.generateClientId());
        publisher = new Publisher(client, getPublishTopic(), maxTime);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i(TAG, "connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Hashtable<String,String> values = getMessageValues(new String(message.getPayload()));

                //Log.i(TAG, "Received message with values: timestep:" + values.get("timestep") + " latitude: " + values.get("latitude") + " longitude: " + values.get("longitude") + " rssi: " + values.get("rssi") + " throughput: " + values.get("throughput"));

                Intent updateIntent = new Intent();
                updateIntent.setAction(UPDATE_MAP);
                updateIntent.putExtra("timestep", values.get("timestep"));
                updateIntent.putExtra("longitude", values.get("longitude"));
                updateIntent.putExtra("latitude", values.get("latitude"));
                updateIntent.putExtra("rssi", values.get("rssi"));
                updateIntent.putExtra("throughput", values.get("throughput"));
                updateIntent.putExtra("speed", values.get("speed"));
                updateIntent.putExtra("angle", values.get("angle"));

                context.sendBroadcast(updateIntent);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                //Log.i(TAG, "msg delivered");
            }
        });
        connect();
    }

    private Hashtable<String,String> getMessageValues(String message) {
        Hashtable<String, String> messageHt = new Hashtable<>();
        String[] arrOfStr = message.split(",");

        messageHt.put("timestep", arrOfStr[0]);
        messageHt.put("id", arrOfStr[1]);
        messageHt.put("longitude", arrOfStr[2]);
        messageHt.put("latitude", arrOfStr[3]);
        messageHt.put("angle", arrOfStr[4]);
        messageHt.put("speed", arrOfStr[5]);
        messageHt.put("rssi", arrOfStr[6]);
        messageHt.put("throughput", arrOfStr[7]);

        return messageHt;
    }

    /*
    Connect client to MQTT broker
     */
    public void connect() {
        try {
            client.connect(context, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "Connect succeeded");

                    subscribe();
                    publishMessages();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(context, "Connection to server failed.\nCheck your IP and port!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Connect failed");
                }
            });
        } catch (MqttException e) {
            Log.i(TAG, "Failed connecting!");
            Log.e(TAG, String.valueOf(e.getStackTrace()));
        }
    }

    /*
    Subscribe to specific topic for the device
     */
    public void subscribe() {
        final String topic = getSubscribeTopic();
        try {
            client.subscribe(getSubscribeTopic(), 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "Subscribed successfully to topic: " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "Subscribing to topic: " + topic + " has failed!");
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, String.valueOf(e.getStackTrace()));
        }
    }

    /*
    Start asyncTask that publishes every second
     */
    public void publishMessages() {
        if (!client.isConnected()) {
            connect();
        }
        publisher.execute(CSV_FILE);
    }

    /*
    Stop asyncTask (stop publishing info)
     */
    public void stopPublishing() {
        publisher.cancel(true);
        disconnect();

        Toast.makeText(context, "Stopped publishing", Toast.LENGTH_SHORT).show();
    }

    /*
    Disconnect from broker
     */
    public void disconnect() {
        if (client.isConnected()) {
            sendEndMessage();
            try {
                client.disconnect();
            } catch (MqttException e) {
                Log.e(TAG, String.valueOf(e.getStackTrace()));
            }
        } else {
            Log.i(TAG, "Not connected to server in order to disconnect!");
        }
    }

    //Find the csv file in the form of vehicle_<DEVICE_ID>.csv
    private String getCSVFile() {
        String csvFile = "";
        File dir = Environment.getExternalStorageDirectory();
        File[] files = dir.listFiles();

        for (File file : files) {
            System.out.println(file.getName());
            if (file.getName().matches("vehicle_\\d+.csv")) {
                csvFile = file.getName();
                break;
            }
        }
        if (csvFile.isEmpty()) {
            Log.e(TAG, "File in the form of \"vehicle_*.csv\" NOT FOUND!");
        }
        return csvFile;
    }

    //Find the id from the topic in order to
    private String getBaseTopic() {
        String topicName = "";
        try {
            Log.i(TAG, "Csv file path: " + Environment.getRootDirectory() + "/" + CSV_FILE);

            File csvPath = Environment.getExternalStorageDirectory();
            File file = new File(csvPath, CSV_FILE);
            BufferedReader csvReader = new BufferedReader(new FileReader(file));

            csvReader.readLine();
            String row = csvReader.readLine();     // Get second line which has the vehicle id
            String[] cols = row.split(",");

            topicName = cols[1];     // Get second column with the vehicle id
            Log.i(TAG, "Topic name is: " + topicName);

            csvReader.close();
        } catch (IOException e) {
            Log.e(TAG, String.valueOf(e.getStackTrace()));
        }
        return topicName;
    }

    private String getSubscribeTopic() {
        return BASE_TOPIC;
    }

    private String getPublishTopic() {
        return BASE_TOPIC;
    }

    public void sendEndMessage() {
        String endMessage = "EOT" + BASE_TOPIC.substring(BASE_TOPIC.lastIndexOf("/") + 1);;

        try {
            Log.i(TAG, "Sending end message!" + endMessage);
            MqttMessage message = new MqttMessage();
            message.setPayload(endMessage.getBytes());
            client.publish(BASE_TOPIC, message);
        } catch (MqttException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
