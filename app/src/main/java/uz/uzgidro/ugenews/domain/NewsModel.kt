package uz.uzgidro.ugenews.domain

/**
 * UI-модель одной новости в выбранном языке.
 *
 * Между экранами передаётся только [id] (детальный экран читает новость из Room-кэша),
 * поэтому Parcelable не требуется — это и убирает плагин kotlin-parcelize из сборки.
 */
data class NewsModel(
    val id: Int,
    val title: String,
    val excerpt: String,
    val text: String,
    val date: String,
    val img: String?,
    val views: Int,
)
