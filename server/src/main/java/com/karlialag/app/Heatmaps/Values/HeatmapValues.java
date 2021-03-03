package com.karlialag.app.Heatmaps.Values;

import com.karlialag.app.Heatmaps.Heatmap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class HeatmapValues extends Heatmap {

    public GridValues AverageValue() {
        final GridValues map = new GridValues();
        double[][] rssiCount = new double[4][];
        for (int i = 0; i < 4; ++i) {
            rssiCount[i] = new double[10];
            for (int j = 0; j < 10; ++j) {
                rssiCount[i][j] = 0;
            }
        }
        double[][] throughputCount = new double[4][];
        for (int i = 0; i < 4; i++) {
            throughputCount[i] = new double[10];
            for (int j = 0; j < 10; j++) {
                throughputCount[i][j] = 0;
            }
        }

        try {
            final BufferedReader resources = new BufferedReader(new FileReader(this.csvFile));
            String line = resources.readLine();
            while ((line = resources.readLine()) != null) {
                line = line.trim();
                String[] fields = line.split(",");
                String lon = fields[2];
                String lat = fields[3];
                String rssi = fields[6];
                String throughput = fields[7];

                double latValue = Double.parseDouble(lat);
                double lonValue = Double.parseDouble(lon);
                double rssiValue = Double.parseDouble(rssi);
                double throughputValue = Double.parseDouble(throughput);


                int i = this.RowFinder(latValue);
                int j = this.ColumnFinder(lonValue);

                if (i != -1 && j != -1) {
                    map.rssi[3 - i][j] += rssiValue;
                    map.throughput[3 - i][j] += throughputValue;

                    rssiCount[3 - i][j]++;
                    throughputCount[3 - i][j]++;
                }

            }
            resources.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int k = 0; k < 4; ++k) {
            for (int l = 0; l < 10; ++l) {
                if (rssiCount[k][l] > 0.0) {
                    map.rssi[k][l] /= rssiCount[k][l];
                } else {
                    map.rssi[k][l] = 0.0;
                }
                if (throughputCount[k][l] > 0.0) {
                    map.throughput[k][l] /= throughputCount[k][l];
                } else {
                    map.throughput[k][l] = 0.0;
                }
            }
        }
        return map;
    }

    public int ColumnFinder(double lonValue) {
        int column = 0;

        if (lonValue < 23.76476 || lonValue > 23.77539) {
            column = -1; //out of map
        } else if (lonValue > 23.76476 + 0.0 * this.width && lonValue < 23.76476 + 1.0 * this.width) {
            column = 0;
        } else if (lonValue > 23.76476 + 1.0 * this.width && lonValue < 23.76476 + 2.0 * this.width) {
            column = 1;
        } else if (lonValue > 23.76476 + 3.0 * this.width && lonValue < 23.76476 + 4.0 * this.width) {
            column = 3;
        } else if (lonValue > 23.76476 + 4.0 * this.width && lonValue < 23.76476 + 5.0 * this.width) {
            column = 4;
        } else if (lonValue > 23.76476 + 5.0 * this.width && lonValue < 23.76476 + 6.0 * this.width) {
            column = 5;
        } else if (lonValue > 23.76476 + 6.0 * this.width && lonValue < 23.76476 + 7.0 * this.width) {
            column = 6;
        } else if (lonValue > 23.76476 + 7.0 * this.width && lonValue < 23.76476 + 8.0 * this.width) {
            column = 7;
        } else if (lonValue > 23.76476 + 8.0 * this.width && lonValue < 23.76476 + 9.0 * this.width) {
            column = 8;
        } else if (lonValue > 23.76476 + 9.0 * this.width && lonValue < 23.76476 + 10.0 * this.width) {
            column = 9;
        }
        return column;
    }

    public int RowFinder(double latValue) {
        int row = 0;
        if (latValue > 37.96862 || latValue < 37.96688) {
            row = -1; //out of map
        } else if (latValue > 37.96688 + 0.0 * this.height && latValue < 37.96688 + 1.0 * this.height) {
            row = 0;
        } else if (latValue > 37.96688 + 1.0 * this.height && latValue < 37.96688 + 2.0 * this.height) {
            row = 1;
        } else if (latValue > 37.96688 + 2.0 * this.height && latValue < 37.96688 + 3.0 * this.height) {
            row = 2;
        } else if (latValue > 37.96688 + 3.0 * this.height && latValue < 37.96688 + 4.0 * this.height) {
            row = 3;
        }
        return row;
    }

    public void Convert2Percent(GridValues arrays) {
        final double maxrssi = 100.0;
        final double maxth = 50.0;
        final double minrssi = 20.0;
        final double minth = 10.0;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 10; ++j) {
                if (arrays.rssi[i][j] != 0.0) {
                    arrays.rssi[i][j] = (arrays.rssi[i][j] - minrssi) / (maxrssi - minrssi);
                }
                if (arrays.throughput[i][j] != 0.0) {
                    arrays.throughput[i][j] = (arrays.throughput[i][j] - minth) / (maxth - minth);
                }
            }
        }

    }

}
