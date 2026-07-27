package uz.uzgidro.ugenews

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.uzgidro.ugenews.domain.html.ContentBlock
import uz.uzgidro.ugenews.domain.html.HtmlContentParser

class HtmlContentParserTest {

    @Test
    fun `paragraphs become text blocks`() {
        val blocks = HtmlContentParser.parse("<p>Первый абзац</p><p>Второй абзац</p>")
        assertEquals(2, blocks.size)
        assertTrue(blocks.all { it is ContentBlock.Text })
    }

    @Test
    fun `img becomes image block with url`() {
        val html = "<p>текст</p><img src=\"https://x.uz/a.png\"/><p>ещё</p>"
        val blocks = HtmlContentParser.parse(html)
        val images = blocks.filterIsInstance<ContentBlock.Image>()
        assertEquals(1, images.size)
        assertEquals("https://x.uz/a.png", images.first().url)
    }

    @Test
    fun `image inside paragraph splits text around it`() {
        val html = "<p>до <img src=\"https://x.uz/b.jpg\"/> после</p>"
        val blocks = HtmlContentParser.parse(html)
        assertEquals(ContentBlock.Text("до"), blocks[0])
        assertEquals(ContentBlock.Image("https://x.uz/b.jpg"), blocks[1])
        assertEquals(ContentBlock.Text("после"), blocks[2])
    }

    @Test
    fun `inline formatting is preserved in text block`() {
        val html = "<p><span style=\"font-size:22px\">Важно <a href=\"https://l.uz\">ссылка</a></span></p>"
        val text = HtmlContentParser.parse(html).filterIsInstance<ContentBlock.Text>().first()
        assertTrue("href сохранён", text.html.contains("href=\"https://l.uz\""))
        assertTrue(text.html.contains("ссылка"))
    }

    @Test
    fun `nested blocks are flattened in order`() {
        val html = "<div><p>A</p><div><p>B</p></div></div>"
        val texts = HtmlContentParser.parse(html).filterIsInstance<ContentBlock.Text>()
        assertTrue(texts.any { it.html.contains("A") })
        assertTrue(texts.any { it.html.contains("B") })
        // порядок A перед B
        val order = HtmlContentParser.parse(html)
            .filterIsInstance<ContentBlock.Text>().map { Regex("[AB]").find(it.html)?.value }
        assertEquals(listOf("A", "B"), order)
    }

    @Test
    fun `malformed html does not crash and yields text`() {
        val html = "<p>Незакрытый <b>жирный <p>новый абзац"
        val blocks = HtmlContentParser.parse(html)
        assertTrue(blocks.isNotEmpty())
        assertTrue(blocks.all { it is ContentBlock.Text })
    }

    @Test
    fun `blank or null yields empty list`() {
        assertTrue(HtmlContentParser.parse(null).isEmpty())
        assertTrue(HtmlContentParser.parse("").isEmpty())
        assertTrue(HtmlContentParser.parse("   ").isEmpty())
        assertTrue(HtmlContentParser.parse("<p></p><p>  </p>").isEmpty())
    }
}
