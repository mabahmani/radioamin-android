package ir.mab.radioamin.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import ir.mab.radioamin.R

class ProgressCompoundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.custom_progress_view, this)
    }
}