package com.example.appopenadsapp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LifecycleObserver
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import timber.log.Timber
import java.util.*

class AppOpenAdsManager(
  private val appApplication: AppApplication
) : LifecycleObserver, Application.ActivityLifecycleCallbacks {

  /*

  App Open Ads  |  Android  |  Google Developers
  https://developers.google.com/admob/android/app-open-ads

  Use this in custom application class

  class AppApplication : Application() {
    private lateinit var appOpenAdsManager: AppOpenAdsManager
    override fun onCreate() {
      super.onCreate()
      MobileAds.initialize(this) {}
      appOpenAdsManager = AppOpenAdsManager(this)
    }
  }

  */

  private var ad: AppOpenAd? = null
  private var activity: Activity? = null
  private var loadTime: Long = 0
  private var showed = false

  init {
    appApplication.registerActivityLifecycleCallbacks(this)
  }

  override fun onActivityResumed(activity: Activity) {
    this.activity = activity
    load()
  }

  override fun onActivityDestroyed(activity: Activity) {
    this.activity = null
  }

  override fun onActivityCreated(activity: Activity, bundle: Bundle?) = Unit
  override fun onActivityStarted(activity: Activity) = Unit
  override fun onActivityPaused(activity: Activity) = Unit
  override fun onActivityStopped(activity: Activity) = Unit
  override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) = Unit

  private fun load() {
    if (loaded) {
      show()
    } else {
      val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
        override fun onAppOpenAdLoaded(ad: AppOpenAd) {
          this@AppOpenAdsManager.ad = ad
          loadTime = Date().time
          show()
        }
        override fun onAppOpenAdFailedToLoad(loadAdError: LoadAdError) = Unit
      }
      AppOpenAd.load(
        appApplication, UNIT_ID, AdRequest.Builder().build(),
        AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
      )
    }
  }

  private fun show() {
    if (showed) return

    val contentCallback = object : FullScreenContentCallback() {
      override fun onAdShowedFullScreenContent() {
        showed = true
      }
      override fun onAdDismissedFullScreenContent() {
        this@AppOpenAdsManager.ad = null
        showed = false
        load()
      }
      override fun onAdFailedToShowFullScreenContent(adError: AdError) = Unit
    }
    this.ad?.apply {
      fullScreenContentCallback = contentCallback
      activity?.run {
        show(this)
      }
    }

  }

  private val loaded: Boolean
    get() = ad != null && Date().time - loadTime < 60 * 60 * 1000 * LIFETIME_HOUR

  companion object {
    private const val UNIT_ID = "ca-app-pub-3940256099942544/3419835294" // TEST ID
    private const val LIFETIME_HOUR = 4
  }

}