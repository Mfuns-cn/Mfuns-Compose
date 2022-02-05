package cn.mfuns.webapp.webview

import android.util.Log
import com.tencent.smtt.export.external.interfaces.ConsoleMessage
import com.tencent.smtt.sdk.WebChromeClient

class MfunsWebChromeClient : WebChromeClient() {
    override fun onConsoleMessage(message: ConsoleMessage): Boolean {
        Log.d("mfwebv", message.message())
        return true
    }
}
