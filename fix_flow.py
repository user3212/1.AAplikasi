import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # fix the flow usage
    content = content.replace("val existingSantri = repository.allSantri.kotlinx.coroutines.flow.first()", 
                              "val existingSantri = repository.allSantri.first()")
                              
    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/viewmodel/PesantrenViewModel.kt')
