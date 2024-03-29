package com.ngapak.dev.javacell.shared.model

import com.google.firebase.firestore.DocumentId

data class Address(
    @DocumentId
    val id: String? = null,
    val receiverName: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val addressDetail: String? = null,
    val note: String? = null,
)
