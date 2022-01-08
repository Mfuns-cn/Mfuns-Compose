package cn.mfuns.webapp

import android.content.*
import android.os.Bundle
import android.os.Process
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebStorage

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
            val version = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            val versionString = "${version.versionName} (${version.versionCode})"
            val preferenceVersion = findPreference<Preference>("version")
            preferenceVersion!!.summaryProvider =
                Preference.SummaryProvider<Preference> { versionString }
            preferenceVersion.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    (requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                        ClipData.newPlainText(
                            requireContext().getText(R.string.settings_version),
                            versionString
                        )
                    )
                    Toast.makeText(requireContext(), R.string.settings_copied, Toast.LENGTH_SHORT).show()
                    true
                }

            // WebView core
            val preferenceWebViewCore = findPreference<ListPreference>("webview_core")
            val preferenceWebViewCoreListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle(R.string.settings_webview_core_change)
                dialog.setMessage(R.string.settings_webview_core_change_prompt)
                dialog.setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { _, _ ->
                        Process.killProcess(Process.myPid())
                    })
                dialog.show()
            }

            // Clear cookies
            val preferenceClearCookies = findPreference<Preference>("clear_cookies")
            preferenceClearCookies!!.summaryProvider =
                Preference.SummaryProvider<Preference> {
                    "${listOf(CookieManager.getInstance()).size} 个 Cookie，${listOf(WebStorage.getInstance()).size} 项 WebStorage"
                }
            preferenceClearCookies.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    val dialog = AlertDialog.Builder(requireContext())
                    dialog.setTitle(R.string.settings_clear_cookies)
                    dialog.setMessage(R.string.settings_clear_cookies_prompt)
                    dialog.setPositiveButton(R.string.positive,
                        DialogInterface.OnClickListener { _, _ ->
                            CookieManager.getInstance().removeAllCookies(null)
                            WebStorage.getInstance().deleteAllData()
                            Process.killProcess(Process.myPid())
                        })
                    dialog.setNegativeButton(R.string.negative, null)
                    dialog.show()
                    true
                }

            // Clear data
            val preferenceClearData = findPreference<Preference>("clear_data")
            preferenceClearData!!.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    val dialog = AlertDialog.Builder(requireContext())
                    dialog.setTitle(R.string.settings_clear_data)
                    dialog.setMessage(R.string.settings_clear_data_prompt)
                    dialog.setPositiveButton(R.string.positive,
                        DialogInterface.OnClickListener { _, _ ->
                            QbSdk.clearAllWebViewCache(requireContext(), true)
                            Process.killProcess(Process.myPid())
                        })
                    dialog.setNegativeButton(R.string.negative, null)
                    dialog.show()
                    true
                }

            val preferenceTbsVersion = findPreference<Preference>("tbs_version")
            preferenceTbsVersion!!.summaryProvider =
                Preference.SummaryProvider<Preference> {
                    QbSdk.getTbsVersion(requireContext()).toString()
                }
        }
    }
}
