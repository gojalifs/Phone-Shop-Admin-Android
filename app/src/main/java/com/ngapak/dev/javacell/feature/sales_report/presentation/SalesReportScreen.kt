package com.ngapak.dev.javacell.feature.sales_report.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.di.Injection
import com.ngapak.dev.javacell.feature.order.presentation.OrderViewModel
import com.ngapak.dev.javacell.shared.component.Center
import com.ngapak.dev.javacell.shared.component.Table
import com.ngapak.dev.javacell.shared.model.Transaction
import com.ngapak.dev.javacell.utils.Converter.toRupiah
import com.ngapak.dev.javacell.utils.Converter.toTime
import com.ngapak.dev.javacell.utils.Converter.toWibDate
import com.ngapak.dev.javacell.utils.Converter.toWibTime
import java.util.Date

//fun Context.findActivity(): Activity? = when (this) {
//    is Activity -> this
//    is ContextWrapper -> baseContext.findActivity()
//    else -> null
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesReportScreen(
    goToDetail: () -> Unit,
    orderDetailViewModel: OrderViewModel,
    modifier: Modifier = Modifier,
//    orientation: Int = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
    salesReportViewModel: SalesReportViewModel = viewModel(
        factory = SalesReportViewModelFactory(Injection.provideSalesReportUseCase())
    ),
) {
//    val context = LocalContext.current
//    DisposableEffect(orientation) {
//        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
//        val originalOrientation = activity.requestedOrientation
//        activity.requestedOrientation = orientation
//        onDispose {
//            // restore original orientation when view disappears
//            activity.requestedOrientation = originalOrientation
//        }
//    }

    val dateRangePickerState = rememberDateRangePickerState(
        initialDisplayMode = DisplayMode.Picker,
        yearRange = 2010..2100
    )
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showDialog) {
            ReportDatePickerDialog(
                rangePickerState = dateRangePickerState,
                salesReportViewModel = salesReportViewModel,
                hideDialog = { showDialog = false },
            )
        }

        Button(onClick = { showDialog = true }, modifier = modifier.padding(bottom = 16.dp)) {
            Text(text = "Select Range of Report")
        }
        if (dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null) {
            Text(
                text = "Showing for ${dateRangePickerState.selectedStartDateMillis?.toTime()} - ${dateRangePickerState.selectedEndDateMillis?.toTime()}",
            )
        } else {
            Text(text = "Please select a date range")
        }

        salesReportViewModel.report.collectAsState().value.let { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Center { CircularProgressIndicator() }
                }

                is Resource.Success -> {
                    if (resource.data.isNullOrEmpty()) {
                        Center {
                            Text(text = "No data available")
                        }
                    } else {
//                        CustomDataTable(resource.data)
                        Table(
                            columnCount = titles.size,
                            cellWidth = { index ->
                                when (index) {
                                    0 -> 75.dp
                                    1 -> 250.dp
                                    2 -> 250.dp
                                    3 -> 75.dp
                                    4 -> 250.dp
                                    else -> 150.dp
                                }
                            },
                            data = resource.data,
                            headerCellContent = { index ->

                                Text(
                                    text = titles[index],
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp),
                                    maxLines = if (index == 1) 2 else 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Black,
                                    textDecoration = TextDecoration.Underline
                                )
                            }
                        ) { index, item ->
                            val value = when (index) {
                                0 -> "${resource.data.indexOf(item)}"
                                1 -> item.id
                                2 -> item.product?.name
                                3 -> item.qty.toString()
                                4 -> item.totalPrice?.toRupiah()
                                else -> ""
                            }

                            Text(
                                text = "$value",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clickable {
                                        if (index == 1) {
                                            /// TODO go to detail order screen
                                            orderDetailViewModel.setOrder(item)
                                            goToDetail()
                                        }
                                    }
                                    .padding(16.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }

                is Resource.Error -> {
                    Center {
                        Text(text = "Something error. ${resource.message}")
                    }
                }
            }
        }

    }
}

@Composable
fun CustomDataTable(data: List<Transaction>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LazyColumn() {
        item {
            LazyRow(modifier = modifier) {
                itemsIndexed(titles, key = null) { index, item ->
                    Text(text = item)
                }
//                Text(
//                    text = "No.", fontWeight = FontWeight.Bold,
//                    modifier = modifier.weight(1f)
//                )
//                Text(
//                    text = "Order ID",
//                    fontWeight = FontWeight.Bold,
//                    modifier = modifier.weight(2f)
//                )
//                Text(
//                    text = "Product Name",
//                    fontWeight = FontWeight.Bold,
//                    modifier = modifier.weight(2f)
//                )
//                Text(
//                    text = "Qty",
//                    fontWeight = FontWeight.Bold,
//                    modifier = modifier.weight(1f)
//                )
//                Text(
//                    text = "Total Paid",
//                    fontWeight = FontWeight.Bold,
//                    modifier = modifier.weight(2f)
//                )
//                Text(
//                    text = "Order At",
//                    fontWeight = FontWeight.Bold,
//                    modifier = modifier.weight(2f)
//                )
            }
        }
        items(data, key = { it.id ?: "" }) {
            LazyRow {
//                Text(text = "", modifier = modifier.weight(1f))
//                Text(text = "${it.id}", modifier = modifier.weight(2f))
//                Text(text = "${it.product?.name}", modifier = modifier.weight(2f))
                items(data, key = { it.id ?: "" }) {
                    Text(text = "${data.indexOf(it)}")
                    Text(text = "${it.id}")
                    Text(text = "${it.product?.name}")
                    Text(text = "${it.qty}")
                    Text(text = "${it.totalPrice?.toRupiah()}")
                    Text(text = "${it.createdAt?.toWibDate()} ${it.createdAt?.toWibTime()}")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDatePickerDialog(
    rangePickerState: DateRangePickerState,
    salesReportViewModel: SalesReportViewModel,
    hideDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf(System.currentTimeMillis()) }

    DatePickerDialog(
        onDismissRequest = {
            hideDialog()
            if (rangePickerState.selectedStartDateMillis != null && rangePickerState.selectedEndDateMillis != null) {
                startDate = rangePickerState.selectedStartDateMillis!!
                endDate = rangePickerState.selectedEndDateMillis!! + 86400000L

                salesReportViewModel.makeReport(Date(startDate), Date(endDate))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                hideDialog()
                if (rangePickerState.selectedStartDateMillis != null && rangePickerState.selectedEndDateMillis != null) {
                    startDate = rangePickerState.selectedStartDateMillis!!
                    endDate = rangePickerState.selectedEndDateMillis!!

                    salesReportViewModel.makeReport(Date(startDate), Date(endDate))
                }
            }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { hideDialog() }) {
                Text(text = "Cancel")
            }
        },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DateRangePicker(
                state = rangePickerState,
                showModeToggle = true,
                title = {
                    Text(
                        text = "Please select a date range",
                        textAlign = TextAlign.Center,
                        modifier = modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        color = Color.DarkGray,
                        fontSize = 22.sp,
                    )
                },
            )
        }
    }
}

private val titles = listOf("No.", "Order ID", "Product Name", "Qty", "Grand Total")