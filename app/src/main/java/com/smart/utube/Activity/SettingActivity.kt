package com.smart.utube.Activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.smart.utube.R
import com.smart.utube.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySettingBinding
    lateinit var toolbar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set up toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //set up related video Id
        binding.videoIdSetUpSettting.setOnClickListener {
            if (binding.relatedVideoIDSetting.visibility == View.GONE){
                binding.relatedVideoIDSetting.visibility = View.VISIBLE
            }else{
                binding.relatedVideoIDSetting.visibility = View.GONE
            }
        }

        binding.buttonSet.setOnClickListener{
            val sharedPreferences = getSharedPreferences("relatedVideoId", MODE_PRIVATE)
            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("videoId", binding.videoId.text.toString())
            editor.apply()
            binding.relatedVideoIDSetting.visibility = View.GONE
            Toast.makeText(this,"New Video Id Set Successfully",Toast.LENGTH_SHORT).show()
        }

        //disable Explore Section

        val sharedPreferences = getSharedPreferences("exploreSection", MODE_PRIVATE)

        var bool = sharedPreferences.getBoolean("exploreEnable",false)

        binding.exploreSwitchSetting.isChecked = bool

        binding.exploreSwitchSetting.setOnClickListener {

            if (binding.exploreSwitchSetting.isChecked){
                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.putBoolean("exploreEnable", true)
                editor.apply()
                Toast.makeText(this, "Enabled",Toast.LENGTH_SHORT).show()
            } else {
                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.putBoolean("exploreEnable", false)
                editor.apply()
                Toast.makeText(this, "Disable",Toast.LENGTH_SHORT).show()
            }

        }



    }
}