package uz.uzgidro.ugenews.data

import uz.uzgidro.ugenews.data.net.dto.NewsItemDto
import uz.uzgidro.ugenews.domain.AppLanguage
import uz.uzgidro.ugenews.domain.NewsModel
import uz.uzgidro.ugenews.domain.html.Excerpt

/**
 * Преобразует DTO новости в UI-модель для выбранного языка.
 *
 * Исправляет две особенности API:
 *  - `img` — битый URL: к абсолютной ссылке приклеен префикс `https://uzgidro.uz/images/news/`;
 *    срезаем его. Пустую/битую ссылку отдаём как null.
 *  - `*small` == заголовок, поэтому отрывок выводим из HTML `text` через [Excerpt].
 */
class NewsMapper {

    fun toModel(dto: NewsItemDto, language: AppLanguage): NewsModel {
        val title = dto.title(language)
        val text = dto.text(language)
        return NewsModel(
            id = dto.id,
            title = title,
            excerpt = Excerpt.from(text),
            text = text,
            date = dto.date.orEmpty(),
            img = cleanImageUrl(dto.img),
            views = dto.views ?: 0,
        )
    }

    private fun NewsItemDto.title(lang: AppLanguage): String = when (lang) {
        AppLanguage.UZ -> uz
        AppLanguage.RU -> ru
        AppLanguage.ENG -> eng
    }.orEmpty()

    private fun NewsItemDto.text(lang: AppLanguage): String = when (lang) {
        AppLanguage.UZ -> uztext
        AppLanguage.RU -> rutext
        AppLanguage.ENG -> engtext
    }.orEmpty()

    companion object {
        private const val BROKEN_IMG_PREFIX = "https://uzgidro.uz/images/news/"

        fun cleanImageUrl(raw: String?): String? {
            if (raw.isNullOrBlank()) return null
            val cleaned = raw.removePrefix(BROKEN_IMG_PREFIX)
            return cleaned.ifBlank { null }
        }
    }
}
