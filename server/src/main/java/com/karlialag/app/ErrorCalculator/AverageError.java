package com.karlialag.app.ErrorCalculator;

public class AverageError {

        public double distance(double lat1, double lon1, double lat2, double lon2) {
            if ((lat1 == lat2) && (lon1 == lon2)) {
                return 0;
            } else {
                double theta = lon1 - lon2;
                double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
                dist = Math.acos(dist);
                dist = Math.toDegrees(dist);
                dist = dist * 60 * 1.1515;
                dist = dist * 1.609344;
                return (dist);
            }
        }

        public void averageErrorValue(double sum,double timestep){
            double average;

            average = sum / timestep ;

            System.out.println("Vehicle just stopped sending.. Average Error is " + average + " meters");

        }
    }
