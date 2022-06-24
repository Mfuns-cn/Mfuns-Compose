package cn.mfuns.webapp.webview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Process
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import cn.mfuns.webapp.R
import cn.mfuns.webapp.view.MainActivity
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class MfunsWebViewContainer @Inject constructor(
    private val activity: MainActivity
) {
    private var webView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    fun initialize(completed: (() -> Unit)) {
        if (webView != null) completed()

        // Initialize dex2oat
        val map = HashMap<String?, Any?>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)

        try {
            webView = WebView(activity)
        } catch (e: Exception) {
            AlertDialog.Builder(activity).apply {
                setTitle(R.string.tbs_initialize_failed)
                setMessage(R.string.tbs_initialize_failed_message)
                setPositiveButton(R.string.ok) { _, _ -> Process.killProcess(Process.myPid()) }
                setCancelable(false)
                show()
            }
            return
        }

        // Cookie
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(webView, true)
        }

        // Configure
        webView!!.apply {
            settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                setSupportZoom(false)
                builtInZoomControls = false
                displayZoomControls = false
                useWideViewPort = true
                loadWithOverviewMode = true
                cacheMode = WebSettings.LOAD_NO_CACHE
                domStorageEnabled = true
                databaseEnabled = true
                setAppCacheEnabled(true)
                setAppCacheMaxSize(1024 * 1024 * 8)
                saveFormData = true
                userAgentString =
                    "Mfuns-WebApp/${
                    activity.packageManager.getPackageInfo(
                        activity.packageName,
                        0
                    ).versionName
                    } $userAgentString"
            }

            x5WebViewExtension?.apply {
                settingsExtension?.apply {
                    setDayOrNight(
                        !PreferenceManager.getDefaultSharedPreferences(activity)
                            .getBoolean("settings_display_night_mode", false)
                    )

                    // region Scroll

                    setVerticalTrackDrawable(null)
                    setHorizontalTrackDrawable(null)
                    isVerticalScrollBarEnabled = false
                    isHorizontalScrollBarEnabled = false
                    setVerticalScrollBarDrawable(null)
                    setHorizontalScrollBarDrawable(null)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        verticalScrollbarTrackDrawable = null
                        horizontalScrollbarTrackDrawable = null
                    }

                    // endregion
                }
            }

            // Handle back
            setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView!!.canGoBack()) {
                        webView!!.goBack()
                        return true
                    }
                    return false
                }
            })

            // Listen onPageFinished()
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    webView!!.webViewClient = MfunsWebViewClient(activity)
                    completed()
                }
            }

            webChromeClient = MfunsWebChromeClient(activity)

            loadUrl(activity.getString(R.string.app_url))
        }
    }

    fun destroy() {
        webView?.apply {
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            clearHistory()
            parent?.let { (it as ViewGroup).removeView(this) }
            webView = null
        }
    }

    fun getView() = webView

    fun goBack(): Boolean = if (webView!!.canGoBack()) {
        webView!!.goBack()
        true
    } else false
}

@Module
@InstallIn(ActivityComponent::class)
class MfunsWebViewContainerModule {
    @Provides
    fun provideMfunsWebViewContainer(
        @ActivityContext context: Context
    ) = MfunsWebViewContainer(context as MainActivity)
}
