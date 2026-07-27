package uz.uzgidro.ugenews.domain

import java.util.Locale

/** Язык контента, поддерживаемый API. */
enum class AppLanguage(val code: String) {
    UZ("uz"),
    RU("ru"),
    ENG("en");

    companion object {
        /** Значение по умолчанию, когда язык устройства не поддерживается. */
        val DEFAULT = RU

        /** Сопоставляет локаль устройства с поддерживаемым языком; иначе [DEFAULT]. */
        fun fromLocale(locale: Locale): AppLanguage = when (locale.language.lowercase()) {
            "uz" -> UZ
            "ru" -> RU
            "en" -> ENG
            else -> DEFAULT
        }

        /** Восстанавливает язык из сохранённого кода; иначе [DEFAULT]. */
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code == code } ?: DEFAULT
    }
}
