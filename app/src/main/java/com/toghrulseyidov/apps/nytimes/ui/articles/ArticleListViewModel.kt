package com.toghrulseyidov.apps.nytimes.ui.articles

import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import com.toghrulseyidov.apps.nytimes.R
import com.toghrulseyidov.apps.nytimes.core.CoreViewModel
import com.toghrulseyidov.apps.nytimes.model.Article
import com.toghrulseyidov.apps.nytimes.model.ArticleDao
import com.toghrulseyidov.apps.nytimes.network.ArticleApi
import com.toghrulseyidov.apps.nytimes.ui.articles.listeners.EndlessRecyclerOnScrollListener
import com.toghrulseyidov.apps.nytimes.utils.HTTP_ERROR_429
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ArticleListViewModel(private val articleDao: ArticleDao) : CoreViewModel() {

    @Inject
    lateinit var articleApi: ArticleApi

    val articleListAdapter: ArticleListAdapter = ArticleListAdapter()

    val onScrollListener = object :
        EndlessRecyclerOnScrollListener() {
        override fun onLoadMoreArticles() {
            Log.d("ON_SCROLL_LISTENER", "paginationIndex: " + paginationIndex)
            paginationIndex++
            loadArticlesByKeyword(true)
        }
    }

    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()

    val errorMessage: MutableLiveData<Int> = MutableLiveData()

    var searchKeyword: String? = null

    var paginationIndex: Int = 0

    val errorClickListener =
        View.OnClickListener { loadArticlesByKeyword(true) }

    val onSearchListener = object : SearchView.OnQueryTextListener {

        override fun onQueryTextChange(newText: String): Boolean {
            Log.d("POX", "text changed: $newText")
            if (newText.length > 2) {
                paginationIndex = 0
                searchKeyword = newText
                loadArticlesByKeyword(false)
                try {
                    Thread.sleep(50)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return false
        }

        override fun onQueryTextSubmit(query: String): Boolean {
            Log.d("POX", "text submitted: $query")
            paginationIndex = 0
            searchKeyword = query
            loadArticlesByKeyword(false)
            // task HERE
            return false
        }
    }

    private lateinit var subscription: Disposable

    init {
        loadArticlesByKeyword(true)
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    private fun loadArticlesByKeyword(isAddArticles: Boolean) {
        Log.d("POX", "searchKeyword: $searchKeyword $paginationIndex")
        subscription = Observable
            .fromCallable { articleDao.all }
            .concatMap { dbArticleList ->
                if (dbArticleList.isEmpty()) {
                    loader(searchKeyword!!)
                } else {
                    Observable.just(dbArticleList)
                    if (searchKeyword != null) {
                        loader(searchKeyword!!)
                    } else {
                        Observable.just(dbArticleList)
                    }
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                loadingVisibility.value = View.VISIBLE
                errorMessage.value = null
            }
            .doOnTerminate { loadingVisibility.value = View.GONE }
            .subscribe(
                { result ->
                    if (isAddArticles)
                        onAddRetrieveArticleListSuccess(result)
                    else
                        onRetrieveArticleListSuccess(result)
                },
                { error ->
                    if (searchKeyword == null) {
                        loadingVisibility.value = View.GONE
                    } else {
                        onRetrieveArticleListError(error)
                    }
                }
            )
    }

    private fun loader(searchKeyword: String): Observable<List<Article>> {
        Log.d("REST_CALL_URL", "searchKeyword: $searchKeyword, paginationIndex: $paginationIndex")
        return articleApi.getArticles(
            searchKeyword,
            paginationIndex,
            "newest",
            "DdgZsx0tRifDd82nFpxjLiiR8fAF9CFG" // TODO: Move to properties
        ).concatMap { apiArticleList ->
            articleDao.insertAll(*apiArticleList.response!!.docs.toTypedArray())
            Observable.just(apiArticleList.response.docs)
        }
    }

    private fun onRetrieveArticleListSuccess(articleList: List<Article>) {
        articleListAdapter.updateArticleList(articleList)
    }


    private fun onAddRetrieveArticleListSuccess(articleList: List<Article>) {
        articleListAdapter.addArticles(articleList)
    }

    private fun onRetrieveArticleListError(error: Throwable) {
        errorMessage.value = R.string.article_error
        if (error is retrofit2.adapter.rxjava2.HttpException) {
            if (error.message == HTTP_ERROR_429) {
                errorMessage.value = R.string.article_error_code_message
            }
        }
    }
}