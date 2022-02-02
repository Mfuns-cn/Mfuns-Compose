package cn.mfuns.webapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import cn.mfuns.webapp.util.AndroidUtil.Companion.setFullscreen
import cn.mfuns.webapp.webview.WebViewContainer
import com.ilharper.droidup.DroidUp
import com.ilharper.droidup.droidUp

class MainActivity : AppCompatActivity() {
    private var isInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Adapt to full screen
        window.setFullscreen(true)

        // Load Splash Image
        val image = ImageView(this)
        image.setImageResource(R.mipmap.splash)
        image.scaleType = ImageView.ScaleType.CENTER_CROP
        setContentView(image)

        // Load WebView
        Handler(Looper.getMainLooper()).postDelayed({
            val useTbs = PreferenceManager.getDefaultSharedPreferences(this).getString(
                "settings_webview_core",
                resources.getStringArray(R.array.settings_webview_core_list)[0]
            ) == resources.getStringArray(R.array.settings_webview_core_list)[1]
            WebViewContainer.defaultContainer = WebViewContainer(useTbs)
            WebViewContainer.defaultContainer.initialize(this, this::initializeWebView)
        }, 100)
    }

    private fun initializeWebView() {
        // Disable Fullscreen
        window.setFullscreen(false)

        // Change Theme
        setTheme(R.style.Theme_Mfuns_Main)

        // Use WebView
        setContentView(WebViewContainer.defaultContainer.getView())

        // Check Update
        Handler(Looper.getMainLooper()).postDelayed({
            DroidUp.default = (DroidUp.default ?: (droidUp {
                useSimpleChecker("https://app.mfuns.cn/releases")
            })).check()
        }, 1000)

        isInitialized = true
    }

    override fun onBackPressed() {
        if (!isInitialized) {
            finish()
            return
        }
        if (!WebViewContainer.defaultContainer.goBack()) super.onBackPressed()
    }

    override fun onDestroy() {
        WebViewContainer.defaultContainer.destroy()
        super.onDestroy()
    }
}
