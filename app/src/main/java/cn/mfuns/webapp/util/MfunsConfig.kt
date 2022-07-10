package cn.mfuns.webapp.util

import android.content.Context
import androidx.preference.PreferenceManager
import cn.mfuns.webapp.MfunsWebApp
import cn.mfuns.webapp.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MfunsConfig @Inject constructor(
    application: MfunsWebApp
) {
    //region Update Channel

    val updateChannel: String
    val updateUrl: String

    init {
        updateChannel = PreferenceManager.getDefaultSharedPreferences(application).getString(
            application.getString(R.string.settings_update_channel_key),
            application.getString(R.string.settings_update_channel_values_default)
        )!!
        updateUrl = application.getString(R.string.settings_update_url, updateChannel)
    }

    //endregion
}

@Module
@InstallIn(SingletonComponent::class)
class MfunsConfigModule {
    @Provides
    fun provideMfunsConfig(
        @ApplicationContext context: Context
    ) = MfunsConfig(context as MfunsWebApp)
}
