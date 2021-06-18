package ir.mab.radioamin

import android.app.Application
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        ViewPump.init(ViewPump.builder()
            .addInterceptor(
                CalligraphyInterceptor(
                    CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()))
            .build())
    }
}