package com.karlialag.app.Server;

import com.karlialag.app.Database.DatabaseController;
import com.karlialag.app.Heatmaps.Heatmap;
import com.karlialag.app.Mqtt.MqttHandler;
import com.karlialag.app.Parser.XmlConverter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/*
This is the class of the server.
Here is where all the required functionality is happening.
 */
public class Server {
    final MqttHandler mqttController;
    final DatabaseController dbController;
    final XmlConverter xmlTocsv = new XmlConverter();
    final Heatmap heatmap = new Heatmap();

    public Server(String mqttBrokerUrl, String databaseUrl, String username, String password) throws IOException, SAXException, ParserConfigurationException {
        this.dbController = new DatabaseController(databaseUrl, username, password);
        dbController.initialiseDatabase();
        xmlTocsv.convertXmlToCsvFiles();
        heatmap.createHeatmaps();

        this.mqttController = new MqttHandler(mqttBrokerUrl,dbController,heatmap);
    }

    public void createCsvFiles() {
        XmlConverter x = new XmlConverter();
        try {
            x.convertXmlToCsvFiles();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    public void createHeatmaps() {
        Heatmap heatmap = new Heatmap();
        try {
            heatmap.createHeatmaps();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectAndInitialiseDb() {
        dbController.initialiseDatabase();
    }

    public void initializeMqttConnections() {
        mqttController.connect();
        mqttController.subscribe();
    }


}
