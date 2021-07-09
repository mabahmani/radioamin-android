package ir.mab.radioamin.ui.binding

import android.graphics.Bitmap
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import ir.mab.radioamin.ui.custom.ProgressCompoundView
import ir.mab.radioamin.ui.custom.SimpleToolbarWithBackIconAndTitleCompoundView

object BindingAdapters {
    @BindingAdapter("imageBitmap")
    @JvmStatic
    fun loadImage(view: AppCompatImageView, imageBitmap: Bitmap?) {
        if (imageBitmap != null) {
            Glide.with(view).load(imageBitmap).into(view)
        }
    }

    @BindingAdapter("stTitle")
    @JvmStatic
    fun setTitle(view: SimpleToolbarWithBackIconAndTitleCompoundView, title: String){
        view.setTitle(title)
    }

    @BindingAdapter("pcVisibility")
    @JvmStatic
    fun setVisibility(view: ProgressCompoundView, visibility: Int){
        view.setVisibilityWithAnim(visibility)
    }
}