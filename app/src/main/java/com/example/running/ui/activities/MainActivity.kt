package com.example.running.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.running.R
import com.example.running.extras.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setupWithNavController(navHostFragment.findNavController())

        navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
                    when (destination.id) {
                        R.id.settingFragment, R.id.runFragment, R.id.statisticsFragment, R.id.scheduleFragment ->
                            View.VISIBLE
                        else -> View.GONE
                    }
            }

        navigateToTrackingFragmentIfNeeded(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            navHostFragment.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }
}
