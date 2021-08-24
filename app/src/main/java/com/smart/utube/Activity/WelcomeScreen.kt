package com.smart.utube.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.smart.utube.R

class WelcomeScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)

        val handler = Handler()

        var uri = intent.data
        if (uri!=null){
            handler.postDelayed(Runnable {
                val intent = Intent(applicationContext,DownloadActivity::class.java)
                intent.putExtra("VideoId",uri.toString().subSequence(16,uri.toString().lastIndex))
                startActivity(intent)
            },2000)
        } else{
            handler.postDelayed(Runnable {
                startActivity(Intent(applicationContext,HomeActivity::class.java))
                finish() },2000)
        }
    }
}