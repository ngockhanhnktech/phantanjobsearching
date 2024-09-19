package org.example.server.classes;

import org.example.database_config;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class class_gui extends JFrame{
    private JTable classTable;
    private DefaultTableModel tableModel;
    private final database_config dbConfig;
    private JTextField idField;
    private JTextField classname;


    public class_gui() {
        dbConfig = new database_config();

        setTitle("Quản lý Công việc");
        setSize(800, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        classTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(classTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Job name:"));
        classname = new JTextField();
        inputPanel.add(classname);
        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton loadButton = new JButton("Load");
        buttonPanel.add(loadButton);

        JButton addButton = new JButton("Add");
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update");
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.err.println("action");
            }
        });
    }

}
