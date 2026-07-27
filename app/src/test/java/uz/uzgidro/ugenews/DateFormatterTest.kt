package uz.uzgidro.ugenews

import org.junit.Assert.assertEquals
import org.junit.Test
import uz.uzgidro.ugenews.domain.DateFormatter

class DateFormatterTest {

    @Test
    fun `formats api datetime to dd MM yyyy`() {
        assertEquals("27.07.2026", DateFormatter.toDisplay("2026-07-27 10:37:26"))
    }

    @Test
    fun `handles date without time`() {
        assertEquals("05.01.2026", DateFormatter.toDisplay("2026-1-5"))
    }

    @Test
    fun `returns trimmed original for unexpected format`() {
        assertEquals("nonsense", DateFormatter.toDisplay("  nonsense  "))
    }

    @Test
    fun `blank yields empty`() {
        assertEquals("", DateFormatter.toDisplay(null))
        assertEquals("", DateFormatter.toDisplay(""))
    }
}
