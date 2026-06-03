package droga_krzyzowa.droga_krzyzowa.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import droga_krzyzowa.droga_krzyzowa.R
import droga_krzyzowa.droga_krzyzowa.ui.activities.TrackScreenView
import droga_krzyzowa.droga_krzyzowa.ui.theme.DeepPurple
import droga_krzyzowa.droga_krzyzowa.ui.theme.DeepPurpleLight
import droga_krzyzowa.droga_krzyzowa.ui.theme.GoldLight
import droga_krzyzowa.droga_krzyzowa.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController,
                   onNavigateToPrivacyPolicy: () -> Unit,) {
    TrackScreenView(screenName = "settings")

    var showLanguageSheet by remember { mutableStateOf(false) }

    val currentLocaleList = AppCompatDelegate.getApplicationLocales()
    val activeLanguage = remember(currentLocaleList) {
        if (currentLocaleList.isEmpty) "pl" else currentLocaleList.get(0)?.language ?: "pl"
    }

    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        containerColor = DeepPurple,
        topBar = {
            // Pasek górny z przyciskiem powrotu
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.settings_back),
                        tint = GoldPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(id = R.string.settings_title),
                    color = GoldPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Przycisk: Zmiana Języka
            SettingsMenuButton(
                text = stringResource(id = R.string.settings_language),
                icon = Icons.Default.Language,
                onClick = { showLanguageSheet = true }
            )

            // Przycisk: Subskrypcje
            SettingsMenuButton(
                text = stringResource(id = R.string.settings_subscriptions),
                icon = Icons.Default.Star,
                onClick = { navController.navigate("subscription") }
            )

            // Przycisk: Polityka Prywatności
            SettingsMenuButton(
                text = stringResource(id = R.string.settings_privacy_policy),
                icon = Icons.Default.Security,
                onClick = onNavigateToPrivacyPolicy
            )
        }

        if (showLanguageSheet) {
            ModalBottomSheet(
                onDismissRequest = { showLanguageSheet = false },
                sheetState = sheetState,
                containerColor = DeepPurpleLight,
                contentColor = GoldLight
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_select_language),
                        color = GoldPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(24.dp)
                    )

                    LanguageOption(
                        title = stringResource(id = R.string.settings_lang_pl),
                        isSelected = activeLanguage.startsWith("pl"),
                        onClick = {
                            changeLanguage("pl")
                            showLanguageSheet = false
                        }
                    )

                    LanguageOption(
                        title = stringResource(id = R.string.settings_lang_en),
                        isSelected = activeLanguage.startsWith("en"),
                        onClick = {
                            changeLanguage("en")
                            showLanguageSheet = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageOption(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = GoldLight,
            fontSize = 18.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = GoldPrimary
            )
        }
    }
}

@Composable
fun SettingsMenuButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DeepPurpleLight // Używamy jaśniejszego fioletu dla kontrastu [cite: 2026-02-17]
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)) // Złota obwoluta jak w main [cite: 2026-02-17]
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = GoldLight,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = GoldPrimary.copy(alpha = 0.7f)
            )
        }
    }
}

fun changeLanguage(languageCode: String) {
    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
    AppCompatDelegate.setApplicationLocales(appLocale)
}