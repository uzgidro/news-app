package uz.uzgidro.ugenews.presentation.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.uzgidro.ugenews.databinding.ItemLoadStateBinding

/** Футер ленты: спиннер при подгрузке страницы, кнопка retry при ошибке. */
class NewsLoadStateAdapter(
    private val retry: () -> Unit,
) : LoadStateAdapter<NewsLoadStateAdapter.LoadStateViewHolder>() {

    inner class LoadStateViewHolder(val binding: ItemLoadStateBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = ItemLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.loadRetry.setOnClickListener { retry() }
        return LoadStateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        with(holder.binding) {
            loadProgress.visibility = if (loadState is LoadState.Loading) android.view.View.VISIBLE else android.view.View.GONE
            loadRetry.visibility = if (loadState is LoadState.Error) android.view.View.VISIBLE else android.view.View.GONE
        }
    }
}
