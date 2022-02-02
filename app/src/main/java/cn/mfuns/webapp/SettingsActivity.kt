package cn.mfuns.webapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.ilharper.droidup.DroidUp
import com.ilharper.droidup.droidUp
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

    private val preferenceWebViewCoreListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key != "settings_webview_core") return@OnSharedPreferenceChangeListener
        AlertDialog.Builder(this).apply {
            setTitle(R.string.settings_webview_core_change)
            setMessage(R.string.settings_webview_core_change_prompt)
            setPositiveButton(R.string.ok) { _, _ -> Process.killProcess(Process.myPid()) }
            setCancelable(false)
            show()
        }
    }

    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(preferenceWebViewCoreListener)
    }

    override fun onPause() {
        super.onPause()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(preferenceWebViewCoreListener)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            // Version
            val preferenceUpdate = findPreference<Preference>("settings_update")
            preferenceUpdate!!.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    DroidUp.default = (DroidUp.default ?: (droidUp {
                        useSimpleChecker("https://app.mfuns.cn/releases")
                    })).check(manual = true)
                    true
                }

            val version = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            val versionString = "${version.versionName} (${version.versionCode})"
            val preferenceVersion = findPreference<Preference>("settings_version")
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
            val preferenceWebViewCore = findPreference<ListPreference>("settings_webview_core")
            preferenceWebViewCore!!.summaryProvider =
                Preference.SummaryProvider<ListPreference> {
                    PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(
                        "settings_webview_core",
                        resources.getStringArray(R.array.settings_webview_core_list)[0]
                    )
                }

            // Clear cookies
            val preferenceClearCookies = findPreference<Preference>("settings_clear_cookies")
            preferenceClearCookies!!.summaryProvider =
                Preference.SummaryProvider<Preference> {
                    "${listOf(CookieManager.getInstance()).size} 个 Cookie，${listOf(WebStorage.getInstance()).size} 项 WebStorage"
                }
            preferenceClearCookies.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle(R.string.settings_clear_cookies)
                        setMessage(R.string.settings_clear_cookies_prompt)
                        setPositiveButton(R.string.positive) { _, _ ->
                            CookieManager.getInstance().removeAllCookies(null)
                            WebStorage.getInstance().deleteAllData()
                            Process.killProcess(Process.myPid())
                        }
                        setNegativeButton(R.string.negative, null)
                        show()
                    }
                    true
                }

            // Clear data
            val preferenceClearData = findPreference<Preference>("settings_clear_data")
            preferenceClearData!!.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle(R.string.settings_clear_data)
                        setMessage(R.string.settings_clear_data_prompt)
                        setPositiveButton(R.string.positive) { _, _ ->
                            QbSdk.clearAllWebViewCache(requireContext(), true)
                            Process.killProcess(Process.myPid())
                        }
                        setNegativeButton(R.string.negative, null)
                        show()
                    }
                    true
                }

            val preferenceTbsVersion = findPreference<Preference>("settings_webview_tbs_version")
            preferenceTbsVersion!!.summaryProvider =
                Preference.SummaryProvider<Preference> {
                    QbSdk.getTbsVersion(requireContext()).toString()
                }

            // Viewer
            val preferenceViewer = findPreference<ListPreference>("settings_viewer_default_action")
            preferenceViewer!!.summaryProvider =
                Preference.SummaryProvider<ListPreference> {
                    PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(
                        "settings_viewer_default_action",
                        resources.getStringArray(R.array.settings_viewer_action_list)[0]
                    )
                }

            // Join QQ group
            val preferenceGroup = findPreference<Preference>("settings_group")
            preferenceGroup!!.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.settings_group_uri))))
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), R.string.settings_group_join_failed, Toast.LENGTH_SHORT).show()
                    }
                    true
                }
        }
    }
}
