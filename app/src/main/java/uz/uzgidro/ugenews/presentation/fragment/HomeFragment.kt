package uz.uzgidro.ugenews.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import uz.uzgidro.ugenews.R
import uz.uzgidro.ugenews.databinding.FragmentHomeBinding
import uz.uzgidro.ugenews.domain.AppLanguage
import uz.uzgidro.ugenews.presentation.App
import uz.uzgidro.ugenews.presentation.recycler.NewsAdapter
import uz.uzgidro.ugenews.presentation.recycler.NewsLoadStateAdapter
import uz.uzgidro.ugenews.presentation.viewmodel.HomeViewModel
import uz.uzgidro.ugenews.presentation.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        val c = (requireActivity().application as App).container
        ViewModelFactory(c.newsRepo, c.languageStore)
    }

    private val adapter by lazy {
        NewsAdapter { news ->
            findNavController().navigate(
                R.id.action_home_to_news,
                Bundle().apply { putInt("newsId", news.id) },
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        applyInsets()
        setupRecycler()
        observePaging()
        binding.swipeRefresh.setOnRefreshListener { adapter.refresh() }
        binding.retryButton.setOnClickListener { adapter.retry() }
    }

    private fun setupMenu() {
        binding.toolbar.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                    inflater.inflate(R.menu.menu_home, menu)
                }

                override fun onMenuItemSelected(item: MenuItem): Boolean =
                    if (item.itemId == R.id.action_language) {
                        showLanguageDialog(); true
                    } else false
            },
            viewLifecycleOwner,
        )
    }

    private fun showLanguageDialog() {
        val languages = listOf(AppLanguage.UZ, AppLanguage.RU, AppLanguage.ENG)
        val labels = arrayOf(
            getString(R.string.language_uz),
            getString(R.string.language_ru),
            getString(R.string.language_en),
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.menu_language)
            .setItems(labels) { _, which ->
                viewModel.setLanguage(languages[which])
                // Ремап ленты на новый язык произойдёт через flatMapLatest в VM.
            }
            .show()
    }

    private fun applyInsets() {
        // Нижний системный инсет — как padding списка, чтобы последняя карточка не пряталась под навбар.
        ViewCompat.setOnApplyWindowInsetsListener(binding.newsRecycler) { v, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            v.updatePadding(bottom = bottom + v.resources.getDimensionPixelSize(R.dimen.list_bottom_padding))
            insets
        }
    }

    private fun setupRecycler() {
        binding.newsRecycler.adapter = adapter.withLoadStateFooter(
            NewsLoadStateAdapter { adapter.retry() },
        )
    }

    private fun observePaging() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.news.collect { adapter.submitData(it) }
                }
                launch {
                    adapter.loadStateFlow.collect { render(it.refresh) }
                }
            }
        }
    }

    private fun render(refresh: LoadState) {
        val isEmpty = adapter.itemCount == 0
        binding.swipeRefresh.isRefreshing = refresh is LoadState.Loading && !isEmpty
        binding.progress.visibility = if (refresh is LoadState.Loading && isEmpty) View.VISIBLE else View.GONE
        binding.stateContainer.visibility = if (refresh is LoadState.Error && isEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.newsRecycler.adapter = null
        _binding = null
    }
}
