# 🔒 GITHUB = BASELINE SOURCE OF TRUTH

==================================================
21. GITHUB ADALAH BASELINE SOURCE OF TRUTH
==================================================

PERATURAN INI SANGAT PENTING.

Source code yang berada di repository GitHub Guruqu yang sudah
berhasil BUILD dan AUDIT adalah BASELINE / SUMBER KEBENARAN
untuk konfigurasi teknis aplikasi.

Google AI Studio TIDAK BOLEH menggunakan konfigurasi project lama
yang berbeda dari baseline GitHub.

Repository GitHub yang menjadi baseline:

https://github.com/user3212/1.AAplikasi.git

Gunakan struktur, konfigurasi, dependency, Gradle, Manifest,
package, database, dan arsitektur yang SUDAH TERBUKTI BERHASIL
di repository tersebut.

JANGAN mengembalikan konfigurasi lama Google AI Studio.

==================================================
22. PRIORITAS KONFIGURASI
==================================================

Jika terdapat perbedaan antara:

A. konfigurasi project lama Google AI Studio
B. konfigurasi baseline GitHub Guruqu

MAKA:

BASELINE GITHUB = MENANG.

Jangan menggabungkan konfigurasi lama secara otomatis.

Jangan mempertahankan konfigurasi lama hanya karena file tersebut
masih ada di project Google AI Studio.

==================================================
23. KONFIGURASI GRADLE
==================================================

Gunakan konfigurasi Gradle yang sama dengan baseline GitHub.

Jangan mengembalikan konfigurasi lama seperti:

- applicationId lama
- namespace lama
- Kotlin version lama
- KSP version lama
- AGP version lama
- Gradle version lama
- dependency lama
- repository lama
- plugin lama

Jika Google AI Studio memiliki konfigurasi berbeda:

ABAIKAN konfigurasi tersebut.

Pertahankan konfigurasi GitHub yang sudah berhasil BUILD.

==================================================
24. APPLICATION ID
==================================================

WAJIB:

applicationId:
com.guruqu.pesantren.offline

Jangan menggunakan:

com.aistudio.pesantrenqu.offline

atau applicationId lain.

Application ID tidak boleh berubah hanya karena Google AI Studio
membuat project atau konfigurasi baru.

==================================================
25. NAMESPACE
==================================================

Pertahankan namespace baseline:

com.example

Jangan mengganti seluruh package aplikasi hanya karena Google AI
Studio menghasilkan package baru.

==================================================
26. VERSION
==================================================

Version hanya boleh dinaikkan sesuai instruksi pengguna.

Contoh:

v1.3:
versionCode = 4
versionName = "1.3"

Jangan membuat version baru secara otomatis.

==================================================
27. DEPENDENCY BASELINE
==================================================

Gunakan dependency yang sudah terbukti berhasil pada GitHub.

Jangan mengembalikan dependency yang sebelumnya telah dihapus.

TERUTAMA:

DILARANG menambahkan kembali:

Retrofit
OkHttp
Moshi
Gemini
Generative AI
Firebase AI
Google AI SDK

jika fitur tidak membutuhkan koneksi internet.

Jika Google AI Studio menghasilkan dependency tersebut secara otomatis,
HAPUS dependency tersebut sebelum menyelesaikan update.

==================================================
28. KSP BASELINE
==================================================

KSP adalah konfigurasi sensitif.

Jangan mengganti versi KSP yang sudah terbukti berhasil.

Jangan menambahkan processor baru hanya karena Google AI Studio
menghasilkan dependency tersebut.

Jika menggunakan Room:

gunakan Room + Room KSP sesuai baseline GitHub.

Jangan menambahkan Moshi KSP.

==================================================
29. ANDROID MANIFEST BASELINE
==================================================

AndroidManifest harus mengikuti baseline GitHub.

Pertahankan:

allowBackup="false"

Jangan menambahkan permission baru secara otomatis.

Jangan mengembalikan:

USE_EXACT_ALARM

jika tidak diperlukan.

Jangan menambahkan:

INTERNET

hanya karena Google AI Studio membuat template networking.

Guruqu tetap OFFLINE-FIRST.

==================================================
30. DATABASE BASELINE
==================================================

Database GitHub adalah baseline.

Database:

pesantrenqu_offline.db

Room version mengikuti baseline.

Jangan membuat database baru.

Jangan mengganti nama database.

Jangan menghapus database lama.

Jangan menggunakan:

fallbackToDestructiveMigration()

Jangan menghapus data pengguna.

Jika database perlu diperbarui:
buat migration yang kompatibel.

==================================================
31. SMART PARSER BASELINE
==================================================

Smart Parser yang berada pada baseline GitHub dianggap STABLE.

Jangan menggantinya dengan Smart Parser baru dari Google AI Studio.

Jangan melakukan refactor parser hanya karena AI Studio memiliki
implementasi yang berbeda.

Jika update Google AI Studio memiliki perbaikan Smart Parser:

ambil HANYA bug fix yang benar-benar diperlukan.

Pertahankan algoritma dan format parser baseline.

==================================================
32. UI BASELINE
==================================================

UI dari baseline GitHub adalah FINAL.

Google AI Studio tidak boleh:

- redesign UI;
- mengubah layout;
- mengubah warna;
- mengubah font;
- mengubah spacing;
- mengubah padding;
- mengubah ukuran tombol;
- mengubah card;
- mengubah navigation;
- mengubah icon;
- mengubah struktur screen.

Jika fitur baru membutuhkan UI tambahan:

TAMBAHKAN hanya komponen yang diperlukan.

