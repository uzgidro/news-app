package uz.uzgidro.ugenews.presentation

import android.content.Context

/**
 * Простой ручной DI-контейнер (граф крошечный — Hilt избыточен).
 * Хранит зависимости уровня приложения; наполняется по мере появления слоёв
 * (сеть — Wave 1, Room/Paging-репозиторий — Wave 2, LanguageStore — Wave 5).
 */
class AppContainer(private val appContext: Context) {
    // Зависимости добавляются в последующих волнах.
}
