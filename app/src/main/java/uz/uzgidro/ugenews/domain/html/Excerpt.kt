package uz.uzgidro.ugenews.domain.html

import org.jsoup.Jsoup

/**
 * Выводит короткий текстовый отрывок из HTML-тела новости.
 *
 * Поле `*small` в API равно заголовку, поэтому под карточкой нельзя показывать его —
 * отрывок строим из `text`: снимаем теги, декодируем сущности, схлопываем пробелы и
 * обрезаем по границе слова с добавлением многоточия.
 */
object Excerpt {

    private const val MAX_LEN = 150
    private const val ELLIPSIS = "…"

    fun from(html: String?, maxLen: Int = MAX_LEN): String {
        if (html.isNullOrBlank()) return ""
        val plain = Jsoup.parse(html).text().trim()
        if (plain.length <= maxLen) return plain

        val cut = plain.substring(0, maxLen)
        val lastSpace = cut.lastIndexOf(' ')
        val boundary = if (lastSpace in 1 until maxLen) lastSpace else maxLen
        return cut.substring(0, boundary).trimEnd() + ELLIPSIS
    }
}
