package uz.uzgidro.ugenews.presentation.fragment

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch
import uz.uzgidro.ugenews.R
import uz.uzgidro.ugenews.databinding.FragmentNewsBinding
import uz.uzgidro.ugenews.domain.html.ContentBlock
import uz.uzgidro.ugenews.presentation.App
import uz.uzgidro.ugenews.presentation.viewmodel.NewsDetailState
import uz.uzgidro.ugenews.presentation.viewmodel.NewsViewModel
import uz.uzgidro.ugenews.presentation.viewmodel.ViewModelFactory

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private val newsId: Int by lazy { requireArguments().getInt("newsId") }

    private val viewModel: NewsViewModel by viewModels {
        val c = (requireActivity().application as App).container
        ViewModelFactory(c.newsRepo, c.languageStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        applyInsets()
        observeState()
        viewModel.load(newsId)
    }

    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.detailScroll) { v, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            v.updatePadding(bottom = bottom)
            insets
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::render)
            }
        }
    }

    private fun render(state: NewsDetailState) {
        val news = state.news ?: return
        binding.collapsingToolbar.title = news.title
        binding.newsTitle.text = news.title
        binding.dateChip.text = news.date
        binding.viewsChip.text = news.views.toString()
        binding.heroImage.load(news.img) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_background)
        }
        renderBlocks(state.blocks)
    }

    private fun renderBlocks(blocks: List<ContentBlock>) {
        val container = binding.contentContainer
        container.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        blocks.forEach { block ->
            when (block) {
                is ContentBlock.Text -> {
                    val tv = inflater.inflate(R.layout.item_content_text, container, false) as MaterialTextView
                    tv.text = HtmlCompat.fromHtml(block.html, HtmlCompat.FROM_HTML_MODE_COMPACT)
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    container.addView(tv)
                }
                is ContentBlock.Image -> {
                    val iv = inflater.inflate(R.layout.item_content_image, container, false) as ImageView
                    iv.load(block.url) { crossfade(true) }
                    container.addView(iv)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
