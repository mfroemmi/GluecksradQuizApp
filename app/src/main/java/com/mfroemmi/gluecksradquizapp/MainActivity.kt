package com.mfroemmi.gluecksradquizapp

import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.mfroemmi.gluecksradquizapp.model.SettingsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent

@KoinApiExtension
class MainActivity : AppCompatActivity(), KoinComponent {

    private val sharedViewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Set up the action bar for use with the NavController
        setupActionBarWithNavController(navController)

        sharedViewModel.setScreenHeight(getDisplaySize())
        sharedViewModel.setMode("question")
        sharedViewModel.setTryLeftover(1)
        sharedViewModel.setIntensity(1)

        // Ausblenden der Navigation- und Statusbar
        hideStatusNavigationBar()
    }

    private fun hideStatusNavigationBar() {
        if (Build.VERSION.SDK_INT <= 30) {
            supportActionBar?.hide()
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    View.KEEP_SCREEN_ON

        } else {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    private fun getDisplaySize(): Int {
        val display =
            windowManager.defaultDisplay
        // TODO: Die Methode .defaultDisplay wird in der aktuellen API nicht mehr unterstützt.
        val size = Point()
        display.getSize(size)
        return size.y
    }
}