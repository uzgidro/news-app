package uz.uzgidro.ugenews

import org.junit.Assert.assertEquals
import org.junit.Test
import uz.uzgidro.ugenews.domain.AppLanguage
import java.util.Locale

@Suppress("DEPRECATION") // Locale(String) на JVM-тесте — форма API не важна
class AppLanguageTest {

    @Test
    fun `fromLocale maps supported languages`() {
        assertEquals(AppLanguage.UZ, AppLanguage.fromLocale(Locale("uz")))
        assertEquals(AppLanguage.RU, AppLanguage.fromLocale(Locale("ru")))
        assertEquals(AppLanguage.ENG, AppLanguage.fromLocale(Locale("en")))
    }

    @Test
    fun `fromLocale falls back to RU for unsupported`() {
        assertEquals(AppLanguage.RU, AppLanguage.fromLocale(Locale("fr")))
        assertEquals(AppLanguage.DEFAULT, AppLanguage.fromLocale(Locale("de")))
    }

    @Test
    fun `fromCode restores by code`() {
        assertEquals(AppLanguage.UZ, AppLanguage.fromCode("uz"))
        assertEquals(AppLanguage.ENG, AppLanguage.fromCode("en"))
    }

    @Test
    fun `fromCode falls back to default for unknown or null`() {
        assertEquals(AppLanguage.DEFAULT, AppLanguage.fromCode(null))
        assertEquals(AppLanguage.DEFAULT, AppLanguage.fromCode("xx"))
    }
}
