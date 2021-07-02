package ir.mab.radioamin.ui.splash

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.ActivitySplashBinding
import ir.mab.radioamin.ui.BaseActivity
import ir.mab.radioamin.ui.deviceonly.DeviceFilesOnlyActivity

class SplashActivity : BaseActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.handlers = MyHandlers()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }


        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE
        )


// Display videos in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val query = applicationContext.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )

        query.use {
            if (it != null){
                Log.d("mQuery", it.count.toString())
            }
            else{
                Log.d("mQuery", "-1")
            }

            while (it != null && it.moveToNext()) {
                // Get values of columns for a given video.
                //val id = cursor.getLong(idColumn)
                val name = it.getString(it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                Log.d("mQuery", name)
//                val duration = cursor.getInt(durationColumn)
//                val size = cursor.getInt(sizeColumn)
//
//                val contentUri: Uri = ContentUris.withAppendedId(
//                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                    id
//                )
//
//                // Stores column values and the contentUri in a local object
//                // that represents the media file.
//                videoList += Video(contentUri, name, duration, size)
            }
        }
    }

    inner class MyHandlers {
        fun onClickDeviceFilesOnly(view: View) {
            val intent = Intent(this@SplashActivity, DeviceFilesOnlyActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}