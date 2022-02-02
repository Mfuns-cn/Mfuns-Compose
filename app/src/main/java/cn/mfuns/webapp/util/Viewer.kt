package cn.mfuns.webapp.util

import android.app.Activity
import android.net.Uri
import androidx.core.content.FileProvider
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.io.File

class Viewer {
    companion object {
        private var queue: RequestQueue? = null

        fun download(
            activity: Activity,
            url: String,
            completed: (Triple<Uri, String, File>) -> Unit,
            failed: () -> Unit
        ) {
            activity.apply {
                Runnable {
                    // Check URL
                    if (url.isBlank()) failed()
                    val filename = url.split('/').last()

                    // Create temp file
                    val viewerProviderFolder = File(cacheDir, "viewerprovider")
                    viewerProviderFolder.mkdirs()
                    val file = File(viewerProviderFolder, filename)

                    // Make download request
                    if (queue == null) queue = Volley.newRequestQueue(applicationContext)
                    val request = DownloadRequest(url, { response ->
                        file.writeBytes(response)
                        val uri = FileProvider.getUriForFile(this, "cn.mfuns.webapp.viewerprovider", file)
                            ?: return@DownloadRequest failed()
                        completed(Triple(uri, filename, file))
                    }) { failed() }

                    // Enqueue request
                    queue!!.add(request)
                }.run()
            }
        }
    }
}

class ViewerProvider : FileProvider()
