package org.example.server.classes;

import org.example.server.interface_socket_server;
import org.example.server.socket_server;

import java.sql.Connection;
import java.sql.SQLException;

public class class_message implements interface_socket_server {
    private socket_server socket_server;
    public class_message() {
        socket_server = new socket_server();
    }

    @Override
    public void handleMessage(String message) {

    }

    @Override
    public void listenForMessages() {

    }

    @Override
    public void sendMessageToClient(String response) {

    }

    @Override
    public void handleJobsMessage(String SQL, Connection conn) throws SQLException {

    }
}
