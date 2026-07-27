package uz.uzgidro.ugenews.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import uz.uzgidro.ugenews.R

/**
 * Единственная Activity — хост навигации (лента → детальный экран).
 * Edge-to-edge обязателен при targetSdk 37; инсеты обрабатываются во фрагментах.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivityIfAvailable(this)
        setContentView(R.layout.activity_main)
    }
}
