package cn.mfuns.webapp.util

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager

class AndroidUtil {
    companion object {
        fun getCurrentActivity(): Activity {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null)
            val activitiesField = activityThreadClass.getDeclaredField("mActivities").apply {
                isAccessible = true
            }
            for (activityRecord in (activitiesField.get(activityThread) as Map<*, *>).values) {
                val activityRecordClass = activityRecord!!.javaClass
                val pausedField = activityRecordClass.getDeclaredField("paused").apply {
                    isAccessible = true
                }
                if (!pausedField.getBoolean(activityRecord)) {
                    val activityField = activityRecordClass.getDeclaredField("activity").apply {
                        isAccessible = true
                    }
                    return activityField.get(activityRecord) as Activity
                }
            }
            throw RuntimeException("Cannot get activity")
        }

        fun Window.setFullscreen(enable: Boolean) {
            if (enable) {
                decorView.systemUiVisibility =
                    (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                        attributes.layoutInDisplayCutoutMode =
                            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                    else attributes.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            } else {
                decorView.systemUiVisibility = 0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    attributes.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
            }
        }
    }
}
