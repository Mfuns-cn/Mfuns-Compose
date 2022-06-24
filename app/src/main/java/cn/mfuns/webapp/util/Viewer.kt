package cn.mfuns.webapp.util

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import cn.mfuns.webapp.R
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.io.File

class Viewer {
    companion object {
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
                        val uri =
                            FileProvider.getUriForFile(this, "cn.mfuns.webapp.viewerprovider", file)
                                ?: return@DownloadRequest failed()
                        completed(Triple(uri, filename, file))
                    }) { failed() }

                    // Enqueue request
                    queue!!.add(request)
                }.run()
            }
        }

        fun open(activity: Activity, uri: Uri) {
            if (!openIntl(activity, uri)) {
                Toast.makeText(activity, R.string.viewer_download_failed, Toast.LENGTH_SHORT).show()
            }
        }

        fun save(
            activity: Activity,
            file: File,
            filename: String
        ) {
            saveIntl(activity, file, filename).apply {
                if (this == null) {
                    Toast.makeText(activity, R.string.viewer_download_failed, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.viewer_download_completed, this),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        private var queue: RequestQueue? = null

        private val imageExtList = hashMapOf(
            ".bmp" to "image/x-ms-bmp",
            ".jpg" to "image/jpeg",
            ".jpeg" to "image/jpeg",
            ".gif" to "image/gif",
            ".png" to "image/png",
            ".webp" to "image/webp"
        )

        fun parseExt(path: String): String? =
            imageExtList.keys.singleOrNull { x -> path.endsWith(x) }

        private fun openIntl(activity: Activity, uri: Uri): Boolean {
            Intent(Intent.ACTION_VIEW).apply {
                data = uri
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (resolveActivity(activity.packageManager) == null) return false
                activity.startActivity(this)
            }
            return true
        }

        private fun saveIntl(
            activity: Activity,
            file: File,
            filename: String
        ): String? {
            val ext = parseExt(filename) ?: return null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStorage
                val now = System.currentTimeMillis()
                activity.contentResolver.apply {
                    val uri = insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        ContentValues().apply {
                            put(MediaStore.Images.Media.TITLE, filename)
                            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                            put(MediaStore.Images.Media.MIME_TYPE, imageExtList[ext])
                            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Mfuns")
                            put(MediaStore.Images.Media.DATE_TAKEN, now)
                            put(MediaStore.Images.Media.DATE_ADDED, now)
                            put(MediaStore.Images.Media.DATE_MODIFIED, now)
                            put(MediaStore.Images.Media.SIZE, file.length())
                        }
                    ) ?: return null
                    val os = openOutputStream(uri) ?: return null
                    file.inputStream().copyTo(os)
                    return uri.path
                }
            } else {
                // Use ExternalStorages
                val path = (
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path +
                        "/Mfuns/" +
                        filename
                    )
                file.copyTo(
                    File(path).apply { if (!exists()) createNewFile() },
                    true
                )
                return path
            }
        }
    }
}

class ViewerProvider : FileProvider()
