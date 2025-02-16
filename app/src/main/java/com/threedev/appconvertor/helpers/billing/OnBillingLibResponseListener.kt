package com.threedev.appconvertor.helpers.billing

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase

interface OnBillingLibResponseListener {

    fun onPurchaseSuccess(purchase: Purchase)
    fun onPurchaseError(er: String)
    fun onSubsOfferDetails(subscriptionOfferDetails: List<ProductDetails>)
    fun onInAppOfferDetails(inAppOfferDetails: List<ProductDetails>)

}