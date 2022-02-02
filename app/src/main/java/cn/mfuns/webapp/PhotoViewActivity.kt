package cn.mfuns.webapp

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.mfuns.webapp.databinding.ActivityPhotoViewBinding
import cn.mfuns.webapp.util.Viewer
import java.io.File

class PhotoViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoViewBinding
    private lateinit var file: Triple<Uri, String, File>

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
                    Viewer.open(this, file.first)
                    true
                }
                R.id.viewer_action_photo_download -> {
                    Viewer.save(this, file.third, file.second)
                    true
                }
                else -> false
            }
        }

        // Download Photo
        val url = intent.getStringExtra("url")
        if (url == null) {
            Toast.makeText(this, R.string.viewer_download_failed, Toast.LENGTH_SHORT).show()
            finish()
        }
        Viewer.download(
            this,
            url!!,
            { f ->
                file = f
                binding.photoView.setImageURI(file.first)
                binding.loadingBar.isIndeterminate = false
            },
            {
                Toast.makeText(this, R.string.viewer_download_failed, Toast.LENGTH_SHORT).show()
                finish()
            }
        )
    }
}
