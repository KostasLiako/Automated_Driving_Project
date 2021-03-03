package com.karlialag.app.Parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class XmlConverter {
    public void convertXmlToCsvFiles() throws ParserConfigurationException, IOException, SAXException {
        System.out.println("Converting From Xml to Csv in progress...");

        List<String> csvHeaders = Arrays.asList("timestep", "id", "long", "lat", "angle", "speed", "RSSI", "throughput");
        List<String> xmlAttributes = Arrays.asList("id", "x", "y", "angle", "speed");

        File dir = new File("resources");
        File[] xmlFileList = dir.listFiles();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Iterate all xml files
        for (File xmlFile : xmlFileList) {
            Document doc = builder.parse(new File("resources/" + xmlFile.getName()));
            doc.getDocumentElement().normalize();

            // Create csv name by changing the extension of the xml file to csv
            String csvFileName = xmlFile.getName().replaceFirst("[.][^.]+$", "") + ".csv";

            FileWriter csvFile = new FileWriter("csvFiles/" + csvFileName);
            NodeList nodeList = doc.getElementsByTagName("timestep");

            // Add headers to csv
            csvFile.append(String.join(",", csvHeaders));
            csvFile.append("\n");

            // Iterate timestep tags in xml
            for (int itr = 0; itr < nodeList.getLength(); itr++) {
                Node node = nodeList.item(itr);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element step = (Element) node;
                    NodeList vehicleList = step.getElementsByTagName("vehicle");

                    // Iterate vehicle tags inside timestep tags
                    for (int i = 0; i < vehicleList.getLength(); i++) {
                        Node vehicleNode = vehicleList.item(i);

                        if (vehicleNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element vehicle = (Element) vehicleNode;
                            Random random = new Random();
                            double number = random.nextGaussian();
                            double rssi = number * 10 + 60;     //RSSI = standardDeviation * num + mean
                            double throughput = (rssi / 100) * 50;

                            csvFile.append(String.valueOf(step.getAttribute("time")));
                            csvFile.append(",");

                            for (String attribute : xmlAttributes) {
                                csvFile.append(String.valueOf(vehicle.getAttribute(attribute)));
                                csvFile.append(",");
                            }
                            csvFile.append(String.valueOf(rssi));
                            csvFile.append(",");
                            csvFile.append(String.valueOf(throughput));
                            csvFile.append("\n");
                        }
                    }
                }
            }
            csvFile.flush();
            csvFile.close();
        }
    }
}
