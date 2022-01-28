package cn.mfuns.webapp.webview

import java.util.EventListener

interface WebViewInitializedListener : EventListener {
    fun initialized()
}
