package uz.uzgidro.ugenews

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import uz.uzgidro.ugenews.data.net.dto.NewsResponseDto

/** Проверяет, что compiler-плагин serialization реально подключён (декодирует форму API). */
class NewsDtoSerializationTest {

    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    @Test
    fun `decodes api response shape`() {
        val payload = """
            {
              "items": [
                {"id": 10278, "uz": "Sarlavha", "uztext": "<p>matn</p>",
                 "ru": "Заголовок", "rutext": "<p>текст</p>",
                 "eng": "Title", "engtext": "<p>body</p>",
                 "date": "2026-07-27 10:37:26",
                 "img": "https://uzgidro.uz/images/news/https://upload.uzgidro.uz/x.png",
                 "views": 789}
              ],
              "_meta": {"totalCount": 10183, "pageCount": 510, "currentPage": 1, "perPage": 20}
            }
        """.trimIndent()

        val result = json.decodeFromString<NewsResponseDto>(payload)

        assertEquals(1, result.items.size)
        assertEquals(10278, result.items[0].id)
        assertEquals("Заголовок", result.items[0].ru)
        assertEquals(789, result.items[0].views)
        assertEquals(10183, result.meta?.totalCount)
        assertEquals(510, result.meta?.pageCount)
    }

    @Test
    fun `unknown keys are ignored and missing fields default`() {
        val payload = """{"items":[{"id":1,"unexpected":"x"}]}"""
        val result = json.decodeFromString<NewsResponseDto>(payload)
        assertEquals(1, result.items[0].id)
        assertEquals(null, result.items[0].views)
    }
}
