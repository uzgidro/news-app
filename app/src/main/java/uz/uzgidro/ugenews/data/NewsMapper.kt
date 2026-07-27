package uz.uzgidro.ugenews.data

import uz.uzgidro.ugenews.data.db.NewsEntity
import uz.uzgidro.ugenews.data.net.dto.NewsItemDto
import uz.uzgidro.ugenews.domain.AppLanguage
import uz.uzgidro.ugenews.domain.NewsModel
import uz.uzgidro.ugenews.domain.html.Excerpt

/**
 * Маппинг новостей на трёх уровнях:
 *  - DTO → Entity (язык-агностичный кэш, срез битого префикса img);
 *  - Entity → NewsModel(lang) (выбор языка, отрывок из HTML `text`);
 *  - DTO → NewsModel(lang) напрямую (для сетевых тестов без БД).
 *
 * Особенности API: `img` — битый URL с приклеенным префиксом; `*small` == заголовок,
 * поэтому отрывок строим из `text` через [Excerpt].
 */
class NewsMapper {

    fun dtoToEntity(dto: NewsItemDto, ordinal: Int): NewsEntity = NewsEntity(
        id = dto.id,
        ordinal = ordinal,
        titleUz = dto.uz,
        titleRu = dto.ru,
        titleEng = dto.eng,
        textUz = dto.uztext,
        textRu = dto.rutext,
        textEng = dto.engtext,
        date = dto.date,
        img = cleanImageUrl(dto.img),
        views = dto.views ?: 0,
    )

    fun entityToModel(entity: NewsEntity, language: AppLanguage): NewsModel {
        val title = when (language) {
            AppLanguage.UZ -> entity.titleUz
            AppLanguage.RU -> entity.titleRu
            AppLanguage.ENG -> entity.titleEng
        }.orEmpty()
        val text = when (language) {
            AppLanguage.UZ -> entity.textUz
            AppLanguage.RU -> entity.textRu
            AppLanguage.ENG -> entity.textEng
        }.orEmpty()
        return NewsModel(
            id = entity.id,
            title = title,
            excerpt = Excerpt.from(text),
            text = text,
            date = entity.date.orEmpty(),
            img = entity.img,
            views = entity.views,
        )
    }

    fun toModel(dto: NewsItemDto, language: AppLanguage): NewsModel {
        val title = when (language) {
            AppLanguage.UZ -> dto.uz
            AppLanguage.RU -> dto.ru
            AppLanguage.ENG -> dto.eng
        }.orEmpty()
        val text = when (language) {
            AppLanguage.UZ -> dto.uztext
            AppLanguage.RU -> dto.rutext
            AppLanguage.ENG -> dto.engtext
        }.orEmpty()
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

    companion object {
        private const val BROKEN_IMG_PREFIX = "https://uzgidro.uz/images/news/"

        fun cleanImageUrl(raw: String?): String? {
            if (raw.isNullOrBlank()) return null
            val cleaned = raw.removePrefix(BROKEN_IMG_PREFIX)
            return cleaned.ifBlank { null }
        }
    }
}
