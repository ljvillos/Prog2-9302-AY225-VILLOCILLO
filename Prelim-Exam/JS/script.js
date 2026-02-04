/*
 * Lovely June S. Villocillo
 * 23-0217-474
 */

// Hardcoded CSV content
const csvData = `
StudentID,first_name,last_name,LAB WORK 1,LAB WORK 2,LAB WORK 3,PRELIM EXAM,ATTENDANCE GRADE
073900438,Osbourne,Wakenshaw,69,5,52,12,78
114924014,Albie,Gierardi,58,92,16,57,97
111901632,Eleen,Pentony,43,81,34,36,16
084000084,Arie,Okenden,31,5,14,39,99
272471551,Alica,Muckley,49,66,97,3,95
104900721,Jo,Burleton,98,94,33,13,29
111924392,Cam,Akram,44,84,17,16,24
292970744,Celine,Brosoli,3,15,71,83,45
107004352,Alan,Belfit,31,51,36,70,48
071108313,Jeanette,Gilvear,4,78,15,69,69
042204932,Ethelin,MacCathay,48,36,23,1,11
111914218,Kakalina,Finnick,69,5,65,10,8
074906059,Mayer,Lorenzetti,36,30,100,41,92
091000080,Selia,Rosenstengel,15,42,85,68,28
055002480,Dalia,Tadd,84,86,13,91,22
063101111,Darryl,Doogood,36,3,78,13,100
071908827,Brier,Wace,69,92,23,75,40
322285668,Bucky,Udall,97,63,19,46,28
103006406,Haslett,Beaford,41,32,85,60,61
104913048,Shelley,Spring,84,73,63,59,3
051403517,Marius,Southway,28,75,29,88,92
021301869,Katharina,Storch,6,61,6,49,56
063115178,Hester,Menendez,70,46,73,40,56
084202442,Shaylynn,Scorthorne,50,80,81,96,83
275079882,Madonna,Willatt,23,12,17,83,5
071001041,Bancroft,Padfield,50,100,58,13,14
261170740,Rici,Everard,51,15,48,99,41
113105478,Lishe,Dashkovich,9,23,48,63,95
267089712,Alexandrina,Abate,34,54,79,44,71
041002203,Jordon,Ribbens,41,42,24,60,21
`;

// Parse CSV into array of objects
const students = csvData
  .trim()
  .split('\n')
  .slice(1) // skip header
  .map(line => {
    const [id, first, last, lab1, lab2, lab3, prelim, attendance] = line.split(',');
    const labAverage = (Number(lab1) + Number(lab2) + Number(lab3)) / 3;
    const classStanding = (Number(attendance) * 0.4) + (labAverage * 0.6);
    const grade = (classStanding * 0.7) + (Number(prelim) * 0.3);

    return {
      id,
      name: `${first} ${last}`,
      grade: grade.toFixed(2)
    };
  });

const tableBody = document.getElementById('tableBody');

// Render function
function render() {
    tableBody.innerHTML = '';
    students.forEach((student, index) => {
        tableBody.innerHTML += `
            <tr>
                <td>${student.id}</td>
                <td>${student.name}</td>
                <td>${student.grade}</td>
                <td><button onclick="deleteStudent(${index})">Delete</button></td>
            </tr>
        `;
    });
}

// Delete function
function deleteStudent(index) {
    students.splice(index, 1);
    render();
}

// Handle form submission (Create)
document.getElementById('studentForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const id = document.getElementById('idField').value.trim();
    const name = document.getElementById('nameField').value.trim();
    const lab1 = Number(document.getElementById('lab1Field').value);
    const lab2 = Number(document.getElementById('lab2Field').value);
    const lab3 = Number(document.getElementById('lab3Field').value);
    const prelim = Number(document.getElementById('prelimField').value);
    const attendance = Number(document.getElementById('attendanceField').value);

    // Compute grade to display
    const labAverage = (lab1 + lab2 + lab3) / 3;
    const classStanding = (attendance * 0.4) + (labAverage * 0.6);
    const grade = (classStanding * 0.7) + (prelim * 0.3);

    students.push({
        id,
        name,
        grade: grade.toFixed(2)
    });

    render();

    // Clear form
    this.reset();
});

// Initial render
render();
