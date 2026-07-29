package uz.uzgidro.ugenews.presentation.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.uzgidro.ugenews.R
import uz.uzgidro.ugenews.databinding.FragmentContactBinding

/**
 * Экран «Контакты» — требование политики Google Play News & Magazines:
 * приложение обязано показывать доступную контактную информацию издателя.
 * Строки кликабельны: email → почтовый клиент, телефон → набор, сайт → браузер,
 * адрес → карты. Если подходящего приложения нет, тап безопасно игнорируется.
 */
class ContactFragment : Fragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        applyInsets()

        binding.emailRow.setOnClickListener {
            launch(Intent(Intent.ACTION_SENDTO, "mailto:${getString(R.string.contacts_email)}".toUri()))
        }
        binding.phoneRow.setOnClickListener {
            launch(Intent(Intent.ACTION_DIAL, "tel:${getString(R.string.contacts_phone_dial)}".toUri()))
        }
        binding.websiteRow.setOnClickListener {
            launch(Intent(Intent.ACTION_VIEW, getString(R.string.contacts_website_url).toUri()))
        }
        binding.addressRow.setOnClickListener {
            val geo = "geo:0,0?q=${Uri.encode(getString(R.string.contacts_address))}"
            launch(Intent(Intent.ACTION_VIEW, geo.toUri()))
        }
    }

    private fun launch(intent: Intent) {
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // Нет приложения-обработчика (почты/звонилки/карт) — молча игнорируем.
        }
    }

    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.contactScroll) { v, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            v.updatePadding(bottom = bottom)
            insets
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
