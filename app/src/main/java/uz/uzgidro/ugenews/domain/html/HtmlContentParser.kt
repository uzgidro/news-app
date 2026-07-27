package uz.uzgidro.ugenews.domain.html

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Разбирает HTML-тело новости в упорядоченный список [ContentBlock] через Jsoup.
 *
 * Правила:
 *  - `<img>` → [ContentBlock.Image] с абсолютным URL (src или data-src);
 *  - остальной текст группируется в [ContentBlock.Text], где сохранена инлайн-разметка
 *    (`<b>`, `<i>`, `<a>` и т.п.), но декоративные обёртки (`<p>`, `<span style>`) роль
 *    разделителей абзацев. Пустые блоки отбрасываются.
 *
 * Устойчив к вложенности, битым тегам и картинкам внутри абзацев (Jsoup чинит разметку).
 */
object HtmlContentParser {

    fun parse(html: String?): List<ContentBlock> {
        if (html.isNullOrBlank()) return emptyList()

        val body = Jsoup.parseBodyFragment(html).body()
        val blocks = mutableListOf<ContentBlock>()
        val currentText = StringBuilder()

        fun flushText() {
            val chunk = currentText.toString().trim()
            if (chunk.isNotEmpty() && Jsoup.parse(chunk).text().isNotBlank()) {
                blocks += ContentBlock.Text(chunk)
            }
            currentText.setLength(0)
        }

        fun walk(node: Node) {
            when (node) {
                is TextNode -> currentText.append(node.text())
                is Element -> when (node.tagName().lowercase()) {
                    "img" -> {
                        flushText()
                        val src = node.absUrl("src").ifBlank { node.attr("src") }
                            .ifBlank { node.attr("data-src") }
                        if (src.isNotBlank()) blocks += ContentBlock.Image(src)
                    }
                    "br" -> currentText.append("<br>")
                    // Инлайн-форматирование — сохраняем тег как есть.
                    "b", "strong", "i", "em", "u", "a", "span" -> {
                        currentText.append(openTag(node))
                        node.childNodes().forEach(::walk)
                        currentText.append("</${node.tagName()}>")
                    }
                    // Блочные элементы — разделители абзацев.
                    else -> {
                        flushText()
                        node.childNodes().forEach(::walk)
                        flushText()
                    }
                }
            }
        }

        body.childNodes().forEach(::walk)
        flushText()
        return blocks
    }

    private fun openTag(el: Element): String {
        val tag = el.tagName()
        // Для ссылок сохраняем href, чтобы Html.fromHtml сделал кликабельной.
        return if (tag == "a" && el.hasAttr("href")) {
            "<a href=\"${el.attr("href")}\">"
        } else {
            "<$tag>"
        }
    }
}
