package com.toghrulseyidov.apps.nytimes.model

import androidx.room.*

@Dao
interface ArticleDao {
    @get:Query("SELECT * FROM articles")
    val all: List<Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg article: Article)

    @Query("DELETE FROM articles")
    fun removeAll()

}