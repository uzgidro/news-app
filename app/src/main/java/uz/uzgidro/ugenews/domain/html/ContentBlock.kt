package uz.uzgidro.ugenews.domain.html

/** Элемент контента детального экрана: абзац текста (HTML-разметка сохранена) или картинка. */
sealed interface ContentBlock {
    /** [html] — фрагмент HTML абзаца (инлайн-форматирование рендерится через Html.fromHtml). */
    data class Text(val html: String) : ContentBlock
    data class Image(val url: String) : ContentBlock
}
