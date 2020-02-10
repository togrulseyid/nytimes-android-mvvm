package com.toghrulseyidov.apps.nytimes.ui.articles

import android.util.Log
import android.view.View
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

    var paginationIndex: Int = 0

    val errorClickListener =
        View.OnClickListener { loadArticlesByKeyword(searchKeyword, paginationIndex) }

    private lateinit var subscription: Disposable

    init {
        if (searchKeyword == null) {
            searchKeyword = "Azerbaijan"
        }
        loadArticlesByKeyword(searchKeyword, paginationIndex)
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    private fun loadArticlesByKeyword(searchKeyword: String?, paginationIndex: Int) {
        if (searchKeyword != null) {
            subscription = Observable
                .fromCallable { articleDao.all }
                .concatMap { dbArticleList ->
                    if (dbArticleList.isEmpty())
                        articleApi.getArticles(
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
                                    Log.d("POX", article.toString())
                                }

                            articleDao.insertAll(*apiArticleList.response.docs.toTypedArray())
                            Observable.just(apiArticleList.response.docs)
                        }
                    else
                        Observable.just(dbArticleList)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    loadingVisibility.value = View.VISIBLE
                    errorMessage.value = null
                }
                .doOnTerminate { loadingVisibility.value = View.GONE }
                .subscribe(
                    { result -> onRetrievePostListSuccess(result) },
                    { error -> onRetrievePostListError(error) }
                )
        }

    }

    private fun onRetrievePostListSuccess(postList: List<Article>) {
        articleListAdapter.updateArticleList(postList)
        paginationIndex++
    }

    private fun onRetrievePostListError(error: Throwable) {
        errorMessage.value = R.string.article_error
//        if(error.localizedMessage)
    }
}