package uz.uzgidro.ugenews

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.uzgidro.ugenews.domain.html.Excerpt

class ExcerptTest {

    @Test
    fun `strips html tags`() {
        val html = "<p><span style=\"font-size:22px\">Привет <a href=\"x\">мир</a></span></p>"
        assertEquals("Привет мир", Excerpt.from(html))
    }

    @Test
    fun `decodes html entities`() {
        val html = "<p>O&lsquo;zbekiston &quot;test&quot;</p>"
        val result = Excerpt.from(html)
        assertTrue(result.contains("O‘zbekiston"))
        assertTrue(result.contains("\"test\""))
    }

    @Test
    fun `collapses whitespace`() {
        val html = "<p>a</p>\r\n\r\n<p>b    c</p>"
        assertEquals("a b c", Excerpt.from(html))
    }

    @Test
    fun `short text is returned whole without ellipsis`() {
        val html = "<p>Короткий текст</p>"
        val result = Excerpt.from(html)
        assertEquals("Короткий текст", result)
        assertTrue(!result.endsWith("…"))
    }

    @Test
    fun `long text is truncated on word boundary with ellipsis`() {
        val word = "слово "
        val html = "<p>${word.repeat(60)}</p>" // ~360 символов
        val result = Excerpt.from(html, maxLen = 50)
        assertTrue("длина в пределах лимита+многоточие", result.length <= 51)
        assertTrue("оканчивается многоточием", result.endsWith("…"))
        assertTrue("нет обрезанного слова", !result.dropLast(1).endsWith("сло"))
    }

    @Test
    fun `null or blank yields empty`() {
        assertEquals("", Excerpt.from(null))
        assertEquals("", Excerpt.from(""))
        assertEquals("", Excerpt.from("   "))
    }
}
