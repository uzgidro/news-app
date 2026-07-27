package uz.uzgidro.ugenews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import uz.uzgidro.ugenews.domain.AppLanguage
import uz.uzgidro.ugenews.domain.NewsModel
import uz.uzgidro.ugenews.domain.NewsRepo

/**
 * Лента новостей как [Flow] of [PagingData]. Поток пересобирается при смене языка
 * ([flatMapLatest] по [LanguageStore]) — те же кэш-сущности ремапятся в выбранный язык
 * без обращения к сети и без потери скролла.
 *
 * [cacheScope] по умолчанию — [viewModelScope]; в тестах передаётся `backgroundScope`.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val repo: NewsRepo,
    private val languageStore: LanguageStore,
    cacheScope: CoroutineScope? = null,
) : ViewModel() {

    val news: Flow<PagingData<NewsModel>> =
        languageStore.language
            .flatMapLatest { lang -> repo.newsFlow(lang) }
            .cachedIn(cacheScope ?: viewModelScope)

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { languageStore.setLanguage(language) }
    }
}
