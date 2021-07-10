package ir.mab.radioamin.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import ir.mab.radioamin.R

class PermissionEducationalCompoundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var permissionEducationalActionListener: (Boolean) -> Unit? = {}
    init {
        inflate(context, R.layout.custom_permission_educational_view, this)

        findViewById<Button>(R.id.positiveButton).setOnClickListener{
            permissionEducationalActionListener(true)
        }

        findViewById<Button>(R.id.negativeButton).setOnClickListener{
            permissionEducationalActionListener(false)
        }
    }

    fun setOnPermissionActionListener(permissionEducationalActionListener: (Boolean) -> Unit?){
        this.permissionEducationalActionListener = permissionEducationalActionListener
    }

    fun setTitle(title: String){
        findViewById<AppCompatTextView>(R.id.title).text = title
    }

    fun setDescription(description: String){
        findViewById<AppCompatTextView>(R.id.description).text = description
    }
}