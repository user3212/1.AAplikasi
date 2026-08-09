package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "santri")
data class Santri(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nis: String,
    val nama: String,
    val gender: String = "L",
    val kelas: String,
    val halqah: String,
    val status: String = "Aktif",
    val catatan: String = ""
)

@Entity(tableName = "tahfiz_records")
data class TahfizRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val santriId: Long,
    val santriNama: String = "",
    val tanggal: String,
    val jenisSetoran: String = "Ziyadah",
    val surahJuz: String,
    val ayatMulai: String,
    val ayatSelesai: String,
    val nilai: String = "Mumtaz (A)",
    val pengampu: String = "Ustaz Halqah",
    val catatan: String = ""
)

@Entity(tableName = "grade_records")
data class GradeRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val santriId: Long,
    val santriNama: String = "",
    val mapelCategory: String, // "MAPEL1" (Blue) or "MAPEL2" (Purple)
    val namaMapel: String,
    val tanggal: String,
    val jenisUjian: String, // "Tugas", "PTS", "PAS", "Praktek", "Lisan"
    val nilai: Double,
    val pengampu: String = "Ustaz Pengampu",
    val catatan: String = ""
)

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val santriId: Long,
    val santriNama: String = "",
    val tanggal: String,
    val sesi: String = "KBM Pagi",
    val status: String, // "H", "T", "I", "S", "A"
    val keterangan: String = ""
)

@Entity(tableName = "master_class")
data class MasterClass(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val namaKelas: String,
    val waliKelas: String = ""
)

@Entity(tableName = "master_halqah")
data class MasterHalqah(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val namaHalqah: String,
    val ustaz: String = ""
)

@Entity(tableName = "master_subject")
data class MasterSubject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val namaMapel: String,
    val category: String, // "MAPEL1", "MAPEL2", or "CUSTOM"
    val pengampu: String = "",
    val isVisible: Boolean = true
)

@Entity(tableName = "module_settings")
data class ModuleSetting(
    @PrimaryKey val key: String,
    val value: String
)
