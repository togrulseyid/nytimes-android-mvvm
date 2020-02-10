package com.toghrulseyidov.apps.nytimes.core

import androidx.lifecycle.ViewModel
import com.toghrulseyidov.apps.nytimes.network.NetworkModule
import com.toghrulseyidov.apps.nytimes.ui.articles.ArticleListViewModel
import com.toghrulseyidov.apps.nytimes.ui.articles.ArticleViewModel
import com.toghrulseyidov.apps.nytimes.utils.DaggerViewModelInjector
import com.toghrulseyidov.apps.nytimes.utils.ViewModelInjector


abstract class CoreViewModel : ViewModel() {
    private val injector: ViewModelInjector = DaggerViewModelInjector
        .builder()
        .networkModule(NetworkModule)
        .build()

    init {
        inject()
    }

    private fun inject() {
        when (this) {
            is ArticleListViewModel -> injector.inject(this)
            is ArticleViewModel -> injector.inject(this)
        }
    }
}
