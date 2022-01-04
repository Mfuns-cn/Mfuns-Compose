package cn.mfuns.webapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initializeWebView() {
        val rootWebView : WebView = findViewById(R.id.rootWebView)

        rootWebView.settings.javaScriptEnabled = true
        rootWebView.webViewClient = WebViewClient()
        rootWebView.loadUrl("https://mfuns.cn")
    }
}
