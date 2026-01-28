import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;

public class PrelimGradeJava extends JFrame {

    private JTextField lab1Field, lab2Field, lab3Field;
    private JTextArea resultArea;
    private JRadioButton[][] attendanceTable;

    // === COLOR PALETTE (MATCHES HTML) ===
    private static final Color BG_MAIN = new Color(30, 30, 47);
    private static final Color BG_PANEL = new Color(44, 47, 74);
    private static final Color TEXT_MAIN = new Color(223, 230, 233);
    private static final Color BLUE = new Color(52, 152, 219);
    private static final Color GREEN = new Color(39, 174, 96);
    private static final Color RED = new Color(192, 57, 43);

    public PrelimGradeJava() {
        setTitle("Prelim Grade Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(BG_MAIN);
        main.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Prelim Grade Calculator", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(164, 195, 245));
        main.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG_MAIN);

        // === ATTENDANCE PANEL ===
        JPanel attendancePanel = createPanel("Attendance (Weeks 1–5)");
        JPanel table = new JPanel(new GridLayout(6, 4, 5, 5));
        table.setBackground(BG_PANEL);

        table.add(createHeader("Week"));
        table.add(createHeader("P"));
        table.add(createHeader("E"));
        table.add(createHeader("A"));

        attendanceTable = new JRadioButton[5][3];
        ButtonGroup[] groups = new ButtonGroup[5];

        for (int w = 0; w < 5; w++) {
            table.add(createText("Week " + (w + 1)));
            groups[w] = new ButtonGroup();
            for (int i = 0; i < 3; i++) {
                JRadioButton rb = new JRadioButton();
                rb.setBackground(BG_PANEL);
                rb.setForeground(TEXT_MAIN);
                attendanceTable[w][i] = rb;
                groups[w].add(rb);
                table.add(rb);
            }
            attendanceTable[w][0].setSelected(true);
        }

        attendancePanel.add(table);
        center.add(attendancePanel);
        center.add(Box.createVerticalStrut(15));

        // === LAB PANEL ===
        JPanel labPanel = createPanel("Lab Work Grades");
        labPanel.setLayout(new GridLayout(3, 2, 10, 10));

        lab1Field = createNumberField();
        lab2Field = createNumberField();
        lab3Field = createNumberField();

        labPanel.add(createText("Lab 1:")); labPanel.add(lab1Field);
        labPanel.add(createText("Lab 2:")); labPanel.add(lab2Field);
        labPanel.add(createText("Lab 3:")); labPanel.add(lab3Field);

        center.add(labPanel);
        center.add(Box.createVerticalStrut(15));

        // === BUTTON ===
        JButton btn = new JButton("Calculate Grades");
        btn.setBackground(BLUE);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.addActionListener(e -> calculateGrades());

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(BG_MAIN);
        btnPanel.add(btn);
        center.add(btnPanel);

        main.add(center, BorderLayout.CENTER);

        // === RESULT AREA ===
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultArea.setBackground(BG_PANEL);
        resultArea.setForeground(TEXT_MAIN);
        resultArea.setBorder(new EmptyBorder(15, 15, 15, 15));

        main.add(new JScrollPane(resultArea), BorderLayout.SOUTH);
        add(main);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // === CALCULATION LOGIC (MATCHES JS EXACTLY) ===
    private void calculateGrades() {
        try {
            double lab1 = validate(lab1Field.getText());
            double lab2 = validate(lab2Field.getText());
            double lab3 = validate(lab3Field.getText());
            double labAvg = (lab1 + lab2 + lab3) / 3;

            int absences = 0, excused = 0;
            for (int i = 0; i < 5; i++) {
                if (attendanceTable[i][2].isSelected()) absences++;
                else if (attendanceTable[i][1].isSelected()) excused++;
            }

            if (absences >= 4) {
                resultArea.setForeground(RED);
                resultArea.setText(
                    "PRELIM GRADE REPORT\n\n" +
                    "Absences: " + absences + " | Excused: " + excused + "\n\n" +
                    "STATUS: FAILED\nReason: 4 or more absences result in failure for this term."
                );
                return;
            }

            double attendanceScore = Math.max(100 - absences * 20, 0);
            double classStanding = attendanceScore * 0.4 + labAvg * 0.6;
            double pass = (75 - classStanding * 0.3) / 0.7;
            double excellent = (100 - classStanding * 0.3) / 0.7;

            StringBuilder out = new StringBuilder();
            out.append("PRELIM GRADE REPORT\n\n");
            out.append("Attendance: Absences = ").append(absences)
               .append(", Excused = ").append(excused).append("\n\n");
            out.append(String.format("Lab Work 1: %.2f\nLab Work 2: %.2f\nLab Work 3: %.2f\n", lab1, lab2, lab3));
            out.append(String.format("Lab Work Average: %.2f\n", labAvg));
            out.append(String.format("Attendance Score: %.2f\nClass Standing: %.2f\n\n", attendanceScore, classStanding));
            out.append("Required Prelim Exam Score:\n");
            out.append(String.format("To PASS (75): %.2f%s\n", pass, pass > 100 ? " (UNACHIEVABLE)" : ""));
            out.append(String.format("To EXCELLENT (100): %.2f%s\n\n", excellent, excellent > 100 ? " (UNACHIEVABLE)" : ""));

            if (pass > 100 && excellent > 100) {
                resultArea.setForeground(RED);
                out.append("The class standing is too low.\n");
                out.append("Even a perfect prelim exam score will not be enough to pass or reach excellence.");
            }
            else if (excellent <= 100) {
                resultArea.setForeground(GREEN);
                out.append("A full score on the prelim exam will achieve a grade of 100!");
            }
            else {
                resultArea.setForeground(BLUE);
                out.append(String.format(
                    "A score of %.2f on the prelim exam will let you pass this term.", pass));
            }


            resultArea.setText(out.toString());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter valid grades 0-100.");
        }
    }

    // === HELPERS ===
    private double validate(String v) {
        double d = Double.parseDouble(v);
        if (d < 0 || d > 100) throw new NumberFormatException();
        return d;
    }

    private JTextField createNumberField() {
        JTextField f = new JTextField();
        f.setBackground(new Color(31, 34, 56));
        f.setForeground(TEXT_MAIN);
        ((AbstractDocument) f.getDocument()).setDocumentFilter(new NumericFilter());
        return f;
    }

    private JPanel createPanel(String title) {
        JPanel p = new JPanel();
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(87, 96, 111)), title));
        return p;
    }

    private JLabel createHeader(String t) {
        JLabel l = new JLabel(t, JLabel.CENTER);
        l.setForeground(new Color(138, 180, 248));
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return l;
    }

    private JLabel createText(String t) {
        JLabel l = new JLabel(t, JLabel.CENTER);
        l.setForeground(TEXT_MAIN);
        return l;
    }

    class NumericFilter extends DocumentFilter {
        public void insertString(FilterBypass fb, int o, String s, AttributeSet a) throws BadLocationException {
            if (s.matches("[0-9]*")) super.insertString(fb, o, s, a);
        }
        public void replace(FilterBypass fb, int o, int l, String s, AttributeSet a) throws BadLocationException {
            if (s.matches("[0-9]*")) super.replace(fb, o, l, s, a);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PrelimGradeJava::new);
    }
}
