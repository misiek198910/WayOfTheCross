package droga_krzyzowa.droga_krzyzowa.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import droga_krzyzowa.droga_krzyzowa.R
import droga_krzyzowa.droga_krzyzowa.ui.theme.DeepPurple
import droga_krzyzowa.droga_krzyzowa.ui.theme.GoldPrimary

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepPurple),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Zamiana Icon na Image dla Twojego pliku logo.png [cite: 2026-02-23]
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Moja Parafia",
                modifier = Modifier.size(150.dp) // Zwiększyłem nieco rozmiar dla logo
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.app_name),
                color = GoldPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Pasek ładowania widoczny podczas czekania na reklamę [cite: 2026-02-17, 2026-02-23]
            CircularProgressIndicator(color = GoldPrimary)
        }
    }
}