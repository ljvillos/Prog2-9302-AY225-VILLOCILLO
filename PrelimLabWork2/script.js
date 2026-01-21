// Hardcoded users
const users = [
    { username: "admin", password: "12345" },
    { username: "student1", password: "abc123" },
    { username: "student2", password: "pass456" }
];

// Attendance records array
let attendanceRecords = [];

// Login handler
document.getElementById("loginForm").addEventListener("submit", function (event) {
    event.preventDefault();

    const usernameInput = document.getElementById("username").value;
    const passwordInput = document.getElementById("password").value;
    const message = document.getElementById("message");
    const timestampDisplay = document.getElementById("timestamp");
    const downloadBtn = document.getElementById("downloadBtn");

    // Check if credentials match any user
    const user = users.find(
        u => u.username === usernameInput && u.password === passwordInput
    );

    if (user) {
        message.textContent = "Welcome, " + user.username + "!";
        message.style.color = "#27ae60";

        const now = new Date();
        const timestamp = now.toLocaleString();
        timestampDisplay.textContent = "Login Time: " + timestamp;

        // Record attendance
        attendanceRecords.push({
            username: user.username,
            timestamp: timestamp
        });

        downloadBtn.style.display = "block";

    } else {
        message.textContent = "Incorrect username or password!";
        message.style.color = "#c0392b";
        timestampDisplay.textContent = "";
        downloadBtn.style.display = "none";

        playBeep();
    }

    // Clear password field after attempt
    document.getElementById("password").value = "";
});

// Oscillating beep sound
function playBeep() {
    const audioContext = new (window.AudioContext || window.webkitAudioContext)();
    const oscillator = audioContext.createOscillator();
    const gainNode = audioContext.createGain();

    oscillator.type = "sine";
    oscillator.frequency.setValueAtTime(800, audioContext.currentTime);
    gainNode.gain.setValueAtTime(0.4, audioContext.currentTime);

    oscillator.connect(gainNode);
    gainNode.connect(audioContext.destination);

    oscillator.start();
    oscillator.stop(audioContext.currentTime + 0.3);
}

// Download attendance summary
document.getElementById("downloadBtn").addEventListener("click", function () {
    if (attendanceRecords.length === 0) return;

    let summary =
        "Attendance Summary\n" +
        "==================\n\n";

    attendanceRecords.forEach((record, index) => {
        summary +=
            (index + 1) + ". Username: " + record.username + "\n" +
            "   Login Time: " + record.timestamp + "\n\n";
    });

    const blob = new Blob([summary], { type: "text/plain" });
    const link = document.createElement("a");

    link.href = URL.createObjectURL(blob);
    link.download = "attendance_summary.txt";
    link.click();
});
