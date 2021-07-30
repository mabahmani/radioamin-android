package ir.mab.radioamin.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.ActivitySplashBinding
import ir.mab.radioamin.ui.BaseActivity
import ir.mab.radioamin.ui.deviceonly.DeviceFilesActivity

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.handlers = MyHandlers()

    }

    inner class MyHandlers {
        fun onClickDeviceFilesOnly(view: View) {
            val intent = Intent(this@SplashActivity, DeviceFilesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}