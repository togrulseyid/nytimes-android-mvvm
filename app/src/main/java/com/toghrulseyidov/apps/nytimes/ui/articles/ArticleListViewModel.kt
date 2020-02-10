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
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ArticleListViewModel(private val articleDao: ArticleDao) :
    CoreViewModel() {

    @Inject
    lateinit var articleApi: ArticleApi

    val articleListAdapter: ArticleListAdapter = ArticleListAdapter()

    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()

    val errorMessage: MutableLiveData<Int> = MutableLiveData()

    var searchKeyword: String? = null
//    val searchKeyword: MutableLiveData<String> = MutableLiveData()

    var paginationIndex: Int = 0

    val errorClickListener =
        View.OnClickListener { loadArticlesByKeyword(searchKeyword, paginationIndex) }

    val onSearchListener =
        object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                Log.d("POX", "text changed: $newText")
                if (newText.length > 2) {
                    loadArticlesByKeyword(newText, paginationIndex)
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d("POX", "text submitted: $query")
                loadArticlesByKeyword(query, paginationIndex)
                // task HERE
                return false
            }
        }

    private lateinit var subscription: Disposable

    init {
        loadArticlesByKeyword(null, 0)
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    private fun loadArticlesByKeyword(searchKeyword: String?, paginationIndex: Int) {
        Log.d("POX", "searchKeyword: $searchKeyword $paginationIndex")

        subscription = Observable
            .fromCallable { articleDao.all }
            .concatMap { dbArticleList ->
                if (dbArticleList.isEmpty()) {
                    loader(searchKeyword!!)
                } else {
                    Observable.just(dbArticleList)
                    if (searchKeyword != null) {
                        loader(searchKeyword)
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
                { result -> onRetrieveArticleListSuccess(result) },
                { error -> onRetrieveArticleListError(error) }
            )
    }

    private fun loader(searchKeyword: String): Observable<List<Article>> {
        return articleApi.getArticles(
            searchKeyword,
            paginationIndex,
            "newest",
            "DdgZsx0tRifDd82nFpxjLiiR8fAF9CFG" // TODO: Move to properties
        ).concatMap { apiArticleList ->
            apiArticleList
                .response!!
                .docs
                .toTypedArray()
                .forEach { article ->
                    Log.d("POX-X", article.toString())
                }

            articleDao.insertAll(*apiArticleList.response.docs.toTypedArray())
            Observable.just(apiArticleList.response.docs)
        }
    }

    private fun onRetrieveArticleListSuccess(articleList: List<Article>) {
        articleListAdapter.updateArticleList(articleList)
        Log.d("POX", "onRetrieveArticleListSuccess: ${articleList.size}")
        articleList.forEach {
            println("onRetrieveArticleListSuccess: $it")
        }
//        paginationIndex++
    }

    private fun onRetrieveArticleListError(error: Throwable) {
        errorMessage.value = R.string.article_error

        error.printStackTrace()
//        if(error.localizedMessage)
    }
}