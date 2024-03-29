package com.ngapak.dev.javacell.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ngapak.dev.javacell.feature.auth.data.AuthRepository
import com.ngapak.dev.javacell.feature.auth.domain.AuthInteractor
import com.ngapak.dev.javacell.feature.auth.domain.usecase.AuthUseCase
import com.ngapak.dev.javacell.feature.billable.data.StockReportRepository
import com.ngapak.dev.javacell.feature.billable.domain.BillingInteractor
import com.ngapak.dev.javacell.feature.stock_report.domain.StockReportUseCase
import com.ngapak.dev.javacell.feature.dashboard.data.HomeRepository
import com.ngapak.dev.javacell.feature.dashboard.domain.HomeInteractor
import com.ngapak.dev.javacell.feature.dashboard.domain.HomeUseCase
import com.ngapak.dev.javacell.feature.order.data.OrderRepository
import com.ngapak.dev.javacell.feature.order.domain.OrderInteractor
import com.ngapak.dev.javacell.feature.order.domain.OrderUseCase
import com.ngapak.dev.javacell.feature.products.data.ProductsRepository
import com.ngapak.dev.javacell.feature.products.domain.ProductsInteractor
import com.ngapak.dev.javacell.feature.products.domain.usecase.ProductsUseCase
import com.ngapak.dev.javacell.feature.sales_report.data.SalesReportRepository
import com.ngapak.dev.javacell.feature.sales_report.domain.SalesReportInteractor
import com.ngapak.dev.javacell.feature.sales_report.domain.SalesReportUseCase
import com.ngapak.dev.javacell.feature.splash.data.SplashRepository
import com.ngapak.dev.javacell.feature.splash.domain.usecase.SplashInteractor
import com.ngapak.dev.javacell.feature.splash.domain.usecase.SplashUseCase

object Injection {
    fun provideAuthUseCase(): AuthUseCase {
        val authRepository = provideAuthRepository()
        return AuthInteractor(authRepository)
    }

    private fun provideAuthRepository(): AuthRepository {
        val auth = Firebase.auth
        return AuthRepository(auth)
    }

    fun provideSplashUseCase(): SplashUseCase {
        val splashRepository = provideSplashRepository()
        return SplashInteractor(splashRepository)
    }

    private fun provideSplashRepository(): SplashRepository {
        val auth = Firebase.auth
        return SplashRepository(auth)
    }

    fun provideHomeUseCase(): HomeUseCase {
        val auth = Firebase.auth
        val db = Firebase.firestore
        val homeRepository = HomeRepository(auth, db)
        return HomeInteractor(homeRepository)
    }

    fun provideProductsUseCase(): ProductsUseCase {
        val auth = Firebase.auth
        val db = Firebase.firestore
        val productsRepository = ProductsRepository(auth, db)
        return ProductsInteractor(productsRepository)
    }

    fun provideOrderUseCase(): OrderUseCase {
        val auth = Firebase.auth
        val db = Firebase.firestore
        val orderRepository = OrderRepository(auth, db)
        return OrderInteractor(orderRepository)
    }

    fun provideSalesReportUseCase(): SalesReportUseCase {
        val auth = Firebase.auth
        val db = Firebase.firestore
        val salesReportRepository = SalesReportRepository(auth, db)
        return SalesReportInteractor(salesReportRepository)
    }

    fun provideBillingReportUseCase(): StockReportUseCase {
        val auth = Firebase.auth
        val db = Firebase.firestore
        val billingRepository = StockReportRepository(auth, db)
        return BillingInteractor(billingRepository)
    }

}