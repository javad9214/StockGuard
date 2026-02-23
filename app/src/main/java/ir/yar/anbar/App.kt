package ir.yar.anbar

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import org.acra.ACRA
import org.acra.config.CoreConfigurationBuilder
import org.acra.config.HttpSenderConfigurationBuilder
import org.acra.data.StringFormat
import org.acra.sender.HttpSender

@HiltAndroidApp
class App: Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        // Initialize ACRA with the proper context
       // initAcra()
    }

    override fun onCreate() {
        super.onCreate()
        // Other initialization code can go here
    }

    private fun initAcra() {
        // Use this URL if testing on device connected via USB with adb reverse
        val acrariumUrl = "http://127.0.0.1:8080/report"

        // If testing over Wi-Fi on the same network, use your PC's local IP instead, for example:
        // val acrariumUrl = "http://192.168.1.10:8000/acra-report"

        val builder = CoreConfigurationBuilder(this)
            .withReportFormat(StringFormat.JSON)

        builder.getPluginConfigurationBuilder(HttpSenderConfigurationBuilder::class.java)
            .withUri(acrariumUrl)
            .withHttpMethod(HttpSender.Method.POST)
            .withBasicAuthLogin("cthbUePpDYm2ZDe4")
            .withBasicAuthPassword("PpWXihu9BuCY4xwK")
            .withEnabled(true)

        ACRA.init(this, builder)
    }
}
