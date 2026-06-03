package droga_krzyzowa.droga_krzyzowa.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.android.billingclient.api.ProductDetails
import droga_krzyzowa.droga_krzyzowa.billing.BillingManager
import droga_krzyzowa.droga_krzyzowa.billing.SubscriptionManager

class SubscriptionViewModel(application: Application) : AndroidViewModel(application) {
    private val subscriptionManager = SubscriptionManager.getInstance(application)
    private val billingManager = subscriptionManager.billingManager

    val isPremium: LiveData<Boolean> = subscriptionManager.isPremium
    val productDetails: LiveData<ProductDetails?> = subscriptionManager.productDetails

    fun getPlanPrice(productDetails: ProductDetails?, planId: String): String? {
        return billingManager.getFormattedPrice(productDetails, planId)
    }

    fun buyPlan(activity: Activity, planId: String) {
        billingManager.buyPlan(activity, planId)
    }

    fun restorePurchases() {
        billingManager.restorePurchases()
    }
}