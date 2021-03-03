package com.karlialag.app.Heatmaps;


import com.karlialag.app.Heatmaps.Graphics.DrawGrid;
import com.karlialag.app.Heatmaps.Graphics.DrawHeatmap;
import com.karlialag.app.Heatmaps.Values.GridValues;
import com.karlialag.app.Heatmaps.Values.HeatmapValues;

import java.io.File;
import java.io.IOException;

public class Heatmap {
    public File csvFile;
    public double width;
    public double height;
    public GridValues values;

    public Heatmap() {
        this.csvFile = new File("csvFiles/all_vehicles.csv");
        this.width = Math.abs(23.7753900 - 23.7647600) / 10.0;
        this.height = Math.abs(37.9668800 - 37.9686200) / 4.0;

    }

    public void createHeatmaps() throws IOException {
        HeatmapValues k = new HeatmapValues();
        GridValues map = new GridValues();
        map = k.AverageValue();
        this.values = map;
        //k.Convert2Percent(map);

        DrawGrid grid = new DrawGrid("src/main/resources/Map.png");

        DrawHeatmap gr = new DrawHeatmap(map);
        gr.CreateMap();
        File file = new File("output/java-heat-Rssi.png");
        file.delete();
        File file2 = new File("output/java-heat-Throughput.png");
        file2.delete();
    }

    public double RssiFinder(double lat, double lon) {
        HeatmapValues finder = new HeatmapValues();

        int column = finder.ColumnFinder(lon);
        int row = finder.RowFinder(lat);
        if(column == -1 || row == -1)
        {
            System.out.println("Coordinates out of grid!");
            return -1;
        }
        return this.values.rssi[3-row][column];
    }

    public double ThroughputFinder(double lat, double lon) {
        HeatmapValues finder = new HeatmapValues();
        int column = finder.ColumnFinder(lon);
        int row = finder.RowFinder(lat);
        if(column == -1 || row == -1)
        {
            System.out.println("Coordinates out of grid!");
            return -1;
        }
        return this.values.throughput[3-row][column];
    }


}
