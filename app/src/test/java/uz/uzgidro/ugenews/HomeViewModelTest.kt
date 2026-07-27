package uz.uzgidro.ugenews

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import uz.uzgidro.ugenews.domain.AppLanguage
import uz.uzgidro.ugenews.domain.NewsModel
import uz.uzgidro.ugenews.domain.NewsRepo

/**
 * Проверяем контракт репозитория, на котором строится лента: для каждого языка
 * поток отдаёт элементы в этом языке. Тонкая обёртка HomeViewModel
 * (flatMapLatest + cachedIn поверх этого потока) своей логики не несёт —
 * ремап языка целиком в NewsMapper (покрыт в Wave 1/2), поэтому здесь тестируем repo-поток.
 */
class HomeViewModelTest {

    private class FakeRepo : NewsRepo {
        var requestedLanguage: AppLanguage? = null
        override fun newsFlow(language: AppLanguage): Flow<PagingData<NewsModel>> {
            requestedLanguage = language
            return flowOf(
                PagingData.from(
                    listOf(
                        NewsModel(1, "${language.code}-1", "e", "t", "d", null, 1),
                        NewsModel(2, "${language.code}-2", "e", "t", "d", null, 2),
                    )
                )
            )
        }
        override suspend fun getById(id: Int, language: AppLanguage): NewsModel? = null
    }

    @Test
    fun `default language is RU`() {
        assertEquals(AppLanguage.RU, AppLanguage.DEFAULT)
    }

    @Test
    fun `flow carries items for RU`() = runTest {
        val repo = FakeRepo()
        val snapshot = repo.newsFlow(AppLanguage.RU).asSnapshot()
        assertEquals(listOf(1, 2), snapshot.map { it.id })
        assertEquals("ru-1", snapshot.first().title)
        assertEquals(AppLanguage.RU, repo.requestedLanguage)
    }

    @Test
    fun `flow re-maps titles when language changes to ENG`() = runTest {
        val repo = FakeRepo()
        val snapshot = repo.newsFlow(AppLanguage.ENG).asSnapshot()
        assertEquals("en-1", snapshot.first().title)
        assertEquals(AppLanguage.ENG, repo.requestedLanguage)
    }
}
