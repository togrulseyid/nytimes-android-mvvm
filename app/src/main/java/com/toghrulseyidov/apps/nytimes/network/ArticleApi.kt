package com.toghrulseyidov.apps.nytimes.network

import com.toghrulseyidov.apps.nytimes.model.NewsList
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticleApi {

    /**
     * Get the list of the Articles from the API
     * base_url/articlesearch.json?q={query}&page={page}&sort={sort}&api-key={api-key}
     */
    @GET("articlesearch.json")
    fun getArticles(
        @Query("q") query: String,
        @Query("page") page: Int = 0, //1
        @Query("sort") sort: String, //newest
        @Query("api-key") apiKey: String
    ): Observable<NewsList>

}