import sys

file_path = 'app/src/main/java/com/example/ui/screens/ExportBackupScreen.kt'
with open(file_path, 'r') as f:
    content = f.read()

# Remove seeded sample santri data
start_str = "    // Seeded Sample Santri Data for Rombel 7A"
end_str = "    // Dynamic Database calculations"
start_idx = content.find(start_str)
end_idx = content.find(end_str)

if start_idx != -1 and end_idx != -1:
    content = content[:start_idx] + content[end_idx:]

# Remove fallback
fallback_start = "        } else {"
fallback_end = "        }"
fallback_idx = content.find(fallback_start)
fallback_end_idx = content.find(fallback_end, fallback_idx + len(fallback_start))

if fallback_idx != -1 and fallback_end_idx != -1:
    content = content[:fallback_idx] + "\n        }" + content[fallback_end_idx + len(fallback_end):]

# Remove defaultSampleRows from remember block
content = content.replace("defaultSampleRows", "")
content = content.replace("activeSession, )", "activeSession)")

with open(file_path, 'w') as f:
    f.write(content)
print("Patched ExportBackupScreen dummy data")
