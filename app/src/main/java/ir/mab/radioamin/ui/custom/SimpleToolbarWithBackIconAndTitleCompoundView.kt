package ir.mab.radioamin.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.findNavController
import ir.mab.radioamin.R

class SimpleToolbarWithBackIconAndTitleCompoundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.custom_simple_toolbar_with_back_icon_and_title, this)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SimpleToolbarWithBackIconAndTitleCompoundView,
            0, 0
        ).apply {

            try {
                findViewById<AppCompatTextView>(R.id.title).text =
                    getString(R.styleable.SimpleToolbarWithBackIconAndTitleCompoundView_stTitle)

                findViewById<AppCompatTextView>(R.id.title).isSelected = true

                findViewById<AppCompatTextView>(R.id.title).setTextColor(
                    getColor(
                        R.styleable.SimpleToolbarWithBackIconAndTitleCompoundView_stTitleColor,
                        resources.getColor(R.color.white)
                    )
                )
                findViewById<AppCompatImageView>(R.id.icon).setColorFilter(
                    getColor(
                        R.styleable.SimpleToolbarWithBackIconAndTitleCompoundView_stBackIconTint,
                        resources.getColor(R.color.white)
                    )
                )
                findViewById<AppCompatImageView>(R.id.icon).setOnClickListener {
                    it.findNavController().popBackStack()
                }
            } finally {
                recycle()
            }
        }
    }

    fun setTitle(title: String){
        findViewById<AppCompatTextView>(R.id.title).text = title
    }

    fun setTitleVisibility(visibility: Int) {
        findViewById<AppCompatTextView>(R.id.title).visibility = visibility
    }

    fun setTitleVisibilityWithAnim(visibility: Int) {
        val view = findViewById<AppCompatTextView>(R.id.title)
        when (visibility) {
            VISIBLE -> {
                view.animate().alpha(1f).setDuration(500)
                    .withStartAction { view.visibility = VISIBLE }.start()
            }
            GONE -> {
                view.animate().alpha(0f).setDuration(500)
                    .withEndAction { view.visibility = GONE }.start()
            }
            INVISIBLE -> {
                view.animate().alpha(0f).setDuration(500)
                    .withEndAction { view.visibility = INVISIBLE }.start()
            }
        }
    }
}