package com.example.airport.ui.rate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airport.R
import com.example.airport.data.LoadMore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeRateScreen(viewModel: RateViewModel) {

    /**
     * 使用 LiveData 並轉換為 Compose 狀態（會自動重組 UI）
     * 從 viewModel.baseAmount（這是個 LiveData<Double>）觀察使用者輸入的基礎金額
     * 將其轉換為 Compose 的 State<Double>，以便於在 UI 中即時響應
     * 如果 LiveData 尚未有值（例如畫面剛啟動），就使用預設值 1.0
     */
    val baseAmount by viewModel.baseAmount.observeAsState(1.0)
    // 當 API 資料成功載入並更新 rateList 時，UI 會自動更新顯示新的匯率清單
    val rateMap by viewModel.rateList.observeAsState(emptyMap())
    // 主幣別
    val baseCurrency by viewModel.baseCurrency.observeAsState("USD")
    var showBaseCurrDialog by remember { mutableStateOf(false) }
    var selectedBaseCurrency by remember { mutableStateOf(baseCurrency) }
    // 其他幣別
    val selectedCurrencies by viewModel.selectedCurrencies.observeAsState(listOf())
    var showOtherCurrDialog by remember { mutableStateOf(false) }
    val currencyOptions = listOf(
        "EUR" to "Euro", "USD" to "US Dollar", "JPY" to "Japanese Yen", "BGN" to "Bulgarian Lev",
        "CZK" to "Czech Republic Koruna", "DKK" to "Danish Krone", "GBP" to "British Pound Sterling",
        "HUF" to "Hungarian Forint", "PLN" to "Polish Zloty", "RON" to "Romanian Leu", "SEK" to "Swedish Krona",
        "CHF" to "Swiss Franc", "ISK" to "Icelandic Króna", "NOK" to "Norwegian Krone", "HRK" to "Croatian Kuna",
        "RUB" to "Russian Ruble", "TRY" to "Turkish Lira", "AUD" to "Australian Dollar", "BRL" to "Brazilian Real",
        "CAD" to "Canadian Dollar", "CNY" to "Chinese Yuan", "HKD" to "Hong Kong Dollar", "IDR" to "Indonesian Rupiah",
        "ILS" to "Israeli New Sheqel", "INR" to "Indian Rupee", "KRW" to "South Korean Won", "MXN" to "Mexican Peso",
        "MYR" to "Malaysian Ringgit", "NZD" to "New Zealand Dollar", "PHP" to "Philippine Peso",
        "SGD" to "Singapore Dollar", "THB" to "Thai Baht", "ZAR" to "South African Rand"
    )

    // 取得當前 Composable 的焦點管理器實例
    val focusManager = LocalFocusManager.current
    // 滑動：用來記錄和控制捲動狀態
    val scrollState = rememberScrollState()
    // 加載狀態
    val loadMoreState by viewModel.loadMoreState.observeAsState(LoadMore())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {

        // 使用者輸入欲換算的金額
        OutlinedTextField(
            value = baseAmount.toString(),
            onValueChange = { newText ->
                // 默認值 1.0
                val value = newText.toDoubleOrNull() ?: 1.0
                // 更新 ViewModel 裡的輸入金額
                viewModel.setBaseAmount(value)
            },
            label = {
                Row(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            showBaseCurrDialog = true
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_rate_change),
                        contentDescription = "幣別選擇",
                        modifier = Modifier
                            .size(26.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                showBaseCurrDialog = true
                            },
                        text = "${stringResource(R.string.tv_rate_base)} $baseCurrency",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    // 為了提示用戶這個可以有更多選項
                    Image(
                        painter = painterResource(id = R.drawable.ic_rate_more),
                        contentDescription = "幣別選擇",
                        modifier = Modifier
                            .size(20.dp)
                    )

                    // 點擊了會有彈窗：選擇主幣別
                    if (showBaseCurrDialog) {
                        AlertDialog(
                            onDismissRequest = { showBaseCurrDialog = false },
                            modifier = Modifier
                                .fillMaxWidth(0.98f)
                                .fillMaxHeight(0.90f),
                            containerColor = MaterialTheme.colorScheme.background,
                            // tonalElevation 用來控制表面的色調提升的參數，會影響背景顏色
                            tonalElevation = 0.dp,
                            title = {
                                Text(
                                    text = stringResource(R.string.tv_rate_base_dialog_title),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleLarge
                                )},
                            text = {
                                LazyColumn {
                                    items(currencyOptions) { (code, name) ->
                                        val selected = selectedBaseCurrency == code
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) {
                                                    selectedBaseCurrency = code
                                                },
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "$code -- $name",
                                                color = MaterialTheme.colorScheme.onSurface,
                                                style = MaterialTheme.typography.bodyLarge,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            RadioButton(
                                                selected = selected,
                                                onClick = {
                                                    selectedBaseCurrency = code
                                                },
                                                colors = androidx.compose.material3.RadioButtonDefaults.colors(
                                                    selectedColor = MaterialTheme.colorScheme.primary,
                                                    unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                )
                                            )
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TextButton(
                                        onClick =
                                            {
                                                showBaseCurrDialog = false
                                                if (selectedBaseCurrency != baseCurrency) {
                                                    viewModel.updateBaseCurrency(selectedBaseCurrency)
                                                    viewModel.fetchAllRates()
                                                }
                                            },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.textButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        )
                                    ) {
                                        Text(
                                            text = stringResource(R.string.tv_rate_base_btn_finish),
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            },
            textStyle = TextStyle(fontSize = 20.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 10.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Transparent)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = colorResource(id = R.color.ripple_click_color)) // 你自定 ripple 顏色
                ) {
                    showOtherCurrDialog = true
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_rate_checkbox),
                contentDescription = "幣別選擇",
                modifier = Modifier
                    .size(30.dp)
                    .padding(start = 6.dp, end = 4.dp)
            )

            Text(
                text = stringResource(R.string.tv_rate_other),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        showOtherCurrDialog = true
                    },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // 點擊了會有彈窗：顯示幣別清單，選擇其他幣別
            if (showOtherCurrDialog) {
                AlertDialog(
                    onDismissRequest = { showOtherCurrDialog = false },
                    modifier = Modifier
                        .fillMaxWidth(0.98f)
                        .fillMaxHeight(0.91f),
                    containerColor = MaterialTheme.colorScheme.background,
                    // tonalElevation 用來控制表面的色調提升的參數，會影響背景顏色
                    tonalElevation = 0.dp,
                    title = {
                        Text(
                            text = stringResource(R.string.tv_rate_other_dialog_title),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge
                        )},
                    text = {
                        LazyColumn {
                            items(currencyOptions) { (code, name) ->
                                val checked = selectedCurrencies.contains(code)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            val updated = selectedCurrencies.toMutableList()
                                            // 如果點擊已選中了，就變成取消
                                            if (checked) updated.remove(code)
                                            // 如果點擊是未選中的，就變成已選（注意最多6個）
                                            else if (updated.size < 6) updated.add(code)
                                            viewModel.updateSelectedCurrencies(updated)
                                        }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "$code -- $name",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                        )
                                    /**
                                     * https://leobert-lan.github.io/Compose/post_28.html
                                     * checkedColor：勾選狀態下的框框顏色（背景色）
                                     * uncheckedColor：未勾選狀態下的框框顏色
                                     * checkmarkColor： 勾選符號（✔） 的顏色
                                     * disabledColor：不可操作狀態下 未勾選的框框顏色（整個 Checkbox 被 disable 時）
                                     * disabledIndeterminateColor：不可操作狀態下 勾選中的顏色（和 checkedColor 搭配）
                                     *
                                     */
                                    Checkbox(
                                        checked = checked,
                                        onCheckedChange = null,
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = colorResource(id = R.color.checkbox_status_checked),
                                            uncheckedColor = colorResource(id = R.color.checkbox_status_unchecked),
                                            checkmarkColor = colorResource(id = R.color.checkbox_status_checkmark),
                                        )
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            TextButton(
                                onClick = { showOtherCurrDialog = false },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.tv_rate_base_btn_finish),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                )
            }
        }

        if (loadMoreState.isLoading) {
            // 加載中：https://developer.android.com/develop/ui/compose/components/progress?hl=zh-tw
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }
        } else if (!loadMoreState.message.isNullOrBlank()) {
            // 錯誤信息
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            viewModel.fetchAllRates()
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_rate_error),
                        contentDescription = "無數據",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = loadMoreState.message!!,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        } else {
            // 顯示六個幣別匯率換算結果
            ExchangeRateList(baseAmount = baseAmount, rates = rateMap, selectedCurrencies = selectedCurrencies)
        }

        // 溫馨提醒
        ExchangeRateTip()
    }
}