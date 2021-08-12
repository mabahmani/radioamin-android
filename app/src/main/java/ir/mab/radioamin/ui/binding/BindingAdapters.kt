package ir.mab.radioamin.ui.binding

import android.graphics.Bitmap
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import ir.mab.radioamin.R
import ir.mab.radioamin.ui.custom.ProgressCompoundView
import ir.mab.radioamin.ui.custom.SimpleToolbarWithBackIconAndTitleCompoundView
import ir.mab.radioamin.vo.DeviceFileType

@BindingAdapter("imageBitmap")
fun loadImage(view: AppCompatImageView, imageBitmap: Bitmap?) {
    if (imageBitmap != null) {
        Glide.with(view).load(imageBitmap).transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(view)
    }
}

@BindingAdapter("stTitle")
fun setSimpleToolbarTitle(view: SimpleToolbarWithBackIconAndTitleCompoundView, title: String) {
    view.setTitle(title)
}

@BindingAdapter("pcVisibility")
fun setProgressCompoundVisibility(view: ProgressCompoundView, visibility: Int) {
    view.setVisibilityWithAnim(visibility)
}

@BindingAdapter("deviceFilesBtsOptionPlaceHolder")
fun setDeviceFilesBottomSheetOptionPlaceHolder(view: AppCompatImageView, type: DeviceFileType) {
    when(type){
        DeviceFileType.PLAYLIST -> {
            view.setImageResource(R.drawable.ic_list_music)
        }
        DeviceFileType.ALBUM -> {
            view.setImageResource(R.drawable.ic_album_collection)
        }
        DeviceFileType.SONG -> {
            view.setImageResource(R.drawable.ic_music_note)
        }
        DeviceFileType.ARTIST -> {
            view.setImageResource(R.drawable.ic_user_music)
        }
    }

}
