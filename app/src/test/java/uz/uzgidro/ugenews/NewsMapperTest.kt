package uz.uzgidro.ugenews

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.uzgidro.ugenews.data.NewsMapper
import uz.uzgidro.ugenews.data.net.dto.NewsItemDto
import uz.uzgidro.ugenews.domain.AppLanguage

class NewsMapperTest {

    private val mapper = NewsMapper()

    private fun dto() = NewsItemDto(
        id = 42,
        uz = "Sarlavha",
        uztext = "<p>Uzbek matni juda uzun</p>",
        ru = "Заголовок",
        rutext = "<p>Русский текст статьи</p>",
        eng = "Title",
        engtext = "<p>English article body</p>",
        date = "2026-07-27 10:37:26",
        img = "https://uzgidro.uz/images/news/https://upload.uzgidro.uz/upload/photo/x.jpg",
        views = 789,
    )

    @Test
    fun `picks title and text for selected language`() {
        assertEquals("Sarlavha", mapper.toModel(dto(), AppLanguage.UZ).title)
        assertEquals("Заголовок", mapper.toModel(dto(), AppLanguage.RU).title)
        assertEquals("Title", mapper.toModel(dto(), AppLanguage.ENG).title)
        assertTrue(mapper.toModel(dto(), AppLanguage.ENG).text.contains("English"))
    }

    @Test
    fun `strips broken image url prefix`() {
        val model = mapper.toModel(dto(), AppLanguage.RU)
        assertEquals("https://upload.uzgidro.uz/upload/photo/x.jpg", model.img)
    }

    @Test
    fun `excerpt is derived from html text not equal to title`() {
        val model = mapper.toModel(dto(), AppLanguage.RU)
        assertEquals("Русский текст статьи", model.excerpt)
        assertTrue(model.excerpt != model.title)
    }

    @Test
    fun `null image yields null`() {
        assertNull(mapper.toModel(dto().copy(img = null), AppLanguage.RU).img)
        assertNull(mapper.toModel(dto().copy(img = ""), AppLanguage.RU).img)
    }

    @Test
    fun `image without prefix passes through`() {
        val url = "https://upload.uzgidro.uz/upload/photo/y.png"
        assertEquals(url, NewsMapper.cleanImageUrl(url))
    }

    @Test
    fun `missing fields default safely`() {
        val bare = NewsItemDto(id = 1)
        val model = mapper.toModel(bare, AppLanguage.RU)
        assertEquals("", model.title)
        assertEquals("", model.excerpt)
        assertEquals(0, model.views)
        assertNull(model.img)
    }
}
