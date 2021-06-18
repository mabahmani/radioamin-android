package ir.mab.radioamin.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.mab.radioamin.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}