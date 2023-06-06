package com.drsync.movieapp

import android.app.Application
import com.drsync.core.di.databaseModule
import com.drsync.core.di.networkModule
import com.drsync.core.di.repositoryModule
import com.drsync.movieapp.di.useCaseModule
import com.drsync.movieapp.di.viewModelModule
import com.jeppeman.globallydynamic.globalsplitcompat.GlobalSplitCompatApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MovieApplication : GlobalSplitCompatApplication() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MovieApplication)
            modules(
                listOf(
                    databaseModule,
                    networkModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }
}