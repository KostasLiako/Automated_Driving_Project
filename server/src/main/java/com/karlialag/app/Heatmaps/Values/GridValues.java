package com.karlialag.app.Heatmaps.Values;

public class GridValues {
    public double[][] rssi;
    public double[][] throughput;

    public GridValues() {
        initMap();
    }

    public GridValues(GridValues gr) {
        this.rssi = gr.rssi;
        this.throughput = gr.throughput;
    }

    public void initMap() {
        this.rssi = new double[4][];
        this.throughput = new double[4][];

        for (int i = 0; i < 4; ++i) {
            this.rssi[i] = new double[10];
            this.throughput[i] = new double[10];
        }
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 10; ++j) {
                this.rssi[i][j] = 0.0;
                this.throughput[i][j] = 0.0;
            }
        }

    }

    public void print() {
        //prints the RSSI and Throughput values for the grid
        System.out.println("Rssi Values\n");
        for (int k = 0; k < 4; ++k) {
            for (int l = 0; l < 10; ++l) {
                System.out.print(this.rssi[k][l] + " " + "\n");
            }
            System.out.println();
        }

        System.out.println("Throughput Values\n");
        for (int k = 0; k < 4; ++k) {
            for (int l = 0; l < 10; ++l) {
                System.out.print(this.throughput[k][l] + " " + "\n");
            }
            System.out.println();
        }
    }
}
