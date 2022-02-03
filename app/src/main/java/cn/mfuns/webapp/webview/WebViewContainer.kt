package cn.mfuns.webapp.webview

import android.app.Activity
import android.view.View

class WebViewContainer(useTbs: Boolean) : MfunsWebView() {
    companion object {
        lateinit var defaultContainer: WebViewContainer
    }

    private var webView: MfunsWebView? = null

    override fun initialize(activity: Activity, listener: (() -> Unit)) =
        webView!!.initialize(activity, listener)

    override fun destroy() = webView!!.destroy()

    override fun getView(): View = webView!!.getView()

    override fun goBack(): Boolean = webView!!.goBack()

    init {
        webView = if (useTbs) TbsWebView()
        else SystemWebView()
    }
}
