package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database_config implements database_interface {
    private String connectString = "jdbc:mysql://localhost:3307/quanlycongviec";

    private String username = "root";
    private String password = "28082001";


    public database_config(String connectString, String username, String password) {
        this.connectString = connectString;
        this.username = username;
        this.password = password;
    }

    public database_config() {
    }

    public String getConnectString() {
        return connectString;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    @Override
    public Connection connect_database() {
        try {
            System.out.println("Connection: "+getConnectString());
            return DriverManager.getConnection(getConnectString(), getUsername(), getPassword());
        } catch (SQLException e) {
            System.err.format("ERROR SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            return null;
        }


    }
}
