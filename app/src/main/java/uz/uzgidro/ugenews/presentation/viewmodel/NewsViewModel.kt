package uz.uzgidro.ugenews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.uzgidro.ugenews.domain.NewsModel
import uz.uzgidro.ugenews.domain.NewsRepo
import uz.uzgidro.ugenews.domain.html.ContentBlock
import uz.uzgidro.ugenews.domain.html.HtmlContentParser

/** Состояние детального экрана: сама новость + разобранные блоки контента. */
data class NewsDetailState(
    val news: NewsModel? = null,
    val blocks: List<ContentBlock> = emptyList(),
)

/**
 * ViewModel детального экрана: читает новость из Room-кэша по id в текущем языке
 * ([LanguageStore], офлайн) и разбирает HTML-тело в блоки контента.
 */
class NewsViewModel(
    private val repo: NewsRepo,
    private val languageStore: LanguageStore,
) : ViewModel() {

    private val _state = MutableStateFlow(NewsDetailState())
    val state: StateFlow<NewsDetailState> = _state.asStateFlow()

    fun load(id: Int) {
        viewModelScope.launch {
            val language = languageStore.language.first()
            val news = repo.getById(id, language) ?: return@launch
            val blocks = withContext(Dispatchers.Default) {
                HtmlContentParser.parse(news.text)
            }
            _state.value = NewsDetailState(news = news, blocks = blocks)
        }
    }
}
