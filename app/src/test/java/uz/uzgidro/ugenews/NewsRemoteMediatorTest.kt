package uz.uzgidro.ugenews

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import uz.uzgidro.ugenews.data.NewsMapper
import uz.uzgidro.ugenews.data.NewsRemoteMediator
import uz.uzgidro.ugenews.data.db.NewsDatabase
import uz.uzgidro.ugenews.data.db.NewsEntity
import uz.uzgidro.ugenews.data.net.api.NewsApi
import uz.uzgidro.ugenews.data.net.dto.MetaDto
import uz.uzgidro.ugenews.data.net.dto.NewsItemDto
import uz.uzgidro.ugenews.data.net.dto.NewsResponseDto

@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class NewsRemoteMediatorTest {

    private lateinit var db: NewsDatabase

    /** Fake API: 2 страницы по 2 элемента, pageCount = 2. */
    private class FakeApi : NewsApi {
        var lastPage = -1
        override suspend fun getNews(page: Int): NewsResponseDto {
            lastPage = page
            val base = (page - 1) * 2
            val items = listOf(base + 1, base + 2).map {
                NewsItemDto(id = it, ru = "ru$it", rutext = "<p>t$it</p>", views = it)
            }
            return NewsResponseDto(items = items, meta = MetaDto(pageCount = 2, currentPage = page, perPage = 2))
        }
    }

    private val api = FakeApi()

    private fun mediator() = NewsRemoteMediator(api, db, NewsMapper())

    private fun emptyState() = PagingState<Int, NewsEntity>(
        pages = emptyList(), anchorPosition = null,
        config = PagingConfig(pageSize = 2), leadingPlaceholderCount = 0,
    )

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NewsDatabase::class.java,
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun `refresh loads first page into db`() = runTest {
        val result = mediator().load(LoadType.REFRESH, emptyState())
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertEquals(1, api.lastPage)
        assertEquals(2, db.newsDao().count())
        assertEquals(2, db.remoteKeysDao().keysByNewsId(1)?.nextKey)
    }

    @Test
    fun `refresh does not reach end when more pages exist`() = runTest {
        val result = mediator().load(LoadType.REFRESH, emptyState()) as RemoteMediator.MediatorResult.Success
        assertEquals(false, result.endOfPaginationReached)
    }

    @Test
    fun `append loads second page and reaches end`() = runTest {
        mediator().load(LoadType.REFRESH, emptyState())

        // Состояние с последним элементом page 1 (id=2), чтобы APPEND взял nextKey=2.
        val last = db.newsDao().getById(2)!!
        val page = androidx.paging.PagingSource.LoadResult.Page<Int, NewsEntity>(
            data = listOf(last), prevKey = null, nextKey = null,
        )
        val state = PagingState<Int, NewsEntity>(
            pages = listOf(page), anchorPosition = 0,
            config = PagingConfig(pageSize = 2), leadingPlaceholderCount = 0,
        )

        val result = mediator().load(LoadType.APPEND, state) as RemoteMediator.MediatorResult.Success
        assertEquals(2, api.lastPage)
        assertEquals(4, db.newsDao().count())
        assertTrue("последняя страница достигнута", result.endOfPaginationReached)
    }

    @Test
    fun `refresh clears previous cache`() = runTest {
        mediator().load(LoadType.REFRESH, emptyState())
        mediator().load(LoadType.REFRESH, emptyState())
        // Дважды REFRESH page 1 → всё ещё 2 записи (clearAll перед вставкой), не 4.
        assertEquals(2, db.newsDao().count())
    }

    /**
     * Регрессия: живой сервер `?page=N` игнорировал параметр и отдавал одинаковые id →
     * APPEND каскадил до 510 стр. Даже если API вернёт дубли, mediator обязан достичь
     * конца по _meta.pageCount, а не крутиться бесконечно.
     */
    @Test
    fun `append reaches end by pageCount even if api returns duplicate ids`() = runTest {
        val brokenApi = object : NewsApi {
            override suspend fun getNews(page: Int) = NewsResponseDto(
                items = listOf(NewsItemDto(id = 1, ru = "x", rutext = "<p>x</p>", views = 1)),
                meta = MetaDto(pageCount = 2, currentPage = page, perPage = 1),
            )
        }
        val m = NewsRemoteMediator(brokenApi, db, NewsMapper())
        m.load(LoadType.REFRESH, emptyState())

        val last = db.newsDao().getById(1)!! // nextKey = 2
        val statePage = androidx.paging.PagingSource.LoadResult.Page<Int, NewsEntity>(
            data = listOf(last), prevKey = null, nextKey = null,
        )
        val state = PagingState<Int, NewsEntity>(
            pages = listOf(statePage), anchorPosition = 0,
            config = PagingConfig(pageSize = 1), leadingPlaceholderCount = 0,
        )
        val result = m.load(LoadType.APPEND, state) as RemoteMediator.MediatorResult.Success
        // page 2 == pageCount → конец достигнут, дальше APPEND не пойдёт.
        assertTrue("конец по pageCount", result.endOfPaginationReached)
    }
}
