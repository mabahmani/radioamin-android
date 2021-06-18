package ir.mab.radioamin.ui

import android.os.Bundle
import ir.mab.radioamin.R

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}