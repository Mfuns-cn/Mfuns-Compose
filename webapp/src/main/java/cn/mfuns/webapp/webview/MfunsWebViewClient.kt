package cn.mfuns.webapp.webview

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.preference.PreferenceManager
import cn.mfuns.webapp.R
import cn.mfuns.webapp.util.Viewer
import cn.mfuns.webapp.view.MainActivity
import cn.mfuns.webapp.view.PhotoViewActivity
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import java.net.URL

class MfunsWebViewClient(private val activity: MainActivity) : WebViewClient() {
    companion object {
        private val protocolWhitelist = listOf(
            "mqqopensdkapi", // App下载 - 加群链接
            "mqqwpa" // 客服
            // "tbopen" // 广告 - 淘宝
        )
    }

    override fun shouldOverrideUrlLoading(v: WebView?, url: String): Boolean {
        try {
            if (protocolWhitelist.any { x -> url.startsWith(x) }) {
                try {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (e: Exception) {
                    Toast.makeText(
                        activity,
                        R.string.settings_group_join_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return true
            }

            // Parse URL
            var path = URL(url).path
            if (path == "/") return false
            path = url.split(path)[0] + path
            if (path.isNullOrBlank()) return false

            if (Viewer.parseExt(path) != null) {
                // Use Viewer
                activity.apply {
                    when (
                        PreferenceManager.getDefaultSharedPreferences(this).getString(
                            "settings_viewer_default_action",
                            resources.getStringArray(R.array.settings_viewer_action_list)[0]
                        )
                    ) {
                        resources.getStringArray(R.array.settings_viewer_action_list)[0] -> {
                            val intent = Intent(this, PhotoViewActivity::class.java)
                            intent.putExtra("url", path)
                            startActivity(intent)
                        }
                        resources.getStringArray(R.array.settings_viewer_action_list)[1] -> {
                            Viewer.download(
                                this,
                                url,
                                { f -> Viewer.open(this, f.first) },
                                {
                                    Toast.makeText(
                                        this,
                                        R.string.viewer_download_failed,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                }
                            )
                        }
                        resources.getStringArray(R.array.settings_viewer_action_list)[2] -> {
                            Viewer.download(
                                this,
                                url,
                                { f -> Viewer.save(this, f.third, f.second) },
                                {
                                    Toast.makeText(
                                        this,
                                        R.string.viewer_download_failed,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                }
                            )
                        }
                    }
                }

                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}
