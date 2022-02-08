package cn.mfuns.webapp.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import cn.mfuns.webapp.R
import cn.mfuns.webapp.databinding.ActivityMainBinding
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

        // Bind view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.webviewContainer.addView(
            webViewContainer.getView(),
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        useView(VIEW_WEBVIEW)

        // Check Update
        Handler(Looper.getMainLooper()).postDelayed({
            DroidUp.default = (DroidUp.default ?: (droidUp {
                useSimpleChecker("https://app.mfuns.cn/releases")
            })).check()
        }, 1000)

        isInitialized = true
    }

    // region View

    lateinit var binding: ActivityMainBinding
    private var currentView = 0

    companion object {
        const val VIEW_SPLASH = 0
        const val VIEW_WEBVIEW = 1
        const val VIEW_CUSTOM = 2
    }

    fun useView(view: Int) {
        currentView = view
        binding.apply {
            splashContainer.visibility = if (view == VIEW_SPLASH) View.VISIBLE else View.GONE
            webviewContainer.visibility = if (view == VIEW_WEBVIEW) View.VISIBLE else View.INVISIBLE
            customViewContainer.visibility = if (view == VIEW_CUSTOM) View.VISIBLE else View.INVISIBLE
        }
    }

    // endregion

    private fun updateTheme() {
        if (
            PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("settings_display_night_mode", false)
        ) {
            window.statusBarColor = -14342349 // 0xff252733.toInt()
            window.navigationBarColor = -14671064 // 0xff202328.toInt()
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
        } else {
            window.statusBarColor = -8945669 // 0xff777ffb.toInt()
            window.navigationBarColor = -1 // 0xffffffff.toInt()
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
        if (currentView == VIEW_CUSTOM) webViewContainer.getView()?.webChromeClient!!.onHideCustomView()
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
