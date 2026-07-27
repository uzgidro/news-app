package uz.uzgidro.ugenews

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import uz.uzgidro.ugenews.data.db.NewsDatabase
import uz.uzgidro.ugenews.data.db.NewsEntity

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class NewsDaoTest {

    private lateinit var db: NewsDatabase

    private fun entity(id: Int, ordinal: Int) = NewsEntity(
        id = id, ordinal = ordinal,
        titleUz = "uz$id", titleRu = "ru$id", titleEng = "en$id",
        textUz = "<p>uz</p>", textRu = "<p>ru</p>", textEng = "<p>en</p>",
        date = "2026-07-27", img = "https://x/$id.png", views = id,
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
    fun `upsert then getById returns entity`() = runTest {
        db.newsDao().upsertAll(listOf(entity(1, 0), entity(2, 1)))
        assertEquals("ru2", db.newsDao().getById(2)?.titleRu)
        assertEquals(2, db.newsDao().count())
    }

    @Test
    fun `upsert replaces on conflict`() = runTest {
        db.newsDao().upsertAll(listOf(entity(1, 0)))
        db.newsDao().upsertAll(listOf(entity(1, 0).copy(views = 999)))
        assertEquals(1, db.newsDao().count())
        assertEquals(999, db.newsDao().getById(1)?.views)
    }

    @Test
    fun `pagingSource returns items ordered by ordinal`() = runTest {
        db.newsDao().upsertAll(listOf(entity(10, 2), entity(11, 0), entity(12, 1)))
        val source = db.newsDao().pagingSource()
        val result = source.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page
        assertEquals(listOf(11, 12, 10), result.data.map { it.id })
    }

    @Test
    fun `clearAll empties table`() = runTest {
        db.newsDao().upsertAll(listOf(entity(1, 0)))
        db.newsDao().clearAll()
        assertEquals(0, db.newsDao().count())
        assertNull(db.newsDao().getById(1))
    }
}
