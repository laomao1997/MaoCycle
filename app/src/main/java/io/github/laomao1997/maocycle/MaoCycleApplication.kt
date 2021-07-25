package io.github.laomao1997.maocycle

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.mapbox.mapboxsdk.Mapbox

class MaoCycleApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        Mapbox.getInstance(this, getString(R.string.mapbox_token))
    }
}