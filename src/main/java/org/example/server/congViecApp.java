package org.example.server;

import org.example.database_config;
import org.example.server.classes.class_gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;

public class congViecApp extends JFrame {
    private final JTextField idField;
    private final JTextField title;
    private final JTextField description;

    private final JTextField status;
    public static JTextField messageSendField;
    private final JTable congViecTable;
    private final DefaultTableModel tableModel;
    public static JTextArea messageField;

    private final database_config dbConfig;
    private final socket_server socket_server;


    public congViecApp() throws IOException {

        dbConfig = new database_config();
        socket_server = new socket_server();
        startSocketServer();

        setTitle("Quản lý Công Việc");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);//
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        congViecTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(congViecTable);
        add(tableScrollPane, BorderLayout.CENTER);

        loadJobs();

        JPanel inputPanel = new JPanel(new GridLayout(5, 3));
        inputPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Title job:"));
        title = new JTextField();
        inputPanel.add(title);

        inputPanel.add(new JLabel("Description:"));
        description = new JTextField();
        inputPanel.add(description);

        inputPanel.add(new JLabel("status:"));
        status = new JTextField();
        inputPanel.add(status);

        inputPanel.add(new JLabel("Message send:"));
        messageSendField = new JTextField();
        inputPanel.add(messageSendField);

        add(inputPanel, BorderLayout.NORTH);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(new JLabel("Message received:"), BorderLayout.NORTH);

        messageField = new JTextArea(15, 40);
        messageField.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messageField);
        messagePanel.add(messageScrollPane, BorderLayout.CENTER);

        add(messagePanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        JButton loadButton = new JButton("Load");
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton classButton = new JButton("Class");
        JButton sendButton = new JButton("Send");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(classButton);
        buttonPanel.add(sendButton);
        add(buttonPanel, BorderLayout.EAST);

        classButton.addActionListener(e -> {
            class_gui classWindow = new class_gui();
            classWindow.setVisible(true);
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageSendField.getText();
                messageField.append("You: "+message + "\n");
                if (message != null && !message.isEmpty()) {
                    socket_server.sendMessageToClient(message);
                }
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refesh();
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addJobs();
                refesh();
                clearFields();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateJobs();
                refesh();
                clearFields();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteJobs();
                refesh();
                clearFields();
            }
        });
    }

    private void startSocketServer() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                socket_server.connect_socket_server();
            }
        }).start();
    }


    private void refesh() {
        tableModel.setRowCount(0);
        loadJobs();
    }

    private void loadJobs() {
        Connection conn = dbConfig.connect_database();
        if (conn != null) {
            try {
                String sql = "SELECT * FROM jobs";
                Statement execute = conn.createStatement();
                ResultSet rs = execute.executeQuery(sql);

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                String[] columnNames = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    columnNames[i - 1] = metaData.getColumnName(i);
                }
                tableModel.setColumnIdentifiers(columnNames);

                while (rs.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        row[i - 1] = rs.getObject(i);
                    }
                    tableModel.addRow(row);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void addJobs() {
        Connection conn = dbConfig.connect_database();
        if (conn != null) {
            try {
                String sql = "INSERT INTO jobs (job_id, title, description, status) VALUES (?, ?, ?, ?)";
                PreparedStatement execute = conn.prepareStatement(sql);
                execute.setString(1, idField.getText());
                execute.setString(2, title.getText().isEmpty() ? null : title.getText());
                execute.setString(3, description.getText().isEmpty() ? null : description.getText());
                execute.setString(4, status.getText().isEmpty()? null : status.getText());
                execute.executeUpdate();

                tableModel.addRow(new Object[]{idField.getText(), title.getText(), description.getText(),status.getText()});
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


private void updateJobs() {
    Connection conn = dbConfig.connect_database();
    if (conn != null) {
        try {
            int selectedRow = congViecTable.getSelectedRow();
            if (selectedRow == -1) {
                System.out.println("No row selected.");
                return;
            }

            // Get old values
            String oldTitle = tableModel.getValueAt(selectedRow, 1).toString();
            String oldDescription = tableModel.getValueAt(selectedRow, 2).toString();
            String oldStatus = tableModel.getValueAt(selectedRow, 3).toString();

            // Get new values
            String newTitle = title.getText().isEmpty() ? oldTitle : title.getText();
            String newDescription = description.getText().isEmpty() ? oldDescription : description.getText();
            String newStatus = status.getText().isEmpty() ? oldStatus : status.getText();

            // Ensure idField is not empty
            String id = idField.getText();
            if (id.isEmpty()) {
                System.out.println("ID is empty. Cannot update record.");
                return;
            }

            int idInt = Integer.parseInt(id);

            // Update query
            String sql = "UPDATE jobs SET title = ?, description = ?, status = ? WHERE job_id = ?";
            PreparedStatement execute = conn.prepareStatement(sql);
            execute.setString(1, newTitle);
            execute.setString(2, newDescription);
            execute.setString(3, newStatus);
            execute.setInt(4, idInt);

            // Execute the update
            int rowsUpdated = execute.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Record updated successfully!");

                // Update table model
                tableModel.setValueAt(newTitle, selectedRow, 1);
                tableModel.setValueAt(newDescription, selectedRow, 2);
                tableModel.setValueAt(newStatus, selectedRow, 3);
            } else {
                System.out.println("No record found with the provided ID.");
            }

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Invalid ID format: " + e.getMessage());
        }
    } else {
        System.out.println("Database connection failed.");
    }
}



    private void deleteJobs() {
        Connection conn = dbConfig.connect_database();
        if (conn != null) {
            try {
                String sql = "DELETE FROM jobs WHERE job_id = ?";
                PreparedStatement execute = conn.prepareStatement(sql);
                execute.setString(1, idField.getText());
                execute.executeUpdate();
                int selectedRow = congViecTable.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearFields() {
        idField.setText("");
        title.setText("");
        description.setText("");
        status.setText("");

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new congViecApp().setVisible(true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }




}
