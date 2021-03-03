package com.karlialag.app.Mqtt;

import com.karlialag.app.Database.DatabaseController;
import com.karlialag.app.Heatmaps.Heatmap;
import com.karlialag.app.Heatmaps.Values.HeatmapValues;
import org.eclipse.paho.client.mqttv3.*;
import com.karlialag.app.Heatmaps.Prediction;
import com.karlialag.app.ErrorCalculator.AverageError;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MqttHandler implements MqttCallback {

    private MqttClient client;
    private final String serverUrl;
    private final String BASE_URI = "karlialag/vehicle/";
    private final List<String> topics;
    String Message;
    DatabaseController dbController;
    Heatmap heatmap;
    private Prediction predicter = new Prediction();
    private AverageError dist = new AverageError();
    private double timestep;
    private double id;
    private double predLon, predLat;
    private double predRssi, predThrouput;
    private double distanceSum26;
    private double distanceSum27;




    public MqttHandler(String url, DatabaseController db, Heatmap heatmap) {
        this.serverUrl = url;
        this.topics = getTopics();
        this.dbController = db;
        this.heatmap = heatmap;
    }

    public void connect() {
        try {
            this.client = new MqttClient(serverUrl, MqttClient.generateClientId());
            //this.client = new MqttClient(serverUrl, MqttClient.generateClientId(), new MqttDefaultFilePersistence("/tmp"));
            System.out.println("Connecting to server with URL: " + serverUrl + " with clientId: " + client.getClientId());
            client.connect();
            client.setCallback(this);
        } catch (MqttException e) {
            System.out.println("Server not connected to broker with url: " + serverUrl);
            e.printStackTrace();
        }
    }

    public void sendMessage(String topic, String message) throws MqttException {
        MqttMessage msg = new MqttMessage();
        msg.setPayload(message.getBytes());
        client.publish(topic, msg);
    }

    public void subscribe() {
        for (String topicIdentifier : topics) {
            String topic = BASE_URI + topicIdentifier;
            try {
                client.subscribe(topic);
                System.out.println("Just subscribed to topic: " + topic);
            } catch (MqttException e) {
                System.out.println("Failed to subscribe to topic: " + topic);
                e.printStackTrace();
            }
        }
    }

    public void disconnect() throws MqttException {
        this.client.disconnect();
    }

    private List<String> getTopics() {
        List<String> topics = new ArrayList<>();
        File dir = new File("resources");
        File[] files = dir.listFiles();

        for (File file : files) {
            String vehicle_id = file.getName().replaceAll("[^0-9]", "");
            if (!vehicle_id.isEmpty()) {
                topics.add(vehicle_id);
            }
        }
        return topics;
    }

    @Override
    public void connectionLost(Throwable cause) {
        // TODO Auto-generated method stub
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws MqttException {

        Message = message.toString();
        if(Message.equals("EOT26")){
            dist.averageErrorValue(this.distanceSum26,this.timestep);
            return;
        }
        else if(Message.equals("EOT27")){

            dist.averageErrorValue(this.distanceSum27,this.timestep);
            return;
        }


            Message = Message.trim();


            String[] fields = Message.split(",");
            String timestep = fields[0];
            String d_id = fields[1];
            String lon = fields[2];
            String lat = fields[3];
            String angle = fields[4];
            String speed = fields[5];
            String rssi = fields[6];
            String thput = fields[7];


            Double lonVal = Double.parseDouble(lon);
            Double latVal = Double.parseDouble(lat);
            Double speedVal = Double.parseDouble(speed);
            Double angleVal = Double.parseDouble(angle);
            Double rssiVal = Double.parseDouble(rssi);
            Double throughputVal = Double.parseDouble(thput);
            double timestepVal = Double.parseDouble(timestep);
            double Id = Double.parseDouble(d_id);


        if(Id != 0.0) {

            System.out.println("Topic: " + topic + "Arrived Message: " + message);

            double predictions[] = predicter.PredictionImplementation(latVal, lonVal, angleVal, speedVal);
            double predictedLat = predictions[0];
            double predictedLon = predictions[1];
            this.timestep = timestepVal;
            this.id = Id;

            double predictedRssi = heatmap.RssiFinder(predictedLat, predictedLon);
            double predictedThroughput = heatmap.ThroughputFinder(predictedLat, predictedLon);


            if( (predictedRssi !=-1) || (predictedThroughput != -1)) {


               this.dbController.insertToDB(timestepVal, Id, latVal, lonVal, this.predLat, this.predLon,rssiVal,throughputVal,this.predRssi,this.predThrouput);

            }

            String sendPredLat = String.valueOf(predictedLat);
            String sendPredLon = String.valueOf(predictedLon);
            String sendTimestep = String.valueOf(timestepVal + 1.0);
            String sendPredRssi = String.valueOf(predictedRssi);
            String sendPredThput = String.valueOf(predictedThroughput);
            String sendId = String.valueOf(0.0);
            String sendAngle = String.valueOf(-1.0);
            String sendSpeed = String.valueOf(-1.0);

            String[] sent = new String[]{sendTimestep, sendId, sendPredLon, sendPredLat, sendAngle, sendSpeed, sendPredRssi, sendPredThput};
            String toSentArray = String.join(",", sent);

            if(timestepVal != 0.0) {

                double distance = this.dist.distance(this.predLat,this.predLon,latVal,lonVal);
                distance = distance * 10000;

                if (Id == 26) {

                    this.distanceSum26 += distance;
                }
                if (Id == 27) {
                    this.distanceSum27 += distance;
                }
            }

            this.predLat = predictedLat;
            this.predLon = predictedLon;
            this.predRssi = predictedThroughput;
            this.predThrouput = predictedThroughput;

            sendMessage(topic, toSentArray);
        }
        else{
            System.out.println("Server Message");
        }





    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // TODO Auto-generated method stub
    }
}