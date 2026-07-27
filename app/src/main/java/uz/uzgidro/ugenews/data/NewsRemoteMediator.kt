package uz.uzgidro.ugenews.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import uz.uzgidro.ugenews.data.db.NewsDatabase
import uz.uzgidro.ugenews.data.db.NewsEntity
import uz.uzgidro.ugenews.data.db.RemoteKeys
import uz.uzgidro.ugenews.data.net.api.NewsApi

/**
 * Подтягивает страницы `GET /api/news?page=N` в Room, который служит единственным
 * источником для ленты. REFRESH чистит кэш и грузит page 1; APPEND — следующую страницу
 * (по [RemoteKeys.nextKey]); PREPEND не нужен (лента только растёт вниз).
 */
@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val api: NewsApi,
    private val db: NewsDatabase,
    private val mapper: NewsMapper,
) : RemoteMediator<Int, NewsEntity>() {

    private val newsDao = db.newsDao()
    private val keysDao = db.remoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsEntity>,
    ): MediatorResult {
        val page: Int = when (loadType) {
            LoadType.REFRESH -> FIRST_PAGE
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                val keys = keysDao.keysByNewsId(lastItem.id)
                keys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = keys != null)
            }
        }

        return try {
            val response = api.getNews(page)
            val items = response.items
            val pageCount = response.meta?.pageCount ?: page
            val endReached = items.isEmpty() || page >= pageCount

            val baseOrdinal = (page - FIRST_PAGE) * PAGE_SIZE
            val entities = items.mapIndexed { index, dto ->
                mapper.dtoToEntity(dto, ordinal = baseOrdinal + index)
            }
            val prevKey = if (page == FIRST_PAGE) null else page - 1
            val nextKey = if (endReached) null else page + 1
            val keys = entities.map { RemoteKeys(newsId = it.id, prevKey = prevKey, nextKey = nextKey) }

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    newsDao.clearAll()
                    keysDao.clearAll()
                }
                newsDao.upsertAll(entities)
                keysDao.insertAll(keys)
            }

            MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    companion object {
        const val FIRST_PAGE = 1
        const val PAGE_SIZE = 20
    }
}
