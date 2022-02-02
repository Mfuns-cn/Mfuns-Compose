package cn.mfuns.webapp.webview

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.preference.PreferenceManager
import cn.mfuns.webapp.PhotoViewActivity
import cn.mfuns.webapp.R
import cn.mfuns.webapp.util.AndroidUtil
import cn.mfuns.webapp.util.Viewer
import java.net.URL

class MfunsWebViewClient {
    companion object {
        fun shouldOverrideUrlLoading(context: Context, url: String): Boolean {
            // Parse URL
            var path = URL(url).path
            if (path == "/") return false
            path = url.split(path)[0] + path
            if (path.isNullOrBlank()) return false

            when (PreferenceManager.getDefaultSharedPreferences(context).getString(
                "settings_viewer_default_action",
                context.resources.getStringArray(R.array.settings_viewer_action_list)[0]
            )) {
                context.resources.getStringArray(R.array.settings_viewer_action_list)[0] -> {
                    val intent = Intent(context, PhotoViewActivity::class.java)
                    intent.putExtra("url", path)
                    context.startActivity(intent)
                }
                context.resources.getStringArray(R.array.settings_viewer_action_list)[1] -> {
                    AndroidUtil.getCurrentActivity().apply {
                        Viewer.download(
                            this,
                            url,
                            { f -> Viewer.open(this, f.first) },
                            {
                                Toast.makeText(this, R.string.viewer_download_failed, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        )
                    }
                }
                context.resources.getStringArray(R.array.settings_viewer_action_list)[2] -> {
                    AndroidUtil.getCurrentActivity().apply {
                        Viewer.download(
                            this,
                            url,
                            { f -> Viewer.save(this, f.third, f.second) },
                            {
                                Toast.makeText(this, R.string.viewer_download_failed, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        )
                    }
                }
            }

            return true
        }
    }
}
