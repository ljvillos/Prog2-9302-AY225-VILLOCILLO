import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class DataQualityAudit {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        File file;

        while (true) {
            System.out.print("Enter dataset file path: ");
            String path = input.nextLine();
            file = new File(path);

            if (!file.exists()) {
                System.out.println("Error: File does not exist.");
            } else if (!file.isFile()) {
                System.out.println("Error: Path is not a file.");
            } else if (!file.canRead()) {
                System.out.println("Error: File is not readable.");
            } else if (!path.toLowerCase().endsWith(".csv")) {
                System.out.println("Error: File is not in CSV format.");
            } else {
                break;
            }
            System.out.println("Please try again.\n");
        }

        System.out.println("\nLoading dataset...\n");

        int missingValues = 0;
        int negativeSales = 0;
        int invalidDates = 0;
        int duplicateRecords = 0;

        Set<String> uniqueRecords = new HashSet<>();

        int totalRecords = 0;
        int dateColumnIndex = -1;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            // =========================
            // Read header properly
            // =========================
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.out.println("File is empty.");
                return;
            }

            String[] headers = headerLine.split(
                ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1
            );

            // Detect date column
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].toLowerCase().contains("date")) {
                    dateColumnIndex = i;
                    break;
                }
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                totalRecords++;

                String[] columns = line.split(
                    ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1
                );

                // =========================
                // Missing values
                // =========================
                for (String col : columns) {
                    if (col.trim().isEmpty()) {
                        missingValues++;
                    }
                }

                // =========================
                // Duplicate check
                // =========================
                if (!uniqueRecords.add(line)) {
                    duplicateRecords++;
                }

                // =========================
                // Negative sales
                // =========================
                for (String value : columns) {
                    try {
                        double num = Double.parseDouble(value.replace("\"", "").trim());
                        if (num < 0) {
                            negativeSales++;
                            break;
                        }
                    } catch (NumberFormatException ignored) {}
                }

                // =========================
                // Invalid Date
                // =========================
                if (dateColumnIndex >= 0 && dateColumnIndex < columns.length) {

                    String dateValue = columns[dateColumnIndex]
                            .replace("\"", "")
                            .trim();

                    if (!dateValue.isEmpty()) {
                        try {
                            LocalDate.parse(dateValue, formatter);
                        } catch (DateTimeParseException e) {
                            invalidDates++;
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return;
        }

        // =========================
        // REPORT
        // =========================
        System.out.println("=======================================");
        System.out.println("        DATA QUALITY REPORT");
        System.out.println("=======================================");
        System.out.println("Total Records Loaded  : " + totalRecords);
        System.out.println("Missing Values Found  : " + missingValues);
        System.out.println("Negative Sales Found  : " + negativeSales);
        System.out.println("Invalid Dates Found   : " + invalidDates);
        System.out.println("Duplicate Records     : " + duplicateRecords);
        System.out.println("=======================================");
        System.out.println("Audit Completed Successfully.");
    }
}
