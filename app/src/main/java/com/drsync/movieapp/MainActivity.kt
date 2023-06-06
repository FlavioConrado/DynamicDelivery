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
import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallManager
import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallManagerFactory
import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallRequest
import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallSessionStatus
import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallUpdatedListener

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var globalSplitInstallManager: GlobalSplitInstallManager
    private val listener2 =
        GlobalSplitInstallUpdatedListener { state ->
            val multiInstall = state.moduleNames().size > 1
            val names = state.moduleNames().joinToString(" - ")
            Log.i(this::class.java.simpleName, "names=$names")
            Log.i(this::class.java.simpleName, "multiInstall=$multiInstall")
            when (state.status()) {
                GlobalSplitInstallSessionStatus.DOWNLOADING -> {
                    Log.i(this::class.java.simpleName, "DOWNLOADING")
                    Toast.makeText(this, "Downloading a module", Toast.LENGTH_LONG)
                }
                GlobalSplitInstallSessionStatus.DOWNLOADED -> {
                    Log.i(this::class.java.simpleName, "DOWNLOADED")
                }
                GlobalSplitInstallSessionStatus.INSTALLING -> {
                    Log.i(this::class.java.simpleName, "INSTALLING")
                }
                GlobalSplitInstallSessionStatus.PENDING -> {
                    Log.i(this::class.java.simpleName, "PENDING")
                }
                GlobalSplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    Log.i(this::class.java.simpleName, "REQUIRES_USER_CONFIRMATION")
                }
                GlobalSplitInstallSessionStatus.INSTALLED -> {
                    Log.i(this::class.java.simpleName, "INSTALLED")
                }
                GlobalSplitInstallSessionStatus.UNKNOWN -> {
                    Log.i(this::class.java.simpleName, "UNKNOWN")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        globalSplitInstallManager = GlobalSplitInstallManagerFactory.create(this)

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

                val request = GlobalSplitInstallRequest.newBuilder()
                    .addModule("favorite")
                    .build()

                Log.i(this::class.java.simpleName, "requesting favorite module download")
                globalSplitInstallManager.startInstall(request)

                true
            }
            R.id.nav_remove_module -> {
                val modules = globalSplitInstallManager.installedModules.toList()
                Log.i(this::class.java.simpleName, "requesting favorite module uninstall. modules to be uninstalled: $modules")
                globalSplitInstallManager.deferredUninstall(globalSplitInstallManager.installedModules.toList())

                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    }

    override fun onResume() {

        globalSplitInstallManager.registerListener(listener2)
        globalSplitInstallManager.installedModules.forEach { installedModule ->
            Log.i(this::class.java.simpleName, "installed module: $installedModule")
        }

        super.onResume()
    }

    override fun onPause() {
        globalSplitInstallManager.unregisterListener(listener2)

        super.onPause()
    }
}