package com.karlialag.app.Database;

import java.sql.*;

public class DatabaseController {
    private Connection connection;
    private String databaseUrl;
    private String username;
    private String passwd;

    // SQL command to create the table we need to use
    private final String createTable = "CREATE TABLE `project` (\n" +
            "  `timestep` double NOT NULL DEFAULT 0,\n" +
            "  `device_id` double NOT NULL DEFAULT 0,\n" +
            "  `real_lat` double DEFAULT NULL,\n" +
            "  `real_long` double DEFAULT NULL,\n" +
            "  `predicted_lat` double DEFAULT NULL,\n" +
            "  `predicted_long` double DEFAULT NULL,\n" +
            "  `real_RSSI` double DEFAULT NULL,\n" +
            "  `real_throughput` double DEFAULT NULL,\n" +
            "  `predicted_RSSI` double DEFAULT NULL,\n" +
            "  `predicted_throughput` double DEFAULT NULL,\n" +
            "  PRIMARY KEY (`device_id`,`timestep`)\n" +
            ")";

    // SQL command to insert dummy values for testing purposes
    private final String insertDummyValues = "INSERT INTO `project` VALUES (1,26,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);";

    public DatabaseController(String databaseUrl, String username, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(databaseUrl, username, password);
            System.out.println("SUCCESFULL CONNECTING TO DATABASE");
            this.databaseUrl = databaseUrl;
            this.username = username;
            this.passwd = password;
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("FAILED CONNECTING TO DATABASE");
            System.out.printf("SQLException: %s\n\n", ex.getMessage());
        }
    }

    public void initialiseDatabase() {
        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                statement.execute(createTable);
                //statement.execute(insertDummyValues);
            } catch (SQLException ex) {
                System.out.println("FAILED INITIALIZING DATABASE");
                System.out.printf("SQLException: %s\n\n", ex.getMessage());
            }
        } else {
            System.out.println("Not connected to database in order to initialize it!");
        }
    }

    public void insertToDB(double timestep,double device_id,double real_lat,double real_long,double predicted_lat,double predicted_long, double real_rssi, double real_throughput, double pred_rssi, double pred_throughput){
        try {
            connection = DriverManager.getConnection(databaseUrl, username, passwd);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO project (timestep, device_id, real_lat, real_long, predicted_lat, predicted_long, real_RSSI, real_throughput, predicted_RSSI, predicted_throughput) VALUES(?,?,?,?,?,?,?,?,?,?)");

            preparedStatement.setDouble(1,timestep);
            preparedStatement.setDouble(2,device_id);
            preparedStatement.setDouble(3,real_lat);
            preparedStatement.setDouble(4,real_long);
            if(predicted_lat == 0.0 && predicted_long==0.0 && pred_rssi==0.0 && pred_throughput==0.0){
                preparedStatement.setNull(5,Types.DOUBLE);
                preparedStatement.setNull(6,Types.DOUBLE);
                preparedStatement.setNull(9,Types.DOUBLE);
                preparedStatement.setNull(10,Types.DOUBLE);
            }
            else {
                preparedStatement.setDouble(5, predicted_lat);
                preparedStatement.setDouble(6, predicted_long);
                preparedStatement.setDouble(9, pred_rssi);
                preparedStatement.setDouble(10, pred_throughput);
            }
            preparedStatement.setDouble(7, real_rssi);
            preparedStatement.setDouble(8, real_throughput);

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void PrintDB() throws SQLException {
        final Statement statement =connection.createStatement();
        final ResultSet resultSet = statement.executeQuery("SELECT * from mydb.Values");
        final ResultSetMetaData rsmd = resultSet.getMetaData();
        final int columnsNumber = rsmd.getColumnCount();
        System.out.println("---------------------DB---------------------");
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; ++i) {
                if (i > 1) {
                    System.out.print(",  ");
                }
                final String columnValue = resultSet.getString(i);
                System.out.println(columnValue + " " + rsmd.getColumnName(i));
            }
            System.out.println("");
        }
    }
}
