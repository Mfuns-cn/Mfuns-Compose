package cn.mfuns.webapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import cn.mfuns.webapp.webview.WebViewContainer
import com.ilharper.droidup.DroidUp
import com.ilharper.droidup.droidUp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(WebViewContainer.defaultContainer.getView())

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            DroidUp.default = (DroidUp.default ?: (droidUp {
                useSimpleChecker("https://app.mfuns.cn/releases")
            })).check()
        }, 1000)
    }

    override fun onBackPressed() {
        if (!WebViewContainer.defaultContainer.goBack()) super.onBackPressed()
    }

    override fun onDestroy() {
        WebViewContainer.defaultContainer.destroy()
        super.onDestroy()
    }
}
