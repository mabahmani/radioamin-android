package ir.mab.radioamin.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import ir.mab.radioamin.R

class EmptyListCompoundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.custom_empty_view, this)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.EmptyListCompoundView,
            0, 0).apply {

            try {
                findViewById<AppCompatTextView>(R.id.text).text = getString(R.styleable.EmptyListCompoundView_elText)
                findViewById<AppCompatTextView>(R.id.text).setTextColor(getColor(R.styleable.EmptyListCompoundView_elTextColor, resources.getColor(R.color.color10)))
                findViewById<AppCompatImageView>(R.id.icon).setImageResource(getResourceId(R.styleable.EmptyListCompoundView_elIcon, 0))
                findViewById<AppCompatImageView>(R.id.icon).setColorFilter(getColor(R.styleable.EmptyListCompoundView_elIconTint, resources.getColor(R.color.color10)))
            } finally {
                recycle()
            }
        }
    }
}