package com.ngapak.dev.javacell.feature.stock_report.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ngapak.dev.javacell.data.Resource
import com.ngapak.dev.javacell.di.Injection
import com.ngapak.dev.javacell.feature.billable.presentation.Person
import com.ngapak.dev.javacell.feature.order.presentation.OrderViewModel
import com.ngapak.dev.javacell.shared.component.Center
import com.ngapak.dev.javacell.shared.component.Table

@Composable
fun StockReportScreen(
    goToDetail: () -> Unit,
    orderDetailViewModel: OrderViewModel,
    modifier: Modifier = Modifier,
    stockReportViewModel: StockReportViewModel = viewModel(
        factory = StockReportViewModelFactory(Injection.provideBillingReportUseCase())
    ),
) {

    LaunchedEffect(Unit){
        stockReportViewModel.getStockReport()
    }
    val data = stockReportViewModel.stockReport.collectAsState().value

    val people = listOf(
        Person("Alex", 21, false, "alex@demo-email.com"),
        Person("Adam", 35, true, "adam@demo-email.com"),
        Person("Iris", 26, false, "iris@demo-email.com"),
        Person("Maria", 32, false, "maria@demo-email.com")
    )

    val cellWidth: (Int) -> Dp = { index ->
        when (index) {
            2 -> 250.dp
            3 -> 350.dp
            else -> 150.dp
        }
    }
    val headerCellTitle: @Composable (Int) -> Unit = { index ->
        val value = when (index) {
            0 -> "No."
            1 -> "Order ID"
            2 -> "Customer Name"
            3 -> "Customer Phone"
            4 -> "Grand Total"
            else -> ""
        }

        Text(
            text = value,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Black,
            textDecoration = TextDecoration.Underline
        )
    }
    val cellText: @Composable (Int, Person) -> Unit = { index, item ->
        val value = when (index) {
            0 -> item.name
            1 -> item.age.toString()
            2 -> if (item.hasDrivingLicense) "YES" else "NO"
            3 -> item.email
            else -> ""
        }

        Text(
            text = value,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

    data.let { resource ->
        when (resource) {
            is Resource.Loading -> Center { CircularProgressIndicator() }
            is Resource.Success -> {
                Table(
                    columnCount = 4,
                    cellWidth = cellWidth,
                    data = resource.data ?: emptyList(),
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    headerCellContent = headerCellTitle,
                    cellContent = { index, item ->
                        val value = when (index) {
                            0 -> "${resource.data?.indexOf(item)}"
                            1 -> "${item.id}"
                            2 -> "${item.address?.receiverName}"
                            3 -> "${item.address?.phone}"
                            4 -> "${item.totalPrice}"
                            else -> ""
                        }

                        Text(
                            text = value,
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
                )
            }

            is Resource.Error -> {}
        }
    }
}