package droga_krzyzowa.droga_krzyzowa.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import droga_krzyzowa.droga_krzyzowa.R
import droga_krzyzowa.droga_krzyzowa.ui.theme.DeepPurple
import droga_krzyzowa.droga_krzyzowa.ui.theme.DeepPurpleLight
import droga_krzyzowa.droga_krzyzowa.ui.theme.GoldPrimary
import droga_krzyzowa.droga_krzyzowa.viewmodel.NewsViewModel

@Composable
fun MainMenuScreen(
    navController: NavController,
    newsViewModel: NewsViewModel = viewModel(),
    onNavigateToWayOfCross: () -> Unit,
    onNavigateToGorzkieZale: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepPurple)
            .statusBarsPadding()
        // Nie używamy navigationBarsPadding tutaj, bo baner reklamowy
        // w MainActivity już zarządza dolną przestrzenią.
    ) {
        if (isLandscape) {
            // --- UKŁAD POZIOMY (LANDSCAPE) ---
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEWA STRONA: Logo na środku
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(180.dp), // Większe logo skoro nie ma boxa pod spodem
                        contentScale = ContentScale.Fit
                    )
                }

                // PRAWA STRONA: Przyciski (przewijalne)
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Dodatkowy odstęp na górze kolumny dla wyważenia
                    Spacer(modifier = Modifier.height(8.dp))

                    MenuContent(
                        navController = navController,
                        newsViewModel = newsViewModel,
                        onNavigateToWayOfCross = onNavigateToWayOfCross,
                        onNavigateToGorzkieZale = onNavigateToGorzkieZale,
                        onNavigateToSettings = onNavigateToSettings
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        } else {
            // --- UKŁAD PIONOWY (PORTRAIT) ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // WYCENTROWANIE ZAWARTOŚCI W PIONIE
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logowide),
                    contentDescription = "Logo Wide",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(bottom = 32.dp),
                    contentScale = ContentScale.FillWidth
                )

                MenuContent(
                    navController = navController,
                    newsViewModel = newsViewModel,
                    onNavigateToWayOfCross = onNavigateToWayOfCross,
                    onNavigateToGorzkieZale = onNavigateToGorzkieZale,
                    onNavigateToSettings = onNavigateToSettings
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun MenuContent(
    navController: NavController,
    newsViewModel: NewsViewModel,
    onNavigateToWayOfCross: () -> Unit,
    onNavigateToGorzkieZale: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    MenuButton(
        title = stringResource(id = R.string.menu_way_of_cross_title),
        subtitle = stringResource(id = R.string.menu_way_of_cross_subtitle),
        onClick = onNavigateToWayOfCross
    )

    Spacer(modifier = Modifier.height(16.dp))

    MenuButton(
        title = stringResource(id = R.string.menu_gorzkie_zale_title),
        subtitle = stringResource(id = R.string.menu_gorzkie_zale_subtitle),
        onClick = onNavigateToGorzkieZale
    )

    Spacer(modifier = Modifier.height(16.dp))

    Box(modifier = Modifier.fillMaxWidth()) {
        MenuButton(
            title = stringResource(id = R.string.menu_news_title),
            subtitle = stringResource(id = R.string.menu_news_subtitle),
            onClick = { navController.navigate("news_feed") }
        )

        if (newsViewModel.hasUnreadNews.value) {
            Surface(
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-12).dp, y = 12.dp),
                color = Color.Red,
                shape = CircleShape,
                border = BorderStroke(2.dp, DeepPurple)
            ) {}
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedButton(
        onClick = onNavigateToSettings,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.height(50.dp).fillMaxWidth()
    ) {
        Icon(Icons.Default.Settings, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(id = R.string.menu_settings))
    }
}

@Composable
fun MenuButton(title: String, subtitle: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepPurpleLight, DeepPurple)
                )
            )
            .border(1.dp, GoldPrimary.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = title,
                color = GoldPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 13.sp
            )
        }
    }
}