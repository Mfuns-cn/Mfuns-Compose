package cn.mfuns.webapp.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
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
import cn.mfuns.webapp.R
import cn.mfuns.webapp.util.MfunsConfig
import com.ilharper.str4j.android.distrib.StrUpdate
import com.ilharper.str4j.android.distrib.strUpdate
import com.tencent.smtt.sdk.QbSdk
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
    }

    @AndroidEntryPoint
    class SettingsFragment : PreferenceFragmentCompat() {

        @Inject
        lateinit var mfunsConfig: MfunsConfig

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            // Version
            val preferenceUpdateChannel = findPreference<ListPreference>(getString(R.string.settings_update_channel_key))
            preferenceUpdateChannel!!.summaryProvider =
                Preference.SummaryProvider<ListPreference> {
                    // Get description text through corresponding index
                    resources.getStringArray(R.array.settings_update_channel_list)[
                        resources.getStringArray(R.array.settings_update_channel_values).indexOf(
                            // Get current value of settings_update_channel
                            PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(
                                getString(R.string.settings_update_channel_key),
                                getString(R.string.settings_update_channel_values_default)
                            )
                        )
                    ]
                }

            val preferenceUpdate = findPreference<Preference>("settings_update")
            preferenceUpdate!!.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    StrUpdate.default = (
                        StrUpdate.default ?: (
                            strUpdate {
                                useSimpleChecker(mfunsConfig.updateUrl)
                            }
                            )
                        ).check(requireActivity(), true)
                    true
                }

            val version =
                requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            val versionString = "${version.versionName} (${version.versionCode})"
            val preferenceVersion = findPreference<Preference>("settings_version")
            preferenceVersion!!.summaryProvider =
                Preference.SummaryProvider<Preference> { versionString }
            preferenceVersion.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    (requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                        ClipData.newPlainText(
                            requireContext().getText(R.string.settings_version),
                            versionString
                        )
                    )
                    Toast.makeText(requireContext(), R.string.settings_copied, Toast.LENGTH_SHORT)
                        .show()
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
                            android.webkit.CookieManager.getInstance().removeAllCookies(null)
                            android.webkit.WebStorage.getInstance().deleteAllData()
                            com.tencent.smtt.sdk.CookieManager.getInstance().removeAllCookies(null)
                            com.tencent.smtt.sdk.WebStorage.getInstance().deleteAllData()
                            QbSdk.clearAllWebViewCache(requireContext(), true)
                            Process.killProcess(Process.myPid())
                        }
                        setNegativeButton(R.string.negative, null)
                        show()
                    }
                    true
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
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(getString(R.string.settings_group_uri))
                            )
                        )
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            R.string.settings_group_join_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
