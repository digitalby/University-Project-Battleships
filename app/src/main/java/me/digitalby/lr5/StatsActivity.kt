package me.digitalby.lr5

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class StatsActivity : AppCompatActivity() {

    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val bundle: Bundle? = intent.extras
        uid = bundle?.getString("uid")!!
    }
}
