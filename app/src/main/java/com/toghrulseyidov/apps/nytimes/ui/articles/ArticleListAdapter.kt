package com.toghrulseyidov.apps.nytimes.ui.articles

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.toghrulseyidov.apps.nytimes.R
import com.toghrulseyidov.apps.nytimes.databinding.ItemArticleBinding
import com.toghrulseyidov.apps.nytimes.model.Article


class ArticleListAdapter() :
    RecyclerView.Adapter<ArticleListAdapter.ViewHolder>() {
    private lateinit var articleList: MutableList<Article>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemArticleBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_article,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleListAdapter.ViewHolder, position: Int) {
        holder.bind(articleList[position])
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
    override fun getItemCount(): Int {
        return if (::articleList.isInitialized) {
            articleList.size
        } else {
            0
        }
    }

    fun updateArticleList(list: List<Article>) {
        this.articleList = list.toMutableList()
        notifyDataSetChanged()
    }

    fun addArticles(list: List<Article>) {
        this.articleList.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val viewModel = ArticleViewModel()

        fun bind(article: Article) {
            viewModel.bind(article)
            binding.viewModel = viewModel
        }
    }
}