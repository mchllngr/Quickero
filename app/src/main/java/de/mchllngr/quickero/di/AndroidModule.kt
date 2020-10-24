package de.mchllngr.quickero.di

import android.content.Context
import android.content.pm.PackageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationContext::class)
object AndroidModule {

    @Singleton
    @Provides
    fun providePackageManager(@ApplicationContext context: Context): PackageManager = context.packageManager
}
