/*
 * Lovely June S. Villocillo
 * 23-0217-474
 */

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class StudentRecordSystem extends JFrame {

    DefaultTableModel model;
    JTable table;
    JTextField idField, nameField, gradeField;

    String[] columns = { "StudentID", "Name", "Grade" };

    public StudentRecordSystem() {
        this.setTitle("Record - Lovely June S. Villocillo [23-0217-474]");
        this.setSize(700, 450);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // center the window
        this.getContentPane().setBackground(Color.WHITE);

        // Table setup
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(128, 0, 0)); // Maroon
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(Color.GRAY);
        table.setSelectionBackground(new Color(255, 204, 204));

        loadCSV();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(128, 0, 0), 2));

        // Input fields
        idField = new JTextField(8);
        nameField = new JTextField(12);
        gradeField = new JTextField(8);

        JLabel idLabel = new JLabel("ID:");
        JLabel nameLabel = new JLabel("Name:");
        JLabel gradeLabel = new JLabel("Grade:");

        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gradeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Buttons
        JButton addBtn = new JButton("Add");
        JButton deleteBtn = new JButton("Delete");

        addBtn.setBackground(new Color(128, 0, 0));
        addBtn.setForeground(Color.WHITE);
        deleteBtn.setBackground(new Color(128, 0, 0));
        deleteBtn.setForeground(Color.WHITE);

        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(new Color(255, 240, 245));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(gradeLabel);
        inputPanel.add(gradeField);
        inputPanel.add(addBtn);
        inputPanel.add(deleteBtn);

        // Add button action
        addBtn.addActionListener(e -> {
            if (!idField.getText().isEmpty() && !nameField.getText().isEmpty() && !gradeField.getText().isEmpty()) {
                model.addRow(new Object[]{
                        idField.getText(),
                        nameField.getText(),
                        gradeField.getText()
                });
                idField.setText("");
                nameField.setText("");
                gradeField.setText("");
            }
        });

        // Delete button action
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                model.removeRow(row);
            }
        });

        // Layout
        this.setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void loadCSV() {
        File file = new File("Prelim-Exam/Java/MOCK_DATA.csv");

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this,
                    "CSV not found at:\n" + file.getAbsolutePath());
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // skip header row

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                String studentID = data[0];
                String fullName = data[1] + " " + data[2];

                double lab1 = Double.parseDouble(data[3]);
                double lab2 = Double.parseDouble(data[4]);
                double lab3 = Double.parseDouble(data[5]);
                double prelim = Double.parseDouble(data[6]);
                double attendance = Double.parseDouble(data[7]);

                // Grade computation
                double labAverage = (lab1 + lab2 + lab3) / 3;
                double classStanding = (attendance * 0.4) + (labAverage * 0.6);
                double finalGrade = (classStanding * 0.7) + (prelim * 0.3);

                model.addRow(new Object[]{
                        studentID,
                        fullName,
                        String.format("%.2f", finalGrade)
                });
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentRecordSystem::new);
    }
}
