package uz.uzgidro.ugenews.presentation

import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import coil.load
import uz.uzgidro.ugenews.R

/**
 * Загружает картинку в [ImageView] с shimmer-скелетоном: пока идёт загрузка,
 * показывается заглушка [R.drawable.image_placeholder] с мягкой пульсацией alpha
 * (эффект «скелетона»). По готовности — плавный crossfade к картинке; пульсация гаснет.
 * Пустой/битый url оставляет статичную заглушку без пульсации.
 */
fun ImageView.loadNews(url: String?) {
    val shimmer = ObjectAnimator.ofFloat(this, "alpha", 1f, 0.45f, 1f).apply {
        duration = 1000
        repeatCount = ObjectAnimator.INFINITE
        interpolator = LinearInterpolator()
    }

    fun stopShimmer() {
        shimmer.cancel()
        alpha = 1f
    }

    if (url.isNullOrBlank()) {
        setImageResource(R.drawable.image_placeholder)
        return
    }

    shimmer.start()
    load(url) {
        crossfade(true)
        placeholder(R.drawable.image_placeholder)
        error(R.drawable.image_placeholder)
        listener(
            onSuccess = { _, _ -> stopShimmer() },
            onError = { _, _ -> stopShimmer() },
            onCancel = { stopShimmer() },
        )
    }
}
