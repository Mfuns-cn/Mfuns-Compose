package cn.mfuns.webapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.mfuns.webapp.webview.WebViewContainer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(WebViewContainer.defaultContainer.getView())
    }

    override fun onBackPressed() {
        if (!WebViewContainer.defaultContainer.goBack()) super.onBackPressed()
    }

    override fun onDestroy() {
        WebViewContainer.defaultContainer.destroy()
        super.onDestroy()
    }
}
