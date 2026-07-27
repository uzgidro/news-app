package uz.uzgidro.ugenews.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Язык-агностичный кэш одной новости: храним все три языка, выбор языка
 * происходит при чтении (маппинг в NewsModel). [ordinal] сохраняет порядок
 * прихода из API для стабильной сортировки ленты.
 */
@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey val id: Int,
    val ordinal: Int,
    val titleUz: String?,
    val titleRu: String?,
    val titleEng: String?,
    val textUz: String?,
    val textRu: String?,
    val textEng: String?,
    val date: String?,
    val img: String?,
    val views: Int,
)
