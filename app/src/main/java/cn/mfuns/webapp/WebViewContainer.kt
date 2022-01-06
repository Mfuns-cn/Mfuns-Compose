package cn.mfuns.webapp

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.*

class WebViewContainer {
    companion object {
        @SuppressLint("StaticFieldLeak")
        var webView: WebView? = null

        private val listeners: HashSet<WebViewContainerInitializedListener> =
            HashSet<WebViewContainerInitializedListener>()

        fun initialize(context: Context, listener: WebViewContainerInitializedListener) {
            listeners += listener

            if (webView != null) notifyInitialized()

            webView = WebView(context.applicationContext)

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
