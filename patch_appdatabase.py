import sys

file_path = 'app/src/main/java/com/example/data/local/AppDatabase.kt'
with open(file_path, 'r') as f:
    content = f.read()

# Change version
content = content.replace('version = 2,', 'version = 3,')

# Remove dummy data from seedInitialData
start_str = "        suspend fun seedInitialData(database: AppDatabase) {"
end_str = "    }\n}"

start_idx = content.find(start_str)
end_idx = content.find(end_str, start_idx)

if start_idx != -1 and end_idx != -1:
    new_seed = """        suspend fun seedInitialData(database: AppDatabase) {
            // Data dummy dihapus untuk rilis final
        }
"""
    content = content[:start_idx] + new_seed + content[end_idx:]

with open(file_path, 'w') as f:
    f.write(content)
print("Patched AppDatabase")
