const fs = require('fs');
const readline = require('readline');

// ==========================
// READLINE SETUP
// ==========================
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

// ==========================
// ASK FILE PATH
// ==========================
async function askFilePath() {
    while (true) {
        const path = await new Promise(resolve =>
            rl.question("Enter dataset file path: ", resolve)
        );

        const trimmed = path.trim();

        if (!fs.existsSync(trimmed)) {
            console.log("Error: File does not exist.\n");
            continue;
        }

        if (!fs.lstatSync(trimmed).isFile()) {
            console.log("Error: Path is not a file.\n");
            continue;
        }

        try {
            fs.accessSync(trimmed, fs.constants.R_OK);
        } catch {
            console.log("Error: File is not readable.\n");
            continue;
        }

        if (!trimmed.toLowerCase().endsWith(".csv")) {
            console.log("Error: File is not a CSV.\n");
            continue;
        }

        console.log("File validated successfully.\n");
        return trimmed;
    }
}

// ==========================
// LOAD DATASET
// ==========================
function loadDataset(filePath) {
    try {
        const rawData = fs.readFileSync(filePath, 'utf8');
        const lines = rawData
            .split(/\r?\n/)
            .filter(line => line.trim() !== "");

        if (lines.length < 2)
            throw new Error("Dataset has no data or only header.");

        return lines;
    } catch (err) {
        throw new Error("Error reading file: " + err.message);
    }
}

// ==========================
// CSV
// ==========================
function splitCsvLine(line) {
    if (!line) return [];

    const columns = [];
    let value = "";
    let inQuotes = false;

    for (let i = 0; i < line.length; i++) {
        const char = line[i];

        if (char === '"') {
            inQuotes = !inQuotes;
        } else if (char === ',' && !inQuotes) {
            columns.push(value);
            value = "";
        } else {
            value += char;
        }
    }

    columns.push(value);

    return columns.map(v => v.trim());
}

// ==========================
// STRICT DATE VALIDATION
// ==========================
function isValidDateStrict(dateStr) {
    if (!/^\d{4}-\d{2}-\d{2}$/.test(dateStr)) {
        return false;
    }

    const [year, month, day] = dateStr.split("-").map(Number);
    const date = new Date(year, month - 1, day);

    return (
        date.getFullYear() === year &&
        date.getMonth() === month - 1 &&
        date.getDate() === day
    );
}

// ==========================
// ANALYZE DATA
// ==========================
function analyzeData(lines) {
    const headers = splitCsvLine(lines[0]);
    const records = lines.slice(1);

    let missingValues = 0;
    let negativeSales = 0;
    let invalidDates = 0;
    let duplicateRecords = 0;

    const seenRecords = new Set();

    const salesCol = headers.findIndex(h => /sales/i.test(h));
    const dateCol = headers.findIndex(h => /date/i.test(h));

    for (const line of records) {
        const columns = splitCsvLine(line);

        // ==========================
        // COUNT MISSING VALUES
        // ==========================
        for (let i = 0; i < headers.length; i++) {
            const value = columns[i];

            if (value === undefined || value.trim() === "") {
                missingValues++;
            }
        }

        // ==========================
        // DUPLICATE CHECK
        // ==========================
        const recordKey = columns.join("|");

        if (seenRecords.has(recordKey)) {
            duplicateRecords++;
        } else {
            seenRecords.add(recordKey);
        }

        // ==========================
        // NEGATIVE SALES CHECK
        // ==========================
        if (salesCol >= 0) {
            const val = columns[salesCol];
            const num = parseFloat(val);

            if (!isNaN(num) && num < 0) {
                negativeSales++;
            }
        }

        // ==========================
        // INVALID DATE CHECK
        // ==========================
        if (dateCol >= 0) {
            const val = columns[dateCol];

            if (val && val.trim() !== "") {
                if (!isValidDateStrict(val.trim())) {
                    invalidDates++;
                }
            }
        }
    }

    return {
        totalRecords: records.length,
        missingValues,
        negativeSales,
        invalidDates,
        duplicateRecords
    };
}

// ==========================
// DISPLAY REPORT
// ==========================
function displayReport(stats) {
    console.log("=======================================");
    console.log("        DATA QUALITY REPORT");
    console.log("=======================================");
    console.log("Total Records Loaded  : " + stats.totalRecords);
    console.log("Missing Values Found  : " + stats.missingValues);
    console.log("Negative Sales Found  : " + stats.negativeSales);
    console.log("Invalid Dates Found   : " + stats.invalidDates);
    console.log("Duplicate Records     : " + stats.duplicateRecords);
    console.log("=======================================");
    console.log("Audit Completed Successfully.");
}

// ==========================
// MAIN PROGRAM
// ==========================
async function main() {
    const filePath = await askFilePath();

    let lines;

    try {
        lines = loadDataset(filePath);
    } catch (err) {
        console.log(err.message);
        rl.close();
        return;
    }

    const stats = analyzeData(lines);
    displayReport(stats);

    rl.close();
}

// Run program
main();
