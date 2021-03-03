package com.karlialag.app;

import com.karlialag.app.Server.Server;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class App {
    private static final String mqttBrokerUrl = "tcp://test.mosquitto.org:1883";
    private static final String databaseUrl = "jdbc:mysql://localhost:3306/test";
    private static final String databaseUsername = "root";
    private static final String databasePassword = "root";  //Thanasis
//    private static final String databasePassword = "12345678";  //Kwstas

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        Server server = new Server(mqttBrokerUrl, databaseUrl, databaseUsername, databasePassword);
        server.initializeMqttConnections();
    }
}

