package uz.uzgidro.ugenews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import uz.uzgidro.ugenews.domain.AppLanguage
import uz.uzgidro.ugenews.domain.NewsModel
import uz.uzgidro.ugenews.domain.NewsRepo

/**
 * Лента новостей как [Flow] of [PagingData]. Поток пересобирается при смене языка
 * ([flatMapLatest]) — те же кэш-сущности ремапятся в выбранный язык без обращения к сети.
 * Язык по умолчанию — RU; в Wave 5 подключается к LanguageStore.
 *
 * [cacheScope] по умолчанию — [viewModelScope]; в тестах передаётся `backgroundScope`,
 * чтобы фоновая корутина [cachedIn] завершалась вместе с тестом.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val repo: NewsRepo,
    cacheScope: CoroutineScope? = null,
) : ViewModel() {

    private val language = MutableStateFlow(AppLanguage.DEFAULT)

    val news: Flow<PagingData<NewsModel>> =
        language
            .flatMapLatest { lang -> repo.newsFlow(lang) }
            .cachedIn(cacheScope ?: viewModelScope)

    fun setLanguage(lang: AppLanguage) {
        language.value = lang
    }
}
