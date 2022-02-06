package cn.mfuns.webapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import cn.mfuns.webapp.util.AndroidUtil.Companion.setFullscreen
import cn.mfuns.webapp.webview.MfunsWebViewContainer
import com.ilharper.droidup.DroidUp
import com.ilharper.droidup.droidUp

class MainActivity : AppCompatActivity() {
    private var isInitialized = false
    private lateinit var webViewContainer: MfunsWebViewContainer

    // region File Chooser

    private var fileChooserLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { fileChooserCompleted?.invoke(it) }
    var fileChooserCompleted: ((ActivityResult) -> Unit)? = null

    fun chooseFile(intent: Intent) = fileChooserLauncher.launch(intent)

    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.setOnSystemUiVisibilityChangeListener { if (isInitialized) updateTheme() }

        // Adapt to full screen
        window.setFullscreen(true)

        // Load Splash Image
        val image = ImageView(this)
        image.setImageResource(R.mipmap.splash)
        image.scaleType = ImageView.ScaleType.CENTER_CROP
        setContentView(image)

        // Load WebView
        Handler(Looper.getMainLooper()).postDelayed({
            webViewContainer = MfunsWebViewContainer(this)
            webViewContainer.initialize(this::initializeWebView)
        }, 100)
    }

    private fun initializeWebView() {
        // Disable Fullscreen
        window.setFullscreen(false)

        // Change Theme
        updateTheme()

        // Use WebView
        setContentView(webViewContainer.getView())

        // Check Update
        Handler(Looper.getMainLooper()).postDelayed({
            DroidUp.default = (DroidUp.default ?: (droidUp {
                useSimpleChecker("https://app.mfuns.cn/releases")
            })).check()
        }, 1000)

        isInitialized = true
    }

    private fun updateTheme() {
        if (
            PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("settings_display_night_mode", false)
        ) {
            window.statusBarColor = 0xff252733.toInt()
            window.navigationBarColor = 0xff202328.toInt()
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
        } else {
            window.statusBarColor = 0xff777ffb.toInt()
            window.navigationBarColor = 0xffffffff.toInt()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    private var backPressTime: Long = -2500

    override fun onBackPressed() {
        if (!isInitialized) {
            finish()
            return
        }
        if (webViewContainer.goBack()) return
        val now = System.currentTimeMillis()
        if (now - backPressTime > 2000) {
            backPressTime = now
            Toast.makeText(this, R.string.back_press_prompt, Toast.LENGTH_SHORT).show()
        } else finish()
    }

    override fun onDestroy() {
        webViewContainer.destroy()
        super.onDestroy()
    }
}
