package cn.mfuns.webapp.webview

import android.content.Context
import android.view.View

class WebViewContainer(useTbs: Boolean) : MfunsWebView() {
    companion object {
        lateinit var defaultContainer: WebViewContainer
    }

    private var webView: MfunsWebView? = null

    override fun initialize(context: Context, listener: WebViewInitializedListener) =
        webView!!.initialize(context, listener)

    override fun destroy() = webView!!.destroy()

    override fun getView(): View = webView!!.getView()

    override fun goBack(): Boolean = webView!!.goBack()

    init {
        webView = if (useTbs) TbsWebView()
        else SystemWebView()
    }
}
