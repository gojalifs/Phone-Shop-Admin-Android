package com.ngapak.dev.javacell.feature.products.domain.model

import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId
    val id: String? = null,
    val name: String? = null,
    val stock: Int? = null,
    val description: String? = null,
    val category: String? = null,
    val imageUrl: String? = null,
    val price: Int? = null,
)
