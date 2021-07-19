package ir.mab.radioamin.ui.binding

import android.graphics.Bitmap
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import ir.mab.radioamin.ui.custom.ProgressCompoundView
import ir.mab.radioamin.ui.custom.SimpleToolbarWithBackIconAndTitleCompoundView

@BindingAdapter("imageBitmap")
fun loadImage(view: AppCompatImageView, imageBitmap: Bitmap?) {
    if (imageBitmap != null) {
        Glide.with(view).load(imageBitmap).transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

@BindingAdapter("stTitle")
fun setTitle(view: SimpleToolbarWithBackIconAndTitleCompoundView, title: String) {
    view.setTitle(title)
}

@BindingAdapter("pcVisibility")
fun setVisibility(view: ProgressCompoundView, visibility: Int) {
    view.setVisibilityWithAnim(visibility)
}