import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # 1. Close dropdown
    content = re.sub(
        r'onOptionSelected = { absensiRombel = it },\s+optionToString = { it },',
        r'onOptionSelected = { \n                                absensiRombel = it \n                                kelompokBelajarExpanded = false\n                            },\n                            optionToString = { it },',
        content
    )

    # 2. Change NAMA SISWA to NO and NAMA SISWA
    header_target = r'''Text\(
                            text = "NAMA SISWA",
                            style = MaterialTheme.typography.labelMedium.copy\(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            \),
                            modifier = Modifier.weight\(1.2f\)
                        \)'''
    
    header_replacement = '''Text(
                            text = "NO",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.width(32.dp)
                        )
                        Text(
                            text = "NAMA SISWA",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.weight(1.2f)
                        )'''
    # We only want to replace the first occurrence (in MapelXAbsensiView)
    # Actually it might be in MapelXPenilaianView as well. But wait, "Fitur absensi Dimapel SKI dan Mapel Informatika", I'll just replace all of them.
    content = re.sub(header_target, header_replacement, content)

    # 3. Change icon abjad to number in AbsensiView
    row_target = r'''filteredAbsensiSantri.forEach { santri ->
                                        val currentStatus = statusMap\[santri.id\] \?= "Hadir"
                                        val currentCatatan = catatanMap\[santri.id\] \?= ""

                                        Row\(
                                            modifier = Modifier
                                                .fillMaxWidth\(\)
                                                .padding\(horizontal = 10.dp, vertical = 4.dp\),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy\(8.dp\)
                                        \) {
                                            Row\(
                                                modifier = Modifier.weight\(1.2f\),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy\(8.dp\)
                                            \) {
                                                Box\(
                                                    modifier = Modifier
                                                        .size\(32.dp\)
                                                        .clip\(androidx.compose.foundation.shape.CircleShape\)
                                                        .background\(MaterialTheme.colorScheme.outline\),
                                                    contentAlignment = Alignment.Center
                                                \) {
                                                    Text\(
                                                        text = santri.nama.take\(1\).uppercase\(\),
                                                        style = MaterialTheme.typography.labelLarge.copy\(
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        \)
                                                    \)
                                                }
                                                Text\('''

    row_replacement = '''filteredAbsensiSantri.forEachIndexed { index, santri ->
                                        val currentStatus = statusMap[santri.id] ?: "Hadir"
                                        val currentCatatan = catatanMap[santri.id] ?: ""

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1.2f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "${index + 1}.",
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    ),
                                                    modifier = Modifier.width(32.dp)
                                                )
                                                Text('''

    content = re.sub(row_target, row_replacement, content)

    # 4. Change icon abjad to number in PenilaianView too?
    # No, user asked for "Fitur absensi Dimapel SKI dan Mapel Informatika dan Fitur Kehadiran Santri, icon Abjad di nama santri hapuskan ganti dengan kolom yang lebih rapi..." 
    # But replacing it everywhere might be better. Let's see if there's a second occurrence.
    row_target_2 = r'''filteredSantri.forEach { santri ->
                                            val sc = scoreMap\[santri.id\] \?= "0"
                                            val nt = noteMap\[santri.id\] \?= ""

                                            Row\(
                                                modifier = Modifier
                                                    .fillMaxWidth\(\)
                                                    .padding\(horizontal = 10.dp, vertical = 6.dp\),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy\(8.dp\)
                                            \) {
                                                Row\(
                                                    modifier = Modifier.weight\(1.2f\),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy\(8.dp\)
                                                \) {
                                                    Box\(
                                                        modifier = Modifier
                                                            .size\(32.dp\)
                                                            .clip\(androidx.compose.foundation.shape.CircleShape\)
                                                            .background\(MaterialTheme.colorScheme.outline\),
                                                        contentAlignment = Alignment.Center
                                                    \) {
                                                        Text\(
                                                            text = santri.nama.take\(1\).uppercase\(\),
                                                            style = MaterialTheme.typography.labelLarge.copy\(
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                            \)
                                                        \)
                                                    }
                                                    Text\('''

    row_replacement_2 = '''filteredSantri.forEachIndexed { index, santri ->
                                            val sc = scoreMap[santri.id] ?: "0"
                                            val nt = noteMap[santri.id] ?: ""

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.weight(1.2f),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(
                                                        text = "${index + 1}.",
                                                        style = MaterialTheme.typography.labelMedium.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        ),
                                                        modifier = Modifier.width(32.dp)
                                                    )
                                                    Text('''

    content = re.sub(row_target_2, row_replacement_2, content)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/Mapel1Screen.kt')
patch_file('app/src/main/java/com/example/ui/screens/Mapel2Screen.kt')
