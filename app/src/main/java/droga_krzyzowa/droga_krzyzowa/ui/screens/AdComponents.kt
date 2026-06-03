package droga_krzyzowa.droga_krzyzowa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import droga_krzyzowa.droga_krzyzowa.BuildConfig
import droga_krzyzowa.droga_krzyzowa.ui.theme.DeepPurple
import droga_krzyzowa.droga_krzyzowa.ui.theme.GoldPrimary

@Composable
fun AdBannerView(onDisableAdsClick: () -> Unit) {
    val configuration = LocalConfiguration.current
    val adWidth = configuration.screenWidthDp

    // Column rozdziela przycisk i reklamę na dwa poziomy [cite: 2026-02-24]
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepPurple)
            .navigationBarsPadding() // Obsługa paska systemowego [cite: 2026-02-23]
    ) {
        // 1. Pasek z przyciskiem "X" (nad reklamą) [cite: 2026-02-24]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, end = 4.dp)
        ) {
            IconButton(
                onClick = onDisableAdsClick,
                modifier = Modifier
                    .align(Alignment.CenterEnd) // Przycisk po prawej stronie [cite: 2026-02-24]
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Usuń reklamy",
                    tint = GoldPrimary, // Złoty kolor Twojej parafii [cite: 2026-02-17]
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // 2. Reklama Adaptacyjna (rozciągnięta na całą szerokość) [cite: 2026-02-24]
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    // Dynamiczne obliczanie rozmiaru banera [cite: 2026-02-24]
                    setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth))
                    adUnitId = BuildConfig.AD_BANNER_ID
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}