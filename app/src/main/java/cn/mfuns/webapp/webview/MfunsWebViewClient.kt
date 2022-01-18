package cn.mfuns.webapp.webview

import android.content.Context
import android.content.Intent
import cn.mfuns.webapp.PhotoViewActivity
import java.net.URL

class MfunsWebViewClient {
    companion object {
        fun shouldOverrideUrlLoading(context: Context, url: String): Boolean {
            // Parse URL
            var path = URL(url).path
            path = url.split(path)[0] + path
            if (path.isNullOrBlank()) return false

            // Start PhotoViewActivity
            val intent = Intent(context, PhotoViewActivity::class.java)
            intent.putExtra("url", path)
            context.startActivity(intent)

            return true
        }
    }
}
