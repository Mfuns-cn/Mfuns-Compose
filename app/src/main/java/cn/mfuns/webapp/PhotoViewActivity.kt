package cn.mfuns.webapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import cn.mfuns.webapp.databinding.ActivityPhotoViewBinding
import cn.mfuns.webapp.util.DownloadRequest
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.io.File

class PhotoViewActivity : AppCompatActivity() {
    companion object {
        private var queue: RequestQueue? = null
    }

    private lateinit var binding: ActivityPhotoViewBinding
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fullscreen
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.statusBarColor = 0x60000000
        window.navigationBarColor = 0 // Transparent

        // Bind view
        binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.viewer_action_photo_open -> {
                    PhotoViewActions.open(this, uri)
                    true
                }
                R.id.viewer_action_photo_download -> {
                    PhotoViewActions.download(this, uri)
                    true
                }
                else -> false
            }
        }

        // Download Photo
        Runnable {
            // Check URL
            val url = intent.getStringExtra("url")
            if (url.isNullOrBlank()) {
                Toast.makeText(this, R.string.viewer_download_failed, Toast.LENGTH_SHORT).show()
                finish()
            }
            val filename = url!!.split('/').last()

            // Create temp file
            val viewerProviderFolder = File(cacheDir, "viewerprovider")
            viewerProviderFolder.mkdirs()
            val file = File(viewerProviderFolder, filename)

            // Make download request
            if (queue == null) queue = Volley.newRequestQueue(this)
            val request = DownloadRequest(url, { response ->
                run {
                    file.writeBytes(response)
                    uri = FileProvider.getUriForFile(this, "cn.mfuns.webapp.viewerprovider", file)
                    binding.photoView.setImageURI(uri)
                    binding.loadingBar.isIndeterminate = false
                }
            }, {
                Toast.makeText(this, R.string.viewer_download_failed, Toast.LENGTH_SHORT).show()
                finish()
            })

            // Enqueue request
            queue!!.add(request)
        }.run()
    }
}

class PhotoViewActions {
    companion object {
        fun open(activity: Activity, uri: Uri) {
            Intent(Intent.ACTION_VIEW).apply {
                data = uri
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (resolveActivity(activity.packageManager) != null) activity.startActivity(this)
            }
        }

        fun download(activity: Activity, uri: Uri) {
            TODO()
        }
    }
}
