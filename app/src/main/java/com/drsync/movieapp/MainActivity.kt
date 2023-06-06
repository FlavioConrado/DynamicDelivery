package com.drsync.movieapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.drsync.movieapp.databinding.ActivityMainBinding
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var manager: SplitInstallManager
    private val listener2 =
        SplitInstallStateUpdatedListener { state ->
            val multiInstall = state.moduleNames().size > 1
            val names = state.moduleNames().joinToString(" - ")
            Log.i(this::class.java.simpleName, "names=$names")
            Log.i(this::class.java.simpleName, "multiInstall=$multiInstall")
            when (state.status()) {
                SplitInstallSessionStatus.DOWNLOADING -> {
                    Log.i(this::class.java.simpleName, "DOWNLOADING")
                    Toast.makeText(this, "Downloading a module", Toast.LENGTH_LONG)
                }
                SplitInstallSessionStatus.DOWNLOADED -> {
                    Log.i(this::class.java.simpleName, "DOWNLOADED")
                }
                SplitInstallSessionStatus.INSTALLING -> {
                    Log.i(this::class.java.simpleName, "INSTALLING")
                }
                SplitInstallSessionStatus.PENDING -> {
                    Log.i(this::class.java.simpleName, "PENDING")
                }
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    Log.i(this::class.java.simpleName, "REQUIRES_USER_CONFIRMATION")
                }
                SplitInstallSessionStatus.INSTALLED -> {
                    Log.i(this::class.java.simpleName, "INSTALLED")
                }
                SplitInstallSessionStatus.UNKNOWN -> {
                    Log.i(this::class.java.simpleName, "UNKNOWN")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manager = SplitInstallManagerFactory.create(this)

        manager.installedModules.forEach { installedModule ->
            Log.i(this::class.java.simpleName, installedModule)
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.nav_movie, R.id.nav_favorite
        ).build()

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.nav_add_module -> {

                val request = SplitInstallRequest.newBuilder()
                    .addModule("favorite")
                    .build()

                Log.i(this::class.java.simpleName, "requesting favorite module download")
                manager.startInstall(request)

                true
            }
            R.id.nav_remove_module -> {
                val modules = manager.installedModules.toList()
                Log.i(this::class.java.simpleName, "requesting favorite module uninstall. modules to be uninstalled: $modules")
                manager.deferredUninstall(manager.installedModules.toList())

                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    }

    override fun onResume() {

        manager.registerListener(listener2)
        manager.installedModules.forEach { installedModule ->
            Log.i(this::class.java.simpleName, "installed module: $installedModule")
        }

        super.onResume()
    }

    override fun onPause() {
        manager.unregisterListener(listener2)

        super.onPause()
    }
}