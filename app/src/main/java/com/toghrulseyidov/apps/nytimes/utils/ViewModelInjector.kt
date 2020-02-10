package com.toghrulseyidov.apps.nytimes.utils

import com.toghrulseyidov.apps.nytimes.network.NetworkModule
import com.toghrulseyidov.apps.nytimes.ui.articles.ArticleListViewModel
import com.toghrulseyidov.apps.nytimes.ui.articles.ArticleViewModel
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [(NetworkModule::class)])
interface ViewModelInjector {

    fun inject(articleListViewModel: ArticleListViewModel)

    fun inject(postViewModel: ArticleViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector

        fun networkModule(networkModule: NetworkModule): Builder
    }
}
