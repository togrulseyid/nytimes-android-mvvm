package com.toghrulseyidov.apps.nytimes.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.toghrulseyidov.apps.nytimes.model.Article
import com.toghrulseyidov.apps.nytimes.model.ArticleDao

@Database(entities = [Article::class], version = 1)
@TypeConverters(MultimediaTypeConverters::class)
abstract class NYTDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao
}
