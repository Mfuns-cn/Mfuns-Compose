package cn.mfuns.webapp

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.Toast
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
        window.statusBarColor = 0xff777ffb.toInt()
        window.navigationBarColor = 0xffffffff.toInt()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

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

    private var backPressTime: Long = -2500

    override fun onBackPressed() {
        if (!isInitialized) {
            finish()
            return
        }
        if (WebViewContainer.defaultContainer.goBack()) return
        val now = System.currentTimeMillis()
        if (now - backPressTime > 2000) {
            backPressTime = now
            Toast.makeText(this, R.string.back_press_prompt, Toast.LENGTH_SHORT).show()
        } else finish()
    }

    override fun onDestroy() {
        WebViewContainer.defaultContainer.destroy()
        super.onDestroy()
    }
}
