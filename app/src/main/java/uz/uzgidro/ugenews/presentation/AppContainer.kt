package uz.uzgidro.ugenews.presentation

import android.content.Context
import androidx.room.Room
import uz.uzgidro.ugenews.data.NewsMapper
import uz.uzgidro.ugenews.data.NewsRepoImpl
import uz.uzgidro.ugenews.data.db.NewsDatabase
import uz.uzgidro.ugenews.data.net.api.NewsApi
import uz.uzgidro.ugenews.data.net.api.RetrofitClient
import uz.uzgidro.ugenews.domain.NewsRepo
import uz.uzgidro.ugenews.presentation.viewmodel.LanguageStore

/**
 * Простой ручной DI-контейнер (граф крошечный — Hilt избыточен).
 * Держит зависимости уровня приложения как лениво-инициализируемые синглтоны.
 */
class AppContainer(private val appContext: Context) {

    private val api: NewsApi by lazy { RetrofitClient.create(appContext) }

    private val database: NewsDatabase by lazy {
        Room.databaseBuilder(appContext, NewsDatabase::class.java, "uzgidro-news.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    private val mapper: NewsMapper by lazy { NewsMapper() }

    val newsRepo: NewsRepo by lazy { NewsRepoImpl(api, database, mapper) }

    val languageStore: LanguageStore by lazy { LanguageStore(appContext) }
}
