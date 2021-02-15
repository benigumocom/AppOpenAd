package com.example.appopenadsapp

import android.app.Application
import com.google.android.gms.ads.MobileAds

class AppApplication : Application() {

  private lateinit var appOpenManager: AppOpenManager

  override fun onCreate() {
    super.onCreate()
    MobileAds.initialize(this) {}
    appOpenManager = AppOpenManager(this)
  }

}