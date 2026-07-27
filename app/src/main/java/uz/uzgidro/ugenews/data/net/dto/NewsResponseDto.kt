package uz.uzgidro.ugenews.data.net.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Корень ответа `GET /api/news?page=N`. */
@Serializable
data class NewsResponseDto(
    val items: List<NewsItemDto> = emptyList(),
    @SerialName("_meta") val meta: MetaDto? = null,
)

@Serializable
data class NewsItemDto(
    val id: Int,
    val uz: String? = null,
    val uztext: String? = null,
    val ru: String? = null,
    val rutext: String? = null,
    val eng: String? = null,
    val engtext: String? = null,
    val date: String? = null,
    val img: String? = null,
    val views: Int? = null,
)

@Serializable
data class MetaDto(
    val totalCount: Int = 0,
    val pageCount: Int = 0,
    val currentPage: Int = 0,
    val perPage: Int = 0,
)
