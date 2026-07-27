package uz.uzgidro.ugenews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.uzgidro.ugenews.domain.NewsRepo

/** Простая фабрика ViewModel'ей поверх ручного [NewsRepo] из AppContainer. */
class ViewModelFactory(
    private val repo: NewsRepo,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repo) as T
        modelClass.isAssignableFrom(NewsViewModel::class.java) -> NewsViewModel(repo) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
