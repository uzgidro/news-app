package uz.uzgidro.ugenews.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.uzgidro.ugenews.data.db.NewsDatabase
import uz.uzgidro.ugenews.data.net.api.NewsApi
import uz.uzgidro.ugenews.domain.AppLanguage
import uz.uzgidro.ugenews.domain.NewsModel
import uz.uzgidro.ugenews.domain.NewsRepo

@OptIn(ExperimentalPagingApi::class)
class NewsRepoImpl(
    private val api: NewsApi,
    private val db: NewsDatabase,
    private val mapper: NewsMapper,
) : NewsRepo {

    private val newsDao = db.newsDao()

    override fun newsFlow(language: AppLanguage): Flow<PagingData<NewsModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = NewsRemoteMediator.PAGE_SIZE,
                enablePlaceholders = false,
            ),
            remoteMediator = NewsRemoteMediator(api, db, mapper),
            pagingSourceFactory = { newsDao.pagingSource() },
        ).flow.map { paging ->
            paging.map { entity -> mapper.entityToModel(entity, language) }
        }
    }

    override suspend fun getById(id: Int, language: AppLanguage): NewsModel? {
        return newsDao.getById(id)?.let { mapper.entityToModel(it, language) }
    }
}
