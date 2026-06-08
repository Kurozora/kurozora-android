package app.kurozora.ui.screens.recap

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.screens.recap.components.RecapScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecapItemScreen(
    year: String,
    month: String, // Başlangıç ayı (navigasyondan gelen)
    windowWidth: WindowWidthSizeClass,
    isLoggedIn: Boolean,
    onNavigateToItemDetail: (Any) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RecapItemViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    // Seçili ayı takip etmek için local state
    var selectedMonth by remember { mutableStateOf(month) }

    // Ay listesi
    val months = listOf(
        "01" to "JAN", "02" to "FEB", "03" to "MAR", "04" to "APR",
        "05" to "MAY", "06" to "JUN", "07" to "JUL", "08" to "AUG",
        "09" to "SEP", "10" to "OCT", "11" to "NOV", "12" to "DEC"
    )

    // selectedMonth her değiştiğinde veriyi tekrar çek
    LaunchedEffect(selectedMonth) {
        viewModel.fetchRecapDetails(year = year, month = selectedMonth)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF050810))) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
            }
            state.errorMessage != null -> {
                Text(
                    text = state.errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.recapData != null -> {
                RecapScreen(data = state.recapData!!)
            }
        }

        // --- SOL ÜST: GERİ BUTONU ---
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // --- SAĞ ÜST: AY SEÇİCİ (MONTH PICKER) ---
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 12.dp, end = 16.dp, start = 60.dp) // Geri butonuyla çakışmaması için start padding
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            months.forEach { (mCode, mName) ->
                val isSelected = selectedMonth == mCode

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFFF5C518) else Color.White.copy(0.1f))
                        .clickable { selectedMonth = mCode }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mName,
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}