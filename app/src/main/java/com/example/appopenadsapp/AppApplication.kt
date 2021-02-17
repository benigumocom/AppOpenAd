package com.example.appopenadsapp

import android.app.Application
import com.google.android.gms.ads.MobileAds
import timber.log.Timber
import timber.log.Timber.DebugTree


class AppApplication : Application() {

  private lateinit var appOpenAdsManager: AppOpenAdsManager

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(DebugTree())
    }

    MobileAds.initialize(this) {}
    appOpenAdsManager = AppOpenAdsManager(this)
  }

}