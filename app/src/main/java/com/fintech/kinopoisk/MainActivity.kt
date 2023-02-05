package com.fintech.kinopoisk

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var controller: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.activity_main)
        val containerId =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                R.id.container
            else
                R.id.home_container
        controller =
            (supportFragmentManager.findFragmentById(containerId) as NavHostFragment)
                .navController
        findViewById<BottomNavigationView>(R.id.bnv_main)
            .setupWithNavController(controller)
    }
}