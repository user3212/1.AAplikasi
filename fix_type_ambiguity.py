import sys

def fix_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()
    
    content = content.replace("val reader = java.io.BufferedReader(java.io.InputStreamReader(inputStream))", "val reader = BufferedReader(InputStreamReader(inputStream))")
    content = content.replace('viewModel.showToast("Gagal membaca file CSV")\n                }', 'viewModel.showToast("Gagal membaca file CSV")\n                }\n                Unit')
    content = content.replace('viewModel.showToast("Gagal menyimpan template")\n                }', 'viewModel.showToast("Gagal menyimpan template")\n                }\n                Unit')
    content = content.replace('outputStream.write(templateData.toByteArray())', 'outputStream.write(templateData.toByteArray())\n                        Unit')
    
    with open(filepath, 'w') as f:
        f.write(content)

fix_file('app/src/main/java/com/example/ui/screens/GenericMapelScreen.kt')
fix_file('app/src/main/java/com/example/ui/screens/Mapel1Screen.kt')
fix_file('app/src/main/java/com/example/ui/screens/SantriScreen.kt')

print("Fixed type ambiguity")
