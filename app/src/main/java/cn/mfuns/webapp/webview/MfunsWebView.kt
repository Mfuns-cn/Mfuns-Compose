package cn.mfuns.webapp.webview

import android.app.Activity
import android.view.View

abstract class MfunsWebView {
    // Lifecycle
    abstract fun initialize(activity: Activity, listener: (() -> Unit))
    abstract fun destroy()

    // Utils
    abstract fun getView(): View
    abstract fun goBack(): Boolean

    // Listener
    protected var listener: (() -> Unit)? = null

    protected fun notifyInitialized() {
        listener?.invoke()
    }
}