Jangan mengubah komponen lama.

==================================================
33. SOURCE CODE LAMA GOOGLE AI STUDIO
==================================================

Jika project Google AI Studio masih memiliki:

- Gradle lama
- settings lama
- dependency lama
- Manifest lama
- package lama
- konfigurasi AI lama
- networking lama
- database lama
- Smart Parser lama

JANGAN gunakan konfigurasi tersebut.

Bandingkan dengan baseline GitHub.

Jika konflik:

GUNAKAN GITHUB BASELINE.

==================================================
34. JANGAN OVERWRITE BASELINE
==================================================

Ketika menerima update source dari Google AI Studio:

JANGAN melakukan:

"Replace entire project"

JANGAN:

"Generate project from scratch"

JANGAN:

"Rebuild all files"

Lakukan:

BASELINE GITHUB
        +
FITUR BARU AI STUDIO
        =
GURUQU UPDATE

Dengan prinsip:

KEEP + PATCH + ADD

bukan:

DELETE + REBUILD

==================================================
35. FILE YANG HARUS DIANGGAP PROTECTED
==================================================

File berikut dianggap PROTECTED.

Jangan mengganti seluruh isinya kecuali benar-benar diperlukan:

- app/build.gradle.kts
- gradle/libs.versions.toml
- settings.gradle.kts
- gradle.properties
- AndroidManifest.xml
- database classes
- DAO
- Entity
- Smart Parser
- theme
- navigation
- launcher icon

Jika perubahan diperlukan:
ubah seminimal mungkin.

==================================================
36. SEBELUM MEMBERIKAN CODE
==================================================

WAJIB lakukan pemeriksaan konfigurasi:

[ ] applicationId sama dengan GitHub
[ ] namespace sama dengan GitHub
[ ] Gradle sesuai GitHub
[ ] Kotlin sesuai GitHub
[ ] KSP sesuai GitHub
[ ] Room sesuai GitHub
[ ] Database sesuai GitHub
[ ] Smart Parser sesuai GitHub
[ ] UI tidak berubah
[ ] Layout tidak berubah
[ ] Manifest sesuai GitHub
[ ] allowBackup=false
[ ] Tidak ada Gemini
[ ] Tidak ada Generative AI
[ ] Tidak ada Retrofit
[ ] Tidak ada OkHttp
[ ] Tidak ada Moshi
[ ] Tidak ada konfigurasi AI Studio lama
[ ] Tidak ada package com.aistudio
[ ] Tidak ada destructive migration

==================================================
37. BUILD WAJIB MENGIKUTI BASELINE
==================================================

Sebelum menyatakan update selesai:

Gunakan konfigurasi build yang sama dengan GitHub baseline.

Build:

./gradlew clean assembleRelease

Jika build gagal:

JANGAN mengganti dependency atau Gradle secara acak.

Cari penyebab error.

Perbaiki source yang menyebabkan error.

Pertahankan baseline.

==================================================
38. ATURAN UTAMA GITHUB → AI STUDIO
==================================================

GITHUB = BASELINE.

AI STUDIO = TEMPAT PENGEMBANGAN FITUR.

AI STUDIO TIDAK BOLEH MENJADI SUMBER KONFIGURASI.

Setiap kali Google AI Studio menghasilkan konfigurasi baru,
bandingkan dengan GitHub terlebih dahulu.

Konfigurasi GitHub selalu menjadi acuan.

==================================================
39. HASIL AKHIR
==================================================

Target setiap update:

GitHub Stable
      ↓
Import/Sync ke AI Studio
      ↓
Tambahkan fitur baru
      ↓
Pertahankan konfigurasi GitHub
      ↓
Build
      ↓
Test
      ↓
Export kembali

Tidak boleh ada konfigurasi lama Google AI Studio yang
menggantikan konfigurasi GitHub.

==================================================
40. PESAN WAJIB SEBELUM MODIFIKASI
==================================================

Sebelum mengubah source, tampilkan:

"BASELINE GITHUB TERDETEKSI"

kemudian tampilkan:

1. Application ID
2. Namespace
3. Kotlin
4. KSP
5. Room
6. Database version
7. Smart Parser
8. Dependency
9. Manifest
10. Build configuration

Kemudian tuliskan:

"Konfigurasi Google AI Studio lama akan DIABAIKAN.
GitHub baseline digunakan sebagai sumber konfigurasi utama."

Baru setelah itu lakukan perubahan fitur.

==================================================
FINAL PROTECTION RULE
==================================================

JANGAN PERNAH MENGANGGAP PROJECT GOOGLE AI STUDIO SAAT INI
SEBAGAI VERSI TERBARU ATAU VERSI YANG BENAR.

PROJECT GOOGLE AI STUDIO SAAT INI MUNGKIN MENGANDUNG
KONFIGURASI LAMA DAN ERROR.

GITHUB REPOSITORY GURUQU YANG SUDAH BERHASIL BUILD
ADALAH BASELINE YANG BENAR.

Jika terdapat konflik antara Google AI Studio dan GitHub:

GITHUB MENANG.

Jika ada file Google AI Studio yang berbeda dari GitHub:

JANGAN langsung mengganti GitHub baseline.

Jika ada keraguan:

STOP.
Jelaskan perbedaannya.
Jangan melakukan perubahan otomatis.

Prioritas:

1. Data pengguna
2. Database
3. Build yang stabil
4. Smart Parser
5. Offline architecture
6. UI/Layout
7. Fitur baru

Fitur baru tidak boleh mengorbankan enam hal di atas.
