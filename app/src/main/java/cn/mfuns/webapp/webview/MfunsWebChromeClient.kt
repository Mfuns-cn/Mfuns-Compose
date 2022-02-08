package cn.mfuns.webapp.webview

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import cn.mfuns.webapp.MainActivity
import cn.mfuns.webapp.util.AndroidUtil.Companion.setFullscreen
import com.tencent.smtt.export.external.interfaces.ConsoleMessage
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.sdk.MimeTypeMap
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView

class MfunsWebChromeClient(private val activity: MainActivity) : WebChromeClient() {
    override fun onConsoleMessage(message: ConsoleMessage): Boolean {
        Log.d("mfwebv", message.message())
        return true
    }

    override fun onShowFileChooser(
        webView: WebView,
        callback: ValueCallback<Array<Uri>>,
        params: FileChooserParams
    ): Boolean {
        val intent = params.createIntent()
        extractValidMimeTypes(params.acceptTypes).apply {
            if (isEmpty()) intent.type = "*/*"
            else intent.type = joinToString(" ")
        }
        activity.fileChooserCompleted = {
            callback.onReceiveValue(FileChooserParams.parseResult(it.resultCode, it.data))
        }
        activity.chooseFile(intent)
        return true
    }

    private fun extractValidMimeTypes(mimeTypes: Array<String>): List<String> {
        val results = ArrayList<String>()
        val mimes =
            if (mimeTypes.size == 1 && mimeTypes[0].contains(","))
                mimeTypes[0].split(",").toTypedArray()
            else mimeTypes
        val typeMap = MimeTypeMap.getSingleton()
        for (mime in mimes) {
            if (mime.trim { it <= ' ' }.startsWith(".")) {
                val dMime = typeMap.getMimeTypeFromExtension(
                    mime.trim { it <= ' ' }.substring(1, mime.trim { it <= ' ' }.length)
                )
                if (!results.contains(dMime)) results.add(dMime)
            } else if (typeMap.getExtensionFromMimeType(mime) != null && !results.contains(mime))
                results.add(mime)
        }
        return results
    }

    // region Custom View

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onHideCustomView() {
        activity.apply {
            binding.customViewContainer.removeAllViews()
            useView(MainActivity.VIEW_WEBVIEW)

            // Disable fullscreen
            window.setFullscreen(false)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onShowCustomView(view: View?, callback: IX5WebChromeClient.CustomViewCallback?) {
        activity.apply {
            // Initialize custom view
            binding.customViewContainer.addView(
                view,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            useView(MainActivity.VIEW_CUSTOM)

            // Enable fullscreen
            window.setFullscreen(true, true)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    // endregion
}
