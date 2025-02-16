package com.threedev.appconvertor.helpers.billing

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.google.common.collect.ImmutableList


class BillingWrapper {
    var itemPurchasesList: MutableList<Purchase> = ArrayList()
    var context: Context? = null
    var billingClient: BillingClient? = null
    var showToast = true

    var onBillingLibResponseListener: OnBillingLibResponseListener? = null
    fun setBillingListener(listener: OnBillingLibResponseListener) {
        onBillingLibResponseListener = listener
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var billingWrapper: BillingWrapper? = null

        @Synchronized
        fun getInstance(): BillingWrapper {
            if (billingWrapper == null) {
                synchronized(this) {
                    billingWrapper = BillingWrapper()
                }
            }
            return billingWrapper!!
        }
    }

    var listener = PurchasesUpdatedListener { billingResult, list ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
            for (purchase in list) {
                itemPurchasesList.add(purchase)
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            if (showToast) {
                showToast = false
                Toast.makeText(this.context, "User has canceled!", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    showToast = true
                },1500)
            }
        } else {
            // Handle any other error codes.
            if (showToast) {
                showToast = false
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    showToast = true
                },1500)
            }
        }
    }
    var TAG = "BILLINGUTILS"
    fun initialize(context: Context) {
        this.context = context

        init(context)
    }


    var subscriptionProductIds = ArrayList<String>()
    var inAppProductIds = ArrayList<String>()
    var offersProductDetails = ArrayList<ProductDetails>()
    var inappoffersProductDetails = ArrayList<ProductDetails>()


    val isBillingReady: Boolean
        get() = billingClient!!.isReady

    private fun init(context: Context) {
        billingClient = BillingClient.newBuilder(context).setListener(listener)
            .enablePendingPurchases()
            .build()
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryOffers()
                    Log.d(TAG, "onBillingSetupFinished: ")
                } else {
                    Log.d(TAG, "onBillingSetupFinished: Not init")
                }
            }

            override fun onBillingServiceDisconnected() {
                Toast.makeText(context, "Unable to start billing! try again", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    //query available purchases
    fun queryOffers() {

        val productList: List<QueryProductDetailsParams.Product> // = ImmutableList.<QueryProductDetailsParams.Product>builder().build();

        if (subscriptionProductIds.isNotEmpty()) {
            val p = QueryProductDetailsParams.Product.newBuilder()
                .setProductId(subscriptionProductIds[0])
                .setProductType(BillingClient.ProductType.SUBS).build()
            val p1 = QueryProductDetailsParams.Product.newBuilder()
                .setProductId(subscriptionProductIds[1])
                .setProductType(BillingClient.ProductType.SUBS).build()
            productList = ImmutableList.of(p, p1)
            val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

            billingClient!!.queryProductDetailsAsync(
                params
            ) { billingResult: BillingResult, productDetailsList: List<ProductDetails>? ->
                // Process the result
                Log.d(
                    TAG, "onProductDetailsResponse: " + productDetailsList!!.size
                )
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (productDetailsList.isNotEmpty()) {
                        offersProductDetails.addAll(productDetailsList)
                        onBillingLibResponseListener?.onSubsOfferDetails(offersProductDetails)
                        Log.d(
                            TAG,
                            "initProDetails: " + productDetailsList[0].subscriptionOfferDetails!![0].pricingPhases.pricingPhaseList[0].formattedPrice
                        )
                    }
                    Log.d(
                        TAG, "onProductDetailsResponse: " + productDetailsList.size
                    )

                }
            }
        }
        if (inAppProductIds.isNotEmpty()) {
            val inappPList: ImmutableList<QueryProductDetailsParams.Product> = ImmutableList.of(
                QueryProductDetailsParams.Product.newBuilder().setProductId(
                    inAppProductIds[0]
                ).setProductType(BillingClient.ProductType.INAPP).build()
            )

            val inappParams =
                QueryProductDetailsParams.newBuilder().setProductList(inappPList).build()

            billingClient!!.queryProductDetailsAsync(inappParams) { billingResult, productDetailsList -> // Process the result
                Log.d(
                    TAG, "onProductDetailsResponse: " + billingResult.responseCode
                )
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(
                        TAG, "onProductDetailsResponse:inapp " + productDetailsList.size
                    )
                    if (productDetailsList.isNotEmpty()) {
                        inappoffersProductDetails.addAll(productDetailsList)
                        onBillingLibResponseListener!!.onInAppOfferDetails(inappoffersProductDetails)
                    }

                }
            }
        }
    }


    fun launchBillingFlow(products: Array<Int>, activity: Activity) {
        var productDetailsParamsList: MutableList<BillingFlowParams.ProductDetailsParams> = ArrayList()
        for (product in products){
            val p = inappoffersProductDetails[product]
            val params = if (p.productType == "inapp") {
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(p) //   .setOfferToken(offerToken)
                    .build()
            }else {
                val offerToken: String = p.subscriptionOfferDetails!![0].offerToken
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(p)
                    .setOfferToken(offerToken)
                    .build()
            }

            productDetailsParamsList.add(params)
        }
        billingParams(productDetailsParamsList, activity)
    }

    private fun billingParams(productDetailsParamsList: MutableList<BillingFlowParams.ProductDetailsParams>, activity: Activity) {
        try {
            val billingFlowParams =
                BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList)
                    .build()
            when (billingClient!!.launchBillingFlow(activity, billingFlowParams).responseCode) {
                BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> Toast.makeText(
                    context, "BILLING_UNAVAILABLE", Toast.LENGTH_SHORT
                ).show()

                BillingClient.BillingResponseCode.ERROR -> Toast.makeText(
                    context, "Some thing went wrong", Toast.LENGTH_SHORT
                ).show()

                BillingClient.BillingResponseCode.DEVELOPER_ERROR -> Toast.makeText(
                    context, "Some thing went wrong", Toast.LENGTH_SHORT
                ).show()

                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> Toast.makeText(
                    context, "You have already owned this item", Toast.LENGTH_SHORT
                ).show()

                BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> Toast.makeText(
                    context, "FEATURE NOT SUPPORTED", Toast.LENGTH_SHORT
                ).show()

                BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> Toast.makeText(
                    context, "ITEM NOT OWNED", Toast.LENGTH_SHORT
                ).show()

                BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> Toast.makeText(
                    context, "ITEM UNAVAILABLE", Toast.LENGTH_SHORT
                ).show()

                BillingClient.BillingResponseCode.OK -> {

                }

                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> Toast.makeText(
                    context, "SERVICE DISCONNECTED", Toast.LENGTH_SHORT
                ).show()

                BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> Toast.makeText(
                    context, "SERVICE UNAVAILABLE", Toast.LENGTH_SHORT
                ).show()

                BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> Toast.makeText(
                    context, "SERVICE TIMEOUT", Toast.LENGTH_SHORT
                ).show()

                BillingClient.BillingResponseCode.USER_CANCELED -> Toast.makeText(
                    context, "USER CANCELED", Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        //Acknowledge
        val listener = AcknowledgePurchaseResponseListener { billingResult: BillingResult? ->
            if (billingResult!!.responseCode == BillingClient.BillingResponseCode.OK) {
                onBillingLibResponseListener!!.onPurchaseSuccess(purchase)
            } else {
                onBillingLibResponseListener?.onPurchaseError(billingResult.debugMessage)
            }
        }
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            Toast.makeText(context, "Your Purchase is successfully", Toast.LENGTH_SHORT).show()
            if (!purchase.isAcknowledged) {
                val params =
                    AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                        .build()
                billingClient!!.acknowledgePurchase(params, listener)
            }
            //PrefrenceUtils.setUserPurchase(context, true)
        }
    }
}
