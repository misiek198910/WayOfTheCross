package droga_krzyzowa.droga_krzyzowa.ui.screens

import android.app.Activity
import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import droga_krzyzowa.droga_krzyzowa.R
import droga_krzyzowa.droga_krzyzowa.billing.SubscriptionManager
import droga_krzyzowa.droga_krzyzowa.ui.activities.MainActivity
import droga_krzyzowa.droga_krzyzowa.ui.activities.TrackScreenView
import droga_krzyzowa.droga_krzyzowa.ui.theme.DeepPurple
import droga_krzyzowa.droga_krzyzowa.ui.theme.DeepPurpleLight
import droga_krzyzowa.droga_krzyzowa.ui.theme.GoldLight
import droga_krzyzowa.droga_krzyzowa.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GorzkieZaleScreen(
    navController: NavController,
    onShowAd: () -> Unit
) {
    val mContext = LocalContext.current
    val subscriptionManager = remember { SubscriptionManager.getInstance(mContext) }
    val isPremium = subscriptionManager.isPremiumValue

    LaunchedEffect(Unit) {
        if (!isPremium) {
            (mContext as? MainActivity)?.loadInterstitialAd()
        }
    }

    TrackScreenView(screenName = stringResource(id = R.string.gorzkie_zale_screen_name))

    val contents = stringArrayResource(id = R.array.gorzkie_zale)
    val titles = stringArrayResource(id = R.array.gorzkie_zale_titles)
    val images = listOf(R.drawable.g1, R.drawable.g2, R.drawable.g3)

    val pagerState = rememberPagerState(pageCount = { images.size })
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DeepPurple
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepPurple)
                .consumeWindowInsets(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Image(
                    painter = painterResource(id = images[page]),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 64.dp, start = 16.dp)
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.gorzkie_zale_back_description),
                    tint = GoldPrimary
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(innerPadding),
                color = DeepPurple.copy(alpha = 0.7f),
                contentColor = GoldPrimary
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 2.dp, top = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                            enabled = pagerState.currentPage > 0,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_media_previous),
                                contentDescription = null,
                                tint = if (pagerState.currentPage > 0) GoldPrimary else GoldPrimary.copy(alpha = 0.3f)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showSheet = true },
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = titles[pagerState.currentPage],
                                color = GoldPrimary,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            )
                            Text(
                                text = stringResource(id = R.string.gorzkie_zale_tap_to_open),
                                color = GoldLight.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        IconButton(
                            onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                            enabled = pagerState.currentPage < pagerState.pageCount - 1,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_media_next),
                                contentDescription = null,
                                tint = if (pagerState.currentPage < pagerState.pageCount - 1) GoldPrimary else GoldPrimary.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showSheet = false
                    if (!isPremium) {
                        onShowAd()
                    }
                },
                sheetState = sheetState,
                containerColor = DeepPurpleLight,
                contentColor = GoldLight
            ) {
                val windowProvider = LocalView.current.parent as? DialogWindowProvider
                windowProvider?.window?.let { window ->
                    SideEffect {
                        val controller = WindowCompat.getInsetsController(window, window.decorView)
                        WindowCompat.setDecorFitsSystemWindows(window, false)
                        controller.isAppearanceLightNavigationBars = false
                        controller.isAppearanceLightStatusBars = false
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = titles[pagerState.currentPage],
                        style = MaterialTheme.typography.headlineMedium,
                        color = GoldPrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = { ctx ->
                            TextView(ctx).apply {
                                setTextColor(GoldLight.toArgb())
                                textSize = 18f
                                setLineSpacing(0f, 1.3f)
                            }
                        },
                        update = { textView ->
                            textView.text = Html.fromHtml(contents[pagerState.currentPage], Html.FROM_HTML_MODE_COMPACT)
                        }
                    )
                }
            }
        }
    }
}