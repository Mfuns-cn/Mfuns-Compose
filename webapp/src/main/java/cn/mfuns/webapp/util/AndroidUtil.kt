package cn.mfuns.webapp.util

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager

class AndroidUtil {
    companion object {
        fun Window.setFullscreen(enable: Boolean, video: Boolean = false) {
            if (enable) {
                decorView.systemUiVisibility =
                    (
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or (
                                if (video) View.KEEP_SCREEN_ON
                                else 0
                                )
                        )
                if (!video) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            attributes.layoutInDisplayCutoutMode =
                                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                        } else attributes.layoutInDisplayCutoutMode =
                            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                    }
                }
            } else {
                decorView.systemUiVisibility = 0
            }
        }
    }
}
