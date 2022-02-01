package cn.mfuns.webapp.util

import android.annotation.SuppressLint
import android.app.Activity

@SuppressLint("PrivateApi")
fun Activity.getCurrentActivity(): Activity {
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
