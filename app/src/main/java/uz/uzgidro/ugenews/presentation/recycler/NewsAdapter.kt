package uz.uzgidro.ugenews.presentation.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.uzgidro.ugenews.databinding.CardNewsBinding
import uz.uzgidro.ugenews.domain.NewsModel
import uz.uzgidro.ugenews.presentation.loadNews

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
            cardImage.loadNews(item.img)
            root.setOnClickListener { onClick(item) }
        }
    }
}
