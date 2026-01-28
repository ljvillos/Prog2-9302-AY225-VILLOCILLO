function calculateGrades() {
    // Grab lab values
    const lab1 = parseFloat(document.getElementById("lab1").value);
    const lab2 = parseFloat(document.getElementById("lab2").value);
    const lab3 = parseFloat(document.getElementById("lab3").value);

    // Validate numeric input
    if ([lab1, lab2, lab3].some(n => isNaN(n) || n < 0 || n > 100)) {
        alert("Please enter valid grades 0-100.");
        return;
    }

    const labAverage = (lab1 + lab2 + lab3) / 3;

    // Attendance
    let absences = 0;
    let excused = 0;

    for (let w = 1; w <= 5; w++) {
        const week = document.getElementsByName("week" + w);
        let selected = false;
        week.forEach(r => {
            if (r.checked) {
                selected = true;
                if (r.value === "A") absences++;
                else if (r.value === "E") excused++;
            }
        });
        if (!selected) week[0].checked = true; // default to Present
    }

    const resultEl = document.getElementById("result");
    resultEl.style.color = "#2c3e50"; // default color

    // Auto-fail if 4+ absences
    if (absences >= 4) {
        resultEl.style.color = "#c0392b"; // red
        resultEl.textContent =
            `PRELIM GRADE REPORT\n\nAbsences: ${absences} | Excused: ${excused}\n\nSTATUS: FAILED\nReason: 4 or more absences result in failure for this term.`;
        return;
    }

    // Compute scores
    const attendanceScore = Math.max(100 - absences * 20, 0);
    const classStanding = attendanceScore * 0.4 + labAverage * 0.6;
    const prelimToPass = (75 - classStanding * 0.3) / 0.7;
    const prelimToExcellent = (100 - classStanding * 0.3) / 0.7;

    // Build output
    let output = `PRELIM GRADE REPORT\n\n`;
    output += `Attendance: Absences = ${absences}, Excused = ${excused}\n\n`;
    output += `Lab Work 1: ${lab1.toFixed(2)}\n`;
    output += `Lab Work 2: ${lab2.toFixed(2)}\n`;
    output += `Lab Work 3: ${lab3.toFixed(2)}\n`;
    output += `Lab Work Average: ${labAverage.toFixed(2)}\n`;
    output += `Attendance Score: ${attendanceScore.toFixed(2)}\n`;
    output += `Class Standing: ${classStanding.toFixed(2)}\n\n`;
    output += `Required Prelim Exam Score:\n`;
    output += `To PASS (75): ${prelimToPass.toFixed(2)}${prelimToPass > 100 ? " (UNACHIEVABLE)" : ""}\n`;
    output += `To EXCELLENT (100): ${prelimToExcellent.toFixed(2)}${prelimToExcellent > 100 ? " (UNACHIEVABLE)" : ""}\n\n`;

    // Remarks with color
    if (prelimToPass > 100 && prelimToExcellent > 100) {
        output += `The class standing is too low.\n`;
        output += `Even a perfect prelim exam score will not be enough to pass or reach excellence.`;
        resultEl.style.color = "#c0392b"; // red

    } else if (prelimToExcellent <= 100) {
        output += `A full score on the prelim exam will achieve a grade of 100!`;
        resultEl.style.color = "#27ae60"; // green

    } else {
        output += `A score of ${prelimToPass.toFixed(2)} on the prelim exam will let you pass this term.`;
        resultEl.style.color = "#2980b9"; // blue
    }


    resultEl.textContent = output;
}

// Force only numbers in lab inputs
["lab1", "lab2", "lab3"].forEach(id => {
    const input = document.getElementById(id);
    input.addEventListener("input", () => {
        input.value = input.value.replace(/[^0-9.]/g, '');
    });
});
