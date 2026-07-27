package uz.uzgidro.ugenews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import uz.uzgidro.ugenews.domain.NewsRepo

/**
 * ViewModel детального экрана. Наполняется в Wave 4 (загрузка новости по id из кэша +
 * разбор HTML в блоки контента). Заготовка, чтобы фабрика компилировалась в Wave 3.
 */
class NewsViewModel(
    private val repo: NewsRepo,
) : ViewModel()
