package com.example.appopenadsapp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.*

class AppOpenAdsManager(
  private val appApplication: AppApplication
) : LifecycleObserver, Application.ActivityLifecycleCallbacks {

  private var ad: AppOpenAd? = null
  private var activity: Activity? = null
  private var loadTime: Long = 0
  private var showed = false

  init {
    appApplication.registerActivityLifecycleCallbacks(this)
  }

  override fun onActivityResumed(activity: Activity) {
    this.activity = activity
    showIfAvailable()
  }
  override fun onActivityDestroyed(activity: Activity) {
    this.activity = null
  }

  override fun onActivityCreated(activity: Activity, bundle: Bundle?) = Unit
  override fun onActivityStarted(activity: Activity) = Unit
  override fun onActivityPaused(activity: Activity) = Unit
  override fun onActivityStopped(activity: Activity) = Unit
  override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) = Unit

  private fun showIfAvailable() {
    if (canShow) {
      Log.d(LOG_TAG, "Will show ad.")
      this.ad?.apply {
        fullScreenContentCallback = object : FullScreenContentCallback() {
          override fun onAdDismissedFullScreenContent() {
            this@AppOpenAdsManager.ad = null
            showed = false
            load()
          }
          override fun onAdShowedFullScreenContent() {
            showed = true
          }
          override fun onAdFailedToShowFullScreenContent(adError: AdError) = Unit
        }
        activity?.run {
          show(this)
        }
      }
    } else {
      Log.d(LOG_TAG, "Can not show ad.")
      load()
    }
  }

  private fun load() {
    if (loaded) return

    val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
      override fun onAppOpenAdLoaded(ad: AppOpenAd) {
        this@AppOpenAdsManager.ad = ad
        loadTime = Date().time
      }
      override fun onAppOpenAdFailedToLoad(loadAdError: LoadAdError) = Unit
    }
    AppOpenAd.load(appApplication, UNIT_ID, AdRequest.Builder().build(),
      AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback)
  }

  private val canShow: Boolean
    get() = !showed && loaded

  private val loaded: Boolean
    get() = ad != null && Date().time - loadTime < 60 * 60 * 1000 * LIFETIME

  companion object {
    private const val LOG_TAG = "AppOpenManager"
    private const val UNIT_ID = "ca-app-pub-3940256099942544/3419835294" // TEST ID
    private const val LIFETIME = 4
  }

}