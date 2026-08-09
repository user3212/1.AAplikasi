import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    content = content.replace('repository.insertGradeRecord(record)', 'repository.insertGrade(record)')
    content = content.replace('repository.insertAttendanceRecord(record)', 'repository.insertAttendance(record)')

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/viewmodel/PesantrenViewModel.kt')
