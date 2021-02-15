package com.example.appopenadsapp

import android.app.Application
import com.google.android.gms.ads.MobileAds

class AppApplication : Application() {

  private lateinit var appOpenAdsManager: AppOpenAdsManager

  override fun onCreate() {
    super.onCreate()
    MobileAds.initialize(this) {}
    appOpenAdsManager = AppOpenAdsManager(this)
  }

}