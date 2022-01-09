package cn.mfuns.webapp.webview

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import cn.mfuns.webapp.R
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.io.File
import java.net.URL

class MfunsWebViewClient {
    companion object {
        private var queue: RequestQueue? = null

        fun shouldOverrideUrlLoading(context: Context, url: String): Boolean {
            // Parse URL
            var path = URL(url).path
            path = url.split(path)[0] + path
            if (path.isNullOrEmpty()) return false
            val filename = path.split('/').last()

            // Notify download started
            Toast.makeText(context, R.string.viewer_downloading, Toast.LENGTH_SHORT).show()

            // Create temp file
            val viewerProviderFolder = File(context.cacheDir, "viewerprovider")
            viewerProviderFolder.mkdirs()
            val file = File(viewerProviderFolder, filename)

            // Make download request
            if (queue == null) queue = Volley.newRequestQueue(context)
            val request = DownloadRequest(path, { response ->
                run {
                    file.writeBytes(response)
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = FileProvider.getUriForFile(context, "com.mfuns.webapp.viewerprovider", file)
                        // type = mime
                    }
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    }
                }
            }, { Toast.makeText(context, R.string.viewer_download_failed, Toast.LENGTH_SHORT).show() })

            // Enqueue request
            queue!!.add(request)

            return true
        }
    }
}
