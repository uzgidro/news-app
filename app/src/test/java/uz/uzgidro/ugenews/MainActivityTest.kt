package uz.uzgidro.ugenews

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import uz.uzgidro.ugenews.presentation.MainActivity

/**
 * Robolectric unit test that runs on the JVM (no device required).
 *
 * Verifies that [MainActivity] goes through its full lifecycle without crashing —
 * i.e. that `DynamicColors.applyToActivityIfAvailable` and `setContentView` complete.
 *
 * `@Config(sdk = ...)` pins the emulated SDK: the project targets API 36, but
 * Robolectric 4.14.1 ships emulated runtimes only up to API 35. The activity logic
 * does not depend on the exact API level.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class MainActivityTest {

    @Test
    fun activity_launchesWithoutCrashing() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        try {
            assertNotNull("MainActivity should be created", controller.get())
        } finally {
            controller.close()
        }
    }
}
