import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class attendance {

    public static void main(String[] args) {

        // Frame
        JFrame frame = new JFrame("Attendance Tracker");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Panel
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Fields
        JTextField nameField = new JTextField();
        JTextField courseField = new JTextField();

        JTextField timeInField = new JTextField();
        timeInField.setEditable(false);

        JTextField eSignatureField = new JTextField();
        eSignatureField.setEditable(false);

        // Button
        JButton submitButton = new JButton("Submit Attendance");

        submitButton.addActionListener(e -> {

            String name = nameField.getText().trim();
            String course = courseField.getText().trim();
            // Empty fields catch
            if (name.isEmpty() || course.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Please fill in all fields.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Formatted system time
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timeIn = LocalDateTime.now().format(formatter);
            timeInField.setText(timeIn);

            // E-Signature
            String eSignature = java.util.UUID.randomUUID().toString();
            eSignatureField.setText(eSignature);
        });

        // Add components
        panel.add(new JLabel("Attendance Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Course / Year:"));
        panel.add(courseField);

        panel.add(new JLabel("Time In:"));
        panel.add(timeInField);

        panel.add(new JLabel("E-Signature:"));
        panel.add(eSignatureField);

        panel.add(new JLabel());
        panel.add(submitButton);

        frame.add(panel);
        frame.setVisible(true);
    }
}
