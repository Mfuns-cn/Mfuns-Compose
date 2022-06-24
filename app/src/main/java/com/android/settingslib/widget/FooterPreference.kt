package com.android.settingslib.widget

import android.content.Context
import android.text.SpannableString
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import cn.mfuns.webapp.R

class FooterPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) :
    Preference(context, attrs, R.style.Preference_Material) {
    @VisibleForTesting
    var mLearnMoreListener: View.OnClickListener? = null
    private var mContentDescription: CharSequence? = null
    private var mLearnMoreContentDescription: CharSequence? = null

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        holder.itemView.findViewById<LinkTextView>(android.R.id.title).apply {
            movementMethod = LinkMovementMethod()
            isClickable = false
            isLongClickable = false
            if (!TextUtils.isEmpty(mContentDescription)) {
                contentDescription = mContentDescription
            }
        }

        holder.itemView.findViewById<LinkTextView>(R.id.settingslib_learn_more).apply {
            if (this != null && mLearnMoreListener != null) {
                visibility = View.VISIBLE
                val learnMoreText = SpannableString(text)
                learnMoreText.setSpan(
                    FooterLearnMoreSpan(mLearnMoreListener),
                    0,
                    learnMoreText.length,
                    0
                )
                text = learnMoreText
                if (!TextUtils.isEmpty(mLearnMoreContentDescription)) {
                    contentDescription = mLearnMoreContentDescription
                }
            } else {
                visibility = View.GONE
            }
        }
    }

    override fun setSummary(summary: CharSequence?) {
        title = summary
    }

    override fun setSummary(summaryResId: Int) = setTitle(summaryResId)

    override fun getSummary(): CharSequence? = title

    /**
     * Return the content description of footer preference.
     *
     * To set content description of the [FooterPreference]. This can use for talkback
     * environment if developer wants to have a customization content.
     *
     * @param contentDescription The resource id of the content description.
     */
    @get:VisibleForTesting
    var contentDescription: CharSequence?
        get() = mContentDescription
        set(contentDescription) {
            if (!TextUtils.equals(mContentDescription, contentDescription)) {
                mContentDescription = contentDescription
                notifyChanged()
            }
        }

    /**
     * Return the content description of learn more link.
     *
     * To set content description of the learn more text. This can use for talkback
     * environment if developer wants to have a customization content.
     *
     * @param learnMoreContentDescription The resource id of the content description.
     */
    @get:VisibleForTesting
    var learnMoreContentDescription: CharSequence?
        get() = mLearnMoreContentDescription
        set(learnMoreContentDescription) {
            if (!TextUtils.equals(mContentDescription, learnMoreContentDescription)) {
                mLearnMoreContentDescription = learnMoreContentDescription
                notifyChanged()
            }
        }

    /**
     * Assign an action for the learn more link.
     */
    fun setLearnMoreAction(listener: View.OnClickListener) {
        if (mLearnMoreListener !== listener) {
            mLearnMoreListener = listener
            notifyChanged()
        }
    }

    /**
     * The builder is convenient to creat a dynamic FooterPreference.
     */
    class Builder(private val mContext: Context) {
        private var mKey: String? = null
        private var mTitle: CharSequence? = null
        private var mContentDescription: CharSequence? = null
        private var mLearnMoreContentDescription: CharSequence? = null

        /**
         * To set the key value of the [FooterPreference].
         *
         * @param key The key value.
         */
        fun setKey(key: String): Builder {
            mKey = key
            return this
        }

        /**
         * To set the title of the [FooterPreference].
         *
         * @param title The title.
         */
        fun setTitle(title: CharSequence?): Builder {
            mTitle = title
            return this
        }

        /**
         * To set the title of the [FooterPreference].
         *
         * @param titleResId The resource id of the title.
         */
        fun setTitle(@StringRes titleResId: Int): Builder {
            mTitle = mContext.getText(titleResId)
            return this
        }

        /**
         * To set content description of the [FooterPreference]. This can use for talkback
         * environment if developer wants to have a customization content.
         *
         * @param contentDescription The resource id of the content description.
         */
        fun setContentDescription(contentDescription: CharSequence?): Builder {
            mContentDescription = contentDescription
            return this
        }

        /**
         * To set content description of the [FooterPreference]. This can use for talkback
         * environment if developer wants to have a customization content.
         *
         * @param contentDescriptionResId The resource id of the content description.
         */
        fun setContentDescription(@StringRes contentDescriptionResId: Int): Builder {
            mContentDescription = mContext.getText(contentDescriptionResId)
            return this
        }

        /**
         * To set content description of the learn more text. This can use for talkback
         * environment if developer wants to have a customization content.
         *
         * @param learnMoreContentDescription The resource id of the content description.
         */
        fun setLearnMoreContentDescription(learnMoreContentDescription: CharSequence?): Builder {
            mLearnMoreContentDescription = learnMoreContentDescription
            return this
        }

        /**
         * To set content description of the [FooterPreference]. This can use for talkback
         * environment if developer wants to have a customization content.
         *
         * @param learnMoreContentDescriptionResId The resource id of the content description.
         */
        fun setLearnMoreContentDescription(
            @StringRes learnMoreContentDescriptionResId: Int
        ): Builder {
            mLearnMoreContentDescription = mContext.getText(learnMoreContentDescriptionResId)
            return this
        }

        /**
         * To generate the [FooterPreference].
         */
        fun build(): FooterPreference {
            val footerPreference = FooterPreference(mContext)
            footerPreference.isSelectable = false
            require(!TextUtils.isEmpty(mTitle)) { "Footer title cannot be empty!" }
            footerPreference.title = mTitle
            if (!TextUtils.isEmpty(mKey)) footerPreference.key = mKey
            if (!TextUtils.isEmpty(mContentDescription)) footerPreference.contentDescription = mContentDescription
            if (!TextUtils.isEmpty(mLearnMoreContentDescription)) {
                footerPreference.learnMoreContentDescription = mLearnMoreContentDescription
            }
            return footerPreference
        }
    }

    /**
     * A [URLSpan] that opens a support page when clicked
     */
    internal class FooterLearnMoreSpan // sets the url to empty string so we can prevent any other span processing from
    // clearing things we need in this string.
    (private val mClickListener: View.OnClickListener?) : URLSpan("") {
        override fun onClick(widget: View) {
            mClickListener?.onClick(widget)
        }
    }

    companion object {
        const val KEY_FOOTER = "footer_preference"
        const val ORDER_FOOTER = Int.MAX_VALUE - 1
    }

    init {
        layoutResource = R.layout.preference_footer
        if (icon == null) setIcon(R.drawable.settingslib_ic_info_outline_24)
        order = ORDER_FOOTER
        if (TextUtils.isEmpty(key)) key = KEY_FOOTER
    }
}
