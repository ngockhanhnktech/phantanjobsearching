package org.example.server;

import java.sql.Connection;
import java.sql.SQLException;

public interface interface_socket_server {

    void handleMessage(String message);

    void listenForMessages();

    void sendMessageToClient(String response);

    void handleJobsMessage(String SQL, Connection conn) throws SQLException;
}
