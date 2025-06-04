package com.example.airport.ui.rate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.math.BigDecimal

@Composable
fun ExchangeRateList(baseAmount: BigDecimal, rates: Map<String, Double>, selectedCurrencies: List<String>) {

    // 過濾選中的幣別匯率
    val filteredRates = selectedCurrencies.mapNotNull { currency ->
        rates[currency]?.let { rate -> currency to rate }
    }
    // 屏幕高度
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // 使用 LazyColumn 列表顯示
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight * 0.4f),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filteredRates) { (currency, rate) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(end = 10.dp),
                    text = currency,
                    style = MaterialTheme.typography.bodyLarge ,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = String.format("%.5f", baseAmount.multiply(BigDecimal.valueOf(rate))),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
