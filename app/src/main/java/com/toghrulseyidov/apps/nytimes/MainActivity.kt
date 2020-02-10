package com.toghrulseyidov.apps.nytimes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.toghrulseyidov.apps.nytimes.ui.articles.ArticleListActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val intent = Intent(this, ArticleListActivity::class.java)
        startActivity(intent)
        finish()

    }

}
