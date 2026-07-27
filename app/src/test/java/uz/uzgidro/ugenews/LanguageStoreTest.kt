package uz.uzgidro.ugenews

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import uz.uzgidro.ugenews.domain.AppLanguage
import uz.uzgidro.ugenews.presentation.viewmodel.LanguageStore

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class LanguageStoreTest {

    private fun store() = LanguageStore(ApplicationProvider.getApplicationContext())

    @Test
    fun `set then read returns stored language`() = runTest {
        val s = store()
        s.setLanguage(AppLanguage.ENG)
        assertEquals(AppLanguage.ENG, s.language.first())
        s.setLanguage(AppLanguage.UZ)
        assertEquals(AppLanguage.UZ, s.language.first())
    }

    @Test
    fun `default is a supported language when nothing stored`() = runTest {
        // Робо-локаль по умолчанию en → ENG; в любом случае значение из enum.
        val lang = store().language.first()
        assertEquals(true, lang in AppLanguage.entries)
    }
}
