package com.lee.dateplanner.webview

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.lee.dateplanner.R
import com.lee.dateplanner.databinding.FestivalWebviewActivityBinding

/**
 * 행사 주최측 홈페이지 webview
 */
class WebViewActivity: Activity() {
    private lateinit var binding: FestivalWebviewActivityBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FestivalWebviewActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 포스터 클릭시 링크 받는 string
        val homepage = intent.getStringExtra("homepage")

        // 받은 링크로 webview 띄우기
        with(binding.festivalHomepage){
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            if (homepage != null) {
                loadUrl(homepage)
            }
        }
    }

    override fun onBackPressed() {
        val webView = findViewById<WebView>(R.id.festival_homepage)
        if (webView.canGoBack()) {
            webView.goBack()
        }
        else {
            finish()
        }
    }
}