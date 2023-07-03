package com.example.newsapp.fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.example.newsapp.R
import kotlinx.android.synthetic.main.activity_article.*

//Open the article URL in a webview
class ArticleActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        val test = intent.getSerializableExtra("data")
        article_webView.apply {
            webViewClient = WebViewClient()
            loadUrl(test as String)
        }
    }
}