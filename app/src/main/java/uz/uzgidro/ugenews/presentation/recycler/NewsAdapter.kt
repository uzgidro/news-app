package uz.uzgidro.ugenews.presentation.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.uzgidro.ugenews.R
import uz.uzgidro.ugenews.databinding.CardNewsBinding
import uz.uzgidro.ugenews.domain.NewsModel

class NewsAdapter(
    private val onClick: (NewsModel) -> Unit,
) : PagingDataAdapter<NewsModel, NewsAdapter.NewsViewHolder>(NewsDiffUtil) {

    inner class NewsViewHolder(val binding: CardNewsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = CardNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = getItem(position) ?: return
        with(holder.binding) {
            cardTitle.text = item.title
            cardExcerpt.text = item.excerpt
            cardViews.text = item.views.toString()
            cardImage.load(item.img) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_background)
            }
            root.setOnClickListener { onClick(item) }
        }
    }
}
