package com.toghrulseyidov.apps.nytimes.ui.articles

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.toghrulseyidov.apps.nytimes.R
import com.toghrulseyidov.apps.nytimes.databinding.ActivityArticleListBinding


class ArticleListActivity : AppCompatActivity() {//}, SwipeRefreshLayout.OnRefreshListener{

    private lateinit var binding: ActivityArticleListBinding
    private lateinit var viewModel: ArticleListViewModel
    private var errorSnackbar: Snackbar? = null

//    private val lastVisibleItemPosition: Int
//        get() = binding.articleList.layoutManager.findLastVisibleItemPosition()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_article_list)

//        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)


        binding.articleList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val itemDecor = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        val dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider)
        if (dividerDrawable != null)
            itemDecor.setDrawable(dividerDrawable)
        binding.articleList.addItemDecoration(itemDecor)


//        binding.articleSwipeRefresh.isRefreshing =true
        binding.articleSwipeRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            binding.articleSwipeRefresh.isRefreshing = false
        })


//        binding.articleList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                val totalItemCount = recyclerView.layoutManager!!.itemCount
//                if (!imageRequester.isLoadingData && totalItemCount == lastVisibleItemPosition + 1) {
//                    requestPhoto()
//                }
//            }
//        })
//
//        binding.articleList.addOnScrollListener(
//            object :  PaginationListener(binding.articleList.layoutManager) {
//                override
//                void loadMoreItems () {
//                    isLoading = true;
//                    currentPage++;
//                    doApiCall();
//                }
//
//                override boolean isLastPage() {
//                    return isLastPage;
//                }
//
//                override boolean isLoading() {
//                    return isLoading;
//                }
//            })


        viewModel =
            ViewModelProviders.of(
                this,
                ViewModelFactory(this)
            )
                .get(ArticleListViewModel::class.java)

        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            if (errorMessage != null)
                showError(errorMessage)
            else
                hideError()
        })

        binding.viewModel = viewModel
    }

    private fun showError(@StringRes errorMessage: Int) {
        errorSnackbar = Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_INDEFINITE)
        errorSnackbar?.setAction(R.string.retry, viewModel.errorClickListener)
        errorSnackbar?.show()
    }

    private fun hideError() {
        errorSnackbar?.dismiss()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setIconifiedByDefault(true)
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setOnQueryTextListener(viewModel.onSearchListener)
        }

        return true
    }

}
