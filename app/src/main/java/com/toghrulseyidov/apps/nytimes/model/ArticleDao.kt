package com.toghrulseyidov.apps.nytimes.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArticleDao {
    @get:Query("SELECT * FROM articles")
    val all: List<Article>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg article: Article)

}