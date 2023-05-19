package cn.mfuns.webapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
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
import cn.mfuns.webapp.util.MfunsConfig
import cn.mfuns.webapp.webview.MfunsWebViewContainer
import com.ilharper.str4j.android.distrib.StrBomb
import com.ilharper.str4j.android.distrib.StrUpdate
import com.ilharper.str4j.android.distrib.strBomb
import com.ilharper.str4j.android.distrib.strUpdate
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var webViewContainer: MfunsWebViewContainer

    @Inject
    lateinit var mfunsConfig: MfunsConfig

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

        window.decorView.setOnSystemUiVisibilityChangeListener { ensureView() }

        // Adapt to full screen
        window.setFullscreen(true)

        // Bind view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        useView(VIEW_SPLASH)

        // Load WebView
        Handler(Looper.getMainLooper()).postDelayed({
            webViewContainer.initialize(this::initializeWebView)
        }, 100)
    }

    override fun onResume() {
        super.onResume()
        ensureView()
    }

    private fun initializeWebView() {
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
            StrBomb.default = (
                StrBomb.default ?: (
                    strBomb {
                        useRollbackLock()
                        useTimeBomb(LocalDateTime.of(2023, 8, 1, 0, 0, 0))
                        useStaticOnlineBomb(mfunsConfig.strbombUrl)
                    }
                    )
                ).check(this)

            StrUpdate.default = (
                StrUpdate.default ?: (
                    strUpdate {
                        useSimpleChecker(mfunsConfig.updateUrl)
                    }
                    )
                ).check(this)
        }, 1000)
    }

    // region View

    lateinit var binding: ActivityMainBinding
    private var currentView = 0

    companion object {
        const val VIEW_SPLASH = 0
        const val VIEW_WEBVIEW = 1
        const val VIEW_FULLSCREEN = 2
    }

    fun useView(view: Int) {
        currentView = view
        ensureView()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun ensureView() {
        binding.apply {
            splashContainer.visibility = if (currentView == VIEW_SPLASH) View.VISIBLE else View.GONE
            webviewContainer.visibility =
                if (currentView == VIEW_WEBVIEW) View.VISIBLE else View.INVISIBLE
            customViewContainer.visibility =
                if (currentView == VIEW_FULLSCREEN) View.VISIBLE else View.INVISIBLE
        }

        when (currentView) {
            VIEW_WEBVIEW -> {
                window.setFullscreen(false)
                updateTheme()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            VIEW_FULLSCREEN -> {
                window.setFullscreen(true, true)
                updateTheme()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
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
            window.decorView.systemUiVisibility =
                (window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LOW_PROFILE)
        } else {
            window.statusBarColor = -8945669 // 0xff777ffb.toInt()
            window.navigationBarColor = -1 // 0xffffffff.toInt()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.decorView.systemUiVisibility =
                    (window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
            }
        }
    }

    private var backPressTime: Long = -2500

    /**
     * 按键返回的实现。
     */
    override fun onBackPressed() {
        // 如果当前视图状态为 VIEW_SPLASH，则主活动尚未完成加载
        if (currentView == VIEW_SPLASH) {
            // 结束活动
            // 活动立即退出，用户返回启动器
            finish()
            // 结束判断
            return
        }

        // 如果当前视图状态为 VIEW_FULLSCREEN，则正在全屏播放视频
        if (currentView == VIEW_FULLSCREEN) {
            // 关闭全屏
            webViewContainer.getView()?.webChromeClient!!.onHideCustomView()
            // 结束判断
            return
        }

        // 当前视图状态一定为 VIEW_WEBVIEW，正常显示网页
        // 如果当前网页可返回，则返回并结束判断
        if (webViewContainer.goBack()) return

        // 由双击返回处理
        val now = System.currentTimeMillis()
        // 若双击时间大于 2s，则判定为首次按下
        if (now - backPressTime > 2000) {
            backPressTime = now
            Toast.makeText(this, R.string.back_press_prompt, Toast.LENGTH_SHORT).show()
        }
        // 否则直接结束活动
        // 活动立即退出，用户返回启动器
        else finish()
    }

    override fun onDestroy() {
        webViewContainer.destroy()
        super.onDestroy()
    }
}
