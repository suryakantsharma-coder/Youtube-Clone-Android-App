package com.smart.utube.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.smart.utube.Fragments.ExploreFragment
import com.smart.utube.Fragments.RecentFragment
import com.smart.utube.Fragments.homeFragment
import com.smart.utube.Fragments.playlistFragment
import com.smart.utube.R
import com.smart.utube.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    lateinit var fragmentList : ArrayList<Fragment>
    lateinit var title : ArrayList<String>
    lateinit var toolbar : Toolbar
    var doubleBackToExitPressedOnce : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar)

        defaultFragment()

        binding.bottomNavigationBar.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.homeFragment -> setFragment(homeFragment())
                R.id.exploreFragment -> setFragment(ExploreFragment())
                R.id.playlistFragment -> setFragment(playlistFragment())
                R.id.recentFragment -> setFragment(RecentFragment())
            }
            true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_icon, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.search_Click -> startActivity(Intent(applicationContext, SearchActivity::class.java))
            R.id.setting_Click -> startActivity(Intent(applicationContext, SettingActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

     fun setFragment(fm: Fragment){
        supportFragmentManager.beginTransaction().apply {
            this.replace(R.id.fragment, fm)
            this.commit()
        }
    }

     fun defaultFragment(){
        setFragment(homeFragment())
    }

    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(applicationContext, "Please press BACK again to exit", Toast.LENGTH_LONG).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}

