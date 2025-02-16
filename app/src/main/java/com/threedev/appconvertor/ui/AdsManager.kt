//package com.threedev.appconvertor.ui
//
//import android.app.Activity
//import android.content.Context
//import android.graphics.Color
//import android.graphics.drawable.ColorDrawable
//import android.media.tv.AdRequest
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import android.view.LayoutInflater
//import com.google.android.gms.ads.AdError
//import com.google.android.gms.ads.AdListener
//import com.google.android.gms.ads.AdLoader
//import com.google.android.gms.ads.AdSize
//import com.google.android.gms.ads.AdView
//import com.google.android.gms.ads.FullScreenContentCallback
//import com.google.android.gms.ads.LoadAdError
//import com.google.android.gms.ads.interstitial.InterstitialAd
//import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
//import com.google.firebase.remoteconfig.ConfigUpdate
//import com.google.firebase.remoteconfig.ConfigUpdateListener
//import com.google.firebase.remoteconfig.FirebaseRemoteConfig
//import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
//import com.threedev.appconvertor.AppController
//import com.threedev.appconvertor.R
//import com.threedev.appconvertor.helpers.SessionManager
//
//class AdsManager {
//
//    private var interstitialAd: InterstitialAd? = null
//    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
//
//    companion object {
//        private var mInstance: AdsManager? = null
//        var showAd = true
//        fun getInstance(context: Context): AdsManager {
//            if (mInstance == null) {
//                mInstance = AdsManager()
//                mInstance!!.loadAd(context)
//            }
//            return mInstance!!
//        }
//    }
//
//    private fun loadAd(context: Context) {
//      /*  if (!SessionManager.getBool(SessionManager.IS_GDPR_CHECKED, false)) {
//            interstitialAd = null
//            Log.d("adsManager", "Block Ad due to GDPR")
//            return
//        }*/
//        if (SessionManager.getBool(SessionManager.IS_REMOVE_AD_PURCHASED, false)) {
//            interstitialAd = null
//            Log.d("adsManager", "Ad purchased")
//            return
//        }
//        if (!remoteConfig.getValue("interstitial_ad").asBoolean()) {
//            interstitialAd = null
//            Log.d("adsManager", "Block Ad using remote Config")
//            return
//        }
//        val adRequest = AdRequest.Builder().build()
//        if (interstitialAd != null) {
//            Log.d("adsManager", "Already loaded")
//            return
//        }
//        InterstitialAd.load(context, context.getString(R.string.interstitial_id), adRequest,
//            object : InterstitialAdLoadCallback() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    interstitialAd = null
//                    Log.d("adsManager", "Interstitial Ad Failed to load.")
//                }
//
//                override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                    this@AdsManager.interstitialAd = interstitialAd
//                    Log.d("adsManager", "Interstitial Ad Loaded.")
//                }
//            })
//    }
//
//    fun showInterstitialAd(activity: Activity, listener: IInterstitialAdListener) {
//        if (interstitialAd != null) {
//            if (showAd) {
//                showAd = false
//                AppController.isShowingAd = true
//                Handler(Looper.getMainLooper()).postDelayed({
//                    interstitialAd?.show(activity)
//                    interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
//                            override fun onAdDismissedFullScreenContent() {
//                                super.onAdDismissedFullScreenContent()
//                                listener.onAdClose()
//                                interstitialAd = null
//                                //loadAd(activity.application)
//                                showAd = false
//                                AppController.isShowingAd = false
//                            }
//
//                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                super.onAdFailedToShowFullScreenContent(p0)
//                                listener.onError("Failed to Show ${p0.message}")
//                                //loadAd(activity.application)
//                                showAd = true
//                                AppController.isShowingAd = false
//                            }
//                        }
//                }, 600)
//            } else {
//                listener.onError("isShow Ad is false")
//                showAd = true
//            }
//        } else {
//            listener.onError("failed to load interstitial")
//            //loadAd(activity.applicationContext)
//            showAd = true
//        }
//    }
//
//    fun loadAndShowInterstitial(activity: Activity, listener: IInterstitialAdListener){
//        if (!showAd) {
//            listener.onError("No need to show ad")
//            showAd = true
//            return
//        }
//        if (!SessionManager.getBool(SessionManager.IS_GDPR_CHECKED, false)) {
//            interstitialAd = null
//            listener.onError("No need to show ad")
//            Log.d("adsManager", "Block Ad due to GDPR")
//            return
//        }
//        if (SessionManager.getBool(SessionManager.IS_REMOVE_AD_PURCHASED, false)) {
//            interstitialAd = null
//            listener.onError("No need to show ad")
//            Log.d("adsManager", "Ad purchased")
//            return
//        }
//        if (!remoteConfig.getValue("interstitial_ad").asBoolean()) {
//            interstitialAd = null
//            listener.onError("No need to show ad")
//            Log.d("adsManager", "Block Ad using remote Config")
//            return
//        }
//        val adRequest = AdRequest.Builder().build()
//        /*if (interstitialAd != null) {
//            Log.d("adsManager", "Already loaded")
//            return
//        }*/
//        val handler = Handler(Looper.getMainLooper())
//        val runnable = Runnable {
//            listener.onError("failed to load interstitial")
//        }
//        InterstitialAd.load(activity,
//            activity.getString(R.string.interstitial_id),
//            adRequest,
//            object : InterstitialAdLoadCallback() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    //interstitialAd = null
//                    listener.onError("failed to load interstitial")
//                    Log.d("adsManager", "Interstitial Ad Failed to load.")
//                    handler.removeCallbacks(runnable)
//
//                }
//
//                override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                    //this@AdsManager.interstitialAd = interstitialAd
//                    Log.d("adsManager", "Interstitial Ad Loaded.")
//                    handler.removeCallbacks(runnable)
//                    interstitialAd.show(activity)
//                    interstitialAd.fullScreenContentCallback =
//                        object : FullScreenContentCallback() {
//                            override fun onAdDismissedFullScreenContent() {
//                                super.onAdDismissedFullScreenContent()
//                                listener.onAdClose()
//                                showAd = false
//                                AppController.isShowingAd = false
//                            }
//
//                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                super.onAdFailedToShowFullScreenContent(p0)
//                                listener.onError("Failed to Show ${p0.message}")
//                                showAd = true
//                                AppController.isShowingAd = false
//                            }
//                        }
//                }
//            })
//
//        handler.postDelayed(runnable, 2000)
//    }
//
//    fun nativeLoadAd(context: Context, listener: NativeLoadCallback) {
//        if (!SessionManager.getBool(SessionManager.IS_GDPR_CHECKED, false)) {
//            Log.d("adsManager", "Block Ad due to GDPR")
//            return
//        }
//        if (!remoteConfig.getValue("native_ad").asBoolean()) {
//            listener.onErrorAd()
//            Log.d("adsManager", "Native Ad Blocked by Remote config")
//            return
//        }
//        if (SessionManager.getBool(SessionManager.IS_REMOVE_AD_PURCHASED, false)) {
//            listener.onErrorAd()
//            Log.d("adsManager", "Ad purchased")
//            return
//        }
//        val binding = NativeSmallAdLayoutBinding.inflate(LayoutInflater.from(context))
//        val adLoader =
//            AdLoader.Builder(context, context.resources.getString(R.string.native_ad_id))
//                .forNativeAd {
//                    val style = NativeTemplateStyle.Builder().withMainBackgroundColor(
//                        ColorDrawable(
//                            Color.WHITE
//                        )
//                    ).build()
//                    binding.myTemplate.setStyles(style)
//                    binding.myTemplate.setNativeAd(it)
//                }.withAdListener(object : AdListener() {
//                    override fun onAdLoaded() {
//                        super.onAdLoaded()
//                        //binding.myTemplate.visibility = View.VISIBLE
//                        listener.onLoadAd(binding.root)
//                        Log.d("adsManager", "Native Ad loaded.")
//                    }
//
//                    override fun onAdFailedToLoad(p0: LoadAdError) {
//                        super.onAdFailedToLoad(p0)
//                        //binding.myTemplate.visibility = View.GONE
//                        listener.onErrorAd()
//                        Log.d("adsManager", "Native Ad Failed to load.")
//                    }
//                }).build()
//        adLoader.loadAds(AdRequest.Builder().build(), 1)
//
//        //return binding.root
//    }
//
//    fun nativeMediumLoadAd(context: Context, listener: NativeLoadCallback) {
//        if (!SessionManager.getBool(SessionManager.IS_GDPR_CHECKED, false)) {
//            Log.d("adsManager", "Block Ad due to GDPR")
//            return
//        }
//        if (!remoteConfig.getValue("native_ad").asBoolean()) {
//            listener.onErrorAd()
//            Log.d("adsManager", "Native Medium Ad Blocked by Remote config")
//            return
//        }
//        if (SessionManager.getBool(SessionManager.IS_REMOVE_AD_PURCHASED, false)) {
//            listener.onErrorAd()
//            Log.d("adsManager", "Ad purchased")
//            return
//        }
//        val binding = NativeMediumAdLayoutBinding.inflate(LayoutInflater.from(context))
//        val adLoader =
//            AdLoader.Builder(context, context.resources.getString(R.string.native_ad_id))
//                .forNativeAd {
//                    val style = NativeTemplateStyle.Builder().withMainBackgroundColor(
//                        ColorDrawable(
//                            Color.WHITE
//                        )
//                    ).build()
//                    binding.myTemplate.setStyles(style)
//                    binding.myTemplate.setNativeAd(it)
//                }.withAdListener(object : AdListener() {
//                    override fun onAdLoaded() {
//                        super.onAdLoaded()
//                        //binding.myTemplate.visibility = View.VISIBLE
//                        listener.onLoadAd(binding.root)
//                        Log.d("adsManager", "Native Medium Ad loaded successfully.")
//                    }
//
//                    override fun onAdFailedToLoad(p0: LoadAdError) {
//                        super.onAdFailedToLoad(p0)
//                        //binding.myTemplate.visibility = View.GONE
//                        listener.onErrorAd()
//                        Log.d("adsManager", "Native Medium Ad failed to load")
//                    }
//                }).build()
//        adLoader.loadAds(AdRequest.Builder().build(), 2)
//
//        //return binding.root
//    }
//
//    fun loadBanner(context: Context, listener: IBannerListener) {
//        if (!SessionManager.getBool(SessionManager.IS_GDPR_CHECKED, false)) {
//            Log.d("adsManager", "Block Ad due to GDPR")
//            return
//        }
//
//        if (SessionManager.getBool(SessionManager.IS_REMOVE_AD_PURCHASED, false)) {
//            listener.onBannerError("Ad purchased")
//            Log.d("adsManager", "Ad purchased")
//            return
//        }
//        val binding = BannerAdViewBinding.inflate(LayoutInflater.from(context))
//        if (!remoteConfig.getValue("banner_ad").asBoolean()) {
//            listener.onBannerError("Ad Closed from Remote Config")
//            Log.d("adsManager", "Banner Ad Blocked by Remote config")
//            return
//        }
//        val adRequest = AdRequest.Builder().build()
//        binding.adView.loadAd(adRequest)
//        binding.adView.adListener = object : AdListener() {
//            override fun onAdClicked() {
//                // Code to be executed when the user clicks on an ad.
//            }
//
//            override fun onAdClosed() {
//                // Code to be executed when the user is about to return
//                // to the app after tapping on an ad.
//            }
//
//            override fun onAdFailedToLoad(adError: LoadAdError) {
//                // Code to be executed when an ad request fails.
//                listener.onBannerError("Failed to Load Message=> ${adError.message}")
//            }
//
//            override fun onAdImpression() {
//                // Code to be executed when an impression is recorded
//                // for an ad.
//            }
//
//            override fun onAdLoaded() {
//                // Code to be executed when an ad finishes loading.
//                listener.onBannerLoaded(binding.root)
//            }
//
//            override fun onAdOpened() {
//                // Code to be executed when an ad opens an overlay that
//                // covers the screen.
//            }
//        }
//    }
//
//    fun loadAdaptiveBanner(context: Context, adSize: AdSize, listener: IBannerListener) {
//        if (!SessionManager.getBool(SessionManager.IS_GDPR_CHECKED, false)) {
//            Log.d("adsManager", "Block Ad due to GDPR")
//            return
//        }
//        if (SessionManager.getBool(SessionManager.IS_REMOVE_AD_PURCHASED, false)) {
//            listener.onBannerError("Ad purchased")
//            Log.d("adsManager", "Ad purchased")
//            return
//        }
//        if (remoteConfig.getValue("banner_ad").asBoolean()) {
//            //val binding = BannerAdViewBinding.inflate(LayoutInflater.from(context))
//            val adView = AdView(context)
//            adView.adUnitId = context.getString(R.string.banner_id)
//            adView.setAdSize(adSize);
//            adView.adListener = object : AdListener() {
//                override fun onAdClicked() {
//                    // Code to be executed when the user clicks on an ad.
//                }
//
//                override fun onAdClosed() {
//                    // Code to be executed when the user is about to return
//                    // to the app after tapping on an ad.
//                }
//
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    // Code to be executed when an ad request fails.
//                    listener.onBannerError("Failed to Load Message=> ${adError.message}")
//                }
//
//                override fun onAdImpression() {
//                    // Code to be executed when an impression is recorded
//                    // for an ad.
//                }
//
//                override fun onAdLoaded() {
//                    // Code to be executed when an ad finishes loading.
//                    listener.onBannerLoaded(adView)
//                }
//
//                override fun onAdOpened() {
//                    // Code to be executed when an ad opens an overlay that
//                    // covers the screen.
//                }
//            }
//            val adRequest = AdRequest.Builder().build()
//            adView.loadAd(adRequest)
//        } else {
//            listener.onBannerError("Ad Closed from Remote Config")
//        }
//    }
//
//    fun remoteConfigUpdateListener(listener: OnAdsConfigChange) {
//        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
//            override fun onUpdate(configUpdate: ConfigUpdate) {
//                Log.d("remote_config", "Updated keys: " + configUpdate.updatedKeys);
//                remoteConfig.activate().addOnCompleteListener {
//                    listener.onAdsChange(configUpdate.updatedKeys)
//                }
//            }
//
//            override fun onError(error: FirebaseRemoteConfigException) {
//                Log.w("TAG", "Config update error with code: " + error.code, error)
//            }
//        })
//    }
//
//    fun loadRewardAd(activity: Activity, listener: IRewardCallback) {
//        if (!SessionManager.getBool(SessionManager.IS_GDPR_CHECKED, false)) {
//            Log.d("adsManager", "Block Ad due to GDPR")
//            return
//        }
//        if (SessionManager.getBool(SessionManager.IS_REMOVE_AD_PURCHASED, false)) {
//            listener.onError("Ad purchased")
//            Log.d("adsManager", "Ad purchased")
//            return
//        }
//        if (!remoteConfig.getValue("reward_ad").asBoolean()) {
//            Log.d("adsManager", "Reward Ad Blocked by Remote config")
//            loadIRewardInterstitialAd(activity, listener)
//            return
//        }
//        val adRequest = AdRequest.Builder().build()
//        RewardedAd.load(
//            activity,
//            activity.getString(R.string.admob_reward_id),
//            adRequest,
//            object : RewardedAdLoadCallback() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    Log.d("adsManager", adError.toString())
//                    loadIRewardInterstitialAd(activity, listener)
//                }
//
//                override fun onAdLoaded(ad: RewardedAd) {
//                    Log.d("adsManager", "Ad was loaded.")
//                    ad.show(activity, OnUserEarnedRewardListener {
////                        SessionManager.setInt(SessionManager.KEY_TRANSLATE_COIN, 5)
//                        listener.onReward("Earned Reward")
//                    })
//                }
//            })
//    }
//
//    fun loadIRewardInterstitialAd(activity: Activity, listener: IRewardCallback) {
//        if (!SessionManager.getBool(SessionManager.IS_GDPR_CHECKED, false)) {
//            Log.d("adsManager", "Block Ad due to GDPR")
//            return
//        }
//        if (SessionManager.getBool(SessionManager.IS_REMOVE_AD_PURCHASED, false)) {
//            listener.onError("Ad purchased")
//            Log.d("adsManager", "Ad purchased")
//            return
//        }
//        if (!remoteConfig.getValue("reward_interstitial_ad").asBoolean()) {
//            listener.onError("Ad Closed from Remote Config")
//            Log.d("adsManager", "Reward Interstitial Ad Blocked by Remote config")
//            return
//        }
//
//        load(activity, activity.getString(R.string.admob_reward_interstitial_id),
//            AdRequest.Builder().build(), object : RewardedInterstitialAdLoadCallback() {
//                override fun onAdLoaded(ad: RewardedInterstitialAd) {
//                    Log.d("adsManager", "Ad was loaded.")
//                    ad.show(activity, OnUserEarnedRewardListener {
////                        SessionManager.setInt(SessionManager.KEY_TRANSLATE_COIN, 5)
//                        listener.onReward("Earned Reward")
//                    })
//                }
//
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    Log.d("adsManager", adError.toString())
//                    listener.onError("Reward Interstitial ads Not loaded.")
//                }
//            })
//    }
//
//    interface IBannerListener {
//        fun onBannerLoaded(root: AdView)
//        fun onBannerError(s: String)
//    }
//
//    interface IInterstitialAdListener {
//        fun onError(s: String)
//        fun onAdClose()
//    }
//
//    interface NativeLoadCallback {
//        fun onLoadAd(root: TemplateView)
//        fun onErrorAd()
//    }
//
//    interface IRewardCallback {
//        fun onReward(s: String)
//        fun onError(s: String)
//    }
//
//    interface OnAdsConfigChange {
//        fun onAdsChange(updatedKeys: MutableSet<String>)
//    }
//
//}