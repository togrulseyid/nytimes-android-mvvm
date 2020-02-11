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
            Log.d("ON_SCROLL_LISTENER", "paginationIndex: $paginationIndex")
            paginationIndex++
            loadArticlesByKeyword(true)
        }
    }

    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()

    val errorMessage: MutableLiveData<Int> = MutableLiveData()

    var searchKeyword: MutableLiveData<String> = MutableLiveData()

    var paginationIndex: Int = 0

    val errorClickListener =
        View.OnClickListener { loadArticlesByKeyword(true) }

    val onSearchListener = object : SearchView.OnQueryTextListener {

        override fun onQueryTextChange(newText: String): Boolean {
            Log.d("POX", "text changed: $newText")
            if (newText.length > 2) {
                paginationIndex = 0
                searchKeyword.value = newText
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
            searchKeyword.value = query
            loadArticlesByKeyword(false)
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
                println("dbArticleList.size: ${dbArticleList.size}")

                loader(searchKeyword, isAddArticles) ?: Observable.just(dbArticleList)

            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                loadingVisibility.value = View.VISIBLE
                errorMessage.value = null
            }
            .doOnTerminate {
                loadingVisibility.value = View.GONE
                onScrollListener.setLoading(false)
            }
            .subscribe(
                { result ->
                    println("result.size: ${result.size}")
                    if (isAddArticles)
                        onAddRetrieveArticleListSuccess(result)
                    else
                        onRetrieveArticleListSuccess(result)
                },
                { error ->
                    if (searchKeyword.value == null) {
                        loadingVisibility.value = View.GONE
                        onScrollListener.setLoading(false)
                    } else {
                        onRetrieveArticleListError(error)
                    }
                }
            )
    }

    private fun loader(searchKeyword: MutableLiveData<String>, isAddArticles: Boolean): Observable<List<Article>>? {
        Log.d("REST_CALL_URL", "searchKeyword: $searchKeyword, paginationIndex: $paginationIndex")
//        if (searchKeyword.value != null) {
//            return articleApi.getArticles(
//                searchKeyword.value!!,
//                paginationIndex,
//                "newest",
//                "DdgZsx0tRifDd82nFpxjLiiR8fAF9CFG" // TODO: Move to properties
//            ).concatMap { apiArticleList ->
//                if (!isAddArticles) {
//                    articleDao.removeAll();
//                }
//                articleDao.insertAll(*apiArticleList.response!!.docs.toTypedArray())
//                Observable.just(apiArticleList.response.docs)
//            }
//        } else {
//            return null
//        }
        return articleApi.getArticles(
            searchKeyword.value!!,
            paginationIndex,
            "newest",
            "DdgZsx0tRifDd82nFpxjLiiR8fAF9CFG" // TODO: Move to properties
        ).concatMap { apiArticleList ->
            if (!isAddArticles) {
                articleDao.removeAll();
            }
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
        errorMessage.value = R.string.article_error_code_message
        if (error is retrofit2.HttpException) {
            val code: Int = error.code()
            errorMessage.value = when (code) {
                400 -> R.string.article_error_code_400
                429 -> R.string.article_error_code_429
                else -> R.string.article_error_code_message
            }
        }
        error.printStackTrace()
    }
}