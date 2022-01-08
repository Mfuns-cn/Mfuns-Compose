package cn.mfuns.webapp

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import cn.mfuns.webapp.webview.WebViewContainer
import cn.mfuns.webapp.webview.WebViewInitializedListener

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Load Splash Image
        val image = ImageView(this)
        image.setImageResource(R.mipmap.splash)
        image.scaleType = ImageView.ScaleType.CENTER_CROP
        setContentView(image)

        // Adapt to full screen
        val decorView = window.decorView
        val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val layoutParams = window.attributes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            else layoutParams.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = layoutParams
        }
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            val useTbs = PreferenceManager.getDefaultSharedPreferences(this).getString(
                "settings_webview_core",
                resources.getStringArray(R.array.settings_webview_core_list)[0]
            ) == resources.getStringArray(R.array.settings_webview_core_list)[1]
            WebViewContainer.defaultContainer = WebViewContainer(useTbs)
            WebViewContainer.defaultContainer.initialize(this@SplashActivity, object : WebViewInitializedListener {
                override fun initialized() {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            })
        }, 100)
    }
}
