package org.example.server;

import org.example.database_config;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class socket_server implements interface_socket_server {
    private static Socket socket1;
    private static BufferedReader reader;
    private static BufferedWriter writer;
    private  final database_config dbConfig;

    private  interface_socket_server interfaceSocketServer;


    public socket_server() {
        dbConfig = new database_config();
        this.interfaceSocketServer = this;
    }
    public void connect_socket_server() {
        try {
            ServerSocket serverSocket = new ServerSocket(2001);
            while (true) {
                socket1 = serverSocket.accept();
                reader = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream()));
                System.err.println("Server Socket connected...");
                congViecApp.messageField.append("Client connected server chat..." + "\n");
                new Thread(this::listenForMessages).start();
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
    }

    @Override
    public void handleMessage(String message) {
        if(message.isEmpty()){
            sendMessageToClient("Xin chào bạn muốn gì? " +
                    "\nOption 1: Nhập mã công việc hoặc mô tả công việc hoặc trạng thái \n " +
                    "vào chat để xem thông tin của bạn vd: in_progress");
        }else {
            SwingUtilities.invokeLater(() -> {
                congViecApp.messageField.append("Client: " + message + "\n");
            });
        }
    }


    @Override
    public void listenForMessages() {
        Connection conn = dbConfig.connect_database();
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Message received from client: " + message);

                // Check if the message is numeric (for job_id) or non-numeric (for search terms)

                if (isNumeric(message)) {
                    int id = Integer.parseInt(message.trim());
                    System.out.println("The message received from client (parsed ID): " + id);
                   handleMessage(message);
                    // Query based on job_id
                    String SQL = "SELECT * FROM jobs WHERE jobs.job_id = " + id + ";";
                    this.handleJobsMessage(SQL, conn);
                }else if(message.equals("all")){
                    String SQL = "SELECT * FROM jobs ;";
                    handleMessage(message);

                    this.handleJobsMessage(SQL, conn);
                } else {
                    // Handle non-numeric search terms
                    String SQL = "SELECT * FROM jobs WHERE title LIKE '%" + message + "%' OR status LIKE '%" + message + "%';";
                    handleMessage(message);

                    this.handleJobsMessage(SQL, conn);
                }
            }
        } catch (IOException e) {
            System.err.println("I/O ERROR: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL ERROR: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (socket1 != null) socket1.close();
            } catch (IOException e) {
                System.err.println("ERROR closing socket: " + e.getMessage());
            }
        }
    }

    // Helper method to check if a string is numeric
    private boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }



    @Override
    public void sendMessageToClient(String response) {
        try {
            if (writer != null) {
                writer.write(response);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            System.err.println("ERROR: " + e);
        }
    }


@Override
public void handleJobsMessage(String SQL, Connection conn) throws SQLException {
    // Ensure the connection is not null
    if (conn == null) {
        System.err.println("Database connection is null.");
        return;
    }

    // Log the SQL query for debugging purposes
    System.out.println("Executing SQL: " + SQL);

    // Prepare the SQL statement and execute it
    PreparedStatement preparedStatement = conn.prepareStatement(SQL);
    ResultSet resultSet = preparedStatement.executeQuery();
    StringBuilder str = new StringBuilder();

    // Loop through the result set
    while (resultSet.next()) {
        // Retrieve the values from the ResultSet
        String id = resultSet.getString("job_id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        String status = resultSet.getString("status");

        // Build the string with proper formatting
        str.append("Thông tin của công việc: \n")
                .append("ID: ").append(id).append("\n")
                .append("Title: ").append(title).append("\n")
                .append("Description: ").append(description).append("\n")
                .append("Status: ").append(status).append("\n\n\n");
    }

    // Check if any data was retrieved
    if (str.length() == 0) {
        System.out.println("No data found.");
    } else {
        // Send the result to the client
        sendMessageToClient(str.toString());
    }
}

}



