package com.ngapak.dev.javacell.shared.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.ngapak.dev.javacell.feature.products.domain.model.Product

data class Transaction(
    @DocumentId
    val id: String? = null,
    val deliveryStatus: String? = null,
    val receiptNumber: String? = null,
    val paymentStatus: String? = null,
    val qty: Int? = null,
    val totalPrice: Int? = null,
    val address: Address? = null,
    val product: Product? = null,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    @ServerTimestamp
    val paymentAt: Timestamp? = null,
)
