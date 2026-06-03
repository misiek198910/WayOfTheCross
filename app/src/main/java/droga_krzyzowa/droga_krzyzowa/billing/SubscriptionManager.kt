package droga_krzyzowa.droga_krzyzowa.billing

import android.content.Context
import androidx.lifecycle.LiveData
import com.android.billingclient.api.ProductDetails

class SubscriptionManager private constructor(context: Context) {

    // Pobieramy instancję BillingManager z poprawnej paczki
    val billingManager: BillingManager = BillingManager.getInstance(context)

    // Obserwacja statusu Premium (Boolean)
    val isPremium: LiveData<Boolean> = billingManager.isPremium

    // Obserwacja szczegółowego statusu (Enum: CHECKING, PREMIUM, NON_PREMIUM)
    val subscriptionStatus: LiveData<SubscriptionStatus> = billingManager.subscriptionStatus

    // Dane o produktach pobrane z Google Play
    val productDetails: LiveData<ProductDetails?> = billingManager.productDetails

    // Szybki dostęp do wartości bez konieczności używania obserwatora w kodzie logicznym
    val isPremiumValue: Boolean
        get() = billingManager.isPremium.value ?: false

    companion object {
        @Volatile
        private var INSTANCE: SubscriptionManager? = null

        fun getInstance(context: Context): SubscriptionManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SubscriptionManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}