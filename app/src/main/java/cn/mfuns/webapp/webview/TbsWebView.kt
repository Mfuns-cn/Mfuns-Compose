package cn.mfuns.webapp.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Process
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import cn.mfuns.webapp.R
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.*

internal class TbsWebView : MfunsWebView() {
    private var webView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun initialize(context: Context, listener: WebViewInitializedListener) {
        listeners += listener

        if (webView != null) notifyInitialized()

        // Initialize dex2oat
        val map = HashMap<String?, Any?>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)

        try {
            webView = WebView(context.applicationContext)
        } catch (e: Exception) {
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle(R.string.webview_missing_title)
            dialog.setMessage(R.string.webview_missing_message)
            dialog.setPositiveButton(
                R.string.ok,
                DialogInterface.OnClickListener { _, _ ->
                    Process.killProcess(Process.myPid())
                })
            dialog.setCancelable(false)
            dialog.show()
            return
        }

        // Configure
        val settings = webView!!.settings
        settings.javaScriptEnabled = true
        settings.setSupportZoom(false)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.userAgentString =
            "Mfuns-WebApp/${
                context.packageManager.getPackageInfo(
                    context.packageName,
                    0
                ).versionName
            } ${settings.userAgentString}"

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
            override fun onPageFinished(view: WebView?, url: String?) {
                webView!!.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        return MfunsWebViewClient.shouldOverrideUrlLoading(context.applicationContext, url)
                    }
                }
                notifyInitialized()
            }
        }

        webView!!.loadUrl(context.getText(R.string.app_url) as String)
    }

    override fun destroy() {
        webView?.let {
            it.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            it.clearHistory()
            (it.parent as ViewGroup).removeView(it)
            webView = null
        }
    }

    override fun getView(): View = webView as View

    override fun goBack(): Boolean = if (webView!!.canGoBack()) {
        webView!!.goBack()
        true
    } else false
}
