package cn.mfuns.webapp

import android.annotation.SuppressLint
import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.*

class WebViewContainer {
    companion object {
        @SuppressLint("StaticFieldLeak")
        var webView: WebView? = null

        private val listeners: HashSet<WebViewContainerInitializedListener> =
            HashSet<WebViewContainerInitializedListener>()

        @SuppressLint("SetJavaScriptEnabled")
        fun initialize(context: Context, listener: WebViewContainerInitializedListener) {
            listeners += listener

            if (webView != null) notifyInitialized()

            webView = WebView(context.applicationContext)

            // Configure
            val settings = webView!!.settings
            settings.javaScriptEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.setSupportZoom(false)
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.loadsImagesAutomatically = true
            settings.defaultTextEncodingName = "utf-8"
            settings.saveFormData = true
            settings.savePassword = true

            // Cache
            settings.domStorageEnabled = true

            // Cookie
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(webView, true)

            // Handle back
            webView!!.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView!!.canGoBack()) {
                        webView!!.goBack()
                        return true
                    }
                    return false
                }
            })

            // Listen onPageFinished()
            webView!!.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    webView!!.webViewClient = WebViewClient()
                    notifyInitialized()
                }
            }

            webView!!.loadUrl("https://mfuns.cn")
        }

        private fun notifyInitialized() {
            for (listener in listeners) listener.initialized()
            listeners.clear()
        }
    }
}

interface WebViewContainerInitializedListener : EventListener {
    fun initialized()
}
