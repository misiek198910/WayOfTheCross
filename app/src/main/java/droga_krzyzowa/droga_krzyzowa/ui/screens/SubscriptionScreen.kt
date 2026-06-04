package droga_krzyzowa.droga_krzyzowa.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import droga_krzyzowa.droga_krzyzowa.R
import droga_krzyzowa.droga_krzyzowa.ui.activities.TrackScreenView
import droga_krzyzowa.droga_krzyzowa.ui.theme.DeepPurple
import droga_krzyzowa.droga_krzyzowa.ui.theme.DeepPurpleLight
import droga_krzyzowa.droga_krzyzowa.ui.theme.GoldLight
import droga_krzyzowa.droga_krzyzowa.ui.theme.GoldPrimary
import droga_krzyzowa.droga_krzyzowa.viewmodel.SubscriptionViewModel
import androidx.core.net.toUri
import droga_krzyzowa.droga_krzyzowa.billing.BillingManager

@Composable
fun SubscriptionScreen(navController: NavController) {
    TrackScreenView(screenName = "subscription")

    val context = LocalContext.current
    val activity = context as Activity
    val viewModel: SubscriptionViewModel = viewModel()

    val isPremium by viewModel.isPremium.observeAsState(false)
    val details by viewModel.productDetails.observeAsState()

    val monthlyPrice = viewModel.getPlanPrice(details, BillingManager.SKU_PREMIUM_MONTH)
    val yearlyPrice = viewModel.getPlanPrice(details, BillingManager.BASE_PLAN_YEARLY_TRIAL)

    Scaffold(
        containerColor = DeepPurple,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.subs_back),
                        tint = Color.White
                    )
                }
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp).padding(start = 4.dp),
                    tint = GoldPrimary
                )
                Text(
                    text = stringResource(id = R.string.subs_title),
                    modifier = Modifier.padding(start = 6.dp),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xE61A1A1A)),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.subs_card_title),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(id = R.string.subs_description),
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color(0xFFBDBDBD),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        BenefitRow(text = stringResource(id = R.string.subs_benefit_no_ads))
                        Spacer(modifier = Modifier.height(8.dp))
                        BenefitRow(text = stringResource(id = R.string.subs_benefit_support))
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 1.dp,
                        color = Color.White.copy(alpha = 0.2f)
                    )

                    Text(
                        text = stringResource(id = R.string.subs_status_label),
                        color = Color(0xFFBDBDBD),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (isPremium) stringResource(id = R.string.subs_status_active) else stringResource(id = R.string.subs_status_inactive),
                        color = if (isPremium) Color(0xFF4CAF50) else Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SubscriptionButton(
                        text = if (isPremium) {
                            stringResource(R.string.subs_manage)
                        } else {
                            monthlyPrice?.let {
                                stringResource(R.string.subs_monthly_button, it)
                            } ?: stringResource(R.string.load_data)
                        },
                        onClick = {
                            if (isPremium) {
                                val url = "https://play.google.com/store/account/subscriptions?package=${context.packageName}"
                                context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                            } else {
                                viewModel.buyPlan(activity, BillingManager.SKU_PREMIUM_MONTH)
                            }
                        }
                    )

                    if (!isPremium) {
                        Spacer(modifier = Modifier.height(8.dp))
                        SubscriptionButton(
                            text = yearlyPrice?.let {
                                stringResource(R.string.subs_annual_button, it)
                            } ?: stringResource(R.string.load_data),
                            onClick = {
                                viewModel.buyPlan(activity, BillingManager.BASE_PLAN_YEARLY_TRIAL)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    TextButton(onClick = { viewModel.restorePurchases() }) {
                        Text("Przywróć zakup", color = GoldLight, fontSize = 14.sp)
                    }

                    Text(
                        text = stringResource(id = R.string.subs_footer_info),
                        modifier = Modifier.padding(top = 12.dp),
                        color = Color(0xFF9E9E9E),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun BenefitRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = GoldPrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun SubscriptionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DeepPurpleLight
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f))
    ) {
        Text(
            text = text,
            color = GoldLight,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}