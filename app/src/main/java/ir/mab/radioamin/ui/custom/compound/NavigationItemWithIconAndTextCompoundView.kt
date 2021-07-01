package ir.mab.radioamin.ui.custom.compound

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import ir.mab.radioamin.R

class NavigationItemWithIconAndTextCompoundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.custom_navigation_item_with_icon_and_text, this)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.NavigationItemWithIconAndTextCompoundView,
            0, 0).apply {

            try {
                findViewById<AppCompatTextView>(R.id.title).text = getString(R.styleable.NavigationItemWithIconAndTextCompoundView_nitTitle)
                findViewById<AppCompatTextView>(R.id.title).setTextColor(getColor(R.styleable.NavigationItemWithIconAndTextCompoundView_nitTitleColor, 0))
                findViewById<AppCompatImageView>(R.id.icon).setImageResource(getResourceId(R.styleable.NavigationItemWithIconAndTextCompoundView_nitIcon, 0))
                findViewById<AppCompatImageView>(R.id.icon).setColorFilter(getColor(R.styleable.NavigationItemWithIconAndTextCompoundView_nitIconTint, 0))
                findViewById<AppCompatImageView>(R.id.chevronRight).setColorFilter(getColor(R.styleable.NavigationItemWithIconAndTextCompoundView_nitChevronTint, 0))
            } finally {
                recycle()
            }
        }
    }
}