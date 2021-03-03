package com.karlialag.app.Heatmaps;

public class Prediction {

    private static int t=1;

    public double[] PredictionImplementation(double lat,double lon,double angle,double speed){
        double R = 6.371 * Math.pow(10.0,6.0);
        double d = Prediction.t * speed / R;

        double latStart = Math.toRadians(lat);
        double lonStart = Math.toRadians(lon);
        double angleStart = Math.toRadians(angle);

        double latEndRad = (Math.asin(Math.sin(latStart)) * Math.cos(d)) + (Math.cos(latStart) * Math.sin(d) * Math.cos(angleStart));
        double lonEndRad = lonStart + (Math.atan2(Math.sin(angleStart) * Math.sin(d) * Math.cos(latStart), Math.cos(d) - Math.sin(latStart) * Math.sin(latEndRad)));


        double latEndDeg = Math.toDegrees(latEndRad);
        double lonEndDeg = Math.toDegrees(lonEndRad);

        return new double[] {latEndDeg,lonEndDeg};
    }
}
