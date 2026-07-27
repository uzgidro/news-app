package uz.uzgidro.ugenews.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface NewsRepo {
    /** Пагинированная лента в выбранном языке (Room — источник, RemoteMediator — сеть). */
    fun newsFlow(language: AppLanguage): Flow<PagingData<NewsModel>>

    /** Новость из кэша по id в выбранном языке (для детального экрана; работает офлайн). */
    suspend fun getById(id: Int, language: AppLanguage): NewsModel?
}
