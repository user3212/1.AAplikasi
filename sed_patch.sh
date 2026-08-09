#!/bin/bash
for file in app/src/main/java/com/example/ui/screens/Mapel1Screen.kt app/src/main/java/com/example/ui/screens/Mapel2Screen.kt; do
    # Replace the forEach loop
    sed -i 's/filteredSantri.forEach { santri ->/filteredSantri.forEachIndexed { index, santri ->/g' "$file"
    
    # Replace the Box with Text and the Text for santri nama (since it spans multiple lines, it's easier with python or perl)
done
