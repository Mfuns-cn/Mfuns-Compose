package cn.mfuns.webapp

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(WebViewContainer.webView)
    }

    override fun onBackPressed() {
        if (WebViewContainer.webView != null && WebViewContainer.webView!!.canGoBack())
            WebViewContainer.webView!!.goBack()
        else
            super.onBackPressed()
    }

    override fun onDestroy() {
        if (WebViewContainer.webView != null) {
            WebViewContainer.webView!!.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            WebViewContainer.webView!!.clearHistory()
            (WebViewContainer.webView!!.parent as ViewGroup).removeView(WebViewContainer.webView)
            // WebViewContainer.webView!!.destroy()
            WebViewContainer.webView = null
        }
        super.onDestroy()
    }
}
