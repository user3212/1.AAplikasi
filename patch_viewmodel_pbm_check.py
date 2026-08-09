import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # ensure that the exportBackupJson also exports "pbm" with grades and attendance for compatibility
    target = r'''root\.put\("grades", gradeArray\)'''
    replacement = '''root.put("grades", gradeArray)\n        root.put("pbm", gradeArray) // Added for compatibility with version 1.3.0'''
    content = re.sub(target, replacement, content)

    target_attendance = r'''root\.put\("attendance", attendanceArray\)'''
    replacement_attendance = '''root.put("attendance", attendanceArray)\n        // PBM bisa di mix dengan attendance, tapi untuk simplifikasi simpan pbm = gradeArray'''
    content = re.sub(target_attendance, replacement_attendance, content)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/viewmodel/PesantrenViewModel.kt')
