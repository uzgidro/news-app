package uz.uzgidro.ugenews.presentation.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.uzgidro.ugenews.domain.AppLanguage
import java.util.Locale

private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore("settings")

/**
 * Хранит выбранный язык в DataStore. При первом запуске (ничего не сохранено)
 * язык берётся из локали устройства ([AppLanguage.fromLocale], фолбэк RU).
 */
class LanguageStore(private val context: Context) {

    private val key = stringPreferencesKey("app_language")
    private val deviceDefault = AppLanguage.fromLocale(Locale.getDefault())

    val language: Flow<AppLanguage> = context.languageDataStore.data.map { prefs ->
        prefs[key]?.let { AppLanguage.fromCode(it) } ?: deviceDefault
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.languageDataStore.edit { it[key] = language.code }
    }
}
