package uz.uzgidro.ugenews.domain

/**
 * Приводит дату API формата `yyyy-MM-dd HH:mm:ss` к виду `dd.MM.yyyy`.
 * Чистая строковая логика (без java.time-локали) — легко тестируется, устойчива к мусору.
 */
object DateFormatter {

    fun toDisplay(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        val datePart = raw.trim().substringBefore(' ')
        val parts = datePart.split('-')
        if (parts.size != 3) return raw.trim()
        val (y, m, d) = parts
        return if (y.length == 4 && m.length in 1..2 && d.length in 1..2) {
            "${d.padStart(2, '0')}.${m.padStart(2, '0')}.$y"
        } else {
            raw.trim()
        }
    }
}
