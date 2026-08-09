package com.example.data.model

data class JuzInfo(
    val number: Int,
    val name: String,
    val totalAyat: Int,
    val surahs: List<Pair<String, Int>>
) {
    val displayLabel: String get() = "$name ($totalAyat Ayat)"
}

object QuranData {
    val ALL_JUZ: List<JuzInfo> = listOf(
        JuzInfo(
            number = 1,
            name = "Juz 1",
            totalAyat = 148,
            surahs = listOf("Al-Fatihah" to 7, "Al-Baqarah" to 141)
        ),
        JuzInfo(
            number = 2,
            name = "Juz 2",
            totalAyat = 111,
            surahs = listOf("Al-Baqarah" to 111)
        ),
        JuzInfo(
            number = 3,
            name = "Juz 3",
            totalAyat = 125,
            surahs = listOf("Al-Baqarah" to 34, "Ali 'Imran" to 91)
        ),
        JuzInfo(
            number = 4,
            name = "Juz 4",
            totalAyat = 131,
            surahs = listOf("Ali 'Imran" to 108, "An-Nisa'" to 23)
        ),
        JuzInfo(
            number = 5,
            name = "Juz 5",
            totalAyat = 124,
            surahs = listOf("An-Nisa'" to 124)
        ),
        JuzInfo(
            number = 6,
            name = "Juz 6",
            totalAyat = 110,
            surahs = listOf("An-Nisa'" to 29, "Al-Ma'idah" to 81)
        ),
        JuzInfo(
            number = 7,
            name = "Juz 7",
            totalAyat = 149,
            surahs = listOf("Al-Ma'idah" to 39, "Al-An'am" to 110)
        ),
        JuzInfo(
            number = 8,
            name = "Juz 8",
            totalAyat = 142,
            surahs = listOf("Al-An'am" to 55, "Al-A'raf" to 87)
        ),
        JuzInfo(
            number = 9,
            name = "Juz 9",
            totalAyat = 159,
            surahs = listOf("Al-A'raf" to 119, "Al-Anfal" to 40)
        ),
        JuzInfo(
            number = 10,
            name = "Juz 10",
            totalAyat = 127,
            surahs = listOf("Al-Anfal" to 35, "At-Tawbah" to 92)
        ),
        JuzInfo(
            number = 11,
            name = "Juz 11",
            totalAyat = 151,
            surahs = listOf("At-Tawbah" to 37, "Yunus" to 109, "Hud" to 5)
        ),
        JuzInfo(
            number = 12,
            name = "Juz 12",
            totalAyat = 170,
            surahs = listOf("Hud" to 118, "Yusuf" to 52)
        ),
        JuzInfo(
            number = 13,
            name = "Juz 13",
            totalAyat = 154,
            surahs = listOf("Yusuf" to 59, "Ar-Ra'd" to 43, "Ibrahim" to 52)
        ),
        JuzInfo(
            number = 14,
            name = "Juz 14",
            totalAyat = 227,
            surahs = listOf("Al-Hijr" to 99, "An-Nahl" to 128)
        ),
        JuzInfo(
            number = 15,
            name = "Juz 15",
            totalAyat = 185,
            surahs = listOf("Al-Isra'" to 111, "Al-Kahf" to 74)
        ),
        JuzInfo(
            number = 16,
            name = "Juz 16",
            totalAyat = 269,
            surahs = listOf("Al-Kahf" to 36, "Maryam" to 98, "Taha" to 135)
        ),
        JuzInfo(
            number = 17,
            name = "Juz 17",
            totalAyat = 190,
            surahs = listOf("Al-Anbiya'" to 112, "Al-Hajj" to 78)
        ),
        JuzInfo(
            number = 18,
            name = "Juz 18",
            totalAyat = 202,
            surahs = listOf("Al-Mu'minun" to 118, "An-Nur" to 64, "Al-Furqan" to 20)
        ),
        JuzInfo(
            number = 19,
            name = "Juz 19",
            totalAyat = 339,
            surahs = listOf("Al-Furqan" to 57, "Ash-Shu'ara'" to 227, "An-Naml" to 55)
        ),
        JuzInfo(
            number = 20,
            name = "Juz 20",
            totalAyat = 171,
            surahs = listOf("An-Naml" to 38, "Al-Qasas" to 88, "Al-'Ankabut" to 45)
        ),
        JuzInfo(
            number = 21,
            name = "Juz 21",
            totalAyat = 178,
            surahs = listOf("Al-'Ankabut" to 24, "Ar-Rum" to 60, "Luqman" to 34, "As-Sajdah" to 30, "Al-Ahzab" to 30)
        ),
        JuzInfo(
            number = 22,
            name = "Juz 22",
            totalAyat = 169,
            surahs = listOf("Al-Ahzab" to 43, "Saba'" to 54, "Fatir" to 45, "Ya-Sin" to 27)
        ),
        JuzInfo(
            number = 23,
            name = "Juz 23",
            totalAyat = 357,
            surahs = listOf("Ya-Sin" to 56, "As-Saffat" to 182, "Sad" to 88, "Az-Zumar" to 31)
        ),
        JuzInfo(
            number = 24,
            name = "Juz 24",
            totalAyat = 175,
            surahs = listOf("Az-Zumar" to 44, "Ghafir" to 85, "Fussilat" to 46)
        ),
        JuzInfo(
            number = 25,
            name = "Juz 25",
            totalAyat = 246,
            surahs = listOf("Fussilat" to 8, "Ash-Shura" to 53, "Az-Zukhruf" to 89, "Ad-Dukhan" to 59, "Al-Jathiyah" to 37)
        ),
        JuzInfo(
            number = 26,
            name = "Juz 26",
            totalAyat = 195,
            surahs = listOf("Al-Ahqaf" to 35, "Muhammad" to 38, "Al-Fath" to 29, "Al-Hujurat" to 18, "Qaf" to 45)
        ),
        JuzInfo(
            number = 27,
            name = "Juz 27",
            totalAyat = 399,
            surahs = listOf("Adh-Dhariyat" to 30, "At-Tur" to 49, "An-Najm" to 62, "Al-Qamar" to 55, "Ar-Rahman" to 78, "Al-Waqi'ah" to 96, "Al-Hadid" to 29)
        ),
        JuzInfo(
            number = 28,
            name = "Juz 28",
            totalAyat = 137,
            surahs = listOf(
                "Al-Mujadila" to 22, "Al-Hashr" to 24, "Al-Mumtahanah" to 13, "As-Saff" to 14,
                "Al-Jumu'ah" to 11, "Al-Munafiqun" to 11, "At-Taghabun" to 18, "At-Talaq" to 12, "At-Tahrim" to 12
            )
        ),
        JuzInfo(
            number = 29,
            name = "Juz 29",
            totalAyat = 431,
            surahs = listOf(
                "Al-Mulk" to 30, "Al-Qalam" to 52, "Al-Haqqah" to 52, "Al-Ma'arij" to 44, "Nuh" to 28,
                "Al-Jinn" to 28, "Al-Muzzammil" to 20, "Al-Muddaththir" to 56, "Al-Qiyamah" to 40,
                "Al-Insan" to 31, "Al-Mursalat" to 50
            )
        ),
        JuzInfo(
            number = 30,
            name = "Juz 30",
            totalAyat = 564,
            surahs = listOf(
                "An-Naba'" to 40, "An-Nazi'at" to 46, "'Abasa" to 42, "At-Takwir" to 29, "Al-Infitar" to 19, "Al-Mutaffifin" to 36,
                "Al-Inshiqaq" to 25, "Al-Buruj" to 22, "At-Tariq" to 17, "Al-A'la" to 19, "Al-Ghashiyah" to 26, "Al-Fajr" to 30,
                "Al-Balad" to 20, "Ash-Shams" to 15, "Al-Layl" to 21, "Ad-Duha" to 11, "Ash-Sharh" to 8, "At-Tin" to 8, "Al-'Alaq" to 19,
                "Al-Qadr" to 5, "Al-Bayyinah" to 8, "Az-Zalzalah" to 8, "Al-'Adiyat" to 11, "Al-Qari'ah" to 11, "At-Takathur" to 8,
                "Al-'Asr" to 3, "Al-Humazah" to 9, "Al-Fil" to 5, "Quraysh" to 4, "Al-Ma'un" to 7, "Al-Kawthar" to 3, "Al-Kafirun" to 6,
                "An-Nasr" to 3, "Al-Masad" to 5, "Al-Ikhlas" to 4, "Al-Falaq" to 5, "An-Nas" to 6
            )
        )
    )

    fun getJuzInfo(juzName: String): JuzInfo {
        val cleanName = juzName.trim()
        val match = ALL_JUZ.find { it.name.equals(cleanName, ignoreCase = true) || cleanName.startsWith(it.name, ignoreCase = true) }
        return match ?: ALL_JUZ.last() // Default to Juz 30 if not found
    }
}
