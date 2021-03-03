package com.karlialag.app.Heatmaps.Graphics;

import com.karlialag.app.Heatmaps.HeatChart;
import com.karlialag.app.Heatmaps.Values.GridValues;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DrawHeatmap {
    public GridValues grid;
    public double[][] rssiGrids;
    public double[][] throughputGrids;
    private double cellHeight;
    private double cellWidth;
    BufferedImage img;


    public DrawHeatmap(GridValues gr) throws IOException {
        this.grid = new GridValues(gr);
        this.rssiGrids = new double[4][];
        this.throughputGrids = new double[4][];
        for (int i = 0; i < 4; ++i) {
            this.rssiGrids[i] = new double[10];
            this.throughputGrids[i] = new double[10];

        }
        this.rssiGrids = gr.rssi;
        this.throughputGrids = gr.throughput;
        this.img = ImageIO.read(new File("src/main/resources/Map.png"));
        this.cellHeight = img.getHeight() / 4;   // Set the cell Height
        this.cellWidth = img.getWidth() / 10;    // Set the cell Width
    }

    public void gridPainting(double[][] data, String name) throws IOException {
        HeatChart heat = new HeatChart(data);
        heat.setColourScale(5);
        heat.setHighValueColour(Color.GREEN);
        heat.setLowValueColour(Color.RED);
        heat.setChartMargin(0);


        heat.setCellSize(new Dimension((int) cellWidth, (int) cellHeight));
        heat.setAxisThickness(0);
        heat.setShowXAxisValues(false);
        heat.setShowYAxisValues(false);

        try {
            heat.saveToFile(new File("output/java-heat-" + name + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void CreateMap() throws IOException {
        Point GRIDWD = new Point(10, 4);
        File file = new File("src/main/resources/Map.png");
        BufferedImage img = ImageIO.read(file);
        BufferedImage outputImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);

        cellHeight = outputImage.getHeight() / GRIDWD.getY();
        cellWidth = outputImage.getWidth() / GRIDWD.getX();


        gridPainting(rssiGrids, "Rssi");
        OverlayImage at = new OverlayImage("output/java-heat-Rssi.png", "output/RssiHeatmap.png");
        MergeImages merge = new MergeImages("output/grid.png", "output/RssiHeatmap.png", "output/HeatmapRssi.png");
        File file2 = new File("output/RssiHeatmap.png");
        file2.delete();

        gridPainting(throughputGrids, "Throughput");
        OverlayImage cl = new OverlayImage("output/java-heat-Throughput.png", "output/ThroughputHeatmap.png");
        MergeImages merge2 = new MergeImages("output/grid.png", "output/ThroughputHeatmap.png", "output/HeatmapThroughput.png");
        File file3 = new File("output/ThroughputHeatmap.png");
        file3.delete();


    }


}
