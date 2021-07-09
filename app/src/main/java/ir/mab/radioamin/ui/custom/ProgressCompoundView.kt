package ir.mab.radioamin.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.RelativeLayout
import ir.mab.radioamin.R

class ProgressCompoundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.custom_progress_view, this)
    }

    fun setVisibilityWithAnim(visibility: Int) {
        val view = findViewById<RelativeLayout>(R.id.parent)
        when (visibility) {
            VISIBLE -> {
                view.animate().alpha(1f).setDuration(300)
                    .withStartAction { view.visibility = VISIBLE }.start()
            }
            GONE -> {
                view.animate().alpha(0f).setDuration(300)
                    .withEndAction { view.visibility = GONE }.start()
            }
            INVISIBLE -> {
                view.animate().alpha(0f).setDuration(300)
                    .withEndAction { view.visibility = INVISIBLE }.start()
            }
        }
    }
}