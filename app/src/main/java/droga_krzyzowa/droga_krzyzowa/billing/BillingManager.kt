package droga_krzyzowa.droga_krzyzowa.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import droga_krzyzowa.droga_krzyzowa.data.db.AppDatabase
import droga_krzyzowa.droga_krzyzowa.entity.SubscriptionEntity
import kotlinx.coroutines.*

class BillingManager private constructor(context: Context) {
    private val billingClient: BillingClient
    private val database = AppDatabase.getDatabase(context.applicationContext)
    private val dao = database.subscriptionDao()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _isPremium = MutableLiveData(false)
    val isPremium: LiveData<Boolean> = _isPremium

    private val _subscriptionStatus = MutableLiveData(SubscriptionStatus.CHECKING)
    val subscriptionStatus: LiveData<SubscriptionStatus> = _subscriptionStatus

    private val _productDetails = MutableLiveData<ProductDetails?>()
    val productDetails: LiveData<ProductDetails?> = _productDetails

    interface BillingManagerListener {
        fun onPurchaseAcknowledged()
        fun onPurchaseError(error: String?)
    }

    private var listener: BillingManagerListener? = null
    fun setListener(listener: BillingManagerListener?) { this.listener = listener }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) { handlePurchase(purchase) }
        } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
            listener?.onPurchaseError("Anulowano zakup.")
        } else {
            listener?.onPurchaseError("Błąd zakupu. Kod: ${billingResult.responseCode}")
        }
    }

    init {
        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .enablePrepaidPlans()
            .build()

        billingClient = BillingClient.newBuilder(context.applicationContext)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(pendingPurchasesParams)
            .enableAutoServiceReconnection() // Automatyczne wznawianie połączenia
            .build()

        connectToGooglePlay()

        scope.launch {
            val status = dao.getStatus()
            val isFull = status?.isPremium ?: false
            _isPremium.postValue(isFull)
            _subscriptionStatus.postValue(if (isFull) SubscriptionStatus.PREMIUM else SubscriptionStatus.NON_PREMIUM)
        }
    }

    private fun connectToGooglePlay() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    Log.d("BillingManager", "Połączono z Google Play")
                    queryPurchasesAsync()
                    queryProductDetails()
                } else {
                    Log.e("BillingManager", "Błąd połączenia: ${billingResult.responseCode}")
                    _subscriptionStatus.postValue(SubscriptionStatus.NON_PREMIUM)
                }
            }
            override fun onBillingServiceDisconnected() {
                Log.d("BillingManager", "Rozłączono z Google Play")
            }
        })
    }

    fun queryPurchasesAsync() {
        if (!billingClient.isReady) return
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(ProductType.SUBS).build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                var hasPremium = false
                var token: String? = null
                purchases.forEach { purchase ->
                    if (purchase.products.contains(SKU_PREMIUM_PRODUCT) && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        hasPremium = true
                        token = purchase.purchaseToken
                        if (!purchase.isAcknowledged) handlePurchase(purchase)
                    }
                }
                updateLocalStatus(hasPremium, token)
            }
        }
    }

    private fun updateLocalStatus(hasPremium: Boolean, token: String?) {
        scope.launch {
            dao.insert(SubscriptionEntity(isPremium = hasPremium, purchaseToken = token))
            _isPremium.postValue(hasPremium)
            _subscriptionStatus.postValue(if (hasPremium) SubscriptionStatus.PREMIUM else SubscriptionStatus.NON_PREMIUM)
        }
    }

    fun queryProductDetails() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SKU_PREMIUM_PRODUCT)
                .setProductType(ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
            val code = billingResult.responseCode
            val list = queryProductDetailsResult.productDetailsList

            // POPRAWIONE LOGOWANIE BŁĘDÓW
            val unfetched = queryProductDetailsResult.unfetchedProductList
            unfetched?.forEach { unfetchedProduct ->
                // W wersji 8.x używamy getId() lub po prostu wypisujemy obiekt
                Log.e("BillingManager", "PRODUKT NIEPOBRANY (Sprawdź ID w Console): ${unfetchedProduct.productId}")
            }

            Log.d("BillingManager", "Status zapytania: $code, Znaleziono produktów: ${list?.size ?: 0}")

            if (code == BillingResponseCode.OK && !list.isNullOrEmpty()) {
                val product = list.find { it.productId == SKU_PREMIUM_PRODUCT }
                if (product != null) {
                    _productDetails.postValue(product)
                    Log.d("BillingManager", "Pobrano szczegóły dla: ${product.productId}")
                } else {
                    Log.e("BillingManager", "Na liście brak produktu o ID: $SKU_PREMIUM_PRODUCT")
                }
            } else if (code != BillingResponseCode.OK) {
                Log.e("BillingManager", "Błąd BillingResult: $code (3=Billing Unavailable, 4=Item Unavailable)")
            }
        }
    }

    fun buyPlan(activity: Activity, basePlanId: String) {
        val product = _productDetails.value ?: return
        val offerDetails = product.subscriptionOfferDetails?.find { it.basePlanId == basePlanId }

        if (offerDetails == null) {
            listener?.onPurchaseError("Nie znaleziono planu: $basePlanId")
            Log.e("BillingManager", "Dostępne plany: ${product.subscriptionOfferDetails?.map { it.basePlanId }}")
            return
        }

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(product)
                    .setOfferToken(offerDetails.offerToken)
                    .build()
            )).build()

        billingClient.launchBillingFlow(activity, flowParams)
    }

    fun restorePurchases() { queryPurchasesAsync() }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
            billingClient.acknowledgePurchase(params) { result ->
                if (result.responseCode == BillingResponseCode.OK) {
                    updateLocalStatus(true, purchase.purchaseToken)
                    listener?.onPurchaseAcknowledged()
                }
            }
        } else if (purchase.isAcknowledged) {
            updateLocalStatus(true, purchase.purchaseToken)
        }
    }

    fun getFormattedPrice(productDetails: ProductDetails?, basePlanId: String): String? {
        val offer = productDetails?.subscriptionOfferDetails?.find { it.basePlanId == basePlanId }
        val basePhase = offer?.pricingPhases?.pricingPhaseList?.lastOrNull() // Pobieramy fazę po trialu
        return basePhase?.formattedPrice
    }

    companion object {
        @Volatile private var INSTANCE: BillingManager? = null
        fun getInstance(context: Context): BillingManager = INSTANCE ?: synchronized(this) {
            INSTANCE ?: BillingManager(context).also { INSTANCE = it }
        }

        const val SKU_PREMIUM_PRODUCT = "remove_ads_year"
        const val SKU_PREMIUM_MONTH = "premium-monthly"
        const val BASE_PLAN_YEARLY_TRIAL = "premium-yearly"
    }
}