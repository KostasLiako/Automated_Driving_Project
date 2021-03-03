package com.example.android_app.mqttHandler;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Publisher is a specific runnable which is executed for publishing all the lines
 * of the csv file to the server
 */
public class Publisher extends AsyncTask<String, Integer, String> {
    static final String TAG = "PUBLISHER";

    private MqttAndroidClient client;
    private String topic;
    private Integer maxTime;

    /**
     * Publishes a message to a topic on the server. Takes an
     * {@link MqttMessage} message and delivers it to the server at the
     * requested quality of service.
     *
     * @param client  MqqtAndroidClient which was connected to the serer
     * @param topic   The topic to which we will publish messages from the android client
     * @param maxTime maxTime holds the integer values of  max seconds specified by the
     *                user for transmitting messages
     */
    public Publisher(MqttAndroidClient client, String topic, Integer maxTime) {
        this.client = client;
        this.topic = topic;
        this.maxTime = maxTime;
    }

    @Override
    protected String doInBackground(String... strings) {
        String csvFileName = strings[0];
        String row;
        Integer secondsPassed = 0;

        try {
            File csvPath = Environment.getExternalStorageDirectory();
            Log.i(TAG, "Searching in path " + csvPath.getName() + " for file " + csvFileName);
            File file = new File(csvPath, csvFileName);

            final BufferedReader csvReader = new BufferedReader(new FileReader(file));
            //Log.i(TAG, "Succesfully read file!");
            //Log.i(TAG, String.valueOf(Thread.currentThread().getId()));

            csvReader.readLine();   // Skip headers
            Log.i(TAG, "Skipped headers!");

            // In case max time has been specified secondsPassed <= maxTime will end the while loop
            // In case max time hasn't been specified maxTime==-1 and while loop while end when the file ends
            while (((row = csvReader.readLine()) != null) && (secondsPassed < maxTime || maxTime == -1)) {
                Thread.sleep(1000);
                publishMessage(row);
                secondsPassed++;
            }
            sendEndMessage();
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
        Log.i(TAG, "FINISHED PUBLISHING");
        return null;
    }

    private void publishMessage(String payload) {
        try {
            Log.i(TAG, "Publishing message " + payload);
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            client.publish(topic, message);
        } catch (MqttException e) {

            Log.e(TAG, e.getMessage());
        }
    }

    public void sendEndMessage() {
        String endMessage = "EOT" + topic.substring(topic.lastIndexOf("/") + 1);;

        try {
            Log.i(TAG, "Sending end message!" + endMessage);
            MqttMessage message = new MqttMessage();
            message.setPayload(endMessage.getBytes());
            client.publish(topic, message);
        } catch (MqttException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
