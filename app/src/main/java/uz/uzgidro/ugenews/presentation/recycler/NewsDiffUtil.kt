package uz.uzgidro.ugenews.presentation.recycler

import androidx.recyclerview.widget.DiffUtil
import uz.uzgidro.ugenews.domain.NewsModel

object NewsDiffUtil : DiffUtil.ItemCallback<NewsModel>() {
    override fun areItemsTheSame(oldItem: NewsModel, newItem: NewsModel) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: NewsModel, newItem: NewsModel) = oldItem == newItem
}
