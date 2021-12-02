package com.example.complamap.views.adapters

import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.underline
import androidx.databinding.BindingAdapter

class Adapters {
    companion object {
        @BindingAdapter(value = ["dynamicText", "specialText"], requireAll = true)
        @JvmStatic
        fun bindText(textView: TextView, dynamicText: String, specialText: String) {
            textView.text = makePartBoldStr(specialText, dynamicText)
        }

        private fun makePartBoldStr(beginning: String, str: String): SpannableStringBuilder {
            return SpannableStringBuilder()
                .bold { underline { append(beginning) } }
                .bold { append(":") }
                .append(" $str")
        }
    }
}