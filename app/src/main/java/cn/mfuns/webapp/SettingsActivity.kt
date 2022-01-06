package cn.mfuns.webapp

import android.content.DialogInterface
import android.os.Bundle
import android.os.Process
import android.webkit.CookieManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            // Version
            findPreference<Preference>("version")?.summaryProvider =
                Preference.SummaryProvider<Preference> {
                    context!!.packageManager.getPackageInfo(context!!.packageName, 0).versionName
                }

            // Clear cookies
            val preferenceClearCookies = findPreference<Preference>("clear_cookies")
            preferenceClearCookies?.summaryProvider =
                Preference.SummaryProvider<Preference> {
                    "${listOf(CookieManager.getInstance()).size} 个 Cookie"
                }
            preferenceClearCookies?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    val dialog = context?.let { it1 -> AlertDialog.Builder(it1) }
                    if (dialog == null) false
                    else {
                        dialog.setTitle(R.string.settings_clear_cookies)
                        dialog.setMessage(R.string.settings_clear_cookies_prompt)
                        dialog.setPositiveButton(R.string.positive,
                            DialogInterface.OnClickListener { _, _ ->
                                CookieManager.getInstance().removeAllCookies(null)
                                Process.killProcess(Process.myPid())
                            })
                        dialog.setNegativeButton(R.string.negative, null)
                        dialog.show()
                        true
                    }
                }
        }
    }
}
