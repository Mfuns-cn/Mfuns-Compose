package cn.mfuns.webapp.webview

import android.content.Context
import android.view.View

abstract class MfunsWebView {
    // Lifecycle
    abstract fun initialize(context: Context, listener: WebViewInitializedListener)
    abstract fun destroy()

    // Utils
    abstract fun getView(): View
    abstract fun goBack(): Boolean

    // Listener
    protected val listeners: HashSet<WebViewInitializedListener> =
        HashSet<WebViewInitializedListener>()

    protected fun notifyInitialized() {
        for (listener in listeners) listener.initialized()
        listeners.clear()
    }
}
