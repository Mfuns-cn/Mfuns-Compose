package com.android.settingslib.widget

import android.content.Context
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class LinkTextView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null
) : AppCompatTextView(
    context!!, attrs
) {
    override fun setText(text: CharSequence, type: BufferType) {
        super.setText(text, type)
        if (text is Spanned) {
            val spans = text.getSpans(0, text.length, ClickableSpan::class.java)
            if (spans.isNotEmpty()) movementMethod = LinkMovementMethod.getInstance()
        }
    }
}
